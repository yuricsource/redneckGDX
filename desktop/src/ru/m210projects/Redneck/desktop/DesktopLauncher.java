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

package ru.m210projects.Redneck.desktop;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.SetLogFile;

import org.lwjgl.LWJGLException;

import ru.m210projects.Build.Audio.ALAudio;
import ru.m210projects.Build.Audio.DummySound;
import ru.m210projects.Build.Audio.Sound;
import ru.m210projects.Build.Audio.BMusic.DummyMusic;
import ru.m210projects.Build.Audio.BMusic.Music;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.BConfig;
import ru.m210projects.Build.desktop.BuildApplicationImpl;
import ru.m210projects.Build.desktop.DesktopMessage;
import ru.m210projects.Build.desktop.Launcher.DesktopFrame;
import ru.m210projects.Build.desktop.Launcher.LaunchCallback;
import ru.m210projects.Build.desktop.audio.ALSoundDrv;
import ru.m210projects.Build.desktop.audio.GdxAL;
import ru.m210projects.Build.desktop.audio.LwjglAL;
import ru.m210projects.Build.desktop.audio.midi.MidiMusicModule;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.Date;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static String[] arg;
	public static final String[] resources = {
		"Redneck Rampage",
		"redneck.grp",
	};
	
	public static void main(String[] arg) {
		try {
			LaunchCallback callback = new LaunchCallback("rrgdx.ver", Main.release) {
				@Override
				public void run(LwjglApplicationConfiguration lwjglConfig, int MidiDevice, BConfig cfg) {
					launchPort(lwjglConfig, MidiDevice, cfg);
				}

				@Override
				public BConfig buildConfig(String path, String cfgname) {
					return new Config(path, cfgname);
				}
			};
			
			new DesktopFrame(Main.appname, Main.sversion, resources, callback, 
					DesktopLauncher.class.getResource("/icons/title.png"), 
					DesktopLauncher.class.getResource("/icons/RR32.png"),
					DesktopLauncher.class.getResource("/icons/RR64.png"));
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	public static void launchPort(LwjglApplicationConfiguration lwjglConfig, int midiDevice, BConfig cfg)
	{
		SetLogFile(Main.appname + ".log");
		Console.Println("BUILD engine by Ken Silverman (http://www.advsys.net/ken)");
		Console.Println(Main.appname + " " + Main.sversion + " by [M210®] (http://m210.duke4.net)");

		Main.date = new Date("MMM dd, yyyy HH:mm:ss");
		Console.Println("Current date " + Main.date.getLaunchDate());
		
		String osver = System.getProperty("os.version");
		String jrever = System.getProperty("java.version");

		Console.Println("Running on " + Main.OS + " (version " + osver + ")");
		Console.Println("\t with JRE version: " + jrever + "\r\n");

		for(int i = 16; i <= 256; i *= 2) 
			lwjglConfig.addIcon("icons/RR" + i + ".png", FileType.Internal);
		
		ALAudio al = null;
		try {
			//try to load JNA version of OpenAL
			al = new GdxAL();
		} catch (Throwable t) {
			try {
				//if not success, try to load GDX version of OpenAL
				al = new LwjglAL();
			} catch (LWJGLException e) {
				e.printStackTrace();
				Console.Println("Unable to initialize OpenAL! - " + e.getLocalizedMessage(), OSDTEXT_RED);
			}
		}
		
		Main.fxdrivers = new Sound[] {
			new DummySound(),
			new ALSoundDrv(al),
		};
		if(cfg.snddrv >= Main.fxdrivers.length)
			cfg.snddrv = 0;

		if(midiDevice != -1) {
			Main.mxdrivers = new Music[] {
				new DummyMusic(),
				new MidiMusicModule(midiDevice, null),
			};
			if(cfg.middrv > Main.mxdrivers.length)
				cfg.middrv = 0;
		} else {
			Main.mxdrivers = new Music[] {
				new DummyMusic(),
			};
			cfg.middrv = 0;
		}

		new BuildApplicationImpl(new Main(cfg, new DesktopMessage(DesktopLauncher.class.getResource("/icons/RR32.png"))), lwjglConfig);
	}
}
