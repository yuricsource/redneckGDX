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

import com.badlogic.gdx.utils.IntArray;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.exceptions.InitializationException;
import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Types.EpisodeInfo;
import ru.m210projects.Redneck.Types.MapInfo;
import ru.m210projects.Redneck.Types.Script;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.net.Mmulti.connecthead;
import static ru.m210projects.Build.net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bstrcmp;
import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Gameutils.neartag;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.ResourceHandler.episodeManager;
import static ru.m210projects.Redneck.RSector.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.*;

public class Gamedef {

    public static final int[] params = new int[35];
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
    //RA
    public static final int justjump1 = 256;
    public static final int justjump2 = 8192;
    public static final int windang = 16384;
    public static final int antifaceplayerslow = 32768;

    public static final int MAXSCRIPTSIZE = 20460;
    public static final int NUMKEYWORDS = 147;

    public static final char[][] keyw = {"definelevelname".toCharArray(),    // 0
            "actor".toCharArray(),                // 1    [#]
            "addammo".toCharArray(),            // 2    [#]
            "ifrnd".toCharArray(),                // 3    [C]
            "enda".toCharArray(),                // 4    [:]
            "ifcansee".toCharArray(),            // 5    [C]
            "ifhitweapon".toCharArray(),        // 6    [#]
            "action".toCharArray(),            // 7    [#]
            "ifpdistl".toCharArray(),            // 8    [#]
            "ifpdistg".toCharArray(),            // 9    [#]
            "else".toCharArray(),                // 10   [#]
            "strength".toCharArray(),            // 11   [#]
            "break".toCharArray(),                // 12   [#]
            "shoot".toCharArray(),                // 13   [#]
            "palfrom".toCharArray(),            // 14   [#]
            "sound".toCharArray(),                // 15   [filename.voc]
            "fall".toCharArray(),                // 16   []
            "state".toCharArray(),                // 17
            "ends".toCharArray(),                // 18
            "define".toCharArray(),            // 19
            "//".toCharArray(),                // 20
            "ifai".toCharArray(),                // 21
            "killit".toCharArray(),            // 22
            "addweapon".toCharArray(),            // 23
            "ai".toCharArray(),                // 24
            "addphealth".toCharArray(),        // 25
            "ifdead".toCharArray(),            // 26
            "ifsquished".toCharArray(),        // 27
            "sizeto".toCharArray(),            // 28
            "{".toCharArray(),                    // 29
            "}".toCharArray(),                    // 30
            "spawn".toCharArray(),                // 31
            "move".toCharArray(),                // 32
            "ifwasweapon".toCharArray(),        // 33
            "ifaction".toCharArray(),            // 34
            "ifactioncount".toCharArray(),        // 35
            "resetactioncount".toCharArray(),    // 36
            "debris".toCharArray(),            // 37
            "pstomp".toCharArray(),            // 38
            "/*".toCharArray(),                // 39
            "cstat".toCharArray(),                // 40
            "ifmove".toCharArray(),            // 41
            "resetplayer".toCharArray(),        // 42
            "ifonwater".toCharArray(),            // 43
            "ifinwater".toCharArray(),            // 44
            "ifcanshoottarget".toCharArray(),    // 45
            "ifcount".toCharArray(),            // 46
            "resetcount".toCharArray(),        // 47
            "addinventory".toCharArray(),        // 48
            "ifactornotstayput".toCharArray(),    // 49
            "hitradius".toCharArray(),            // 50
            "ifp".toCharArray(),                // 51
            "count".toCharArray(),                // 52
            "ifactor".toCharArray(),            // 53
            "music".toCharArray(),                // 54
            "include".toCharArray(),            // 55
            "ifstrength".toCharArray(),        // 56
            "definesound".toCharArray(),        // 57
            "guts".toCharArray(),                // 58
            "ifspawnedby".toCharArray(),        // 59
            "gamestartup".toCharArray(),        // 60
            "wackplayer".toCharArray(),        // 61
            "ifgapzl".toCharArray(),            // 62
            "ifhitspace".toCharArray(),        // 63
            "ifoutside".toCharArray(),            // 64
            "ifmultiplayer".toCharArray(),        // 65
            "operate".toCharArray(),            // 66
            "ifinspace".toCharArray(),            // 67
            "debug".toCharArray(),                // 68
            "endofgame".toCharArray(),            // 69
            "ifbulletnear".toCharArray(),        // 70
            "ifrespawn".toCharArray(),            // 71
            "iffloordistl".toCharArray(),        // 72
            "ifceilingdistl".toCharArray(),    // 73
            "spritepal".toCharArray(),            // 74
            "ifpinventory".toCharArray(),        // 75
            "betaname".toCharArray(),            // 76
            "cactor".toCharArray(),            // 77
            "ifphealthl".toCharArray(),        // 78
            "definequote".toCharArray(),        // 79
            "quote".toCharArray(),                // 80
            "ifinouterspace".toCharArray(),    // 81
            "ifnotmoving".toCharArray(),        // 82
            "respawnhitag".toCharArray(),        // 83
            "tip".toCharArray(),                // 84
            "ifspritepal".toCharArray(),        // 85
            "feathers".toCharArray(),            // 86
            "soundonce".toCharArray(),            // 87
            "addkills".toCharArray(),            // 88
            "stopsound".toCharArray(),            // 89
            "ifawayfromwall".toCharArray(),    // 90
            "ifcanseetarget".toCharArray(),    // 91
            "globalsound".toCharArray(),        // 92
            "lotsofglass".toCharArray(),        // 93
            "ifgotweaponce".toCharArray(),        // 94
            "getlastpal".toCharArray(),        // 95
            "pkick".toCharArray(),            // 96
            "mikesnd".toCharArray(),            // 97
            "useractor".toCharArray(),        // 98
            "sizeat".toCharArray(),            // 99
            "addstrength".toCharArray(),        // 100   [#]
            "cstator".toCharArray(),            // 101
            "mail".toCharArray(),                // 102
            "paper".toCharArray(),                // 103
            "tossweapon".toCharArray(),        // 104
            "sleeptime".toCharArray(),            // 105
            "nullop".toCharArray(),            // 106
            "definevolumename".toCharArray(),    // 107
            "defineskillname".toCharArray(),    // 108
            "ifnosounds".toCharArray(),        // 109

            //REDNECK RAMPAGE CMDS
            "ifnocover".toCharArray(),            // 110 --no
            "ifhittruck".toCharArray(),            // 111 COOT
            "iftipcow".toCharArray(),            // 112 COW
            "isdrunk".toCharArray(),            // 113 --no
            "iseat".toCharArray(),                // 114 GAME
            "destroyit".toCharArray(),            // 115 GAME
            "larrybird".toCharArray(),            // 116 TORNADO
            "strafeleft".toCharArray(),            // 117 Sheriff
            "straferight".toCharArray(),        // 118 Sheriff
            "ifactorhealthg".toCharArray(),        // 119 Sheriff
            "ifactorhealthl".toCharArray(),    // 120 Sheriff
            "slapplayer".toCharArray(),            // 121 Vixen
            "ifpdrunk".toCharArray(),            // 122 --no
            "tearitup".toCharArray(),            // 123 TORNADO
            "smackbubba".toCharArray(),            // 124 BUBBA
            "soundtagonce".toCharArray(),        // 125 Crickets
            "soundtag".toCharArray(),            // 126 --no
            "ifsoundid".toCharArray(),            // 127 Crickets
            "ifsounddist".toCharArray(),        // 128 Crickets
            "ifonmud".toCharArray(),            // 129 GAME
            "ifcoop".toCharArray(),                // 130 BUBBA

            //RR RIDES AGAIN CMDS
            "ifmotofast".toCharArray(),            //131 GAME
            "ifwind".toCharArray(),                //132 --no
            "smacksprite".toCharArray(),        //133 RABBIT
            "ifonmoto".toCharArray(),            //134 GAME
            "ifonboat".toCharArray(),            //135 GAME
            "fakebubba".toCharArray(),            //136 BUBBA
            "mamatrigger".toCharArray(),        //137 MAMA
            "mamaspawn".toCharArray(),            //138 MAMA
            "mamaquake".toCharArray(),            //139 MAMA
            "clipdist".toCharArray(),            //140 --no
            "mamaend".toCharArray(),            //141 MAMA
            "newpic".toCharArray(),                //142 GAME
            "garybanjo".toCharArray(),            //143 COOTPLAY
            "motoloopsnd".toCharArray(),        //144 BIKERB
            "ifsizedown".toCharArray(),            //145 MAMAC
            "rndmove".toCharArray(),            //146 MAMAC
    };
    private static final char[] tempbuf = new char[2048];
    public static final String confilename = "GAME.CON";
    public static int conweigth = 0;
    public static int insptr;
    public static int scriptptr, error, warning, killit_flag;
    public static final IntArray labelcode = new IntArray();
    public static char[] label;
    public static int labelcnt;
    public static short checking_ifelse, parsing_state;
    public static String last_used_text;
    public static short num_squigilly_brackets;
    public static short g_i, g_p;
    public static int g_x;
    public static Sprite g_sp;
    public static int[] g_t;
    public static int furthest_x, furthest_y;
    private static char[] text;
    private static int textptr = 0;
    private static int parsing_actor;
    private static short line_number;

    public static float getincangle(float a, float na) {
        a = BClampAngle(a);
        na = BClampAngle(na);

        if (!(Math.abs(a - na) < 1024)) {
            if (na > 1024) {
                na -= 2048;
            }
            if (a > 1024) {
                a -= 2048;
            }

            na -= 2048;
            a -= 2048;
        }
        return (na - a);
    }

    public static int getincangle(int a, int na) {
        a &= 2047;
        na &= 2047;

        if (klabs(a - na) >= 1024) {
            if (na > 1024) {
                na -= 2048;
            }
            if (a > 1024) {
                a -= 2048;
            }

            na -= 2048;
            a -= 2048;
        }
        return (na - a);
    }

    public static boolean ispecial(char c) {
        if (c == 0x0a) {
            line_number++;
            return true;
        }

        return c == ' ' || c == 0x0d;
    }

    public static boolean isaltok(char c) {
        return (isalnum(c) || c == '{' || c == '}' || c == '/' || c == '*' || c == '-' || c == '_' || c == '.');
    }

    public static boolean isaltok(byte c) {
        return (Character.isLetterOrDigit(c) || c == '{' || c == '}' || c == '/' || c == '*' || c == '-' || c == '_' || c == '.');
    }

    public static boolean isalnum(char c) {
        return Character.isLetterOrDigit(c);
    }

    public static void getglobalz(int i) {
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        Sector sec = boardService.getSector(s.getSectnum());
        if (sec == null) {
            return;
        }

        if (s.getStatnum() == 10 || s.getStatnum() == 6 || s.getStatnum() == 2 || s.getStatnum() == 1 || s.getStatnum() == 4) {
            int zr;
            if (s.getStatnum() == 4) {
                zr = 4;
            } else {
                zr = 127;
            }

            engine.getzrange(s.getX(), s.getY(), s.getZ() - (FOURSLEIGHT), s.getSectnum(), zr, CLIPMASK0);
            hittype[i].ceilingz = zr_ceilz;
            hittype[i].floorz = zr_florz;
            int lz = zr_florhit;

            if ((lz & kHitTypeMask) == kHitSprite) {
                lz &= kHitIndexMask;
                Sprite hsp = boardService.getSprite(lz);
                if (hsp != null && (hsp.getCstat() & 48) == 0) {
                    if (badguy(hsp) && hsp.getPal() != 1) {
                        if (s.getStatnum() != 4) {
                            hittype[i].dispicnum = -4; // No shadows on actors
                            s.setXvel(-256);
                            ssp(i, CLIPMASK0);
                        }
                    } else if (hsp.getPicnum() == APLAYER && badguy(s)) {
                        hittype[i].dispicnum = -4; // No shadows on actors
                        s.setXvel(-256);
                        ssp(i, CLIPMASK0);
                    } else if (s.getStatnum() == 4 && hsp.getPicnum() == APLAYER) {
                        if (s.getOwner() == lz) {
                            hittype[i].ceilingz = sec.getCeilingz();
                            hittype[i].floorz = sec.getFloorz();
                        }
                    }
                }
            }
        } else {
            hittype[i].ceilingz = sec.getCeilingz();
            hittype[i].floorz = sec.getFloorz();
        }
    }

    public static void makeitfall(Script con, int i) {
        final Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        final Sector sec = boardService.getSector(s.getSectnum());
        if (sec == null) {
            return;
        }

        int c;

        if (floorspace(s.getSectnum())) {
            c = 0;
        } else {
            if (ceilingspace(s.getSectnum()) || sec.getLotag() == 2) {
                c = con.gc / 6;
            } else {
                c = con.gc;
            }
        }

        if ((s.getStatnum() == 1 || s.getStatnum() == 10 || s.getStatnum() == 2 || s.getStatnum() == 6)) {
            engine.getzrange(s.getX(), s.getY(), s.getZ() - (FOURSLEIGHT), s.getSectnum(), 127, CLIPMASK0);
            hittype[i].ceilingz = zr_ceilz;
            hittype[i].floorz = zr_florz;
        } else {
            hittype[i].ceilingz = sec.getCeilingz();
            hittype[i].floorz = sec.getFloorz();
        }

        if (s.getZ() < hittype[i].floorz - (FOURSLEIGHT)) {
            if (sec.getLotag() == 2 && s.getZvel() > 3122) {
                s.setZvel(3144);
            }
            if (s.getZvel() < 6144) {
                s.setZvel(s.getZvel() + c);
            } else {
                s.setZvel(6144);
            }
            s.setZ(s.getZ() + s.getZvel());
        }
        if (s.getZ() >= hittype[i].floorz - (FOURSLEIGHT)) {
            s.setZ(hittype[i].floorz - FOURSLEIGHT);
            s.setZvel(0);
        }
    }

    public static void getlabel() {
        while (!isalnum(text[textptr])) {
            if (text[textptr] == 0x0a) {
                line_number++;
            }
            textptr++;
            if (text[textptr] == 0) {
                return;
            }
        }

        int len = 0;
        while (true) {
            if (ispecial(text[textptr + ++len])) {
                break;
            }
        }

        if (label.length <= (labelcnt << 6) + len) // resize
        {
            char[] newItems = new char[(labelcnt << 6) + len + 1];
            System.arraycopy(label, 0, newItems, 0, label.length);
            label = newItems;
        }

        System.arraycopy(text, textptr, label, (labelcnt << 6), len);
        label[(labelcnt << 6) + len] = 0;
        textptr += len;
    }

