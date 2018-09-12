// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;

public class MenuPage extends MenuTitle {
	public MenuPage(int x, int y, int nTile) {
		super(null, 0, x, y, nTile);
	}
	
	@Override
	public void draw() {
		engine.rotatesprite(x << 16, y << 16, 65536, 0, nTile, -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}
	
	@Override
	public int callback(MENU pMenu, int opt) {

		switch(opt) {
		case MKLEFT:
		case MKBSPACE:
			mNavUp(pMenu);
			return 0;
		case MKRIGHT:
		case MKENTER:
		case MKSPACE:
			mNavDown(pMenu);
			return 0;
		case MKNONE:
		case MKANY:
		case MKUP:
		case MKDW:
		case MKESC:
		case MKDELETE:
		    return mNavigation(pMenu, opt);
		}

		return 0;
	}
}
