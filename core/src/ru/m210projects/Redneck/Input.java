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

package ru.m210projects.Redneck;

import static ru.m210projects.Redneck.Globals.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Input {
	
	private static final int sizeof = 10;
	private static final int gdxsizeof = 16;
	
	public float avel;
	public float horz;
	public short fvel, svel;
	public int bits;
	
	public Input(){}
	public Input(Object data, int nVersion)
	{
		setBytes(data, nVersion);
	}
	
	public void copy(Input src)
	{
		this.fvel = src.fvel;
		this.svel = src.svel;
		this.avel = src.avel;
		this.bits = src.bits;
		this.horz = src.horz;
	}
	
	public void clear()
	{
		this.fvel = 0;
		this.svel = 0;
		this.avel = 0;
		this.bits = 0;
		this.horz = 0;
	}
	
	public int setBytes(Object data, int nVersion, int... offs) {
		int offset = 0;
		if(offs.length == 1) offset = offs[0];
		
		ByteBuffer bb = null;
		if(data instanceof byte[]) {
			bb = ByteBuffer.wrap((byte[])data);
			bb.order(ByteOrder.LITTLE_ENDIAN); 
			bb.position(offset);
		} else if(data instanceof ByteBuffer)
			bb = (ByteBuffer) data;
		else return offset;
		
		if(nVersion < GDXBYTEVERSION) 
		{
			avel = bb.get();
			horz = bb.get();
		} else {
			avel = bb.getFloat();
			horz = bb.getFloat();
		}

		fvel = bb.getShort();
		svel = bb.getShort();
		
		bits = bb.getInt();  
		
		return bb.position();
	}
	
	private int bufferVersion;
	private ByteBuffer InputBuffer;
	public byte[] getBytes(int nVersion)
	{
		if(InputBuffer == null || nVersion != bufferVersion) {
			InputBuffer = ByteBuffer.allocate(sizeof(nVersion)); 
			bufferVersion = nVersion;
		} else InputBuffer.clear();
		InputBuffer.order(ByteOrder.LITTLE_ENDIAN); 
		
		if(nVersion < GDXBYTEVERSION) {
			InputBuffer.put((byte)avel);
			InputBuffer.put((byte)horz);
		} else {
			InputBuffer.putFloat(avel);
			InputBuffer.putFloat(horz);
		}
		
		InputBuffer.putShort(fvel);
		InputBuffer.putShort(svel);

		InputBuffer.putInt(bits);
	
		return InputBuffer.array();
	}
	
	public static int sizeof(int nVersion)
	{
		int size = sizeof;
		if(nVersion >= GDXBYTEVERSION) size = gdxsizeof;
		return size;
	}
}
