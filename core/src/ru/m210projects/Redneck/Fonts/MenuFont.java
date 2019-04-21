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

import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Redneck.Names.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, tilesizy[BIGALPHANUM] / 2, 32768, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);
		for(int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;

			addChar((char) ('A' + i), nTile, tilesizx[nTile] / 2, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, tilesizx[nTile] / 2, nScale, 0, 0);
		}
		
		for(int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			addChar((char) ('0' + i), nTile, tilesizx[nTile] / 2, nScale, 0, 0);
		}
		addChar('-', BIGALPHANUM-11, tilesizx[BIGALPHANUM-11] / 2, nScale, 0, 0);
		addChar('.', BIGPERIOD, tilesizx[BIGPERIOD] / 2, nScale, 0, 0);
		addChar(',', BIGCOMMA, tilesizx[BIGCOMMA] / 2, nScale, 0, 0);
		addChar('!', BIGX, tilesizx[BIGX] / 2, nScale, 0, 0);
		addChar('\'', BIGAPPOS, tilesizx[BIGAPPOS] / 2, nScale, 0, 0);
		addChar('?', BIGQ, tilesizx[BIGQ] / 2, nScale, 0, 0);
		addChar(';', BIGSEMI, tilesizx[BIGSEMI] / 2, nScale, 0, 0);
		addChar(':', BIGCOLIN, tilesizx[BIGCOLIN] / 2, nScale, 0, 0);
	}

}
