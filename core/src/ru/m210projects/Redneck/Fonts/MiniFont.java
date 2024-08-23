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

import static ru.m210projects.Redneck.Main.engine;

public class MiniFont extends ConsoleFont {

    public MiniFont(Engine draw) {
        super(draw);
        this.size /= 2;
        this.setVerticalScaled(true);
    }

    @Override
    protected void addChar(char ch, int nTile) {
        final float scale = 0.5f;

        this.addCharInfo(ch, new CharInfo(this, nTile, scale, 8, engine.getTile(nTile).getWidth(), 0, 0) {
            @Override
            public int getWidth() {
                return (int) ((engine.getTile(nTile).getWidth() + 1) * tileScale);
            }

            @Override
            public int getHeight() {
                return (int) ((engine.getTile(nTile).getHeight() + 1) * tileScale);
            }
        });
    }
}
