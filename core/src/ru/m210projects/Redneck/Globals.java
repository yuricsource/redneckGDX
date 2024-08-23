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

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Redneck.Types.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;

public class Globals {

    public static final int BACKBUTTON = 9216;
    public static final int MOUSECURSOR = 9217;

    public static final int KEYSIGN = 9220;
    public static final int GUTSMETTER = 9221;
    public static final int KILLSSIGN = 9222;

    public static final int WIDEHUD_LEFTSHADOW = 9223;
    public static final int WIDEHUD_RIGHTSHADOW = 9224;

    public static final int WIDEHUD_PART1 = 9225;
    public static final int WIDEHUD_PART2 = 9226;

    public static final int RR = 0;
    public static final int RR66 = 1;
    public static final int RRRA = 2;
    public static final int BYTEVERSIONRR = 108;
    public static final int GDXBYTEVERSION = 147;
    public static final int BYTEVERSION = GDXBYTEVERSION;
    public static final int TICRATE = 120;
    public static final int TICSPERFRAME = (TICRATE / 26);
    public static final int TILE_ANIM = MAXTILES - 3;
    public static final short ANIM_PAL = (short) (NORMALPAL - 1);
    public static final int MAX_WEAPONS = 13;
    public static final int MAX_WEAPONSRA = 17;
    public static final int MAXANIMWALLS = 512;
    public static final int NUMOFFIRSTTIMEACTIVE = 192;
    public static final int MAXCYCLERS = 256;
    public static final int RECSYNCBUFSIZ = 2520; //2520 is the (LCM of 1-8)*3
    public static final int FOURSLEIGHT = 1 << 8;
    public static final int PHEIGHT = 10240;
    public static final int MAXSLEEPDIST = 16384;
    public static final int SLEEPTIME = 24 * 64;
    public static final int AUTO_AIM_ANGLE = 48;
    // These tile positions are reserved!;
    public static final int MODE_EOL = 1;
    public static final int MODE_END = 2;
    // Defines weapon, not to be used with the 'shoot' keyword.;
    public static final int KNEE_WEAPON = 0;
    public static final int PISTOL_WEAPON = 1;
    public static final int SHOTGUN_WEAPON = 2;
    public static final int RIFLEGUN_WEAPON = 3;
    public static final int DYNAMITE_WEAPON = 4;
    public static final int CROSSBOW_WEAPON = 5;
    public static final int THROWSAW_WEAPON = 6;
    public static final int ALIENBLASTER_WEAPON = 7;
    public static final int POWDERKEG_WEAPON = 8;
    public static final int TIT_WEAPON = 9;
    public static final int HANDREMOTE_WEAPON = 10;
    public static final int BUZSAW_WEAPON = 11;
    public static final int BOWLING_WEAPON = 12;
    public static final int MOTO_WEAPON = 13;
    public static final int BOAT_WEAPON = 14;
    public static final int RATE_WEAPON = 15;
    public static final int CHICKENBOW_WEAPON = 16;
    // Hit definitions
    public static final int kHitTypeMask = HIT_TYPE_MASK;
    public static final int kHitIndexMask = HIT_INDEX_MASK;
    public static final int kHitSector = HIT_SECTOR;
    public static final int kHitWall = HIT_WALL;
    public static final int kHitSprite = HIT_SPRITE;
    public static final int nMaxMaps = 11;
    public static final int nMaxEpisodes = 3;
    public static final int nMaxSkills = 5;
    public static final short[] weaponsandammosprites = {
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
    public static final int kAngleMask = 0x7FF;
    public static Entry boardfilename;
    public static boolean mFakeMultiplayer;
    public static int nFakePlayers;
    public static HashMap<String, GameInfo> episodeCache = new HashMap<>();
    public static GameInfo defGame;
    public static GameInfo currentGame;
    public static final NetInfo pNetInfo = new NetInfo();
    public static final AtomicInteger fz = new AtomicInteger();
    public static final AtomicInteger cz = new AtomicInteger();
    //XXX RA
    public static short BellTime;
    public static int BellSound;
    public static short word_119BE0;
    public static int WindDir;
    public static int WindTime;
    public static int mamaspawn_count;
    public static int fakebubba_spawn;
    public static int dword_119C08;
    public static int uGameFlags = 0;
    public static boolean MODE_TYPE; //== 16
    public static int gVisibility;

    public static int musicvolume, musiclevel;
    public static int LeonardCrack;
    public static final PlayerOrig[] po = new PlayerOrig[MAXPLAYERS];
    public static final PlayerStruct[] ps = new PlayerStruct[MAXPLAYERS];
    public static final Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
    public static final UserDefs ud = new UserDefs();
    public static short global_random;
    public static int neartagsector, neartagwall, neartagsprite;
    public static final int numframes = 0;
    public static int lockclock;
    public static final short[] spriteq = new short[1024];
    public static short spriteqloc;
    public static final Animwalltype[] animwall = new Animwalltype[MAXANIMWALLS];
    public static short numanimwalls;
    public static final int[] msx = new int[2048];
    public static final int[] msy = new int[2048];
    public static final short[][] cyclers = new short[MAXCYCLERS][6];
    public static short numcyclers;
    public static final char[] buf = new char[80];
    public static short camsprite;
    public static final short[] mirrorwall = new short[64];
    public static final short[] mirrorsector = new short[64];
    public static short mirrorcnt;
    public static final int[] soundsiz = new int[NUM_SOUNDS];
    public static final Sample[] Sound = new Sample[NUM_SOUNDS];

    public static short numplayersprites, earthquaketime;
    public static boolean loadfromgrouponly;
    public static int fricxv, fricyv;
    public static final Input[] sync = new Input[MAXPLAYERS];
    //Multiplayer syncing variables
    public static short screenpeek;
    public static char display_mirror;
    public static final byte[] tempbuf = new byte[2048];
    public static final short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];
    public static byte multipos, multiwhat, multiflag;
    public static char everyothertime;
    public static final char gamequit = 0;

}
