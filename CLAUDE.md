# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

RedneckGDX is a Java port of the *Redneck Rampage* game (id-tech / Build-engine derivative) built on top of two shared libraries maintained by the same author:

- **BuildGDX** — a Java reimplementation of Ken Silverman's Build engine. Referenced as an external IntelliJ module (`module-name="BuildGDX"` in `RedneckGDX.iml`); it is **not** vendored in this repo.
- **libGDX** — cross-platform game framework used for the LWJGL desktop backend, input, audio, and OpenGL access.

Both must be available on the classpath before this project can compile.

Original code license is GPLv3 (`gpl3.txt`); the underlying Build engine has its own separate license (`buildlic.txt`) — any modifications to Build-derived files must preserve the Ken Silverman notice described there.

## Build & run

There is no Gradle/Maven configuration checked in — the `.gitignore` actively excludes `build.gradle` / `settings.gradle`. The canonical build environment is **IntelliJ IDEA** using `RedneckGDX.iml`, which declares:

- Source roots: `core/src` (Java) and `core/res` (resources — contains `rrgdx.dat`, the bundled game data patch)
- Dependency on sibling IntelliJ module `BuildGDX` and project libraries `libgdx`, `gdx`, `annotations-24.1.0`
- Desktop launcher lives in `desktop/src` (needs to be added as its own module/source root pointing at the same JDK and the `core` module)

Run configuration:

- **Main class:** `ru.m210projects.Redneck.desktop.DesktopLauncher`
- **Program argument (required, `arg[0]`):** absolute path to the directory containing game data (`redneck.grp`, etc.). The launcher stores `redneckgdx.ini` alongside the grp and treats that directory as the game path.

No test suite, linter, or formatter is configured.

## Architecture

### Entry flow

`DesktopLauncher` (LWJGL) → constructs `Config` (extends `BuildGDX GameConfig`) → hands control to `Main extends BuildGame`. `Main` wires up screens, the engine, the menu handler, and networking, and keeps most globally-shared game state as `public static` fields (see `Main.game`, `Main.engine`, `Main.cfg`, plus the `Globals`, `Names`, `Sounds` static-import "namespace" pattern used throughout).

### The Factory pattern (`Factory/`)

This directory contains all subclasses that plug the game into BuildGDX's abstract framework. When looking for how a BuildGDX interface is implemented for Redneck, start here:

- `RRFactory` — top-level `BuildFactory`; constructs engine, fonts, renderer, menu handler, network. This is the wiring hub.
- `RREngine` — Redneck's `Engine` subclass.
- `RRRenderer` / `RRSoftware` / `RRPolymost` / `RRPolygdx` / `RRDummyRenderer` — the four selectable renderers (classic software, Polymost GL, "Polygdx" modern GL, headless dummy).
- `RRGameProcessor`, `RRNetwork`, `RRMenuHandler`, `RRPaletteManager`, `RRBoardService`, `RROsdFunc`, `RRPrompt`, `RRSliderDrawable`, `RRMapSettings` — game-specific overrides of BuildGDX behavior.

### Screens (`Screens/`) and Menus (`Menus/`)

`Screens/*Screen.java` are libGDX `Screen`s representing top-level game states (game, loading, statistics, end-of-episode, network lobby, demo playback, and full-motion cutscene players `AnmScreen`/`MVEScreen`). `Menus/` holds all in-engine UI menus, which are pushed/popped by `RRMenuHandler`.

### Core gameplay modules (flat files under `Redneck/`)

Ported closely from the original C source; they interoperate through shared static state in `Globals`, `Names`, and `Sounds`, plus the `Types/` structs (`PlayerStruct`, `UserDefs`, `Weaponhit`, `ANIMATION`, `EpisodeInfo`, `MapInfo`, `SafeLoader`, etc.). Notable files: `Actors`, `Animate`, `Player`, `Weapons`, `Spawn`, `Premap`, `View`, `Screen`, `LoadSave`, `Gamedef` (CON script compiler), `Cheats`, `ResourceHandler`.

### Custom file formats (`filehandle/`)

Redneck-specific decoders that BuildGDX doesn't provide: `VOCDecoder` (Creative Voice audio), `MVEFile` (Interplay full-motion video), `AnimFile` (DP ANIM cutscenes), `RTSFile` (remote taunt sounds), `DemoFile`/`DemoRecorder`, `LZWInputStream`/`LZWOutputStream` (used for save-game compression). `EpisodeEntry` and `UserEntry` describe user-content and episode registration inside a mounted group file.

### Game-variant flags

`Globals` defines three variants of the game that runtime code branches on: `RR` (Redneck Rampage), `RR66` (v1.66), `RRRA` (Rides Again). Weapons and some content differ (`MAX_WEAPONS` vs `MAX_WEAPONSRA`).

### Coding conventions worth knowing

- Heavy use of `static` state and `import static` — most modules pull in `Globals.*`, `Names.*`, `Engine.*`, `Sounds.*` to mirror the original C source's flat namespace. When adding code, follow this pattern rather than fighting it; refactoring to instance state can cascade across dozens of files.
- Any file modified from Ken Silverman's Build source **must** carry the "This file has been modified from Ken Silverman's original release" notice per `buildlic.txt` clause 4C.
