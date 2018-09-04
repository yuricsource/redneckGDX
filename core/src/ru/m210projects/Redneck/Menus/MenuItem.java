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
