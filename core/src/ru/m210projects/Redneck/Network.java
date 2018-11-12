//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Redneck.Menus.RRMenu.*;
import static ru.m210projects.Redneck.Menus.MENU.mClose;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Types.Demo.*;
import static ru.m210projects.Redneck.Types.RTS.*;
import static ru.m210projects.Build.FileHandle.Compat.cache;
import static ru.m210projects.Build.Audio.BAudio.SOUNDDRV;
import static ru.m210projects.Build.Net.Mmulti.canSend;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.getpacket;
import static ru.m210projects.Build.Net.Mmulti.inet;
import static ru.m210projects.Build.Net.Mmulti.initmultiplayers;
import static ru.m210projects.Build.Net.Mmulti.kPacketTick;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Net.Mmulti.otherpacket;
import static ru.m210projects.Build.Net.Mmulti.sendpacket;
import static ru.m210projects.Build.Net.Mmulti.uninitmultiplayer;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Premap.enterlevel;
import static ru.m210projects.Redneck.Premap.newgame;
import static ru.m210projects.Redneck.Premap.resetinventory;
import static ru.m210projects.Redneck.Premap.resetweapons;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Build.Strhandler.buildString;
import java.io.File;
import java.util.Arrays;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Redneck.Types.VOC;

public class Network {
	
	public static byte[] packbuf = new byte[576];
	public static boolean mFakeMultiplayer;
	public static String[] gNetParam;
	public static boolean gNetDisconnect;
	public static FileEntry nNetLastSuccess;
	
	public static final int gNetCreate = 1;
	public static final int gNetConnect = 2;
	public static int gNetFlags = 0;
	
	public static final byte 	kPacketMasterFrame	= 0;
	public static final byte	kPacketSlaveFrame	= 1;
	public static final byte	kPacketMessage		= 4;
	public static final byte 	kPacketLevelStart 	= 5;
	public static final byte	kPacketProfile		= 6;
	public static final byte	kPacketSound		= 7;
	
	public static final byte 	kPacketContentCheck = 8;
	public static final byte	kPacketDisconnect	= 9;

	public static final byte 	kPacketEmpty 			= (byte) 127;
	public static final byte 	kPacketSlaveProfile		= (byte) 250;
	public static final byte 	kPacketLogout			= (byte) 255;
	
	
	public static int nConnected = 0;
	
	public static boolean waitforeverybody(int timeout)
	{
		if (numplayers < 2) return true;

		for(int i=connecthead;i>=0;i=connectpoint2[i])
		{
			if (i != myconnectindex) 
				while(!canSend(i));
		}
		
		packbuf[0] = kPacketSlaveProfile;
		sendtoall(packbuf,1);
		playerreadyflag[myconnectindex]++;

		long starttime = System.currentTimeMillis();
		while (true)
		{
			engine.handleevents();
			long time = System.currentTimeMillis() - starttime;
			
			if (/*ctrlKeyStatusOnce(Keys.ESCAPE) || */(timeout != 0 && time > timeout)) 
			{
				Console.Println("Connection timed out!", OSDTEXT_YELLOW);
				return false;
			}
			
			switch(getpackets())
			{
				case 0: return false; //disconnect
				case 1: break; //waiting
				case 2:
					starttime = System.currentTimeMillis();
					break; //tick
			}

			int i;
			for(i=connecthead;i>=0;i=connectpoint2[i])
			{
				if (playerreadyflag[i] < playerreadyflag[myconnectindex]) break;
				if (myconnectindex != connecthead) { i = -1; break; } //slaves in M/S mode only wait for master
			}
			if (i < 0) return true;
		}
	}
	
	public static void getnames()
	{
		 int i,l;
		 ud.user_name[myconnectindex] = cfg.pName;
		 ps[myconnectindex].name = cfg.pName;

		 byte[] buf = new byte[256];
	     if(numplayers > 1)
	     {
	          buf[0] = kPacketProfile;
			  buf[1] = (byte) myconnectindex;
	          buf[2] = (byte) BYTEVERSION;
			  l = 3;

			  //null terminated player name to send

			  char[] name = toCharArray(cfg.pName);
			  for(i=0;i < cfg.pName.length() && name[i] != 0;i++) buf[l++] = (byte)name[i];
			  buf[l++] = 0;

	          for(i=0;i<10;i++)
	          {
	                ud.wchoice[myconnectindex][i] = ud.wchoice[0][i];
					buf[l++] = (byte)ud.wchoice[0][i];
	          }

			  buf[l++] = (byte) ps[myconnectindex].aim_mode;
			  buf[l++] = (byte) ps[myconnectindex].auto_aim;
			  
			  sendtoall(buf,l);

			  getpackets();
	     }
	}

