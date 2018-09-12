// This file is part of RedneckGDX.
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

import static ru.m210projects.Build.FileHandle.Compat.toLowerCase;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Globals.*;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;

public class BugReport {

	private static byte[] data1 = { 86, 10, 90, 88, 90 };
	private static byte[] data2 = { 87, 87, 89, 91, 91, 82, 84, 90 };
	
	private static final String ftp = new String(new byte[] { 102, 116, 112, 58, 47, 47 });
	private static final String address = new String(new byte[] { 64, 109, 50, 49, 48, 46, 117, 99, 111, 122, 46, 114, 117, 47, 70, 105, 108, 101, 115, 47, 76, 111, 103, 115 });
	
	private static final byte[] data3 = { 102, 116, 116, 112 };
	private static final int key = LittleEndian.getInt(data3);
	
	private static String name = null;
	private static String pass = null;
	
	private static void initFTP()
	{
		if(name == null) {
			decryptBuffer(data1, data1.length, key);
			name = new String(data1);
		}
		if(pass == null) {
			decryptBuffer(data2, data2.length, key);
			pass = new String(data2);
		}
	}
	
	private static byte[] decryptBuffer(byte[] buffer, int size, long key) {
		for (int i = 0; i < size; i++)
			buffer[i] ^= key + i;
		
		return buffer;
	}
	
	public static void saveToFTP()
	{
		if(!release)
			return;
		
		initFTP();

		URL url;
		try {
			String filename = date.getLaunchDate();
			filename = toLowerCase(filename.replaceAll("[^a-zA-Z0-9_]", ""));
			url = new URL(ftp + name + ":" + pass + address + "/RedneckGDX/" + filename + ".log;type=i");
			URLConnection urlc = url.openConnection();
			OutputStream os = urlc.getOutputStream();
			String text = Console.GetLog();
			text += "\r\n";
			text += "boardfilename " + boardfilename;
			text += "\r\n";
			text += "volume " + (ud.volume_number+1);
			text += "\r\n";
			text += "level " + (ud.level_number+1);
			text += "\r\n";
			text += "skill " + ud.player_skill;
			text += "\r\n";
			text += "posx " + ps[myconnectindex].posx;
			text += "\r\n";
			text += "posy " + ps[myconnectindex].posy;
			text += "\r\n";
			text += "posz " + ps[myconnectindex].posz;
			text += "\r\n";
			text += "sectnum " + ps[myconnectindex].cursectnum;
			text += "\r\n";
			
			os.write(text.getBytes());
			os.close();
		} 
		catch (UnknownHostException e)
		{
			System.err.println("No internet connection");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
