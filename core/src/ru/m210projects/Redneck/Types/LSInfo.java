package ru.m210projects.Redneck.Types;

import java.nio.ByteBuffer;

public class LSInfo {
	public int skill;
	public int episode;
	public int level;
	public String info;
	public String date;
	
	public void read(ByteBuffer bb)
	{
		bb.getInt(); //ud.multimode
		episode = bb.getInt() + 1;
		level = bb.getInt() + 1;
		skill = bb.getInt();
		update();
	}
	
	public void update()
	{
		info = "Episode:" + episode + " / Level:" + level + " / Skill:" + (skill != 6 ? skill : "CM");
	}
	
	public void clear()
	{
		skill = 0;
		episode = 0;
		level = 0;
		info = "Empty slot";
		date = null;
	}
}
