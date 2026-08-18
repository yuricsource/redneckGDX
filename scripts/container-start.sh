#!/bin/bash
# Container supervisor. Xpra owns display + WM + audio pump + HTML5 client
# on port 14500. Nginx fronts the whole thing on 8080 (landing page,
# upload endpoint, proxy to Xpra). The game is added to the Xpra session
# once the grp lands.

set -euo pipefail

DATA=/data
GRP="$DATA/redneck.grp"
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

# ---- 2. nginx (serves landing + proxies /xpra/ to Xpra) ------------------
log "starting upload server (8081)"
python3 /opt/rrgdx/upload_server.py >/tmp/upload.log 2>&1 &

log "starting nginx (8080)"
nginx -g 'daemon off;' &

# ---- 3. Xpra: display + WM + HTML5 client + audio ------------------------
# We use `xpra start` (not shadow) so Xpra owns the X server; Xvfb comes up
# as a side-effect. --html=on serves the HTML5 client at :14500/. --speaker
# with opus codec streams pulseaudio's null_sink to the browser tab.
# --start-child=fluxbox gives the game a WM so fullscreen requests stick.
# We deliberately DO NOT start the game yet — that comes after the grp
# arrives; Xpra's session stays up and the game is added via `xpra control`.
log "starting Xpra on :${DISPLAY_NUM}, HTML5 on :${XPRA_PORT}"
xpra start ":${DISPLAY_NUM}" \
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
    --start=fluxbox \
    --exit-with-children=no \
    --sharing=yes \
    --resize-display="${DISPLAY_W}x${DISPLAY_H}" \
    --xvfb="Xvfb +extension GLX +extension RANDR +extension Composite -screen 0 ${DISPLAY_W}x${DISPLAY_H}x24+32 -nolisten tcp -noreset" \
    >/tmp/xpra.log 2>&1 &
XPRA_PID=$!

# Wait for Xpra to be ready before we go on to the grp-wait: users hitting
# the landing page shouldn't get 502 on /xpra/ while it's still booting.
for i in $(seq 1 60); do
    if curl -sf "http://127.0.0.1:${XPRA_PORT}/" >/dev/null 2>&1; then
        log "xpra HTML5 ready"; break
    fi
    sleep 0.5
done

# ---- 4. seed redneckgdx.ini so the game boots at the Xpra display size ---
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

# ---- 5. wait for the grp -------------------------------------------------
if [ ! -f "$GRP" ]; then
    log "waiting for $GRP (upload via http://<host>:8080/)"
    while [ ! -f "$GRP" ]; do
        inotifywait -q -e create -e moved_to "$DATA" >/dev/null || sleep 1
    done
fi
log "grp present ($(stat -c%s "$GRP") bytes) — launching game inside Xpra session"

# ---- 6. fullscreen enforcer ---------------------------------------------
(
    for _ in $(seq 1 60); do
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

# ---- 7. launch the game inside Xpra --------------------------------------
# `xpra control` sends a command to the running server to start a new child.
# The game inherits DISPLAY=:100 and pulseaudio so audio is captured.
xpra control ":${DISPLAY_NUM}" start-child \
    "java -Djava.awt.headless=false -Dorg.lwjgl.util.NoChecks=true -jar /opt/rrgdx/game.jar ${DATA}" \
    >/tmp/xpra-game-start.log 2>&1 || \
    log "xpra control start-child failed; see /tmp/xpra-game-start.log"

# ---- 8. block on Xpra so the container stays up --------------------------
wait "$XPRA_PID"
