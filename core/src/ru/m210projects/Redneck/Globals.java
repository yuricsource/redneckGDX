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

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Compat.FilePath;
import static ru.m210projects.Build.OnSceenDisplay.Console.CloseLogFile;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Redneck.Redneck.appdispose;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Types.BugReport.saveToFTP;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;

import com.badlogic.gdx.Gdx;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Redneck.Types.SoundOwner;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Sample;
import ru.m210projects.Redneck.Types.UserDefs;
import ru.m210projects.Redneck.Types.Weaponhit;

public class Globals {
	
	public static final int RR = 0;
	public static final int RR66 = 1;
	public static final int RRRA = 2;
	
	public static int GameCON = RR;
	
	public static final int BYTEVERSIONRR = 108;
	public static final int GDXBYTEVERSION = 147;
	
	public static final int BYTEVERSION = GDXBYTEVERSION;
	
	public static final int TICRATE = 120;
	public static final int TICSPERFRAME = (TICRATE/26);
	public static final int TIMERUPDATESIZ = 32;
	
	public static final int TILE_LOADSHOT = MAXTILES - 2;
	public static final int TILE_ANIM = MAXTILES - 3;
	public static final int TILE_VIEWSCR = MAXTILES - 4;
	
	public static int VIEWSCR_Lock = 199;
	public static int[] zofslope = new int[2];
	
	//XXX RA
	public static int BellSound = 0;
	public static int WindDir;
	public static int WindTime;
	
	public static int numepisodes, numlevels[] = new int[3];
	
	public static final int MAX_WEAPONS = 13;
	public static final int MAX_WEAPONSRA = 17;
	
	public static final int MAXANIMWALLS = 512;
	public static final int NUMOFFIRSTTIMEACTIVE = 192;
	
	public static final int MAXCYCLERS = 256;
	
	public static final int RECSYNCBUFSIZ = 2520; //2520 is the (LCM of 1-8)*3

	public static final int MOVEFIFOSIZ = 256;
	
	public static final int FOURSLEIGHT = 1 << 8;
	public static final int PHEIGHT = 10240;
	
	public static final int MAXSLEEPDIST = 16384;
	public static final int SLEEPTIME = 24*64;
	
	public static final int AUTO_AIM_ANGLE = 48;
	
	// These tile positions are reserved!;
	public static final int RESERVEDSLOT1 = 6132;
	public static final int RESERVEDSLOT2 = 6133;
	public static final int RESERVEDSLOT3 = 6134;
	public static final int RESERVEDSLOT4 = 6135;
	public static final int RESERVEDSLOT5 = 6136;
	public static final int RESERVEDSLOT6 = 6137;
	public static final int RESERVEDSLOT7 = 6138;
	public static final int RESERVEDSLOT8 = 6139;
	public static final int RESERVEDSLOT9 = 6140;
	public static final int RESERVEDSLOT10 = 6141;
	public static final int RESERVEDSLOT11 = 6142;
	public static final int RESERVEDSLOT12 = 6143;
	
	public static int gm;
	
	public static final int MODE_MENU       = 1;
	public static final int MODE_DEMO       = 2;
	public static final int MODE_GAME       = 4;
	public static final int MODE_EOL        = 8;
	public static final int MODE_RESTART    = 32;
	public static final int MODE_SENDTOWHOM = 64;
	public static final int MODE_END        = 128;
	public static final int MODE_LOADING    = 256;
	public static final int MODE_LOGO    	= 512;
	public static final int MODE_LOGO2    	= 1024;
	public static final int MODE_LOGO3    	= 2048;
	public static final int MODE_CUTSCENE   = 4096;
	public static final int MODE_WAIT  		= 8192;
	
	public static boolean MODE_TYPE;

	public static final int GET_STEROIDS     = 0;
	public static final int GET_SHIELD       = 1;
	public static final int GET_SCUBA        = 2;
	public static final int GET_HOLODUKE     = 3;
	public static final int GET_JETPACK      = 4;
	public static final int GET_ACCESS       = 6;
	public static final int GET_HEATS        = 7;
	public static final int GET_FIRSTAID     = 9;
	public static final int GET_BOOTS       = 10;
	
	
	// Defines weapon, not to be used with the 'shoot' keyword.;
	public static final int KNEE_WEAPON         = 0;
	public static final int PISTOL_WEAPON       = 1;
	public static final int SHOTGUN_WEAPON      = 2;
	public static final int RIFLEGUN_WEAPON     = 3;
	public static final int DYNAMITE_WEAPON     = 4;
	public static final int CROSSBOW_WEAPON     = 5;
	public static final int THROWSAW_WEAPON     = 6;
	public static final int ALIENBLASTER_WEAPON = 7;
	public static final int POWDERKEG_WEAPON    = 8;
	public static final int TIT_WEAPON       	= 9;
	public static final int HANDREMOTE_WEAPON   = 10;
	public static final int BUZSAW_WEAPON       = 11;
	public static final int BOWLING_WEAPON      = 12;
	
