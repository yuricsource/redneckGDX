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

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Types.RTS.*;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildConfig;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Redneck.Factory.RREngine;
import ru.m210projects.Redneck.Factory.RRFactory;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Factory.RRNetwork;
import ru.m210projects.Redneck.Menus.GameMenu;
import ru.m210projects.Redneck.Menus.MainMenu;
import ru.m210projects.Redneck.Screens.AnmScreen;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Screens.DisconnectScreen;
import ru.m210projects.Redneck.Screens.EndScreen;
import ru.m210projects.Redneck.Screens.GameScreen;
import ru.m210projects.Redneck.Screens.LoadingScreen;
import ru.m210projects.Redneck.Screens.MenuScreen;
import ru.m210projects.Redneck.Screens.NetScreen;
import ru.m210projects.Redneck.Screens.StatisticScreen;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.MVEFile;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Weaponhit;

public class Main extends BuildGame {

	/*
	 * v0.761
	 * Weapon drop fix after dead
	 * RRRA E1L1 destruct wall in secret place fix
	 * Moving door after load game fix
	 * Quick pee don't resurect the player anymore
	 * 
	 * Endscreen - 8677 - 8678
	 * 
	 * TODO:
	 * slotwin screenpeek
	 * прверить прекэш
	 * в статистике съехало имя 1го плеера
	 * isSwamp unsync
	 * в меню перенести табло с игроками вниз
	 * показывать табло с плеерами в коопе
	 * 
	 * 
	 * hud из новых ресурсов
	 * 1) In level "Gamblin' Boat" in the engineroom you have to turn a wheel which lets the ship explode, it is not possible to activate this wheel, because you cannot enter the metal box in where it is located (no problem in Dosbox)
	 * проблема с fakebubba после загрузки сохранения
	 * as I said once, you cannot pickup a weapon if you already have it
	 * в грязи глючит мультиплеер камеру
	 * и еще есть проблема с большой задержкой при нырянии. (на лестницах лаги)
	 * в мультиплеере конец эпизода без заставки и перехода на след эпизод
	 * Улучшение, которое я хотел бы увидеть, - зафиксировать счетчик врагов. NukeyT сказал мне много вещей, которые не следует считать врагами (например, торнадо или даже Бубба), а Виксен считается только мертвым, если их тела выбиты, что должно быть только для стражей Халка. 
	 * Также, если начинаются моды, убедитесь, что куры и коровы не привлекают автоматическую цель и имеют правильные удары. Мертвые коровы, создающие невидимую стену, блокирующую пули над своим трупом, действительно плохи. 
	 * cd audio from cue
	 * cutscenes MVE
	 * загружать ресурсы из отдельных папок(архивов) для юзеркарт
	 */

	public static final String sversion = "v0.950";

	public static AnmScreen gAnmScreen;
	public static MenuScreen gMenuScreen;
	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static StatisticScreen gStatisticScreen;
	public static EndScreen gEndScreen;
	public static NetScreen gNetScreen;
	public static DisconnectScreen gDisconnectScreen;

	public static enum UserFlag {
		None, UserMap, Addon
	};

	public static UserFlag mUserFlag = UserFlag.None;
	public static Main game;
	public static RREngine engine;
	public static Config cfg;
	public RRMenuHandler menu;
	public RRNetwork net;

	public Main(BuildConfig bcfg, String appname, String sversion, boolean release) {
		super(bcfg, appname, sversion, release);
		game = this;
		cfg = (Config) bcfg;
	}

	@Override
	public BuildFactory getFactory() {
		return new RRFactory(this);
	}

