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

/*
 * Freezes
 * CD muisc doincrement
 * Dobonus text tweak
 */

public class Main extends ApplicationAdapter {

	public static final String appname = "RedneckGDX";
	public static final String sversion = "v0.500";
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
			
			if(Gdx.graphics instanceof BGraphics 
					&& !((BGraphics)Gdx.graphics).setDisplayConfiguration(
							cfg.gamma, cfg.brightness, cfg.contrast)) {
				
				((BGraphics)Gdx.graphics).setDefaultDisplayConfiguration();
				cfg.gamma = 1.0f;
				cfg.brightness = 0.0f;
				cfg.contrast = 1.0f;
			}
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
	}

	@Override
	public void resume() {
		if(ud.multimode < 2 && ud.recstat == 0) {
			ud.pause_on = 0;
			if(cfg.MusicToggle && currMusic != null) 
				currMusic.resume();
		}
	}

	@Override
	public void dispose() {
		appdispose();
	}
}
