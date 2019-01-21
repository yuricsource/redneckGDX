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

package ru.m210projects.Redneck;

import static java.lang.Math.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.FilePath;
import static ru.m210projects.Build.FileHandle.Compat.cache;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.CloseLogFile;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Config.Show_Console;
import static ru.m210projects.Redneck.Animlib.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Animate.doanimations;
import static ru.m210projects.Redneck.Interpolation.updateinterpolations;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.MIRROR;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Sector.animatewalls;
import static ru.m210projects.Redneck.Sector.ceilingspace;
import static ru.m210projects.Redneck.Sector.checksectors;
import static ru.m210projects.Redneck.Sector.floorspace;
import static ru.m210projects.Redneck.Types.Demo.closedemowrite;
import static ru.m210projects.Redneck.Types.Demo.record;
import static ru.m210projects.Redneck.Types.RTS.rtsplaying;
import static ru.m210projects.Redneck.Weapons.addweapon;
import static ru.m210projects.Redneck.Weapons.moveweapons;
import static ru.m210projects.Redneck.Animlib.anmInited;
import static ru.m210projects.Redneck.Animlib.closeanm;
import static ru.m210projects.Redneck.Animlib.initanm;
import static ru.m210projects.Redneck.Animlib.playanm;
import static ru.m210projects.Redneck.Controls.*;
import static ru.m210projects.Redneck.Gameutils.toCharArray;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.gpmanager;
import static ru.m210projects.Redneck.Menus.RRMenu.MAIN;
import static ru.m210projects.Redneck.Menus.RRMenu.NETWORKGAME;
import static ru.m210projects.Redneck.Menus.MENU.mClose;
import static ru.m210projects.Redneck.Menus.MENU.mDrawMenu;
import static ru.m210projects.Redneck.Menus.MENU.mKeyHandler;
import static ru.m210projects.Redneck.Menus.MENU.mMenuHistory;
import static ru.m210projects.Redneck.Menus.MENU.mMenus;
import static ru.m210projects.Redneck.Menus.MENU.mOpen;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Redneck.Premap.enterlevel;
import static ru.m210projects.Redneck.Screen.alignx;
import static ru.m210projects.Redneck.Screen.bonuscnt;
import static ru.m210projects.Redneck.Screen.dobonus;
import static ru.m210projects.Redneck.Screen.gametext;
import static ru.m210projects.Redneck.Screen.mGetAlign;
import static ru.m210projects.Redneck.Screen.menutext;
import static ru.m210projects.Redneck.Screen.scrReset;
import static ru.m210projects.Redneck.Screen.screensize;
import static ru.m210projects.Redneck.Screen.tinc;
import static ru.m210projects.Redneck.Types.Demo.demofiles;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Redneck.Menus.RRMenu.GAME;
import static ru.m210projects.Redneck.Types.RTS.*;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.Network.mFakeMultiplayer;
import static ru.m210projects.Redneck.Types.Demo.*;
import static ru.m210projects.Redneck.Menus.RRMenu.mInit;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Interpolation.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.View.*;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;

import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Premap.*;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Architecture.BuildGDX;
import ru.m210projects.Build.Architecture.BuildMessage;
import ru.m210projects.Build.Architecture.GLFrame;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Audio.BAudio;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Types.Sample;
import ru.m210projects.Redneck.Types.SoundOwner;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.RREngine;
import ru.m210projects.Redneck.Types.RROSDFunc;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Weaponhit;

public class Redneck {

	public static final DefScript baseDef = new DefScript(false);
	public static DefScript currentDef;
	
	public static int playerswhenstarted;
	public static float gLoadingTicks = 0;
	public static String boardfilename;
	public static int soundanm = 0;
	public static int scenestatus = 0;
	
	public static boolean gShowMenu;
	public static GameInfo currentGame;
	public static int gCutsClock = 0;
	
	public static void InitUserDefs()
	{
		ud.setDefaults(cfg);
	    ud.fta_on = 1;
	    ud.god = false;
	    ud.m_respawn_items = false;
	    ud.m_respawn_monsters = false;
	    ud.m_respawn_inventory = false;
	    ud.warp_on = 0;
	    ud.cashman = 0;
	    ud.m_player_skill = ud.player_skill = 2;
	}
	
	public static void InitRR(BuildMessage message)
	{
		try {
			if(initgroupfile("Redneck.grp") == -1)
				throw new Exception("Resource initialization error!");

			BAudio audio = new BAudio(fxdrivers[cfg.snddrv], mxdrivers[cfg.middrv]);
			engine = new RREngine(message, audio, true);
			
			compilecons();
			
			ConsoleInit();
			
			engine.inittimer(TICRATE);
			
			if(engine.loadpics("tiles000.art") == 0)
				dassert("ART files not found " + new File(FilePath + "TILES###.ART").getAbsolutePath());

			InitSpecialTextures();
			
			for(int i=0;i<MAXPLAYERS;i++) 
				playerreadyflag[i] = 0;

			InitUserDefs();
			uninitmultiplayer();

			RTS_Init(ud.rtsname);
			if(numlumps != 0) 
				Console.Println("Using .RTS file:" + ud.rtsname);
			
			SoundStartup();
			MusicStartup();

		    ud.last_level = -1;
		    
		    genspriteremaps();
		    
		    initinterpolations();
		    initanimations();
		    demoscan();
		    
		    mInit();
		    ud.user_name[myconnectindex] = cfg.pName;
		    
		    for(int i = 0; i < MAXPLAYERS; i++) {
		    	ps[i] = new PlayerStruct();  
		    	po[i] = new PlayerOrig();
		    }
		    ps[myconnectindex].palette = palette;
		    ps[myconnectindex].aim_mode = cfg.gMouseAim?1:0;
		    ps[myconnectindex].auto_aim = cfg.gAutoAim?1:0;
		    
		    for(int i = 0; i < MAXPLAYERS; i++)
		    	ps[i].copy(ps[myconnectindex]);
		    
		    for(int i = 0; i < MAXANIMWALLS; i++)
		    	animwall[i] = new Animwalltype();
		    
		    for(int i = 0; i < MAXSPRITES; i++)
		    	hittype[i] = new Weaponhit();

		    for(int i = 0; i < NUM_SOUNDS; i++) {
		    	Sound[i] = new Sample();
		    	for(int j = 0; j < 4; j++) 
		    		SoundOwner[i][j] = new SoundOwner();
		    }
		    
		    loc = new Input();
		    for(int i = 0; i < MAXPLAYERS; i++)
		    	sync[i] = new Input();
		    for(int i = 0; i < MOVEFIFOSIZ; i++)
		    	for(int j = 0; j < MAXPLAYERS; j++)
		    		inputfifo[i][j] = new Input();
		    
		    Console.Println("Initializing def-scripts...");
			if(cfg.autoloadFolder) {
				Console.Println("Initializing autoload folder");
				DirectoryEntry autoload;
				if((autoload = cache.checkDirectory("autoload")) != null)
				{
					for (Iterator<FileEntry> it = autoload.getFiles().values().iterator(); it.hasNext();) {
						FileEntry file = it.next();
						if (file.getExtension().equals("zip")) {
							String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
							int group = initgroupfile(file.getPath());
							boolean defgroup = false;
							for(RESHANDLE res : kList(group)) {
								if(res.filename.lastIndexOf('.') == -1)
									continue;
								
								String resname = res.filename.substring(0, res.filename.lastIndexOf('.'));
								if(resname.equals(filename) && res.fileformat.equals("def")) {
									byte[] buf = res.getBytes();
									baseDef.loadScript(res.filename, buf);
						    		defgroup = true;
						    		break;
								}
							}
							
							if(!defgroup)
							{
								setgroupflags(group, true, false);
								try {
									prepareusergroup(group, false);
								} 
								catch (Exception e)
								{
									GameMessage("Error in \"" + file.getName() + "\" \r\n" + e.getMessage(), false);
									continue;
								}
								InitGroupResources(kList(group));
							}
						}

						if (file.getExtension().equals("def")) 
							baseDef.loadScript(file);
					}
				}
			}
			FileEntry mainDef = null;
			if((mainDef = cache.checkFile("rrgdx.def")) != null)
				baseDef.loadScript(mainDef);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void ConsoleInit()
	{
		Console.Println("Initializing on-screen display system");
		Console.setCaptureKey(cfg.primarykeys[Show_Console], 0);
		Console.setCaptureKey(cfg.secondkeys[Show_Console], 1);
		Console.setCaptureKey(cfg.mousekeys[Show_Console], 2);
		Console.setCaptureKey(cfg.gpadkeys[Show_Console], 3);
		
		Console.setFunction(new RROSDFunc(engine));
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);
		
		Console.RegisterCvar(new OSDCOMMAND("memusage",
				"mem usage / total", new OSDCVARFUNC() {
					@Override
					public void execute() {
						Console.Println("Memory used: " + MemLog.used() + " / " + MemLog.total() + " mb");
					}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("nextlevel",
				"nextlevel", new OSDCVARFUNC() {
					@Override
					public void execute() {
						LeaveMap();
						ud.level_number++;
//						checknextlevel();
			            ud.m_level_number = ud.level_number;
					}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("net_bufferjitter",
				"net_bufferjitter", new OSDCVARFUNC() {
					@Override
					public void execute() {
						Console.Println("bufferjitter: " + bufferjitter);
					}
		}));
		
//		Console.RegisterCvar(new OSDCOMMAND("net_player",
//				"net_player", new OSDCVARFUNC() {
//					@Override
//					public void execute() {
//						if (Console.osd_argc != 2) {
//							Console.Println("net_player: num");
//							return;
//						}
//						try {
//						String num = osd_argv[1];
//						int pnum = Integer.parseInt(num);
//						Console.Println("Player: ");
//						Console.Println(ps[pnum].toString());
//						Console.Println("Sprite: ");
//						Console.Println(sprite[ps[pnum].i].toString());
//						
//						sendtoall(new byte[] { kPacketPlayer, (byte) pnum }, 2);
//						} catch (Exception e) { }
//					}
//		}));
		
		Console.RegisterCvar(new OSDCOMMAND("initgroupfile",
				"initgroupfile", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("initgroupfile: <path to [zip/grp]>");
							return;
						}
						
						if(gm != MODE_MENU) {
							Console.Println("initgroupfile: Back to menu at first");
							return;
						}

						String filename = osd_argv[1];
						FileEntry file = cache.checkFile(filename);
						if (file != null) {
							if(file.getExtension().equals("zip") 
									|| file.getExtension().equals("grp"))
							{
								try {
									int gr = initgroupfile(file.getPath());
									setgroupflags(gr, true, false);
									prepareusergroup(gr, false);
									InitGroupResources(kList(gr));
									scrReset();
									
									Console.Println("initgroupfile: " + filename + " successfuly added to game resources");
								} catch(Exception e) { 
									Console.Println("Error to load " + file.getName(), OSDTEXT_RED); 
								}
							}
						} else Console.Println("initgroupfile: File not found");
					}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("quicksave",
				"quicksave: performs a quick save", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if ( ud.multimode != 1 || numplayers > 1 ) {
							Console.Println("quicksave: Single player only");
							return;
						}
						
						if (gm == MODE_GAME) {
							quicksave();
						} else
							Console.Println("quicksave: not in a game");
					}
				}));
			
			Console.RegisterCvar(new OSDCOMMAND("quickload",
				"quickload: performs a quick load", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (gm == MODE_GAME) {
							quickload();
						} else
							Console.Println("quickload: not in a game");
					}
				}));
			
			Console.RegisterCvar(new OSDCOMMAND("quit",
				null, new OSDCVARFUNC() {
					@Override
					public void execute() {
						gm = MODE_END;
					}
				}));
	}

