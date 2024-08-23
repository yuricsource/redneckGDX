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

import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.exceptions.AssertException;
import ru.m210projects.Build.exceptions.WarningException;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.filehandle.grp.GrpFile;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Fonts.GameFont;
import ru.m210projects.Redneck.Fonts.MenuFont;
import ru.m210projects.Redneck.Types.EpisodeManager;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.Script;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;
import ru.m210projects.Redneck.filehandle.UserEntry;

import java.util.List;

import static ru.m210projects.Build.filehandle.CacheResourceMap.CachePriority.HIGHEST;
import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_DIRECTORY;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Gamedef.error;
import static ru.m210projects.Redneck.Gamedef.loaduserdef;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.GRID;
import static ru.m210projects.Redneck.Names.MIRROR;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;

public class ResourceHandler {

    public final static EpisodeManager episodeManager = new EpisodeManager();

    public static boolean usecustomarts;
    private static GrpFile usergroup;

    public static void resetEpisodeResources() {
        Console.out.println("Resetting custom resources", OsdColor.GREEN);
        if (usergroup != null) {
            game.getCache().removeGroup(usergroup);
        }
        usergroup = null;
        currentGame = defGame;

        for (int i = 0; i < NUM_SOUNDS; i++) {
            Sound[i].ptr = null;
            Sound[i].setGlobalSound((currentGame.getCON().soundm[i] & 16) != 0);
        }

        if (!usecustomarts) {
            game.setDefs(game.baseDef);
            return;
        }

        System.err.println("Reset to default resources");
        if (engine.loadpics() == 0) {
            throw new AssertException("ART files not found " + game.getCache().getGameDirectory().getPath().resolve(engine.getTileManager().getTilesPath()));
        }

        if(!game.setDefs(game.baseDef)) {
            game.baseDef.apply();
            ((GameFont) game.getFont(1)).update();
            ((MenuFont) game.getFont(2)).update();
        }

        InitSpecialTextures();
        BowlReset();

        usecustomarts = false;
    }

    public static void InitSpecialTextures() {
        engine.allocatepermanenttile(MIRROR, 0, 0);
        engine.allocatepermanenttile(13, 0, 0); //ROR tile
        engine.allocatepermanenttile(GRID, 0, 0);
    }

    private static void InitGroupResources(List<Entry> list) {
        for (Entry res : list) {
            if (res.isExtension("art")) {
                engine.loadpic(res);
                usecustomarts = true;
            }
        }
    }

    private static void searchEpisodeResources(Group container, GrpFile resourceHolder) {
        for (Entry file : container.getEntries()) {
            Group subContainer = DUMMY_DIRECTORY;
            if (file.isDirectory() && file instanceof FileEntry) {
                subContainer = ((FileEntry) file).getDirectory();
            } else if (file.isExtension("pk3") || file.isExtension("zip") || file.isExtension("grp") || file.isExtension("rff")) {
                subContainer = game.getCache().newGroup(file);
            }

            if (!subContainer.equals(DUMMY_DIRECTORY)) {
                searchEpisodeResources(subContainer, resourceHolder);
            } else {
                resourceHolder.addEntry(new UserEntry(file));
            }
        }
    }

    public static void checkEpisodeResources(GameInfo addon) {
        if (addon == null) {
            return;
        }

        if (addon.equals(currentGame)) {
            return;
        }

        resetEpisodeResources();

        usergroup = new GrpFile("RemovableGroup");
        EpisodeEntry addonEntry = addon.getEpisodeEntry(); // redneck.grp
        Group parent = addonEntry.getGroup();
        DefScript addonScript;
        if (addonEntry.isPackageEpisode()) {
            addonScript = new DefScript(game.baseDef, addonEntry.getFileEntry());
            try {
                Entry res = parent.getEntry(appdef); // load def scripts
                if (res.exists()) {
                    addonScript.loadScript(parent.getName() + " script", res);
                }
                searchEpisodeResources(parent, usergroup);
            } catch (Exception e) {
                throw new AssertException("Error found in " + ((EpisodeEntry.Pack) addonEntry).getName() + "\r\n" + e);
            }
        } else {
            addonScript = new DefScript(game.baseDef, addonEntry.getFileEntry());
            if (!game.getCache().isGameDirectory(parent)) {
                searchEpisodeResources(parent, usergroup);
                Entry def = parent.getEntry(appdef);
                if (def.exists()) {
                    addonScript.loadScript(def);
                }
            }
        }

        if (addon.title.equals("Route 66")) { // official addon
            engine.loadpic(parent.getEntry("TILESA66.ART"));
            engine.loadpic(parent.getEntry("TILESB66.ART"));
            usecustomarts = true;
        }

        error = 0;
        // Loading user package files
        game.getCache().addGroup(usergroup, HIGHEST);
        InitGroupResources(usergroup.getEntries());
        if (addon.getCON() == null) {
            Script script = loaduserdef(addonEntry.getConFile());
            addon.setCON(script);
        }

        if (error == 0) {
            currentGame = addon;
            for (int i = 0; i < NUM_SOUNDS; i++) {
                Sound[i].setGlobalSound((currentGame.getCON().soundm[i] & 16) != 0);
            }
            game.setDefs(addonScript);
        } else {
            throw new WarningException("\nErrors found in " + addonEntry.getConFile().getName() + " file.");
        }
    }

    public static GameInfo levelGetEpisode(Entry entry) {
        if (entry == null || !entry.exists()) {
            return null;
        }

        if (entry instanceof FileEntry) {
            List<EpisodeEntry> list = episodeManager.getEpisodeEntries((FileEntry) entry);
            if (!list.isEmpty()) {
                // grp is for official addons searching...
                if (entry.isDirectory() || entry.isExtension("grp")) {
                    return episodeManager.getEpisode(list.get(0));
                }

                for (EpisodeEntry episodeEntry : list) {
                    if (episodeEntry.getConFile().equals(entry)) {
                        return episodeManager.getEpisode(episodeEntry);
                    }
                }
            }
        }

        return null;
    }
}
