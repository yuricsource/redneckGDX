package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.FilePath;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Redneck.currentGame;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sounds.*;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Redneck.Types.GameInfo;

public class ResourceHandler {
	
	public static int[] deftiletovox = new int[MAXTILES];
	
	private static int usergroup;
	private static boolean usecustomarts;

	public static void resetEpisodeResources()
	{
		kDynamicClear();
		usergroup = -1;
		currentGame = defGame;
		
		System.arraycopy(deftiletovox, 0, tiletovox, 0, MAXTILES); //reset user voxels
		for(int i = 0; i < NUM_SOUNDS; i++)
			Sound[i].ptr = null;

		if(!usecustomarts)
			return; 

		System.err.println("Reset to default resources");
		Arrays.fill(tilesizx, 0, MAXTILES, (short)0);
		Arrays.fill(tilesizy, 0, MAXTILES, (short)0);
		Arrays.fill(picanm, 0, MAXTILES, 0);
		Arrays.fill(waloff, 0, MAXTILES, null);
		
		if(engine.loadpics("tiles000.art") == 0)
			dassert("ART files not found " + new File(FilePath + "TILES###.ART").getAbsolutePath());
		
		LoadUserRes();
		
		tilesizy[0] = 0;
	    tilesizx[0] = 0;
	    waloff[0] = null;
	    
	    BowlReset();
	    
	    usecustomarts = false;
	}
	
	public static void InitGroupResources(List<RESHANDLE> list)
	{
		for(RESHANDLE res : list) {
			if(res.fileformat.equals("art")) {
				engine.loadpic(res.filename);
				usecustomarts = true;
			}
		}
	}
	
	private static void searchEpisodeResources(DirectoryEntry cache)
	{
		if(cache.getDirectories().size() > 0)
		{
			for (Iterator<DirectoryEntry> it = cache.getDirectories().values().iterator(); it.hasNext(); ) {
				DirectoryEntry dir = it.next();
				dir.InitDirectory(dir.getAbsolutePath());
				if(!dir.getName().equals("<userdir>"))
					searchEpisodeResources(dir);
			}
		}

		if(usergroup == -1)
			usergroup = kGroupNew("User", true);
		
		for (Iterator<FileEntry> it = cache.getFiles().values().iterator(); it.hasNext(); ) {
			FileEntry file = it.next();
			if(!file.getExtension().equals("zip")
					&& !file.getExtension().equals("grp")) 
				kGroupAdd(usergroup, file.getPath(), null, 0);
	    }
	}
	
	public static void checkEpisodeResources(GameInfo addon)
	{
		resetEpisodeResources();
		
//		if(ini.isPackage()) //if in main blood folder
//		{
//			try {
//				String ininame = ini.getFile().getName();
//				if(!currPath.equals("<main>"))
//					ininame = currPath + File.separator + ininame;
//				int gr = initgroupfile(ininame);
//				setgroupflags(gr, true, true);
//				prepareusergroup(gr, true);
//			} catch(Exception e) { 
//				GameCrash("Error found in " + ini.getFile().getName() + "\r\n" + e.getMessage()); 
//				return;
//			}
//		} else

		if(!addon.getDirectory().getName().equals("<main>"))
			searchEpisodeResources(addon.getDirectory());
		else if(addon.Title.equals("Route 66")) {
			engine.loadpic("TILESA66.ART");
			engine.loadpic("TILESB66.ART");
			usecustomarts = true;
		}
		
		//Loading user package files
		InitGroupResources(kDynamicList());
		if(addon.getCON() == null) 
			addon.setCON(loaduserdef(addon.ConName));

		currentGame = addon;
	}
}
