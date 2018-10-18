// This file is part of RedneckGDX
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

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Controls.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Cheats.*;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Build.Strhandler.Bstrcasecmp;
import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;

public class RROSDFunc extends DEFOSDFUNC {

	public RROSDFunc(Engine engine) {
		super(engine);
		
		BGTILE = LOADSCREEN;
		BGCTILE = INGAMELNRDTHREEDEE;
		BORDTILE = VIEWBORDER;

		BITSTH = 1+8+16;
		BITSTL = 1+8+16+32;
		BITS = 8+16+64+4;	
		BORDERANG = 512;
		SHADE = 30;
		PALETTE = 0;

		OSDTEXT_RED      = 1;
		OSDTEXT_BLUE     = 2;
		OSDTEXT_GOLD     = 3;
		OSDTEXT_WHITE 	 = 4;
		OSDTEXT_BROWN    = 5;
		OSDTEXT_YELLOW   = 6;
		OSDTEXT_GREEN	 = 7;
		OSDTEXT_GREY     = 8;
	}
	
	@Override
	public void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale) {
		char[][] osdtext = Console.getTextPtr();
		short[][] fmt = Console.getFmtPtr();
		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			int pos = 0;
			x += 3;
			while (text != null && pos < text.length && text[pos] != 0) {
				pal = ((fmt[ptr][pos]) & ~0xE0);
				charbuf[0] = text[pos++];
				engine.printext256(x, (y << 3) + 3, colorswap(pal), -1, charbuf, 0);
				x += 8;
			}
		}
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		engine.printext256(4+(x<<3),(y<<3), colorswap(pal), -1, text, 0);
	}
	
	private int colorswap(int col)
	{
		switch(col) {
			case 1: //OSDTEXT_RED
				return 143;
			case 2: //OSDTEXT_BLUE
				return 70;
			case 3: //OSDTEXT_GOLD
				return 155;
			case 4: //OSDTEXT_BROWN
				return 50;
			case 5: //OSDTEXT_YELLOW
				return 155;
			case 6: //OSDTEXT_GREEN
				return 127;
			case 7: //OSDTEXT_GREY
				return 10;
		}
		return 30; //WHITE
	}


	

	@Override
	public void showosd(int shown) {
		// fix for TCs like Layre which don't have the BGTILE for
		// some reason
		// most of this is copied from my dummytile stuff in defs.c
		if (tilesizx[BGTILE] == 0 || tilesizy[BGTILE] == 0)
			engine.allocatepermanenttile(BGTILE, BGTILE_SIZEX,
					BGTILE_SIZEY);

		if (cfg.fullscreen == 0 && !gShowMenu)
			Gdx.input.setCursorCatched(shown == 0);
		resetMousePos();
	}

	@Override
	public boolean textHandler(String message) {
		if ( numplayers > 1 ) 
			return false;
		
		char[] lockeybuf = message.toCharArray();
		int i = 0;
		while (i < lockeybuf.length && lockeybuf[i] != 0)
			lockeybuf[i++] += 1;
		String cheat = new String(lockeybuf).toUpperCase();

		int ep = -1, lvl = -1;
		boolean wrap1 = false;
		boolean wrap2 = false;
		
//		System.err.println("/*" + cheatnum++ + "*/" + "\"" + cheat + "\", // " + message);

		boolean IsSkillCheat = cheat.startsWith(cheatCode[7]);
		boolean IsSkipMapCheat = cheat.startsWith(cheatCode[10]);

		if (IsSkillCheat || IsSkipMapCheat) {
			boolean bad = false;
			i = 0;
			while (i < message.length() && message.charAt(i) != 0
					&& message.charAt(i) != ' ')
				i++;
			cheat = cheat.substring(0, i);
			message = message.replaceAll("[\\s]{2,}", " ");
			int startpos = ++i;
			while (i < message.length() && message.charAt(i) != 0
					&& message.charAt(i) != ' ')
				i++;

			if (i <= message.length()) 
			{
				String nEpisode = message.substring(startpos, i);
				nEpisode = nEpisode.replaceAll("[^0-9]", "");
				if (!nEpisode.isEmpty()) {
					try {
						ep = Integer.parseInt(nEpisode);
						wrap1 = true;
						startpos = ++i;
						while (i < message.length()
								&& message.charAt(i) != 0
								&& message.charAt(i) != ' ')
							i++;
						if (i <= message.length()) {
							String nLevel = message.substring(
									startpos, i);
							nLevel = nLevel
									.replaceAll("[^0-9]", "");
							if (!nLevel.isEmpty()) {
								lvl = Integer.parseInt(nLevel);
								wrap2 = true;
							}
						} else if(IsSkipMapCheat) bad = true;
					} catch (Exception e) {
					}
				} else bad = true;
			} else bad = true;

			
			if(bad)
			{
				if(IsSkipMapCheat) 
					Console.Println("rdmeadow [episode] [level]");
				else if(IsSkillCheat)
					Console.Println("rdskill [skill]");
				return true;
			}
		}

		boolean isCheat = false;
		for (int nCheatCode = 0; nCheatCode < cheatCode.length; nCheatCode++)
			if (Bstrcasecmp(cheat, cheatCode[nCheatCode]) == 0) {
				isCheat = true;
				break;
			}

		if ((gm&MODE_GAME) == 0 && isCheat) {
			Console.Println(message + ": not in a game");
			return true;
		}

		if (wrap1) {
			if (wrap2) {
				if (!IsCheatCode(cheat, ep, lvl))
					return false;
			} else {
				if (!IsCheatCode(cheat, ep))
					return false;
			}
		} else {
			if (!IsCheatCode(cheat))
				return false;
		}
		return true;
	}
}