    public static int keyword() {
        int temptextptr = textptr;

        while (!isaltok(text[temptextptr])) {
            temptextptr++;
            if (text[temptextptr] == 0) {
                return 0;
            }
        }

        int i = 0;
        while (isaltok(text[temptextptr])) {
            tempbuf[i] = text[temptextptr++];
            i++;
        }
        tempbuf[i] = 0;

        for (i = 0; i < NUMKEYWORDS; i++) {
            if (Bstrcmp(tempbuf, keyw[i]) == 0) {
                return i;
            }
        }

        return -1;
    }

    public static int transword(Script con) //Returns its code #
    {
        while (!isaltok(text[textptr])) {
            if (text[textptr] == 0x0a) {
                line_number++;
            }
            if (text[textptr] == 0) {
                return -1;
            }
            textptr++;
        }

        int l = 0;
        while (isaltok(text[textptr + l])) {
            tempbuf[l] = text[textptr + l];
            l++;
        }

        tempbuf[l] = 0;

        for (int i = 0; i < NUMKEYWORDS; i++) {
            if (Bstrcmp(tempbuf, keyw[i]) == 0) {
                con.script[scriptptr] = i; //set to struct script
                textptr += l;
                scriptptr++;
                return i;
            }
        }

        textptr += l;
        if (tempbuf[0] == '{' && tempbuf[1] != 0) {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting a SPACE or CR between '{' and '" + new String(tempbuf, 1, l) + "'.");
        } else if (tempbuf[0] == '}' && tempbuf[1] != 0) {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting a SPACE or CR between '}' and '" + new String(tempbuf, 1, l) + "'.");
        } else if (tempbuf[0] == '/' && tempbuf[1] == '/' && tempbuf[2] != 0) {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '//' and '" + new String(tempbuf, 2, l) + "'.");
        } else if (tempbuf[0] == '/' && tempbuf[1] == '*' && tempbuf[2] != 0) {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '/*' and '" + new String(tempbuf, 2, l) + "'.");
        } else if (tempbuf[0] == '*' && tempbuf[1] == '/' && tempbuf[2] != 0) {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting a SPACE between '*/' and '" + new String(tempbuf, 2, l) + "'.");
        } else {
            Console.out.println("  * ERROR!(L" + line_number + ") Expecting key word, but found '" + new String(tempbuf, 0, l) + "'.");
        }

        error++;
        return -1;
    }

    public static void transnum(Script con) {
        while (!isaltok(text[textptr])) {
            if (text[textptr] == 0x0a) {
                line_number++;
            }
            textptr++;
            if (text[textptr] == 0) {
                return;
            }
        }

        int l = 0;
        while (isaltok(text[textptr + l])) {
            tempbuf[l] = text[textptr + l];
            l++;
        }

        tempbuf[l] = 0;
        for (int i = 0; i < NUMKEYWORDS; i++) {
            if (Bstrcmp(label, (labelcnt << 6), keyw[i], 0) == 0) {
                error++;
                Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[(labelcnt << 6)] + "' is a key word.");
                textptr += l;
            }
        }

        for (int i = 0; i < labelcnt; i++) {
            if (Bstrcmp(tempbuf, 0, label, i << 6) == 0) {
                con.script[scriptptr] = labelcode.get(i);
                scriptptr++;
                textptr += l;
                return;
            }
        }

        if (!Character.isDigit(text[textptr]) && text[textptr] != '-') {
            Console.out.println("  * ERROR!(L" + line_number + ") Parameter '" + new String(text, textptr, l) + "' is undefined.");
            error++;
            textptr += l;
            return;
        }

        try {
            con.script[scriptptr] = Integer.parseInt(new String(text, textptr, l));
        } catch (Exception e) {
            Console.out.println("  * ERROR!(L" + line_number + ") Parameter '" + new String(tempbuf, 0, l) + "' is undefined.", OsdColor.RED);
            error++;
            textptr += l;
            return;
        }

        scriptptr++;
        textptr += l;
    }

    public static boolean parsecommand(Script con) {
        int i, j, k;
        char temp_ifelse_check;
        short temp_line_number;
        int tempscrptr;

        if (error > 12 || (text[textptr] == '\0') || (text[textptr + 1] == '\0')) {
            return true;
        }

        int tw = transword(con);

        switch (tw) {
            default:
            case -1:
                return false; //End
            case 39:      //Rem endrem
                scriptptr--;
                j = line_number;
                do {
                    textptr++;
                    if (text[textptr] == 0x0a) {
                        line_number++;
                    }
                    if (text[textptr] == 0) {
                        Console.out.println("  * ERROR!(L" + j + ") Found '/*' with no '*/'.");
                        error++;
                        return false;
                    }
                } while (text[textptr] != '*' || text[textptr + 1] != '/');
                textptr += 2;
                return false;

            case 17:
                if (parsing_actor == 0 && parsing_state == 0) {
                    getlabel();
                    scriptptr--;
                    labelcode.add(scriptptr);
                    labelcnt = labelcode.size;

                    parsing_state = 1;

                    return false;
                }

                getlabel();

                for (i = 0; i < NUMKEYWORDS; i++) {
                    if (Bstrcmp(label, labelcnt << 6, keyw[i], 0) == 0) {
                        error++;
                        Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt << 6] + "' is a key word.");
                        return false;
                    }
                }

                for (j = 0; j < labelcnt; j++) {
                    if (Bstrcmp(label, (j << 6), label, (labelcnt << 6)) == 0) {
                        con.script[scriptptr] = labelcode.get(j);
                        break;
                    }
                }

                if (j == labelcnt) {
                    Console.out.println("  * ERROR!(L" + line_number + ") State '" + label[labelcnt << 6] + "' not found.");
                    error++;
                }
                scriptptr++;
                return false;
            case 15:
            case 92:
            case 87:
            case 89:
            case 93:

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
                transnum(con);
                return false;
            case 18:
                if (parsing_state == 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'ends' with no 'state'.");
                    error++;
                }

                if (num_squigilly_brackets > 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found more '{' than '}' before 'ends'.");
                    error++;
                }
                if (num_squigilly_brackets < 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found more '}' than '{' before 'ends'.");
                    error++;
                }
                parsing_state = 0;

                return false;
            case 19:
                getlabel();
                // Check to see it's already defined
                for (i = 0; i < NUMKEYWORDS; i++) {
                    if (Bstrcmp(label, labelcnt << 6, keyw[i], 0) == 0) {
                        error++;
                        Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt << 6] + "' is a key word.");
                        return false;
                    }
                }

                for (i = 0; i < labelcnt; i++) {
                    if (Bstrcmp(label, labelcnt << 6, label, i << 6) == 0) {
                        warning++;
                        Console.out.println("  * WARNING.(L" + line_number + ") Duplicate definition '" + label[labelcnt << 6] + "' ignored.");
                        break;
                    }
                }
                transnum(con);
                if (i == labelcnt) {
                    labelcode.add(con.script[scriptptr - 1]);
                    labelcnt = labelcode.size;
                }
//	                labelcode[labelcnt++] = script[scriptptr-1];
                scriptptr -= 2;
                return false;
            case 14:
                for (j = 0; j < 4; j++) {
                    if (keyword() == -1) {
                        transnum(con);
                    } else {
                        break;
                    }
                }

                while (j < 4) {
                    con.script[scriptptr] = 0;
                    scriptptr++;
                    j++;
                }
                return false;
            case 32:
                if (parsing_actor != 0 || parsing_state != 0) {
                    transnum(con);

                    j = 0;
                    while (keyword() == -1) {
                        transnum(con);
                        scriptptr--;
                        j |= con.script[scriptptr];
                    }
                    con.script[scriptptr] = j;
                    scriptptr++;
                } else {
                    scriptptr--;
                    getlabel();
                    // Check to see it's already defined

                    for (i = 0; i < NUMKEYWORDS; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), keyw[i], 0) == 0) {
                            error++;
                            Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt << 6] + "' is a key word.");
                            return false;
                        }
                    }

                    for (i = 0; i < labelcnt; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), label, (i << 6)) == 0) {
                            warning++;
                            Console.out.println("  * WARNING.(L" + line_number + ") Duplicate move '" + label[labelcnt << 6] + "' ignored.");
                            break;
                        }
                    }
                    if (i == labelcnt) {
                        labelcode.add(scriptptr);
                        labelcnt = labelcode.size;
                    }
//	                    labelcode[labelcnt++] = scriptptr;
                    for (j = 0; j < 2; j++) {
                        if (keyword() >= 0) {
                            break;
                        }
                        transnum(con);
                    }
                    for (k = j; k < 2; k++) {
                        con.script[scriptptr] = 0;
                        scriptptr++;
                    }
                }
                return false;
            case 54: {
                scriptptr--;
                transnum(con); // Volume Number (0/4)
                scriptptr--;

                k = con.script[scriptptr] - 1;
                i = 0;
                if (k >= 0) // if it's background music
                {
                    while (keyword() == -1) {
                        while (!isaltok(text[textptr])) {
                            if (text[textptr] == 0x0a) {
                                line_number++;
                            }
                            textptr++;
                            if (text[textptr] == 0) {
                                break;
                            }
                        }
                        int startptr = textptr;
                        j = 0;
                        while (isaltok(text[textptr + j])) {
                            j++;
                        }

                        con.music_fn[k][i] = new String(text, startptr, j);
                        textptr += j;
                        if (i > 9) {
                            break;
                        }
                        i++;
                    }
                } else {
                    while (keyword() == -1) {
                        while (!isaltok(text[textptr])) {
                            if (text[textptr] == 0x0a) {
                                line_number++;
                            }
                            textptr++;
                            if (text[textptr] == 0) {
                                break;
                            }
                        }
                        int startptr = textptr;
                        j = 0;
                        while (isaltok(text[textptr + j])) {
                            j++;
                        }
                        con.env_music_fn[i] = new String(text, startptr, j);

                        textptr += j;
                        if (i > 9) {
                            break;
                        }
                        i++;
                    }
                }
            }
            return false;
            case 55: {
                scriptptr--;
                while (!isaltok(text[textptr])) {
                    if (text[textptr] == 0x0a) {
                        line_number++;
                    }
                    textptr++;
                    if (text[textptr] == 0) {
                        break;
                    }
                }
                j = 0;
                while (isaltok(text[textptr])) {
                    tempbuf[j] = text[textptr++];
                    j++;
                }
                tempbuf[j] = '\0';

                String name = new String(tempbuf, 0, j).trim();
                Entry fp = game.getCache().getEntry(name, !loadfromgrouponly);
                if (!fp.exists()) {
                    error++;
                    Console.out.println("  * ERROR!(L" + line_number + ") Could not find '" + label[labelcnt << 6] + "'.", OsdColor.RED);
                    return false;
                }

                j = (int) fp.getSize();
                byte[] buf = new byte[j + 1];
                System.arraycopy(fp.getBytes(), 0, buf, 0, j);

                Console.out.println("Including: '" + name + "'.");
                if (name.equalsIgnoreCase("gator66.con") || name.equalsIgnoreCase("pig66.con") || name.equalsIgnoreCase("bubba66.con")) {
                    conweigth += 1;
                }

                if (conweigth == 3 && con.type != RRRA) {
                    con.type = RR66;
                    conweigth = 0;
                }

                temp_line_number = line_number;
                line_number = 1;
                temp_ifelse_check = (char) checking_ifelse;
                checking_ifelse = 0;

                char[] origtext = text;
                int origtptr = textptr;
                textptr = 0;

                last_used_text = new String(buf);

                text = last_used_text.toCharArray();

                text[j] = 0;

                boolean done;
                do {
                    done = parsecommand(con);
                } while (!done);

                text = origtext;
                textptr = origtptr;
                line_number = temp_line_number;
                checking_ifelse = (short) temp_ifelse_check;

                return false;
            }
            case 24:
                if (parsing_actor != 0 || parsing_state != 0) {
                    transnum(con);
                } else {
                    scriptptr--;
                    getlabel();

                    for (i = 0; i < NUMKEYWORDS; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), keyw[i], 0) == 0) {
                            error++;
                            Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt << 6] + "' is a key word.");
                            return false;
                        }
                    }

                    for (i = 0; i < labelcnt; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), label, (i << 6)) == 0) {
                            warning++;
                            Console.out.println("  * WARNING.(L" + line_number + ") Duplicate ai '" + label[labelcnt << 6] + "' ignored.");
                            break;
                        }
                    }

                    if (i == labelcnt) {
                        labelcode.add(scriptptr);
                        labelcnt = labelcode.size;
                    }
//	                    labelcode[labelcnt++] = scriptptr;

                    for (j = 0; j < 3; j++) {
                        if (keyword() >= 0) {
                            break;
                        }
                        if (j == 2) {
                            k = 0;
                            while (keyword() == -1) {
                                transnum(con);
                                scriptptr--;
                                k |= con.script[scriptptr];
                            }
                            con.script[scriptptr] = k;
                            scriptptr++;
                            return false;
                        } else {
                            transnum(con);
                        }
                    }
                    for (k = j; k < 3; k++) {
                        con.script[scriptptr] = 0;
                        scriptptr++;
                    }
                }
                return false;
            case 7:
                if (parsing_actor != 0 || parsing_state != 0) {
                    transnum(con);
                } else {
                    scriptptr--;
                    getlabel();
                    // Check to see it's already defined

                    for (i = 0; i < NUMKEYWORDS; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), keyw[i], 0) == 0) {
                            error++;
                            Console.out.println("  * ERROR!(L" + line_number + ") Symbol '" + label[labelcnt << 6] + "' is a key word.");
                            return false;
                        }
                    }

                    for (i = 0; i < labelcnt; i++) {
                        if (Bstrcmp(label, (labelcnt << 6), label, (i << 6)) == 0) {
                            warning++;
                            Console.out.println("  * WARNING.(L" + line_number + ") Duplicate action '" + label[labelcnt << 6] + "' ignored.");
                            break;
                        }
                    }

                    if (i == labelcnt) {
                        labelcode.add(scriptptr);
                        labelcnt = labelcode.size;
                    }
