package ru.m210projects.Redneck.Types;

public class ANIMATION {
	
	public static final int WALLX = 1 << 0;
	public static final int WALLY = 1 << 1;
	public static final int FLOORZ = 1 << 2;
	public static final int CEILZ = 1 << 3;
	
	public Object ptr;
	public short id;
	public byte type;
	public int goal;
	public int vel;
	public int sect;
	
	public ANIMATION copy(ANIMATION src)
	{
		this.ptr = src.ptr;
		this.id = src.id;
		this.type = src.type;
		this.goal = src.goal;
		this.vel = src.vel;
		this.sect = src.sect;
		
		return this;
	}
	
}
