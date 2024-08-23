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

package ru.m210projects.Redneck.Types;

import ru.m210projects.Build.Types.Serializable;
import ru.m210projects.Build.filehandle.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayerOrig implements Serializable<PlayerOrig> {

    public int ox, oy, oz;
    public int oa, os;

    public void copy(PlayerOrig src) {
        ox = src.ox;
        oy = src.oy;
        oz = src.oz;
        oa = src.oa;
        os = src.os;
    }

    @Override
    public PlayerOrig readObject(InputStream is) throws IOException {
        ox = StreamUtils.readInt(is);
        oy = StreamUtils.readInt(is);
        oz = StreamUtils.readInt(is);
        oa = StreamUtils.readShort(is);
        os = StreamUtils.readShort(is);
        return this;
    }

    @Override
    public PlayerOrig writeObject(OutputStream os) throws IOException {
        StreamUtils.writeInt(os, ox);
        StreamUtils.writeInt(os, oy);
        StreamUtils.writeInt(os, oz);
        StreamUtils.writeShort(os, oa);
        StreamUtils.writeShort(os, this.os);

        return this;
    }
}