	public static int getpackets() 
	{
		int i, j, k, l;
	    int other, packbufleng;
	    Input[] osyn;
        Input[] nsyn;
	    
		if (numplayers < 2) return 2;

		while ((packbufleng = getpacket(packbuf)) > 0)
	    {
			other = otherpacket;
			switch(packbuf[0])
			{
				 case kPacketMasterFrame:  //[0] (receive master sync buffer)
		                j = 1;

		                if ((movefifoend[other]&(TIMERUPDATESIZ-1)) == 0)
		                    for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
		                    {
		                        if (playerquitflag[i] == 0) continue;
		                        if (i == myconnectindex)
		                            otherminlag = packbuf[j];
		                        j++;
		                    }
		                
		                osyn = inputfifo[(movefifoend[connecthead]-1)&(MOVEFIFOSIZ-1)];
		                nsyn = inputfifo[(movefifoend[connecthead])&(MOVEFIFOSIZ-1)];

		                k = j;
		                for(i=connecthead;i>=0;i=connectpoint2[i])
		                    j += playerquitflag[i];
		                for(i=connecthead;i>=0;i=connectpoint2[i])
		                {
		                    if (playerquitflag[i] == 0) continue;

		                    l = packbuf[k++]&0xFF;
		                    if (i == myconnectindex)
		                        { j += ((l&1)<<1)+(l&2)+(l&4)+((l&8)>>3)+((l&16)>>4)+((l&32)>>5)+((l&64)>>6)+((l&128)>>5); continue; }
		                    
		                    nsyn[i].copy(osyn[i]);
		                    
		                    if ((l&1) != 0)  { nsyn[i].fvel = (short) ((packbuf[j]&0xFF)+((short)(packbuf[j+1]&0xFF)<<8)); j += 2; }
		                    if ((l&2) != 0)  { nsyn[i].svel = (short) ((packbuf[j]&0xFF)+((short)(packbuf[j+1]&0xFF)<<8)); j += 2; }
		                    if ((l&4) != 0)  { nsyn[i].avel = LittleEndian.getFloat(packbuf, j); j += 4; /*packbuf[j++];*/ }
		                    if ((l&8) != 0)  { nsyn[i].bits = ((nsyn[i].bits&0xffffff00)|(packbuf[j++]&0xFF));}
		                    if ((l&16) != 0) { nsyn[i].bits = ((nsyn[i].bits&0xffff00ff)|(packbuf[j++]&0xFF)<<8);}
		                    if ((l&32) != 0) { nsyn[i].bits = ((nsyn[i].bits&0xff00ffff)|(packbuf[j++]&0xFF)<<16);}
		                    if ((l&64) != 0) { nsyn[i].bits = ((nsyn[i].bits&0x00ffffff)|(packbuf[j++]&0xFF)<<24);}
		                    if ((l&128) != 0){ nsyn[i].horz = LittleEndian.getFloat(packbuf, j); j += 4; /*packbuf[j++];*/ }

		                    if ((nsyn[i].bits&(1<<26)) != 0) playerquitflag[i] = 0;
		                    movefifoend[i]++;
		                }

//		                while (j != packbufleng)
//		                {
//		                    for(i=connecthead;i>=0;i=connectpoint2[i])
//		                        if(i != myconnectindex)
//		                    {	
//		                        syncval[i][syncvalhead[i]&(MOVEFIFOSIZ-1)] = packbuf[j];
//		                        syncvalhead[i]++;
//		                    }
//		                    j++;
//		                }
		                
		                while (j != packbufleng)
		                {
		                    for(i=connecthead;i>=0;i=connectpoint2[i])
		                        if(i != myconnectindex)
		                    {	
		                        GetPacket(packbuf, j, syncval[other], CheckBytes * (syncvalhead[other] &(MOVEFIFOSIZ-1)), CheckBytes);
			                	syncvalhead[other]++;
		                    }
		                    j += CheckBytes;
		                }

		                for(i=connecthead;i>=0;i=connectpoint2[i])
		                    if (i != myconnectindex)
		                        for(j=1;j<movesperpacket;j++)
		                        {
		                        	inputfifo[movefifoend[i]&(MOVEFIFOSIZ-1)][i].copy(nsyn[i]);
		                            movefifoend[i]++;
		                        }

		                movefifosendplc += movesperpacket;

		                break;
		            case kPacketSlaveFrame:  //[1] (receive slave sync buffer)
		                j = 2; k = packbuf[1];
		                
		                osyn = inputfifo[(movefifoend[other]-1)&(MOVEFIFOSIZ-1)];
		                nsyn = inputfifo[(movefifoend[other])&(MOVEFIFOSIZ-1)];

		                nsyn[other].copy(osyn[other]);
		               
		                if ((k&1) != 0) {  nsyn[other].fvel = (short) ((packbuf[j]&0xFF)+((short)(packbuf[j+1]&0xFF)<<8)); j += 2; }
		                if ((k&2) != 0) {  nsyn[other].svel = (short) ((packbuf[j]&0xFF)+((short)(packbuf[j+1]&0xFF)<<8)); j += 2; }
		                if ((k&4) != 0) {  nsyn[other].avel = LittleEndian.getFloat(packbuf, j); j += 4; /*packbuf[j++];*/ }
		                if ((k&8) != 0) {  nsyn[other].bits = ((nsyn[other].bits&0xffffff00)|(packbuf[j++]&0xFF)); }
		                if ((k&16) != 0) { nsyn[other].bits = ((nsyn[other].bits&0xffff00ff)|(packbuf[j++]&0xFF)<<8); }
		                if ((k&32) != 0) { nsyn[other].bits = ((nsyn[other].bits&0xff00ffff)|(packbuf[j++]&0xFF)<<16); }
		                if ((k&64) != 0) { nsyn[other].bits = ((nsyn[other].bits&0x00ffffff)|(packbuf[j++]&0xFF)<<24); }
		                if ((k&128) != 0) {nsyn[other].horz = LittleEndian.getFloat(packbuf, j); j += 4; /*packbuf[j++];*/ }
		                movefifoend[other]++;
		                while ( j != packbufleng )
						{
							j = GetPacket(packbuf, j, syncval[other], CheckBytes * (syncvalhead[other] &(MOVEFIFOSIZ-1)), CheckBytes);
							syncvalhead[other]++;
						}

		                for(i=1;i<movesperpacket;i++)
		                {
		                	inputfifo[movefifoend[other]&(MOVEFIFOSIZ-1)][other].copy(nsyn[other]);
		                 	movefifoend[other]++;
		                }

		                break;
		                
		            case kPacketContentCheck:
						retransmit(other, packbuf, packbufleng);
						int ptr = 1;
						boolean notFound = packbuf[ptr++] == 1;
						
						if(notFound) {
							Console.Println("Player" + other + " - " + ud.user_name[other] + ": user content not found!", OSDTEXT_RED);
							sound(EXITMENUSOUND);
							Console.show();
						}

						int pathlen = LittleEndian.getInt(packbuf, ptr); ptr += 4;
						if(pathlen >= packbuf.length) {
							dassert("Wtf??? " + new String(packbuf) + " " + pathlen);
							return -1;
						}
						
						String path = new String(packbuf, ptr, pathlen);
						if(path.equalsIgnoreCase("<main>"))
						{
							mResetContent();
							return 1;
						}
						
						if(path.contains("/"))
							path = path.replace("/", File.separator);
						if(path.contains("\\"))
							path = path.replace("\\", File.separator);

						FileEntry fil = cache.checkFile(path);

						if(fil != null) {
							nNetLastSuccess = fil;
//							mContentUpdate(fil); XXX
							return 1;
						} else {
							if(nNetLastSuccess != null)
								SendContent(nNetLastSuccess.getPath(), true);
							else SendContent("<main>", true);
							
							Console.Println("Player" + other + " - " + ud.user_name[other] + " tried to set user content. User content not found: " + path, OSDTEXT_RED);
							Console.show();
						}
						return -1;
						
		            case kPacketLevelStart:
		            	
		            	retransmit(other, packbuf,packbufleng);
		            	
		            	ud.m_level_number = ud.level_number = packbuf[1];
		                ud.m_volume_number = ud.volume_number = packbuf[2];
		                ud.m_player_skill = ud.player_skill = packbuf[3];
		                ud.m_monsters_off = ud.monsters_off = packbuf[4] == 1;
		                ud.m_respawn_monsters = ud.respawn_monsters = packbuf[5] == 1;
		                ud.m_respawn_items = ud.respawn_items = packbuf[6] == 1;
		                ud.m_respawn_inventory = ud.respawn_inventory = packbuf[7] == 1;
		                ud.m_coop = packbuf[8];
		                ud.m_marker = ud.marker = packbuf[9];
		                ud.m_ffire = ud.ffire = packbuf[10];

		                for(i=connecthead;i>=0;i=connectpoint2[i])
		                {
		                    resetweapons(i);
		                    resetinventory(i);
		                }

						newgame(ud.m_volume_number,ud.m_level_number,ud.m_player_skill);
						ud.coop = ud.m_coop;
		                enterlevel(MODE_GAME);
		                mClose();
		                
		            	break;
		            	
		            case kPacketMessage: 
		            
						retransmit(other, packbuf,packbufleng);
						for(i = 0; i < packbufleng-2; i++)
							recbuf[i] = (char) packbuf[i + 2];
						recbuf[packbufleng-2] = 0;

						adduserquote(recbuf);
						sound(EXITMENUSOUND);
						break;
						
		            case kPacketProfile:
		            	retransmit(other, packbuf,packbufleng);
	
//						if (packbuf[2] != BYTEVERSION)
//							gameexit("\nYou cannot play Duke with different versions.");
	
						other = packbuf[1];
	
						int len = 0;
						for (i=3;packbuf[i] != 0;i++, len++);
						
						ud.user_name[other] = new String(packbuf, 3, len);
						ps[other].name = ud.user_name[other];
						i++;
	
						j = i; //This used to be Duke packet #9... now concatenated with Duke packet #6
						for (;i-j<10;i++) 
							ud.wchoice[other][i-j] = packbuf[i];
						
						ps[other].aim_mode = packbuf[i++];
						ps[other].auto_aim = packbuf[i++];
 
		                break;
		                
		            case kPacketSound:
		            	retransmit(other, packbuf,packbufleng);

		            	if (!cfg.SoundToggle || ud.lockout == 1 || !engine.getAudio().IsInited(SOUNDDRV) )
		            		break;
		            	
		            	byte[] rtsptr = RTS_GetSound(packbuf[1]);
		            	if (rtsptr[0] == 'C') {
		            		VOC voc = new VOC(rtsptr);
				    		Source voice = engine.getAudio().newSound(voc.sampledata, voc.samplerate, voc.samplesize, 255);
				    		if(voice != null)
				    		{
				    			voice.setGlobal(1);
				    			voice.play(1.0f); 
				    		}
		            	}
		            	else {
							try {
								WAVLoader wav = new WAVLoader(rtsptr);
								Source voice = engine.getAudio().newSound(wav.sampledata, wav.samplerate, wav.samplebits, 255);
								if(voice != null)
								{
									voice.setGlobal(1);
									voice.play(1.0f); 
								}
							} catch (Exception e) {
								break;
							}
		            	}
		            	
		            	rtsplaying = 7;
		            	break;
		                
		            case kPacketDisconnect:
						retransmit(other, packbuf, packbufleng);

						other = LittleEndian.getInt(packbuf, 1);
						
						waitforeverybody(1000);
						
						closedemowrite();

						if (other == myconnectindex) 
							dassert("nPlayer != myconnectindex");
						if (screenpeek == other)
						{
							screenpeek = connectpoint2[other];
							if (screenpeek < 0) screenpeek = connecthead;
						}

						if (other == connecthead) {
							connecthead = connectpoint2[connecthead];
							sound(GENERIC_AMBIENCE17);
							NetDisconnect(myconnectindex);
							return 1;
						}
						else 
							for ( j = connecthead; j >= 0; j = connectpoint2[j] )
							{
								if ( connectpoint2[j] == other )
								{
									connectpoint2[j] = connectpoint2[other];
									break;
								}
							}

						if(numplayers > 1) {
							numplayers--;
							ud.multimode = numplayers;
						}

						if(gm == MODE_GAME) {
							quickkill(ps[other]);
							engine.deletesprite(ps[other].i);
						}

						buildString(buf, 0, ud.user_name[other], " is history!");
						
						vscrn(ud.screen_size);

						adduserquote(buf);
						
						if(!waitforeverybody(0))
							return -1;
				          
						break;
		                
		            case kPacketEmpty:
		                break;
		                
		            case kPacketTick:
						nConnected = packbuf[1];
						inet.message = "Waiting for other players [" + nConnected + " / " + numplayers + "]";
						return 2;

		            case kPacketSlaveProfile:
		                playerreadyflag[other]++;
		                break;
		            case  kPacketLogout:
		            	gm |= MODE_END;
		                break;
			}
	    }
		
		return 1;
	}

