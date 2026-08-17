#!/bin/sh
# Applies out-of-tree patches to vendored submodules that are required for
# the Gradle build to succeed on modern Gradle/Kotlin. Idempotent — safe to
# rerun. See PATCHES.md for the reasoning behind each patch.
#
# Called from the Dockerfile before ./gradlew and from local dev workflows.

set -eu

repo_root="$(cd "$(dirname "$0")/.." && pwd)"

# 1) gdx-teavm plugin: Gradle 8's Kotlin API types Task.group as non-null String.
plugin_kt="$repo_root/external/gdx-teavm/tools/gdx-teavm-plugin/src/main/kotlin/com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMGradlePlugin.kt"
if [ -f "$plugin_kt" ] && grep -q 'group = null' "$plugin_kt"; then
    sed -i 's/group = null/group = ""/g' "$plugin_kt"
    echo "patched: $plugin_kt (group = null -> group = \"\")"
fi
