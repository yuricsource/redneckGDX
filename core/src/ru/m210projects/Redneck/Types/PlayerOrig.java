// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Kirill Klimenko-KLIMaka 
// and Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import java.nio.ByteBuffer;

import ru.m210projects.Build.Types.LittleEndian;

public class PlayerOrig {
	public static final int sizeof = 16;
	
	public int ox,oy,oz;
    public short oa,os;
    
    private byte[] buf = new byte[sizeof];
    public byte[] getBytes()
    {
    	LittleEndian.putInt(buf, 0, ox);
    	LittleEndian.putInt(buf, 4, oy);
    	LittleEndian.putInt(buf, 8, oz);
    	LittleEndian.putShort(buf, 12, oa);
    	LittleEndian.putShort(buf, 14, os);
    	return buf;
    }
    
    public void set(ByteBuffer bb)
    {
    	ox = bb.getInt();
    	oy = bb.getInt();
    	oz = bb.getInt();
    	oa = bb.getShort();
    	os = bb.getShort();
    }
}
