package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.fs.FileEntry;

import java.util.List;
import java.util.Locale;

import static ru.m210projects.Redneck.Main.game;

public interface EpisodeEntry {

    String getHashKey();

    FileEntry getFileEntry(); // con file or group (parent group) when con file placed

    Entry getConFile();

    Group getGroup();

    boolean isPackageEpisode();

    List<Entry> getIncludes();

    String getName();

    class Pack extends FileEntry implements EpisodeEntry {

        private final Entry conFile;
        private final FileEntry groupFile;
        private final List<Entry> includes;

        public Pack(FileEntry group, Entry conFile, List<Entry> includes) {
            super(group.getPath(), String.format("%s:%s", group.getName(), conFile.getName()), group.getSize());
            this.conFile = conFile;
            this.groupFile = group;
            this.setParent(group.getParent());
            this.includes = includes;
        }

        @Override
        public String getHashKey() {
            return String.format("%s:%s", groupFile.getRelativePath().toString().toUpperCase(Locale.ROOT), conFile.getName().toUpperCase(Locale.ROOT));
        }

        @Override
        public FileEntry getFileEntry() {
            return groupFile;
        }

        public Entry getConFile() {
            return conFile;
        }

        @Override
        public Group getGroup() {
            return game.getCache().newGroup(groupFile);
        }

        @Override
        public boolean isPackageEpisode() {
            return true;
        }

        @Override
        public List<Entry> getIncludes() {
            return includes;
        }

        @Override
        public String getExtension() {
            return groupFile.getExtension();
        }
    }

    class File extends FileEntry implements EpisodeEntry {

        private final List<Entry> includes;

        public File(FileEntry fileEntry, List<Entry> includes) {
            super(fileEntry);
            this.includes = includes;
        }

        @Override
        public String getHashKey() {
            return getRelativePath().toString().toUpperCase(Locale.ROOT);
        }

        @Override
        public FileEntry getFileEntry() {
            return this;
        }

        @Override
        public Entry getConFile() {
            return this;
        }

        @Override
        public Group getGroup() {
            return getParent();
        }

        @Override
        public boolean isPackageEpisode() {
            return false;
        }

        @Override
        public List<Entry> getIncludes() {
            return includes;
        }
    }

}
