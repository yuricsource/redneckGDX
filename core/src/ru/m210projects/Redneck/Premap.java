package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Redneck.Redneck.gShowMenu;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.Types.Demo.opendemowrite;
import static ru.m210projects.Redneck.Screen.setgamepalette;
import static ru.m210projects.Redneck.Redneck.gLoadingTicks;
import static ru.m210projects.Redneck.Types.Demo.closedemowrite;
import static ru.m210projects.Redneck.Interpolation.InterpolationCount;
import static ru.m210projects.Redneck.Interpolation.gInterpolationData;
import static ru.m210projects.Redneck.Types.INTERPOLATION.CEILZ;
import static ru.m210projects.Redneck.Types.INTERPOLATION.FLOORZ;
import static ru.m210projects.Redneck.Types.INTERPOLATION.WALLX;
import static ru.m210projects.Redneck.Types.INTERPOLATION.WALLY;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Redneck.View.*;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.PlayerInfo;
import ru.m210projects.Redneck.Types.INTERPOLATION;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Premap {
	
	public static char[] lastmapname;
	public static boolean shadeEffect[] = new boolean[MAXSECTORS];

	public static final int MAXJAILDOORS = 32;
	public static int jailspeed[] = new int[MAXJAILDOORS];
	public static int jaildistance[] = new int[MAXJAILDOORS];
	public static short jailsect[] = new short[MAXJAILDOORS];
	public static short jaildirection[] = new short[MAXJAILDOORS];
	public static short jailunique[] = new short[MAXJAILDOORS];
	public static short jailsound[] = new short[MAXJAILDOORS];
	public static short jailstatus[] = new short[MAXJAILDOORS];
	public static int jailcount2[] = new int[MAXJAILDOORS];
	
	
	public static final int MAXMINECARDS = 16;
	public static int minespeed[] = new int[MAXMINECARDS];
	public static int minefulldist[] = new int[MAXMINECARDS];
	public static int minedistance[] = new int[MAXMINECARDS];
	public static short minechild[] = new short[MAXMINECARDS];
	public static short mineparent[] = new short[MAXMINECARDS];
	public static short minedirection[] = new short[MAXMINECARDS];
	public static short minesound[] = new short[MAXMINECARDS];
	public static short minestatus[] = new short[MAXMINECARDS];
	
	public static final int MAXTORCHES = 64;
	public static short torchsector[] = new short[MAXTORCHES];
	public static byte torchshade[] = new byte[MAXTORCHES];
	public static short torchflags[] = new short[MAXTORCHES];
	
	public static final int MAXLIGHTNINS = 64;
	public static short lightninsector[] = new short[MAXLIGHTNINS];
	public static short lightninshade[] = new short[MAXLIGHTNINS];
	
	public static final int MAXAMBIENTS = 64;
	public static short ambienttype[] = new short[MAXAMBIENTS];
	public static short ambientid[] = new short[MAXAMBIENTS];
	public static short ambienthitag[] = new short[MAXAMBIENTS];
	
	public static final int MAXGEOMETRY = 64;
	public static short geomsector[] = new short[MAXGEOMETRY];
	public static short geoms1[] = new short[MAXGEOMETRY];
	public static int geomx1[] = new int[MAXGEOMETRY];
	public static int geomy1[] = new int[MAXGEOMETRY];
	public static int geomz1[] = new int[MAXGEOMETRY];
	public static short geoms2[] = new short[MAXGEOMETRY];
	public static int geomx2[] = new int[MAXGEOMETRY];
	public static int geomy2[] = new int[MAXGEOMETRY];
	public static int geomz2[] = new int[MAXGEOMETRY];

	public static boolean plantProcess = false;
	
	public static int numlightnineffects, numtorcheffects, 
		numgeomeffects, numjaildoors, numminecart, numambients; 
	private static int haveLigthning;

	public static byte[] packbuf = new byte[576];
	
	public static short which_palookup = 9;

	public static void tloadtile(int tilenume)
	{
		if(tilesizx[tilenume] != 0 && tilesizy[tilenume] != 0)
			gotpic[tilenume>>3] |= (1<<(tilenume&7));
	}
	
	public static void cachespritenum(int i)
	{
	    short j;

	    if(ud.monsters_off && badguy(sprite[i])) return;

	    int maxc = 1;
	    switch(sprite[i].picnum)
	    {
	        case APLAYER:
	            maxc = 0;
	            if(ud.multimode > 1)
	            {
	                maxc = 5;
	                for(j = 1420;j < 1420+106; j++)
	                	tloadtile(j);
	            }
	            break;
	        case FORCERIPPLE:
	        	 maxc = 9;
	        	break;
	        case SEENINE:
	        case OOZFILTER:
	            maxc = 3;
	            break;
	        case TORNADO:
	        	maxc = 4;
	        	break;
	        case LTH:
	        	maxc = 4462 - LTH;
	        	break;
	        case DOGRUN:
	        	maxc = 4339 - DOGRUN;
	        	break;
	        case HULK:
		    case HULKSTAYPUT:
		    	maxc = 4746 - sprite[i].picnum;
		    	break;
		    case MOSQUITO:
		    	maxc = 6;
		    	break;
		    case PIG:
		    	maxc = 5012 - PIG;
		    	break;
		    case SBMOVE:
		    	maxc = 5114 - SBMOVE;
		    	break;
		    case MINION:
		    case MINIONSTAYPUT:
		    	maxc = 5259 - sprite[i].picnum;
		    	break;
		    case UFO1:
		    case UFO2:
		    case UFO3:
		    case UFO4:
		    case UFO5:
		    	maxc = 4;
		    	break;
		    case COOT:
		    case COOTSTAYPUT:
		    	maxc = 5593 - sprite[i].picnum;
		    	break;
		    case VIXEN:
		    	maxc = 5870 - VIXEN;
		    	break;
		    case HEN:
		    	maxc = 4901 - HEN;
		    	break;
		    	
		    	

		    case BILLYRAYSTAYPUT:
		    	break;
		    case BUBBASTAND:
		    	break;
	    }

	    for(j = sprite[i].picnum; j < (sprite[i].picnum+maxc); j++)
	    	tloadtile(j);
	}

	public static void cachegoodsprites()
	{
	    short i;
	    
	    //HUD
	    tloadtile(BOTTOMSTATUSBAR);
        if( ud.multimode > 1)
        {
            tloadtile(FRAGBAR);
            tloadtile(KILLSICON);
        }
        for( i = DIGITALNUM; i < DIGITALNUM+9; i++)
	    	tloadtile(i);
        
        for( i = 3374; i < 3379; i++) //MASK
	    	tloadtile(i);
        
        tloadtile(ARROW);
        tloadtile(INVENTORYBOX);
        tloadtile(HEALTHBOX);
        tloadtile(AMMOBOX);
    	tloadtile(WHISHKEY_ICON);
    	tloadtile(BOOT_ICON);
    	tloadtile(COWPIE_ICON);
    	tloadtile(SNORKLE_ICON);
    	tloadtile(MOONSHINE_ICON);
    	tloadtile(BEER_ICON);
    	tloadtile(ACCESS_ICON);
    	tloadtile(NEWCROSSHAIR);
    	tloadtile(CROSSHAIR);
    	tloadtile(FRAGBAR-1);
        
    	for(i=920;i<924;i++)
	    	tloadtile(i);
    	for(i=930;i<939;i++)
	    	tloadtile(i);
	    
    	//FONTS
	    for(i=STARTALPHANUM;i<ENDALPHANUM+1;i++)
	    	tloadtile(i);
	    for( i = BIGALPHANUM; i < BIGALPHANUM+82; i++)
	    	tloadtile(i);
	    for(i=MINIFONT;i<MINIFONT+63;i++)
        	tloadtile(i);
	    
	    //WEAPONS
	    for( i = NEWCROWBAR; i < NEWCROWBAR+8; i++ )
	    	tloadtile(i);
	    for( i = NEWPISTOL; i < NEWPISTOL+10; i++ )
	    	tloadtile(i);
	    for( i = NEWSHOTGUN; i < NEWSHOTGUN+8 ; i++ )
	    	tloadtile(i);
	    for( i = 3370; i < 3373; i++ )
	    	tloadtile(i);
	    for( i = RIFLE; i < RIFLE+3 ; i++ )
	    	tloadtile(i);
	    for( i = 1752; i < 1757 ; i++ ) //Dynamite
	    	tloadtile(i);
	    for( i = NEWDYNAMITE; i < NEWDYNAMITE+7; i++ )
	    	tloadtile(i);
	    for( i = CIRCLESTUCK-5; i < CIRCLESTUCK; i++ )
	    	tloadtile(i);
	    for( i = BUZSAW; i < BUZSAW+3; i++ )
	    	tloadtile(i);
	    for( i = 3415; i < 3419; i++ )
	    	tloadtile(i);
	    for( i = 3427; i < 2429; i++ )
	    	tloadtile(i);
	    tloadtile(3438);
	    for( i = 3445; i < 3448; i++ )
	    	tloadtile(i);
	    for( i = 3452; i < 3459; i++ )
	    	tloadtile(i);
	    
	    //PICKUPS
	    for( i = FIRSTGUNSPRITE; i <= ALIENARMGUN; i++ )
	    	tloadtile(i);
	    for( i = AMMO; i <= BOOTS+1; i++ )
	    	tloadtile(i);
	    tloadtile(TEATAMMO);
	    
	    
	    
	    for( i = SHOTSPARK1; i <= SHOTSPARK1+3; i++ )
	    	tloadtile(i);
	    for(i=FOOTPRINTS;i<FOOTPRINTS+3;i++)
	    	tloadtile(i);
	    for( i = BURNING; i < BURNING+14; i++)
	    	tloadtile(i);
	    for( i = BURNING2; i < BURNING2+14; i++)
	    	tloadtile(i);
	    for( i = EXPLOSION2; i < EXPLOSION2+21 ; i++ )
	    	tloadtile(i);
	    tloadtile(BULLETHOLE);
	    for( i = JIBS1; i < (JIBS5+5); i++)
	    	tloadtile(i);
	    for( i = JIBS6; i < (JIBS6+8); i++)
	    	tloadtile(i);
	    for( i = SCRAP1; i < (SCRAP1+19); i++)
	    	tloadtile(i);
	    for( i = SMALLSMOKE; i < (SMALLSMOKE+4); i++)
	    	tloadtile(i);
	}

	public static boolean getsound(int num)
	{
	    if(num >= NUM_SOUNDS || !cfg.SoundToggle) return false;

	    if(sounds[num] == null) return false;
	    int fp = kOpen(sounds[num], loadfromgrouponly);
	    if(fp == -1) return false;

	    int l = kFileLength( fp );
	    soundsiz[num] = l;

//	    if( (ud.level_number == 0 && ud.volume_number == 0 && (num == 189 || num == 232 || num == 99 || num == 233 || num == 17 ) ) || ( l < 12288 ) )
	    {
	        Sound[num].lock = 2;
	        
	        byte[] tmp = new byte[l];
	        kRead( fp, tmp , l);
	        
	        loadSample(tmp, num);
	    }
	    kClose( fp );
	    return true;
	}

	public static void precachenecessarysounds()
	{
	    int j = 0;
	    for(int i=0;i<NUM_SOUNDS;i++)
	        if(Sound[i].ptr == null)
	        {
	            j++;
	            if( (j&7) == 0 )
	                getpackets();
	            getsound(i);
	        }
	}

	public static void cacheit()
	{
	    precachenecessarysounds();

	    cachegoodsprites();

	    for(int i=0;i<numwalls;i++) {
	    	tloadtile(wall[i].picnum);
	        if(wall[i].overpicnum >= 0 )
	            tloadtile(wall[i].overpicnum);
	    }
	    
	    for(int i=0;i<numsectors;i++)
	    {
            tloadtile( sector[i].floorpicnum );
            tloadtile( sector[i].ceilingpicnum );

	        int j = headspritesect[i];
	        while(j >= 0)
	        {
	            if(sprite[j].xrepeat != 0 && sprite[j].yrepeat != 0 && (sprite[j].cstat&32768) == 0)
	            	cachespritenum(j);
	            j = nextspritesect[j];
	        }
	    }
	}
	
//	private static HashSet<Short> picnums = new HashSet<Short>();
	public static void docacheit()
	{
	    int j = 0;
	    for(int i=0;i<MAXTILES;i++) {
	        if( (gotpic[i>>3]&(1<<(i&7))) != 0 )
		    {
	        	if (waloff[i] == null) {
	        		engine.loadtile(i);
	        		engine.invalidatetile(i, 0, 1<<4);
	        	}
		        j++;
		        if((j&7) == 0) getpackets();
		    } 
	    }

	    Arrays.fill(gotpic, (byte)0);
	    
//	    picnums.clear();
//	    for(int i = 0; i < numsectors; i++)
//	    {
//	    	SECTOR s = sector[i];
//	    	picnums.add(s.floorpicnum);
//	    	picnums.add(s.ceilingpicnum);
//	    	for(int w = s.wallptr; w < s.wallptr + s.wallnum; w++)
//	    		picnums.add(wall[w].picnum);
//	    }
//	    for(int i = 0; i < numsprites; i++)
//	    	picnums.add(sprite[i].picnum);
//	    
//	    for(int i = 0; i < picnums.size(); i++) {
//	    	Short tile = (Short)picnums.toArray()[i];
//	    	if(waloff[tile] == null) {
//		    	engine.loadtile(tile);
//		    	engine.invalidatetile(tile, 0, 1<<4);
//	    	}
//	    }
	}
	
	public static void xyzmirror(int i,int wn)
	{
	    engine.setviewtotile(wn,tilesizy[wn],tilesizx[wn]);

	    engine.drawrooms(sprite[i].x,sprite[i].y,sprite[i].z,sprite[i].ang,100+sprite[i].shade,sprite[i].sectnum);
		display_mirror = 1; 
		animatesprites(sprite[i].x,sprite[i].y,sprite[i].z,sprite[i].ang,65536);
		display_mirror = 0;
		engine.drawmasks();

		engine.setviewback();
	}

	public static void pickrandomspot(int snum)
	{
	    int i;

	    PlayerStruct p = ps[snum];

	    if( ud.multimode > 1 && ud.coop != 1)
	        i = engine.krand()%numplayersprites;
	    else i = snum;

	    p.bobposx = p.oposx = p.posx = po[i].ox;
	    p.bobposy = p.oposy = p.posy = po[i].oy;
	    p.oposz = p.posz = po[i].oz;
	    p.ang = po[i].oa;
	    p.cursectnum = po[i].os;
	}
	
	public static void resetplayerstats(int snum)
	{
	    PlayerStruct p = ps[snum];

	    ud.showallmap       = 0;
	    p.dead_flag        = 0;
	    p.wackedbyactor    = -1;
	    p.falling_counter  = 0;
	    p.quick_kick       = 0;
	    p.subweapon        = 0;
	    p.last_full_weapon = 0;
	    p.ftq              = 0;
	    p.fta              = 0;
	    p.tipincs          = 0;
	    p.buttonpalette    = 0;
	    p.actorsqu         =-1;
	    p.invdisptime      = 0;
	    p.refresh_inventory= false;
	    p.last_pissed_time = 0;
	    p.holster_weapon   = 0;
	    p.pycount          = 0;
	    p.pyoff            = 0;
	    p.opyoff           = 0;
	    p.loogcnt          = 0;
	    p.angvel           = 0;
	    p.weapon_sway      = 0;
	    p.extra_extra8     = 0;
	    p.show_empty_weapon= 0;
	    p.dummyplayersprite=-1;
	    p.crack_time       = 0;
	    p.hbomb_hold_delay = 0;
	    p.transporter_hold = 0;
	    p.wantweaponfire  = -1;
	    p.hurt_delay       = 0;
	    p.footprintcount   = 0;
	    p.footprintpal     = 0;
	    p.footprintshade   = 0;
	    p.jumping_toggle   = 0;
	    p.ohoriz = p.horiz= 140;
	    p.horizoff         = 0;
	    p.bobcounter       = 0;
	    p.on_ground        = false;
	    p.player_par       = 0;
	    p.return_to_center = 9;
	    p.airleft          = 15*26;
	    p.rapid_fire_hold  = 0;
	    p.toggle_key_flag  = 0;
	    p.access_spritenum = -1;
	    p.random_club_frame= 0;
	    p.on_warping_sector = 0;
	    p.spritebridge      = 0;
	    p.palette = palette;

	    if(p.moonshine_amount < 400 )
	    {
	        p.moonshine_amount = 0;
	        p.inven_icon = 0;
	    }
	    p.heat_on =            0;
	    p.jetpack_on =         0;
	    p.holoduke_on =       -1;

	    p.look_ang          = (short) (512 - ((ud.level_number&1)<<10));

	    p.rotscrnang        = 0;
	    p.newowner          =-1;
	    p.jumping_counter   = 0;
	    p.hard_landing      = 0;
	    p.posxv             = 0;
	    p.posyv             = 0;
	    p.poszv             = 0;
	    fricxv            = 0;
	    fricyv            = 0;
	    p.somethingonplayer =-1;
	    p.one_eighty_count  = 0;
	    p.cheat_phase       = 0;

	    p.on_crane          = -1;

	    if(p.curr_weapon == PISTOL_WEAPON)
	        p.kickback_pic  = 22;
	    else p.kickback_pic = 0;

	    p.weapon_pos        = 6;
	    p.walking_snd_toggle= 0;
	    p.weapon_ang        = 0;

	    p.knuckle_incs      = 1;
	    p.fist_incs = 0;
	    p.knee_incs         = 0;
	    p.jetpack_on        = 0;
	    setpal(p);
	    
	    p.field_280 = 0;
	    p.field_284 = 0;
	    p.field_X = 0;
	    p.field_Y = 0;
	    p.field_28E = 0;
	    p.field_290 = 0;
	    if ( ud.multimode <= 1 || ud.coop == 1 )
	    {
	    	p.gotkey[0] = 0;
	    	p.gotkey[1] = 0;
	    	p.gotkey[2] = 0;
	    	p.gotkey[3] = 0;
	    	p.gotkey[4] = 0;
	    }
	    else
	    {
	    	p.gotkey[0] = 1;
	    	p.gotkey[1] = 1;
	    	p.gotkey[2] = 1;
	    	p.gotkey[3] = 1;
	    	p.gotkey[4] = 1;
	    }
	    p.alcohol_meter = 1647;
	    p.gut_meter = 1647;
	    p.alcohol_amount = 0;
	    p.gut_amount = 0;
	    p.alcohol_count = 4096;
	    p.gut_count = 4096;
	    p.drunk = 0;
	    p.shotgunstatus = 0;
	    p.shotgun_splitshot = 0;
	    p.field_57C = 0;
	    p.kickback = 0;
	    p.field_count = 0;
	    dword_D7FAC = 0;
	    p.detonate_count = 0;
	    if ( numplayers >= 2 )
	    {
	    	word_18B7A4 = 32;
	    	word_18B7A6 = 0;
	    	word_18B7AA = 2;
	    }
	    else
	    {
	    	 word_18B7A4 = (ud.m_player_skill << 2) + 1;
	    	 if ( word_18B7A4 > 32 )
	    		 word_18B7A4 = 32;
	    	 word_18B7A6 = 0;
	    	 word_18B7AA = ud.m_player_skill + 1;
	    }
	}
	
	public static void resetweapons(int snum)
	{
	    int  weapon;
	    PlayerStruct p = ps[snum];

	    for ( weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++ )
	        p.gotweapon[weapon] = false;
	    for ( weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++ )
	        p.ammo_amount[weapon] = 0;

	    p.weapon_pos = 6;
	    p.kickback_pic = 5;
	    p.curr_weapon = PISTOL_WEAPON;
	    p.gotweapon[PISTOL_WEAPON] = true;
	    p.gotweapon[KNEE_WEAPON] = true;
	    p.ammo_amount[PISTOL_WEAPON] = 48;
	    p.gotweapon[HANDREMOTE_WEAPON] = true;
	    p.last_weapon = -1;

	    p.show_empty_weapon= 0;
	    p.last_pissed_time = 0;
	    p.holster_weapon = 0;
	}

	public static void resetinventory(int snum)
	{
		PlayerStruct p = ps[snum];

	    p.inven_icon       = 0;
	    p.boot_amount = 0;
	    p.scuba_on =           0;p.snorkle_amount =         0;
	    p.empty_amount        = 0;p.heat_on = 0;
	    p.jetpack_on =         0;p.cowpie_amount =       0;
	    p.shield_amount =      (short) max_armour_amount;
	    p.holoduke_on = -1;
	    p.beer_amount =    0;
	    p.whishkey_amount = 0;
	    p.moonshine_amount = 0;
	    p.inven_icon = 0;
	    
	    if ( ud.multimode <= 1 || ud.coop == 1 )
	    {
	    	p.gotkey[0] = 0;
	    	p.gotkey[1] = 0;
	    	p.gotkey[2] = 0;
	    	p.gotkey[3] = 0;
	    	p.gotkey[4] = 0;
	    }
	    else
	    {
	    	p.gotkey[0] = 1;
	    	p.gotkey[1] = 1;
	    	p.gotkey[2] = 1;
	    	p.gotkey[3] = 1;
	    	p.gotkey[4] = 1;
	    }

	    p.alcohol_meter = 1647;
	    p.gut_meter = 1647;
	    p.alcohol_amount = 0;
	    p.gut_amount = 0;
	    p.alcohol_count = 0;
	    p.gut_count = 0;
	    p.drunk = 0;
	    p.shotgunstatus = 0;
	    p.shotgun_splitshot = 0;
	    p.field_57C = 0;
	    p.detonate_count = 0;
	    p.kickback = 0;
	    p.field_count = 0;
	    if ( numplayers >= 2 )
	    {
	    	word_18B7A4 = 32;
	    	word_18B7A6 = 0;
	    	word_18B7AA = 2;
	    }
	    else
	    {
	    	word_18B7A4 = (ud.m_player_skill << 2) + 1;
	    	if ( word_18B7A4 > 32 )
	    		word_18B7A4 = 32;
	    	word_18B7A6 = 0;
	    	word_18B7AA = ud.m_player_skill + 1;
	    }
	}

	public static void resetprestat(int snum, int g)
	{
		PlayerStruct p = ps[snum];

	    spriteqloc = 0;
	    for(int i=0;i<spriteqamount;i++) spriteq[i] = -1;

	    p.hbomb_on          = 0;
	    p.cheat_phase       = 0;
	    p.pals_time         = 0;
	    p.toggle_key_flag   = 0;
	    p.secret_rooms      = 0;
	    p.max_secret_rooms  = 0;
	    p.actors_killed     = 0;
	    p.max_actors_killed = 0;
	    p.lastrandomspot = 0;
	    p.weapon_pos = 6;
	    p.kickback_pic = 5;
	    p.last_weapon = -1;
	    p.weapreccnt = 0;
	    p.show_empty_weapon= 0;
	    p.holster_weapon = 0;
	    p.last_pissed_time = 0;

	    p.one_parallax_sectnum = -1;
	    p.visibility = ud.const_visibility;

	    screenpeek              = myconnectindex;
	    numanimwalls            = 0;
	    numcyclers              = 0;
	    gAnimationCount         = 0;
	    parallaxtype            = 0;
	    engine.srand(17);
	    ud.pause_on             = 0;
	    ud.camerasprite         =-1;
	    ud.eog                  = 0;
	    tempwallptr             = 0;
	    camsprite               =-1;
	    earthquaketime          = 0;

	    InterpolationCount = 0;
	    startofdynamicinterpolations = 0;

	    if( ( (g&MODE_EOL) != MODE_EOL && numplayers < 2) || (ud.coop != 1 && numplayers > 1) )
	    {
	        resetweapons(snum);
	        resetinventory(snum);
	    }
	    else if(p.curr_weapon == HANDREMOTE_WEAPON)
	    {
	        p.ammo_amount[4]++;
	        p.curr_weapon = 4;
	    }

	    p.timebeforeexit   = 0;
	    p.customexitsound  = 0;
	    
	    p.field_280 = 0;
	    p.field_284 = 0;
	    p.field_X = 0x20000;
	    p.field_Y = 0x20000;
	    p.field_28E = 0;
	    p.field_290 = 0;
	    if ( ud.multimode <= 1 || ud.coop == 1 )
	    {
	    	p.gotkey[0] = 0;
	    	p.gotkey[1] = 0;
	    	p.gotkey[2] = 0;
	    	p.gotkey[3] = 0;
	    	p.gotkey[4] = 0;
	    }
	    else
	    {
	    	p.gotkey[0] = 1;
	    	p.gotkey[1] = 1;
	    	p.gotkey[2] = 1;
	    	p.gotkey[3] = 1;
	    	p.gotkey[4] = 1;
	    }

	    p.alcohol_meter = 1647;
	    p.gut_meter = 1647;
	    p.alcohol_amount = 0;
	    p.gut_amount = 0;
	    p.alcohol_count = 0;
	    p.gut_count = 0;
	    p.drunk = 0;
	    p.shotgunstatus = 0;
	    p.shotgun_splitshot = 0;
	    p.field_57C = 0;
	    p.detonate_count = 0;
	    p.kickback = 0;
	    p.field_count = 0;

	    if ( numplayers >= 2 )
	    {
	    	word_18B7A4 = 32;
	    	word_18B7A6 = 0;
	    	word_18B7AA = 2;
	    }
	    else
	    {
	     	word_18B7A4 = (ud.m_player_skill << 2) + 1;
	     	if ( word_18B7A4 > 32 )
	     		word_18B7A4 = 32;
	     	word_18B7A6 = 0;
	     	word_18B7AA = ud.m_player_skill + 1;
	    }
	}

	public static void setupbackdrop(short sky)
	{
		Arrays.fill(pskyoff, (short)0);

	    parallaxyscale = 32768;

	    switch(sky)
	    {
	        case 1022:
	            pskyoff[6]=1; pskyoff[1]=2; pskyoff[4]=2; pskyoff[2]=3;
	            break;
	        case 1026:
	            pskyoff[5]=1; pskyoff[6]=2; pskyoff[7]=3; pskyoff[2]=4;
	            break;
	        case 1031:
	            parallaxyscale = 16384+1024;
	            pskyoff[0]=1; pskyoff[1]=2; pskyoff[2]=1; pskyoff[3]=3;
	            pskyoff[4]=4; pskyoff[5]=0; pskyoff[6]=2; pskyoff[7]=3;
	            break;
	   }
	    
	   Arrays.fill(zeropskyoff, (short)0);
	   System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

	   pskybits=2;
	}
	
	public static final short lotags[] = new short[65];
	public static void prelevel(int g)
	{
	    short i, nexti, j, startwall, endwall, lotaglist;
	    Arrays.fill(lotags,(short)0);

	    Arrays.fill(show2dsector,(byte)0);
	    Arrays.fill(show2dwall,(byte)0);
	    Arrays.fill(show2dsprite,(byte)0);
	    
	    Arrays.fill(shadeEffect, false);
	    Arrays.fill(geoms1, (short)-1);
	    Arrays.fill(geoms2, (short)-1);
	    Arrays.fill(ambienttype, (short)-1);
	    Arrays.fill(ambientid, (short)-1);
	    Arrays.fill(ambienthitag, (short)-1);

	    resetprestat(0,g);
	    
	    numlightnineffects = 0;
	    numtorcheffects = 0;
	    numgeomeffects = 0;
	    numjaildoors = 0;
	    numminecart = 0;
	    numambients = 0;
	    haveLigthning = 0;
	    plantProcess = false;
	    
	    BowlReset();
	    
	    int distance = 0, speed = 0, sound = 0;
	    for(i=0;i<numsectors;i++)
	    {
	        sector[i].extra = 256;
	        if ( sector[i].ceilingpicnum == STARSKY2 )
	            haveLigthning = 1;

	        switch(sector[i].lotag)
	        {
	            case 20:
	            case 22:
	                if( sector[i].floorz > sector[i].ceilingz)
	                    sector[i].lotag |= 32768;
	                continue;
	            case 41:
	            	j = headspritesect[i];
	            	while(j >= 0)
	     	        {
	            		nexti = nextspritesect[j];
	     	        	if(sprite[j].picnum == JAILDOOR)
	     	            {
	     	            	distance = sprite[j].lotag << 4;
	     	            	speed = sprite[j].hitag;
	     	                engine.deletesprite(j);
	     	            }
	     	        	if(sprite[j].picnum == JAILSOUND)
	     	        	{
	     	        		sound = sprite[j].lotag;
	     	        		engine.deletesprite(j);
	     	        	}
	     	            j = nexti;
	     	        }
	            	
	            	for(j = 0; j < numsectors; j++)
	            	{
	            		if(sector[i].hitag == sector[j].hitag && i != j)
	            		{
	            			if ( numjaildoors > MAXJAILDOORS )
	                            dassert("Too many jaildoor sectors");
	            			
	            			int num = numjaildoors;
	            			jailspeed[num] = speed;
	            			jaildistance[num] = distance;
	            			jailsect[num] = j;
	            			jaildirection[num] = sector[j].lotag;
	            			jailunique[num] = sector[i].hitag;
	            			jailsound[num] = (short) sound;
	            			jailstatus[num] = 0;
	            			jailcount2[num] = 0;
	            			
	            			numjaildoors++;
	            		}
	            	}
	            	break;
	            case 42:
	            	j = headspritesect[i];
	            	while(j >= 0)
	     	        {
	            		nexti = nextspritesect[j];
	     	        	if(sprite[j].picnum == 64)
	     	            {
	     	            	distance = sprite[j].lotag << 4;
	     	            	speed = sprite[j].hitag;
	     	            	
	     	            	for(int k = 0; k < MAXSPRITES; k++)
	     	            	{
	     	            		if ( sprite[k].picnum == 66 )
	     	            		{
	     	                    	if ( sprite[k].lotag == sprite[j].sectnum )
	     	                    	{
	     	                    		minechild[numminecart] = sprite[k].sectnum;
	     	                    		engine.deletesprite(k);
	     	                    	}
	     	            		}
	     	            	}
	     	                engine.deletesprite(j);
	     	            }
	     	        	if(sprite[j].picnum == 65)
	     	        	{
	     	        		sound = sprite[j].lotag;
	     	        		engine.deletesprite(j);
	     	        	}
	     	            j = nexti;
	     	        }

        			if ( numminecart > MAXMINECARDS )
                        dassert("Too many minecart sectors");
        			
        			int num = numminecart;
        			minespeed[num] = speed;
        			mineparent[num] = i;
        			minedirection[num] = sector[i].hitag;
        			minefulldist[num] = distance;
        			minedistance[num] = distance;
        			minesound[num] = (short) sound;
        			minestatus[num] = 1;
        			numminecart++;
	            	break;
	        }

	        if((sector[i].ceilingstat&1) != 0)
	        {
                if(sector[i].ceilingpicnum == 1031)
                    for(j=0;j<5;j++)
                    	tloadtile(sector[i].ceilingpicnum+j);
	            
	            setupbackdrop(sector[i].ceilingpicnum);

	            if(ps[0].one_parallax_sectnum == -1)
	                ps[0].one_parallax_sectnum = i;
	        }

	        if(sector[i].lotag == 32767) //Found a secret room
	        {
	            ps[0].max_secret_rooms++;
	            continue;
	        }

	        if(sector[i].lotag == -1)
	        {
	            ps[0].exitx = wall[sector[i].wallptr].x;
	            ps[0].exity = wall[sector[i].wallptr].y;
	            continue;
	        }
	    }
	  
	    i = headspritestat[0];
	    while(i >= 0)
	    {
	        nexti = nextspritestat[i];

	        if(sprite[i].lotag == -1 && (sprite[i].cstat&16) != 0 )
	        {
	            ps[0].exitx = sprite[i].x;
	            ps[0].exity = sprite[i].y;
	        }
	        else switch(sprite[i].picnum)
	        {
	            case GPSPEED:
	                sector[sprite[i].sectnum].extra = sprite[i].lotag;
	                engine.deletesprite(i);
	                break;

	            case CYCLER:
	                if(numcyclers >= MAXCYCLERS)
	                    dassert("\nToo many cycling sectors.");
	                cyclers[numcyclers][0] = sprite[i].sectnum;
	                cyclers[numcyclers][1] = sprite[i].lotag;
	                cyclers[numcyclers][2] = sprite[i].shade;
	                cyclers[numcyclers][3] = sector[sprite[i].sectnum].floorshade;
	                cyclers[numcyclers][4] = sprite[i].hitag;
	                cyclers[numcyclers][5] = (short) ((sprite[i].ang == 1536)?1:0);
	                numcyclers++;
	                engine.deletesprite(i);
	                break;
	            case TORCH:
	            	if(numtorcheffects >= MAXTORCHES)
	                    dassert("Too many torch effects.");
	            	
	            	int num = numtorcheffects;
	            	torchsector[num] = sprite[i].sectnum;
	            	torchshade[num] = sector[sprite[i].sectnum].floorshade;
	            	torchflags[num] = sprite[i].lotag;
	            	numtorcheffects++;
	            	engine.deletesprite(i);
	            	break;
	            	
	            case LIGHTNIN:
	            	if(numlightnineffects >= MAXLIGHTNINS)
	                    dassert("Too many lightnin effects.");
	            	
	            	int lnum = numlightnineffects;
	            	lightninsector[lnum] = sprite[i].sectnum;
	                lightninshade[lnum] = sprite[i].lotag;
	                numlightnineffects++;
	                engine.deletesprite(i);
	            	break;
	            	
	            case MINECARTKILLER:
	            	sprite[i].cstat |= 32768;
	            	break;
	            	
	            case SHADESECTOR:
	            	shadeEffect[sprite[i].sectnum] = true;
	            	engine.deletesprite(i);
	            	break;
	            	
	            case SOUNDFX:
	            	if(numambients >= MAXAMBIENTS)
	                    dassert("Too many ambient effects.");
	            	
	            	int anum = numambients;
	            	ambientid[anum] = i;
	            	ambienttype[anum] = sprite[i].lotag;
	            	ambienthitag[anum] = sprite[i].hitag;
	            	sprite[i].ang = (short) numambients;
	            	sprite[i].lotag = 0;
	            	sprite[i].hitag = 0;
	            	numambients++;
	            	break;
	            	
	            case 94:
	            	plantProcess = true;
	            	break;
	        }
	        i = nexti;
	    }
	    
	    for(i=0;i < MAXSPRITES;i++)
	    {
	    	if(sprite[i].picnum == 19)
	    	{
	    		if(numgeomeffects >= MAXGEOMETRY)
                    dassert("Too many geometry effects.");
	    		if(sprite[i].hitag == 0)
	    		{
	    			geomsector[numgeomeffects] = sprite[i].sectnum;
	    			for(int k=0;k < MAXSPRITES;k++)
	    			{
	    				if(sprite[k].lotag == sprite[i].lotag && i != k && sprite[k].picnum == 19)
	    				{
	    					if(sprite[k].hitag == 1)
	    					{
	    						geoms1[numgeomeffects] = sprite[k].sectnum;
	    						geomx1[numgeomeffects] = sprite[i].x - sprite[k].x;
	    						geomy1[numgeomeffects] = sprite[i].y - sprite[k].y;
	    						geomz1[numgeomeffects] = sprite[i].z - sprite[k].z;
	    					}
	    					if(sprite[k].hitag == 2)
	    					{
	    						geoms2[numgeomeffects] = sprite[k].sectnum;
	    						geomx2[numgeomeffects] = sprite[i].x - sprite[k].x;
	    						geomy2[numgeomeffects] = sprite[i].y - sprite[k].y;
	    						geomz2[numgeomeffects] = sprite[i].z - sprite[k].z;
	    					}
	    				}
	    			}
	    			numgeomeffects++;
	    		}
	    		
	    	}
	    }

	    for(i=0;i < MAXSPRITES;i++)
	    {
	        if(sprite[i].statnum < MAXSTATUS)
	        {
	            if(sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 14)
	                continue;
	            spawn(-1,i);
	        }
	    }

	    for(i=0;i < MAXSPRITES;i++)
	        if(sprite[i].statnum < MAXSTATUS)
	        {
	            if( sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 14 )
	                spawn(-1,i);
	            
	            if(sprite[i].picnum == 19)
	            	engine.deletesprite(i);
	            
	            if(sprite[i].picnum == DOORKEYS)
	            {
	            	sector[sprite[i].sectnum].filler = sprite[i].lotag;
	            	engine.deletesprite(i);
	            }
	        }

	    lotaglist = 0;

	    i = headspritestat[0];
	    while(i >= 0)
	    {
	        switch(sprite[i].picnum)
	        {
	            case 85:
	            case 87:
	            case 89:
	            case 91:
	            case 93:
	            case 94:
	            case 95:
	            case 122:
	            case 124:
	            case LIGHTSWITCH2+1:
	            case 2223:
	            case LOCKSWITCH1+1:
	            case 2227:
	            case 2250:
	            case 2255:
	                for(j=0;j<lotaglist;j++)
	                    if( sprite[i].lotag == lotags[j] )
	                        break;

	                if( j == lotaglist )
	                {
	                    lotags[lotaglist] = sprite[i].lotag;
	                    lotaglist++;
	                    if(lotaglist > 64)
	                        dassert("\nToo many switches (64 max).");

	                    j = headspritestat[3];
	                    while(j >= 0)
	                    {
	                        if(sprite[j].lotag == 12 && sprite[j].hitag == sprite[i].lotag)
	                            hittype[j].temp_data[0] = 1;
	                        j = nextspritestat[j];
	                    }
	                }
	                break;
	        }
	        i = nextspritestat[i];
	    }

	    mirrorcnt = 0;
	    for( i = 0; i < numwalls; i++ )
	    {
	        WALL wal = wall[i];

	        if(wal.overpicnum == MIRROR && (wal.cstat&32) != 0)
	        {
	            j = wal.nextsector;

	            if(mirrorcnt > 63)
	                dassert("\nToo many mirrors (64 max.)");
	            if ( (j >= 0) && sector[j].ceilingpicnum != MIRROR )
	            {
	                sector[j].ceilingpicnum = MIRROR;
	                sector[j].floorpicnum = MIRROR;
	                mirrorwall[mirrorcnt] = i;
	                mirrorsector[mirrorcnt] = j;
	                mirrorcnt++;
	                continue;
	            }
	        }

	        if(numanimwalls >= MAXANIMWALLS)
	            dassert("\nToo many 'anim' walls (max 512.)");

	        animwall[numanimwalls].tag = 0;
	        animwall[numanimwalls].wallnum = 0;

	        switch(wal.overpicnum)
	        {
	            case FANSPRITEWORK:
	            	wall[0].cstat |= 65; //original typo wall->cstat |= 65 instead of wal->cstat |= 65;
	                animwall[numanimwalls].wallnum = i;
	                numanimwalls++;
	                break;
	            case BIGFORCE:

	                animwall[numanimwalls].wallnum = i;
	                numanimwalls++;

	                continue;
	        }

	        wal.extra = -1;

	        switch(wal.picnum)
	        {
	            case WATERTILE2:
	                for(j=0;j<3;j++)
	                	tloadtile(wal.picnum+j);
	                break;

	            case BUSTAWIN4A:
	            case BUSTAWIN4B:
	            case BUSTAWIN5A:
	            case BUSTAWIN5B:
	               	tloadtile(wal.picnum);
	                break;
	            case SCREENBREAK6:
	            	for(j=SCREENBREAK6;j<SCREENBREAK8;j++)
	                	tloadtile(j);
	                animwall[numanimwalls].wallnum = i;
	                animwall[numanimwalls].tag = -1;
	                numanimwalls++;
	                break;
	        }
	    }

	    //Invalidate textures in sector behind mirror
	    for(i=0;i<mirrorcnt;i++)
	    {
	        startwall = sector[mirrorsector[i]].wallptr;
	        endwall = (short) (startwall + sector[mirrorsector[i]].wallnum);
	        for(j=startwall;j<endwall;j++)
	        {
	            wall[j].picnum = MIRROR;
	            wall[j].overpicnum = MIRROR;
	        }
	    }
	    
	    if ( haveLigthning == 0 )
	    { 
//XXX	    	engine.setbrightness((ud.brightness >> 2) & 0xFF, palette);
	    	visibility = ps[screenpeek].visibility;
	    }
	    
	    tilesizy[0] = 0;
	    tilesizx[0] = 0;
	    waloff[0] = null;
	    
	    gNameShowTime = 500;
	}
	
	public static void newgame(int vn,int ln,int sk)
	{
	    PlayerStruct p = ps[0];
	    short i;

	    ready2send = false;
	    setgamepalette(ps[myconnectindex], palette, 3);

	    gEndFirstEpisode = 0;
	    gEndGame = 0;
	    ud.level_number =   ln;
	    ud.volume_number =  vn;
	    ud.player_skill =   sk;
	    ud.secretlevel =    0;
	    ud.from_bonus = 0;
	    parallaxyscale = 0;

	    ud.last_level = -1;
	    p.zoom            = 768;

	    if(ud.m_coop != 1)
	    {
	        p.curr_weapon = PISTOL_WEAPON;
	        p.gotweapon[PISTOL_WEAPON] = true;
	        p.gotweapon[KNEE_WEAPON] = true;
	        p.ammo_amount[PISTOL_WEAPON] = 48;
	        p.gotweapon[HANDREMOTE_WEAPON] = true;
	        p.last_weapon = -1;
	    }

	    display_mirror =        0;

	    if(ud.multimode > 1 )
	    {
	        if(numplayers < 2)
	        {
	            connecthead = 0;
	            for(i=0;i<MAXPLAYERS;i++) connectpoint2[i] = (short) (i+1);
	            connectpoint2[ud.multimode-1] = -1;
	        }
	    }
	    else
	    {
	        connecthead = 0;
	        connectpoint2[0] = -1;
	    }
	}
	
	public static void LeaveMap()
	{
		gm = MODE_EOL;
		ready2send = false;
		gLoadingTicks = 0.0f;
		closedemowrite();
		
		lastmapname = level_names[(ud.volume_number*11)+ud.level_number];
	}
	
	public static PlayerInfo[] info = new PlayerInfo[MAXPLAYERS];
	
	public static void resetpspritevars(int  g)
	{
	    short i, j, nexti;

	    EGS(ps[0].cursectnum,ps[0].posx,ps[0].posy,ps[0].posz,
	        APLAYER,0,0,0,(short)ps[0].ang,0,0,0,10);

	    if(ud.recstat != 2) for(i=0;i<MAXPLAYERS;i++)
	    {
	    	if(info[i] == null) 
	    		info[i] = new PlayerInfo();
	    	info[i].set(ps[i]);
	    }

	    resetplayerstats(0);

	    for(i=1;i<MAXPLAYERS;i++) 
	    	ps[i].copy(ps[0]);

	    if(ud.recstat != 2) for(i=0;i<MAXPLAYERS;i++)
	    	info[i].restore(ps[i]);

	    numplayersprites = 0;

	    which_palookup = 9;
	    j = connecthead;
	    i = headspritestat[10];
	    while(i >= 0)
	    {
	        nexti = nextspritestat[i];
	        SPRITE s = sprite[i];

	        if( numplayersprites == MAXPLAYERS)
	            dassert("\nToo many player sprites (max 16.)");

	        po[numplayersprites].ox = s.x;
	        po[numplayersprites].oy = s.y;
	        po[numplayersprites].oz = s.z;
	        po[numplayersprites].oa = s.ang;
	        po[numplayersprites].os = s.sectnum;

	        numplayersprites++;
	        if(j >= 0)
	        {
	            s.owner = i;
	            s.shade = 0;
	            s.xrepeat = 24;
	            s.yrepeat = 17;
	            s.cstat = 1+256;
	            s.xoffset = 0;
	            s.clipdist = 64;

	            if( (g&MODE_EOL) != MODE_EOL || ps[j].last_extra == 0)
	            {
	                ps[j].last_extra = (short) max_player_health;
	                s.extra = (short) max_player_health;
	            }
	            else s.extra = ps[j].last_extra;

	            s.yvel = j;

	            if(s.pal == 0)
	            {
	                s.pal = ps[j].palookup = which_palookup;
	                which_palookup++;
	                if( which_palookup >= 17 ) which_palookup = 9;
	            }
	            else ps[j].palookup = s.pal;

	            ps[j].i = i;
	            ps[j].frag_ps = j;
	            hittype[i].owner = i;

	            hittype[i].bposx = ps[j].bobposx = ps[j].oposx = ps[j].posx =        s.x;
	            hittype[i].bposy = ps[j].bobposy = ps[j].oposy = ps[j].posy =        s.y;
	            hittype[i].bposz = ps[j].oposz = ps[j].posz =        s.z;
	            ps[j].oang  = ps[j].ang  =        s.ang;

	            ps[j].cursectnum = engine.updatesector(s.x,s.y,ps[j].cursectnum);

	            j = connectpoint2[j];

	        }
	        else engine.deletesprite(i);
	        i = nexti;
	    }
	}
	
	public static void clearfrags()
	{
	    for(int i = 0;i<MAXPLAYERS;i++) {
	        ps[i].frag = ps[i].fraggedself = 0;
	        Arrays.fill(frags[i], (short) 0);
	    }
	}
	
	public static void resettimevars()
	{
		engine.sampletimer();
		
	    vel = svel = 0;
	    horiz = angvel = 0;

	    totalclock = 0;
	    ototalclock = 0;
	    lockclock = 0;

	    numframes = 0;
	    
	    ready2send = true;
	}
	
	public static void genspriteremaps()
	{
	    int j;
	    int look_pos;
	    int numl = 0;

	    int fp = kOpen("lookup.dat",0);
	    if(fp != -1)
	    	numl = kRead(fp,1);
	    else
	        dassert("\nERROR: File 'LOOKUP.DAT' not found.");

	    for(j=0; j < numl; j++)
	    {
	        look_pos = kRead(fp,1);
	        kRead(fp,tempbuf,256);
	        engine.makepalookup(look_pos,tempbuf,0,0,0,1);
	    }

	    kRead(fp,waterpal,768);
	    kRead(fp,slimepal,768);
	    kRead(fp,titlepal,768);
	    kRead(fp,drealms,768);
	    kRead(fp,endingpal,768);

	    palette[765] = palette[766] = palette[767] = 0;
	    slimepal[765] = slimepal[766] = slimepal[767] = 0;
	    waterpal[765] = waterpal[766] = waterpal[767] = 0;

	    kClose(fp);
	    
	    for(int i = 0; i < 768; i++)
	    	tempbuf[i] = (byte) i;
	    for(int i = 0; i < 32; i++)
	    	tempbuf[i] = (byte) (i + 32);
	    engine.makepalookup(7,tempbuf,0,0,0,1);
	    for(int i = 0; i < 768; i++)
	    	tempbuf[i] = (byte) i;
	    engine.makepalookup(30, tempbuf, 0, 0, 0, 1);
	    engine.makepalookup(31, tempbuf, 0, 0, 0, 1);
	    engine.makepalookup(32, tempbuf, 0, 0, 0, 1);
	    engine.makepalookup(33, tempbuf, 0, 0, 0, 1);
	    
	    int col = 63;
	    for(int i = 64; i < 80; i++) {
	    	tempbuf[i] = (byte) (--col);
	    	tempbuf[i+16] = (byte) (i - 24);
	    }
	    for(int i = 0; i < 32; i++) 
	    	tempbuf[i] = (byte) (i + 32);
	    engine.makepalookup(34, tempbuf, 0, 0, 0, 1);
	    
	    for(int i = 0; i < 768; i++)
	    	tempbuf[i] = (byte) i;
	    for(int i = 0; i < 16; i++)
	    	tempbuf[i] = (byte) (i - 127);
	    for(int i = 16; i < 32; i++) 
	    	tempbuf[i] = (byte) (i - 64);
	    engine.makepalookup(35, tempbuf, 0, 0, 0, 1);
	}

	public static void dofrontscreens()
	{
		gLoadingTicks = 0;
		gm = MODE_LOADING;
	}

	public static void clearfifo()
	{
	    bufferjitter = 1;
	    mymaxlag = otherminlag = 0;

	    movefifoplc = movefifosendplc = fakemovefifoplc = 0;
	    avgfvel = avgsvel = avgbits = 0;
	    avghorz = avgavel = 0;
	    otherminlag = mymaxlag = 0;
	    
	    loc.clear();
	    for(int i = 0; i < MAXPLAYERS; i++)
	    	sync[i].clear();
	    for(int i = 0; i < MOVEFIFOSIZ; i++)
	    	for(int j = 0; j < MAXPLAYERS; j++)
	    		inputfifo[i][j].clear();
	    
	    Arrays.fill(movefifoend, 0);
	    Arrays.fill(myminlag, 0);
	    syncvaltail = 0;
	    syncvaltottail = 0;
	    syncstat = 0;
	    for(int i = 0; i < MAXPLAYERS; i++)
	    	Arrays.fill(syncval[i], (byte)0);
	    Arrays.fill(syncvalhead, 0);
	}
	
	public static void resetmys()
	{
	      myx = omyx = ps[myconnectindex].posx;
	      myy = omyy = ps[myconnectindex].posy;
	      myz = omyz = ps[myconnectindex].posz;
	      myxvel = myyvel = myzvel = 0;
	      myang = omyang = ps[myconnectindex].ang;
	      myhoriz = omyhoriz = (short) ps[myconnectindex].horiz;
	      myhorizoff = omyhorizoff = ps[myconnectindex].horizoff;
	      mycursectnum = ps[myconnectindex].cursectnum;
	      myjumpingcounter = ps[myconnectindex].jumping_counter;
	      myjumpingtoggle = (char) ps[myconnectindex].jumping_toggle;
	      myonground = ps[myconnectindex].on_ground;
	      myhardlanding = (char) ps[myconnectindex].hard_landing;
	      myreturntocenter = (char) ps[myconnectindex].return_to_center;
	}
	
	private static int[] posx = new int[1], posy = new int[1], posz = new int[1];
	private static short[] sect = new short[1], ang = new short[1];
	
	public static void enterlevel(final int g)
	{
		if( (g&MODE_DEMO) != MODE_DEMO ) ud.recstat = ud.m_recstat;
		ud.respawn_monsters = ud.m_respawn_monsters;
	    ud.respawn_items    = ud.m_respawn_items;
	    ud.respawn_inventory    = ud.m_respawn_inventory;
	    ud.monsters_off = ud.m_monsters_off;
	    ud.coop = ud.m_coop;
	    ud.marker = ud.m_marker;
	    ud.ffire = ud.m_ffire;

	    if( (g&MODE_DEMO) == 0 && ud.recstat == 2)
	        ud.recstat = 0;

	    StopAllSounds();
	    clearsoundlocks();
	    engine.getAudio().getSound().setReverb(0);

	    dofrontscreens();
		    
	    Gdx.app.postRunnable(new Runnable() {
			public void run() {
			    if( ud.warp_on == 1 && boardfilename != null && ud.m_level_number == 3 && ud.m_volume_number == 2 )
			    {
			        if ( engine.loadboard( boardfilename,posx, posy, posz, ang, sect ) == -1 )
			            dassert("Map " + boardfilename + " not found!");
			    }
			    else {
			    	String map = new String(level_file_names[ (ud.volume_number*11)+ud.level_number]).trim();
			    	if(gEndGame != 0) {
			    		map = "endgame.map";
			    		ud.level_number = 0;
			    	}
			    	if ( engine.loadboard(map,posx, posy, posz, ang, sect ) == -1)
			    		dassert("Map " + map + " not found!");
			    }
			    
			    ps[0].posx = posx[0];
			    ps[0].posy = posy[0];
			    ps[0].posz = posz[0];
			    ps[0].ang = ang[0];
			    ps[0].cursectnum = sect[0];
			    
			    Arrays.fill(gotpic, (byte)0);
			    prelevel(g);
			    allignwarpelevators();
			    resetpspritevars(g);
		
		//	    cachedebug = 0;
			    automapping = 0;
		
			    if(ud.recstat != 2) {
			    	if(currMusic != null)
			    		currMusic.stop();
			    }
		
			    cacheit();
			    docacheit();

			    if(ud.recstat != 2)
			    {
			    	musicvolume = ud.volume_number;
			    	musiclevel = ud.level_number;
			    	sndPlayMusic(music_fn[musicvolume][musiclevel]);
			    }
			    
			    if( (ud.recstat == 1) && (g&MODE_RESTART) != MODE_RESTART )
			        opendemowrite(GDXBYTEVERSION);

			    for(int i=connecthead;i>=0;i=connectpoint2[i]) {
			    	if(sprite[ps[i].i].sectnum == 1024) continue;
			        switch(sector[sprite[ps[i].i].sectnum].floorpicnum)
			        {
			            case HURTRAIL:
			                resetweapons(i);
			                resetinventory(i);
			                ps[i].gotweapon[PISTOL_WEAPON] = false;
			                ps[i].ammo_amount[PISTOL_WEAPON] = 0;
			                ps[i].curr_weapon = KNEE_WEAPON;
			                ps[i].kickback_pic = 0;
			                break;
			        }
			    }
		
			      //PREMAP.C - replace near the my's at the end of the file

			     ps[myconnectindex].palette = palette;
		
			     setpal(ps[myconnectindex]);
		
			     everyothertime = 0;
			     global_random = 0;
		
			     ud.last_level = ud.level_number+1;

			     for ( int i = 0; i < InterpolationCount; i++ )
			     {
					INTERPOLATION gInt = gInterpolationData[i];
					Object obj = gInt.ptr;
					switch(gInt.type)
					{
						case WALLX:
							gInt.bakpos = ((WALL)obj).x;
							break;
						case WALLY:
							gInt.bakpos = ((WALL)obj).y;
							break;
						case FLOORZ:
							gInt.bakpos = ((SECTOR)obj).floorz;
							break;
						case CEILZ:
							gInt.bakpos = ((SECTOR)obj).ceilingz;
							break;
					}
			     }

			     changepalette = 1;

			     waitforeverybody(0);
		
			     palto(0,0,0,0);
			     if(!gShowMenu)
			    	 vscrn(ud.screen_size);
			     engine.clearview(0);

			     Arrays.fill(playerquitflag, 1);
			     ps[myconnectindex].over_shoulder_on = 0;
		
			     resetmys();
			     clearfifo();
			     clearfrags();
			    
			     resettimevars();  // Here we go
			     
			     if(ud.recstat == 2)
		    		 gm = MODE_DEMO;

			     if( (g&MODE_GAME)  != 0 || (g&MODE_EOL) != 0 )
			    	 gm = MODE_GAME;
			     else if((g&MODE_RESTART) != 0)
			     {
			    	 if(ud.recstat == 2)
			    		 gm = MODE_DEMO;
			    	 else gm = MODE_GAME;
			     }
			}
		});
	}

}

