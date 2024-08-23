// This file is part of RedneckGDX
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.m210projects.Build.Types.Serializable;
import ru.m210projects.Build.filehandle.StreamUtils;

public class Weaponhit implements Serializable<Weaponhit> {

    public short cgg;
    public int picnum, ang, extra, owner, movflag;
    public int tempang, actorstayput, dispicnum;
    public int timetosleep;
    public int floorz, ceilingz, lastvx, lastvy;
    public final int[] temp_data = new int[6];

    public void set(Weaponhit src) {
        cgg = src.cgg;
        picnum = src.picnum;

        ang = src.ang;
        extra = src.extra;
        owner = src.owner;
        movflag = src.movflag;

        tempang = src.tempang;
        actorstayput = src.actorstayput;
        dispicnum = src.dispicnum;

        timetosleep = src.timetosleep;

        floorz = src.floorz;
        ceilingz = src.ceilingz;
        lastvx = src.lastvx;
        lastvy = src.lastvy;

        System.arraycopy(src.temp_data, 0, temp_data, 0, 6);
    }

    @Override
    public Weaponhit readObject(InputStream is) throws IOException {
        cgg = StreamUtils.readShort(is);
        picnum = StreamUtils.readInt(is);

        ang = StreamUtils.readInt(is);
        extra = StreamUtils.readInt(is);
        owner = StreamUtils.readInt(is);
        movflag = StreamUtils.readInt(is);

        tempang = StreamUtils.readInt(is);
        actorstayput = StreamUtils.readInt(is);
        dispicnum = StreamUtils.readInt(is);

        timetosleep = StreamUtils.readInt(is);

        floorz = StreamUtils.readInt(is);
        ceilingz = StreamUtils.readInt(is);
        lastvx = StreamUtils.readInt(is);
        lastvy = StreamUtils.readInt(is);

        for (int i = 0; i < 6; i++) {
            temp_data[i] = StreamUtils.readInt(is);
        }

        return this;
    }

    @Override
    public Weaponhit writeObject(OutputStream os) throws IOException {
        StreamUtils.writeShort(os, cgg);
        StreamUtils.writeInt(os, picnum);
        StreamUtils.writeInt(os, ang);
        StreamUtils.writeInt(os, extra);
        StreamUtils.writeInt(os, owner);
        StreamUtils.writeInt(os, movflag);
        StreamUtils.writeInt(os, tempang);
        StreamUtils.writeInt(os, actorstayput);
        StreamUtils.writeInt(os, dispicnum);
        StreamUtils.writeInt(os, timetosleep);
        StreamUtils.writeInt(os, floorz);
        StreamUtils.writeInt(os, ceilingz);
        StreamUtils.writeInt(os, lastvx);
        StreamUtils.writeInt(os, lastvy);
        for (int i = 0; i < 6; i++) {
            StreamUtils.writeInt(os, temp_data[i]);
        }

        return this;
    }
}