	public static final int MOTO_WEAPON      = 14;
	public static final int BOAT_WEAPON      = 15;
	public static final int CHICKENBOW_WEAPON  = 16;
	
	public static boolean kGameCrash;
	public static int musicvolume, musiclevel;
	
	public static short gEndGame;
	public static short gEndFirstEpisode;
	public static int LeonardCrack;
	
	public static PlayerOrig po[] = new PlayerOrig[MAXPLAYERS];
	public static PlayerStruct ps[] = new PlayerStruct[MAXPLAYERS];
	public static Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
	public static UserDefs ud = new UserDefs();
	
	// Hit definitions
	public static final int kHitTypeMask	= 0xE000;
	public static final int kHitIndexMask	= 0x1FFF;
	public static final int kHitSector		= 0x4000;
	public static final int kHitWall		= 0x8000;
	public static final int kHitSprite		= 0xC000;
	
	public static short global_random;
	public static short neartagsector, neartagwall, neartagsprite;

	public static int numframes, gc=176,neartaghitdist,lockclock,max_player_health,max_armour_amount, max_ammo_amount[] = new int[MAX_WEAPONSRA];

	public static short spriteq[] = new short[1024],spriteqloc,spriteqamount=64,moustat;
	public static Animwalltype animwall[] = new Animwalltype[MAXANIMWALLS];
	public static short numanimwalls;
	public static int[] msx = new int[2048],msy = new int[2048];
	public static short cyclers[][] = new short[MAXCYCLERS][6], numcyclers;

	public static char[][] fta_quotes = new char[NUMOFFIRSTTIMEACTIVE][64];

	public static char[] buf = new char[80];

	public static short camsprite;
	public static short mirrorwall[] = new short[64], mirrorsector[] = new short[64], mirrorcnt;

	public static int current_menu;

	public static char[] betaname = new char[80];

	public static final int nMaxMaps = 11;
	public static final int nMaxEpisodes = 4;
	public static final int nMaxSkills = 5;
	
	public static char[][] level_names = new char[nMaxMaps * nMaxEpisodes][33],level_file_names = new char[nMaxMaps * nMaxEpisodes][128];
	public static int[] partime = new int[nMaxMaps * nMaxEpisodes],designertime = new int[nMaxMaps * nMaxEpisodes];
	public static char[][] volume_names = new char[nMaxMaps][33];
	public static char[][] skill_names = new char[nMaxSkills][33];

	public static int checksume;
	public static int[] soundsiz = new int[NUM_SOUNDS];

	public static short[] soundps = new short[NUM_SOUNDS],soundpe = new short[NUM_SOUNDS],soundvo = new short[NUM_SOUNDS];
	public static short[] soundm = new short[NUM_SOUNDS],soundpr = new short[NUM_SOUNDS];
	public static String[] sounds = new String[NUM_SOUNDS];  //len 14

	public static short title_zoom;

	public static Sample[] Sound = new Sample[ NUM_SOUNDS ];
	public static SoundOwner[][] SoundOwner = new SoundOwner[NUM_SOUNDS][4];

	public static short numplayersprites,loadfromgrouponly,earthquaketime;

	public static int fricxv,fricyv;

	public static byte syncstat, syncval[][] = new byte[MAXPLAYERS][MOVEFIFOSIZ];
	public static int syncvalhead[] = new int[MAXPLAYERS], syncvaltail, syncvaltottail;

	public static Input sync[] = new Input[MAXPLAYERS], loc;
	public static Input recsync[][] = new Input[RECSYNCBUFSIZ][MAXPLAYERS];
	public static int avgfvel, avgsvel, avgbits;
	public static float avghorz, avgavel;
	

	public static Input[][] inputfifo = new Input[MOVEFIFOSIZ][MAXPLAYERS];
	
	public static int movefifosendplc;

	  //Multiplayer syncing variables
	public static short screenpeek;
	public static int movefifoend[] = new int[MAXPLAYERS];


	 //Game recording variables

	public static int playerreadyflag[] = new int[MAXPLAYERS], playerquitflag[] = new int[MAXPLAYERS];
	public static boolean ready2send;
	public static short vel, svel;
	public static float angvel;
	public static float horiz;
	
	public static int ototalclock, respawnactortime=768, respawnitemtime=768, groupfile;

	public static char display_mirror,typebuflen;
	public static byte[] tempbuf = new byte[2048];
	public static String music_fn[][] = new String[5][11];
	public static String env_music_fn[] = new String[5];
	
