package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.FilePath;
import static ru.m210projects.Build.FileHandle.Compat.cache;
import static ru.m210projects.Build.FileHandle.Compat.getFilename;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Redneck.currentGame;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sounds.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource;
import ru.m210projects.Build.FileHandle.IResource.RESHANDLE;
import ru.m210projects.Redneck.Types.GameInfo;

public class ResourceHandler {
	
	public static int[] deftiletovox = new int[MAXTILES];
	
	private static int usergroup;
	private static boolean usecustomarts;

	public static final int[][] replace = {
		{ 3363, 9217, 0x7dbfeb81 }, 
		{ 3364, 9218, 0xa5597825 }, 
		{ 3415, 9219, 0x8e7e5403 }, 
		{ 3416, 9220, 0x85e8efed }, 
		{ 3417, 9221, 0xc8593b46 }, 
		{ 3418, 9222, 0x33e84b95 }, 
		{ 3453, 9223, 0x6b3fe05b },
		{ 3454, 9224, 0x94fd4ae7 },
		{ 3455, 9225, 0xdac92bd0 },
		{ 3456, 9226, 0xe24aad2f },
		{ 3457, 9227, 0x951da8a7 },
		{ 3458, 9228, 0x1f712375 },
		
		{ 7170, 9238, 0x6ae0ef58 }, //RA
		{ 7171, 9239, 0xca7aade1 },
		{ 7172, 9240, 0x2ffabf2f },
		{ 7173, 9241, 0x1752cc40 },
		{ 7174, 9242, 0x68d8cb91 },
		{ 7175, 9243, 0xc340bd18 },
		{ 7176, 9244, 0x81906353 },
		{ 7177, 9245, 0x80f6302b },
		{ 7178, 9246, 0xc2fa1ec },
		{ 7179, 9247, 0xc7158fae },
		{ 7180, 9248, 0xb0579843 },
		{ 7181, 9249, 0xa8ce255b },
		{ 7182, 9250, 0xe303385 },
		{ 7183, 9251, 0x461043d },
	};

	public static void LoadUserRes()
	{
		FileHandle fil = Gdx.files.internal("RedneckGDX.ART");
		if(fil != null)
		{
			ByteBuffer bb = ByteBuffer.wrap(fil.readBytes());
	    	bb.order( ByteOrder.LITTLE_ENDIAN);

			int artversion = bb.getInt();
			if (artversion != 1)
				return;
			
			numtiles = bb.getInt();
			int localtilestart = bb.getInt();
			int localtileend = bb.getInt();
			if(localtilestart >= MAXTILES || localtileend >= MAXTILES)
				return;
			
			for (int i = localtilestart; i <= localtileend; i++) 
				tilesizx[i] = bb.getShort();
			for (int i = localtilestart; i <= localtileend; i++) 
				tilesizy[i] = bb.getShort();
			for (int i = localtilestart; i <= localtileend; i++)
				picanm[i] = bb.getInt();
			
			for (int tilenume = localtilestart; tilenume <= localtileend; tilenume++) {
				if(bb.position() == bb.capacity())
					break;
				int dasiz = tilesizx[tilenume] * tilesizy[tilenume];
				waloff[tilenume] = new byte[dasiz];
				bb.get(waloff[tilenume]);
			}
			bb.clear();
			bb = null;
			
			CRC32 tilecrc32 = new CRC32();
			for(int i = 0; i < replace.length; i++)
			{
				int tilenume = replace[i][0];
				int newtile = replace[i][1];
				long crc32 = replace[i][2] & 0xFFFFFFFFL;
				if(waloff[tilenume] == null)
					if(engine.loadtile(tilenume) == null)
						continue; //nothing replace
				
				tilecrc32.update(waloff[tilenume]);
				if(tilecrc32.getValue() != crc32) //RA protect
					continue;
				
				waloff[tilenume] = new byte[tilesizx[newtile] * tilesizy[newtile]];
				System.arraycopy(waloff[newtile], 0, waloff[tilenume], 0, waloff[tilenume].length);
				tilesizx[tilenume] = tilesizx[newtile];
				tilesizy[tilenume] = tilesizy[newtile];
				picanm[tilenume] = picanm[newtile];
			}
		}
	}
		
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
		
