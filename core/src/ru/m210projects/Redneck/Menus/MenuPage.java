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