class PlayerInfo
{
	public int aimmode;
	public int autoaim;

	public int[] ammo_amount = new int[MAX_WEAPONS];
	public boolean[] gotweapon = new boolean[MAX_WEAPONS];
	
   	public short shield_amount;
   	public short curr_weapon;
   	public int inven_icon;

   	public short whishkey_amount;
   	public short moonshine_amount;
   	public short beer_amount;
   	public short cowpie_amount;
   	public short empty_amount;
   	public short snorkle_amount;
   	public short boot_amount;
   	
   	public void set(PlayerStruct p)
   	{
   		aimmode = p.aim_mode;
        autoaim = p.auto_aim;

        if(ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0)
        {
            for(int j=0;j<MAX_WEAPONS;j++)
            {
                ammo_amount[j] = p.ammo_amount[j];
                gotweapon[j] = p.gotweapon[j];
            }
            shield_amount = p.shield_amount;
            curr_weapon = p.curr_weapon;
            inven_icon = p.inven_icon;

            whishkey_amount = p.whishkey_amount;
            moonshine_amount = p.moonshine_amount;
            beer_amount = p.beer_amount;
            cowpie_amount = p.cowpie_amount;
            empty_amount = p.empty_amount;
            snorkle_amount = p.snorkle_amount;
            boot_amount = p.boot_amount;
        }
   	}
   	
   	public void restore(PlayerStruct p)
   	{
   		p.aim_mode = aimmode;
        p.auto_aim = autoaim;

        if(ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0)
        {
            for(int j=0;j<MAX_WEAPONS;j++)
            {
                p.ammo_amount[j] = ammo_amount[j];
                p.gotweapon[j] = gotweapon[j];
            }
            p.shield_amount = shield_amount;
            p.curr_weapon = curr_weapon;
            p.inven_icon = inven_icon;

            p.whishkey_amount = whishkey_amount;
            p.moonshine_amount= moonshine_amount;
            p.beer_amount = beer_amount;
            p.cowpie_amount = cowpie_amount;
            p.empty_amount = empty_amount;
            p.snorkle_amount= snorkle_amount;
            p.boot_amount = boot_amount;
        }
   	}
}	
