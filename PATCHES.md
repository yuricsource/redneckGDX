# Vendored-Submodule Patches Needed for `:html:assembleWeb`

The CI/CD pipeline (`:html:dist` → Dockerfile → GHA) ships a placeholder landing
page. Compiling the real game to JS via `:html:assembleWeb` currently requires
two local patches to vendored submodules. Both are one-liner-ish and blocked
on getting them merged upstream.

## 1. `external/gdx-teavm` (plugin) — Gradle 8 Kotlin nullability

`tools/gdx-teavm-plugin/src/main/kotlin/com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMGradlePlugin.kt`,
around line 526:

```diff
 val pathingJar = project.tasks.register<Jar>(DEV_SERVER_CLASSPATH_TASK_NAME) {
-    group = null
+    group = ""  // Gradle 8's Kotlin API types `group` as non-null String
     description = "Creates a short TeaVM development-server classpath for Windows."
```

Without this, `:gdx-teavm-plugin:compileKotlin` fails with
`Null can not be a value of a non-null type String`.

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
