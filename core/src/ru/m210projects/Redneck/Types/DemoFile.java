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

import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.FileHandle.Cache1D.dfWrite;
import static ru.m210projects.Build.FileHandle.Cache1D.kClose;
import static ru.m210projects.Build.FileHandle.Cache1D.kOpen;
import static ru.m210projects.Build.FileHandle.Cache1D.kRead;
import static ru.m210projects.Build.FileHandle.Cache1D.kdfRead;
import static ru.m210projects.Build.FileHandle.Compat.Bclose;
import static ru.m210projects.Build.FileHandle.Compat.Blseek;
import static ru.m210projects.Build.FileHandle.Compat.Bopen;
import static ru.m210projects.Build.FileHandle.Compat.Bwrite;
import static ru.m210projects.Build.FileHandle.Compat.SEEK_SET;
import static ru.m210projects.Build.FileHandle.Compat.cache;
import static ru.m210projects.Build.FileHandle.Compat.toLowerCase;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.ResourceHandler.levelGetEpisode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main.UserFlag;

public class DemoFile {

	public int rcnt = 0;
	public Input recsync[][];
	public static int recfilep;
	public int reccnt;
	public int version;
	public int volume_number, level_number, player_skill;
	public int coop;
	public int ffire;
	public int multimode;
	public boolean monsters_off;
	public boolean respawn_monsters;
	public boolean respawn_items;
	public boolean respawn_inventory;
	public int playerai;
	public String[] user_name = new String[MAXPLAYERS];
	public int auto_run;
	public String boardfilename;
	public int[] aim_mode = new int[MAXPLAYERS], auto_aim = new int[MAXPLAYERS];
	public GameInfo addon;

	private final boolean unpacked = false; //debug
	
	//Record
	
	public int totalreccnt;
	public int recversion;
	public byte[] recbuf;

	public DemoFile(String filename) throws Exception {
		rcnt = 0;
		recversion = -1;
		ud.rec = null;
		recfilep = kOpen(filename, loadfromgrouponly);
		if(recfilep == -1) throw new Exception("File not found");
		reccnt = kRead(recfilep, 4);
		version = (kRead(recfilep, 1) & 0xFF);
		
		if( version != BYTEVERSIONRR && version != GDXBYTEVERSION)
		{
			kClose(recfilep);
			throw new Exception("Wrong version!");
		}
	     
		volume_number = kRead(recfilep, 1);
		level_number = kRead(recfilep, 1);
		player_skill = kRead(recfilep, 1);
		
		coop = kRead(recfilep, 1);
		ffire = kRead(recfilep, 1);
		multimode = kRead(recfilep, 2);
		monsters_off = kRead(recfilep, 2)==1;
		respawn_monsters = kRead(recfilep, 4)==1;
		respawn_items = kRead(recfilep, 4)==1;
		respawn_inventory = kRead(recfilep, 4)==1;
		playerai = kRead(recfilep, 4);
		for ( int i = 0; i < MAXPLAYERS; i++ ) {
			kRead(recfilep, tempbuf, 32);
			user_name[i] = new String(tempbuf, 0, 32).trim();
		}

		if(version >= GDXBYTEVERSION)
		{
			kRead(recfilep, tempbuf, 144);
			String addonName = toLowerCase(new String(tempbuf).trim());
			addon = levelGetEpisode(addonName);
		}

		for(int i=0;i<multimode;i++) {
			aim_mode[i] = kRead(recfilep, 1);
			if(version >= GDXBYTEVERSION)
				auto_aim[i] = kRead(recfilep, 1);
		}

		recsync = new Input[reccnt][MAXPLAYERS];
		int dasizeof = Input.sizeof(version)*multimode;
		byte[] recsyncbuf = new byte[dasizeof * RECSYNCBUFSIZ];
		
		int rccnt = 0;
		for(int c = 0; c <= reccnt / RECSYNCBUFSIZ; c++)
		{
			int l = min(reccnt - rccnt, RECSYNCBUFSIZ);
			if(!unpacked) 
				kdfRead(recsyncbuf, dasizeof, l / multimode, recfilep);
			else kRead(recfilep, recsyncbuf, Input.sizeof(version) * l);

			ByteBuffer bb = ByteBuffer.wrap(recsyncbuf);
			bb.order( ByteOrder.LITTLE_ENDIAN);
			
			for(int rcnt = rccnt; rcnt < rccnt + l; rcnt += multimode)
				for ( int i = 0; i < multimode; i++ ) 
					recsync[rcnt / multimode][i] = new Input(bb, version);
			rccnt += RECSYNCBUFSIZ;
		}
		
		kClose(recfilep);
	}
	
