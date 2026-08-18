#!/usr/bin/env python3
# Tiny multi-endpoint server on 127.0.0.1:8081. nginx proxies /upload,
# /logs, and /audio.ogg to this.
#
#   POST /upload   : multipart form-data with field "grp" -> atomic
#                    write to /data/redneck.grp, then 303 to /play.
#   GET  /logs     : container-side diagnostics dump.
#   GET  /audio.ogg: OGG/Vorbis audio stream sourced by gstreamer from
#                    PulseAudio's null_out.monitor. Chunked HTTP; the
#                    browser's <audio> element streams it forever.

import cgi
import os
import shutil
import signal
import subprocess
import tempfile
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

DATA_DIR = "/data"
DEST = os.path.join(DATA_DIR, "redneck.grp")
MAX_BYTES = 1024 * 1024 * 1024

LOG_FILES = [
    "/tmp/kasmvnc.log",
    "/tmp/game.log",
    "/tmp/pulseaudio.log",
    "/tmp/pactl-sources.log",
    "/tmp/gst-audio.log",
    "/tmp/fluxbox.log",
    "/tmp/upload.log",
]

# gstreamer pipeline: pull PCM from pulseaudio's null-sink monitor at
# low latency, encode as Opus in an OGG container (Opus is designed
# for realtime — 20ms frames vs Vorbis's ~200ms lookahead), write to
# stdout. Streams forever until client disconnects.
#
# `pulsesrc buffer-time=40000 latency-time=10000` = 40ms buffer, 10ms
# period, keeping the input pipeline tight.
# `opusenc bitrate=96000 frame-size=20` gives 20ms frames at 96 kbps
# which is plenty for game audio and low-latency.
# `oggmux max-delay=20000000 max-page-duration=20000000` (20ms in ns)
# forces small pages so nothing accumulates in the muxer.
GST_AUDIO_CMD = [
    "gst-launch-1.0", "-q",
    "pulsesrc", "device=null_out.monitor",
        "buffer-time=40000", "latency-time=10000",
    "!", "audioconvert",
    "!", "audioresample",
    "!", "audio/x-raw,rate=48000,channels=2",
    "!", "opusenc", "bitrate=96000", "frame-size=20", "audio-type=generic",
    "!", "oggmux", "max-delay=20000000",
    "!", "fdsink", "fd=1", "sync=false",
]


def tail(path: str, n: int = 200) -> str:
    try:
        with open(path, "rb") as fh:
            fh.seek(0, os.SEEK_END)
            size = fh.tell()
            fh.seek(max(0, size - 64 * 1024))
            data = fh.read().decode("utf-8", errors="replace")
        return "\n".join(data.splitlines()[-n:])
    except FileNotFoundError:
        return "(file not found)"
    except Exception as exc:  # noqa: BLE001
        return f"(read error: {exc})"


def run(cmd: list[str]) -> str:
    try:
        return subprocess.run(cmd, capture_output=True, text=True, timeout=3).stdout
    except Exception as exc:  # noqa: BLE001
        return f"(command failed: {exc})"


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):  # noqa: N802
        if self.path == "/logs":
            self._logs()
        elif self.path == "/audio.ogg":
            self._audio()
        else:
            self.send_error(404, "not found")

    def _logs(self):
        parts = ["=== /data ===\n" + run(["ls", "-la", DATA_DIR]),
                 "=== ps ===\n" + run(["ps", "-eo", "pid,user,cmd"])]
        for path in LOG_FILES:
            parts.append(f"=== {path} ===\n" + tail(path))
        body = "\n\n".join(parts).encode("utf-8", errors="replace")
        self.send_response(200)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def _audio(self):
        # Spawn a per-connection gstreamer pipe. When the client
        # disconnects, wfile.write raises and we kill the child.
        # bufsize=0 -> unbuffered stdout; combined with read1() below
        # we forward each opus frame the moment gstreamer emits it
        # rather than waiting to fill a 4096-byte block (~340ms at
        # 96kbps, previously the dominant latency source).
        gst_err = open("/tmp/gst-audio.log", "ab", buffering=0)
        proc = subprocess.Popen(
            GST_AUDIO_CMD,
            stdout=subprocess.PIPE,
            stderr=gst_err,
            preexec_fn=os.setsid,
            bufsize=0,
        )
        try:
            self.send_response(200)
            self.send_header("Content-Type", "audio/ogg; codecs=opus")
            self.send_header("Cache-Control", "no-cache, no-store")
            self.send_header("Connection", "close")
            self.end_headers()
            self.wfile.flush()
            while True:
                # read1() returns whatever is immediately available
                # (up to N bytes) without waiting to fill the buffer.
                chunk = proc.stdout.read1(4096)
                if not chunk:
                    break
                self.wfile.write(chunk)
                self.wfile.flush()
        except (BrokenPipeError, ConnectionResetError):
            pass  # client disconnected — normal
        finally:
            try:
                os.killpg(proc.pid, signal.SIGTERM)
            except ProcessLookupError:
                pass
            proc.wait(timeout=2)

    def do_POST(self):  # noqa: N802
        if self.path != "/upload":
            self.send_error(404, "not found")
            return
        length = int(self.headers.get("content-length", "0"))
        if length <= 0 or length > MAX_BYTES:
            self.send_error(413, "payload too large or missing")
            return
        ctype = self.headers.get("content-type", "")
        if not ctype.startswith("multipart/form-data"):
            self.send_error(415, "expected multipart/form-data")
            return

        form = cgi.FieldStorage(
            fp=self.rfile, headers=self.headers,
            environ={"REQUEST_METHOD": "POST", "CONTENT_TYPE": ctype},
        )
        field = form["grp"] if "grp" in form else None
        if field is None or not getattr(field, "file", None):
            self.send_error(400, "missing 'grp' file field")
            return

        os.makedirs(DATA_DIR, exist_ok=True)
        tmp_fd, tmp_path = tempfile.mkstemp(dir=DATA_DIR, suffix=".partial")
        try:
            with os.fdopen(tmp_fd, "wb") as out:
                shutil.copyfileobj(field.file, out, length=1 << 16)
            os.replace(tmp_path, DEST)
        except Exception as exc:  # noqa: BLE001
            try:
                os.unlink(tmp_path)
            except OSError:
                pass
            self.send_error(500, f"write failed: {exc}")
            return

        self.send_response(303)
        self.send_header("Location", "/play")
        self.end_headers()

    def log_message(self, fmt, *args):
        self.log_date_time_string()
        print("upload:", fmt % args)


if __name__ == "__main__":
    server = ThreadingHTTPServer(("127.0.0.1", 8081), Handler)
    print("upload/audio/logs server listening on 127.0.0.1:8081")
    server.serve_forever()
