// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Kirill Klimenko-KLIMaka 
// and Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

public class MenuPicnum extends MenuItem
{
	public int nTile;
	public final int defTile;
	
	public MenuPicnum(String text, int nFondId, int x, int y, int nTile, int defTile) 
	{
		this.flags = 1;
		this.m_pMenu = null;
		
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = nFondId;
		this.x = x;
		this.y = y;
		this.nTile = nTile;
		this.defTile = defTile;
	}
	
	@Override
	public void draw() {
		int shade = 32;
		if ( mGetFocusedItem(m_pMenu, this) )
			shade = 32 - (totalclock & 0x3F);
		if ( text != null ) {
			mDrawText(textStyle, text, x, y, shade, 0, 0, 0);
		}
		int stat, picnum, ang;
		if(m_pMenu != null) {
			stat = 64 | 2 | 8;
			picnum = nTile;
		    ang = 0;
		} else {
			stat = 64 | 4 | 2 | 8; //70
			picnum = 0; //nextMenu->nTile
		    ang = 512;
		}
		
		int scale = 0x10000;
		if(picnum == defTile)
			scale = 0x8000;

		engine.rotatesprite(x << 16, y << 16, scale, ang, picnum, 0, 0, stat, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		return mNavigation(pMenu, opt);
	}
	
	@Override
	public void open(MENU pMenu) {

	}
	
	@Override
	public void close(MENU pMenu) {

	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}
}