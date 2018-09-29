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

import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Redneck.Screen.*;

import java.util.List;

public class MenuList extends MenuItem
{
	int l_nMin = 0;
	int l_nFocus;
	int nListItems;
	int align;
	List<char[]> text;
	MENUPROC specialCall;
	public MENU nextMenu;
	int nItemHeight = 10;
	
	public MenuList(List<char[]> text, int textStyle, int x, int y, int width,
			int align, int nItemHeight, MENU nextMenu, MENUPROC specialCall,
			int nListItems) {
		
		this.text = text;
		this.align = align;
		this.flags = 3 | 4;
		this.m_pMenu = null;
		this.textStyle = textStyle;
		this.x = x;
		this.y = y;
		this.width = width;
		this.nItemHeight = nItemHeight;
		this.nListItems = nListItems;
		this.nextMenu = nextMenu;
		this.specialCall = specialCall;
	}
	
	/*	CycleList
			int cpos = (nListItems / 2);
			for(int i = -cpos; i < nListItems - cpos; i++) {
				int menuY = m_pMenu.m_pItems[cpos+1].y + i * 10;
				int shade = 32;
			    if ( mGetFocusedItem(m_pMenu, this) )
			     	shade = 32 - (totalclock & 0x3F);
				int px = x;
			    if(align == 1) {
			    	mGetAlign(textStyle, text);
			        px = width / 2 + x - alignx / 2;
			    }
			    if(align == 2) {
			    	mGetAlign(textStyle, text);
			        px = x + width - 1 - alignx;
			    }
//			      	int item = m_pMenu.m_nFocus+i;
//			    	if((m_pMenu.m_nFocus+i) < 0)
//			    	item = usermaps.size() + i + m_pMenu.m_nFocus+1;
//			    	if((m_pMenu.m_nFocus+i) > usermaps.size()+1)
//			    	item = m_pMenu.m_nFocus - usermaps.size() + i - 1;
			    
			    int item = m_pMenu.m_nFocus+i;
			    if((m_pMenu.m_nFocus+i-1) < 0)
			    	item = usermaps.size() + i + m_pMenu.m_nFocus;
			    
			    if((m_pMenu.m_nFocus+i) > usermaps.size())
			    	item = m_pMenu.m_nFocus - usermaps.size() + i;
			    
			    if(this.equals(m_pMenu.m_pItems[item]))
			    	viewDrawText(textStyle, text, px, menuY, shade, 0, 0, true);
			}
	 */
	
	@Override
	public void draw() {
		if(text.size() > 0) {
			//if(text.size() < nListItems)
			//	nListItems = text.size();

			mGetAlign(textStyle, null);
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
				int pal = 0;
				int shade = 8;
				if ( i == l_nFocus ) {
					if(mGetFocusedItem(m_pMenu, this))
						shade = 8 - (totalclock & 0x3F);
					else { shade = 0; pal = 0; }
				}
			    if(align == 1) {
			    	mGetAlign(textStyle, text.get(i));
			        px = width / 2 + x - alignx / 2;
			    }
			    if(align == 2) {
			    	mGetAlign(textStyle, text.get(i));
			        px = x + width - 1 - alignx;
			    }
			    mDrawText(textStyle, text.get(i), px, py, shade, pal, 0, 0);
				py += aligny + nItemHeight;
			}
		} else {
			int pal = 0;
			int shade = 8;
			String text = "List is empty";
			mGetAlign(textStyle, text.toCharArray());
			int px = x, py = y;		
			if(align == 1) {
		    	mGetAlign(textStyle, text.toCharArray());
		        px = width / 2 + x - alignx / 2;
		    }
		    if(align == 2) {
		    	mGetAlign(textStyle, text.toCharArray());
		        px = x + width - 1 - alignx;
		    }	    
			mGetAlign(textStyle, null);
			if(mGetFocusedItem(m_pMenu, this))
				shade = 8 - (totalclock & 0x3F);
			mDrawText(textStyle, text.toCharArray(), px, py, shade, pal, 0, 0);
		}
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		switch(opt)
		{
			case 16:
				if(l_nMin > 0)
					l_nMin--;
				return 0;
			case 17:
				if(text != null)
					if(l_nMin < text.size() - nListItems)
						l_nMin++;
				return 0;
			case 2:
				l_nFocus--;
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					if(l_nMin > 0) l_nMin--;
				if(l_nFocus < 0) {
					l_nFocus = text.size() - 1;
					l_nMin = text.size() - nListItems;
					if(l_nMin < 0) l_nMin = 0;
				}
				return 0;
			case 3:
				l_nFocus++;
				if(l_nFocus >= l_nMin + nListItems && l_nFocus < text.size())
					l_nMin++;
				if(l_nFocus >= text.size()) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				return 0;
			case 4: //left
				mNavUp(pMenu);
				return 0;
			case 5: //right
				mNavDown(pMenu);
				return 0;
			case 6: //enter
			case 11:
				if ( (flags & 2) == 0 ) return 0;
				if(text.size() > 0) {
					specialCall.run(this);
					if ( nextMenu != null )
				    	mOpen(nextMenu, -1);
				}
				return 0;
			case 7: //esc
			case 18:
				//l_nFocus = l_nMin = 0;
				return 1;
		}
		return 0;
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}
	
	@Override
	public void close(MENU pMenu) {
		l_nFocus = l_nMin = 0;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text.size() > 0) {
			mGetAlign(textStyle, null);
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
			    if(align == 1) {
			    	mGetAlign(textStyle, text.get(i));
			        px = width / 2 + x - alignx / 2;
			    }
			    if(align == 2) {
			    	mGetAlign(textStyle, text.get(i));
			        px = x + width - 1 - alignx;
			    }

			    if(mx > px && mx < px + alignx)
					if(my > py && my < py + aligny)
					{
						l_nFocus = i;
						return true;
					}
			    
				py += aligny + nItemHeight;
			}
		}
		return false;
	}
}