	@Override
	public void init() throws Exception {
		net = (RRNetwork) pNet;

		compilecons();

		ConsoleInit();

		engine.inittimer(TICRATE);

		InitSpecialTextures();

		InitUserDefs();

		RTS_Init(ud.rtsname);
		if (numlumps != 0)
			Console.Println("Using .RTS file:" + ud.rtsname);

		SoundStartup();
		MusicStartup();

		genspriteremaps();

		initanimations();
		FindSaves();

		LoadUserRes();

		for (int i = 0; i < MAXPLAYERS; i++) {
			ps[i] = new PlayerStruct();
			po[i] = new PlayerOrig();
		}

		InitPlayers();

		for (int i = 0; i < MAXANIMWALLS; i++)
			animwall[i] = new Animwalltype();

		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i] = new Weaponhit();

		Console.Println("Initializing def-scripts...");
		if (cfg.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if ((autoload = cache.checkDirectory("autoload")) != null) {
				for (Iterator<FileEntry> it = autoload.getFiles().values().iterator(); it.hasNext();) {
					FileEntry file = it.next();
					if (file.getExtension().equals("zip")) {
						String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
						int group = initgroupfile(file.getPath());
						for (RESHANDLE res : kList(group)) {
							if (res.filename.lastIndexOf('.') == -1)
								continue;

							String resname = res.filename.substring(0, res.filename.lastIndexOf('.'));
							if (resname.equals(filename) && res.fileformat.equals("def")) {
								byte[] buf = res.getBytes();
								baseDef.loadScript(res.filename, buf);
								break;
							}
						}
					}

					if (file.getExtension().equals("def"))
						baseDef.loadScript(file);
				}
			}
		}

		FileEntry mainDef = null;
		if ((mainDef = cache.checkFile("rrgdx.def")) != null)
			baseDef.loadScript(mainDef);

		setDefs(baseDef);

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new GameMenu(this);

		gAnmScreen = new AnmScreen(this);
		gMenuScreen = new MenuScreen(this);
		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gStatisticScreen = new StatisticScreen(this);
		gEndScreen = new EndScreen();
		gNetScreen = new NetScreen(this);
		gDisconnectScreen = new DisconnectScreen(this);

		gDemoScreen.demoscan();
		
