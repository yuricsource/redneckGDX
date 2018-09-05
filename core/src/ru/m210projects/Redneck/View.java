package ru.m210projects.Redneck;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.CLIPMASK1;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.ceilzsofslope;
import static ru.m210projects.Build.Engine.floorzsofslope;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.mirrorang;
import static ru.m210projects.Build.Engine.mirrorx;
import static ru.m210projects.Build.Engine.mirrory;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.picanm;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.spritesortcnt;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Network.mFakeMultiplayer;
import static ru.m210projects.Redneck.Redneck.boardfilename;
import static ru.m210projects.Redneck.Redneck.gShowMenu;
import static ru.m210projects.Redneck.LoadSave.gScreenCapture;
import static ru.m210projects.Redneck.Gamedef.actorscrptr;
import static ru.m210projects.Redneck.Gamedef.getincangle;
import static ru.m210projects.Redneck.Gamedef.script;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Interpolation.dointerpolations;
import static ru.m210projects.Redneck.Interpolation.restoreinterpolations;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Menus.RRMenu.*;
import static ru.m210projects.Redneck.Weapons.*;

import java.io.File;
import java.util.Arrays;

import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Premap.geoms1;
import static ru.m210projects.Redneck.Premap.geoms2;
import static ru.m210projects.Redneck.Premap.geomsector;
import static ru.m210projects.Redneck.Premap.geomx1;
import static ru.m210projects.Redneck.Premap.geomx2;
import static ru.m210projects.Redneck.Premap.geomy1;
import static ru.m210projects.Redneck.Premap.geomy2;
import static ru.m210projects.Redneck.Premap.numgeomeffects;
import static ru.m210projects.Redneck.Premap.shadeEffect;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Sector.ldist;
import static ru.m210projects.Redneck.Actors.*;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class View {
	
	public static int oyrepeat=-1;
	private static final char[] buffer = new char[256];
	
	public static int gPlayerIndex = -1;
	
	public static final int MAXUSERQUOTES = 4;
	public static int quotebot, quotebotgoal;
	public static short user_quote_time[] = new short[MAXUSERQUOTES];
	public static char user_quote[][] = new char[MAXUSERQUOTES][80];
	
	public static int cameradist = 0, cameraclock = 0;
	public static int gNameShowTime;
	
	public static void adduserquote(char[] daquote)
	{
	    for(int i=MAXUSERQUOTES-1;i>0;i--)
	    {
	    	System.arraycopy(user_quote[i-1], 0, user_quote[i], 0, 80);
	        user_quote_time[i] = user_quote_time[i-1];
	    }
	    System.arraycopy(daquote, 0, user_quote[0], 0, Math.min(daquote.length, 80));

	    user_quote_time[0] = 180;
	}
	
	public static void displayrest(int smoothratio)
	{
	    int a, i, j;

	    int cposx,cposy;
	    PlayerStruct pp = ps[screenpeek];

	    float cang = 0;

	    int cr = 0, cg = 0, cb = 0, cf = 0;
	    boolean dotint = false;

	    if((gScreenCapture || gShowMenu) && pp.newowner < 0)
			return;
	    
	    if( changepalette != 0 )
	    {
	    	setgamepalette(pp,pp.palette, 2);
	        changepalette = 0;
	    }

	    if( pp.pals_time > 0 && pp.loogcnt == 0)
	    {
	        dotint = true;
	    	cr = pp.pals[0];
	    	cg = pp.pals[1];
	    	cb = pp.pals[2];
	    	cf = pp.pals_time;
	    }

	    if (dotint) 
		    palto(cr,cg,cb,cf|128);

	    i = pp.cursectnum;

	    if(i != -1) {
	    	show2dsector[i>>3] |= (1<<(i&7));

		    int startwall = sector[i].wallptr;
		    int endwall = startwall + sector[i].wallnum;
		    
		    for(j = startwall; j < endwall; j++)
		    {
		    	WALL wal = wall[j];
		        i = wal.nextsector;
		        if (i < 0) continue;
		        if ((wal.cstat&0x0071) != 0) continue;
		        if ((wall[wal.nextwall].cstat&0x0071) != 0) continue;
		        if (sector[i].lotag == 32767) continue;
		        if (sector[i].ceilingz >= sector[i].floorz) continue;
		        show2dsector[i>>3] |= (1<<(i&7));
		    }
	    }

	    if(ud.camerasprite == -1)
	    {
	        if( ud.overhead_on != 2 && pp.newowner < 0)
	        {
                displayweapon(screenpeek);
                if(pp.over_shoulder_on == 0 )
                    displaymasks(screenpeek);
	        }

	        if( ud.overhead_on > 0 )
	        {
	                smoothratio = min(max(smoothratio,0),65536);
	                dointerpolations(smoothratio);
	                if( !ud.scrollmode )
	                {
	                     if(pp.newowner == -1)
	                     {
	                         if (screenpeek == myconnectindex && numplayers > 1)
	                         {
	                             cposx = omyx+mulscale((myx-omyx),smoothratio, 16);
	                             cposy = omyy+mulscale((myy-omyy),smoothratio, 16);
	                             cang = omyang+((((BClampAngle(myang+1024-omyang))-1024) * smoothratio) / 65536.0f);
	                         }
	                         else
	                         {
	                              cposx = pp.oposx+mulscale((pp.posx-pp.oposx),smoothratio, 16);
	                              cposy = pp.oposy+mulscale((pp.posy-pp.oposy),smoothratio, 16);
	                              cang = pp.oang + (BClampAngle(pp.ang+1024-pp.oang)-1024) * smoothratio / 65536.0f;
	                         }
	                    }
	                    else
	                    {
	                        cposx = pp.oposx;
	                        cposy = pp.oposy;
	                        cang = pp.oang;
	                    }
	                }
	                else
	                {

	                     ud.fola += ud.folavel / 8f;
	                     ud.folx += (ud.folfvel*sintable[(512+2048-ud.fola)&2047])>>14;
	                     ud.foly += (ud.folfvel*sintable[(512+1024-512-ud.fola)&2047])>>14;

	                     cposx = ud.folx;
	                     cposy = ud.foly;
	                     cang = ud.fola;
	                }

	                if(ud.overhead_on == 2)
	                {
	                    engine.clearview(0);
	                    engine.drawmapview(cposx,cposy,pp.zoom, (short)cang);
	                }
	                engine.drawoverheadmap( cposx,cposy,pp.zoom, (short)cang);

	                restoreinterpolations();

	                if(ud.overhead_on == 2)
	                {
	                    if(ud.screen_size > 0) a = 145;
	                    else a = 182;

	                    if(gEndGame != 0)
	                    {
	                    	Arrays.fill(buffer, (char)0);
	                    	buildString(buffer, 0, "Close Encounters");
		                    minitext(5,a+6,buffer,65536,0,0,8+16+256);
	                    } else {
		                    minitext(5,a,volume_names[ud.volume_number],65536, 0,0,8+16+256);
		                    minitext(5,a+6,level_names[ud.volume_number*11 + ud.level_number],65536,0,0,8+16+256);
	                    }
	                    
	                    if ( cfg.gShowStat == 2 ) {
	                    	int k = 0;
	                    	if (ud.coop != 1 && ud.screen_size > 0 && ud.multimode > 1)
	                    	{
		               	         j = 0; k = 8;
		               	         for(i=connecthead;i>=0;i=connectpoint2[i])
		               	             if (i > j) j = i;
	
		               	         if (j >= 4 && j <= 8) k += 8;
		               	         else if (j > 8 && j <= 12) k += 16;
		               	         else if (j > 12) k += 24;
	                    	}
	            	    	viewDrawStats(10, 35+k, cfg.gStatSize);
	                    }
	                }
	        }
	    }

	    coolgaugetext(screenpeek);
	    operatefta();

	    if(ps[myconnectindex].newowner == -1 && ud.overhead_on == 0 && ud.crosshair != 0 && ud.camerasprite == -1)
	        engine.rotatesprite((160-(ps[myconnectindex].look_ang>>1))<<16,100<<16,cfg.gCrossSize,0,CROSSHAIR,0,0,2+1,windowx1,windowy1,windowx2,windowy2);

	    if ( cfg.gShowStat == 1 ) {
//	    	int y = 202;
//	    	if(ud.screen_size == 2) y = 168;
//	    	if(ud.screen_size == 1) y = 172;
//	    	if(ud.screen_size >= 3) y = 158;
//	    	viewDrawStats(10, y, cfg.gStatSize);
	    	
	    	int k = 0;
        	if (ud.coop != 1 && ud.screen_size > 0 && ud.multimode > 1)
        	{
       	         j = 0; k = 8;
       	         for(i=connecthead;i>=0;i=connectpoint2[i])
       	             if (i > j) j = i;

       	         if (j >= 4 && j <= 8) k += 8;
       	         else if (j > 8 && j <= 12) k += 16;
       	         else if (j > 12) k += 24;
        	}
	    	viewDrawStats(10, 35+k, cfg.gStatSize);
	    }

	    if((gm&MODE_GAME) != 0 && totalclock < gNameShowTime)
		{
			int transp = 0;
			if(totalclock > gNameShowTime - 20) transp = 1;
			if(totalclock > gNameShowTime - 10) transp = 33;
		
			if(cfg.showMapInfo != 0 && !gShowMenu)
			{	
				char[] mapname;
				if(boardfilename == null) {
					if(gEndGame != 0)
					{
						buildString(buffer, 0, "Close Encounters");
						mapname = buffer;
					} else mapname = level_names[(ud.volume_number*11)+ud.level_number];
				}
				else {
					Arrays.fill(buffer, (char)0);
					int index = boardfilename.lastIndexOf(File.separator);
					boardfilename.getChars(index+1, boardfilename.length(), buffer, 0);
					mapname = buffer;
				}
				mGetAlign(2, mapname);

				menutext(160 - alignx / 2, 114, -128, 0, mapname, transp);
			}
		}
	    
	    if(MODE_TYPE)
	    	typemode();
	    
	    if( ud.pause_on==1 && !gShowMenu )
	    {
	    	buildString(buffer, 0, "GAME PAUSED");
	    	mGetAlign(2, buffer);
	    	menutext(160 - alignx / 2,100,0,0,buffer, 0);
	    }
	    
	    if(gPlayerIndex != -1 && gPlayerIndex != myconnectindex)
	    {
	    	if(ud.user_name[gPlayerIndex] == null || ud.user_name[gPlayerIndex].isEmpty())
	    		buildString(buf, 0, "Player ", gPlayerIndex+1);
	    	else buildString(buf, 0, ud.user_name[gPlayerIndex]);
        	int shade = 16 - (totalclock & 0x3F);

        	int y = scale(windowy1, 200, ydim)+100;
        	if(ud.screen_size <= 3) //XXX
        		y += (tilesizy[BOTTOMSTATUSBAR] + tilesizy[1649]) / 4;

        	gametext(160,y,buf,65536,shade,0,8+16);
	    }

	    if(ud.coords != 0)
	    	coords(screenpeek);
	}

	public static void typemode()
	{
		if(Console.IsShown()) return;
		
		int j = 200-8;
		if (ud.screen_size > 0) j = 200-45;
		
		char[] buf = getInput().getMessageBuffer();
		int len = getInput().getMessageLength() + 1;
		if(len < buf.length)
			buf[len] = 0;
		gametext(320>>1,j,getInput().getMessageBuffer(),65536, 0,0,8+16);
		
		mGetAlign(1, buf);
		engine.rotatesprite((((320+alignx+16)>>1))<<16,(j+6)<<16,4096,0,SPINNINGNUKEICON+(((totalclock>>3))&15),0,0,10,0,0,xdim-1,ydim-1);
	}
	
	public static void operatefta()
	{
	     int i, j, k;

	     if(ud.screen_size > 0) j = 200-45; else j = 200-8;
	     quotebot = Math.min(quotebot,j);
	     quotebotgoal = Math.min(quotebotgoal,j);
	     if(MODE_TYPE) j -= 8;
	     quotebotgoal = j; j = quotebot;
	     for(i=0;i<MAXUSERQUOTES;i++)
	     {
	    	 k = user_quote_time[i]; if (k <= 0) break;
	         if (k > 4)
	              gametext(320>>1,j,user_quote[i],65536,0,0,8+16);
	         else if (k > 2) gametext(320>>1,j,user_quote[i],65536,0,0,8+16+1);
	             else gametext(320>>1,j,user_quote[i],65536,0,0,8+16+1+32);
	         j -= 8;
	     }

	     if (ps[screenpeek].fta <= 1) return;

	     if (ud.coop != 1 && ud.screen_size > 0 && ud.multimode > 1)
	     {
	         j = 0; k = 8;
	         for(i=connecthead;i>=0;i=connectpoint2[i])
	             if (i > j) j = i;

	         if (j >= 4 && j <= 8) k += 8;
	         else if (j > 8 && j <= 12) k += 16;
	         else if (j > 12) k += 24;
	     }
	     else k = 0;

	     if (ps[screenpeek].ftq == 115 || ps[screenpeek].ftq == 116)
	     {
	         k = quotebot;
	         for(i=0;i<MAXUSERQUOTES;i++)
	         {
	             if (user_quote_time[i] <= 0) break;
	             k -= 8;
	         }
	         k -= 4;
	     }

	     j = ps[screenpeek].fta;
	     if (j > 4)
	          gametext(320>>1,k,fta_quotes[ps[screenpeek].ftq],65536,0,0,8+16);
	     else
	         if (j > 2) gametext(320>>1,k,fta_quotes[ps[screenpeek].ftq],65536,0,0,8+16+1);
	     else
	         gametext(320>>1,k,fta_quotes[ps[screenpeek].ftq],65536,0,0,8+16+1+32);
	}
	
	public static void displayfragbar(int yoffset, boolean showpalette)
	{
		int row = (ud.multimode - 1) / 4;
		if(row >= 0)
		{
			int framesx = 2 * xdim / tilesizx[BACKGROUND];
			int framesy = mulscale(tilesizy[FRAGBAR] / 2 * (row + 1), divscale(ydim, 200, 16), 16);

			int x = 0;
			for(int i = 0; i <= framesx; i++) {
		    	engine.rotatesprite(x<<16, 0, 32768, 0, BACKGROUND, 0, 0, 8 | 16 | 256, 0, 0, xdim-1, framesy);
		    	x += tilesizx[BACKGROUND] / 2;
		    }
			
			if(yoffset > 0) yoffset -= 9 * row;
			for(int r = 0; r <= row; r++) 
				engine.rotatesprite(0,(r * tilesizy[FRAGBAR]) << 16,34000,0,FRAGBAR,0,0,2+8+16+64,0,0,xdim-1,ydim-1);

			for(int i=connecthead;i>=0;i=connectpoint2[i])
		    {
				if(ud.user_name[i] == null || ud.user_name[i].isEmpty())
					buildString(buffer, 0, "Player ", i+1);
				else buildString(buffer, 0, ud.user_name[i]);
				
		        minitext(26+(73*(i&3)),2+((i&28)<<1),buffer,65536,0,sprite[ps[i].i].pal,8+16);
		        buildString(buffer, 0, "", ps[i].frag-ps[i].fraggedself);
		        minitext(23+50+(73*(i&3)),2+((i&28)<<1),buffer,65536,0,sprite[ps[i].i].pal,8+16);
		    }
		}
	}
	
	public static void displaymeters(int snum)
	{
		PlayerStruct p = ps[snum];

		p.alcohol_meter = (short) ((8 * p.alcohol_amount + 1647) & 2047);
		if(p.alcohol_amount >= 100)
		{
			p.alcohol_amount = 100;
			p.alcohol_meter = 400;
		}  
		engine.rotatesprite(16842752, 11862016, 0x8000, p.alcohol_meter, 62, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(19202048, 11862016, 0x8000, p.gut_meter, 62, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
		
		int x, pic;
		if(p.alcohol_amount >= 0 && p.alcohol_amount <= 30)
		{
			x = 15663104;
			pic = 920;
		}
		else if(p.alcohol_amount >= 31 && p.alcohol_amount <= 65)
		{
			x = 16252928;
			pic = 921;
		} 
		else if(p.alcohol_amount >= 66 && p.alcohol_amount <= 87)
		{
			x = 0x1000000;
			pic = 922;
		}
		else 
		{
			x = 17367040;
			pic = 923;
		}
		engine.rotatesprite(x, 12451840, 0x8000, 0, pic, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
		
		if(p.gut_amount >= 0 && p.gut_amount <= 30)
		{
			x = 18087936;
			pic = 920;
		}
		else if(p.gut_amount >= 31 && p.gut_amount <= 65)
		{
			x = 18677760;
			pic = 921;
		} 
		else if(p.gut_amount >= 66 && p.gut_amount <= 87)
		{
			x = 19267584;
			pic = 922;
		}
		else 
		{
			x = 19791872;
			pic = 923;
		}
		engine.rotatesprite(x, 12451840, 0x8000, 0, pic, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	}
	
	public static void coolgaugetext(int snum)
	{
	    int i, o, ss;

	    PlayerStruct p = ps[snum];

	    if (p.invdisptime > 0) 
	    	displayinventory(p);

	    if(screenpeek != myconnectindex)
		{
        	if(ud.user_name[screenpeek] == null || ud.user_name[screenpeek].isEmpty())
        		buildString(buf, 0, "View from player ", screenpeek+1);
        	else buildString(buf, 0, "View from ", ud.user_name[screenpeek]);
        	int shade = 16 - (totalclock & 0x3F);

        	gametext(160, scale(windowy1, 200, ydim) + 10, buf,65536,shade,0,8+16);
		}

	    ss = ud.screen_size; if (ss < 1) return;

	    if ( (ud.multimode > 1 || mFakeMultiplayer) && ud.coop != 1 )
	    {
	    	displayfragbar(0, true);
	    }

	    if (ss == 1)   //DRAW MINI STATUS BAR:
	    {
	    	engine.rotatesprite(0x20000, 11272192, 0x8000, 0, HEALTHBOX, 0, 21, 26 | 256, 0, 0, xdim - 1, ydim - 1);

	        if(sprite[p.i].pal == 1 && p.last_extra < 2)
	            digitalnumber(20,200-17,1,-16,10+16+256);
	        else digitalnumber(20,200-17,p.last_extra,-16,10+16+256);


	        int x = 41;
	        if (p.curr_weapon == HANDREMOTE_WEAPON) i = CROSSBOW_WEAPON; else i = p.curr_weapon;
	        if(p.ammo_amount[i] != 0) {
		        engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,AMMOBOX,0,21,26|256,0,0,xdim-1,ydim-1);
		        digitalnumber(x+16,200-17,p.ammo_amount[i],-16,10+16+256);
		        x += tilesizx[AMMOBOX] / 2 + 2;
	        }
	        
	        if((p.gotkey[0]|p.gotkey[1]|p.gotkey[2]) != 0) {
	        	engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,AMMOBOX,0,21,26|256,0,0,xdim-1,ydim-1);
		        engine.rotatesprite(x<<16,(200-28)<<16,0x8000,0,9216,0,21,10+16+256,0,0,xdim-1,ydim-1);
		        
		        if ( p.gotkey[3] != 0 )
		    		engine.rotatesprite(x+5<<16,182<<16, 0x8000, 0, 1656, 0, 23, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	if ( p.gotkey[2] != 0  )
		    		engine.rotatesprite(x+18<<16,182<<16, 0x8000, 0, 1656, 0, 21, 10+16+256, 0, 0, xdim - 1, ydim - 1);
		    	if ( p.gotkey[1] != 0  )
		    		engine.rotatesprite(x+11<<16,189<<16, 0x8000, 0, 1656, 0, 0, 10+16+256, 0, 0, xdim - 1, ydim - 1); 
		        x += tilesizx[9216] / 2 + 2;
	        }
	      
	        if (p.inven_icon != 0)
	        {
	        	engine.rotatesprite(x<<16, (200-30)<<16, 0x8000, 0, INVENTORYBOX, 0, 21, 26 | 256, 0, 0, xdim - 1, ydim - 1);
	        	buf[0] = '%';
	        	buf[1] = 0;
	            switch(p.inven_icon)
	            {
	                case 1: i = 1645; minitext(x+37,190,buf,65536,0,0,8+16+256); break;
	                case 2: i = 1654; minitext(x+37,190,buf,65536,0,0,8+16+256); break;
	                case 3: i = 1655; break;
	                case 4: i = 1652; break;
	                case 5: i = 1646; break;
	                case 6: i = 1653; break;
	                case 7: i = BOOT_ICON; break;
	                default: i = -1;
	            }
	            if (i >= 0) engine.rotatesprite((x+6)<<16, (200-21)<<16, 0x8000, 0, i, 0, 0, 26 | 256, 0, 0, xdim - 1, ydim - 1);

	            buildString(buf, 0, "AUTO");
	            if (p.inven_icon >= 6) minitext(x+22, 180,buf,65536,0, 2,8+16+256);

	            switch(p.inven_icon)
	            {
	            case 1: i = p.whishkey_amount; break;
                case 2: i = ((p.moonshine_amount+3)>>2); break;
                case 3: i = ((p.beer_amount)/400); break;
                case 4: i = ((p.cowpie_amount)/100); break;
                case 5: i = p.empty_amount/12; break;
                case 6: i = ((p.snorkle_amount+63)>>6); break;
                case 7: i = (p.boot_amount >> 1); break;
	            }
	            invennum(x+27, 194, i, 0, 8 | 256);
	        }
	        return;
	    }

	    //DRAW/UPDATE FULL STATUS BAR:

        patchstatusbar(0,0,320,200);
        
        if(ss > 2) {
	        engine.rotatesprite(0, 10354688, 0x8020, 0, 1649, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	        int wpic = 930;
	        for(int w = 0; w < 9; w++, wpic++)
	        {
	        	if(p.gotweapon[w+1])
	        		engine.rotatesprite((32 * w + 18) << 16, 10485760, 0x8020, 0, wpic, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	        	invennum(32 * w + 38, 160, p.ammo_amount[w+1], 0, 8+16);
	        }
        }
        
        if (ud.multimode > 1 && ud.coop != 1)
        	engine.rotatesprite(142<<16,(169) << 16,65536,0,KILLSICON,0,0,10+16,0,0,xdim-1,ydim-1);
	    
	    if (ud.multimode > 1 && ud.coop != 1)
	    {
            digitalnumber(150,180,max(p.frag-p.fraggedself,0),-16,10+16); 
	    }
	    else
	    {
	    	if ( p.gotkey[3] != 0 )
	    		engine.rotatesprite(9175040, 11927552, 0x8000, 0, 1656, 0, 23, 10+16, 0, 0, xdim - 1, ydim - 1);
	    	if ( p.gotkey[2] != 0  )
	    		engine.rotatesprite(10027008, 11927552, 0x8000, 0, 1656, 0, 21, 10+16, 0, 0, xdim - 1, ydim - 1);
	    	if ( p.gotkey[1] != 0  )
	    		engine.rotatesprite(9568256, 12386304, 0x8000, 0, 1656, 0, 0, 10+16, 0, 0, xdim - 1, ydim - 1);
	    }
	   
        if(sprite[p.i].pal == 1 && p.last_extra < 2)
            digitalnumber(64,200-17,1,-16,10+16);
        else digitalnumber(64,200-17,p.last_extra,-16,10+16);
	    
        if (p.curr_weapon != KNEE_WEAPON)
        {
            if (p.curr_weapon == HANDREMOTE_WEAPON) i = CROSSBOW_WEAPON; else i = p.curr_weapon;
            digitalnumber(107,200-17,p.ammo_amount[i],-16,10+16);
        }

        i = 0;
        if (p.inven_icon != 0)
        {
        	o = 11730944;
        	buf[0] = '%';
        	buf[1] = 0;
            switch(p.inven_icon)
            {
                case 1: i = 1645; o = 11665408; minitext(216,190,buf,65536,0, 6,8+16); break;
                case 2: i = 1654; o = 11665408;  minitext(216,190,buf,65536,0, 6,8+16); break;
                case 3: i = 1655; break;
                case 4: i = 1652; break;
                case 5: i = 1646; break;
                case 6: i = 1653; o = 11534336; break;
                case 7: i = BOOT_ICON; o = 11665408; break;
            }
            engine.rotatesprite(11993088,o,32768,0,i,0,0,10+16,0,0,xdim-1,ydim-1);
         
            buildString(buf, 0, "AUTO");
            if (p.inven_icon >= 6) minitext(201, 180,buf,65536,0, 2,8+16);

            switch(p.inven_icon)
            {
                case 1: i = p.whishkey_amount; break;
                case 2: i = ((p.moonshine_amount+3)>>2); break;
                case 3: i = ((p.beer_amount)/400); break;
                case 4: i = ((p.cowpie_amount)/100); break;
                case 5: i = p.empty_amount/12; break;
                case 6: i = ((p.snorkle_amount+63)>>6); break;
                case 7: i = (p.boot_amount / 10 >> 1); break;
            }
            invennum(206, 194, i, 0, 8);
            
        }
        displaymeters(screenpeek);
	}
	
	public static void displayrooms(int snum,int smoothratio)
	{
	    int cposx,cposy,cposz,dst,j,fz,cz;
	    short sect, k;
	    float cang, choriz;
	    int tposx,tposy,i;
	    short tang;

	    PlayerStruct p = ps[snum];

	    gPlayerIndex = -1;
	    
	    if( (!gShowMenu && ud.overhead_on == 2) || isOpened(mMenus[HELP]) || p.cursectnum == -1)
	    	return;

	    smoothratio = min(max(smoothratio,0),65536);

	    visibility = p.visibility;

	    if(ud.pause_on != 0 || ps[snum].on_crane > -1) smoothratio = 65536;

	    sect = p.cursectnum;
	    if(sect < 0 || sect >= MAXSECTORS) return;

	    dointerpolations(smoothratio);

	    if(ud.camerasprite >= 0)
	    {
	        SPRITE s = sprite[ud.camerasprite];

	        if(s.yvel < 0) s.yvel = -100;
	        else if(s.yvel > 199) s.yvel = 300;

	        cang = (hittype[ud.camerasprite].tempang+mulscale((((s.ang+1024-hittype[ud.camerasprite].tempang)&2047)-1024),smoothratio, 16));

//	        se40code(s.x,s.y,s.z,cang,s.yvel,smoothratio);

	        engine.drawrooms(s.x,s.y,s.z-(4<<8),cang,s.yvel,s.sectnum);
	        animatesprites(s.x,s.y,s.z-(4<<8),(short)cang,smoothratio);
	        engine.drawmasks();
	    }
	    else
	    {
	        i = (int) divscale(1,sprite[p.i].yrepeat+28, 22);
	        if (i != oyrepeat)
	        {
	        	oyrepeat = i;
	            vscrn(ud.screen_size);
	        }

	        if( ( ud.screen_tilting != 0 && p.rotscrnang != 0 ) )
	        {
                if (ud.screen_tilting != 0) tang = p.rotscrnang; else tang = 0;

                engine.getrender().settiltang(p.orotscrnang + mulscale(((p.rotscrnang - p.orotscrnang + 1024)&2047)-1024,smoothratio, 16));
        		p.orotscrnang = p.rotscrnang;	// JBF: save it for next time 	  
	        } else engine.getrender().settiltang(0);

	          if ( (snum == myconnectindex) && (numplayers > 1) )
              {
                    cposx = omyx+mulscale((myx-omyx),smoothratio,16);
                    cposy = omyy+mulscale((myy-omyy),smoothratio,16);
                    cposz = omyz+mulscale((myz-omyz),smoothratio,16);
                    cang = omyang + (BClampAngle(myang+1024-omyang)-1024) * smoothratio / 65536.0f;
                    choriz = omyhoriz+omyhorizoff+(((myhoriz+myhorizoff-omyhoriz-omyhorizoff) * smoothratio) / 65536.0f);
                    sect = mycursectnum;
              }
              else
              {
                    cposx = p.oposx+mulscale((p.posx-p.oposx),smoothratio,16);
                    cposy = p.oposy+mulscale((p.posy-p.oposy),smoothratio,16);
                    cposz = p.oposz+mulscale((p.posz-p.oposz),smoothratio,16);
                    cang = p.oang + (BClampAngle(p.ang+1024-p.oang)-1024) * smoothratio / 65536.0f;
                    choriz = (p.ohoriz+p.ohorizoff+((p.horiz+p.horizoff-p.ohoriz-p.ohorizoff) * smoothratio) / 65536.0f);
              }
              cang += p.look_ang;

              if (p.newowner >= 0)
              {
                    cang = (short) (p.ang+p.look_ang);
                    choriz = p.horiz+p.horizoff;
                    cposx = p.posx;
                    cposy = p.posy;
                    cposz = p.posz;
                    sect = sprite[p.newowner].sectnum;
                    smoothratio = 65536;
              }

              else if( p.over_shoulder_on == 0 )
            	  cposz += p.opyoff+mulscale((p.pyoff-p.opyoff),smoothratio, 16);
              else {
            	  view(p,cposx,cposy,cposz,sect,cang,choriz); 
            	  
            	  cposx = viewout.ox;
            	  cposy = viewout.oy;
            	  cposz = viewout.oz;
            	  sect = viewout.os;
              }

	        cz = hittype[p.i].ceilingz;
	        fz = hittype[p.i].floorz;

	        if(earthquaketime > 0 && p.on_ground)
	        {
	            cposz += 256-(((earthquaketime)&1)<<9);
	            cang += (2-((earthquaketime)&2))<<2;
	        }

	        if(sprite[p.i].pal == 1) cposz -= (18<<8);

	        if(p.newowner >= 0)
	            choriz = (short) (100+sprite[p.newowner].shade);
	        else if(p.spritebridge == 0)
	        {
	            if( cposz < ( p.truecz + (4<<8) ) ) cposz = cz + (4<<8);
	            else if( cposz > ( p.truefz - (4<<8) ) ) cposz = fz - (4<<8);
	        }

	        if (sect >= 0)
	        {
	            engine.getzsofslope(sect,cposx,cposy);
	            if (cposz < ceilzsofslope+(4<<8)) cposz = ceilzsofslope+(4<<8);
	            if (cposz > floorzsofslope-(4<<8)) cposz = floorzsofslope-(4<<8);
	        }

	        if(choriz > 299) choriz = 299;
	        else if(choriz < -99) choriz = -99;

//	        se40code(cposx,cposy,cposz,cang,choriz,smoothratio); XXX

	        if ((gotpic[MIRROR>>3]&(1<<(MIRROR&7))) > 0)
	        {
	            dst = 0x7fffffff; i = 0;
	            for(k=0;k<mirrorcnt;k++)
	            {
	                j = (int) klabs(wall[mirrorwall[k]].x-cposx);
	                j += klabs(wall[mirrorwall[k]].y-cposy);
	                if (j < dst) { dst = j; i = k; }
	            }

	            if( wall[mirrorwall[i]].overpicnum == MIRROR )
	            {
	                engine.preparemirror(cposx,cposy,cposz,cang,choriz,mirrorwall[i],mirrorsector[i]);

	                tposx = mirrorx;
	                tposy = mirrory;
	        		tang = (short) mirrorang; 
	        		
	                j = visibility;
	                visibility = (j>>1) + (j>>2);

	                engine.drawrooms(tposx,tposy,cposz,tang,choriz,mirrorsector[i]+MAXSECTORS);

	                display_mirror = 1;
	                animatesprites(tposx,tposy,cposz,tang,smoothratio);
	                display_mirror = 0;

	                engine.drawmasks();
	                engine.completemirror();   //Reverse screen x-wise in this function
	                visibility = j;
	            }
	            gotpic[MIRROR>>3] &= ~(1<<(MIRROR&7));
	        }

	        engine.drawrooms(cposx,cposy,cposz,cang,choriz,sect);
	        animatesprites(cposx,cposy,cposz,(short)cang,smoothratio);
	        engine.drawmasks();
	        
	        displaygeom3d(sect, cposx, cposy, cposz, choriz, cang, sect, smoothratio);
	    }

	    restoreinterpolations();

	    if (totalclock < lastvisinc)
	    {
	        if (klabs(p.visibility-ud.const_visibility) > 8)
	            p.visibility += (ud.const_visibility-p.visibility)>>2;
	    }
	    else p.visibility = ud.const_visibility;
	}

	public static String lastmessage;
	public static void FTA(int q, PlayerStruct p )
	{
	    if( ud.fta_on == 1)
	    {
	        if( p.fta > 0 && q != 115 && q != 116 )
	            if( p.ftq == 115 || p.ftq == 116 ) return;
	        
	        p.fta = 100;

	        if( p.ftq != q || q == 26 )
	        {
	            p.ftq = (short) q;
	        }
	        
	        int len = 0;
	        while(len < fta_quotes[p.ftq].length && fta_quotes[p.ftq][++len] != 0);

	        if (p == ps[screenpeek]) {
	        	String message = new String(fta_quotes[p.ftq], 0, len);
	        	if(!message.equals(lastmessage)) {
		        	Console.Println(message);
		        	lastmessage = message;
	        	}
	        }
	    }
	}

	public static void animatesprites(int x,int y,int z, short a,int smoothratio) //XXX
	{
	    short i, j, k, p, sect;
	    int l, t1,t3,t4;
	    SPRITE s, t;

	    for(j=0;j < spritesortcnt; j++)
	    {
	        t = tsprite[j];
	        i = t.owner;
	        s = sprite[t.owner];

	        switch(t.picnum)
	        {
	            case BLOODPOOL:
	            case FOOTPRINTS:
	            case FOOTPRINTS2:
	            case FOOTPRINTS3:
	            case FOOTPRINTS4:
	                if(t.shade == 127) continue;
	                break;
	                
	            case BLOODSPLAT1:
	            case BLOODSPLAT2:
	            case BLOODSPLAT3:
	            case BLOODSPLAT4:
	                if(ud.lockout != 0) t.xrepeat = t.yrepeat = 0;
	                else if(t.pal == 6)
	                {
	                    t.shade = -127;
	                    continue;
	                }
	            case BULLETHOLE:
	            case CRACK1:
	            case CRACK2:
	            case CRACK3:
	            case CRACK4:
	                t.shade = 16;
	                continue;
	            case 1152:
	            	break;
	            default:
	                if( ( (t.cstat&16) != 0 ) || ( badguy(t) && t.extra > 0) || t.statnum == 10)
	                    continue;
	        }

	        if ((sector[t.sectnum].ceilingstat&1) != 0)
	            l = sector[t.sectnum].ceilingshade;
	        else
	            l = sector[t.sectnum].floorshade;

	        if(l < -127) l = -127;
	        if(l > 128) l =  127;
	        t.shade = (byte) l;
	    }


	    for(j=0;j < spritesortcnt; j++ )  //Between drawrooms() and drawmasks()
	    {                             //is the perfect time to animate sprites
	        t = tsprite[j];
	        i = t.owner;
	        s = sprite[i];

	        switch(s.picnum)
	        {
	            case SECTOREFFECTOR:
	                if(t.lotag == 27 && ud.recstat == 1)
	                {
	                    t.picnum = (short) (11+((totalclock>>3)&1));
	                    t.cstat |= 128;
	                }
	                else
	                    t.xrepeat = t.yrepeat = 0;
	                break;
	            case 1097:
	            case 1106:
	            case 1115:
	            case 1168:
	            case 1174:
	            case 1175:
	            case 1176:
	            case 1178:
	            case 1225:
	            case 1226:
	            case 1529:
	            case 1530:
	            case 1531:
	            case 1532:
	            case 1533:
	            case 1534:
	            case 2231:
	            case 5581:
	            case 5583:
	                if(ud.lockout != 0)
	                {
	                    t.xrepeat = t.yrepeat = 0;
	                    continue;
	                }
	        }

	        if( t.statnum == 99 ) continue;
	        if( s.statnum != 1 && s.picnum == APLAYER && ps[s.yvel].newowner == -1 && s.owner >= 0 )
	        {
	            t.x -= mulscale(65536-smoothratio,ps[s.yvel].posx-ps[s.yvel].oposx, 16);
	            t.y -= mulscale(65536-smoothratio,ps[s.yvel].posy-ps[s.yvel].oposy, 16);
	            t.z = ps[s.yvel].oposz + mulscale(smoothratio,ps[s.yvel].posz-ps[s.yvel].oposz, 16);
	            t.z += (40<<8);
	            
	            s.xrepeat = 24;
	            s.yrepeat = 17;
	        }
	        else if( ( s.statnum == 0 && s.picnum != 1298) || s.statnum == 10 || s.statnum == 6 || s.statnum == 4 || s.statnum == 5 || s.statnum == 1 )
	        {
	            t.x -= mulscale(65536-smoothratio,s.x-hittype[i].bposx, 16);
	            t.y -= mulscale(65536-smoothratio,s.y-hittype[i].bposy, 16);
	            t.z -= mulscale(65536-smoothratio,s.z-hittype[i].bposz, 16);
	        }

	        sect = s.sectnum;
	        t1 = hittype[i].temp_data[1];
	        t3 = hittype[i].temp_data[3];
	        t4 = hittype[i].temp_data[4];

	        switch(s.picnum)
	        {
	        case BURNING:
                if( sprite[s.owner].statnum == 10 )
                {
                    if( display_mirror == 0 && sprite[s.owner].yvel == screenpeek && ps[sprite[s.owner].yvel].over_shoulder_on == 0 )
                        t.xrepeat = 0;
                    else
                    {
                        t.ang = engine.getangle(x-t.x,y-t.y);
                        t.x = sprite[s.owner].x;
                        t.y = sprite[s.owner].y;
                        t.x += sintable[(t.ang+512)&2047]>>10;
                        t.y += sintable[t.ang&2047]>>10;
                    }
                }
                break;

	        case 4041:
            case 4046:
            case 4055:
            case 4235:
            case 4244:
            case 4748:
            case 4753:
            case 4758:
            case 5290:
            case 5295:
            case 5300:
            case 5602:
            case 5607:
            case 5616: 
	        case JIBS1:
            case JIBS2:
            case JIBS3:
            case JIBS4:
            case JIBS5:
            case JIBS6:
                if(ud.lockout != 0)
                {
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }
                if(t.pal == 6) t.shade = -120;
                if ( shadeEffect[s.sectnum] )
                    t.shade = 16;
                    
            case SCRAP1:
            case SCRAP2: 
            case SCRAP3: 
            case SCRAP4: 
            case SCRAP5:
            case SCRAP6: 
            case SCRAP6+1:
            case SCRAP6+2:
            case SCRAP6+3:
            case SCRAP6+4:
            case SCRAP6+5:
            case SCRAP6+6:
            case SCRAP6+7:
                if(t.picnum == SCRAP1 && s.yvel > 0)
                    t.picnum = s.yvel;
                else t.picnum += hittype[i].temp_data[0];
            
                if( sector[sect].floorpal != 0 )
                    t.pal = sector[sect].floorpal;
                break;     
            case BLOODPOOL:
            case FOOTPRINTS:
            case FOOTPRINTS2:
            case FOOTPRINTS3:
            case FOOTPRINTS4:
                if(t.pal == 6)
                    t.shade = -127;
            case 1310:
            case 1311:
            	if(ud.lockout != 0 && s.pal == 2)
                {
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }
                break;
            case RESPAWNMARKERRED:
            case 876:
            case 886:
            	t.picnum = (short) (((totalclock >> 4) & 0xD) + 861);
                if ( s.picnum == RESPAWNMARKERRED )
                	t.pal = 0;
                else if ( s.picnum == 876 )
                	t.pal = 1;
                else
                	t.pal = 2;
                if ( ud.marker == 0 )
                	t.xrepeat = t.yrepeat = 0;
            	break;                
            case 46:
                t.shade = (byte) (sintable[(totalclock<<4)&2047]>>10);
                continue;
            case WATERBUBBLE:
                if(sector[t.sectnum].floorpicnum == FLOORSLIME)
                {
                    t.pal = 7;
                    break;
                }
            default:
            	if( sector[sect].floorpal != 0 )
            		t.pal = sector[sect].floorpal;
            	break;
            case 4989:
            	k = engine.getangle(s.x-x,s.y-y);
                if( hittype[i].temp_data[0] < 4 )
                    k = (short) (((s.ang+3072+128-k)&2047)/170);
                else k = (short) (((s.ang+3072+128-k)&2047)/170);

                if(k>6)
                {
                    k = (short) (12-k);
                    t.cstat |= 4;
                }
                else t.cstat &= ~4;

                if( klabs(t3) > 64 ) k += 7;
                t.picnum = (short) (4989+k);
            	break;
            case ECLAIRHEALTH:
                t.z -= (4<<8);
                break;
            case CROSSBOW:
                k = engine.getangle(s.x-x,s.y-y);
                k = (short) (((s.ang+3072+128-k)&2047)/170);
                if(k > 6)
                {
                   k = (short) (12-k);
                   t.cstat |= 4;
                }
                else t.cstat &= ~4;
                t.picnum = (short) (CROSSBOW+k);
                break; 
            case SHITBALL:
            	t.picnum = (short) (((totalclock >> 4) & 3) + SHITBALL);
            	break;
            case CIRCLESAW:
            	t.picnum = (short) (((totalclock >> 4) & 7) + CIRCLESAW);
            	break;
            case FORCESPHERE:
                if(t.statnum == 5)
                {
                    short sqa,sqb;

                    sqa =
                        engine.getangle(
                            sprite[s.owner].x-ps[screenpeek].posx,
                            sprite[s.owner].y-ps[screenpeek].posy);
                    sqb =
                    		engine.getangle(
                            sprite[s.owner].x-t.x,
                            sprite[s.owner].y-t.y);

                    if( klabs(getincangle(sqa,sqb)) > 512 )
                        if( ldist(sprite[s.owner],t) < ldist(sprite[ps[screenpeek].i],sprite[s.owner]) )
                            t.xrepeat = t.yrepeat = 0;
                }
                continue;
            case LNRDLYINGDEAD:
            	s.xrepeat = 24;
            	s.yrepeat = 17;
            	if ( s.extra > 0 )
            		t.z += 1536;
            	break;
            
            case APLAYER:

                p = s.yvel;

                if(t.pal == 1) t.z -= (18<<8);

                if(ps[p].over_shoulder_on > 0 && ps[p].newowner < 0 )
                {
                    t.cstat |= 2;
                    if ( screenpeek == myconnectindex && numplayers >= 2 )
                    {
                        t.x = omyx+mulscale((int)(myx-omyx),smoothratio, 16);
                        t.y = omyy+mulscale((int)(myy-omyy),smoothratio, 16);
                        t.z = omyz+mulscale((int)(myz-omyz),smoothratio, 16)+(40<<8);
                        t.ang = (short) (omyang+mulscale((int)((BClampAngle(myang+1024-omyang))-1024),smoothratio, 16));
                        t.sectnum = mycursectnum;
                    }
                }

                if( ( display_mirror == 1 || screenpeek != p || s.owner == -1 ) && ud.multimode > 1 && ud.showweapons != 0 && sprite[ps[p].i].extra > 0 && ps[p].curr_weapon > 0 )
                {
                	if( tsprite[spritesortcnt] == null )
                		tsprite[spritesortcnt] = new SPRITE();
                	
                    tsprite[spritesortcnt].set(t);
                    tsprite[spritesortcnt].statnum = 99;

                    tsprite[spritesortcnt].yrepeat = (short) ( t.yrepeat>>3 );
                    if(t.yrepeat < 4) t.yrepeat = 4;

                    tsprite[spritesortcnt].shade = t.shade;
                    tsprite[spritesortcnt].cstat = 0;

                    switch(ps[p].curr_weapon)
                    {
                        case 1:  tsprite[spritesortcnt].picnum = 21;      break;
                        case 2:  tsprite[spritesortcnt].picnum = 28;      break;
                        case 3:  tsprite[spritesortcnt].picnum = 22;      break;
                        case 10:
                        case 4:  tsprite[spritesortcnt].picnum = 26;      break;
                        case 5:	 tsprite[spritesortcnt].picnum = 23;      break;
                        case 11: 
                        case 6:  tsprite[spritesortcnt].picnum = 3400;    break;
                        case 7:  tsprite[spritesortcnt].picnum = 29;      break;
                        case 8:  tsprite[spritesortcnt].picnum = 27;      break;
                        case 9:  tsprite[spritesortcnt].picnum = 24;      break;
                        case 12: tsprite[spritesortcnt].picnum = 3437;    break;
                    }

                    if(s.owner >= 0)
                        tsprite[spritesortcnt].z = ps[p].posz-(12<<8);
                    else tsprite[spritesortcnt].z = s.z-(51<<8);
                    if(ps[p].curr_weapon == 4)
                    {
                        tsprite[spritesortcnt].xrepeat = 10;
                        tsprite[spritesortcnt].yrepeat = 10;
                    }
                    else
                    {
                        tsprite[spritesortcnt].xrepeat = 16;
                        tsprite[spritesortcnt].yrepeat = 16;
                    }
                    tsprite[spritesortcnt].pal = 0;
                    spritesortcnt++;
                }

                if(s.owner == -1)
                {
                    k = (short) ((((s.ang+3072+128-a)&2047)>>8)&7);
                    if(k>4)
                    {
                        k = (short) (8-k);
                        t.cstat |= 4;
                    }
                    else t.cstat &= ~4;

                    if(sector[t.sectnum].lotag == 2) k += 1795-1405;
                    else if( (hittype[i].floorz-s.z) > (64<<8) ) k += 60;

                    t.picnum += k;
                    t.pal = ps[p].palookup;

                    if( sector[sect].floorpal != 0 )
	                    t.pal = sector[sect].floorpal;
                    
                    continue;
                }

                if( ps[p].on_crane == -1 && (sector[s.sectnum].lotag&0x7ff) != 1 )
                {
                    l = s.z-hittype[ps[p].i].floorz+(3<<8);
                    if( l > 1024 && s.yrepeat > 32 && s.extra > 0 )
                        s.yoffset = (short) (l/(s.yrepeat<<2));
                    else s.yoffset=0;
                }

                if(ps[p].newowner > -1)
                {
                    t4 = script[actorscrptr[APLAYER]+1];
                    t3 = 0;
                    t1 = script[actorscrptr[APLAYER]+2];
                }

                if(ud.camerasprite == -1 && ps[p].newowner == -1)
                    if(s.owner >= 0 && display_mirror == 0 && ps[p].over_shoulder_on == 0 )
                        if( ud.multimode < 2 || ( ud.multimode > 1 && p == screenpeek ) )
                {
                    t.owner = -1;
                    t.xrepeat = t.yrepeat = 0;
                    continue;
                }

                if( sector[sect].floorpal != 0 )
                    t.pal = sector[sect].floorpal;

                if(s.owner == -1) continue;

                if( t.z > hittype[i].floorz && t.xrepeat < 32 )
                    t.z = hittype[i].floorz;
                
                int tx = t.x - x;
				int ty = t.y - y;
				int angle = ((1024 + engine.getangle(tx, ty) - a) & kAngleMask) - 1024;
				long dist = engine.qdist(tx, ty);

				if(klabs(mulscale(angle, dist, 14)) < 4) {
					int horizoff = (int) (100-ps[screenpeek].horiz);
					long z1 = mulscale(dist, horizoff, 3) + z;

					int zTop = t.z; 
					int zBot = zTop;
					int yoffs = (picanm[APLAYER] >> 16) & 255;
					zTop -= (yoffs + tilesizy[APLAYER]) * (t.yrepeat << 2);
					zBot += -yoffs * (t.yrepeat << 2);
					
					if ((z1 < zBot) && (z1 > zTop))
					{
						if(engine.cansee(x, y, z, sprite[ps[screenpeek].i].sectnum, t.x, t.y, t.z, t.sectnum) != 0)
							gPlayerIndex = t.yvel;
					}
				}

                break;   
	        case 27:
	        	continue;   
	        }

	        if( actorscrptr[s.picnum] != 0 && (t.cstat & 0x30) != 48 )
	        {
	            if(t4 != 0)
	            {
	                l = script[t4+2];
	                switch( l )
	                {
	                    case 2:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>8)&1);
	                        break;

	                    case 3:
	                    case 4:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>7)&7);
	                        if(k > 3)
	                        {
	                            t.cstat |= 4;
	                            k = (short) (7-k);
	                        }
	                        else t.cstat &= ~4;
	                        break;

	                    case 5:
	                        k = engine.getangle(s.x-x,s.y-y);
	                        k = (short) ((((s.ang+3072+128-k)&2047)>>8)&7);
	                        if(k>4)
	                        {
	                            k = (short) (8-k);
	                            t.cstat |= 4;
	                        }
	                        else t.cstat &= ~4;
	                        break;
	                    case 7:
	                        k = engine.getangle(s.x-x,s.y-y);
	                        k = (short) (((s.ang+3072+128-k)&2047)/170);
	                        if(k>6)
	                        {
	                            k = (short) (12-k);
	                            t.cstat |= 4;
	                        }
	                        else t.cstat &= ~4;
	                        break;
	                    case 8:
	                        k = (short) ((((s.ang+3072+128-a)&2047)>>8)&7);
	                        t.cstat &= ~4;
	                        break;
	                    default:
	                        if(badguy(s) && s.statnum == 2 && s.extra > 0)
	                        {
	                        	k = engine.getangle(s.x-x,s.y-y);
		                        k = (short) ((((s.ang+3072+128-k)&2047)>>8)&7);
		                        if(k>4)
		                        {
		                            k = (short) (8-k);
		                            t.cstat |= 4;
		                        }
		                        else t.cstat &= ~4;
	                        } else {
	                        	k = 0;
	                        }
	                        break;
	                }

	                t.picnum += (k + ( script[t4] ) + l * t3);

	                if(l > 0) while(tilesizx[t.picnum] == 0 && t.picnum > 0 )
	                    t.picnum -= l;       //Hack, for actors

	                if( hittype[i].dispicnum >= 0)
	                    hittype[i].dispicnum = t.picnum;
	            }
	            else if(display_mirror == 1)
	                t.cstat |= 4;
	        }
	        if ( s.picnum == 5015 )
	            t.shade = -127;
	      
	        if( s.statnum == 13 || badguy(s) || (s.picnum == APLAYER && s.owner >= 0) )
	            if((s.cstat & 0x30) == 0 && t.statnum != 99 && s.picnum != EXPLOSION2 && s.picnum != 1080 && s.picnum != TORNADO && s.picnum != EXPLOSION3 && s.picnum != 5015)
	        {
	            if( hittype[i].dispicnum < 0 )
	            {
	                hittype[i].dispicnum++;
	                continue;
	            }
	            else if( ud.shadows != 0 && spritesortcnt < (MAXSPRITESONSCREEN-2))
	            {
	                int daz,xrep,yrep;

	                if( (sector[sect].lotag&0xff) > 2 || s.statnum == 4 || s.statnum == 5 || s.picnum == MOSQUITO )
	                    daz = sector[sect].floorz;
	                else
	                    daz = hittype[i].floorz;

	                if( (s.z-daz) < (8<<8) )
	                    if( ps[screenpeek].posz < daz )
	                {
	                    if( tsprite[spritesortcnt] == null )
	                    	 tsprite[spritesortcnt] = new SPRITE();
	                    	
	                    tsprite[spritesortcnt].set(t);
	                    tsprite[spritesortcnt].statnum = 99;

	                    tsprite[spritesortcnt].yrepeat = (short) ( t.yrepeat>>3 );
	                    if(t.yrepeat < 4) t.yrepeat = 4;

	                    tsprite[spritesortcnt].shade = 127;
	                    tsprite[spritesortcnt].cstat |= 2;

	                    tsprite[spritesortcnt].z = daz;
	                    xrep = tsprite[spritesortcnt].xrepeat;
	                    tsprite[spritesortcnt].xrepeat = (short) xrep;
	                    tsprite[spritesortcnt].pal = 4;

	                    yrep = tsprite[spritesortcnt].yrepeat;
	                    tsprite[spritesortcnt].yrepeat = (short) yrep;
	                    spritesortcnt++;
	                }
	            }
	        }

	        switch(s.picnum)
	        {
	        	case 2944:
	        		t.shade = -127;
	        		t.picnum = (short) (((totalclock >> 2) & 4) + 2944);
	        		break;
	        	case MUD:
	        		t.picnum = (short) (t1 + MUD);
	        		break;
	        	case FRAMEEFFECT1:
	                if(s.owner >= 0 && sprite[s.owner].statnum < MAXSTATUS)
	                {
	                    if(sprite[s.owner].picnum == APLAYER)
	                        if(ud.camerasprite == -1)
	                            if(screenpeek == sprite[s.owner].yvel && display_mirror == 0)
	                    {
	                        t.owner = -1;
	                        break;
	                    }
	                    if( (sprite[s.owner].cstat&32768) == 0 )
	                    {
	                    	if ( sprite[s.owner].picnum == APLAYER )
	                            t.picnum = 1554;
	                        else t.picnum = (short) hittype[s.owner].dispicnum;
	                        t.pal = sprite[s.owner].pal;
	                        t.shade = sprite[s.owner].shade;
	                        t.ang = sprite[s.owner].ang;
	                        t.cstat = (short) (2|sprite[s.owner].cstat);
	                    }
	                }
	                break;
	        	 case PLAYERONWATER:
		                k = (short) ((((t.ang+3072+128-a)&2047)>>8)&7);
		                if(k>4)
		                {
		                    k = (short) (8-k);
		                    t.cstat |= 4;
		                }
		                else t.cstat &= ~4;

		                t.picnum = (short) (s.picnum+k+((hittype[i].temp_data[0]<4)?5:0));
		                t.shade = sprite[s.owner].shade;

		                break;
	        	 case 1409:
	        	 case EXPLOSION2:
	        	 case 1442:
	        	 case 1774:
	        	 case 2095:
	        	 case 3380:
	        	 case 3400:
	        	 case FIRELASER:
	        	 case 3471:
	        	 case 3475:
	        	 case 5595:
	        		 if(t.picnum == EXPLOSION2)
	        		 {
	        			 ps[screenpeek].visibility = -127;
	        			 lastvisinc = totalclock+32;
	        			 t.pal = 0;
	        		 } 
	        		 else if(t.picnum == FIRELASER)
	        			 t.picnum = (short) (((totalclock >> 2) & 5) + FIRELASER);
	        		 
	                t.shade = -127;
	                break;
	        	 case 1878:
	        	 case 1952:
	        	 case 1953:
	        	 case 1990:
	        	 case 2050:
	        	 case 2056:
	        	 case 2072:
	        	 case 2075:
	        	 case 2083:
	        	 case 2097:
	        	 case 2156:
	        	 case 2157:
	        	 case 2158:
	        	 case 2159:
	        	 case 2160:
	        	 case 2161:
	        	 case 2175:
	        	 case 2176:
	        	 case 2357:
	        	 case 2564:
	        	 case 2573:
	        	 case 2574:
	        	 case 2583:
	        	 case 2604:
	        	 case 2689:
	        	 case 2893:
	        	 case 2894:
	        	 case 2915:
	        	 case 2945:
	        	 case 2946:
	        	 case 2947:
	        	 case 2948:
	        	 case 2949:
	        	 case 2977:
	        	 case 2978:
	        	 case 3116:
	        	 case 3171:
	        	 case 3216:
	        	 case 3720:
	        		 t.shade = -127;
	        		 break;
	        	 case UFOBEAM:
	        	 case 3586:
	        	 case 3587:
	        		 t.cstat |= 32768;
	        		 s.cstat |= 32768;
	        		 break;
	        	 case 36:
	        		 t.cstat |= 32768;
	        		 break;
	        	 case 1107:
	        		 t.picnum = (short) (s.picnum + hittype[i].temp_data[2]);
	        		 break;
	        	 case CAMERA1:
	        	 case RAT:
	        		 k = (short) ((((t.ang+3072+128-a)&2047)>>8)&7);
	        		 if(k>4)
	        		 {
	        			 k = (short) (8-k);
	        			 t.cstat |= 4;
	        		 }
	        		 else t.cstat &= ~4;
	        		 t.picnum = (short) (s.picnum+k);
	        		 break;
	        	 case 2034:
	                 t.picnum = (short) ((totalclock & 1) + 2034);
	        		 break; 
	        	 case WATERSPLASH2:
	        		 t.picnum = (short) (WATERSPLASH2+t1);
	        		 break;
	        	 case BURNING:
	        	 case FIRE:
	        		 if( sprite[s.owner].picnum != 1191 && sprite[s.owner].picnum != 1193 )
	        			 t.z = sector[t.sectnum].floorz;
	        		 t.shade = -127;
	        		 break;
	        	 case SHELL:
	        		 t.picnum = (short) (s.picnum+(hittype[i].temp_data[0]&1));
	        		 break;
	        }

	        hittype[i].dispicnum = t.picnum;
	        if(sector[t.sectnum].floorpicnum == MIRROR)
	            t.xrepeat = t.yrepeat = 0;
	    }
	}
	
	public static void addmessage(String message) {
		buildString(fta_quotes[122], 0, message);
		FTA(122,ps[myconnectindex]);
	}
	
	public static void viewDrawStats(int x, int y, int zoom)  
	{ 
		if(cfg.gShowStat == 0)
			return;

		float viewzoom = (zoom / 65536.0f);

		buildString(buffer, 0, "kills:   ");
		mGetAlign(1, buffer);
		
		int yoffset = (int) (2 * (aligny + 10) * viewzoom);
		y -= yoffset;
		
		int statx = x;
		int staty = y;
		
		gametext(statx, staty, buffer, zoom, 0, 2, 24 | 256);
	
		int offs = Bitoa(ps[0].actors_killed, buffer);
		offs = buildString(buffer, offs, " /   ", ps[0].max_actors_killed);
		gametext(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, 24 | 256);
		
		statx = x;
		staty = y + (int) (12 * viewzoom);
		
		buildString(buffer, 0, "secrets:    ");
		gametext(statx, staty, buffer, zoom, 0, 2, 24 | 256);
		mGetAlign(1, buffer);
		offs = Bitoa(ps[myconnectindex].secret_rooms, buffer);
		offs = buildString(buffer, offs, " /   ", ps[0].max_secret_rooms);
		gametext(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, 24 | 256);
		
		statx = x;
		staty = y + (int) (22 * viewzoom);
		
		buildString(buffer, 0, "time:    ");
		gametext(statx, staty, buffer, zoom, 0, 2, 24 | 256);
		mGetAlign(1, buffer);
		
		int minutes = ps[myconnectindex].player_par/(26*60);
		int sec = (ps[myconnectindex].player_par/26)%60;
		
		offs = Bitoa(minutes, buffer, 2);
		offs = buildString(buffer, offs, " :   ", sec, 2);
		gametext(statx += (alignx + 6) * viewzoom, staty, buffer, zoom, 0, 15, 24 | 256);
	}
	
	private static PlayerOrig viewout = new PlayerOrig();
	public static PlayerOrig view(PlayerStruct pp, int vx, int vy,int vz,short vsectnum, float ang, float horiz)
	{
	     viewout.ox = vx;
	     viewout.oy = vy;
	     viewout.oz = vz;
	     viewout.os = vsectnum;

	     int nx = (int) (-BCosAngle(ang) / 16.0f);
	     int ny = (int) (-BSinAngle(ang) / 16.0f);
	     int nz = (int) ((horiz-100)*128 - 4096);

	     SPRITE sp = sprite[pp.i];

	     short bakcstat = sp.cstat;
	     sp.cstat &= ~0x101;

	     vsectnum = engine.updatesectorz(vx,vy,vz,vsectnum);
	     
	     engine.hitscan(vx,vy,vz,vsectnum,nx,ny,nz,pHitInfo,CLIPMASK1);
	     int hitx = pHitInfo.hitx, hity = pHitInfo.hity;

	     if(vsectnum < 0)
	     {
	        sp.cstat = bakcstat;
	        return viewout;
	     }

	     int hx = hitx-(vx); 
	     int hy = hity-(vy);
	     if( (klabs(hx) + klabs(hy)) - (klabs(nx) + klabs(ny)) < 1024)
	     {
			int wx = 1; if(nx < 0) wx = -1;
			int wy = 1; if(ny < 0) wy = -1;
			
			hx -= wx << 9;
			hy -= wy << 9;
			
			int dist = 0;
			if(nx != 0 && ny != 0) {
				if(klabs(nx) > klabs(ny))
					dist = (int) divscale(hx,nx,16);
				else dist = (int) divscale(hy,ny,16);
			}
			
			if (dist < cameradist) cameradist = dist;
	     }
	     
	     vx += mulscale(nx,cameradist,16);
	     vy += mulscale(ny,cameradist,16);
	     vz += mulscale(nz,cameradist,16);
	  
	     cameradist = min(cameradist+((totalclock-cameraclock)<<10),65536);
	     cameraclock = totalclock;

	     vsectnum = engine.updatesectorz(vx,vy,vz,vsectnum);

	     sp.cstat = bakcstat;
	     
	     viewout.ox = vx;
	     viewout.oy = vy;
	     viewout.oz = vz;
	     viewout.os = vsectnum;
	     
	     return viewout;
	}
	
	public static void coords(short snum)
	{
	    short y = 0;

	    if(ud.coop != 1)
	    {
	        if(ud.multimode > 1 && ud.multimode < 5)
	            y = 8;
	        else if(ud.multimode > 4)
	            y = 16;
	    }

	    buildString(buffer, 0, "X= ", ps[snum].posx);
	    engine.printext256(250,y,31,-1,buffer,1);
	    buildString(buffer, 0, "Y= ", ps[snum].posy);
	    engine.printext256(250,y+7,31,-1,buffer,1);
	    buildString(buffer, 0, "Z= ", ps[snum].posz);
	    engine.printext256(250,y+14,31,-1,buffer,1);
	    buildString(buffer, 0, "A= ", (int)ps[snum].ang);
	    engine.printext256(250,y+21,31,-1,buffer,1);
	    buildString(buffer, 0, "ZV= ", ps[snum].poszv);
	    engine.printext256(250,y+28,31,-1,buffer,1);
	    buildString(buffer, 0, "OG= ", ps[snum].on_ground?1:0);
	    engine.printext256(250,y+35,31,-1,buffer,1);
	    buildString(buffer, 0, "LFW= ", ps[snum].last_full_weapon);
	    engine.printext256(250,y+50,31,-1,buffer,1);
	    buildString(buffer, 0, "SECTL= ", sector[ps[snum].cursectnum].lotag);
	    engine.printext256(250,y+57,31,-1,buffer,1);
	    buildString(buffer, 0, "SEED= ", engine.getrand());
	    engine.printext256(250,y+64,31,-1,buffer,1);
	    buildString(buffer, 0, "THOLD= ", ps[snum].transporter_hold);
	    engine.printext256(250,y+64+7,31,-1,buffer,1);
	}

	public static void displaygeom3d(int sectnum, int cposx, int cposy, int cposz,  float choriz, float cang, int csect, int smoothratio)
	{
		if(sector[sectnum].lotag == 848)
		{
	        int geomsect = 0;
	        
	        for(int i = 0; i < numgeomeffects; i++)
	        {
	        	int k = headspritesect[geomsector[i]]; 
	        	while(k != -1)
	        	{
	        		int nextk = nextspritesect[k];
	        		engine.changespritesect(k, geoms1[i]);
	        		engine.setsprite(k, sprite[k].x + geomx1[i], sprite[k].y + geomy1[i], sprite[k].z);
	        		k = nextk;
	        	}
	        	if ( csect == geomsector[i] )
	        		geomsect = i;
	        }
	        
	        engine.drawrooms(cposx - geomx1[geomsect], cposy - geomy1[geomsect], cposz, cang, choriz, geomsect);
	        
	        for(int i = 0; i < numgeomeffects; i++)
	        {
	        	int k = headspritesect[geoms1[i]]; 
	        	while(k != -1)
	        	{
	        		int nextk = nextspritesect[k];
	        		engine.changespritesect(k, geomsector[i]);
	        		engine.setsprite(k, sprite[k].x - geomx1[i], sprite[k].y - geomy1[i], sprite[k].z);
	        		k = nextk;
	        	}
	        }
	        
	        animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
	        engine.drawmasks();
	        
	        for(int i = 0; i < numgeomeffects; i++)
	        {
	        	int k = headspritesect[geomsector[i]]; 
	        	while(k != -1)
	        	{
	        		int nextk = nextspritesect[k];
	        		engine.changespritesect(k, geoms2[i]);
	        		engine.setsprite(k, sprite[k].x + geomx2[i], sprite[k].y + geomy2[i], sprite[k].z);
	        		k = nextk;
	        	}
	        	if ( csect == geomsector[i] )
	        		geomsect = i;
	        }
	        
	        engine.drawrooms(cposx - geomx2[geomsect], cposy - geomy2[geomsect], cposz, cang, choriz, geomsect);
	        
	        for(int i = 0; i < numgeomeffects; i++)
	        {
	        	int k = headspritesect[geoms2[i]]; 
	        	while(k != -1)
	        	{
	        		int nextk = nextspritesect[k];
	        		engine.changespritesect(k, geomsector[i]);
	        		engine.setsprite(k, sprite[k].x - geomx2[i], sprite[k].y - geomy2[i], sprite[k].z);
	        		k = nextk;
	        	}
	        }
	        
	        animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
	        engine.drawmasks();
		}
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
	    n |= (p.empty_amount > 0)?1<<4:0; if((n&16) != 0) j++;
	    n |= (p.boot_amount > 0)?1<<6:0; if((n&64) != 0) j++;
	    
	    xoff = 160-(j*11);

	    j = 0;

	    if(ud.screen_size > 1)
	        y = 134;
	    else y = 178;

	    if(ud.screen_size == 1)
	    {
	        if(ud.multimode > 1)
	            xoff += 56;
	        else xoff += 65;
	    }

	    if((p.gotkey[0]|p.gotkey[1]|p.gotkey[2]) != 0)
	        xoff += tilesizx[9216] / 4;
        
	    while( j <= 9 )
	    {
	        if( (n&(1<<j)) != 0 )
	        {
	            switch( n&(1<<j) )
	            {
	                case   1:
	                	engine.rotatesprite((xoff+4)<<16,y<<16,32768,0,WHISHKEY_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   2:
	                	engine.rotatesprite((xoff+4)<<16,y<<16,32768,0,MOONSHINE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   4:
	                	engine.rotatesprite((xoff)<<16,(y+2)<<16,32768,0, BEER_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case   8:
	                	engine.rotatesprite((xoff-2)<<16,(y+5)<<16,32768,0,COWPIE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  16:
	                	engine.rotatesprite((xoff-2)<<16,y<<16,32768,0, EMPTY_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case  32:
	                	engine.rotatesprite((xoff)<<16,y<<16,32768,0,SNORKLE_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	                case 64:
	                	engine.rotatesprite((xoff+4)<<16,(y-1)<<16,32768,0,BOOT_ICON,0,0,10+16,windowx1,windowy1,windowx2,windowy2);break;
	            }

	            xoff += 22;

	            if(p.inven_icon == j+1)
	            	engine.rotatesprite((xoff)<<16,(y+20)<<16,32768,1024,ARROW,-32,0,10+16,windowx1,windowy1,windowx2,windowy2);
	        }

	        j++;
	    }
	}
	
	
	
	public static int lastvisinc;
	public static void displaymasks(short snum)
	{
	    int p = sector[ps[snum].cursectnum].floorpal;
	    
	    if(sprite[ps[snum].i].pal == 1)
	        p = 1;
	    if(ps[snum].scuba_on != 0)
		{
        	engine.rotatesprite(
    	            (320 - (tilesizx[3374] >> 1) - 15) << 16,
    	            (200 - (tilesizy[3374] >> 1) + (sintable[totalclock & 0x7FF] >> 10)) << 16,
    	            49152,0,3374,0,p,10 | 16 |512,
    	            windowx1, windowy1, windowx2, windowy2);
        	
        	int framesx = xdim / tilesizx[3377];
			
			int x = -tilesizx[3377]/2;
			for(int i = 0; i <= framesx; i++) {
				engine.rotatesprite(x<<16, (-1)<<16, 65536, 0, 3377, 0, p, 10 | 16, 0, 0, xdim-1, ydim-1);
	        	engine.rotatesprite(x<<16, 200<<16, 65536, 0, 3377, 0, p, 4 | 10| 16, 0, 0, xdim-1, ydim-1);
		    	x += tilesizx[3377] - 1;
		    }
			
    	    engine.rotatesprite(
    	            (320 - (tilesizx[3378])) << 16,
    	            (200 - (tilesizy[3378])) << 16,
    	            65536, 0, 3378, 0, p, 10 | 16 |512,
    	            windowx1, windowy1, windowx2, windowy2);
    	    engine.rotatesprite(
    	            tilesizx[3378] << 16,
    	            (200 - (tilesizy[3378])) << 16,
    	            65536,1024,3378,0,p, 4 | 10 | 16 |256,
    	            windowx1, windowy1, windowx2, windowy2);
    		
		}
	}
}
