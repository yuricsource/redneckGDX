package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Gameutils.*;
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
import static ru.m210projects.Build.FileHandle.Compat.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Redneck.Types.IniFile;
import ru.m210projects.Build.FileHandle.FileEntry;
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
	IniFile currIni;

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
		
		if(showmain)
		{
			tmpList.add("none");
			list[FILE].addAll(tmpList);
			tmpList.clear();
		}

//		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext(); ) {
//			FileEntry file = it.next();
//			String name = file.getName();
//			if(file.getExtension().equals("grp") || file.getExtension().equals("zip"))
//				tmpList.add(toLowerCase(name));
//		}
//		
//		Collections.sort(tmpList);
//		list[FILE].addAll(tmpList);
//		tmpList.clear();
		
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

			text = toCharArray(list[FILE].get(i));
			if(!filename.equals("none"))
			{
				String extension = currDir.checkFile(filename).getExtension();
				if(extension.equals("zip")
					|| extension.equals("grp"))
					pal = 2;
			} else pal = 2;
			
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
					if(l_nFocus[FILE] == -1) return 0;
					filename = list[FILE].get(l_nFocus[FILE]);
					currFile = currDir.checkFile(filename);
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


