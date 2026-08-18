#!/bin/bash
# Container supervisor: start the display stack, nginx, and the upload
# endpoint; block until /data/redneck.grp exists; then exec the game
# under DISPLAY=:1 so JVM's exit tears the container down (docker restart
# policy resurrects).

set -euo pipefail

DATA=/data
GRP="$DATA/redneck.grp"
INI="$DATA/redneckgdx.ini"

# Display resolution. Kept a common 16:9 so noVNC's `resize=scale` maps
# cleanly to typical browser windows.
DISPLAY_W=${DISPLAY_W:-1280}
DISPLAY_H=${DISPLAY_H:-720}

log() { echo "[$(date -u +%H:%M:%S)] $*"; }

# ---- 1. background daemons ------------------------------------------------
log "starting Xvfb :1 (${DISPLAY_W}x${DISPLAY_H})"
Xvfb :1 -screen 0 "${DISPLAY_W}x${DISPLAY_H}x24" -nolisten tcp +extension GLX +extension RANDR &

# Wait for the X server socket to be ready before starting anything that talks
# to it. Xvfb takes a beat on first boot.
for i in $(seq 1 50); do
    [ -S /tmp/.X11-unix/X1 ] && break
    sleep 0.1
done

log "starting fluxbox"
DISPLAY=:1 fluxbox >/tmp/fluxbox.log 2>&1 &

log "starting pulseaudio (null sink; audio is not carried by noVNC)"
# System-wide daemon with a null sink so OpenAL initialises against a real
# device and the game boots cleanly. Real browser-audible audio would need
# switching from noVNC to Xpra HTML5 or KasmVNC.
mkdir -p /run/user/0/pulse
pulseaudio --system --disallow-exit --disallow-module-loading=false \
    --exit-idle-time=-1 --daemonize=yes --log-target=file:/tmp/pulseaudio.log \
    --load="module-native-protocol-unix" \
    --load="module-null-sink sink_name=null_out sink_properties=device.description=NullSink" \
    --load="module-suspend-on-idle" >/dev/null 2>&1 || \
    log "pulseaudio failed to start (game will run silently on OpenAL null)"
export PULSE_SERVER=unix:/var/run/pulse/native

log "starting x11vnc (5901)"
x11vnc -display :1 -rfbport 5901 -forever -shared -nopw -quiet -bg -o /tmp/x11vnc.log

log "starting websockify (6080 -> 5901)"
websockify --web /usr/share/novnc 6080 localhost:5901 >/tmp/websockify.log 2>&1 &

log "starting upload server (8081)"
python3 /opt/rrgdx/upload_server.py >/tmp/upload.log 2>&1 &

log "starting nginx (8080)"
nginx -g 'daemon off;' &

# ---- 2. seed redneckgdx.ini so the game boots fullscreen at Xvfb's size --
mkdir -p "$DATA"
if [ ! -f "$INI" ]; then
    log "seeding $INI (Fullscreen=true, ${DISPLAY_W}x${DISPLAY_H})"
    # Minimal ini — the game rewrites this file with the full schema on
    # first exit. Only the [Screen] block matters at startup.
    cat > "$INI" <<EOF
[Screen]
Fullscreen = true
ScreenWidth = ${DISPLAY_W}
ScreenHeight = ${DISPLAY_H}
EOF
fi

# ---- 3. wait for the grp --------------------------------------------------
if [ ! -f "$GRP" ]; then
    log "waiting for $GRP (upload via http://<host>:8080/)"
    while [ ! -f "$GRP" ]; do
        inotifywait -q -e create -e moved_to "$DATA" >/dev/null || sleep 1
    done
fi
log "grp present ($(stat -c%s "$GRP") bytes) — launching game"

# ---- 4. background fullscreen enforcer -----------------------------------
# In case the seeded ini's Fullscreen=true is ignored (e.g. game overrides
# on first boot), force fullscreen on the game window once it appears.
(
    for _ in $(seq 1 60); do
        if DISPLAY=:1 wmctrl -l 2>/dev/null | grep -qi redneck; then
            DISPLAY=:1 wmctrl -r 'RedneckGDX' -b add,fullscreen 2>/dev/null || \
                DISPLAY=:1 xdotool search --name RedneckGDX \
                    windowsize --sync 100% 100% \
                    windowmove --sync 0 0 2>/dev/null || true
            break
        fi
        sleep 1
    done
) &

# ---- 5. exec the game ----------------------------------------------------
cd "$DATA"
exec java \
    -Djava.awt.headless=false \
    -Dorg.lwjgl.util.NoChecks=true \
    -jar /opt/rrgdx/game.jar "$DATA"
