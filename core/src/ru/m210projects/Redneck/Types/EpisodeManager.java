package ru.m210projects.Redneck.Types;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Redneck.Gamedef.isaltok;
import static ru.m210projects.Redneck.Gamedef.preparescript;
import static ru.m210projects.Redneck.Main.game;

public class EpisodeManager {

    private final Map<String, GameInfo> episodeCache = new HashMap<>();

    /**
     * @param file: Directory with con files or GRP/ZIP file of addon
     * @return List of episode entries
     */
    public List<EpisodeEntry> getEpisodeEntries(FileEntry file) {
        List<EpisodeEntry> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }

        List<EpisodeEntry> entries = new ArrayList<>();
        Group group = file.getDirectory();
        if (!file.isDirectory()) {
            if (file.isExtension("con")) {
                group = file.getParent();
            } else {
                group = game.getCache().getGroup(file.getRelativePath().toString());
                if (group.isEmpty()) {
                    group = game.getCache().newGroup(file);
                }
            }
        }

        for (Entry groupFile : group) {
            if (groupFile.isExtension("con")) {
                List<Entry> includes = checkIncludes(groupFile);
                if (!includes.isEmpty()) {
                    if (file.isDirectory() || file.isExtension("con")) {
                        entries.add(new EpisodeEntry.File((FileEntry) groupFile, includes));
                    } else {
                        entries.add(new EpisodeEntry.Pack(file, groupFile, includes));
                    }
                }
            }
        }

        for (EpisodeEntry entry : entries) {
            GameInfo gameInfo = episodeCache.computeIfAbsent(entry.getHashKey(), e -> buildEpisode(entry));
            if (gameInfo != null) {
                list.add(entry);
            }
        }
        return list;
    }

    public GameInfo getEpisode(EpisodeEntry entry) {
        if (entry != null) {
            return episodeCache.get(entry.getHashKey());
        }
        return null;
    }

    private GameInfo buildEpisode(EpisodeEntry file) {
        try {
            return new GameInfo(file);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Entry> checkIncludes(Entry confile) {
        List<Entry> list = new ArrayList<>();
        if (!confile.exists()) {
            return list;
        }

        Group parent = confile.getParent();
        byte[] buf = preparescript(confile.getBytes());
        int index = -1;
        while ((index = indexOf("include ", buf, index + 1)) != -1) {
            int textptr = index + 7;
            while (!isaltok(buf[textptr])) {
                textptr++;
                if (buf[textptr] == 0) {
                    break;
                }
            }

            int i = 0;
            while (textptr + i < buf.length && isaltok(buf[textptr + i])) {
                i++;
            }
            Path path = FileUtils.getPath(new String(buf, textptr, i));
            Entry includedEntry = parent.getEntry(path);
            if (!includedEntry.exists()) {
                // try find con-files in redneck.grp
                includedEntry = game.getCache().getGroup(game.mainGrp).getEntry(path);
            }

            if (includedEntry.exists()) {
                list.add(includedEntry);
            } else {
                // The script is broken
                return new ArrayList<>();
            }
        }
        return list;
    }

}
