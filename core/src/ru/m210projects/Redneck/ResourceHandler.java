package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.FilePath;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sounds.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Redneck.Types.GameInfo;

public class ResourceHandler {
	
	public static int[] deftiletovox = new int[MAXTILES];
	
//	private static int usergroup;
	private static boolean usecustomarts;
	private static boolean userscript;
	
	public static void resetEpisodeResources()
	{
		kDynamicClear();
//		usergroup = -1;
		
		System.arraycopy(deftiletovox, 0, tiletovox, 0, MAXTILES); //reset user voxels
		for(int i = 0; i < NUM_SOUNDS; i++)
			Sound[i].ptr = null;
		
		if(userscript) {
			loaduserdef(defGame.mainCon);
			userscript = false;
		}
		
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
	
//	private static void searchEpisodeResources(DirectoryEntry cache)
//	{
//		
//	}
	
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
//		if(!path.equals("<main>"))
//			searchEpisodeResources(ini.getFile().getParent());
		
		//Loading user package files
//		InitGroupResources(kDynamicList());
		
		if(addon.Title.equals("Suckin' Grits on Route 66"))
		{
			System.err.println("Load 66");
			engine.loadpic("TILESA66.ART");
			engine.loadpic("TILESB66.ART");
			if(loaduserdef(addon.mainCon))
				userscript = true;
			usecustomarts = true;
		}
	}
}
