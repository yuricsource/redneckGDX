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

import static ru.m210projects.Build.Render.VideoMode.initVideoModes;
import static ru.m210projects.Build.Render.VideoMode.setFullscreen;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.FileHandle.Cache1D;
import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.desktop.BuildApplicationConfiguration;
import ru.m210projects.Build.desktop.BuildApplicationImpl;
import ru.m210projects.Build.desktop.DesktopMessage;
import ru.m210projects.Build.desktop.audio.ALAudio;
import ru.m210projects.Build.desktop.audio.ALSoundDrv;
import ru.m210projects.Build.desktop.audio.GdxAL;
import ru.m210projects.Build.desktop.audio.LwjglAL;
import ru.m210projects.Build.desktop.audio.midi.MidiMusicModule;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

public class DesktopLauncher {
	public static final String appname = "RedneckGDX";

	public static void main(final String[] arg) {
		String filepath = arg[0] + File.separator;
		BuildGdx.compat = new Compat(filepath, filepath);
		BuildGdx.cache = new Cache1D(BuildGdx.compat);
		
		int midiDevice = 0;

		BuildConfig cfg = new Config(Path.Game.getPath(), appname + ".ini");

		BuildApplicationConfiguration lwjglConfig = new BuildApplicationConfiguration();
		lwjglConfig.fullscreen = setFullscreen(cfg.ScreenWidth, cfg.ScreenHeight, cfg.fullscreen == 1);
		lwjglConfig.width = (cfg.ScreenWidth);
		lwjglConfig.height = (cfg.ScreenHeight);
		lwjglConfig.resizable = false;
		lwjglConfig.depth = 16; // z-buffer
		lwjglConfig.stencil = 8;

		lwjglConfig.backgroundFPS = cfg.fpslimit;
		lwjglConfig.foregroundFPS = cfg.fpslimit;
		lwjglConfig.vSyncEnabled = cfg.gVSync;
		lwjglConfig.borderless = cfg.borderless;

		BuildAudio.registerDriver(Driver.Sound, new ALSoundDrv(new ALSoundDrv.DriverCallback() {
			public ALAudio InitDriver() throws Throwable {
				return new LwjglAL();
			}
		}, "OpenAL 1.15.1"));
		
		BuildAudio.registerDriver(Driver.Sound, new ALSoundDrv(new ALSoundDrv.DriverCallback() {
			public ALAudio InitDriver() throws Throwable {
				return new GdxAL();
			}
		}, "OpenAL 1.18.1"));
		BuildAudio.registerDriver(Driver.Music, new MidiMusicModule(midiDevice, null));
		
		initVideoModes(LwjglApplicationConfiguration.getDisplayModes(), LwjglApplicationConfiguration.getDesktopDisplayMode());

		new BuildApplicationImpl(new Main(cfg, appname, "?.??", false, false), new DesktopMessage(null, false), cfg.renderType, lwjglConfig);
	}
}