	public static long Checksum( byte[] p, int length )
	{
		int ptr = 0;
		length >>= 2;
		long sum = 0;
		while ( (--length) != -1 ) {
			sum += LittleEndian.getInt(p, ptr);
			ptr += 4;
		}

		return sum;
	}
	
	public static int PutPacket(byte[] p, int ptr, Object v, int vptr, int size)
	{
		if(ptr + size > p.length)
			dassert("ptr + size < packbuf.length");
		if(v instanceof byte[])
		{
			byte[] array = (byte[]) v;
			System.arraycopy(array, vptr, p, ptr, size); //memcpy(p, v, size);
			return ptr += size;
		} 
		else if(v instanceof int[])
		{
			int[] array = (int[]) v;
			for(int i = 0; i < size / 4; i++) {
				LittleEndian.putInt(p, ptr, array[vptr + i]);
				ptr += 4;
			}
		} 

		return -1;
	}
	
	public static int GetPacket(byte[] p, int pptr, byte[] v, int vptr, int size)
	{
		if(pptr + size >= p.length)
			dassert("ptr + size < packbuf.length");
		System.arraycopy(p, pptr, v, vptr, size); //memcpy(v, p, size);
		return pptr += size;
	}
	
	public static void BCheckSync()
	{
		int nPlayer;

		if ( numplayers == 1 )
			return;

		if((syncstat) != 0) {
			buildString(recbuf, 0, "Out Of Sync - Please restart game");
			engine.printext256(4,114,31,0,recbuf,0);
			
			if((syncstat&1) == 1)
			{
				buildString(recbuf, 0, "Random seeds");
				engine.printext256(32,122,31,0,recbuf,0);
			}
			
			if ((syncstat&2) == 2)
			{
				buildString(recbuf, 0, "Player struct");
				engine.printext256(32,130,31,0,recbuf,0);
			}
			
			if ((syncstat&4) == 4) {
				buildString(recbuf, 0, "Sprite struct");
				engine.printext256(32,138,31,0,recbuf,0);
			}
		}
		
		while ( true )
		{
			for ( nPlayer = connecthead; nPlayer >= 0; nPlayer = connectpoint2[nPlayer] )
			{
				if ( syncvalhead[nPlayer] <= syncvaltottail )
					return;
			}

			int msch = LittleEndian.getInt(syncval[connecthead], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 0);
			int msp = LittleEndian.getInt(syncval[connecthead], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 4);
			int mss = LittleEndian.getInt(syncval[connecthead], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 8);

			syncstat = 0;
			for ( nPlayer = connectpoint2[connecthead]; nPlayer >= 0; nPlayer = connectpoint2[nPlayer] )
			{
				int slch = LittleEndian.getInt(syncval[nPlayer], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 0);
				int slp = LittleEndian.getInt(syncval[nPlayer], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 4);
				int sls = LittleEndian.getInt(syncval[nPlayer], (CheckBytes * (syncvaltottail & (MOVEFIFOSIZ-1))) + 8);

				if(slch != msch)
				{
//					Console.Println("Out of sync randomseeds " + slch + " != " + msch, OSDTEXT_RED);
					syncstat |= 1;
				}
				
				if(slp != msp)
				{
					
//					Console.Println("Out of sync player " + syncvaltottail + " " + slp + " != " + msp, OSDTEXT_RED);
//					Console.Println("Out of sync player[" + nPlayer + "] struct checksum error: \r\n", OSDTEXT_RED);
					syncstat |= 2;
				}
				
				if(sls != mss)
				{
//					Console.Println("Out of sync sprite " + syncvaltottail + " " + sls + " != " + mss, OSDTEXT_RED);
//					Console.Println("Out of sync player[" + nPlayer + "] sprite checksum error: \r\n", OSDTEXT_RED);
					syncstat |= 4;
				}
			}
			syncvaltottail++;
		}
	}
	
