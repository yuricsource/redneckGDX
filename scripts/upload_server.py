#!/usr/bin/env python3
# Tiny grp-upload endpoint. Listens on 127.0.0.1:8081; nginx proxies /upload
# to this. Accepts a single multipart/form-data POST with field name "grp"
# and writes it atomically to /data/redneck.grp, then responds 303 to
# /vnc.html so the browser follows through to noVNC.

import cgi
import os
import shutil
import tempfile
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

DATA_DIR = "/data"
DEST = os.path.join(DATA_DIR, "redneck.grp")
MAX_BYTES = 64 * 1024 * 1024  # 64 MiB is well over the ~40 MB grp size


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):  # noqa: N802 (http.server contract)
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
        self.send_header("Location", "/vnc.html?autoconnect=1&resize=scale")
        self.end_headers()

    def log_message(self, fmt, *args):  # quieter default logs
        self.log_date_time_string()
        print("upload:", fmt % args)


if __name__ == "__main__":
    server = ThreadingHTTPServer(("127.0.0.1", 8081), Handler)
    print("upload server listening on 127.0.0.1:8081")
    server.serve_forever()
