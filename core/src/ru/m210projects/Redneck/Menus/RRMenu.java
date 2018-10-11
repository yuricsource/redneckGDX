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

import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Premap.enterlevel;
import static ru.m210projects.Redneck.Premap.newgame;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Gameutils.toCharArray;
import static ru.m210projects.Redneck.Types.Demo.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Menus.MenuTextField.*;
import static ru.m210projects.Redneck.Types.Demo.DemoReset;
import static ru.m210projects.Build.Audio.BAudio.MUSICDRV;
import static ru.m210projects.Build.Audio.BAudio.SOUNDDRV;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.MOUSE_BUTTON11;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Render.VideoMode.strvmodes;
import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Menus.MENU.*;

import ru.m210projects.Redneck.Menus.MENU;
import ru.m210projects.Redneck.Menus.MENUPROC;
import ru.m210projects.Redneck.Menus.MenuButton;
import ru.m210projects.Redneck.Menus.MenuItem;
import ru.m210projects.Redneck.Menus.MenuSlider;
import ru.m210projects.Redneck.Menus.MenuTextField;
import ru.m210projects.Redneck.Menus.MenuTitle;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.VideoMode;
import ru.m210projects.Build.Types.BGraphics;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.SaveManager;

public class RRMenu {
	
	public static final int TA_LEFT	= 0;
	public static final int	TA_CENTER = 1;
	public static final int	TA_RIGHT = 2;

	public static VideoMode choosedMode;
	public static VideoMode currentMode;
	public static boolean isFullscreen;
	
	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int OPTIONS = 2;
	public static final int HELP = 3;
	public static final int CREDITS = 4;
	public static final int QUIT = 5;
	public static final int QUITTITLE = 6;
	public static final int LOAD = 7;
	public static final int SAVE = 8;
	public static final int GAMEST = 9;
	public static final int SOUNDST = 10;
	public static final int VIDEOST = 11;
	public static final int VIDEOMODES = 13;
	public static final int RESOLUTIONST = 14;
	public static final int CTRLST = 15;
	public static final int HUDST = 16;
	public static final int MOUSESETUP = 17;
	public static final int JOYSTICKSETUP = 18;
	public static final int KEYSET = 19;
	public static final int RESETSETUP = 20;
	public static final int JOYKEYSET = 21;
	public static final int ADVANCEDMOUSESET = 22;
	public static final int LOADGAME = 23;
	public static final int SAVEGAME = 24;
	public static final int NEWGAME = 25;
	public static final int DIFFICULTY = 26;
	public static final int MULTIPLAYER = 27;
	public static final int MCREATE = 28;
	public static final int MJOIN = 29;
	public static final int NETWORKGAME = 30;
	public static final int USERCONTENT = 31;
	public static final int COLORCORR = 32;
	public static final int RESETCSETUP = 33;
	public static final int NEWUSERGAME = 34;
	
	public static boolean mUseFakeMultiplayer;
	private static int mUserFlag;
	
	public static int mContentUpdate(FileEntry fil, GameInfo ini)
	{
		System.err.println("mContentUpdate");
		int nFlags = 0;
		mContent = fil.getName();

		if (fil.getExtension().equals("map")) {
			boardfilename = fil.getPath();
			ud.m_level_number = 3;
	        ud.m_volume_number = 2;
			nFlags = 2;
			mGameInfo = null;
			mSkilllist.clear();
			for (int i = 0; i < nMaxSkills; i++) {
				if(defGame.skillnames[i] != null) 
					mSkilllist.add(defGame.skillnames[i].toCharArray());
			}
		} else {
			if (ini != null) {
				nFlags = 1;
				if(ini == defGame) {
					nFlags = 0;
					mContent = "None";
				}

				updateUserEpisodeList(ini);
			} else {
				nFlags = 0;
				mContent = "None";
				updateUserEpisodeList(defGame);
			}
			ud.m_level_number = 0;
	        ud.m_volume_number = 0;
		}

		if (!mFromNetworkSetup() && nFlags == 2)
			mOpen(mMenus[DIFFICULTY], -1);

		if (nFlags != 2) {
			if (mCount > 0 && mMenuHistory[mCount - 1] == mMenus[NEWGAME])
				mOpen(mMenus[NEWUSERGAME], -1);
			mEpisodeUpdateRequest = true;
		}

		mUserFlag = nFlags;

		return nFlags;
	}
	
	private static final List<char[]> mEpisodelist = new ArrayList<char[]>();
	private static final List<char[]> mSkilllist = new ArrayList<char[]>();
	private static GameInfo mGameInfo;
	
	private static void updateUserEpisodeList(GameInfo gInfo) {
		mGameInfo = gInfo;
		mEpisodelist.clear();
		for (int i = 0; i < nMaxEpisodes; i++) {
			if(gInfo.episodes[i] != null && gInfo.episodes[i].nMaps != 0) 
				mEpisodelist.add(gInfo.episodes[i].Title.toCharArray());
			else mEpisodelist.add(null);
		} 
		
		mSkilllist.clear();
		for (int i = 0; i < nMaxSkills; i++) {
			if(mGameInfo.skillnames[i] != null) 
				mSkilllist.add(mGameInfo.skillnames[i].toCharArray());
		}
	}
	
	public static void mResetContent()
	{
		System.err.println("mResetContent");
		mContent = "None";
		updateUserEpisodeList(defGame);
		mEpisodeUpdateRequest = true;
		mUserFlag = 0;
		ud.m_level_number = 0;
        ud.m_volume_number = 0;
		boardfilename = null;
	}
	
