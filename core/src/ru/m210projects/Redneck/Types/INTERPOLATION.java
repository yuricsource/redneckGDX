package ru.m210projects.Redneck.Types;

public class INTERPOLATION {
	
	public static final int WALLX = 1 << 0;
	public static final int WALLY = 1 << 1;
	public static final int FLOORZ = 1 << 2;
	public static final int CEILZ = 1 << 3;
	public static final int FLOORH = 1 << 4;
	
	public Object ptr;
	public int type;
	public int oldpos;
	public int bakpos;
}
