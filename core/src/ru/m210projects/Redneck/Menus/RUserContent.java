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

package ru.m210projects.Redneck.Menus;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.fs.Directory;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.util.List;

import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Main.gDemoScreen;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.ResourceHandler.episodeManager;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.sound;

public class RUserContent extends BuildMenu {

    private final Main app;
    private final MenuFileBrowser list;
    public boolean showmain;

    public RUserContent(final Main app) {
        super(app.pMenu);
        this.app = app;
        MenuTitle title = new RRTitle("User Content");

        int width = 240;
        list = new MenuFileBrowser(app, app.getFont(0), app.getFont(1), app.getFont(0), 40, 45, width, 1, 14, LOADSCREEN) {

            @Override
            public void init() {
                registerExtension("map", 0, 0);
                registerExtension("grp", 2, 1);
                registerExtension("zip", 2, 1);
                registerExtension("pk3", 2, 1);
                registerExtension("con", 2, 1);
                registerExtension("dmo", 1, -1);
            }

            @Override
            public void handleFile(FileEntry fil) {
                switch (fil.getExtension()) {
                    case "MAP":
                        addFile(fil);
                        break;
                    case "DMO":
                        if (showmain) {
                            break; // multiplayer menu
                        }

                        if (gDemoScreen.isDemoFile(fil))
                            addFile(fil);
                        break;
                    case "GRP":
                    case "ZIP":
                    case "PK3":
                        if (!showmain && fil.getName().equalsIgnoreCase(game.mainGrp)) {
                            break;
                        }

                        for (EpisodeEntry entry : episodeManager.getEpisodeEntries(fil)) {
                            addFile((FileEntry) entry);
                        }
                        break;
                }
            }

            @Override
            public void invoke(FileEntry fil) {
                switch (fil.getExtension()) {
                    case "MAP":
                        launchMap(fil);
                        break;
                    case "CON":
                    case "GRP":
                    case "ZIP":
                    case "PK3":
                        if (fil instanceof EpisodeEntry) {
                            launchEpisode(episodeManager.getEpisode((EpisodeEntry) fil));
                        }
                        break;
                    case "DMO":
                        Directory parent = fil.getParent();
                        List<Entry> demoList = gDemoScreen.checkDemoEntry(parent);
                        gDemoScreen.nDemonum = demoList.indexOf(fil);
                        gDemoScreen.showDemo(fil, null);
                        app.pMenu.mClose();
                        break;
                }
            }

            @Override
            public void handleDirectory(Directory dir) {
                if (app.pMenu.gShowMenu) {
                    sound(PISTOL_BODYHIT);
                }

                for (EpisodeEntry entry : episodeManager.getEpisodeEntries(dir.getDirectoryEntry())) {
                    addFile(entry.getFileEntry());
                }
            }

            @Override
            public void drawHeader(Renderer renderer, int x1, int x2, int y) {
                /*directories*/
                topFont.drawTextScaled(renderer, x1, y, dirs, 1.0f, -32, topPal, TextAlign.Left, Transparent.None, ConvertType.Normal, true);
                /*files*/
                topFont.drawTextScaled(renderer, x2, y, ffs, 1.0f, -32, topPal, TextAlign.Left, Transparent.None, ConvertType.Normal, true);
            }

            @Override
            public void drawPath(Renderer renderer, int x, int y, String path) {
                super.drawPath(renderer, x, y, this.path);
            }
        };

        list.topPal = 10;
        list.pathPal = 10;
        list.listPal = 12;
        list.backgroundPal = 4;

        addItem(title, false);
        addItem(list, true);
    }

    public boolean mFromNetworkMenu() {
        return app.menu.getLastMenu() == app.menu.mMenus[NETWORKGAME];
    }

    public void setShowMain(boolean show) {
        this.showmain = show;
        if (game.getCache().isGameDirectory(list.getDirectory())) {
            list.refreshList();
        }
    }

    private void launchEpisode(GameInfo game) {
        if (game == null) {
            return;
        }

        if (mFromNetworkMenu()) {
            NetworkMenu network = (NetworkMenu) app.menu.mMenus[NETWORKGAME];
            network.setEpisode(game);
            app.menu.mMenuBack();
            return;
        }

        NewAddonMenu next = (NewAddonMenu) app.menu.mMenus[NEWADDON];
        next.setEpisode(game);
        app.menu.mOpen(next, -1);
    }

    private void launchMap(FileEntry file) {
        if (file == null) {
            return;
        }

        if (mFromNetworkMenu()) {
            NetworkMenu network = (NetworkMenu) app.menu.mMenus[NETWORKGAME];
            network.setMap(file);
            app.menu.mMenuBack();
            return;
        }

        DifficultyMenu next = (DifficultyMenu) app.menu.mMenus[DIFFICULTY];
        next.setMap(file);
        app.menu.mOpen(next, -1);
    }
}
