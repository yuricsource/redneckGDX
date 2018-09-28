//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import static java.lang.Math.max;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Compat.cache;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Strhandler.isdigit;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.gpmanager;
import static ru.m210projects.Redneck.Names.MENUSCREEN;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH1;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH2;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH3;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH4;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.clearsoundlocks;
import static ru.m210projects.Redneck.Sounds.sndStopMusic;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Premap.*;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Screen {
	
	public static int changepalette;
	public static int screensize;
	public static int gViewXScaled;
	public static int gViewYScaled;
	
	public static void vscrn(int size)
	{
	     int i, j, ss, x1, x2, y1, y2;

		 if(size < 0) size = 0;
		 else if(size > 4) size = 4;

		 ss = max(size-4,0);

		 x1 = scale(ss,xdim,160);
		 x2 = xdim-x1;

		 y1 = 5*ss; y2 = 200;
	     if ( size > 0 && ud.coop != 1 && ud.multimode > 1)
		 {
	         j = 0;
	         for(i=connecthead;i>=0;i=connectpoint2[i])
	             if(i > j) j = i;

	         if (j >= 1) y1 += 8;
	         if (j >= 4) y1 += 8;
	         if (j >= 8) y1 += 8;
	         if (j >= 12) y1 += 8;
		 }

		 if (size >= 4) y2 -= (4*(ss)+41);

		 y1 = scale(y1,ydim,200);
		 y2 = scale(y2,ydim,200);
		 
		 engine.setview(x1,y1,x2-1,y2-1);
		 screensize = size;
	}

	public static void setup3dscreen(int w, int h)
	{
		if(!engine.setgamemode(cfg.fullscreen, w, h))
			cfg.fullscreen = 0;
		fullscreen = cfg.fullscreen;
		
		cfg.ScreenWidth = Gdx.graphics.getWidth();
		cfg.ScreenHeight = Gdx.graphics.getHeight();
		
		gViewXScaled = (xdim << 16) / 320;
		gViewYScaled = (ydim << 16) / 200;

		engine.setbrightness(ud.brightness>>2, ps[myconnectindex].palette, 2);
	}
	
	public static void setgamepalette(PlayerStruct player, byte[] pal, int set)
	{
		if (player != ps[screenpeek]) {
			// another head
			player.palette = pal;
			return;
		}
		
		engine.setbrightness(ud.brightness>>2, pal, set);
		player.palette = pal;
		engine.setpalettefade(0,0,0,0);
	}
	
	public static void palto(int r, int g, int b, int count)
	{
		int fr = 0, fg = 0, fb = 0;
		if(r > 0) fr = count - 128;
		if(g > 0) fg = count - 128;
		if(b > 0) fb = count - 128;

		if(count > 0) {
			engine.setpalettefade(fr, fg, fb, 1);
			engine.showfade();	
		}
	}
	
	public static void scrReset()
	{
		engine.setpalettefade(0, 0, 0, 1);
		setgamepalette(ps[myconnectindex], palette, 2);
	}

	public static void myospal(int x, int y, int scale, int tilenum, int shade, int orientation, int p)
	{
	    short a = 0;
	    if((orientation&4) != 0)
	        a = 1024;
	    engine.rotatesprite(x<<16,y<<16,scale,a,tilenum,shade,p,10|orientation,windowx1,windowy1,windowx2,windowy2);
	}
	
	public static void myospal(int x, int y, int tilenum, int shade, int orientation, int p)
	{
	    short a = 0;
	    if((orientation&4) != 0)
	        a = 1024;
	    engine.rotatesprite(x<<16,y<<16,47040,a,tilenum,shade,p,10|orientation,windowx1,windowy1,windowx2,windowy2);
	}
	
	public static void myos(int x, int y, int tilenum, int shade, int orientation)
	{
	    int a = 0;
	    if((orientation&4) != 0)
	        a = 1024;

	    int p = sector[ps[screenpeek].cursectnum].floorpal;
	    engine. rotatesprite(x<<16,y<<16,65536,a,tilenum,shade,p,10|orientation,windowx1,windowy1,windowx2,windowy2);
	}
	
	public static void patchstatusbar(int x1,int y1, int x2, int y2)
	{
		if(ud.screen_size > 3)
		{
			int framesx = xdim / tilesizx[BACKGROUND];
			int framesy = ydim - scale((tilesizy[BOTTOMSTATUSBAR] + tilesizy[1649]) / 2, ydim, 200);

			int x = 0;
			for(int i = 0; i <= framesx; i++) {
		    	engine.rotatesprite(x<<16, framesy<<16, 0x10000, 0, BACKGROUND, 0, 0, 8 | 16 | 256, 0, 0, xdim-1, ydim-1);
		    	x += tilesizx[2339];
		    }
		}
		
		engine.rotatesprite(0,10878976,0x8000,0,BOTTOMSTATUSBAR,4,0,10+16+64, 
		        scale(x1,xdim,320),scale(y1,ydim,200),                             
		        scale(x2,xdim,320)-1,scale(y2,ydim,200)-1); 
	}

	public static void displayinventory(PlayerStruct p)
	{
	    int n, j, xoff, y;

	    j = xoff = 0;

	    n = (p.cowpie_amount > 0)?1<<3:0; if((n&8) != 0) j++;
	    n |= ( p.snorkle_amount > 0 )?1<<5:0; if((n&32) != 0) j++;
	    n |= (p.moonshine_amount > 0)?1<<1:0; if((n&2) != 0) j++;
	    n |= ( p.beer_amount > 0)?1<<2:0; if((n&4) != 0) j++;
	    n |= (p.whishkey_amount > 0)?1:0; if((n&1) != 0) j++;
	    n |= (p.yeehaa_amount > 0)?1<<4:0; if((n&16) != 0) j++;
	    n |= (p.boot_amount > 0)?1<<6:0; if((n&64) != 0) j++;
	    
	    xoff = 160-(j*11);

	    j = 0;

	    if(ud.screen_size > 4)
	    {
	    	y = 140; //160
	    	if(ud.multimode > 1)
	        	y = 156;
	        if(ud.multimode > 4)
	        	y -=4;
	    } else y = 180;

	    if(ud.screen_size == 4)
	    	 xoff += 56;
	    
	    while( j <= 9 )
	    {
	        if( (n&(1<<j)) != 0 )
	        {
	            switch( n&(1<<j) )
	            {
	                case   1:
	                	engine.rotatesprite(xoff<<16,y<<16,32768,0,WHISHKEY_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   2:
	                	engine.rotatesprite((xoff+1)<<16,y<<16,32768,0,MOONSHINE_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   4:
	                	engine.rotatesprite((xoff+2)<<16,y<<16,32768,0, BEER_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   8:
	                	engine.rotatesprite(xoff<<16,y<<16,32768,0,COWPIE_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  16:
	                	engine.rotatesprite(xoff<<16,y<<16,32768,0, EMPTY_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  32:
	                	engine.rotatesprite(xoff<<16,y<<16,32768,0,SNORKLE_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	                case 64:
	                	engine.rotatesprite(xoff<<16,(y-1)<<16,32768,0,BOOT_ICON,0,0,2+16,windowx1,windowy1,windowx2,windowy2);break;
	            }

	            xoff += 22;

	            if(p.inven_icon == j+1)
	            	engine.rotatesprite((xoff-2)<<16,(y+19)<<16,32768,1024,ARROW,-32,0,2+16,windowx1,windowy1,windowx2,windowy2);
	        }

	        j++;
	    }
	}
	
	public static void invennum(int x,int y,int num1,int ha,int sbits)
	{
		char[] dabuf = Globals.buf;
		
		Bitoa(num1, dabuf);
	    if(num1 > 99)
	    {
	        engine.rotatesprite(coordsConvertXScaled(x-4, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[0]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	        engine.rotatesprite(coordsConvertXScaled(x, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[1]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	        engine.rotatesprite(coordsConvertXScaled(x+4, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[2]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	    }
	    else if(num1 > 9)
	    {
	    	engine.rotatesprite(coordsConvertXScaled(x, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[0]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	    	engine.rotatesprite(coordsConvertXScaled(x+4, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[1]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	    }
	    else
	    	engine.rotatesprite(coordsConvertXScaled(x+4, sbits)<<16,coordsConvertYScaled(y)<<16,gViewYScaled>>1,0,THREEBYFIVE+dabuf[0]-'0',ha,0,sbits,0,0,xdim-1,ydim-1);
	}
	
	public static void digitalnumber(int x,int y,int n,int s,int cs)
	{
		int i, j, k, p, c;
		char[] b = Globals.buf;
		i = Bitoa(n, b);
	    j = 0;

	    for(k=0;k<i;k++)
	    {
	        p = DIGITALNUM+b[k]-'0';
	        j += (tilesizx[p] >> 1)+1;
	    }
	    c = x-(j>>1);

	    j = 0;
	    for(k=0;k<i;k++)
	    {
	        p = DIGITALNUM+b[k]-'0';
	        engine.rotatesprite((c+j)<<16,(y)<<16,32768,0,p,s,0,cs,0,0,xdim-1,ydim-1);
	        j += (tilesizx[p] >> 1)+1;
	    }
	}
	
	public static int minitext(int x,int y,char[] t, int scale, int shade, int p,int sb)
	{
	    int ac, tptr = 0;
	    while(tptr < t.length && t[tptr] != 0)
	    {
	    	t[tptr] = Character.toUpperCase(t[tptr]);
	        if(t[tptr] == 32) {x+=mulscale(5,scale, 16);tptr++;continue;}
	        else ac = t[tptr] - '!' + MINIFONT;

	        engine.rotatesprite(coordsConvertXScaled(x, sb)<<16,coordsConvertYScaled(y)<<16,mulscale(scale, gViewYScaled, 17),0,ac,shade,p,sb,0,0,xdim-1,ydim-1);
	        x += mulscale(5,scale, 16);
	        tptr++;
	    }
	    return (x);
	}

	public static int gametext(int x,int y,char[] t, int scale, int s, int pal, int dabits)
	{
	    int ac,newx;
	    boolean centre = ( x == (320>>1) );
	    newx = 0;
	   
	    int tptr = 0;

	    if(centre)
	    {
	        while(tptr < t.length && t[tptr] != 0)
	        {
	        	if((ac = getFontTile(1, t[tptr])) == -1 || ac == 0) {
		    		tptr++;
		    		continue;
		    	}

	            if(isdigit(t[tptr]))
	                newx += mulscale(10,scale, 16);
	            else newx += mulscale(tilesizx[ac]/2 + 2, scale, 16);
	            tptr++;
	        }

	        tptr = 0;
	        x = (320>>1)-(newx>>1);
	    }

	    while(tptr < t.length && t[tptr] != 0)
	    {
	    	if((ac = getFontTile(1, t[tptr])) == -1 || ac == 0) {
	    		x += mulscale(6,scale, 16);
	    		tptr++;
	    		continue;
	    	}
	        engine.rotatesprite(coordsConvertXScaled(x, dabits)<<16,coordsConvertYScaled(y)<<16,mulscale(scale, gViewYScaled, 17),0,ac,s,pal,dabits,0,0,xdim-1,ydim-1);

	        if(isdigit(t[tptr]))
	            x += mulscale(10,scale, 16);
	        else x += mulscale(tilesizx[ac]/2 + 2, scale, 16);

	        tptr++;
	    }

	    return (x);
	}

	public static int getFontTile(int nFondId, char sym)
	{
		int ac = 0;
		switch(nFondId) 
		{
			case 0:
				if(sym == ' ') return ac;
				ac = sym - '!' + MINIFONT;

				return ac;
			case 1:
				if(sym == ' ') return ac;
				ac = sym - '!' + STARTALPHANUM;
				
				if( ac < STARTALPHANUM || ac > ENDALPHANUM ) break;
				
				return ac;
			case 2:
				if(sym == ' ') return ac;
		        
		        if(isdigit(sym))
		            ac = sym - '0' + BIGALPHANUM-10;
		        else if(sym >= 'a' && sym <= 'z')
		            ac = Character.toUpperCase(sym) - 'A' + BIGALPHANUM;
		        else if(sym >= 'A' && sym <= 'Z')
		            ac = sym - 'A' + BIGALPHANUM;
		        else switch(sym)
		        {
		            case '-':
		                ac = BIGALPHANUM-11;
		                break;
		            case '.':
		                ac = BIGPERIOD;
		                break;
		            case ',':
		                ac = BIGCOMMA;
		                break;
		            case '!':
		                ac = BIGX;
		                break;
		            case '\'':
		                ac = BIGAPPOS;
		                break;
		            case '?':
		                ac = BIGQ;
		                break;
		            case ';':
		                ac = BIGSEMI;
		                break;
		            case ':':
		                ac = BIGCOLIN;
		                break;
		        }
		        return ac;
		}
		
		return -1;
	}
		
	public static int alignx, aligny;
	public static void mGetAlign(int nFontId, char[] text) {
		if ( nFontId >= 0 && nFontId < 3 ) {
			alignx = 0;
			
			if(nFontId == 0) aligny = (tilesizy[MINIFONT] / 2) + 4;
			if(nFontId == 1) aligny = (tilesizy[STARTALPHANUM] / 2) + 4;
			if(nFontId == 2) aligny = (tilesizy[BIGALPHANUM] / 2) + 1;
				
			if ( text != null ) {
				int ac, pos = 0;
				while( pos < text.length && text[pos] != 0 )
		        {
					if((ac = getFontTile(nFontId, text[pos])) == -1) {
						pos++;
						continue;
					}
					
					if(ac == 0) {
						if(nFontId == 0) alignx += 5;
						else alignx += 2;
		                pos++;
		                continue;
					}
					pos++;
					if(nFontId == 0) { alignx += 5;  continue; }
					if(nFontId == 1) {
						if(isdigit(text[pos-1])) {
							alignx += 10; 
							continue;
						}
					}
					if(nFontId == 1) alignx += (tilesizx[ac] / 2 + 2);
					else alignx += tilesizx[ac] / 2 + 1;
		        }
			}
		}
	}
	
	public static int menutext(int x,int y,int s,int p, char[] t, int bits)
	{
	    int ac, tp = 0;
	    y -= 12;
	    while(tp < t.length && t[tp] != 0)
	    {
	    	ac = getFontTile(2, t[tp]);
			if(ac == 0) {
				x += 5;
                tp++;
                continue;
			}

//	        engine.rotatesprite(coordsConvertXScaled(x, 0)<<16,coordsConvertYScaled(y)<<16,gViewXScaled>>1,0,ac,s,p,8|16|bits,0,0,xdim-1,ydim-1);
			engine.rotatesprite(x<<16,y<<16,32768,0,ac,s,p,2|8|16|bits,0,0,xdim-1,ydim-1);

	        x += tilesizx[ac] / 2 + 1;
	        tp++;
	    }
	    return (x);
	}
	
	public static int coordsConvertXScaled(int coord, int bits)
	{
		int oxdim = xdim;
		int xdim = (4 * ydim) / 3;
		int offset = oxdim - xdim;
	
		int normxofs = coord - (320 << 15);
		int wx = (xdim << 15) + scale(normxofs, xdim, 320);
		wx += (oxdim - xdim) / 2;
		
		if((bits & 256) == 256)
			return wx - offset / 2 - 1;
		if((bits & 512) == 512)
			return wx + offset / 2 - 1;

		return wx - 1;
	}
	
	public static int coordsConvertYScaled(int coord)
	{
		int ydim = (3 * xdim) / 4;
		int buildim = 200 * ydim / Engine.ydim;
		int normxofs = coord - (buildim << 15);
		int wy = (ydim << 15) + scale(normxofs, ydim, buildim);

		return wy;
	}
	
	private static char[] bonusbuf = new char[128];
	public static boolean showbonus = false;
	public static int bonuscnt = 0, tinc = 0;
	
	private static int[] checkSound = {
		8, 23, 24, 26 //Bubba pain
	};
	
	public static boolean dobonus(boolean bonusonly)
	{
		int t, gfx;
	    int i, y,xfragtotal,yfragtotal;
		int clockpad = 2;
		char[] mapname;
		
		if(!showbonus)
		{
			totalclock = 0;
			
			for(int s = 0; s < checkSound.length; s++) {
				int num = checkSound[s];
				if(Sound[num].num == 0) continue;
				Source voice = SoundOwner[num][Sound[num].num - 1].voice;
				while(voice != null && voice.isActive());
			}
			
			StopAllSounds();
		    sndStopMusic();
		    clearsoundlocks();
		    
		    bonuscnt = 0;
		    tinc = 0;

		    getInput().resetKeyStatus();
			gpmanager.resetButtonStatus();
		    showbonus = true;
		}

		engine.clearview(0);

	    if(ud.multimode > 1 && ud.coop != 1 )
	    {
	    	engine.rotatesprite(0,0,65536,0,MENUSCREEN,16,0,2+8+16+64,0,0,xdim-1,ydim-1);
	    	engine.rotatesprite(160<<16,34<<16,65536,0,INGAMELNRDTHREEDEE,0,0,10,0,0,xdim-1,ydim-1);

			buildString(bonusbuf, 0, "MULTIPLAYER TOTALS");
			mGetAlign(1, bonusbuf);
			gametext(160-alignx/2,58+2,bonusbuf,65536, 0, 0,2+8+16);
			buildString(bonusbuf, 0, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title);
			mGetAlign(1, bonusbuf);
	        gametext(160-alignx/2,58+10,bonusbuf,65536, 0, 0,2+8+16);
	   
	        t = 0;
	        
	        buildString(bonusbuf, 0, "   NAME                                           KILLS");
	        minitext(23,80,bonusbuf,65536, 0, 8,2+8+16+128);
	        for(i=0;i<numplayers;i++)
	        {
	        	Bitoa(i+1, bonusbuf);
	            minitext(92+(i*23),80,bonusbuf,65536, 0,3,2+8+16+128);
	        }

	        for(i=0;i<numplayers;i++)
	        {
	            xfragtotal = 0;
	            Bitoa(i+1, bonusbuf);

	            minitext(30,90+t,bonusbuf,65536, 0,0,2+8+16+128);
	            buildString(bonusbuf, 0, ud.user_name[i]);
	            minitext(38,90+t,bonusbuf,65536, 0, ps[i].palookup,2+8+16+128);

	            for(y=0;y<numplayers;y++)
	            {
	                if(i == y)
	                {
	                	Bitoa(ps[y].fraggedself, bonusbuf);
	                    minitext(92+(y*23),90+t,bonusbuf,65536, 0,2,2+8+16+128);
	                    xfragtotal -= ps[y].fraggedself;
	                }
	                else
	                {
	                	Bitoa(frags[i][y], bonusbuf);
	                    minitext(92+(y*23),90+t,bonusbuf,65536, 0,0,2+8+16+128);
	                    xfragtotal += frags[i][y];
	                }
	            }

	            Bitoa(xfragtotal, bonusbuf);
	            minitext(101+(8*23),90+t,bonusbuf,65536, 0,2,2+8+16+128);

	            t += 7;
	        }

	        for(y=0;y<numplayers;y++)
	        {
	            yfragtotal = 0;
	            for(i=0;i<numplayers;i++)
	            {
	                if(i == y)
	                    yfragtotal += ps[i].fraggedself;
	                yfragtotal += frags[i][y];
	            }
	            Bitoa(yfragtotal, bonusbuf);
	            minitext(92+(y*23),96+(8*7),bonusbuf,65536, 0,2,2+8+16+128);
	        }

	        buildString(bonusbuf, 0, "DEATHS");
	        minitext(45,96+(8*7),bonusbuf,65536, 0,8,2+8+16+128);
	        
	        buildString(bonusbuf, 0, "PRESS ANY KEY TO CONTINUE");
		    mGetAlign(1, bonusbuf);
		    gametext(160-alignx/2,165,bonusbuf,65536,0,0,2+8+16);
		    
		    if( ( getInput().getKey(ANYKEY) != 0 ) && totalclock > (60*2) )
                return true;
            
	    }
	 
	    if(!bonusonly && (ud.multimode < 2 || ud.coop == 1)) {
	    	int level = ud.level_number;
	    	if ( ud.volume_number != 0 ) 
	    		gfx = 408 + level;	
	        else {
	        	if(level == 0) level = 1;
	        	gfx = 402 + level;
	        }

		    if (boardfilename != null) {
				FileEntry file = cache.checkFile(boardfilename);
				mapname = file.getName().toCharArray();
				engine.rotatesprite(0, 0, 65536, 0, 403, 0, 0, 2+8+16+64, 0, 0, xdim - 1, ydim - 1);
			} else {
				engine.rotatesprite(0, 0, 65536, 0, gfx, 0, 0, 2+8+16+64, 0, 0, xdim - 1, ydim - 1);
				mapname = lastmapname;
			}
		    
		    mGetAlign(2, mapname);
		    menutext(160-alignx/2,20-6,0,0,mapname, 0);

		    buildString(bonusbuf, 0, "PRESS ANY KEY TO CONTINUE");
		    mGetAlign(2, bonusbuf);
		    menutext(155-alignx/2,192,0,0,bonusbuf,8+16);

		    int ii, ij;

			for (ii=ps[myconnectindex].player_par/(26*60), ij=1; ii>9; ii/=10, ij++) ;
				clockpad = max(clockpad,ij);
			for (ii=currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].partime/(26*60), ij=1; ii>9; ii/=10, ij++) ;
				clockpad = max(clockpad,ij);
			for (ii=currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].designertime/(26*60), ij=1; ii>9; ii/=10, ij++) ;
				clockpad = max(clockpad,ij);

			if( totalclock >= (1000000000) && totalclock < (1000000320) )
            {
                if( ((totalclock>>4)%15) == 0 && bonuscnt == 6)
                { 
                    bonuscnt++;
                    sound(425);
                    Source voice = null;
                    switch(engine.rand()&3)
                    {
                        case 0:
                        	voice = sound(BONUS_SPEECH1);
                            break;
                        case 1:
                        	voice = sound(BONUS_SPEECH2);
                            break;
                        case 2:
                        	voice = sound(BONUS_SPEECH3);
                            break;
                        case 3:
                        	voice = sound(BONUS_SPEECH4);
                            break;
                    }
                    while(voice != null && voice.isActive());
                }
            }
            else if( totalclock > (10240+120) ) { showbonus = false; return true; }
			
			int pos = 40;
            if( totalclock > (60*3) )
            {
            	buildString(bonusbuf, 0, "Yer Time:");
            	menutext(30,pos,0,0,bonusbuf,8+16);
                buildString(bonusbuf, 0, "Par time:");
                menutext(30,pos+=19,0,0,bonusbuf,8+16);
                buildString(bonusbuf, 0, "Xatrix Time:");
                menutext(30,pos+=19,0,0,bonusbuf,8+16);
                if(bonuscnt == 0)
                    bonuscnt++;

                if( totalclock > (60*4) )
                {
                    if(bonuscnt == 1)
                    {
                        bonuscnt++;
                        sound(404);
                    }
                    
                    pos = 40;
                    int num = Bitoa(ps[myconnectindex].player_par/(26*60), bonusbuf, 2);
                    buildString(bonusbuf, num, " : ", (ps[myconnectindex].player_par/26)%60, 2);
                    menutext(211,pos,0,0,bonusbuf,8+16);
                    
                    num = Bitoa(currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].partime/(26*60), bonusbuf, 2);
                    buildString(bonusbuf, num, " : ", (currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].partime/26)%60, 2);
                    menutext(211,pos+=19,0,0,bonusbuf,8+16);

                    num = Bitoa(currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].designertime/(26*60), bonusbuf, 2);
                    buildString(bonusbuf, num, " : ", (currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level-1].designertime/26)%60, 2);
                    menutext(211,pos+=19,0,0,bonusbuf,8+16);
                }
            }

            if( totalclock > (60*6) )
            {
            	pos = 106;
            	buildString(bonusbuf, 0, "Varmints Killed:");
            	menutext(30,pos,0,0,bonusbuf,8+16);
                buildString(bonusbuf, 0, "Varmints Left:");
                menutext(30,pos+=19,0,0,bonusbuf,8+16);

                if(bonuscnt == 2)
                    bonuscnt++;

                if( totalclock > (60*7) )
                {
                    if(bonuscnt == 3)
                    {
                        bonuscnt++;
                        sound(422);
                    } 
                    pos = 106;
                    Bitoa(ps[connecthead].actors_killed, bonusbuf);
                    menutext(251,pos,0,0,bonusbuf,8+16);
                    if(ud.player_skill > 3 )
                    {
                    	buildString(bonusbuf, 0, "N/A");
                    	menutext(251,pos+=19,0,0,bonusbuf,8+16);
                    }
                    else
                    {
                        if( (ps[connecthead].max_actors_killed-ps[connecthead].actors_killed) < 0 )
                        	Bitoa(0, bonusbuf);
                        else Bitoa(ps[connecthead].max_actors_killed-ps[connecthead].actors_killed, bonusbuf);
                        menutext(251,pos+=19,0,0,bonusbuf,8+16);
                    }
                }
            }
            
            if( totalclock > (60*9) )
            {
            	pos = 148;
            	buildString(bonusbuf, 0, "Secrets Found:");
            	menutext(30,pos,0,0,bonusbuf,8+16);
                buildString(bonusbuf, 0, "Secrets Missed:");
                menutext(30,pos+=19,0,0,bonusbuf,8+16);
                if(bonuscnt == 4) bonuscnt++;

                if( totalclock > (60*10) )
                {
                    if(bonuscnt == 5)
                    {
                        bonuscnt++;
                        sound(404);
                    }
                    pos = 148;
                    Bitoa(ps[myconnectindex].secret_rooms, bonusbuf);
                    menutext(251,pos,0,0,bonusbuf,8+16);
                    Bitoa(ps[myconnectindex].max_secret_rooms-ps[myconnectindex].secret_rooms, bonusbuf);
                    menutext(251,pos+=19,0,0,bonusbuf,8+16);
                }
            }

            if(totalclock > 10240 && totalclock < 10240+10240)
                totalclock = 1024;
            
            if( ( getInput().getKey(ANYKEY) != 0 ) && totalclock > (60*2) )	// JBF 20030809
            {
                if( totalclock < (60*13) )
                    totalclock = (60*13);
                else if( totalclock < (1000000000))
                   totalclock = (1000000000);
            }
	    }
		
		return false;
	}
}
