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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import ru.m210projects.Build.Architecture.common.audio.Source;
import ru.m210projects.Build.Render.Renderer;


import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.sound;

public class EndScreen extends ScreenAdapter {

    private Source voice;

    @Override
    public void show() {
        engine.setbrightness(cfg.getPaletteGamma(), engine.getPaletteManager().getBasePalette());
        engine.getTimer().reset();
        StopAllSounds();
        voice = sound(35);
    }

    @Override
    public void render(float delta) {
        Renderer renderer = game.getRenderer();
        renderer.clearview(0);

        if ((engine.getTotalClock() >> 4 & 1) != 0) {
            renderer.rotatesprite(0, 0, 65536, 0, 8677, 0, 0, 2 + 8 + 16 + 64 + 128);
        } else {
            renderer.rotatesprite(0, 0, 65536, 0, 8678, 0, 0, 2 + 8 + 16 + 64 + 128);
        }

        if (engine.getTotalClock() > 500 && (voice == null || !voice.isPlaying())) {
            Gdx.app.postRunnable(() -> game.changeScreen(gStatisticScreen));
        }

        engine.nextpage(delta);
    }

    public void episode1() {
        Gdx.app.postRunnable(() -> {
            String filename = "turdmov.anm";
            if (currentGame.getCON().type == RR66) {
                filename = "turd66.anm";
            }

            if (gAnmScreen.init(filename, 6)) {
                gAnmScreen.setCallback(() -> game.changeScreen(gStatisticScreen));
                game.changeScreen(gAnmScreen.escSkipping(true));
            } else {
                game.changeScreen(gStatisticScreen);
            }
        });
    }

    public void episode2() {
        Gdx.app.postRunnable(() -> {
            if (currentGame.getCON().type == RRRA) {
                game.changeScreen(gEndScreen);
                return;
            }

            String filename = "rr_outro.anm";
            if (currentGame.getCON().type == RR66) {
                filename = "end66.anm";
            }

            if (gAnmScreen.init(filename, 5)) {
                gAnmScreen.setCallback(() -> game.changeScreen(gStatisticScreen));
                game.changeScreen(gAnmScreen.escSkipping(true));
            } else {
                game.changeScreen(gStatisticScreen);
            }
        });
    }
}
