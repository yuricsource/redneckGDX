# Vendored-Submodule Patches

> **NOTE (2026-08-17):** The runnable browser experience is now delivered
> via streaming — see `Dockerfile.stream`, `scripts/container-start.sh`,
> and the noVNC front-end. The TeaVM route documented below is preserved
> for anyone who wants to pick it back up, but is **not** on the critical
> path for `docker compose up`. The streaming path does not need any of
> these patches applied at runtime; the plugin patch (§1 below) is still
> required today because `html/build.gradle` applies the plugin during
> configuration for the placeholder `:html:dist` task, which the build
> stage still evaluates.


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

## Also applied by `patch-vendored.sh` (partial coverage of `:html:assembleWeb`)

The script now also drops in three replacement files under `scripts/patches/BuildGDX/`:

### 2. `MD4.java` — MessageDigest dropped

`core/src/ru/m210projects/Build/Types/MD4.java`.
`MD4Digest` no longer `extends java.security.MessageDigest`; adds thin
`update(byte[])` / `digest()` wrappers, drops the `super("MD4")` call,
strips four `@Override` annotations. Fixes the hard
"class extends missing supertype" TeaVM error.

### 3. `WaifUPnp/GatewayFinder.java` — stub

Skeleton `abstract class GatewayFinder { public abstract void gatewayFound(Gateway g); }`,
constructor is a no-op. Eliminates NetworkInterface / DatagramSocket /
DatagramPacket / Inet4Address as reachable classes.

### 4. `WaifUPnp/UPnP.java` — stub

All static methods (`waitInit`, `isUPnPAvailable`, `openPortTCP`,
`openPortUDP`, `closePortTCP`, `closePortUDP`, `isMappedTCP`,
`isMappedUDP`, `getExternalIP`, `getLocalIP`) return the "not available"
answer (false / null). Multiplayer is disabled in the browser build.

## Still-blocking TeaVM compile errors (not yet patched)

Even with the patches above, `./gradlew :html:generateJavaScript`
currently fails on:

| Missing symbol | BuildGDX site |
| --- | --- |
| `java.util.StringJoiner` | `filehandle/grp/GrpFile.toString:169`, `settings/InputContext.save:161,169` (4 sites) |
| `java.net.InetAddress` / `InetSocketAddress` | `net/Mmulti` — 5 sites in `netinit`, `initmultiplayersparms` (multiplayer bootstrap paths) |
| `java.lang.System.exit(int)` | `Pattern/BuildGame.ThrowError:397`, `Pattern/ScreenAdapters/InitScreen.forceExit:294`, `Types/MemLog` |
| `java.lang.Runtime.maxMemory()` | `Types/MemLog.total` (debug logging) |
| `java.lang.ClassLoader.getResource` | `filehandle/Cache.loadGdxDef:215` |
| `java.io.FileInputStream.getChannel` | `filehandle/StreamUtils:233`, `Redneck/filehandle/MVEFile:1213` |

Each is a one-file surgery — mostly sed'able (System.exit,
Runtime.maxMemory, ClassLoader.getResource), with GrpFile / InputContext
needing manual StringJoiner→StringBuilder rewrites and Mmulti needing
specific line-level patches to guard the networking paths. Add to
`scripts/patches/BuildGDX/` and re-run `patch-vendored.sh`.

## Beyond compile-time: runtime blockers

Even after compile succeeds, the compiled `app.js` will surface
additional TeaVM issues at runtime that will need per-symptom debugging
in the browser console:
- Threading — TeaVM ignores `Thread.start()` silently, but `InitScreen`
  precache thread and networking threads may deadlock on `.join()`.
- Asset paths — Grp file upload flow needs to wire IndexedDB bytes into
  TeaVM virtual FS at `/rrgdx/redneck.grp` before `Main` constructs.
- Renderer — even with `RRPolygdx` forced, first-frame GL calls may
  reveal WebGL-incompatible pipeline setup deep in BuildGDX.
- Sound — Howler.js integration is auto-wired by gdx-teavm but
  BuildGDX's audio codec chain (VOC / MVE / OggDecoder) needs testing.

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
