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
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

public class MenuSwitch extends MenuItem
{
	public boolean value;
	public MENUPROC callback;
	char[] onMessage, offMessage;
	
	public MenuSwitch(String text, int nFondId, int x, int y, int width, boolean value, 
			MENUPROC callback, String onMessage, String offMessage) 
	{
		this.flags = 3 | 4;
		this.m_pMenu = null;
		
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = nFondId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.value = value;
		this.callback = callback;
		if(onMessage != null)
			this.onMessage = onMessage.toCharArray();
		else
			this.onMessage = new char[]{ 'O', 'n' };
		if(offMessage != null)
			this.offMessage = offMessage.toCharArray();
		else
			this.offMessage = new char[]{ 'O', 'f', 'f' };
	}
	
	@Override
	public void draw() {
		
		int shade = 8;
		int scale = 16384;
		if(textStyle == 2) scale = 32768;
		
		boolean focused = mGetFocusedItem(m_pMenu, this);
		if ( focused ) 
			shade = 8 - (totalclock & 0x3F);
		
		int pal = this.pal;
		if(pal == 0 && textStyle < 2) pal = 10;
		int yoff = 0;
	    if(textStyle == 2) yoff = 13;
	    
		if ( text != null )
			mDrawText(textStyle, text, x, y+yoff, shade, pal, 0, 0);
		char[] sw = offMessage;
		if(value) sw = onMessage;

		mGetAlign(textStyle, sw);
		 
		mDrawText(textStyle, sw, x + width - 1 - alignx, y+yoff, shade, pal, 0, 0);
		
		scale = 4096;
		int yoffset = -4;
		if(textStyle == 1) yoffset = -6;
		if(textStyle == 2) { yoffset = 6 - yoff; scale = 8192; }
		if ( focused )
			engine.rotatesprite((x-10)<<16, (y - yoffset) << 16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
	}
	
	@Override
	public int callback(MENU pMenu, int opt) {
		if(opt == 4 || opt == 5 || opt == 6 || opt == 11)
		{
			if ( (flags & 4) == 0 ) return 0;
			value = !value;
			if(callback != null) 
				callback.run(this);
			sound(KICK_HIT);
			return 0;
		} else return mNavigation(pMenu, opt);
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}
	
	@Override
	public void close(MENU pMenu) {
		
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			mGetAlign(textStyle, text);
			if(mx > x && mx < x + alignx)
				if(my > y && my < y + aligny)
					return true;
		}
		
		char[] sw = offMessage;
		if(value) sw = onMessage;

		mGetAlign(textStyle, sw);
		int px = x + width - 1 - alignx;
		if(mx > px && mx < px + alignx)
			if(my > y && my < y + aligny)
				return true;
		
		return false;
	}
}