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

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.kAngleMask;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.SLIDEBAR;
import static ru.m210projects.Redneck.Screen.coordsConvertYScaled;

public abstract class MenuItem {

	public MENU m_pMenu = null;
	public char[] text;          
	public int textStyle = -1;      
	public int x = 0;              
	public int y = 0;             
	public int width = 0;
	public int flags = 0;
	public int pal = 0;

	public abstract void draw();
	public abstract int callback(MENU pMenu, int opt);
	public abstract boolean mouseAction(int mx, int my);
	public abstract void open(MENU pMenu);
	public abstract void close(MENU pMenu);
	
	protected int mDrawSlider(int x, int y, int nPos, int len, boolean focus)
	{
		int barlen = tilesizx[SLIDEBAR] - 19;
		
		int ang = 512;
		int sy = y + 9;
		
		engine.rotatesprite(x + 11 << 16, y + 1 << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, 0, xdim-1, coordsConvertYScaled(sy));

		int clen = len;
		int dy = barlen;
		int posy = sy;
		while(clen > 0)
		{
			if(dy > clen) dy = clen;
			engine.rotatesprite(x + 11 << 16, (posy-8) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(posy), xdim-1, coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}

		int y2 = sy + len;
		engine.rotatesprite(x + 11 << 16, (y2 - tilesizx[SLIDEBAR] + 11) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(y2), xdim-1, ydim-1);
		
		int shade = 8;
		if(focus)
			shade += mulscale(16, sintable[(32 * totalclock) & kAngleMask], 16);

		engine.rotatesprite((x + 14)<< 16, (y + nPos + 10) << 16, 32768, 512, 623, shade, 2, 10 | 16, 0, 0, xdim-1, ydim-1);	

		return y + nPos + 5;
	}
}