	private static void mNewUserGame(int nMenuId) {
		MENUPROC UserProc = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuList button = (MenuList) pItem;
				ud.m_volume_number = button.l_nFocus;
				button.l_nFocus = button.l_nMin = 0;
			}
		};

		MenuTitle mTitle = new MenuTitle("SELECT AN EPISODE", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);
		MenuList mSlot = new MenuList(mEpisodelist, 2, 0, 55, 320, 1, 5, mMenus[DIFFICULTY], UserProc, nMaxEpisodes);
		mAddItem(mMenus[nMenuId], mSlot, true);
	}
	
	public static void mInit()
	{
		Console.Println("Initializing menu system");
		for (int i = 0; i < kMaxGameMenus; i++)
			mMenus[i] = new MENU();
		
		FindSaves();
		
		MENUPROC UserContProc = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuFileBrowser item = (MenuFileBrowser) pItem;
				FileEntry fil = item.currFile;
				
				if (numplayers > 1) {
					String path;
					if (item.currGame == null)
						path = fil.getPath();
					else
						path = levelEpisodePath(item.currGame);
					if (!SendContent(path, false)) {
						mMenuBack();
						return;
					}
				}

				mContentUpdate(fil, item.currGame);
				
				if (mFromNetworkSetup())
					mMenuBack();
			}
		};
		
		Console.Println("Searching for addition content...");

		mUserContent(USERCONTENT, UserContProc);
		mMain(MAIN);
		mGame(GAME);
		mNewUserGame(NEWUSERGAME);
		mOptions(OPTIONS);
		mHelp(HELP);
		mCredits(CREDITS);
		mQuit(QUIT);
		
		mSounds(SOUNDST);
		mVideoSet(VIDEOST);
		mVideoMode(VIDEOMODES);
		mResolution(RESOLUTIONST);
		mGameSet(GAMEST);
		mHUDSet(HUDST);
		mControlsSet(CTRLST);
		mJoySet(JOYSTICKSETUP);
		mJoyKeySet(JOYKEYSET);
		mMouseSet(MOUSESETUP);
		mAdvancedMouseSet(ADVANCEDMOUSESET);
		mReSet(RESETSETUP);
		mReSetClassic(RESETCSETUP);
		mKeySet(KEYSET);
		mLoadGame(LOADGAME);
		mSaveGame(SAVEGAME);
		mQTitle(QUITTITLE);
		mDifficulty(DIFFICULTY);
		mNewGame(NEWGAME);
		mMultiplayer(MULTIPLAYER);
		mCreate(MCREATE);
		mJoin(MJOIN);
		mNetwork(NETWORKGAME);
		mColorMode(COLORCORR);
	}
	
	private static String levelEpisodePath(GameInfo game)
	{
//		if(game.isPackage())
//			return game.getFile().getPath() + ":" + ini.getName();
//		else 
		return game.resDir.getRelativePath();
	}
	
	private static void mReSetClassic(int nMenuId) {
		
		MenuTitle question = new MenuTitle("Do you really want reset to classic?", 1, 160, 90, -1);
		MenuVariants QuitVariants = new MenuVariants("[Y/N]", 1, 160, 99) {
			@Override
			public void positive() {

				for (int i = 0; i < keynames.length; i++)
					cfg.primarykeys[i] = defclassickeys[i];

				Arrays.fill(cfg.secondkeys, 0);
				Arrays.fill(cfg.mousekeys, 0);

				cfg.mousekeys[Weapon_Fire] = MOUSE_LBUTTON;
				cfg.mousekeys[Last_Weapon_Switch] = MOUSE_RBUTTON;
				cfg.mousekeys[Open] = MOUSE_MBUTTON;
				cfg.mousekeys[Next_Weapon] = MOUSE_WHELLUP;
				cfg.mousekeys[Previous_Weapon] = MOUSE_WHELLDN;
				
				mMenuBack();
			}
		};

		mAddItem(mMenus[nMenuId], question, false);
		mAddItem(mMenus[nMenuId], QuitVariants, true);
	}

	public static boolean mFromNetworkSetup() {
		return mCount > 0 && mMenuHistory[mCount - 1] == mMenus[NETWORKGAME];
	}
	
	private static void mColorMode(int nMenuId)
	{
		MenuTitle mTitle = new MenuTitle("Color correction", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 40;
		final MenuSlider mGamma = new MenuSlider("GAMMA:", 1, false, 43, pos += 12, 240, (int) ((1 - cfg.gamma) * 4096), 0, 4096, 64,
		new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSlider slider = (MenuSlider) pItem;
//				ud.brightness = slider.value;
//				engine.setbrightness(ud.brightness>>2, ps[myconnectindex].palette, 2);
				
				float gamma = slider.value / 4096.0f;
				if(((BGraphics)Gdx.graphics).setDisplayConfiguration(1 - gamma, cfg.brightness, cfg.contrast))
					cfg.gamma = (1 - gamma);
				else 
					slider.value = (int) ((1 - cfg.gamma) * 4096);
			}
		}, -1, -1, true);
		mGamma.digitalMax = 4096;
		
		final MenuSlider mBrightness = new MenuSlider("Brightness:", 1, false, 43, pos += 12, 240,(int) (cfg.brightness * 4096), -4096, 4096, 64,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						float brightness = slider.value / 4096.0f;
						if(((BGraphics)Gdx.graphics).setDisplayConfiguration(cfg.gamma, brightness, cfg.contrast))
							cfg.brightness = brightness;
						else 
							slider.value = (int) (cfg.brightness * 4096);
					}
				}, -1, -1, true);
		mBrightness.digitalMax = 4096;
		
		final MenuSlider mContrast = new MenuSlider("Contrast:", 1, false, 43, pos += 12, 240, (int) (cfg.contrast * 4096), 0, 8192, 64,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						float contrast = slider.value / 4096.0f;
						if(((BGraphics)Gdx.graphics).setDisplayConfiguration(cfg.gamma, cfg.brightness, contrast))
							cfg.contrast = contrast;
						else 
							slider.value = (int) (cfg.contrast * 4096);
					}
				}, -1, -1, true);
		mContrast.digitalMax = 4096;

		MenuButton mDefault = new MenuButton("Set to default", 2, 0, pos+=19, 320, 1, 0, null, -1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				cfg.gamma = 1.0f;
				cfg.brightness = 0.0f;
				cfg.contrast = 1.0f;
				
				mGamma.value = (int) ((1 - cfg.gamma) * 4096);
				mBrightness.value = (int) (cfg.brightness * 4096);
				mContrast.value = (int) (cfg.contrast * 4096);
				((BGraphics)Gdx.graphics).setDisplayConfiguration(cfg.gamma, cfg.brightness, cfg.contrast);
			}
		}, -1);
		
		mAddItem(mMenus[nMenuId], mGamma, true);
		mAddItem(mMenus[nMenuId], mBrightness, false);
		mAddItem(mMenus[nMenuId], mContrast, false);
		mAddItem(mMenus[nMenuId], mDefault, false);
	}
	
	private static void mNewGame(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("SELECT AN EPISODE", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);
		
		MENUPROC newEpProc = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuButton but = (MenuButton) pItem;
				mUserFlag = 0;
				mGameInfo = null;
				if (but.specialOpt > -1) {
					ud.m_volume_number = but.specialOpt;
					mSkilllist.clear();
					for (int i = 0; i < nMaxSkills; i++) {
						if(defGame.skillnames[i] != null) 
							mSkilllist.add(defGame.skillnames[i].toCharArray());
					}
				}
			}
		};

		int epnum = 0;
		int pos = 30;
		for(int i = 0; i < 2; i++)
		{
			if(defGame.episodes[i] != null) { //empty check
				MenuButton skill = new MenuButton(defGame.episodes[i].Title, 2, 0, pos+=19, 320, 1, 0, mMenus[DIFFICULTY], -1, newEpProc, i);
				mAddItem(mMenus[nMenuId], skill, i == 0);
				epnum++;
			}
		}
		
		if(RR66Game != null)
		{
			MenuButton skill = new MenuButton(RR66Game.Title, 2, 0, pos+=19, 320, 1, 0, mMenus[NEWUSERGAME], -1, new MENUPROC() {
				@Override
				public void run(MenuItem pItem) {
					mUserFlag = 1;
					updateUserEpisodeList(RR66Game);
					ud.m_level_number = 0;
					ud.m_volume_number = 0;
				}
			}, 0);
			mAddItem(mMenus[nMenuId], skill, epnum == 0);
			epnum++;
		}
		
		MenuButton mUser = new MenuButton("USER CONTENT", 2, 0, pos+=25, 320, 1, 2, mMenus[USERCONTENT], -1, null, -1);
		mAddItem(mMenus[nMenuId], mUser, epnum == 0);
	}
	
	private static void mDifficulty(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("SELECT SKILL", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);
		
		MENUPROC newGameProc = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
//				MenuButton button = (MenuButton) pItem;
//				if (button.specialOpt > -1)
//					ud.m_player_skill = button.specialOpt;
				MenuList button = (MenuList) pItem;
				ud.m_player_skill = button.l_nFocus;

                if(ud.m_player_skill == 3) ud.m_respawn_monsters = true;
                else ud.m_respawn_monsters = false;

                ud.m_monsters_off = ud.monsters_off = false;

                ud.m_respawn_items = false;
                ud.m_respawn_inventory = false;
                
                ud.warp_on = mUserFlag;

                if(ud.warp_on != 2)
                	ud.m_level_number = 0;
                
                DemoReset();
                mFakeMultiplayer = false;
                lastload = null;
                ud.multimode = 1;
                if(numplayers > 1)
                	NetDisconnect(myconnectindex);
                
                if (mGameInfo != null)
        			checkEpisodeResources(mGameInfo);
        		else
        			resetEpisodeResources();
                
                if(kGameCrash)
                	return;
                
                Source skillvoice = null;
				switch(ud.m_player_skill) {
					case 0: skillvoice = sound(427);break;
		            case 1: skillvoice = sound(428);break;
		            case 2: skillvoice = sound(196);break;
		            case 3: skillvoice = sound(195);break;
		            case 4: skillvoice = sound(197);break;
				}
				
				while(skillvoice != null && skillvoice.isActive());
                
        		if ( ud.warp_on == 1) 
        			Console.Println("Start user addon " + mGameInfo.Title, 0);
        		if ( ud.warp_on == 2) 
        			Console.Println("Start user map - " + boardfilename);
                
				newgame(ud.m_volume_number,ud.m_level_number,ud.m_player_skill+1);
				enterlevel(MODE_GAME);
				mClose();
			}
		};

		MenuList mSlot = new MenuList(mSkilllist, 2, 0, 59, 320, 1, 5, null, newGameProc, nMaxSkills);
		mAddItem(mMenus[nMenuId], mSlot, true);
	}
	
	private static void mSaveGame(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Save Game", 2, 160, 19, MENUBAR);
		final MenuPicnum mPicnum = new MenuPicnum(null, 0, 115, 107, TILE_LOADSHOT, LOADSCREEN);

		MenuTitle mInfo = new MenuTitle(lsInf.info, 0, 45, 145, -1) {
			@Override
			public void draw() {
				int ty = y;
				if (lsInf.date != null && !lsInf.date.isEmpty()) {

					mDrawText(textStyle, toCharArray(lsInf.date), x, ty, -128, 12, 0, 0);
					ty -= 10;
				}
				if (lsInf.info != null)
					mDrawText(textStyle, toCharArray(lsInf.info), x, ty, -128, 12, 0, 0);
			}
		};

		MENUPROC confirmCallback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSlotList item = (MenuSlotList) pItem;

				String filename;
				if (item.l_nFocus == 0) {
					int num = 0;
					do {
						if (num > 9999)
							return;
						filename = "game" + makeNum(num) + ".sav";
						if (Bcheck(FileUserdir + filename, "R") == null)
							break;

						num++;
					} while (true);
				} else
					filename = SaveManager.getSlot(item.l_nFocus - 1).filename;

				if (item.typed == null || item.typed.isEmpty())
					item.typed = filename;

				savegame(item.typed, filename);
				mClose();
			}
		};

		MENUPROC updateCallback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSlotList pSlot = (MenuSlotList) pItem;
				if (lsReadLoadData(pSlot.FileName()) != -1)
					mPicnum.nTile = TILE_LOADSHOT;
				else
					mPicnum.nTile = mPicnum.defTile;
			}
		};

		MenuSlotList mList = new MenuSlotList(0, 205, 60, 100, SaveManager.getList(), 10, 1, updateCallback,
				confirmCallback, true);
		mAddItem(mMenus[nMenuId], mTitle, false);
		mAddItem(mMenus[nMenuId], mPicnum, false);
		mAddItem(mMenus[nMenuId], mList, true);
		mAddItem(mMenus[nMenuId], mInfo, false);
	}
	
	private static void mLoadGame(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Load Game", 2, 160, 19, MENUBAR);
		final MenuPicnum mPicnum = new MenuPicnum(null, 0, 115, 107, TILE_LOADSHOT, LOADSCREEN);

		MENUPROC Proc = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				final MenuSlotList item = (MenuSlotList) pItem;
				final int oFlags = gm;
				mClose();
				
				setloading(null, 0, false);
				
				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						if (loadgame(item.FileName())) {
							if (gm == MODE_DEMO) {
								DemoReset();
								gm = MODE_GAME;
							}
						} else {
							gm = oFlags;
							if (gm == MODE_GAME || gm == MODE_DEMO) {
								if (!kGameCrash) 
									ready2send = true;
							} 
							addmessage("Incompatible version of saved game found!");
						}
					}
				});
			}
		};

		MenuTitle mInfo = new MenuTitle(lsInf.info, 0, 45, 145, -1) {
			@Override
			public void draw() {
				int ty = y;
				if (lsInf.date != null && !lsInf.date.isEmpty()) {

					mDrawText(textStyle, toCharArray(lsInf.date), x+1, ty+1, -128, 4, 0, 0);
					mDrawText(textStyle, toCharArray(lsInf.date), x, ty, -128, 12, 0, 0);
					ty -= 10;
				}
				if (lsInf.info != null) {
					mDrawText(textStyle, toCharArray(lsInf.info), x+1, ty+1, -128, 4, 0, 0);
					mDrawText(textStyle, toCharArray(lsInf.info), x, ty, -128, 12, 0, 0);
				}
			}
		};

		MENUPROC updateCallback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSlotList pSlot = (MenuSlotList) pItem;
				if (lsReadLoadData(pSlot.FileName()) != -1)
					mPicnum.nTile = TILE_LOADSHOT;
				else
					mPicnum.nTile = mPicnum.defTile;
			}
		};

		MenuSlotList mList = new MenuSlotList(0, 205, 60, 100, SaveManager.getList(), 10, 1, updateCallback, Proc, false);

		mAddItem(mMenus[nMenuId], mTitle, false);
		mAddItem(mMenus[nMenuId], mPicnum, false);
		mAddItem(mMenus[nMenuId], mList, true);
		mAddItem(mMenus[nMenuId], mInfo, false);
	}
	
	private static void mJoyKeySet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Config. buttons", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		MENUPROC callback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuJoyList item = (MenuJoyList) pItem;
				if (item.l_set == 0) {
					item.l_pressedId = -1;
					item.l_set = 1;
				} else if (item.l_set == 1) {
					switch (item.l_pressedId) {
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
						for (int kb = 0; kb < gpmanager.getButtonCount(0); kb++) {
							if (gpmanager.buttonPressed(kb))
								cfg.setButton(item.l_nFocus, kb);
						}
						item.l_set = 0;
						break;
					default:
						for (int kb = 0; kb < gpmanager.getButtonCount(0); kb++) {
							if (gpmanager.getButton(kb)) {
								cfg.setButton(item.l_nFocus, kb);
								item.l_set = 0;
							}
						}
						break;
					}
				}
				if (item.l_nFocus == Show_Console) {
					getInput().resetKeyStatus();
					Console.setCaptureKey(cfg.gpadkeys[Show_Console], 3);
				}
			}
		};

		MenuJoyList mList = new MenuJoyList(null, 0, 70, 40, 180, 9, keynames.length, callback);

		MenuTitle mText = new MenuTitle("UP/DOWN = Select action", 1, 160, 160, -1);
		MenuTitle mText2 = new MenuTitle("Enter = modify  DELETE = clear", 1, 160, 170, -1);

		mAddItem(mMenus[nMenuId], mList, true);
		mAddItem(mMenus[nMenuId], mText, false);
		mAddItem(mMenus[nMenuId], mText2, false);
	}
	
	private static void mKeySet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Configure Keys", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		MENUPROC callback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuKeyboardList item = (MenuKeyboardList) pItem;
				if (item.l_set == 0) {
					item.l_pressedId = -1;
					item.l_set = 1;
				} else if (item.l_set == 1) {
					int[] mousekeys = cfg.mousekeys;

					switch (item.l_pressedId) {
					case 2:
						cfg.setKey(item.l_nFocus, Keys.UP);
						item.l_set = 0;
						break;
					case 3:
						cfg.setKey(item.l_nFocus, Keys.DOWN);
						item.l_set = 0;
						break;
					case 4:
						cfg.setKey(item.l_nFocus, Keys.LEFT);
						item.l_set = 0;
						break;
					case 5:
						cfg.setKey(item.l_nFocus, Keys.RIGHT);
						item.l_set = 0;
						break;
					case 6:
						cfg.setKey(item.l_nFocus, Keys.ENTER);
						item.l_set = 0;
						break;
					case 7:
						getInput().resetKeyStatus();
						sound(EXITMENUSOUND);
						item.l_set = 0;
						break;
					case 8:
						cfg.setKey(item.l_nFocus, Keys.SPACE);
						item.l_set = 0;
						break;
					case 9:
						cfg.setKey(item.l_nFocus, Keys.BACKSPACE);
						item.l_set = 0;
						break;
					case 10:
						if (item.l_nFocus != Show_Console && item.l_nFocus != Menu_open)
							cfg.setKey(item.l_nFocus, Keys.FORWARD_DEL);
						item.l_set = 0;
						break;
					case 11:
						mousekeys[item.l_nFocus] = MOUSE_LBUTTON;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_LBUTTON == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case 12:
						cfg.setKey(item.l_nFocus, Keys.PAGE_UP);
						item.l_set = 0;
						break;
					case 13:
						cfg.setKey(item.l_nFocus, Keys.PAGE_DOWN);
						item.l_set = 0;
						break;
					case 14:
						cfg.setKey(item.l_nFocus, Keys.HOME);
						item.l_set = 0;
						break;
					case 15:
						cfg.setKey(item.l_nFocus, Keys.END);
						item.l_set = 0;
						break;
					case 16:
						mousekeys[item.l_nFocus] = MOUSE_WHELLUP;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_WHELLUP == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case 17:
						mousekeys[item.l_nFocus] = MOUSE_WHELLDN;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_WHELLDN == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					case 18:
						mousekeys[item.l_nFocus] = MOUSE_RBUTTON;
						for (int i = 0; i < mousekeys.length; i++) {
							if (i != item.l_nFocus && MOUSE_RBUTTON == mousekeys[i]) {
								mousekeys[i] = 0;
							}
						}
						item.l_set = 0;
						break;
					default:
						for (int kb = 0; kb < 256; kb++) {
							if (kb >= MOUSE_WHELLUP && kb <= MOUSE_BUTTON11) {
								if (getInput().getKey(kb) != 0) {
									mousekeys[item.l_nFocus] = kb;

									for (int i = 0; i < mousekeys.length; i++) {
										if (i != item.l_nFocus && kb == mousekeys[i])
											mousekeys[i] = 0;
									}

									item.l_set = 0;
								}
							} else if (getInput().getKey(kb) != 0) {
								cfg.setKey(item.l_nFocus, kb);
								item.l_set = 0;
							}
						}
						break;
					}
				}

				if (item.l_nFocus == Show_Console) {
					getInput().resetKeyStatus();
					Console.setCaptureKey(cfg.primarykeys[Show_Console], 0);
					Console.setCaptureKey(cfg.secondkeys[Show_Console], 1);
					Console.setCaptureKey(cfg.mousekeys[Show_Console], 2);
				}
			}
		};

		MenuKeyboardList mList = new MenuKeyboardList(null, 0, 30, 50, 200, 10, keynames.length, callback);

		MenuTitle mText = new MenuTitle("UP/DOWN = Select action", 1, 160, 160, -1);
		MenuTitle mText2 = new MenuTitle("Enter = modify  DELETE = clear", 1, 160, 170, -1);

		mAddItem(mMenus[nMenuId], mList, true);
		mAddItem(mMenus[nMenuId], mText, false);
		mAddItem(mMenus[nMenuId], mText2, false);
	}
	
	private static void mReSet(int nMenuId) {
		
		MenuTitle question = new MenuTitle("Do you really want to reset keys?", 1, 160, 90, -1);
		MenuVariants QuitVariants = new MenuVariants("[Y/N]", 1, 160, 99) {
			@Override
			public void positive() {

				for (int i = 0; i < keynames.length; i++)
					cfg.primarykeys[i] = defkeys[i];

				Arrays.fill(cfg.secondkeys, 0);
				Arrays.fill(cfg.mousekeys, 0);

				cfg.mousekeys[Weapon_Fire] = MOUSE_LBUTTON;
				cfg.mousekeys[Last_Weapon_Switch] = MOUSE_RBUTTON;
				cfg.mousekeys[Open] = MOUSE_MBUTTON;
				cfg.mousekeys[Next_Weapon] = MOUSE_WHELLUP;
				cfg.mousekeys[Previous_Weapon] = MOUSE_WHELLDN;
				
				mMenuBack();
			}
		};

		mAddItem(mMenus[nMenuId], question, false);
		mAddItem(mMenus[nMenuId], QuitVariants, true);
	}
	
	private static void mAdvancedMouseSet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Digital axis", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 40;
		char[][] keynameslist = new char[keynames.length + 1][];
		keynameslist[0] = "None".toCharArray();
		for (int i = 1; i < keynameslist.length; i++)
			keynameslist[i] = keynames[i - 1].toCharArray();

		pos += 12;
		MenuConteiner mAxisUp = new MenuConteiner("Digital up: ", 1, 46, pos += 12, 240, null, 0, null) {

			@Override
			public void open(MENU pMenu) {
				num = cfg.mouseaxis[AXISUP] + 1;
			}

			@Override
			public int callback(MENU pMenu, int opt) {
				switch (opt) {
				case 4:
				case 17:
					if (num > 0)
						num--;
					else
						num = 0;
					cfg.mouseaxis[AXISUP] = num - 1;
					return 0;
				case 5:
				case 16:
					if (num < list.length - 1)
						num++;
					else
						num = list.length - 1;
					cfg.mouseaxis[AXISUP] = num - 1;
					return 0;
				case 6:
				case 11:
					if (num < list.length - 1) {
						num++;
					} else
						num = 0;
					cfg.mouseaxis[AXISUP] = num - 1;
					return 0;
				default:
					return mNavigation(pMenu, opt);
				}
			}
		};

		MenuConteiner mAxisDown = new MenuConteiner("Digital down: ", 1, 46, pos += 12, 240, null, 0, null) {

			@Override
			public void open(MENU pMenu) {
				num = cfg.mouseaxis[AXISDOWN] + 1;
			}

			@Override
			public int callback(MENU pMenu, int opt) {
				switch (opt) {
				case 4:
				case 17:
					if (num > 0)
						num--;
					else
						num = 0;
					cfg.mouseaxis[AXISDOWN] = num - 1;
					return 0;
				case 5:
				case 16:
					if (num < list.length - 1)
						num++;
					else
						num = list.length - 1;
					cfg.mouseaxis[AXISDOWN] = num - 1;
					return 0;
				case 6:
				case 11:
					if (num < list.length - 1) {
						num++;
					} else
						num = 0;
					cfg.mouseaxis[AXISDOWN] = num - 1;
					return 0;
				default:
					return mNavigation(pMenu, opt);
				}
			}
		};

		MenuConteiner mAxisLeft = new MenuConteiner("Digital Left: ", 1, 46, pos += 12, 240, null, 0, null) {

			@Override
			public void open(MENU pMenu) {
				num = cfg.mouseaxis[AXISLEFT] + 1;
			}

			@Override
			public int callback(MENU pMenu, int opt) {
				switch (opt) {
				case 4:
				case 17:
					if (num > 0)
						num--;
					else
						num = 0;
					cfg.mouseaxis[AXISLEFT] = num - 1;
					return 0;
				case 5:
				case 16:
					if (num < list.length - 1)
						num++;
					else
						num = list.length - 1;
					cfg.mouseaxis[AXISLEFT] = num - 1;
					return 0;
				case 6:
				case 11:
					if (num < list.length - 1) {
						num++;
					} else
						num = 0;
					cfg.mouseaxis[AXISLEFT] = num - 1;
					return 0;
				default:
					return mNavigation(pMenu, opt);
				}
			}
		};

		MenuConteiner mAxisRight = new MenuConteiner("Digital Right: ", 1, 46, pos += 12, 240, null, 0, null) {

			@Override
			public void open(MENU pMenu) {
				num = cfg.mouseaxis[AXISRIGHT] + 1;
			}

			@Override
			public int callback(MENU pMenu, int opt) {
				switch (opt) {
				case 4:
				case 17:
					if (num > 0)
						num--;
					else
						num = 0;
					cfg.mouseaxis[AXISRIGHT] = num - 1;
					return 0;
				case 5:
				case 16:
					if (num < list.length - 1)
						num++;
					else
						num = list.length - 1;
					cfg.mouseaxis[AXISRIGHT] = num - 1;
					return 0;
				case 6:
				case 11:
					if (num < list.length - 1) {
						num++;
					} else
						num = 0;
					cfg.mouseaxis[AXISRIGHT] = num - 1;
					return 0;
				default:
					return mNavigation(pMenu, opt);
				}
			}
		};
		mAxisUp.list = mAxisDown.list = mAxisLeft.list = mAxisRight.list = keynameslist;

		mAddItem(mMenus[nMenuId], mAxisUp, true);
		mAddItem(mMenus[nMenuId], mAxisDown, false);
		mAddItem(mMenus[nMenuId], mAxisLeft, false);
		mAddItem(mMenus[nMenuId], mAxisRight, false);
	}
	
	private static void mMouseSet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Mouse Setup", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 23;

		MenuSwitch mEnable = new MenuSwitch("Enable mouse:", 1, 22, pos += 12, 280, cfg.useMouse, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.useMouse = sw.value;
			}
		}, "Yes", "No");

		pos += 5;
		MenuSlider mSens = new MenuSlider("Mouse Sensitivity:", 1, false, 22, pos += 12, 280, cfg.gSensitivity, 0x1000,
				0x28000, 512, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gSensitivity = slider.value;
					}
				}, -1, -1, true);
		mSens.digitalMax = 65536f;

		MenuSlider mTurn = new MenuSlider("Turning speed:", 1, false, 22, pos += 12, 280, cfg.gMouseTurnSpeed, 0,
				0x10000, 512, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseTurnSpeed = slider.value;
					}
				}, -1, -1, true);
		mTurn.digitalMax = 65536f;

		MenuSlider mLook = new MenuSlider("aiming up/down speed:", 1, false, 22, pos += 12, 280, cfg.gMouseLookSpeed, 0,
				0x10000, 512, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseLookSpeed = slider.value;
					}
				}, -1, -1, true);
		mLook.digitalMax = 65536f;

		MenuSlider mMove = new MenuSlider("Forward/Backward speed:", 1, false, 22, pos += 12, 280, cfg.gMouseMoveSpeed,
				0, 0x28000, 4096, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseMoveSpeed = slider.value;
					}
				}, -1, -1, true);
		mMove.digitalMax = 65536f;

		MenuSlider mStrafe = new MenuSlider("Strafing speed:", 1, false, 22, pos += 12, 280, cfg.gMouseStrafeSpeed, 0,
				0x28000, 4096, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseStrafeSpeed = slider.value;
					}
				}, -1, -1, true);
		mStrafe.digitalMax = 65536f;

		pos += 5;

		MenuSwitch mAiming = new MenuSwitch("Mouse aiming:", 1, 22, pos += 12, 280, cfg.gMouseAim, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gMouseAim = sw.value;
				ps[myconnectindex].aim_mode = cfg.gMouseAim?1:0;
			}
		}, null, null);
		MenuSwitch mInvert = new MenuSwitch("Invert mouse aim:", 1, 22, pos += 12, 280, cfg.gInvertmouse,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gInvertmouse = sw.value;
					}
				}, null, null);

		pos += 5;

		MenuButton mAdvance = new MenuButton("Digital axis setup", 1, 22, pos += 12, 280, 1, 0,
				mMenus[ADVANCEDMOUSESET], -1, null, 0);

		mAddItem(mMenus[nMenuId], mEnable, true);
		mAddItem(mMenus[nMenuId], mSens, false);

		mAddItem(mMenus[nMenuId], mTurn, false);
		mAddItem(mMenus[nMenuId], mLook, false);
		mAddItem(mMenus[nMenuId], mMove, false);
		mAddItem(mMenus[nMenuId], mStrafe, false);

		mAddItem(mMenus[nMenuId], mAiming, false);
		mAddItem(mMenus[nMenuId], mInvert, false);
		mAddItem(mMenus[nMenuId], mAdvance, false);
	}
	
	private static void mJoySet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Joystick Setup", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		MenuTitle mJoyName = new MenuTitle("No joystics detected", 1, 160, 40, -1) {
			@Override
			public void open(MENU pMenu) {
				int size = gpmanager.getControllers();
				if (size > 0) {
					if (size == 1)
						text = gpmanager.getControllerName(0).toCharArray();
					else
						text = (size + " controllers detected").toCharArray();
				} else
					text = "No joystics detected".toCharArray();
			}
		};
		mAddItem(mMenus[nMenuId], mJoyName, false);

		int pos = 40;
		MenuSwitch mEnable = new MenuSwitch("Enable joystick:", 1, 46, pos += 12, 230, cfg.useJoystick, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.useJoystick = sw.value;
			}
		}, "Yes", "No");

		MenuButton mJoyKey = new MenuButton("Configure buttons", 1, 46, pos += 15, 230, 1, 0, mMenus[JOYKEYSET], -1,
				null, 0);

		final char[][] StickName = { "Stick1_Y".toCharArray(), "Stick1_X".toCharArray(), "Stick2_Y".toCharArray(),
				"Stick2_X".toCharArray(), };

		MenuConteiner mJoyTurn = new MenuConteiner("Turn axis:", 1, 46, pos += 15, 230, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gJoyTurnAxis = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				num = cfg.gJoyTurnAxis;
			}
		};
		mJoyTurn.list = StickName;

		MenuConteiner mJoyLook = new MenuConteiner("Look axis:", 1, 46, pos += 12, 230, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gJoyLookAxis = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				num = cfg.gJoyLookAxis;
			}
		};
		mJoyLook.list = StickName;

		MenuConteiner mJoyStrafe = new MenuConteiner("Strafe axis:", 1, 46, pos += 12, 230, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gJoyStrafeAxis = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				num = cfg.gJoyStrafeAxis;
			}
		};
		mJoyStrafe.list = StickName;

		MenuConteiner mJoyMove = new MenuConteiner("Move axis:", 1, 46, pos += 12, 230, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gJoyMoveAxis = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				num = cfg.gJoyMoveAxis;
			}
		};
		mJoyMove.list = StickName;

		pos += 5;
		MenuSlider mDeadZone = new MenuSlider("Dead zone:", 1, false, 46, pos += 12, 230, cfg.gJoyDeadZone, 0, 0x8000,
				2048, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gJoyDeadZone = slider.value;
						gpmanager.setDeadZone(cfg.gJoyDeadZone / 65536f);
					}
				}, -1, -1, false);

		MenuSlider mLookSpeed = new MenuSlider("Look speed:", 1, false, 46, pos += 12, 230, cfg.gJoyLookSpeed, 0,
				0x140000, 4096, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gJoyLookSpeed = slider.value;
					}
				}, -1, -1, false);

		MenuSlider mTurnSpeed = new MenuSlider("Turn speed:", 1, false, 46, pos += 12, 230, cfg.gJoyTurnSpeed, 0,
				0x140000, 4096, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gJoyTurnSpeed = slider.value;
					}
				}, -1, -1, false);
		
		MenuSlider mSmoothing = new MenuSlider("Smoothing:", 1, false, 46, pos += 12, 230, cfg.gJoySmoothing,
				0,0x8000, 2048, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gJoySmoothing = slider.value;
						gpmanager.setSmoothing(cfg.gJoySmoothing);
					}
				}, -1, -1, false);

		MenuSwitch mInvert = new MenuSwitch("Invert look axis:", 1, 46, pos += 15, 230, cfg.gJoyInvert, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gJoyInvert = sw.value;
			}
		}, "Yes", "No");

		mAddItem(mMenus[nMenuId], mEnable, true);
		mAddItem(mMenus[nMenuId], mJoyKey, false);
		mAddItem(mMenus[nMenuId], mJoyTurn, false);
		mAddItem(mMenus[nMenuId], mJoyLook, false);
		mAddItem(mMenus[nMenuId], mJoyStrafe, false);
		mAddItem(mMenus[nMenuId], mJoyMove, false);
		mAddItem(mMenus[nMenuId], mDeadZone, false);
		mAddItem(mMenus[nMenuId], mLookSpeed, false);
		mAddItem(mMenus[nMenuId], mTurnSpeed, false);
		mAddItem(mMenus[nMenuId], mSmoothing, false);
		mAddItem(mMenus[nMenuId], mInvert, false);
	}
	
	private static void mControlsSet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Controls Setup", 2, 160, 19, MENUBAR);

		mAddItem(mMenus[nMenuId], mTitle, false);
		int pos = 39;

		MenuButton mMouseSet = new MenuButton("Mouse setup", 2, 0, pos += 20, 320, 1, 0, mMenus[MOUSESETUP], -1, null,0);
		MenuButton mJoySet = new MenuButton("Joystick setup", 2, 0, pos += 20, 320, 1, 0, mMenus[JOYSTICKSETUP], -1,null, 0);
		MenuButton mKeySet = new MenuButton("Configure Keys", 2, 0, pos += 20, 320, 1, 0, mMenus[KEYSET], -1, null, 0);
		MenuButton mKeyReset = new MenuButton("Reset to default", 2, 0, pos += 30, 320, 1, 0, mMenus[RESETSETUP], -1, null,0);
		MenuButton mKeyClassic = new MenuButton("Reset to classic", 2, 0, pos += 20, 320, 1, 0, mMenus[RESETCSETUP], -1, null,0);
		
		mAddItem(mMenus[nMenuId], mMouseSet, true);
		mAddItem(mMenus[nMenuId], mJoySet, false);
		mAddItem(mMenus[nMenuId], mKeySet, false);
		mAddItem(mMenus[nMenuId], mKeyReset, false);
		mAddItem(mMenus[nMenuId], mKeyClassic, false);
	}
	
	private static void mHUDSet(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Interface Setup", 2, 160, 19, MENUBAR);
		int pos = 30;

		MenuSwitch messages = new MenuSwitch("Messages:", 1, 47, pos += 12, 240, ud.fta_on==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.fta_on = sw.value?1:0;
			}
		}, null, null) {
			@Override
			public void open(MENU pMenu) {
				value = ud.fta_on == 1;
			}
		};

		MenuSlider sScreenSize = new MenuSlider("Screen size:", 1, false, 47, pos += 12, 240, ud.screen_size, 0, 4, 1,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						ud.screen_size = slider.value;
					}
				}, -1, -1, false);
		
