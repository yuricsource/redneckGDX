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
import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

public class DesktopLauncher {
    public static final String appname = "RedneckGDX";

    public static void main(final String[] arg) throws IOException {
        GameConfig cfg = new Config(Paths.get(arg[0], (appname + ".ini").toLowerCase(Locale.ROOT)));
        cfg.load();
        cfg.setGamePath(cfg.getCfgPath().getParent());
        cfg.addMidiDevices(LwjglLauncherUtil.getMidiDevices());
        LwjglLauncherUtil.launch(new Main(cfg, appname, "?.??",false), null);
    }
}
