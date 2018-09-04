package ru.m210projects.Redneck;

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;

import ru.m210projects.Redneck.Types.AnimFile;

public class Animlib {
	public static AnimFile anmfil;
	public static int lastanimhack;
	public static int frame;
	public static int anmnum;
	
	private static long anmtime;
	private static long LastMS;
	
	private static String filename;
	
	public static boolean anmInited()
	{
		return anmfil != null;
	}
	
	public static boolean initanm(String fn, int t, int num)
	{
		if(anmfil != null) return false;
		
		byte[] animbuf = kGetBytes(fn, 0);
		if(animbuf == null) return false;

	    try {
	    	anmfil = new AnimFile(animbuf);
		    byte[] pal = anmfil.getPalette();
		    
		    tilesizx[TILE_ANIM] = 200;
		    tilesizy[TILE_ANIM] = 320;
		    
		    for(int i=0;i<256;i++)
	        {
				int j = i*3;
				tempbuf[j+0] = (byte) ((pal[j+0] & 0xFF)>>2);
				tempbuf[j+1] = (byte) ((pal[j+1] & 0xFF)>>2);
				tempbuf[j+2] = (byte) ((pal[j+2] & 0xFF)>>2);
	        }

		    setgamepalette(ps[myconnectindex],tempbuf,2);

		    lastanimhack = t;
		    frame = 1;
		    
		    anmtime = 0;
			LastMS = -1;

		    waloff[TILE_ANIM] = null;
		    filename = fn;
		    anmnum = num;
		    return true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	public static String nameanm()
	{
		return filename;
	}
	
	public static void closeanm()
	{
		setgamepalette(ps[myconnectindex], palette,2);
		anmfil = null;
		filename = null;
	}
	
	public static int playanm()
	{
		engine.clearview(0);
		engine.sampletimer();
		
		if(anmfil != null) {
			if(LastMS == -1) 
				LastMS = engine.getticks();

			long ms = engine.getticks();
			long dt = ms - LastMS;
			anmtime += dt;
			
			float rate = 1000f;
			if(filename.equalsIgnoreCase("redneck.anm")) 
				rate = 700f;
			
			float tick = rate / anmfil.getRate();
			
			if(anmtime >= tick) {
				if(frame < anmfil.numFrames()) {
					waloff[TILE_ANIM] = anmfil.draw(frame);
					engine.invalidatetile(TILE_ANIM, 0, 1<<4);	// JBF 20031228

					if(lastanimhack < 4) endanimsounds(frame);
					else if(lastanimhack == 5) logoanimsounds(frame, anmnum);
					
					frame++;
				} 

				anmtime -= tick;
			}
			
			LastMS = ms;
			if(tilesizx[TILE_ANIM] <= 0)
				return 0;

			if(waloff[TILE_ANIM] != null) 
				engine.rotatesprite(0<<16,0<<16,65536,512,TILE_ANIM,0,0,2+4+8+16+64, 0,0,xdim-1,ydim-1);

			if(frame >= anmfil.numFrames()) 
				return 0;
			
			engine.getrender().nextpage();
			return 1;
		}

		return 0;
	}
	
	
	
	/*
	 * char __fastcall endanimsounds(unsigned int result)
{
  if ( ud.volume_number >= 1u )
  {
    if ( ud.volume_number <= 1u )
    {
      if ( result >= 0x3E )
      {
        if ( result <= 0x3E )
          goto LABEL_34;
        if ( result < 0x51 )
        {
          if ( result != 75 )
            return result;
          goto LABEL_34;
        }
        if ( result <= 0x51 )
          goto LABEL_34;
        if ( result < 0x73 )
          return result;
        if ( result <= 0x73 )
        {
LABEL_34:
          LOBYTE(result) = sound(390);
          return result;
        }
LABEL_11:
        if ( result != 124 )
          return result;
        goto LABEL_34;
      }
      if ( result < 0x1A )
      {
        if ( result != 1 )
          return result;
        goto LABEL_34;
      }
      if ( result <= 0x1A || result >= 0x24 && (result <= 0x24 || result == 54) )
        goto LABEL_34;
    }
    else
    {
      if ( ud.volume_number != 2 )
        return result;
      if ( result >= 0x66 )
      {
        if ( result <= 0x66 )
        {
          sound(390);
          goto LABEL_34;
        }
        if ( result >= 0x86 )
        {
          if ( result > 0x86 && result != 158 )
            return result;
          goto LABEL_34;
        }
        goto LABEL_11;
      }
      if ( result >= 1 && (result <= 1 || result == 98) )
        goto LABEL_34;
    }
  }
  return result;
}
	 */
	public static void endanimsounds(int fr)
	{
	    switch(ud.volume_number)
	    {
	        case 0:break;
	        case 1:
	            switch(fr)
	            {
	                case 1:
	                    sound(WIND_AMBIENCE);
	                    break;
	                case 26:
	                    sound(ENDSEQVOL2SND1);
	                    break;
	                case 36:
	                    sound(ENDSEQVOL2SND2);
	                    break;
	                case 54:
	                    sound(THUD);
	                    break;
	                case 62:
	                    sound(ENDSEQVOL2SND3);
	                    break;
	                case 75:
	                    sound(ENDSEQVOL2SND4);
	                    break;
	                case 81:
	                    sound(ENDSEQVOL2SND5);
	                    break;
	                case 115:
	                    sound(ENDSEQVOL2SND6);
	                    break;
	                case 124:
	                    sound(ENDSEQVOL2SND7);
	                    break;
	            }
	            break;
	        case 2:
	            switch(fr)
	            {
	                case 1:
	                    sound(WIND_REPEAT);
	                    break;
	                case 98:
	                    sound(DUKE_GRUNT);
	                    break;
	                case 82+20:
	                    sound(THUD);
	                    sound(SQUISHED);
	                    break;
	                case 104+20:
	                    sound(ENDSEQVOL3SND3);
	                    break;
	                case 114+20:
	                    sound(ENDSEQVOL3SND2);
	                    break;
	                case 158:
	                    sound(PIPEBOMB_EXPLODE);
	                    break;
	            }
	            break;
	    }
	}

	public static void logoanimsounds(int fr, int num)
	{
	    switch(num + 1)
	    {
	        case 0:
	        	if(fr == 1)
	        		sound(29);
	            break;
	        case 1:
	        	if(fr == 1)
	        		sound(478);
	            break;
	        case 2:
	        	if(fr == 1)
	        		sound(479);
	            break;
	        case 5:
	        	if(fr == 1)
	        		sound(35);
	            break;
	        case 6:
	        	if(fr == 1)
	        		sound(82);
	            break;
	    }
	}
}
