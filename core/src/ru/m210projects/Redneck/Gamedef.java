package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;

import java.util.Arrays;

import com.badlogic.gdx.utils.IntArray;

import static ru.m210projects.Build.FileHandle.Cache1D.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Redneck.Globals.MODE_RESTART;
import static ru.m210projects.Redneck.Globals.gm;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.Menus.RRMenu.LOADGAME;
import static ru.m210projects.Redneck.Menus.MENU.mMenus;
import static ru.m210projects.Redneck.Menus.MENU.mOpen;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.*;
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;

public class Gamedef {
	
	public static final int[] params = new int[35];
	public static String confilename = "GAME.CON";
	public static int conweigth = 10;
	
	// Defines the motion characteristics of an actor;
	public static final int face_player = 1;
	public static final int geth = 2;
	public static final int getv = 4;
	public static final int random_angle = 8;
	public static final int face_player_slow = 16;
	public static final int spin = 32;
	public static final int face_player_smart = 64;
	public static final int fleeenemy = 128;
	public static final int jumptoplayer = 257;
	public static final int seekplayer = 512;
	public static final int furthestdir = 1024;
	public static final int dodgebullet = 4096;
	
	// Some misc public static final ints;
	public static final int NO       = 0;
	public static final int YES      = 1;

	// Defines for 'useractor' keyword;
	public static final int notenemy       = 0;
	public static final int enemy          = 1;
	public static final int enemystayput   = 2;

	// Player Actions.;
	public static final int pstanding = 1;
	public static final int pwalking = 2;
	public static final int prunning = 4;
	public static final int pducking = 8;
	public static final int pfalling = 16;
	public static final int pjumping = 32;
	public static final int phigher = 64;
	public static final int pwalkingback = 128;
	public static final int prunningback = 256;
	public static final int pkicking = 512;
	public static final int pshrunk = 1024;
	public static final int pjetpack = 2048;
	public static final int ponsteroids = 4096;
	public static final int ponground = 8192;
	public static final int palive = 16384;
	public static final int pdead = 32768;
	public static final int pfacing = 65536;
		
	public static final int MAXSCRIPTSIZE = 20460;
	public static final int NUMKEYWORDS = 147;
	private static char[] tempbuf = new char[2048];
	
	private static char[] text;
	private static int textptr = 0;
	
	private static char[] origtext;
	private static int origtptr;
	
	private static int parsing_actor;
	public static int script[] = new int[MAXSCRIPTSIZE], labelcnt;
	public static int insptr;
	public static IntArray labelcode = new IntArray();
	public static int scriptptr, error, warning, killit_flag;
	public static char[] label;
	
	public static int actorscrptr[] = new int[MAXTILES];
	public static short actortype[] = new short[MAXTILES];
	
	public static final String defaultcons[] =
	{
		"GAME.CON",
	    "USER.CON",
	    "DEFS.CON"
	};
	
	public static final char[][] keyw = {
		"definelevelname".toCharArray(),  	// 0
	    "actor".toCharArray(),            	// 1    [#]  
	    "addammo".toCharArray(),   			// 2    [#]
	    "ifrnd".toCharArray(),            	// 3    [C]
	    "enda".toCharArray(),            	// 4    [:]  
	    "ifcansee".toCharArray(),         	// 5    [C]  
	    "ifhitweapon".toCharArray(),      	// 6    [#]
	    "action".toCharArray(),           	// 7    [#]
	    "ifpdistl".toCharArray(),         	// 8    [#]  
	    "ifpdistg".toCharArray(),         	// 9    [#]
	    "else".toCharArray(),             	// 10   [#]	 
	    "strength".toCharArray(),         	// 11   [#]
	    "break".toCharArray(),            	// 12   [#]
	    "shoot".toCharArray(),            	// 13   [#]
	    "palfrom".toCharArray(),          	// 14   [#]
	    "sound".toCharArray(),            	// 15   [filename.voc]
	    "fall".toCharArray(),             	// 16   []  
	    "state".toCharArray(),            	// 17	
	    "ends".toCharArray(),             	// 18		
	    "define".toCharArray(),           	// 19
	    "//".toCharArray(),               	// 20
	    "ifai".toCharArray(),             	// 21		
	    "killit".toCharArray(),           	// 22		
	    "addweapon".toCharArray(),        	// 23
	    "ai".toCharArray(),               	// 24
	    "addphealth".toCharArray(),       	// 25
	    "ifdead".toCharArray(),           	// 26
	    "ifsquished".toCharArray(),       	// 27
	    "sizeto".toCharArray(),           	// 28
	    "{".toCharArray(),                	// 29
	    "}".toCharArray(),                	// 30
	    "spawn".toCharArray(),            	// 31
	    "move".toCharArray(),            	// 32
	    "ifwasweapon".toCharArray(),      	// 33
	    "ifaction".toCharArray(),         	// 34
	    "ifactioncount".toCharArray(),    	// 35
	    "resetactioncount".toCharArray(), 	// 36
	    "debris".toCharArray(),           	// 37
	    "pstomp".toCharArray(),          	// 38
	    "/*".toCharArray(),               	// 39
	    "cstat".toCharArray(),            	// 40
	    "ifmove".toCharArray(),           	// 41
	    "resetplayer".toCharArray(),      	// 42
	    "ifonwater".toCharArray(),        	// 43
	    "ifinwater".toCharArray(),        	// 44
	    "ifcanshoottarget".toCharArray(), 	// 45
	    "ifcount".toCharArray(),          	// 46
	    "resetcount".toCharArray(),       	// 47
	    "addinventory".toCharArray(),     	// 48
	    "ifactornotstayput".toCharArray(),	// 49
	    "hitradius".toCharArray(),        	// 50
	    "ifp".toCharArray(),              	// 51
	    "count".toCharArray(),            	// 52
	    "ifactor".toCharArray(),          	// 53
	    "music".toCharArray(),            	// 54
	    "include".toCharArray(),          	// 55	
	    "ifstrength".toCharArray(),       	// 56
	    "definesound".toCharArray(),      	// 57
	    "guts".toCharArray(),             	// 58
	    "ifspawnedby".toCharArray(),      	// 59
	    "gamestartup".toCharArray(),      	// 60
	    "wackplayer".toCharArray(),       	// 61
	    "ifgapzl".toCharArray(),          	// 62
	    "ifhitspace".toCharArray(),       	// 63	
	    "ifoutside".toCharArray(),        	// 64
	    "ifmultiplayer".toCharArray(),    	// 65
	    "operate".toCharArray(),          	// 66
	    "ifinspace".toCharArray(),        	// 67
	    "debug".toCharArray(),            	// 68
	    "endofgame".toCharArray(),        	// 69
	    "ifbulletnear".toCharArray(),     	// 70
	    "ifrespawn".toCharArray(),        	// 71
	    "iffloordistl".toCharArray(),     	// 72	
	    "ifceilingdistl".toCharArray(),   	// 73
	    "spritepal".toCharArray(),        	// 74
	    "ifpinventory".toCharArray(),     	// 75
	    "betaname".toCharArray(),         	// 76
	    "cactor".toCharArray(),           	// 77	
	    "ifphealthl".toCharArray(),       	// 78
	    "definequote".toCharArray(),      	// 79
	    "quote".toCharArray(),            	// 80
	    "ifinouterspace".toCharArray(),   	// 81
	    "ifnotmoving".toCharArray(),      	// 82
	    "respawnhitag".toCharArray(),     	// 83
	    "tip".toCharArray(),              	// 84
	    "ifspritepal".toCharArray(),      	// 85
	    "feathers".toCharArray(),         	// 86
	    "soundonce".toCharArray(),        	// 87
	    "addkills".toCharArray(),         	// 88
	    "stopsound".toCharArray(),        	// 89
	    "ifawayfromwall".toCharArray(),   	// 90
	    "ifcanseetarget".toCharArray(),   	// 91
	    "globalsound".toCharArray(),  		// 92
	    "lotsofglass".toCharArray(), 		// 93
	    "ifgotweaponce".toCharArray(), 		// 94
	    "getlastpal".toCharArray(), 		// 95
	    "pkick".toCharArray(),  			// 96
	    "mikesnd".toCharArray(), 			// 97
	    "useractor".toCharArray(),  		// 98
	    "sizeat".toCharArray(),  			// 99		
	    "addstrength".toCharArray(), 		// 100   [#]
	    "cstator".toCharArray(), 			// 101
	    "mail".toCharArray(), 				// 102
	    "paper".toCharArray(), 				// 103
	    "tossweapon".toCharArray(), 		// 104
	    "sleeptime".toCharArray(), 			// 105
	    "nullop".toCharArray(), 			// 106
	    "definevolumename".toCharArray(), 	// 107
	    "defineskillname".toCharArray(), 	// 108
	    "ifnosounds".toCharArray(), 		// 109
	    
	    //REDNECK RAMPAGE CMDS
	    "ifnocover".toCharArray(),			// 110 --no
	    "ifhittruck".toCharArray(),			// 111 COOT
	    "iftipcow".toCharArray(),			// 112 COW
	    "isdrunk".toCharArray(),			// 113 --no
	    "iseat".toCharArray(),				// 114 GAME
	    "destroyit".toCharArray(),			// 115 GAME
	    "larrybird".toCharArray(),			// 116 TORNADO
	    "strafeleft".toCharArray(),			// 117 Sheriff
	    "straferight".toCharArray(),		// 118 Sheriff
	    "ifactorhealthg".toCharArray(),		// 119 Sheriff
	    "ifactorhealthl".toCharArray(),   	// 120 Sheriff
	    "slapplayer".toCharArray(),			// 121 Vixen
	    "ifpdrunk".toCharArray(),			// 122 --no
	    "tearitup".toCharArray(),			// 123 TORNADO
	    "smackbubba".toCharArray(),			// 124 BUBBA
	    "soundtagonce".toCharArray(),		// 125 Crickets
	    "soundtag".toCharArray(),			// 126 --no
	    "ifsoundid".toCharArray(),			// 127 Crickets
	    "ifsounddist".toCharArray(),		// 128 Crickets
	    "ifonmud".toCharArray(),			// 129 GAME
	    "ifcoop".toCharArray(),				// 130 BUBBA
	    
	    //RR RIDES AGAIN CMDS
	    "ifmotofast".toCharArray(),			//131 GAME
	    "ifwind".toCharArray(),				//132 --no
	    "smacksprite".toCharArray(),		//133 RABBIT
	    "ifonmoto".toCharArray(),			//134 GAME
	    "ifonboat".toCharArray(),			//135 GAME
	    "fakebubba".toCharArray(),			//136 BUBBA
	    "mamatrigger".toCharArray(),		//137 MAMA
	    "mamaspawn".toCharArray(),			//138 MAMA
	    "mamaquake".toCharArray(),			//139 MAMA
	    "clipdist".toCharArray(),			//140 --no
	    "mamaend".toCharArray(),			//141 MAMA
	    "newpic".toCharArray(),				//142 GAME
	    "garybanjo".toCharArray(),			//143 COOTPLAY
	    "motoloopsnd".toCharArray(),		//144 BIKERB
	    "ifsizedown".toCharArray(),			//145 MAMAC
	    "rndmove".toCharArray(),			//146 MAMAC
	};

	public static short total_lines,line_number;
	public static short checking_ifelse, parsing_state;
	public static String last_used_text;
	public static short num_squigilly_brackets;
	public static int last_used_size;

	public static short g_i,g_p;
	public static int g_x;
	public static SPRITE g_sp;
	public static int[] g_t;

	public static float getincangle(float a, float na)
	{
	    a = BClampAngle(a);
	    na = BClampAngle(na);

	    if(Math.abs(a-na) < 1024)
	        return (na-a);
	    else
	    {
	        if(na > 1024) na -= 2048;
	        if(a > 1024) a -= 2048;

	        na -= 2048;
	        a -= 2048;
	        return (na-a);
	    }
	}
	
	public static int getincangle(int a, int na)
	{
	    a &= 2047;
	    na &= 2047;

	    if(klabs(a-na) < 1024)
	        return (na-a);
	    else
	    {
	        if(na > 1024) na -= 2048;
	        if(a > 1024) a -= 2048;

	        na -= 2048;
	        a -= 2048;
	        return (na-a);
	    }
	}
	
	public static boolean ispecial(char c)
	{
	    if(c == 0x0a)
	    {
	        line_number++;
	        return true;
	    }

	    if(c == ' ' || c == 0x0d)
	        return true;

	    return false;
	}
	
	public static boolean isaltok(char c)
	{
	    return ( isalnum(c) || c == '{' || c == '}' || c == '/' || c == '*' || c == '-' || c == '_' || c == '.' );
	}
	
	public static boolean isalnum(char c)
	{
		return Character.isLetterOrDigit(c);
	}

	public static void getglobalz(int i)
	{
	    int lz,zr;

	    SPRITE s = sprite[i];

	    if( s.statnum == 10 || s.statnum == 6 || s.statnum == 2 || s.statnum == 1 || s.statnum == 4)
	    {
	        if(s.statnum == 4)
	            zr = 4;
	        else zr = 127;

	        engine.getzrange(s.x,s.y,s.z-(FOURSLEIGHT),s.sectnum,zr,CLIPMASK0);
	        hittype[i].ceilingz = zr_ceilz;
	        hittype[i].floorz = zr_florz;
	        lz = zr_florhit;

	        if( (lz&kHitTypeMask) == kHitSprite && (sprite[lz&kHitIndexMask].cstat&48) == 0 )
	        {
	            lz &= kHitIndexMask;
	            if( badguy(sprite[lz]) && sprite[lz].pal != 1)
	            {
	                if( s.statnum != 4 )
	                {
	                    hittype[i].dispicnum = -4; // No shadows on actors
	                    s.xvel = -256;
	                    ssp(i,CLIPMASK0);
	                }
	            }
	            else if(sprite[lz].picnum == APLAYER && badguy(s) )
	            {
	                hittype[i].dispicnum = -4; // No shadows on actors
	                s.xvel = -256;
	                ssp(i,CLIPMASK0);
	            }
	            else if(s.statnum == 4 && sprite[lz].picnum == APLAYER)
	                if(s.owner == lz)
	            {
	                hittype[i].ceilingz = sector[s.sectnum].ceilingz;
	                hittype[i].floorz   = sector[s.sectnum].floorz;
	            }
	        }
	    }
	    else
	    {
	        hittype[i].ceilingz = sector[s.sectnum].ceilingz;
	        hittype[i].floorz   = sector[s.sectnum].floorz;
	    }
	}

