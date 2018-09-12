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

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Build.Gameutils.*;
import ru.m210projects.Build.Input.ButtonMap;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class MenuJoyList extends MenuKeyboardList {

	final int menupal = 15;
	public MenuJoyList(String text, int nFontId, int x, int y, int width,
			int nItems, int len, MENUPROC callback) {
		super(text, nFontId, x, y, width, nItems, len, callback);
	}
	
	@Override
	public void draw() {
		mGetAlign(textStyle, null);
		int px = x, py = y;
		for(int i = l_nMin; i >= 0 && i < l_nMin + nItems && i < len; i++) {	
			int pal = 2;
			int shade = 8;
			String text = keynames[i];
			String key;
			
			if(i == Move_Forward) {
				text = "Menu_up";
				pal = menupal;
			}
			if(i == Move_Backward) {
				text = "Menu_down";
				pal = menupal;
			}
			if(i == Turn_Left) {
				text = "Menu_left";
				pal = menupal;
			}
			if(i == Turn_Right) {
				text = "Menu_right";
				pal = menupal;
			}
			if(i == Turn_Around) 
				py += 4;
			
			if(i == Open) {
				text += " / Menu_enter";
				pal = menupal;
			}
			
			if(i == Menu_open) 
				pal = menupal;
			
			if(cfg.gpadkeys[i] >= 0)
				key = ButtonMap.buttonName(cfg.gpadkeys[i]);
			else key = "N/A";

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

			mDrawText(textStyle, toCharArray(text), px, py, shade, pal, 0, 0);
			char[] k = toCharArray(key);
			mGetAlign(textStyle, k);
			mDrawText(textStyle, k, x + width - 1 - alignx, py, shade, pal, 0, 0);
	
			py += aligny;
		}
		
		int nList = ClipLow(len - nItems, 1);
		int posy = (((nItems) * aligny - 19)) * l_nMin / nList;

		scrollX = x + width + 10;
		scrollY = mDrawSlider(scrollX, y, posy, 65, true);	
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
				cfg.gpadkeys[l_nFocus] = -1;
				if(l_nFocus == Show_Console) {
					Console.setCaptureKey(cfg.gpadkeys[Show_Console], 3);
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

			if(l_nFocus == Menu_open 
					|| l_nFocus == Open
					|| l_nFocus == Move_Forward
					|| l_nFocus == Move_Backward
					|| l_nFocus == Turn_Left
					|| l_nFocus == Turn_Right) {
				gpmanager.resetButtonStatus();
			}

			return 0;
		}
	}
}