	public static void GameLoop()
	{
		if((gm&MODE_END) == 0) 
		{
			engine.timerhandler();
			if(kGameCrash)
			{
				backtomenu();
				kGameCrash = false;
			}
			
			if( ctrlGetInputKey(Screenshot, true)  )
		    {
				engine.screencapture("rr0000.png");
				FTA(103,ps[myconnectindex]);
		    }
			
			if(!MODE_TYPE && !gShowMenu 
					&& (gm & MODE_CUTSCENE) == 0
					&& ctrlGetInputKey(Menu_open, true)
					&& (gm == MODE_MENU 
					|| gm == MODE_GAME 
					|| gm == MODE_DEMO))
			{
				engine.getAudio().getSound().stopAllSounds();
				if(gm == MODE_MENU || gm == MODE_DEMO)
					mOpen(mMenus[MAIN], -1);
				else mOpen(mMenus[GAME], -1);
			}
			
			if(gm != MODE_GAME || gShowMenu || Console.IsShown()) {
				engine.handleevents();
				if(gpmanager != null)
					gpmanager.handler();
			}
			
			if(gShowMenu && gm != MODE_MENU && gm != MODE_WAIT && gm != MODE_LOADING) 
				mKeyHandler(mMenuHistory[0]);

			updatesounds();

			if((gm & MODE_CUTSCENE)!= 0)
			{
				boolean skip;
				if(getInput().getKey(ANYKEY) != 0)
					gCutsClock = totalclock;

				if(scenestatus == 0)
					skip = (ctrlGetInputKey(Menu_open, true) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Open]));
				else skip = (ctrlGetInputKey(Menu_open, true) || ctrlPadStatusOnce(cfg.gJoyDevice, cfg.gpadkeys[Open]) || scenestatus == 2);
				int playing = playanm();

				int shade = mulscale(64, sintable[(20 * totalclock) & kAngleMask], 16);
				if (totalclock - gCutsClock < 200) {// 2 sec
					Arrays.fill(buf, (char)0);
					buildString(buf, 0, "Press ESC to skip");
					mGetAlign(0, buf);
					minitext(160 - alignx / 2, 10, buf, 65536, shade, 0, 0);
				}
				
				if((gm & MODE_EOL) == MODE_EOL)
				{ 
					if(ud.multimode < 2)
                    {
						if(scenestatus == 1)
						{
							engine.clearview(0);
							engine.rotatesprite(0,0,65536,0,LOADSCREEN,0,0,2+8+16+64+128,0,0,xdim-1,ydim-1);
							if(getInput().getKey(ANYKEY) != 0) 
							{
								if(ud.volume_number == 0 && ud.level_number == 7) {
						            closeanm();
						            gm = MODE_EOL;
								} else skip = true;
					    	}
						}
						
						if(scenestatus == 0) 
						{
	                    	if(anmInited() && playing == 0)
	                    		skip = true;
	    					
	                    	if(skip && bonuscnt == 0)
    						{
                    			closeanm();
                    			StopAllSounds();
                    			scenestatus = 1;
                    			bonuscnt = 1;
                    			return;
    						}
						}
                    } else 
                    	skip = true;
					
					if(skip) {
                		scenestatus = 2;
                		closeanm(); //and set palette to default
                		if (dobonus(false)) {
                    		StopAllSounds();
    						getInput().setKey(ANYKEY, 0);
                    		backtomenu();
    					}
                	}
					return;
				}
			}

