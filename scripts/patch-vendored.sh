#!/bin/sh
# Applies out-of-tree patches to vendored submodules that are required for
# the Gradle build to succeed on modern Gradle/Kotlin and for TeaVM to
# compile BuildGDX. Idempotent — safe to rerun. See PATCHES.md for the
# reasoning behind each patch.
#
# Called from the Dockerfile before ./gradlew and from local dev workflows.

set -eu

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
patches="$repo_root/scripts/patches"

# ------------------------------------------------------------------
# gdx-teavm plugin: Gradle 8's Kotlin API types Task.group as non-null.
# ------------------------------------------------------------------
plugin_kt="$repo_root/external/gdx-teavm/tools/gdx-teavm-plugin/src/main/kotlin/com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMGradlePlugin.kt"
if [ -f "$plugin_kt" ] && grep -q 'group = null' "$plugin_kt"; then
    sed -i 's/group = null/group = ""/g' "$plugin_kt"
    echo "patched: gdx-teavm GdxTeaVMGradlePlugin.kt (group = null -> \"\")"
fi

# ------------------------------------------------------------------
# BuildGDX: file-replacement patches for TeaVM classlib gaps.
# Copies each file from scripts/patches/BuildGDX/<path> to
# external/BuildGDX/<path>, preserving the tree.
# ------------------------------------------------------------------
if [ -d "$patches/BuildGDX" ]; then
    (cd "$patches/BuildGDX" && find . -type f -name '*.java' -print) | while IFS= read -r rel; do
        src="$patches/BuildGDX/$rel"
        dst="$repo_root/external/BuildGDX/$rel"
        if [ -f "$dst" ]; then
            if ! cmp -s "$src" "$dst"; then
                cp "$src" "$dst"
                echo "patched: BuildGDX/$rel"
            fi
        else
            mkdir -p "$(dirname "$dst")"
            cp "$src" "$dst"
            echo "added:   BuildGDX/$rel"
        fi
    done
fi
