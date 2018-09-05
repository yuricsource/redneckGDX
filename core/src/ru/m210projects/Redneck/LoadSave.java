package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Interpolation.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Network.mFakeMultiplayer;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Types.ANIMATION.CEILZ;
import static ru.m210projects.Redneck.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLX;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLY;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.ANIMATION;
import ru.m210projects.Redneck.Types.LSInfo;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.SaveManager;
import ru.m210projects.Redneck.Types.Weaponhit;

public class LoadSave {
	
	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;
	public static boolean gScreenCapture;
	
	public static LSInfo lsInf = new LSInfo();
	
	public static byte[] saveBuffer;
	
	public static final String savsign = "RGDX";
	
	public static final int gdxSave = 100;
	public static final int SAVEVERSION = savsign.length() + 2; //version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 16;
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;
	
	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 128;

	public static String lastload;
	public static int quickslot = 0;
	
	public static void FindSaves()
	{
		byte[] buf = new byte[SAVENAME];
		
		int fil = -1;
		for (Iterator<FileEntry> it = cache.checkDirectory("<userdir>").getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			
			if (file.getExtension().equals("sav")) {
				String name = file.getFile().getName();
				if ((fil = Bopen(name, "R")) != -1) {
					Bread(fil, buf, 4);
					String signature = new String(buf,0,4);
					if(signature.equals(savsign)) {
						int nVersion = Bread(fil, 2);
						if(nVersion == gdxSave) {
							Bread(fil, buf, 8);
							long time = LittleEndian.getLong(buf);
							Bread(fil, buf, SAVENAME);
							String savname = new String(buf).trim();
							SaveManager.add(savname, time, file.getName());
						}
					}
					Bclose(fil);
				}
			}
		}
		SaveManager.sort();
	}
	
	public static int lsReadLoadData(String filename)
	{
		int fil = Bopen(FileUserdir+filename, "R");
		if( fil != -1)
		{
			byte[] buf = new byte[144];
			Bread(fil, buf, SAVEHEADER);
			ByteBuffer bb = ByteBuffer.wrap(buf);
			bb.order( ByteOrder.LITTLE_ENDIAN);
			
			if(waloff[TILE_LOADSHOT] == null)
				engine.allocatepermanenttile(TILE_LOADSHOT, 160, 100);
			
			int nVersion = checkSave(bb);
			lsInf.clear();
			
			if(nVersion == gdxSave)
			{
				bb.position(SAVEVERSION);
				lsInf.date = Main.date.getDate(bb.getLong());
				bb.position(SAVEVERSION + SAVETIME + SAVENAME); //to SAVELEVELINFO
				
				lsInf.read(bb);

				if(Bread(fil, waloff[TILE_LOADSHOT], SAVESCREENSHOTSIZE) == -1)
					return -1;

				engine.invalidatetile(TILE_LOADSHOT, 0, 255);
				Bclose(fil);
				return 1;
			}	
		} else lsInf.clear();
		return -1;
	}
	
	public static final char[] filenum = new char[4];
	public static String makeNum(int num)
	{
		filenum[3] = (char) ((num%10)+48);
		filenum[2] = (char) (((num/10)%10)+48);
		filenum[1] = (char) (((num/100)%10)+48);
		filenum[0] = (char) (((num/1000)%10)+48);
		
		return new String(filenum);
	}

	public static int checkSave(ByteBuffer bb)
	{
		byte[] buf = new byte[4];
		bb.get(buf);
		String signature = new String(buf);
		
		if(!signature.equals(savsign))
			return 0;

		return bb.getShort();
	}
	
	public static int savegame(String savename, String filename)
	{
		File file = Bcheck(FileUserdir+filename, "R");
		if(file != null)
			file.delete();
	
		int fil = Bopen(FileUserdir+filename, "RW");
		if(fil != -1) {
			long time = Main.date.getCurrentDate();
			save(fil, savename, time);
			SaveManager.add(savename, time, filename);
			lastload = filename;

			addmessage("GAME SAVED");
			return 0;
		} else 
			addmessage("Game not saved. Access denied!");

		return -1;
	}
	
