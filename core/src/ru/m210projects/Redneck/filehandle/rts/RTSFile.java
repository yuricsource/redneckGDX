//-------------------------------------------------------------------------
/*
Copyright (C) 1996, 2003 - 3D Realms Entertainment

This file is part of Duke Nukem 3D version 1.5 - Atomic Edition

Duke Nukem 3D is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Original Source: 1996 - Todd Replogle
Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
Modifications for JonoF's port by Jonathon Fowler (jonof@edgenetwk.com)
*/
//-------------------------------------------------------------------------

package ru.m210projects.Redneck.filehandle.rts;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.InputStreamProvider;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.osd.Console;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_ENTRY;

public class RTSFile implements Group {

    protected final List<LumpInfo> entries;
    private final String name;

    public RTSFile(String filename, InputStreamProvider provider) throws IOException {
        this.name = filename.toUpperCase();
        Console.out.println("    Adding " + filename);

        try (InputStream is = new BufferedInputStream(provider.newInputStream())) {
            String identification = StreamUtils.readString(is, 4);
            if (!identification.equalsIgnoreCase("IWAD")) {
                throw new RuntimeException("RTS file " + filename + " doesn't have IWAD id");
            }

            int infonumlumps = StreamUtils.readInt(is);
            int infotableofs = StreamUtils.readInt(is);

            this.entries = new ArrayList<>(infonumlumps);
            int skipBytes = infotableofs - 12;
            StreamUtils.skip(is, skipBytes);

            for (int i = 0; i < infonumlumps; i++) {
                int offset = StreamUtils.readInt(is);
                int size = StreamUtils.readInt(is);
                String name = StreamUtils.readString(is, 8);

                LumpInfo entry = new LumpInfo(provider, name, offset, size);
                entry.parent = this;

                if (size> 0) {
                    entries.add(entry);
                }
            }
        }
    }

    @Override
    public synchronized int getSize() {
        return entries.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public Entry getEntry(String name) {
        return DUMMY_ENTRY;
    }

    public Entry getEntry(int lump) {
        if (lump < 0 || lump >= entries.size()) {
            return DUMMY_ENTRY;
        }
        return entries.get(lump);
    }
}
