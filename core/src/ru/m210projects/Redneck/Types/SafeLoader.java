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

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.FileHandle.Compat.toLowerCase;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Redneck.Globals.MAXANIMWALLS;
import static ru.m210projects.Redneck.Globals.MAXCYCLERS;
import static ru.m210projects.Redneck.Animate.MAXANIMATES;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class SafeLoader {

	public String boardfilename;
	public GameInfo addon;
	public String addonFileName;
	public byte warp_on;
	
	public short spriteq[] = new short[1024],spriteqloc,spriteqamount=64;
	public short numanimwalls;
	public int[] msx = new int[2048],msy = new int[2048];
	public short cyclers[][] = new short[MAXCYCLERS][6], numcyclers;
	
	public short mirrorwall[] = new short[64], mirrorsector[] = new short[64], mirrorcnt;
	
	public Animwalltype animwall[] = new Animwalltype[MAXANIMWALLS];
	public PlayerOrig po[] = new PlayerOrig[MAXPLAYERS];
	public PlayerStruct ps[] = new PlayerStruct[MAXPLAYERS];
	public Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
	
	public byte[] show2dsector = new byte[(MAXSECTORS + 7) >> 3];
	
	public boolean shadeEffect[] = new boolean[MAXSECTORS];

	public final int MAXJAILDOORS = 32;
	public int jailspeed[] = new int[MAXJAILDOORS];
	public int jaildistance[] = new int[MAXJAILDOORS];
	public short jailsect[] = new short[MAXJAILDOORS];
	public short jaildirection[] = new short[MAXJAILDOORS];
	public short jailunique[] = new short[MAXJAILDOORS];
	public short jailsound[] = new short[MAXJAILDOORS];
	public short jailstatus[] = new short[MAXJAILDOORS];
	public int jailcount2[] = new int[MAXJAILDOORS];
	
	public final int MAXMINECARDS = 16;
	public int minespeed[] = new int[MAXMINECARDS];
	public int minefulldist[] = new int[MAXMINECARDS];
	public int minedistance[] = new int[MAXMINECARDS];
	public short minechild[] = new short[MAXMINECARDS];
	public short mineparent[] = new short[MAXMINECARDS];
	public short minedirection[] = new short[MAXMINECARDS];
	public short minesound[] = new short[MAXMINECARDS];
	public short minestatus[] = new short[MAXMINECARDS];
	
	public final int MAXTORCHES = 64;
	public short torchsector[] = new short[MAXTORCHES];
	public byte torchshade[] = new byte[MAXTORCHES];
	public short torchflags[] = new short[MAXTORCHES];
	
	public final int MAXLIGHTNINS = 64;
	public short lightninsector[] = new short[MAXLIGHTNINS];
	public short lightninshade[] = new short[MAXLIGHTNINS];
	
	public final int MAXAMBIENTS = 64;
	public short ambienttype[] = new short[MAXAMBIENTS];
	public short ambientid[] = new short[MAXAMBIENTS];
	public short ambienthitag[] = new short[MAXAMBIENTS];
	
	public final int MAXGEOMETRY = 64;
	public short geomsector[] = new short[MAXGEOMETRY];
	public short geoms1[] = new short[MAXGEOMETRY];
	public int geomx1[] = new int[MAXGEOMETRY];
	public int geomy1[] = new int[MAXGEOMETRY];
	public int geomz1[] = new int[MAXGEOMETRY];
	public short geoms2[] = new short[MAXGEOMETRY];
	public int geomx2[] = new int[MAXGEOMETRY];
	public int geomy2[] = new int[MAXGEOMETRY];
	public int geomz2[] = new int[MAXGEOMETRY];

	public boolean plantProcess = false;
	
	public int numlightnineffects, numtorcheffects, 
		numgeomeffects, numjaildoors, numminecart, numambients; 
	public int haveLigthning;

	public int UFO_SpawnCount;
	public int UFO_SpawnTime;
	public int UFO_SpawnHulk;
	
	public short gEndGame;
	public short gEndFirstEpisode;
	
	//RA
	public short BellTime;
	public int BellSound;
	public short word_119BE0;
	public int WindDir;
	public int WindTime;
	public int mamaspawn_count;
	public int fakebubba_spawn;
	public int dword_119C08;
	
	public short actortype[] = new short[MAXTILES];
	
	public int script[] = new int[MAXSCRIPTSIZE];
	public int actorscrptr[] = new int[MAXTILES];

	public int gAnimationCount = 0;
	public ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	
	public short pskyoff[] = new short[MAXPSKYTILES], pskybits, earthquaketime;
	public int parallaxyscale;
	
	public short camsprite, numplayersprites;
	public short connecthead, connectpoint2[] = new short[MAXPLAYERS];
	public short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];
	
	public int randomseed;
	public short global_random;
	
	//UserDef
	public int multimode;
	public int volume_number;
	public int level_number;
	public int player_skill;
	public short from_bonus;
	public short secretlevel;
	public boolean respawn_monsters,respawn_items,respawn_inventory;
	public int eog;
	public boolean god,scrollmode,clipping;
	public int auto_run, crosshair;
	public boolean monsters_off;
	public int last_level, coop, marker, ffire;
	
	public short numsectors, numwalls, numsprites;

	public short[] headspritesect, headspritestat;
	public short[] prevspritesect, prevspritestat;
	public short[] nextspritesect, nextspritestat;
	
	public short[] rorsector = new short[16];
	public byte[] rortype = new byte[16];
	public int rorcnt;
	
	public SECTOR[] sector = new SECTOR[MAXSECTORS];
	public WALL[] wall = new WALL[MAXWALLS];
	public SPRITE[] sprite = new SPRITE[MAXSPRITES];
	
	private String message;
	
	public SafeLoader()
	{
		headspritesect = new short[MAXSECTORS + 1]; 
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES]; 
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES]; 
		nextspritestat = new short[MAXSPRITES];
		
		for(int i = 0; i < MAXPLAYERS; i++)
		{
			ps[i] = new PlayerStruct();
			po[i] = new PlayerOrig();
		}
		for(int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
		for(int i = 0; i < MAXANIMWALLS; i++)
			animwall[i] = new Animwalltype();
		for(int i = 0; i < MAXSPRITES; i++) {
			hittype[i] = new Weaponhit();
			sprite[i] = new SPRITE();
		}
		for(int i = 0; i < MAXSECTORS; i++)
			sector[i] = new SECTOR();
		for(int i = 0; i < MAXWALLS; i++)
			wall[i] = new WALL();
	}
	
	public boolean load(ByteBuffer bb)
	{
		addon = null;
		addonFileName = null;
		message = null;
		warp_on = 0;
		
		try {
			bb.position(SAVEHEADER - SAVELEVELINFO);
			
			multimode = bb.getInt();
			volume_number = bb.getInt();
			level_number = bb.getInt();
			player_skill = bb.getInt();
			
			bb.position(SAVEHEADER + SAVESCREENSHOTSIZE);
	
			LoadGDXBlock(bb);
			MapLoad(bb);
			StuffLoad(bb);
			ConLoad(bb);
			AnimationLoad(bb);
			GameInfoLoad(bb);
			
			if (warp_on == 1) { // try to find addon
				addon = levelGetEpisode(addonFileName);
				if (addon == null) {
					message = "Can't find user episode file: " + addonFileName;
					warp_on = 2;

					volume_number = 0;
					level_number = 7;
				}
			}
			
			if(bb.position() == bb.capacity())
				return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void AnimationLoad(ByteBuffer bb)
	{
		for(int i = 0; i < MAXANIMATES; i++) {
			short index = bb.getShort();
			byte type = bb.get();
			gAnimationData[i].id = index;
			gAnimationData[i].type = type;
			gAnimationData[i].ptr = null;
			gAnimationData[i].goal = bb.getInt();
			gAnimationData[i].vel = bb.getInt();
			gAnimationData[i].sect = bb.getShort();
		}	
		gAnimationCount = bb.getInt();
	}
	
	public void ConLoad(ByteBuffer bb)
	{
		for(int i = 0; i < MAXTILES; i++)
			actortype[i] = (short) (bb.get() & 0xFF);
		for(int i=0;i<MAXSCRIPTSIZE;i++)
			script[i] = bb.getInt();
		for(int i=0;i<MAXTILES;i++)
			actorscrptr[i] = bb.getInt();
		for(int i=0;i<MAXSPRITES;i++) 
			hittype[i].set(bb);
	}
	
	public void GameInfoLoad(ByteBuffer bb)
	{
		pskybits = bb.getShort();
		parallaxyscale = bb.getInt();
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.getShort();

		earthquaketime = bb.getShort();
		from_bonus = bb.getShort();
		secretlevel = bb.getShort();
		respawn_monsters = bb.get() == 1;
		respawn_items = bb.get() == 1;
		respawn_inventory = bb.get() == 1;
		god =  bb.get() == 1;
		auto_run = (bb.getInt() == 1)?1:0;
		crosshair = (bb.getInt() == 1)?1:0;
		monsters_off = bb.get() == 1;
		last_level = bb.getInt();
		eog = bb.getInt();
		coop = bb.getInt();
		marker = bb.getInt();
		ffire = bb.getInt();
		camsprite = bb.getShort();
	    
		connecthead = bb.getShort();
        for(int i = 0; i < MAXPLAYERS; i++) 
        	connectpoint2[i] = bb.getShort();
        numplayersprites = bb.getShort();
	  
        for(int i = 0; i < MAXPLAYERS; i++) 
        	for(int j = 0; j < MAXPLAYERS; j++) 
        		frags[i][j] = bb.getShort();
        
        randomseed = bb.getInt();
        global_random = bb.getShort();
	}

	public void StuffLoad(ByteBuffer bb)
	{
		numcyclers = bb.getShort();
		for(int i = 0; i < MAXCYCLERS; i++)
			for(int j = 0; j < 6; j++)
				cyclers[i][j] = bb.getShort();
		
		for(int i = 0; i < MAXPLAYERS; i++)
			ps[i].set(bb);
		for(int i = 0; i < MAXPLAYERS; i++)
			po[i].set(bb);
		
		numanimwalls = bb.getShort();
		for(int i = 0; i < MAXANIMWALLS; i++) {
			animwall[i].wallnum = bb.getShort();
			animwall[i].tag = bb.getInt();
		}
		for(int i = 0; i < 2048; i++) 
			msx[i] = bb.getInt();
		for(int i = 0; i < 2048; i++) 
			msy[i] = bb.getInt();
		
		spriteqloc = bb.getShort();
		spriteqamount = bb.getShort();
		for(int i = 0; i < 1024; i++)
			spriteq[i] = bb.getShort();
		
		mirrorcnt = bb.getShort();
		for(int i = 0; i < 64; i++)
			mirrorwall[i] = bb.getShort();
		for(int i = 0; i < 64; i++)
			mirrorsector[i] = bb.getShort();
		
		bb.get(show2dsector);
		
		for(int i = 0; i < MAXSECTORS; i++)
			shadeEffect[i] = bb.get() == 1;
		
		numjaildoors = bb.getInt();
		for(int i = 0; i < MAXJAILDOORS; i++)
		{
			jailspeed[i] = bb.getInt();
			jaildistance[i] = bb.getInt();
			jailsect[i] = bb.getShort();
			jaildirection[i] = bb.getShort();
			jailunique[i] = bb.getShort();
			jailsound[i] = bb.getShort();
			jailstatus[i] = bb.getShort();
			jailcount2[i] = bb.getInt();
		}
		
		numminecart = bb.getInt();
		for(int i = 0; i < MAXMINECARDS; i++)
		{
			minespeed[i] = bb.getInt();
			minefulldist[i] = bb.getInt();
			minedistance[i] = bb.getInt();
			minechild[i] = bb.getShort();
			mineparent[i] = bb.getShort();
			minedirection[i] = bb.getShort();
			minesound[i] = bb.getShort();
			minestatus[i] = bb.getShort();
		}

		numtorcheffects = bb.getInt();
		for(int i = 0; i < MAXTORCHES; i++)
		{
			torchsector[i] = bb.getShort();
			torchshade[i] = bb.get();
			torchflags[i] = bb.getShort();
		}
		
		numlightnineffects = bb.getInt();
		for(int i = 0; i < MAXLIGHTNINS; i++)
		{
			lightninsector[i] = bb.getShort();
			lightninshade[i] = bb.getShort();
		}
		
		numambients = bb.getInt();
		for(int i = 0; i < MAXAMBIENTS; i++)
		{
			ambienttype[i] = bb.getShort();
			ambientid[i] = bb.getShort();
			ambienthitag[i] = bb.getShort();
		}

		numgeomeffects = bb.getInt();
		for(int i = 0; i < MAXGEOMETRY; i++)
		{
			geomsector[i] = bb.getShort();
			geoms1[i] = bb.getShort();
			geomx1[i] = bb.getInt();
			geomy1[i] = bb.getInt();
			geomz1[i] = bb.getInt();
			
			geoms2[i] = bb.getShort();
			geomx2[i] = bb.getInt();
			geomy2[i] = bb.getInt();
			geomz2[i] = bb.getInt();
		}
		
		UFO_SpawnCount = bb.getShort();
		UFO_SpawnTime = bb.getShort();
		UFO_SpawnHulk = bb.getShort();
		
		gEndFirstEpisode = bb.getShort();
		gEndGame = bb.getShort();

		plantProcess = bb.get() == 1;
		
		
		BellTime = bb.getShort();
		BellSound = bb.getInt();
		word_119BE0 = bb.getShort();
		WindDir = bb.getInt();
		WindTime = bb.getInt();
		mamaspawn_count = bb.getInt();
		fakebubba_spawn = bb.getInt();
		dword_119C08 = bb.getInt();
	}
	
	public void MapLoad(ByteBuffer bb) throws Exception
	{
		byte[] buf = new byte[144];
		bb.get(buf);
		boardfilename = null;
		String name = new String(buf).trim();
		if(!name.isEmpty()) boardfilename = name;
		
		numwalls = bb.getShort();
		for(int w = 0; w < numwalls; w++) 
			wall[w].buildWall(bb);
		numsectors = bb.getShort();
		for(int s = 0; s < numsectors; s++) 
			sector[s].buildSector(bb);
		for(int i = 0; i < MAXSPRITES; i++) 
			sprite[i].buildSprite(bb);
		for(int i = 0; i <= MAXSECTORS; i++)
			headspritesect[i] = bb.getShort();
		for(int i = 0; i <= MAXSTATUS; i++)
			headspritestat[i] = bb.getShort();
		
		for(int i = 0; i < MAXSPRITES; i++) {
			prevspritesect[i] = bb.getShort();
			prevspritestat[i] = bb.getShort();
			nextspritesect[i] = bb.getShort();
			nextspritestat[i] = bb.getShort();
		}
		
		rorcnt = bb.getInt();
		for(int i = 0; i < 16; i++) {
			rorsector[i] = bb.getShort();
			rortype[i] = bb.get();
		}
	}
	
	public String getMessage() {
		return message;
	}
	
	public void LoadGDXBlock(ByteBuffer bb)
	{
		int pos = bb.position();
		//reserve SAVEGDXDATA bytes for extra data

		addon = null;
		warp_on = bb.get();
		if (warp_on == 1) {
			byte[] buf = new byte[144];
			bb.get(buf);
			addonFileName = toLowerCase(new String(buf).trim());
		}

		bb.position(pos + SAVEGDXDATA);
	}

}
