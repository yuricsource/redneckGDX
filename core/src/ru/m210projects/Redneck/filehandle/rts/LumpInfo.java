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

package ru.m210projects.Redneck.filehandle.rts;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.EntryInputStream;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.InputStreamProvider;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class LumpInfo implements Entry {

    private final String name;
    private final InputStreamProvider provider;
    private final int offset;
    private final int size;
    private byte[] cache;
    Group parent;

    public LumpInfo(InputStreamProvider provider, String name, int offset, int size) {
        this.provider = provider;
        this.offset = offset;
        this.size = size;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = provider.newInputStream();
        if (is.skip(offset) != offset) {
            throw new EOFException();
        }
        return new EntryInputStream(is, size);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Group getParent() {
        return parent;
    }

    @Override
    public void setParent(Group parent) {
        this.parent = parent;
    }

    @Override
    public byte[] getBytes() {
        if (cache == null) {
            cache =  Entry.super.getBytes();
        }
        return cache;
    }
}