	public static void MapSave(int fil)
	{
		if(boardfilename != null)
			Bwrite(fil, boardfilename.toCharArray(), 144);
		else Bwrite(fil, new byte[144], 144);
		
		int bufsize = 2 + (numsectors * SECTOR.sizeof) 
				+ 2 + (numwalls * WALL.sizeof)
				+ (MAXSPRITES * SPRITE.sizeof) 
				+ (MAXSECTORS+1) * 2
				+ (MAXSTATUS+1) * 2 + MAXSPRITES * 8;
		
		ByteBuffer bb = ByteBuffer.allocate(bufsize); 
		bb.order(ByteOrder.LITTLE_ENDIAN); 

		bb.putShort(numwalls);
		for(int w = 0; w < numwalls; w++)
			bb.put(wall[w].getBytes());
		
		bb.putShort(numsectors);
		for(int s = 0; s < numsectors; s++) 
			bb.put(sector[s].getBytes());

		for(int i = 0; i < MAXSPRITES; i++)
			bb.put(sprite[i].getBytes());

		for(int i = 0; i <= MAXSECTORS; i++)
			bb.putShort(headspritesect[i]);
		for(int i = 0; i <= MAXSTATUS; i++)
			bb.putShort(headspritestat[i]);
		for(int i = 0; i < MAXSPRITES; i++) {
			bb.putShort(prevspritesect[i]);
			bb.putShort(prevspritestat[i]);
			bb.putShort(nextspritesect[i]);
			bb.putShort(nextspritestat[i]);
		}

		Bwrite(fil,bb.array(),bb.capacity());
	}
	
	public static void StuffSave(int fil)
	{
		int bufsize = 2 + MAXCYCLERS * 6 * 2
					+ MAXPLAYERS * PlayerStruct.sizeof
					+ MAXPLAYERS * PlayerOrig.sizeof
					+ 2 + MAXANIMWALLS * 6
					+ 2048 * 8 + 4 + 1024 * 2
					+ 2 + 64 * 4 + show2dsector.length
					+ MAXSECTORS + 4 + 22 * MAXJAILDOORS +  4 + 22 * MAXMINECARDS
					+ 4 + 5 * MAXTORCHES + 4 + 4 * MAXLIGHTNINS + 4 + 6 * MAXAMBIENTS
					+ 30 * MAXGEOMETRY + 15;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN); 

		bb.putShort(numcyclers);
		for(int i = 0; i < MAXCYCLERS; i++)
			for(int j = 0; j < 6; j++)
				bb.putShort(cyclers[i][j]);
		for(int i = 0; i < MAXPLAYERS; i++)
			bb.put(ps[i].getBytes());
		for(int i = 0; i < MAXPLAYERS; i++)
			bb.put(po[i].getBytes());
		bb.putShort(numanimwalls);
		for(int i = 0; i < MAXANIMWALLS; i++) {
			bb.putShort(animwall[i].wallnum);
			bb.putInt(animwall[i].tag);
		}
		for(int i = 0; i < 2048; i++) 
			bb.putInt(msx[i]);
		for(int i = 0; i < 2048; i++) 
			bb.putInt(msy[i]);
		
		bb.putShort(spriteqloc);
		bb.putShort(spriteqamount);
		for(int i = 0; i < 1024; i++)
			bb.putShort(spriteq[i]);
		
		bb.putShort(mirrorcnt);
		for(int i = 0; i < 64; i++)
			bb.putShort(mirrorwall[i]);
		for(int i = 0; i < 64; i++)
			bb.putShort(mirrorsector[i]);
		
		bb.put(show2dsector);
		
		for(int i = 0; i < MAXSECTORS; i++)
			bb.put(shadeEffect[i]?(byte)1:0);
		
		bb.putInt(numjaildoors);
		for(int i = 0; i < MAXJAILDOORS; i++)
		{
			bb.putInt(jailspeed[i]);
			bb.putInt(jaildistance[i]);
			bb.putShort(jailsect[i]);
			bb.putShort(jaildirection[i]);
			bb.putShort(jailunique[i]);
			bb.putShort(jailsound[i]);
			bb.putShort(jailstatus[i]);
			bb.putInt(jailcount2[i]);
		}
	
		bb.putInt(numminecart);
		for(int i = 0; i < MAXMINECARDS; i++)
		{
			bb.putInt(minespeed[i]);
			bb.putInt(minefulldist[i]);
			bb.putInt(minedistance[i]);
			bb.putShort(minechild[i]);
			bb.putShort(mineparent[i]);
			bb.putShort(minedirection[i]);
			bb.putShort(minesound[i]);
			bb.putShort(minestatus[i]);
		}
		
		bb.putInt(numtorcheffects);
		for(int i = 0; i < MAXTORCHES; i++)
		{
			bb.putShort(torchsector[i]);
			bb.put(torchshade[i]);
			bb.putShort(torchflags[i]);
		}
		
		bb.putInt(numlightnineffects);
		for(int i = 0; i < MAXLIGHTNINS; i++)
		{
			bb.putShort(lightninsector[i]);
			bb.putShort(lightninshade[i]);
		}
		
