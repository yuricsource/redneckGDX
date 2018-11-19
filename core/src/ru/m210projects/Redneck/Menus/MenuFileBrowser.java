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

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Build.FileHandle.Cache1D.checkgroupfile;
import static ru.m210projects.Build.FileHandle.Cache1D.kGetBytes;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource;
import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.FileHandle.DirectoryEntry;

public class MenuFileBrowser extends MenuItem {

	private final int DIRECTORY = 0;
	private final int FILE = 1;
	private String back = "..";
	private char[] dirs = "Directories".toCharArray();
	private char[] ffs = "Files".toCharArray();
	private class StringList extends LinkedList<String> { private static final long serialVersionUID = 1L; }

	private int touchY;
	private int[] scrollX = new int[2], scrollY = new int[2];
	public boolean scrollTouch[] = new boolean[2];
	public boolean showmain;
	
	int[] l_nMin;
	int[] l_nFocus;
	final int nListItems;
	final MENUPROC specialCall;
	final int nItemHeight;
	
	DirectoryEntry currDir;
	FileEntry currFile;
	GameInfo currGame;

	StringList[] list = new StringList[2];
	
	String path;
	int currColumn;
	
	public MenuFileBrowser(int textStyle, int x, int y, int width,
			int nItemHeight, MENUPROC specialCall,
			int nListItems) {
		
		this.flags = 3| 4;
		this.m_pMenu = null;
		this.textStyle = textStyle;
		this.x = x;
		this.y = y;
		this.width = width;
		this.nItemHeight = nItemHeight;
		this.nListItems = nListItems;
		this.specialCall = specialCall;
		
		this.l_nMin = new int[2];
		this.l_nFocus = new int[2];
		this.currColumn = FILE;
		changeDir(cache);
	}
	
