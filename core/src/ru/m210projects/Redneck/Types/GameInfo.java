package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.FileHandle.Cache1D.kGetBytes;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.Bstrcmp;
import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Globals.*;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.IResource;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class GameInfo {

	public DirectoryEntry resDir; 
	public String Title;
	
	public EpisodeInfo[] episodes;
	public String[] skillnames;
	public int nEpisodes;
	public String ConName;
	private Script ConScr;
	public boolean isInited = false;
	private int nMaps;
	private FileEntry pack;
	
	public GameInfo(DirectoryEntry resDir, String mainCon)
	{
		this.ConName = mainCon;
		this.Title = mainCon;
		this.resDir = resDir;
		skillnames = new String[nMaxSkills];
		episodes = new EpisodeInfo[nMaxEpisodes];
		isInited = false;
	}
	
	public GameInfo(IResource res, FileEntry name, String mainCon)
	{
		this.ConName = mainCon;
		this.Title = name.getName() + ":" + mainCon;
		skillnames = new String[nMaxSkills];
		episodes = new EpisodeInfo[nMaxEpisodes];

		try {
			List<String> list = new ArrayList<String>();
			list.add(ConName);
			for(int i = 0; i < list.size(); i++) 
				InitTree(list, res, list.get(i));
			
			nMaps = 0;
			nEpisodes = 0;
			for(int i = 0; i < list.size(); i++) {
				int fil = res.Lookup(list.get(i));
				if(fil == -1) continue;
				
				byte[] data = preparescript(res.Lock(fil));
				findSkillNames(data);
				findVolumes(data);
				findMaps(data, res);
				res.Close(fil);
			}

			if(nEpisodes != 0 && nMaps != 0) 
				isInited = true;
			checkEpisodes();
			this.pack = name;
		} catch(Exception e) { 
			e.printStackTrace(); 
			Console.Println("Build addon: " + name.getName() + " failed!", OSDTEXT_RED);
			isInited = false; 
		}
	}
	
	public FileEntry isPackage()
	{
		return pack;
	}

	public void setDirectory(DirectoryEntry resDir)
	{
		this.resDir = resDir;
	}
	
	public DirectoryEntry getDirectory()
	{
		return resDir;
	}
	
	public Script getCON()
	{
		return ConScr;
	}
	
	public void setCON(Script con)
	{
		this.ConScr = con;
	}
	
	public void init()
	{
		try {
			List<FileEntry> list = new ArrayList<FileEntry>();
			list.add(resDir.checkFile(ConName));
			for(int i = 0; i < list.size(); i++)
				InitTree(list, list.get(i));
			
			nMaps = 0;
			nEpisodes = 0;
			for(int i = 0; i < list.size(); i++) {
				FileEntry scriptfile = list.get(i);
				if(scriptfile == null) continue;
				byte[] data = preparescript(kGetBytes(scriptfile.getPath(), 0));
				findSkillNames(data);
				findVolumes(data);
				findMaps(data, null);
			}

			if(nEpisodes != 0 && nMaps != 0) 
				isInited = true;
			checkEpisodes();
			
		} catch(Exception e) { e.printStackTrace(); isInited = false; }
	}
	
	private void checkEpisodes()
	{
		int sum = 0;
		for(int e = 0; e < nEpisodes; e++)
		{
			if(episodes[e].gMapInfo[0] == null)
				episodes[e].nMaps = 0;
			sum += episodes[e].nMaps;
		}
		
		if(sum == 0) {
			isInited = false;
		} 
//		else { //sort episodes
//			int e = 0, ep = nEpisodes;
//			while(e != ep)
//			{
//				if(episodes[e] != null && episodes[e].nMaps == 0) {
//					System.arraycopy(episodes, e+1, episodes, e, ep-(e+1));
//					nEpisodes--;
//					episodes[nEpisodes] = null;
//					continue;
//				}
//				e++;
//			}
//		}
	}
	
	private void InitTree(List<FileEntry> list, FileEntry confile)
	{
		if(confile == null) return;
		byte[] buf = preparescript(kGetBytes(confile.getPath(),0));
		int index = -1;
        while( (index = indexOf("include ", buf, index+1)) != -1)
        {
        	int textptr = index + 7;
        	while( !isaltok(buf[textptr]) )
            {
                textptr++;
                if( buf[textptr] == 0 ) break;
            }

            int i = 0;
            while( textptr+i < buf.length && isaltok(buf[textptr+i]) ) i++;
            String name = new String(buf, textptr, i);
            list.add(resDir.checkFile(name));
        }
	}
	
	private void InitTree(List<String> list, IResource res, String filename)
	{
		int fil = res.Lookup(filename);
		if(fil != -1)
		{
			byte[] buf = preparescript(res.Lock(fil));
			int index = -1;
	        while( (index = indexOf("include ", buf, index+1)) != -1)
	        {
	        	int textptr = index + 7;
	        	while( !isaltok(buf[textptr]) )
	            {
	                textptr++;
	                if( buf[textptr] == 0 ) break;
	            }

	            int i = 0;
	            while( textptr+i < buf.length && isaltok(buf[textptr+i]) ) i++;
	            String name = new String(buf, textptr, i);
	            list.add(name);
	        }
	        res.Close(fil);
		}
	}
	
	private boolean findVolumes(byte[] buf)
	{
        int index = -1;

        while( (index = indexOf("definevolumename ", buf, index+1)) != -1)
        {
        	textptr = index + 16;
            Integer j = transnum(buf);
            if(j == null) continue;
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            int startptr = textptr;
            while( buf[textptr+i] != 0x0a ) i++;
            
            episodes[j] = new EpisodeInfo(new String(buf, startptr, i-1).toUpperCase());
            nEpisodes = Math.max(nEpisodes, j + 1);
        }
        
        if(nEpisodes != 0) 
	        return true;
        
        return false;
	}
	
	
	private void findMaps(byte[] buf, IResource res)
	{
		int index = -1;
		
        while( (index = indexOf("definelevelname ", buf, index+1)) != -1)
        {
        	textptr = index + 15;
            Integer epnum = transnum(buf);
            if(epnum == null) continue;
            Integer mapnum = transnum(buf);
            if(mapnum == null) continue;
            
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            int ptr = textptr;
            while( buf[textptr] != ' ' && buf[textptr] != 0x0a ) { textptr++; i++; }
            
            String path = new String(buf, ptr, i);
            
            boolean mapFound = false;
            String mapPath = path;
            if(res == null)
            {
            	FileEntry mapFile = resDir.checkFile(path);
            	mapFound = mapFile != null;
            	if(mapFound)
            		mapPath = mapFile.getPath();
            } else {
            	mapFound = res.Lookup(path) != -1;
            	mapPath = path;
            }
            
			if(mapFound) {
	            while( buf[textptr] == ' ' ) textptr++;
	
	            int partime = (((buf[textptr+0]-'0')*10+(buf[textptr+1]-'0'))*26*60)+
	                (((buf[textptr+3]-'0')*10+(buf[textptr+4]-'0'))*26);
	
	            textptr += 5;
	            while( buf[textptr] == ' ' ) textptr++;
	
	            int designertime =
	            	(((buf[textptr+0]-'0')*10+(buf[textptr+1]-'0'))*26*60)+
	            	(((buf[textptr+3]-'0')*10+(buf[textptr+4]-'0'))*26);
	
	            textptr += 5;
	            while( buf[textptr] == ' ' ) textptr++;
	
	            i = 0;
	            while( buf[textptr+i] != 0x0a ) i++;
	            String title = new String(buf, textptr, i-1);
	            episodes[epnum].gMapInfo[mapnum] = new MapInfo(mapPath, title, partime, designertime);
	            episodes[epnum].nMaps = Math.max(episodes[epnum].nMaps, mapnum + 1);
	            nMaps++;
			}
        }
	}
	
	private boolean findSkillNames(byte[] buf)
	{
		int index = -1;
		int size = 0;
        while( (index = indexOf("defineskillname ", buf, index+1)) != -1)
        {
        	textptr = index + 15;
        	Integer j = transnum(buf);
            if(j == null) continue;
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            while( buf[textptr+i] != 0x0a ) i++;
            
            skillnames[j] = new String(buf, textptr, i-1).toUpperCase();
            size = Math.max(size, j+1);  
        }
        
        if(size != 0)
        	return true;

        return false;
	}
	private int textptr;
	private char[] tempbuf = new char[2048];
	private Integer transnum(byte[] text)
	{
		while( !isaltok(text[textptr]) )
	    {
	    	textptr++;
	        if( text[textptr] == 0 )
	            return null;
	    }

	    int l = 0;
	    while( isaltok(text[textptr + l]) )
	    {
	        tempbuf[l] = (char) text[textptr + l];
	        l++;
	    }
	    
	    tempbuf[l] = 0;
	    for(int i=0;i<NUMKEYWORDS;i++)
	        if( Bstrcmp( label, (labelcnt<<6), keyw[i], 0) == 0 )
	    {
	        error++;
	        Console.Println("  * ERROR! Symbol '" + label[(labelcnt<<6)] + "' is a key word.");
	        textptr+=l;
	    }

	    for(int i=0;i<labelcnt;i++)
	    {
	        if( Bstrcmp(tempbuf, 0, label, i<<6) == 0 )
	        {
	        	textptr += l;
	            return labelcode.get(i);
	        }
	    }

	    if( !Character.isDigit(text[textptr]) && text[textptr] != '-')
	    {
	    	Console.Println("  * ERROR! Parameter '" + tempbuf[0] + "' is undefined.");
	        error++;
	        textptr+=l;
	        return null;
	    }
	    
	    String number = new String(text, textptr, l);
	    textptr += l;
	    return Integer.parseInt(number);
	}
	
}
