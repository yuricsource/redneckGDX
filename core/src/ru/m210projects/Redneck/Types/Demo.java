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

package ru.m210projects.Redneck.Types;

import static java.lang.Math.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Build.Strhandler.buildString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Redneck.Input;

public class Demo {

	public int rcnt = 0;
	public Input recsync[][];
	public static byte[] recbuf = new byte[RECSYNCBUFSIZ * Input.sizeof(BYTEVERSION)];
	public static int recversion;

	private static int nDemonum = 0;
	public static List<String> demofiles = new ArrayList<String>();
	public static Demo demfile;
	public static int recfilep, totalreccnt;
	public static String firstdemofile;

	public static int version;

	public static boolean unpacked = false;

	public Demo(int which_demo) throws Exception {
		if (!opendemoread(which_demo, this))
			throw new Exception("Can't load demo file: ");
		rcnt = 0;
	}

	public static boolean opendemoread(int which_demo, Demo dem)
	{
		try {
			ud.reccnt = 0;
	
			if(firstdemofile != null) {
				if ((recfilep = kOpen(firstdemofile,loadfromgrouponly)) == -1) return false;
			} else if ((recfilep = kOpen(demofiles.get(which_demo),loadfromgrouponly)) == -1) return false;
	
			ud.reccnt = kRead(recfilep, 4);
			version = (kRead(recfilep, 1) & 0xFF);
			if( version != BYTEVERSIONRR && version != GDXBYTEVERSION) // || (ud.reccnt < 512) )
			{
				kClose(recfilep);
		        return false;
			}
		     
			ud.volume_number = kRead(recfilep, 1);
			ud.level_number = kRead(recfilep, 1);
			ud.player_skill = kRead(recfilep, 1);
			
			ud.m_coop = kRead(recfilep, 1);
			ud.m_ffire = kRead(recfilep, 1);
			ud.multimode = kRead(recfilep, 2);
			ud.m_monsters_off = kRead(recfilep, 2)==1;
			ud.m_respawn_monsters = kRead(recfilep, 4)==1;
			ud.m_respawn_items = kRead(recfilep, 4)==1;
			ud.m_respawn_inventory = kRead(recfilep, 4)==1;
			ud.playerai = kRead(recfilep, 4);
			for ( int i = 0; i < MAXPLAYERS; i++ ) {
				kRead(recfilep, tempbuf, 32);
				ud.user_name[i] = new String(tempbuf, 0, 32).trim();
			}

			if (version >= GDXBYTEVERSION) {
				kRead(recfilep, tempbuf, 260);
				String name = bCorrectPath(new String(tempbuf, 0, 260).trim());
	
				if( !name.isEmpty() && cache.checkFile(name) != null)
				{
					boardfilename = name; 
					ud.m_level_number = 7;
					ud.m_volume_number = 0;
				}
			}
	
			for(int i=0;i<ud.multimode;i++) {
				ps[i].aim_mode = kRead(recfilep, 1);
				if(version >= GDXBYTEVERSION)
					ps[i].auto_aim = kRead(recfilep, 1);
			}
	
			ud.god = false;
			ud.cashman = ud.eog = ud.showallmap = 0;
			ud.clipping = ud.scrollmode = false;
			ud.overhead_on = ud.pause_on = 0;
	
			newgame(ud.volume_number,ud.level_number,ud.player_skill);
	
			dem.recsync = new Input[ud.reccnt][MAXPLAYERS];
			int dasizeof = Input.sizeof(version)*ud.multimode;
			byte[] recsyncbuf = new byte[dasizeof * RECSYNCBUFSIZ];

			int reccnt = 0;
			for(int c = 0; c <= ud.reccnt / RECSYNCBUFSIZ; c++)
			{
				int l = min(ud.reccnt - reccnt, RECSYNCBUFSIZ);
				if(!unpacked) 
					kdfRead(recsyncbuf,dasizeof, l/ud.multimode, recfilep);
				else kRead(recfilep, recsyncbuf, Input.sizeof(version)*l);
	
				ByteBuffer bb = ByteBuffer.wrap(recsyncbuf);
				bb.order( ByteOrder.LITTLE_ENDIAN);
				
				for(int rcnt = reccnt; rcnt < reccnt + l; rcnt++)
					for ( int i = 0; i < ud.multimode; i++ ) 
						dem.recsync[rcnt][i] = new Input(bb, version);
	
				reccnt += RECSYNCBUFSIZ;
			}
	
			kClose(recfilep);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			kClose(recfilep);
	        return false;
		}
	}

	public static boolean IsOriginalDemo() {
		return (gm == MODE_DEMO && version <= GDXBYTEVERSION)
				|| (gm == MODE_GAME && ud.recstat == 1 && recversion <= GDXBYTEVERSION);
	}

