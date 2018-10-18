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

import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.gpmanager;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.SoundDefs.EXITMENUSOUND;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Config.Open;
import static ru.m210projects.Redneck.Controls.ctrlPadStatusOnce;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Compat.FileUserdir;
import static ru.m210projects.Build.Gameutils.*;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Redneck.Types.SaveManager;
import ru.m210projects.Redneck.Types.SaveManager.SaveInfo;

public class MenuSlotList extends MenuItem
{
	private int touchY;
	private int scrollX;
	public boolean scrollTouch;
	public boolean deleteQuestion;
	
	int l_nMin = 0;
	int l_nFocus;
	int nListItems;
	int nItemHeight = 10;
	
	public List<SaveInfo> text;
	public MENUPROC updateCallback;
	public MENUPROC confirmCallback;
	
	
	public final boolean saveList;
	public boolean typing;
	public char[] typingBuf = new char[SAVENAME];
	public String typed;

	public MenuSlotList(int textStyle, int x, int y, int width, List<SaveInfo> text, 
			int nListItems, int nItemHeight, MENUPROC updateCallback, MENUPROC confirmCallback, boolean saveList ) {
		this.flags = 3;
		this.m_pMenu = null;
		
		this.typing = false;
		this.x = x;
		this.y = y;
		this.width = width;
		this.text = text;
		this.textStyle = textStyle;
		this.nListItems = nListItems;
		this.nItemHeight = nItemHeight;
		
		this.updateCallback = updateCallback;
		this.confirmCallback = confirmCallback;
		this.saveList = saveList;
	}
	
	public String FileName()
	{
		int ptr = l_nFocus;
		if(saveList) ptr--;
		if(ptr == -1 || text.size() == 0)
			return "Empty slot";
		return text.get(ptr).filename;
	}
	
	public String SaveName()
	{
		int ptr = l_nFocus;
		if(saveList) ptr--;
		if(ptr == -1 || text.size() == 0)
			return "Empty slot";
		return text.get(ptr).name;
	}
	
