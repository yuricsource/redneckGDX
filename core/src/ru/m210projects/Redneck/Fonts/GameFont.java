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
import static ru.m210projects.Build.Strhandler.isdigit;
import static ru.m210projects.Redneck.Names.STARTALPHANUM;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class GameFont extends BuildFont {

	public GameFont(Engine draw) {
		super(draw, tilesizy[STARTALPHANUM] / 2 + 2, 32768, 8 | 16);

		this.addChar(' ', nSpace, 3, nScale, 0, 0);
		int nTile = STARTALPHANUM;

		for(int i = 0; i < 95; i++) {
			if(tilesizx[nTile + i] != 0) {
				char symbol = (char) (i + '!');
				this.addChar(symbol, nTile + i, isdigit(symbol) ? 8 : tilesizx[nTile + i] / 2, nScale, 0, 0);
			}
		}
	}
	
	public void update()
	{
		int nTile = STARTALPHANUM;
		for(int i = 0; i < 95; i++) {
			if(tilesizx[nTile + i] != 0) {
				char symbol = (char) (i + '!');
				charInfo[symbol].nWidth = isdigit(symbol) ? 8 : (short) (tilesizx[nTile + i] / 2);
			}
		}
	}
}