	public static void makeitfall(int i)
	{
	    SPRITE s = sprite[i];
	    int c;
	
	    if( floorspace(s.sectnum) )
	        c = 0;
	    else
	    {
	        if( ceilingspace(s.sectnum) || sector[s.sectnum].lotag == 2)
	            c = gc / 6;
	        else c = gc;
	    }
	
	    if( ( s.statnum == 1 || s.statnum == 10 || s.statnum == 2 || s.statnum == 6 ) ) {
	        engine.getzrange(s.x,s.y,s.z-(FOURSLEIGHT),s.sectnum,127,CLIPMASK0);
	        hittype[i].ceilingz = zr_ceilz;
	        hittype[i].floorz = zr_florz;
	    }
	    else
	    {
	        hittype[i].ceilingz = sector[s.sectnum].ceilingz;
	        hittype[i].floorz   = sector[s.sectnum].floorz;
	    }
	
	    if( s.z < hittype[i].floorz-(FOURSLEIGHT) )
	    {
	        if( sector[s.sectnum].lotag == 2 && s.zvel > 3122 )
	            s.zvel = 3144;
	        if(s.zvel < 6144)
	            s.zvel += c;
	        else s.zvel = 6144;
	        s.z += s.zvel;
	    }
	    if( s.z >= hittype[i].floorz-(FOURSLEIGHT) )
	    {
	        s.z = hittype[i].floorz - FOURSLEIGHT;
	        s.zvel = 0;
	    }
	}

	public static void getlabel()
	{
	    while( !isalnum(text[textptr]) )
	    {
	        if(text[textptr] == 0x0a) line_number++;
	        textptr++;
	        if( text[textptr] == 0)
	            return;
	    }

	    int len = 0;
	    while( !ispecial(text[textptr + ++len]) );
	    
	    if(label.length <= (labelcnt<<6) + len) // resize
	    {
	    	char[] newItems = new char[(labelcnt<<6) + len + 1];
			System.arraycopy(label, 0, newItems, 0, label.length);
			label = newItems;
	    }

	    System.arraycopy(text, textptr, label, (labelcnt<<6), len);
	    label[(labelcnt<<6)+len] = 0;
	    textptr += len;
	}
	
	public static int keyword()
	{
		int temptextptr = textptr;
		 
	    while( !isaltok(text[temptextptr]) )
	    {
	    	temptextptr++;
	    	if( text[temptextptr] == 0)
	            return 0;
	    }

	    int i = 0;
	    while( isaltok(text[temptextptr]) )
	    {
	        tempbuf[i] = text[temptextptr++];
	        i++;
	    }
	    tempbuf[i] = 0;

	    for(i=0;i<NUMKEYWORDS;i++)
	        if( Bstrcmp( tempbuf, keyw[i]) == 0 )
	            return i;

	    return -1;
	}

	public static int transword() //Returns its code #
	{
		while( !isaltok(text[textptr]) )
	    {
			if(text[textptr] == 0x0a) line_number++;
	        if( text[textptr] == 0 )
	            return -1;
	        textptr++;
	    }

	    int l = 0;
	    while( isaltok(text[textptr + l]) )
	    {
	        tempbuf[l] = text[textptr + l];
	        l++;
	    }

	    tempbuf[l] = 0;

	    for(int i=0;i<NUMKEYWORDS;i++)
	    {
	        if( Bstrcmp( tempbuf,keyw[i]) == 0 )
	        {
	            script[scriptptr] = i; //set to struct script
	            textptr += l;
	            scriptptr++;
	            return i;
	        }
	    }

	    textptr += l;
	    if( tempbuf[0] == '{' && tempbuf[1] != 0)
	        Console.Println("  * ERROR!(L" + line_number + ") Expecting a SPACE or CR between '{' and '" + tempbuf[1] + "'.");
	    else if( tempbuf[0] == '}' && tempbuf[1] != 0)
	        Console.Println("  * ERROR!(L" + line_number + ") Expecting a SPACE or CR between '}' and '" + tempbuf[1] + "'.");
	    else if( tempbuf[0] == '/' && tempbuf[1] == '/' && tempbuf[2] != 0 )
	        Console.Println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '//' and '" + tempbuf[2] + "'.");
	    else if( tempbuf[0] == '/' && tempbuf[1] == '*' && tempbuf[2] != 0 )
	        Console.Println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '/*' and '" + tempbuf[2] + "'.");
	    else if( tempbuf[0] == '*' && tempbuf[1] == '/' && tempbuf[2] != 0 )
	        Console.Println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '*/' and '" + tempbuf[2] + "'.");
	    else Console.Println("  * ERROR!(L" + line_number + ") Expecting key word, but found '" + tempbuf[0] + "'.");

	    error++;
	    return -1;
	}

	public static void transnum()
	{
	    while( !isaltok(text[textptr]) )
	    {
	    	if(text[textptr] == 0x0a) line_number++;
	    	textptr++;
	        if( text[textptr] == 0 )
	            return;
	    }

	    int l = 0;
	    while( isaltok(text[textptr + l]) )
	    {
	        tempbuf[l] = text[textptr + l];
	        l++;
	    }
	    
	    tempbuf[l] = 0;
	    for(int i=0;i<NUMKEYWORDS;i++)
	        if( Bstrcmp( label, (labelcnt<<6), keyw[i], 0) == 0 )
	    {
	        error++;
	        Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[(labelcnt<<6)] + "' is a key word.");
	        textptr+=l;
	    }

	    for(int i=0;i<labelcnt;i++)
	    {
	        if( Bstrcmp(tempbuf, 0, label, i<<6) == 0 )
	        {
	        	script[scriptptr] = labelcode.get(i);
	        	scriptptr++;
	        	textptr += l;
	            return;
	        }
	    }

	    if( !Character.isDigit(text[textptr]) && text[textptr] != '-')
	    {
	    	Console.Println("  * ERROR!(L" + line_number + ") Parameter '" + tempbuf[0] + "' is undefined.");
	        error++;
	        textptr+=l;
	        return;
	    }

	    script[scriptptr] = Integer.parseInt(new String(text, textptr, l));
	    scriptptr++;

	    textptr += l;
	}

	public static boolean parsecommand()
	{
		int i, j, k;
	    char temp_ifelse_check;
	    short temp_line_number;
	    int tempscrptr;
	    int fp;

	    if( error > 12 || ( text[textptr] == '\0' ) || ( text[textptr+1] == '\0' ) ) return true;

	    int tw = transword();

	    switch(tw)
	    {
	        default:
	        case -1:
	            return false; //End
	        case 39:      //Rem endrem
	            scriptptr--;
	            j = line_number;
	            do
	            {
	                textptr++;
	                if(text[textptr] == 0x0a) line_number++;
	                if( text[textptr] == 0 )
	                {
	                    Console.Println("  * ERROR!(L" + j + ") Found '/*' with no '*/'.");
	                	error++;
	                    return false;
	                }
	            }
	            while( text[textptr] != '*' || text[textptr + 1] != '/' );
	            textptr+=2;
	            return false;
	            
	        case 17:
	        	if( parsing_actor == 0 && parsing_state == 0 )
	            {
	                getlabel();
	                scriptptr--;
	                labelcode.add(scriptptr);
	                labelcnt = labelcode.size;

	                parsing_state = 1;

	                return false;
	            }

	            getlabel();

	            for(i=0;i<NUMKEYWORDS;i++)
	            	if( Bstrcmp( label, labelcnt<<6,keyw[i], 0) == 0 )
	                {
	                    error++;
	                    Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt<<6] + "' is a key word.");
	                    return false;
	                }

	            for(j=0;j<labelcnt;j++)
	            {
	                if( Bstrcmp(label, (j<<6),label, (labelcnt<<6)) == 0 )
	                {
	                    script[scriptptr] = labelcode.get(j);
	                    break;
	                }
	            }

	            if(j==labelcnt)
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") State '" + label[labelcnt<<6] + "' not found.");
	                error++;
	            }
	            scriptptr++;
	        	return false;
	        case 15:
	        case 92:
	        case 87:
	        case 89:
	        case 93:
	            transnum();
	            return false;
	        case 18:
	        	if( parsing_state == 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'ends' with no 'state'.");
	                error++;
	            }
	            
                if( num_squigilly_brackets > 0 )
                {
	                Console.Println("  * ERROR!(L" + line_number + ") Found more '{' than '}' before 'ends'.");
                    error++;
                }
                if( num_squigilly_brackets < 0 )
                {
	                Console.Println("  * ERROR!(L" + line_number + ") Found more '}' than '{' before 'ends'.");
                    error++;
                }
                parsing_state = 0;
	            
	        	return false;
	        case 19:
	        	getlabel();
	            // Check to see it's already defined
	            for(i=0;i<NUMKEYWORDS;i++)
	                if( Bstrcmp( label, labelcnt<<6,keyw[i], 0) == 0 )
	                {
	                    error++;
	                    Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt<<6] + "' is a key word.");
	                    return false;
	                }

	            for(i=0;i<labelcnt;i++)
	            {
	                if( Bstrcmp(label, labelcnt<<6,label, i<<6) == 0 )
	                {
	                    warning++;
	                    Console.Println("  * WARNING.(L" + line_number + ") Duplicate definition '" + label[labelcnt<<6] + "' ignored.");
	                    break;
	                }
	            }
	            transnum();
	            if(i == labelcnt) {
	            	labelcode.add(script[scriptptr-1]);
	            	labelcnt = labelcode.size; 
	            }
//	                labelcode[labelcnt++] = script[scriptptr-1];
	            scriptptr -= 2;
	        	return false;
	        case 14:
	        	for(j = 0;j < 4;j++)
	            {
	                if( keyword() == -1 )
	                    transnum();
	                else break;
	            }

	            while(j < 4)
	            {
	                script[scriptptr] = 0;
	                scriptptr++;
	                j++;
	            }
	        	return false;
	        case 32:
	        	if( parsing_actor != 0 || parsing_state != 0 )
	            {
	                transnum();

	                j = 0;
	                while(keyword() == -1)
	                {
	                    transnum();
	                    scriptptr--;
	                    j |= script[scriptptr];
	                }
	                script[scriptptr] = j;
	                scriptptr++;
	            }
	            else
	            {
	                scriptptr--;
	                getlabel();
	                // Check to see it's already defined

	                for(i=0;i<NUMKEYWORDS;i++)
	                    if( Bstrcmp( label, (labelcnt<<6),keyw[i], 0) == 0 )
	                {
	                    error++;
	                    Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt<<6] + "' is a key word.");
	                    return false;
	                }

	                for(i=0;i<labelcnt;i++)
	                    if( Bstrcmp(label, (labelcnt<<6),label, (i<<6)) == 0 )
	                    {
	                        warning++;
	                        Console.Println("  * WARNING.(L" + line_number + ") Duplicate move '" + label[labelcnt<<6] + "' ignored.");
	                        break;
	                    }
	                if(i == labelcnt) {
	                	labelcode.add(scriptptr);
	                	labelcnt = labelcode.size;
	                }
//	                    labelcode[labelcnt++] = scriptptr;
	                for(j=0;j<2;j++)
	                {
	                    if(keyword() >= 0) break;
	                    transnum();
	                }
	                for(k=j;k<2;k++)
	                {
	                    script[scriptptr] = 0;
	                    scriptptr++;
	                }
	            }
	        	return false;
	        case 54:
		        {
	                scriptptr--;
	                transnum(); // Volume Number (0/4)
	                scriptptr--;
	
	                k = script[scriptptr] - 1;
	                if(k >= 0) // if it's background music
	                {
	                    i = 0;
	                    while(keyword() == -1)
	                    {
	                        while( !isaltok(text[textptr]) )
	                        {
	                            if(text[textptr] == 0x0a) line_number++;
	                            textptr++;
	                            if( text[textptr] == 0 ) break;
	                        }
	                        int startptr = textptr;
	                        j = 0;
	                        while( isaltok(text[textptr+j]) )
	                            j++;
	                        
	                        music_fn[k][i] = new String(text, startptr, j);
	                        textptr += j;
	                        if(i > 9) break;
	                        i++;
	                    }
	                }
	                else
	                {
	                    i = 0;
	                    while(keyword() == -1)
	                    {
	                        while( !isaltok(text[textptr]) )
	                        {
	                            if(text[textptr] == 0x0a) line_number++;
	                            textptr++;
	                            if( text[textptr] == 0 ) break;
	                        }
	                        int startptr = textptr;
	                        j = 0;
	                        while( isaltok(text[textptr+j]) )
	                        {
	                            j++;
	                        }
	                        env_music_fn[i] = new String(text, startptr, j);
	
	                        textptr += j;
	                        if(i > 9) break;
	                        i++;
	                    }
	                }
	            }
	        	return false;
	        case 55:
	            scriptptr--;
	            while( !isaltok(text[textptr]) )
	            {
	                if(text[textptr] == 0x0a) line_number++;
	                textptr++;
	                if( text[textptr] == 0 ) break;
	            }
	            j = 0;
	            while( isaltok(text[textptr]) )
	            {
	                tempbuf[j] = text[textptr++];
	                j++;
	            }
	            tempbuf[j] = '\0';

	            String name = new String(tempbuf, 0, j).trim();
	            fp = kOpen(name, loadfromgrouponly);
	            if(fp == -1)
	            {
	                error++;
	                Console.Println("  * ERROR!(L" + line_number + ") Could not find '" + label[labelcnt<<6] + "'.");
	                return false;
	            }

	            j = kFileLength(fp);
	            byte[] buf = new byte[j+1];

	            Console.Println("Including: '" + name + "'.");
	            if(name.equalsIgnoreCase("gator66.con") 
	            		|| name.equalsIgnoreCase("pig66.con") 
	            		|| name.equalsIgnoreCase("bubba66.con"))
	            	conweigth += 1;
	            
	            if(conweigth == 3 && GameCON != RRRA )
	            {
	            	 Console.Println("Looks like Redneck Rampage: Suckin' Grits on Route 66 Edition CON files.");
	            	 GameCON = RR66;
	            	 conweigth = 0;
	            }

	            temp_line_number = line_number;
	            line_number = 1;
	            temp_ifelse_check = (char) checking_ifelse;
	            checking_ifelse = 0;
	            
	            origtext = text;
	            origtptr = textptr;
	            textptr = 0;

	            kRead(fp,buf,j);
	            kClose(fp);
	            
	            last_used_text = new String(buf);
		        last_used_size = j;

		        text = last_used_text.toCharArray();
		        
		        text[j] = 0;

	            boolean done = false;
	            do
	                done = parsecommand();
	            while( !done );

	            text = origtext;
	            textptr = origtptr;
	            total_lines += line_number;
	            line_number = temp_line_number;
	            checking_ifelse = (short) temp_ifelse_check;

	            return false;
	        case 24:
	            if( parsing_actor != 0 || parsing_state != 0 )
	                transnum();
	            else
	            {
	                scriptptr--;
	                getlabel();

	                for(i=0;i<NUMKEYWORDS;i++)
	                    if( Bstrcmp( label,(labelcnt<<6),keyw[i], 0) == 0 )
	                    {
	                        error++;
	                        Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt<<6] + "' is a key word.");
	                        return false;
	                    }

	                for(i=0;i<labelcnt;i++)
	                	if( Bstrcmp(label,(labelcnt<<6),label,(i<<6)) == 0 )
	                    {
	                        warning++;
	                        Console.Println("  * WARNING.(L" + line_number + ") Duplicate ai '" + label[labelcnt<<6] + "' ignored.");
	                        break;
	                    }

	                if(i == labelcnt) {
	                	labelcode.add(scriptptr);
	                	labelcnt = labelcode.size;
	                }
