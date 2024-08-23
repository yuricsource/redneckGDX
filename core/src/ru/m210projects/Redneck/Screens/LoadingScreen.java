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

package ru.m210projects.Redneck.Screens;

import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;

import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Main.UserFlag;

import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;

public class LoadingScreen extends LoadingAdapter {

    private final Main app;

    public LoadingScreen(Main game) {
        super(game);
        this.app = game;
    }

    @Override
    public void draw(String title, float delta) {
        Renderer renderer = game.getRenderer();
        renderer.clearview(129);
        renderer.rotatesprite(320 << 15, 200 << 15, 65536, 0, LOADSCREEN, 0, 0, 2 + 8 + 64);

        if (title == null) {
            app.getFont(2).drawTextScaled(renderer, 160, 90 + 16 + 8, "Please wait", 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        } else {
            if (mUserFlag == UserFlag.UserMap) {
                app.getFont(2).drawTextScaled(renderer, 160, 90, "Entering usermap", 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            } else {
                app.getFont(2).drawTextScaled(renderer, 160, 90, "Entering ", 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
            app.getFont(2).drawTextScaled(renderer, 160, 90 + 16 + 8, title, 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }
    }

    @Override
    public void show() {
        super.show();
        StopAllSounds();
        engine.setbrightness(cfg.getPaletteGamma(), engine.getPaletteManager().getBasePalette());
    }

}
