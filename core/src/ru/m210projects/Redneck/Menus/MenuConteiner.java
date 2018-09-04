package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Screen.*;

public class MenuConteiner extends MenuItem
{
	int num;
	MENUPROC callback;
	MENU nextMenu;
	char[][] list;
	
	public MenuConteiner(String text, int nFontId, int x, int y, int width, String[] list, int num, MENUPROC callback)
	{
		this.flags = 3 | 4;
		if(list != null)
		{
			this.list = new char[list.length][];
			for(int i = 0; i < list.length; i++)
				this.list[i] = list[i].toCharArray();
		}
		
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = nFontId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.callback = callback;
		this.num = num;
		this.pal = 0;
	}
	
	@Override
	public void draw() {
		int px = x, py = y;
		int shade = 8;
		char[] key = null;
		if(list != null && num != -1 && num < list.length) 
			key = list[num];	

		boolean focused = mGetFocusedItem(m_pMenu, this);
		if ( focused ) 
			shade = 8 - (totalclock & 0x3F);
		
		int pal = this.pal;

		int yoff = 0;
	    if(textStyle == 2) yoff = 13;
			
		mDrawText(textStyle, text, px, py+yoff, shade, pal, 0, 0);
		if(key == null) return;
		
		mGetAlign(textStyle, key);
		mDrawText(textStyle, key, x + width - 1 - alignx, py+yoff, shade, pal, 0, 0);
		
		int scale = 4096;
		int yoffset = -4;
		if(textStyle == 1) yoffset = -6;
		if(textStyle == 2) { yoffset = 6 - yoff; scale = 8192; }
		if ( focused )
			engine.rotatesprite((x-10)<<16, (y - yoffset) << 16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		
		switch(opt)
		{
		case 4:
		case 17:
			if ( (flags & 4) == 0 ) return 0;
			if(num > 0) num--;
			else num = 0;
			if(callback != null)
				callback.run(this);
			sound(KICK_HIT);
			return 0;
		case 5:
		case 16:
			if ( (flags & 4) == 0 ) return 0;
			if(num < list.length - 1) num++;
			else num = list.length - 1;
			if(callback != null)
				callback.run(this);
			sound(KICK_HIT);
			return 0;
		case 6:
		case 11:
			if ( (flags & 4) == 0 ) return 0;
			if(num < list.length - 1) {
				num++;
			} else num = 0;
			if(callback != null)
				callback.run(this);
			sound(KICK_HIT);
			return 0;
		}
		
		return mNavigation(pMenu, opt);
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
		
		if(list == null) return false;
		char[] key = null;
		if(num != -1 && num < list.length) {
			key = list[num];
			mGetAlign(textStyle, key);
			int px = x + width - 1 - alignx;
			if(mx > px && mx < px + alignx)
				if(my > y && my < y + aligny)
					return true;
		}
		
		return false;
	}	
}