//	                    labelcode[labelcnt++] = scriptptr;

                    for (j = 0; j < 5; j++) {
                        if (keyword() >= 0) {
                            break;
                        }
                        transnum(con);
                    }
                    for (k = j; k < 5; k++) {
                        con.script[scriptptr] = 0;
                        scriptptr++;
                    }
                }
                return false;

            case 1:
                if (parsing_state != 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'actor' within 'state'.");
                    error++;
                }

                if (parsing_actor != 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'actor' within 'actor'.");
                    error++;
                }

                num_squigilly_brackets = 0;
                scriptptr--;
                parsing_actor = scriptptr;

                transnum(con);
                scriptptr--;
                con.actorscrptr[con.script[scriptptr]] = parsing_actor;

                for (j = 0; j < 4; j++) {
                    con.script[parsing_actor + j] = 0;
                    if (j == 3) {
                        j = 0;
                        while (keyword() == -1) {
                            transnum(con);
                            scriptptr--;
                            j |= con.script[scriptptr];
                        }
                        con.script[scriptptr] = j;
                        scriptptr++;
                        break;
                    } else {
                        if (keyword() >= 0) {
                            scriptptr += (4 - j);
                            break;
                        }
                        transnum(con);

                        con.script[parsing_actor + j] = con.script[scriptptr - 1];
                    }
                }

                checking_ifelse = 0;

                return false;

            case 98:

                if (parsing_state != 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'useritem' within 'state'.");
                    error++;
                }

                if (parsing_actor != 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'useritem' within 'actor'.");
                    error++;
                }

                num_squigilly_brackets = 0;
                scriptptr--;
                parsing_actor = scriptptr;

                transnum(con);
                scriptptr--;
                j = con.script[scriptptr];
                transnum(con);
                scriptptr--;
                con.actorscrptr[con.script[scriptptr]] = parsing_actor;
                con.actortype[con.script[scriptptr]] = (short) j;

                for (j = 0; j < 4; j++) {
                    con.script[parsing_actor + j] = 0;
                    if (j == 3) {
                        j = 0;
                        while (keyword() == -1) {
                            transnum(con);
                            scriptptr--;
                            j |= con.script[scriptptr];
                        }
                        con.script[scriptptr] = j;
                        scriptptr++;
                        break;
                    } else {
                        if (keyword() >= 0) {
                            scriptptr += (4 - j);
                            break;
                        }
                        transnum(con);

                        con.script[parsing_actor + j] = con.script[scriptptr - 1];
                    }
                }

                checking_ifelse = 0;
                return false;

            case 2:
            case 23:
            case 28:
            case 99:
            case 37:
            case 48:
            case 58:
                transnum(con);
                transnum(con);
                break;
            case 50:
                transnum(con);
                transnum(con);
                transnum(con);
                transnum(con);
                transnum(con);
                break;
            case 10:
                if (checking_ifelse != 0) {
                    checking_ifelse--;
                    tempscrptr = scriptptr;
                    scriptptr++; //Leave a spot for the fail location
                    parsecommand(con);

                    con.script[tempscrptr] = scriptptr;
                } else {
                    scriptptr--;
                    error++;
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'else' with no 'if'.");
                }

                return false;

            case 75:
                transnum(con);
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
                transnum(con);
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

                if (tw == 51) {
                    j = 0;
                    do {
                        transnum(con);
                        scriptptr--;
                        j |= con.script[scriptptr];
                    } while (keyword() == -1);
                    con.script[scriptptr] = j;
                    scriptptr++;
                }

                tempscrptr = scriptptr;
                scriptptr++; //Leave a spot for the fail location

                do {
                    j = keyword();
                    if (j == 20 || j == 39) {
                        parsecommand(con);
                    }
                } while (j == 20 || j == 39);

                parsecommand(con);
                con.script[tempscrptr] = scriptptr;
                checking_ifelse++;
                return false;
            case 29: {
                num_squigilly_brackets++;
                boolean done;
                do {
                    done = parsecommand(con);
                } while (!done);
                return false;
            }
            case 30:
                num_squigilly_brackets--;
                if (num_squigilly_brackets < 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found more '}' than '{'.");
                    error++;
                }
                return true;
            case 76:
                scriptptr--;
                j = 0;
                while (text[textptr] != 0x0a) {
                    con.betaname[j] = text[textptr];
                    j++;
                    textptr++;
                }
                con.betaname[j] = 0;
                return false;
            case 20:
                scriptptr--; //Negate the rem
                while (text[textptr] != 0x0a) {
                    textptr++;
                }

                return false;

            case 107:
                scriptptr--;
                transnum(con);
                scriptptr--;
                j = con.script[scriptptr];
                while (text[textptr] == ' ') {
                    textptr++;
                }

                i = 0;

                while (text[textptr] != 0x0a) {
                    con.volume_names[j][i] = Character.toUpperCase(text[textptr]);
                    textptr++;
                    i++;
                    if (i >= 32) {
                        Console.out.println("  * ERROR!(L" + line_number + ") Volume name exceeds character size limit of 32.");
                        error++;
                        while (text[textptr] != 0x0a) {
                            textptr++;
                        }
                        break;
                    }
                }
                con.volume_names[j][i - 1] = '\0';
                con.nEpisodes = Math.max(con.nEpisodes, j + 1);
                return false;
            case 108:
                scriptptr--;
                transnum(con);
                scriptptr--;
                j = con.script[scriptptr];
                while (text[textptr] == ' ') {
                    textptr++;
                }

                i = 0;

                while (text[textptr] != 0x0a) {
                    con.skill_names[j][i] = Character.toUpperCase(text[textptr]);
                    textptr++;
                    i++;
                    if (i >= 32) {
                        Console.out.println("  * ERROR!(L" + line_number + ") Skill name exceeds character size limit of 32.");
                        error++;
                        while (text[textptr] != 0x0a) {
                            textptr++;
                        }
                        break;
                    }
                }
                con.skill_names[j][i - 1] = '\0';
                con.nSkills = Math.max(con.nSkills, j + 1);
                return false;
            case 0:
                scriptptr--;
                transnum(con);
                scriptptr--;
                j = con.script[scriptptr];
                transnum(con);
                scriptptr--;
                k = con.script[scriptptr];
                while (text[textptr] == ' ') {
                    textptr++;
                }

                i = 0;
                while (text[textptr] != ' ' && text[textptr] != 0x0a) {
                    con.level_file_names[j * 11 + k][i] = text[textptr];
                    textptr++;
                    i++;
                    if (i > 127) {
                        Console.out.println("  * ERROR!(L" + line_number + ") Level file name exceeds character size limit of 128.");
                        error++;
                        while (text[textptr] != ' ') {
                            textptr++;
                        }
                        break;
                    }
                }
                con.level_names[j * 11 + k][i - 1] = '\0';

                while (text[textptr] == ' ') {
                    textptr++;
                }

                con.partime[j * 11 + k] = (((text[textptr] - '0') * 10 + (text[textptr + 1] - '0')) * 26 * 60) + (((text[textptr + 3] - '0') * 10 + (text[textptr + 4] - '0')) * 26);

                textptr += 5;
                while (text[textptr] == ' ') {
                    textptr++;
                }

                con.designertime[j * 11 + k] = (((text[textptr] - '0') * 10 + (text[textptr + 1] - '0')) * 26 * 60) + (((text[textptr + 3] - '0') * 10 + (text[textptr + 4] - '0')) * 26);

                textptr += 5;
                while (text[textptr] == ' ') {
                    textptr++;
                }

                i = 0;

                while (text[textptr] != 0x0a) {
                    con.level_names[j * 11 + k][i] = Character.toUpperCase(text[textptr]);
                    textptr++;
                    i++;
                    if (i >= 32) {
                        Console.out.println("  * ERROR!(L" + line_number + ") Level name exceeds character size limit of 32.");
                        error++;
                        while (text[textptr] != 0x0a) {
                            textptr++;
                        }
                        break;
                    }
                }
                con.level_names[j * 11 + k][i - 1] = '\0';
                con.nMaps[j] = Math.max(con.nMaps[j], k + 1);
                return false;
            case 79:
                scriptptr--;
                transnum(con);
                k = con.script[scriptptr - 1];
                if (k >= NUMOFFIRSTTIMEACTIVE) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Quote amount exceeds limit of " + NUMOFFIRSTTIMEACTIVE + " characters.");
                    error++;
                    break;
                }
                scriptptr--;
                i = 0;
                while (text[textptr] == ' ') {
                    textptr++;
                }

                while (text[textptr] != 0x0a) {
                    con.fta_quotes[k][i] = text[textptr];
                    textptr++;
                    i++;
                    if (i >= 64) {
                        Console.out.println("  * ERROR!(L" + line_number + ") Quote exceeds character size limit of 64.");
                        error++;
                        while (text[textptr] != 0x0a) {
                            textptr++;
                        }
                        break;
                    }
                }
                con.fta_quotes[k][i] = '\0';
                return false;
            case 57:
                scriptptr--;
                transnum(con);
                k = con.script[scriptptr - 1];
                if (k >= NUM_SOUNDS) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Exceeded sound limit of " + NUM_SOUNDS + ".");
                    error++;
                }
                scriptptr--;
                i = 0;
                while (text[textptr] == ' ') {
                    textptr++;
                }

                int soundptr = textptr;
                while (text[textptr] != ' ') {
                    textptr++;
                    i++;
                    if (i >= 13) {
                        Console.out.println(new String(text, soundptr, i));
                        Console.out.println("  * ERROR!(L" + line_number + ") Sound filename exceeded limit of 13 characters.");
                        error++;
                        while (text[textptr] != ' ') {
                            textptr++;
                        }
                        break;
                    }
                }
                con.sounds[k] = new String(text, soundptr, i);

                transnum(con);
                con.soundps[k] = (short) con.script[scriptptr - 1];
                scriptptr--;
                transnum(con);
                con.soundpe[k] = (short) con.script[scriptptr - 1];
                scriptptr--;
                transnum(con);
                con.soundpr[k] = (short) con.script[scriptptr - 1];
                scriptptr--;
                transnum(con);
                con.soundm[k] = (short) con.script[scriptptr - 1];
                scriptptr--;
                transnum(con);
                con.soundvo[k] = (short) con.script[scriptptr - 1];
                scriptptr--;
                return false;
            case 4:
                if (parsing_actor == 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found 'enda' without defining 'actor'.");
                    error++;
                }

                if (num_squigilly_brackets > 0) {
                    Console.out.println("  * ERROR!(L" + line_number + ") Found more '{' than '}' before 'enda'.");
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
                for (j = 0; j < 34; j++) {
                    transnum(con);
                    scriptptr--;
                    params[j] = con.script[scriptptr];

                    if (j != 30) {
                        continue;
                    }

                    if (error > 0) {
                        return false;
                    }

                    if (keyword() != -1) {
                        con.type = RR;
                        break;
                    } else {
                        con.type = RRRA;
                    }
                }

                j = 0;
                con.const_visibility = params[j++];
                con.impact_damage = params[j++];
                con.max_player_health = params[j++];
                con.max_armour_amount = params[j++];
                con.respawnactortime = params[j++];
                con.respawnitemtime = params[j++];
                con.dukefriction = params[j++];
                con.gc = params[j++];
                con.crossbowblastradius = params[j++];
                con.tntblastradius = params[j++];
                con.shrinkerblastradius = params[j++];
                con.powderblastradius = params[j++];
                con.morterblastradius = params[j++];
                con.bouncemineblastradius = params[j++];
                con.seenineblastradius = params[j++];
                con.max_ammo_amount[PISTOL_WEAPON] = params[j++];
                con.max_ammo_amount[SHOTGUN_WEAPON] = params[j++];
                con.max_ammo_amount[RIFLEGUN_WEAPON] = params[j++];
                con.max_ammo_amount[CROSSBOW_WEAPON] = params[j++];
                con.max_ammo_amount[DYNAMITE_WEAPON] = params[j++];
                con.max_ammo_amount[THROWSAW_WEAPON] = params[j++];
                con.max_ammo_amount[ALIENBLASTER_WEAPON] = params[j++];
                con.max_ammo_amount[POWDERKEG_WEAPON] = params[j++];
                con.max_ammo_amount[TIT_WEAPON] = params[j++];
                con.max_ammo_amount[BUZSAW_WEAPON] = params[j++];
                con.max_ammo_amount[BOWLING_WEAPON] = params[j++];
                con.camerashitable = (char) params[j++];
                con.numfreezebounces = params[j++];
                con.freezerhurtowner = (char) params[j++];
                con.spriteqamount = (short) ClipRange(params[j++], 0, 1024);
                con.dildoblase = (char) params[j++];

                if (con.type == RRRA) {
                    con.max_ammo_amount[MOTO_WEAPON] = params[j++];
                    con.max_ammo_amount[BOAT_WEAPON] = params[j++];
                    con.max_ammo_amount[CHICKENBOW_WEAPON] = params[j];
                }

                scriptptr++;
                return false;
        }
        return false;
    }

    public static void passone(Script con) {
        while (true) {
            if (parsecommand(con)) {
                break;
            }
        }

        if ((error + warning) > 12) {
            Console.out.println("  * ERROR! Too many warnings or errors.");
        }
    }

    public static Script loadefs(String filenam) throws InitializationException {
        Entry fp = game.getCache().getEntry(filenam, !loadfromgrouponly);
        if (!fp.exists()) {
            throw new InitializationException("\nMissing con file(s).");
        }

        Console.out.println("Compiling: " + filenam + ".");

        int fs = (int) fp.getSize();

        byte[] buf = new byte[fs + 1];
        label = new char[131072];
        System.arraycopy(fp.getBytes(), 0, buf, 0, fs);

        last_used_text = new String(buf);
        text = last_used_text.toCharArray();
        text[fs] = 0;

        Script con = new Script();

        labelcode.clear();
        labelcnt = 0;
        scriptptr = 1;
        warning = 0;
        error = 0;
        line_number = 1;

        try {
            passone(con); //Tokenize
        } catch (Exception e) {
            e.printStackTrace();
            error = 1;
        }

        if ((warning | error) != 0) {
            Console.out.println("Found " + warning + " warning(s), " + error + " error(s).");
        }

        if (error != 0) {
            throw new InitializationException("\nCompilation error in " + filenam + ".");
        } else {
            Console.out.println("Code Size:" + (((scriptptr) << 2) - 4) + " bytes(" + labelcnt + " labels).");
        }

        return con;
    }

    public static boolean dodge(Sprite s) {
        int bx, by, mx, my, bxvect, byvect, mxvect, myvect, d;

        mx = s.getX();
        my = s.getY();
        mxvect = EngineUtils.cos(s.getAng());
        myvect = EngineUtils.sin(s.getAng());

        for (ListNode<Sprite> node = boardService.getStatNode(4); node != null; node = node.getNext())//weapons list
        {
            Sprite sp = node.get();
            if (sp.getOwner() == node.getIndex() || sp.getSectnum() != s.getSectnum()) {
                continue;
            }

            bx = sp.getX() - mx;
            by = sp.getY() - my;
            bxvect = EngineUtils.cos(sp.getAng());
            byvect = EngineUtils.sin(sp.getAng());

            if (mxvect * bx + myvect * by >= 0) {
                if (bxvect * bx + byvect * by < 0) {
                    d = bxvect * by - byvect * bx;
                    if (klabs(d) < 65536 * 64) {
                        s.setAng(s.getAng() - (512 + (engine.krand() & 1024)));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static short furthestangle(int i, int angs) {
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return 0;
        }

        long greatestd = -(1 << 30);
        int angincs = (short) (2048 / angs);

        if (s.getPicnum() != APLAYER) {
            if ((g_t[0] & 63) > 2) {
                return (short) (s.getAng() + 1024);
            }
        }

        int furthest_angle = 0;
        for (int j = s.getAng(); j < (2048 + s.getAng()); j += angincs) {
            engine.hitscan(s.getX(), s.getY(), s.getZ() - (8 << 8), s.getSectnum(), EngineUtils.sin((j + 512) & 2047), EngineUtils.sin(j & 2047), 0, pHitInfo, CLIPMASK1);

            long d = klabs(pHitInfo.hitx - s.getX()) + klabs(pHitInfo.hity - s.getY());
            if (d > greatestd) {
                greatestd = d;
                furthest_angle = j;
            }
        }
        return (short) (furthest_angle & 2047);
    }

    public static int furthestcanseepoint(int i, Sprite ts, int dax, int day) {
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return -1;
        }

        furthest_x = dax;
        furthest_y = day;

        if ((g_t[0] & 63) != 0) {
            return -1;
        }

        short angincs = 1024;
        if (ud.multimode >= 2 || ud.player_skill >= 3) {
            angincs = (short) (2048 / (1 + (engine.krand() & 1)));
        }

        for (int j = ts.getAng(); j < (2048 + ts.getAng()); j += (angincs - (engine.krand() & 511))) {
            engine.hitscan(ts.getX(), ts.getY(), ts.getZ() - (16 << 8), ts.getSectnum(), EngineUtils.sin((j + 512) & 2047), EngineUtils.sin(j & 2047), 16384 - (engine.krand() & 32767), pHitInfo, CLIPMASK1);

            long d = klabs(pHitInfo.hitx - ts.getX()) + klabs(pHitInfo.hity - ts.getY());
            long da = klabs(pHitInfo.hitx - s.getX()) + klabs(pHitInfo.hity - s.getY());

            if (d < da) {
                if (engine.cansee(pHitInfo.hitx, pHitInfo.hity, pHitInfo.hitz, pHitInfo.hitsect, s.getX(), s.getY(), s.getZ() - (16 << 8), s.getSectnum())) {
                    furthest_x = pHitInfo.hitx;
                    furthest_y = pHitInfo.hity;
                    return pHitInfo.hitsect;
                }
            }
        }
        return -1;
    }

    private static void alterang(Script con, int a) {
        int ticselapsed = (g_t[0]) & 31;

        int aang = g_sp.getAng();

        g_sp.setXvel(g_sp.getXvel() + (con.script[g_t[1]] - g_sp.getXvel()) / 5);
        if (g_sp.getZvel() < 648) {
            g_sp.setZvel(g_sp.getZvel() + ((con.script[g_t[1] + 1] << 4) - g_sp.getZvel()) / 5);
        }

        if ((a & seekplayer) != 0) {
            int j = ps[g_p].holoduke_on;
            Sprite shd = boardService.getSprite(j);

            if (shd != null && engine.cansee(shd.getX(), shd.getY(), shd.getZ(), shd.getSectnum(), g_sp.getX(), g_sp.getY(), g_sp.getZ(), g_sp.getSectnum())) {
                g_sp.setOwner(j);
            } else {
                g_sp.setOwner(ps[g_p].i);
            }

            int goalang = 0;
            Sprite spo = boardService.getSprite(g_sp.getOwner());
            if (spo != null) {
                if (spo.getPicnum() == APLAYER) {
                    goalang = EngineUtils.getAngle(hittype[g_i].lastvx - g_sp.getX(), hittype[g_i].lastvy - g_sp.getY());
                } else {
                    goalang = EngineUtils.getAngle(spo.getX() - g_sp.getX(), spo.getY() - g_sp.getY());
                }
            }

            if (g_sp.getXvel() != 0 && g_sp.getPicnum() != MOSQUITO) {
                int angdif = getincangle(aang, goalang);

                if (ticselapsed < 2) {
                    if (klabs(angdif) < 256) {
                        j = 128 - (engine.krand() & 256);
                        g_sp.setAng(g_sp.getAng() + j);
                        if (hits(g_i) < 844) {
                            g_sp.setAng(g_sp.getAng() - j);
                        }
                    }
                } else if (ticselapsed > 18 && ticselapsed < 26) // choose
                {
                    if (klabs(angdif >> 2) < 128) {
                        g_sp.setAng((short) goalang);
                    } else {
                        g_sp.setAng(g_sp.getAng() + (angdif >> 2));
                    }
                }
            } else {
                g_sp.setAng((short) goalang);
            }
        }

        if (ticselapsed < 1) {
            int j = 2;
            if ((a & furthestdir) != 0) {
                int goalang = furthestangle(g_i, j);
                g_sp.setAng((short) goalang);
                g_sp.setOwner(ps[g_p].i);
            }

            if ((a & fleeenemy) != 0) {
                int goalang = furthestangle(g_i, j);
                g_sp.setAng((short) goalang);
            }
        }
    }

    private static void move(Script con) {
        int l;
        int goalang, angdif;
        int daxvel;

        short a = g_sp.getHitag();

        if (a == -1) {
            a = 0;
        }

        g_t[0]++;

        if ((a & face_player) != 0) {
            if (ps[g_p].newowner >= 0) {
                goalang = EngineUtils.getAngle(ps[g_p].oposx - g_sp.getX(), ps[g_p].oposy - g_sp.getY());
            } else {
                goalang = EngineUtils.getAngle(ps[g_p].posx - g_sp.getX(), ps[g_p].posy - g_sp.getY());
            }
            angdif = (short) (getincangle(g_sp.getAng(), goalang) >> 2);
            if (angdif > -8 && angdif < 0) {
                angdif = 0;
            }
            g_sp.setAng(g_sp.getAng() + angdif);
        }

        if ((a & spin) != 0) {
            g_sp.setAng(g_sp.getAng() + (EngineUtils.sin(((g_t[0] << 3) & 2047)) >> 6));
        }

        if ((a & face_player_slow) != 0) {
            if (ps[g_p].newowner >= 0) {
                goalang = EngineUtils.getAngle(ps[g_p].oposx - g_sp.getX(), ps[g_p].oposy - g_sp.getY());
            } else {
                goalang = EngineUtils.getAngle(ps[g_p].posx - g_sp.getX(), ps[g_p].posy - g_sp.getY());
            }
            angdif = (short) (ksgn(getincangle(g_sp.getAng(), goalang)) << 5);
            if (angdif > -32 && angdif < 0) {
                angdif = 0;
                g_sp.setAng(goalang);
            }
            g_sp.setAng(g_sp.getAng() + angdif);
        }

        if ((a & antifaceplayerslow) != 0) {
            if (ps[g_p].newowner >= 0) {
                goalang = EngineUtils.getAngle(ps[g_p].oposx - g_sp.getX(), ps[g_p].oposy - g_sp.getY());
            } else {
                goalang = EngineUtils.getAngle(ps[g_p].posx - g_sp.getX(), ps[g_p].posy - g_sp.getY());
            }
            angdif = (short) (ksgn(getincangle(g_sp.getAng(), (goalang + 512) & kAngleMask)) << 5);
            if (angdif > -32 && angdif < 0) {
                angdif = 0;
                g_sp.setAng(goalang);
            }
            g_sp.setAng(g_sp.getAng() + angdif);
        }

        if ((a & jumptoplayer) == jumptoplayer) {
            if (g_sp.getPicnum() == DAISYMAE) {
                if (g_t[0] < 16) {
                    g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) / 40));
                }
            } else {
                if (g_t[0] < 16) {
                    g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) >> 5));
                }
            }
        }

        if ((a & justjump1) != 0) {
            if (g_sp.getPicnum() == JACKOLOPE) {
                if (g_t[0] < 8) {
                    g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) >> 5));
                }
            } else if (g_sp.getPicnum() == MAMAJACKOLOPE && g_t[0] < 8) {
                g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) / 35));
            }
        }

        if ((a & justjump2) != 0) {
            if (g_sp.getPicnum() == JACKOLOPE) {
                if (g_t[0] < 8) {
                    g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) / 24));
                }
            } else if (g_sp.getPicnum() == MAMAJACKOLOPE && g_t[0] < 8) {
                g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) / 28));
            }
        }

        if ((a & windang) != 0) {
            if (g_t[0] < 8) {
                g_sp.setZvel(g_sp.getZvel() - (EngineUtils.sin((512 + (g_t[0] << 4)) & 2047) / 24));
            }
        }

        if ((a & face_player_smart) != 0) {
            int newx = ps[g_p].posx + (ps[g_p].posxv / 768);
            int newy = ps[g_p].posy + (ps[g_p].posyv / 768);
            goalang = EngineUtils.getAngle(newx - g_sp.getX(), newy - g_sp.getY());
            angdif = (short) (getincangle(g_sp.getAng(), goalang) >> 2);
            if (angdif > -8 && angdif < 0) {
                angdif = 0;
            }
            g_sp.setAng(g_sp.getAng() + angdif);
        }

        if (g_t[1] == 0 || a == 0) {
            ILoc oldLoc = game.pInt.getsprinterpolate(g_i);
            if (oldLoc != null && ((badguy(g_sp) && g_sp.getExtra() <= 0) || (oldLoc.x != g_sp.getX()) || (oldLoc.y != g_sp.getY()))) {
                oldLoc.x = g_sp.getX();
                oldLoc.y = g_sp.getY();
                engine.setsprite(g_i, g_sp.getX(), g_sp.getY(), g_sp.getZ());
            }
            return;
        }

        if ((a & geth) != 0) {
            g_sp.setXvel(g_sp.getXvel() + ((con.script[g_t[1]] - g_sp.getXvel()) >> 1));
        }
        if ((a & getv) != 0) {
            g_sp.setZvel(g_sp.getZvel() + (((con.script[g_t[1] + 1] << 4) - g_sp.getZvel()) >> 1));
        }


        if ((a & dodgebullet) != 0) {
            dodge(g_sp);
        }

        if (g_sp.getPicnum() != APLAYER) {
            alterang(con, a);
        }

        if (g_sp.getXvel() > -6 && g_sp.getXvel() < 6) {
            g_sp.setXvel(0);
        }

        a = (short) (badguy(g_sp) ? 1 : 0);

        if (g_sp.getXvel() != 0 || g_sp.getZvel() != 0) {
            if ((g_sp.getPicnum() == MOSQUITO) && g_sp.getExtra() > 0) {
                if (g_sp.getZvel() > 0) {
                    hittype[g_i].floorz = l = engine.getflorzofslope(g_sp.getSectnum(), g_sp.getX(), g_sp.getY());
                    if (g_sp.getZ() > (l - (30 << 8))) {
                        g_sp.setZ(l - (30 << 8));
                    }
                } else {
                    hittype[g_i].ceilingz = l = engine.getceilzofslope(g_sp.getSectnum(), g_sp.getX(), g_sp.getY());
                    if ((g_sp.getZ() - l) < (50 << 8)) {
                        g_sp.setZ(l + (50 << 8));
                        g_sp.setZvel(0);
                    }
                }
            }

            if (g_sp.getZvel() > 0 && hittype[g_i].floorz < g_sp.getZ()) {
                g_sp.setZ(hittype[g_i].floorz);
            }
            if (g_sp.getZvel() < 0) {
                l = engine.getceilzofslope(g_sp.getSectnum(), g_sp.getX(), g_sp.getY());
                if ((g_sp.getZ() - l) < (66 << 8)) {
                    g_sp.setZ(l + (66 << 8));
                    g_sp.setZvel(g_sp.getZvel() >> 1);
                }
            }

            if (g_sp.getPicnum() == APLAYER) {
                if ((g_sp.getZ() - hittype[g_i].ceilingz) < (32 << 8)) {
                    g_sp.setZ(hittype[g_i].ceilingz + (32 << 8));
                }
            }

            daxvel = g_sp.getXvel();
            angdif = g_sp.getAng();

            if (a != 0) {
                if (g_x < 960 && g_sp.getXrepeat() > 16) {
                    daxvel = -(1024 - g_x);
                    angdif = EngineUtils.getAngle(ps[g_p].posx - g_sp.getX(), ps[g_p].posy - g_sp.getY());

                    if (g_x < 512) {
                        ps[g_p].posxv = 0;
                        ps[g_p].posyv = 0;
                    } else {
                        ps[g_p].posxv = mulscale(ps[g_p].posxv, con.dukefriction - 0x2000, 16);
                        ps[g_p].posyv = mulscale(ps[g_p].posyv, con.dukefriction - 0x2000, 16);
                    }
                } else if (g_sp.getPicnum() != MOSQUITO && g_sp.getPicnum() != 5501 && g_sp.getPicnum() != UFO1 && g_sp.getPicnum() != UFO2 && g_sp.getPicnum() != UFO3 && g_sp.getPicnum() != UFO4 && g_sp.getPicnum() != UFO5 && g_sp.getPicnum() != MINIONUFO) {
                    ILoc oldLoc = game.pInt.getsprinterpolate(g_i);
                    if ((oldLoc != null && oldLoc.z != g_sp.getZ()) || (ud.multimode < 2 && ud.player_skill < 2)) {
                        if ((g_t[0] & 1) != 0 || ps[g_p].actorsqu == g_i) {
                            return;
                        } else {
                            daxvel <<= 1;
                        }
                    } else {
                        if ((g_t[0] & 3) != 0 || ps[g_p].actorsqu == g_i) {
                            return;
                        } else {
                            daxvel <<= 2;
                        }
                    }
                }
            }

            Sector sec = boardService.getSector(g_sp.getSectnum());
            if (sec != null && sec.getLotag() == 1) {
                if (currentGame.getCON().type == RRRA) {
                    switch (g_sp.getPicnum()) {
                        case BIKERRIDE:
                        case BIKERRIDE + 1:
                        case BIKERRIDEDAISY:
                            daxvel >>= 1;
                            break;
                    }
                }
            } else {
                switch (g_sp.getPicnum()) {
                    case MINIONAIRBOAT:
                    case HULKAIRBOAT:
                    case DAISYAIRBOAT:
                        daxvel >>= 1;
                        break;
                }
            }

            hittype[g_i].movflag = movesprite(g_i, (daxvel * (EngineUtils.sin((angdif + 512) & 2047))) >> 14, (daxvel * (EngineUtils.sin(angdif & 2047))) >> 14, g_sp.getZvel(), CLIPMASK0);
        }

        Sector sec = boardService.getSector(g_sp.getSectnum());
        if (sec != null && a != 0) {
            if ((sec.getCeilingstat() & 1) != 0) {
                if (shadeEffect.getBit(g_sp.getSectnum())) {
                    g_sp.setShade(g_sp.getShade() + ((16 - g_sp.getShade()) >> 1));
                } else {
                    g_sp.setShade(g_sp.getShade() + ((sec.getCeilingshade() - g_sp.getShade()) >> 1));
                }
            } else {
                g_sp.setShade(g_sp.getShade() + ((sec.getFloorshade() - g_sp.getShade()) >> 1));
            }

            if (sec.getFloorpicnum() == MIRROR) {
                engine.deletesprite(g_i);
            }
        }
    }

    private static void parseifelse(Script con, boolean condition) {
        if (condition) {
            insptr += 2;
            parse(con);
        } else {
            insptr = con.script[insptr + 1];
            if (con.script[insptr] == 10) {
                insptr += 2;
                parse(con);
            }
        }
    }

    private static boolean parse(Script con) {
        int j, l;
        int s;
        boolean cansee;

        if (killit_flag != 0) {
            return true;
        }

        switch (con.script[insptr]) {
            case 3:
                insptr++;
                parseifelse(con, rnd(con.script[insptr]));
                break;
            case 45:

                if (g_x > 1024) {
                    int temphit, sclip, angdif;

                    if (badguy(g_sp) && g_sp.getXrepeat() > 56) {
                        sclip = 3084;
                        angdif = 48;
                    } else {
                        sclip = 768;
                        angdif = 16;
                    }

                    j = hitasprite(g_i);
                    temphit = pHitInfo.hitsprite;
                    if (j == (1 << 30)) {
                        parseifelse(con, true);
                        break;
                    }
                    if (j > sclip) {
                        Sprite hsp = boardService.getSprite(temphit);
                        if (hsp != null && hsp.getPicnum() == g_sp.getPicnum()) {
                            j = 0;
                        } else {
                            g_sp.setAng(g_sp.getAng() + angdif);
                            j = hitasprite(g_i);
                            temphit = pHitInfo.hitsprite;
                            hsp = boardService.getSprite(temphit); // Attension!
                            g_sp.setAng(g_sp.getAng() - angdif);
                            if (j > sclip) {
                                if (hsp != null && hsp.getPicnum() == g_sp.getPicnum()) {
                                    j = 0;
                                } else {
                                    g_sp.setAng(g_sp.getAng() - angdif);
                                    j = hitasprite(g_i);
                                    temphit = pHitInfo.hitsprite;
                                    hsp = boardService.getSprite(temphit); // Attension!
                                    g_sp.setAng(g_sp.getAng() + angdif);
                                    if (j > 768) {
                                        if (hsp != null && hsp.getPicnum() == g_sp.getPicnum()) {
                                            j = 0;
                                        } else {
                                            j = 1;
                                        }
                                    } else {
                                        j = 0;
                                    }
                                }
                            } else {
                                j = 0;
                            }
                        }
                    } else {
                        j = 0;
                    }
                } else {
                    j = 1;
                }

                parseifelse(con, j != 0);
                break;
            case 91: {
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                cansee = engine.cansee(g_sp.getX(),
                        g_sp.getY(),
                        g_sp.getZ() - ((engine.krand() & 41) << 8),
                        g_sp.getSectnum(),
                        ps[g_p].posx,
                        ps[g_p].posy,
                        ps[g_p].posz,
                        psp.getSectnum());
                parseifelse(con, cansee);
                if (cansee) {
                    hittype[g_i].timetosleep = SLEEPTIME;
                }
                break;
            }
            case 110: {
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                cansee = engine.cansee(g_sp.getX(), g_sp.getY(), g_sp.getZ(), g_sp.getSectnum(), ps[g_p].posx, ps[g_p].posy, ps[g_p].posz, psp.getSectnum());
                parseifelse(con, cansee);
                if (cansee) {
                    hittype[g_i].timetosleep = SLEEPTIME;
                }
                break;
            }
            case 49:
                parseifelse(con, hittype[g_i].actorstayput == -1);
                break;
            case 5: {
                Sprite spr;
                if (ps[g_p].holoduke_on >= 0) {
                    spr = boardService.getSprite(ps[g_p].holoduke_on);
                    cansee = spr != null && engine.cansee(g_sp.getX(), g_sp.getY(), g_sp.getZ() - (engine.krand() & ((32 << 8) - 1)), g_sp.getSectnum(), spr.getX(), spr.getY(), spr.getZ(), spr.getSectnum());
                    if (!cansee) {
                        spr = boardService.getSprite(ps[g_p].i);
                    }
                } else {
                    spr = boardService.getSprite(ps[g_p].i);
                }

                if (spr == null) {
                    break;
                }

                cansee = engine.cansee(g_sp.getX(), g_sp.getY(), g_sp.getZ() - (engine.krand() & ((47 << 8))), g_sp.getSectnum(), spr.getX(), spr.getY(), spr.getZ() - (24 << 8), spr.getSectnum());

                if (!cansee) {
                    j = furthestcanseepoint(g_i, spr, hittype[g_i].lastvx, hittype[g_i].lastvy);

                    hittype[g_i].lastvx = furthest_x;
                    hittype[g_i].lastvy = furthest_y;

                    cansee = j != -1;
                } else {
                    hittype[g_i].lastvx = spr.getX();
                    hittype[g_i].lastvy = spr.getY();
                }

                if (cansee && (g_sp.getStatnum() == 1 || g_sp.getStatnum() == 6)) {
                    hittype[g_i].timetosleep = SLEEPTIME;
                }

                parseifelse(con, cansee);
                break;
            }

            case 6:
                parseifelse(con, ifhitbyweapon(g_i) >= 0);
                break;
            case 27:
                parseifelse(con, ifsquished(g_i, g_p));
                break;
            case 26: {
                j = g_sp.getExtra();
                if (g_sp.getPicnum() == APLAYER) {
                    j--;
                }
                parseifelse(con, j < 0);
            }
            break;
            case 24:
                insptr++;
                g_t[5] = con.script[insptr];
                g_t[4] = con.script[g_t[5]];       // Action
                g_t[1] = con.script[g_t[5] + 1];       // move
                g_sp.setHitag((short) (con.script[g_t[5] + 2]));    // Ai
                g_t[0] = g_t[2] = g_t[3] = 0;
                if ((g_sp.getHitag() & random_angle) != 0) {
                    g_sp.setAng((short) (engine.krand() & 2047));
                }
                insptr++;
                break;
            case 7:
                insptr++;
                g_t[2] = 0;
                g_t[3] = 0;
                g_t[4] = con.script[insptr];
                insptr++;
                break;

            case 8:
                insptr++;
                parseifelse(con, g_x < con.script[insptr]);
                if (g_x > MAXSLEEPDIST && hittype[g_i].timetosleep == 0) {
                    hittype[g_i].timetosleep = SLEEPTIME;
                }
                break;
            case 9:
                insptr++;
                parseifelse(con, g_x > con.script[insptr]);
                if (g_x > MAXSLEEPDIST && hittype[g_i].timetosleep == 0) {
                    hittype[g_i].timetosleep = SLEEPTIME;
                }
                break;
            case 10:
                insptr = con.script[insptr + 1];
                break;
            case 100:
                insptr++;
                g_sp.setExtra(g_sp.getExtra() + con.script[insptr]);
                insptr++;
                break;
            case 11:
                insptr++;
                g_sp.setExtra((short) con.script[insptr]);
                insptr++;
                break;
            case 94:
                insptr++;

                if (ud.coop >= 1 && ud.multimode > 1) {
                    if (con.script[insptr] == 0) {
                        for (j = 0; j < ps[g_p].weapreccnt; j++) {
                            if (ps[g_p].weaprecs[j] == g_sp.getPicnum()) {
                                break;
                            }
                        }

                        parseifelse(con, j < ps[g_p].weapreccnt && g_sp.getOwner() == g_i);
                    } else if (ps[g_p].weapreccnt < 16) {
                        ps[g_p].weaprecs[ps[g_p].weapreccnt++] = g_sp.getPicnum();
                        parseifelse(con, g_sp.getOwner() == g_i);
                    }
                } else {
                    parseifelse(con, false);
                }
                break;
            case 95:
                insptr++;
                if (g_sp.getPicnum() == APLAYER) {
                    g_sp.setPal(ps[g_sp.getYvel()].palookup);
                } else {
                    g_sp.setPal((short) hittype[g_i].tempang);
                }
                hittype[g_i].tempang = 0;
                break;
            case 104:
                insptr++;
                checkweapons(ps[g_sp.getYvel()]);
                break;
            case 106:
                insptr++;
                break;
            case 97:
                insptr++;
                if (Sound[g_sp.getYvel()].getSoundOwnerCount() == 0) {
                    spritesound(g_sp.getYvel(), g_i);
                }
                break;
            case 96:
                insptr++;

                if (ud.multimode > 1 && g_sp.getPicnum() == APLAYER) {
                    if (ps[otherp].quick_kick == 0) {
                        ps[otherp].quick_kick = 14;
                    }
                } else if (g_sp.getPicnum() != APLAYER && ps[g_p].quick_kick == 0) {
                    ps[g_p].quick_kick = 14;
                }
                break;
            case 28:
                insptr++;

                j = ((con.script[insptr]) - g_sp.getXrepeat()) << 1;
                g_sp.setXrepeat(g_sp.getXrepeat() + ksgn(j));

                insptr++;

                if ((g_sp.getPicnum() == APLAYER && g_sp.getYrepeat() < 36) || con.script[insptr] < g_sp.getYrepeat() || ((g_sp.getYrepeat() * (engine.getTile(g_sp.getPicnum()).getHeight() + 8)) << 2) < (hittype[g_i].floorz - hittype[g_i].ceilingz)) {
                    j = ((con.script[insptr]) - g_sp.getYrepeat()) << 1;
                    if (klabs(j) != 0) {
                        g_sp.setYrepeat(g_sp.getYrepeat() + ksgn(j));
                    }
                }

                insptr++;

                break;
            case 99:
                insptr++;
                g_sp.setXrepeat((short) con.script[insptr]);
                insptr++;
                g_sp.setYrepeat((short) con.script[insptr]);
                insptr++;
                break;
            case 13:
                insptr++;
                shoot(g_i, con.script[insptr]);
                insptr++;
                break;
            case 127:
                insptr++;
                parseifelse(con, ambienttype[g_sp.getAng()] == con.script[insptr]);
                break;
            case 128:
                insptr++;
                if (con.script[insptr] == 1) {
                    parseifelse(con, ambienthitag[g_sp.getAng()] < g_x);
                } else if (con.script[insptr] == 0) {
                    parseifelse(con, ambienthitag[g_sp.getAng()] > g_x);
                }
                insptr++;
                return false;
            case 126:
                spritesound(ambienttype[g_sp.getAng()], g_i);
                insptr++;
                break;
            case 125:
                if (Sound[ambienttype[g_sp.getAng()]].getSoundOwnerCount() == 0) {
                    spritesound(ambienttype[g_sp.getAng()], g_i);
                }
                insptr++;
                break;
            case 87:
                insptr++;
                if (Sound[con.script[insptr]].getSoundOwnerCount() == 0) {
                    spritesound(con.script[insptr], g_i);
                }
                insptr++;
                break;
            case 89:
                insptr++;
                if (Sound[con.script[insptr]].getSoundOwnerCount() > 0) {
                    stopsound(con.script[insptr]);
                }
                insptr++;
                break;
            case 92:
                insptr++;
                if (g_p == screenpeek || ud.coop == 1) {
                    spritesound(con.script[insptr], ps[screenpeek].i);
                }
                insptr++;
                break;
            case 124:
                insptr++;
                LeaveMap();
                ud.level_number++;
                break;
            case 119:
                insptr++;
                parseifelse(con, g_sp.getExtra() > con.script[insptr]);
                break;
            case 120:
                insptr++;
                parseifelse(con, g_sp.getExtra() < con.script[insptr]);
                break;
            case 15:
                insptr++;
                spritesound((short) con.script[insptr], g_i);
                insptr++;
                break;
            case 84:
                insptr++;
                ps[g_p].tipincs = 26;
                break;
            case 112:
            case 111:
                int d = g_sp.getDetail();
                if (d == 1) {
                    g_sp.setDetail(g_sp.getDetail() + 1);
                }
                parseifelse(con, d == 1);
                break;
            case 123: {
                insptr++;
                ListNode<Sprite> node = boardService.getSectNode(g_sp.getSectnum());
                while (node != null) {
                    j = node.getIndex();
                    if (node.get().getPicnum() == 36) {
                        hittype[j].picnum = SHOTSPARK1;
                        hittype[j].extra = 1;
                    }
                    node = node.getNext();
                }
                return false;
            }
            case 16: {
                insptr++;
                g_sp.setXoffset(0);
                g_sp.setYoffset(0);
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec == null) {
                    break;
                }

                if (sec.getLotag() == 800) {
                    if (g_sp.getPicnum() == AMMO) {
                        engine.deletesprite(g_i);
                        break;
                    }

                    if (g_sp.getPicnum() != APLAYER && (badguy(g_sp) || g_sp.getPicnum() == HEN || g_sp.getPicnum() == COW || g_sp.getPicnum() == PIG || g_sp.getPicnum() == DOGRUN) && g_sp.getDetail() < 128) {
                        g_sp.setZvel(8000);
                        g_sp.setExtra(0);
                        g_sp.setZ(hittype[g_i].floorz - 256);
                        g_sp.setDetail(g_sp.getDetail() + 1);
                    } else if (g_sp.getPicnum() != APLAYER) {
                        if (g_sp.getDetail() != 0) {
                            break;
                        }
                        engine.deletesprite(g_i);
                        break;
                    }
                    hittype[g_i].picnum = SHOTSPARK1;
                    hittype[g_i].extra = 1;
                }

                long c;

                if (floorspace(g_sp.getSectnum())) {
                    c = 0;
                } else {
                    if (ceilingspace(g_sp.getSectnum()) || sec.getLotag() == 2) {
                        c = con.gc / 6;
                    } else {
                        c = con.gc;
                    }
                }

                if (hittype[g_i].cgg <= 0 || (sec.getFloorstat() & 2) != 0) {
                    getglobalz(g_i);
                    hittype[g_i].cgg = 6;
                } else {
                    hittype[g_i].cgg--;
                }

                if (g_sp.getZ() < (hittype[g_i].floorz - FOURSLEIGHT)) {
                    g_sp.setZvel((int) (g_sp.getZvel() + c));
                    g_sp.setZ(g_sp.getZ() + g_sp.getZvel());

                    if (g_sp.getZvel() > 6144) {
                        g_sp.setZvel(6144);
                    }
                } else {
                    g_sp.setZ(hittype[g_i].floorz - FOURSLEIGHT);

                    if (badguy(g_sp) || (g_sp.getPicnum() == APLAYER && g_sp.getOwner() >= 0)) {

                        if (g_sp.getZvel() > 3084 && g_sp.getExtra() <= 1) {
                            if (g_sp.getPal() != 1 && g_sp.getPicnum() != MOSQUITO) {
                                if (g_sp.getPicnum() != APLAYER || g_sp.getExtra() <= 0) {
                                    guts(g_sp, JIBS6, 15, g_p);
                                    spritesound(SQUISHED, g_i);
                                    spawn(g_i, BLOODPOOL);
                                }
                            }

                            hittype[g_i].picnum = SHOTSPARK1;
                            hittype[g_i].extra = 1;
                            g_sp.setZvel(0);
                        } else if (g_sp.getZvel() > 2048 && sec.getLotag() != 1) {
                            short pushsect = g_sp.getSectnum();
                            engine.pushmove(g_sp.getX(), g_sp.getY(), g_sp.getZ(), pushsect, 128, (4 << 8), (4 << 8), CLIPMASK0);

                            g_sp.setX(pushmove_x);
                            g_sp.setY(pushmove_y);
                            g_sp.setZ(pushmove_z);
                            pushsect = pushmove_sectnum;
                            if (pushsect != g_sp.getSectnum() && pushsect != -1) {
                                engine.changespritesect(g_i, pushsect);
                            }

                            spritesound(THUD, g_i);
                        }
                    }
                    if (sec.getLotag() == 1) {
                        if (g_sp.getPicnum() != MOSQUITO) {
                            g_sp.setZ(g_sp.getZ() + (24 << 8));
                        }
                    } else {
                        g_sp.setZvel(0);
                    }
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
                if (ps[g_p].ammo_amount[con.script[insptr]] >= con.max_ammo_amount[con.script[insptr]]) {
                    killit_flag = 2;
                    break;
                }
                addammo(con.script[insptr], ps[g_p], con.script[insptr + 1]);
                if (ps[g_p].curr_weapon == KNEE_WEAPON) {
                    if (ps[g_p].gotweapon[con.script[insptr]]) {
                        addweapon(ps[g_p], con.script[insptr]);
                    }
                }
                insptr += 2;
                break;
            case 86:
            case 103:
            case 102:
                insptr++;
                lotsofmoney(g_sp, con.script[insptr]);
                insptr++;
                break;
            case 105:
                insptr++;
                hittype[g_i].timetosleep = (short) con.script[insptr];
                insptr++;
                break;
            case 88:
                insptr++;
                if (g_sp.getDetail() < 1 || g_sp.getDetail() == 128) {
                    if (checkaddkills(g_sp)) {
                        ps[connecthead].actors_killed += (short) con.script[insptr];
                        if (ud.coop == 1) {
                            ps[g_p].frag += (short) con.script[insptr];
                        }
                    }
                }
                hittype[g_i].actorstayput = -1;
                insptr++;
                break;
            case 93:
                insptr++;
                spriteglass(g_i, con.script[insptr]);
                insptr++;
                break;
            case 22:
                insptr++;
                killit_flag = 1;
                break;
            case 23:
                insptr++;
                if (!ps[g_p].gotweapon[con.script[insptr]]) {
                    addweapon(ps[g_p], con.script[insptr]);
                } else if (ps[g_p].ammo_amount[con.script[insptr]] >= con.max_ammo_amount[con.script[insptr]]) {
                    killit_flag = 2;
                    break;
                }
                addammo(con.script[insptr], ps[g_p], con.script[insptr + 1]);
                if (ps[g_p].curr_weapon == KNEE_WEAPON) {
                    if (ps[g_p].gotweapon[con.script[insptr]]) {
                        addweapon(ps[g_p], con.script[insptr]);
                    }
                }
                insptr += 2;
                break;
            case 68:
                insptr++;
                Console.out.println("" + con.script[insptr]);
                insptr++;
                break;
            case 69:
                insptr++;
                ps[g_p].timebeforeexit = (short) con.script[insptr];
                ps[g_p].customexitsound = -1;
                ud.eog = 1;
                insptr++;
                break;
            case 113: {
                insptr++;
                ps[g_p].alcohol_amount += (short) con.script[insptr];
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                int extra = psp.getExtra();
                if (extra != 0) {
                    extra += con.script[insptr];
                }

                if (2 * con.max_player_health < extra) {
                    extra = (2 * con.max_player_health);
                }
                if (extra < 0) {
                    extra = 0;
                }

                if (!ud.god) {
                    if (con.script[insptr] > 0) {
                        if (extra - con.script[insptr] < (con.max_player_health >> 2) && (con.max_player_health >> 2) <= extra) {
                            spritesound(229, ps[g_p].i);
                        }
                        ps[g_p].last_extra = (short) extra;
                    }
                    psp.setExtra((short) extra);
                }

                if (ps[g_p].alcohol_amount > 100) {
                    ps[g_p].alcohol_amount = 100;
                }

                if (psp.getExtra() >= con.max_player_health) {
                    psp.setExtra((short) con.max_player_health);
                    ps[g_p].last_extra = (short) con.max_player_health;
                }
                insptr++;
                break;
            }
            case 117:
                insptr++;
                movesprite(g_i, (EngineUtils.sin((g_sp.getAng() + 1024) & 2047)) >> 10, (EngineUtils.sin((g_sp.getAng() + 512) & 2047)) >> 10, g_sp.getZvel(), CLIPMASK0);
                break;
            case 118:
                insptr++;
                movesprite(g_i, (EngineUtils.sin(g_sp.getAng() & 2047)) >> 10, (EngineUtils.sin((g_sp.getAng() - 512) & 2047)) >> 10, g_sp.getZvel(), CLIPMASK0);
                break;
            case 116: {
                insptr++;
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                Sector psec = boardService.getSector(psp.getSectnum());
                if (psec != null) {
                    ps[g_p].posz = psec.getCeilingz();
                    psp.setZ(ps[g_p].posz);
                }
                return false;
            }
            case 115: {
                insptr++;
                int sect = g_sp.getSectnum();
                int hitag = 0;
                int lotag = 0;

                ListNode<Sprite> node = boardService.getSectNode(sect);
                while (node != null) {
                    Sprite sp = node.get();
                    if (sp.getPicnum() == 63) {
                        lotag = sp.getLotag();
                        if (sp.getHitag() != 0) {
                            hitag = sp.getHitag();
                        }
                    }
                    node = node.getNext();
                }

                node = boardService.getStatNode(100);
                while (node != null) {
                    Sprite sp = node.get();
                    if (hitag != 0 && hitag == sp.getHitag()) {
                        ListNode<Sprite> ns = boardService.getSectNode(sp.getSectnum());
                        while (ns != null) {
                            s = ns.getIndex();
                            Sprite sp2 = ns.get();
                            if (sp2.getPicnum() == 36) {
                                hittype[s].picnum = SHOTSPARK1;
                                hittype[s].extra = 1;
                            }
                            ns = ns.getNext();
                        }
                    }

                    if (sp.getSectnum() != g_sp.getSectnum()) {
                        if (lotag == sp.getLotag()) {
                            Sector sec = boardService.getSector(g_sp.getSectnum());
                            Sector sec2 = boardService.getSector(sp.getSectnum());

                            if (sec != null && sec2 != null) {
                                int spwall = sec2.getWallptr();
                                for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal = wn.get();
                                    Wall wal2 = boardService.getWall(spwall++);
                                    if (wal2 != null) {
                                        wal.setPicnum(wal2.getPicnum());
                                        wal.setOverpicnum(wal2.getOverpicnum());
                                        wal.setShade(wal2.getShade());
                                        wal.setXrepeat(wal2.getXrepeat());
                                        wal.setYrepeat(wal2.getYrepeat());
                                        wal.setXpanning(wal2.getXpanning());
                                        wal.setYpanning(wal2.getYpanning());

                                        if (currentGame.getCON().type == RRRA) {
                                            Wall nwal = boardService.getWall(wal.getNextwall());
                                            if (nwal != null) {
                                                wal.setCstat(0);
                                                nwal.setCstat(0);
                                            }
                                        }
                                    }
                                }

                                sec.setFloorz(sec2.getFloorz());
                                sec.setCeilingz(sec2.getCeilingz());
                                sec.setCeilingstat(sec2.getCeilingstat());
                                sec.setFloorstat(sec2.getFloorstat());
                                sec.setCeilingpicnum(sec2.getCeilingpicnum());
                                sec.setCeilingheinum(sec2.getCeilingheinum());
                                sec.setCeilingshade(sec2.getCeilingshade());
                                sec.setCeilingpal(sec2.getCeilingpal());
                                sec.setCeilingxpanning(sec2.getCeilingxpanning());
                                sec.setCeilingypanning(sec2.getCeilingypanning());
                                sec.setFloorpicnum(sec2.getFloorpicnum());
                                sec.setFloorheinum(sec2.getFloorheinum());
                                sec.setFloorshade(sec2.getFloorshade());
                                sec.setFloorpal(sec2.getFloorpal());
                                sec.setFloorxpanning(sec2.getFloorxpanning());
                                sec.setFloorypanning(sec2.getFloorypanning());
                                sec.setVisibility(sec2.getVisibility());
                                sec.setFiller(sec2.getFiller());
                                sec.setLotag(sec2.getLotag());
                                sec.setHitag(sec2.getHitag());
                                sec.setExtra(sec2.getExtra());
                            }
                        }
                    }
                    node = node.getNext();
                }

                node = boardService.getSectNode(sect);
                while (node != null) {
                    Sprite sp = node.get();

                    ListNode<Sprite> next = node.getNext();
                    if (sp.getPicnum() != 63 && sp.getPicnum() != DESTRUCTO && sp.getPicnum() != COOT && sp.getPicnum() != TORNADO && sp.getPicnum() != APLAYER) {
                        engine.deletesprite(node.getIndex());
                    }

                    node = next;
                }
                break;
            }
            case 114: {
                insptr++;
                ps[g_p].gut_amount += (short) con.script[insptr];
                ps[g_p].alcohol_amount -= (short) con.script[insptr];
                if (ps[g_p].gut_amount > 100) {
                    ps[g_p].gut_amount = 100;
                }

                if (ps[g_p].alcohol_amount < 0) {
                    ps[g_p].alcohol_amount = 0;
                }
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                int extra = psp.getExtra();
                if (g_sp.getPicnum() == ECLAIRHEALTH) {
                    if (extra > 0) {
                        extra += con.script[insptr];
                    }
                    if (2 * con.max_player_health < extra) {
                        extra = (2 * con.max_player_health);
                    }
                } else {
                    if (extra > con.max_player_health && con.script[insptr] > 0) {
                        insptr++;
                        return false;
                    }
                    if (extra > 0) {
                        extra = (extra + 3 * con.script[insptr]);
                    }
                    if (extra > con.max_player_health && con.script[insptr] > 0) {
                        extra = con.max_player_health;
                    }
                }
                if (extra < 0) {
                    extra = 0;
                }
                if (!ud.god) {
                    if (con.script[insptr] > 0) {
                        if (extra - con.script[insptr] < (con.max_player_health >> 2) && (con.max_player_health >> 2) <= extra) {
                            spritesound(229, ps[g_p].i);
                        }
                        ps[g_p].last_extra = (short) extra;
                    }
                    psp.setExtra((short) extra);
                }
                insptr++;
                break;
            }
            case 25: {
                insptr++;

                if (ps[g_p].newowner >= 0) {
                    ps[g_p].newowner = -1;
                    ps[g_p].posx = ps[g_p].oposx;
                    ps[g_p].posy = ps[g_p].oposy;
                    ps[g_p].posz = ps[g_p].oposz;
                    ps[g_p].ang = ps[g_p].oang;
                    ps[g_p].cursectnum = engine.updatesector(ps[g_p].posx, ps[g_p].posy, ps[g_p].cursectnum);
                    setpal(ps[g_p]);

                    ListNode<Sprite> node = boardService.getStatNode(1);
                    while (node != null) {
                        Sprite sp = node.get();
                        if (sp.getPicnum() == CAMERA1) {
                            sp.setYvel(0);
                        }
                        node = node.getNext();
                    }
                }

                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                j = psp.getExtra();

                if (g_sp.getPicnum() != ECLAIRHEALTH) {
                    if (j > con.max_player_health && con.script[insptr] > 0) {
                        insptr++;
                        break;
                    } else {
                        if (j > 0) {
                            j += con.script[insptr];
                        }
                        if (j > con.max_player_health && con.script[insptr] > 0) {
                            j = con.max_player_health;
                        }
                    }
                } else {
                    if (j > 0) {
                        j += con.script[insptr];
                    }
                    if (j > (con.max_player_health << 1)) {
                        j = (con.max_player_health << 1);
                    }
                }

                if (j < 0) {
                    j = 0;
                }

                if (!ud.god) {
                    if (con.script[insptr] > 0) {
                        if ((j - con.script[insptr]) < (con.max_player_health >> 2) && j >= (con.max_player_health >> 2)) {
                            spritesound(DUKE_GOTHEALTHATLOW, ps[g_p].i);
                        }

                        ps[g_p].last_extra = (short) j;
                    }

                    psp.setExtra((short) j);
                }

                insptr++;
                break;
            }
            case 17: {
                int tempscrptr = insptr + 2;
                insptr = con.script[insptr + 1];
                while (true) {
                    if (parse(con)) {
                        break;
                    }
                }
                insptr = tempscrptr;
            }
            break;
            case 29:
                insptr++;
                while (true) {
                    if (parse(con)) {
                        break;
                    }
                }
                break;
            case 32:
                g_t[0] = 0;
                insptr++;
                g_t[1] = con.script[insptr];
                insptr++;
                g_sp.setHitag((short) con.script[insptr]);
                insptr++;
                if ((g_sp.getHitag() & random_angle) != 0) {
                    g_sp.setAng((short) (engine.krand() & 2047));
                }
                break;
            case 31: {
                insptr++;
                Sector g_sec = boardService.getSector(g_sp.getSectnum());
                if (g_sec != null) {
                    spawn(g_i, con.script[insptr]);
                }
                insptr++;
                break;
            }
            case 33:
            case 59:
                insptr++;
                parseifelse(con, hittype[g_i].picnum == con.script[insptr]);
                break;
            case 21:
                insptr++;
                parseifelse(con, g_t[5] == con.script[insptr]);
                break;
            case 34:
                insptr++;
                parseifelse(con, g_t[4] == con.script[insptr]);
                break;
            case 35:
                insptr++;
                parseifelse(con, g_t[2] >= con.script[insptr]);
                break;
            case 36:
                insptr++;
                g_t[2] = 0;
                break;
            case 37: {
                int dnum;

                insptr++;
                dnum = con.script[insptr];
                insptr++;

                Sector g_sec = boardService.getSector(g_sp.getSectnum());
                if (g_sec != null) {
                    for (j = (con.script[insptr]) - 1; j >= 0; j--) {
                        if (dnum == SCRAP1) {
                            s = 0;
                        } else {
                            s = (short) (engine.krand() % 3);
                        }

                        int vz = -(engine.krand() & 2047);
                        int ve = (engine.krand() & 127) + 32;
                        int va = engine.krand() & 2047;
                        int vy = 32 + (engine.krand() & 15);
                        int vx = 32 + (engine.krand() & 15);
                        int sz = g_sp.getZ() - (8 << 8) - (engine.krand() & 8191);
                        int sy = g_sp.getY() + (engine.krand() & 255) - 128;
                        int sx = g_sp.getX() + (engine.krand() & 255) - 128;

                        l = EGS(g_sp.getSectnum(), sx, sy, sz, dnum + s, g_sp.getShade(), vx, vy, va, ve, vz, g_i, (short) 5);
                        Sprite lsp = boardService.getSprite(l);
                        if (lsp != null) {
                            if (dnum == SCRAP1) {
                                lsp.setYvel(weaponsandammosprites[j % 14]);
                            } else {
                                lsp.setYvel(-1);
                            }
                            lsp.setPal(g_sp.getPal());
                        }
                    }
                }
                insptr++;
            }
            break;
            case 52:
                insptr++;
                g_t[0] = (short) con.script[insptr];
                insptr++;
                break;
            case 101:
                insptr++;
                g_sp.setCstat(g_sp.getCstat() | (short) con.script[insptr]);
                insptr++;
                break;
            case 40:
                insptr++;
                g_sp.setCstat((short) con.script[insptr]);
                insptr++;
                break;
            case 41:
                insptr++;
                parseifelse(con, g_t[1] == con.script[insptr]);
                break;
            case 42:
                insptr++;
                if (ud.multimode < 2) {
                    if (DemoScreen.isDemoPlaying()) {
                        break;
                    }

                    if (!gGameScreen.enterlevel(gGameScreen.getTitle())) {
                        game.show();
                        return false;
                    }
                    killit_flag = 2;
                } else {
                    pickrandomspot(g_p);
                    game.pInt.clearspriteinterpolate(g_i);
                    game.pInt.setsprinterpolate(g_i, boardService.getSprite(ps[g_p].i));
                    g_sp.setX(ps[g_p].bobposx = ps[g_p].oposx = ps[g_p].posx);
                    g_sp.setY(ps[g_p].bobposy = ps[g_p].oposy = ps[g_p].posy);
                    g_sp.setZ(ps[g_p].oposz = ps[g_p].posz);
                    ps[g_p].cursectnum = engine.updatesector(ps[g_p].posx, ps[g_p].posy, ps[g_p].cursectnum);
                    engine.setsprite(ps[g_p].i, ps[g_p].posx, ps[g_p].posy, ps[g_p].posz + PHEIGHT);
                    g_sp.setCstat(257);

                    g_sp.setShade(-12);
                    g_sp.setClipdist(32);
                    g_sp.setXrepeat(24);
                    g_sp.setYrepeat(17);
                    g_sp.setOwner(g_i);
                    g_sp.setXoffset(0);
                    g_sp.setPal(ps[g_p].palookup);

                    ps[g_p].last_extra = (short) con.max_player_health;
                    g_sp.setExtra(con.max_player_health);
                    ps[g_p].wantweaponfire = -1;
                    ps[g_p].horiz = 100;
                    ps[g_p].on_crane = -1;
                    ps[g_p].frag_ps = g_p;
                    ps[g_p].horizoff = 0;
                    ps[g_p].opyoff = 0;
                    ps[g_p].wackedbyactor = -1;
                    ps[g_p].shield_amount = (short) con.max_armour_amount;
                    ps[g_p].dead_flag = 0;
                    ps[g_p].pals_time = 0;
                    ps[g_p].footprintcount = 0;
                    ps[g_p].weapreccnt = 0;
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

                    fta = 0;
                    ftq = 0;

                    cameradist = 0;
                    cameraclock = engine.getTotalClock();
                }
                setpal(ps[g_p]);

                break;
            case 130:
                parseifelse(con, ud.coop != 0 || numplayers > 2);
                break;
            case 129: {
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec != null) {
                    parseifelse(con, klabs(g_sp.getZ() - sec.getFloorz()) < (32 << 8) && sec.getFloorpicnum() == 3073);
                }
                break;
            }
            case 43: {
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec != null) {
                    parseifelse(con, klabs(g_sp.getZ() - sec.getFloorz()) < (32 << 8) && sec.getLotag() == 1);
                }
                break;
            }
            case 44: {
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec != null) {
                    parseifelse(con, sec.getLotag() == 2);
                }
                break;
            }
            case 46:
                insptr++;
                parseifelse(con, g_t[0] >= con.script[insptr]);
                break;
            case 53:
                insptr++;
                parseifelse(con, g_sp.getPicnum() == con.script[insptr]);
                break;
            case 47:
                insptr++;
                g_t[0] = 0;
                break;
            case 48:
                insptr += 2;
                switch (con.script[insptr - 1]) {
                    case 0:
                        ps[g_p].moonshine_amount = (short) con.script[insptr];
                        ps[g_p].inven_icon = 2;
                        break;
                    case 1:
                        ps[g_p].shield_amount += (short) con.script[insptr];// 100;
                        if (ps[g_p].shield_amount > con.max_player_health) {
                            ps[g_p].shield_amount = (short) con.max_player_health;
                        }
                        break;
                    case 2:
                        ps[g_p].snorkle_amount = (short) con.script[insptr];// 1600;
                        ps[g_p].inven_icon = 6;
                        break;
                    case 3:
                        ps[g_p].beer_amount = (short) con.script[insptr];// 1600;
                        ps[g_p].inven_icon = 3;
                        break;
                    case 4:
                        ps[g_p].cowpie_amount = (short) con.script[insptr];// 1600;
                        ps[g_p].inven_icon = 4;
                        break;
                    case 6:
                        switch (g_sp.getLotag()) {
                            case 100:
                                ps[g_p].gotkey[1] = 1;
                                break;
                            case 101:
                                ps[g_p].gotkey[2] = 1;
                                break;
                            case 102:
                                ps[g_p].gotkey[3] = 1;
                                break;
                            case 103:
                                ps[g_p].gotkey[4] = 1;
                                break;
                        }
                        break;
                    case 7:
                        ps[g_p].yeehaa_amount = (short) con.script[insptr];
                        ps[g_p].inven_icon = 5;
                        break;
                    case 9:
                        ps[g_p].inven_icon = 1;
                        ps[g_p].whishkey_amount = (short) con.script[insptr];
                        break;
                    case 10:
                        ps[g_p].inven_icon = 7;
                        ps[g_p].boot_amount = (short) con.script[insptr];
                        break;
                }
                insptr++;
                break;
            case 50:
                hitradius(g_i, con.script[insptr + 1], con.script[insptr + 2], con.script[insptr + 3], con.script[insptr + 4], con.script[insptr + 5]);
                insptr += 6;
                break;
            case 51: {
                insptr++;

                l = con.script[insptr];
                j = 0;

                s = g_sp.getXvel();
                Sprite psp = boardService.getSprite(ps[g_p].i);

                if (((l & 8) != 0) && ps[g_p].on_ground && (sync[g_p].bits & 2) != 0) {
                    j = 1;
                } else if (((l & 16) != 0) && ps[g_p].jumping_counter == 0 && !ps[g_p].on_ground && ps[g_p].poszv > 2048) {
                    j = 1;
                } else if (((l & 32) != 0) && ps[g_p].jumping_counter > 348) {
                    j = 1;
                } else if (((l & 1) != 0) && s >= 0 && s < 8) {
                    j = 1;
                } else if (((l & 2) != 0) && s >= 8 && (sync[g_p].bits & (1 << 5)) == 0) {
                    j = 1;
                } else if (((l & 4) != 0) && s >= 8 && (sync[g_p].bits & (1 << 5)) != 0) {
                    j = 1;
                } else if (((l & 64) != 0) && ps[g_p].posz < (g_sp.getZ() - (48 << 8))) {
                    j = 1;
                } else if (((l & 128) != 0) && s <= -8 && (sync[g_p].bits & (1 << 5)) == 0) {
                    j = 1;
                } else if (((l & 256) != 0) && s <= -8 && (sync[g_p].bits & (1 << 5)) != 0) {
                    j = 1;
                } else if (((l & 512) != 0) && (ps[g_p].quick_kick > 0 || (ps[g_p].curr_weapon == KNEE_WEAPON && ps[g_p].kickback_pic > 0))) {
                    j = 1;
                } else if (((l & 1024) != 0) && psp != null && psp.getXrepeat() < 8) {
                    j = 1;
                } else if (((l & 2048) != 0) && ps[g_p].jetpack_on != 0) {
                    j = 1;
                } else if (((l & 4096) != 0) && ps[g_p].moonshine_amount > 0 && ps[g_p].moonshine_amount < 400) {
                    j = 1;
                } else if (((l & 8192) != 0) && ps[g_p].on_ground) {
                    j = 1;
                } else if (((l & 16384) != 0) && ps[g_p].getPlayerSprite().getXrepeat() > 8 && ps[g_p].getPlayerSprite().getExtra() > 0 && ps[g_p].timebeforeexit == 0) {
                    j = 1;
                } else if (((l & 32768) != 0) && psp != null && psp.getExtra() <= 0) {
                    j = 1;
                } else if (((l & 65536)) != 0) {
                    if (g_sp.getPicnum() == APLAYER && ud.multimode > 1) {
                        j = getincangle((int) ps[otherp].ang, EngineUtils.getAngle(ps[g_p].posx - ps[otherp].posx, ps[g_p].posy - ps[otherp].posy));
                    } else {
                        j = getincangle((int) ps[g_p].ang, EngineUtils.getAngle(g_sp.getX() - ps[g_p].posx, g_sp.getY() - ps[g_p].posy));
                    }

                    if (j > -128 && j < 128) {
                        j = 1;
                    } else {
                        j = 0;
                    }
                }

                parseifelse(con, j != 0);

            }
            break;
            case 56:
                insptr++;
                parseifelse(con, g_sp.getExtra() <= con.script[insptr]);
                break;
            case 58:
                insptr += 2;
                guts(g_sp, con.script[insptr - 1], con.script[insptr], g_p);
                insptr++;
                break;
            case 121:
                insptr++;
                forceplayerangle(ps[g_p]);
                ps[g_p].posxv -= EngineUtils.sin(((int) ps[g_p].ang + 512) & 2047) << 7;
                ps[g_p].posyv -= EngineUtils.sin((int) ps[g_p].ang & 2047) << 7;
                break;
            case 61:
                insptr++;
                ps[g_p].posxv -= EngineUtils.sin(((int) ps[g_p].ang + 512) & 0x7FF) << 10;
                ps[g_p].posyv -= EngineUtils.sin(((int) ps[g_p].ang) & 0x7FF) << 10;
                ps[g_p].jumping_counter = 767;
                ps[g_p].jumping_toggle = 1;
                return false;
            case 62:
                insptr++;
                parseifelse(con, ((hittype[g_i].floorz - hittype[g_i].ceilingz) >> 8) < con.script[insptr]);
                break;
            case 63:
                parseifelse(con, (sync[g_p].bits & (1 << 29)) != 0);
                break;
            case 64: {
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec != null) {
                    parseifelse(con, (sec.getCeilingstat() & 1) != 0);
                }
                break;
            }
            case 65:
                parseifelse(con, ud.multimode > 1);
                break;
            case 66: {
                insptr++;
                Sector sec = boardService.getSector(g_sp.getSectnum());
                if (sec != null) {
                    if (sec.getLotag() == 0) {
                        neartag(g_sp.getX(), g_sp.getY(), g_sp.getZ() - (32 << 8), g_sp.getSectnum(), g_sp.getAng(), 768, 1);
                        Sector nearsec = boardService.getSector(neartagsector);
                        if (nearsec != null && isanearoperator(nearsec.getLotag())) {
                            if ((nearsec.getLotag() & 0xff) == 23 || nearsec.getFloorz() == nearsec.getCeilingz()) {
                                if ((nearsec.getLotag() & 16384) == 0) {
                                    if ((nearsec.getLotag() & 32768) == 0) {
                                        ListNode<Sprite> node = boardService.getSectNode(neartagsector);
                                        while (node != null) {
                                            Sprite sp = node.get();
                                            if (sp.getPicnum() == ACTIVATOR) {
                                                break;
                                            }
                                            node = node.getNext();
                                        }

                                        if (node == null) {
                                            operatesectors(neartagsector, g_i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 67:
                parseifelse(con, ceilingspace(g_sp.getSectnum()));
                break;

            case 74:
                insptr++;
                if (g_sp.getPicnum() != APLAYER) {
                    hittype[g_i].tempang = g_sp.getPal();
                }
                g_sp.setPal((short) con.script[insptr]);
                insptr++;
                break;

            case 77:
            case 142: //newpic
                insptr++;
                g_sp.setPicnum((short) con.script[insptr]);
                insptr++;
                break;

            case 70:
                parseifelse(con, dodge(g_sp));
                break;
            case 71:
                if (badguy(g_sp)) {
                    parseifelse(con, ud.respawn_monsters);
                } else if (inventory(g_sp)) {
                    parseifelse(con, ud.respawn_inventory);
                } else {
                    parseifelse(con, ud.respawn_items);
                }
                break;
            case 72:
                insptr++;
                parseifelse(con, (hittype[g_i].floorz - g_sp.getZ()) <= ((con.script[insptr]) << 8));
                break;
            case 73:
                insptr++;
                parseifelse(con, (g_sp.getZ() - hittype[g_i].ceilingz) <= ((con.script[insptr]) << 8));
                break;
            case 14:

                insptr++;
                ps[g_p].pals_time = con.script[insptr];
                insptr++;
                for (j = 0; j < 3; j++) {
                    ps[g_p].pals[j] = (short) con.script[insptr];
                    insptr++;
                }
                break;
            case 78:
                insptr++;
                parseifelse(con, ps[g_p].getPlayerSprite().getExtra() < con.script[insptr]);
                break;

            case 75: {
                insptr++;
                j = 0;
                switch (con.script[insptr++]) {
                    case 0:
                        if (ps[g_p].moonshine_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 1:
                        if (ps[g_p].shield_amount != con.max_player_health) {
                            j = 1;
                        }
                        break;
                    case 2:
                        if (ps[g_p].snorkle_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 3:
                        if (ps[g_p].beer_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 4:
                        if (ps[g_p].cowpie_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 6:
                        switch (g_sp.getLotag()) {
                            case 100:
                                if (ps[g_p].gotkey[1] != 0) {
                                    j = 1;
                                }
                                break;
                            case 101:
                                if (ps[g_p].gotkey[2] != 0) {
                                    j = 1;
                                }
                                break;
                            case 102:
                                if (ps[g_p].gotkey[3] != 0) {
                                    j = 1;
                                }
                                break;
                            case 103:
                                if (ps[g_p].gotkey[4] != 0) {
                                    j = 1;
                                }
                                break;
                        }
                        break;
                    case 7:
                        if (ps[g_p].yeehaa_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 9:
                        if (ps[g_p].whishkey_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                    case 10:
                        if (ps[g_p].boot_amount != con.script[insptr]) {
                            j = 1;
                        }
                        break;
                }

                parseifelse(con, j != 0);
                break;
            }
            case 38: {
                insptr++;
                Sprite psp = boardService.getSprite(ps[g_p].i);
                if (psp == null) {
                    break;
                }

                if (ps[g_p].knee_incs == 0 && psp.getXrepeat() >= 9) {
                    if (engine.cansee(g_sp.getX(), g_sp.getY(), g_sp.getZ() - (4 << 8), g_sp.getSectnum(), ps[g_p].posx, ps[g_p].posy, ps[g_p].posz + (16 << 8), psp.getSectnum())) {
                        ps[g_p].knee_incs = 1;
                        if (ps[g_p].weapon_pos == 0) {
                            ps[g_p].weapon_pos = -1;
                        }
                        ps[g_p].actorsqu = g_i;
                    }
                }
                break;
            }
            case 90: {
                int s1 = g_sp.getSectnum();

                j = 0;

                s1 = engine.updatesector(g_sp.getX() + 108, g_sp.getY() + 108, s1);
                if (s1 == g_sp.getSectnum()) {
                    s1 = engine.updatesector(g_sp.getX() - 108, g_sp.getY() - 108, s1);
                    if (s1 == g_sp.getSectnum()) {
                        s1 = engine.updatesector(g_sp.getX() + 108, g_sp.getY() - 108, s1);
                        if (s1 == g_sp.getSectnum()) {
                            s1 = engine.updatesector(g_sp.getX() - 108, g_sp.getY() + 108, s1);
                            if (s1 == g_sp.getSectnum()) {
                                j = 1;
                            }
                        }
                    }
                }
                parseifelse(con, j != 0);
            }

            break;
            case 80:
                insptr++;
                FTA(con.script[insptr], ps[g_p]);
                insptr++;
                break;
            case 81:
                parseifelse(con, floorspace(g_sp.getSectnum()));
                break;
            case 82:
                // hit wall or sprite
                int hitType = hittype[g_i].movflag & HIT_TYPE_MASK;
                parseifelse(con, hitType == HIT_WALL || hitType == HIT_SPRITE);
                break;
            case 83:
                insptr++;
                switch (g_sp.getPicnum()) {
                    case 1115:
                    case 1168:
                    case 5581:
                        if (g_sp.getYvel() != 0) {
                            operaterespawns(g_sp.getYvel());
                        }
                        break;
                    default:
                        if (g_sp.getHitag() >= 0) {
                            operaterespawns(g_sp.getHitag());
                        }
                        break;
                }
                break;
            case 85:
                insptr++;
                parseifelse(con, g_sp.getPal() == con.script[insptr]);
                break;

            case 109:
                for (j = 1; j < NUM_SOUNDS; j++) {
                    if (Sound[j].getSoundOwner(0).i == g_i) {
                        break;
                    }
                }

                parseifelse(con, j == NUM_SOUNDS);
                break;
            case 131: //ifmotofast
                parseifelse(con, ps[g_p].CarSpeed > 60);
                break;
            case 132: //ifwind
                parseifelse(con, WindTime > 0);
                break;
            case 133: //smacksprite
                if ((engine.krand() & 1) != 0) {
                    g_sp.setAng((short) ((g_sp.getAng() - (engine.krand() & 1)) & kAngleMask));
                } else {
                    g_sp.setAng((short) ((g_sp.getAng() + 512 + (engine.krand() & 1)) & kAngleMask));
                }
                insptr++;
                break;
            case 134: //ifonmoto
                parseifelse(con, ps[g_p].OnMotorcycle);
                break;
            case 135: //ifonboat
                parseifelse(con, ps[g_p].OnBoat);
                break;
            case 136: //fakebubba
                fakebubba_spawn++;
                switch (fakebubba_spawn - 1) {
                    case 0:
                        spawn(g_i, PIG);
                        break;
                    case 1:
                        spawn(g_i, MINION);
                        break;
                    case 2:
                        spawn(g_i, DAISYMAE);
                        break;
                    case 3:
                        spawn(g_i, VIXEN);
                        operateactivators(666, ps[g_p].i);
                        break;
                }

                insptr++;
                break;
            case 137: //mamatrigger
                operateactivators(667, ps[g_p].i);
                insptr++;
                break;
            case 138: //mamaspawn
                if (mamaspawn_count != 0) {
                    mamaspawn_count--;
                    spawn(g_i, JACKOLOPE);
                }
                insptr++;
                break;
            case 139: //mamaquake
                if (g_sp.getPal() == 31) {
                    earthquaketime = 4;
                } else if (g_sp.getPal() == 32) {
                    earthquaketime = 6;
                }
                insptr++;
                break;
            case 140: //clipdist
                insptr++;
                g_sp.setClipdist(con.script[insptr]);
                insptr++;
                break;
            case 141: //mamaend
                insptr++;
                ps[connecthead].MamaEnd = 150;
                break;
            case 143: //garybanjo
                if (BellSound != 0) {
                    if (Sound[BellSound].getSoundOwnerCount() == 0) {
                        spritesound(BellSound, g_i);
                    }
                } else {
                    switch ((engine.krand() & 3) + 1) {
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
                insptr++;
                break;
            case 144: //motoloopsnd
                if (Sound[411].getSoundOwnerCount() == 0) {
                    spritesound(411, g_i);
                }
                insptr++;
                break;
            case 145: //ifsizedown
                g_sp.setXrepeat(g_sp.getXrepeat() - 1);
                g_sp.setYrepeat(g_sp.getYrepeat() - 1);
                parseifelse(con, g_sp.getXrepeat() <= 5);
                break;
            case 146: //rndmove
                g_sp.setAng((short) (engine.krand() & kAngleMask));
                g_sp.setXvel(25);
                insptr++;
                break;
            default:
                killit_flag = 1;
                break;
        }
        return false;
    }

    public static void execute(Script con, int i, int p, int x) {
        boolean done;

        g_i = (short) i;
        g_p = (short) p;
        g_x = x;
        g_sp = boardService.getSprite(g_i);
        g_t = hittype[g_i].temp_data;

        if (g_sp == null) {
            return;
        }

        if (con.actorscrptr[g_sp.getPicnum()] == 0) {
            return;
        }

        insptr = 4 + con.actorscrptr[g_sp.getPicnum()];

        killit_flag = 0;

        Sector g_sec = boardService.getSector(g_sp.getSectnum());
        if (g_sec == null) {
            if (badguy(g_sp)) {
                ps[connecthead].actors_killed++;
                if (ud.coop == 1) {
                    ps[g_p].frag++;
                }
            }
            engine.deletesprite(g_i);
            return;
        }

        if (g_t[4] != 0) {
            g_sp.setLotag(g_sp.getLotag() + TICSPERFRAME);
            if (g_sp.getLotag() > con.script[g_t[4] + 4]) {
                g_t[2]++;
                g_sp.setLotag(0);
                g_t[3] += con.script[g_t[4] + 3];
            }
            if (klabs(g_t[3]) >= klabs(con.script[g_t[4] + 1] * con.script[g_t[4] + 3])) {
                g_t[3] = 0;
            }
        }

        do {
            done = parse(con);
        } while (!done);

        if (killit_flag == 1) {
            if (ps[g_p].actorsqu == g_i) {
                ps[g_p].actorsqu = -1;
            }
            engine.deletesprite(g_i);
        } else {
            move(con);

            if (g_sp.getStatnum() == 1) {
                if (badguy(g_sp)) {
                    if (g_sp.getXrepeat() > 60) {
                        return;
                    }
                    if (ud.respawn_monsters && g_sp.getExtra() <= 0) {
                        return;
                    }
                } else if (ud.respawn_items && (g_sp.getCstat() & 32768) != 0) {
                    return;
                }

                if (hittype[g_i].timetosleep > 1) {
                    hittype[g_i].timetosleep--;
                } else if (hittype[g_i].timetosleep == 1) {
                    engine.changespritestat(g_i, (short) 2);
                }
            } else if (g_sp.getStatnum() == 6) {
                switch (g_sp.getPicnum()) {
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
                        if (hittype[g_i].timetosleep > 1) {
                            hittype[g_i].timetosleep--;
                        } else if (hittype[g_i].timetosleep == 1) {
                            engine.changespritestat(g_i, (short) 2);
                        }
                        break;
                }
            }
        }
    }

    public static void compilecons() throws InitializationException {
        conweigth = 0;
        Script con = loadefs(confilename);

        try {
            FileEntry grpEntry = game.getCache().getGameDirectory().getEntry(game.mainGrp);
            if (!grpEntry.exists()) {
                throw new FileNotFoundException();
            }

            List<EpisodeEntry> entryList = episodeManager.getEpisodeEntries(grpEntry);
            EpisodeEntry entry = entryList.get(0);
            if (!entry.getConFile().getName().equalsIgnoreCase(confilename)) {
                throw new RuntimeException("Can't initialize script file: " + confilename);
            }
            defGame = episodeManager.getEpisode(entry);
        } catch (Exception e) {
            throw new RuntimeException("[ " + e.getClass().getSimpleName() + "]: " + "Unknown error!", e);
        }

        switch (con.type) {
            case RR:
                Console.out.println("Looks like Redneck Rampage Edition CON files.");
                break;
            case RR66:
                Console.out.println("Looks like Redneck Rampage: Suckin' Grits on Route 66 Edition CON files.");
                break;
            case RRRA:
                Console.out.println("Looks like Redneck Rampage: Rides Again Edition CON files.");
                break;
        }

        defGame.setCON(con);
        defGame.title = "Default";

        //Create episode info
        defGame.nEpisodes = con.nEpisodes;
        for (int i = 0; i < con.nEpisodes; i++) {
            defGame.episodes[i] = new EpisodeInfo(new String(con.volume_names[i]).trim());
            defGame.episodes[i].nMaps = con.nMaps[i];
            for (int j = 0; j < con.nMaps[i]; j++) {
                Path path = FileUtils.getPath(new String(con.level_file_names[i * 11 + j]).trim());
                if (!defGame.episodes[i].setMapInfo(j, new MapInfo(path, new String(con.level_names[i * 11 + j]).trim(), con.partime[i * 11 + j], con.designertime[i * 11 + j]))) {
                    Console.out.println("Warning! Can't set map info for index " + j, OsdColor.YELLOW); // #GDX 31.12.2024
                }
            }
        }

        for (int i = 0; i < con.nSkills; i++) {
            defGame.skillnames[i] = new String(con.skill_names[i]).trim();
        }

        if (defGame.episodes[1] != null && con.type != RRRA) {
            defGame.episodes[1].setMapInfo(7, new MapInfo(FileUtils.getPath("endgame.map"), "Close encounters", defGame.episodes[1].getMapInfo(0).partime, defGame.episodes[1].getMapInfo(0).designertime)); //EndGame map
            defGame.episodes[1].nMaps = 8;
        }

        currentGame = defGame;
    }

    //For user episodes

    public static byte[] preparescript(byte[] buf) {
        if (buf == null) {
            return null;
        }

        int index = -1;
        while ((index = indexOf("//", buf, index + 1)) != -1) {
            int textptr = index + 2;
            while (buf[textptr] != 0x0a) {
                buf[textptr] = 0;
                textptr++;
                if (textptr >= buf.length) {
                    return buf;
                }
            }
        }

        while ((index = indexOf("/*", buf, index + 1)) != -1) {
            int textptr = index + 2;
            do {
                buf[textptr] = 0;
                textptr++;
                if (textptr >= buf.length) {
                    return buf;
                }
            } while (buf[textptr] != '*' || buf[textptr + 1] != '/');
        }

        return buf;
    }

    public static Script loaduserdef(Entry fp) {
        if (!fp.exists()) {
            return null;
        }
        int fs = (int) fp.getSize();
        Console.out.println("Compiling: " + fp.getName() + ".");

        byte[] buf = new byte[fs + 1];
        label = new char[131072];

        System.arraycopy(fp.getBytes(), 0, buf, 0, fs);

        parsing_actor = 0;
        parsing_state = 0;
        num_squigilly_brackets = 0;
        checking_ifelse = 0;
        killit_flag = 0;

        textptr = 0;
        last_used_text = new String(buf);
        text = last_used_text.toCharArray();
        text[fs] = 0;

        Script con = new Script();

        Arrays.fill(con.actorscrptr, 0);
        Arrays.fill(con.actortype, (short) 0);

        conweigth = 0;
        labelcode.clear();
        labelcnt = 0;
        scriptptr = 1;
        warning = 0;
        error = 0;
        line_number = 1;

        try {
            passone(con); //Tokenize
        } catch (Exception e) {
            e.printStackTrace();
            error = 1;
            return null;
        }

        switch (con.type) {
            case RR:
                Console.out.println("Looks like Redneck Rampage Edition CON files.");
                break;
            case RR66:
                Console.out.println("Looks like Redneck Rampage: Suckin' Grits on Route 66 Edition CON files.");
                break;
            case RRRA:
                Console.out.println("Looks like Redneck Rampage: Rides Again Edition CON files.");
                break;
        }

        if ((warning | error) != 0) {
            Console.out.println("Found " + warning + " warning(s), " + error + " error(s).");
        }

        if (error != 0) {
            return null;
        } else {
            Console.out.println("Code Size:" + (((scriptptr) << 2) - 4) + " bytes(" + labelcnt + " labels).");
        }

        return con;
    }
}
