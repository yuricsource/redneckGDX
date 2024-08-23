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

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.*;

public class MenuFont extends Font {

    public MenuFont(Engine draw) {
        this.size = draw.getTile(BIGALPHANUM).getHeight() / 2;
        this.addCharInfo(' ', new CharInfo(this, -1, 0.5f, 10));
        update();
    }

    protected void setChar(char ch, int nTile) {
        int width = engine.getTile(nTile).getWidth();
        this.addCharInfo(ch, new CharInfo(this, nTile, 0.5f, width, width, 0, 0));
    }

    public void update() {
        for (int i = 0; i < 26; i++) {
            int nTile = i + BIGALPHANUM;

            setChar((char) ('A' + i), nTile);
            setChar((char) ('a' + i), nTile);
        }

        for (int i = 0; i < 10; i++) {
            int nTile = i + BIGALPHANUM - 10;
            setChar((char) ('0' + i), nTile);
        }
        setChar('-', BIGALPHANUM - 11);
        setChar('.', BIGPERIOD);
        setChar(',', BIGCOMMA);
        setChar('!', BIGX);
        setChar('\'', BIGAPPOS);
        setChar('?', BIGQ);
        setChar(';', BIGSEMI);
        setChar(':', BIGCOLIN);
    }
}