		ByteBuffer bb = kGetBuffer("REDINT.MVE", 0);
		if(bb != null)
		{
			System.err.println("Found");
			new MVEFile(bb);
		}
	}

	public static boolean IsOriginalDemo() {
		ScreenAdapter screen = (ScreenAdapter) game.getScreen();
		if(screen instanceof DemoScreen)
			return ((DemoScreen) screen).IsOriginalGame();
		if(screen instanceof GameScreen)
			return ((GameScreen) screen).IsOriginalGame();

		return false;
	}

	@Override
	public void show() {
		uGameFlags = 0;
		kGameCrash = false;
		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();
		resetEpisodeResources();

		if (gAnmScreen.init("rr_intro.anm", 0)) {
			gAnmScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					if (gAnmScreen.init("redneck.anm", 1)) {
						gAnmScreen.setCallback(new Runnable() {
							@Override
							public void run() {
								if (gAnmScreen.init("xatlogo.anm", 2)) {
									setScreen(gAnmScreen.setCallback(rMenu).escSkipping(false));
								}
							}
						});
						setScreen(gAnmScreen.escSkipping(false));
					}
				}
			}).setSkipping(rMenu);
			setScreen(gAnmScreen.escSkipping(false));
		} else {
			setScreen(gLoadingScreen);
			Gdx.app.postRunnable(rMenu);
		}
	}

	private Runnable rMenu = new Runnable() {
		@Override
		public void run() {
			StopAllSounds();
			ud.level_number = 0;
			ud.multimode = 1;
			mFakeMultiplayer = false;

			if (!menu.gShowMenu)
				menu.mOpen(menu.mMenus[MAIN], -1);

			if (numplayers > 1 || gDemoScreen.demofiles.size() == 0 || cfg.gDemoSeq == 0 || !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	public void InitUserDefs() {
		ud.setDefaults(cfg);
		ud.god = false;
		ud.cashman = 0;
		ud.player_skill = 2;
	}

	public void ConsoleInit() {
		Console.Println("Initializing on-screen display system");
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);

		Console.RegisterCvar(new OSDCOMMAND("memusage", "mem usage / total", new OSDCVARFUNC() {
			@Override
			public void execute() {
				Console.Println("Memory used: " + MemLog.used() + " / " + MemLog.total() + " mb");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("restart", "restart", new OSDCVARFUNC() {
			@Override
			public void execute() {
				LeaveMap();
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("net_bufferjitter", "net_bufferjitter", new OSDCVARFUNC() {
			@Override
			public void execute() {
				Console.Println("bufferjitter: " + net.bufferJitter);
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("net_player", "net_player", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (Console.osd_argc != 2) {
					Console.Println("net_player: num");
					return;
				}
				try {
					String num = osd_argv[1];

					int pnum = Integer.parseInt(num);
					int p = game.net.PutPacketByte(packbuf, 0, RRNetwork.kPacketPlayer);
					p = game.net.PutPacketByte(packbuf, p, pnum);

					int trail = game.net.gNetFifoTail;
					if (myconnectindex == connecthead)
						trail += 1;

					LittleEndian.putInt(packbuf, p, trail);
					p += 4;
					game.net.PlayerSyncRequest = pnum;

					game.net.sendtoall(packbuf, p);
				} catch (Exception e) {
				}
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quicksave", "quicksave: performs a quick save", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (ud.multimode != 1 || numplayers > 1) {
					Console.Println("quicksave: Single player only");
					return;
				}

				if (isCurrentScreen(gGameScreen)) {
					quicksave();
				} else
					Console.Println("quicksave: not in a game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quickload", "quickload: performs a quick load", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (ud.multimode != 1 || numplayers > 1) {
					Console.Println("quickload: Single player only");
					return;
				}

				if (isCurrentScreen(gGameScreen)) {
					quickload();
				} else
					Console.Println("quickload: not in a game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quit", null, new OSDCVARFUNC() {
			@Override
			public void execute() {
				game.gExit = true;
			}
		}));

//		Console.RegisterCvar(new OSDCOMMAND("net_nextmap",
//				"net_nextmap", new OSDCVARFUNC() {
//					@Override
//					public void execute() {
//						LeaveMap();
//						ud.level_number++;
//						game.net.sendtoall(new byte[] { kPacketLevelEnd }, 1);
//					}
//		}));
	}

	public void dassert(String msg) {
		if (kGameCrash)
			return;

		ThrowError(msg);
		System.exit(0);
	}

	public boolean GameMessage(String text, boolean question) {
		if (!question) {
			BuildGdx.message.show("Warning: ", text, MessageType.Info);
			Console.Println("Warning: " + text, OSDTEXT_YELLOW);
			return true;
		} else {
			Console.Println("Warning: " + text, OSDTEXT_YELLOW);
			return BuildGdx.message.show("Warning: ", text, MessageType.Question);
		}
	}

	public void GameCrash(String errorText) {
		BuildGdx.message.show("Error: ", errorText, MessageType.Info);
		Console.Println("Game error: " + errorText, OSDTEXT_RED);

		kGameCrash = true;
	}

	public void Disconnect() {
		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();

		changeScreen(gDisconnectScreen);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (ud.rec != null)
			ud.rec.close();
	}

	@Override
	protected String reportData() {
		String text = "";
		text += "boardfilename " + boardfilename;
		text += "\r\n";
		text += "volume " + (ud.volume_number + 1);
		text += "\r\n";
		text += "level " + (ud.level_number + 1);
		text += "\r\n";
		text += "skill " + ud.player_skill;
		text += "\r\n";
		text += "posx " + ps[myconnectindex].posx;
		text += "\r\n";
		text += "posy " + ps[myconnectindex].posy;
		text += "\r\n";
		text += "posz " + ps[myconnectindex].posz;
		text += "\r\n";
		text += "sectnum " + ps[myconnectindex].cursectnum;
		text += "\r\n";

		return text;
	}
}
