package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.grp.GrpEntry;
import ru.m210projects.Build.filehandle.zip.ZipEntry;

import java.io.IOException;
import java.io.InputStream;

public class UserEntry extends GrpEntry {

    private final Entry entry;

    public UserEntry(Entry entry) {
        super(null, entry.getName(), 0, (int) entry.getSize());
        this.entry = entry;
        setParent(entry.getParent());
        if (entry instanceof ZipEntry && !entry.isDirectory()) {
            ((ZipEntry) entry).load();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return entry.getInputStream();
    }
}
