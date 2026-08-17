// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

package ru.m210projects.Redneck.html;

import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

// Browser entry point analogue of DesktopLauncher.
//
// This class is compiled but not yet wired to a running TeaVM output — see
// html/build.gradle for the pipeline-activation TODO. The class shape matches
// the intended integration: build a Config against the TeaVM virtual FS root,
// force the PolyGDX renderer (Polymost uses fixed-function GL that WebGL
// forbids), then hand off to the TeaVM libGDX launcher.
public class HtmlLauncher {

    public static final String appname = "RedneckGDX";

    public static void main(String[] args) throws IOException {
        // TeaVM's virtual FS mounts preloaded assets at a stable root.
        GameConfig cfg = new Config(Paths.get("/rrgdx", (appname + ".ini").toLowerCase(Locale.ROOT)));
        cfg.load();
        cfg.setGamePath(cfg.getCfgPath().getParent());

        // Force the modern GL renderer. Polymost (default) uses fixed-function
        // GL that WebGL rejects; PolyGDX uses libGDX's GL20/GL30 abstraction
        // and is WebGL-friendly.
        forcePolyGdx(cfg);

        Main game = new Main(Collections.<String>emptyList(), cfg, appname, "web", false);

        // TeaVM launcher wiring goes here (once the gdx-teavm gradle plugin
        // integration is complete). Expected shape:
        //
        //   TeaApplicationConfiguration teaCfg = new TeaApplicationConfiguration("gameCanvas");
        //   teaCfg.width = 640;
        //   teaCfg.height = 480;
        //   new TeaApplication(game, teaCfg);
        //
        // Keeping the instantiation above so class-loading errors surface even
        // in the current scaffold-only build.
        System.out.println("[HtmlLauncher] Main instantiated: " + game.getClass().getName());
    }

    private static void forcePolyGdx(GameConfig cfg) {
        // Renderer selection lives in BuildGDX's GameConfig. The exact method
        // name has churned across versions; try the current one and fall back.
        try {
            cfg.getClass()
                .getMethod("setRenderType", Class.forName("ru.m210projects.Build.Render.Renderer$RenderType"))
                .invoke(cfg, Class.forName("ru.m210projects.Build.Render.Renderer$RenderType")
                    .getField("PolyGDX").get(null));
        } catch (Throwable t) {
            System.err.println("[HtmlLauncher] Could not force PolyGDX renderer: " + t);
        }
    }
}
