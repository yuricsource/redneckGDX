package ru.m210projects.Redneck.Types;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Types.LittleEndian;

public class Weaponhit {
	
	public static final int sizeof = 90;
	
	public short cgg;
	public int picnum,ang,extra,owner,movflag;
	public int tempang,actorstayput,dispicnum;
	public int timetosleep;
	public int floorz,ceilingz,lastvx,lastvy,bposx,bposy,bposz;
	public int temp_data[] = new int[6];
	 
	 
	 
	private byte buf[] = new byte[sizeof];
	public byte[] getBytes()
	{
		 int ptr = 0;
		 LittleEndian.putShort(buf, ptr, cgg); ptr += 2;
		 LittleEndian.putInt(buf, ptr, picnum); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, ang); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, extra); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, owner); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, movflag); ptr += 4; 
		 
		 LittleEndian.putInt(buf, ptr, tempang); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, actorstayput); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, dispicnum); ptr += 4; 
		 
		 LittleEndian.putInt(buf, ptr, timetosleep); ptr += 4; 
		 
		 LittleEndian.putInt(buf, ptr, floorz); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, ceilingz); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, lastvx); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, lastvy); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, bposx); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, bposy); ptr += 4; 
		 LittleEndian.putInt(buf, ptr, bposz); ptr += 4; 
		 
		 for(int i = 0; i < 6; i++) {
			 LittleEndian.putInt(buf, ptr, temp_data[i]); ptr += 4; 
		 }
			 
		 return buf;
	}
	
	 public void set(ByteBuffer bb)
	 {
		 cgg = bb.getShort();
		 picnum = bb.getInt();
		 
		 ang = bb.getInt();
		 extra = bb.getInt();
		 owner = bb.getInt();
		 movflag = bb.getInt();
		 
		 tempang = bb.getInt();
		 actorstayput = bb.getInt();
		 dispicnum = bb.getInt();
		 
		 timetosleep = bb.getInt();
		 
		 floorz = bb.getInt();
		 ceilingz = bb.getInt();
		 lastvx = bb.getInt();
		 lastvy = bb.getInt();
		 bposx = bb.getInt();
		 bposy = bb.getInt();
		 bposz = bb.getInt();
		 
		 for(int i = 0; i < 6; i++) 
			 temp_data[i] = bb.getInt();
	 }
}