	@Override
	public void draw() {
		mGetAlign(textStyle, null);
		int pal, len = text.size();
		
		engine.rotatesprite((x - 10) << 16, (y - 3) << 16, 65536, 0, LOADSCREEN, 128, 0, 10 + 16 + 1, 0, 0, coordsConvertXScaled(x+90, 0), coordsConvertYScaled(y+97));
		
		if(text.size() > 0) {
			int px = x, py = y;

			if(saveList) len += 1;

			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {	
				int ptr = i;
				if(saveList) ptr -= 1;
				
				int shade = 20;
				char[] rtext;
				if(i == 0 && saveList)
				{
					rtext = toCharArray("New savegame");
				} else rtext = toCharArray(text.get(ptr).name);
				
				if(ptr >= 0 && (text.get(ptr).filename.equals("autosave.sav") 
						|| text.get(ptr).filename.startsWith("quicksav")))
					pal = 2;
				else pal = 12;
				
				if ( i == l_nFocus ) {
					if(mGetFocusedItem(m_pMenu, this)) {
						if(!typing) {
							if(!deleteQuestion)
								shade = 32 - (totalclock & 0x3F);
						}
						else {
							Arrays.fill(typingBuf, (char) 0);
							System.arraycopy(getInput().getMessageBuffer(), 0, typingBuf, 0, getInput().getMessageLength()+1);
							rtext = typingBuf;
							shade = -128;
						}
					}
					else { shade = 0; }
				}

				mDrawText(textStyle, rtext, px, py, shade, pal, 0, 0);

				py += aligny + nItemHeight;
			}
		} else {
			int px = x;
			int py = y;	
			int shade = 8 - (totalclock & 0x3F);
			char[] rtext;
			
			if(saveList) {
				rtext = toCharArray("New saved game");
				if(typing) {
					Arrays.fill(typingBuf, (char) 0);
					System.arraycopy(getInput().getMessageBuffer(), 0, typingBuf, 0, getInput().getMessageLength()+1);
					rtext = typingBuf;
					shade = -128;
				}
				
			} else 
				rtext = toCharArray("List is empty");

			mDrawText(textStyle, rtext, px, py, shade, 12, 0, 0);
		}

		int nList = ClipLow(len - nListItems, 1);
		int posy = ((nListItems * (aligny + nItemHeight) - 14)) * l_nMin / nList;

		scrollX = x + width - 20;
		mDrawSlider(scrollX, y - 4, posy, 82, true);

		if(deleteQuestion)
		{
			engine.setpalettefade(0, 0, 0, 48);
			engine.showfade();
			
			String text = "Do you want to delete \"" + SaveName() + "\"";
			mGetAlign(textStyle, toCharArray(text));
			mDrawText(textStyle, toCharArray(text), 160 - alignx / 2, 100, 32 - (totalclock & 0x3F), 12, 0, 0);
			text = "[Y/N]";
			mGetAlign(textStyle, toCharArray(text));
			mDrawText(textStyle, toCharArray(text), 160 - alignx / 2, 110, 32 - (totalclock & 0x3F), 12, 0, 0);
		} else {
			String text = "Press \"DELETE\" to remove the savegame file";
			mGetAlign(textStyle, toCharArray(text));
			mDrawText(textStyle, toCharArray(text), 160 - alignx / 2, 190, 0, 12, 0, 0);
		}
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		if(deleteQuestion)
		{
			if(getInput().getKey(Keys.Y) != 0 || opt == 6) {
				SaveManager.delete(FileName());
			    getInput().setKey(Keys.Y, 0);
			    if(l_nFocus >= text.size()) {
			    	int len = text.size();
			    	if(saveList) len += 1;
			    	l_nFocus = len - 1;
			    	l_nMin = len - nListItems;
					if(l_nMin < 0) l_nMin = 0;
			    }
			    if(updateCallback != null)
					updateCallback.run(this);
			    deleteQuestion = false;
			}
			if(getInput().getKey(Keys.N) != 0 || opt == 7 || opt == 18) {
				
				getInput().setKey(Keys.N, 0);
				deleteQuestion = false;
			}
			
			return 0;
		}
		
		int focus = l_nFocus; 
		int len = text.size();
		if(saveList) {
			len += 1;
			focus -= 1;
		}
		if(typing) 
		{
			if(opt != 7) {
				if(gpmanager != null)
					gpmanager.handler();
				if(getInput().putMessage(16, true, false, false) == 1 || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Open]))
				{
					typed = new String(getInput().getMessageBuffer(), 0, getInput().getMessageLength());
					typing = false;
					if(confirmCallback != null)
						confirmCallback.run(this);	
				}
			} else {
				typing = false;
				sound(EXITMENUSOUND);
			}
		} else {
			switch(opt)
			{
				case 10:
					if((!saveList && text.size() > 0) || l_nFocus != 0)
						deleteQuestion = true;
					return 0;
				case 16:
					if(l_nMin > 0)
						l_nMin--;
					sound(KICK_HIT);
					return 0;
				case 17:
					if(text != null)
						if(l_nMin < len - nListItems)
							l_nMin++;
					sound(KICK_HIT);
					return 0;
				case 2:
					l_nFocus--;
					if(l_nFocus >= 0 && l_nFocus < l_nMin)
						if(l_nMin > 0) l_nMin--;
					if(l_nFocus < 0) {
						l_nFocus = len - 1;
						l_nMin = len - nListItems;
						if(l_nMin < 0) l_nMin = 0;
					}
					if(updateCallback != null)
						updateCallback.run(this);
					sound(KICK_HIT);
					return 0;
				case 3:
					l_nFocus++;
					if(l_nFocus >= l_nMin + nListItems && l_nFocus < len)
						l_nMin++;
					if(l_nFocus >= len) {
						l_nFocus = 0;
						l_nMin = 0;
					}
					if(updateCallback != null)
						updateCallback.run(this);
					sound(KICK_HIT);
					return 0;
				case 4: //left
					mNavUp(pMenu);
					if(updateCallback != null)
						updateCallback.run(this);
					return 0;
				case 5: //right
					mNavDown(pMenu);
					if(updateCallback != null)
						updateCallback.run(this);
					return 0;
				case 6: //enter
				case 11:
					if(opt == 11 && scrollTouch)
					{
						l_nFocus = -1;
						int nList = ClipLow(len - nListItems, 1);
						int nRange = nListItems * aligny - 16;
						mGetAlign(textStyle, null);
						int py = y;
						float dr = (float)(touchY - py) / nRange;

						l_nMin = (int) BClipRange(dr * nList, 0, nList);
						
						return 0;
					}
					
					if(l_nFocus != -1 && len > 0) {
						if(saveList) {
							if(l_nFocus == 0) getInput().initMessageInput(null); 
							else getInput().initMessageInput(text.get(focus).name);
				        	typing = true;
				        	sound(PISTOL_BODYHIT);
							
							return 0;
						}
						if(confirmCallback != null)
							confirmCallback.run(this);	
						getInput().resetKeyStatus();
					}
					
					return 0;
				case 7: //esc
				case 18:
					return 1;
				case MKPGUP:
					l_nFocus -= (len - 1);
					if(l_nFocus >= 0 && l_nFocus < l_nMin)
						if(l_nMin > 0) l_nMin -= (len - 1);
					if(l_nFocus < 0 || l_nMin < 0) {
						l_nFocus = 0;
						l_nMin = 0;
					}
					sound(KICK_HIT);
					return 0;
				case MKPGDW:
					l_nFocus += (len - 1);
					if(l_nFocus >= l_nMin + len && l_nFocus < len)
						l_nMin += (len - 1);
					if(l_nFocus >= len || l_nMin > len - len) {
						l_nFocus = len - 1;
						if(len >= len)
							l_nMin = len - len;
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
					if(len >= len)
						l_nMin = len - len;
					else l_nMin = len - 1;
					sound(KICK_HIT);
					return 0;
			}
		}
		return 0;
	}
	
	@Override
	public void open(MENU pMenu) {
		l_nMin = l_nFocus = 0;
		
		Iterator<SaveInfo> i = text.iterator();
		while (i.hasNext()) {
			SaveInfo s = i.next();
			File file = new File(FileUserdir+s.filename);
			if(!file.exists())
				i.remove();
		}
		
		if(updateCallback != null)
			updateCallback.run(this);
	}
	
	@Override
	public void close(MENU pMenu) {
	}

	@Override
	public boolean mouseAction(int mx, int my) {
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
		
		if(!scrollTouch && text.size() > 0) {
			mGetAlign(textStyle, null);
			int px = x, py = y;
			int len = text.size();
			if(saveList) len += 1;
			
			int ol_nFocus = l_nFocus;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {	
			    
				if(mx > px && mx < px + width - 14)
					if(my > py && my < py + aligny)
					{
						l_nFocus = i;
						if(ol_nFocus != i && updateCallback != null)
							updateCallback.run(this);
						return true;
					}
			    
				py += aligny + nItemHeight;
			}
		}
		return false;
	}
}