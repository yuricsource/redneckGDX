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

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Animlib.initanm;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Screen.setup3dscreen;
import static ru.m210projects.Redneck.Sounds.clearsoundlocks;
import static ru.m210projects.Redneck.Sounds.currMusic;
import static ru.m210projects.Redneck.ResourceHandler.*;

import com.badlogic.gdx.ApplicationAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage;
import ru.m210projects.Build.Architecture.GLFrame;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Audio.Music;
import ru.m210projects.Build.Audio.Sound;
import ru.m210projects.Build.Input.GPManager;
import ru.m210projects.Build.Types.BConfig;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Redneck.Types.RRPolymost;
import ru.m210projects.Redneck.Types.Date;
import ru.m210projects.Redneck.Types.RREngine;

public class Main extends ApplicationAdapter {
	
	/*
	 * v0.761
	 * Weapon drop fix after dead
	 * RRRA E1L1 destruct wall in secret place fix
	 * Moving door after load game fix
	 * 
	 * TODO:
	 * color correction fix
	 * if (ctrlGetInputKey(Screenshot, true)) String name = "scrxxxx.png";
	 * hud из новых ресурсов
	 * savegameslot from WH (new displaytext)
	 * fps text scale
	 * 1) In level "Gamblin' Boat" in the engineroom you have to turn a wheel which lets the ship explode, it is not possible to activate this wheel, because you cannot enter the metal box in where it is located (no problem in Dosbox)
	 * проблема с fakebubba после загрузки сохранения
	 * as I said once, you cannot pickup a weapon if you already have it
	 * в грязи глючит мультиплеер камеру
	 * и еще есть проблема с большой задержкой при нырянии. (на лестницах лаги)
	 * в мультиплеере конец эпизода без заставки и перехода на след эпизод
	 * multiplayer usercontent
	 * Улучшение, которое я хотел бы увидеть, - зафиксировать счетчик врагов. NukeyT сказал мне много вещей, которые не следует считать врагами (например, торнадо или даже Бубба), а Виксен считается только мертвым, если их тела выбиты, что должно быть только для стражей Халка. 
	 * Также, если начинаются моды, убедитесь, что куры и коровы не привлекают автоматическую цель и имеют правильные удары. Мертвые коровы, создающие невидимую стену, блокирующую пули над своим трупом, действительно плохи. 
	 * cd audio from cue
	 * cutscenes MVE
	 * загружать ресурсы из отдельных папок(архивов) для юзеркарт
	 * get crc32 from map and script
	 */

	public static final String appname = "RedneckGDX";
	public static final String sversion = "v0.761";
	public static String OS = System.getProperty("os.name");
	public static Date date;
	public static final char[] version = sversion.toCharArray();
	public static boolean release = false;
	
	public static RREngine engine;
	public static Config cfg;
	public static Sound[] fxdrivers;
	public static Music[] mxdrivers;
	public static GPManager gpmanager;

	public Main(BConfig cfg, BuildMessage message)
	{
		Main.cfg = (Config) cfg;
		InitRR(message);
	}

	@Override
	public void create() {
		try {
			engine.setrendermode(new RRPolymost(engine));
			setup3dscreen(cfg.ScreenWidth, cfg.ScreenHeight);

			LoadUserRes();
			gpmanager = new GPManager();
			gpmanager.setDeadZone(cfg.gJoyDeadZone / 65536f);

			// if user unplugged a device between two runs, reset to default device
			// it could have been done in the menu but user might not even browse to it ...
			// this also automatically fix the weird UX that would have occurred on menu otherwise
			// as a bonus, if user unplugs device 1 but leaves device 2 in,
			// it becomes default which is kind of nice since he doesn't have to go to menu again !
			if (!Main.gpmanager.isValidDevice(cfg.gJoyDevice))
				cfg.gJoyDevice = 0;

			updateColorCorrection();
			cfg.checkFps(cfg.fpslimit);
			engine.setanisotropy(cfg, cfg.anisotropy);
			engine.setwidescreen(cfg, cfg.widescreen != 0);

			gm = MODE_LOGO;
			initanm("rr_intro.anm",5, -1);
			setDefs(baseDef);

			MemLog.log("create");
			System.gc();
		} catch (Exception e) {
			dassert(exceptionHandler(e) + " in create(): "
				+ (e.getMessage() == null ? e.toString() : e.getMessage())
				+ " \r\n" + stackTraceToString(e));
		}
	}
	
	@Override
	public void render() {
		try {
			GameLoop();
		} catch (Exception e) {
			if (!release) {
				e.printStackTrace();
				dispose();
				System.exit(1);
			} else {
				dassert(exceptionHandler(e) + " in gameloop(): "
					+ (e.getMessage() == null ? "" : e.getMessage())
					+ " \r\n" + stackTraceToString(e));
			}
		}
	}

	@Override
	public void pause() {
		if(ud.multimode < 2 && numplayers < 2 && ud.recstat == 0) {
			ud.pause_on = 1;
			if(currMusic != null)
        		currMusic.pause();
            engine.getAudio().getSound().stopAllSounds();
            clearsoundlocks();
		}
		if (BuildGdx.app.getFrameType() == FrameType.GL)
			((GLFrame) BuildGdx.app.getFrame()).setDefaultDisplayConfiguration();
	}

	@Override
	public void resume() {
		if(ud.multimode < 2 && numplayers < 2 && ud.recstat == 0) {
			ud.pause_on = 0;
			ototalclock = totalclock;
			if(cfg.MusicToggle && currMusic != null) 
				currMusic.resume();
		}
		updateColorCorrection();
	}
	
	public void updateColorCorrection() {
		if (BuildGdx.app.getFrameType() == FrameType.GL) {
			if (!((GLFrame) BuildGdx.app.getFrame()).setDisplayConfiguration(cfg.gamma, cfg.brightness, cfg.contrast)) {
				((GLFrame) BuildGdx.app.getFrame()).setDefaultDisplayConfiguration();
				cfg.gamma = 1.0f;
				cfg.brightness = 0.0f;
				cfg.contrast = 1.0f;
			}
		}
	}

	@Override
	public void dispose() {
		appdispose();
	}
}
