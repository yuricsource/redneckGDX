// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

package ru.m210projects.Redneck.html;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;

import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

// Browser entry point analogue of DesktopLauncher. TeaVM compiles this
// method to a global main() in the generated app.js.
public class HtmlLauncher {

    public static final String appname = "RedneckGDX";

    public static void main(String[] args) throws IOException {
        // TeaVM's virtual FS mounts preloaded assets at /assets/. The user's
        // redneck.grp arrives via IndexedDB (see html/webapp/index.html).
        GameConfig cfg = new Config(Paths.get("/assets", (appname + ".ini").toLowerCase(Locale.ROOT)));
        cfg.load();
        cfg.setGamePath(cfg.getCfgPath().getParent());

        // Force the modern GL renderer. Polymost (BuildGDX default) uses
        // fixed-function GL that WebGL rejects; PolyGDX goes through
        // libGDX's GL20/GL30 abstraction.
        forcePolyGdx(cfg);

        Main game = new Main(Collections.<String>emptyList(), cfg, appname, "web", false);

        WebApplicationConfiguration webCfg = new WebApplicationConfiguration("canvas");
        webCfg.width = 640;
        webCfg.height = 480;
        webCfg.useGL30 = true;
        new WebApplication(game, webCfg);
    }

    private static void forcePolyGdx(GameConfig cfg) {
        // Renderer selection lives in BuildGDX's GameConfig. The exact
        // method name has churned across versions; try reflection so we
        // don't hard-fail if the API shifts.
        try {
            Class<?> renderTypeCls = Class.forName("ru.m210projects.Build.Render.Renderer$RenderType");
            Object polyGdx = renderTypeCls.getField("PolyGDX").get(null);
            cfg.getClass().getMethod("setRenderType", renderTypeCls).invoke(cfg, polyGdx);
        } catch (Throwable t) {
            System.err.println("[HtmlLauncher] Could not force PolyGDX renderer: " + t);
        }
    }
}
