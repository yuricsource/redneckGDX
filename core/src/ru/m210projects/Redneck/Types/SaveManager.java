// This file is part of RedneckGDX
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.FileHandle.Compat.Bcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SaveManager {

	public static class SaveInfo implements Comparable<SaveInfo> {
		public String name;
		public long time;
		public String filename;

		public SaveInfo(String name, long time, String filename)
		{
			this.name = name;
			this.time = time;
			this.filename = filename;
		}
		
		public void update(String name, long time, String filename)
		{
			this.name = name;
			this.time = time;
			this.filename = filename;
		}

		@Override
		public int compareTo(SaveInfo obj) {
			return (obj.time < this.time)? -1 : 1;
		}
	}
	
	private static List<SaveInfo> SavList = new ArrayList<SaveInfo>();
	private static HashMap<String, SaveInfo> SavHash = new HashMap<String, SaveInfo>();
	
	public static SaveInfo getSlot(int num)
	{
		return SavList.get(num);
	}
	
	public static List<SaveInfo> getList()
	{
		return SavList;
	}
	
	public static void add(String savname, long time, String filename)
	{
		SaveInfo info;
		if((info = SavHash.get(filename)) == null) {
			info = new SaveInfo(savname, time, filename);
			SavList.add(0, info);
			SavHash.put(filename, info);
		} else {
			SavList.remove(info);
			info.update(savname, time, filename);
			SavList.add(0, info);
		}
	}
	
	public static void delete(String filename)
	{
		SaveInfo info;
		if((info = SavHash.get(filename)) != null) {
			File file = Bcheck(filename, "R");
			if(file != null) {
				SavList.remove(info);
				file.delete();
			}
		} 
	}
	
	public static String getLast()
	{
		if(SavList.size() > 0)
			return SavList.get(0).filename;
		return null;
	}
	
	public static void sort()
	{
		Collections.sort(SavList);
	}
}
