package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Build.Engine.*;

public class MenuTitle extends MenuItem {
	public int nTile;
	public MenuTitle(String text, int textStyle, int x, int y, int nTile) {
		this.flags = 1;
		this.m_pMenu = null;
		this.width = 0;
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = textStyle;
		this.x = x;
		this.y = y;
		this.nTile = nTile;
	}
	
	@Override
	public void draw() {
		if ( text != null )
		{
		    mGetAlign(textStyle, null);
		    if(nTile != -1)
		    	engine.rotatesprite(160 << 16, y << 16, 65536, 0, nTile, 16, 0, 78, 0, 0, xdim - 1, ydim - 1);
		    
		    int yoff = 0;
		    if(textStyle == 2) yoff = 12;
		    
		    mDrawText(textStyle, text, x, y - aligny / 2 + yoff, -128, 0, 1, 0);
		}
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		return mNavigation(pMenu, opt);
	}

	@Override
	public void close(MENU pMenu) {
		
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}
}
