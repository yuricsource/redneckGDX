#!/bin/bash
# Container supervisor. KasmVNC (Xvnc) owns the X display + noVNC-fork
# HTML5 client + audio channel. Nginx fronts everything on 8080.

set -euo pipefail

DATA=/data
INI="$DATA/redneckgdx.ini"

DISPLAY_NUM=1
DISPLAY_W=${DISPLAY_W:-1280}
DISPLAY_H=${DISPLAY_H:-720}
KASM_PORT=6901

log() { echo "[$(date -u +%H:%M:%S)] $*"; }

# ---- 1. pulseaudio (in per-user mode, not --system which needs D-Bus) ---
# System mode requires /run/dbus + cookie auth + a `pulse` user's
# runtime dir; none of that is set up in this container. User mode is
# simpler and works for a single-process container: pick a socket path,
# start it with anonymous auth, and export PULSE_SERVER so every child
# (Xvnc, the game, the Python server that spawns gstreamer) picks it
# up automatically.
export XDG_RUNTIME_DIR=/tmp/xdg
mkdir -p "$XDG_RUNTIME_DIR" && chmod 700 "$XDG_RUNTIME_DIR"
export PULSE_SERVER=unix:${XDG_RUNTIME_DIR}/pulse-native

log "starting pulseaudio (user mode, socket=${XDG_RUNTIME_DIR}/pulse-native)"
pulseaudio \
    --exit-idle-time=-1 \
    --daemonize=yes \
    --log-target=file:/tmp/pulseaudio.log \
    --disable-shm=true \
    --disallow-exit=true \
    -L "module-native-protocol-unix socket=${XDG_RUNTIME_DIR}/pulse-native auth-anonymous=1" \
    -L "module-null-sink sink_name=null_out sink_properties=device.description=NullSink" \
    >/dev/null 2>&1 || \
    log "pulseaudio failed to start (game will run silently)"

# Verify the source we care about is available; log for /logs diagnostics.
sleep 0.5
pactl list sources short 2>&1 | tee /tmp/pactl-sources.log | while read line; do log "pactl: $line"; done

# ---- 2. nginx + upload server -------------------------------------------
log "starting upload server (8081)"
python3 /opt/rrgdx/upload_server.py >/tmp/upload.log 2>&1 &

log "starting nginx (8080)"
nginx -g 'daemon off;' &

# ---- 3. seed redneckgdx.ini ---------------------------------------------
mkdir -p "$DATA"
if [ ! -f "$INI" ]; then
    log "seeding $INI (Fullscreen=false, ${DISPLAY_W}x${DISPLAY_H})"
    cat > "$INI" <<EOF
[Screen]
Fullscreen = false
ScreenWidth = ${DISPLAY_W}
ScreenHeight = ${DISPLAY_H}

[Controls]
UseMouse = true
MouseAiming = true
RawMouseInput = true
EOF
fi

# ---- 4. KasmVNC (Xvnc) -- direct binary invocation ----------------------
# We call Xvnc directly (not the kasmvncserver wrapper) so we can pin
# every flag explicitly. -SecurityTypes None + -disableBasicAuth means
# no password challenge and no HTTP Basic-Auth on the built-in webserver.
# -websocketPort serves the HTML5 client + WS on one port. -httpd points
# at the KasmVNC-bundled noVNC-fork HTML5 client.
log "starting KasmVNC on :${DISPLAY_NUM} (HTTP+WS on :${KASM_PORT})"
Xvnc ":${DISPLAY_NUM}" \
    -SecurityTypes None \
    -disableBasicAuth \
    -websocketPort ${KASM_PORT} \
    -interface 127.0.0.1 \
    -sslOnly 0 \
    -PublicIP 0.0.0.0 \
    -httpd /usr/share/kasmvnc/www \
    -depth 24 \
    -geometry "${DISPLAY_W}x${DISPLAY_H}" \
    -desktop RedneckGDX \
    -pn \
    -localhost 0 \
    +extension GLX \
    +extension RANDR \
    +extension Composite \
    -noreset \
    >/tmp/kasmvnc.log 2>&1 &
KASM_PID=$!

# Wait for KasmVNC to be ready before we start the WM and the game.
for i in $(seq 1 60); do
    if curl -sf "http://127.0.0.1:${KASM_PORT}/" >/dev/null 2>&1; then
        log "KasmVNC HTTP ready"
        break
    fi
    sleep 0.5
done

# ---- 5. fluxbox WM inside the KasmVNC display ---------------------------
log "starting fluxbox inside :${DISPLAY_NUM}"
DISPLAY=":${DISPLAY_NUM}" fluxbox >/tmp/fluxbox.log 2>&1 &

# ---- 6. fullscreen enforcer (background) --------------------------------
(
    for _ in $(seq 1 120); do
        if DISPLAY=":${DISPLAY_NUM}" wmctrl -l 2>/dev/null | grep -qi redneck; then
            DISPLAY=":${DISPLAY_NUM}" wmctrl -r 'RedneckGDX' -b add,fullscreen 2>/dev/null || \
                DISPLAY=":${DISPLAY_NUM}" xdotool search --name RedneckGDX \
                    windowsize --sync 100% 100% \
                    windowmove --sync 0 0 2>/dev/null || true
            break
        fi
        sleep 1
    done
) &

# ---- 7. wait-and-launch (blocks on grp then execs game) -----------------
log "spawning wait-and-launch (blocks until grp arrives, then exec's the game)"
DISPLAY=":${DISPLAY_NUM}" /opt/rrgdx/wait-and-launch.sh >/tmp/game.log 2>&1 &

# ---- 8. block on KasmVNC so the container stays up ----------------------
wait "$KASM_PID"