			switch(gm)
			{
				case MODE_WAIT:
					engine.clearview(0);
					engine.rotatesprite(320<<15,200<<15,65536,0,LOADSCREEN,0,0,2+8+64,0,0,xdim-1,ydim-1);
	
					switch(gNetFlags)
					{
						case 0:
							Arrays.fill(buf, (char)0);
							buildString(buf, 0, "Please wait ");
							mGetAlign(2, buf);
							menutext(160 - alignx / 2, 90+16+8, -128, 0, buf, 0);
							break;
						case gNetCreate:
						case gNetConnect:
							if(inet.waiting()) {
								if(myconnectindex == connecthead) {
									gametext(160, 150, toCharArray("Local IP: ", inet.myip), 65536, -128, 0, 24);
									if(inet.useUPnP) {
										String extip = "Public IP: ";
										if(inet.extip != null)
											extip += inet.extip;
										
										gametext(160, 160, toCharArray(extip), 65536, -128, 0, 24);
									}
								}
								
								if(inet.message != null)
									gametext(160, 180, toCharArray(inet.message), 65536, -128, 0, 24);
								else gametext(160, 180, toCharArray("Initializing..."), 65536, -128, 0, 24);
								return;
							}
							
							if(inet.netready == 0) {
								Console.Println(inet.message, OSDTEXT_YELLOW);
								gm = MODE_MENU;
								return;
							}
							
							if(ConnectStep == 0) {
								if(inet.message != null)
									gametext(160, 180, toCharArray(inet.message), 65536, -128, 0, 24);
								else gametext(160, 180, toCharArray("Connected! Waiting for other players..."), 65536, -128, 0, 24);
								ConnectStep = 1;
								netStartWaiting(5000);
								
								return;
							}
	
							screenpeek = myconnectindex;
							ud.multimode = numplayers;
					        
					        getnames();
	
							gm = MODE_MENU;
	
							mClose();
							mOpen(mMenus[NETWORKGAME], -1);
							break;
					}
					
					return;
				case MODE_LOGO:
					if (!anmInited() || playanm() == 0 || getInput().getKey(ANYKEY) != 0) // end video
					{
						gm = MODE_LOGO2;
						gLoadingTicks = 0;
						StopAllSounds();
						getInput().resetKeyStatus();
						gpmanager.resetButtonStatus();
						closeanm();
						initanm("redneck.anm",5, 0);
					}
					
					
					return;
				case MODE_LOGO2:
					if (!anmInited() || playanm() == 0 || getInput().getKey(ANYKEY) != 0) // end video
					{
						gm = MODE_LOGO3;
						gLoadingTicks = 0;
						StopAllSounds();
						getInput().resetKeyStatus();
						gpmanager.resetButtonStatus();
						closeanm();
						initanm("xatlogo.anm",5, 1);
					}
	
					return;
					
				case MODE_LOGO3:
					if (!anmInited() || playanm() == 0 || getInput().getKey(ANYKEY) != 0) // end video
					{
						closeanm();
						if (demofiles.size() != 0 && cfg.gDemoSeq != 0 && numplayers < 2)
			        		gm = MODE_DEMO;
			        	else gm = MODE_MENU;

			        	scrReset();
			        	StopAllSounds();
			        	mOpen(mMenus[MAIN], -1);
						getInput().resetKeyStatus();
						gpmanager.resetButtonStatus();
					}
					return;
					
				case MODE_MENU:
					mKeyHandler(mMenuHistory[0]);
					if(gm != MODE_MENU)
						break; //new game started
					if(tilesizx[BACKGROUND] == 0 || tilesizy[BACKGROUND] == 0) 
						break;
					int framesx = xdim / tilesizx[BACKGROUND];
					int framesy = ydim / tilesizy[BACKGROUND];
			
					int x, y = 0;
					for(int j = 0; j <= framesy; j++) {
					    x = 0;
						for(int i = 0; i <= framesx; i++) {
					    	engine.rotatesprite(x<<16, y<<16, 0x10000, 0, BACKGROUND, 0, 0, 8 | 16 | 256, 0, 0, xdim-1, ydim-1);
					    	x += tilesizx[BACKGROUND];
					    }
					    y += tilesizy[BACKGROUND];
					}
				    
					break;
				case MODE_GAME: //XXX
					int i;
					nonsharedkeys();
					if (numplayers > 1) {
						getpackets();
						while (fakemovefifoplc < movefifoend[myconnectindex] && ud.pause_on == 0) 
							fakedomovethings();
					} else bufferjitter = 0;

					while (movefifoend[myconnectindex]-movefifoplc > bufferjitter)
					{
						for(i=connecthead;i>=0;i=connectpoint2[i])
							if (movefifoplc == movefifoend[i]) break;
						if (i >= 0) break;
						domovethings();
					}
					
					int ovscr = screensize;
					if (gScreenCapture) 
						vscrn(0);

					int smoothratio = 65536;
					if( ud.pause_on == 0 && ((ud.multimode < 2 && !gShowMenu && !Console.IsShown() ) || ud.multimode > 1 || ud.recstat == 2)) {
						
						if(ud.multimode < 2) 
							smoothratio = engine.getsmoothratio();
						else smoothratio = ((totalclock - ototalclock + TICSPERFRAME) << 16) / TICSPERFRAME;
			            if (smoothratio < 0 || smoothratio > 0x10000) {
			            	smoothratio = BClipRange(smoothratio, 0, 0x10000);
			    		}
					}

					dointerpolations(smoothratio);
					
					displayrooms(screenpeek,smoothratio);
					displayrest(smoothratio);
					
					restoreinterpolations();

					CheckSync();
					
					if (gScreenCapture) {
						saveBuffer = engine.screencapture(160, 100);
						vscrn(ovscr);
						
						if(gAutosaveRequest)
						{
							savegame("[autosave]", "autosave.sav");
							gAutosaveRequest = false;
						}

						if (gQuickSaving) {
							savegame("[quicksave_" + + quickslot + "]", "quicksav" + quickslot + ".sav");
							quickslot ^= 1;
							gQuickSaving = false;
						}

						gScreenCapture = false;
					}
					numframes++;
					
					break;
				case MODE_DEMO:
					playback();
					break;
				case MODE_EOL:	
					if(ud.eog != 0)
	                {
	                    ud.eog = 0;
	                    totalclock = 0;
	                    tinc = 0;
	                    bonuscnt = 0;
	                    
	                    sndStopMusic();
	                    StopAllSounds();
	                    if(ud.multimode < 2)
	                    {
	                    	switch(ud.volume_number)
						    {
						   		case 0:
									scenestatus = 0;
									if(currentGame.getCON().type == RR66) {
										initanm("turd66.anm",5,5);
										break;
									}
									
									initanm("turdmov.anm",5,5);
									break;
								case 1:
									scenestatus = 0;
									if(currentGame.getCON().type == RR66) {
										initanm("end66.anm",5,4);
										break;
									}
									
									initanm("rr_outro.anm",5,4);
									break;
								default:
									scenestatus = 2;
									break;
						    }
	                    	
	                    	if(scenestatus == 0 && !anmInited())
	                    		scenestatus = 1;
	                    	
	                    	getInput().resetKeyStatus();
							gpmanager.resetButtonStatus();
	                    	
                    		gm |= MODE_CUTSCENE;
	                    	gCutsClock = totalclock - 199;
		                    return;
	                    }
	                    else
	                    {
	                        ud.m_level_number = 0;
	                        ud.level_number = 0;
	                        backtomenu();
	                        return;
	                    }
	                }
					
					if (dobonus(false)) {
						getInput().resetKeyStatus();
						gpmanager.resetButtonStatus();
						
//						if ( ud.volume_number == 1 && ud.level_number >= currentGame.episodes[ud.volume_number].nMaps ) {
//							backtomenu();
//							return;
//						}

		                ready2send = false;
			            if(numplayers > 1) 
			            	gm = MODE_GAME;

			            if(ud.multimode < 2) { 
				 			gAutosaveRequest = true;
				 			gScreenCapture = true;
				 		}
			            
			            enterlevel(gm);
					}
	                break;
					
				case MODE_RESTART:
					ready2send = false;
		            if(numplayers > 1) 
		            	gm = MODE_GAME;
		            enterlevel(gm);
		           
		            if (kGameCrash) {
						backtomenu();
					}
					break;
			}
			
			if(gm == MODE_LOADING)
			{
				ready2send = false;
				engine.clearview(0);
				engine.rotatesprite(320<<15,200<<15,65536,0,LOADSCREEN,0,0,2+8+64,0,0,xdim-1,ydim-1);

				switch(loading_type)
				{
				case 0:
					Arrays.fill(buf, (char)0);
					buildString(buf, 0, "Please wait ");
					mGetAlign(2, buf);
					menutext(160 - alignx / 2, 90+16+8, -128, 0, buf, 0);
					break;
				case 1:
					Arrays.fill(buf, (char)0);
					buildString(buf, 0, loading_mapname);

					mGetAlign(2, buf);
					menutext(160 - alignx / 2, 90+16+8, -128, 0, buf, 0);
					
					Arrays.fill(buf, (char)0);
					buildString(buf, 0, "Entering ");
					if(loading_usermap) buildString(buf, 9, "user map");
					
					mGetAlign(2, buf);
					menutext(160 - alignx / 2, 90, -128, 0, buf, 0);
					break;
				}
			}
			
			if(ud.pause_on == 0)
				getpackets();
			
			if ( gNetDisconnect )
	            NetDisconnect(myconnectindex);

			if(gm != MODE_LOADING && gm != MODE_EOL 
					&& gm != MODE_RESTART && gShowMenu)
				mDrawMenu();
			
			if (cfg.gShowFPS)
				engine.printfps(cfg.gFpsScale);

