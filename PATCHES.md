# Vendored-Submodule Patches

Some patches to vendored submodules are required for the build to succeed on
modern Gradle/Kotlin. They live out-of-tree so the submodules stay at their
official upstream SHAs and can be updated cleanly. Apply them by running:

```sh
./scripts/patch-vendored.sh
```

The Dockerfile runs this automatically before invoking Gradle. Local dev
should run it once after `git submodule update --init --recursive`, and any
time you `git submodule update`.

## Applied by `patch-vendored.sh` (required for `:html:dist` — CI-critical)

### 1. `external/gdx-teavm` (plugin) — Gradle 8 Kotlin nullability

`tools/gdx-teavm-plugin/src/main/kotlin/com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMGradlePlugin.kt`,
two sites (~lines 288, 526 in tag 1.6.1):

```diff
-group = null
+group = ""  // Gradle 8's Kotlin API types `group` as non-null String
```

Without this, `:gdx-teavm-plugin:compileKotlin` fails with
`Null can not be a value of a non-null type String`, which cascades into any
`html/build.gradle` evaluation because the `plugins { id 'com.github.xpenatan.gdx-teavm' }`
block forces the plugin to compile.

## Manual patches needed for `:html:assembleWeb` (real TeaVM game compile)

These are NOT applied by the script yet — they need per-site engineering
work and are only required if you're iterating on the real browser game
build (not the CI placeholder).

## 2. `external/BuildGDX` (game engine) — MD4 inherits from missing class

`core/src/ru/m210projects/Build/Types/MD4.java`:

TeaVM 0.15.0's classlib does not include `java.security.MessageDigest`, so
`MD4Digest extends MessageDigest` is unloadable. Rewrite it as a standalone
class with thin `update(byte[])` / `digest()` methods, drop the `super("MD4")`
constructor call, and strip `@Override` from `engineReset` / `engineUpdate` /
`engineDigest`.

Full patched file: see `git log` or apply the diff at any BuildGDX 1.18-era
`MD4.java`.

## Further TeaVM-classlib gaps (BuildGDX)

Even with the two patches above, `generateJavaScript` reports SEVERE errors
for the following classlib gaps and refuses to emit output. Each needs a
per-site patch in BuildGDX (or a stub in the `:html` module):

| Missing symbol | BuildGDX site |
| --- | --- |
| `java.util.StringJoiner` | `Properties.java:40`, `GrpFile.toString`, `InputContext.save` (5 sites) |
| `java.net.NetworkInterface`, `Inet4Address`, `InetAddress` | `WaifUPnp/GatewayFinder.getLocalIPs` |
| `java.net.DatagramSocket`, `DatagramPacket`, `InetSocketAddress` | `WaifUPnp/GatewayFinder$GatewayListener.run` |
| `java.lang.System.exit(int)` | `BuildGame.ThrowError` (3 sites) |
| `java.lang.Runtime.maxMemory()` | `MemLog.total` (called from `InitScreen` precache thread) |
| `java.lang.ClassLoader.getResource` | `Cache.loadGdxDef` |
| `java.io.FileInputStream.getChannel` | one asset loader |

All of these are called from single-user-irrelevant paths (multiplayer NAT
traversal, debug logging, forced-exit dialog, save-game checksum). The
minimum viable browser build strips WaifUPnp entirely (disable multiplayer
for HTML5) and replaces `System.exit` / `Runtime.maxMemory` with no-ops in
a browser-only override module.
