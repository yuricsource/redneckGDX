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

import static ru.m210projects.Redneck.Gameutils.ClipLow;
import static ru.m210projects.Redneck.Gameutils.toCharArray;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.*;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class MenuKeyboardList extends MenuItem
{
	int len;
	int l_nMin = 0;
	int l_nFocus, nItems;
	int l_set = 0; 
	int l_pressedId = -1;
	MENUPROC callback;
	
	protected int touchY;
	protected int scrollX, scrollY;
	protected boolean scrollTouch;
	
	public MenuKeyboardList(String text, int nFontId, int x, int y, int width, int nItems, int len, MENUPROC callback)
	{
		this.flags = 3 | 4;
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = nFontId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.nItems = nItems;
		this.len = len;
		this.callback = callback;
	}
	
	@Override
	public void draw() {
		mGetAlign(textStyle, null);
		int px = x, py = y;
		for(int i = l_nMin; i >= 0 && i < l_nMin + nItems && i < len; i++) {	
			int shade = 8;
			String text = keynames[i];
			String key;
			
			if(cfg.primarykeys[i] != 0)
				key = Keymap.toString(cfg.primarykeys[i]);
			else key = "N/A";
			
			if(cfg.secondkeys[i] != 0)
				key += " or " + Keymap.toString(cfg.secondkeys[i]);

			if ( i == l_nFocus ) {
				if(mGetFocusedItem(m_pMenu, this)) {
					shade = 8 - (totalclock & 0x3F);
					engine.rotatesprite((x-14)<<16, (py + 4) << 16, 4096,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
				}
				if(l_set == 1 && (totalclock & 0x20) != 0)
				{
					key = "____";
				}
			}

			mDrawText(textStyle, toCharArray(text), px, py, shade, 2, 0, 0);
			char[] k = toCharArray(key);
			mGetAlign(textStyle, k);
			mDrawText(textStyle, k, x + width - 1 - alignx, py, shade, 2, 0, 0);		
			
			if(cfg.mousekeys[i] != 0)
				key = Keymap.toString(cfg.mousekeys[i]);
			else key = " - ";
			if ( i == l_nFocus ) {
				if(mGetFocusedItem(m_pMenu, this))
					shade = 8 - (totalclock & 0x3F);
				if(l_set == 1 && (totalclock & 0x20) != 0)
				{
					key = "____";
				}
			}
			k = toCharArray(key);
			mGetAlign(textStyle, k);
			mDrawText(textStyle, k, x + width - 1 - alignx + 60, py, shade, 2, 0, 0);
				
			py += aligny;
		}
		
		int nList = ClipLow(len - nItems, 1);
		int posy = (((nItems) * aligny - 19)) * l_nMin / nList;

		scrollX = x + width + 70;
		scrollY = mDrawSlider(scrollX, y, posy, 72, true);	
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		if(l_set == 0) {
			switch(opt)
			{
			case 16:
				if(l_nMin > 0)
					l_nMin--;
				sound(KICK_HIT);
				return 0;
			case 17:
				if(l_nMin < len - nItems)
					l_nMin++;
				sound(KICK_HIT);
				return 0;
			case 2:
				l_nFocus--;
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					l_nMin--;
				if(l_nFocus < 0) {
					l_nFocus = len - 1;
					l_nMin = len - nItems;
				}
				sound(KICK_HIT);
				return 0;
			case 3:
				l_nFocus++;
				if(l_nFocus >= l_nMin + nItems && l_nFocus < len)
					l_nMin++;
				if(l_nFocus >= len) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				sound(KICK_HIT);
				return 0;
			case 6:
			case 11:
				if(opt == 11 && scrollTouch)
				{
					l_nFocus = -1;
					int nList = ClipLow(len - nItems, 1);
					int nRange = nItems * aligny - 42;
					mGetAlign(textStyle, null);
					int py = y + 8;
					float dr = (float)(touchY - py) / nRange;
					l_nMin = (int) BClipRange(dr * nList, 0, nList);
					
					return 0;
				}
				if(l_nFocus != -1 && callback != null) 
					callback.run(this);
				
				sound(PISTOL_BODYHIT);
				
				getInput().resetKeyStatus();
				return 0;
			case 10:
				if(l_nFocus == -1) return 0;
				cfg.primarykeys[l_nFocus] = 0;
				cfg.secondkeys[l_nFocus] = 0;
				cfg.mousekeys[l_nFocus] = 0;
				
				if(l_nFocus == Show_Console) {
					Console.setCaptureKey(cfg.primarykeys[Show_Console], 0);
					Console.setCaptureKey(cfg.secondkeys[Show_Console], 1);
					Console.setCaptureKey(cfg.mousekeys[Show_Console], 2);
				}
				
				sound(PISTOL_BODYHIT);
				return 0;
			case MKPGUP:
				l_nFocus -= (nItems - 1);
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					if(l_nMin > 0) l_nMin -= (nItems - 1);
				if(l_nFocus < 0 || l_nMin < 0) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				sound(KICK_HIT);
				return 0;
			case MKPGDW:
				l_nFocus += (nItems - 1);
				if(l_nFocus >= l_nMin + nItems && l_nFocus < len)
					l_nMin += (nItems - 1);
				if(l_nFocus >= len || l_nMin > len - nItems) {
					l_nFocus = len - 1;
					if(len >= nItems)
						l_nMin = len - nItems;
					else l_nMin = len - 1;
				}
				sound(KICK_HIT);
				return 0;
			case MKHOME:
				l_nFocus = 0;
				l_nMin = 0;
				sound(KICK_HIT);
				return 0;
			case MKEND:
				l_nFocus = len - 1;
				if(len >= nItems)
					l_nMin = len - nItems;
				else l_nMin = len - 1;
				sound(KICK_HIT);
				return 0;
			default:
				return mNavigation(pMenu, opt);
			}
		}
		else
		{
			l_pressedId = opt;
			if(callback != null)
				callback.run(this);
			
			if(l_nFocus == Menu_open) 
				getInput().resetKeyStatus();

			return 0;
		}
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}

	@Override
	public void close(MENU pMenu) {
		
		
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(l_set != 0)
			return false;
		
		if(!Gdx.input.isTouched()) 
			scrollTouch= false;
		
		touchY = my;
		if(mx > scrollX && mx < scrollX + 14) 
		{
			if(Gdx.input.isTouched())
				scrollTouch = true;
			else scrollTouch = false;
			return true;
		}
		
		if(!scrollTouch) {
			mGetAlign(textStyle, null);
			int py = y;
	
			for(int i = l_nMin; i >= 0 && i < l_nMin + nItems && i < len; i++) {	
				if(my > py && my < py + aligny)
				{
					l_nFocus = i;
					return true;
				}
			    
				py += aligny;
			}
		}
		
		return false;
	}
}