	public static final short weaponsandammosprites[] = {
		CROSSBOWSPRITE,
		RIFLESPRITE,
		ALIENBLASTERAMMO,
		44,
		44,
		COWPIE,
		54,
		WHISKEY,
		MOONSHINE,
		44,
		44,
		CROSSBOWSPRITE,
		44,
		TEATGUN,
		37,
	};

	public static int impact_damage;

	        //GLOBAL.C - replace the end "my's" with this
	public static int myx, omyx, myxvel, myy, omyy, myyvel, myz, omyz, myzvel;
	public static short myhorizoff, omyhorizoff;
	public static short mycursectnum, myjumpingcounter,frags[][] = new short[MAXPLAYERS][MAXPLAYERS];
	public static float myang, omyang,myhoriz, omyhoriz;
	
	public static char myjumpingtoggle, myhardlanding, myreturntocenter;
	public static boolean myonground;
	public static byte multiwho, multipos, multiwhat, multiflag;

	public static int fakemovefifoplc,movefifoplc;
	public static int[] myxbak = new int[MOVEFIFOSIZ], myybak = new int[MOVEFIFOSIZ], myzbak = new int[MOVEFIFOSIZ];
	public static int dukefriction = 0xcc00;
	public static float myhorizbak[] = new float[MOVEFIFOSIZ];
	public static float[] myangbak = new float[MOVEFIFOSIZ];

	public static char camerashitable,freezerhurtowner=0,dildoblase;
	// CTW - MODIFICATION
	// char networkmode = 255, movesperpacket = 1,gamequit = 0,playonten = 0,everyothertime;
	public static char networkmode = 255, movesperpacket = 1,gamequit = 0,everyothertime;
	// CTW END - MODIFICATION
	public static int numfreezebounces=3,crossbowblastradius,tntblastradius,bouncemineblastradius,shrinkerblastradius,morterblastradius,powderblastradius,seenineblastradius;

	public static int myminlag[] = new int[MAXPLAYERS], mymaxlag, otherminlag, bufferjitter = 1;
	public static int totalmemory = 0;
	public static int startofdynamicinterpolations = 0;
	
	public static final int kAngleMask = 0x7FF;
	
	public static final int kAngle5 = 28;
	public static final int kAngle15 = 85;
	public static final int kAngle30 = 170;
	public static final int kAngle45 = 256;
	public static final int kAngle60 = 341;
	public static final int kAngle90 = 512;
	public static final int kAngle120 = 682;
	public static final int kAngle180 = 1024;
	public static final int kAngle360 = 2048;
	
	public static String exceptionHandler(Exception e)
	{
		if (e instanceof ArithmeticException) 
			return "ArithmeticException";
		if (e instanceof ArrayIndexOutOfBoundsException) 
			return "ArrayIndexOutOfBoundsException";
		if (e instanceof ArrayStoreException )
			return "ArrayStoreException";		
		if (e instanceof ClassCastException )
			return "ClassCastException";		
		if (e instanceof IllegalMonitorStateException )
			return "IllegalMonitorStateException";		
		if (e instanceof IllegalStateException )
			return "IllegalStateException";		
		if (e instanceof IllegalThreadStateException )
			return "IllegalThreadStateException";		
		if (e instanceof IndexOutOfBoundsException )
			return "IndexOutOfBoundsException";	
		if (e instanceof NegativeArraySizeException )
			return "NegativeArraySizeException";		
		if (e instanceof NullPointerException )
			return "NullPointerException";		
		if (e instanceof NumberFormatException )
			return "NumberFormatException";		
		if (e instanceof SecurityException )
			return "SecurityException";

		return "Application exception";
	}
	
	public static String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	    	sb.append("\t" + element.toString());
	        sb.append("\r\n");
	    }
	    return sb.toString();
	}

	public static void dassert(String msg) {
		if(kGameCrash)
			return;
		
		String message = msg;

		StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
	    	sb.append("\t" + element.toString());
	        sb.append("\r\n");
	    }
	    message += "\r\nFull stack trace: ";
	    message += sb.toString();

		Console.LogPrint("Dassert: " + message);
		System.err.println("Dassert: " + message);
		CloseLogFile();
		
		try {
			if(engine.showMessage("Dassert", message, true))
				saveToFTP();
			throw new RuntimeException();
		} catch (Exception e) {}
		finally {
			appdispose();
			System.exit(0);
		}
	}
	
	public static boolean GameMessage(String text, boolean choise)
	{
		boolean out = engine.showMessage("Warning: ", text, choise);
		if(Gdx.graphics != null)
			cfg.fullscreen = 0;
		Console.Println("Warning: "+ text, OSDTEXT_YELLOW);
		
		return out;
	}
	
	public static void saveConfig()
	{
		cfg.anisotropy = glanisotropy;
		cfg.widescreen = r_usenewaspect;
		cfg.saveConfig(FilePath);
	}
}
