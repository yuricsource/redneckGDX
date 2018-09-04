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
