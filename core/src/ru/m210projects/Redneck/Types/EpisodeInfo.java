package ru.m210projects.Redneck.Types;

import static ru.m210projects.Redneck.Globals.*;

public class EpisodeInfo {

	public String Title;
	public int nMaps;
	
	public MapInfo[] gMapInfo;

	public EpisodeInfo(String title) {
		this.Title = title;
		gMapInfo = new MapInfo[nMaxMaps + 1];
	}

	public void clear()
	{
		Title = null;
		nMaps = 0;

		for(int i = 0; i <= nMaxMaps; i++)
			if(gMapInfo[i] != null) 
				gMapInfo[i].clear();
	}
}