//	                    labelcode[labelcnt++] = scriptptr;

	                for(j=0;j<3;j++)
	                {
	                    if(keyword() >= 0) break;
	                    if(j == 2)
	                    {
	                        k = 0;
	                        while(keyword() == -1)
	                        {
	                            transnum();
	                            scriptptr--;
	                            k |= script[scriptptr];
	                        }
	                        script[scriptptr] = k;
	                        scriptptr++;
	                        return false;
	                    }
	                    else transnum();
	                }
	                for(k=j;k<3;k++)
	                {
	                    script[scriptptr] = 0;
	                    scriptptr++;
	                }
	            }
	            return false;
	        case 7:
	            if( parsing_actor != 0 || parsing_state != 0 )
	                transnum();
	            else
	            {
	                scriptptr--;
	                getlabel();
	                // Check to see it's already defined

	                for(i=0;i<NUMKEYWORDS;i++)
	                    if( Bstrcmp( label,(labelcnt<<6),keyw[i], 0) == 0 )
	                    {
	                        error++;
	                        Console.Println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt<<6] + "' is a key word.");
	                        return false;
	                    }

	                for(i=0;i<labelcnt;i++)
	                    if( Bstrcmp(label,(labelcnt<<6),label,(i<<6)) == 0 )
	                    {
	                        warning++;
	                        Console.Println("  * WARNING.(L" + line_number + ") Duplicate action '" + label[labelcnt<<6] + "' ignored.");
	                        break;
	                    }

	                if(i == labelcnt) {
	                	labelcode.add(scriptptr);
	                	labelcnt = labelcode.size;
	                }