	private void buildAddons(List<String> tmpList, DirectoryEntry dir)
	{
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if(file.getExtension().equals("con"))
				InitTree(map, preparescript(kGetBytes(file.getPath(), 0)), file.getName());
		}
		
		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if(file.getExtension().equals("con"))
			{
				List<String> list = map.get(file.getName());
				if(list != null) 
					handleList(map, list);
			}
		}

		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			String con = it.next();
			if(!dir.getName().equals("<main>") || !con.equals("game.con")) {
				GameInfo addon = episodes.get(dir.checkFile(con).getPath());
				if(addon == null) {
					addon = new GameInfo(dir, con);
					addon.init();
					if(addon.isInited) {
						Console.Println("Found addon: " + addon.ConName);
						tmpList.add(con);
						episodes.put(dir.checkFile(con).getPath(), addon);
						if(con.equals("game66.con")) {
							RR66Game = addon;
							RR66Game.Title = "Route 66";	
						}
					}
				} else {
					if(addon.isInited) 
						tmpList.add(con);
				}
			} else {
				if(showmain)
					tmpList.add(defGame.ConName);
			}
		}
	}
	
	private void buildAddons(List<String> tmpList, IResource res, FileEntry file)
	{
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		for(RESHANDLE files : res.fList()) {
			if(files.fileformat.equals("con")) 
				InitTree(map, preparescript(files.getBytes()), files.filename);	
		}
		
		for(RESHANDLE files : res.fList()) {
			if(files.fileformat.equals("con")) {
				List<String> list = map.get(files.filename);
				if(list != null) 
					handleList(map, list);
			}
		}

		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			String con = res.name+":"+it.next();
			if(!con.equals("redneck.grp:game.con")) {
				GameInfo addon = episodes.get(con);
				if(addon == null) {
					String conName = con.substring(con.indexOf(":")+1);
					addon = new GameInfo(res, file, conName);
					if(addon.isInited) {
						Console.Println("Found addon: " + con);
						tmpList.add(con);
						episodes.put(con, addon);
					} else Console.Print(con + " found, but can't be load", OSDTEXT_RED);
				} else {
					if(addon.isInited) 
						tmpList.add(con);
				}
			}
		}
	}
	
	
	private void InitTree(HashMap<String, List<String>> map, byte[] buf, String parentName)
	{
        List<String> list = null;
		int index = -1;
        while( (index = indexOf("include ", buf, index+1)) != -1)
        {
        	int textptr = index + 7;
        	if(list == null) list = new ArrayList<String>();
        	
        	while( !isaltok(buf[textptr]) )
            {
                textptr++;
                if( buf[textptr] == 0 ) break;
            }

            int i = 0;
            while( isaltok(buf[textptr+i]) ) i++;
            
            String name = new String(buf, textptr, i);
            list.add(name.toLowerCase());
        }

        if(list != null)
        	map.put(parentName, list);
	}
	
	private void handleList(HashMap<String, List<String>> map, List<String> list)
	{
		for(String child : list)
			for (Iterator<String> con = map.keySet().iterator(); con.hasNext();) {
				String name = con.next();
				if(name.equals(child)) {
					List<String> other = map.get(name);
					con.remove();
					if(other != null) 
						handleList(map, other);
					break;
				}
			}
	}

	List<String> tmpList;
	private void changeDir(DirectoryEntry dir)
	{
		if(list[DIRECTORY] == null)
			list[DIRECTORY] = new StringList();
		else list[DIRECTORY].clear();
		
		if(list[FILE] == null)
			list[FILE] = new StringList();
		else list[FILE].clear();
		
		if(tmpList == null)
			tmpList = new ArrayList<String>();
		else tmpList.clear();
		
		if(currDir == dir)
			return;

		if(dir.getParent() != null)
			list[DIRECTORY].add(back);
		
		for (Iterator<DirectoryEntry> it = dir.getDirectories().values().iterator(); it.hasNext(); ) {
			DirectoryEntry sdir = it.next();
			if(!sdir.getName().equals("<userdir>")) 
				tmpList.add(toLowerCase(sdir.getName()));
		}
		
		Collections.sort(tmpList);
		list[DIRECTORY].addAll(tmpList);
		tmpList.clear();
		
		buildAddons(tmpList, dir);
		
		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext(); ) {
			FileEntry file = it.next();
			if(file.getExtension().equals("grp") || file.getExtension().equals("zip"))
			{
				try {
					IResource res = checkgroupfile(file.getPath());
					if(res != null)
					{
						buildAddons(tmpList, res, file);	
						res.Dispose();
						res = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		Collections.sort(tmpList);
		list[FILE].addAll(tmpList);
		tmpList.clear();

		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext(); ) {
			FileEntry file = it.next();
			String name = file.getFile().getName();
			if(file.getExtension().equals("map"))
				tmpList.add(toLowerCase(name));
		}
		
		Collections.sort(tmpList);
		list[FILE].addAll(tmpList);
		tmpList.clear();

		currDir = dir;
		path = File.separator;
		if(dir.getRelativePath() != null)
			path += currDir.getRelativePath();
	}
	
	
	
	@Override
	public void draw() {
		mGetAlign(textStyle, null);
		int px = x, py = y;
		char[] text = toCharArray(path);

		engine.rotatesprite((px - 23) << 16, (py - 8) << 16, 50000, 0, LOADSCREEN, 128, 0, 10 + 16 + 1, 0, 0, xdim - 1, ydim - 1);
		mDrawText(1, dirs, px - 20, py-5, -32, 10, 0, 0);
		mDrawText(1, ffs, px + width - 25, py-5, -32, 10, 0, 0);
		py += aligny + 2;
		brDrawText(textStyle, text, px, py, -32, 10, 0, this.x + this.width);
		py += aligny + 2;
		
		int height = py;
		for(int i = l_nMin[DIRECTORY]; i >= 0 && i < l_nMin[DIRECTORY] + nListItems && i < list[DIRECTORY].size(); i++) {	
			int pal = 12;
			int shade = 8;
			if ( currColumn == DIRECTORY && i == l_nFocus[DIRECTORY] ) {
				if(mGetFocusedItem(m_pMenu, this))
					shade = 8 - (totalclock & 0x3F);
				else { shade = 0; pal = 10; }
			}
			text = toCharArray(list[DIRECTORY].get(i));
			brDrawText(textStyle, text, px, py, shade, pal, 0, this.x + this.width / 2 - 4);
			py += aligny + nItemHeight;
		}
		height = py - height;

		px = x + width; py = y + 2 * aligny + 4;
		for(int i = l_nMin[FILE]; i >= 0 && i < l_nMin[FILE] + nListItems && i < list[FILE].size(); i++) {	
			int pal = 12;
			int shade = 8;
			if ( currColumn == FILE && i == l_nFocus[FILE] ) {
				if(mGetFocusedItem(m_pMenu, this))
					shade = 8 - (totalclock & 0x3F);
				else { shade = 0; pal = 10; }
			}

			String filename = list[FILE].get(i);
			GameInfo addon;
			if(currDir.checkFile(filename) == null) //archived addon
			{
				if((addon = episodes.get(filename)) != null) {
					filename = addon.Title;
					pal = 2;
				} else continue;
			} 
			else if((addon = episodes.get(currDir.checkFile(filename).getPath())) != null)
			{
				filename = addon.Title;
				pal = 2;
			}
			
			if(currDir.getName().equals("<main>") && filename.equalsIgnoreCase("game.con"))
			{
				filename = "None";
				pal = 2;
			}
			text = toCharArray(filename);
			
			mGetAlign(textStyle, text);
	        px = x + width - 1 - alignx;
	        brDrawText(textStyle, text, px, py, shade, pal, this.x + this.width / 2 + 4, this.x + this.width);
			py += aligny + nItemHeight;
		}

		//Files scroll
		int nList = ClipLow(list[FILE].size() - nListItems, 1);
		int posy = (((nListItems) * aligny - 10)) * l_nMin[FILE] / nList;
		
		scrollX[FILE] = x + width + 8;
		scrollY[FILE] = mDrawSlider(scrollX[FILE], y + 10, posy, 110, currColumn == FILE);

		//Directory scroll
		nList = ClipLow(list[DIRECTORY].size() - nListItems, 1);
		posy = (((nListItems) * aligny - 10)) * l_nMin[DIRECTORY] / nList;
		
		scrollX[DIRECTORY] = x - 16;
		scrollY[DIRECTORY] = mDrawSlider(scrollX[DIRECTORY], y + 10, posy, 110, currColumn == DIRECTORY);	
	}
	
	private void brDrawText( int nFontId, char[] text, int x, int y, int shade, int nPLU, int x1, int x2 )
	{
		int ac, tptr = 0, tx = 0;
	    while(tptr < text.length && text[tptr] != 0)
	    {
	    	text[tptr] = Character.toUpperCase(text[tptr]);
	        if(text[tptr] == 32) {x+=5;tptr++;continue;}
	        else ac = text[tptr] - '!' + MINIFONT;

	        if(tx + x > x1 && tx + x <= x2) 
	        	engine.rotatesprite(coordsConvertXScaled(x,0)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,ac,shade,nPLU,24,0,0,xdim-1,ydim-1);
	        x += 5;
	        tptr++;
	    }
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		switch(opt)
		{
			case MKMWUP:
				if(l_nMin[currColumn] > 0)
					l_nMin[currColumn]--;
				sound(KICK_HIT);
				return 0;
			case MKMWDW:
				if(l_nMin[currColumn] < list[currColumn].size() - nListItems)
					l_nMin[currColumn]++;
				sound(KICK_HIT);
				return 0;
			case MKUP:
				l_nFocus[currColumn]--;
				if(l_nFocus[currColumn] >= 0 && l_nFocus[currColumn] < l_nMin[currColumn])
					if(l_nMin[currColumn] > 0) l_nMin[currColumn]--;
				if(l_nFocus[currColumn] < 0) {
					l_nFocus[currColumn] = list[currColumn].size() - 1;
					l_nMin[currColumn] = list[currColumn].size() - nListItems;
					if(l_nMin[currColumn] < 0) l_nMin[currColumn] = 0;
				}
				sound(KICK_HIT);
				return 0;
			case MKDW:
				l_nFocus[currColumn]++;
				if(l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems && l_nFocus[currColumn] < list[currColumn].size())
					l_nMin[currColumn]++;
				if(l_nFocus[currColumn] >= list[currColumn].size()) {
					l_nFocus[currColumn] = 0;
					l_nMin[currColumn] = 0;
				}
				sound(KICK_HIT);
				return 0;
			case MKLEFT: //left
				if(list[DIRECTORY].size() > 0)
					currColumn = DIRECTORY;
				sound(KICK_HIT);
				return 0;
			case MKRIGHT: //right
				if(list[FILE].size() > 0)
					currColumn = FILE;
				sound(KICK_HIT);
				return 0;
			case MKENTER: //enter
			case MKLMB:
				if(opt == MKLMB && scrollTouch[FILE] || scrollTouch[DIRECTORY])
				{
					if(list[currColumn].size() <= nListItems)
						return 0;
					
					l_nFocus[currColumn] = -1;
					int nList = ClipLow(list[currColumn].size() - nListItems, 1);
					int nRange = nListItems * aligny - 16;
					mGetAlign(textStyle, null);
					int py = y + 2 * aligny + 10;
					float dr = (float)(touchY - py) / nRange;

					l_nMin[currColumn] = (int) BClipRange(dr * nList, 0, nList);
					
					return 0;
				}
				if(list[DIRECTORY].size() > 0 && currColumn == DIRECTORY)
				{
					if(l_nFocus[DIRECTORY] == -1) return 0;
					String dirName = list[DIRECTORY].get(l_nFocus[DIRECTORY]);
					if(dirName.equals(back))
						changeDir(currDir.getParent());
					else changeDir(currDir.checkDirectory(dirName));
					l_nFocus[DIRECTORY] = l_nMin[DIRECTORY] = 0;
					l_nFocus[FILE] = l_nMin[FILE] = 0;
				} else if(list[FILE].size() > 0 && currColumn == FILE) {
					String filename = null;
					currGame = null;
					if(l_nFocus[FILE] == -1) return 0;
					if(currDir.checkFile(list[FILE].get(l_nFocus[FILE])) == null) { //then multiEpisode file (archive)
						String ptr = list[FILE].get(l_nFocus[FILE]);
						currGame = episodes.get(ptr);
						currFile = currGame.isPackage();
					} 
					else 
					{
						filename = list[FILE].get(l_nFocus[FILE]);
						currFile = currDir.checkFile(filename);
						currGame = episodes.get(currFile.getPath());
					}
					specialCall.run(this);
				}
				sound(PISTOL_BODYHIT);
				getInput().resetKeyStatus();
				return 0;
			case MKESC: //esc
			case MKMRB:
				//l_nFocus = l_nMin = 0;
				return 1;
			case MKBSPACE: //backspace
				if(currDir.getParent() != null)
				{
					changeDir(currDir.getParent());
					
					l_nFocus[DIRECTORY] = l_nMin[DIRECTORY] = 0;
					l_nFocus[FILE] = l_nMin[FILE] = 0;
				}
				sound(KICK_HIT);
				return 0;
			case MKPGUP:
				l_nFocus[currColumn] -= (nListItems - 1);
				if(l_nFocus[currColumn] >= 0 && l_nFocus[currColumn] < l_nMin[currColumn])
					if(l_nMin[currColumn] > 0) l_nMin[currColumn] -= (nListItems - 1);
				if(l_nFocus[currColumn] < 0 || l_nMin[currColumn] < 0) {
					l_nFocus[currColumn] = 0;
					l_nMin[currColumn] = 0;
				}
				sound(KICK_HIT);
				return 0;
			case MKPGDW:
				l_nFocus[currColumn] += (nListItems - 1);
				if(l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems && l_nFocus[currColumn] < list[currColumn].size())
					l_nMin[currColumn] += (nListItems - 1);
				if(l_nFocus[currColumn] >= list[currColumn].size() || l_nMin[currColumn] > list[currColumn].size() - nListItems) {
					l_nFocus[currColumn] = list[currColumn].size() - 1;
					if(list[currColumn].size() >= nListItems)
						l_nMin[currColumn] = list[currColumn].size() - nListItems;
					else l_nMin[currColumn] = list[currColumn].size() - 1;
				}
				sound(KICK_HIT);
				return 0;
			case MKHOME:
				l_nFocus[currColumn] = 0;
				l_nMin[currColumn] = 0;
				sound(KICK_HIT);
				return 0;
			case MKEND:
				l_nFocus[currColumn] = list[currColumn].size() - 1;
				if(list[currColumn].size() >= nListItems)
					l_nMin[currColumn] = list[currColumn].size() - nListItems;
				else l_nMin[currColumn] = list[currColumn].size() - 1;
				sound(KICK_HIT);
				return 0;
		}
		return 0;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(mx >= x + width / 2) currColumn = 1;
		else currColumn = 0;

		if(!Gdx.input.isTouched()) {
			scrollTouch[DIRECTORY] = false;
			scrollTouch[FILE] = false;
		}
		
		touchY = my;
		if(mx > scrollX[currColumn] && mx < scrollX[currColumn] + 14) 
		{
			if(Gdx.input.isTouched())
				scrollTouch[currColumn] = true;
			else scrollTouch[currColumn] = false;
			return true;
		}

		if((!scrollTouch[DIRECTORY] && !scrollTouch[FILE]) && list[currColumn].size() > 0)
		{
			mGetAlign(textStyle, null);
			int py = y + 2 * aligny + 3;

			for(int i = l_nMin[currColumn]; i >= 0 && i < l_nMin[currColumn] + nListItems && i < list[currColumn].size(); i++) {	
			    if(mx > x && mx < scrollX[FILE])
					if(my > py && my < py + aligny)
					{
						l_nFocus[currColumn] = i;
						return true;
					}
			    
				py += aligny + nItemHeight;
			}
		}
		return false;
	}

	@Override
	public void open(MENU pMenu) {
		boolean ostate = showmain;
		if(numplayers > 1 || mFakeMultiplayer) 
			showmain = true;
		else showmain = false;

		if(currDir == cache && ostate != showmain)
		{
			currDir = null; //force to update filelist
			changeDir(cache);
		}
	}

	@Override
	public void close(MENU pMenu) {
		for(int i = 0; i < 2; i++)
			l_nFocus[i] = l_nMin[i] = 0;
	}
}


