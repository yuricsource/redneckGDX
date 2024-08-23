// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Screens;

import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.art.DynamicArtEntry;
import ru.m210projects.Redneck.Globals;
import ru.m210projects.Redneck.Sounds;
import ru.m210projects.Redneck.filehandle.AnimFile;

import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.RR66;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Sounds.sound;

public class AnmScreen extends MovieScreen {

    private int lastanimhack;

    public AnmScreen(BuildGame game) {
        super(game, Globals.TILE_ANIM);

        this.nFlags |= 4;
    }

    public static void logoanimsounds(int fr, int num) {
        switch (num) {
            case 0: // intro
                if (fr == 1) {
                    sound(29);
                }
                break;
            case 1: // interplay
                if (fr == 1) {
                    sound(478);
                }
                break;
            case 2: // xatrix
                if (fr == 1) {
                    sound(479);
                }
                break;
            case 5: // episode 1
                if (fr == 1 && currentGame.getCON().type != RR66) {
                    sound(35);
                }
                break;
            case 6: // episode 2
                if (fr == 1 && currentGame.getCON().type != RR66) {
                    sound(82);
                }
                break;
        }
    }

    public boolean init(String fn, int t) {
        if (isInited()) {
            return false;
        }

        if (!open(fn)) {
            return false;
        }

        lastanimhack = t;
        frame = 1;

        return true;
    }

    @Override
    protected boolean play() {
        Renderer renderer = game.getRenderer();
        if (mvfil != null) {
            if (LastMS == -1) {
                LastMS = engine.getCurrentTimeMillis();
            }
            DynamicArtEntry pic = (DynamicArtEntry) engine.getTile(TILE_MOVIE);

            long ms = engine.getCurrentTimeMillis();
            long dt = ms - LastMS;
            mvtime += dt;
            float tick = mvfil.getRate();
            int numFrames = mvfil.getFrames();
            if (mvtime >= tick) {
                if (frame < numFrames) {
                    pic.copyData(DoDrawFrame(frame));
                    frame++;
                }
                mvtime -= (long) tick;
            }
            LastMS = ms;

            if (pic.getWidth() <= 0) {
                return false;
            }

            if (pic.getBytes() != null) {
                renderer.rotatesprite(nPosX << 16, nPosY << 16, nScale, 512, TILE_MOVIE, 0, 0, nFlags);
            }

            return frame < numFrames;
        }
        return false;
    }

    @Override
    protected MovieFile GetFile(String file) {
        Entry entry = game.getCache().getEntry(file, true);
        if (!entry.exists()) {
            return null;
        }

        float rate = 1000f;
        if (file.equalsIgnoreCase("redneck.anm")) {
            rate = 700f;
        }
        if (file.equalsIgnoreCase("rr_outro.anm")) {
            rate = 1200f;
        }
        if (file.startsWith("lvl")) { // RRRA statistics screen
            rate = 2000f;
        }

        return new AnimFile(entry.getBytes(), rate);
    }

    @Override
    protected void StopAllSounds() {
        Sounds.StopAllSounds();
        Sounds.sndStopMusic();
    }

    @Override
    protected byte[] DoDrawFrame(int frame) {
        logoanimsounds(frame, lastanimhack);

        return mvfil.getFrame(frame);
    }

    @Override
    protected Font GetFont() {
        return game.getFont(0);
    }

    @Override
    protected void DrawEscText(Font font, int pal) {
        Renderer renderer = game.getRenderer();
        int shade = 16 + mulscale(16, EngineUtils.sin((20 * engine.getTotalClock()) & 2047), 16);
        font.drawTextScaled(renderer, 160, 5, "Press ESC to skip", 1.0f, shade, pal, TextAlign.Center, Transparent.None, ConvertType.Normal, true);
    }

}
