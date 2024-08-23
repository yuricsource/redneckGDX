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

package ru.m210projects.Redneck.Fonts;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Types.font.CharInfo;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.filehandle.art.ArtEntry;

import static ru.m210projects.Build.Strhandler.isdigit;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.STARTALPHANUM;

public class GameFont extends Font {

    public GameFont(Engine draw) {
        this.size = (draw.getTile(STARTALPHANUM).getHeight() + 2) / 2;
        this.addCharInfo(' ', new CharInfo(this, -1, 0.5f, 8));
        update();
    }

    public void update() {
        int nTile = STARTALPHANUM;
        for (int i = 0; i < 95; i++) {
            ArtEntry pic = engine.getTile(nTile + i);

            if (pic.getWidth() != 0) {
                char symbol = (char) (i + '!');
                int cellSize = isdigit(symbol) ? 16 : pic.getWidth();
                this.addCharInfo(symbol, new CharInfo(this, nTile + i, 0.5f, cellSize, cellSize, 0, 0) {
                    @Override
                    public int getWidth() {
                        return (int) ((pic.getWidth() + 1) * tileScale);
                    }

                    @Override
                    public int getHeight() {
                        return (int) ((pic.getHeight() + 1) * tileScale);
                    }
                });
            }
        }
    }

}