		bb.putInt(numambients);
		for(int i = 0; i < MAXAMBIENTS; i++)
		{
			bb.putShort(ambienttype[i]);
			bb.putShort(ambientid[i]);
			bb.putShort(ambienthitag[i]);
		}
		
		bb.putInt(numgeomeffects);
		for(int i = 0; i < MAXGEOMETRY; i++)
		{
			bb.putShort(geomsector[i]);
			bb.putShort(geoms1[i]);
			bb.putInt(geomx1[i]);
			bb.putInt(geomy1[i]);
			bb.putInt(geomz1[i]);
			
			bb.putShort(geoms2[i]);
			bb.putInt(geomx2[i]);
			bb.putInt(geomy2[i]);
			bb.putInt(geomz2[i]);
		}
		
		bb.putShort((short)word_18B7A4);
		bb.putShort((short)word_18B7A6);
		bb.putShort((short)word_18B7AA);

		bb.putShort(gEndFirstEpisode);
		bb.putShort(gEndGame);
		bb.put((byte) (plantProcess?1:0));

		Bwrite(fil,bb.array(),bb.capacity());
	}

	public static void ConSave(int fil)
	{
		int bufsiz = MAXTILES + 4 * MAXSCRIPTSIZE 
				+ 4 * MAXTILES 
				+ MAXSPRITES * Weaponhit.sizeof;
		
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);
		bb.order(ByteOrder.LITTLE_ENDIAN); 
		for(int i = 0; i < MAXTILES; i++)
			bb.put((byte)actortype[i]);
		for(int i=0;i<MAXSCRIPTSIZE;i++)
	    	bb.putInt(script[i]);
		for(int i=0;i<MAXTILES;i++)
			bb.putInt(actorscrptr[i]);
		for(int i=0;i<MAXSPRITES;i++) 
			bb.put(hittype[i].getBytes());

		Bwrite(fil,bb.array(),bb.capacity());
	}
	
	public static void Stuff2Save(int fil) //XXX funcname
	{
		ByteBuffer bb = ByteBuffer.allocate(1113);
		bb.order(ByteOrder.LITTLE_ENDIAN); 
		bb.putShort(pskybits);
		bb.putInt(parallaxyscale);
		for(int i = 0; i < MAXPSKYTILES; i++)
			bb.putShort(pskyoff[i]);
		
		bb.putShort(earthquaketime);
		bb.putShort((short)ud.from_bonus);
		bb.putShort((short)ud.secretlevel);
        bb.put(ud.respawn_monsters?(byte)1:0);
        bb.put(ud.respawn_items?(byte)1:0);
        bb.put(ud.respawn_inventory?(byte)1:0);
        bb.put(ud.god?(byte)1:0);
        bb.putInt(ud.auto_run);
        bb.putInt(ud.crosshair);
        bb.put(ud.monsters_off?(byte)1:0);
        bb.putInt(ud.last_level);
        bb.putInt(ud.eog);
        bb.putInt(ud.coop);
        bb.putInt(ud.marker);
        bb.putInt(ud.ffire);
        bb.putShort(camsprite);
	    
        bb.putShort(connecthead);
        for(int i = 0; i < MAXPLAYERS; i++) 
        	bb.putShort(connectpoint2[i]);
        bb.putShort(numplayersprites);
	  
        for(int i = 0; i < MAXPLAYERS; i++) 
        	for(int j = 0; j < MAXPLAYERS; j++) 
        		bb.putShort(frags[i][j]);
        bb.putInt(engine.getrand());
        bb.putShort(global_random);
        
        Bwrite(fil,bb.array(),bb.capacity());
	}
	
	public static void AnimationSave(int fil)
	{
		for(int i = 0; i < MAXANIMATES; i++) {
			Bwrite(fil,gAnimationData[i].id, 2);
			Bwrite(fil,gAnimationData[i].type, 1);
			Bwrite(fil,gAnimationData[i].goal, 4);
            Bwrite(fil,gAnimationData[i].vel, 4);
            Bwrite(fil,gAnimationData[i].sect, 2);
		}	
		Bwrite(fil,gAnimationCount,4);
	}
	
	public static void SaveVersion(int fil, int nVersion)
	{
		Bwrite(fil, savsign.toCharArray(), 4);
		Bwrite(fil, nVersion, 2);
	}
	
	public static void SaveHeader(int fil, String savename, long time)
	{
		SaveVersion(fil, gdxSave);
		
		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		Bwrite(fil, buf, 8);
		Bwrite(fil, savename.toCharArray(), SAVENAME);
		
		Bwrite(fil, ud.multimode, 4);
		Bwrite(fil, ud.volume_number, 4);
		Bwrite(fil, ud.level_number, 4); 
		Bwrite(fil, ud.player_skill, 4); 
	}
	
	public static void SaveGDXBlock(int fil)
	{
		Bwrite(fil, saveBuffer, SAVESCREENSHOTSIZE);	
		Bwrite(fil, new byte[SAVEGDXDATA], SAVEGDXDATA); 	
	}

	public static void save(int fil, String savename, long time)
	{
		SaveHeader(fil, savename, time);
		SaveGDXBlock(fil);
		
		MapSave(fil);
		StuffSave(fil);
		ConSave(fil);
		AnimationSave(fil);
		Stuff2Save(fil);
		
		Bclose(fil);
		
		System.gc();
	}

	public static void quicksave() {
		if(numplayers > 1 || mFakeMultiplayer) return;
		if (sprite[ps[myconnectindex].i].extra > 0) {
			gQuickSaving = true;
			gScreenCapture = true;
		}
	}
	
	public static void quickload()
	{
		if(numplayers > 1 || mFakeMultiplayer) return;
		final String loadname = SaveManager.getLast();
		if(loadname != null)
		{
			gm = MODE_LOADING;
			Gdx.app.postRunnable(new Runnable() {
				public void run() {
					loadgame(loadname);
				}
			});
		}
	}
	
	public static void AnimationLoad(ByteBuffer bb)
	{
		for(int i = 0; i < MAXANIMATES; i++) {
			short index = bb.getShort();
			byte type = bb.get();
			Object object = getobject(index, type);
			gAnimationData[i].id = index;
			gAnimationData[i].type = type;
			gAnimationData[i].ptr = object;
			gAnimationData[i].goal = bb.getInt();
			gAnimationData[i].vel = bb.getInt();
			gAnimationData[i].sect = bb.getShort();
		}	
		gAnimationCount = bb.getInt();
	}
	
	public static void ConLoad(ByteBuffer bb)
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
	
	public static void Stuff2Load(ByteBuffer bb)
	{
		pskybits = bb.getShort();
		parallaxyscale = bb.getInt();
		for(int i = 0; i < MAXPSKYTILES; i++)
			pskyoff[i] = bb.getShort();

	    System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);
		
		earthquaketime = bb.getShort();
		ud.from_bonus = bb.getShort();
		ud.secretlevel = bb.getShort();
		ud.respawn_monsters = bb.get() == 1;
		ud.respawn_items = bb.get() == 1;
		ud.respawn_inventory = bb.get() == 1;
		ud.god =  bb.get() == 1;
		ud.auto_run = (bb.getInt() == 1)?1:0;
		ud.crosshair = (bb.getInt() == 1)?1:0;
		ud.monsters_off = bb.get() == 1;
		ud.last_level = bb.getInt();
		ud.eog = bb.getInt();
		ud.coop = bb.getInt();
		ud.marker = bb.getInt();
		ud.ffire = bb.getInt();
		camsprite = bb.getShort();
	    
		connecthead = bb.getShort();
        for(int i = 0; i < MAXPLAYERS; i++) 
        	connectpoint2[i] = bb.getShort();
        numplayersprites = bb.getShort();
	  
        for(int i = 0; i < MAXPLAYERS; i++) 
        	for(int j = 0; j < MAXPLAYERS; j++) 
        		frags[i][j] = bb.getShort();
        
        engine.srand(bb.getInt());
        global_random = bb.getShort();
	}
	
	public static void StuffLoad(ByteBuffer bb)
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
		
		word_18B7A4 = bb.getShort();
		word_18B7A6 = bb.getShort();
		word_18B7AA = bb.getShort();
		
		gEndFirstEpisode = bb.getShort();
		gEndGame = bb.getShort();

		tilesizy[0] = 0;
	    tilesizx[0] = 0;
	    waloff[0] = null;
	    
	    BowlReset();
	    
		plantProcess = bb.get() == 1;
	}
	
	public static void MapLoad(ByteBuffer bb)
	{
		byte[] buf = new byte[144];
		bb.get(buf);
		
		boardfilename = null;
		String name = new String(buf).trim();
		if(!name.isEmpty()) boardfilename = name;
		
		numwalls = bb.getShort();
		for(int w = 0; w < numwalls; w++) {
			wall[w] = new WALL();
			wall[w].buildWall(bb);
		}

		numsectors = bb.getShort();
		for(int s = 0; s < numsectors; s++) {
			sector[s] = new SECTOR();
			sector[s].buildSector(bb);
		}
		
		// Store all sprites (even holes) to preserve indeces
		for(int i = 0; i < MAXSPRITES; i++) {
			sprite[i] = new SPRITE();
			sprite[i].buildSprite(bb);
		}

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
	}
	
	public static void LoadGDXBlock(ByteBuffer bb)
	{
		int pos = bb.position();
		//reserve SAVEGDXDATA bytes for extra data
		bb.position(pos + SAVEGDXDATA);
	}
	
	public static boolean load(ByteBuffer bb)
	{
		int nVersion = checkSave(bb);	

		if(nVersion != gdxSave)
			return false;
		
		ready2send = false;
		
		engine.getAudio().getSound().stopAllSounds();

		try {
			bb.position(SAVEHEADER - SAVELEVELINFO);
			
			ud.multimode = bb.getInt();
			ud.volume_number = bb.getInt();
			ud.level_number = bb.getInt();
			ud.player_skill = bb.getInt();
			
			bb.position(SAVEHEADER + SAVESCREENSHOTSIZE);

			LoadGDXBlock(bb);
			MapLoad(bb);
			StuffLoad(bb);
			ConLoad(bb);
			AnimationLoad(bb);
			Stuff2Load(bb);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		if(ps[myconnectindex].over_shoulder_on != 0)
		{
	         cameradist = 0;
	         cameraclock = 0;
	         ps[myconnectindex].over_shoulder_on = 1;
		}

		screenpeek = myconnectindex;

		Arrays.fill(gotpic, (byte)0);
		clearsoundlocks();
		cacheit();
		docacheit();

		musicvolume = ud.volume_number;
    	musiclevel = ud.level_number;
    	sndPlayMusic(music_fn[ud.volume_number][ud.level_number]);

		ud.recstat = 0;

		if(ps[myconnectindex].jetpack_on != 0)
			spritesound(DUKE_JETPACK_IDLE,ps[myconnectindex].i);

		setpal(ps[myconnectindex]);
		vscrn(ud.screen_size);

		engine.getAudio().getSound().setReverb(0);
	   
		if(ud.lockout == 0)
		{
			for(int x=0;x<numanimwalls;x++)
				if( wall[animwall[x].wallnum].extra >= 0 )
					wall[animwall[x].wallnum].picnum = wall[animwall[x].wallnum].extra;
		}
	
		InterpolationCount = 0;
		startofdynamicinterpolations = 0;

		int k = headspritestat[3];
		while(k >= 0)
		{
	        switch(sprite[k].lotag)
	        {
	            case 31:
	            case 32:
	            case 25:
	            case 17:
	                viewBackupSectorLoc(sprite[k].sectnum, sector[sprite[k].sectnum]);
	                break;
	            case 0:
	            case 5:
	            case 6:
	            case 11:
	            case 14:
	            case 15:
	            case 16:
	            case 26:
	            case 30:
	                setsectinterpolate(k);
	                break;
	        }

	        k = nextspritestat[k];
		}

		for(int i = gAnimationCount-1;i>=0;i--)
		{
			ANIMATION gAnm = gAnimationData[i];
			Object obj = gAnm.ptr;
			switch(gAnm.type)
			{
	    	 	case WALLX:
	    	 	case WALLY:
	    	 		viewBackupWallLoc(gAnm.id, (WALL)obj);
					break;
	    	 	case FLOORZ:
	    	 	case CEILZ:
	    	 		viewBackupSectorLoc(gAnm.id, (SECTOR)obj);
					break;
			}
		}
		
		ps[myconnectindex].fta = 0;

		everyothertime = 0;

		Arrays.fill(playerquitflag, 1);

		resetmys();
		
		if ( ps[myconnectindex].one_parallax_sectnum >= 0 )
			setupbackdrop(sector[ps[myconnectindex].one_parallax_sectnum].ceilingpicnum);

		clearfifo();
		
		resettimevars();
		
		gm = MODE_GAME;

		return true;
	}
	
	
	public static boolean loadgame(String filename)
	{
		int fil = Bopen(FileUserdir + filename, "R");
		if(fil != -1) {
			byte[] data = new byte[Bfilelength(fil)];
			Bread(fil, data, data.length);
			ByteBuffer bb = ByteBuffer.wrap(data);
			bb.order( ByteOrder.LITTLE_ENDIAN);

			boolean status = false;
			try {
				status = load(bb);
			} catch(Exception e) {
				e.printStackTrace();
//				GameCrash("Saved game file is corrupt \r\n" + e.toString());
//				clearInstances();
				kGameCrash = true;
			}
			if(status && (lastload == null || lastload.isEmpty()))
				lastload = filename;

			Bclose(fil);
			return status;
		}
		return false;
	}
}