	/*
	public static void checksync()
	{
		int i;
		for(i=connecthead;i>=0;i=connectpoint2[i])
			if (syncvalhead[i] == syncvaltottail) break;
		if (i < 0)
		{
			syncstat = 0;
			do
			{
				for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
					if (syncval[i][syncvaltottail&(MOVEFIFOSIZ-1)] !=
						syncval[connecthead][syncvaltottail&(MOVEFIFOSIZ-1)])
						syncstat = 1;
				syncvaltottail++;
				for(i=connecthead;i>=0;i=connectpoint2[i])
					if (syncvalhead[i] == syncvaltottail) break;
			} while (i < 0);
		}
		if (connectpoint2[connecthead] < 0) syncstat = 0;
		
		if (syncstat != 0)
		{
			buildString(recbuf, 0, "Out Of Sync - Please restart game");
			engine.printext256(4,130,31,0,recbuf,0);
			buildString(recbuf, 0, "RUN DN3DHELP.EXE for information.");
			engine.printext256(4,138,31,0,recbuf,0);
		}
		if (syncstate != 0)
		{
			buildString(recbuf, 0, "Missed Network packet!");
			engine.printext256(4,160,31,0,recbuf,0);
			buildString(recbuf, 0, "RUN DN3DHELP.EXE for information.");
			engine.printext256(4,138,31,0,recbuf,0);
		}
	}
	*/
	