//	                    labelcode[labelcnt++] = scriptptr;

	                for(j=0;j<5;j++)
	                {
	                    if(keyword() >= 0) break;
	                    transnum();
	                }
	                for(k=j;k<5;k++)
	                {
	                    script[scriptptr] = 0;
	                    scriptptr++;
	                }
	            }
	            return false;

	        case 1:
	            if( parsing_state != 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'actor' within 'state'.");
	                error++;
	            }

	            if( parsing_actor != 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'actor' within 'actor'.");
	                error++;
	            }

	            num_squigilly_brackets = 0;
	            scriptptr--;
	            parsing_actor = scriptptr;

	            transnum();
	            scriptptr--;
	            actorscrptr[script[scriptptr]] = parsing_actor;

	            for(j=0;j<4;j++)
	            {
	            	script[parsing_actor+j] = 0;
	                if(j == 3)
	                {
	                    j = 0;
	                    while(keyword() == -1)
	                    {
	                        transnum();
	                        scriptptr--;
	                        j |= script[scriptptr];
	                    }
	                    script[scriptptr] = j;
	                    scriptptr++;
	                    break;
	                }
	                else
	                {
	                    if(keyword() >= 0)
	                    {
	                        scriptptr += (4-j);
	                        break;
	                    }
	                    transnum();

	                    script[parsing_actor+j] = script[scriptptr-1];
	                }
	            }

	            checking_ifelse = 0;

	            return false;

	        case 98:

	            if( parsing_state != 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'useritem' within 'state'.");
	                error++;
	            }

	            if( parsing_actor != 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'useritem' within 'actor'.");
	                error++;
	            }

	            num_squigilly_brackets = 0;
	            scriptptr--;
	            parsing_actor = scriptptr;

	            transnum();
	            scriptptr--;
	            j = script[scriptptr];
	            transnum();
	            scriptptr--;
	            actorscrptr[script[scriptptr]] = parsing_actor;
	            actortype[script[scriptptr]] = (short) j;

	            for(j=0;j<4;j++)
	            {
	            	script[parsing_actor+j] = 0;
	                if(j == 3)
	                {
	                    j = 0;
	                    while(keyword() == -1)
	                    {
	                        transnum();
	                        scriptptr--;
	                        j |= script[scriptptr];
	                    }
	                    script[scriptptr] = j;
	                    scriptptr++;
	                    break;
	                }
	                else
	                {
	                    if(keyword() >= 0)
	                    {
	                        scriptptr += (4-j);
	                        break;
	                    }
	                    transnum();

	                    script[parsing_actor+j] = script[scriptptr-1];
	                }
	            }

	            checking_ifelse = 0;
	            return false;
	            
	        case 11:
	        case 13:
	        case 25:
	        case 31:
	        case 40:
	        case 52:
	        case 69:
	        case 74:
	        case 77:
	        case 80:
	        case 86:
	        case 88:
	        case 68:
	        case 100:
	        case 101:
	        case 102:
	        case 103:
	        case 105:
	        case 113:
	        case 114:
	        	
	        case 140: //RA
	        case 142:
	            transnum();
	            return false;

	        case 2:
	        case 23:
	        case 28:
	        case 99:
	        case 37:
	        case 48:
	        case 58:
	            transnum();
	            transnum();
	            break;
	        case 50:
	            transnum();
	            transnum();
	            transnum();
	            transnum();
	            transnum();
	            break;
	        case 10:
	            if( checking_ifelse != 0 )
	            {
	                checking_ifelse--;
	                tempscrptr = scriptptr; 
	                scriptptr++; //Leave a spot for the fail location
	                parsecommand();

	                script[tempscrptr] = scriptptr;
	            }
	            else
	            {
	                scriptptr--;
	                error++;
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'else' with no 'if'.");
	            }

	            return false;

	        case 75:
	            transnum();
	        case 3:
	        case 8:
	        case 9:
	        case 21:
	        case 33:
	        case 34:
	        case 35:
	        case 41:
	        case 46:
	        case 53:
	        case 56:
	        case 59:
	        case 62:
	        case 72:
	        case 73:
	        case 78:
	        case 85:
	        case 94:
	        case 119:
	        case 120:
	        case 127:
	        case 128:
	            transnum();
	        case 43:
	        case 44:
	        case 49:
	        case 5:
	        case 6:
	        case 27:
	        case 26:
	        case 45:
	        case 51:
	        case 63:
	        case 64:
	        case 65:
	        case 67:
	        case 70:
	        case 71:
	        case 81:
	        case 82:
	        case 90:
	        case 91:
	        case 109:
	        case 110:
	        case 111:
	        case 112:
	        case 129:
	        case 130:
	        
	        case 131: //RA
	        case 132:
	        case 134:
	        case 135:
	        case 145:
	        
	            if(tw == 51)
	            {
	                j = 0;
	                do
	                {
	                    transnum();
	                    scriptptr--;
	                    j |= script[scriptptr];
	                }
	                while(keyword() == -1);
	                script[scriptptr] = j;
	                scriptptr++;
	            }

	            tempscrptr = scriptptr;
	            scriptptr++; //Leave a spot for the fail location

	            do
	            {
	                j = keyword();
	                if(j == 20 || j == 39)
	                    parsecommand();
	            } while(j == 20 || j == 39);

	            parsecommand();
	            script[tempscrptr] = scriptptr;
	            checking_ifelse++;
	            return false;
	        case 29:
	            num_squigilly_brackets++;
	            do
	                done = parsecommand();
	            while( !done );
	            return false;
	        case 30:
	            num_squigilly_brackets--;
	            if( num_squigilly_brackets < 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found more '}' than '{'.");
	                error++;
	            }
	            return true;
	        case 76:
	            scriptptr--;
	            j = 0;
	            while( text[textptr] != 0x0a )
	            {
	                betaname[j] = text[textptr];
	                j++; textptr++;
	            }
	            betaname[j] = 0;
	            return false;
	        case 20:
	            scriptptr--; //Negate the rem
	            while( text[textptr] != 0x0a )
	                textptr++;

	            return false;
	            
	        case 107:
	            scriptptr--;
	            transnum();
	            scriptptr--;
	            j = script[scriptptr];
	            while( text[textptr] == ' ' ) textptr++;

	            i = 0;

	            while( text[textptr] != 0x0a )
	            {
	                volume_names[j][i] = Character.toUpperCase(text[textptr]);
	                textptr++;
	                i++;
	                if(i >= 32)
	                {
	                    Console.Println("  * ERROR!(L" + line_number + ") Volume name exceeds character size limit of 32.");
	                    error++;
	                    while( text[textptr] != 0x0a ) textptr++;
	                    break;
	                }
	            }
	            volume_names[j][i-1] = '\0';
	            if(numepisodes < j)
	            	numepisodes = j;

	            return false;
	        case 108:
	            scriptptr--;
	            transnum();
	            scriptptr--;
	            j = script[scriptptr];
	            while( text[textptr] == ' ' ) textptr++;

	            i = 0;

	            while( text[textptr] != 0x0a )
	            {
	                skill_names[j][i] = Character.toUpperCase(text[textptr]);
	                textptr++;
	                i++;
	                if(i >= 32)
	                {
	                    Console.Println("  * ERROR!(L" + line_number + ") Skill name exceeds character size limit of 32.");
	                    error++;
	                    while( text[textptr] != 0x0a ) textptr++;
	                    break;
	                }
	            }
	            skill_names[j][i-1] = '\0';
	            return false;
	        case 0:
	            scriptptr--;
	            transnum();
	            scriptptr--;
	            j = script[scriptptr];
	            transnum();
	            scriptptr--;
	            k = script[scriptptr];
	            while( text[textptr] == ' ' ) textptr++;

	            i = 0;
	            while( text[textptr] != ' ' && text[textptr] != 0x0a )
	            {
	                level_file_names[j*11+k][i] = text[textptr];
	                textptr++; i++;
	                if(i > 127)
	                {
	                    Console.Println("  * ERROR!(L" + line_number + ") Level file name exceeds character size limit of 128.");
	                    error++;
	                    while( text[textptr] != ' ') textptr++;
	                    break;
	                }
	            }
	            level_names[j*11+k][i-1] = '\0';

	            while( text[textptr] == ' ' ) textptr++;

	            partime[j*11+k] =
	                (((text[textptr+0]-'0')*10+(text[textptr+1]-'0'))*26*60)+
	                (((text[textptr+3]-'0')*10+(text[textptr+4]-'0'))*26);

	            textptr += 5;
	            while( text[textptr] == ' ' ) textptr++;

	            designertime[j*11+k] =
	            		(((text[textptr+0]-'0')*10+(text[textptr+1]-'0'))*26*60)+
		                (((text[textptr+3]-'0')*10+(text[textptr+4]-'0'))*26);

	            textptr += 5;
	            while( text[textptr] == ' ' ) textptr++;

	            i = 0;

	            while( text[textptr] != 0x0a )
	            {
	                level_names[j*11+k][i] = Character.toUpperCase(text[textptr]);
	                textptr++; i++;
	                if(i >= 32)
	                {
	                    Console.Println("  * ERROR!(L" + line_number + ") Level name exceeds character size limit of 32.");
	                    error++;
	                    while( text[textptr] != 0x0a ) textptr++;
	                    break;
	                }
	            }
	            level_names[j*11+k][i-1] = '\0';
	            if(numlevels[j] < k)
	            	numlevels[j] = k;
	            return false;
	        case 79:
	            scriptptr--;
	            transnum();
	            k = script[scriptptr-1];
	            if(k >= NUMOFFIRSTTIMEACTIVE)
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Quote amount exceeds limit of " + NUMOFFIRSTTIMEACTIVE + " characters.");
	                error++;
	                break;
	            }
	            scriptptr--;
	            i = 0;
	            while( text[textptr] == ' ' )
	                textptr++;

	            while( text[textptr] != 0x0a )
	            {
	                fta_quotes[k][i] = text[textptr];
	                textptr++; i++;
	                if(i >= 64)
	                {
	                    Console.Println("  * ERROR!(L" + line_number + ") Quote exceeds character size limit of 64.");
	                    error++;
	                    while( text[textptr] != 0x0a ) textptr++;
	                    break;
	                }
	            }
	            fta_quotes[k][i] = '\0';
	            return false;
	        case 57:
	            scriptptr--;
	            transnum();
	            k = script[scriptptr-1];
	            if(k >= NUM_SOUNDS)
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Exceeded sound limit of " + NUM_SOUNDS + ".");
	                error++;
	            }
	            scriptptr--;
	            i = 0;
	            while( text[textptr] == ' ')
	                textptr++;

	            int soundptr = textptr;
	            while( text[textptr] != ' ' )
	            {
	                textptr++; 
	                i++;
	                if(i >= 13)
	                {
	                	Console.Print(new String(text, soundptr, i));
	                    Console.Println("  * ERROR!(L" + line_number + ") Sound filename exceeded limit of 13 characters.",line_number);
	                    error++;
	                    while( text[textptr] != ' ' ) textptr++;
	                    break;
	                }
	            }
	            sounds[k] = new String(text, soundptr, i);
	            

	            transnum();
	            soundps[k] = (short) script[scriptptr-1];
	            scriptptr--;
	            transnum();
	            soundpe[k] = (short) script[scriptptr-1];
	            scriptptr--;
	            transnum();
	            soundpr[k] = (short) script[scriptptr-1];
	            scriptptr--;
	            transnum();
	            soundm[k] = (short) script[scriptptr-1];
	            scriptptr--;
	            transnum();
	            soundvo[k] = (short) script[scriptptr-1];
	            scriptptr--;
	            return false;
	        case 4:
	            if( parsing_actor == 0 )
	            {
	                Console.Println("  * ERROR!(L" + line_number + ") Found 'enda' without defining 'actor'.");
	                error++;
	            } 
	            
                if( num_squigilly_brackets > 0 )
                {
                    Console.Println("  * ERROR!(L" + line_number + ") Found more '{' than '}' before 'enda'.");
                    error++;
                }
                parsing_actor = 0;

	            return false;
	        case 12:
	        case 16:
	        case 84:
	        case 22:    //KILLIT
	        case 36:
	        case 38:
	        case 42:
	        case 47:
	        case 61:
	        case 66:
	        case 83:
	        case 95:
	        case 96:
	        case 97:
	        case 104:
	        case 106:
	        case 115:
	        case 116:
	        case 117:
	        case 118:
	        case 121:
	        case 122:
	        case 123:
	        case 124:
	        case 125:
	        case 126:
	        	
	        case 133: //RA
	        case 136:
	        case 137:
	        case 138:
	        case 139:
	        case 141:
	        case 144:
	        case 143:
	        case 146:
	            return false;
	        case 60:
	        	for(j = 0; j < 34; j++)
	        	{
	        		 transnum();
	        		 scriptptr--;
	        		 params[j] = script[scriptptr];
	        		 
	        		 if (j != 30) continue;
	        		 
	        		 if (keyword() != -1) {
	        			 Console.Println("Looks like Redneck Rampage Edition CON files.");
		            	 GameCON = RR;
	        			 break;
	        		 } else {
	        			 Console.Println("Looks like Redneck Rampage: Rides Again Edition CON files.");
	        			 GameCON = RRRA;
	        		 }
	        	}
	        	
	        	j = 0;
	    		ud.const_visibility = params[j++];
	    		impact_damage = params[j++];
	    		max_player_health = params[j++];
	    		max_armour_amount = params[j++];
	    		respawnactortime = params[j++];
	    		respawnitemtime = params[j++];
	    		dukefriction = params[j++];
	    		gc = params[j++];
	    		crossbowblastradius = params[j++];
	    		tntblastradius = params[j++];
	    		shrinkerblastradius = params[j++];
	    		powderblastradius = params[j++];
	    		morterblastradius = params[j++];
	    		bouncemineblastradius = params[j++];
	    		seenineblastradius = params[j++];
	    		max_ammo_amount[PISTOL_WEAPON] = params[j++];
	    		max_ammo_amount[SHOTGUN_WEAPON] = params[j++];
	    		max_ammo_amount[RIFLEGUN_WEAPON] = params[j++];
	    		max_ammo_amount[CROSSBOW_WEAPON] = params[j++];
	    		max_ammo_amount[DYNAMITE_WEAPON] = params[j++];
	    		max_ammo_amount[THROWSAW_WEAPON] = params[j++];
	    		max_ammo_amount[ALIENBLASTER_WEAPON] = params[j++];
	    		max_ammo_amount[POWDERKEG_WEAPON] = params[j++];
	    		max_ammo_amount[TIT_WEAPON] = params[j++];
	    		max_ammo_amount[BUZSAW_WEAPON] = params[j++];
	    		max_ammo_amount[BOWLING_WEAPON] = params[j++];
	    		camerashitable = (char)params[j++];
	    		numfreezebounces = params[j++];
	    		freezerhurtowner = (char)params[j++];
	    		spriteqamount = (short)ClipRange(params[j++], 0, 1024);
    			dildoblase = (char)params[j++];
    			
    			if(GameCON == RRRA)
    			{
    				max_ammo_amount[MOTO_WEAPON] = params[j++];
    	    		max_ammo_amount[BOAT_WEAPON] = params[j++];
    	    		max_ammo_amount[CHICKENBOW_WEAPON] = params[j++];
    			}

	            scriptptr++;
	            return false;
	    }
	    return false;
	}
	
	public static void passone()
	{
	    while( !parsecommand() );
	    if( (error+warning) > 12)
	    	Console.Println(  "  * ERROR! Too many warnings or errors.");
	}
	
	public static void copydefaultcons()
	{
	    for(int i = 0; i < 3; i++)
	    {
	    	byte[] data = kGetBytes(defaultcons[i], 1);
	        int fpo = Bopen( defaultcons[i], "rw");

	        if(fpo == -1 || data == null)
	            continue;

	        Bwrite(fpo, data, data.length);
	        Bclose(fpo);
	    }
	}

	public static void loadefs(String filenam)
	{
	    int fs,fp;

	    if(cache.checkFile(filenam) == null && loadfromgrouponly == 0)
	    {
	    	if(GameMessage("Missing external con file(s). \n \"COPY INTERNAL DEFAULTS TO DIRECTORY?\"", true))
	    	{
	    		Console.Println(" Yes");
	            copydefaultcons();
	    	};
	    }

	    fp = kOpen(filenam,loadfromgrouponly);
	    if( fp == -1 )
	    {
	        if( loadfromgrouponly == 1 )
	            dassert("\nMissing con file(s).");

	        loadfromgrouponly = 1;
	        return; //Not there
	    }
	    else
	    {
	    	Console.Println("Compiling: " + filenam + ".");

	        fs = kFileLength(fp);
	        
	        byte[] buf = new byte[fs+1];
	        label = new char[131072];

	        kRead(fp,buf,fs);
	        last_used_text = new String(buf);
	        last_used_size = fs;

	        text = last_used_text.toCharArray();
	        
	        kClose(fp);
	    }

	    text[fs - 1] = 0;

	    Arrays.fill(actorscrptr, 0);
	    Arrays.fill(actortype, (short) 0);

	    labelcnt = 0;
	    scriptptr = 1;
	    warning = 0;
	    error = 0;
	    line_number = 1;
	    total_lines = 0;

	    passone(); //Tokenize

	    if((warning|error) != 0)
	        Console.Println("Found " + warning + " warning(s), " + error + " error(s).");

	    if(error != 0)
	    {
	        if( loadfromgrouponly != 0 )
	            dassert("\nError in " + filenam + ".");
	        else
	        {
	        	if(GameMessage("\nErrors found in " + filenam + " file.  You should backup the original copies \n"
    	    			+ "before attempting to modify them.\nDo you want to use the internal defaults?"
            			, true))
    	    	{
            		Console.Println(" Yes");
                    loadfromgrouponly = 1;
                    return;
    	    	} else {
    	    		appdispose();
    				System.exit(0);
    	    	}
	        }
	    }
	    else
	    {
	        total_lines += line_number; //1795
	        Console.Println("Code Size:" + (((scriptptr)<<2)-4) + " bytes(" + labelcnt + " labels).");
	    }
	}

	public static boolean dodge(SPRITE s)
	{
	    short i;
	    int bx,by,mx,my,bxvect,byvect,mxvect,myvect,d;

	    mx = s.x;
	    my = s.y;
	    mxvect = sintable[(s.ang+512)&2047]; myvect = sintable[s.ang&2047];

	    for(i=headspritestat[4];i>=0;i=nextspritestat[i]) //weapons list
	    {
	        if( sprite[i].owner == i || sprite[i].sectnum != s.sectnum)
	            continue;

	        bx = sprite[i].x-mx;
	        by = sprite[i].y-my;
	        bxvect = sintable[(sprite[i].ang+512)&2047]; byvect = sintable[sprite[i].ang&2047];

	        if (mxvect*bx + myvect*by >= 0)
	            if (bxvect*bx + byvect*by < 0)
	        {
	            d = bxvect*by - byvect*bx;
	            if (klabs(d) < 65536*64)
	            {
	                s.ang -= 512+(engine.krand()&1024);
	                return true;
	            }
	        }
	    }
	    return false;
	}

	public static short furthestangle(int i, int angs)
	{
	    short j, furthest_angle = 0, angincs;
	    long d, greatestd;
	    SPRITE s = sprite[i];

	    greatestd = -(1<<30);
	    angincs = (short) (2048/angs);

	    if(s.picnum != APLAYER)
	        if( (g_t[0]&63) > 2 ) return (short) ( s.ang + 1024 );

	    for(j=s.ang;j<(2048+s.ang);j+=angincs)
	    {
	        engine.hitscan(s.x, s.y, s.z-(8<<8), s.sectnum,
	            sintable[(j+512)&2047],
	            sintable[j&2047],0,
	            pHitInfo,CLIPMASK1);

	        d = klabs(pHitInfo.hitx-s.x) + klabs(pHitInfo.hity-s.y);
	        if(d > greatestd)
	        {
	            greatestd = d;
	            furthest_angle = j;
	        }
	    }
	    return (short) (furthest_angle&2047);
	}
	
	public static int furthest_x, furthest_y;
	public static int furthestcanseepoint(int i, SPRITE ts, int dax, int day)
	{
	    long d, da;
	    SPRITE s = sprite[i];
	    
	    furthest_x = dax;
	    furthest_y = day;

	    if( (g_t[0]&63) != 0 ) return -1;

	    short angincs = 1024; 
	    if(ud.multimode >= 2 && ud.player_skill >= 3)
	        angincs = (short) (2048/(1+(engine.krand()&1)));

	    for(int j=ts.ang;j<(2048+ts.ang);j+=(angincs-(engine.krand()&511)))
	    {
	        engine.hitscan(ts.x, ts.y, ts.z-(16<<8), ts.sectnum,
	            sintable[(j+512)&2047],
	            sintable[j&2047],16384-(engine.krand()&32767),
	            pHitInfo,CLIPMASK1);

	        d = klabs(pHitInfo.hitx-ts.x)+klabs(pHitInfo.hity-ts.y);
	        da = klabs(pHitInfo.hitx-s.x)+klabs(pHitInfo.hity-s.y);

	        if( d < da )
	            if(engine.cansee(pHitInfo.hitx,pHitInfo.hity,pHitInfo.hitz,(short)pHitInfo.hitsect,s.x,s.y,s.z-(16<<8),s.sectnum) != 0)
	        {
	            furthest_x = pHitInfo.hitx;
	            furthest_y = pHitInfo.hity;
	            return pHitInfo.hitsect;
	        }
	    }
	    return -1;
	}

	public static void alterang(int a)
	{
	    int aang, angdif, goalang;

	    int ticselapsed = (g_t[0])&31;

	    aang = g_sp.ang;

	    g_sp.xvel += (script[g_t[1]]-g_sp.xvel)/5;
	    if(g_sp.zvel < 648) g_sp.zvel += ((script[g_t[1]+1]<<4)-g_sp.zvel)/5;

	    if((a&seekplayer) != 0)
	    {
	        int j = ps[g_p].holoduke_on;

	        if(j >= 0 && engine.cansee(sprite[j].x,sprite[j].y,sprite[j].z,sprite[j].sectnum,g_sp.x,g_sp.y,g_sp.z,g_sp.sectnum) != 0 )
	            g_sp.owner = (short) j;
	        else g_sp.owner = ps[g_p].i;

	        if(sprite[g_sp.owner].picnum == APLAYER)
	            goalang = engine.getangle(hittype[g_i].lastvx-g_sp.x,hittype[g_i].lastvy-g_sp.y);
	        else
	            goalang = engine.getangle(sprite[g_sp.owner].x-g_sp.x,sprite[g_sp.owner].y-g_sp.y);

	        if(g_sp.xvel != 0 && g_sp.picnum != MOSQUITO)
	        {
	            angdif = getincangle(aang,goalang);

	            if(ticselapsed < 2)
	            {
	                if( klabs(angdif) < 256)
	                {
	                    j = 128-(engine.krand()&256);
	                    g_sp.ang += j;
	                    if( hits(g_i) < 844 )
	                        g_sp.ang -= j;
	                }
	            }
	            else if(ticselapsed > 18 && ticselapsed < 26) // choose
	            {
	                if(klabs(angdif>>2) < 128) g_sp.ang = (short) goalang;
	                else g_sp.ang += angdif>>2;
	            }
	        }
	        else g_sp.ang = (short) goalang;
	    }

	    if(ticselapsed < 1)
	    {
	        int j = 2;
	        if((a&furthestdir) != 0)
	        {
	            goalang = furthestangle(g_i,j);
	            g_sp.ang = (short) goalang;
	            g_sp.owner = ps[g_p].i;
	        }

	        if((a&fleeenemy) != 0)
	        {
	            goalang = furthestangle(g_i,j);
	            g_sp.ang = (short) goalang;
	        }
	    }
	}

	public static void move()
	{
	    int l;
	    short goalang, angdif;
	    int daxvel;

	    short a = g_sp.hitag;

	    if(a == -1) a = 0;

	    g_t[0]++;

	    if((a&face_player) != 0)
	    {
	        if(ps[g_p].newowner >= 0)
	            goalang = engine.getangle(ps[g_p].oposx-g_sp.x,ps[g_p].oposy-g_sp.y);
	        else goalang = engine.getangle(ps[g_p].posx-g_sp.x,ps[g_p].posy-g_sp.y);
	        angdif = (short) (getincangle(g_sp.ang,goalang)>>2);
	        if(angdif > -8 && angdif < 0) angdif = 0;
	        g_sp.ang += angdif;
	    }

	    if((a&spin) != 0)
	        g_sp.ang += sintable[ ((g_t[0]<<3)&2047) ]>>6;

	    if((a&face_player_slow) != 0)
	    {
	        if(ps[g_p].newowner >= 0)
	            goalang = engine.getangle(ps[g_p].oposx-g_sp.x,ps[g_p].oposy-g_sp.y);
	        else goalang = engine.getangle(ps[g_p].posx-g_sp.x,ps[g_p].posy-g_sp.y);
	        angdif = (short) (ksgn(getincangle(g_sp.ang,goalang))<<5);
	        if(angdif > -32 && angdif < 0)
	        {
	            angdif = 0;
	            g_sp.ang = goalang;
	        }
	        g_sp.ang += angdif;
	    }


	    if((a&jumptoplayer) == jumptoplayer)
	    {
	        if(g_t[0] < 16)
	            g_sp.zvel -= (sintable[(512+(g_t[0]<<4))&2047]>>5);
	    }

	    if((a&face_player_smart) != 0)
	    {
	        int newx = ps[g_p].posx+(ps[g_p].posxv/768);
	        int newy = ps[g_p].posy+(ps[g_p].posyv/768);
	        goalang = engine.getangle(newx-g_sp.x,newy-g_sp.y);
	        angdif = (short) (getincangle(g_sp.ang,goalang)>>2);
	        if(angdif > -8 && angdif < 0) angdif = 0;
	        g_sp.ang += angdif;
	    }

	    if( g_t[1] == 0 || a == 0 )
	    {
	        if( ( badguy(g_sp) && g_sp.extra <= 0 ) || (hittype[g_i].bposx != g_sp.x) || (hittype[g_i].bposy != g_sp.y) )
	        {
	            hittype[g_i].bposx = g_sp.x;
	            hittype[g_i].bposy = g_sp.y;
	            engine.setsprite(g_i,g_sp.x,g_sp.y,g_sp.z);
	        }
	        return;
	    }

	    if((a&geth) != 0) g_sp.xvel += (script[g_t[1]]-g_sp.xvel)>>1;
	    if((a&getv) != 0) g_sp.zvel += ((script[g_t[1]+1]<<4)-g_sp.zvel)>>1;
	    
	  
	    
	    if((a&dodgebullet) != 0)
	        dodge(g_sp);

	    if(g_sp.picnum != APLAYER)
	        alterang(a);

	    if(g_sp.xvel > -6 && g_sp.xvel < 6 ) g_sp.xvel = 0;

	    a = (short) (badguy(g_sp)?1:0);

	    if(g_sp.xvel != 0 || g_sp.zvel != 0)
	    {
            if( (g_sp.picnum == MOSQUITO) && g_sp.extra > 0)
            {
                if( g_sp.zvel > 0 )
                {
                    hittype[g_i].floorz = l = engine.getflorzofslope(g_sp.sectnum,g_sp.x,g_sp.y);
                    if( g_sp.z > (l-(30<<8)) )
                        g_sp.z = l-(30<<8);
                }
                else
                {
                    hittype[g_i].ceilingz = l = engine.getceilzofslope(g_sp.sectnum,g_sp.x,g_sp.y);
                    if( (g_sp.z-l) < (50<<8) )
                    {
                        g_sp.z = l+(50<<8);
                        g_sp.zvel = 0;
                    }
                }
            }
            
            if(g_sp.zvel > 0 && hittype[g_i].floorz < g_sp.z)
                g_sp.z = hittype[g_i].floorz;
            if( g_sp.zvel < 0)
            {
                l = engine.getceilzofslope(g_sp.sectnum,g_sp.x,g_sp.y);
                if( (g_sp.z-l) < (66<<8) )
                {
                    g_sp.z = l+(66<<8);
                    g_sp.zvel >>= 1;
                }
            }
	
	        if(g_sp.picnum == APLAYER)
	            if( (g_sp.z-hittype[g_i].ceilingz) < (32<<8) )
	                g_sp.z = hittype[g_i].ceilingz+(32<<8);

	        daxvel = g_sp.xvel;
	        angdif = g_sp.ang;

	        if( a != 0 )
	        {
	            if( g_x < 960 && g_sp.xrepeat > 16 )
	            {
	                daxvel = -(1024-g_x);
	                angdif = engine.getangle(ps[g_p].posx-g_sp.x,ps[g_p].posy-g_sp.y);

	                if(g_x < 512)
	                {
	                    ps[g_p].posxv = 0;
	                    ps[g_p].posyv = 0;
	                }
	                else
	                {
	                    ps[g_p].posxv = mulscale(ps[g_p].posxv,dukefriction-0x2000,16);
	                    ps[g_p].posyv = mulscale(ps[g_p].posyv,dukefriction-0x2000,16);
	                }
	            }
	            else if(g_sp.picnum != MOSQUITO 
            		&& g_sp.picnum != 5501 
            		&& g_sp.picnum != UFO1 
            		&& g_sp.picnum != UFO2 
            		&& g_sp.picnum != UFO3 
            		&& g_sp.picnum != UFO4 
            		&& g_sp.picnum != UFO5)
	            {
	                if( hittype[g_i].bposz != g_sp.z || ( ud.multimode < 2 && ud.player_skill < 2 ) )
	                {
	                    if( (g_t[0]&1) != 0 || ps[g_p].actorsqu == g_i ) return;
	                    else daxvel <<= 1;
	                }
	                else
	                {
	                    if( (g_t[0]&3) != 0 || ps[g_p].actorsqu == g_i ) return;
	                    else daxvel <<= 2;
	                }
	            }
	        }

	        hittype[g_i].movflag = movesprite(g_i,
	            (daxvel*(sintable[(angdif+512)&2047]))>>14,
	            (daxvel*(sintable[angdif&2047]))>>14,g_sp.zvel,CLIPMASK0);
	   }

	   if( a != 0 )
	   {
	       if ((sector[g_sp.sectnum].ceilingstat&1) != 0) {
	    	   if ( shadeEffect[g_sp.sectnum] )
	    		   g_sp.shade += (16-g_sp.shade)>>1;
	    	   else
	    		   g_sp.shade += (sector[g_sp.sectnum].ceilingshade-g_sp.shade)>>1;
	       }
	       else g_sp.shade += (sector[g_sp.sectnum].floorshade-g_sp.shade)>>1;

	       if( sector[g_sp.sectnum].floorpicnum == MIRROR )
	           engine.deletesprite(g_i);
	   }
	}
	
	public static void parseifelse(boolean condition)
	{
	    if( condition)
	    {
	        insptr+=2;
	        parse();
	    }
	    else
	    {
	    	insptr = script[insptr+1];
	        if(script[insptr] == 10)
	        {
	            insptr+=2;
	            parse();
	        }
	    }
	}
	
	public static boolean parse()
	{
		int j, l, s;

	    if(killit_flag != 0) return true;

	    switch(script[insptr])
	    {
	        case 3:
	            insptr++;
	            parseifelse( rnd(script[insptr]) );
	            break;
	        case 45:

	            if(g_x > 1024)
	            {
	                int temphit, sclip, angdif;

	                if( badguy(g_sp) && g_sp.xrepeat > 56 )
	                {
	                    sclip = 3084;
	                    angdif = 48;
	                }
	                else
	                {
	                    sclip = 768;
	                    angdif = 16;
	                }

	                j = hitasprite(g_i);
	                temphit = pHitInfo.hitsprite;
	                if(j == (1<<30))
	                {
	                    parseifelse(1 != 0);
	                    break;
	                }
	                if(j > sclip)
	                {
	                    if(temphit >= 0 && sprite[temphit].picnum == g_sp.picnum)
	                        j = 0;
	                    else
	                    {
	                        g_sp.ang += angdif;
	                        j = hitasprite(g_i);
	                        temphit = pHitInfo.hitsprite;
	                        g_sp.ang -= angdif;
	                        if(j > sclip)
	                        {
	                            if(temphit >= 0 && sprite[temphit].picnum == g_sp.picnum)
	                                j = 0;
	                            else
	                            {
	                                g_sp.ang -= angdif;
	                                j = hitasprite(g_i);
	                                temphit = pHitInfo.hitsprite;
	                                g_sp.ang += angdif;
	                                if( j > 768 )
	                                {
	                                    if(temphit >= 0 && sprite[temphit].picnum == g_sp.picnum)
	                                        j = 0;
	                                    else j = 1;
	                                }
	                                else j = 0;
	                            }
	                        }
	                        else j = 0;
	                    }
	                }
	                else j =  0;
	            }
	            else j = 1;

	            parseifelse(j != 0);
	            break;
	        case 91:
	            j = engine.cansee(g_sp.x,g_sp.y,g_sp.z-((engine.krand()&41)<<8),g_sp.sectnum,ps[g_p].posx,ps[g_p].posy,ps[g_p].posz/*-((engine.krand()&41)<<8)*/,sprite[ps[g_p].i].sectnum);
	            parseifelse(j != 0);
	            if( j != 0 ) hittype[g_i].timetosleep = SLEEPTIME;
	            break;
	        case 110:
	        	j = engine.cansee(g_sp.x,g_sp.y,g_sp.z,g_sp.sectnum,ps[g_p].posx,ps[g_p].posy,ps[g_p].posz,sprite[ps[g_p].i].sectnum);
	        	parseifelse(j != 0);
	        	if( j != 0 ) hittype[g_i].timetosleep = SLEEPTIME;
	        	break;

	        case 49:
	            parseifelse(hittype[g_i].actorstayput == -1);
	            break;
	        case 5:
	        {
	            SPRITE spr;
	            if(ps[g_p].holoduke_on >= 0)
	            {
	            	spr = sprite[ps[g_p].holoduke_on];
	                j = engine.cansee(g_sp.x,g_sp.y,g_sp.z-(engine.krand()&((32<<8)-1)),g_sp.sectnum,
	                		spr.x,spr.y,spr.z,spr.sectnum);
	                if(j == 0)
	                	spr = sprite[ps[g_p].i];
	            }
	            else spr = sprite[ps[g_p].i];

	            j = engine.cansee(g_sp.x,g_sp.y,g_sp.z-(engine.krand()&((47<<8))),g_sp.sectnum,
	            		spr.x,spr.y,spr.z-(24<<8),spr.sectnum);

	            if(j == 0)
	            {
	                if( ( klabs(hittype[g_i].lastvx-g_sp.x)+klabs(hittype[g_i].lastvy-g_sp.y) ) <
	                    ( klabs(hittype[g_i].lastvx-spr.x)+klabs(hittype[g_i].lastvy-spr.y) ) )
	                        j = 0;

	                if( j == 0 )
	                {
	                    j = furthestcanseepoint(g_i,spr, hittype[g_i].lastvx, hittype[g_i].lastvy);
	                    
	                    hittype[g_i].lastvx = furthest_x;
	                    hittype[g_i].lastvy = furthest_y;

	                    if(j == -1) j = 0;
	                    else j = 1;
	                }
	            }
	            else
	            {
	                hittype[g_i].lastvx = spr.x;
	                hittype[g_i].lastvy = spr.y;
	            }

	            if( j == 1 && ( g_sp.statnum == 1 || g_sp.statnum == 6 ) )
	                hittype[g_i].timetosleep = SLEEPTIME;

	            parseifelse(j == 1);
	            break;
	        }

	        case 6:
	            parseifelse(ifhitbyweapon(g_i) >= 0);
	            break;
	        case 27:
	            parseifelse( ifsquished(g_i, g_p) );
	            break;
	        case 26:
	            {
	                j = g_sp.extra;
	                if(g_sp.picnum == APLAYER)
	                    j--;
	                parseifelse(j < 0);
	            }
	            break;
	        case 24:
	            insptr++;
	            g_t[5] = script[insptr];
	            g_t[4] = script[g_t[5]];       // Action
	            g_t[1] = script[g_t[5]+1];       // move
	            g_sp.hitag = (short) (script[g_t[5]+2]);    // Ai
	            g_t[0] = g_t[2] = g_t[3] = 0;
	            if((g_sp.hitag&random_angle) != 0)
	                g_sp.ang = (short) (engine.krand()&2047);
	            insptr++;
	            break;
	        case 7:
	            insptr++;
	            g_t[2] = 0;
	            g_t[3] = 0;
	            g_t[4] = script[insptr];
	            insptr++;
	            break;

	        case 8:
	            insptr++;
	            parseifelse(g_x < script[insptr]);
	            if(g_x > MAXSLEEPDIST && hittype[g_i].timetosleep == 0)
	                hittype[g_i].timetosleep = SLEEPTIME;
	            break;
	        case 9:
	            insptr++;
	            parseifelse(g_x > script[insptr]);
	            if(g_x > MAXSLEEPDIST && hittype[g_i].timetosleep == 0)
	                hittype[g_i].timetosleep = SLEEPTIME;
	            break;
	        case 10:
	            insptr = script[insptr+1];
	            break;
	        case 100:
	            insptr++;
	            g_sp.extra += script[insptr];
	            insptr++;
	            break;
	        case 11:
	            insptr++;
	            g_sp.extra = (short) script[insptr];
	            insptr++;
	            break;
	        case 94:
	            insptr++;

	            if(ud.coop >= 1 && ud.multimode > 1)
	            {
	                if(script[insptr] == 0)
	                {
	                    for(j=0;j < ps[g_p].weapreccnt;j++)
	                        if( ps[g_p].weaprecs[j] == g_sp.picnum )
	                            break;

	                    parseifelse(j < ps[g_p].weapreccnt && g_sp.owner == g_i);
	                }
	                else if(ps[g_p].weapreccnt < 16)
	                {
	                    ps[g_p].weaprecs[ps[g_p].weapreccnt++] = g_sp.picnum;
	                    parseifelse(g_sp.owner == g_i);
	                }
	            }
	            else parseifelse(false);
	            break;
	        case 95:
	            insptr++;
	            if(g_sp.picnum == APLAYER)
	                g_sp.pal = ps[g_sp.yvel].palookup;
	            else g_sp.pal = (short) hittype[g_i].tempang;
	            hittype[g_i].tempang = 0;
	            break;
	        case 104:
	            insptr++;
	            checkweapons(ps[g_sp.yvel]);
	            break;
	        case 106:
	            insptr++;
	            break;
	        case 97:
	            insptr++;
	            if(Sound[g_sp.yvel].num == 0)
	                spritesound(g_sp.yvel,g_i);
	            break;
	        case 96:
	            insptr++;

	            if( ud.multimode > 1 && g_sp.picnum == APLAYER )
	            {
	                if(ps[otherp].quick_kick == 0)
	                    ps[otherp].quick_kick = 14;
	            }
	            else if(g_sp.picnum != APLAYER && ps[g_p].quick_kick == 0)
	                ps[g_p].quick_kick = 14;
	            break;
	        case 28:
	            insptr++;

	            j = ((script[insptr])-g_sp.xrepeat)<<1;
	            g_sp.xrepeat += ksgn(j);

	            insptr++;

	            if( ( g_sp.picnum == APLAYER && g_sp.yrepeat < 36 ) || script[insptr] < g_sp.yrepeat || ((g_sp.yrepeat*(tilesizy[g_sp.picnum]+8))<<2) < (hittype[g_i].floorz - hittype[g_i].ceilingz) )
	            {
	                j = ((script[insptr])-g_sp.yrepeat)<<1;
	                if( klabs(j) != 0 ) g_sp.yrepeat += ksgn(j);
	            }

	            insptr++;

	            break;
	        case 99:
	            insptr++;
	            g_sp.xrepeat = (short) script[insptr];
	            insptr++;
	            g_sp.yrepeat = (short) script[insptr];
	            insptr++;
	            break;
	        case 13:
	            insptr++;
	            shoot(g_i, script[insptr]);
	            insptr++;
	            break;
	        case 127: 
	        	insptr++;
	            parseifelse(ambienttype[g_sp.ang] == script[insptr]);
	        	break;
	        case 128:
	        	//XXX?
	        	insptr++;
	        	if(script[insptr] != 0)
	        	{
	        		if(script[insptr] != 1)
	        		{
	        			insptr++;
	        		    return false;
	        		} 
	        		
	        		if(ambientid[g_sp.ang] < g_x)
	        		{
	        			insptr = insptr+2;
	        			parse();
	        			insptr++;
	        			return false;
	        		}
	        		
	        		insptr++;
	        		if(insptr != 10)
	        		{
	        			insptr++;
	        		    return false;
	        		}
	        	} 
	        	else
	        	{
	        		if(ambientid[g_sp.ang] > g_x)
	        		{
	        			insptr = insptr+2;
	        			parse();
	        			insptr++;
	        			return false;
	        		}
	        		insptr++;
	        		if(insptr != 10)
	        		{
	        			insptr++;
	        		    return false;
	        		}
	        	}
	        	break;
	        case 126:
	        	spritesound(ambienttype[g_sp.ang],g_i);
	            insptr++;
	        	break;
	        case 125:
	            if( Sound[ambienttype[g_sp.ang]].num == 0 )
	                spritesound(ambienttype[g_sp.ang],g_i);
	            insptr++;
	        	break;
	        case 87:
	            insptr++;
	            if( Sound[script[insptr]].num == 0 )
	                spritesound(script[insptr],g_i);
	            insptr++;
	            break;
	        case 89:
	            insptr++;
	            if( Sound[script[insptr]].num > 0 )
	                stopsound(script[insptr]);
	            insptr++;
	            break;
	        case 92:
	            insptr++;
	            if(g_p == screenpeek || ud.coop==1)
	                spritesound(script[insptr],ps[screenpeek].i);
	            insptr++;
	            break;
	        case 124:
	        	insptr++;
	        	LeaveMap();
	        	if(++ud.level_number > 6)
	        		ud.level_number = 0;
	            ud.m_level_number = ud.level_number;
	        	break;
	        case 119:
	        	insptr++;
	            parseifelse(g_sp.extra > script[insptr]);
	        	break;	
	        case 120:
	        	insptr++;
	            parseifelse(g_sp.extra < script[insptr]);
	        	break;
	        case 15:
	            insptr++;
	            spritesound((short) script[insptr],g_i);
	            insptr++;
	            break;
	        case 84:
	            insptr++;
	            ps[g_p].tipincs = 26;
	            break;
	        case 112:
	        case 111:
	        	int d = g_sp.detail;
	        	if(d == 1)
	        		g_sp.detail++;
	        	parseifelse(d > 0);
	        	break;
	        case 123:
	            insptr++;
	            j = headspritesect[g_sp.sectnum];
		        while(j >= 0)
		        {
		        	if ( sprite[j].picnum == 36 )
		        	{
		        		hittype[j].picnum = SHOTSPARK1;
		        		hittype[j].extra = 1;
		        	}
		            j = nextspritesect[j];
		        }
		        return false;
	        case 16:
	            insptr++;
	            g_sp.xoffset = 0;
	            g_sp.yoffset = 0;
	            
	            if ( sector[g_sp.sectnum].lotag == 800 )
	            {
	            	if(g_sp.picnum == AMMO)
	            	{
	            		engine.deletesprite(g_i);
	                    break;
	            	}
	            	
	            	if(g_sp.picnum != APLAYER && (badguy(g_sp) || g_sp.picnum == HEN || g_sp.picnum == COW || g_sp.picnum == PIG || g_sp.picnum == DOGRUN) && g_sp.detail < 128 )
	            	{
	                    g_sp.zvel = 8000;
	                    g_sp.extra = 0;
	                    g_sp.z = hittype[g_i].floorz - 256;
	                    g_sp.detail++;
	            	} 
	            	else if ( g_sp.picnum != APLAYER )
	                {
	                    if ( g_sp.detail != 0 )
	                    	break;
	                    engine.deletesprite(g_i);
	                    break;
	                }
	            	hittype[g_i].picnum = SHOTSPARK1;
	            	hittype[g_i].extra = 1;
	            }
	            {
	                long c;

	                if( floorspace(g_sp.sectnum) )
	                    c = 0;
	                else
	                {
	                    if( ceilingspace(g_sp.sectnum) || sector[g_sp.sectnum].lotag == 2)
	                        c = gc/6;
	                    else c = gc;
	                }

	                if( hittype[g_i].cgg <= 0 || (sector[g_sp.sectnum].floorstat&2) != 0 )
	                {
	                    getglobalz(g_i);
	                    hittype[g_i].cgg = 6;
	                }
	                else hittype[g_i].cgg --;

	                if( g_sp.z < (hittype[g_i].floorz-FOURSLEIGHT) )
	                {
	                    g_sp.zvel += c;
	                    g_sp.z+=g_sp.zvel;

	                    if(g_sp.zvel > 6144) g_sp.zvel = 6144;
	                }
	                else
	                {
	                    g_sp.z = hittype[g_i].floorz - FOURSLEIGHT;

	                    if( badguy(g_sp) || ( g_sp.picnum == APLAYER && g_sp.owner >= 0) )
	                    {

	                        if( g_sp.zvel > 3084 && g_sp.extra <= 1)
	                        {
	                            if(g_sp.pal != 1 && g_sp.picnum != MOSQUITO)
	                            {
	                                if(g_sp.picnum != APLAYER || g_sp.extra <= 0) {
		                                guts(g_sp,JIBS6,15,g_p);
		                                spritesound(SQUISHED,g_i);
		                                spawn(g_i,BLOODPOOL);
	                                } 
	                            }

	                            hittype[g_i].picnum = SHOTSPARK1;
	                            hittype[g_i].extra = 1;
	                            g_sp.zvel = 0;
	                        }
	                        else if(g_sp.zvel > 2048 && sector[g_sp.sectnum].lotag != 1)
	                        {
	                            j = g_sp.sectnum;
	                            engine.pushmove(g_sp.x,g_sp.y,g_sp.z,j,128,(4<<8),(4<<8),CLIPMASK0);
	                            
	                            g_sp.x = pushmove_x;
	                            g_sp.y = pushmove_y;
	                            g_sp.z = pushmove_z;
		                		j = (short) pushmove_sectnum;
	                            if(j != g_sp.sectnum && j >= 0 && j < MAXSECTORS)
	                                engine.changespritesect(g_i,j);

	                            spritesound(THUD,g_i);
	                        }
	                    }
	                    if(sector[g_sp.sectnum].lotag == 1)
	                        switch (g_sp.picnum)
	                        {
	                            case MOSQUITO:
	                                break;
	                            default:
	                                g_sp.z += (24<<8);
	                                break;
	                        }
	                    else g_sp.zvel = 0;
	                }
	            }

	            break;
	        case 4:
	        case 12:
	        case 18:
	            return true;
	        case 30:
	            insptr++;
	            return true;
	        case 2:
	            insptr++;
	            if( ps[g_p].ammo_amount[script[insptr]] >= max_ammo_amount[script[insptr]] )
	            {
	                killit_flag = 2;
	                break;
	            }
	            addammo( script[insptr], ps[g_p], script[insptr+1] );
	            if(ps[g_p].curr_weapon == KNEE_WEAPON)
	                if( ps[g_p].gotweapon[script[insptr]] )
	                    addweapon( ps[g_p], script[insptr] );
	            insptr += 2;
	            break;
	        case 86:
	            insptr++;
	            lotsofmoney(g_sp,script[insptr]);
	            insptr++;
	            break;
	        case 102:
	            insptr++;
	            lotsofmoney(g_sp,script[insptr]);
	            insptr++;
	            break;
	        case 105:
	            insptr++;
	            hittype[g_i].timetosleep = (short)script[insptr];
	            insptr++;
	            break;
	        case 103:
	            insptr++;
	            lotsofmoney(g_sp,script[insptr]);
	            insptr++;
	            break;
	        case 88:
	            insptr++;
	            if(g_sp.detail < 1 || g_sp.detail == 128)
	            {
	            	if ( checkaddkills(g_sp) )
	            		ps[g_p].actors_killed += script[insptr];
	            }
	            hittype[g_i].actorstayput = -1;
	            insptr++;
	            break;
	        case 93:
	            insptr++;
	            spriteglass(g_i,script[insptr]);
	            insptr++;
	            break;
	        case 22:
	            insptr++;
	            killit_flag = 1;
	            break;
	        case 23:
	            insptr++;
	            if( !ps[g_p].gotweapon[script[insptr]] ) addweapon( ps[g_p], script[insptr] );
	            else if( ps[g_p].ammo_amount[script[insptr]] >= max_ammo_amount[script[insptr]] )
	            {
	                 killit_flag = 2;
	                 break;
	            }
	            addammo( script[insptr], ps[g_p], script[insptr+1] );
	            if(ps[g_p].curr_weapon == KNEE_WEAPON)
	                if( ps[g_p].gotweapon[script[insptr]] )
	                    addweapon( ps[g_p], script[insptr] );
	            insptr+=2;
	            break;
	        case 68:
	            insptr++;
	            Console.Println("" + script[insptr]);
	            insptr++;
	            break;
	        case 69:
	            insptr++;
	            ps[g_p].timebeforeexit = (short) script[insptr];
	            ps[g_p].customexitsound = -1;
	            ud.eog = 1;
	            insptr++;
	            break;
	        case 113:
	        	insptr++;
	        	ps[g_p].alcohol_amount += script[insptr];
	        	int extra = sprite[ps[g_p].i].extra;
	        	if(extra != 0)
	        		extra += script[insptr];
	        	
	        	if ( 2 * max_player_health < extra )
	        		extra = (2 * max_player_health);
	        	if ( extra < 0 )
	        		extra = 0;
	        	
	        	 if ( !ud.god )
	             {
	        		 if ( script[insptr] > 0 )
	        		 {
	        			 if ( extra - script[insptr] < (max_player_health >> 2) && (max_player_health >> 2) <= extra )
	        				 spritesound(229, ps[g_p].i);
	        			 ps[g_p].last_extra = (short) extra;
	        		 }
	        		 sprite[ps[g_p].i].extra = (short) extra;
	             }
	        	 
	        	 if ( ps[g_p].alcohol_amount > 100 )
	        		 ps[g_p].alcohol_amount = 100;

	        	 if ( sprite[ps[g_p].i].extra >= max_player_health )
	        	 {
	        		 sprite[ps[g_p].i].extra = (short) max_player_health;
	        		 ps[g_p].last_extra = (short) max_player_health;
	        	 }
	        	 insptr++;
	        	 break;
	        case 117:
	        	insptr++;
	        	movesprite(g_i,
	        			(sintable[(g_sp.ang+1024)&2047])>>10,
	        			(sintable[(g_sp.ang+512)&2047])>>10,g_sp.zvel,CLIPMASK0);
	        	break;
	        case 118:
	        	insptr++;
	        	movesprite(g_i,
	        			(sintable[g_sp.ang&2047])>>10,
	        			(sintable[(g_sp.ang-512)&2047])>>10,g_sp.zvel,CLIPMASK0);
	        	break;
	        case 116:
	        	insptr++;
	        	ps[g_p].posz = sector[sprite[ps[g_p].i].sectnum].ceilingz;
	            sprite[ps[g_p].i].z = ps[g_p].posz;
	            return false;
	        case 115:
	        	insptr++;
	        	int sect = g_sp.sectnum;
	        	int hitag = 0;
	        	int lotag = 0;
	        	
	        	j = headspritesect[sect];
		        while(j >= 0)
		        {
		        	if ( sprite[j].picnum == 63 )
		            {
		        		lotag = sprite[j].lotag;
		        		if ( sprite[j].hitag != 0 )
		        			hitag = sprite[j].hitag;
		            }
		            j = nextspritesect[j];
		        }
		        
		        j = headspritestat[100];
                while(j >= 0)
                {
                    SPRITE sp = sprite[j];
                    if ( hitag != 0 && hitag == sp.hitag )
                    {
                    	s = headspritesect[sp.sectnum];
            		    while(s >= 0)
            		    {
            		    	if ( sprite[s].picnum == 36 )
            		    	{
            		    		hittype[s].picnum = SHOTSPARK1;
            	                hittype[s].extra = 1;
            		    	}   
            		    	s = nextspritesect[s];
            		    }
                    }
                    
                    if(sp.sectnum != g_sp.sectnum)
                    {
                    	if ( lotag == sp.lotag )
                        {
                    		int wallptr = sector[g_sp.sectnum].wallptr;
                			int wallnum = wallptr + sector[g_sp.sectnum].wallnum;
                			int spwall = sector[sp.sectnum].wallptr;
                			for(int w = wallptr; w < wallnum; w++, spwall++)
                			{
                				wall[w].picnum = wall[spwall].picnum;
                                wall[w].overpicnum = wall[spwall].overpicnum;
                                wall[w].shade = wall[spwall].shade;
                                wall[w].xrepeat = wall[spwall].xrepeat;
                                wall[w].yrepeat = wall[spwall].yrepeat;
                                wall[w].xpanning = wall[spwall].xpanning;
                                wall[w].ypanning = wall[spwall].ypanning;
                			}
                			s = g_sp.sectnum;
                			sector[s].floorz = sector[sp.sectnum].floorz;
                            sector[s].ceilingz = sector[sp.sectnum].ceilingz;
                            sector[s].ceilingstat = sector[sp.sectnum].ceilingstat;
                            sector[s].floorstat = sector[sp.sectnum].floorstat;
                            sector[s].ceilingpicnum = sector[sp.sectnum].ceilingpicnum;
                            sector[s].ceilingheinum = sector[sp.sectnum].ceilingheinum;
                            sector[s].ceilingshade = sector[sp.sectnum].ceilingshade;
                            sector[s].ceilingpal = sector[sp.sectnum].ceilingpal;
                            sector[s].ceilingxpanning = sector[sp.sectnum].ceilingxpanning;
                            sector[s].ceilingypanning = sector[sp.sectnum].ceilingypanning;
                            sector[s].floorpicnum = sector[sp.sectnum].floorpicnum;
                            sector[s].floorheinum = sector[sp.sectnum].floorheinum;
                            sector[s].floorshade = sector[sp.sectnum].floorshade;
                            sector[s].floorpal = sector[sp.sectnum].floorpal;
                            sector[s].floorxpanning = sector[sp.sectnum].floorxpanning;
                            sector[s].floorypanning = sector[sp.sectnum].floorypanning;
                            sector[s].visibility = sector[sp.sectnum].visibility;
                            sector[s].filler = sector[sp.sectnum].filler;
                            sector[s].lotag = sector[sp.sectnum].lotag;
                            sector[s].hitag = sector[sp.sectnum].hitag;
                            sector[s].extra = sector[sp.sectnum].extra;;
                        }
                    }
                    j = nextspritestat[j];
                }

                s = headspritesect[sect];
    		    while(s >= 0)
    		    {
    		    	int next = nextspritesect[s];
    		    	if(sprite[s].picnum != 63 
    		    			&& sprite[s].picnum != DESTRUCTO
    		    			&& sprite[s].picnum != COOT 
    		    			&& sprite[s].picnum != TORNADO 
    		    			&& sprite[s].picnum != APLAYER) 
    		    		engine.deletesprite(s);

    		    	s = next;
    		    }
	        	break;
	        case 114:
	        	insptr++;
	        	ps[g_p].gut_amount += script[insptr];
	        	ps[g_p].alcohol_amount -= script[insptr];
	        	if(ps[g_p].gut_amount > 100) 
	        		ps[g_p].gut_amount = 100;
	        	
	        	if ( ps[g_p].alcohol_amount < 0 ) 
	        		ps[g_p].alcohol_amount = 0;
	        	
	        	extra = sprite[ps[g_p].i].extra;
	        	if ( g_sp.picnum == ECLAIRHEALTH )
	        	{
	        		if ( extra > 0 )
	        			extra += script[insptr];
	        		if ( 2 * max_player_health < extra )
	        			extra = (2 * max_player_health);
	        	}
	        	else 
	        	{
	        		if ( extra > max_player_health && script[insptr] > 0 )
	        		{
	        			insptr++;
	        			return false;
	                }
	                if ( extra > 0 )
	                	extra = (extra + 3 * script[insptr]);
	                if ( extra > max_player_health && script[insptr] > 0 )
	                	extra = max_player_health;
	        	}
	        	if ( extra < 0 ) extra = 0;
	        	if ( !ud.god )
	        	{
	        		if ( script[insptr] > 0 )
	        		{
	        			if ( extra - script[insptr] < (max_player_health >> 2) && (max_player_health >> 2) <= extra )
	        				spritesound(229, ps[g_p].i);
	        			ps[g_p].last_extra = (short) extra;
	        		}
	        		sprite[ps[g_p].i].extra = (short) extra;
	        	}
	        	insptr++;
	        	break;
	        case 25:
	            insptr++;

	            if(ps[g_p].newowner >= 0)
	            {
	                ps[g_p].newowner = -1;
	                ps[g_p].posx = ps[g_p].oposx;
	                ps[g_p].posy = ps[g_p].oposy;
	                ps[g_p].posz = ps[g_p].oposz;
	                ps[g_p].ang = ps[g_p].oang;
	                ps[g_p].cursectnum = engine.updatesector(ps[g_p].posx,ps[g_p].posy,ps[g_p].cursectnum);
	                setpal(ps[g_p]);

	                j = headspritestat[1];
	                while(j >= 0)
	                {
	                    if(sprite[j].picnum==CAMERA1)
	                        sprite[j].yvel = 0;
	                    j = nextspritestat[j];
	                }
	            }

	            j = sprite[ps[g_p].i].extra;

	            if(g_sp.picnum != ECLAIRHEALTH)
	            {
	                if( j > max_player_health && script[insptr] > 0 )
	                {
	                    insptr++;
	                    break;
	                }
	                else
	                {
	                    if(j > 0)
	                        j += script[insptr];
	                    if ( j > max_player_health && script[insptr] > 0 )
	                        j = max_player_health;
	                }
	            }
	            else
	            {
	                if( j > 0 )
	                    j += script[insptr];
	                if ( j > (max_player_health<<1) )
	                    j = (max_player_health<<1);
	            }

	            if(j < 0) j = 0;

	            if(!ud.god)
	            {
	                if(script[insptr] > 0)
	                {
	                    if( ( j - script[insptr] ) < (max_player_health>>2) &&
	                        j >= (max_player_health>>2) )
	                            spritesound(DUKE_GOTHEALTHATLOW,ps[g_p].i);

	                    ps[g_p].last_extra = (short) j;
	                }

	                sprite[ps[g_p].i].extra = (short) j;
	            }

	            insptr++;
	            break;
	        case 17:
	            {
	                int tempscrptr = insptr+2;
	                insptr = script[insptr+1];
	                while(true) if(parse()) break;
	                insptr = tempscrptr;
	            }
	            break;
	        case 29:
	            insptr++;
	            while(true) if(parse()) break;
	            break;
	        case 32:
	            g_t[0]=0;
	            insptr++;
	            g_t[1] = script[insptr];
	            insptr++;
	            g_sp.hitag = (short) script[insptr];
	            insptr++;
	            if((g_sp.hitag&random_angle) != 0)
	                g_sp.ang = (short) (engine.krand()&2047);
	            break;
	        case 31:
	            insptr++;
	            if(g_sp.sectnum >= 0 && g_sp.sectnum < MAXSECTORS)
	                spawn(g_i,script[insptr]);
	            insptr++;
	            break;
	        case 33:
	            insptr++;
	            parseifelse( hittype[g_i].picnum == script[insptr]);
	            break;
	        case 21:
	            insptr++;
	            parseifelse(g_t[5] == script[insptr]);
	            break;
	        case 34:
	            insptr++;
	            parseifelse(g_t[4] == script[insptr]);
	            break;
	        case 35:
	            insptr++;
	            parseifelse(g_t[2] >= script[insptr]);
	            break;
	        case 36:
	            insptr++;
	            g_t[2] = 0;
	            break;
	        case 37:
	            {
	                int dnum;

	                insptr++;
	                dnum = script[insptr];
	                insptr++;

	                if(g_sp.sectnum >= 0 && g_sp.sectnum < MAXSECTORS)
	                    for(j=(script[insptr])-1;j>=0;j--)
	                {
	                    if(dnum == SCRAP1) s = 0;
	                    else s = (engine.krand()%3);

	                    int vz = -(engine.krand()&2047);
	                    int ve = (engine.krand()&127)+32;
	                    int va = engine.krand()&2047;
	                    int vy = 32+(engine.krand()&15);
	                    int vx = 32+(engine.krand()&15);
	                    int sz = g_sp.z-(8<<8)-(engine.krand()&8191);
	                    int sy = g_sp.y+(engine.krand()&255)-128;
	                    int sx = g_sp.x+(engine.krand()&255)-128;
	                    
	                    l = EGS(g_sp.sectnum,sx,sy,sz,dnum+s,g_sp.shade,vx,vy,va,ve, vz,g_i,5);
	                    if(dnum == SCRAP1)
	                        sprite[l].yvel = weaponsandammosprites[j%14];
	                    else sprite[l].yvel = -1;
	                    sprite[l].pal = g_sp.pal;
	                }
	                insptr++;
	            }
	            break;
	        case 52:
	            insptr++;
	            g_t[0] = (short) script[insptr];
	            insptr++;
	            break;
	        case 101:
	            insptr++;
	            g_sp.cstat |= (short)script[insptr];
	            insptr++;
	            break;
	        case 40:
	            insptr++;
	            g_sp.cstat = (short) script[insptr];
	            insptr++;
	            break;
	        case 41:
	            insptr++;
	            parseifelse(g_t[1] == script[insptr]);
	            break;
	        case 42:
	            insptr++;
	            if(gm == MODE_DEMO) break;
	            
	            if(ud.multimode < 2)
	            {
	                if( lastload != null && ud.recstat != 2 )
	                	mOpen(mMenus[LOADGAME], -1);
	                else gm = MODE_RESTART;
	                killit_flag = 2;
	            }
	            else
	            {
	                pickrandomspot(g_p);
	                g_sp.x = hittype[g_i].bposx = ps[g_p].bobposx = ps[g_p].oposx = ps[g_p].posx;
	                g_sp.y = hittype[g_i].bposy = ps[g_p].bobposy = ps[g_p].oposy =ps[g_p].posy;
	                g_sp.z = hittype[g_i].bposy = ps[g_p].oposz =ps[g_p].posz;
	                ps[g_p].cursectnum = engine.updatesector(ps[g_p].posx,ps[g_p].posy,ps[g_p].cursectnum);
	                engine.setsprite(ps[g_p].i,ps[g_p].posx,ps[g_p].posy,ps[g_p].posz+PHEIGHT);
	                g_sp.cstat = 257;

	                g_sp.shade = -12;
	                g_sp.clipdist = 32;
	                g_sp.xrepeat = 24;
	                g_sp.yrepeat = 17;
	                g_sp.owner = g_i;
	                g_sp.xoffset = 0;
	                g_sp.pal = ps[g_p].palookup;

	                ps[g_p].last_extra = g_sp.extra = (short) max_player_health;
	                ps[g_p].wantweaponfire = -1;
	                ps[g_p].horiz = 100;
	                ps[g_p].on_crane = -1;
	                ps[g_p].frag_ps = g_p;
	                ps[g_p].horizoff = 0;
	                ps[g_p].opyoff = 0;
	                ps[g_p].wackedbyactor = -1;
	                ps[g_p].shield_amount = (short) max_armour_amount;
	                ps[g_p].dead_flag = 0;
	                ps[g_p].pals_time = 0;
	                ps[g_p].footprintcount = 0;
	                ps[g_p].weapreccnt = 0;
	                ps[g_p].fta = 0;
	                ps[g_p].ftq = 0;
	                ps[g_p].posxv = ps[g_p].posyv = 0;
	                ps[g_p].rotscrnang = 0;

	                ps[g_p].falling_counter = 0;

	                hittype[g_i].extra = -1;
	                hittype[g_i].owner = g_i;

	                hittype[g_i].cgg = 0;
	                hittype[g_i].movflag = 0;
	                hittype[g_i].tempang = 0;
	                hittype[g_i].actorstayput = -1;
	                hittype[g_i].dispicnum = 0;
	                hittype[g_i].owner = ps[g_p].i;

	                resetinventory(g_p);
	                resetweapons(g_p);

	                cameradist = 0;
	                cameraclock = totalclock;
	            }
	            setpal(ps[g_p]);

	            break;
	        case 130:
	        	parseifelse( ud.coop != 0 || numplayers > 2 );
	        	break;
	        case 129:
	        	parseifelse( klabs(g_sp.z-sector[g_sp.sectnum].floorz) < (32<<8) && sector[g_sp.sectnum].floorpicnum == 3073);
	        	break;
	        case 43:
	            parseifelse( klabs(g_sp.z-sector[g_sp.sectnum].floorz) < (32<<8) && sector[g_sp.sectnum].lotag == 1);
	            break;
	        case 44:
	            parseifelse( sector[g_sp.sectnum].lotag == 2);
	            break;
	        case 46:
	            insptr++;
	            parseifelse(g_t[0] >= script[insptr]);
	            break;
	        case 53:
	            insptr++;
	            parseifelse(g_sp.picnum == script[insptr]);
	            break;
	        case 47:
	            insptr++;
	            g_t[0] = 0;
	            break;
	        case 48:
	            insptr+=2;
	            switch(script[insptr-1])
	            {
	                case 0:
	                    ps[g_p].moonshine_amount = (short) script[insptr];
	                    ps[g_p].inven_icon = 2;
	                    break;
	                case 1:
	                    ps[g_p].shield_amount +=          script[insptr];// 100;
	                    if(ps[g_p].shield_amount > max_player_health)
	                        ps[g_p].shield_amount = (short) max_player_health;
	                    break;
	                case 2:
	                    ps[g_p].snorkle_amount =             (short) script[insptr];// 1600;
	                    ps[g_p].inven_icon = 6;
	                    break;
	                case 3:
	                    ps[g_p].beer_amount =          (short) script[insptr];// 1600;
	                    ps[g_p].inven_icon = 3;
	                    break;
	                case 4:
	                    ps[g_p].cowpie_amount =           (short) script[insptr];// 1600;
	                    ps[g_p].inven_icon = 4;
	                    break;
	                case 6:
	                    switch(g_sp.lotag)
	                    {
	                        case 100: ps[g_p].gotkey[1] = 1;break;
	                        case 101: ps[g_p].gotkey[2] = 1;break;
	                        case 102: ps[g_p].gotkey[3] = 1;break;
	                        case 103: ps[g_p].gotkey[4] = 1;break;
	                    }
	                    break;
	                case 7:
	                    ps[g_p].empty_amount = (short) script[insptr];
	                    ps[g_p].inven_icon = 5;
	                    break;
	                case 9:
	                    ps[g_p].inven_icon = 1;
	                    ps[g_p].whishkey_amount = (short) script[insptr];
	                    break;
	                case 10:
	                    ps[g_p].inven_icon = 7;
	                    ps[g_p].boot_amount = (short) script[insptr];
	                    break;
	            }
	            insptr++;
	            break;
	        case 50:
	            hitradius(g_i,script[insptr+1],script[insptr+2],script[insptr+3],script[insptr+4],script[insptr+5]);
	            insptr+=6;
	            break;
	        case 51:
	            {
	                insptr++;

	                l = script[insptr];
	                j = 0;

	                s = g_sp.xvel;

	                if( ((l&8) != 0) && ps[g_p].on_ground && (sync[g_p].bits&2) != 0 )
	                       j = 1;
	                else if( ((l&16)!= 0) && ps[g_p].jumping_counter == 0 && !ps[g_p].on_ground &&
	                    ps[g_p].poszv > 2048 )
	                        j = 1;
	                else if( ((l&32) != 0) && ps[g_p].jumping_counter > 348 )
	                       j = 1;
	                else if( ((l&1) != 0) && s >= 0 && s < 8)
	                       j = 1;
	                else if( ((l&2) != 0) && s >= 8 && (sync[g_p].bits&(1<<5)) == 0 )
	                       j = 1;
	                else if( ((l&4) != 0) && s >= 8 && (sync[g_p].bits&(1<<5)) != 0 )
	                       j = 1;
	                else if( ((l&64) != 0) && ps[g_p].posz < (g_sp.z-(48<<8)) )
	                       j = 1;
	                else if( ((l&128) != 0) && s <= -8 && (sync[g_p].bits&(1<<5)) == 0 )
	                       j = 1;
	                else if( ((l&256) != 0) && s <= -8 && (sync[g_p].bits&(1<<5)) != 0 )
	                       j = 1;
	                else if( ((l&512) != 0) && ( ps[g_p].quick_kick > 0 || ( ps[g_p].curr_weapon == KNEE_WEAPON && ps[g_p].kickback_pic > 0 ) ) )
	                       j = 1;
	                else if( ((l&1024) != 0) && sprite[ps[g_p].i].xrepeat < 8 )
	                       j = 1;
	                else if( ((l&2048) != 0) && ps[g_p].jetpack_on != 0 )
	                       j = 1;
	                else if( ((l&4096) != 0) && ps[g_p].moonshine_amount > 0 && ps[g_p].moonshine_amount < 400 )
	                       j = 1;
	                else if( ((l&8192) != 0) && ps[g_p].on_ground)
	                       j = 1;
	                else if( ((l&16384) != 0) && sprite[ps[g_p].i].xrepeat > 8 && sprite[ps[g_p].i].extra > 0 && ps[g_p].timebeforeexit == 0 )
	                       j = 1;
	                else if( ((l&32768) != 0) && sprite[ps[g_p].i].extra <= 0)
	                       j = 1;
	                else if( ((l&65536)) != 0 )
	                {
	                    if(g_sp.picnum == APLAYER && ud.multimode > 1)
	                        j = getincangle((int)ps[otherp].ang,engine.getangle(ps[g_p].posx-ps[otherp].posx,ps[g_p].posy-ps[otherp].posy));
	                    else
	                        j = getincangle((int)ps[g_p].ang,engine.getangle(g_sp.x-ps[g_p].posx,g_sp.y-ps[g_p].posy));

	                    if( j > -128 && j < 128 )
	                        j = 1;
	                    else
	                        j = 0;
	                }

	                parseifelse(j != 0);

	            }
	            break;
	        case 56:
	            insptr++;
	            parseifelse(g_sp.extra <= script[insptr]);
	            break;
	        case 58:
	            insptr += 2;
	            guts(g_sp,script[insptr-1],script[insptr],g_p);
	            insptr++;
	            break;
	        case 59:
	            insptr++;
	            parseifelse( hittype[g_i].picnum == script[insptr]);
	            break;
	        case 121:
	        	insptr++;
	        	forceplayerangle(ps[g_p]);
	        	ps[g_p].posxv -= sintable[((int)ps[g_p].ang + 512) & 2047] << 7;
	        	ps[g_p].posyv -= sintable[(int)ps[g_p].ang & 2047] << 7;
	        	break;
	        case 61:
	            insptr++;
	            ps[g_p].posxv -= sintable[((int)ps[g_p].ang + 512) & 0x7FF] << 10;
	            ps[g_p].posyv -= sintable[((int)ps[g_p].ang) & 0x7FF] << 10;
	            ps[g_p].jumping_counter = 767;
	            ps[g_p].jumping_toggle = 1;
	            return false;
	        case 62:
	            insptr++;
	            parseifelse( (( hittype[g_i].floorz - hittype[g_i].ceilingz ) >> 8 ) < script[insptr]);
	            break;
	        case 63:
	            parseifelse( (sync[g_p].bits&(1<<29)) != 0);
	            break;
	        case 64:
	            parseifelse((sector[g_sp.sectnum].ceilingstat&1) != 0);
	            break;
	        case 65:
	            parseifelse(ud.multimode > 1);
	            break;
	        case 66:
	            insptr++;
	            if( sector[g_sp.sectnum].lotag == 0 )
	            {
	                neartag(g_sp.x,g_sp.y,g_sp.z-(32<<8),g_sp.sectnum,g_sp.ang,768,1);
	                if( neartagsector >= 0 && isanearoperator(sector[neartagsector].lotag) )
	                    if( (sector[neartagsector].lotag&0xff) == 23 || sector[neartagsector].floorz == sector[neartagsector].ceilingz )
	                        if( (sector[neartagsector].lotag&16384) == 0 )
	                            if( (sector[neartagsector].lotag&32768) == 0 )
	                        {
	                            j = headspritesect[neartagsector];
	                            while(j >= 0)
	                            {
	                                if(sprite[j].picnum == ACTIVATOR)
	                                    break;
	                                j = nextspritesect[j];
	                            }
	                            if(j == -1)
	                                operatesectors(neartagsector,g_i);
	                        }
	            }
	            break;
	        case 67:
	            parseifelse(ceilingspace(g_sp.sectnum));
	            break;

	        case 74:
	            insptr++;
	            if(g_sp.picnum != APLAYER)
	                hittype[g_i].tempang = g_sp.pal;
	            g_sp.pal = (short) script[insptr];
	            insptr++;
	            break;

	        case 77:
	            insptr++;
	            g_sp.picnum = (short) script[insptr];
	            insptr++;
	            break;

	        case 70:
	            parseifelse( dodge(g_sp) );
	            break;
	        case 71:
	            if( badguy(g_sp) )
	                parseifelse( ud.respawn_monsters );
	            else if( inventory(g_sp) )
	                parseifelse( ud.respawn_inventory );
	            else
	                parseifelse( ud.respawn_items );
	            break;
	        case 72:
	            insptr++;
	            parseifelse( (hittype[g_i].floorz - g_sp.z) <= ((script[insptr])<<8));
	            break;
	        case 73:
	            insptr++;
	            parseifelse( ( g_sp.z - hittype[g_i].ceilingz ) <= ((script[insptr])<<8));
	            break;
	        case 14:

	            insptr++;
	            ps[g_p].pals_time = script[insptr];
	            insptr++;
	            for(j=0;j<3;j++)
	            {
	                ps[g_p].pals[j] = (short) script[insptr];
	                insptr++;
	            }
	            break;
	        case 78:
	            insptr++;
	            parseifelse( sprite[ps[g_p].i].extra < script[insptr]);
	            break;

	        case 75:
	            {
	                insptr++;
	                j = 0;
	                switch(script[insptr++])
	                {
	                    case 0:if( ps[g_p].moonshine_amount != script[insptr])
	                           j = 1;
	                        break;
	                    case 1:if(ps[g_p].shield_amount != max_player_health )
	                            j = 1;
	                        break;
	                    case 2:if(ps[g_p].snorkle_amount != script[insptr]) j = 1;break;
	                    case 3:if(ps[g_p].beer_amount != script[insptr]) j = 1;break;
	                    case 4:if(ps[g_p].cowpie_amount != script[insptr]) j = 1;break;
	                    case 6:
	                        switch(g_sp.lotag)
	                        {
	                            case 100: if(ps[g_p].gotkey[1] != 0) j = 1;break;
	                            case 101: if(ps[g_p].gotkey[2] != 0) j = 1;break;
	                            case 102: if(ps[g_p].gotkey[3] != 0) j = 1;break;
	                            case 103: if(ps[g_p].gotkey[4] != 0) j = 1;break;
	                        }
	                        break;
	                    case 7:if(ps[g_p].empty_amount != script[insptr]) j = 1;break;
	                    case 9:
	                        if(ps[g_p].whishkey_amount != script[insptr]) j = 1;break;
	                    case 10:
	                        if(ps[g_p].boot_amount != script[insptr]) j = 1;break;
	                }

	                parseifelse(j != 0);
	                break;
	            }
	        case 38:
	            insptr++;
	            if( ps[g_p].knee_incs == 0 && sprite[ps[g_p].i].xrepeat >= 9 )
	                if( engine.cansee(g_sp.x,g_sp.y,g_sp.z-(4<<8),g_sp.sectnum,ps[g_p].posx,ps[g_p].posy,ps[g_p].posz+(16<<8),sprite[ps[g_p].i].sectnum) != 0 )
	            {
	                ps[g_p].knee_incs = 1;
	                if(ps[g_p].weapon_pos == 0)
	                    ps[g_p].weapon_pos = -1;
	                ps[g_p].actorsqu = g_i;
	            }
	            break;
	        case 90:
	            {
	                short s1;

	                s1 = g_sp.sectnum;

	                j = 0;

	                s1 = engine.updatesector(g_sp.x+108,g_sp.y+108,s1);
	                    if( s1 == g_sp.sectnum )
	                    {
	                    	s1 = engine.updatesector(g_sp.x-108,g_sp.y-108,s1);
	                        if( s1 == g_sp.sectnum )
	                        {
	                        	s1 = engine.updatesector(g_sp.x+108,g_sp.y-108,s1);
	                            if( s1 == g_sp.sectnum )
	                            {
	                            	s1 = engine.updatesector(g_sp.x-108,g_sp.y+108,s1);
	                                if( s1 == g_sp.sectnum )
	                                    j = 1;
	                            }
	                        }
	                    }
	                    parseifelse( j != 0 );
	            }

	            break;
	        case 80:
	            insptr++;
	            FTA(script[insptr],ps[g_p]);
	            insptr++;
	            break;
	        case 81:
	            parseifelse( floorspace(g_sp.sectnum));
	            break;
	        case 82:
	            parseifelse( (hittype[g_i].movflag&49152) > 16384 );
	            break;
	        case 83:
	            insptr++;
	            switch(g_sp.picnum)
	            {
	            	case 1115:
	            	case 1168:
	            	case 5581:
	                    if(g_sp.yvel != 0) operaterespawns(g_sp.yvel);
	                    break;
	                default:
	                    if(g_sp.hitag >= 0) operaterespawns(g_sp.hitag);
	                    break;
	            }
	            break;
	        case 85:
	            insptr++;
	            parseifelse( g_sp.pal == script[insptr]);
	            break;

	        case 109:
	            for(j=1;j<NUM_SOUNDS;j++)
	                if( SoundOwner[j][0].i == g_i )
	                    break;

	            parseifelse( j == NUM_SOUNDS );
	            break; 
	        case 131: //ifmotofast
	        	parseifelse(ps[g_p].Motospeed > 60);
	        	break;
	        case 132: //ifwind
	        	parseifelse(WindTime > 0);
	        	break;
	        case 133: //smacksprite
	        	if((engine.krand() & 1) != 0)
	        		g_sp.ang = (short) ((g_sp.ang - (engine.krand() & 1)) & kAngleMask);
	        	else
	        		g_sp.ang = (short) ((g_sp.ang + 512 + (engine.krand() & 1)) & kAngleMask);
	        	insptr++;
	        	break;
	        case 134: //ifonmoto
	        	parseifelse(ps[g_p].OnMotorcycle); 
	        	break;
	        case 135: //ifonboat
	        	parseifelse(ps[g_p].OnBoat);
	        	break;
	        case 136: //fakebubba XXX
	
	        	insptr++;
	        	break;
	        case 137: //mamatrigger
//	        	sub_56430(667, ps[g_p].i); XXX
	        	insptr++;
	        	break;
	        case 138: //mamaspawn XXX
//	        	if (	word_119BDA != 0 )
//	            {
//	        		word_119BDA--;
//	        		sub_76710(g_i, 7280);
//	            }
	        	insptr++;
	        	break;
	        case 139: //mamaquake XXX
//	        	if ( g_sp.pal == 31 )
//	        		byte_1D7E72 = 4;
//	            else if ( g_sp.pal == 32 )
//	            	byte_1D7E72 = 6;
	        	insptr++;
	        	break;
	        case 140:
	        	insptr++;
	        	g_sp.clipdist = script[insptr];
	        	insptr++;
	        	break;
	        case 141: //mamaend
	        	insptr++;
	        	ps[myconnectindex].field_609 = 150;
	        	break;
	        case 142:
	        	insptr++;
	        	g_sp.picnum = (short) script[insptr];
	        	insptr++;
	        	break;
	        case 143: //garybanjo
	        	if ( BellSound != 0)
	            {
	              if ( Sound[BellSound].num == 0 )
	                spritesound(BellSound, g_i);
	            }
	        	else 
	            {
	        		switch((engine.krand() & 3) + 1)
	        		{
		        		case 1:
		        			spritesound(272, g_i);
		        			BellSound = 272;
		        			break;
		        		case 4:
		        			spritesound(262, g_i);
		        			BellSound = 262;
		        			break;
		        		default:
		        			spritesound(273, g_i);
		        			BellSound = 273;
		        			break;
	        		}
	            }
	        	break;
	        case 144: //motoloopsnd
	        	if ( Sound[411].num == 0 )
	        		spritesound(411, g_i);
	        	insptr++;
	        	break;
	        case 145: //ifsizedown
	        	g_sp.xrepeat--;
	        	g_sp.yrepeat--;
	        	parseifelse(g_sp.xrepeat <= 5);
	        	break;
	        case 146: //rndmove
	        	g_sp.ang = (short) (engine.krand() & kAngleMask);
	        	g_sp.xvel = 25;
	        	insptr++;
	        	break;
	        default:
	            killit_flag = 1;
	            break;
	    }
		return false;
	}
	
	public static void execute(int i,int p,int x)
	{
	    boolean done;

	    g_i = (short) i;
	    g_p = (short) p;
	    g_x = x;
	    g_sp = sprite[g_i];
	    g_t = hittype[g_i].temp_data;

	    if( actorscrptr[g_sp.picnum] == 0 ) return;

	    insptr = 4 + actorscrptr[g_sp.picnum];

	    killit_flag = 0;

	    if(g_sp.sectnum < 0 || g_sp.sectnum >= MAXSECTORS)
	    {
	        if(badguy(g_sp))
	            ps[g_p].actors_killed++;
	        engine.deletesprite(g_i);
	        return;
	    }

	    if(g_t[4] != 0)
	    {
	        g_sp.lotag += TICSPERFRAME;
	        if(g_sp.lotag > script[g_t[4]+4] )
	        {
	        	g_t[2]++;
	            g_sp.lotag = 0;
	            g_t[3] += script[g_t[4]+3];
	        }
	        if( klabs(g_t[3]) >= klabs( script[g_t[4]+1] * script[g_t[4]+3] ) )
	        	g_t[3] = 0;
	    }

	    do
	        done = parse();
	    while( !done );

	    if(killit_flag == 1)
	    {
	        if(ps[g_p].actorsqu == g_i)
	            ps[g_p].actorsqu = -1;
	        engine.deletesprite(g_i);
	    }
	    else
	    {
	        move();

	        if( g_sp.statnum == 1)
	        {
	            if( badguy(g_sp) )
	            {
	                if( g_sp.xrepeat > 60 ) return;
	                if( ud.respawn_monsters && g_sp.extra <= 0 ) return;
	            }
	            else if( ud.respawn_items && (g_sp.cstat&32768) != 0 ) return;

	            if(hittype[g_i].timetosleep > 1)
	                hittype[g_i].timetosleep--;
	            else if(hittype[g_i].timetosleep == 1)
	                 engine.changespritestat(g_i,2);
	        }

	        else if(g_sp.statnum == 6)
	            switch(g_sp.picnum)
	            {
		            case 1147:
		            case 1187:
		            case 1251:
		            case 1268:
		            case 1304:
		            case 1305:
		            case 1306:
		            case 1309:
		            case 1315:
		            case 1317:
	                    if(hittype[g_i].timetosleep > 1)
	                        hittype[g_i].timetosleep--;
	                    else if(hittype[g_i].timetosleep == 1)
	                        engine.changespritestat(g_i,2);
	                    break;
	            }
	    }
	}
	
	public static void compilecons()
	{
		conweigth = 0;
		loadefs(confilename);
		if( loadfromgrouponly != 0 )
		{
			Console.Println("  * Writing defaults to current directory.");
			loadefs(confilename);
		}
	}
}