			engine.sampletimer();
			engine.nextpage();
		} 
		else
			Gdx.app.exit();
	}

	public static void domovethings()
	{	
	    ud.camerasprite = -1;
	    everyothertime++;

	    for(int i=connecthead;i>=0;i=connectpoint2[i]) 
	    	sync[i].copy(inputfifo[movefifoplc&(MOVEFIFOSIZ-1)][i]);
	    movefifoplc++;

	    updateinterpolations();
	    
	    int j = -1;
	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    {
		    cheatkeys(i);
	    
	    	if (gm == MODE_DEMO || (sync[i].bits&(1<<26)) == 0) { j = i; continue; }

	    	closedemowrite();

	    	if (i == myconnectindex) Gdx.app.exit();
	    	if (screenpeek == i)
	    	{
	    		screenpeek = connectpoint2[i];
	    		if (screenpeek < 0) screenpeek = connecthead;
	    	}

	    	if (i == connecthead) connecthead = connectpoint2[connecthead];
	    	else connectpoint2[j] = connectpoint2[i];

	    	numplayers--;
	    	ud.multimode--;

	    	if (numplayers < 2)
	    		sound(GENERIC_AMBIENCE17);

	    	quickkill(ps[i]);
	    	engine.deletesprite(ps[i].i);

	    	buildString(buf, 0, ud.user_name[i], " is history!");
	    	adduserquote(buf);

	    	vscrn(ud.screen_size);

	    	if(j < 0 && networkmode == 0 )
	    	{
	    		backtomenu();
	    		Console.Print( " \nThe 'MASTER/First player' just quit the game.  All\nplayers are returned from the game.");
	    	} 
	    }

	    if ((numplayers >= 2) && ((movefifoplc&7) == 7)) //build sync variables
	    {
	    	int ch = engine.getrand();
	    	int /*p = 0, */s = 0; 
	    	for(int i=connecthead;i>=0;i=connectpoint2[i]) {
//	    		p ^= Checksum(ps[i].getBytes(), PlayerStruct.sizeof);
	    		s ^= Checksum(sprite[ps[i].i].getBytes(), SPRITE.sizeof);
	    	}
	    	
	    	LittleEndian.putInt(syncval[myconnectindex], CheckBytes * (syncvalhead[myconnectindex]&(MOVEFIFOSIZ-1)) + 0, ch);
//	    	LittleEndian.putInt(syncval[myconnectindex], CheckBytes * (syncvalhead[myconnectindex]&(MOVEFIFOSIZ-1)) + 4, p);
	    	LittleEndian.putInt(syncval[myconnectindex], CheckBytes * (syncvalhead[myconnectindex]&(MOVEFIFOSIZ-1)) + 4, s);
	    	syncvalhead[myconnectindex]++;
	    }

	    lockclock += TICSPERFRAME;
	    if( ud.pause_on != 0 || ud.recstat != 2 && ud.multimode < 2 && (gShowMenu || Console.IsShown())) 
	    	return;
	      
	    if(ud.recstat == 1) record();
	    
	    if(earthquaketime > 0) earthquaketime--;
	    if(rtsplaying > 0) rtsplaying--;

	    for(int i=0;i < MAXUSERQUOTES;i++)
	    	if (user_quote_time[i] != 0)
	    		user_quote_time[i]--;
	     
	    if ((klabs(quotebotgoal-quotebot) <= 16) && (ud.screen_size <= 2))
	    	quotebot += ksgn(quotebotgoal-quotebot);
	    else quotebot = quotebotgoal;
	    
	    if(fta > 0)
	    {
	        fta--;
	        if(fta == 0) ftq = 0;
	    }
	    
	    if ( ps[screenpeek].fogtype == 0 ) {
		    if (totalclock < lastvisinc)
		    {
		        if (klabs(gVisibility-currentGame.getCON().const_visibility) > 8)
		        	gVisibility += (currentGame.getCON().const_visibility-gVisibility)>>2;
		    }
		    else gVisibility = currentGame.getCON().const_visibility;
	    }

	    global_random = (short) engine.krand();
	    movedummyplayers();//ST 13
	    
	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	    {
	        processinput(i);
	        checksectors(i);
	    }

        movefta();//ST 2
        moveweapons();          //ST 5 (must be last)
        movetransports();       //ST 9

        moveplayers();          //ST 10
        movefallers();          //ST 12

        moveexplosions();       //ST 4

        moveactors();           //ST 1
        moveeffectors();        //ST 3
        movestandables();       //ST 6
        doanimations();
        movefx();               //ST 11
	    
	    if ( numtorcheffects != 0)
	    	torchesprocess();

	    fakedomovethingscorrect();

	    if( (everyothertime&1) == 0)
	    {
	        animatewalls();
	        movecyclers();
	        pan3dsound();
	    }
	}
	
	public static void fakedomovethingscorrect()
	{
	     if (numplayers < 2) return;

	     int i = ((movefifoplc-1)&(MOVEFIFOSIZ-1));
	     PlayerStruct p = ps[myconnectindex];

	     if (p.posx == myxbak[i] && p.posy == myybak[i] && p.posz == myzbak[i]
	          && p.horiz == myhorizbak[i] && p.ang == myangbak[i]) return;

	     myx = p.posx; myxvel = p.posxv;
	     myy = p.posy; myyvel = p.posyv;
	     myz = p.posz;  myzvel = p.poszv;
	     myang = p.ang; 
	     mycursectnum = p.cursectnum;
	     myhoriz = p.horiz; 
	     myhorizoff = p.horizoff; 
	     myjumpingcounter = p.jumping_counter;
	     myjumpingtoggle = (char) p.jumping_toggle;
	     myonground = p.on_ground;
	     myhardlanding = (char) p.hard_landing;
	     myreturntocenter = (char) p.return_to_center;
	     
	     omyx = p.oposx;
	     omyy = p.oposy;
	     omyz = p.oposz;
	     omyang = p.oang;
	     omyhoriz = p.ohoriz;
	     omyhorizoff = (short) p.ohorizoff;

	     fakemovefifoplc = movefifoplc;
	     while (fakemovefifoplc < movefifoend[myconnectindex])
	          fakedomovethings();
	}
	
	public static void cheatkeys(int snum)
	{
	    int i, k;
	    short dainv;
	    int sb_snum, j;

	    sb_snum = sync[snum].bits;
	    PlayerStruct p = ps[snum];

	    if(p.cheat_phase == 1) return;

	    i = p.aim_mode;
	    p.aim_mode = (sb_snum>>23)&1;
	    if(p.aim_mode < i)
	        p.return_to_center = 9;
	    
	    if((sb_snum & 1 << 22) != 0 && p.last_pissed_time == 0)
	    {
	    	p.last_pissed_time = 4000;
	    	if(ud.lockout == 0)
	    		spritesound(437, p.i);
	
	    	if ( sprite[p.i].extra > currentGame.getCON().max_player_health - currentGame.getCON().max_player_health / 10 )
	  	   	{
	  	        if ( sprite[p.i].extra < currentGame.getCON().max_player_health )
	  	          sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	  	    }
	  	    else
	  	    {
	  	        sprite[p.i].extra += 2;
	  	        p.last_extra = sprite[p.i].extra;
	  	    }
	    }

	    if( (sb_snum&((15<<8)|(1<<12)|(1<<15)|(1<<16)|(1<<22)|  (1<<19)| (1<<20)|(1<<21)|(1<<24)|(1<<25)|(1<<27)|(1<<28)|(1<<29)|(1<<30)|(1<<31))) == 0 )
	        p.interface_toggle_flag = 0;
	    else if(p.interface_toggle_flag == 0 && ( sb_snum&(1<<17) ) == 0)
	    {
	        p.interface_toggle_flag = 1;

	        if( (sb_snum&(1<<21)) != 0)
	        {
	            ud.pause_on ^= 1;
	            if( ud.pause_on == 1 && (sb_snum&(1<<5)) != 0 ) ud.pause_on = 2;
	            if(ud.pause_on != 0)
	            {
	            	if(currMusic != null)
	            		currMusic.pause();
	                engine.getAudio().getSound().stopAllSounds();
	                clearsoundlocks();
	            }
	            else
	            {
	                if(cfg.MusicToggle && currMusic != null) 
	                	currMusic.resume();
	            }
	        }

	        if(ud.pause_on != 0) return;
	        
	        if(sprite[p.i].extra <= 0) return;

	        if( (sb_snum&(1<<30)) != 0 && p.newowner == -1 )
	        {
	            switch(p.inven_icon)
	            {
	                case 4: sb_snum |= (1<<25);break;
	                case 3: sb_snum |= (1<<24);break;
	                case 5: sb_snum |= (1<<15);break;
	                case 1: sb_snum |= (1<<16);break;
	                case 2: sb_snum |= (1<<12);break;
	            }
	        }

	        if( (sb_snum&(1<<12)) != 0 )
	        {
	            if(p.moonshine_amount == 400 )
	            {
	                p.moonshine_amount = 399;
	                spritesound(DUKE_TAKEPILLS,p.i);
	                p.inven_icon = 2;
	                FTA(12,p);
	            }
	            return;
	        }

	        if(p.newowner == -1)
	            if( (sb_snum&(1<<20)) != 0 || (sb_snum&(1<<27)) != 0 || p.refresh_inventory)
	        {
	            p.invdisptime = 26*2;

	            if( (sb_snum&(1<<27)) != 0) k = 1;
	            else k = 0;

	            if(p.refresh_inventory) p.refresh_inventory = false;
	            dainv = (short) p.inven_icon;

	            i = 0;
	            
	            boolean CHECKINV;
	            do
	            {
	            	CHECKINV = false;
		            if(i < 9)
		            {
		                i++;
	
		                switch(dainv)
		                {
		                    case 4:
		                        if(p.cowpie_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 5;
		                        else dainv = 3;
		                        CHECKINV = true;
		                        break;
		                    case 6:
		                        if(p.snorkle_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 7;
		                        else dainv = 5;
		                        CHECKINV = true;
		                        break;
		                    case 2:
		                        if(p.moonshine_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 3;
		                        else dainv = 1;
		                        CHECKINV = true;
		                        break;
		                    case 3:
		                        if(p.beer_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 4;
		                        else dainv = 2;
		                        CHECKINV = true;
		                        break;
		                    case 0:
		                    case 1:
		                        if(p.whishkey_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 2;
		                        else dainv = 7;
		                        CHECKINV = true;
		                        break;
		                    case 5:
		                        if(p.yeehaa_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 6;
		                        else dainv = 4;
		                        CHECKINV = true;
		                        break;
		                    case 7:
		                        if(p.boot_amount > 0 && i > 1)
		                            break;
		                        if(k != 0) dainv = 1;
		                        else dainv = 6;
		                        CHECKINV = true;
		                        break;
		                }
		            }
		            else dainv = 0;
		            p.inven_icon = dainv;
	            } while(CHECKINV);
	            
	            switch(dainv)
	            {
	                case 1: FTA(3,p);break;
	                case 2: FTA(90,p);break;
	                case 3: FTA(91,p);break;
	                case 4: FTA(88,p);break;
	                case 5: FTA(88,p);break;
	                case 6: FTA(89,p);break;
	                case 7: FTA(6,p);break;
	            }
	        }

	        j = ( (sb_snum&(15<<8))>>8 ) - 1;

	        if( j != 1 && p.kickback_pic > 0)
	            p.wantweaponfire = (short) j;

	        if(p.last_pissed_time <= (26*218) 
	        		&& p.show_empty_weapon == 0 
	        		&& p.kickback_pic == 0 
	        		&& p.quick_kick == 0 && sprite[p.i].xrepeat > 8 && p.access_incs == 0 && p.knee_incs == 0 )
	        {
	            if(  ( p.weapon_pos == 0 || ( p.holster_weapon != 0 && p.weapon_pos == -9 ) ) )
	            {
	            	if(j == 12) //last used weapon
	            	{
	            		j = p.curr_weapon;
	            		if(p.last_used_weapon == 0 || p.last_used_weapon == 15)
	            			j = p.last_used_weapon;
	            		else if( p.gotweapon[p.last_used_weapon] && p.ammo_amount[p.last_used_weapon] > 0 )
                            j = p.last_used_weapon;
	            	}
	            	
	                if(j == 10 || j == 11) //next prev weapon
	                {
	                    k = p.curr_weapon;
	                    switch ( k )
	                    {
	                    case CHICKENBOW_WEAPON:
	                    	k = CROSSBOW_WEAPON;
	                    	break;
	                    case BUZSAW_WEAPON:
	                        k = THROWSAW_WEAPON;
	                        break;
	                    case RATE_WEAPON:
	                        k = KNEE_WEAPON;
	                        break;
	                    }
	                    
	                    j = ( j == 10 ? -1 : 1 );
	                    i = 0;

	                    while( ( k >= 0 && k < 10 ) /*|| ( k == BUZSAW_WEAPON && (p.subweapon&(1<<BUZSAW_WEAPON) ) != 0 )*/ )
	                    {
	                    	k += j;
	                    	
	                        if(k == -1) k = 9;
	                        else if(k == 10) k = 0;

	                        if( p.gotweapon[k] && p.ammo_amount[k] > 0 )
	                        {
	                            j = k;
	                            break;
	                        }

	                        i++;
	                        if(i == 10)
	                        {
	                            addweapon( p, KNEE_WEAPON );
	                            break;
	                        }
	                    }
	                }

	                k = -1;

	                if( j == DYNAMITE_WEAPON && p.ammo_amount[DYNAMITE_WEAPON] == 0 )
	                {
	                	k = headspritestat[1];
	                    while(k >= 0)
	                    {
	                        if( sprite[k].picnum == DYNAMITE && sprite[k].owner == p.i )
	                        {
	                            p.gotweapon[DYNAMITE] = true;
	                            j = HANDREMOTE_WEAPON;
	                            break;
	                        }
	                        k = nextspritestat[k];
	                    }
	                }
	                
	                if(currentGame.getCON().type == RRRA && j == CROSSBOW_WEAPON)
	                {
	                    if( p.curr_weapon != CROSSBOW_WEAPON && p.ammo_amount[CROSSBOW_WEAPON] != 0) 
	                    {
                            if( (p.subweapon&4) != 0 || p.ammo_amount[CHICKENBOW_WEAPON] == 0 )
                            {
                                j = CROSSBOW_WEAPON;
                                p.subweapon = 0;
                            }
	                    }
	                    else 
	                    {
	                        p.subweapon = 4;
	                        j = CHICKENBOW_WEAPON;
	                    }
	                }

	                if(j == THROWSAW_WEAPON)
	                {
	                    if( p.curr_weapon != THROWSAW_WEAPON && p.ammo_amount[THROWSAW_WEAPON] != 0) //v0.751
	                    {
                            if( (p.subweapon&(1<<BUZSAW_WEAPON)) != 0 || p.ammo_amount[BUZSAW_WEAPON] == 0 )
                            {
                                j = THROWSAW_WEAPON;
                                p.subweapon = 0;
                            }
	                    }
	                    else 
	                    {
	                        p.subweapon = (1<<BUZSAW_WEAPON);
	                        j = BUZSAW_WEAPON;
	                    }
	                }
	                
	                if(j == POWDERKEG_WEAPON)
	                {
	                	
	                	if ( p.curr_weapon != POWDERKEG_WEAPON && p.ammo_amount[POWDERKEG_WEAPON] != 0 )
	                	{
	                		if ( (p.subweapon&(1<<BOWLING_WEAPON)) != 0 || p.ammo_amount[BOWLING_WEAPON] == 0 )
	                		{
	                			j = POWDERKEG_WEAPON;
	                        	p.subweapon = 0;
	                		}
	                    }
	                    else
	                    {
	                    	j = BOWLING_WEAPON;
	                    	p.subweapon = (1<<BOWLING_WEAPON);
	                    }
	                }
	                
	                if(currentGame.getCON().type == RRRA && j == KNEE_WEAPON)
	                {
	                	
	                	if ( p.curr_weapon != KNEE_WEAPON )
	                	{
	                		if ( (p.subweapon & 2) != 0 )
	                		{
	                			j = KNEE_WEAPON;
	                        	p.subweapon = 0;
	                		}
	                    }
	                    else
	                    {
	                    	j = RATE_WEAPON;
	                    	p.subweapon = 2;
	                    }
	                }
	                

	                if(p.holster_weapon != 0)
	                {
	                    sb_snum |= 1<<19;
	                    p.weapon_pos = -9;
	                }
	                else if( j >= 0 && p.gotweapon[j] && p.curr_weapon != j ) switch(j)
	                {
	                    case KNEE_WEAPON:
	                        addweapon( p, KNEE_WEAPON );
	                        break;
	                    case PISTOL_WEAPON:
	                        if ( p.ammo_amount[PISTOL_WEAPON] == 0 )
	                            if(p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, PISTOL_WEAPON );
	                        break;
	                    case SHOTGUN_WEAPON:
	                        if( p.ammo_amount[SHOTGUN_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, SHOTGUN_WEAPON);
	                        break;
	                    case RIFLEGUN_WEAPON:
	                        if( p.ammo_amount[RIFLEGUN_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, RIFLEGUN_WEAPON);
	                        break;
	                    case DYNAMITE_WEAPON:
	                        if( p.ammo_amount[DYNAMITE_WEAPON] == 0 )
	                            if(p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, DYNAMITE_WEAPON );
	                        break;
	                    case ALIENBLASTER_WEAPON:
	                        if( p.ammo_amount[ALIENBLASTER_WEAPON] == 0 && p.show_empty_weapon == 0 )
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, ALIENBLASTER_WEAPON );
	                        break;
	                    case TIT_WEAPON:
	                        if( p.ammo_amount[TIT_WEAPON] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.last_full_weapon = p.curr_weapon;
	                            p.show_empty_weapon = 32;
	                        }
	                        addweapon( p, TIT_WEAPON );
	                        break;
	                    case BUZSAW_WEAPON:
	                    case THROWSAW_WEAPON:
	                        if( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0)
	                        {
	                            p.show_empty_weapon = 32;
	                            p.last_full_weapon = p.curr_weapon;
	                        }

	                        addweapon(p, j);
	                        break;
	                    case HANDREMOTE_WEAPON:
	                        if(k >= 0) // Found in list of [1]'s
	                        {
	                            p.curr_weapon = HANDREMOTE_WEAPON;
	                            p.last_weapon = -1;
	                            p.weapon_pos = 10;
	                        }
	                        break;
	                    case CROSSBOW_WEAPON:
	                        if( p.ammo_amount[CROSSBOW_WEAPON] > 0 && p.gotweapon[CROSSBOW_WEAPON] )
	                            addweapon( p, CROSSBOW_WEAPON );
	                        break;
	                    case POWDERKEG_WEAPON:
	                    case BOWLING_WEAPON:
	                    case CHICKENBOW_WEAPON:
	                    	if( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0)
	                    	{
	                    		p.show_empty_weapon = 32;
	                    		p.last_full_weapon = p.curr_weapon;
	                    	}
	                    	addweapon( p, j );
	                        break;
	                    case MOTO_WEAPON:
	                    case BOAT_WEAPON:
	                    	if ( p.ammo_amount[j] == 0 && p.show_empty_weapon == 0 )
	                    		p.show_empty_weapon = 32;
	                    		addweapon(p, j);
	                    	break;
	                    case RATE_WEAPON:
	                        spritesound(496, p.i);
	                        addweapon(p, j);
	                        break;
	                }
	            }

	            if( (sb_snum&(1<<19)) != 0 )
	            {
	                if( p.curr_weapon > KNEE_WEAPON )
	                {
	                    if(p.holster_weapon == 0 && p.weapon_pos == 0)
	                    {
	                        p.holster_weapon = 1;
	                        p.weapon_pos = -1;
	                        FTA(73,p);
	                    }
	                    else if(p.holster_weapon == 1 && p.weapon_pos == -9)
	                    {
	                        p.holster_weapon = 0;
	                        p.weapon_pos = 10;
	                        FTA(74,p);
	                    }
	                }
	            }
	        }

	        if( (sb_snum&(1<<24)) != 0 && p.beer_amount > 0 && sprite[p.i].extra < currentGame.getCON().max_player_health )
	        {
	        	p.beer_amount -= 400;
	        	sprite[p.i].extra += 5;
	            p.inven_icon = 3;

	            if ( sprite[p.i].extra > currentGame.getCON().max_player_health )
	            	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	            p.alcohol_amount += 5;
	            if ( p.beer_amount == 0 )
	                checkavailinven(p);
	            if ( p.alcohol_amount < 99 && Sound[425].num == 0)
	                  spritesound(425, p.i);
	        }
	        
	        if( (sb_snum&(1<<15)) != 0 )
	        {
	        	if( p.newowner == -1 && p.field_count == 0 )
	        	{
	        		p.field_count = 126;
	        		spritesound(390, p.i);
	        		p.field_290 = 0x4000;
	        		sub_64EF0(snum);
	        		if ( sector[p.cursectnum].lotag == 857 )
	        	    {
	        			if(sprite[p.i].extra < currentGame.getCON().max_player_health)
	        			{
	        				sprite[p.i].extra += 10;
	        				if(sprite[p.i].extra > currentGame.getCON().max_player_health)
	                        	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	        			}
	        	    }
	        	    else
	        	    {
	        	    	if(sprite[p.i].extra + 1 <= currentGame.getCON().max_player_health)
	        	    		sprite[p.i].extra++;
	        	    }
	        	}
	        }

	        if( (sb_snum&(1<<16)) != 0 )
	        {
	            if( p.whishkey_amount > 0 && sprite[p.i].extra < currentGame.getCON().max_player_health )
	            {
	                if(p.whishkey_amount > 10)
	                {
	                    p.whishkey_amount -= 10;
	                    sprite[p.i].extra += 10;
	                    p.inven_icon = 1;
	                }
	                else
	                {
	                    sprite[p.i].extra += p.whishkey_amount;
	                    p.whishkey_amount = 0;
	                    checkavailinven(p);
	                }
	                if(sprite[p.i].extra > currentGame.getCON().max_player_health)
                    	sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	               
	                p.alcohol_amount += 10;
	                if ( p.alcohol_amount <= 100 && Sound[DUKE_USEMEDKIT].num == 0)
	                	 spritesound(DUKE_USEMEDKIT,p.i);
	            }
	        }

	        if( (sb_snum&(1<<25)) != 0)
	        {
	        	if ( p.cowpie_amount > 0 )
	            {
	        		if ( sprite[p.i].extra < currentGame.getCON().max_player_health )
	        		{
	        			if ( Sound[429].num == 0 )
	        				spritesound(429, p.i);
	        			p.cowpie_amount -= 100;
	        			if ( p.alcohol_amount > 0 )
	        			{   
	        				p.alcohol_amount -= 5;
	        				if(p.alcohol_amount < 0)
	        					p.alcohol_amount = 0;
	        			}
	        			if ( p.gut_amount < 100 )
	        			{
	        				p.gut_amount += 5;
	        				if ( p.gut_amount > 100 )
	        					p.gut_amount = 100;
	        			}
	                
	        			sprite[p.i].extra += 5;
	        			if(sprite[p.i].extra > currentGame.getCON().max_player_health)
	        				sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	        			p.inven_icon = 4;
	        			if(p.cowpie_amount <= 0)
	        				checkavailinven(p);
	        		}
	            }
	        }

	        if((sb_snum&(1<<28)) != 0 && p.one_eighty_count == 0)
	            p.one_eighty_count = -1024;
	    }
	}
	
	public static void fakedomovethings()
	{
	        Input syn;
	        PlayerStruct p;
	        int i, j, k, doubvel, fz, cz, hz, lz, x, y;
	        int sb_snum;
	        short psect, psectlotag, tempsect, backcstat;
	        boolean shrunk;
			short spritebridge;

	        syn = inputfifo[fakemovefifoplc&(MOVEFIFOSIZ-1)][myconnectindex];

	        p = ps[myconnectindex];

	        backcstat = sprite[p.i].cstat;
	        sprite[p.i].cstat &= ~257;

	        sb_snum = syn.bits;
	 
	        psect = mycursectnum;
	        psectlotag = sector[psect].lotag;
	        spritebridge = 0;

	        shrunk = (sprite[p.i].yrepeat < 8);

	        if( !ud.clipping && ( sector[psect].floorpicnum == MIRROR || psect < 0 || psect >= MAXSECTORS) )
	        {
	            myx = omyx;
	            myy = omyy;
	        }
	        else
	        {
	            omyx = myx;
	            omyy = myy;
	        }

	        omyhoriz = myhoriz;
	        omyhorizoff = myhorizoff;
	        omyz = myz;
	        omyang = myang;

	        engine.getzrange(myx,myy,myz,psect,163,CLIPMASK0);
	        cz = zr_ceilz;
		    hz = zr_ceilhit;
		    fz = zr_florz;
		    lz = zr_florhit;

	        j = engine.getflorzofslope(psect,myx,myy);

	        if( (lz&49152) == 16384 && psectlotag == 1 && klabs(myz-j) > PHEIGHT+(16<<8) )
	            psectlotag = 0;

	        if( p.aim_mode == 0 && myonground && psectlotag != 2 && (sector[psect].floorstat&2) != 0 )
	        {
                x = (int) (myx+(BCosAngle(BClampAngle(myang)) / 32.0f));
                y = (int) (myy+(BSinAngle(BClampAngle(myang)) / 32.0f));
                tempsect = psect;
                tempsect = engine.updatesector(x,y, tempsect);
                if (tempsect >= 0)
                {
                     k = engine.getflorzofslope(psect,x,y);
                     if (psect == tempsect)
                          myhorizoff += mulscale(j-k,160, 16);
                     else if (klabs(engine.getflorzofslope(tempsect,x,y)-k) <= (4<<8))
                          myhorizoff += mulscale(j-k,160, 16);
                }
	        }
	        if (myhorizoff > 0) myhorizoff -= ((myhorizoff>>3)+1);
	        else if (myhorizoff < 0) myhorizoff += (((-myhorizoff)>>3)+1);

	        if(hz >= 0 && (hz&kHitTypeMask) == kHitSprite)
	        {
                hz &= (kHitIndexMask);
                if (sprite[hz].statnum == 1 && sprite[hz].extra >= 0)
                {
                    hz = 0;
                    cz = engine.getceilzofslope(psect,myx,myy);
                }
                if ( sprite[hz].picnum == 3587 )
    	        {
    	        	if ( p.field_280 == 0 )
    	            {
    	            	if ( (sb_snum & 1) != 0 )
    	            	{
    	            		hz = 0;
    	            		cz = p.truecz;
    	            	}
    	            }
    	        }
	        }

	        if(lz >= 0 && (lz&kHitTypeMask) == kHitSprite)
	        {
                 j = lz&(kHitIndexMask);
                 if ((sprite[j].cstat&33) == 33)
                 {
                        psectlotag = 0;
                        spritebridge = 1;
                 }
                 if(badguy(sprite[j]) && sprite[j].xrepeat > 24 && klabs(sprite[p.i].z-sprite[j].z) < (84<<8) )
                 {
                    j = engine.getangle( sprite[j].x-myx,sprite[j].y-myy);
                    myxvel -= sintable[(j+512)&2047]<<4;
                    myyvel -= sintable[j&2047]<<4;
                 }
                 
                 if ( sprite[j].picnum == 3587 )
                 {
     	        	if ( p.field_280 == 0 )
     	            {
     	            	if ( (sb_snum & 2) != 0 )
     	            	{
     	            		cz = sprite[j].z;
     	                    hz = 0;
     	                    fz = cz + 1024;
     	            	}
     	            }
                 }
	        }

	        if( sprite[p.i].extra <= 0 )
	        {
	                 if( psectlotag == 2 )
	                 {
	                            if(p.on_warping_sector == 0)
	                            {
	                                     if( klabs(myz-fz) > (PHEIGHT>>1))
	                                             myz += 348;
	                            }
	                            engine.clipmove(myx,myy,myz,mycursectnum,0,0,164,(4<<8),(4<<8),CLIPMASK0);
	                            if(clipmove_sectnum != -1) {
	                            	myx = clipmove_x;
	            		            myy = clipmove_y;
	            		            myz = clipmove_z;
	            		            mycursectnum = (short) clipmove_sectnum;
	            	            }
	                 }

	                 short sect =engine.updatesector(myx,myy,mycursectnum);
	                 if(sect != -1)
	                	 mycursectnum = sect;
	                 engine.pushmove(myx,myy,myz,mycursectnum,128,(4<<8),(20<<8),CLIPMASK0);
	                if(pushmove_sectnum != -1) {
		                 myx = pushmove_x;
		                 myy = pushmove_y;
		                 myz = pushmove_z;
		                 mycursectnum = (short) pushmove_sectnum;
	                }
	                myhoriz = 100;
	                myhorizoff = 0;

	                myxbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myx;
	    	        myybak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myy;
	    	        myzbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myz;
	    	        myangbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myang;
	    	        myhorizbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myhoriz;
	    	        fakemovefifoplc++;

	    	        sprite[p.i].cstat = backcstat;
	    	        return;
	        }

	        doubvel = TICSPERFRAME;

	        if(p.on_crane < 0) 
	        {
		        if(p.one_eighty_count < 0) myang += 128;
	
		        i = 40;
	
		        if( psectlotag == 2)
		        {
		                 myjumpingcounter = 0;
	
		                 if ( (sb_snum&1) != 0 )
		                 {
		                            if(myzvel > 0) myzvel = 0;
		                            myzvel -= 348;
		                            if(myzvel < -(256*6)) myzvel = -(256*6);
		                 }
		                 else if ((sb_snum&(1<<1)) != 0)
		                 {
		                            if(myzvel < 0) myzvel = 0;
		                            myzvel += 348;
		                            if(myzvel > (256*6)) myzvel = (256*6);
		                 }
		                 else
		                 {
		                    if(myzvel < 0)
		                    {
		                        myzvel += 256;
		                        if(myzvel > 0)
		                            myzvel = 0;
		                    }
		                    if(myzvel > 0)
		                    {
		                        myzvel -= 256;
		                        if(myzvel < 0)
		                            myzvel = 0;
		                    }
		                }
	
		                if(myzvel > 2048) myzvel >>= 1;
	
		                 myz += myzvel;
	
		                 if(myz > (fz-(15<<8)) )
		                            myz += ((fz-(15<<8))-myz)>>1;
	
		                 if(myz < (cz+(4<<8)) )
		                 {
		                            myz = cz+(4<<8);
		                            myzvel = 0;
		                 }
		        }
	
		        else if(p.jetpack_on != 0)
		        {
		                 myonground = false;
		                 myjumpingcounter = 0;
		                 myhardlanding = 0;
	
		                 if(p.jetpack_on < 11)
		                            myz -= (p.jetpack_on<<7); //Goin up
	
		                 if(shrunk) j = 512;
		                 else j = 2048;
	
		                 if ((sb_snum&1) != 0)                            //A
		                            myz -= j;
		                 if ((sb_snum&(1<<1)) != 0)                       //Z
		                            myz += j;
	
		                 if(!shrunk && ( psectlotag == 0 || psectlotag == 2 ) ) k = 32;
		                 else k = 16;
	
		                 if(myz > (fz-(k<<8)) )
		                            myz += ((fz-(k<<8))-myz)>>1;
		                 if(myz < (cz+(18<<8)) )
		                            myz = cz+(18<<8);
		        }
		        else if( psectlotag != 2 )
		        {
		            if (psectlotag == 1 && p.spritebridge == 0)
		            {
		                 if(!shrunk) i = 34;
		                 else i = 12;
		            }
		                 if(myz < (fz-(i<<8)) && !floorspace(psect) && !ceilingspace(psect) ) //falling
		                 {
		                            if( (sb_snum&3) == 0 && myonground && (sector[psect].floorstat&2) != 0 && myz >= (fz-(i<<8)-(16<<8) ) )
		                                     myz = fz-(i<<8);
		                            else
		                            {
		                                     myonground = false;
	
		                                     myzvel += (currentGame.getCON().gc+80);
	
		                                     if(myzvel >= (4096+2048)) myzvel = (4096+2048);
		                            }
		                 }
		                 else
		                 {
		                            if(psectlotag != 1 && psectlotag != 2 && !myonground && myzvel > (6144>>1))
		                                 myhardlanding = (char) (myzvel>>10);
		                            myonground = true;
	
		                            if(i==40)
		                            {
		                                     //Smooth on the ground
	
		                                     k = ((fz-(i<<8))-myz)>>1;
		                                     if( klabs(k) < 256 ) k = 0;
		                                     myz += k; // ((fz-(i<<8))-myz)>>1;
		                                     myzvel -= 768; // 412;
		                                     if(myzvel < 0) myzvel = 0;
		                            }
		                            else if(myjumpingcounter == 0)
		                            {
		                                myz += ((fz-(i<<7))-myz)>>1; //Smooth on the water
		                                if(p.on_warping_sector == 0 && myz > fz-(16<<8))
		                                {
		                                    myz = fz-(16<<8);
		                                    myzvel >>= 1;
		                                }
		                            }
	
		                            if( (sb_snum&2) != 0 )
		                                     myz += (2048+768);
	
		                            if( (sb_snum&1) == 0 && myjumpingtoggle == 1)
		                                     myjumpingtoggle = 0;
	
		                            else if( (sb_snum&1) != 0 && myjumpingtoggle == 0 )
		                            {
		                                     if( myjumpingcounter == 0 )
		                                             if( (fz-cz) > (56<<8) )
		                                             {
		                                                myjumpingcounter = 1;
		                                                myjumpingtoggle = 1;
		                                             }
		                            }
		                            if( myjumpingcounter != 0 && (sb_snum&1) == 0 )
		                                myjumpingcounter = 0;
		                 }
	
		                 if(myjumpingcounter != 0)
		                 {
		                            if( (sb_snum&1) == 0 && myjumpingtoggle == 1)
		                                     myjumpingtoggle = 0;
	
		                            if( myjumpingcounter < (768) )
		                            {
		                                     if(psectlotag == 1 && myjumpingcounter > 768)
		                                     {
		                                             myjumpingcounter = 0;
		                                             myzvel = -512;
		                                     }
		                                     else
		                                     {
		                                             myzvel -= (sintable[(2048-128+myjumpingcounter)&2047])/12;
		                                             myjumpingcounter += 180;
	
		                                             myonground = false;
		                                     }
		                            }
		                            else
		                            {
		                                     myjumpingcounter = 0;
		                                     myzvel = 0;
		                            }
		                 }
	
		                 myz += myzvel;
	
		                 if(myz < (cz+(4<<8)) )
		                 {
		                            myjumpingcounter = 0;
		                            if(myzvel < 0) myxvel = myyvel = 0;
		                            myzvel = 128;
		                            myz = cz+(4<<8);
		                 }
	
		        }
	
		        if ( p.fist_incs != 0 ||
		                     p.transporter_hold > 2 ||
		                     myhardlanding != 0 ||
		                     p.access_incs > 0 ||
		                     p.knee_incs > 0 ||
		                     (p.curr_weapon == POWDERKEG_WEAPON &&
		                      p.kickback_pic > 1 &&
		                      p.kickback_pic < 4 ) )
		        {
		                 doubvel = 0;
		                 myxvel = 0;
		                 myyvel = 0;
		        }
		        else if ( syn.avel != 0)          //p.ang += syncangvel * constant
		        {                         //ENGINE calculates angvel for you
		            long tempang;
	
		            tempang = (long) (syn.avel * 2);
	
		            if(psectlotag == 2)
		                myang += (tempang-(tempang>>3))*ksgn(doubvel);
		            else myang += (tempang)*ksgn(doubvel);
		            myang = BClampAngle(myang);
		        }
	
		        if ( myxvel != 0 || myyvel != 0 || syn.fvel != 0 || syn.svel != 0 )
		        {
		                 if(p.jetpack_on == 0 && p.moonshine_amount > 0 && p.moonshine_amount < 400)
		                     doubvel <<= 1;
	
		                 myxvel += ((syn.fvel*doubvel)<<6);
		                 myyvel += ((syn.svel*doubvel)<<6);
	
		                 if( ( p.curr_weapon == KNEE_WEAPON && p.kickback_pic > 10 && myonground ) || ( myonground && (sb_snum&2) != 0 ) )
		                 {
		                            myxvel = mulscale(myxvel,currentGame.getCON().dukefriction-0x2000, 16);
		                            myyvel = mulscale(myyvel,currentGame.getCON().dukefriction-0x2000, 16);
		                 }
		                 else
		                 {
		                    if(psectlotag == 2)
		                    {
		                        myxvel = mulscale(myxvel,currentGame.getCON().dukefriction-0x1400, 16);
		                        myyvel = mulscale(myyvel,currentGame.getCON().dukefriction-0x1400, 16);
		                    }
		                    else
		                    {
		                        myxvel = mulscale(myxvel,currentGame.getCON().dukefriction, 16);
		                        myyvel = mulscale(myyvel,currentGame.getCON().dukefriction, 16);
		                    }
		                 }
	
		                 if( abs(myxvel) < 2048 && abs(myyvel) < 2048 )
		                     myxvel = myyvel = 0;
	
		                 if( shrunk )
		                 {
		                     myxvel =
		                         mulscale(myxvel,(currentGame.getCON().dukefriction)-(currentGame.getCON().dukefriction>>1)+(currentGame.getCON().dukefriction>>2), 16);
		                     myyvel =
		                         mulscale(myyvel,(currentGame.getCON().dukefriction)-(currentGame.getCON().dukefriction>>1)+(currentGame.getCON().dukefriction>>2), 16);
		                 }
		        }
	        }

	        if(psectlotag == 1 || spritebridge == 1) i = (4<<8); else i = (20<<8);

	        engine.clipmove(myx,myy,myz,mycursectnum,myxvel,myyvel,164,4<<8,i,CLIPMASK0);
	        if(clipmove_sectnum != -1) {
            	myx = clipmove_x;
	            myy = clipmove_y;
	            myz = clipmove_z;
	            mycursectnum = (short) clipmove_sectnum;
            }
	        
	        engine.pushmove(myx,myy,myz,mycursectnum,164, 4<<8,4<<8,CLIPMASK0);
	        if(pushmove_sectnum != -1) {
		        myx = pushmove_x;
	            myy = pushmove_y;
	            myz = pushmove_z;
	            mycursectnum = (short) pushmove_sectnum;
	        }
            
	        if( p.jetpack_on == 0 && psectlotag != 1 && psectlotag != 2 && shrunk)
	            myz += 30<<8;

	        if (((sb_snum&(1<<18)) != 0) || myhardlanding != 0)
	            myreturntocenter = 9;

	        if ((sb_snum&(1<<13)) != 0)
	        {
	                myreturntocenter = 9;
	                if ((sb_snum&(1<<5)) != 0) myhoriz += 6;
	                myhoriz += 6;
	        }
	        else if ((sb_snum&(1<<14)) != 0)
	        {
	                myreturntocenter = 9;
	                if ((sb_snum&(1<<5)) != 0) myhoriz -= 6;
	                myhoriz -= 6;
	        }
	        else if ((sb_snum&(1<<3)) != 0)
	        {
	                if ((sb_snum&(1<<5)) != 0) myhoriz += 6;
	                myhoriz += 6;
	        }
	        else if ((sb_snum&(1<<4)) != 0)
	        {
	                if ((sb_snum&(1<<5)) != 0) myhoriz -= 6;
	                myhoriz -= 6;
	        }

	        if (myreturntocenter > 0)
	            if ((sb_snum&(1<<13)) == 0 && (sb_snum&(1<<14)) == 0)
	        {
	             myreturntocenter--;
	             myhoriz += 33-(myhoriz/3);
	        }

	        if(p.aim_mode != 0)
	            myhoriz += syn.horz / 2;
	        else
	        {
	            if( myhoriz > 95 && myhoriz < 105) myhoriz = 100;
	            if( myhorizoff > -5 && myhorizoff < 5) myhorizoff = 0;
	        }

	        if (myhardlanding > 0)
	        {
	            myhardlanding--;
	            myhoriz -= (myhardlanding<<4);
	        }

	        if (myhoriz > 299) myhoriz = 299;
	        else if (myhoriz < -99) myhoriz = -99;

	        if(p.knee_incs > 0)
	        {
	            myhoriz -= 48;
	            myreturntocenter = 9;
	        }

	        myxbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myx;
	        myybak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myy;
	        myzbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myz;
	        myangbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myang;
	        myhorizbak[fakemovefifoplc&(MOVEFIFOSIZ-1)] = myhoriz;
	        fakemovefifoplc++;

	        sprite[p.i].cstat = backcstat;
	}
	
	public static void input()
	{
		if(numplayers > 1)
			getpackets();

		for(int i=connecthead;i>=0;i=connectpoint2[i])
			if (i != myconnectindex)
				if (movefifoend[i] < movefifoend[myconnectindex]-200) return;

		if ( ps[myconnectindex].OnMotorcycle )
			motoinput(myconnectindex);
		else if ( ps[myconnectindex].OnBoat ) {
			boatinput(myconnectindex);
		} else getinput(myconnectindex);

		if ((movefifoend[myconnectindex]&(movesperpacket-1)) != 0)
		{
			inputfifo[movefifoend[myconnectindex]&(MOVEFIFOSIZ-1)][myconnectindex].
				copy(inputfifo[(movefifoend[myconnectindex]-1)&(MOVEFIFOSIZ-1)][myconnectindex]);
			movefifoend[myconnectindex]++;
			return;
		}

		inputfifo[movefifoend[myconnectindex]&(MOVEFIFOSIZ-1)][myconnectindex].copy(loc);
		movefifoend[myconnectindex]++;

		if (numplayers < 2)
		{
			if (ud.multimode > 1)
				for(int i=connecthead;i>=0;i=connectpoint2[i])
					if(i != myconnectindex)
					{
						if(ud.playerai != 0)
							computergetinput(i,inputfifo[movefifoend[i]&(MOVEFIFOSIZ-1)][i]);
						movefifoend[i]++;
					}
			return;
		}

		netinput();
	}

	public static void backtomenu()
	{
		ready2send = false;
		boardfilename = null;
		if (ud.recstat == 1) 
			closedemowrite();
		ud.warp_on = 0;

		scrReset();
		StopAllSounds();
		sndStopMusic();
		DemoReset();
		mFakeMultiplayer = false;
		
		lastload = null;
		
		getInput().resetKeyStatus();
		gpmanager.resetButtonStatus();
		
		if(anmInited())
			closeanm();
		
		mClose();
		gm = MODE_WAIT;
		gNetFlags = 0;
		new Thread(new Runnable() {
			public void run() {
				resetEpisodeResources();
				initanm("rr_intro.anm",5, -1);
				gm = MODE_LOGO;
			}
		}).start();
		
		mClose();
	}
	
	public static void appdispose()
	{
		closedemowrite();
		if(engine != null)
			engine.uninit();

		for(int i = 0; fxdrivers != null && i < fxdrivers.length; i++)
			if(i != cfg.snddrv && fxdrivers[i].getName().contains("OpenAL")) {
				fxdrivers[i].destroy(); //OpenAL dispose if DummySound choosed
			}
		
		saveConfig();
		if(BuildGDX.app.getFrameType() == FrameType.GL) 
			((GLFrame)BuildGDX.app.getFrame()).setDefaultDisplayConfiguration();
		CloseLogFile();
		System.out.println("disposed");
	}
	
	public static void setDefs(DefScript script)
	{
		currentDef = script;
		engine.setDefs(script);
	}
}