	public static void playback() {
		ready2send = false;
		if (demfile == null) {

			if (cfg.gDemoSeq == 0) {
				gm = MODE_MENU;
				return; // OFF
			}

			if (demofiles != null && demofiles.size() > 0)
			{
				try {
					demfile = new Demo(nDemonum);
				} 
				catch (Exception e)
				{
					Console.Println(e.getMessage() + demofiles.get(nDemonum), OSDTEXT_RED);
					demofiles.remove(nDemonum);
					return;
				} 

				switch (cfg.gDemoSeq) {
				case 1: // Consistently
					if (nDemonum < (demofiles.size() - 1))
						nDemonum++;
					else
						nDemonum = 0;
					break;
				case 2: // Accidentally
					int nextnum = nDemonum;
					if (demofiles.size() > 1) {
						while (nextnum == nDemonum)
							nextnum = (int) (Math.random() * (demofiles.size()));
					}
					nDemonum = nextnum;
					break;
				}
				
			} else {
				gm = MODE_MENU;
				currentGame = null;
				return;
			}

			currentGame = defGame;
			ud.recstat = 2;
			enterlevel(MODE_DEMO);

			engine.nextpage();
			engine.sampletimer();
			return;
		}

		if(ud.pause_on == 0) {
			while (totalclock >= (lockclock + TICSPERFRAME)) {
				for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
					inputfifo[movefifoend[j] & (MOVEFIFOSIZ - 1)][j].copy(demfile.recsync[demfile.rcnt][j]);
					movefifoend[j]++;
					ud.reccnt--;
				}
				demfile.rcnt++;
				domovethings();
	
				if (ud.reccnt <= 0) {
					//backtomenu();
					demfile = null;
					return;
				}
			}
		} else lockclock = totalclock;

		int j = min(max(engine.getsmoothratio(), 0), 65536);
		displayrooms(screenpeek, j);
		displayrest(j);

		if (ud.multimode > 1 && gm != 0)
			getpackets();

		operatefta();

		if (ud.last_camsprite != ud.camerasprite) {
			ud.last_camsprite = ud.camerasprite;
			ud.camera_time = totalclock + (TICRATE * 2);
		}
	}

	public static void demoscan() {
		byte[] buf = new byte[4];

		int fil = -1;
		for (Iterator<FileEntry> it = cache.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("dmo")) {
				String name = file.getFile().getName();
				if ((fil = kOpen(name, 0)) != -1) {
					kRead(fil, buf, 4);
					kRead(fil, buf, 1);
					int version = buf[0] & 0xFF;

					if (version == BYTEVERSIONRR
							|| version == GDXBYTEVERSION)
						demofiles.add(name);
					kClose(fil);
				}
			}
		}
		if (demofiles.size() != 0)
			Collections.sort(demofiles);
		Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);
	}

	public static void opendemowrite(int nVersion) {
		if (ud.recstat == 2)
			kClose(recfilep);

		int a, b, c, d, democount = 0;
		String fn;
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

		if ((recfilep = Bopen(fn, "rw")) == -1)
			return;

		Console.Println("Start recording to " + fn);

		demofiles.add(fn);

		Bwrite(recfilep, 0, 4);
		Bwrite(recfilep, nVersion, 1);
		Bwrite(recfilep, ud.volume_number, 1);
		Bwrite(recfilep, ud.level_number, 1);
		Bwrite(recfilep, ud.player_skill, 1);
		Bwrite(recfilep, ud.m_coop, 1);
		Bwrite(recfilep, ud.m_ffire, 1);
		Bwrite(recfilep, ud.multimode, 2);
		Bwrite(recfilep, ud.m_monsters_off ? 1 : 0, 2);
		Bwrite(recfilep, ud.m_respawn_monsters ? 1 : 0, 4);
		Bwrite(recfilep, ud.m_respawn_items ? 1 : 0, 4);
		Bwrite(recfilep, ud.m_respawn_inventory ? 1 : 0, 4);
		Bwrite(recfilep, ud.playerai, 4);

		for (int i = 0; i < MAXPLAYERS; i++) {
			buildString(buf, 0, ud.user_name[i]);
			Bwrite(recfilep, tempbuf, 32);
		}

		int MAX_PATH = 260;
		if (boardfilename != null)
			Bwrite(recfilep, boardfilename.toCharArray(), MAX_PATH);
		else Bwrite(recfilep, new byte[MAX_PATH], MAX_PATH);

		for (int i = 0; i < ud.multimode; i++) {
			Bwrite(recfilep, ps[i].aim_mode, 1);
			if (nVersion >= GDXBYTEVERSION) // JBF 20031126
				Bwrite(recfilep, ps[i].auto_aim, 1);
		}

		totalreccnt = 0;
		ud.reccnt = 0;

		recversion = nVersion;
	}

	public static void record() {
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			int len = Input.sizeof(recversion);
			System.arraycopy(sync[i].getBytes(recversion), 0, recbuf, ud.reccnt * len, len);
			ud.reccnt++;
			totalreccnt++;

			if (ud.reccnt >= RECSYNCBUFSIZ) {
				if (!unpacked) {
					int dasizeof = len * ud.multimode;
					try {
					dfWrite(recbuf, dasizeof, ud.reccnt / ud.multimode, recfilep);
					} catch (Exception e) {
						Console.Println(e.getMessage(), OSDTEXT_RED);
						closedemowrite();
					}
				} else
					Bwrite(recfilep, recbuf, ud.reccnt * len);
				ud.reccnt = 0;
			}
		}
	}

	public static void closedemowrite() {
		if (ud.recstat == 1) {
			try {
				if (ud.reccnt > 0) {
					int len = Input.sizeof(recversion);
					if (!unpacked) {
						int dasizeof = len * ud.multimode;
						dfWrite(recbuf, dasizeof, ud.reccnt / ud.multimode, recfilep);
					} else
						Bwrite(recfilep, recbuf, ud.reccnt * len);

					Blseek(recfilep, SEEK_SET, 0);
					Bwrite(recfilep, totalreccnt, 4);
					ud.recstat = ud.m_recstat = 0;
					recversion = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Console.Println("Stop recording");
			Bclose(recfilep);
		}
	}

	public static void DemoReset() {
		demfile = null;
	}
}
