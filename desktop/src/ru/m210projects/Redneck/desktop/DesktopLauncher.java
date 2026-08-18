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

import com.badlogic.gdx.backends.LwjglLauncherUtil;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALAudio;
import ru.m210projects.Build.Architecture.common.audio.AudioDriver;
import ru.m210projects.Build.Architecture.common.audio.BuildAudio;
import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DesktopLauncher {
    public static final String appname = "RedneckGDX";

    public static void main(final String[] arg) throws IOException {
        GameConfig cfg = new Config(Paths.get(arg[0], (appname + ".ini").toLowerCase(Locale.ROOT)));
        cfg.load();
        cfg.setGamePath(cfg.getCfgPath().getParent());
        cfg.addMidiDevices(LwjglLauncherUtil.getMidiDevices());

        // Register at least one audio driver — BuildGDX's ApplicationContext
        // throws "No audio drivers registered in config file" from
        // setAudioDriver() otherwise (called during Sounds.SoundStartup at
        // Main.init:197). LwjglLauncherUtil disables libGDX's audio and
        // expects the caller to register drivers. Register DUMMY as a
        // guaranteed fallback and OpenAL for real audio when the platform
        // supports it (OpenAL falls back to DUMMY at run-time internally if
        // no device is available).
        cfg.registerAudioDriver(AudioDriver.DUMMY_AUDIO, new BuildAudio.DummyAudio(cfg));
        try {
            cfg.registerAudioDriver(AudioDriver.OPENAL_AUDIO, new OpenALAudio(cfg));
        } catch (Throwable t) {
            System.err.println("[DesktopLauncher] OpenAL unavailable, falling back to DUMMY_AUDIO: " + t);
            cfg.setAudioDriver(AudioDriver.DUMMY_AUDIO);
        }

        List<String> args = Arrays.asList(arg);
        LwjglLauncherUtil.launch(new Main(args, cfg, appname, "?.??", false), null);
    }
}
