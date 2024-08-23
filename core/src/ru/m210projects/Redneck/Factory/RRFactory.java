// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.FontHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Build.osd.OsdFunc;
import ru.m210projects.Redneck.Fonts.ConsoleFont;
import ru.m210projects.Redneck.Fonts.GameFont;
import ru.m210projects.Redneck.Fonts.MenuFont;
import ru.m210projects.Redneck.Fonts.MiniFont;
import ru.m210projects.Redneck.Main;

import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.LOADSCREEN;

public class RRFactory extends BuildFactory {

    private final Main app;

    public RRFactory(Main app) {
        super("redneck.grp");
        this.app = app;

        OsdColor.DEFAULT.setPal(100); // WHITE
    }

    @Override
    public void drawInitScreen() {
        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(160 << 16, 100 << 16, 65536, 0, LOADSCREEN, 0, 0, 2 | 8);
    }

    @Override
    public Engine engine() throws Exception {
        return Main.engine = new RREngine(app);
    }

    @Override
    public Renderer renderer(RenderType type) {
        if (type == RenderType.Software) {
            return new RRSoftware(app.pCfg);
        } else if (type == RenderType.PolyGDX) {
            return new RRPolygdx(app.pCfg);
        } else {
            return new RRPolymost(app.pCfg);
        }
    }

    @Override
    public DefScript getBaseDef(Engine engine) {
        return new DefScript(engine);
    }

    @Override
    public OsdFunc getOsdFunc() {
        return new RROsdFunc();
    }

    @Override
    public MenuHandler menus() {
        return app.menu = new RRMenuHandler(app);
    }

    @Override
    public FontHandler fonts() {
        return new FontHandler(5) {
            @Override
            protected Font init(int i) {
                switch (i) {
                    case 0:
                        return new MiniFont(app.pEngine);
                    case 1:
                        return new GameFont(app.pEngine);
                    case 2:
                        return new MenuFont(app.pEngine);
                    case 3:
                        return new ConsoleFont(app.pEngine);
                    default:
                        return EngineUtils.getLargeFont();
                }
            }
        };
    }

    @Override
    public BuildNet net() {
        return new RRNetwork(app);
    }

    @Override
    public SliderDrawable slider() {
        return new RRSliderDrawable();
    }

}