//		MenuConteiner mMenuCursor = new MenuConteiner("Mouse cursor style:", 1, 47, pos += 12, 240, null, 0,
//		new MENUPROC() {
//			@Override
//			public void run(MenuItem pItem) {
//				MenuConteiner item = (MenuConteiner) pItem;
//				cfg.gMouseCursor = item.num;
//			}
//		}) {
//	@Override
//	public void open(MENU pMenu) {
//		if (this.list == null) {
//			this.list = new char[1][];
//			this.list[0] = "Arrow".toCharArray();
//		}
//		num = cfg.gMouseCursor;
//	}
//};
		MenuSwitch mMenuEnab = new MenuSwitch("Mouse in menu:", 1, 47, pos += 12, 240, cfg.menuMouse,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.menuMouse = sw.value;
					}
				}, "Yes", "No");
		MenuSlider mCurSize = new MenuSlider("Mouse cursor size:", 1, false, 47, pos += 12, 240, cfg.gMouseCursorSize,
				0x1000, 0x28000, 4096, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseCursorSize = slider.value;
					}
				}, -1, -1, false);
		
		pos += 5;
		MenuSwitch sCrosshair = new MenuSwitch("CROSSHAIR:", 1, 47, pos += 12, 240, ud.crosshair == 1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.crosshair = sw.value?1:0;
			}
		}, null, null) {
			@Override
			public void open(MENU pMenu) {
				value = ud.crosshair == 1;
			}
		};

		MenuSlider sCrossSize = new MenuSlider("Crosshair size:", 1, false, 47, pos += 12, 240, cfg.gCrossSize, 16384,
				65536, 8192, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gCrossSize = slider.value;
					}
				}, -1, -1, false);
		pos += 5;
		MenuConteiner sShowStat = new MenuConteiner("Statistics:", 1, 47, pos += 12, 240, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gShowStat = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "Always show".toCharArray();
					this.list[2] = "Only on a minimap".toCharArray();
				}
				num = cfg.gShowStat;
			}
		};

		MenuSlider sStatSize = new MenuSlider("Statistics size:", 1, false, 47, pos += 12, 240, cfg.gStatSize, 16384,
				65536, 8192, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gStatSize = slider.value;
					}
				}, -1, -1, false);
		pos += 5;

		MenuSwitch sShowMapName = new MenuSwitch("Info at level startup:", 1, 47, pos += 12, 240, cfg.showMapInfo==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.showMapInfo = sw.value?1:0;
			}
		}, null, null);

		MenuSwitch sShowFPS = new MenuSwitch("fps counter:", 1, 47, pos += 12, 240, cfg.gShowFPS, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShowFPS = sw.value;
			}
		}, null, null);

		mAddItem(mMenus[nMenuId], mTitle, false);

		mAddItem(mMenus[nMenuId], messages, true);
		mAddItem(mMenus[nMenuId], sScreenSize, false);
		mAddItem(mMenus[nMenuId], mMenuEnab, false);
