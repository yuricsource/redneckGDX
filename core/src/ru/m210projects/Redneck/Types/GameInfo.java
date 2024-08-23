// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Types;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.nio.file.Path;
import java.util.*;

import static ru.m210projects.Build.Strhandler.Bstrcmp;
import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_ENTRY;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.game;

public class GameInfo {

    public String title;
    private final EpisodeEntry file;
    public final EpisodeInfo[] episodes;
    public final String[] skillnames;
    public int nEpisodes;
    private Script ConScr;
    private int textptr;

    public GameInfo(EpisodeEntry entry) throws Exception {
        this.file = entry;
        this.title = entry.getName();
        this.skillnames = new String[nMaxSkills];
        this.episodes = new EpisodeInfo[nMaxEpisodes];

        nEpisodes = 0;
        Map<String, Group> cachedGroups = new LinkedHashMap<>();
        for (Entry scriptEntry : entry.getIncludes()) {
            byte[] data = preparescript(scriptEntry.getBytes());
            findSkillNames(data);
            findVolumes(data);
            findMaps(data);

            Group parent = scriptEntry.getParent();
            cachedGroups.put(parent.getName(), parent);
        }

        checkEpisodes(cachedGroups);
    }

    @Override
    public String toString() {
        return file.getHashKey();
    }

    public EpisodeEntry getEpisodeEntry() {
        return file;
    }

    public Script getCON() {
        return ConScr;
    }

    public void setCON(Script con) {
        this.ConScr = con;
    }

    public EpisodeInfo getCurrentEpisode() {
        if (ud.volume_number < episodes.length) {
            return episodes[ud.volume_number];
        }
        return null;
    }

    private Entry findMap(Path mapPath, Map<String, Group> groups) {
        for(Group group : groups.values()) {
            Entry mapEntry = group.getEntry(mapPath);
            if (mapEntry.exists()) {
                return mapEntry;
            }
        }

        Map<String, Group> extraGroups = new HashMap<>();
        // Try to find maps in grp or zip files of the parent
        for(Group group : groups.values()) {
            if (game.getCache().isGameDirectory(group)) {
                continue;
            }

            for (Entry entry : group.getEntries()) {
                if (groups.containsKey(entry.getName())) {
                    continue;
                }

                if (entry.isExtension("pk3") || entry.isExtension("zip") || entry.isExtension("grp") || entry.isExtension("rff")) {
                    Group extraGroup = game.getCache().newGroup(entry);
                    extraGroups.put(entry.getName(), extraGroup);

                    Entry mapEntry = extraGroup.getEntry(mapPath);
                    if (mapEntry.exists()) {
                        groups.putAll(extraGroups);
                        return mapEntry;
                    }
                }
            }
        }

        groups.putAll(extraGroups);
        return DUMMY_ENTRY;
    }

    private void checkEpisodes(Map<String, Group> cachedGroups) throws Exception {
        int sum = 0;
        for (int e = 0; e < nEpisodes; e++) {
            EpisodeInfo episodeInfo = episodes[e];
            if (episodeInfo == null) {
                continue; //The episode isn't found
            }

            // Checking for map file presence
            int presenceCount = 0;
            for (int i = 0; i < episodeInfo.nMaps; i++) {
                MapInfo mapInfo = episodeInfo.getMapInfo(i);
                if (mapInfo == null) {
                    continue;
                }

                Entry mapEntry = findMap(mapInfo.getPath(), cachedGroups);
                if (mapEntry.exists()) {
                    presenceCount++;
                } else {
                    mapInfo.clear();
                    break;
                }
            }

            episodeInfo.nMaps = presenceCount;
            sum += episodeInfo.nMaps;
        }

        if (sum == 0) {
            throw new Exception("Maps in addon are not found");
        } else {
            if (defGame != null && skillnames[0] == null) {
                Console.out.println("Appling default skill names for " + title);
                for (int i = 0; i < defGame.getCON().nSkills; i++) {
                    skillnames[i] = new String(defGame.getCON().skill_names[i]).trim();
                }
            }
        }
    }

