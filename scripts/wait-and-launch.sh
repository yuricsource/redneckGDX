#!/bin/bash
# Xpra --start-child wrapper: block until the user has uploaded a grp,
# then exec the game so Xpra owns the JVM process directly and can
# surface exit codes.

set -euo pipefail

DATA=/data
GRP="$DATA/redneck.grp"

echo "[wait-and-launch] pid=$$ waiting for $GRP"
while [ ! -f "$GRP" ]; do
    inotifywait -q -e create -e moved_to "$DATA" >/dev/null 2>&1 || sleep 1
done
echo "[wait-and-launch] grp present ($(stat -c%s "$GRP") bytes) — exec'ing game"

cd "$DATA"
exec java \
    -Djava.awt.headless=false \
    -Dorg.lwjgl.util.NoChecks=true \
    -jar /opt/rrgdx/game.jar "$DATA"