//		mAddItem(mMenus[nMenuId], mMenuCursor, false);
		mAddItem(mMenus[nMenuId], mCurSize, false);
		mAddItem(mMenus[nMenuId], sCrosshair, false);
		mAddItem(mMenus[nMenuId], sCrossSize, false);
		mAddItem(mMenus[nMenuId], sShowStat, false);
		mAddItem(mMenus[nMenuId], sStatSize, false);
		mAddItem(mMenus[nMenuId], sShowMapName, false);
		mAddItem(mMenus[nMenuId], sShowFPS, false);
	}
	
	private static void mGameSet(int nMenuId)
	{
		MenuTitle mTitle = new MenuTitle("Game Setup", 2, 160, 19, MENUBAR);
		int pos = 40;

		MenuSwitch sSlopeTilt = new MenuSwitch("SCREEN TILTING:", 1, 46, pos += 12, 240, ud.screen_tilting==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.screen_tilting = sw.value?1:0;
			}
		}, null, null);

		MenuSwitch sAutoAim = new MenuSwitch("AutoAim:", 1, 46, pos += 12, 240, cfg.gAutoAim, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gAutoAim = sw.value;
				ps[myconnectindex].auto_aim = cfg.gAutoAim?1:0;
				if(numplayers > 1) getnames();
			}
		}, null, null);

		MenuSwitch sStartup = new MenuSwitch("Startup window:", 1, 46, pos += 12, 240, cfg.startup, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.startup = sw.value;
			}
		}, null, null);

		MenuSwitch sCheckVersion = new MenuSwitch("Check for updates:", 1, 46, pos += 12, 240, cfg.checkVersion,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.checkVersion = sw.value;
					}
				}, null, null);

		MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback:", 1, 46, pos += 12, 240, null, 0,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						cfg.gDemoSeq = item.num;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "In order".toCharArray();
					this.list[2] = "Randomly".toCharArray();
				}
				num = cfg.gDemoSeq;
			}
		};
		
		MenuSwitch sRecord = new MenuSwitch("Record demo:", 1, 46, pos += 12, 240, ud.m_recstat == 1,
		new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.m_recstat = sw.value?1:0;
			}
		}, null, null) {
			
			@Override
			public void open(MENU pMenu) {
				value = ud.m_recstat == 1;
				
				mCheckEnableItem(this, gm != MODE_GAME);
			}
		};

		mAddItem(mMenus[nMenuId], mTitle, false);
		mAddItem(mMenus[nMenuId], sSlopeTilt, true);
		mAddItem(mMenus[nMenuId], sAutoAim, false);
		mAddItem(mMenus[nMenuId], sStartup, false);
		mAddItem(mMenus[nMenuId], sCheckVersion, false);
		mAddItem(mMenus[nMenuId], mPlayingDemo, false);
		mAddItem(mMenus[nMenuId], sRecord, false);
	}
	
	private static void mVideoMode(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Video mode", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		MENUPROC callback = new MENUPROC() {
			public void run(MenuItem pItem) {
				cfg.fullscreen = isFullscreen ? 1 : 0;
				currentMode = choosedMode;
				setup3dscreen(choosedMode.xdim, choosedMode.ydim);
			}
		};

		int pos = 40;
		MenuConteiner mResolution = new MenuConteiner("Resolution: ", 1, 30,
				pos += 12, 280, strvmodes, 0, new MENUPROC() {
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						choosedMode = validmodes.get(item.num);
					}
				}) {
			
			public int callback(MENU pMenu, int opt) {
				switch (opt) {
				case 4:
					if ( (flags & 4) == 0 ) return 0;
					if (num > 0)
						num--;
					else
						num = 0;
					if (callback != null)
						callback.run(this);
					sound(KICK_HIT);
					return 0;
				case 5:
					if ( (flags & 4) == 0 ) return 0;
					if (num < list.length - 1)
						num++;
					else
						num = list.length - 1;
					if (callback != null)
						callback.run(this);
					sound(KICK_HIT);
					return 0;
				case 6:
				case 11:
					if (nextMenu != null)
						mOpen(nextMenu, -1);
					return 0;
				default:
					return mNavigation(pMenu, opt);
				}
			}

			
			public void open(MENU pMenu) {
				num = -1;
				for (int m = 0; m < validmodes.size(); m++) {
					if ((validmodes.get(m).xdim == xdim)
							&& (validmodes.get(m).ydim == ydim)) {
						num = m;
						break;
					}
				}
				if (num != -1) {
					currentMode = validmodes.get(num);
					choosedMode = currentMode;
				} else {
					currentMode = new VideoMode(Gdx.graphics.getDisplayMode());
				}
			}

			
			public void draw() {
				int px = x, py = y;
				char[] key;
				if (num != -1 && list != null)
					key = list[num];
				else
					key = new String(cfg.ScreenWidth + " x " + cfg.ScreenHeight + " 32bpp").toCharArray();
				
				int scale = 4096;
	
				int pal = this.pal;
				
				int shade = 8;
				boolean focused = mGetFocusedItem(m_pMenu, this);
			    if ( focused ) {
			    	shade = 8 - (totalclock & 0x3F);
			    }

			    mDrawText(textStyle, text, px, py, shade, pal, 0, 0);
				mGetAlign(textStyle, key);
				mDrawText(textStyle, key, x + width - 12 - alignx, y, shade, pal, 0, 0);
				
				int yoffset = -4;
				if(textStyle == 1) yoffset = -6;
				if(textStyle == 2) { yoffset = 6; scale = 8192; }
				if ( focused )
					engine.rotatesprite((x-10)<<16, (y - yoffset) << 16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
			}
		};
		mResolution.nextMenu = mMenus[RESOLUTIONST];

		MenuConteiner mRenderer = new MenuConteiner("Renderer: ", 1, 30,
				pos += 12, 280, new String[] { "Polymost" }, 0, null) {
			@Override
			public void draw() {
				super.draw();
				pal = 1;
			}
		};

		MenuSwitch mFullscreen = new MenuSwitch("Fullscreen:", 1, 30,
				pos += 12, 280, cfg.fullscreen == 1, new MENUPROC() {
					public void run(MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						isFullscreen = sw.value;
					}
				}, null, null) {
			
			public void open(MENU pMenu) {
				value = isFullscreen = (cfg.fullscreen == 1);
			}
		};
		
		MenuButton mApplyChanges = new MenuButton("Apply changes", 2, 0, pos += 20, 320, 1, 0, null, -1, callback, 0) {
			@Override
			public void draw() {
				super.draw();
				if (choosedMode != null && (choosedMode != currentMode || isFullscreen != (cfg.fullscreen == 1))) {
					flags = 7;
					pal = 0;
				} else {
					flags = 3;
					pal = 1;
				}
			}
		};

		mAddItem(mMenus[nMenuId], mResolution, true);
		mAddItem(mMenus[nMenuId], mRenderer, false);
		mAddItem(mMenus[nMenuId], mFullscreen, false);
		mAddItem(mMenus[nMenuId], mApplyChanges, false);
	}
	
	public static void mResolution(int nMenuId)
	{
		MenuTitle mTitle = new MenuTitle("Resolution", 2, 160, 19, MENUBAR);

		List<char[]> list = new ArrayList<char[]>();
		if (strvmodes != null) {
			for (int i = 0; i < strvmodes.length; i++)
				list.add(strvmodes[i].toCharArray());
		}

		MENUPROC callback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuList item = (MenuList) pItem;
				if (item.l_nFocus == -1)
					return;
				currentMode = choosedMode = validmodes.get(item.l_nFocus);
				setup3dscreen(choosedMode.xdim, choosedMode.ydim);
				mLoadRes(mMenuHistory[mCount - 1], 0x8000);
				mMenuBack();
			}
		};

		MenuList mSlot = new MenuResolutionList(list, 100, 50, 120, 1, 0, null, callback, 9);

		mAddItem(mMenus[nMenuId], mTitle, false);
		mAddItem(mMenus[nMenuId], mSlot, true);
	}
	
	private static int calcAnisotropy(int anisotropy) {
		int anisotropysize = 0;
		for (int s = anisotropy; s > 1; s >>= 1)
			anisotropysize++;
		return anisotropysize;
	}
	
	public static void mVideoSet(int nMenuId)
	{
		MenuTitle mTitle = new MenuTitle("Display Setup", 2, 160, 19, MENUBAR);
		MenuButton mVideoMode = new MenuButton("Video mode", 2, 0, 39, 320, 1, 0, mMenus[VIDEOMODES], -1, null, 0);
		
		MenuButton mColorMode = new MenuButton("Color correction", 2, 0, 60, 320, 1, 0, mMenus[COLORCORR], -1, null, 0);
		
		int pos = 75;
		MenuConteiner sFilter = new MenuConteiner("Texture mode:", 1, 47, pos += 12, 240, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				int filter = item.num;
				if (filter == 2)
					filter = 5;

				Console.Set("r_texturemode", filter);
				engine.render.gltexapplyprops();
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Classic".toCharArray();
					this.list[1] = "Bilinear".toCharArray();
					this.list[2] = "Trilinear".toCharArray();
				}

				int filter = Console.Geti("r_texturemode");
				if (filter == 5)
					filter = 2;
				num = filter;
			}
		};

		MenuConteiner sAnisotropy = new MenuConteiner("Anisotropy: ", 1, 47, pos += 12, 240, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				engine.setanisotropy(cfg, pow2long[item.num]);
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[calcAnisotropy((int) GLInfo.maxanisotropy) + 1][];
					this.list[0] = "None".toCharArray();
					for (int i = 1; i < list.length; i++)
						this.list[i] = (Integer.toString(pow2long[i]) + "x").toCharArray();
				}
				if (cfg.anisotropy > GLInfo.maxanisotropy)
					engine.setanisotropy(cfg, (int) GLInfo.maxanisotropy);
				num = calcAnisotropy(cfg.anisotropy);
			}
		};

		MenuSwitch sWidescreen = new MenuSwitch("Widescreen:", 1, 47, pos += 12, 240, cfg.widescreen == 1,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						engine.setwidescreen(cfg, sw.value);
					}
				}, null, null);

		MenuConteiner mMenuFPS = new MenuConteiner("Framerate limit:", 1, 47, pos += 12, 240, null, 0,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						
						int fps = 0;
						switch(item.num) {
							case 1: fps = 30; break;
							case 2: fps = 60; break;
							case 3: fps = 120; break;
							case 4: fps = 144; break;
						}
						cfg.fpslimit = fps;

						((BGraphics)Gdx.graphics).setMaxFramerate(fps);
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[5][];
					this.list[0] = "None".toCharArray();
					this.list[1] = "30 fps".toCharArray();
					this.list[2] = "60 fps".toCharArray();
					this.list[3] = "120 fps".toCharArray();
					this.list[4] = "144 fps".toCharArray();
				}
				
				num = cfg.checkFps(cfg.fpslimit);
				mCheckEnableItem(this, Gdx.graphics instanceof BGraphics);
			}
		};
		
		MenuSwitch sVSync = new MenuSwitch("VSync:", 1, 47, pos += 12, 240, cfg.gVSync, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gVSync = sw.value;
				try { // crash if hires textures loaded
					Gdx.graphics.setVSync(cfg.gVSync);
				} catch (Exception e) {
				}
			}
		}, null, null);

		pos += 5;
		MenuSwitch UseVoxels = new MenuSwitch("Voxels:", 1, 47, pos += 12, 240, usevoxels, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				usevoxels = sw.value;
			}
		}, null, null) {
			@Override
			public void open(MENU pMenu) {
				value = usevoxels;
			}
		};
		MenuSwitch UseModels = new MenuSwitch("3d models:", 1, 47, pos += 12, 240, usemodels, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				usemodels = sw.value;
			}
		}, null, null) {
			@Override
			public void open(MENU pMenu) {
				value = usemodels;
			}
		};
		MenuSwitch Usehrp = new MenuSwitch("True color textures:", 1, 47, pos += 12, 240, usehightile, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				usehightile = sw.value;
				engine.getrender().gltexinvalidateall(1);
			}
		}, null, null) {
			@Override
			public void open(MENU pMenu) {
				value = usehightile;
			}
		};

		mAddItem(mMenus[nMenuId], mTitle, false);
		mAddItem(mMenus[nMenuId], mVideoMode, true);
		mAddItem(mMenus[nMenuId], mColorMode, false);
		mAddItem(mMenus[nMenuId], sFilter, false);
		mAddItem(mMenus[nMenuId], sAnisotropy, false);
		mAddItem(mMenus[nMenuId], sWidescreen, false);
		mAddItem(mMenus[nMenuId], mMenuFPS, false);
		mAddItem(mMenus[nMenuId], sVSync, false);
		mAddItem(mMenus[nMenuId], UseVoxels, false);
		mAddItem(mMenus[nMenuId], UseModels, false);
		mAddItem(mMenus[nMenuId], Usehrp, false);
	}
	
	public static int snddriver;
	public static int middriver;
	public static int resampler;
	public static int osnddriver;
	public static int omiddriver;
	public static int oresampler;
	public static int voices;
	public static int ovoices;
	public static int cdaudio;
	public static int ocdaudio;
	
	private static void SndDriverDraw(MenuConteiner m)
	{
		int px = m.x, py = m.y;
		int shade = 8;
		char[] key = null;
		if(m.list != null && m.num != -1 && m.num < m.list.length) 
			key = m.list[m.num];	

		boolean focused = mGetFocusedItem(m.m_pMenu, m);
		if ( focused ) 
			shade = 8 - (totalclock & 0x3F);
		
		int pal = m.pal;

		int yoff = 0;
	    if(m.textStyle == 2) yoff = 13;
			
		mDrawText(1, m.text, px, py+yoff-3, shade, pal, 0, 0);
		if(key == null) return;
		
		mGetAlign(m.textStyle, key);
		mDrawText(m.textStyle, key, m.x + m.width - 1 - alignx, py+yoff, shade, pal, 0, 0);
		
		int scale = 4096;
		int yoffset = -4;
		if(m.textStyle == 1) yoffset = -6;
		if(m.textStyle == 2) { yoffset = 6 - yoff; scale = 8192; }
		if ( focused )
			engine.rotatesprite((m.x-10)<<16, (m.y - yoffset) << 16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
		
	}
	
	public static void mSounds(int nMenuId)
	{
		MenuTitle title = new MenuTitle("AUDIO SETUP", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], title, false);
		
		int posx = 37;
		int posy = 30;
		int width = 250;
		int style = 1;
		
		final MenuConteiner sSoundDrv = new MenuConteiner("Sound driver:", 0, posx, posy += 10, width, null, 0,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						snddriver = item.num;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[fxdrivers.length][];
					for (int i = 0; i < fxdrivers.length; i++)
						this.list[i] = fxdrivers[i].getName().toCharArray();
				}
				num = snddriver = osnddriver = cfg.snddrv;
				if (engine.getAudio().IsInited(SOUNDDRV)) {
					list[num] = fxdrivers[num].getName().toCharArray();
				} else
					list[num] = "initialization failed".toCharArray();
			}
			
			@Override
			public void draw() {
				SndDriverDraw(this);
			}
		};
		final MenuConteiner sMusicDrv = new MenuConteiner("Midi driver:", 0, posx, posy += 10, width, null, 0,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						middriver = item.num;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[mxdrivers.length][];
					for (int i = 0; i < mxdrivers.length; i++)
						this.list[i] = mxdrivers[i].getName().toCharArray();
				}
				num = middriver = omiddriver = cfg.middrv;
				if (engine.getAudio().IsInited(MUSICDRV)) {
					list[num] = mxdrivers[num].getName().toCharArray();
				} else
					list[num] = "initialization failed".toCharArray();
			}
			@Override
			public void draw() {
				SndDriverDraw(this);
			}
		};

		final MenuConteiner sResampler = new MenuConteiner("Resampler:", 0, posx, posy += 10, width, null, 0,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						resampler = item.num;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				if(this.list == null) {
					this.list = new char[engine.getAudio().getSound().getNumResamplers()][];
					for (int i = 0; i < list.length; i++)
						this.list[i] = engine.getAudio().getSound().getSoftResamplerName(i).toCharArray();
				}
				if(cfg.resampler_num < 0 || cfg.resampler_num >= engine.getAudio().getSound().getNumResamplers())
					cfg.resampler_num = 0;
				num = resampler = oresampler = cfg.resampler_num;
			}
			
			@Override
			public void draw() {
				SndDriverDraw(this);
			}
		};

		final MenuSlider sSound = new MenuSlider("SOUND VOLUME:", style, false, posx, 85, width, (int) (cfg.soundVolume * 256),
				0, 256, 16, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.soundVolume = slider.value / 256.0f;
						engine.getAudio().setVolume(SOUNDDRV, cfg.soundVolume);
					}
				}, -1, -1, false) {

			@Override
			public void open(MENU pMenu) {
				enable = cfg.SoundToggle && engine.getAudio().IsInited(SOUNDDRV);
				if (enable)
					flags = 7;
				else
					flags = 1;
			}
		};
		final MenuSlider sVoices = new MenuSlider("VOICES:", style, false, posx, 95, width, 0, 8, 256, 8, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSlider slider = (MenuSlider) pItem;
				voices = slider.value;
			}
		}, -1, -1, true) {
			@Override
			public void open(MENU pMenu) {
				value = voices = ovoices = cfg.NumVoices;
				enable = cfg.SoundToggle && engine.getAudio().IsInited(SOUNDDRV);
				if (enable)
					flags = 7;
				else
					flags = 1;
			}
		};

		MenuSwitch sSoundSwitch = new MenuSwitch("Sound:", style, posx, 75, width, cfg.SoundToggle, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.SoundToggle = sw.value;
				if (sw.value) {
					sVoices.enable = sSound.enable = true;
					sVoices.flags = sSound.flags = 7;
				} else {
					StopAllSounds();
					sVoices.enable = sSound.enable = false;
					sVoices.flags = sSound.flags = 1;
				}
			}
		}, null, null);

		final MenuSlider sMusic = new MenuSlider("MUSIC VOLUME:", style, false, posx, 125, width, (int) (cfg.musicVolume * 256), 0, 256, 8,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.musicVolume = slider.value / 256.0f;
						engine.getAudio().setVolume(MUSICDRV, cfg.musicVolume);				
					}
				}, -1, -1, false) {
			@Override
			public void open(MENU pMenu) {
				enable = cfg.MusicToggle && engine.getAudio().IsInited(MUSICDRV);
				if (enable)
					flags = 7;
				else
					flags = 1;
			}
		};

		MenuSwitch sMusicSwitch = new MenuSwitch("Music:", style, posx, 115, width, cfg.MusicToggle, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.MusicToggle = sw.value;
				if (!cfg.MusicToggle)
					engine.getAudio().setVolume(MUSICDRV, 0);	
				else
					engine.getAudio().setVolume(MUSICDRV, cfg.musicVolume);
				sMusic.enable = cfg.MusicToggle;
				if (sMusic.enable)
					sMusic.flags = 7;
				else
					sMusic.flags = 1;
			}
		}, null, null);

		MenuConteiner sMusicType = new MenuConteiner("Music type:", style, posx, 135, width, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				cdaudio = item.num;
			}
		}) {
			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "midi".toCharArray();
					this.list[1] = "external".toCharArray();
					this.list[2] = "cd audio".toCharArray();
				}
				cdaudio = ocdaudio = num = cfg.musicType;
			}
			
			@Override
			public void draw() {
				super.draw();
				mCheckEnableItem(this, cfg.MusicToggle);
			}
		};

		MENUPROC callback = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				if (snddriver != osnddriver || voices != ovoices || resampler != oresampler) {
					StopAllSounds();
					
					if (snddriver != osnddriver)
						engine.getAudio().setDriver(SOUNDDRV, fxdrivers[snddriver]);
					if (voices != ovoices)
						cfg.NumVoices = voices;
					if(resampler != oresampler)
						cfg.resampler_num = resampler;

					if (sndRestart(cfg.NumVoices, cfg.resampler_num)) {
						cfg.snddrv = osnddriver = snddriver;
						sSoundDrv.list[sSoundDrv.num] = fxdrivers[snddriver].getName().toCharArray();
						ovoices = voices;
						oresampler = resampler;
						
						sResampler.list = new char[engine.getAudio().getSound().getNumResamplers()][];
						for (int i = 0; i < sResampler.list.length; i++)
							sResampler.list[i] = engine.getAudio().getSound().getSoftResamplerName(i).toCharArray();
						if(cfg.resampler_num < 0 || cfg.resampler_num >= engine.getAudio().getSound().getNumResamplers())
							cfg.resampler_num = 0;
						sResampler.num = resampler = oresampler = cfg.resampler_num;
					} else
						sSoundDrv.list[sSoundDrv.num] = "initialization failed".toCharArray();
				}

				if (middriver != omiddriver) {
					engine.getAudio().setDriver(MUSICDRV, mxdrivers[middriver]);
					if (midRestart()) {
						cfg.middrv = omiddriver = middriver;
						sMusicDrv.list[sMusicDrv.num] = mxdrivers[sMusicDrv.num].getName().toCharArray();
					} else
						sMusicDrv.list[sMusicDrv.num] = "initialization failed".toCharArray();
				}

				if (cdaudio != ocdaudio) {
					cfg.musicType = cdaudio;
					ocdaudio = cdaudio;
				}

				if ((gm&MODE_GAME) != 0 || (gm&MODE_DEMO) != 0 || (gm&MODE_MENU) != 0) {
					sndStopMusic();
					if((gm&MODE_MENU) != 0) { sndPlayMusic(currentGame.getCON().env_music_fn[0]); } 
					else sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);
				}
			}
		};
		
		posy = 135;
		MenuButton mApplyChanges = new MenuButton("Apply changes", 2, 0, posy += 20, 320, 1, 0, null, -1, callback, 0) {
			@Override
			public void draw() {
				super.draw();
				if (snddriver != osnddriver || middriver != omiddriver || resampler != oresampler || voices != ovoices || cdaudio != ocdaudio) {
					flags = 7; pal = 0;
				} else {
					pal = 1; flags = 3;
				}
			}
		};

		mAddItem(mMenus[nMenuId], sSoundDrv, true);
		mAddItem(mMenus[nMenuId], sMusicDrv, false);
		mAddItem(mMenus[nMenuId], sResampler, false);
		mAddItem(mMenus[nMenuId], sSoundSwitch, false);
		mAddItem(mMenus[nMenuId], sSound, false);
		mAddItem(mMenus[nMenuId], sVoices, false);
		mAddItem(mMenus[nMenuId], sMusicSwitch, false);
		mAddItem(mMenus[nMenuId], sMusic, false);
		mAddItem(mMenus[nMenuId], sMusicType, false);
		mAddItem(mMenus[nMenuId], mApplyChanges, false);
	}
	
	public static void mOptions(int nMenuId)
	{
		MenuTitle title = new MenuTitle("Options", 2, 160, 19, MENUBAR) {
			@Override
			public void close(MENU pMenu) {
				saveConfig();
			}
		};
		
		mAddItem(mMenus[nMenuId], title, false);
		
		int pos = 35;
		MenuButton bGameSetup = new MenuButton("GAME SETUP", 2, 0, pos += 20, 320, 1, 0, mMenus[GAMEST], 1, null, 0);
		MenuButton bHUDSetup = new MenuButton("INTERFACE SETUP", 2, 0, pos += 20, 320, 1, 0, mMenus[HUDST], 1, null, 0);
		MenuButton bSoundSetup = new MenuButton("AUDIO SETUP", 2, 0, pos += 20, 320, 1, 0, mMenus[SOUNDST], 1, null, 0);
		MenuButton bVideoSetup = new MenuButton("VIDEO SETUP", 2, 0, pos += 20, 320, 1, 0, mMenus[VIDEOST], 1, null, 0);
		MenuButton bKeySetup = new MenuButton("CONTROL SETUP", 2, 0, pos += 20, 320, 1, 0,  mMenus[CTRLST], 1, null, 0);
		
		mAddItem(mMenus[nMenuId], bGameSetup, true);
		mAddItem(mMenus[nMenuId], bHUDSetup, false);
		mAddItem(mMenus[nMenuId], bSoundSetup, false);
		mAddItem(mMenus[nMenuId], bVideoSetup, false);
		mAddItem(mMenus[nMenuId], bKeySetup, false);
	}
	
	public static void mQuit(int nMenuId)
	{
		MenuTitle question = new MenuTitle("QUIT: You ain't done yet", 1, 160, 90, -1);
		MenuVariants QuitVariants = new MenuVariants("[Y/N]", 1, 160, 99) {
			@Override
			public void positive() {
				if (numplayers > 1)
					gNetDisconnect = true;
				
				gm |= MODE_END;
				mClose();
			}
		};
		
		mAddItem(mMenus[nMenuId], question, false);
		mAddItem(mMenus[nMenuId], QuitVariants, true);
	}
	
	public static void mHelp(int nMenuId)
	{
		MenuPage mPage1 = new MenuPage(0, 0, TEXTSTORY);
		mPage1.flags |= 10;
		MenuPage mPage2 = new MenuPage(0, 0, F1HELP);
		mPage2.flags |= 10;
		
		mAddItem(mMenus[nMenuId], mPage2, true);
		mAddItem(mMenus[nMenuId], mPage1, false);
	}
	
	public static void mCredits(int nMenuId)
	{
		MenuItem mPages[] = new MenuItem[24];

		mPages[0] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Original concept, design and direction");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Drew Markham");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		mPages[1] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Produced by");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Greg Goodrich");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		mPages[2] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Game programming");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Rafael PAIZ");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		mPages[3] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "ART Directors");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Claire Praderie     Maxx Raufman");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		
		mPages[4] = new MenuPage(160, 80, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Lead Level Designer");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Alex Mayberry");
				mDrawText(1, buf, x, y+10, 0, 0, 1, 0);
				buildString(buf, 0, "Level Design");
				mDrawText(1, buf, x, y+25, 0, 0, 1, 0);
				buildString(buf, 0, "Mal BlackWell");
				mDrawText(1, buf, x, y+35, 0, 0, 1, 0);
				buildString(buf, 0, "Sverre Kvernmo");
				mDrawText(1, buf, x, y+45, 0, 0, 1, 0);
			}
		};
		
		mPages[5] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Senior animatior and artist");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Jason Hoover");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		
		mPages[6] = new MenuPage(160, 90, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Technical Director");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Barry Dempsey");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
			}
		};
		
		mPages[7] = new MenuPage(160, 60, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Motion Capture Specialist and");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "character animation");
				mDrawText(1, buf, x, y+10, 0, 0, 1, 0);
				buildString(buf, 0, "Amit Doron");
				mDrawText(1, buf, x, y+20, 0, 0, 1, 0);
				
				buildString(buf, 0, "A.I. Programming");
				mDrawText(1, buf, x, y+35, 0, 0, 1, 0);
				buildString(buf, 0, "Arthur Donavan");
				mDrawText(1, buf, x, y+45, 0, 0, 1, 0);
				
				buildString(buf, 0, "Additional animation");
				mDrawText(1, buf, x, y+60, 0, 0, 1, 0);
				buildString(buf, 0, "George Karl");
				mDrawText(1, buf, x, y+70, 0, 0, 1, 0);
			}
		};
		
		mPages[8] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Character design");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Corkey Lehmkuhl");
				mDrawText(1, buf, x, y+10, 0, 0, 1, 0);
				
				buildString(buf, 0, "Map painters");
				mDrawText(1, buf, x, y+25, 0, 0, 1, 0);
				buildString(buf, 0, "Viktor Antonov");
				mDrawText(1, buf, x, y+35, 0, 0, 1, 0);
				buildString(buf, 0, "Matthias Beeguer");
				mDrawText(1, buf, x, y+45, 0, 0, 1, 0);
				buildString(buf, 0, "Stephan Burle");
				mDrawText(1, buf, x, y+55, 0, 0, 1, 0);
				
				buildString(buf, 0, "Sculptors");
				mDrawText(1, buf, x, y+70, 0, 0, 1, 0);
				buildString(buf, 0, "George Engel");
				mDrawText(1, buf, x, y+80, 0, 0, 1, 0);
				buildString(buf, 0, "Jake Garber");
				mDrawText(1, buf, x, y+90, 0, 0, 1, 0);
				buildString(buf, 0, "Jeff Himmel");
				mDrawText(1, buf, x, y+100, 0, 0, 1, 0);
			}
		};
		
		mPages[9] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Character voices");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Leonard");
				mDrawText(1, buf, x, y+15, 0, 0, 1, 0);
				buildString(buf, 0, "Burton Gilliam");
				mDrawText(1, buf, x, y+25, 0, 0, 1, 0);
				
				buildString(buf, 0, "Bubba. Billy Ray. Skinny Ol'Coot");
				mDrawText(1, buf, x, y+40, 0, 0, 1, 0);
				buildString(buf, 0, "and the Turd Minion");
				mDrawText(1, buf, x, y+50, 0, 0, 1, 0);
				buildString(buf, 0, "Drew Markham");
				mDrawText(1, buf, x, y+60, 0, 0, 1, 0);
				
				buildString(buf, 0, "Sheriff Lester T.Hobbes");
				mDrawText(1, buf, x, y+75, 0, 0, 1, 0);
				buildString(buf, 0, "Mojo Nixon");
				mDrawText(1, buf, x, y+85, 0, 0, 1, 0);
				buildString(buf, 0, "Alien Vixen");
				mDrawText(1, buf, x, y+100, 0, 0, 1, 0);
				buildString(buf, 0, "Peggy Jo Jacobs");
				mDrawText(1, buf, x, y+110, 0, 0, 1, 0);
			}
		};
		
		mPages[10] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				buildString(buf, 0, "Sound Design");
				mDrawText(1, buf, x, y, 0, 0, 1, 0);
				buildString(buf, 0, "Gary Bradfield");
				mDrawText(1, buf, x, y+10, 0, 0, 1, 0);
				
				buildString(buf, 0, "Music");
				mDrawText(1, buf, x, y+25, 0, 0, 1, 0);
				buildString(buf, 0, "Mojo Nixon");
				mDrawText(1, buf, x, y+35, 0, 0, 1, 0);
				buildString(buf, 0, "The Beat Farmers");
				mDrawText(1, buf, x, y+45, 0, 0, 1, 0);
				buildString(buf, 0, "The reverend Horton Heat");
				mDrawText(1, buf, x, y+55, 0, 0, 1, 0);
				buildString(buf, 0, "Cement Pond");
				mDrawText(1, buf, x, y+65, 0, 0, 1, 0);
				
				buildString(buf, 0, "Additional Sound Effects");
				mDrawText(1, buf, x, y+80, 0, 0, 1, 0);
				buildString(buf, 0, "Jim Spurgin");
				mDrawText(1, buf, x, y+90, 0, 0, 1, 0);
			}
		};
		
		mPages[11] = new MenuPage(160, 70, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Motion capture actor");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "J.P. Manoux");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Motion capture vixen");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Shawn Wolfe");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[12] = new MenuPage(160, 40, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Production Assistance");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Minerva Mayberry");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Nuts and bolts");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Steve Goldberg");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Marcus Hutchinson");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Bean Counting");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Max Yoshikawa");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Administrative Assistance");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Serafin Lewis");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[13] = new MenuPage(160, 60, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Location Manager. Louisiana");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Rick Skinner");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Location Scout. Louisiana");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Brian Benos");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Photographer");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Carlos Serrao");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[14] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Additional 3D modelling by");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "3 name 3D");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "viewpoint datalabs international");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Audio Recorded at");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Pacific ocean pos. Santa Monica. C.A");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Cement pond tracks recorded at");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Dreamstate recording. Burbank. C.A.");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Recording engeneer");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Dave Ahlert");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[15] = new MenuPage(160, 70, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "3D Build Engine licensed from");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "3D Realms Intertainment");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Build Engine and related tools");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Created by Ken Silverman");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[16] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "For Interplay");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				
				buildString(buf, 0, "Lead tester");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Darrell Jones");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Testers");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Tim Anderson");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Erick Lujan");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Tien Tran");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[17] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Is techs");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Bill Delk");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Aaron Meyers");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Compatibility techs");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Marc Duran");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Dan Forsyth");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Derek Gibbs");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Aaron Olaiz");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Jack Parker");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[18] = new MenuPage(160, 60, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Director of compatibility");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Phuong Nguyen");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Assistant QA Director");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Colin Totman");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "QA Director");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Chad Allison");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[19] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Interplay Producer");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Bill Dugan");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Interplay Line Produces");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Chris Benson");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Product Manager");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Jim Veevaert");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Public Relations");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Erika Price");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[20] = new MenuPage(160, 50, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Special thanks");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Jim Gauger");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Paul Vais");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Scott Miller");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Todd Replogle");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Chuck Bueche");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Carter Lipscomb");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "John Conley");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Don Maggi");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[21] = new MenuPage(160, 80, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Extra special thanks");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Brian Fargo");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[22] = new MenuPage(160, 70, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Redneck Rampage");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "(C) 1997 Xatrix Entertainment, inc.");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				pos += 5;
				buildString(buf, 0, "Redneck Rampage is a Trademark of");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "Interplay productions");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
		
		mPages[23] = new MenuPage(160, 80, -1) {
			@Override
			public void draw() {
				int pos = y;
				buildString(buf, 0, "Redneck Rampage GDX");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
				buildString(buf, 0, "(C) 2018 by [M210] (http://m210.duke4.net)");
				mDrawText(1, buf, x, pos += 10, 0, 0, 1, 0);
			}
		};
				
	
		mAddItem(mMenus[nMenuId], mPages[mPages.length - 1], true);
		mPages[mPages.length - 1].flags |= 10;
		for(int i = 0; i < mPages.length - 1; i++) {
			mAddItem(mMenus[nMenuId], mPages[i], false);
			mPages[i].flags |= 10;
		}
	}
	
	public static void mMain(int nMenuId)
	{
		MenuPicnum bLogo = new MenuPicnum(null, 0, 160, 28, INGAMELNRDTHREEDEE, INGAMELNRDTHREEDEE) {
			@Override
			public void draw() {
				if (currentGame.getCON().type == RRRA)
					engine.rotatesprite(x<<16,(y+27)<<16,16384,0,1686,(sintable[(totalclock<<4)&2047]>>11),0,2+8,0,0,xdim-1,ydim-1);
				engine.rotatesprite(x << 16, y << 16, 24000, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			}
		};
		
		int pos = 56;
		MenuButton bNewgame = new MenuButton("NEW GAME", 2, 0, pos, 320, 1, 0, mMenus[NEWGAME], -1, null, 0);
		MenuButton bMultiplayer = new MenuButton("MULTIPLAYER", 2, 0, pos += 18, 320, 1, 0, mMenus[MULTIPLAYER], 1, null, 0) {
			@Override
			public void open(MENU pMenu) {
				if (numplayers > 1)
					nextMenu = mMenus[NETWORKGAME];
				else
					nextMenu = mMenus[MULTIPLAYER];
			}
		};
		
		MenuButton bOptions = new MenuButton("OPTIONS", 2, 0, pos += 18, 320, 1, 0, mMenus[OPTIONS], 1, null, 0);
		MenuButton bLoad = new MenuButton("LOAD GAME", 2, 0, pos += 18, 320, 1, 0, mMenus[LOADGAME], 1, null, 0) {
			@Override
			public void open(MENU pMenu) {
				mCheckEnableItem(this, numplayers < 2 && !mFakeMultiplayer);
			}
		};
		MenuButton bHelp = new MenuButton("HELP", 2, 0, pos += 18, 320, 1, 0, mMenus[HELP], 1, null, 0);
		MenuButton bCredits = new MenuButton("CREDITS", 2, 0, pos += 18, 320, 1, 0, mMenus[CREDITS], 1, null, 0);
		MenuButton bQuit = new MenuButton("QUIT", 2, 0, pos += 18, 320, 1, 0, mMenus[QUIT], 1, null, 0);
		
		mAddItem(mMenus[nMenuId], bLogo, false);
		mAddItem(mMenus[nMenuId], bNewgame, true);
		mAddItem(mMenus[nMenuId], bMultiplayer, false);
		mAddItem(mMenus[nMenuId], bOptions, false);
		mAddItem(mMenus[nMenuId], bLoad, false);
		mAddItem(mMenus[nMenuId], bHelp, false);
		mAddItem(mMenus[nMenuId], bCredits, false);
		mAddItem(mMenus[nMenuId], bQuit, false);
	}
	
	private static void mQTitle(int nMenuId) {
		
		MenuTitle question = new MenuTitle("Quit to title?", 1, 160, 90, -1);
		MenuVariants QuitVariants = new MenuVariants("[Y/N]", 1, 160, 99) {
			@Override
			public void positive() {
				if (numplayers > 1 || ud.multimode > 1)
					gNetDisconnect = true;
				else backtomenu();
				mClose();
			}
		};
		
		mAddItem(mMenus[nMenuId], question, false);
		mAddItem(mMenus[nMenuId], QuitVariants, true);
	}
	
	public static void mGame(int nMenuId)
	{
		MenuPicnum bLogo = new MenuPicnum(null, 0, 160, 28, INGAMELNRDTHREEDEE, INGAMELNRDTHREEDEE) {
			@Override
			public void draw() {
				if (currentGame.getCON().type == RRRA)
					engine.rotatesprite(x<<16,(y+27)<<16,16384,0,1686,(sintable[(totalclock<<4)&2047]>>11),0,2+8,0,0,xdim-1,ydim-1);
				engine.rotatesprite(x << 16, y << 16, 24000, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			}
		};
		
		MENUPROC mScreenCapture = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				gScreenCapture = true;
			}
		};
		
		int pos = 56;
		MenuButton bNewgame = new MenuButton("NEW GAME", 2, 0, pos, 320, 1, 0, mMenus[NEWGAME], 1, null, 0) {
			@Override
			public void open(MENU pMenu) {
				nextMenu = mMenus[NEWGAME];
				if (numplayers > 1 || mFakeMultiplayer)
					nextMenu = mMenus[NETWORKGAME];
			}
		};
		
		MenuButton bSave = new MenuButton("SAVE GAME", 2, 0, pos += 18, 320, 1, 0, mMenus[SAVEGAME], 1, mScreenCapture, 0)
		{
			@Override
			public void open(MENU pMenu) {
				mCheckEnableItem(this, numplayers < 2 && !mFakeMultiplayer);
			}
		};
		
		MenuButton bLoad = new MenuButton("LOAD GAME", 2, 0, pos += 18, 320, 1, 0, mMenus[LOADGAME], 1, null, 0) 
		{
			@Override
			public void open(MENU pMenu) {
				mCheckEnableItem(this, numplayers < 2 && !mFakeMultiplayer);
			}
		};
		
		MenuButton bOptions = new MenuButton("OPTIONS", 2, 0, pos += 18, 320, 1, 0, mMenus[OPTIONS], 1, null, 0);
		MenuButton bHelp = new MenuButton("HELP", 2, 0, pos += 18, 320, 1, 0, mMenus[HELP], 1, null, 0);
		MenuButton bQuitTitle = new MenuButton("QUIT TO TITLE", 2, 0, pos += 18, 320, 1, 0, mMenus[QUITTITLE], 1, null, 0);
		MenuButton bQuit = new MenuButton("QUIT GAME", 2, 0, pos += 18, 320, 1, 0, mMenus[QUIT], 1, null, 0);
		
		mAddItem(mMenus[nMenuId], bLogo, false);
		mAddItem(mMenus[nMenuId], bNewgame, true);
		mAddItem(mMenus[nMenuId], bSave, false);
		mAddItem(mMenus[nMenuId], bLoad, false);
		mAddItem(mMenus[nMenuId], bOptions, false);
		mAddItem(mMenus[nMenuId], bHelp, false);
		mAddItem(mMenus[nMenuId], bQuitTitle, false);
		mAddItem(mMenus[nMenuId], bQuit, false);
	}
	
	private static void mUserContent(int nMenuId, MENUPROC UserProc) {
		MenuTitle mTitle = new MenuTitle("User Content", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		MenuFileBrowser mSlot = new MenuFileBrowser(0, 60, 45, 200, 0, UserProc, 13);

		mAddItem(mMenus[nMenuId], mSlot, true);
	}
	
	private static void mMultiplayer(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Multiplayer", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 45;
		MenuButton mCreate = new MenuButton("New game", 2, 0, pos += 20, 320, 1, 0, mMenus[MCREATE], -1, null, 0);
		MenuButton mJoin = new MenuButton("Join a game", 2, 0, pos += 20, 320, 1, 0, mMenus[MJOIN], -1, null, 0);
		// splitscreen game
		// end game XXX

		mAddItem(mMenus[nMenuId], mCreate, true);
		mAddItem(mMenus[nMenuId], mJoin, false);
	}
	
	private static int mPlayers = 2;
	private static String mContent = "None";
	private static boolean mEpisodeUpdateRequest = false;

	private static void mCreate(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Multiplayer", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 45;
		MenuSlider mPlayerNum = new MenuSlider("Number of players:", 1, false, 46, pos += 12, 240, mPlayers, 1, 8, 1, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						mPlayers = slider.value;
					}
				}, -1, -1, true);

		MenuTextField mPortnum = new MenuTextField("Network number:", "" + cfg.mPort, 1, 46, pos += 12, 240,
				NUMBERS, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						cfg.mPort = Integer.parseInt(item.typed);
					}
				});

		MenuTextField mPlayer = new MenuTextField("Player name:", cfg.pName, 1, 46, pos += 12, 240, NUMBERS | LETTERS,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						cfg.pName = item.typed;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				Arrays.fill(typingBuf, (char) 0);
				inputlen = cfg.pName.length();
				System.arraycopy(toCharArray(cfg.pName), 0, typingBuf, 0, inputlen);
			}
		};
		
		MenuSwitch mMenuFakeMM = new MenuSwitch("Fake multiplayer", 1, 46, pos += 12, 240, false, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				mUseFakeMultiplayer = sw.value;
			}
		}, "Yes", "No") {
			@Override
			public void open(MENU pMenu) {
				value = mUseFakeMultiplayer;
			}
		};
		
		MenuSwitch mMenuBots = new MenuSwitch("Bots", 1, 46, pos += 12, 240, ud.playerai==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.playerai = sw.value?1:0;
			}
		}, "Yes", "No") {
			
			@Override
			public void draw() {
				super.draw();
				value = ud.playerai==1;
				mCheckEnableItem(this, mPlayers > 1 && mUseFakeMultiplayer);
			}
		};

		MenuButton mCreate = new MenuButton("Create", 2, 0, pos += 25, 320, 1, 0, null, -1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				gNetParam = new String[] { "-n0" + (mPlayers != 2 ? (":" + mPlayers) : ""),
						(cfg.mPort != NETPORT ? ("-p " + cfg.mPort) : null) };

				if (gm == MODE_DEMO)
					DemoReset();

				mResetContent();
				
				if (mPlayers == 1 || mUseFakeMultiplayer) {
					getnames();
					mFakeMultiplayer = true;
					ud.multimode = mPlayers;
					gm = MODE_MENU;
					mOpen(mMenus[NETWORKGAME], -1);
				} else {
					mFakeMultiplayer = false;
					StartMultiplayer(gNetCreate);
				}
			}
		}, 0);

		mAddItem(mMenus[nMenuId], mPlayerNum, true);
		mAddItem(mMenus[nMenuId], mPortnum, false);
		mAddItem(mMenus[nMenuId], mPlayer, false);
		mAddItem(mMenus[nMenuId], mMenuFakeMM, false);
		mAddItem(mMenus[nMenuId], mMenuBots, false);
		mAddItem(mMenus[nMenuId], mCreate, false);
	}
	
	private static void mJoin(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("Join a game", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);

		int pos = 45;
		MenuTextField mPortnum = new MenuTextField("Network socket:", "" + cfg.mPort, 1, 46, pos += 12, 240,
				NUMBERS, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						cfg.mPort = Integer.parseInt(item.typed);
					}
				});

		MenuTextField mPlayer = new MenuTextField("Player name:", cfg.pName, 1, 46, pos += 12, 240, NUMBERS | LETTERS,
				new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						cfg.pName = item.typed;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				Arrays.fill(typingBuf, (char) 0);
				inputlen = cfg.pName.length();
				System.arraycopy(toCharArray(cfg.pName), 0, typingBuf, 0, inputlen);
			}
		};

		MenuTextField mIPAddress = new MenuTextField("IP Address:", cfg.mAddress, 1, 46, pos += 12, 240,
				NUMBERS | LETTERS | SYMBOLS, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						cfg.mAddress = item.typed;
					}
				}) {
			@Override
			public void open(MENU pMenu) {
				Arrays.fill(typingBuf, (char) 0);
				inputlen = cfg.mAddress.length();
				System.arraycopy(toCharArray(cfg.mAddress), 0, typingBuf, 0, inputlen);
			}
		};

		MenuButton mConnect = new MenuButton("Connect", 2, 0, pos += 20, 320, 1, 0, null, -1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				if (cfg.mAddress.isEmpty())
					return;
				gNetParam = new String[] { "-n0", cfg.mAddress, (cfg.mPort != NETPORT ? ("-p " + cfg.mPort) : null) };

				mResetContent();
				if (gm == MODE_DEMO)
					DemoReset();

				StartMultiplayer(gNetConnect);
			}
		}, 0);

		mAddItem(mMenus[nMenuId], mPortnum, true);
		mAddItem(mMenus[nMenuId], mPlayer, false);
		mAddItem(mMenus[nMenuId], mIPAddress, false);
		mAddItem(mMenus[nMenuId], mConnect, false);
	}
	
	private static void mNetwork(int nMenuId) {
		MenuTitle mTitle = new MenuTitle("NETWORK GAME", 2, 160, 19, MENUBAR);
		mAddItem(mMenus[nMenuId], mTitle, false);
		
		final MenuConteiner pItem = new MenuConteiner("Content", 2, 20,
				45, 280, new String[] { mContent }, 0, new MENUPROC() {
					@Override
					public void run(MenuItem pItem) {
						mOpen(mMenus[USERCONTENT], -1);
					}
				}) {

			@Override
			public void draw() {
				super.draw();
				this.list[0] = mContent.toCharArray();
			}
		};
		
		MenuConteiner mMenuGame = new MenuConteiner("GAME TYPE", 1, 20, 70, 280, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				ud.m_coop = item.num;
			}
		}) {

			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "DUKEMATCH (SPAWN)".toCharArray();
					this.list[1] = "COOPERATIVE PLAY".toCharArray();
					this.list[2] = "DUKEMATCH (NO SPAWN)".toCharArray();
				}
				num = ud.m_coop;
			}
		};
		
		final MENUPROC mLevelsUpdate = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;

				int size = mGameInfo.episodes[ud.m_volume_number].nMaps;
				if (item.list == null || item.list.length != size)
					item.list = new char[size][];
				for (int i = 0; i < size; i++)
					item.list[i] =  mGameInfo.episodes[ud.m_volume_number].gMapInfo[i].title.toCharArray();
				ud.m_level_number = item.num = 0;
			}
		};

		final MenuConteiner mMenuLevel = new MenuConteiner("LEVEL", 1, 20, 90, 280, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				if(item.num > mGameInfo.episodes[ud.m_volume_number].nMaps) 
					item.num = mGameInfo.episodes[ud.m_volume_number].nMaps;
				ud.m_level_number = item.num;
			}
		}) {

			@Override
			public void open(MENU pMenu) {
				if (this.list == null)
					mLevelsUpdate.run(this);
				num = ud.m_level_number;
			}

			@Override
			public void draw() {
				super.draw();
				mCheckEnableItem(this, mUserFlag != 2);
			}
		};
		
		MenuConteiner mMenuEpisode = new MenuConteiner("EPISODE", 1, 20, 80, 280, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				if(item.num > mGameInfo.nEpisodes) item.num = mGameInfo.nEpisodes;
				ud.m_volume_number = item.num;
				mLevelsUpdate.run(mMenuLevel);
			}
		}) {

			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					updateUserEpisodeList(mGameInfo);
					int size = mEpisodelist.size();
					this.list = new char[size][];
					for (int i = 0; i < size; i++) 
						this.list[i] = mEpisodelist.get(i);
				}
				mCheckEnableItem(this, mUserFlag != 2);
				num = ud.m_volume_number;
			}
			
			@Override
			public void draw() {
				super.draw();
				mCheckEnableItem(this, mUserFlag != 2);
				
				if (mEpisodeUpdateRequest) {
					mLevelsUpdate.run(mMenuLevel);

					int size = mEpisodelist.size();
					if (this.list == null || this.list.length != size)
						this.list = new char[size][];

					for (int i = 0; i < size; i++) {
						this.list[i] = mEpisodelist.get(i);
					}
					num = 0;
					mEpisodeUpdateRequest = false;
				}
			}
		};
		
		int pos = 90;
		MenuConteiner mMenuDifficulty = new MenuConteiner("MONSTERS", 1, 20, pos += 12, 280, null, 0, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				if(item.num == 0)
				{
					ud.m_monsters_off = true;
					ud.m_player_skill = 0;
				} else {
					ud.m_monsters_off = false;
					ud.m_player_skill = item.num-1;
				}
			}
		}) {

			@Override
			public void open(MENU pMenu) {
				if (this.list == null) {
					this.list = new char[5][];
					this.list[0] = "NONE".toCharArray();
					for(int i = 0; i < 4; i++)
						this.list[1 + i] = mGameInfo.skillnames[i].toCharArray();
				}
				num = ud.m_player_skill;
			}
		};
		
		MenuSwitch mMenuMarkers = new MenuSwitch("MARKERS", 1, 20, pos += 12, 280, ud.m_marker==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.m_marker = sw.value?1:0;
			}
		}, "Yes", "No") {
			
			@Override
			public void draw() {
				super.draw();
				mCheckEnableItem(this, ud.m_coop == 0);
			}
		};
		
		MenuSwitch mMenuFFire = new MenuSwitch("FRIENDLY FIRE", 1, 20, pos += 12, 280, ud.m_ffire==1, new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.m_ffire = sw.value?1:0;
			}
		}, "Yes", "No") {
			
			@Override
			public void draw() {
				super.draw();
				mCheckEnableItem(this, ud.m_coop == 1);
			}
		};
		
		final MENUPROC mNetStart = new MENUPROC() {
			@Override
			public void run(MenuItem pItem) {
				
				ud.warp_on = mUserFlag;

				tempbuf[0] = kPacketLevelStart;
                tempbuf[1] = (byte)ud.m_level_number;
                tempbuf[2] = (byte)ud.m_volume_number;
                tempbuf[3] = (byte)(ud.m_player_skill);

                if( ud.m_player_skill == 3 ) ud.m_respawn_monsters = true;
                else ud.m_respawn_monsters = false;

                if(ud.m_coop == 0) ud.m_respawn_items = true;
                else ud.m_respawn_items = false;

                ud.m_respawn_inventory = true;

                tempbuf[4] = ud.m_monsters_off?(byte)1:0;
                tempbuf[5] = ud.m_respawn_monsters?(byte)1:0;
                tempbuf[6] = ud.m_respawn_items?(byte)1:0;
                tempbuf[7] = ud.m_respawn_inventory?(byte)1:0;
                tempbuf[8] = (byte)ud.m_coop;
                tempbuf[9] = (byte)ud.m_marker;
                tempbuf[10] = (byte)ud.m_ffire;

                for(int c=connecthead;c>=0;c=connectpoint2[c])
                {
                    resetweapons(c);
                    resetinventory(c);
				}
                
                sendtoall(tempbuf,11);
                
                if (mGameInfo != null)
        			checkEpisodeResources(mGameInfo);
        		else
        			resetEpisodeResources();
                
        		if ( ud.warp_on == 1) 
        			Console.Println("Start user addon " + mGameInfo.Title, 0);
        		
        		if ( ud.warp_on == 2) 
        			Console.Println("Start user map - " + boardfilename);
                
				newgame(ud.m_volume_number,ud.m_level_number,ud.m_player_skill);
                enterlevel(MODE_GAME);
                mClose();
			}
		};
		
		MenuButton mStart = new MenuButton("START GAME", 2, 20, pos += 15, 280, 1, 0, null, -1, mNetStart, 0) {
			@Override
			public void open(MENU pMenu) {
				mCheckEnableItem(this, myconnectindex == connecthead);
			}
		};
		
		mAddItem(mMenus[nMenuId], pItem, true);
		mAddItem(mMenus[nMenuId], mMenuGame, false);
		mAddItem(mMenus[nMenuId], mMenuEpisode, false);
		mAddItem(mMenus[nMenuId], mMenuLevel, false);
		mAddItem(mMenus[nMenuId], mMenuDifficulty, false);
		mAddItem(mMenus[nMenuId], mMenuMarkers, false);
		mAddItem(mMenus[nMenuId], mMenuFFire, false);
		mAddItem(mMenus[nMenuId], mStart, false);
	}
	
	private static void mCheckEnableItem(MenuItem item, boolean nEnable) {
		if (nEnable) {
			item.pal = 0;
			item.flags = 3 | 4;
		} else {
			item.pal = 1;
			item.flags = 1;
		}
	}
}
