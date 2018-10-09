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

import static ru.m210projects.Redneck.Animlib.initanm;
import static ru.m210projects.Redneck.Redneck.appdispose;
import static ru.m210projects.Redneck.Globals.MODE_LOGO;
import static ru.m210projects.Redneck.Globals.dassert;
import static ru.m210projects.Redneck.Globals.exceptionHandler;
import static ru.m210projects.Redneck.Globals.gm;
import static ru.m210projects.Redneck.Globals.stackTraceToString;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Screen.setup3dscreen;
import static ru.m210projects.Redneck.Sounds.clearsoundlocks;
import static ru.m210projects.Redneck.Sounds.currMusic;

import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Gameutils.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Audio.Sound;
import ru.m210projects.Build.Audio.BMusic.Music;
import ru.m210projects.Build.Input.GPManager;
import ru.m210projects.Build.Types.BConfig;
import ru.m210projects.Build.Types.BGraphics;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Build.Types.Message;
import ru.m210projects.Redneck.Types.RRPolymost;
import ru.m210projects.Redneck.Types.Date;
import ru.m210projects.Redneck.Types.RREngine;

public class Main extends ApplicationAdapter {
	
	/*
	 * v0.753
	 * Autoload folder can load resources as cusspack
	 * Addons support
	 * Joystick fix + smooth feature by aybe0
	 * Torches fix
	 * Cow "use" fix
	 * 
	 * TODO:
	 * сохранения
	 * грабли выключаются в читах
	 * мотоцикл перестал стрелять
	 * на уровне с кораблем странный стук
	 * seasick неправильно работает
	 * dobonus другие картинки
	 * fog / spawn RA fog method sub_86730
	 * ROR
	 * 
	 * 
	 * не работают комары в R66
	 * cd audio from cue
	 * загружать ресурсы из отдельных папок(архивов) для юзеркарт
	 * инфа об юзерэпизоде в меню загрузок
	 * get crc32 from map and script
	 * cheat @mario
	 * добавить kills и alchogol
	 * keys multiplayer bug
	 * drop dynamite insteadof crowbar
	 * max_kills multiplayer coop
	 * cutscenes MVE
	 */

	public static final String appname = "RedneckGDX";
	public static final String sversion = "v0.753";
	public static String OS = System.getProperty("os.name");
	public static Date date;
	public static final char[] version = sversion.toCharArray();
	public static boolean release = false;
	
	public static RREngine engine;
	public static Config cfg;
	public static Sound[] fxdrivers;
	public static Music[] mxdrivers;
	public static GPManager gpmanager;

	public Main(BConfig cfg, Message message)
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

			updateColorCorrection();
			cfg.checkFps(cfg.fpslimit);

			gm = MODE_LOGO;
			initanm("rr_intro.anm",5, -1);

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
		if(ud.multimode < 2 && ud.recstat == 0) {
			ud.pause_on = 1;
			if(currMusic != null)
        		currMusic.pause();
            engine.getAudio().getSound().stopAllSounds();
            clearsoundlocks();
		}
		((BGraphics)Gdx.graphics).setDefaultDisplayConfiguration();
	}

	@Override
	public void resume() {
		if(ud.multimode < 2 && ud.recstat == 0) {
			ud.pause_on = 0;
			if(cfg.MusicToggle && currMusic != null) 
				currMusic.resume();
		}
		updateColorCorrection();
	}
	
	public void updateColorCorrection()
	{
		if(Gdx.graphics instanceof BGraphics 
				&& !((BGraphics)Gdx.graphics).setDisplayConfiguration(
						cfg.gamma, cfg.brightness, cfg.contrast)) {
			
			((BGraphics)Gdx.graphics).setDefaultDisplayConfiguration();
			cfg.gamma = 1.0f;
			cfg.brightness = 0.0f;
			cfg.contrast = 1.0f;
		}
	}

	@Override
	public void dispose() {
		appdispose();
	}
}
