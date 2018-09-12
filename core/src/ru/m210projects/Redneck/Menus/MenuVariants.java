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
import static ru.m210projects.Redneck.Names.BIGFNTCURSOR;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Build.Engine.*;

import com.badlogic.gdx.Input.Keys;

public class MenuVariants extends MenuTitle
{
	public MenuVariants(String text, int textStyle, int x, int y) {
		super(text, textStyle, x, y, -1);
		this.flags = 3 | 4;
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		if(getInput().getKey(Keys.Y) != 0 || opt == 6 || opt == 11) {
			positive();
		    getInput().setKey(Keys.Y, 0);
		}
		if(getInput().getKey(Keys.N) != 0 || opt == 7 || opt == 18) {
			negative();
			getInput().setKey(Keys.N, 0);
		}
		return 0;
	}
	
	@Override
	public void draw() {
		super.draw();
		engine.rotatesprite((x)<<16,(y + tilesizy[BIGFNTCURSOR] - 4)<<16,8192,0,SPINNINGNUKEICON+(((totalclock>>3))&15),0,0,10,0,0,xdim-1,ydim-1);
	}
	
	public void positive()
	{
		mClose();
	}
	
	public void negative()
	{
		mMenuBack();
	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}
}