    private void findVolumes(byte[] buf) {
        int index = -1;

        while ((index = indexOf("definevolumename ", buf, index + 1)) != -1) {
            textptr = index + 16;
            Integer j = transnum(buf);
            if (j == null) {
                continue;
            }
            while (buf[textptr] == ' ') {
                textptr++;
            }

            int i = 0;
            int startptr = textptr;
            while (buf[textptr + i] != 0x0a) {
                i++;
            }

            episodes[j] = new EpisodeInfo(new String(buf, startptr, i - 1).toUpperCase());
            nEpisodes = Math.max(nEpisodes, j + 1);
        }

    }

    private void findMaps(byte[] buf) {
        int index = -1;

        while ((index = indexOf("definelevelname ", buf, index + 1)) != -1) {
            textptr = index + 15;
            Integer epnum = transnum(buf);
            if (epnum == null) {
                continue;
            }
            Integer mapnum = transnum(buf);
            if (mapnum == null) {
                continue;
            }

            while (buf[textptr] == ' ') {
                textptr++;
            }

            int i = 0;
            int ptr = textptr;
            while (buf[textptr] != ' ' && buf[textptr] != 0x0a) {
                textptr++;
                i++;
            }

            Path path = FileUtils.getPath(new String(buf, ptr, i));
            if (episodes[epnum] != null) {
                while (buf[textptr] == ' ') {
                    textptr++;
                }

                int partime = (((buf[textptr] - '0') * 10 + (buf[textptr + 1] - '0')) * 26 * 60) +
                        (((buf[textptr + 3] - '0') * 10 + (buf[textptr + 4] - '0')) * 26);

                textptr += 5;
                while (buf[textptr] == ' ') {
                    textptr++;
                }

                int designertime =
                        (((buf[textptr] - '0') * 10 + (buf[textptr + 1] - '0')) * 26 * 60) +
                                (((buf[textptr + 3] - '0') * 10 + (buf[textptr + 4] - '0')) * 26);

                textptr += 5;
                while (buf[textptr] == ' ') {
                    textptr++;
                }

                i = 0;
                while (buf[textptr + i] != 0x0a) {
                    i++;
                }
                String title = new String(buf, textptr, i - 1);
                episodes[epnum].setMapInfo(mapnum, new MapInfo(path, title, partime, designertime));
                episodes[epnum].nMaps = Math.max(episodes[epnum].nMaps, mapnum + 1);
            }
        }
    }

    private void findSkillNames(byte[] buf) {
        int index = -1;
        int size = 0;
        while ((index = indexOf("defineskillname ", buf, index + 1)) != -1) {
            textptr = index + 15;
            Integer j = transnum(buf);
            if (j == null) {
                continue;
            }
            while (buf[textptr] == ' ') {
                textptr++;
            }

            int i = 0;
            while (buf[textptr + i] != 0x0a) {
                i++;
            }

            skillnames[j] = new String(buf, textptr, i - 1).toUpperCase();
            size = Math.max(size, j + 1);
        }

    }

    private Integer transnum(byte[] text) {
        char[] tempbuf = new char[2048];
        while (!isaltok(text[textptr])) {
            textptr++;
            if (text[textptr] == 0) {
                return null;
            }
        }

        int l = 0;
        while (isaltok(text[textptr + l])) {
            tempbuf[l] = (char) text[textptr + l];
            l++;
        }

        tempbuf[l] = 0;
        for (int i = 0; i < NUMKEYWORDS; i++) {
            if (Bstrcmp(label, (labelcnt << 6), keyw[i], 0) == 0) {
                error++;
                Console.out.println("  * ERROR! Symbol '" + label[(labelcnt << 6)] + "' is a key word.");
                textptr += l;
            }
        }

        for (int i = 0; i < labelcnt; i++) {
            if (Bstrcmp(tempbuf, 0, label, i << 6) == 0) {
                textptr += l;
                return labelcode.get(i);
            }
        }

        if (!Character.isDigit(text[textptr]) && text[textptr] != '-') {
            Console.out.println("  * ERROR! Parameter '" + new String(tempbuf, 0, l) + "' is undefined.");
            error++;
            textptr += l;
            return null;
        }

        String number = new String(text, textptr, l);
        textptr += l;
        return Integer.parseInt(number);
    }

}