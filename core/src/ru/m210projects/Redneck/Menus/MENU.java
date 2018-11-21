// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Menus.RRMenu.*;
import static ru.m210projects.Redneck.Controls.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class MENU {

	public int m_nItems = 0;
	public int m_nFocus = -1;
	public int m_nFirst = -1;
	public static int mCount = 0;
	public static final int kMaxGameMenus = 35;
	public static final int kMaxGameMenuItems = 32;
	public static MENU[] mMenuHistory = new MENU[10];
	public static MENU[] mMenus = new MENU[kMaxGameMenus];
	public MenuItem[] m_pItems = new MenuItem[kMaxGameMenuItems];

	public final static int[] mCursorTile = {
		62, //arrow
	};
	
	public final static int[][] mCursorSetings = {
		{48000, 16, 16, 1800},
	};
	
	public static final int MKNONE = 0;
	public static final int MKANY = 1;
	public static final int MKUP = 2;
	public static final int MKDW = 3;
	public static final int MKLEFT = 4;
	public static final int MKRIGHT = 5;
	public static final int MKENTER = 6;
	public static final int MKESC = 7;
	public static final int MKSPACE = 8;
	public static final int MKBSPACE = 9; //backspace
	public static final int MKDELETE = 10;
	public static final int MKLMB = 11;
	public static final int MKPGUP = 12;
	public static final int MKPGDW = 13;
	public static final int MKHOME = 14;
	public static final int MKEND = 15;
	public static final int MKMWUP = 16; //mouse wheel up
	public static final int MKMWDW = 17; //mouse wheel down
	public static final int MKMRB = 18;

	//0x8000 - OpenMenu
	//0x8001 - CloseMenu
	
	public static float keycount = 0;
	public static final float hitTime = 0.5f; //kTimerRate;
	public static final float changeTime = 0.05f;
	
	public static void mKeyHandler(MENU pMenu) {
//		engine.handleevents();
		
		if(pMenu != null) {
			int opt = 0;
			if(ctrlKeyStatusOnce(Keys.UP) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Move_Forward]))
				opt = 2;
			if(ctrlKeyStatusOnce(Keys.DOWN) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Move_Backward]))
				opt = 3;
			if(ctrlKeyStatusOnce(Keys.LEFT) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Turn_Left]))
				opt = 4;
			if(ctrlKeyStatusOnce(Keys.RIGHT) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Turn_Right]))
				opt = 5;
			if(ctrlKeyStatusOnce(Keys.ENTER) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Open]))
				opt = 6;
			if(ctrlGetInputKey(Menu_open, true)) 
				opt = 7;
			if(ctrlKeyStatusOnce(Keys.SPACE)) 
				opt = 8;
			if(ctrlKeyStatusOnce(Keys.BACKSPACE)) 
				opt = 9;
			if(ctrlKeyStatusOnce(Keys.FORWARD_DEL)) 
				opt = 10;
			if(ctrlKeyStatusOnce(Keys.PAGE_UP)) 
				opt = 12;
			if(ctrlKeyStatusOnce(Keys.PAGE_DOWN)) 
				opt = 13;
			if(ctrlKeyStatusOnce(Keys.HOME)) 
				opt = 14;
			if(ctrlKeyStatusOnce(Keys.END)) 
				opt = 15;

			if(Gdx.app.getType() == ApplicationType.Android) {
				if(ctrlKeyStatusOnce(Keys.DPAD_UP))
					opt = 2;
				if(ctrlKeyStatusOnce(Keys.DPAD_DOWN)) 
					opt = 3;
				if(ctrlKeyStatusOnce(Keys.DPAD_LEFT)) 
					opt = 4;
				if(ctrlKeyStatusOnce(Keys.DPAD_RIGHT)) 
					opt = 5;
				if(ctrlKeyStatusOnce(Keys.BACK)) 
					opt = 7;
				
				if(Gdx.input.isTouched())
				{
					int focus;
					if((focus = mCheckButton(pMenu, Gdx.input.getX(), Gdx.input.getY())) != -1) {
						pMenu.m_nFocus = focus;
						opt = 11;
					}

					if(pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuSlider 
						|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuFileBrowser
						|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuResolutionList
						|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuSlotList
						|| (pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuKeyboardList 
								&& ((MenuKeyboardList)pMenu.m_pItems[pMenu.m_nFocus]).l_set == 0))
					{
						opt = 11;
					} 
				}
			} else {
				if(opt > 0) mUseMouse = false;
				int mopt = mUpdateMouse(pMenu);
				if(mopt > 0) opt = mopt;
			}

			if(mLoadRes(pMenu, opt) != 0) {
				mMenuBack();
			}	
			
			if(!Gdx.input.isTouched() && getInput().keyPressed()
					&& !getInput().keyPressed(Keys.ENTER) 
					&& !getInput().keyPressed(Keys.ESCAPE)) {
				keycount += Gdx.graphics.getDeltaTime();
				if(keycount >= hitTime) {
					if(keycount >= (hitTime + changeTime)) {
						for (int kb = 0; kb < 256; kb++) {
							getInput().setKey(kb, 0);
							getInput().keyPressed(kb, false);
						}
						keycount = hitTime;
					}
				}
			} else keycount = 0;
		}
	}
	
	public static int mCheckButton(MENU pMenu, int x, int y)
	{
		int oxdim = xdim;
		int xdim = (4 * ydim) / 3;
		int normxofs = x - oxdim / 2;
		int touchX = scale(normxofs, 320, xdim) + 320 / 2;
		int touchY = scale(y, 200, ydim);

		for(int i = 0; i < pMenu.m_pItems.length; i++)
		{
			if(pMenu.m_pItems[i] != null && mCheckItemsFlags(pMenu, i) && 
					pMenu.m_pItems[i].mouseAction(touchX, touchY) && !ctrlKeyStatus(MOUSE_LBUTTON)) {
				return i;
			}
		}
		return -1;
	}
	
	private static boolean mUseMouse = false;
	public static int mUpdateMouse(MENU pMenu)
	{
		int opt = 0;
		if(Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0)
			mUseMouse = true;
		
		if(!mUseMouse)
			return 0;
		
		int mx = ClipRange(Gdx.input.getX(), 0, xdim);
		int my = ClipRange(Gdx.input.getY(), 0, ydim);
	
		if(!cfg.menuMouse)
			return 0;
		
		if(mCount > 1) {
			//Back button
			int zoom = scale(16384, ydim, 200);
			int size = mulscale(tilesizx[BACKBUTTON], zoom, 16);
			int bx = 0;
			int by = ydim - size;
			if(mx >= bx && mx < bx + size)
				if(my >= by && my < by + size) 
					if(ctrlKeyStatusOnce(MOUSE_LBUTTON)) {
						mMenuBack();
						return 0;
					}
		}
			
		//Sliders
		if(pMenu.m_nFocus != -1 && mCheckItemsFlags(pMenu, pMenu.m_nFocus)) {
			if(pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuSlider 
				|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuFileBrowser
				|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuResolutionList
				|| pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuSlotList
				|| (pMenu.m_pItems[pMenu.m_nFocus] instanceof MenuKeyboardList 
						&& ((MenuKeyboardList)pMenu.m_pItems[pMenu.m_nFocus]).l_set == 0))
			{
				if(ctrlKeyStatus(MOUSE_LBUTTON)) 
					opt = 11;
			} 
			else if(ctrlKeyStatusOnce(MOUSE_LBUTTON)) 
				opt = 11;
		}
		
		if(ctrlKeyStatusOnce(MOUSE_RBUTTON)) 
			opt = 18;
		if(ctrlKeyStatusOnce(MOUSE_WHELLUP)) 
			opt = 16;
		if(ctrlKeyStatusOnce(MOUSE_WHELLDN)) 
			opt = 17;
		int focus = mCheckButton(pMenu, mx, my);
		if(focus != -1 && mUseMouse)
			pMenu.m_nFocus = focus;

		return opt;
	}

	public static void mDrawMouse(int x, int y)
	{
		if(!cfg.menuMouse) return;
		int zoom = scale(16384, ydim, 200);
		if(mCount > 1) {
			//Back button
			int shade = 4 + mulscale(16, sintable[(20 * totalclock) & kAngleMask], 16);
			engine.rotatesprite(0, (ydim-mulscale(tilesizy[BACKBUTTON], zoom, 16))<<16, zoom, 0, BACKBUTTON, shade, 0, 8|16, 0, 0, mulscale(zoom, tilesizx[BACKBUTTON]-1, 16), ydim-1);
		}

		cfg.gMouseCursor = ClipRange(cfg.gMouseCursor, 0, mCursorTile.length - 1);
		int czoom = mulscale(mCursorSetings[cfg.gMouseCursor][0], mulscale(zoom, cfg.gMouseCursorSize, 15), 16);
		int xoffset = mulscale(mCursorSetings[cfg.gMouseCursor][1], czoom, 16);
		int yoffset = mulscale(mCursorSetings[cfg.gMouseCursor][2], czoom, 16);	
		int ang = mCursorSetings[cfg.gMouseCursor][3];

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, mCursorTile[cfg.gMouseCursor], 0, 0, 8, 0, 0, xdim-1, ydim-1);
	}

	public static int mAddItem(MENU pMenu, MenuItem pItem, boolean nFirstItem)
	{
		if(pItem == null)
			dassert("pItem != NULL");
	 
		if(pMenu.m_nItems >= kMaxGameMenuItems)
			dassert("m_nItems < kMaxGameMenuItems");
	  	int m_nItems = pMenu.m_nItems;
	  	pMenu.m_pItems[pMenu.m_nItems] = pItem;
	  	pItem.m_pMenu = pMenu;
	  	if ( nFirstItem )
	  	{
	  		pMenu.m_nFirst = pMenu.m_nItems;
	  		pMenu.m_nFocus = pMenu.m_nItems;
	  	}
	  	pMenu.m_nItems++;
	  	return m_nItems;
	}

	public static int mOpen(MENU pMenu, int nItem)
	{
		if(pMenu == null)
			dassert("pMenu != NULL");
		if ( mCount == 8 )
			return 0;
	  
		mMenuHistory[0] = pMenu;
		mMenuHistory[++mCount] = pMenu;
	  
		if ( nItem >= 0 ) {
			if(!(nItem >= 0 && nItem < pMenu.m_nItems && nItem < kMaxGameMenuItems))
				dassert("nItem >= 0 && nItem < m_nItems && nItem < kMaxGameMenuItems");

			if ( mCheckItemsFlags(pMenu, nItem) )
			{
				pMenu.m_nFirst = nItem;
				pMenu.m_nFocus = nItem;
			}
		}
		
		gShowMenu = true;
		vscrn(0);

	    if(cfg.fullscreen == 0)
	    	Gdx.input.setCursorCatched(false);

	    if(mMenuHistory[0] != null)
	    	mLoadRes(mMenuHistory[0], 0x8000);

	    return 1;
	}
	
	public static int mClose()
	{
		engine.setpalettefade(0, 0, 0, 0);
		
		for(int i = 0; i < 10; i++)
			mMenuHistory[i] = null;
		mCount = 0;
		
		gShowMenu = false;
		vscrn(ud.screen_size);

	    if(Gdx.input != null) {
	    	Gdx.input.setCursorCatched(true);
	    	resetMousePos();
	    }
	    
	    
	    
	    return 0;
	}
	
	public static void mMenuBack() {
		if(mCount > 0) {
			if(mMenuHistory[0] != null)
				mLoadRes(mMenuHistory[0], 0x8001);
			mCount = ClipLow(mCount - 1, 0);
			if(mCount > 0) {
				mMenuHistory[0] = mMenuHistory[mCount];
			} else {
				mClose();
			}
		}
	}
	
	public static boolean isOpened(MENU pMenu)
	{
		return mMenuHistory[0] == pMenu;
	}
	
	public static void mDrawMenu() {
		if(!isOpened(mMenus[COLORCORR])) {
			engine.setpalettefade(0, 0, 0, 32);
			engine.showfade();
		}
		
		if(mMenuHistory[0] != null) 
			mDraw(mMenuHistory[0]);

		if(mUseMouse && cfg.menuMouse)
			mDrawMouse(Gdx.input.getX(), Gdx.input.getY());
	}
	
	public static void mDraw(MENU pMenu)
	{
		for ( int i = 0; i < pMenu.m_nItems; ++i )
		{
			if ( i == pMenu.m_nFocus || i != pMenu.m_nFocus && (pMenu.m_pItems[i].flags & 8) == 0 ) {
				pMenu.m_pItems[i].draw();
			}
		}
	}

	public static int mLoadRes(MENU pMenu, int opt)
	{
		if ( pMenu.m_nItems <= 0 )
			return 1;
		
		switch(opt) {
		case 0x8000: //load menu
			if ( pMenu.m_nFirst >= 0 )
				pMenu.m_nFocus = pMenu.m_nFirst;
			for ( int i = 0; i < pMenu.m_nItems; i++ )
				pMenu.m_pItems[i].open(pMenu);
			if(mCount > 1)
				sound(PISTOL_BODYHIT);
			else 
				sound(JIBBED_ACTOR6);

			return 0;
		case 0x8001: //close menu
			for ( int i = 0; i < pMenu.m_nItems; i++ )
				pMenu.m_pItems[i].close(pMenu);
			sound(EXITMENUSOUND);
			return 0;
		default:
			if ( pMenu.m_nFocus >= 0 && mCheckItemsFlags(pMenu, pMenu.m_nFocus)) 
		    	return pMenu.m_pItems[pMenu.m_nFocus].callback(pMenu, opt);
		}
		return 0;
	}
	
	public static boolean mCheckItemsFlags(MENU pMenu, int nItem) {
		if(!(nItem >= 0 && nItem < pMenu.m_nItems && nItem < kMaxGameMenuItems))
			dassert("nItem >= 0 && nItem < m_nItems && nItem < kMaxGameMenuItems");
		MenuItem pItem = pMenu.m_pItems[nItem];
		return (pItem.flags & 1) != 0 && (pItem.flags & 2) != 0;
	}
	
	public static boolean mGetFocusedItem(MENU pMenu, MenuItem m_pItem) {
		if ( pMenu.m_nFocus >= 0 )
		{
			if(!(pMenu.m_nFocus >= 0 && pMenu.m_nFocus < pMenu.m_nItems && pMenu.m_nFocus < kMaxGameMenuItems))
				dassert("m_nFocus >= 0 && m_nFocus < m_nItems && m_nFocus < kMaxGameMenuItems");
		    return m_pItem == pMenu.m_pItems[pMenu.m_nFocus];
		}
		return false;
	}
	
	public static int mNavUp(MENU pMenu)
	{
		int nItem;
		if(!(pMenu.m_nFocus >= -1 && pMenu.m_nFocus < pMenu.m_nItems && pMenu.m_nFocus < kMaxGameMenuItems))
			dassert("m_nFocus >= -1 && m_nFocus < m_nItems && m_nFocus < kMaxGameMenuItems");
		do
		{
			nItem = pMenu.m_nFocus - 1;
			if ( nItem < 0 )
				nItem += pMenu.m_nItems;
			pMenu.m_nFocus = nItem;
		}
		while ( !mCheckItemsFlags(pMenu, nItem) );
		
		sound(335);
		return nItem;
	}
	
	public static int mNavDown(MENU pMenu)
	{
		int nItem;
		if(!(pMenu.m_nFocus >= -1 && pMenu.m_nFocus < pMenu.m_nItems && pMenu.m_nFocus < kMaxGameMenuItems))
			dassert("m_nFocus >= -1 && m_nFocus < m_nItems && m_nFocus < kMaxGameMenuItems");
		do
		{
			nItem = pMenu.m_nFocus + 1;
			if ( nItem >= pMenu.m_nItems )
				nItem = 0;
			pMenu.m_nFocus = nItem;
		}
		while ( !mCheckItemsFlags(pMenu, nItem) );
		
		sound(335);
		return nItem;
	}
	
	public static int mNavigation(MENU pMenu, int opt) 
	{
		switch(opt) {
			case 2:
				mNavUp(pMenu);
			    return 0;
			case 3:
				mNavDown(pMenu);
			    return 0;
			case 7:
			case 18:
				return 1;
		}
			
		return 0;
	}
	
	public static void mDrawText(int textStyle, char[] text, int x, int y, int shade, int pal, int align, int flags)
	{
		if ( align != TA_LEFT )
		{
			mGetAlign(textStyle, text);
			int nWidth = alignx;

			if ( align == TA_CENTER ) {
				nWidth >>= 1;
			}
			x -= nWidth;
		}
		
		if(textStyle == 0)
			minitext(x, y, text, 65536, shade, pal, flags | 8 | 16);
		else if(textStyle == 1)
			gametext(x, y, text, 65536, shade, pal, flags | 8 | 16);
		else menutext(x, y, shade, pal, text, flags);
	}
	
	private static char[] symb = new char[1];
	public static void mDrawChar(int textStyle, char symbol, int x, int y, int scale, int shade, int pal, int align, int flags)
	{
		symb[0] = symbol;
		if(textStyle == 0)
			minitext(x, y, symb, scale, shade, pal, flags | 8 | 16);
		else if(textStyle == 1)
			gametext(x, y, symb, scale, shade, pal, flags | 8 | 16);
		else menutext(x, y, shade, pal, symb, flags);
	}
}