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
			url = new URL("ftp://" + name + ":" + pass + "@m210.ucoz.ru/Files/Logs/RedneckGDX/" + filename + ".log;type=i");
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
