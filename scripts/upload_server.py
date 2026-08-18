#!/usr/bin/env python3
# Tiny grp-upload + /logs endpoint. Listens on 127.0.0.1:8081; nginx
# proxies /upload and /logs to this.
#
#   POST /upload : multipart form-data with field "grp" -> atomic write
#                  to /data/redneck.grp, then 303 to /xpra/index.html
#   GET  /logs   : concatenated tails of /tmp/xpra.log, /tmp/pulseaudio.log,
#                  /tmp/upload.log, plus /data listing and process tree.
#                  Purely a debug convenience, exposed on the same port
#                  as /upload so users can share state via URL.

import cgi
import os
import shutil
import subprocess
import tempfile
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

DATA_DIR = "/data"
DEST = os.path.join(DATA_DIR, "redneck.grp")
MAX_BYTES = 1024 * 1024 * 1024  # 1 GiB — same ceiling as nginx

LOG_FILES = [
    "/tmp/xpra.log",
    "/tmp/pulseaudio.log",
    "/tmp/upload.log",
    "/tmp/fluxbox.log",
]


def tail(path: str, n: int = 200) -> str:
    try:
        with open(path, "rb") as fh:
            fh.seek(0, os.SEEK_END)
            size = fh.tell()
            fh.seek(max(0, size - 64 * 1024))
            data = fh.read().decode("utf-8", errors="replace")
        lines = data.splitlines()[-n:]
        return "\n".join(lines)
    except FileNotFoundError:
        return "(file not found)"
    except Exception as exc:  # noqa: BLE001
        return f"(read error: {exc})"


def run(cmd: list[str]) -> str:
    try:
        return subprocess.run(
            cmd, capture_output=True, text=True, timeout=3,
        ).stdout
    except Exception as exc:  # noqa: BLE001
        return f"(command failed: {exc})"


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):  # noqa: N802
        if self.path == "/logs":
            self._logs()
            return
        self.send_error(404, "not found")

    def _logs(self):
        parts = []
        parts.append("=== /data contents ===\n" + run(["ls", "-la", DATA_DIR]))
        parts.append("=== ps -ef ===\n" + run(["ps", "-eo", "pid,user,cmd"]))
        for path in LOG_FILES:
            parts.append(f"=== tail {path} ===\n" + tail(path))
        body = "\n\n".join(parts).encode("utf-8", errors="replace")
        self.send_response(200)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

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
            fp=self.rfile,
            headers=self.headers,
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
        self.send_header(
            "Location",
            "/xpra/connect.html?autoconnect=true&sound=on"
            "&audio_codec=opus&clipboard=true&keyboard_layout=us&scaling=auto",
        )
        self.end_headers()

    def log_message(self, fmt, *args):  # quieter default logs
        self.log_date_time_string()
        print("upload:", fmt % args)


if __name__ == "__main__":
    server = ThreadingHTTPServer(("127.0.0.1", 8081), Handler)
    print("upload server listening on 127.0.0.1:8081")
    server.serve_forever()
