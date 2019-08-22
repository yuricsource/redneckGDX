// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
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

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.PackedZipGroup;
import ru.m210projects.Build.FileHandle.ZipGroup;
import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Redneck.Types.GameInfo;

public class ResourceHandler {
	
	public static final int BACKBUTTON = 9237;
	public static final int GUTSMETTER = 9238;
	public static final int KILLSSIGN = 9239;
	public static final int WIDEHUD_PART1 = 9254;
	public static final int WIDEHUD_PART2 = 9255;

	private static Group usergroup;
	private static boolean usecustomarts;

	public static final int[][] replace = {
		{ 3363, 9217, 0x7dbfeb81 }, 
		{ 3364, 9218, 0x2cc3f6c9 }, 
		{ 3415, 9219, 0xc1230767 }, 
		{ 3416, 9220, 0xacaaa49c }, 
		{ 3417, 9221, 0x237f9b83 }, 
		{ 3418, 9222, 0x7508a5b9 }, 
		{ 3453, 9223, 0x40870de8 },
		{ 3454, 9224, 0x5d46d512 },
		{ 3455, 9225, 0xdc2832ef },
		{ 3456, 9226, 0x92ee2add },
		{ 3457, 9227, 0x6ff18f18 },
		{ 3458, 9228, 0xd4a5ae9a },
		
		{ 3483, 9231, 0x5f540506 }, //RA
		{ 3484, 9229, 0x5d46d512 },
		{ 3485, 9230, 0xdc2832ef },
		{ 3486, 9232, 0xdad4bf27 },
		{ 3487, 9233, 0xb4072cdd },
		{ 3488, 9234, 0x74adda9e },
		{ 3511, 9235, 0x7ecf8467 },
		{ 3515, 9236, 0x5c078007 },
		{ 7170, 9240, 0x3ec225f2 }, 
		{ 7171, 9241, 0xadd86032 },
		{ 7172, 9242, 0x48a62a19 },
		{ 7173, 9243, 0x9e6d81ef },
		{ 7174, 9244, 0x7533bf87 },
		{ 7175, 9245, 0x4839e578 },
		{ 7176, 9246, 0xc3361622 },
		{ 7177, 9247, 0xf2023e92 },
		{ 7178, 9248, 0x69ccdc8 },
		{ 7179, 9249, 0x4f858cef },
		{ 7180, 9250, 0xe2e2dcd7 },
		{ 7181, 9251, 0x70991197 },
		{ 7182, 9252, 0x507a5475 },
		{ 7183, 9253, 0xa91a2178 },
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

			ReplaceUserTiles();
		}
	}
		
	public static void resetEpisodeResources()
	{
		BuildGdx.cache.clearDynamicResources();

		usergroup = null;
		currentGame = defGame;
		
		for(int i = 0; i < NUM_SOUNDS; i++)
			Sound[i].ptr = null;

		if(!usecustomarts)
			return; 

		System.err.println("Reset to default resources");
		Arrays.fill(tilesizx, 0, kMaxTiles, (short)0);
		Arrays.fill(tilesizy, 0, kMaxTiles, (short)0);
		Arrays.fill(picanm, 0, kMaxTiles, 0);
		Arrays.fill(waloff, 0, kMaxTiles, null);
		
		if(engine.loadpics("tiles000.art") == 0)
			game.dassert("ART files not found " + new File(Path.Game.getPath() + "TILES###.ART").getAbsolutePath());
		
		ReplaceUserTiles();
		game.currentDef.dispose();
		game.setDefs(game.baseDef);  //return tilefromtiles textures
			
		InitSpecialTextures();
		BowlReset();

	    usecustomarts = false;
	}
	
	public static void InitGroupResources(List<GroupResource> list)
	{
		for(GroupResource res : list) {
			if(res.getExtension().equals("art")) {
				engine.loadpic(res.getFullName());
				usecustomarts = true;
			}
		}
	}
	
	public static GameInfo levelGetEpisode(String filepath)
	{
		if(filepath == null) return null;
		
		String fullname = filepath;
		String conName = null;
		int filenameIndex = -1;
		if((filenameIndex = fullname.indexOf(":")) != -1)
		{
			filepath = fullname.substring(0, filenameIndex);
			conName = fullname.substring(filenameIndex+1);
		}

		FileEntry file = BuildGdx.compat.checkFile(filepath);
		if(file != null)
		{
			GameInfo ini = null;
			if(filenameIndex == -1 && (ini = episodes.get(file.getPath())) == null)
			{
				if(file.getExtension().equals("con")) {
					ini = new GameInfo(file, file.getName());
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
						Group res = BuildGdx.cache.isGroup(file.getPath());
						if(res != null)
						{
							ini = new GameInfo(res, file, conName);
							if(ini.isInited) {
								System.err.println("load: put " + fullname);
								episodes.put(fullname, ini);
							}
							else ini = null;
						}
						res.dispose();
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
	
	public static void prepareusergroup(Group group, boolean removable) throws Exception
	{
		if(group.type == PackageType.Zip) //Correct path in archive (files shouldn't be in a subfolder)
			((ZipGroup) group).removeFolders();
		else if(group.type == PackageType.PackedZip)
			((PackedZipGroup) group).removeFolders();
		
		List<GroupResource> list = group.getList();
		for(GroupResource res : list) {
			if(res.getExtension().equals("grp") || res.getExtension().equals("zip"))
			{
				BuildGdx.cache.add(res, removable);
			}
			
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

		if(usergroup == null)
			usergroup = BuildGdx.cache.add("User", true);
		
		for (Iterator<FileEntry> it = cache.getFiles().values().iterator(); it.hasNext(); ) {
			FileEntry file = it.next();
			if(!file.getExtension().equals("zip")
					&& !file.getExtension().equals("grp")) 
				usergroup.add(file.getPath(), null, 0);
	    }
	}
	
	public static void checkEpisodeResources(GameInfo addon)
	{
		resetEpisodeResources();
		
		if(addon.isPackage())
		{
			FileEntry fil = addon.getFile();
			try {
				Group gr = BuildGdx.cache.add(fil.getPath());
				gr.setFlags(true, true);
				prepareusergroup(gr, true);
			} catch(Exception e) { 
				game.GameCrash("Error found in " + fil.getPath() + "\r\n" + e.getMessage()); 
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
		InitGroupResources(BuildGdx.cache.getDinamicResources());
		if(addon.getCON() == null) 
			addon.setCON(loaduserdef(addon.ConName));
		
		if(error == 0) {
			currentGame = addon;
			ReplaceUserTiles();
		}
		else {
			game.GameCrash("\nErrors found in " + addon.ConName + " file.");
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
	
	public static void ReplaceUserTiles()
	{
		CRC32 tilecrc32 = new CRC32();
		for(int i = 0; i < replace.length; i++)
		{
			int tilenume = replace[i][0];
			int newtile = replace[i][1];
			long crc32 = replace[i][2] & 0xFFFFFFFFL;
			
			if(game.currentDef != null && game.currentDef.texInfo.isHighTile(tilenume))
				continue;
	
			if(waloff[tilenume] == null)
				if(engine.loadtile(tilenume) == null)
					continue; //nothing replace
			
			tilecrc32.reset();
			tilecrc32.update(waloff[tilenume]);
			if(tilecrc32.getValue() != crc32)
				continue;
			
			waloff[tilenume] = new byte[tilesizx[newtile] * tilesizy[newtile]];
			System.arraycopy(waloff[newtile], 0, waloff[tilenume], 0, waloff[tilenume].length);
			tilesizx[tilenume] = tilesizx[newtile];
			tilesizy[tilenume] = tilesizy[newtile];
			picanm[tilenume] = picanm[newtile];
		}
	}

}
