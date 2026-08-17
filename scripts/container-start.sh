#!/bin/bash
# Container supervisor: start the display stack, nginx, and the upload
# endpoint; block until /data/redneck.grp exists; then exec the game
# under DISPLAY=:1 so JVM's exit tears the container down (docker restart
# policy resurrects).

set -euo pipefail

DATA=/data
GRP="$DATA/redneck.grp"

log() { echo "[$(date -u +%H:%M:%S)] $*"; }

# ---- 1. background daemons ------------------------------------------------
log "starting Xvfb :1"
Xvfb :1 -screen 0 1280x800x24 -nolisten tcp +extension GLX +extension RANDR &
XVFB_PID=$!

# Wait for the X server socket to be ready before starting anything that talks
# to it. Xvfb takes a beat on first boot.
for i in $(seq 1 50); do
    [ -S /tmp/.X11-unix/X1 ] && break
    sleep 0.1
done

log "starting fluxbox"
DISPLAY=:1 fluxbox >/tmp/fluxbox.log 2>&1 &

log "starting x11vnc (5901)"
x11vnc -display :1 -rfbport 5901 -forever -shared -nopw -quiet -bg -o /tmp/x11vnc.log

log "starting websockify (6080 -> 5901)"
websockify --web /usr/share/novnc 6080 localhost:5901 >/tmp/websockify.log 2>&1 &

log "starting upload server (8081)"
python3 /opt/rrgdx/upload_server.py >/tmp/upload.log 2>&1 &

log "starting nginx (8080)"
nginx -g 'daemon off;' &

# ---- 2. wait for the grp --------------------------------------------------
mkdir -p "$DATA"
if [ ! -f "$GRP" ]; then
    log "waiting for $GRP (upload via http://<host>:8080/)"
    while [ ! -f "$GRP" ]; do
        # inotifywait exits on any create/move event in $DATA; loop guards
        # against unrelated files.
        inotifywait -q -e create -e moved_to "$DATA" >/dev/null || sleep 1
    done
fi
log "grp present ($(stat -c%s "$GRP") bytes) — launching game"

# ---- 3. exec the game -----------------------------------------------------
# `exec` replaces this shell so signals propagate cleanly and JVM exit is
# container exit. Docker restart policy handles crashes.
cd "$DATA"
exec java \
    -Djava.awt.headless=false \
    -Dorg.lwjgl.util.NoChecks=true \
    -jar /opt/rrgdx/game.jar "$DATA"