	public DemoFile(int nVersion)
	{
		if (ud.recstat == 2)
			kClose(recfilep);
		int a, b, c, d, democount = 0;
		String fn = null;
		do {
			if (democount > 9999)
				return;

			a = ((democount / 1000) % 10);
			b = ((democount / 100) % 10);
			c = ((democount / 10) % 10);
			d = (democount % 10);

			fn = "demo" + a + b + c + d + ".dmo";
			if (cache.checkFile(fn) == null)
				break;

			democount++;
		} while (true);
		
		if (fn == null || (recfilep = Bopen(fn, "rw")) == -1)
			return;

		Console.Println("Start recording to " + fn);
		
		Bwrite(recfilep, 0, 4);
		Bwrite(recfilep, nVersion, 1);
		Bwrite(recfilep, ud.volume_number, 1);
		Bwrite(recfilep, ud.level_number, 1);
		Bwrite(recfilep, ud.player_skill, 1);
		Bwrite(recfilep, ud.coop, 1);
		Bwrite(recfilep, ud.ffire, 1);
		Bwrite(recfilep, ud.multimode, 2);
		Bwrite(recfilep, ud.monsters_off ? 1 : 0, 2);
		Bwrite(recfilep, ud.respawn_monsters ? 1 : 0, 4);
		Bwrite(recfilep, ud.respawn_items ? 1 : 0, 4);
		Bwrite(recfilep, ud.respawn_inventory ? 1 : 0, 4);
		Bwrite(recfilep, ud.playerai, 4);

		for (int i = 0; i < MAXPLAYERS; i++) {
			buildString(buf, 0, ud.user_name[i]);
			Bwrite(recfilep, buf, 32);
		}

		if(nVersion >= GDXBYTEVERSION) {
			byte[] name = new byte[144];
			if(mUserFlag == UserFlag.Addon && currentGame != null)
			{
				FileEntry addon = currentGame.getFile();
				if(addon != null) {
					String path = addon.getPath();
					path += ":" + currentGame.ConName;
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
				else {
					String path = currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
			}
			Bwrite(recfilep, name, name.length);
		}
		
		for (int i = 0; i < ud.multimode; i++) {
			Bwrite(recfilep, ps[i].aim_mode, 1);
			if (nVersion >= GDXBYTEVERSION) // JBF 20031126
				Bwrite(recfilep, ps[i].auto_aim, 1);
		}

		totalreccnt = 0;
		reccnt = 0;
		recversion = nVersion;
		recbuf = new byte[RECSYNCBUFSIZ * Input.sizeof(BYTEVERSION)];
//		gDemoScreen.demofiles.add(fn); XXX
	}
	
	public void record() {
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			int len = Input.sizeof(recversion);
			System.arraycopy(sync[i].getBytes(recversion), 0, recbuf, reccnt * len, len);
			reccnt++;
			totalreccnt++;

			if (reccnt >= RECSYNCBUFSIZ) {
				if (!unpacked) {
					int dasizeof = len * ud.multimode;
					try {
						dfWrite(recbuf, dasizeof, reccnt / ud.multimode, recfilep);
					} catch (Exception e) {
						Console.Println(e.getMessage(), OSDTEXT_RED);
						close();
					}
				} else
					Bwrite(recfilep, recbuf, reccnt * len);	
				reccnt = 0;
			}
		}
	}
	
	public void close()
	{
		if (ud.recstat == 1) {
			try {
				if (reccnt > 0) {
					int len = Input.sizeof(recversion);
					if (!unpacked) {
						int dasizeof = len * ud.multimode;
						dfWrite(recbuf, dasizeof, reccnt / ud.multimode, recfilep);
					} else
						Bwrite(recfilep, recbuf, reccnt * len);	
				}
				Blseek(recfilep, SEEK_SET, 0);
				Bwrite(recfilep, totalreccnt, 4);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Console.Println("Stop recording");
			
			ud.recstat = ud.m_recstat = 0;
			ud.rec = null;
			recversion = 0;
			Bclose(recfilep);
		}
	}

}
