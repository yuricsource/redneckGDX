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

import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Redneck.Types.CommonPart;
import ru.m210projects.Redneck.Types.PlayerStruct;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Gameutils.neartag;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Types.ANIMATION.*;
import static ru.m210projects.Redneck.View.FTA;
import static ru.m210projects.Redneck.Weapons.shoot;

public class RSector {

    private static final int[] chitsw = new int[3];
    public static boolean haltsoundhack;
    public static int player_dist;
    public static final int[] wallfind = new int[2];
    public static int dword_18D0A8, dword_18D0A0, dword_18D0AC, dword_18D0A4;
    public static byte byte_18D0BB;

    public static boolean ceilingspace(int sectnum) {
        Sector sec = boardService.getSector(sectnum);
        if (sec != null && (sec.getCeilingstat() & 1) != 0 && sec.getCeilingpal() == 0) {
            switch (sec.getCeilingpicnum()) {
                case 1022:
                case 1026:
                    return true;
            }
        }
        return false;
    }

    public static boolean floorspace(int sectnum) {
        Sector sec = boardService.getSector(sectnum);
        if (sec != null && (sec.getFloorstat() & 1) != 0 && sec.getCeilingpal() == 0) {
            switch (sec.getFloorpicnum()) {
                case 1022:
                case 1026:
                    return true;
            }
        }
        return false;
    }

    public static boolean wallswitchcheck(int i) {
        Sprite spr = boardService.getSprite(i);
        if (spr == null) {
            return false;
        }

        switch (spr.getPicnum()) {
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 98:
            case 99:
            case 100:
            case 101:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 250:
            case 251:
            case 1278:
            case 1279:
            case 2222:
            case 2223:
            case 2224:
            case 2225:
            case 2226:
            case 2227:
            case 2249:
            case 2250:
            case 2254:
            case 2255:
            case 2259:
            case 2260:

            case 8048: //RA
            case 8049:
            case 8050:
            case 8051:
            case 8464:
            case 8465:
                return true;
        }
        return false;
    }

    public static int callsound(int sn, int whatsprite) {
        if (haltsoundhack) {
            haltsoundhack = false;
            return -1;
        }

        ListNode<Sprite> n = boardService.getSectNode(sn);
        while (n != null) {
            Sprite sp = n.get();
            if (sp.getPicnum() == MUSICANDSFX && sp.getLotag() < 1000) {
                int i = n.getIndex();
                if (whatsprite == -1) {
                    whatsprite = i;
                }

                if (hittype[i].temp_data[0] == 0) {
                    if (sp.getLotag() < NUM_SOUNDS && (currentGame.getCON().soundm[sp.getLotag()] & 16) == 0) {
                        if (sp.getLotag() != 0) {
                            spritesound(sp.getLotag(), whatsprite);
                            if (sp.getHitag() != 0 && sp.getLotag() != sp.getHitag() && sp.getHitag() < NUM_SOUNDS) {
                                stopsound(sp.getHitag());
                            }
                        }

                        Sector sec = boardService.getSector(sp.getSectnum());
                        if (sec != null && (sec.getLotag() & 0xff) != 22) {
                            hittype[i].temp_data[0] = 1;
                        }
                    }
                } else if (sp.getHitag() < NUM_SOUNDS) {
                    if (sp.getHitag() != 0) {
                        spritesound(sp.getHitag(), whatsprite);
                    }
                    if ((currentGame.getCON().soundm[sp.getLotag()] & 1) != 0 || (sp.getHitag() != 0 && sp.getHitag() != sp.getLotag())) {
                        stopsound(sp.getLotag());
                    }
                    hittype[i].temp_data[0] = 0;
                }
                return sp.getLotag();
            }
            n = n.getNext();
        }
        return -1;
    }

    public static boolean check_activator_motion(int lotag) {
        ListNode<Sprite> n = boardService.getStatNode(8);
        while (n != null) {
            Sprite s = n.get();
            if (s.getLotag() == lotag) {
                for (int j = gAnimationCount - 1; j >= 0; j--) {
                    if (s.getSectnum() == gAnimationData[j].sect) {
                        return (true);
                    }
                }

                ListNode<Sprite> n2 = boardService.getStatNode(3);
                while (n2 != null) {
                    Sprite sp = n2.get();
                    int j = n2.getIndex();
                    if (s.getSectnum() == sp.getSectnum()) {
                        switch (sp.getLotag()) {
                            case 11:
                            case 30:
                                if (hittype[j].temp_data[4] != 0) {
                                    return (true);
                                }
                                break;
                            case 20:
                            case 31:
                            case 32:
                            case 18:
                                if (hittype[j].temp_data[0] != 0) {
                                    return (true);
                                }
                                break;
                        }
                    }

                    n2 = n2.getNext();
                }
            }
            n = n.getNext();
        }
        return (false);
    }

    public static boolean isadoorwall(int dapic) {
        switch (dapic) {
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 115:
            case 116:
            case 117:
            case 119:
            case 229:
            case 230:
            case 232:
            case 1856: //METAL
            case 1877: //WOOD
            case 2261:
            case 2263:
            case 2267:
            case 2268:
                return true;
        }
        return false;
    }

    public static boolean isadoorwall2(int dapic) {
        switch (dapic) {
            case 1792:
            case 1801:
            case 1805:
            case 1807:
            case 1808:
            case 1812:
            case 1821:
            case 1826:
            case 1850:
            case 1851:
            case 1856:
            case 1877:
            case 1938:
            case 1942:
            case 1944:
            case 1945:
            case 1951:
            case 1961:
            case 1964:
            case 1985:
            case 1995:
            case 2022:
            case 2052:
            case 2053:
            case 2060:
            case 2074:
            case 2132:
            case 2136:
            case 2139:
            case 2150:
            case 2178:
            case 2186:
            case 2319:
            case 2321:
            case 2326:
            case 2329:
            case 2578:
            case 2581:
            case 2610:
            case 2613:
            case 2621:
            case 2622:
            case 2676:
            case 2732:
            case 2831:
            case 2832:
            case 2842:
            case 2940:
            case 2970:
            case 3083:
            case 3100:
            case 3155:
            case 3195:
            case 3232:
            case 3600:
            case 3631:
            case 3635:
            case 3637:
            case 3645:
            case 3646:
            case 3647:
            case 3652:
            case 3653:
            case 3671:
            case 3673:
            case 3684:
            case 3708:
            case 3714:
            case 3716:
            case 3723:
            case 3725:
            case 3737:
            case 3754:
            case 3762:
            case 3763:
            case 3764:
            case 3765:
            case 3767:
            case 3793:
            case 3814:
            case 3815:
            case 3819:
            case 3827:
            case 3837:

                //RA
            case 1996:
            case 2382:
            case 2961:
            case 3804:
            case 7430:
            case 7467:
            case 7469:
            case 7470:
            case 7475:
            case 7566:
            case 7576:
            case 7716:
            case 8063:
            case 8067:
            case 8076:
            case 8106:
            case 8379:
            case 8380:
            case 8565:
            case 8605:
                return true;
        }
        return false;
    }

