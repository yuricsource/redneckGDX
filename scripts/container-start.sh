#!/bin/bash
# Container supervisor. Xpra owns display + WM + audio pump + HTML5 client
# on port 14500 and manages the game process directly via --start-child
# (which invokes wait-and-launch.sh, blocking until the grp arrives).
# Nginx fronts everything on :8080 (landing page, upload endpoint, /xpra/
# proxy).

set -euo pipefail

DATA=/data
INI="$DATA/redneckgdx.ini"

DISPLAY_NUM=100
DISPLAY_W=${DISPLAY_W:-1280}
DISPLAY_H=${DISPLAY_H:-720}
XPRA_PORT=14500

log() { echo "[$(date -u +%H:%M:%S)] $*"; }

# ---- 1. pulseaudio (Xpra will capture from it) ---------------------------
log "starting pulseaudio (null_sink; Xpra captures for browser audio)"
mkdir -p /var/run/pulse /var/lib/pulse
pulseaudio --system --disallow-exit --disallow-module-loading=false \
    --exit-idle-time=-1 --daemonize=yes --log-target=file:/tmp/pulseaudio.log \
    --load="module-native-protocol-unix" \
    --load="module-null-sink sink_name=null_out sink_properties=device.description=NullSink" \
    --load="module-suspend-on-idle" >/dev/null 2>&1 || \
    log "pulseaudio failed to start (game will run silently)"
export PULSE_SERVER=unix:/var/run/pulse/native

# ---- 2. nginx + upload server -------------------------------------------
log "starting upload server (8081)"
python3 /opt/rrgdx/upload_server.py >/tmp/upload.log 2>&1 &

log "starting nginx (8080)"
nginx -g 'daemon off;' &

# ---- 3. seed redneckgdx.ini so the game boots fullscreen ----------------
mkdir -p "$DATA"
if [ ! -f "$INI" ]; then
    log "seeding $INI (Fullscreen=true, ${DISPLAY_W}x${DISPLAY_H})"
    cat > "$INI" <<EOF
[Screen]
Fullscreen = true
ScreenWidth = ${DISPLAY_W}
ScreenHeight = ${DISPLAY_H}

[Controls]
UseMouse = true
MouseAiming = true
RawMouseInput = true
EOF
fi

# ---- 4. fullscreen enforcer (background loop) ---------------------------
# Belt-and-suspenders for the seeded Fullscreen=true: force the game
# window fullscreen once it appears.
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

# ---- 5. Xpra: display + WM + HTML5 client + audio + game ----------------
# --start=fluxbox: WM the game requests fullscreen against.
# --start-child=wait-and-launch.sh: blocks until grp is uploaded, then
#   exec's the JVM. Xpra owns the process and its stdout/stderr are
#   captured in the Xpra log.
# --exit-with-children=no: keep the Xpra session alive if the game
#   exits (crash or user quit) so the container stays up and a refresh
#   works.
log "starting Xpra on :${DISPLAY_NUM}, HTML5 on :${XPRA_PORT}"
exec xpra start ":${DISPLAY_NUM}" \
    --daemon=no \
    --bind-tcp="0.0.0.0:${XPRA_PORT}" \
    --html=on \
    --auth=allow \
    --tcp-auth=allow \
    --mdns=no \
    --pulseaudio=no \
    --speaker=on \
    --speaker-codec=opus \
    --microphone=off \
    --webcam=no \
    --notifications=no \
    --system-tray=no \
    --clipboard=yes \
    --file-transfer=no \
    --printing=no \
    --pixel-depth=24 \
    --sharing=yes \
    --resize-display="${DISPLAY_W}x${DISPLAY_H}" \
    --xvfb="Xvfb +extension GLX +extension RANDR +extension Composite -screen 0 ${DISPLAY_W}x${DISPLAY_H}x24+32 -nolisten tcp -noreset" \
    --start=fluxbox \
    --start-child=/opt/rrgdx/wait-and-launch.sh \
    --exit-with-children=no \
    --log-dir=/tmp \
    --log-file=xpra.log
