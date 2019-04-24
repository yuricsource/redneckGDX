// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.waloff;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.FileHandle.Cache1D.kGetBytes;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.RR66;
import static ru.m210projects.Redneck.Globals.TILE_ANIM;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.sndStopMusic;
import static ru.m210projects.Redneck.Sounds.sound;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;
import ru.m210projects.Redneck.Types.AnimFile;

public class AnmScreen extends SkippableAdapter {

	private Runnable callback;
	private int gCutsClock;
	private AnimFile anmfil;
	private int lastanimhack;
	private int frame;
	private long anmtime;
	private long LastMS;
	private String name;

	public AnmScreen(BuildGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		if(game.pMenu.gShowMenu)
			game.pMenu.mClose();

		engine.sampletimer();
		LastMS = engine.getticks();
		gCutsClock = totalclock = 0;
		
		StopAllSounds();
		sndStopMusic();
	}
	
	@Override
	public void hide () {
		engine.setbrightness(ud.brightness>>2, palette, 2);
	}

	@Override
	public void skip() {
		anmClose();
		super.skip();
	}
	
	public AnmScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.setSkipping(callback);
		return this;
	}
	
	public boolean init(String fn, int t)
	{
		if(anmfil != null) return false;
		
		byte[] animbuf = kGetBytes(fn, 0);
		if(animbuf == null) return false;

	    try {
	    	anmfil = new AnimFile(animbuf);

	    	System.arraycopy(anmfil.getPalette(), 0, curpalette, 0, 768);
	    	
		    tilesizx[TILE_ANIM] = 200;
		    tilesizy[TILE_ANIM] = 320;
		    lastanimhack = t;
		    frame = 1;
		    
		    anmtime = 0;
			LastMS = -1;
			name = fn;

		    waloff[TILE_ANIM] = null;
		    return true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	public boolean anmPlay()
	{
		if(anmfil != null) {
			if(LastMS == -1) 
				LastMS = engine.getticks();

			long ms = engine.getticks();
			long dt = ms - LastMS;
			anmtime += dt;
			
			float rate = 1000f;
			if(name.equalsIgnoreCase("redneck.anm")) 
				rate = 700f;
			if(name.equalsIgnoreCase("rr_outro.anm")) 
				rate = 1200f;
			if(lastanimhack == -1) //RRRA statistics screen
				rate = 2000f;
			
			float tick = rate / anmfil.getRate();
			if(anmtime >= tick) {
				if(frame < anmfil.numFrames()) {
					waloff[TILE_ANIM] = anmfil.draw(frame);
					engine.invalidatetile(TILE_ANIM, 0, -1);	// JBF 20031228

					logoanimsounds(frame, lastanimhack);
					
					frame++;
				} 

				anmtime -= tick;
			}
			
			LastMS = ms;
			if(tilesizx[TILE_ANIM] <= 0)
				return false;

			if(waloff[TILE_ANIM] != null) 
				engine.rotatesprite(0<<16,0<<16,65536,512,TILE_ANIM,0,0,2+4+8+16+64, 0,0,xdim-1,ydim-1);
			
			if(frame >= anmfil.numFrames())
				return false;

			return true;
		}

		return false;
	}

	@Override
	public void draw(float delta) {
		if(!anmPlay() && skipCallback != null) {
//			if(!checkAnm()) {
				anmClose();
				if (callback != null) {
					Gdx.app.postRunnable(callback);
					callback = null;
				}
//			}
		}
		
		if (game.pInput.ctrlKeyStatus(ANYKEY)) 
			gCutsClock = totalclock;
		
		int shade = 32 + mulscale(32, sintable[(20 * totalclock) & 2047], 16);
		if (totalclock - gCutsClock < 200 && escSkip) // 2 sec 
			game.getFont(3).drawText(160, 5, "Press ESC to skip", shade, 31, TextAlign.Center, 2, true);
	}
	
	public boolean isInited()
	{
		return anmfil != null;
	}

	public void anmClose() {
		anmfil = null;
	}

	public static void logoanimsounds(int fr, int num)
	{
	    switch(num)
	    {
	        case 0: //intro
	        	if(fr == 1)
	        		sound(29);
	            break;
	        case 1: //interplay
	        	if(fr == 1)
	        		sound(478);
	            break;
	        case 2: //xatrix
	        	if(fr == 1)
	        		sound(479);
	            break;
	        case 5: //episode 1
	        	if(fr == 1 && currentGame.getCON().type != RR66) 
	        		sound(35);
	            break;
	        case 6: //episode 2
	        	if(fr == 1 && currentGame.getCON().type != RR66)
	        		sound(82);
	            break;
	    }
	}

}