    public static boolean isanunderoperator(int lotag) {
        switch (lotag & 0xff) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 26:
                return true;
        }
        return false;
    }

    public static boolean isanearoperator(int lotag) {
        switch (lotag & 0xff) {
            case 9:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 25:
            case 26:
            case 29:
            case 41:
                return true;
        }
        return false;
    }

    public static int checkcursectnums(int sect) {
        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            Sprite psp = boardService.getSprite(ps[i].i);
            if (psp != null && psp.getSectnum() == sect) {
                return i;
            }
        }
        return -1;
    }

    public static int ldist(Sprite s1, Sprite s2) {
        int vx = s1.getX() - s2.getX();
        int vy = s1.getY() - s2.getY();
        return (FindDistance2D(vx, vy) + 1);
    }

    public static int dist(Sprite s1, Sprite s2) {
        int vx = s1.getX() - s2.getX();
        int vy = s1.getY() - s2.getY();
        int vz = s1.getZ() - s2.getZ();
        return (FindDistance3D(vx, vy, vz >> 4));
    }

    public static int findplayer(Sprite s) {
        if (ud.multimode < 2) {
            player_dist = klabs(ps[myconnectindex].oposx - s.getX()) + klabs(ps[myconnectindex].oposy - s.getY()) + ((klabs(ps[myconnectindex].oposz - s.getZ() + (28 << 8))) >> 4);
            return myconnectindex;
        }

        int closest = 0x7fffffff;
        int closest_player = 0;

        for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
            Sprite psp = boardService.getSprite(ps[j].i);
            int x = klabs(ps[j].oposx - s.getX()) + klabs(ps[j].oposy - s.getY()) + ((klabs(ps[j].oposz - s.getZ() + (28 << 8))) >> 4);
            if (psp != null && x < closest && psp.getExtra() > 0) {
                closest_player = j;
                closest = x;
            }
        }

        player_dist = closest;
        return closest_player;
    }

    public static int findotherplayer(int p) {
        int closest = 0x7fffffff;
        int closest_player = p;

        for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
            Sprite psp = boardService.getSprite(ps[j].i);
            if (p != j && psp != null && psp.getExtra() > 0) {
                int x = klabs(ps[j].oposx - ps[p].posx) + klabs(ps[j].oposy - ps[p].posy) + (klabs(ps[j].oposz - ps[p].posz) >> 4);

                if (x < closest) {
                    closest_player = j;
                    closest = x;
                }
            }
        }

        player_dist = closest;
        return closest_player;
    }

    public static void animatewalls() {
        if (ps[connecthead].isSea) {
            for (int i = 0; i < boardService.getWallCount(); ++i) {
                Wall wal = boardService.getWall(i);
                if (wal == null) {
                    continue;
                }
                if (wal.getPicnum() == 7873) {
                    wal.setXpanning(wal.getXpanning() + 6);
                } else if (wal.getPicnum() == 7870) {
                    wal.setXpanning(wal.getXpanning() + 6);
                }
            }
        }

        for (int p = 0; p < numanimwalls; p++) {
            final int i = animwall[p].wallnum;
            Wall wal = boardService.getWall(i);
            if (wal == null) {
                continue;
            }

            int j = wal.getPicnum();
            switch (j) {
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 167:
                case 168:
                case 169:
                case 170:
                case 171:
                    if ((engine.krand() & 255) < 16) {
                        animwall[p].tag = wal.getPicnum();
                        wal.setPicnum(SCREENBREAK6);
                    }

                    continue;

                case SCREENBREAK6:
                case SCREENBREAK7:
                case SCREENBREAK8:

                    if (animwall[p].tag >= 0) {
                        wal.setPicnum((short) animwall[p].tag);
                    } else {
                        wal.setPicnum(wal.getPicnum() + 1);
                        if (wal.getPicnum() == (SCREENBREAK6 + 3)) {
                            wal.setPicnum(SCREENBREAK6);
                        }
                    }
                    continue;

            }

            if ((wal.getCstat() & 16) != 0) {
                switch (wal.getOverpicnum()) {
                    case W_FORCEFIELD:
                    case W_FORCEFIELD + 1:
                    case W_FORCEFIELD + 2: {

                        int t = animwall[p].tag;

                        if ((wal.getCstat() & 254) != 0) {
                            wal.setXpanning(wal.getXpanning() - (t >> 10));
                            wal.setYpanning(wal.getYpanning() - (t >> 10));

                            if (wal.getExtra() == 1) {
                                wal.setExtra(0);
                                animwall[p].tag = 0;
                            } else {
                                animwall[p].tag += 128;
                            }

                            if (animwall[p].tag < (128 << 4)) {
                                if ((animwall[p].tag & 128) != 0) {
                                    wal.setOverpicnum(W_FORCEFIELD);
                                } else {
                                    wal.setOverpicnum(W_FORCEFIELD + 1);
                                }
                            } else {
                                if ((engine.krand() & 255) < 32) {
                                    animwall[p].tag = 128 << (engine.krand() & 3);
                                } else {
                                    wal.setOverpicnum(W_FORCEFIELD + 1);
                                }
                            }
                        }

                        break;
                    }
                }
            }
        }
    }

    public static boolean activatewarpelevators(int s, int d) //Parm = sectoreffectornum
    {
        Sprite spr = boardService.getSprite(s);
        if (spr == null) {
            return false;
        }

        int sn = spr.getSectnum();
        Sector sec = boardService.getSector(sn);
        if (sec == null) {
            return false;
        }
        // See if the sector exists

        ListNode<Sprite> n = boardService.getStatNode(3);
        while (n != null) {
            Sprite sp = n.get();
            if (sp.getLotag() == 17) {
                if (sp.getHitag() == spr.getHitag()) {
                    Sector sec2 = boardService.getSector(sp.getSectnum());
                    if (sec2 != null) {
                        if ((klabs(sec.getFloorz() - hittype[s].temp_data[2]) > sp.getYvel())
                                || (sec2.getHitag() == (sec.getHitag() - d))) {
                            break;
                        }
                    }
                }
            }
            n = n.getNext();
        }

        if (n == null) {
            return true; // No find
        } else {
            if (d == 0) {
                spritesound(ELEVATOR_OFF, s);
            } else {
                spritesound(ELEVATOR_ON, s);
            }
        }

        n = boardService.getStatNode(3);
        while (n != null) {
            Sprite sp = n.get();
            int i = n.getIndex();
            if (sp.getLotag() == 17) {
                if (sp.getHitag() == spr.getHitag()) {
                    hittype[i].temp_data[0] = d;
                    hittype[i].temp_data[1] = d; //Make all check warp
                }
            }
            n = n.getNext();
        }
        return false;
    }

    public static void operatesectors(final int sn, final int ii) {
        Sector ssec = boardService.getSector(sn);
        if (ssec == null) {
            return;
        }

        switch (ssec.getLotag() & (0xffff - 49152)) {
            case 41:
                if (numjaildoors > 0) {
                    for (int i = 0; i < numjaildoors; i++) {
                        if (ssec.getHitag() == jailunique[i]) {
                            if (jailstatus[i] == 0) {
                                jailstatus[i] = 1;
                                jailcount2[i] = jaildistance[i];
                                if (jailsound[i] != 0) {
                                    spritesound(jailsound[i], ps[screenpeek].i);
                                }
                            }
                            if (jailstatus[i] == 2) {
                                jailstatus[i] = 3;
                                jailcount2[i] = jaildistance[i];
                                if (jailsound[i] != 0) {
                                    spritesound(jailsound[i], ps[screenpeek].i);
                                }
                            }
                        }
                    }
                }
                break;
            case 7: {
                for (ListNode<Wall> wn = ssec.getWallNode(); wn != null; wn = wn.getNext()) {
                    Wall wal = wn.get();
                    setanimation(sn, wn.getIndex(), wal.getX() + 1024, 4, WALLX);
                    setanimation(sn, wal.getPoint2(), wal.getWall2().getX() + 1024, 4, WALLX);
                }
                break;
            }
            case 30: {
                int j = ssec.getHitag();
                Sprite s = boardService.getSprite(j);
                if (s == null) {
                    break;
                }

                if (hittype[j].tempang == 0 ||
                        hittype[j].tempang == 256) {
                    callsound(sn, ii);
                }
                if (s.getExtra() == 1) {
                    s.setExtra(3);
                } else {
                    s.setExtra(1);
                }
                break;
            }
            case 31: {
                int j = ssec.getHitag();
                if (hittype[j].temp_data[4] == 0) {
                    hittype[j].temp_data[4] = 1;
                }

                callsound(sn, ii);
                break;
            }
            case 26: { //The split doors
                int i = getanimationgoal(ssec, CEILZ);
                if (i == -1) //if the door has stopped
                {
                    haltsoundhack = true;
                    ssec.setLotag(ssec.getLotag() & 0xff00);
                    ssec.setLotag(ssec.getLotag() | 22);
                    operatesectors(sn, ii);
                    ssec.setLotag(ssec.getLotag() & 0xff00);
                    ssec.setLotag(ssec.getLotag() | 9);
                    operatesectors(sn, ii);
                    ssec.setLotag(ssec.getLotag() & 0xff00);
                    ssec.setLotag(ssec.getLotag() | 26);
                }
                return;
            }

            case 9: {
                int dax, day, dax2, day2, sp;
                sp = ssec.getExtra() >> 4;

                //first find center point by averaging all points
                dax = 0;
                day = 0;
                for (ListNode<Wall> wn = ssec.getWallNode(); wn != null; wn = wn.getNext()) {
                    Wall wal = wn.get();
                    dax += wal.getX();
                    day += wal.getY();
                }
                int startwall = ssec.getWallptr();
                int endwall = ssec.getEndWall();

                dax /= (endwall - startwall + 1);
                day /= (endwall - startwall + 1);

                //find any points with either same x or same y coordinate
                //  as center (dax, day) - should be 2 points found.
                wallfind[0] = -1;
                wallfind[1] = -1;
                for (int i = startwall; i <= endwall; i++) {
                    Wall wal = boardService.getWall(i);
                    if (wal != null && ((wal.getX() == dax) || (wal.getY() == day))) {
                        if (wallfind[0] == -1) {
                            wallfind[0] = i;
                        } else {
                            wallfind[1] = i;
                        }
                    }
                }

                for (int j = 0; j < 2; j++) {
                    Wall wal = boardService.getWall(wallfind[j]);
                    if (wal != null && (wal.getX() == dax) && (wal.getY() == day)) {
                        //find what direction door should open by averaging the
                        //  2 neighboring points of wallfind[0] & wallfind[1].
                        int i = wallfind[j] - 1;
                        if (i < startwall) {
                            i = endwall;
                        }

                        Wall wal2 = boardService.getWall(i);
                        if (wal2 == null) {
                            continue;
                        }

                        dax2 = ((wal2.getX() + wal.getWall2().getX()) >> 1) - wal.getX();
                        day2 = ((wal2.getY() + wal.getWall2().getY()) >> 1) - wal.getY();
                        if (dax2 != 0) {
                            dax2 = wal.getWall2().getWall2().getX();
                            dax2 -= wal.getWall2().getX();
                            setanimation(sn, wallfind[j], wal.getX() + dax2, sp, WALLX);
                            setanimation(sn, i, wal2.getX() + dax2, sp, WALLX);
                            setanimation(sn, wal.getPoint2(), wal.getWall2().getX() + dax2, sp, WALLX);
                            callsound(sn, ii);
                        } else if (day2 != 0) {
                            day2 = wal.getWall2().getWall2().getY();
                            day2 -= wal.getWall2().getY();
                            setanimation(sn, wallfind[j], wal.getY() + day2, sp, WALLY);
                            setanimation(sn, i, wal2.getY() + day2, sp, WALLY);
                            setanimation(sn, wal.getPoint2(), wal.getWall2().getY() + day2, sp, WALLY);
                            callsound(sn, ii);
                        }
                    } else if (wal != null) {
                        int i = wallfind[j] - 1;
                        if (i < startwall) {
                            i = endwall;
                        }

                        Wall wal2 = boardService.getWall(i);
                        if (wal2 == null) {
                            continue;
                        }

                        dax2 = ((wal2.getX() + wal.getWall2().getX()) >> 1) - wal.getX();
                        day2 = ((wal2.getY() + wal.getWall2().getY()) >> 1) - wal.getY();
                        if (dax2 != 0) {
                            // setanimation(short animsect,long *animptr, long thegoal, long thevel)
                            setanimation(sn, wallfind[j], dax, sp, WALLX);
                            setanimation(sn, i, dax + dax2, sp, WALLX);
                            setanimation(sn, wal.getPoint2(), dax + dax2, sp, WALLX);
                            callsound(sn, ii);
                        } else if (day2 != 0) {
                            setanimation(sn, wallfind[j], day, sp, WALLY);
                            setanimation(sn, i, day + day2, sp, WALLY);
                            setanimation(sn, wal.getPoint2(), day + day2, sp, WALLY);
                            callsound(sn, ii);
                        }
                    }
                }

            }
            return;

            case 15://Warping elevators
            {
                Sprite s = boardService.getSprite(ii);
                if (s == null || s.getPicnum() != APLAYER) {
                    return;
                }

                ListNode<Sprite> n = boardService.getSectNode(sn);
                while (n != null) {
                    if (n.get().getPicnum() == SECTOREFFECTOR && n.get().getLotag() == 17) {
                        break;
                    }
                    n = n.getNext();
                }

                if (n == null) {
                    return;
                }

                int i = n.getIndex();
                if (s.getSectnum() == sn) {
                    if (activatewarpelevators(i, -1)) {
                        activatewarpelevators(i, 1);
                    } else if (activatewarpelevators(i, 1)) {
                        activatewarpelevators(i, -1);
                    }
                    return;
                } else {
                    if (ssec.getFloorz() > n.get().getZ()) {
                        activatewarpelevators(i, -1);
                    } else {
                        activatewarpelevators(i, 1);
                    }
                }

                return;
            }
            case 16:
            case 17: {
                int i = getanimationgoal(ssec, FLOORZ);
                if (i == -1) {
                    i = engine.nextsectorneighborz(sn, ssec.getFloorz(), 1, 1);
                    if (i == -1) {
                        i = engine.nextsectorneighborz(sn, ssec.getFloorz(), 1, -1);
                        if (i == -1) {
                            return;
                        }
                        // FIXME The same block
                        Sector sec = boardService.getSector(i);
                        if (sec != null) {
                            int j = sec.getFloorz();
                            setanimation(sn, sn, j, ssec.getExtra(), FLOORZ);
                        }
                    } else {
                        // FIXME The same block
                        Sector sec = boardService.getSector(i);
                        if (sec != null) {
                            int j = sec.getFloorz();
                            setanimation(sn, sn, j, ssec.getExtra(), FLOORZ);
                        }
                    }
                    callsound(sn, ii);
                }

                return;
            }
            case 18:
            case 19: {

                int i = getanimationgoal(ssec, FLOORZ);

                if (i == -1) {
                    i = engine.nextsectorneighborz(sn, ssec.getFloorz(), 1, -1);
                    if (i == -1) {
                        i = engine.nextsectorneighborz(sn, ssec.getFloorz(), 1, 1);
                    }
                    if (i == -1) {
                        return;
                    }
                    Sector sec = boardService.getSector(i);
                    if (sec != null) {
                        int j = sec.getFloorz();
                        int q = ssec.getExtra();
                        int l = ssec.getCeilingz() - ssec.getFloorz();
                        setanimation(sn, sn, j, q, FLOORZ);
                        setanimation(sn, sn, j + l, q, CEILZ);
                        callsound(sn, ii);
                    }
                }
                return;
            }
            case 29: {
                int j = 0;
                if ((ssec.getLotag() & 0x8000) != 0) {
                    Sector sec = boardService.getSector(engine.nextsectorneighborz(sn, ssec.getCeilingz(), 1, 1));
                    if (sec != null) {
                        j = sec.getFloorz();
                    }
                } else {
                    Sector sec = boardService.getSector(engine.nextsectorneighborz(sn, ssec.getCeilingz(), -1, -1));
                    if (sec != null) {
                        j = sec.getCeilingz();
                    }
                }

                ListNode<Sprite> ni = boardService.getStatNode(3); // Effectors
                while (ni != null) {
                    Sprite sp = ni.get();
                    if ((sp.getLotag() == 22) && (sp.getHitag() == ssec.getHitag())) {
                        Sector sec = boardService.getSector(sp.getSectnum());
                        if (sec != null) {
                            sec.setExtra(-sec.getExtra());
                            int i = ni.getIndex();
                            hittype[i].temp_data[0] = sn;
                            hittype[i].temp_data[1] = 1;
                        }
                    }
                    ni = ni.getNext();
                }

                ssec.setLotag(ssec.getLotag() ^ 0x8000);

                setanimation(sn, sn, j, ssec.getExtra(), CEILZ);

                callsound(sn, ii);

                return;
            }
            case 20: {
                int j = 0;
                boolean REDODOOR;
                do {
                    REDODOOR = false;
                    if ((ssec.getLotag() & 0x8000) != 0) {
                        ListNode<Sprite> node = boardService.getSectNode(sn);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (sp.getStatnum() == 3 && sp.getLotag() == 9) {
                                j = sp.getZ();
                                break;
                            }
                            node = node.getNext();
                        }
                        if (node == null) {
                            j = ssec.getFloorz();
                        }
                    } else {
                        j = engine.nextsectorneighborz(sn, ssec.getCeilingz(), -1, -1);
                        Sector sec = boardService.getSector(j);
                        if (sec != null) {
                            j = sec.getCeilingz();
                        } else {
                            ssec.setLotag(ssec.getLotag() | 32768);
                            REDODOOR = true;
                        }
                    }
                } while (REDODOOR);

                ssec.setLotag(ssec.getLotag() ^ 0x8000);

                setanimation(sn, sn, j, ssec.getExtra(), CEILZ);
                callsound(sn, ii);

                return;
            }
            case 21: {
                int i = getanimationgoal(ssec, FLOORZ);
                if (i >= 0) {
                    if (gAnimationData[i].goal == ssec.getCeilingz()) {
                        Sector sec = boardService.getSector(engine.nextsectorneighborz(sn, ssec.getCeilingz(), 1, 1));
                        if (sec != null) {
                            gAnimationData[i].goal = sec.getFloorz();
                        }
                    } else {
                        gAnimationData[i].goal = ssec.getCeilingz();
                    }
                } else {
                    int j = ssec.getCeilingz();
                    if (ssec.getCeilingz() == ssec.getFloorz()) {
                        Sector sec = boardService.getSector(engine.nextsectorneighborz(sn, ssec.getCeilingz(), 1, 1));
                        if (sec != null) {
                            j = sec.getFloorz();
                        }
                    }

                    ssec.setLotag(ssec.getLotag() ^ 0x8000);
                    if (setanimation(sn, sn, j, ssec.getExtra(), FLOORZ) >= 0) {
                        callsound(sn, ii);
                    }
                }
                return;
            }
            case 22:

                // REDODOOR22:

                if ((ssec.getLotag() & 0x8000) != 0) {
                    int q = (ssec.getCeilingz() + ssec.getFloorz()) >> 1;
                    setanimation(sn, sn, q, ssec.getExtra(), FLOORZ);
                    setanimation(sn, sn, q, ssec.getExtra(), CEILZ);
                } else {
                    int fsect = engine.nextsectorneighborz(sn, ssec.getFloorz(), 1, 1);
                    Sector sec = boardService.getSector(fsect);
                    if (sec != null) {
                        int q = sec.getFloorz();
                        setanimation(sn, sn, q, ssec.getExtra(), FLOORZ);
                    }
                    int csect = engine.nextsectorneighborz(sn, ssec.getCeilingz(), -1, -1);
                    sec = boardService.getSector(csect);
                    if (sec != null) {
                        int q = sec.getCeilingz();
                        setanimation(sn, sn, q, ssec.getExtra(), CEILZ);
                    }
                }

                ssec.setLotag(ssec.getLotag() ^ 0x8000);

                callsound(sn, ii);

                return;

            case 23: { //Swingdoor
                int q = 0;

                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    int i = ni.getIndex();
                    if (sp.getLotag() == 11
                            && sp.getSectnum() == sn
                            && hittype[i].temp_data[4] == 0) {
                        break;
                    }
                    ni = ni.getNext();
                }

                if (ni == null) {
                    return;
                }

                final Sprite s = ni.get();
                Sector seci = boardService.getSector(s.getSectnum());
                if (seci == null) {
                    return;
                }

                int l = seci.getLotag() & 0x8000;
                ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    Sector sec = boardService.getSector(sp.getSectnum());

                    int i = ni.getIndex();
                    if (sec != null && l == (sec.getLotag() & 0x8000) && sp.getLotag() == 11
                            && s.getHitag() == sp.getHitag() && hittype[i].temp_data[4] == 0) {
                        if ((sec.getLotag() & 0x8000) != 0) {
                            sec.setLotag(sec.getLotag() & 0x7fff);
                        } else {
                            sec.setLotag(sec.getLotag() | 0x8000);
                        }
                        hittype[i].temp_data[4] = 1;
                        hittype[i].temp_data[3] = -hittype[i].temp_data[3];
                        if (q == 0) {
                            callsound(sn, i);
                            q = 1;
                        }
                    }
                    ni = ni.getNext();
                }
                return;
            }
            case 25: { //Subway type sliding doors
                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null)// Find the sprite
                {
                    Sprite sp = ni.get();
                    if ((sp.getLotag()) == 15 && sp.getSectnum() == sn) {
                        break; // Found the sectoreffector.
                    }
                    ni = ni.getNext();
                }

                if (ni == null) {
                    return;
                }

                Sprite spr = ni.get();
                ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    int i = ni.getIndex();
                    Sector sec = boardService.getSector(sp.getSectnum());

                    if (sec != null && sp.getHitag() == spr.getHitag()) {
                        if (sp.getLotag() == 15) {
                            sec.setLotag(sec.getLotag() ^ 0x8000); // Toggle the open or close
                            sp.setAng(sp.getAng() + 1024);
                            if (hittype[i].temp_data[4] != 0) {
                                callsound(sp.getSectnum(), i);
                            }
                            callsound(sp.getSectnum(), i);
                            if ((sec.getLotag() & 0x8000) != 0) {
                                hittype[i].temp_data[4] = 1;
                            } else {
                                hittype[i].temp_data[4] = 2;
                            }
                        }
                    }
                    ni = ni.getNext();
                }
                return;
            }
            case 27: { // Extended bridge
                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    if ((sp.getLotag() & 0xff) == 20 && sp.getSectnum() == sn) { // Bridge
                        ssec.setLotag(ssec.getLotag() ^ 0x8000);
                        if ((ssec.getLotag() & 0x8000) != 0) { // OPENING
                            hittype[ni.getIndex()].temp_data[0] = 1;
                        } else {
                            hittype[ni.getIndex()].temp_data[0] = 2;
                        }
                        callsound(sn, ii);
                        break;
                    }
                    ni = ni.getNext();
                }
                return;
            }
            case 28: {
                // activate the rest of them
                int j = -1;
                ListNode<Sprite> ni = boardService.getSectNode(sn);
                while (ni != null) {
                    Sprite sp = ni.get();
                    if (sp.getStatnum() == 3 && (sp.getLotag() & 0xff) == 21) {
                        break; // Found it
                    }
                    ni = ni.getNext();
                }

                if (ni != null) {
                    j = ni.get().getHitag();
                }

                ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    int l = ni.getIndex();
                    if ((sp.getLotag() & 0xff) == 21 && hittype[l].temp_data[0] == 0 &&
                            (sp.getHitag()) == j) {
                        hittype[l].temp_data[0] = 1;
                    }
                    ni = ni.getNext();
                }
                callsound(sn, ii);
            }
        }
    }

    public static void operaterespawns(int low) {
        ListNode<Sprite> ni = boardService.getStatNode(11);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            Sprite sp = ni.get();
            if (sp.getLotag() == low) {
                switch (sp.getPicnum()) {
                    case RESPAWN:
                        if (sp.getHitag() < 0 || sp.getHitag() >= MAXTILES || (badguypic(sp.getHitag()) && ud.monsters_off)) {
                            break;
                        }

                        int j = spawn(ni.getIndex(), TRANSPORTERSTAR);
                        Sprite sj = boardService.getSprite(j);
                        if (sj != null) {
                            sj.setZ(sj.getZ() - (32 << 8));
                        }
                        sp.setExtra(66 - 12);   // Just a way to killit
                        break;
                    case 7424:
                        if (!ud.monsters_off) {
                            engine.changespritestat(ni.getIndex(), (short) 119);
                        }
                        break;
                }
            }
            ni = nexti;
        }
    }

    public static void operateactivators(int low, int snum) {
        for (int i = numcyclers - 1; i >= 0; i--) {
            short[] p = cyclers[i];

            if (p[4] == low) {
                p[5] ^= 1;
                Sector sec = boardService.getSector(p[0]);
                if (sec != null) {
                    sec.setCeilingshade(p[3]);
                    sec.setFloorshade(p[3]);
                    for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                        Wall wal = wn.get();
                        wal.setShade((byte) p[3]);
                    }
                }
            }
        }

        int k = -1;
        ListNode<Sprite> ni = boardService.getStatNode(8);
        while (ni != null) {
            Sprite sp = ni.get();
            Sector sec = boardService.getSector(sp.getSectnum());

            if (sec != null && sp.getLotag() == low) {
                if (sp.getPicnum() == ACTIVATORLOCKED) {
                    if ((sec.getLotag() & 16384) != 0) {
                        sec.setLotag(sec.getLotag() & 65535 - 16384);
                    } else {
                        sec.setLotag(sec.getLotag() | 16384);
                    }

                    if (snum >= 0) {
                        if ((sec.getLotag() & 16384) != 0) {
                            FTA(4, ps[snum]);
                        } else {
                            FTA(8, ps[snum]);
                        }
                    }
                } else {
                    switch (sp.getHitag()) {
                        case 0:
                            break;
                        case 1:
                            if (sec.getFloorz() != sec.getCeilingz()) {
                                ni = ni.getNext();
                                continue;
                            }
                            break;
                        case 2:
                            if (sec.getFloorz() == sec.getCeilingz()) {
                                ni = ni.getNext();
                                continue;
                            }
                            break;
                    }

                    if (sec.getLotag() < 3) {
                        ListNode<Sprite> node = boardService.getSectNode(sp.getSectnum());
                        while (node != null) {
                            Sprite jspr = node.get();
                            if (jspr.getStatnum() == 3) {
                                switch (jspr.getLotag()) {
                                    case 36:
                                    case 31:
                                    case 32:
                                    case 18:
                                        int j = node.getIndex();
                                        hittype[j].temp_data[0] = 1 - hittype[j].temp_data[0];
                                        callsound(sp.getSectnum(), j);
                                        break;
                                }
                            }
                            node = node.getNext();
                        }
                    }

                    int i = ni.getIndex();
                    if (k == -1 && (sec.getLotag() & 0xff) == 22) {
                        k = (short) callsound(sp.getSectnum(), i);
                    }

                    operatesectors(sp.getSectnum(), i);
                }
            }
            ni = ni.getNext();
        }

        operaterespawns(low);
    }

    public static void operatemasterswitches(int low) {
        ListNode<Sprite> node = boardService.getStatNode(6);
        while (node != null) {
            Sprite sp = node.get();
            if (sp.getPicnum() == MASTERSWITCH && sp.getLotag() == low && sp.getYvel() == 0) {
                sp.setYvel(1);
            }
            node = node.getNext();
        }
    }

    public static void operateforcefields(int s, int low) {
        for (int p = numanimwalls; p >= 0; p--) {
            int i = animwall[p].wallnum;
            Wall wal = boardService.getWall(i);
            if (wal == null) {
                continue;
            }

            if (low == wal.getLotag() || low == -1) {
                if (wal.getOverpicnum() == BIGFORCE) {
                    animwall[p].tag = 0;

                    if (wal.getCstat() != 0) {
                        wal.setCstat(0);

                        Sprite sp = boardService.getSprite(s);
                        if (sp != null && sp.getPicnum() == SECTOREFFECTOR && sp.getLotag() == 30) {
                            wal.setLotag(0);
                        }
                    } else {
                        wal.setCstat(85);
                    }
                }
            }
        }
    }

    public static boolean checkhitswitch(int snum, int w, int switchtype) {
        if (w < 0) {
            return false;
        }

        int correctdips = 1;
        int numdips = 0;

        int switchpal;
        int lotag, hitag, picnum;
        int sx, sy;

        if (switchtype == 1) // A wall sprite
        {
            Sprite s = boardService.getSprite(w);
            if (s == null) {
                return false;
            }

            lotag = s.getLotag();
            if (lotag == 0) {
                return false;
            }

            hitag = s.getHitag();
            sx = s.getX();
            sy = s.getY();
            picnum = s.getPicnum();
            switchpal = s.getPal();
        } else {
            Wall wal = boardService.getWall(w);
            if (wal == null) {
                return false;
            }

            lotag = wal.getLotag();
            if (lotag == 0) {
                return false;
            }

            hitag = wal.getHitag();
            sx = wal.getX();
            sy = wal.getY();
            picnum = wal.getPicnum();
            switchpal = wal.getPal();
        }

        switch (picnum) {
            case 121:
            case 122:
            case 125:
            case 126:
            case 2259:
            case 2260:
                break;
            case 82:
            case 129:
                if (ps[snum].access_incs == 0) {
                    if (currentGame.getCON().key_quotes == null) {
                        currentGame.getCON().key_quotes = new char[3][64];
                        System.arraycopy(currentGame.getCON().fta_quotes[70], 0, currentGame.getCON().key_quotes[0], 0, 64);
                        System.arraycopy(currentGame.getCON().fta_quotes[71], 0, currentGame.getCON().key_quotes[1], 0, 64);
                        System.arraycopy(currentGame.getCON().fta_quotes[72], 0, currentGame.getCON().key_quotes[2], 0, 64);
                    }

                    if (switchpal == 0) {
                        if (ps[snum].gotkey[1] != 0) {
                            ps[snum].access_incs = 1;
                        } else {
                            if (cfg.gColoredKeys) {
                                buildString(currentGame.getCON().fta_quotes[70], 0, "BLUE KEY REQUIRED"); //v0.751
                            } else {
                                System.arraycopy(currentGame.getCON().key_quotes[0], 0, currentGame.getCON().fta_quotes[70], 0, 64);
                            }
                            FTA(70, ps[snum]);
                        }
                    } else if (switchpal == 21) {
                        if (ps[snum].gotkey[2] != 0) {
                            ps[snum].access_incs = 1;
                        } else {
                            if (cfg.gColoredKeys) {
                                buildString(currentGame.getCON().fta_quotes[71], 0, "RED KEY REQUIRED");
                            } else {
                                System.arraycopy(currentGame.getCON().key_quotes[1], 0, currentGame.getCON().fta_quotes[71], 0, 64);
                            }
                            FTA(71, ps[snum]);
                        }
                    } else if (switchpal == 23) {
                        if (ps[snum].gotkey[3] != 0) {
                            ps[snum].access_incs = 1;
                        } else {
                            if (cfg.gColoredKeys) {
                                buildString(currentGame.getCON().fta_quotes[72], 0, "BROWN KEY REQUIRED");
                            } else {
                                System.arraycopy(currentGame.getCON().key_quotes[2], 0, currentGame.getCON().fta_quotes[72], 0, 64);
                            }
                            FTA(72, ps[snum]);
                        }
                    }

                    if (ps[snum].access_incs == 1) {
                        if (switchtype == 0) {
                            ps[snum].access_wallnum = (short) w;
                        } else {
                            ps[snum].access_spritenum = (short) w;
                        }
                    }

                    return false;
                }
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 98:
            case 99:
            case 100:
            case 101:
            case 123:
            case 124:
            case 127:
            case 128:
            case 250:
            case 251:
            case 2214:
            case 2222:
            case 2223:
            case 2224:
            case 2225:
            case 2226:
            case 2227:
            case 2249:
            case 2250:
            case 2254:
            case 2255:
            case 2697:
            case 2698:
            case 2707:
            case 2708:

                //RA
            case 8048:
            case 8049:
            case 8050:
            case 8051:
            case 8464:
            case 8660:
                if (check_activator_motion(lotag)) {
                    return false;
                }
                break;
            default:
                if (!isadoorwall(picnum)) {
                    return false;
                }
                break;
        }

        ListNode<Sprite> node = boardService.getStatNode(0);
        while (node != null) {
            Sprite sp = node.get();
            int i = node.getIndex();
            if (lotag == sp.getLotag()) {
                switch (sp.getPicnum()) {
                    case 85:
                    case 87:
                    case 89:
                    case 91:
                    case 93:
                    case 95:
                    case 124:
                    case 128:
                    case 251:
                    case 2223:
                    case 2225:
                    case 2227:
                    case 2250:
                    case 2255:
                    case 2698:
                    case 2708:
                        if (sp.getPicnum() == 95) {
                            plantProcess = true;
                        }
                        if (sp.getHitag() != 999) {
                            sp.setPicnum(sp.getPicnum() - 1);
                        }
                        break;
                    case 82:
                    case 84:
                    case 86:
                    case 88:
                    case 90:
                    case 92:
                    case 94:
                    case 123:
                    case 127:
                    case 129:
                    case 250:
                    case 2222:
                    case 2224:
                    case 2226:
                    case 2249:
                    case 2254:
                    case 2697:
                    case 2707:
                    case 8660:
                        if (sp.getPicnum() != 127 || sp.getHitag() != 999) {
                            if (sp.getPicnum() == 94) {
                                plantProcess = false;
                            }
                            if (sp.getPicnum() == 8660) {
                                BellTime = 132;
                                word_119BE0 = (short) i;
                            }
                            sp.setPicnum(sp.getPicnum() + 1);
                        } else {
                            ListNode<Sprite> nx = boardService.getStatNode(107);
                            while (nx != null) {
                                ListNode<Sprite> next = nx.getNext();
                                Sprite xsp = nx.get();
                                int x = (short) nx.getIndex();

                                if (xsp.getPicnum() == 3410) {
                                    xsp.setPicnum(3411);
                                    xsp.setHitag(100);
                                    xsp.setExtra(0);
                                    spritesound(474, x);
                                } else if (xsp.getPicnum() == 295) {
                                    engine.deletesprite(x);
                                }

                                nx = next;
                            }
                            sp.setPicnum(sp.getPicnum() + 1);
                        }
                        break;

                    case MULTISWITCH:
                    case MULTISWITCH + 1:
                    case MULTISWITCH + 2:
                    case MULTISWITCH + 3:
                        sp.setPicnum(sp.getPicnum() + 1);
                        if (sp.getPicnum() > (MULTISWITCH + 3)) {
                            sp.setPicnum(MULTISWITCH);
                        }
                        break;

                    case 121:
                    case 125:
                    case 2259:
                        if (switchtype == 1 && w == i) {
                            sp.setPicnum(sp.getPicnum() + 1);
                        } else if (sp.getHitag() == 0) {
                            correctdips++;
                        }
                        numdips++;
                        break;
                    case 122:
                    case 126:
                    case 2260:
                        if (switchtype == 1 && w == i) {
                            sp.setPicnum(sp.getPicnum() - 1);
                        } else if (sp.getHitag() == 1) {
                            correctdips++;
                        }
                        numdips++;
                        break;
                    case 2214:
                        //	            	checknextlevel();
                        sp.setPicnum(sp.getPicnum() + 1);
                        break;

                    //RA
                    case 8048:
                    case 8049:
                    case 8050:
                    case 8051:
                        sp.setPicnum(sp.getPicnum() + 1);
                        if (sp.getPicnum() > 8051) {
                            sp.setPicnum(8048);
                        }
                        break;
                }
            }
            node = node.getNext();
        }

        for (int i = 0; i < boardService.getWallCount(); i++) {
            Wall wal = boardService.getWall(i);
            if (wal == null) {
                continue;
            }

            if (lotag == wal.getLotag()) {
                switch (wal.getPicnum()) {
                    case 121:
                    case 125:
                    case 2259:
                        if (switchtype == 0 && i == w) {
                            wal.setPicnum(wal.getPicnum() + 1);
                        } else if (wal.getHitag() == 0) {
                            correctdips++;
                        }
                        numdips++;
                        break;
                    case 122:
                    case 126:
                    case 2260:
                        if (switchtype == 0 && i == w) {
                            wal.setPicnum(wal.getPicnum() - 1);
                        } else if (wal.getHitag() == 1) {
                            correctdips++;
                        }
                        numdips++;
                        break;
                    case MULTISWITCH:
                    case MULTISWITCH + 1:
                    case MULTISWITCH + 2:
                    case MULTISWITCH + 3:
                        wal.setPicnum(wal.getPicnum() + 1);
                        if (wal.getPicnum() > (MULTISWITCH + 3)) {
                            wal.setPicnum(MULTISWITCH);
                        }
                        break;
                    case 82:
                    case 84:
                    case 86:
                    case 88:
                    case 90:
                    case 123:
                    case 127:
                    case 129:
                    case 250:
                    case 2222:
                    case 2224:
                    case 2226:
                    case 2249:
                    case 2254:
                    case 2697:
                    case 2707:
                    case 8660:
                        wal.setPicnum(wal.getPicnum() + 1);
                        break;
                    case 85:
                    case 87:
                    case 89:
                    case 91:
                    case 124:
                    case 128:
                    case 251:
                    case 2223:
                    case 2225:
                    case 2227:
                    case 2250:
                    case 2255:
                    case 2698:
                    case 2708:
                        wal.setPicnum(wal.getPicnum() - 1);
                        break;

                    //RA
                    case 8048:
                    case 8049:
                    case 8050:
                    case 8051:
                        wal.setPicnum(wal.getPicnum() + 1);
                        if (wal.getPicnum() > 8051) {
                            wal.setPicnum(8048);
                        }
                        break;
                }
            }
        }

        if (lotag == (short) 65535) {
            LeaveMap();
            if (ud.from_bonus != 0) {
                ud.level_number = ud.from_bonus;
                ud.from_bonus = 0;
            } else {
                ud.level_number++;
            }
            return true;
        }

        final boolean b = picnum == 121 || picnum == 122 ||
                picnum == 125 || picnum == 126 ||
                picnum == 2259 || picnum == 2260;

        switch (picnum) {
            default:
                if (!isadoorwall(picnum)) {
                    break;
                }

            case 121:
            case 122:
            case 125:
            case 126:
            case 2259:
            case 2260:
                if (b) {
                    if (picnum == 2259 || picnum == 2260) {
                        if (switchtype == 1) {
                            xyzsound(ALIEN_SWITCH1, w, sx, sy, ps[snum].posz);
                        } else {
                            xyzsound(ALIEN_SWITCH1, ps[snum].i, sx, sy, ps[snum].posz);
                        }
                    } else {
                        if (switchtype == 1) {
                            xyzsound(SWITCH_ON, w, sx, sy, ps[snum].posz);
                        } else {
                            xyzsound(SWITCH_ON, ps[snum].i, sx, sy, ps[snum].posz);
                        }
                    }
                    if (numdips != correctdips) {
                        break;
                    }
                    xyzsound(END_OF_LEVEL_WARN, ps[snum].i, sx, sy, ps[snum].posz);
                }

            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 250:
            case 251:
            case 2249:
            case 2250:
            case 2707:
            case 2708:

            case 82:
            case 123:
            case 124:
            case 127:
            case 128:
            case 129:
            case 2222:
            case 2223:
            case 2224:
            case 2225:
            case 2226:
            case 2227:
            case 2254:
            case 2255:
            case 2697:
            case 2698:
            case MULTISWITCH:
            case MULTISWITCH + 1:
            case MULTISWITCH + 2:
            case MULTISWITCH + 3:

                //RA
            case 8048:
            case 8049:
            case 8050:
            case 8051:
            case 8660:
            case 8464:

                if (picnum == 8660) {
                    BellTime = 132;
                    word_119BE0 = (short) w;
                    Sprite s = boardService.getSprite(w);
                    if (s != null) {
                        s.setPicnum(s.getPicnum() + 1);
                    }
                }

                if (picnum == 8464) {
                    Sprite s = boardService.getSprite(w);
                    if (s != null) {
                        s.setPicnum(s.getPicnum() + 1);
                    }
                    if (hitag == 10001) {
                        if (ps[snum].SeaSick == 0) {
                            ps[snum].SeaSick = 350;
                        }
                        operateactivators(668, snum);
                        operatemasterswitches(668);
                        spritesound(328, ps[snum].i);
                        return true;
                    }
                } else {
                    if (hitag == 10000) {
                        if (picnum == MULTISWITCH
                                || picnum == (MULTISWITCH + 1)
                                || picnum == (MULTISWITCH + 2)
                                || picnum == (MULTISWITCH + 3)
                                || picnum == 8048
                                || picnum == 8049
                                || picnum == 8050
                                || picnum == 8051) {
                            xyzsound(76, w, sx, sy, ps[snum].posz);

                            int count = 0;
                            for (int k = 0; k < boardService.getSpriteCount(); ++k) {
                                Sprite s = boardService.getSprite(k);
                                if (s != null && (s.getPicnum() == 98
                                        || s.getPicnum() == 8048)
                                        && s.getHitag() == 10000
                                        && count < 3) {
                                    chitsw[count++] = k;
                                }
                            }

                            if (count == 3) {
                                xyzsound(78, w, sx, sy, ps[snum].posz);
                                for (int k = 0; k < count; ++k) {
                                    Sprite s = boardService.getSprite(chitsw[k]);
                                    if (s != null) {
                                        s.setHitag(0);
                                        if (picnum < 8048) {
                                            s.setPicnum(101);
                                        } else {
                                            s.setPicnum(8051);
                                        }
                                        checkhitswitch(snum, chitsw[k], 1);
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }

                //411
                if (picnum == MULTISWITCH || picnum == (MULTISWITCH + 1) ||
                        picnum == (MULTISWITCH + 2) || picnum == (MULTISWITCH + 3)) {
                    lotag += picnum - MULTISWITCH;
                }

                if (picnum == 8048 || picnum == 8049 ||
                        picnum == 8050 || picnum == 8051) {
                    lotag += picnum - 8048;
                }

                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null) {
                    int x = ni.getIndex();
                    Sprite xsp = ni.get();
                    if (((xsp.getHitag()) == lotag)) {
                        switch (xsp.getLotag()) {
                            case 12:
                                //RA
                            case 46:
                            case 47:
                            case 48: {
                                Sector xsec = boardService.getSector(xsp.getSectnum());
                                if (xsec != null) {
                                    xsec.setFloorpal(0);
                                }
                                hittype[x].temp_data[0]++;
                                if (hittype[x].temp_data[0] == 2) {
                                    hittype[x].temp_data[0]++;
                                }
                                break;
                            }
                            case 24:
                            case 34:
                            case 25:
                                hittype[x].temp_data[4] ^= 1;
                                if (hittype[x].temp_data[4] != 0) {
                                    FTA(15, ps[snum]);
                                } else {
                                    FTA(2, ps[snum]);
                                }
                                break;
                            case 21:
                                FTA(2, ps[screenpeek]);
                                break;
                        }
                    }
                    ni = ni.getNext();
                }

                operateactivators(lotag, snum);
                operateforcefields(ps[snum].i, lotag);
                operatemasterswitches(lotag);

                if (b) {
                    return true;
                }

                if (hitag == 0 && !isadoorwall(picnum)) {
                    if (switchtype == 1) {
                        xyzsound(SWITCH_ON, w, sx, sy, ps[snum].posz);
                    } else {
                        xyzsound(SWITCH_ON, ps[snum].i, sx, sy, ps[snum].posz);
                    }
                } else if (hitag != 0) {
                    if (hitag < NUM_SOUNDS) {
                        if (switchtype == 1 && (currentGame.getCON().soundm[hitag] & 4) == 0) {
                            xyzsound(hitag, w, sx, sy, ps[snum].posz);
                        } else {
                            spritesound(hitag, ps[snum].i);
                        }
                    }
                }

                return true;
        }
        return false;
    }

    public static void activatebysector(int sect, int j) {
        ListNode<Sprite> node = boardService.getSectNode(sect);
        while (node != null) {
            Sprite sp = node.get();
            if (sp.getPicnum() == ACTIVATOR) {
                operateactivators(sp.getLotag(), -1);
            }
            node = node.getNext();
        }

        Sector sec = boardService.getSector(sect);
        if (sec != null && sec.getLotag() != 22) {
            operatesectors(sect, j);
        }
    }

    public static void checkplayerhurt(PlayerStruct p, int moveHit) {
        if ((moveHit & kHitTypeMask) == kHitSprite) {
            moveHit &= kHitIndexMask;

            Sprite hsp = boardService.getSprite(moveHit);
            if (hsp != null) {
                switch (hsp.getPicnum()) {
                    case 1194:
                        //RA
                    case 2430:
                    case 2431:
                    case 2432:
                    case 2443:
                    case 2446:
                    case 2451:
                    case 2455:
                        if (p.hurt_delay < 8) {
                            Sprite psp =  boardService.getSprite(p.i);
                            if (psp != null) {
                                psp.setExtra(psp.getExtra() - 5);
                            }

                            p.hurt_delay = 16;
                            p.pals_time = 32;
                            p.pals[0] = 32;
                            p.pals[1] = 0;
                            p.pals[2] = 0;
                            spritesound(DUKE_LONGTERM_PAIN, p.i);
                        }
                        break;
                }
            }
            return;
        }

        if ((moveHit & kHitTypeMask) != HIT_WALL) {
            return;
        }
        moveHit &= kHitIndexMask;
        Wall hw = boardService.getWall(moveHit);
        if (hw == null) {
            return;
        }

        if (p.hurt_delay > 0) {
            p.hurt_delay--;
        } else if ((hw.getCstat() & 85) != 0) {
            if (hw.getOverpicnum() == BIGFORCE) {
                p.hurt_delay = 26;
                if (IsOriginalDemo()) {
                    checkhitwall(p.i, moveHit,
                            p.posx + (EngineUtils.cos(((int) p.ang) & 2047) >> 9),
                            p.posy + (EngineUtils.sin((int) p.ang & 2047) >> 9),
                            p.posz, -1);
                } else {
                    checkhitwall(p.i, moveHit,
                            (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 512.0f)),
                            (int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 512.0f)),
                            p.posz, -1);
                }
            }
        }
    }

    @CommonPart
    public static void allignwarpelevators() {
        ListNode<Sprite> ni = boardService.getStatNode(3);
        while (ni != null) {
            Sprite sp = ni.get();
            if (sp.getLotag() == 17 && sp.getShade() > 16) {
                ListNode<Sprite> node = boardService.getStatNode(3);
                while (node != null) {
                    Sprite sp2 = node.get();
                    if ((sp2.getLotag()) == 17 && ni.getIndex() != node.getIndex() && (sp.getHitag()) == (sp2.getHitag())) {
                        Sector sec = boardService.getSector(sp.getSectnum());
                        Sector sec2 = boardService.getSector(sp2.getSectnum());
                        if (sec != null && sec2 != null) {
                            sec2.setFloorz(sec.getFloorz());
                            sec2.setCeilingz(sec.getCeilingz());
                        }
                    }

                    node = node.getNext();
                }
            }
            ni = ni.getNext();
        }
    }

    @CommonPart
    public static void breakwall(int newpn, int spr, int dawallnum) {
        Wall wal = boardService.getWall(dawallnum);
        if (wal != null) {
            wal.setPicnum((short) newpn);
            spritesound(VENT_BUST, spr);
            spritesound(GLASS_HEAVYBREAK, spr);
            lotsofglass(spr, dawallnum, 10);
        }
    }

    public static void checkhitwall(int spr, final int dawallnum, int x, int y, int z, int atwith) {
        Wall wal = boardService.getWall(dawallnum);
        if (wal == null) {
            return;
        }

        if (wal.getOverpicnum() == MIRROR) {
            switch (atwith) {
                case 26:
                case 1228:
                case 1273:
                case 1315:
                case 1324:
                case 1426:
                case 1774:
                    lotsofglass(spr, dawallnum, 70);
                    wal.setCstat(wal.getCstat() & ~16);
                    wal.setOverpicnum(70);
                    spritesound(GLASS_HEAVYBREAK, spr);
                    return;
            }
        }

        Sector nsec = boardService.getSector(wal.getNextsector());
        if (((wal.getCstat() & 16) != 0 || wal.getOverpicnum() == BIGFORCE) && nsec != null) {
            if (nsec.getFloorz() > z) {
                if (nsec.getFloorz() - nsec.getCeilingz() != 0) {
                    switch (wal.getOverpicnum()) {
                        case 210: {
                            wal.setOverpicnum(FANSPRITEBROKE);
                            wal.setCstat(wal.getCstat() & 65535 - 65);
                            Wall nw = boardService.getWall(wal.getNextwall());
                            if (nw != null) {
                                nw.setOverpicnum(FANSPRITEBROKE);
                                nw.setCstat(nw.getCstat() & 65535 - 65);
                            }
                            spritesound(VENT_BUST, spr);
                            spritesound(GLASS_BREAKING, spr);
                            return;
                        }
                        case GLASS:
                        case 1973: {
                            int sn = engine.updatesector(x, y, -1);
                            if (sn < 0) {
                                return;
                            }
                            wal.setOverpicnum(GLASS2);
                            if (wal.getOverpicnum() == 1973) {
                                rr_lotsofglass(spr, dawallnum, 64);
                            } else {
                                lotsofglass(spr, dawallnum, 10);
                            }
                            wal.setCstat(0);

                            Wall nw = boardService.getWall(wal.getNextwall());
                            if (nw != null) {
                                nw.setCstat(0);
                            }

                            int i = EGS(sn, x, y, z, SECTOREFFECTOR, 0, 0, 0, (short) ps[0].ang, 0, 0, spr, (short) 3);
                            Sprite sp =  boardService.getSprite(i);
                            if (sp != null) {
                                sp.setLotag(128);
                                hittype[i].temp_data[1] = 2;
                                hittype[i].temp_data[2] = dawallnum;
                                spritesound(GLASS_BREAKING, i);
                            }
                            return;
                        }
                        case 1063: {
                            int sn = engine.updatesector(x, y, -1);
                            if (sn < 0) {
                                return;
                            }
                            lotsofcolourglass(spr, dawallnum, 80);
                            wal.setCstat(0);
                            Wall nw = boardService.getWall(wal.getNextwall());
                            if (nw != null) {
                                nw.setCstat(0);
                            }
                            spritesound(VENT_BUST, spr);
                            spritesound(GLASS_BREAKING, spr);
                            return;
                        }
                    }
                }
            }
        }

        switch (wal.getPicnum()) {
            case COLAMACHINE:
            case VENDMACHINE:
                breakwall(wal.getPicnum() + 2, spr, dawallnum);
                spritesound(VENT_BUST, spr);
                return;
            case 3643: //Car textures
            case 3644:
            case 3645:
            case 3646: {
                Wall nw = boardService.getWall(wal.getNextwall());
                if (nw != null) {
                    ListNode<Sprite> ni = boardService.getSectNode(nw.getNextsector());
                    while (ni != null) {
                        ListNode<Sprite> next = ni.getNext();  //v0.751
                        int i = ni.getIndex();
                        Sprite nspr = ni.get();
                        if (nspr.getLotag() == 6) {
                            for (int k = 0; k < 16; k++) {
                                RANDOMSCRAP(nspr, i);
                            }

                            nspr.setDetail(nspr.getDetail() + 1);
                            Sector sec = boardService.getSector(nspr.getSectnum());
                            if (sec != null && nspr.getDetail() == 25) {
                                for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal2 = wn.get();
                                    Sector nsec2 = boardService.getSector(wal2.getNextsector());
                                    if (nsec2  == null) {
                                        break;
                                    }

                                    nsec2.setLotag(0);
                                }

                                sec.setLotag(0);
                                stopsound(nspr.getLotag());
                                spritesound(400, i);
                                engine.deletesprite(i);
                            }
                        }
                        ni = next;
                    }
                }
                return;
            }
            case 164:
            case 165:
            case 166:
            case 2217:

                lotsofglass(spr, dawallnum, 30);
                wal.setPicnum((short) (199 + (engine.krand() % 3)));
                spritesound(GLASS_HEAVYBREAK, spr);
                return;
            case 2229:
                wal.setPicnum(2233);
                lotsofmoney(boardService.getSprite(spr), ((engine.krand() & 7) + 1));
                spritesound(20, spr);
                return;
            case 72:
            case 74:
            case 76:
            case 244:
            case 246:
            case 1814:
            case 1939:
            case 1986:
            case 1988:
            case 2123:
            case 2125:
            case 2636:
            case 2878:
            case 2898:
            case 3200:
            case 3202:
            case 3204:
            case 3206:
            case 3208: {

                if (rnd(128)) {
                    spritesound(GLASS_HEAVYBREAK, spr);
                } else {
                    spritesound(GLASS_BREAKING, spr);
                }
                lotsofglass(spr, dawallnum, 30);

                if (wal.getPicnum() == 1814) {
                    wal.setPicnum(1817);
                }
                if (wal.getPicnum() == 1986) {
                    wal.setPicnum(1987);
                }
                if (wal.getPicnum() == 1939) {
                    wal.setPicnum(2004);
                }
                if (wal.getPicnum() == 1988) {
                    wal.setPicnum(2005);
                }
                if (wal.getPicnum() == 2898) {
                    wal.setPicnum(2899);
                }
                if (wal.getPicnum() == 2878) {
                    wal.setPicnum(2879);
                }
                if (wal.getPicnum() == 2123) {
                    wal.setPicnum(2124);
                }
                if (wal.getPicnum() == 2125) {
                    wal.setPicnum(2126);
                }
                if (wal.getPicnum() == 3200) {
                    wal.setPicnum(3201);
                }
                if (wal.getPicnum() == 3202) {
                    wal.setPicnum(3203);
                }
                if (wal.getPicnum() == 3204) {
                    wal.setPicnum(3205);
                }
                if (wal.getPicnum() == 3206) {
                    wal.setPicnum(3207);
                }
                if (wal.getPicnum() == 3208) {
                    wal.setPicnum(3209);
                }
                if (wal.getPicnum() == 2636) {
                    wal.setPicnum(2637);
                }
                if (wal.getPicnum() == 246) {
                    wal.setPicnum(247);
                }
                if (wal.getPicnum() == 244) {
                    wal.setPicnum(245);
                }
                if (wal.getPicnum() == 76) {
                    wal.setPicnum(77);
                }
                if (wal.getPicnum() == 72) {
                    wal.setPicnum(73);
                }
                if (wal.getPicnum() == 74) {
                    wal.setPicnum(75);
                }

                if (wal.getLotag() == 0) {
                    return;
                }

                int sn = wal.getNextsector();
                Sector sec = boardService.getSector(sn);
                if (sec == null) {
                    return;
                }
                int darkestwall = 0;

                for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                    Wall wal2 = wn.get();
                    if (wal2 != null && wal2.getShade() > darkestwall) {
                        darkestwall = wal2.getShade();
                    }
                }

                int j = engine.krand() & 1;
                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    if (sp.getHitag() == wal.getLotag() && sp.getLotag() == 3) {
                        int i = ni.getIndex();
                        hittype[i].temp_data[2] = j;
                        hittype[i].temp_data[3] = darkestwall;
                        hittype[i].temp_data[4] = 1;
                    }
                    ni = ni.getNext();
                }
                break;
            }
            //RA
            case 7433:
                wal.setPicnum(5018);
                spritesound(20, spr);
                return;
            case 7441:
                wal.setPicnum(5016);
                spritesound(20, spr);
                return;
            case 7453:
                wal.setPicnum(5035);
                spritesound(495, spr);
                return;
            case 7540:
                wal.setPicnum(5023);
                spritesound(20, spr);
                return;
            case 7552:
                wal.setPicnum(5021);
                spritesound(20, spr);
                return;
            case 7553:
                wal.setPicnum(5020);
                spritesound(20, spr);
                return;
            case 7554:
                wal.setPicnum(5025);
                spritesound(20, spr);
                return;
            case 7555:
                wal.setPicnum(5015);
                spritesound(20, spr);
                return;
            case 7557:
                wal.setPicnum(5019);
                spritesound(20, spr);
                return;
            case 7558:
                wal.setPicnum(5024);
                spritesound(20, spr);
                return;
            case 7559:
                wal.setPicnum(5017);
                spritesound(20, spr);
                return;
            case 7561:
                wal.setPicnum(5027);
                spritesound(20, spr);
                return;
            case 7568:
                wal.setPicnum(5022);
                spritesound(20, spr);
                return;
            case 7579:
                wal.setPicnum(5026);
                spritesound(20, spr);
                return;
            case 7580:
                wal.setPicnum(5037);
                spritesound(20, spr);
                return;
            case 7657:
                wal.setPicnum(7659);
                spritesound(20, spr);
                return;
            case 7859:
                wal.setPicnum(5081);
                spritesound(20, spr);
                return;
            case 8227:
                wal.setPicnum(5070);
                spritesound(20, spr);
                return;
            case 8496:
                wal.setPicnum(5061);
                spritesound(20, spr);
                return;
            case 8497:
                wal.setPicnum(5076);
                spritesound(495, spr);
                return;
            case 8503:
                wal.setPicnum(5079);
                spritesound(20, spr);
                return;
            case 8567:
            case 8568:
            case 8569:
            case 8570:
            case 8571:
                wal.setPicnum(5082);
                spritesound(20, spr);
                return;
            case 8617:
                if (numplayers < 2) {
                    wal.setPicnum(8618);
                    spritesound(47, spr);
                }
                return;
            case 8620:
                wal.setPicnum(8621);
                spritesound(47, spr);
                return;
            case 8622:
                wal.setPicnum(8623);
                spritesound(495, spr);
        }
    }

    public static void checkhitceiling(final int sn) {
        final Sector sec = boardService.getSector(sn);
        if (sec == null) {
            return;
        }

        switch (sec.getCeilingpicnum()) {
            case 72:
            case 74:
            case 76:
            case 244:
            case 246:
            case 1939:
            case 1986:
            case 1988:
            case 2123:
            case 2125:
            case 2878:
            case 2898:
                ceilingglass(ps[myconnectindex].i, sn, 10);
                spritesound(GLASS_BREAKING, ps[screenpeek].i);

                if (sec.getCeilingpicnum() == 246) {
                    sec.setCeilingpicnum(247);
                }
                if (sec.getCeilingpicnum() == 244) {
                    sec.setCeilingpicnum(245);
                }
                if (sec.getCeilingpicnum() == 76) {
                    sec.setCeilingpicnum(77);
                }
                if (sec.getCeilingpicnum() == 72) {
                    sec.setCeilingpicnum(73);
                }
                if (sec.getCeilingpicnum() == 74) {
                    sec.setCeilingpicnum(75);
                }
                if (sec.getCeilingpicnum() == 1986) {
                    sec.setCeilingpicnum(1987);
                }
                if (sec.getCeilingpicnum() == 1939) {
                    sec.setCeilingpicnum(2004);
                }
                if (sec.getCeilingpicnum() == 1988) {
                    sec.setCeilingpicnum(2005);
                }
                if (sec.getCeilingpicnum() == 2898) {
                    sec.setCeilingpicnum(2899);
                }
                if (sec.getCeilingpicnum() == 2878) {
                    sec.setCeilingpicnum(2879);
                }
                if (sec.getCeilingpicnum() == 2123) {
                    sec.setCeilingpicnum(2124);
                }
                if (sec.getCeilingpicnum() == 2125) {
                    sec.setCeilingpicnum(2126);
                }

                if (sec.getHitag() == 0) {
                    for (ListNode<Sprite> node = boardService.getSectNode(sn); node != null; node = node.getNext()) {
                        Sprite sp = node.get();
                        if (sp.getPicnum() == SECTOREFFECTOR && sp.getLotag() == 12) {
                            for (ListNode<Sprite> n = boardService.getStatNode(3); n != null; n = n.getNext()) {
                                if (n.get().getHitag() == sp.getHitag()) {
                                    hittype[n.getIndex()].temp_data[3] = 1;
                                }
                            }
                            break;
                        }
                    }
                }

                int j = engine.krand() & 1;
                ListNode<Sprite> ni = boardService.getStatNode(3);
                while (ni != null) {
                    Sprite sp = ni.get();
                    int i = ni.getIndex();
                    if (sp.getHitag() == (sec.getHitag()) && sp.getLotag() == 3) {
                        hittype[i].temp_data[2] = j;
                        hittype[i].temp_data[4] = 1;
                    }
                    ni = ni.getNext();
                }
        }
    }

    public static void checkhitsprite(int i, final int sn) {
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        switch (s.getPicnum()) {
            case 1194: {
                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                switch (sp.getPicnum()) {
                    case 26:
                    case 27:
                    case 1228:
                    case 1426:
                    case 1774:
                    case 2095:
                    case 3420:
                    case 3471:
                    case 3475:
                        for (int k = 0; k < 64; k++) {
                            int j = EGS(s.getSectnum(), s.getX(), s.getY(), s.getZ() - (engine.krand() % (48 << 8)), SCRAP3 + (engine.krand() & 3), -8, 48, 48, engine.krand() & 2047, (engine.krand() & 63) + 64, -(engine.krand() & 4095) - (s.getZvel() >> 2), i, (short) 5);
                            Sprite s2 = boardService.getSprite(j);
                            if (s2 != null) {
                                s2.setPal(8);
                            }
                        }

                        if (s.getPicnum() == 1194) {
                            s.setPicnum(1203);
                        }
                        s.setCstat(s.getCstat() & ~257);
                        break;
                }
                break;
            }
            case 1141:
            case 1150:
            case 1152:
            case 1157:
            case 1158:
            case 1163:
            case 1164:
            case 1165:
            case 1166:
                spritesound(GLASS_HEAVYBREAK, i);
                for (int j = 0; j < 16; j++) {
                    RANDOMSCRAP(s, i);
                }
                engine.deletesprite(i);
                break;
            case FANSPRITE:
                s.setPicnum(FANSPRITEBROKE);
                s.setCstat(s.getCstat() & (65535 - 257));
                if (s.getPicnum() == 234) {
                    s.setPicnum(235);
                    spritesound(18, i);
                }
                spritesound(GLASS_HEAVYBREAK, i);
                for (int j = 0; j < 16; j++) {
                    RANDOMSCRAP(s, i);
                }
                break;
            case GRATE1:
                s.setPicnum(BGRATE1);
                s.setCstat(s.getCstat() & (65535 - 256 - 1));
                spritesound(VENT_BUST, i);
                break;

            case 1085:
            case 1086:
                s.setPicnum(1088);
                s.setCstat(0);
                break;
            case WATERFOUNTAIN:
            case WATERFOUNTAIN + 1:
            case WATERFOUNTAIN + 2:
            case WATERFOUNTAIN + 3:
                spawn(i, 1196);
                break;
            case 1098:
                s.setPicnum(1120);
                s.setCstat(s.getCstat() | (engine.krand() & 1) << 2);
                s.setCstat(s.getCstat() & ~257);
                spawn(i, 1196);
                spritesound(GLASS_BREAKING, i);
                break;
            case 1100:
                s.setPicnum(1102);
                s.setCstat(s.getCstat() | (engine.krand() & 1) << 2);
                s.setCstat(s.getCstat() & ~257);
                spawn(i, 1196);
                spritesound(GLASS_HEAVYBREAK, i);
                break;
            case 1066:
            case 1067:
            case 1114:
            case 1117: {
                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                if (sp.getExtra() != currentGame.getCON().script[currentGame.getCON().actorscrptr[SHOTSPARK1]]) {
                    for (int j = 0; j < 15; j++) {
                        int vz = -(engine.krand() & 511) - 256;
                        int ve = (engine.krand() & 127) + 64;
                        int va = engine.krand() & 2047;

                        Sector sec = boardService.getSector(s.getSectnum());
                        if (sec != null) {
                            EGS(s.getSectnum(), s.getX(), s.getY(), sec.getFloorz() - (12 << 8) - (j << 9), SCRAP1 + (engine.krand() & 15), -8, 64, 64,
                                    va, ve, vz, i, (short) 5);
                        }
                    }
                    spawn(i, EXPLOSION2);
                    engine.deletesprite(i);
                }
                break;
            }
            case 1221:
                //RA
            case 2654:
            case 2656:
            case 3172:
                spritesound(GLASS_BREAKING, i);
                lotsofglass(i, -1, 10);
                engine.deletesprite(i);
                break;
            case 3430: {
                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                sp.setXvel((short) ((s.getXvel() >> 1) + (s.getXvel() >> 2)));
                sp.setAng(sp.getAng() - (engine.krand() & 16));
                spritesound(355, i);
                break;
            }
            case QUEBALL:
            case STRIPEBALL:
            case 3440:
            case 3441:
            case 4897:
            case 4898: {
                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                if (sp.getPicnum() == QUEBALL || sp.getPicnum() == STRIPEBALL) {
                    sp.setXvel((short) ((s.getXvel() >> 1) + (s.getXvel() >> 2)));
                    sp.setAng(sp.getAng() - ((s.getAng() << 1) + 1024));
                    s.setAng((short) (EngineUtils.getAngle(s.getX() - sp.getX(), s.getY() - sp.getY()) - 512));
                    if (Sound[POOLBALLHIT].getSoundOwnerCount() < 2) {
                        spritesound(POOLBALLHIT, i);
                    }
                    break;
                }

                if (sp.getPicnum() == 3440 || sp.getPicnum() == 3441) {
                    sp.setXvel((short) ((s.getXvel() >> 1) + (s.getXvel() >> 2)));
                    sp.setAng(sp.getAng() - ((s.getAng() << 1) + engine.krand() & 0x40));
                    s.setAng(s.getAng() + (engine.krand() & 0x10));
                    spritesound(355, i);
                    break;
                }

                if (sp.getPicnum() == 4897 || sp.getPicnum() == 4898) {
                    sp.setXvel((short) ((s.getXvel() >> 1) + (s.getXvel() >> 2)));
                    sp.setAng(sp.getAng() - ((s.getAng() << 1) + engine.krand() & 0x10));
                    s.setAng(s.getAng() + (engine.krand() & 0x10));
                    spritesound(355, i);
                    break;
                }

                if ((engine.krand() & 3) != 0) {
                    s.setXvel(164);
                    s.setAng(sp.getAng());
                }

                break;
            }
            case 3152:
                s.setPicnum(3218);
                break;
            case 3153:
                s.setPicnum(3219);
                break;
            case 2893:
            case 2915:
            case 3115:
            case 3171:
                switch (s.getPicnum()) {
                    case 2893:
                        s.setPicnum(2978);
                        break;
                    case 2915:
                        s.setPicnum(2977);
                        break;
                    case 3115:
                        s.setPicnum(3116);
                        break;
                    case 3171:
                        s.setPicnum(3216);
                        break;
                }

                spritesound(19, i);
                lotsofglass(i, -1, 10);
                break;
            case 2876:
                s.setPicnum(2990);
                break;
            case 3114:
                s.setPicnum(3117);
                break;
            case 2251:
                s.setPicnum(2252);
                s.setCstat(s.getCstat() & (65535 - 256 - 1));
                spritesound(VENT_BUST, i);
                break;
            case 1080:
            case 1168:
            case 1172:
            case 1174:
            case 1175:
            case 1176:
            case 1178:
            case 1180:
            case 1215:
            case 1216:
            case 1217:
            case 1218:
            case 1219:
            case 1220:
            case 1222:
            case 1280:
            case 1281:
            case 1282:
            case 1283:
            case 1284:
            case 1285:
            case 1286:
            case 1287:
            case 1288:
            case 1289:
            case 1824: //RA
            case 2215:
            case 2231:
                if (s.getPicnum() == 1280) {
                    lotsofmoney(s, 4 + (engine.krand() & 3));
                } else if (s.getPicnum() == 1168 || s.getPicnum() == 2231) {
                    lotsofcolourglass(i, -1, 40);
                    spritesound(GLASS_HEAVYBREAK, i);
                } else if (s.getPicnum() == 1172) {
                    lotsofglass(i, -1, 40);
                }

                spritesound(GLASS_BREAKING, i);
                s.setAng((short) (engine.krand() & 2047));
                lotsofglass(i, -1, 8);
                engine.deletesprite(i);
                break;
            case 1228:
                s.setPicnum(1210);
                spawn(i, 1196);
                spritesound(GLASS_HEAVYBREAK, i);
                break;
            case FORCESPHERE:
                s.setXrepeat(0);
                hittype[s.getOwner()].temp_data[0] = 32;
                hittype[s.getOwner()].temp_data[1] ^= 1;
                hittype[s.getOwner()].temp_data[2]++;
                spawn(i, EXPLOSION2);
                break;
            case 1121:
            case 1123:
            case 1124:
            case 1232:
            case 1233:
            case 1234: {
                switch (s.getPicnum()) {
                    case 1121:
                        s.setPicnum(1126);
                        break;
                    case 1123:
                        s.setPicnum(1132);
                        break;
                    case 1124:
                        s.setPicnum(1122);
                        break;
                    case 1232:
                        s.setPicnum(1239);
                        break;
                    case 1233:
                        s.setPicnum(1337);
                        break;
                    case 1234:
                        s.setPicnum(1235);
                        break;
                }

                Sprite ssp = boardService.getSprite(spawn(i, STEAM));
                Sector sec = boardService.getSector(s.getSectnum());
                if (ssp != null && sec != null) {
                    ssp.setZ(sec.getFloorz() - (32 << 8));
                }
                break;
            }
            case 1191:
            case 1193:
            case 1211:
            case 1230: {
                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                switch (sp.getPicnum()) {
                    case 26:
                    case 27:
                    case 1228:
                    case 1426:
                    case 1774:
                    case 2095:
                    case 3420:
                    case 3471:
                    case 3475:
                        if (hittype[i].temp_data[0] == 0) {
                            s.setCstat(s.getCstat() & ~257);
                            hittype[i].temp_data[0] = 1;
                            spawn(i, BURNING);
                        }
                        break;
                }
                break;
            }
            case 2030:
                s.setPicnum(2034);
                spritesound(19, i);
                lotsofglass(i, -1, 10);
                break;
            case 2156:
            case 2158:
            case 2160:
            case 2175:
            case 2137:
            case 2151:
            case 2152: {
                s.setPicnum(s.getPicnum() + 1);
                spritesound(19, i);
                lotsofglass(i, -1, 10);
                Sector sec = boardService.getSector(s.getSectnum());
                if  (sec != null) {
                    for (int j = 0; j < 6; j++) {
                        EGS(s.getSectnum(), s.getX(), s.getY(), sec.getFloorz() - (8 << 8), SCRAP6 + (engine.krand() & 15), -8, 48, 48,
                                engine.krand() & 2047, (engine.krand() & 63) + 64, -(engine.krand() & 4095) - s.getZvel() >> 2, i, (short) 5);
                    }
                }
                break;
            }
            //RA
            case FANSPRITEWORK:
                s.setPicnum(FANSPRITEBROKE);
                s.setCstat(s.getCstat() & (65535 - 257));
                spritesound(GLASS_HEAVYBREAK, i);
                for (int j = 0; j < 16; j++) {
                    RANDOMSCRAP(s, i);
                }
                break;
            case 74:
                s.setPicnum(75);
                spritesound(20, i);
                break;
            case 2123:
                s.setPicnum(2124);
                spritesound(19, i);
                lotsofglass(i, -1, 10);
                break;
            case 2431:
                if (s.getPal() != 4) {
                    s.setPicnum(2451);
                    if (s.getLotag() != 0) {
                        for (int j = 0; j < boardService.getSpriteCount(); ++j) {
                            Sprite sp = boardService.getSprite(j);
                            if (sp != null && sp.getPicnum() == 2431 && sp.getPal() == 4 && s.getLotag() == sp.getLotag()) {
                                sp.setPicnum(2451);
                            }
                        }
                    }
                }
                break;
            case 2437:
                spritesound(439, i);
                break;
            case 2443:
                if (s.getPal() != 19) {
                    s.setPicnum(2455);
                }
                break;
            case 2445:
                s.setPicnum(2450);
                spritesound(20, i);
                break;
            case 2451:
                if (s.getPal() != 4) {
                    spritesound(69, i);

                    if (s.getLotag() != 0) {
                        for (int l = 0; l < boardService.getSpriteCount(); ++l) {
                            Sprite sp = boardService.getSprite(l);
                            if (sp != null && sp.getPicnum() == 2451 && sp.getPal() == 4 && s.getLotag() == sp.getLotag()) {
                                guts(s, 2460, 12, myconnectindex);
                                guts(s, 2465, 3, myconnectindex);
                                sp.setXrepeat(0);
                                sp.setYrepeat(0);
                                s.setXrepeat(0);
                                s.setYrepeat(0);
                            }
                        }
                    } else {
                        guts(s, 2460, 12, myconnectindex);
                        guts(s, 2465, 3, myconnectindex);
                        s.setXrepeat(0);
                        s.setYrepeat(0);
                    }
                }
                break;
            case 2455:
                spritesound(69, i);
                guts(s, 2465, 3, myconnectindex);
                engine.deletesprite(i);
                break;
            case 3462:
            case 8461:
            case 8462:
                s.setPicnum(5074);
                spritesound(20, i);
                break;
            case 3475:
                s.setPicnum(5075);
                spritesound(20, i);
                break;
            case 3497:
                s.setPicnum(5076);
                spritesound(20, i);
                break;
            case 3498:
                s.setPicnum(5077);
                spritesound(20, i);
                break;
            case 3499:
                s.setPicnum(5078);
                spritesound(20, i);
                break;
            case 3584:
                s.setPicnum(8681);
                spritesound(495, i);
                hitradius(i, 250, 0, 0, 1, 1);
                break;
            case 3773:
                s.setPicnum(8651);
                spritesound(19, i);
                lotsofglass(i, -1, 10);
                break;
            case 7441:
                s.setPicnum(5016);
                spritesound(20, i);
                break;
            case 7478:
                s.setPicnum(5035);
                spritesound(20, i);
                break;
            case 7533:
                s.setPicnum(5035);
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
                break;
            case 7534:
                s.setPicnum(5029);
                spritesound(20, i);
                break;
            case 7545:
                s.setPicnum(5030);
                spritesound(20, i);
                break;
            case 7547:
                s.setPicnum(5031);
                spritesound(20, i);
                break;
            case 7553:
                s.setPicnum(5035);
                spritesound(20, i);
                break;
            case 7574:
                s.setPicnum(5032);
                spritesound(20, i);
                break;
            case 7575:
                s.setPicnum(5033);
                spritesound(20, i);
                break;
            case 7578:
                s.setPicnum(5034);
                spritesound(20, i);
                break;
            case 7636:
            case 7875:
                s.setPicnum(s.getPicnum() + 3);
                spritesound(18, i);
                break;

            case 8567:
            case 8568:
            case 8569:
            case 8570:
            case 8571:
                s.setPicnum(5082);
                spritesound(20, i);
                break;
            case 7640:
                s.setPicnum(s.getPicnum() + 2);
                spritesound(18, i);
                break;
            case 7696:
                s.setPicnum(7697);
                spritesound(47, i);
                break;
            case 7806:
                s.setPicnum(5043);
                spritesound(20, i);
                break;
            case 7879:
                s.setPicnum(s.getPicnum() + 1);
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
                break;
            case 7886:
                s.setPicnum(5046);
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
                break;
            case 7887:
                s.setPicnum(5044);
                spritesound(20, i);
                hitradius(i, 10, 0, 0, 1, 1);
                break;
            case 7900:
                s.setPicnum(5047);
                spritesound(20, i);
                break;
            case 7901:
                s.setPicnum(5080);
                spritesound(20, i);
                break;
            case 7906:
                s.setPicnum(5048);
                spritesound(20, i);
                break;
            case 7912:
            case 7913:
                s.setPicnum(5049);
                spritesound(20, i);
                break;
            case 8047:
                s.setPicnum(5050);
                spritesound(20, i);
                break;
            case 8059:
                s.setPicnum(5051);
                spritesound(20, i);
                break;
            case 8060:
                s.setPicnum(5052);
                spritesound(20, i);
                break;
            case 8099:
                if (s.getLotag() == 5) {
                    s.setLotag(0);
                    s.setPicnum(5087);
                    spritesound(340, i);
                    for (int l = 0; l < boardService.getSpriteCount(); ++l) {
                        Sprite sp = boardService.getSprite(l);
                        if (sp != null && sp.getPicnum() == 8094) {
                            sp.setPicnum(5088);
                        }
                    }
                }
                break;
            case 8215:
                s.setPicnum(5064);
                spritesound(20, i);
                break;
            case 8216:
                s.setPicnum(5065);
                spritesound(20, i);
                break;
            case 8217:
                s.setPicnum(5066);
                spritesound(20, i);
                break;
            case 8218:
                s.setPicnum(5067);
                spritesound(20, i);
                break;
            case 8220:
                s.setPicnum(5068);
                spritesound(20, i);
                break;
            case 8221:
                s.setPicnum(5069);
                spritesound(20, i);
                break;
            case 8222:
                s.setPicnum(5053);
                spritesound(20, i);
                break;
            case 8223:
                s.setPicnum(5054);
                spritesound(20, i);
                break;
            case 8224:
                s.setPicnum(5055);
                spritesound(20, i);
                break;
            case 8312:
                s.setPicnum(5071);
                spritesound(472, i);
                break;
            case 8370:
                s.setPicnum(5056);
                spritesound(20, i);
                break;
            case 8371:
                s.setPicnum(5057);
                spritesound(20, i);
                break;
            case 8372:
                s.setPicnum(5058);
                spritesound(20, i);
                break;
            case 8373:
                s.setPicnum(5059);
                spritesound(20, i);
                break;
            case 8385:
                s.setPicnum(8386);
                spritesound(20, i);
                break;
            case 8387:
                s.setPicnum(8388);
                spritesound(20, i);
                break;
            case 8389:
                s.setPicnum(8390);
                spritesound(20, i);
                break;
            case 8391:
                s.setPicnum(8392);
                spritesound(20, i);
                break;
            case 8394:
                s.setPicnum(5072);
                spritesound(495, i);
                break;
            case 8395:
                s.setPicnum(5072);
                spritesound(20, i);
                break;
            case 8396:
                s.setPicnum(5038);
                spritesound(20, i);
                break;
            case 8397:
                s.setPicnum(5039);
                spritesound(20, i);
                break;
            case 8398:
                s.setPicnum(5040);
                spritesound(20, i);
                break;
            case 8399:
                s.setPicnum(5041);
                spritesound(20, i);
                break;
            case 8423:
                s.setPicnum(5073);
                spritesound(20, i);
                break;
            case 8475:
                s.setPicnum(5075);
                spritesound(472, i);
                break;
            case 8497:
                s.setPicnum(5076);
                spritesound(20, i);
                break;
            case 8498:
                s.setPicnum(5077);
                spritesound(20, i);
                break;
            case 8499:
                s.setPicnum(5078);
                spritesound(20, i);
                break;
            case 8503:
                s.setPicnum(5079);
                spritesound(20, i);
                break;
            case 8525:
                s.setPicnum(5036);
                spritesound(20, i);
                break;
            case 8537:
                s.setPicnum(5062);
                spritesound(20, i);
                break;
            case 8579:
                s.setPicnum(5014);
                spritesound(20, i);
                break;
            case 8596:
                s.setPicnum(8598);
                spritesound(20, i);
                break;
            case 8608:
                s.setPicnum(5083);
                spritesound(20, i);
                break;
            case 8609:
                s.setPicnum(5084);
                spritesound(20, i);
                break;
            case 8611:
                s.setPicnum(5086);
                spritesound(20, i);
                break;
            case 8640:
                s.setPicnum(5085);
                spritesound(20, i);
                break;
            case 8679:
                s.setPicnum(8680);
                spritesound(47, i);
                hitradius(i, 10, 0, 0, 1, 1);
                if (s.getLotag() != 0) {
                    for (int l = 0; l < boardService.getSpriteCount(); ++l) {
                        Sprite sp = boardService.getSprite(l);
                        if (sp != null && sp.getPicnum() == 8679 && sp.getPal() == 4 && s.getLotag() == sp.getLotag()) {
                            sp.setPicnum(8680);
                        }
                    }
                }
                break;
            case 8682:
                s.setPicnum(8683);
                spritesound(20, i);
                break;
            case 8487:
            case 8489:
                spritesound(471, i);
                s.setPicnum(s.getPicnum() + 1);
                break;

            case 8162:
            case 8163:
            case 8164:
            case 8165:
            case 8166:
            case 8167:
            case 8168:
                engine.changespritestat(i, (short) 5);
                s.setPicnum(5063);
                spritesound(20, i);
                break;

            case 8589:
            case 8590:
            case 8591:
            case 8592:
            case 8593:
            case 8594:
            case 8595:
                engine.changespritestat(i, (short) 5);
                s.setPicnum(8588);
                spritesound(20, i);
                break;
            case 7648:
            case 7694:
            case 7700:
            case 7702:
            case 7711:
                s.setPicnum(s.getPicnum() + 1);
                spritesound(47, i);
                break;
            case 7638:
            case 7644:
            case 7646:
            case 7650:
            case 7653:
            case 7655:
            case 7691:
            case 7881:
            case 7883:
            case 7876:
                s.setPicnum(s.getPicnum() + 1);
                spritesound(18, i);
                break;
            case 7595:
            case 7704:
                s.setPicnum(7705);
                spritesound(495, i);
                break;
            case 7885:
            case 7890:
                s.setPicnum(5045);
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
                break;

            case PLAYERONWATER:
                i = s.getOwner(); // Attension
                s = boardService.getSprite(i);
                if (s == null) {
                    return;
                }
            default: {
                if ((s.getCstat() & 16) != 0 && s.getHitag() == 0 && s.getLotag() == 0 && s.getStatnum() == 0) {
                    break;
                }

                Sprite sp = boardService.getSprite(sn);
                if (sp == null) {
                    break;
                }

                if ((sp.getPicnum() == ALIENBLAST || sp.getOwner() != i) && s.getStatnum() != 4) {
                    if (badguy(s)) {
                        if (sp.getPicnum() == CROSSBOW) {
                            sp.setExtra(sp.getExtra() << 1);
                        }

                        if ((s.getPicnum() != MOSQUITO)) {
                            if (sp.getPicnum() != ALIENBLAST) {
                                if (currentGame.getCON().actortype[s.getPicnum()] == 0) {
                                    int j = spawn(sn, JIBS6);
                                    Sprite sj = boardService.getSprite(j);
                                    if (sj == null) {
                                        return;
                                    }

                                    if (sp.getPal() == 6) {
                                        sj.setPal(6);
                                    }

                                    sj.setZ(sj.getZ() + (4 << 8));
                                    sj.setXvel(16);
                                    sj.setXrepeat(24);
                                    sj.setYrepeat(24);
                                    sj.setAng(sj.getAng() + 32 - (engine.krand() & 63));
                                }
                            }
                        }

                        int j = sp.getOwner();
                        Sprite sj = boardService.getSprite(j);
                        if (sj != null && sj.getPicnum() == APLAYER && s.getPicnum() != MOSQUITO) {
                            if (ps[sj.getYvel()].curr_weapon == SHOTGUN_WEAPON) {
                                shoot(i, BLOODSPLAT3);
                                shoot(i, BLOODSPLAT1);
                                shoot(i, BLOODSPLAT2);
                                shoot(i, BLOODSPLAT4);
                            }
                        }

                        if (s.getStatnum() == 2) {
                            engine.changespritestat(i, (short) 1);
                            hittype[i].timetosleep = SLEEPTIME;
                        }
                    }

                    if (s.getStatnum() != 2) {
                        if (sp.getPicnum() == ALIENBLAST) {
                            if ((s.getPicnum() == APLAYER && s.getPal() == 1)) {
                                return;
                            }

                            if (currentGame.getCON().freezerhurtowner == 0) {
                                if (sp.getOwner() == i) {
                                    return;
                                }
                            }
                        }

                        hittype[i].picnum = sp.getPicnum();
                        hittype[i].extra += sp.getExtra();
                        hittype[i].ang = sp.getAng();
                        hittype[i].owner = sp.getOwner();
                    }

                    if (s.getStatnum() == 10) {
                        int p = s.getYvel();
                        if (ps[p].newowner >= 0) {
                            ps[p].newowner = -1;
                            ps[p].posx = ps[p].oposx;
                            ps[p].posy = ps[p].oposy;
                            ps[p].posz = ps[p].oposz;
                            ps[p].ang = ps[p].oang;

                            ps[p].cursectnum = engine.updatesector(ps[p].posx, ps[p].posy, ps[p].cursectnum);
                            setpal(ps[p]);

                            for (ListNode<Sprite> node = boardService.getStatNode(1); node != null; node = node.getNext()) {
                                Sprite sp2 = node.get();
                                if (sp2.getPicnum() == CAMERA1) {
                                    sp2.setYvel(0);
                                }
                            }
                        }

                        Sprite hso = boardService.getSprite(hittype[i].owner);
                        if (hso != null && hso.getPicnum() != APLAYER) {
                            if (ud.player_skill >= 3) {
                                sp.setExtra(sp.getExtra() + (sp.getExtra() >> 1));
                            }
                        }
                    }

                }
                break;
            }
        }
    }

    public static void checksectors(int snum) {
        PlayerStruct p = ps[snum];
        final Sector psec = boardService.getSector(p.cursectnum);
        if (psec != null) {
            switch (psec.getLotag()) {

                case 32767:
                    psec.setLotag(0);
                    FTA(9, p);
                    ps[connecthead].secret_rooms++;
                    return;
                case -1:
                    LeaveMap();
                    psec.setLotag(0);
                    if (ud.from_bonus != 0) {
                        ud.level_number = ud.from_bonus;
                        ud.from_bonus = 0;
                    } else {
                        ud.level_number++;
                    }
                    return;
                case -2:
                    psec.setLotag(0);
                    p.timebeforeexit = 26 * 8;
                    p.customexitsound = psec.getHitag();
                    return;
                default:
                    if (psec.getLotag() >= 10000) {
                        if (snum == screenpeek || ud.coop == 1) {
                            spritesound(psec.getLotag() - 10000, p.i);
                        }
                        psec.setLotag(0);
                    }
                    break;

            }
        }

        // After this point the player effects the map with space
        Sprite psp = boardService.getSprite(p.i);
        if (psp == null || psp.getExtra() <= 0) {
            return;
        }

        if (ud.cashman != 0 && (sync[snum].bits & (1 << 29)) != 0) {
            lotsofmoney(boardService.getSprite(p.i), 2);
        }

        if (p.newowner >= 0) {
            if (klabs(sync[snum].svel) > 768 || klabs(sync[snum].fvel) > 768) {
                CLEARCAMERAS(p, -1);
                return;
            }
        }

        if ((sync[snum].bits & (1 << 29)) == 0 && (sync[snum].bits & (1 << 31)) == 0) {
            p.toggle_key_flag = 0;
        } else if (p.toggle_key_flag == 0) {

            if ((sync[snum].bits & (1 << 31)) != 0) {
                if (p.newowner >= 0) {
                    CLEARCAMERAS(p, -1);
                }
                return;
            }

            neartagsprite = -1;
            p.toggle_key_flag = 1;

            hitawall(p);
            int hitscanwall = pHitInfo.hitwall;
            Wall hwal = boardService.getWall(hitscanwall);

            if (hwal != null && hwal.getOverpicnum() == MIRROR) {
                if (hwal.getLotag() > 0 && Sound[hwal.getLotag()].getSoundOwnerCount() == 0 && snum == screenpeek) {
                    if (currentGame.getCON().type == RRRA) {
                        if (screenpeek == myconnectindex) {
                            if (Sound[27].getSoundOwnerCount() == 0 && Sound[28].getSoundOwnerCount() == 0 && Sound[29].getSoundOwnerCount() == 0 && Sound[257].getSoundOwnerCount() == 0 && Sound[258].getSoundOwnerCount() == 0) {
                                switch (engine.krand() % 5) {
                                    case 0:
                                        spritesound(27, p.i);
                                        break;
                                    case 1:
                                        spritesound(28, p.i);
                                        break;
                                    case 2:
                                        spritesound(29, p.i);
                                        break;
                                    case 3:
                                        spritesound(257, p.i);
                                        break;
                                    default:
                                        spritesound(258, p.i);
                                        break;
                                }
                            }
                        }
                        return;
                    }

                    spritesound(hwal.getLotag(), p.i);
                    return;
                }
            }

            if (hwal != null && (hwal.getCstat() & 16) != 0) {
                if (hwal.getLotag() != 0) {
                    return;
                }
            }

            if (p.OnMotorcycle) {
                if (p.CarSpeed < 20) {
                    leaveMoto(p);
                }
                return;
            }
            if (p.OnBoat) {
                if (p.CarSpeed < 20) {
                    leaveBoard(p);
                }
                return;
            }

            if (p.newowner >= 0) {
                neartag(p.oposx, p.oposy, p.oposz, psp.getSectnum(), (short) p.oang, 1280, 1);
            } else {
                neartag(p.posx, p.posy, p.posz, psp.getSectnum(), (short) p.oang, 1280, 1);
                if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1) {
                    neartag(p.posx, p.posy, p.posz + (8 << 8), psp.getSectnum(), (short) p.oang, 1280, 1);
                }
                if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1) {
                    neartag(p.posx, p.posy, p.posz + (16 << 8), psp.getSectnum(), (short) p.oang, 1280, 1);
                }
                if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1) {
                    neartag(p.posx, p.posy, p.posz + (16 << 8), psp.getSectnum(), (short) p.oang, 1280, 3);
                    Sprite nsp = boardService.getSprite(neartagsprite);
                    if (nsp != null) {
                        switch (nsp.getPicnum()) {
                            case 1115:
                            case 1168:
                            case 5581:
                            case 5583:
                                return;
                            case 5317:
                                nsp.setDetail(1);
                                return;
                        }
                    }

                    neartagsprite = -1;
                    neartagwall = -1;
                    neartagsector = -1;
                }
            }

            if (p.newowner == -1 && neartagsprite == -1 && neartagsector == -1 && neartagwall == -1) {
                Sector pssec = boardService.getSector(psp.getSectnum());
                if (pssec != null && isanunderoperator(pssec.getLotag())) {
                    neartagsector = psp.getSectnum();
                }
            }

            Sector nsec = boardService.getSector(neartagsector);
            if (nsec != null && (nsec.getLotag() & 16384) != 0) {
                return;
            }

            if (neartagsprite == -1 && neartagwall == -1) {
                if (psec != null && psec.getLotag() == 2) {
                    int oldz = hitasprite(p.i);
                    neartagsprite = pHitInfo.hitsprite;
                    if (oldz > 1280) {
                        neartagsprite = -1;
                    }
                }
            }

            Sprite hsp = boardService.getSprite(neartagsprite);
            if (hsp != null) {
                if (checkhitswitch(snum, neartagsprite, 1)) {
                    return;
                }

                switch (hsp.getPicnum()) {
                    case 1098:
                    case 1100:
                    case 2121:
                    case 2122:
                        if (p.last_pissed_time == 0) {
                            if (ud.lockout == 0) {
                                spritesound(435, p.i);
                            }

                            p.last_pissed_time = 26 * 220;
                            p.transporter_hold = 29 * 2;
                            if (p.holster_weapon == 0) {
                                p.holster_weapon = 1;
                                p.weapon_pos = -1;
                            }
                            if (psp.getExtra() <= (currentGame.getCON().max_player_health - (currentGame.getCON().max_player_health / 10))) {
                                psp.setExtra(psp.getExtra() + currentGame.getCON().max_player_health / 10);
                                p.last_extra = psp.getExtra();
                            } else if (psp.getExtra() < currentGame.getCON().max_player_health) {
                                psp.setExtra((short) currentGame.getCON().max_player_health);
                            }
                        } else if (Sound[38].getSoundOwnerCount() == 0) {
                            spritesound(38, p.i);
                        }
                        return;

                    case WATERFOUNTAIN:
                        if (hittype[neartagsprite].temp_data[0] != 1) {
                            hittype[neartagsprite].temp_data[0] = 1;
                            hsp.setOwner(p.i);

                            if (psp.getExtra() < currentGame.getCON().max_player_health) {
                                psp.setExtra(psp.getExtra() + 1);
                                spritesound(DUKE_DRINKING, p.i);
                            }
                        }
                        return;
                    case 1272:
                        spritesound(SHORT_CIRCUIT, p.i);
                        psp.setExtra(psp.getExtra() - (2 + (engine.krand() & 3)));
                        p.pals[0] = 48;
                        p.pals[1] = 48;
                        p.pals[2] = 64;
                        p.pals_time = 32;
                        break;
                    case MOTORCYCLE:
                        getMoto(p, neartagsprite);
                        return;
                    case SWAMPBUGGY:
                        getBoard(p, neartagsprite);
                        return;
                    case 8448:
                        if (Sound[340].getSoundOwnerCount() == 0) {
                            spritesound(340, neartagsprite);
                        }
                        return;
                    case 8704:
                        if (numplayers == 1) {
                            if (Sound[445].getSoundOwnerCount() != 0 || dword_119C08 != 0) {
                                if (Sound[445].getSoundOwnerCount() == 0 && Sound[446].getSoundOwnerCount() == 0 && Sound[447].getSoundOwnerCount() == 0 && dword_119C08 != 0) {
                                    if ((engine.krand() % 2) == 1) {
                                        spritesound(446, neartagsprite);
                                    } else {
                                        spritesound(447, neartagsprite);
                                    }
                                }
                            } else {
                                spritesound(445, neartagsprite);
                                dword_119C08 = 1;
                            }
                        }
                        return;
                    case 8164:
                    case 8165:
                    case 8166:
                    case 8167:
                    case 8168:
                    case 8591:
                    case 8592:
                    case 8593:
                    case 8594:
                    case 8595:
                        hsp.setExtra(60);
                        spritesound(235, neartagsprite);
                        return;
                }
            }

            if ((sync[snum].bits & (1 << 29)) == 0) {
                return;
            }

            if (neartagwall == -1 && neartagsector == -1 && neartagsprite == -1) {
                if (klabs(hits(p.i)) < 512) {
                    if ((engine.krand() & 255) < 16) {
                        spritesound(DUKE_SEARCH2, p.i);
                    } else {
                        spritesound(DUKE_SEARCH, p.i);
                    }
                    return;
                }
            }

            Wall nw = boardService.getWall(neartagwall);
            if (nw != null) {
                if (nw.getLotag() > 0 && isadoorwall(nw.getPicnum())) {
                    if (hitscanwall == neartagwall || hitscanwall == -1) {
                        checkhitswitch(snum, neartagwall, 0);
                    }
                    return;
                }
            }

            nsec = boardService.getSector(neartagsector);
            Sector pssec = boardService.getSector(psp.getSectnum());
            if (nsec != null && (nsec.getLotag() & 16384) == 0 && isanearoperator(nsec.getLotag())) {
                for (ListNode<Sprite> node = boardService.getSectNode(neartagsector); node != null; node = node.getNext()) {
                    Sprite sp = node.get();
                    if (sp.getPicnum() == ACTIVATOR || sp.getPicnum() == MASTERSWITCH) {
                        return;
                    }
                }
                if (checkaccess(neartagsector, snum)) {
                    operatesectors(neartagsector, p.i);
                } else {
                    if (nsec.getFiller() > 3) {
                        spritesound(99, p.i);
                    } else {
                        spritesound(419, p.i);
                    }

                    if (currentGame.getCON().key_quotes == null) {
                        currentGame.getCON().key_quotes = new char[3][64];
                        System.arraycopy(currentGame.getCON().fta_quotes[70], 0, currentGame.getCON().key_quotes[0], 0, 64);
                        System.arraycopy(currentGame.getCON().fta_quotes[71], 0, currentGame.getCON().key_quotes[1], 0, 64);
                        System.arraycopy(currentGame.getCON().fta_quotes[72], 0, currentGame.getCON().key_quotes[2], 0, 64);
                    }

                    if (nsec.getFiller() == 1) { //v0.751
                        if (cfg.gColoredKeys) {
                            buildString(currentGame.getCON().fta_quotes[70], 0, "BLUE KEY REQUIRED");
                        } else {
                            System.arraycopy(currentGame.getCON().key_quotes[0], 0, currentGame.getCON().fta_quotes[70], 0, 64);
                        }
                        FTA(70, ps[snum]);
                    } else if (nsec.getFiller() == 2) {
                        if (cfg.gColoredKeys) {
                            buildString(currentGame.getCON().fta_quotes[71], 0, "RED KEY REQUIRED");
                        } else {
                            System.arraycopy(currentGame.getCON().key_quotes[1], 0, currentGame.getCON().fta_quotes[71], 0, 64);
                        }
                        FTA(71, ps[snum]);
                    } else if (nsec.getFiller() == 3) {
                        if (cfg.gColoredKeys) {
                            buildString(currentGame.getCON().fta_quotes[72], 0, "BROWN KEY REQUIRED");
                        } else {
                            System.arraycopy(currentGame.getCON().key_quotes[2], 0, currentGame.getCON().fta_quotes[72], 0, 64);
                        }
                        FTA(72, ps[snum]);
                    } else {
                        FTA(41, ps[snum]);
                    }
                }
            } else if (pssec != null && (pssec.getLotag() & 16384) == 0) {
                if (isanunderoperator(pssec.getLotag())) {
                    for (ListNode<Sprite> node = boardService.getSectNode(psp.getSectnum()); node != null; node = node.getNext()) {
                        Sprite sp = node.get();
                        if (sp.getPicnum() == ACTIVATOR || sp.getPicnum() == MASTERSWITCH) {
                            return;
                        }
                    }

                    if (checkaccess(neartagsector, snum)) {
                        operatesectors(psp.getSectnum(), p.i);
                    } else {
                        if (nsec != null && nsec.getFiller() > 3) {
                            spritesound(99, p.i);
                        } else {
                            spritesound(419, p.i);
                        }
                        FTA(41, ps[snum]);
                    }
                } else {
                    checkhitswitch(snum, neartagwall, 0);
                }
            }
        }


    }

    public static void pushwall(Wall wal, Sector sec, int snum) {
        if (wal == null || sec == null) {
            return;
        }

        Sector nextsec = boardService.getSector(wal.getNextsector());
        if (nextsec == null) {
            return;
        }

        int hitag = sec.getHitag();
        if (hitag == 0) {
            hitag = 4;
        }
        if (hitag > 16) {
            hitag = 16;
        }

        int minx = 131072;
        int miny = 131072;
        int maxx = -131072;
        int maxy = -131072;

        int wx, wy;
        for (ListNode<Wall> wn = nextsec.getWallNode(); wn != null; wn = wn.getNext()) {
            Wall wal2 = wn.get();
            wx = wal2.getX();
            wy = wal2.getY();
            if (wx > maxx) {
                maxx = wx;
            }
            if (wy > maxy) {
                maxy = wy;
            }
            if (wx < minx) {
                minx = wx;
            }
            if (wy < miny) {
                miny = wy;
            }
        }

        maxx += hitag + 1;
        maxy += hitag + 1;
        minx -= hitag + 1;
        miny -= hitag + 1;

        boolean inside = sec.inside(maxx, maxy);
        if (!sec.inside(maxx, miny)) {
            inside = false;
        }
        if (!sec.inside(minx, miny)) {
            inside = false;
        }
        if (!sec.inside(minx, maxy)) {
            inside = false;
        }

        if (inside) {
            if (Sound[389].getSoundOwnerCount() == 0) {
                spritesound(389, ps[snum].i);
            }
            for (ListNode<Wall> wn = nextsec.getWallNode(); wn != null; wn = wn.getNext()) {
                Wall wal2 = wn.get();
                wx = wal2.getX();
                wy = wal2.getY();
                int dir = wal.getLotag() - 40;
                if (dir < 4) {
                    switch (dir) {
                        case 0:
                            wy -= hitag;
                            break;
                        case 1:
                            wx -= hitag;
                            break;
                        case 2:
                            wy += hitag;
                            break;
                        case 3:
                            wx += hitag;
                            break;
                    }
                    engine.dragpoint(wn.getIndex(), wx, wy);
                }
            }
        } else {
            for (ListNode<Wall> wn = nextsec.getWallNode(); wn != null; wn = wn.getNext()) {
                Wall wal2 = wn.get();
                wx = wal2.getX();
                wy = wal2.getY();
                int dir = wal.getLotag() - 40;
                if (dir < 4) {
                    switch (dir) {
                        case 0:
                            wy += hitag - 2;
                            break;
                        case 1:
                            wx += hitag - 2;
                            break;
                        case 2:
                            wy -= hitag - 2;
                            break;
                        case 3:
                            wx -= hitag - 2;
                            break;
                    }
                    engine.dragpoint(wn.getIndex(), wx, wy);
                }
            }
        }
    }

    public static void CLEARCAMERAS(PlayerStruct p, int i) {
        if (i < 0) {
            p.posx = p.oposx;
            p.posy = p.oposy;
            p.posz = p.oposz;
            p.ang = p.oang;
            p.newowner = -1;

            p.cursectnum = engine.updatesector(p.posx, p.posy, p.cursectnum);
            setpal(p);

            for (ListNode<Sprite> node = boardService.getStatNode(1); node != null; node = node.getNext()) {
                Sprite sp = node.get();
                if (sp.getPicnum() == CAMERA1) {
                    sp.setYvel(0);
                }
            }
        } else if (p.newowner >= 0) {
            p.newowner = -1;
        }
    }

    public static void movejails() {
        for (int i = 0; i < numjaildoors; i++) {
            int speed = jailspeed[i];
            if (speed < 2) {
                speed = 2;
            }

            if (jailstatus[i] == 1 || jailstatus[i] == 3) {
                jailcount2[i] -= speed;
                if (jailcount2[i] > 0) {
                    int wx = 0, wy = 0;
                    int sect = jailsect[i];
                    Sector jsec = boardService.getSector(sect);
                    if (jsec != null) {
                        for (ListNode<Wall> wn = jsec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            switch (jaildirection[i]) {
                                case 10:
                                    wx = wal.getX();
                                    wy = wal.getY() + speed;
                                    break;
                                case 20:
                                    wx = wal.getX() - speed;
                                    wy = wal.getY();
                                    break;
                                case 30:
                                    wx = wal.getX();
                                    wy = wal.getY() - speed;
                                    break;
                                case 40:
                                    wx = wal.getX() + speed;
                                    wy = wal.getY();
                                    break;
                            }
                            engine.dragpoint(wn.getIndex(), wx, wy);
                        }
                    }
                } else {
                    jailcount2[i] = 0;
                    if (jailstatus[i] == 1) {
                        jailstatus[i] = 2;
                    } else if (jailstatus[i] == 3) {
                        jailstatus[i] = 0;
                    }

                    switch (jaildirection[i]) {
                        case 10:
                            jaildirection[i] = 30;
                            break;
                        case 20:
                            jaildirection[i] = 40;
                            break;
                        case 30:
                            jaildirection[i] = 10;
                            break;
                        case 40:
                            jaildirection[i] = 20;
                            break;
                    }
                }
            }
        }
    }

    public static void movecarts() {
        for (int i = 0; i < numminecart; i++) {
            int speed = minespeed[i];
            if (speed < 2) {
                speed = 2;
            }

            if (minestatus[i] == 1 || minestatus[i] == 2) {
                minedistance[i] -= speed;
                if (minedistance[i] > 0) {
                    int wx = 0, wy = 0;
                    int sect = mineparent[i];
                    Sector msec = boardService.getSector(sect);
                    if (msec != null) {
                        for (ListNode<Wall> wn = msec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            switch (minedirection[i]) {
                                case 10:
                                    wx = wal.getX();
                                    wy = wal.getY() + speed;
                                    break;
                                case 20:
                                    wx = wal.getX() - speed;
                                    wy = wal.getY();
                                    break;
                                case 30:
                                    wx = wal.getX();
                                    wy = wal.getY() - speed;
                                    break;
                                case 40:
                                    wx = wal.getX() + speed;
                                    wy = wal.getY();
                                    break;
                            }
                            engine.dragpoint(wn.getIndex(), wx, wy);
                        }
                    }
                } else {
                    minedistance[i] = minefulldist[i];
                    if (minestatus[i] == 1) {
                        minestatus[i] = 2;
                    } else {
                        minestatus[i] = 1;
                    }

                    switch (minedirection[i]) {
                        case 10:
                            minedirection[i] = 30;
                            break;
                        case 20:
                            minedirection[i] = 40;
                            break;
                        case 30:
                            minedirection[i] = 10;
                            break;
                        case 40:
                            minedirection[i] = 20;
                            break;
                    }
                }
            }


            int sect = minechild[i];
            int minx = 131072;
            int miny = 131072;
            int maxx = -131072;
            int maxy = -131072;

            int wx, wy;
            Sector msec = boardService.getSector(sect);
            if (msec != null) {
                for (ListNode<Wall> wn = msec.getWallNode(); wn != null; wn = wn.getNext()) {
                    Wall wal = wn.get();
                    wx = wal.getX();
                    wy = wal.getY();
                    if (wx > maxx) {
                        maxx = wx;
                    }
                    if (wy > maxy) {
                        maxy = wy;
                    }
                    if (wx < minx) {
                        minx = wx;
                    }
                    if (wy < miny) {
                        miny = wy;
                    }
                }

                ListNode<Sprite> ns = boardService.getSectNode(sect);
                while (ns != null) {
                    Sprite s = ns.get();
                    if (badguy(s)) {
                        engine.setsprite(ns.getIndex(), (minx + maxx) >> 1, (maxy + miny) >> 1, s.getZ());
                    }
                    ns = ns.getNext();
                }
            }
        }
    }

    public static void torchesprocess() {
        int shade = engine.krand() & 8;
        for (int i = 0; i < numtorcheffects; i++) {
            int tshade = torchshade[i] - shade;
            Sector tsec =  boardService.getSector(torchsector[i]);
            if (tsec == null) {
                continue;
            }

            switch (torchflags[i]) {
                case 0:
                    tsec.setFloorshade((byte) tshade);
                    tsec.setCeilingshade((byte) tshade);
                    break;
                case 1:
                case 4:
                    tsec.setCeilingshade((byte) tshade);
                    break;
                case 2:
                case 5:
                    tsec.setFloorshade((byte) tshade);
                    break;
            }

            for (ListNode<Wall> wn = tsec.getWallNode(); wn != null; wn = wn.getNext()) {
                Wall wal = wn.get();
                if (wal.getLotag() != 1) {
                    switch (torchflags[i]) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            wal.setShade((byte) tshade);
                            break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void lightningprocess() {
        int tourcheffect = 0;
        byte brightness = (byte) (cfg.getPaletteGamma());
        Renderer renderer = game.getRenderer();
        byte[] gotpic = renderer.getRenderedPics();

        if (dword_18D0A0 != 0) {
            dword_18D0A8 -= 4;
            if (dword_18D0A8 < 0) {
                byte_18D0BB = brightness;
                dword_18D0A0 = 0;
                visibility = gVisibility;

//				engine.setbrightness(brightness, palette);
                for (tourcheffect = 0; tourcheffect < numlightnineffects; tourcheffect++) {
                    int sect = lightninsector[tourcheffect];
                    Sector lsec = boardService.getSector(sect);
                    if (lsec != null) {
                        lsec.setFloorshade((byte) lightninshade[tourcheffect]);
                        lsec.setCeilingshade((byte) lightninshade[tourcheffect]);
                        for (ListNode<Wall> wn = lsec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            wal.setShade((byte) lightninshade[tourcheffect]);
                        }
                    }
                }
            }
        } else if ((gotpic[322] & 2) != 0) {
            gotpic[322] &= ~2;
            if (renderer.getTile(2577).exists()) {
                visibility = 256;
                if (engine.krand() > 65000) {
                    dword_18D0A8 = 256;
                    dword_18D0A0 = 1;
                    sound((engine.rand() % 3 + 351));
                }
            }
        } else {
            byte_18D0BB = brightness;
            visibility = gVisibility;
        }
        if (dword_18D0A4 != 0) {
            dword_18D0AC -= 4;
            if (dword_18D0AC < 0) {
                dword_18D0A4 = 0;
                for (tourcheffect = 0; tourcheffect < numlightnineffects; tourcheffect++) {
                    int sect = lightninsector[tourcheffect];

                    Sector lsec = boardService.getSector(sect);
                    if (lsec != null) {
                        lsec.setFloorshade((byte) lightninshade[tourcheffect]);
                        lsec.setCeilingshade((byte) lightninshade[tourcheffect]);
                        for (ListNode<Wall> wn = lsec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            wal.setShade((byte) lightninshade[tourcheffect]);
                        }
                    }
                }
            }
        } else if ((gotpic[320] & 4) != 0) {
            gotpic[320] &= ~4;
            if (renderer.getTile(2562).exists() && engine.krand() > 65000) {
                dword_18D0A4 = 1;
                dword_18D0AC = 128;
                sound((engine.rand() % 3 + 351));
            }
        }

        if (dword_18D0A0 == 1) {
            byte chance = (byte) (engine.krand() % 4); // #GDX 05.07.2024 was (krand & 4)
            byte_18D0BB += chance;
            switch (chance) {
                case 0:
                    visibility = 2048;
                    break;
                case 1:
                    visibility = 1024;
                    break;
                case 2:
                    visibility = 512;
                    break;
                case 3:
                    visibility = 256;
                    break;
                default:
                    visibility = 4096;
                    break;
            }
            if (byte_18D0BB > 8) {
                byte_18D0BB = 0;
            }

//		    engine.setbrightness(byte_18D0BB, palette);
            for (int i = 0; i < numlightnineffects; i++) {
                int sect = lightninsector[i];
                Sector lsec = boardService.getSector(sect);
                if (lsec != null) {
                    byte tshade = (byte) (lightninshade[i] - byte_18D0BB << 4);
                    lsec.setFloorshade(tshade);
                    lsec.setCeilingshade(tshade);
                    for (ListNode<Wall> wn = lsec.getWallNode(); wn != null; wn = wn.getNext()) {
                        Wall wal = wn.get();
                        wal.setShade(tshade);
                    }
                }
            }
        }

        if (dword_18D0A4 == 1) {
            int shade = engine.krand() & 8 + torchshade[tourcheffect];
            for (int i = 0; i < numlightnineffects; i++) {
                int sect = lightninsector[i];


                Sector lsec = boardService.getSector(sect);
                if (lsec != null) {
                    byte tshade = (byte) (lightninshade[i] - shade);
                    lsec.setFloorshade(tshade);
                    lsec.setCeilingshade(tshade);
                    for (ListNode<Wall> wn = lsec.getWallNode(); wn != null; wn = wn.getNext()) {
                        Wall wal = wn.get();
                        wal.setShade(tshade);
                    }
                }
            }
        }
    }

    @CommonPart
    public static void setsectinterpolate(int i) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        Sector sec = boardService.getSector(sp.getSectnum());
        if (sec == null) {
            return;
        }

        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
            Wall w = wn.get();

            game.pInt.setwallinterpolate(wn.getIndex(), w);
            int k = w.getNextwall();
            w = boardService.getWall(k);
            if (w != null) {
                game.pInt.setwallinterpolate(k, w);
                game.pInt.setwallinterpolate(w.getPoint2(), w.getWall2());
            }
        }
    }
}