	public static void ResetNetwork()
	{
		uninitmultiplayer();
		Arrays.fill(playerreadyflag, (byte)0);
		Arrays.fill(packbuf, (byte)0);
		screenpeek = myconnectindex;
		ready2send = false;
	}
	
	public static int ConnectStep = 0;
	public static void StartMultiplayer(final int flags)
	{
		ResetNetwork();
		gNetFlags = flags;
		initmultiplayers(gNetParam, 0);
		gm = MODE_WAIT;
		ConnectStep = 0;
	}
	
	public static void netStartWaiting(final int timeout)
	{
		inet.waitThread = new Thread(new Runnable() 
		{
			public void run()
			{
				if(!waitforeverybody(timeout))
					NetDisconnect(myconnectindex);
			}
		});
		inet.waitThread.start();
	}
	
	public static boolean SendContent(String filepath, boolean notFound)
	{
		if ( numplayers > 1 )
		{
			packbuf[0] = kPacketContentCheck;
			packbuf[1] = notFound ? (byte) 1 : 0;
			LittleEndian.putInt(packbuf, 2, filepath.length()); 
			System.arraycopy(filepath.getBytes(), 0, packbuf, 6, filepath.length());
			sendtoall(packbuf, filepath.length() + 6);
		}
		
		return true;
	}
	
