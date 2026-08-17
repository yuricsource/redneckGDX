# syntax=docker/dockerfile:1

# ---------- Stage 1: build the browser bundle ----------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /src

# Cache Gradle downloads across builds.
COPY gradle/ ./gradle/
COPY gradlew settings.gradle build.gradle gradle.properties ./
RUN chmod +x ./gradlew && ./gradlew --version --no-daemon

# Copy the sources. The submodule content must be present in the build
# context; CI checks out with `submodules: recursive`, and local builds
# should do the same (git submodule update --init --recursive).
COPY buildgdx-core/    ./buildgdx-core/
COPY buildgdx-lwjgl3/  ./buildgdx-lwjgl3/
COPY core/             ./core/
COPY desktop/          ./desktop/
COPY html/             ./html/
COPY external/         ./external/
COPY scripts/          ./scripts/

# Apply required out-of-tree patches to vendored submodules. See PATCHES.md.
RUN sh ./scripts/patch-vendored.sh

RUN ./gradlew :html:dist --no-daemon

# ---------- Stage 2: nginx serves the static bundle ----------
FROM nginx:alpine

# Explicit MIME for .wasm — needed once the TeaVM pipeline activates.
# nginx:alpine's default mime.types already includes application/wasm, but
# pin it explicitly so future distro-image changes don't break the game.
RUN printf 'types {\n    application/wasm  wasm;\n}\n' > /etc/nginx/conf.d/wasm.conf

# no-cache on index.html so refreshes always pick up new bundle hashes.
RUN sed -i '/location \/ {/a\        add_header Cache-Control "no-cache" always;' /etc/nginx/conf.d/default.conf

COPY --from=build /src/html/build/dist/ /usr/share/nginx/html/

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s CMD wget -q -O - http://localhost/ >/dev/null || exit 1
