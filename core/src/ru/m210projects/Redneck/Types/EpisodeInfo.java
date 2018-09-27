package ru.m210projects.Redneck.Types;

import static ru.m210projects.Redneck.Globals.*;
import ru.m210projects.Build.FileHandle.DirectoryEntry;

public class EpisodeInfo {

	public String conname;
	public int episodenum;
	public String Title;
	public int nMaps;
	
	public MapInfo[] gMapInfo;
	public DirectoryEntry resDir; 

	public EpisodeInfo() {
		gMapInfo = new MapInfo[nMaxMaps + 1];
	}
	
	public void setDirectory(DirectoryEntry resDir)
	{
		this.resDir = resDir;
	}
	
	public DirectoryEntry getDirectory()
	{
		return resDir;
	}
	
	public void clear()
	{
		conname = null;
		Title = null;
		nMaps = 0;
		episodenum = 0;

		for(int i = 0; i <= nMaxMaps; i++)
			if(gMapInfo[i] != null) 
				gMapInfo[i].clear();
	}
}