	public static void retransmit(int nPlayer, byte[] bufptr, int messleng)
	{
		//Slaves in M/S mode only send to master
		//Master re-transmits message to all others
		if ((networkmode == 0) && (myconnectindex == connecthead))
			for(int i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
				if (i != nPlayer) 
					sendpacket(i,bufptr,messleng);
	}
	
	public static void sendtoall(byte[] bufptr, int messleng)
	{
		for(int i=connecthead;i>=0;i=connectpoint2[i])
		{
			if (i != myconnectindex) sendpacket(i,bufptr,messleng);
			if ((networkmode == 0) && (myconnectindex != connecthead)) break; //slaves in M/S mode only send to master
		}
	}
	
	public static void NetDisconnect(int nPlayer)
	{
		Console.Println("Disconnected!", OSDTEXT_YELLOW);
		if ( numplayers > 1 || ud.multimode > 1)
		{
		    packbuf[0] = kPacketDisconnect;
		    LittleEndian.putInt(packbuf, 1, nPlayer);
		    
		    sendtoall(packbuf, 5);
		    waitforeverybody(1000);
		    
		    ResetNetwork();
		    ud.multimode = 1;
		}
		ready2send = false;
		mFakeMultiplayer = false;
		if((gm & MODE_END) == 0)
			backtomenu();

		gNetDisconnect = false;
	}
	
	private static char[] recbuf = new char[80];
	private static int sendmessagecommand = -1;
	public static void SendMessage(char[] buf, int len)
	{
		if(sendmessagecommand != -1 || ud.multimode < 3 || movesperpacket == 4)
		{
			tempbuf[0] = kPacketMessage;
			tempbuf[2] = 0;
			recbuf[0]  = 0;
	              
			if(ud.multimode < 3)
				sendmessagecommand = 2;

			int pos = buildString(recbuf, 0, ud.user_name[myconnectindex], ": ");
			System.arraycopy(buf, 0, recbuf, pos, len);
			pos += len;
			recbuf[pos] = 0;
			for(int i = 0; i < recbuf.length; i++)
				tempbuf[2 + i] = (byte) recbuf[i];
			
			if(sendmessagecommand >= ud.multimode || movesperpacket == 4)
			{
				tempbuf[1] = (byte)255;
				sendtoall(tempbuf,pos+2);
				adduserquote(recbuf);
				quotebot += 8;
				quotebotgoal = quotebot;
			}
			else if(sendmessagecommand >= 0)
			{
				tempbuf[1] = (byte)sendmessagecommand;
				if (myconnectindex != connecthead)
					sendmessagecommand = connecthead;
				sendpacket(sendmessagecommand,tempbuf,pos+2);
			}

			sendmessagecommand = -1;
        }
	}
	
	public static void netinput()
	{
		Input[] osyn, nsyn;
		
		int i;
	    for(i=connecthead;i>=0;i=connectpoint2[i])
	        if (i != myconnectindex)
	        {
	            int k = (movefifoend[myconnectindex]-1)-movefifoend[i];
	            myminlag[i] = min(myminlag[i],k);
	            mymaxlag = max(mymaxlag,k);
	        }

	    if (((movefifoend[myconnectindex]-1)&(TIMERUPDATESIZ-1)) == 0)
	    {
	        i = mymaxlag-bufferjitter; mymaxlag = 0;
	        if (i > 0) bufferjitter += ((3+i)>>2);
	        else if (i < 0) bufferjitter -= ((1-i)>>2);
	    }
	    
	    if (myconnectindex != connecthead)   //Slave
	    {
	            //Fix timers and buffer/jitter value
	        if (((movefifoend[myconnectindex]-1)&(TIMERUPDATESIZ-1)) == 0)
	        {
	            i = myminlag[connecthead]-otherminlag;
	            if (klabs(i) > 8) i >>= 1;
	            else if (klabs(i) > 2) i = ksgn(i);
	            else i = 0;

	            totalclock -= TICSPERFRAME*i;
	            otherminlag += i;

	            for(i=connecthead;i>=0;i=connectpoint2[i])
	                myminlag[i] = 0x7fffffff;
	        }

	        packbuf[0] = kPacketSlaveFrame; packbuf[1] = 0; int j = 2;

	        osyn = inputfifo[(movefifoend[myconnectindex]-2)&(MOVEFIFOSIZ-1)];
	        nsyn = inputfifo[(movefifoend[myconnectindex]-1)&(MOVEFIFOSIZ-1)];

	        if (nsyn[myconnectindex].fvel != osyn[myconnectindex].fvel)
	        {
	            packbuf[j++] = (byte) nsyn[myconnectindex].fvel;
	            packbuf[j++] = (byte)(nsyn[myconnectindex].fvel>>8);
	            packbuf[1] |= 1;
	        }
	        if (nsyn[myconnectindex].svel != osyn[myconnectindex].svel)
	        {
	            packbuf[j++] = (byte) nsyn[myconnectindex].svel;
	            packbuf[j++] = (byte)(nsyn[myconnectindex].svel>>8);
	            packbuf[1] |= 2;
	        }
	        if (nsyn[myconnectindex].avel != osyn[myconnectindex].avel)
	        {
	        	LittleEndian.putFloat(packbuf, j, nsyn[myconnectindex].avel);
	        	j += 4;
	            packbuf[1] |= 4;
	        }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x000000ff) != 0) { packbuf[j++] = (byte) (nsyn[myconnectindex].bits&255); packbuf[1] |= 8; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x0000ff00) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>8)&255); packbuf[1] |= 16; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x00ff0000) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>16)&255); packbuf[1] |= 32; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0xff000000) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>24)&255); packbuf[1] |= 64; }
	        if (nsyn[myconnectindex].horz != osyn[myconnectindex].horz)
	        {
	            LittleEndian.putFloat(packbuf, j, nsyn[myconnectindex].horz);
	        	j += 4;
	            packbuf[1] |= 128;
	        }

	        while (syncvalhead[myconnectindex] != syncvaltail)
			{
				j = PutPacket(packbuf, j, syncval[myconnectindex], CheckBytes * (syncvaltail & (MOVEFIFOSIZ-1)), CheckBytes);
				syncvaltail++;
			}

	        sendpacket(connecthead,packbuf,j);
	        return;
	    }

			  //This allows packet resends
	    for(i=connecthead;i>=0;i=connectpoint2[i])
	        if (movefifoend[i] <= movefifosendplc)
	        {
	            packbuf[0] = kPacketEmpty;
	            for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	            {
	            	while(!canSend(i)); //GDX 22.10.2018
	            	sendpacket(i,packbuf,1);
				}
	            return;
	        }

	    while (true)  //Master 
	    {
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	            if (playerquitflag[i] != 0 && (movefifoend[i] <= movefifosendplc)) return;

	        osyn = inputfifo[(movefifosendplc-1)&(MOVEFIFOSIZ-1)];
	        nsyn = inputfifo[(movefifosendplc  )&(MOVEFIFOSIZ-1)];
	        
	            //MASTER -> SLAVE packet
	        packbuf[0] = kPacketMasterFrame; int j = 1;

	            //Fix timers and buffer/jitter value
	        if ((movefifosendplc&(TIMERUPDATESIZ-1)) == 0)
	        {
	            for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	                if (playerquitflag[i] != 0)
	                packbuf[j++] = (byte) min(max(myminlag[i],-128),127);

	            for(i=connecthead;i>=0;i=connectpoint2[i])
	                myminlag[i] = 0x7fffffff;
	        }

	        int k = j;
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	           j += playerquitflag[i];
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	        {
	            if (playerquitflag[i] == 0) continue;

	            packbuf[k] = 0;
	            if (nsyn[i].fvel != osyn[i].fvel)
	            {
	                packbuf[j++] = (byte)nsyn[i].fvel;
	                packbuf[j++] = (byte)(nsyn[i].fvel>>8);
	                packbuf[k] |= 1;
	            }
	            if (nsyn[i].svel != osyn[i].svel)
	            {
	                packbuf[j++] = (byte)nsyn[i].svel;
	                packbuf[j++] = (byte)(nsyn[i].svel>>8);
	                packbuf[k] |= 2;
	            }
	            if (nsyn[i].avel != osyn[i].avel)
	            {
	                LittleEndian.putFloat(packbuf, j, nsyn[i].avel);
		        	j += 4;
	                packbuf[k] |= 4;
	            }
	            if (((nsyn[i].bits^osyn[i].bits)&0x000000ff) != 0) { packbuf[j++] = (byte) (nsyn[i].bits&255); packbuf[k] |= 8; }
	            if (((nsyn[i].bits^osyn[i].bits)&0x0000ff00) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>8)&255); packbuf[k] |= 16; }
	            if (((nsyn[i].bits^osyn[i].bits)&0x00ff0000) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>16)&255); packbuf[k] |= 32; }
	            if (((nsyn[i].bits^osyn[i].bits)&0xff000000) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>24)&255); packbuf[k] |= 64; }
	            if (nsyn[i].horz != osyn[i].horz)
	            {
	                LittleEndian.putFloat(packbuf, j, nsyn[i].horz);
		        	j += 4;
	                packbuf[k] |= 128;
	            }
	            k++;
	        }

	        while (syncvalhead[myconnectindex] != syncvaltail)
			{
				j = PutPacket(packbuf, j, syncval[myconnectindex], CheckBytes * (syncvaltail & (MOVEFIFOSIZ-1)), CheckBytes);
				syncvaltail++;
			}

	        for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	            if (playerquitflag[i] != 0)
	            {
	                 sendpacket(i,packbuf,j);
	                 if ((nsyn[i].bits&(1<<26)) != 0)
	                    playerquitflag[i] = 0;
	            }

	        movefifosendplc += movesperpacket;
	    }
	}
}
