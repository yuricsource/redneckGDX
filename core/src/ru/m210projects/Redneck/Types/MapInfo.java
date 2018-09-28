package ru.m210projects.Redneck.Types;

public class MapInfo {
	
	public String path;
	public String title;
	public int partime;
	public int designertime;
	
	public MapInfo(String path, String title, int partime, int designertime) {
		this.path = path;
		this.title = title;
		this.partime = partime;
		this.designertime = designertime;
	}
	
	public void clear()
	{
		path = null;
		title = null;
		partime = 0;
		designertime = 0;
	}
}