		InitSpecialTextures();
	    
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
	
	public static GameInfo levelGetEpisode(String filepath)
	{
		String fullname = filepath;
		String conName = null;
		int filenameIndex = -1;
		if((filenameIndex = fullname.indexOf(":")) != -1)
		{
			filepath = fullname.substring(0, filenameIndex);
			conName = fullname.substring(filenameIndex+1);
		}

		FileEntry file = cache.checkFile(filepath);
		if(file != null)
		{
			GameInfo ini = null;
			if(filenameIndex == -1 && (ini = episodes.get(file.getPath())) == null)
			{
				if(file.getExtension().equals("con")) {
					
					ini = new GameInfo(file.getParent(), file.getName());
					ini.init();
					if(ini.isInited)
						episodes.put(file.getPath(), ini);
				}
			} 
			else if(filenameIndex != -1 && (ini = episodes.get(fullname)) == null)
			{
				if(file.getExtension().equals("zip") 
					|| file.getExtension().equals("grp"))
				{
					try {
						IResource res = checkgroupfile(file.getPath());
						if(res != null)
						{
							ini = new GameInfo(res, file, conName);
							if(ini.isInited) {
								System.err.println("load: put " + fullname);
								episodes.put(fullname, ini);
							}
							else ini = null;
						}
						res.Dispose();
						res = null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			return ini;
		}
		return null;
	}
	
	public static void prepareusergroup(int group, boolean removable) throws Exception
	{
		//Searching and loading rfs scripts
		for(RESHANDLE res : kList(group)) {
			if(res.paktype == ZIP) //zips can use subfolders
				res.filename = getFilename(res.filename); //Correct path in archive (files shouldn't be in a subfolder)
			
			if(res.fileformat.equals("grp"))
			{
				int groupnum = initgroupfile(res.getBytes());
				setgroupflags(groupnum, true, removable);
			}
			if(res.fileformat.equals("zip"))
				throw new Exception("ZIP in groupfile not support!");

//			if(res.fileformat.equals("cue")) {
//				Console.Println("Cd tracks found...");
//				parserfs(removable?group:-1, res.filename, res.getBytes());
//			}
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
		
		FileEntry fil;
		if((fil = addon.isPackage()) != null)
		{
			try {
				int gr = initgroupfile(fil.getPath());
				setgroupflags(gr, true, true);
				prepareusergroup(gr, true);
			} catch(Exception e) { 
				GameCrash("Error found in " + fil.getPath() + "\r\n" + e.getMessage()); 
				return;
			}
		} else
		if(!addon.getDirectory().getName().equals("<main>"))
			searchEpisodeResources(addon.getDirectory());
		else if(addon.Title.equals("Route 66")) {
			engine.loadpic("TILESA66.ART");
			engine.loadpic("TILESB66.ART");
			usecustomarts = true;
		}
		
		error = 0;
		//Loading user package files
		InitGroupResources(kDynamicList());
		if(addon.getCON() == null) 
			addon.setCON(loaduserdef(addon.ConName));
		
		if(error == 0)
			currentGame = addon;
		else {
			GameCrash("\nErrors found in " + addon.ConName + " file.");
		}
	}
	
	public static void InitSpecialTextures()
	{
		tilesizx[GRID] = tilesizy[GRID] = 0;
	    waloff[GRID] = null;
		tilesizx[MIRROR] = tilesizy[MIRROR] = 0;
		
		tilesizy[13] = 0; //ROR tile
	    tilesizx[13] = 0;
	    waloff[13] = null;
	}
}
