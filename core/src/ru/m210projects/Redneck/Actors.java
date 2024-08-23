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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Types.*;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.exceptions.AssertException;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Build.filehandle.art.DynamicArtEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Types.CommonPart;
import ru.m210projects.Redneck.Types.PlayerStruct;

import java.util.Arrays;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Animate.getanimationgoal;
import static ru.m210projects.Redneck.Animate.setanimation;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.LoadSave.loadgame;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Player.quickkill;
import static ru.m210projects.Redneck.Player.setpal;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.RSector.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Types.ANIMATION.CEILZ;
import static ru.m210projects.Redneck.Types.UserDefs.DEMOSTAT_NULL;
import static ru.m210projects.Redneck.View.FTA;
import static ru.m210projects.Redneck.Weapons.*;

public class Actors {
    public static final int[] tempshort = new int[MAXSECTORS];
    public static final byte[] statlist = {0, 1, 6, 10, 12, 2, 5};
    public static int actor_tog = 0;
    public static int UFO_SpawnCount;
    public static int UFO_SpawnTime;
    public static int UFO_SpawnHulk;
    public static int otherp;
    private static final boolean[] pinstay = new boolean[10];

    public static boolean checkaddkills(Sprite s) {
        if (s == null) {
            return false;
        }

        switch (s.getPicnum()) {
            case BOULDER:
            case BOULDER1:
            case 1147:
            case RAT:
            case TORNADO:
            case BILLYCOCK:
            case BILLYRAY:
            case BILLYRAYSTAYPUT:
            case 4249:
            case DOGRUN:
            case LTH:
            case 4429:
            case BUBBASTAND:
            case HULK:
            case HULKSTAYPUT:
            case MOSQUITO:
            case PIG:
            case 4989:
            case SBMOVE:
            case MINION:
            case MINIONSTAYPUT:
            case UFO1:
            case UFO2:
            case UFO3:
            case UFO4:
            case UFO5:
            case COOT:
            case COOTSTAYPUT:
            case 5501:
            case VIXEN:
                return true;

            case 5890: // RA
            case 5891:
            case 5995:
            case 6225:
            case 6401:
            case 6658:
            case 6659:
            case 7030:
            case 7035:
            case 7192:
            case 7199:
            case 7206:
            case 7280:
            case 8035:
            case 8036:
            case 8705:
                if (currentGame.getCON().type == RRRA) {
                    return true;
                }
                break;
        }
        return false;
    }

    @CommonPart
    public static int LocateTheLocator(int n, int sn) {
        ListNode<Sprite> node = boardService.getStatNode(7);
        while (node != null) {
            Sprite sp = node.get();
            if ((sn == -1 || sn == sp.getSectnum()) && n == sp.getLotag()) {
                return node.getIndex();
            }
            node = node.getNext();
        }
        return -1;
    }

    public static boolean badguy(Sprite s) {
        if (s == null) {
            return false;
        }

        switch (s.getPicnum()) {
            case BOULDER:
            case BOULDER1:
            case TORNADO:
            case BILLYCOCK:
            case BILLYSHOOT:
            case BILLYRAYSTAYPUT:
            case DOGRUN:
            case LTH:
            case 4429: // LTH
            case BUBBASTAND:
            case HULK:
            case HULKSTAYPUT:
            case HEN:
            case MOSQUITO:
            case PIG:
            case 4989:
            case SBMOVE:
            case MINION:
            case MINIONSTAYPUT:
            case UFO1:
            case UFO2:
            case UFO3:
            case UFO4:
            case UFO5:
            case COOT:
            case COOTSTAYPUT:
            case 5501:
            case VIXEN:
                return true;

            case 1147: // RA
            case RAT:
            case 4249:
            case 4770:
            case MINIONUFO:
            case BIKERRIDE:
            case BIKERRIDE + 1:
            case BIKERSTAND:
            case 6225:
            case BIKERRIDEDAISY:
            case DAISYMAE:
            case DAISYMAE + 1:
            case BANJOCOOTER:
            case GUITARBILLY:
            case MINIONAIRBOAT:
            case HULKAIRBOAT:
            case DAISYAIRBOAT:
            case JACKOLOPE:
            case MAMAJACKOLOPE:
                if (currentGame.getCON().type == RRRA) {
                    return true;
                }
                break;
        }

        return currentGame.getCON().actortype[s.getPicnum()] != 0;
    }

    public static boolean badguypic(int pn) // Redneck
    {
        switch (pn) {
            case BOULDER:
            case BOULDER1:
            case 1147:
            case 1344:
            case TORNADO:
            case BILLYCOCK:
            case BILLYRAY:
            case BILLYRAYSTAYPUT:
            case 4249:
            case DOGRUN:
            case LTH:
            case 4429:
            case BUBBASTAND:
            case HULK:
            case HULKSTAYPUT:
            case MOSQUITO:
            case PIG:
            case 4989:
            case SBMOVE:
            case MINION:
            case MINIONSTAYPUT:
            case UFO1:
            case UFO2:
            case UFO3:
            case UFO4:
            case UFO5:
            case COOT:
            case COOTSTAYPUT:
            case 5501:
            case VIXEN:
                return true;

            // RA
            case 4770:
            case MINIONUFO:
            case 5890:
            case 5891:
            case 5995:
            case 6225:
            case 6401:
            case 6658:
            case 6659:
            case 7030:
            case 7035:
            case 7192:
            case 7199:
            case 7206:
            case 7280:
            case 8705:
                if (currentGame.getCON().type == RRRA) {
                    return true;
                }
                break;
        }

        return pn < MAXTILES && currentGame.getCON().actortype[pn] != 0;
    }

    @CommonPart
    public static boolean ifsquished(final int i, int p) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return false;
        }

        boolean squishme = false;
        if (sp.getPicnum() == APLAYER && ud.clipping) {
            return false;
        }

        Sector sc = boardService.getSector(sp.getSectnum());
        if (sc == null) {
            return false;
        }

        int floorceildist = sc.getFloorz() - sc.getCeilingz();
        if (sc.getLotag() != 23) {
            if (sp.getPal() == 1) {
                squishme = floorceildist < (32 << 8) && (sc.getLotag() & 32768) == 0;
            } else {
                squishme = floorceildist < (12 << 8);
            }
        }

        if (squishme) {
            FTA(10, ps[p]);

            if (badguy(sp)) {
                sp.setXvel(0);
            }

            if (sp.getPal() == 1) {
                hittype[i].picnum = SHOTSPARK1;
                hittype[i].extra = 1;
                return false;
            }

            return true;
        }
        return false;
    }

    public static void hitradius(final int i, int r, int hp1, int hp2, int hp3, int hp4) {
        final Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        if (s.getPicnum() != CROSSBOW || s.getPicnum() != CHIKENCROSSBOW || s.getXrepeat() >= 11) {

            tempshort[0] = s.getSectnum();
            int sectcnt = 0;
            int sectend = 1;
            int sect = 0;

            do {
                final int dasect = tempshort[sectcnt++];
                Sector sec = boardService.getSector(dasect);
                if (sec == null || sec.getWallNode() == null) {
                    continue;
                }

                if (((sec.getCeilingz() - s.getZ()) >> 8) < r) {
                    Wall walptr = sec.getWallNode().get();
                    int d = klabs(walptr.getX() - s.getX()) + klabs(walptr.getY() - s.getY());
                    if (d < r) {
                        checkhitceiling(dasect);
                    } else {
                        Wall w3 = walptr.getWall2().getWall2();
                        d = klabs(w3.getX() - s.getX()) + klabs(w3.getY() - s.getY());
                        if (d < r) {
                            checkhitceiling(dasect);
                        }
                    }
                }

                int startwall = sec.getWallptr();
                int endwall = startwall + sec.getWallnum();
                for (int x = startwall; x < endwall; x++) {
                    Wall wal = boardService.getWall(x);
                    if (wal != null && (klabs(wal.getX() - s.getX()) + klabs(wal.getY() - s.getY())) < r) {
                        int nextsect = wal.getNextsector();
                        if (nextsect >= 0) {
                            int ndasect;
                            for (ndasect = (sectend - 1); ndasect >= 0; ndasect--) {
                                if (tempshort[ndasect] == nextsect) {
                                    break;
                                }
                            }
                            if (ndasect < 0) {
                                tempshort[sectend++] = nextsect;
                            }
                        }

                        int x1 = (((wal.getX() + wal.getWall2().getX()) >> 1) + s.getX()) >> 1;
                        int y1 = (((wal.getY() + wal.getWall2().getY()) >> 1) + s.getY()) >> 1;
                        sect = engine.updatesector(x1, y1, sect);
                        if (sect >= 0 && engine.cansee(x1, y1, s.getZ(), sect, s.getX(), s.getY(), s.getZ(), s.getSectnum())) {
                            checkhitwall(i, x, wal.getX(), wal.getY(), s.getZ(), s.getPicnum());
                        }
                    }
                }
            } while (sectcnt < sectend);
        }

        int q = -(16 << 8) + (engine.krand() & ((32 << 8) - 1));

        for (int x = 0; x < 7; x++) {
            ListNode<Sprite> node = boardService.getStatNode(statlist[x]);
            ListNode<Sprite> nextj;
            for (; node != null; node = nextj) {
                nextj = node.getNext();
                Sprite sj = node.get();
                final int j = node.getIndex();
                Sprite sowner = boardService.getSprite(s.getOwner());

                if (x == 0 || x >= 5 || AFLAMABLE(sj.getPicnum())) {
                    if ((sj.getCstat() & 257) != 0) {
                        if (dist(s, sj) < r) {
                            if (badguy(sj) && !engine.cansee(sj.getX(), sj.getY(), sj.getZ() + q, sj.getSectnum(), s.getX(), s.getY(), s.getZ() + q, s.getSectnum())) {
                                continue;
                            }
                            checkhitsprite(j, i);
                        }
                    }
                } else if (sj.getExtra() >= 0 && sj != s && (badguy(sj) || sj.getPicnum() == 1185 || sj.getPicnum() == 3440 || sj.getPicnum() == 1184 || (sj.getCstat() & 257) != 0 || sj.getPicnum() == LNRDLYINGDEAD)) {
                    if (s.getPicnum() == MORTER && j == s.getOwner()) {
                        continue;
                    }

                    if (sj.getPicnum() == APLAYER) {
                        sj.setZ(sj.getZ() - PHEIGHT);
                    }
                    int d = dist(s, sj);
                    if (sj.getPicnum() == APLAYER) {
                        sj.setZ(sj.getZ() + PHEIGHT);
                    }

                    if (d < r && engine.cansee(sj.getX(), sj.getY(), sj.getZ() - (8 << 8), sj.getSectnum(), s.getX(), s.getY(), s.getZ() - (12 << 8), s.getSectnum())) {
                        hittype[j].ang = EngineUtils.getAngle(sj.getX() - s.getX(), sj.getY() - s.getY());

                        if (sj.getPicnum() == MINION && sj.getPal() == 19) {
                            continue;
                        }

                        if (s.getPicnum() == CROSSBOW && sj.getExtra() > 0) {
                            hittype[j].picnum = CROSSBOW;
                        } else if (s.getPicnum() == CHIKENCROSSBOW && sj.getExtra() > 0) {
                            hittype[j].picnum = CROSSBOW;
                        } else {
                            hittype[j].picnum = RADIUSEXPLOSION;
                        }

                        if (d < r / 3) {
                            if (hp4 == hp3) {
                                hp4++;
                            }
                            hittype[j].extra = hp3 + (engine.krand() % (hp4 - hp3));
                        } else if (d < 2 * r / 3) {
                            if (hp3 == hp2) {
                                hp3++;
                            }
                            hittype[j].extra = hp2 + (engine.krand() % (hp3 - hp2));
                        } else {
                            if (hp2 == hp1) {
                                hp2++;
                            }
                            hittype[j].extra = hp1 + (engine.krand() % (hp2 - hp1));
                        }

                        if (sj.getPicnum() != HULK && sj.getPicnum() != SBMOVE
                                // GDX RA 2.10.2018
                                && sj.getPicnum() != MAMAJACKOLOPE && sj.getPicnum() != GUITARBILLY && sj.getPicnum() != BANJOCOOTER && sj.getPicnum() != 8663) {
                            if (sj.getXvel() < 0) {
                                sj.setXvel(0);
                            }
                            sj.setXvel(sj.getXvel() + (s.getExtra() << 2));
                        }
                        if (sj.getPicnum() == 2231 || sj.getPicnum() == 1185 || sj.getPicnum() == 1184 || sj.getPicnum() == 3440) {
                            checkhitsprite(j, i);
                        } else if (s.getExtra() == 0) {
                            hittype[j].extra = 0;
                        }

                        if (sj.getPicnum() != RADIUSEXPLOSION && sowner != null && sowner.getStatnum() < MAXSTATUS) {
                            if (sj.getPicnum() == APLAYER) {
                                int p = sj.getYvel();
                                if (ps[p].newowner >= 0) {
                                    ps[p].newowner = -1;
                                    ps[p].posx = ps[p].oposx;
                                    ps[p].posy = ps[p].oposy;
                                    ps[p].posz = ps[p].oposz;
                                    ps[p].ang = ps[p].oang;
                                    ps[p].cursectnum = engine.updatesector(ps[p].posx, ps[p].posy, ps[p].cursectnum);
                                    setpal(ps[p]);

                                    ListNode<Sprite> k = boardService.getStatNode(1);
                                    while (k != null) {
                                        Sprite sp = k.get();
                                        if (sp.getPicnum() == CAMERA1) {
                                            sp.setYvel(0);
                                        }
                                        k = k.getNext();
                                    }
                                }
                            }
                            hittype[j].owner = s.getOwner();
                        }
                    }
                }
            }
        }
    }

    public static int movesprite(int spritenum, int xchange, int ychange, int zchange, int cliptype) {
        Sprite spr = boardService.getSprite(spritenum);
        if (spr == null) {
            return 0;
        }

        int moveHit;
        boolean bg = badguy(spr);

        game.pInt.setsprinterpolate(spritenum, spr);

        if (spr.getStatnum() == 5 || (bg && spr.getXrepeat() < 4)) {
            spr.setX(spr.getX() + ((xchange * TICSPERFRAME) >> 2));
            spr.setY(spr.getY() + ((ychange * TICSPERFRAME) >> 2));
            spr.setZ(spr.getZ() + ((zchange * TICSPERFRAME) >> 2));
            if (bg) {
                engine.setsprite(spritenum, spr.getX(), spr.getY(), spr.getZ());
            }
            return 0;
        }

        int dasectnum = spr.getSectnum();
        int daz = spr.getZ();
        int h = ((engine.getTile(spr.getPicnum()).getHeight() * spr.getYrepeat()) << 1);
        daz -= h;

        if (bg) {
            int oldx = spr.getX();
            int oldy = spr.getY();

            if (spr.getXrepeat() > 60) {
                moveHit = engine.clipmove(spr.getX(), spr.getY(), daz, dasectnum, (((long) xchange * TICSPERFRAME) << 11), (((long) ychange * TICSPERFRAME) << 11), 1024, (4 << 8), (4 << 8), cliptype);
            } else {
                moveHit = engine.clipmove(spr.getX(), spr.getY(), daz, dasectnum, (((long) xchange * TICSPERFRAME) << 11), (((long) ychange * TICSPERFRAME) << 11), 192, (4 << 8), (4 << 8), cliptype);
            }
            spr.setX(clipmove_x);
            spr.setY(clipmove_y);
            dasectnum = clipmove_sectnum;

            Sector sec = boardService.getSector(dasectnum);
            if (sec == null || hittype[spritenum].actorstayput >= 0
                    && hittype[spritenum].actorstayput != dasectnum) {

                if (dasectnum < 0) {
                    dasectnum = 0;
                    sec = boardService.getSector(dasectnum);
                }

                spr.setX(oldx);
                spr.setY(oldy);
                if (sec != null && sec.getLotag() == 1 || (hittype[spritenum].temp_data[0] & 3) == 1) {
                    spr.setAng((short) (engine.krand() & 2047));
                }
                engine.setsprite(spritenum, oldx, oldy, spr.getZ());

                return (kHitSector + dasectnum);
            }

            int nHitType = (moveHit & kHitTypeMask);
            if ((nHitType == kHitWall || nHitType == kHitSprite) && (hittype[spritenum].cgg == 0)) {
                spr.setAng(spr.getAng() + 768);
            }
        } else {
            if (spr.getStatnum() == 4) {
                moveHit = engine.clipmove(spr.getX(), spr.getY(), daz, dasectnum, (((long) xchange * TICSPERFRAME) << 11), (((long) ychange * TICSPERFRAME) << 11), 8, (4 << 8), (4 << 8), cliptype);
            } else {
                moveHit = engine.clipmove(spr.getX(), spr.getY(), daz, dasectnum, (((long) xchange * TICSPERFRAME) << 11), (((long) ychange * TICSPERFRAME) << 11), 128, (4 << 8), (4 << 8), cliptype);
            }
            spr.setX(clipmove_x);
            spr.setY(clipmove_y);
            dasectnum = clipmove_sectnum;
        }

        if (dasectnum >= 0) {
            if ((dasectnum != spr.getSectnum())) {
                engine.changespritesect(spritenum, dasectnum);
            }
        }
        daz = spr.getZ() + ((zchange * TICSPERFRAME) >> 3);
        if ((daz > hittype[spritenum].ceilingz) && (daz <= hittype[spritenum].floorz)) {
            spr.setZ(daz);
        } else if (moveHit == 0) {
            return (kHitSector + dasectnum);
        }

        return (moveHit);
    }

    /**
     * Move sprite
     */
    @CommonPart
    public static boolean ssp(int i, int cliptype) { // The set sprite function
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return false;
        }

        int movetype = movesprite(i, (s.getXvel() * (EngineUtils.cos((s.getAng()) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), cliptype);
        return (movetype == 0);
    }

    @CommonPart
    public static void insertspriteq(int i) {
        if (currentGame.getCON().spriteqamount > 0) {
            Sprite qspr = boardService.getSprite(spriteq[spriteqloc]);
            if (qspr != null) {
                qspr.setXrepeat(0);
            }
            spriteq[spriteqloc] = (short) i;
            spriteqloc = (short) ((spriteqloc + 1) % currentGame.getCON().spriteqamount);
        } else {
            Sprite spr = boardService.getSprite(i);
            if (spr != null) {
                spr.setXrepeat(0);
                spr.setYrepeat(0);
            }
        }
    }

    @CommonPart
    public static void ms(int i) {
        // T1,T2 and T3 are used for all the sector moving stuff!!!
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        s.setX(s.getX() + ((s.getXvel() * (EngineUtils.cos((s.getAng()) & 2047))) >> 14));
        s.setY(s.getY() + ((s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14));

        int j = hittype[i].temp_data[1];
        int k = hittype[i].temp_data[2];

        Sector sec = boardService.getSector(s.getSectnum());
        if (sec != null) {
            for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                Point p = EngineUtils.rotatepoint(0, 0, msx[j], msy[j], k & 2047);
                engine.dragpoint(wn.getIndex(), s.getX() + p.getX(), s.getY() + p.getY());
                j++;
            }
        }
    }

    public static void movefta() {
        ListNode<Sprite> ni = boardService.getStatNode(2);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            final Sprite s = ni.get();
            final int i = ni.getIndex();
            int p = findplayer(s);

            int psect = s.getSectnum();
            int ssect = psect;

            Sprite psp = boardService.getSprite(ps[p].i);

            if (psp != null && psp.getExtra() > 0) {
                if (player_dist < 30000) {

                    hittype[i].timetosleep++;
                    if (hittype[i].timetosleep >= (player_dist >> 8)) {
                        boolean cansee = false;
                        if (badguy(s)) {
                            int px = ps[p].oposx + 64 - (engine.krand() & 127);
                            int py = ps[p].oposy + 64 - (engine.krand() & 127);
                            psect = engine.updatesector(px, py, psect);
                            if (psect == -1) {
                                ni = nexti;
                                continue;
                            }
                            int sx = s.getX() + 64 - (engine.krand() & 127);
                            int sy = s.getY() + 64 - (engine.krand() & 127);
                            ssect = engine.updatesector(px, py, ssect);
                            if (ssect == -1) {
                                ni = nexti;
                                continue;
                            }

                            if (currentGame.getCON().type != RRRA) {
                                if ((s.getPal() == 33 || s.getPicnum() == VIXEN)) {
                                    int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
                                    int cz2 = s.getZ() - (engine.krand() % (52 << 8));
                                    cansee = engine.cansee(sx, sy, cz2, s.getSectnum(), px, py, cz1, ps[p].cursectnum);
                                } else {
                                    if (((px - sx) * EngineUtils.sin((s.getAng() + 512) & kAngleMask)) + ((py - sy) * EngineUtils.sin((s.getAng()) & kAngleMask)) >= 0) {
                                        int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
                                        int cz2 = s.getZ() - (engine.krand() % (52 << 8));
                                        cansee = engine.cansee(sx, sy, cz2, s.getSectnum(), px, py, cz1, ps[p].cursectnum);
                                    }
                                }
                            }

                            if (currentGame.getCON().type == RRRA) {
                                if (s.getPal() == 33 || s.getPicnum() == COOT || s.getPicnum() == COOTSTAYPUT || s.getPicnum() == VIXEN || s.getPicnum() == BIKERSTAND || s.getPicnum() == BIKERRIDE || s.getPicnum() == 5891 || s.getPicnum() == 6658 || s.getPicnum() == BIKERRIDEDAISY || s.getPicnum() == 6659 || s.getPicnum() == MINIONAIRBOAT || s.getPicnum() == HULKAIRBOAT || s.getPicnum() == DAISYAIRBOAT || s.getPicnum() == JACKOLOPE || s.getPicnum() == BANJOCOOTER || s.getPicnum() == GUITARBILLY || s.getPicnum() == 6225 || s.getPicnum() == MAMAJACKOLOPE || (s.getPicnum() == MINION && s.getPal() == 8)) {
                                    int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
                                    int cz2 = s.getZ() - (engine.krand() % (52 << 8));
                                    cansee = engine.cansee(sx, sy, cz2, s.getSectnum(), px, py, cz1, ps[p].cursectnum);
                                } else {
                                    if (((px - sx) * EngineUtils.sin((s.getAng() + 512) & kAngleMask)) + ((py - sy) * EngineUtils.sin((s.getAng()) & kAngleMask)) >= 0) {
                                        int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
                                        int cz2 = s.getZ() - (engine.krand() % (52 << 8));
                                        cansee = engine.cansee(sx, sy, cz2, s.getSectnum(), px, py, cz1, ps[p].cursectnum);
                                    }
                                }
                            }
                        } else {
                            int cz1 = ps[p].oposz - ((engine.krand() & 31) << 8);
                            int cz2 = s.getZ() - ((engine.krand() & 31) << 8);
                            cansee = engine.cansee(s.getX(), s.getY(), cz2, s.getSectnum(), ps[p].oposx, ps[p].oposy, cz1, ps[p].cursectnum);
                        }

                        if (cansee) {
                            switch (s.getPicnum()) {
                                case 1251:
                                case 1268:
                                case 1187:
                                case 1304:
                                case 1305:
                                case 1306:
                                case 1309:
                                case 1315:
                                case 1317:
                                case 1388:
                                    Sector sec = boardService.getSector(s.getSectnum());
                                    if (sec != null) {
                                        if ((sec.getCeilingstat() & 1) != 0) {
                                            s.setShade(sec.getCeilingshade());
                                        } else {
                                            s.setShade(sec.getFloorshade());
                                        }
                                    }

                                    hittype[i].timetosleep = 0;
                                    engine.changespritestat(i, (short) 6);
                                    break;
                                default:
                                    hittype[i].timetosleep = 0;
                                    check_fta_sounds(i);
                                    engine.changespritestat(i, (short) 1);
                                    break;
                            }
                        } else {
                            hittype[i].timetosleep = 0;
                        }
                    }
                }
                if (badguy(s)) {
                    Sector sec = boardService.getSector(s.getSectnum());
                    if (sec != null) {
                        if ((sec.getCeilingstat() & 1) != 0) {
                            s.setShade(sec.getCeilingshade());
                        } else {
                            s.setShade(sec.getFloorshade());
                        }
                    }

                    switch (s.getPicnum()) {
                        case HEN:
                        case COW:
                        case PIG:
                        case DOGRUN:
                        case JACKOLOPE:
                            if (sub_64F28(i, p)) {
                                hittype[i].timetosleep = 0;
                                check_fta_sounds(i);
                                engine.changespritestat(i, (short) 1);
                            }
                            break;
                    }
                }
            }
            ni = nexti;
        }
    }

    public static void sub_64EF0(int snum) {
        PlayerStruct p = ps[snum];
        p.field_28E = 1;
        p.field_X = p.posx;
        p.field_Y = p.posy;
    }

    public static boolean sub_64F28(int nSprite, int nPlayer) {
        PlayerStruct p = ps[nPlayer];
        Sprite pSprite = boardService.getSprite(nSprite);

        if (pSprite != null && p.field_28E != 0) {
            int pal = pSprite.getPal();
            if (pal != 30 && pal != 32 && pal != 33) {
                if (p.field_X - p.field_290 < pSprite.getX() && p.field_290 + p.field_X > pSprite.getX()) {
                    return p.field_Y - p.field_290 < pSprite.getY() && p.field_Y + p.field_290 > pSprite.getY();
                }
            }
        }
        return false;
    }

    public static int ifhitsectors(short sectnum) {
        ListNode<Sprite> ni = boardService.getStatNode(5);
        while (ni != null) {
            Sprite sp = ni.get();
            if (sp.getPicnum() == EXPLOSION2 || sp.getPicnum() == EXPLOSION3 && sectnum == sp.getSectnum()) {
                return ni.getIndex();
            }
            ni = ni.getNext();
        }
        return -1;
    }

    public static int ifhitbyweapon(int sn) {
        Sprite npc = boardService.getSprite(sn);
        if (npc == null) {
            return -1;
        }

        if (hittype[sn].extra >= 0) {
            if (npc.getExtra() >= 0) {
                if (npc.getPicnum() == APLAYER) {
                    if (ud.god) {
                        return -1;
                    }

                    int p = npc.getYvel();
                    final int j = hittype[sn].owner;
                    Sprite jspr = boardService.getSprite(j);

                    if (jspr != null && jspr.getPicnum() == APLAYER && ud.coop == 1 && ud.ffire == 0) {
                        return -1;
                    }

                    npc.setExtra(npc.getExtra() - hittype[sn].extra);

                    if (jspr != null) {
                        if (npc.getExtra() <= 0 && hittype[sn].picnum != 1409) {
                            npc.setExtra(0);

                            ps[p].wackedbyactor = (short) j;

                            if (jspr.getPicnum() == APLAYER && p != jspr.getYvel()) {
                                ps[p].frag_ps = jspr.getYvel();
                            }

                            hittype[sn].owner = ps[p].i;
                        }
                    }

                    switch (hittype[sn].picnum) {
                        case DYNAMITE:
                        case POWDERKEGSPRITE:
                        case 1228:
                        case 1273:
                        case 1315:
                        case SEENINE:
                        case RADIUSEXPLOSION:
                        case CROSSBOW:
                        case CHIKENCROSSBOW:
                            ps[p].posxv += hittype[sn].extra * (EngineUtils.cos((hittype[sn].ang) & 2047)) << 2;
                            ps[p].posyv += hittype[sn].extra * (EngineUtils.sin(hittype[sn].ang & 2047)) << 2;
                            break;
                        default:
                            ps[p].posxv += hittype[sn].extra * (EngineUtils.cos((hittype[sn].ang) & 2047)) << 1;
                            ps[p].posyv += hittype[sn].extra * (EngineUtils.sin(hittype[sn].ang & 2047)) << 1;
                            break;
                    }
                } else {
                    if (hittype[sn].extra == 0 && npc.getXrepeat() < 24) {
                        return -1;
                    }

                    npc.setExtra(npc.getExtra() - hittype[sn].extra);
                    Sprite npcOwner = boardService.getSprite(npc.getOwner());
                    if (npc.getPicnum() != 4989 && npcOwner != null && npcOwner.getStatnum() < MAXSTATUS) {
                        npc.setOwner((short) hittype[sn].owner);
                    }
                }

                hittype[sn].extra = -1;
                return hittype[sn].picnum;
            }
        }

        hittype[sn].extra = -1;
        return -1;
    }

    @CommonPart
    public static void movecyclers() {
        for (int q = numcyclers - 1; q >= 0; q--) {
            Sector sec = boardService.getSector(cyclers[q][0]);
            if (sec == null) {
                continue;
            }

            int t = cyclers[q][3];
            int j = (t + (EngineUtils.sin(cyclers[q][1] & 2047) >> 10));
            byte cshade = (byte) cyclers[q][2];

            if (j < cshade) {
                j = cshade;
            } else if (j > t) {
                j = t;
            }

            cyclers[q][1] += sec.getExtra();
            if (cyclers[q][5] != 0) {
                for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                    Wall wal = wn.get();
                    if (wal.getHitag() != 1) {
                        wal.setShade(j);
                        Wall nwal = boardService.getWall(wal.getNextwall());
                        if ((wal.getCstat() & 2) != 0 && nwal != null) {
                            nwal.setShade(j);
                        }
                    }
                }
                sec.setCeilingshade(j);
                sec.setFloorshade(j);
            }
        }
    }

    public static void movedummyplayers() {
        ListNode<Sprite> ni = boardService.getStatNode(13);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            final int i = ni.getIndex();
            Sprite s = ni.get();
            Sprite sonwer = boardService.getSprite(s.getOwner());
            if (sonwer == null) {
                ni = nexti;
                continue;
            }

            int p = sonwer.getYvel();
            Sprite psp = boardService.getSprite(ps[p].i);
            Sector psec = boardService.getSector(ps[p].cursectnum);

            if ((psec != null && psec.getLotag() != 1) || (psp != null && psp.getExtra() <= 0)) {
                ps[p].dummyplayersprite = -1;
                engine.deletesprite(i);
                ni = nexti;
                continue;
            } else {
                Sector sec = boardService.getSector(s.getSectnum());
                if (sec != null) {
                    if (ps[p].on_ground && ps[p].on_warping_sector == 1 && psec != null && psec.getLotag() == 1) {
                        s.setCstat(257);
                        s.setZ(sec.getCeilingz() + (27 << 8));
                        s.setAng((short) ps[p].ang);
                        if (hittype[i].temp_data[0] == 8) {
                            hittype[i].temp_data[0] = 0;
                        } else {
                            hittype[i].temp_data[0]++;
                        }
                    } else {
                        if (sec.getLotag() != 2) {
                            s.setZ(sec.getFloorz());
                        }
                        s.setCstat(32768);
                    }
                }
            }

            s.setX(s.getX() + (ps[p].posx - ps[p].oposx));
            s.setY(s.getY() + (ps[p].posy - ps[p].oposy));
            engine.setsprite(i, s.getX(), s.getY(), s.getZ());

            ni = nexti;
        }
    }

    public static void moveplayers() // Players
    {
        ListNode<Sprite> ni = boardService.getStatNode(10);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            Sprite s = ni.get();
            final int i = ni.getIndex();

            PlayerStruct p = ps[s.getYvel()];
            if (s.getOwner() >= 0) {
                if (p.newowner >= 0) // Looking thru the camera
                {
                    game.pInt.setsprinterpolate(i, s);
                    s.setX(p.oposx);
                    s.setY(p.oposy);
                    s.setZ(p.oposz + PHEIGHT);
                    s.setAng((short) p.oang);
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                } else {
                    int otherx = 0;
                    if (ud.multimode > 1) {
                        otherp = findotherplayer(s.getYvel());
                        otherx = player_dist;
                    } else {
                        otherp = s.getYvel();
                    }

                    execute(currentGame.getCON(), i, s.getYvel(), otherx);

                    if (ud.multimode > 1) {
                        Sprite psp = boardService.getSprite(ps[otherp].i);
                        if (psp != null && psp.getExtra() > 0) {
                            if (s.getYrepeat() > 32 && psp.getYrepeat() < 32) {
                                if (otherx < 1400 && p.knee_incs == 0) {
                                    p.knee_incs = 1;
                                    p.weapon_pos = -1;
                                    p.actorsqu = ps[otherp].i;
                                }
                            }
                        }
                    }
                    if (ud.god) {
                        s.setExtra((short) currentGame.getCON().max_player_health);
                        s.setCstat(257);
                    }

                    if (s.getExtra() > 0) {
                        hittype[i].owner = i;

                        if (!ud.god) {
                            if (ceilingspace(s.getSectnum()) || floorspace(s.getSectnum())) {
                                quickkill(p);
                            }
                        }
                    } else {
                        p.posx = s.getX();
                        p.posy = s.getY();
                        p.posz = s.getZ() - (20 << 8);
                        p.newowner = -1;

                        Sprite wackedSpr = boardService.getSprite(p.wackedbyactor);
                        if (wackedSpr != null && wackedSpr.getStatnum() < MAXSTATUS) {
                            p.ang += getincangle(p.ang, EngineUtils.getAngle(wackedSpr.getX() - p.posx, wackedSpr.getY() - p.posy)) / 2.0f;
                            p.ang = BClampAngle(p.ang);
                        }

                        if (!isPsychoSkill()) {
                            if (ud.multimode < 2 && lastload != null && lastload.exists() && !DemoScreen.isDemoPlaying()) {
                                if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
                                    game.changeScreen(gLoadingScreen.setTitle(lastload.getName()));
                                    gLoadingScreen.init(() -> {
                                        if (!loadgame(lastload)) {
                                            game.GameMessage("Can't load game!");
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                    }
                    s.setAng((short) p.ang);
                }
            } else {
                if (p.holoduke_on == -1) {
                    engine.deletesprite(i);
                    ni = nexti;
                    continue;
                }

                game.pInt.setsprinterpolate(i, s);

                s.setCstat(0);

                if (s.getXrepeat() < 42) {
                    s.setXrepeat(s.getXrepeat() + 4);
                    s.setCstat(s.getCstat() | 2);
                } else {
                    s.setXrepeat(42);
                }
                if (s.getYrepeat() < 36) {
                    s.setYrepeat(s.getYrepeat() + 4);
                } else {
                    s.setYrepeat(36);
                    Sector sec = boardService.getSector(s.getSectnum());
                    if (sec != null) {
                        if (sec.getLotag() != 2) {
                            makeitfall(currentGame.getCON(), i);
                        }

                        if (s.getZvel() == 0 && sec.getLotag() == 1) {
                            s.setZ(s.getZ() + (32 << 8));
                        }
                    }
                }

                if (s.getExtra() < 8) {
                    s.setXvel(128);
                    s.setAng((short) p.ang);
                    s.setExtra(s.getExtra() + 1);
                    ssp(i, CLIPMASK0);
                } else {
                    s.setAng((short) (2047 - p.ang));
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                }
            }

            Sector sec = boardService.getSector(s.getSectnum());
            if (sec != null) {
                if ((sec.getCeilingstat() & 1) != 0) {
                    s.setShade(s.getShade() + ((sec.getCeilingshade() - s.getShade()) >> 1));
                } else {
                    s.setShade(s.getShade() + ((sec.getFloorshade() - s.getShade()) >> 1));
                }
            }

            ni = nexti;
        }
    }

    public static void movefx() {
        ListNode<Sprite> ni = boardService.getStatNode(11);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            final Sprite s = ni.get();
            final int i = ni.getIndex();

            switch (s.getPicnum()) {
                case RESPAWN:
                    if (s.getExtra() == 66) {
                        int j = spawn(i, s.getHitag());
                        Sprite sj = boardService.getSprite(j);
                        if (sj != null && currentGame.getCON().type == RRRA) {
                            sj.setPal(s.getPal());
                            if (sj.getPicnum() == MAMAJACKOLOPE) {
                                if (sj.getPal() == 30) {
                                    sj.setXrepeat(26);
                                    sj.setYrepeat(26);
                                    sj.setClipdist(75);
                                } else if (sj.getPal() == 31) {
                                    sj.setXrepeat(36);
                                    sj.setYrepeat(36);
                                    sj.setClipdist(100);
                                } else {
                                    sj.setXrepeat(50);
                                    sj.setYrepeat(50);
                                    sj.setClipdist(100);
                                }
                            }
                            if (sj.getPal() == 8) {
                                sj.setCstat(sj.getCstat() | 2);
                            }
                            if (sj.getPal() == 6) {
                                s.setExtra(53);
                                sj.setPal(0);
                            } else {
                                engine.deletesprite(i);
                            }
                        } else {
                            engine.deletesprite(i);
                        }
                    } else if (s.getExtra() > (66 - 13)) {
                        s.setExtra(s.getExtra() + 1);
                    }
                    break;

                case MUSICANDSFX:
                    int ht = s.getHitag();
                    if (hittype[i].temp_data[1] != (cfg.isNoSound() ? 0 : 1)) {
                        hittype[i].temp_data[1] = (cfg.isNoSound() ? 0 : 1);
                        hittype[i].temp_data[0] = 0;
                    }

                    Sprite psp = boardService.getSprite(ps[screenpeek].i);
                    if (psp == null) {
                        break;
                    }

                    Sector sec = boardService.getSector(s.getSectnum());

                    if (s.getLotag() >= 1000 && s.getLotag() < 2000) {
                        int x = ldist(psp, s);
                        if (x < ht && hittype[i].temp_data[0] == 0) {
                            Sounds.setReverb(true, (s.getLotag() - 1000) / 255f);
                            hittype[i].temp_data[0] = 1;
                        }
                        if (x >= ht && hittype[i].temp_data[0] == 1) {
                            Sounds.setReverb(false, 0);
                            hittype[i].temp_data[0] = 0;
                        }
                    } else if (sec != null && s.getLotag() < 999 && sec.getLotag() < 9
                            && cfg.AmbienceToggle && sec.getFloorz() != sec.getCeilingz()) {
                        if (s.getLotag() < NUM_SOUNDS && (currentGame.getCON().soundm[s.getLotag()] & 2) != 0) {
                            int x = dist(psp, s);
                            if (x < ht && hittype[i].temp_data[0] == 0 && Sounds.isAvailable(currentGame.getCON().soundpr[s.getLotag()] - 1)) {
                                if (numenvsnds == cfg.getMaxvoices()) {
                                    ListNode<Sprite> nj = boardService.getStatNode(11);
                                    while (nj != null) {
                                        int j = nj.getIndex();
                                        Sprite sp = nj.get();
                                        if (s.getPicnum() == MUSICANDSFX && j != i && sp.getLotag() < 999 && hittype[j].temp_data[0] == 1
                                                && dist(sp, psp) > x) {
                                            stopenvsound(sp.getLotag(), j);
                                            break;
                                        }
                                        nj = nj.getNext();
                                    }
                                    if (nj == null) {
                                        ni = nexti;
                                        continue;
                                    }
                                }
                                spritesound(s.getLotag(), i);
                                hittype[i].temp_data[0] = 1;
                            }
                            if (x >= ht && hittype[i].temp_data[0] == 1) {
                                hittype[i].temp_data[0] = 0;
                                stopenvsound(s.getLotag(), i);
                            }
                        }
                        if (s.getLotag() < NUM_SOUNDS && (currentGame.getCON().soundm[s.getLotag()] & 16) != 0) {
                            if (hittype[i].temp_data[4] > 0) {
                                hittype[i].temp_data[4]--;
                            } else {
                                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                    if (p == myconnectindex && ps[p].cursectnum == s.getSectnum()) {
                                        int j = s.getLotag() + ((global_random & 0xFFFF) % (s.getHitag() + 1));
                                        sound(j);
                                        hittype[i].temp_data[4] = 26 * 40 + (global_random % (26 * 40));
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
            ni = nexti;
        }
    }

    public static void movefallers() {
        ListNode<Sprite> ni = boardService.getStatNode(12);
        while (ni != null) {
            ListNode<Sprite> nexti = ni.getNext();
            final Sprite s = ni.get();
            final int i = ni.getIndex();
            Sector sec = boardService.getSector(s.getSectnum());

            if (hittype[i].temp_data[0] == 0) {
                s.setZ(s.getZ() - (16 << 8));
                hittype[i].temp_data[1] = s.getAng();
                int x = s.getExtra();

                int j = ifhitbyweapon(i);
                if (j >= 0) {
                    if (j == CROSSBOW || j == CHIKENCROSSBOW || j == RADIUSEXPLOSION || j == SEENINE || j == 1273) {
                        if (s.getExtra() <= 0) {
                            hittype[i].temp_data[0] = 1;
                            ListNode<Sprite> node = boardService.getStatNode(12);
                            while (node != null) {
                                Sprite sprite = node.get();
                                j = node.getIndex();
                                if (sprite.getHitag() == s.getHitag()) {
                                    hittype[j].temp_data[0] = 1;
                                    sprite.setCstat(sprite.getCstat() & (65535 - 64));
                                    if (sprite.getPicnum() == CEILINGSTEAM || sprite.getPicnum() == STEAM) {
                                        sprite.setCstat(sprite.getCstat() | 32768);
                                    }
                                }
                                node = node.getNext();
                            }
                        }
                    } else {
                        hittype[i].extra = 0;
                        s.setExtra((short) x);
                    }
                }
                s.setAng(hittype[i].temp_data[1]);
                s.setZ(s.getZ() + (16 << 8));
            } else if (hittype[i].temp_data[0] == 1) {
                if (s.getLotag() > 0) {
                    s.setLotag(s.getLotag() - 3);
                    // if(s.lotag <= 0)
                    {
                        s.setXvel((short) (64 + engine.krand() & 127));
                        s.setZvel((short) -(1024 + (engine.krand() & 1023))); // XXX
                        // IDA
                        // PRO
                    }
                } else {
                    if (s.getXvel() > 0) {
                        s.setXvel(s.getXvel() - 2);
                        ssp(i, CLIPMASK0);
                    }

                    int x;
                    if (floorspace(s.getSectnum())) {
                        x = 0;
                    } else {
                        if (ceilingspace(s.getSectnum())) {
                            x = currentGame.getCON().gc / 6;
                        } else {
                            x = currentGame.getCON().gc;
                        }
                    }

                    if (sec != null) {
                        if (s.getZ() < (sec.getFloorz() - FOURSLEIGHT)) {
                            s.setZvel(s.getZvel() + x);
                            if (s.getZvel() > 6144) {
                                s.setZvel(6144);
                            }
                            s.setZ(s.getZ() + s.getZvel());
                        }
                        if ((sec.getFloorz() - s.getZ()) < (16 << 8)) {
                            int j = 1 + (engine.krand() & 7);
                            for (x = 0; x < j; x++) {
                                RANDOMSCRAP(s, i);
                            }
                            engine.deletesprite(i);
                        }
                    }
                }
            }

            ni = nexti;
        }
    }

    @CommonPart
    private static void Detonate(Sprite s, int i, int[] t) {
        earthquaketime = 16;

        ListNode<Sprite> ni = boardService.getStatNode(3);
        while (ni != null) {
            Sprite sp = ni.get();
            int j = ni.getIndex();
            if (s.getHitag() == sp.getHitag()) {
                if (sp.getLotag() == 13) {
                    if (hittype[j].temp_data[2] == 0) {
                        hittype[j].temp_data[2] = 1;
                    }
                } else if (sp.getLotag() == 8) {
                    hittype[j].temp_data[4] = 1;
                } else if (sp.getLotag() == 18) {
                    if (hittype[j].temp_data[0] == 0) {
                        hittype[j].temp_data[0] = 1;
                    }
                } else if (sp.getLotag() == 21) {
                    hittype[j].temp_data[0] = 1;
                }
            }
            ni = ni.getNext();
        }

        s.setZ(s.getZ() - (32 << 8));

        if ((t[3] == 1 && s.getXrepeat() != 0) || s.getLotag() == -99) {
            int x = s.getExtra();
            spawn(i, EXPLOSION2);
            hitradius(i, currentGame.getCON().seenineblastradius, x >> 2, x - (x >> 1), x - (x >> 2), x);
            spritesound(PIPEBOMB_EXPLODE, i);
        }

        if (s.getXrepeat() != 0) {
            for (int x = 0; x < 8; x++) {
                RANDOMSCRAP(s, i);
            }
        }

        engine.deletesprite(i);
    }

    public static void movestandables() {
        ListNode<Sprite> ni = boardService.getStatNode(6), nexti;

        BOLT:
        for (; ni != null; ni = nexti) {
            nexti = ni.getNext();
            final int i = ni.getIndex();
            final Sprite s = ni.get();

            int[] t = hittype[i].temp_data;
            final int sect = s.getSectnum();
            final Sector sec = boardService.getSector(sect);
            if (sec == null) {
                engine.deletesprite(i);
                continue;
            }

            game.pInt.setsprinterpolate(i, s);

            if (IFWITHIN(s, CRANE, CRANE + 3)) {
                // t[0] = state
                // t[1] = checking sector number

                if (s.getXvel() != 0) {
                    getglobalz(i);
                }

                if (t[0] == 0) // Waiting to check the sector
                {
                    ListNode<Sprite> nj = boardService.getSectNode(t[1]), nextj;
                    while (nj != null) {
                        nextj = nj.getNext();
                        Sprite sp = nj.get();

                        switch (sp.getStatnum()) {
                            case 1:
                            case 2:
                            case 6:
                            case 10:
                                s.setAng(EngineUtils.getAngle(msx[t[4] + 1] - s.getX(), msy[t[4] + 1] - s.getY()));
                                engine.setsprite(nj.getIndex(), msx[t[4] + 1], msy[t[4] + 1], sp.getZ());
                                t[0]++;
                                continue BOLT;
                        }
                        nj = nextj;
                    }
                } else if (t[0] == 1) {
                    if (s.getXvel() < 184) {
                        s.setPicnum(CRANE + 1);
                        s.setXvel(s.getXvel() + 8);
                    }
                    ssp(i, CLIPMASK0);
                    if (sect == t[1]) {
                        t[0]++;
                    }
                } else if (t[0] == 2 || t[0] == 7) {
                    s.setZ(s.getZ() + (1024 + 512));

                    if (t[0] == 2) {
                        if ((sec.getFloorz() - s.getZ()) < (64 << 8)) {
                            if (s.getPicnum() > CRANE) {
                                s.setPicnum(s.getPicnum() - 1);
                            }
                        }

                        if ((sec.getFloorz() - s.getZ()) < (4096 + 1024)) {
                            t[0]++;
                        }
                    }
                    if (t[0] == 7) {
                        if ((sec.getFloorz() - s.getZ()) < (64 << 8)) {
                            if (s.getPicnum() > CRANE) {
                                s.setPicnum(s.getPicnum() - 1);
                            } else {
                                if (s.getOwner() == -2) {
                                    int p = findplayer(s);
                                    spritesound(390, ps[p].i);
                                    if (ps[p].on_crane == i) {
                                        ps[p].on_crane = -1;
                                    }
                                }
                                t[0]++;
                                s.setOwner(-1);
                            }
                        }
                    }
                } else if (t[0] == 3) {
                    s.setPicnum(s.getPicnum() + 1);
                    if (s.getPicnum() == (CRANE + 2)) {
                        int p = checkcursectnums(t[1]);
                        if (p >= 0 && ps[p].on_ground) {
                            s.setOwner(-2);
                            ps[p].on_crane = (short) i;
                            spritesound(390, ps[p].i);
                            ps[p].ang = (short) (s.getAng() + 1024);

                            ps[p].posxv = 0;
                            ps[p].posyv = 0;
                            ps[p].poszv = 0;
                            Sprite psp = boardService.getSprite(ps[p].i);
                            if (psp != null) {
                                psp.setXvel(0);
                            }
                            ps[p].look_ang = 0;
                            ps[p].rotscrnang = 0;
                        } else {
                            ListNode<Sprite> node = boardService.getSectNode(t[1]), nextj;
                            while (node != null) {
                                nextj = node.getNext();
                                switch (node.get().getStatnum()) {
                                    case 1:
                                    case 6:
                                        s.setOwner(node.getIndex());
                                        break;
                                }
                                node = nextj;
                            }
                        }

                        t[0]++;// Grabbed the sprite
                        t[2] = 0;
                        continue;
                    }
                } else if (t[0] == 4) // Delay before going up
                {
                    t[2]++;
                    if (t[2] > 10) {
                        t[0]++;
                    }
                } else if (t[0] == 5 || t[0] == 8) {
                    if (t[0] == 8 && s.getPicnum() < (CRANE + 2)) {
                        if ((sec.getFloorz() - s.getZ()) > 8192) {
                            s.setPicnum(s.getPicnum() + 1);
                        }
                    }

                    if (s.getZ() < msx[t[4] + 2]) {
                        t[0]++;
                        s.setXvel(0);
                    } else {
                        s.setZ(s.getZ() - (1024 + 512));
                    }
                } else if (t[0] == 6) {
                    if (s.getXvel() < 192) {
                        s.setXvel(s.getXvel() + 8);
                    }
                    s.setAng(EngineUtils.getAngle(msx[t[4]] - s.getX(), msy[t[4]] - s.getY()));
                    ssp(i, CLIPMASK0);
                    if (((s.getX() - msx[t[4]]) * (s.getX() - msx[t[4]]) + (s.getY() - msy[t[4]]) * (s.getY() - msy[t[4]])) < (128 * 128)) {
                        t[0]++;
                    }
                } else if (t[0] == 9) {
                    t[0] = 0;
                }

                if (boardService.isValidSprite(msy[t[4] + 2])) {
                    game.pInt.setsprinterpolate(msy[t[4] + 2], boardService.getSprite(msy[t[4] + 2]));
                    engine.setsprite((short) msy[t[4] + 2], s.getX(), s.getY(), s.getZ() - (34 << 8));
                }

                if (s.getOwner() != -1) {
                    int p = findplayer(s);
                    int j = ifhitbyweapon(i);
                    if (j >= 0) {
                        if (s.getOwner() == -2) {
                            if (ps[p].on_crane == i) {
                                ps[p].on_crane = -1;
                            }
                        }
                        s.setOwner(-1);
                        s.setPicnum(CRANE);
                        continue;
                    }

                    if (s.getOwner() >= 0) {
                        engine.setsprite(s.getOwner(), s.getX(), s.getY(), s.getZ());
                        s.setZvel(0);
                    } else if (s.getOwner() == -2) {

                        ps[p].oposx = ps[p].posx;
                        ps[p].oposy = ps[p].posy;
                        ps[p].oposz = ps[p].posz;
                        ps[p].oang = ps[p].ang;
                        ps[p].opyoff = ps[p].pyoff;

                        if (IsOriginalDemo()) {
                            ps[p].posx = s.getX() - (EngineUtils.cos(((int) ps[p].ang) & 2047) >> 6);
                            ps[p].posy = s.getY() - (EngineUtils.sin((int) ps[p].ang & 2047) >> 6);
                        } else {
                            ps[p].posx = (int) (s.getX() - (BCosAngle(BClampAngle(ps[p].ang)) / 64.0f));
                            ps[p].posy = (int) (s.getY() - (BSinAngle(BClampAngle(ps[p].ang)) / 64.0f));
                        }

                        ps[p].posz = s.getZ() + (2 << 8);
                        engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy, ps[p].posz);
                        Sprite psp = boardService.getSprite(ps[p].i);
                        if (psp != null) {
                            ps[p].cursectnum = psp.getSectnum();
                        }
                    }
                }

                continue;
            }

            if (IFWITHIN(s, WATERFOUNTAIN, WATERFOUNTAIN + 3)) {
                if (t[0] > 0) {
                    if (t[0] < 20) {
                        t[0]++;

                        s.setPicnum(s.getPicnum() + 1);

                        if (s.getPicnum() == (WATERFOUNTAIN + 3)) {
                            s.setPicnum(WATERFOUNTAIN + 1);
                        }
                    } else {
                        findplayer(s);
                        int x = player_dist;
                        if (x > 512) {
                            t[0] = 0;
                            s.setPicnum(WATERFOUNTAIN);
                        } else {
                            t[0] = 1;
                        }
                    }
                }
                continue;
            }

            if (AFLAMABLE(s.getPicnum())) {
                if (hittype[i].temp_data[0] == 1) {
                    hittype[i].temp_data[1]++;
                    if ((hittype[i].temp_data[1] & 3) > 0) {
                        continue;
                    }

                    if (s.getPicnum() == TIRE && hittype[i].temp_data[1] == 32) {
                        s.setCstat(0);
                        int j = spawn(i, BLOODPOOL);
                        Sprite sp = boardService.getSprite(j);
                        if (sp != null) {
                            sp.setShade(127);
                        }
                    } else {
                        if (s.getShade() < 64) {
                            s.setShade(s.getShade() + 1);
                        } else {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    int j = s.getXrepeat() - (engine.krand() & 7);
                    if (j < 10) {
                        engine.deletesprite(i);
                        continue;
                    }

                    s.setXrepeat((short) j);

                    j = s.getYrepeat() - (engine.krand() & 7);
                    if (j < 4) {
                        engine.deletesprite(i);
                        continue;
                    }
                    s.setYrepeat(j);
                }
                continue;
            }

            if (s.getPicnum() >= CRACK1 && s.getPicnum() <= CRACK4) {
                if (s.getHitag() > 0) {
                    t[0] = s.getCstat();
                    t[1] = s.getAng();
                    int j = ifhitbyweapon(i);
                    if (j == CROSSBOW || j == CHIKENCROSSBOW || j == RADIUSEXPLOSION || j == SEENINE || j == OOZFILTER) {
                        ListNode<Sprite> node = boardService.getStatNode(6);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (s.getHitag() == sp.getHitag() && (sp.getPicnum() == OOZFILTER || sp.getPicnum() == SEENINE)) {
                                if (sp.getShade() != -32) {
                                    sp.setShade(-32);
                                }
                            }
                            node = node.getNext();
                        }

                        Detonate(s, i, t);
                        continue;
                    } else {
                        s.setCstat((short) t[0]);
                        s.setAng((short) t[1]);
                        s.setExtra(0);
                    }
                }
                continue;
            }

            if (s.getPicnum() == OOZFILTER || s.getPicnum() == SEENINE || s.getPicnum() == SEENINEDEAD || s.getPicnum() == (SEENINEDEAD + 1)) {
                if (s.getShade() != -32 && s.getShade() != -33) {
                    int j = 0;
                    if (s.getXrepeat() != 0) {
                        j = (ifhitbyweapon(i) >= 0) ? 1 : 0;
                    }

                    if (j != 0 || s.getShade() == -31) {
                        if (j != 0) {
                            s.setLotag(0);
                        }

                        t[3] = 1;

                        ListNode<Sprite> node = boardService.getStatNode(6);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (s.getHitag() == sp.getHitag() && (sp.getPicnum() == SEENINE || sp.getPicnum() == OOZFILTER)) {
                                sp.setShade(-32);
                            }
                            node = node.getNext();
                        }
                    }
                } else {
                    if (s.getShade() == -32) {
                        if (s.getLotag() > 0) {
                            s.setLotag(s.getLotag() - 3);
                            if (s.getLotag() <= 0) {
                                s.setLotag(-99);
                            }
                        } else {
                            s.setShade(-33);
                        }
                    } else {
                        if (s.getXrepeat() > 0) {
                            hittype[i].temp_data[2]++;
                            if (hittype[i].temp_data[2] == 3) {
                                if (s.getPicnum() == OOZFILTER) {
                                    hittype[i].temp_data[2] = 0;
                                    Detonate(s, i, t);
                                    continue;
                                }
                                if (s.getPicnum() != (SEENINEDEAD + 1)) {
                                    hittype[i].temp_data[2] = 0;

                                    if (s.getPicnum() == SEENINEDEAD) {
                                        s.setPicnum(s.getPicnum() + 1);
                                    } else if (s.getPicnum() == SEENINE) {
                                        s.setPicnum(SEENINEDEAD);
                                    }
                                } else {
                                    Detonate(s, i, t);
                                    continue;
                                }
                            }
                            continue;
                        }

                        Detonate(s, i, t);
                    }
                }

                continue;
            }

            if (s.getPicnum() == MASTERSWITCH) {
                if (s.getYvel() == 1) {
                    s.setHitag(s.getHitag() - 1);
                    if (s.getHitag() <= 0) {
                        operatesectors(sect, i);

                        ListNode<Sprite> node = boardService.getSectNode(sect);
                        while (node != null) {
                            Sprite sp = node.get();
                            int j = node.getIndex();
                            if (sp.getStatnum() == 3) {
                                switch (sp.getLotag()) {
                                    case 2:
                                    case 21:
                                    case 31:
                                    case 32:
                                    case 36:
                                        hittype[j].temp_data[0] = 1;
                                        break;
                                    case 3:
                                        hittype[j].temp_data[4] = 1;
                                        break;
                                }
                            } else if (sp.getStatnum() == 6) {
                                switch (sp.getPicnum()) {
                                    case SEENINE:
                                    case OOZFILTER:
                                        sp.setShade(-31);
                                        break;
                                }
                            }
                            node = node.getNext();
                        }
                        engine.deletesprite(i);
                    }
                }
                continue;
            }

            switch (s.getPicnum()) {
                case TRASH: // ok

                    if (s.getXvel() == 0) {
                        s.setXvel(1);
                    }
                    if (ssp(i, CLIPMASK0)) {
                        makeitfall(currentGame.getCON(), i);
                        if ((engine.krand() & 1) != 0) {
                            s.setZvel(s.getZvel() - 256);
                        }
                        if (klabs(s.getXvel()) < 48) {
                            s.setXvel(s.getXvel() + (engine.krand() & 3));
                        }
                    } else {
                        engine.deletesprite(i);
                        continue;
                    }
                    break;

                case BOLT1:
                case BOLT1 + 1:
                case BOLT1 + 2:
                case BOLT1 + 3: {
                    findplayer(s);
                    int x = player_dist;
                    if (x > 20480) {
                        continue;
                    }

                    if (t[3] == 0) {
                        t[3] = sec.getFloorshade();
                    }

                    do {
                        if (t[2] != 0) {
                            t[2]--;
                            sec.setFloorshade(20);
                            sec.setCeilingshade(20);
                            continue BOLT;
                        }

                        if ((s.getXrepeat() | s.getYrepeat()) == 0) {
                            s.setXrepeat((short) t[0]);
                            s.setYrepeat((short) t[1]);
                        } else if (((engine.krand() & 8) == 0)) {
                            t[0] = s.getXrepeat();
                            t[1] = s.getYrepeat();
                            t[2] = global_random & 4;
                            s.setXrepeat(0);
                            s.setYrepeat(0);
                            continue;
                        }
                        break;
                    } while (true);

                    s.setPicnum(s.getPicnum() + 1);

                    int l = global_random & 7;
                    s.setXrepeat((short) (l + 8));

                    if ((l & 1) != 0) {
                        s.setCstat(s.getCstat() ^ 2);
                    }

                    if (s.getPicnum() == (BOLT1 + 1) && (engine.krand() & 7) == 0 && sec.getFloorpicnum() == HURTRAIL) {
                        spritesound(SHORT_CIRCUIT, i);
                    }

                    if (s.getPicnum() == BOLT1 + 4) {
                        s.setPicnum(BOLT1);
                    }

                    if ((s.getPicnum() & 1) != 0) {
                        sec.setFloorshade(0);
                        sec.setCeilingshade(0);
                    } else {
                        sec.setFloorshade(20);
                        sec.setCeilingshade(20);
                    }
                    continue;
                }
                case WATERDRIP:
                    if (t[1] != 0) {
                        t[1]--;
                        if (t[1] == 1) // XXX == 0 in duke
                        {
                            s.setCstat(s.getCstat() & 32767);
                        }
                    } else {
                        makeitfall(currentGame.getCON(), i);
                        ssp(i, CLIPMASK0);
                        if (s.getXvel() > 0) {
                            s.setXvel(s.getXvel() - 2);
                        }

                        if (s.getZvel() == 0) {
                            s.setCstat(s.getCstat() | 32768);

                            if (s.getPal() != 2 && s.getHitag() == 0) {
                                spritesound(SOMETHING_DRIPPING, i);
                            }

                            Sprite sp = boardService.getSprite(s.getOwner());
                            if (sp != null && sp.getPicnum() != WATERDRIP) {
                                engine.deletesprite(i);
                                continue;
                            } else {
                                game.pInt.setsprinterpolate(i, s);
                                s.setZ(t[0]);
                                t[1] = 48 + (engine.krand() & 31);
                            }
                        }
                    }

                    continue;

                case DOORSHOCK: {
                    int j = klabs(sec.getCeilingz() - sec.getFloorz()) >> 9;
                    s.setYrepeat((short) (j + 4));
                    s.setXrepeat(16);
                    s.setZ(sec.getFloorz());
                    continue;
                }
                case TOUCHPLATE: { // ok
                    if (t[1] == 1 && s.getHitag() >= 0) // Move the sector floor
                    {
                        int x = sec.getFloorz();
                        if (t[3] == 1) { // down
                            game.pInt.setfloorinterpolate(sect, sec);
                            if (x >= t[2]) {
                                sec.setFloorz(x);
                                t[1] = 0;
                            } else {
                                sec.setFloorz(sec.getFloorz() + sec.getExtra());
                                int p = checkcursectnums(sect);
                                if (p >= 0) {
                                    ps[p].posz += sec.getExtra();
                                }
                            }
                        } else {
                            if (x <= s.getZ()) {
                                game.pInt.setfloorinterpolate(sect, sec);
                                sec.setFloorz(s.getZ());
                                t[1] = 0;
                            } else {
                                game.pInt.setfloorinterpolate(sect, sec);
                                sec.setFloorz(sec.getFloorz() - sec.getExtra());
                                int p = checkcursectnums(sect);
                                if (p >= 0) {
                                    ps[p].posz -= sec.getExtra();
                                }
                            }
                        }
                        continue;
                    }

                    if (t[5] == 1) {
                        continue;
                    }

                    int p = checkcursectnums(sect);
                    if (p >= 0 && (ps[p].on_ground || s.getAng() == 512)) {
                        if (t[0] == 0 && !check_activator_motion(s.getLotag())) {
                            t[0] = 1;
                            t[1] = 1;
                            t[3] ^= 1;
                            operatemasterswitches(s.getLotag());
                            operateactivators(s.getLotag(), p);
                            if (s.getHitag() > 0) {
                                s.setHitag(s.getHitag() - 1);
                                if (s.getHitag() == 0) {
                                    t[5] = 1;
                                }
                            }
                        }
                    } else {
                        t[0] = 0;
                    }

                    if (t[1] == 1) {
                        ListNode<Sprite> node = boardService.getStatNode(6);
                        while (node != null) {
                            int j = node.getIndex();
                            Sprite sp = node.get();
                            if (j != i && sp.getPicnum() == TOUCHPLATE && sp.getLotag() == s.getLotag()) {
                                hittype[j].temp_data[1] = 1;
                                hittype[j].temp_data[3] = t[3];
                            }
                            node = node.getNext();
                        }
                    }
                    continue;
                }
                case CANWITHSOMETHING: { // ok?
                    makeitfall(currentGame.getCON(), i);
                    int j = ifhitbyweapon(i);

                    if (j >= 0) {
                        spritesound(VENT_BUST, i);
                        for (j = 0; j < 10; j++) {
                            RANDOMSCRAP(s, i);
                        }

                        if (s.getLotag() != 0) {
                            spawn(i, s.getLotag());
                        }

                        engine.deletesprite(i);
                    }
                    continue;
                }
                case 1187:
                case 1196:
                case 1251:
                case 1268:
                case 1304:
                case 1305:
                case 1306:
                case 1315:
                case 1317:
                case 1388:
                case STEAM:
                case CEILINGSTEAM: {
                    int p = findplayer(s);
                    execute(currentGame.getCON(), i, p, player_dist);
                    continue;
                }
                case WATERBUBBLEMAKER: { // ok
                    int p = findplayer(s);
                    execute(currentGame.getCON(), i, p, player_dist);
                    break;
                }
            }
        }
    }

    @CommonPart
    public static void bounce(int i) {
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        int xvect = mulscale(s.getXvel(), EngineUtils.sin((s.getAng() + 512) & 2047), 10);
        int yvect = mulscale(s.getXvel(), EngineUtils.sin(s.getAng() & 2047), 10);
        int zvect = s.getZvel();

        final int hitsect = s.getSectnum();
        Sector hsec = boardService.getSector(hitsect);
        if (hsec == null || hsec.getWallNode() == null) {
            return;
        }

        int daang = hsec.getWallNode().get().getWallAngle();

        int k;
        if (s.getZ() < (hittype[i].floorz + hittype[i].ceilingz) >> 1) {
            k = hsec.getCeilingheinum();
        } else {
            k = hsec.getFloorheinum();
        }

        int dax = mulscale(k, EngineUtils.sin((daang) & 2047), 14);
        int day = mulscale(k, EngineUtils.sin((daang + 1536) & 2047), 14);
        int daz = 4096;

        k = xvect * dax + yvect * day + zvect * daz;
        int l = dax * dax + day * day + daz * daz;
        if ((klabs(k) >> 14) < l) {
            k = divscale(k, l, 17);
            xvect -= mulscale(dax, k, 16);
            yvect -= mulscale(day, k, 16);
            zvect -= mulscale(daz, k, 16);
        }

        s.setZvel(zvect);
        s.setXvel(EngineUtils.sqrt(dmulscale(xvect, xvect, yvect, yvect, 8)));
        s.setAng(EngineUtils.getAngle(xvect, yvect));
    }

    public static void movetransports() {
        ListNode<Sprite> ni = boardService.getStatNode(9), nexti;

        BOLT:
        for (; ni != null; ni = nexti) {
            nexti = ni.getNext();
            final Sprite sp = ni.get();
            final int i = ni.getIndex(); // it
            final int sect = sp.getSectnum();
            final Sector sec = boardService.getSector(sect);
            if (sec == null) {
                continue;
            }

            int sectlotag = sec.getLotag();
            if (sp.getOwner() == i) {
                continue;
            }

            int onfloorz = hittype[i].temp_data[4];
            if (hittype[i].temp_data[0] > 0) {
                hittype[i].temp_data[0]--;
            }

            ListNode<Sprite> node = boardService.getSectNode(sect), nextj;
            for (; node != null; node = nextj) {
                nextj = node.getNext();
                final Sprite spr = node.get();
                final int j = node.getIndex();

                switch (spr.getStatnum()) {
                    case 10: { // Player

                        Sprite spo = boardService.getSprite(sp.getOwner());
                        if (spo != null) {
                            int p = spr.getYvel();

                            ps[p].on_warping_sector = 1;

                            if (ps[p].transporter_hold == 0 && ps[p].jumping_counter == 0) {
                                if (ps[p].on_ground && sectlotag == 0 && onfloorz != 0 && ps[p].jetpack_on == 0) {
                                    if (sp.getPal() == 0) {
                                        spawn(i, TRANSPORTERBEAM);
                                        spritesound(TELEPORTER, i);
                                    }

                                    for (int k = connecthead; k >= 0; k = connectpoint2[k]) {
                                        if (ps[k].cursectnum == spo.getSectnum()) {
                                            ps[k].frag_ps = (short) p;
                                            Sprite psp = boardService.getSprite(ps[k].i);
                                            if (psp != null) {
                                                psp.setExtra(0);
                                            }
                                        }
                                    }

                                    ps[p].ang = spo.getAng();

                                    if (spo.getOwner() != sp.getOwner()) {
                                        hittype[i].temp_data[0] = 13;
                                        hittype[sp.getOwner()].temp_data[0] = 13;
                                        ps[p].transporter_hold = 13;
                                    }

                                    ps[p].bobposx = ps[p].oposx = ps[p].posx = spo.getX();
                                    ps[p].bobposy = ps[p].oposy = ps[p].posy = spo.getY();
                                    ps[p].oposz = ps[p].posz = spo.getZ() - PHEIGHT;

                                    game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                                    ps[p].UpdatePlayerLoc();

                                    engine.changespritesect(j, spo.getSectnum());
                                    ps[p].cursectnum = spr.getSectnum();

                                    int k = (short) spawn(sp.getOwner(), TRANSPORTERBEAM);
                                    spritesound(TELEPORTER, k);

                                    break;
                                }
                            } else // if (!(sectlotag == 1 && ps[p].on_ground)) GDX 2.10.18
                            {
                                break;
                            }

                            if (onfloorz == 0 && klabs(sp.getZ() - ps[p].posz) < 6144) {
                                if ((ps[p].jetpack_on == 0) || (ps[p].jetpack_on != 0 && (sync[p].bits & 1) != 0) || (ps[p].jetpack_on != 0 && (sync[p].bits & 2) != 0)) {
                                    ps[p].oposx = ps[p].posx += spo.getX() - sp.getX();
                                    ps[p].oposy = ps[p].posy += spo.getY() - sp.getY();

                                    if (ps[p].jetpack_on != 0 && ((sync[p].bits & 1) != 0 || ps[p].jetpack_on < 11)) {
                                        ps[p].posz = spo.getZ() - 6144;
                                    } else {
                                        ps[p].posz = spo.getZ() + 6144;
                                    }
                                    ps[p].oposz = ps[p].posz;

                                    game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                                    ps[p].UpdatePlayerLoc();

                                    engine.changespritesect(j, spo.getSectnum());
                                    ps[p].cursectnum = spo.getSectnum();

                                    break;
                                }
                            }

                            int k = 0;
                            Sector owsec = boardService.getSector(spo.getSectnum());
                            if (currentGame.getCON().type == RRRA) {
                                if (onfloorz != 0 && sectlotag == 160 && ps[p].posz > (sec.getFloorz() - 12288)) {
                                    k = 2;
                                    if (owsec != null) {
                                        ps[p].oposz = ps[p].posz = owsec.getCeilingz() + 1792;
                                    }
                                }

                                if (onfloorz != 0 && sectlotag == 161 && ps[p].posz < (sec.getCeilingz() + 1536)) {
                                    k = 2;
                                    Sprite psp = boardService.getSprite(ps[p].i);
                                    if (psp != null && psp.getExtra() <= 0) {
                                        break;
                                    }

                                    if (owsec != null) {
                                        ps[p].oposz = ps[p].posz = owsec.getFloorz() - 12544;
                                    }
                                }
                            }

                            if ((onfloorz != 0 && sectlotag == 1
                                    // && ps[p].on_ground GDX
                                    && ps[p].posz > (sec.getFloorz() - 1536)) || (onfloorz != 0 && sectlotag == 1 && ps[p].OnMotorcycle)) // (16 << 8)))
                            // && ((sync[p].bits & 2) != 0 || ps[p].poszv > 2048)) GDX
                            {
                                if (ps[p].OnBoat) {
                                    break;
                                }
                                k = 1;
                                if (screenpeek == p) {
                                    Sounds.stopAllSounds();
                                    clearsoundlocks();
                                }
                                spritesound(DUKE_UNDERWATER, j);
                                if (owsec != null) {
                                    ps[p].oposz = ps[p].posz = owsec.getCeilingz() + (7 << 8);
                                }

                                if (ps[p].OnMotorcycle) {
                                    ps[p].CarVar1 = 1;
                                }

                                game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                                ps[p].UpdatePlayerLoc();
                            }

                            if (onfloorz != 0 && sectlotag == 2 && ps[p].posz < (sec.getCeilingz() + (6 << 8))) {
                                k = 1;
                                if (spr.getExtra() <= 0) {
                                    break;
                                }
                                if (screenpeek == p) {
                                    Sounds.stopAllSounds();
                                    clearsoundlocks();
                                }
                                spritesound(DUKE_GASP, j);

                                if (owsec != null) {
                                    ps[p].oposz = ps[p].posz = owsec.getFloorz() - (7 << 8);
                                }
                                game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                                ps[p].UpdatePlayerLoc();

//							ps[p].jumping_toggle = 1; GDX 2.10.2018
//							ps[p].jumping_counter = 0;
                            }

                            if (k == 1) {
                                game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));

                                ps[p].oposx = ps[p].posx += spo.getX() - sp.getX();
                                ps[p].oposy = ps[p].posy += spo.getY() - sp.getY();

                                if (spo.getOwner() != sp.getOwner()) {
                                    ps[p].transporter_hold = -2;
                                }
                                ps[p].cursectnum = spo.getSectnum();

                                engine.changespritesect(j, spo.getSectnum());
//							engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy,
//									ps[p].posz + PHEIGHT); GDX 2.10.2018

                                setpal(ps[p]);

                                if ((engine.krand() & 255) < 32) {
                                    spawn(j, WATERSPLASH2);
                                }

                                ps[p].UpdatePlayerLoc();
                            } else if (k == 2) {
                                game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));

                                ps[p].oposx = ps[p].posx += spo.getX() - sp.getX();
                                ps[p].oposy = ps[p].posy += spo.getY() - sp.getY();

                                if (spo.getOwner() != sp.getOwner()) {
                                    ps[p].transporter_hold = -2;
                                }
                                ps[p].cursectnum = spo.getSectnum();
                                engine.changespritesect(j, spo.getSectnum());

                                ps[p].UpdatePlayerLoc();
                            }
                        }
                        break;
                    }
                    case 1:
                        if (currentGame.getCON().type != RRRA) {
                            switch (spr.getPicnum()) {
                                case 5501:
                                case UFO1:
                                case UFO2:
                                case UFO3:
                                case UFO4:
                                case UFO5:
                                    // if (spr.extra > 0) GDX 2.10.2018
                                    continue;
                            }
                        } else {
                            switch (spr.getPicnum()) {
                                case 5501:
                                case DAISYAIRBOAT:
                                case HULKAIRBOAT:
                                case MINIONAIRBOAT:
                                case MINIONUFO:
                                    continue;
                            }
                        }
                    case 4:
                    case 5:
                    case 12:
                    case 13:

                        int ll = klabs(spr.getZvel());

                    {
                        int warpspriteto = 0;
                        if (ll != 0 && sectlotag == 2 && spr.getZ() < (sec.getCeilingz() + ll)) {
                            warpspriteto = 1;
                        }

                        if (ll != 0 && sectlotag == 1 && spr.getZ() > (sec.getFloorz() - ll) && spr.getPicnum() != DAISYAIRBOAT && spr.getPicnum() != HULKAIRBOAT && spr.getPicnum() != MINIONAIRBOAT) {
                            warpspriteto = 1;
                        }

                        if (currentGame.getCON().type == RRRA) {
//                            int zz;
                            if (ll != 0 && sectlotag == 161 && spr.getZ() < (sec.getCeilingz() + ll) && spr.getZvel() < 0) {
                                warpspriteto = 1;
//                                zz = ll - klabs(spr.getZ() - sec.getCeilingz());
                            } else if (sectlotag == 161) {
                                if (sec.getCeilingz() + 1000 > spr.getZ() && spr.getZvel() < 0) {
                                    warpspriteto = 1;
//                                    zz = 1;
                                }
                            }
                            if (ll != 0 && sectlotag == 160 && spr.getZ() > (sec.getFloorz() - ll) && spr.getZvel() >= 0) {
                                warpspriteto = 1;
//                                zz = ll - klabs(sec.getFloorz() - spr.getZ());
                            } else if (sectlotag == 160) {
                                if (sec.getFloorz() - 1000 < spr.getZ() && spr.getZvel() >= 0) {
                                    warpspriteto = 1;
//                                    zz = 1;
                                }
                            }
                        }

                        if (sectlotag == 0 && (onfloorz != 0 || klabs(spr.getZ() - sp.getZ()) < 4096)) {
                            Sprite spo = boardService.getSprite(sp.getOwner());
                            if (spo != null && spo.getOwner() != sp.getOwner() && onfloorz != 0 && hittype[i].temp_data[0] > 0 && spr.getStatnum() != 5) {
                                hittype[i].temp_data[0]++;
                                continue BOLT;
                            }
                            warpspriteto = 1;
                        }

                        if (warpspriteto != 0) {
                            switch (spr.getPicnum()) {
                                case TRANSPORTERSTAR:
                                case TRANSPORTERBEAM:
                                case BULLETHOLE:
                                case WATERSPLASH2:
                                case BURNING:
                                case BURNING2:
                                case FIRE:
                                case FIRE2:
                                case MUD:
                                    continue;
                                case PLAYERONWATER:
                                    if (sectlotag == 2) {
                                        spr.setCstat(spr.getCstat() & 32767);
                                        break;
                                    }
                                default:
                                    if (spr.getStatnum() == 5 && !(sectlotag == 1 || sectlotag == 2 || sectlotag == 160 || sectlotag == 161)) {
                                        break;
                                    }

                                case WATERBUBBLE:
                                    if ((engine.krand() >> 8) < 63) { // XXX Check it (not in duke)
                                        Sprite spo = boardService.getSprite(sp.getOwner());
                                        if (sectlotag > 0) {
                                            int k = spawn(j, WATERSPLASH2);
                                            Sprite s = boardService.getSprite(k);
                                            if (s != null && sectlotag == 1 && spr.getStatnum() == 4) {
                                                s.setXvel((short) (spr.getXvel() >> 1));
                                                s.setAng(spr.getAng());
                                                ssp(k, CLIPMASK0);
                                            }
                                        }

                                        if (spo != null) {
                                            Sector owsec = boardService.getSector(spo.getSectnum());
                                            if (owsec == null) {
                                                break;
                                            }

                                            switch (sectlotag) {
                                                case 0:
                                                    if (onfloorz != 0) {
                                                        if (spr.getStatnum() == 4 || (checkcursectnums(sect) == -1 && checkcursectnums(spo.getSectnum()) == -1)) {
                                                            game.pInt.setsprinterpolate(j, spr);

                                                            spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                            spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                            spr.setZ(spr.getZ() - (sp.getZ() - owsec.getFloorz()));
                                                            spr.setAng(spo.getAng());

                                                            if (sp.getPal() == 0) {
                                                                int k = spawn(i, TRANSPORTERBEAM);
                                                                spritesound(TELEPORTER, k);

                                                                k = spawn(sp.getOwner(), TRANSPORTERBEAM);
                                                                spritesound(TELEPORTER, k);
                                                            }

                                                            if (spo.getOwner() != sp.getOwner()) {
                                                                hittype[i].temp_data[0] = 13;
                                                                hittype[sp.getOwner()].temp_data[0] = 13;
                                                            }

                                                            engine.changespritesect(j, spo.getSectnum());
                                                        }
                                                    } else {
                                                        game.pInt.setsprinterpolate(j, spr);

                                                        spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                        spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                        spr.setZ(spo.getZ() + 4096);

                                                        engine.changespritesect(j, spo.getSectnum());
                                                    }
                                                    break;
                                                case 1:

                                                    game.pInt.setsprinterpolate(j, spr);

                                                    spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                    spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                    spr.setZ(owsec.getCeilingz() + ll);

                                                    engine.changespritesect(j, spo.getSectnum());

                                                    break;
                                                case 2:

                                                    game.pInt.setsprinterpolate(j, spr);

                                                    spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                    spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                    spr.setZ(owsec.getFloorz() - ll);

                                                    engine.changespritesect(j, spo.getSectnum());
                                                    break;
                                                case 160:
                                                    if (currentGame.getCON().type == RRRA) {
                                                        int zz = 0; // FIXME:?
                                                        game.pInt.setsprinterpolate(j, spr);

                                                        spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                        spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                        spr.setZ(zz + owsec.getCeilingz());

                                                        engine.changespritesect(j, spo.getSectnum());

                                                        movesprite(j, (spr.getXvel() * (EngineUtils.cos((spr.getAng()) & 2047))) >> 14, (spr.getXvel() * (EngineUtils.sin(spr.getAng() & 2047))) >> 14, 0, CLIPMASK1);
                                                    }
                                                    break;
                                                case 161:
                                                    if (currentGame.getCON().type == RRRA) {
                                                        int zz = 0; // FIXME:?
                                                        game.pInt.setsprinterpolate(j, spr);

                                                        spr.setX(spr.getX() + (spo.getX() - sp.getX()));
                                                        spr.setY(spr.getY() + (spo.getY() - sp.getY()));
                                                        spr.setZ(owsec.getFloorz() - zz);

                                                        engine.changespritesect(j, spo.getSectnum());

                                                        movesprite(j, (spr.getXvel() * (EngineUtils.cos((spr.getAng()) & 2047))) >> 14, (spr.getXvel() * (EngineUtils.sin(spr.getAng() & 2047))) >> 14, 0, CLIPMASK1);
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void moveactors() {
        if (numjaildoors != 0) {
            movejails();
        }
        if (numminecart != 0) {
            movecarts();
        }

        for (ListNode<Sprite> node = boardService.getStatNode(115); node != null; node = node.getNext()) { // RA
            final int i = node.getIndex();
            final Sprite s = node.get();

            if (s.getExtra() != 0) {
                if (s.getPicnum() != 8162) {
                    s.setPicnum(8162);
                }
                s.setExtra(s.getExtra() - 1);
                if (s.getExtra() == 0) {
                    int chance = engine.krand() & 0x7F;
                    if (chance >= 96) {
                        if (chance >= 112) {
                            if (chance >= 120) {
                                if (chance >= 126) {
                                    if ((ps[connecthead].SlotWin & 8) != 0) {
                                        s.setPicnum(8165);
                                    } else {
                                        s.setPicnum(8166);
                                        spawn(i, 5595);
                                        ps[connecthead].SlotWin |= 8;
                                        spritesound(52, i);
                                    }
                                } else if ((ps[connecthead].SlotWin & 4) != 0) {
                                    s.setPicnum(8165);
                                } else {
                                    s.setPicnum(8167);
                                    spawn(i, 52);
                                    ps[connecthead].SlotWin |= 4;
                                    spritesound(52, i);
                                }
                            } else if ((ps[connecthead].SlotWin & 2) != 0) {
                                s.setPicnum(8165);
                            } else {
                                s.setPicnum(8168);
                                spawn(i, 26);
                                ps[connecthead].SlotWin |= 2;
                                spritesound(52, i);
                            }
                        } else if ((ps[connecthead].SlotWin & 1) != 0) {
                            s.setPicnum(8165);
                        } else {
                            s.setPicnum(8164);
                            spawn(i, 41);
                            ps[connecthead].SlotWin |= 1;
                            spritesound(52, i);
                        }
                    } else {
                        s.setPicnum(8165);
                    }
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(116), nexti; node != null; node = nexti) {
            nexti = node.getNext();
            Sprite sp = node.get();
            int i = node.getIndex();

            if (sp.getExtra() != 0) {
                if (sp.getExtra() == sp.getLotag()) {
                    sound(183);
                }
                sp.setExtra(sp.getExtra() - 1);

                int k = movesprite(i, mulscale(EngineUtils.sin((sp.getAng() + 512) & kAngleMask), sp.getHitag(), 14), mulscale(EngineUtils.sin(sp.getAng() & kAngleMask), sp.getHitag(), 14), 2 * sp.getHitag(), CLIPMASK0);
                if (k > 0) {
                    spritesound(14, i);
                    engine.deletesprite(i);
                }
                if (sp.getExtra() == 0) {
                    sound(215);
                    engine.deletesprite(i);
                    earthquaketime = 32;
                    for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                        ps[p].pals[0] = 32;
                        ps[p].pals[1] = 32;
                        ps[p].pals[2] = 32;
                        ps[p].pals_time = 48;
                    }
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(117), nexti; node != null; node = nexti) {
            nexti = node.getNext();
            Sprite sp = node.get();
            int i = node.getIndex();

            if (sp.getHitag() > 2) {
                sp.setHitag(0);
            }
            if ((sp.getPicnum() == 8488 || sp.getPicnum() == 8490) && sp.getHitag() != 2) {
                sp.setHitag(2);
                sp.setExtra(-100);
            }

            if (sp.getHitag() != 0) {
                if (sp.getHitag() == 1) {
                    sp.setExtra(sp.getExtra() - 1);
                    if (sp.getExtra() <= -30) {
                        sp.setHitag(0);
                    }
                } else if (sp.getHitag() == 2) {
                    sp.setExtra(sp.getExtra() - 1);
                    if (sp.getExtra() <= -104) {
                        spawn(i, sp.getLotag());
                        engine.deletesprite(i);
                    }
                }
            } else {
                int extra = sp.getExtra() + 1;
                sp.setExtra(extra);
                if (extra >= 30) {
                    sp.setHitag(1);
                }
            }

            movesprite(i, 0, 0, 2 * sp.getExtra(), CLIPMASK0);
        }

        for (ListNode<Sprite> node = boardService.getStatNode(118); node != null; node = node.getNext()) {
            int i = node.getIndex(); // RA
            Sprite sp = node.get();
            if (sp.getHitag() > 1) {
                sp.setHitag(0);
            }
            if (sp.getHitag() != 0) {
                if (sp.getHitag() == 1) {
                    sp.setExtra(sp.getExtra() - 1);
                    if (sp.getExtra() <= -20) {
                        sp.setHitag(0);
                    }
                }
            } else {
                int extra = sp.getExtra() + 1;
                sp.setExtra(extra);
                if (extra >= 20) {
                    sp.setHitag(1);
                }
            }

            movesprite(i, 0, 0, sp.getExtra(), CLIPMASK0);
        }

        if (ps[connecthead].MamaEnd > 0 && --ps[connecthead].MamaEnd == 0) {
            LeaveMap();
            ud.eog = 1;
        }

        if (ps[connecthead].field_607 > 0) {
            for (int j = 0; j < boardService.getSpriteCount(); j++) {
                Sprite sp = boardService.getSprite(j);
                if (sp == null) {
                    continue;
                }

                switch (sp.getPicnum()) {
                    case 4147:
                    case 4162:
                    case 4163:
                    case 4249:
                    case 4260:
                    case 4352:
                    case 4429:
                    case 4649:
                    case 4650:
                    case 4861:
                    case 4916:
                    case 4945:
                    case 5120:
                    case 5121:
                    case MINIONUFO:
                    case 5274:
                    case 5278:
                    case 5282:
                    case 5286:
                    case 5376:
                    case 5377:
                    case 5635:
                    case 5890:
                    case 5891:
                    case 5995:
                    case 6225:
                    case 6401:
                    case 6658:
                    case 6659:
                    case 7030:
                    case 7035:
                    case 7192:
                    case 7199:
                    case 7206:
                    case 7280:
                    case 8705:
                        if (ps[connecthead].field_607 == 3) {
                            sp.setXrepeat(sp.getXrepeat() << 1);
                            sp.setYrepeat(sp.getYrepeat() << 1);
                            sp.setClipdist(engine.getTile(sp.getPicnum()).getWidth() * sp.getXrepeat() >> 7);
                        } else if (ps[connecthead].field_607 == 2) {
                            sp.setXrepeat(sp.getXrepeat() >> 1);
                            sp.setYrepeat(sp.getYrepeat() >> 1);
                            sp.setClipdist(engine.getTile(sp.getPicnum()).getWidth() * sp.getXrepeat() >> 7);
                        }
                        break;
                }
            }
            ps[connecthead].field_607 = 0;
        }

        for (ListNode<Sprite> node = boardService.getStatNode(119); node != null; node = node.getNext()) { // RA
            int i = node.getIndex();
            Sprite sp = node.get();
            if (sp.getHitag() > 0) {
                if (sp.getExtra() != 0) {
                    sp.setExtra(sp.getExtra() - 1);
                } else {
                    sp.setHitag(sp.getHitag() - 1);
                    sp.setExtra(150);
                    spawn(i, 7280);
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(121); node != null; node = node.getNext()) {
            int i = node.getIndex(); // RA
            Sprite sp = node.get();
            int extra = sp.getExtra() + 1;
            sp.setExtra(extra);
            if (extra >= 100) {
                Sector sec = boardService.getSector(sp.getSectnum());
                if (sec != null && sp.getExtra() == 200) {
                    engine.setsprite(i, sp.getX(), sp.getY(), sec.getFloorz() - 10);
                    sp.setExtra(1);
                    sp.setPicnum(4956);
                    spawn(i, 1398);
                }
            } else {
                if (sp.getExtra() == 90) {
                    int picnum = sp.getPicnum() - 1;
                    sp.setPicnum(picnum);
                    if (picnum < 4952) {
                        sp.setPicnum(4952);
                    }
                    sp.setExtra(1);
                }

                movesprite(i, 0, 0, -300, CLIPMASK0);
                Sector sec = boardService.getSector(sp.getSectnum());
                if (sec != null && sec.getCeilingz() + 1024 > sp.getZ()) {
                    sp.setPicnum(0);
                    sp.setExtra(100);
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(122); node != null; node = node.getNext()) { // RA
            int i = node.getIndex();
            Sprite sp = node.get();
            if (sp.getExtra() != 0) {
                if (sp.getPicnum() != 8589) {
                    sp.setPicnum(8589);
                }
                sp.setExtra(sp.getExtra() - 1);
                if (sp.getExtra() == 0) {
                    int change = engine.krand() & 0x7F;
                    if (change >= 96) {
                        if (change >= 112) {
                            if (change >= 120) {
                                if (change >= 126) {
                                    if ((ps[connecthead].SlotWin & 8) != 0) {
                                        sp.setPicnum(8593);
                                    } else {
                                        sp.setPicnum(8592);
                                        spawn(i, 5595);
                                        ps[connecthead].SlotWin |= 8;
                                        spritesound(342, i);
                                    }
                                } else if ((ps[connecthead].SlotWin & 4) != 0) {
                                    sp.setPicnum(8593);
                                } else {
                                    sp.setPicnum(8591);
                                    spawn(i, 52);
                                    ps[connecthead].SlotWin |= 4;
                                    spritesound(342, i);
                                }
                            } else if ((ps[connecthead].SlotWin & 2) != 0) {
                                sp.setPicnum(8593);
                            } else {
                                sp.setPicnum(8595);
                                spawn(i, 26);
                                ps[connecthead].SlotWin |= 2;
                                spritesound(342, i);
                            }
                        } else if ((ps[connecthead].SlotWin & 1) != 0) {
                            sp.setPicnum(8593);
                        } else {
                            sp.setPicnum(8594);
                            spawn(i, 41);
                            ps[connecthead].SlotWin |= 1;
                            spritesound(342, i);
                        }
                    } else {
                        sp.setPicnum(8593);
                    }
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(123); node != null; node = node.getNext()) { // RA
            int i = node.getIndex();
            if (node.get().getLotag() == 5 && Sound[WITNESSSTAND].getSoundOwnerCount() == 0) {
                spritesound(WITNESSSTAND, i);
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(107); node != null; node = node.getNext()) {
            Sprite s = node.get();
            if (s.getHitag() == 100) {
                Sector sec = boardService.getSector(s.getSectnum());
                if (sec != null) {
                    s.setZ(s.getZ() + 1024);
                    int z = sec.getFloorz() + 15168;
                    if (z <= s.getZ()) {
                        s.setZ(z);
                    }
                }
            }

            if (s.getPicnum() == LUMBERBLADE) {
                s.setExtra(s.getExtra() + 1);
                Sector sec = boardService.getSector(s.getSectnum());
                if (sec != null && s.getExtra() == 192) {
                    s.setHitag(0);
                    s.setExtra(0);
                    s.setPicnum(LUMBERBLADE - 1);
                    s.setZ(sec.getFloorz() - 15168);

                    for (ListNode<Sprite> nj = boardService.getStatNode(0), nextj; nj != null; nj = nextj) {
                        nextj = nj.getNext();
                        Sprite sp = nj.get();

                        if (sp.getPicnum() == 128 && sp.getHitag() == 999) {
                            sp.setPicnum(127);
                        }
                    }
                }
            }
        }

        if (plantProcess) {
            for (ListNode<Sprite> node = boardService.getStatNode(106); node != null; node = node.getNext()) {
                final int i = node.getIndex();
                final Sprite s = node.get();
                switch (s.getPicnum()) {
                    case CHICKENA:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, CHICKENATILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(128);
                        }
                        break;
                    case CHICKENC:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, CHICKENCTILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(256);
                        }
                        break;
                    case HEADCHK:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            lotsofmoney(s, ((engine.krand() & 3) + 4));
                            s.setLotag(84);
                        }
                        break;
                    case FEATHERCHK:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            s.setLotag(96);
                            spritesound(472, spawn(i, HEAD1TILE));
                        }
                        break;
                    case LOAF:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, LOAFTILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(448);
                        }
                        break;
                    case NUGGETS:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, NUGGETTILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(64);
                        }
                        break;
                    case PACKEDCHK:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, BROASTEDTILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(512);
                        }
                        break;
                    case BONELESSCHK:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            int c = spawn(i, BONELESSTILE);
                            Sprite spc = boardService.getSprite(c);
                            if (spc != null) {
                                spc.setAng(s.getAng());
                            }
                            s.setLotag(224);
                        }
                        break;
                    case JIBSCHK:
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() < 0) {
                            guts(s, 1463, 1, myconnectindex);
                            guts(s, 1468, 1, myconnectindex);
                            guts(s, 1473, 1, myconnectindex);
                            guts(s, 1478, 1, myconnectindex);
                            s.setLotag(256);
                        }
                        break;
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(105); node != null; node = node.getNext()) {
            Sprite s = node.get();
            if (s.getPicnum() == BOWLLINE && s.getLotag() == 100) {
                if (BowlOpen(s.getSectnum())) {
                    s.setLotag(0);
                    if (s.getExtra() == 1 && BowlCheck(s.getSectnum()) == 0) {
                        s.setExtra(2);
                    }
                    if (s.getExtra() == 2) {
                        s.setExtra(0);
                        BowlUpdate(s.getSectnum());
                    }
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(108), nexti; node != null; node = nexti) {
            nexti = node.getNext();
            Sprite sp = node.get();

            if (sp.getPicnum() == 296) {
                int p = findplayer(sp);

                if (player_dist < 2048) {
                    ListNode<Sprite> nj = boardService.getStatNode(108);
                    while (nj != null) {
                        ListNode<Sprite> nextj = nj.getNext();
                        Sprite s = nj.get();
                        if (s.getPicnum() == 297) {
                            ps[p].ang = s.getAng();
                            ps[p].posx = ps[p].oposx = ps[p].bobposx = s.getX();
                            ps[p].posy = ps[p].oposy = ps[p].bobposy = s.getY();
                            ps[p].posz = ps[p].oposz = s.getZ();
                            engine.changespritesect(ps[p].i, s.getSectnum());
                            Sprite spp = boardService.getSprite(p); // #GDX 02.07.2024 Really? Check it
                            if (spp != null) {
                                ps[p].cursectnum = spp.getSectnum();
                            }
                            spritesound(TELEPORTER, nj.getIndex());

                            game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                            ps[p].UpdatePlayerLoc();

                            if (ud.multimode < 2) {
                                engine.deletesprite(nj.getIndex());
                            } else {
                                s.setCstat(s.getCstat() & ~1);
                                s.setCstat(s.getCstat() | 32768);
                            }
                        }
                        nj = nextj;
                    }
                }
            }
        }

        ListNode<Sprite> ni = boardService.getStatNode(1), nexti;

        BOLT:
        for (; ni != null; ni = nexti) {
            nexti = ni.getNext();
            final int i = ni.getIndex();
            final Sprite s = ni.get();

            final int sect = s.getSectnum();
            final Sector sec = boardService.getSector(sect);

            if (s.getXrepeat() == 0 || sec == null) {
                engine.deletesprite(i);
                continue;
            }

            int[] t = hittype[i].temp_data;

            game.pInt.clearspriteinterpolate(i);
            game.pInt.setsprinterpolate(i, s);

            switch (s.getPicnum()) {
                case CAMERA1:

                    if (t[0] == 0) {
                        t[1] += 8;
                        if (currentGame.getCON().camerashitable != 0) {
                            int j = ifhitbyweapon(i);
                            if (j >= 0) {
                                t[0] = 1; // static
                                s.setCstat((short) 32768);
                                for (int x = 0; x < 5; x++) {
                                    RANDOMSCRAP(s, i);
                                }
                                continue;
                            }
                        }

                        if (s.getHitag() > 0) {
                            if (t[1] < s.getHitag()) {
                                s.setAng(s.getAng() + 8);
                            } else if (t[1] < (s.getHitag() * 3)) {
                                s.setAng(s.getAng() - 8);
                            } else if (t[1] < (s.getHitag() << 2)) {
                                s.setAng(s.getAng() + 8);
                            } else {
                                t[1] = 8;
                                s.setAng(s.getAng() + 16);
                            }
                        }
                    }
                    continue;

                case QUEBALL:
                case STRIPEBALL:
                    if (s.getXvel() != 0) {
                        ListNode<Sprite> node = boardService.getStatNode(0);
                        while (node != null) {
                            ListNode<Sprite> nextj = node.getNext();
                            Sprite sp = node.get();

                            if (sp.getPicnum() == POCKET && ldist(sp, s) < 52) {
                                engine.deletesprite(i);
                                continue BOLT;
                            }
                            node = nextj;
                        }

                        int moveHit = engine.clipmove(s.getX(), s.getY(), s.getZ(), s.getSectnum(), ((((long) s.getXvel() * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14) * TICSPERFRAME) << 11, ((((long) s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14) * TICSPERFRAME) << 11, 24, (4 << 8), (4 << 8), CLIPMASK1);

                        if (clipmove_sectnum != -1) {
                            s.setX(clipmove_x);
                            s.setY(clipmove_y);
                            s.setZ(clipmove_z);
                            s.setSectnum(clipmove_sectnum);
                        }

                        int nHitObject = moveHit & kHitTypeMask;
                        if (nHitObject != 0) {
                            if (nHitObject == kHitWall) {
                                moveHit &= kHitIndexMask;
                                Wall hwal = boardService.getWall(moveHit);
                                if (hwal != null) {
                                    int k = hwal.getWallAngle(); //EngineUtils.getAngle(boardService.getWall(boardService.getWall(moveHit).getPoint2()).getX() - boardService.getWall(moveHit).getX(), boardService.getWall(boardService.getWall(moveHit).getPoint2()).getY() - boardService.getWall(moveHit).getY());
                                    s.setAng((((k << 1) - s.getAng()) & 2047));
                                }
                            } else if (nHitObject == kHitSprite) {
                                moveHit &= kHitIndexMask;
                                checkhitsprite(i, moveHit);
                            }
                        }
                        s.setXvel(s.getXvel() - 1);
                        if (s.getXvel() < 0) {
                            s.setXvel(0);
                        }
                        if (s.getPicnum() == STRIPEBALL) {
                            s.setCstat(257);
                            s.setCstat(s.getCstat() | 4 & s.getXvel());
                            s.setCstat(s.getCstat() | 8 & s.getXvel());
                        }
                    } else {
                        int p = (short) findplayer(s);
                        int x = player_dist;
                        if (x < 1596) {

                            // if(s.pal == 12)
                            {
                                float fj = getincangle((int) ps[p].ang, EngineUtils.getAngle(s.getX() - ps[p].posx, s.getY() - ps[p].posy));
                                if (fj > -64 && fj < 64 && ((sync[p].bits & (1 << 29)) != 0)) {
                                    if (ps[p].toggle_key_flag == 1) {

                                        ListNode<Sprite> node = boardService.getStatNode(1);
                                        while (node != null) {
                                            Sprite sp = node.get();
                                            if (sp.getPicnum() == QUEBALL || sp.getPicnum() == STRIPEBALL) {
                                                fj = getincangle((int) ps[p].ang, EngineUtils.getAngle(sp.getX() - ps[p].posx, sp.getY() - ps[p].posy));
                                                if (fj > -64 && fj < 64) {
                                                    findplayer(sp);
                                                    int l = player_dist;
                                                    if (x > l) {
                                                        break;
                                                    }
                                                }
                                            }
                                            node = node.getNext();
                                        }
                                        if (node == null) {
                                            if (s.getPal() == 12) {
                                                s.setXvel(164);
                                            } else {
                                                s.setXvel(140);
                                            }
                                            s.setAng((short) ps[p].ang);
                                            ps[p].toggle_key_flag = 2;
                                        }
                                    }
                                }
                            }
                        }
                        if (x < 512 && s.getSectnum() == ps[p].cursectnum) {
                            s.setAng(EngineUtils.getAngle(s.getX() - ps[p].posx, s.getY() - ps[p].posy));
                            s.setXvel(48);
                        }
                    }

                    break;
                case RAT:
                    makeitfall(currentGame.getCON(), i);
                    if (ssp(i, CLIPMASK0)) {
                        if ((engine.krand() & 255) < 3) {
                            spritesound(RATTY, i);
                        }
                        s.setAng(s.getAng() + (engine.krand() & 31) - 15 + (EngineUtils.sin((t[0] << 8) & 2047) >> 11));
                    } else {
                        hittype[i].temp_data[0]++;
                        if (hittype[i].temp_data[0] > 1) {
                            engine.deletesprite(i);
                            continue;
                        } else {
                            s.setAng((short) (engine.krand() & 2047));
                        }
                    }
                    if (s.getXvel() < 128) {
                        s.setXvel(s.getXvel() + 2);
                    }
                    s.setAng(s.getAng() + (engine.krand() & 3) - 6);
                    break;

                case MORTER:
                case DYNAMITE:
                case 3464: {
                    if ((s.getCstat() & 32768) != 0) {
                        t[2]--;
                        if (t[2] <= 0) {
                            spritesound(TELEPORTER, i);
                            spawn(i, TRANSPORTERSTAR);
                            s.setCstat(257);
                        }
                        continue;
                    }

                    int p = findplayer(s);
                    int x = player_dist;

                    if (x < 1220) {
                        s.setCstat(s.getCstat() & ~257);
                    } else {
                        s.setCstat(s.getCstat() | 257);
                    }

                    int l = 0;
                    //noinspection SwitchStatementWithTooFewBranches
                    switch(l) { // goto block
                        default:
                        if (t[3] == 0) {
                            if (ifhitbyweapon(i) >= 0) {
                                t[3] = 1;
                                t[4] = 0;
                                s.setXvel(0);
                                break; // goto DETONATEB;
                            }
                        }

                        makeitfall(currentGame.getCON(), i);

                        if (sec.getLotag() != 1 && sec.getLotag() != 160 && s.getZ() >= hittype[i].floorz - (FOURSLEIGHT) && s.getYvel() < 3) {
                            if (s.getYvel() > 0 || (s.getYvel() == 0 && hittype[i].floorz == sec.getFloorz() && s.getPicnum() != 3464)) {
                                spritesound(PIPEBOMB_BOUNCE, i);
                            }

                            if (s.getPicnum() != 3464) {
                                s.setZvel((short) -((4 - s.getYvel()) << 8));
                                Sector sec2 = boardService.getSector(s.getSectnum());
                                if (sec2 != sec) { // FIXME: Check and delete
                                    Console.out.println("Actors(3598) sec2 != sec", OsdColor.RED);
                                }

                                if (sec2 != null && sec2.getLotag() == 2) {
                                    s.setZvel(s.getZvel() >> 2);
                                }
                                s.setYvel(s.getYvel() + 1);
                            } else {
                                t[3] = 1;
                                t[4] = 1;
                                break; // goto DETONATEB;
                            }
                        }

                        if (s.getPicnum() != 3464 && s.getZ() < hittype[i].ceilingz && sec.getLotag() != 2) {
                            s.setZ(hittype[i].ceilingz + (3 << 8));
                            s.setZvel(0);
                        }

                        int moveHit = movesprite(i, (s.getXvel() * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), CLIPMASK0);
                        Sector sec2 = boardService.getSector(s.getSectnum());
                        if (sec2 != null && sec2.getLotag() == 1 && s.getZvel() == 0) {
                            s.setZ(s.getZ() + (32 << 8));
                            if (t[5] == 0) {
                                t[5] = 1;
                                spawn(i, WATERSPLASH2);
                            }
                        } else {
                            t[5] = 0;
                        }

                        if (t[3] == 0 && (s.getPicnum() == MORTER || s.getPicnum() == 3464) && (moveHit != 0 || x < 844)) {
                            t[3] = 1;
                            t[4] = 0;
                            s.setXvel(0);
                            // goto DETONATEB;
                        } else {
                            Sprite spo = boardService.getSprite(s.getOwner());
                            if (spo != null && spo.getPicnum() == APLAYER) {
                                l = spo.getYvel();
                            } else {
                                l = -1;
                            }

                            if (s.getXvel() > 0) {
                                s.setXvel(s.getXvel() - 5);
                                if (sec.getLotag() == 2) {
                                    s.setXvel(s.getXvel() - 10);
                                }

                                if (s.getXvel() < 0) {
                                    s.setXvel(0);
                                }
                                if ((s.getXvel() & 8) != 0) {
                                    s.setCstat(s.getCstat() ^ 4);
                                }
                            }

                            if ((moveHit & kHitTypeMask) == kHitWall) {
                                moveHit &= kHitIndexMask;

                                checkhitwall(i, moveHit, s.getX(), s.getY(), s.getZ(), s.getPicnum());

                                if (s.getPicnum() == 3464) {
                                    t[3] = 1;
                                    t[4] = 0;
                                    s.setXvel(0);
                                } else {
                                    Wall hwall = boardService.getWall(moveHit);
                                    if (hwall != null) {
                                        int k = hwall.getWallAngle(); //EngineUtils.getAngle(boardService.getWall(boardService.getWall(moveHit).getPoint2()).getX() - boardService.getWall(moveHit).getX(), boardService.getWall(boardService.getWall(moveHit).getPoint2()).getY() - boardService.getWall(moveHit).getY());
                                        s.setAng((short) (((k << 1) - s.getAng()) & 2047));
                                        s.setXvel(s.getXvel() >> 1);
                                    }
                                }
                            }
                        }
                    }

                    // DETONATEB:

                    if ((l >= 0 && ps[l].hbomb_on == 0) || t[3] == 1) {
                        t[4]++;

                        if (t[4] == 2) {
                            x = s.getExtra();
                            int m = 0;
                            switch (s.getPicnum()) {
                                case DYNAMITE:
                                    m = currentGame.getCON().tntblastradius;
                                    break;
                                case MORTER:
                                case 3464:
                                    m = currentGame.getCON().morterblastradius;
                                    break;
                                case POWDERKEGSPRITE:
                                    m = currentGame.getCON().powderblastradius;
                                    break;
                            }

                            Sector sec2 = boardService.getSector(s.getSectnum());
                            if (sec2 != null && sec2.getLotag() != 800) {
                                hitradius(i, m, x >> 2, x >> 1, x - (x >> 2), x);
                                spawn(i, EXPLOSION2);
                                spritesound(PIPEBOMB_EXPLODE, i);
                                for (x = 0; x < 8; x++) {
                                    RANDOMSCRAP(s, i);
                                }
                            }
                        }

                        if (s.getYrepeat() != 0) {
                            s.setYrepeat(0);
                            continue;
                        }

                        if (t[4] > 20) {
                            if (s.getOwner() != i || !ud.respawn_items) {
                                engine.deletesprite(i);
                            } else {
                                t[2] = currentGame.getCON().respawnitemtime;
                                spawn(i, RESPAWNMARKERRED);
                                s.setCstat((short) 32768);
                                s.setYrepeat(9);
                            }
                            continue;
                        }

                        if (s.getPicnum() == 3464) {
                            spawn(i, BURNING);
                            engine.deletesprite(i);
                            continue;
                        }
                    } else if (s.getPicnum() == DYNAMITE && x < 788 && t[0] > 7 && s.getXvel() == 0) {
                        if (engine.cansee(s.getX(), s.getY(), s.getZ() - (8 << 8), s.getSectnum(), ps[p].posx, ps[p].posy, ps[p].posz, ps[p].cursectnum)) {
                            if (ps[p].ammo_amount[4] < currentGame.getCON().max_ammo_amount[4]) { // GDX 3.10.2018
                                if (ud.coop >= 1 && s.getOwner() == i) {
                                    for (int j = 0; j < ps[p].weapreccnt; j++) {
                                        if (ps[p].weaprecs[j] == s.getPicnum()) {
                                            continue BOLT; // v0.751
                                        }
                                    }

                                    if (ps[p].weapreccnt < 16) // v0.751
                                    {
                                        ps[p].weaprecs[ps[p].weapreccnt++] = s.getPicnum();
                                    }
                                }

                                addammo(4, ps[p], 1);
                                addammo(5, ps[p], 1);
                                spritesound(DUKE_GET, ps[p].i);

                                if (!ps[p].gotweapon[4] || s.getOwner() == ps[p].i) {
                                    addweapon(ps[p], 4);
                                }

                                Sprite spo = boardService.getSprite(s.getOwner());
                                if (spo != null && spo.getPicnum() != APLAYER) {
                                    ps[p].pals[0] = 0;
                                    ps[p].pals[1] = 32;
                                    ps[p].pals[2] = 0;
                                    ps[p].pals_time = 32;
                                }

                                if (s.getOwner() != i || !ud.respawn_items) {
                                    if (s.getOwner() == i && ud.coop >= 1) {
                                        continue;
                                    }

                                    engine.deletesprite(i);
                                    continue;
                                } else {
                                    t[2] = currentGame.getCON().respawnitemtime;
                                    spawn(i, RESPAWNMARKERRED);
                                    s.setCstat((short) 32768);
                                }
                            }
                        }
                    }

                    if (t[0] < 8) {
                        t[0]++;
                    }
                    continue;
                }
                case OOZ: {

                    getglobalz(i);

                    int j = (hittype[i].floorz - hittype[i].ceilingz) >> 9;
                    if (j > 255) {
                        j = 255;
                    }

                    int x = 25 - (j >> 1);
                    if (x < 8) {
                        x = 8;
                    } else if (x > 48) {
                        x = 48;
                    }

                    s.setYrepeat(j);
                    s.setXrepeat(x);
                    s.setZ(hittype[i].floorz);

                    continue;
                }
                case FORCESPHERE:
                    if (s.getYvel() == 0) {
                        s.setYvel(1);

                        for (int l = 512; l < (2048 - 512); l += 128) {
                            for (int j = 0; j < 2048; j += 128) {
                                int k = spawn(i, FORCESPHERE);
                                Sprite sp = boardService.getSprite(k);
                                if (sp != null) {
                                    sp.setCstat(257 + 128);
                                    sp.setClipdist(64);
                                    sp.setAng((short) j);
                                    sp.setXvel((short) (EngineUtils.cos(l & 2047) >> 9));
                                    sp.setZvel((short) (EngineUtils.sin(l & 2047) >> 5));
                                    sp.setOwner(i);
                                }
                            }
                        }
                    }

                    if (t[3] > 0) {
                        if (s.getZvel() < 6144) {
                            s.setZvel(s.getZvel() + 192);
                        }
                        s.setZ(s.getZ() + s.getZvel());
                        if (s.getZ() > sec.getFloorz()) {
                            s.setZ(sec.getFloorz());
                        }
                        t[3]--;
                        if (t[3] == 0) {
                            engine.deletesprite(i);
                            continue;
                        }
                    } else if (t[2] > 10) {
                        ListNode<Sprite> node = boardService.getStatNode(5);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (sp.getOwner() == i && sp.getPicnum() == FORCESPHERE) {
                                hittype[node.getIndex()].temp_data[1] = 1 + (engine.krand() & 63);
                            }
                            node = node.getNext();
                        }
                        t[3] = 64;
                    }

                    continue;

                case POWDERKEGSPRITE:
                    if (sec.getLotag() != 1 && sec.getLotag() != 160 && s.getXvel() != 0) {
                        movesprite(i, (s.getXvel() * (EngineUtils.cos((s.getAng()) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), CLIPMASK0);
                        s.setXvel(s.getXvel() - 1);
                    }

                    break;

                case RESPAWNMARKERRED:
                case 876:
                    hittype[i].temp_data[0]++;
                    if (hittype[i].temp_data[0] > currentGame.getCON().respawnitemtime) {
                        engine.deletesprite(i);
                        continue;
                    }
                    if (hittype[i].temp_data[0] >= (currentGame.getCON().respawnitemtime >> 1) && hittype[i].temp_data[0] < ((currentGame.getCON().respawnitemtime >> 1) + (currentGame.getCON().respawnitemtime >> 2))) {
                        s.setPicnum(876);
                    }
                    makeitfall(currentGame.getCON(), i);
                    break;

                case REACTORBURNT:
                case REACTOR2BURNT:
                    continue;

                case REACTOR:
                case REACTOR2: {
                    if (t[4] == 1) {
                        ListNode<Sprite> node = boardService.getSectNode(sect);
                        while (node != null) {
                            Sprite sp = node.get();
                            switch (sp.getPicnum()) {
                                case SECTOREFFECTOR:
                                    if (sp.getLotag() == 1) {
                                        sp.setLotag(-1);
                                        sp.setHitag(-1);
                                    }
                                    break;
                                case REACTOR:
                                    sp.setPicnum(REACTORBURNT);
                                    break;
                                case REACTOR2:
                                    sp.setPicnum(REACTOR2BURNT);
                                    break;
                                case REACTORSPARK:
                                case REACTOR2SPARK:
                                    sp.setCstat((short) 32768);
                                    break;
                            }
                            node = node.getNext();
                        }
                        continue;
                    }

                    if (t[1] >= 20) {
                        t[4] = 1;
                        continue;
                    }

                    int p = findplayer(s);
                    int x = player_dist;

                    t[2]++;
                    if (t[2] == 4) {
                        t[2] = 0;
                    }

                    if (x < 4096) {
                        if ((engine.krand() & 255) < 16) {
                            if (Sound[DUKE_LONGTERM_PAIN].getSoundOwnerCount() < 1) {
                                spritesound(DUKE_LONGTERM_PAIN, ps[p].i);
                            }

                            spritesound(SHORT_CIRCUIT, i);

                            ps[p].getPlayerSprite().setExtra(ps[p].getPlayerSprite().getExtra() - 1);
                            ps[p].pals_time = 32;
                            ps[p].pals[0] = 32;
                            ps[p].pals[1] = 0;
                            ps[p].pals[2] = 0;
                        }
                        t[0] += 128;
                        if (t[3] == 0) {
                            t[3] = 1;
                        }
                    } else {
                        t[3] = 0;
                    }

                    if (t[1] != 0) {
                        t[1]++;

                        t[4] = s.getZ();
                        s.setZ(sec.getFloorz());
                        int dz = sec.getFloorz() - sec.getCeilingz();
                        if (dz != 0) {
                            s.setZ(s.getZ() - (engine.krand() % dz));
                        }

                        switch (t[1]) {
                            case 3: {
                                // Turn on all of those flashing sectoreffector.
                                hitradius(i, 4096, currentGame.getCON().impact_damage << 2, currentGame.getCON().impact_damage << 2, currentGame.getCON().impact_damage << 2, currentGame.getCON().impact_damage << 2);

                                ListNode<Sprite> node = boardService.getStatNode(6);
                                while (node != null) {
                                    Sprite sp = node.get();
                                    if (sp.getPicnum() == MASTERSWITCH) {
                                        if (sp.getHitag() == s.getHitag()) {
                                            if (sp.getYvel() == 0) {
                                                sp.setYvel(1);
                                            }
                                        }
                                    }
                                    node = node.getNext();
                                }
                                break;
                            }
                            case 4:
                            case 7:
                            case 10:
                            case 15:
                                ListNode<Sprite> node = boardService.getSectNode(sect);
                                while (node != null) {
                                    ListNode<Sprite> next = node.getNext();
                                    int j = node.getIndex();
                                    if (j != i) {
                                        engine.deletesprite(j);
                                        break;
                                    }
                                    node = next;
                                }
                                break;
                        }
                        for (x = 0; x < 16; x++) {
                            RANDOMSCRAP(s, i);
                        }

                        s.setZ(t[4]);
                        t[4] = 0;

                    } else {
                        if (ifhitbyweapon(i) >= 0) {
                            for (x = 0; x < 32; x++) {
                                RANDOMSCRAP(s, i);
                            }
                            if (s.getExtra() < 0) {
                                t[1] = 1;
                            }
                        }
                    }
                    continue;
                }
                case BOWLINGBALL:
                    if (s.getXvel() == 0) {
                        spawn(i, BOWLINGBALLSPRITE);
                        engine.deletesprite(i);
                        continue;
                    }
                    if (Sound[356].getSoundOwnerCount() == 0) {
                        spritesound(356, i);
                    }
                    if (sec.getLotag() == 900) {
                        stopsound(356);
                    }
                    // fall
                case 3440:
                case 3441:
                case HENSTAND:
                case HENSTAND + 1:
                    if (s.getPicnum() == HENSTAND || s.getPicnum() == HENSTAND + 1) {
                        s.setLotag(s.getLotag() - 1);
                        if (s.getLotag() == 0) {
                            spawn(i, HEN);
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    if (sec.getLotag() == 900) {
                        s.setXvel(0);
                    }

                    if (s.getXvel() != 0) {
                        makeitfall(currentGame.getCON(), i);
                        int j = movesprite(i, (s.getXvel() * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), CLIPMASK0);

                        if ((j & kHitTypeMask) == kHitWall) {
                            j &= kHitIndexMask;

                            Wall hwall = boardService.getWall(j);
                            if (hwall != null) {
                                int k = hwall.getWallAngle();
                                s.setAng((short) (((k << 1) - s.getAng()) & 2047));
                            }
                        } else if ((j & kHitTypeMask) == kHitSprite) {
                            j &= kHitIndexMask;

                            checkhitsprite(j, i);
                            Sprite hsp = boardService.getSprite(j);
                            if (hsp != null && hsp.getPicnum() == HEN) {
                                int ss = spawn(j, HENSTAND);
                                Sprite sp = boardService.getSprite(ss);
                                if (sp != null) {
                                    engine.deletesprite(j);

                                    sp.setXvel(32);
                                    sp.setLotag(40);
                                    sp.setAng(s.getAng());
                                }
                            }
                        }

                        s.setXvel(s.getXvel() - 1);
                        if (s.getXvel() < 0) {
                            s.setXvel(0);
                        }
                        s.setCstat(257);

                        if (s.getPicnum() == 3440) {
                            s.setCstat(s.getCstat() | ((s.getXvel() & 4) | (s.getXvel() & 8)));
                            if ((engine.krand() & 1) != 0) {
                                s.setPicnum(3441);
                            }
                        } else if (s.getPicnum() == HENSTAND) {
                            s.setCstat(s.getCstat() | ((s.getXvel() & 4) | (s.getXvel() & 8)));
                            if ((engine.krand() & 1) != 0) {
                                s.setPicnum(HENSTAND + 1);
                            }
                            if (s.getXvel() == 0) {
                                engine.deletesprite(i);
                            }
                        }

                        if ((s.getPicnum() == 3440 || s.getPicnum() == 3441) && s.getXvel() == 0) {
                            engine.deletesprite(i);
                        }
                    } else if (sec.getLotag() == 900) {
                        if (s.getPicnum() == BOWLINGBALL) {
                            BowlClose(i);
                        }
                        engine.deletesprite(i);
                        continue;
                    }
                    break;
                case LOAFTILE:
                case NUGGETTILE:
                case BROASTEDTILE:
                case BONELESSTILE:
                case HEAD1TILE: {

                    if (!plantProcess) {
                        engine.deletesprite(i);
                        continue;
                    }

                    makeitfall(currentGame.getCON(), i);
                    int j = movesprite(i, (s.getXvel() * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), CLIPMASK0);
                    Sector sec2 = boardService.getSector(s.getSectnum());
                    if (s.getPicnum() == HEAD1TILE) {
                        if (sec2 != null && (sec2.getFloorz() - 2048) <= s.getZ()) {
                            if (sec2.getLotag() == 1) {
                                Sprite sp = boardService.getSprite(spawn(i, WATERSPLASH2));
                                if (sp != null) {
                                    sp.setZ(sec2.getFloorz());
                                }
                            }

                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    if ((j & kHitTypeMask) == kHitWall || (j & kHitTypeMask) == kHitSprite) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (sec2 != null) {
                        if (sec2.getLotag() == 903) {
                            if (sec2.getFloorz() - 1024 <= s.getZ()) {
                                engine.deletesprite(i);
                                continue;
                            }
                        }
                        if (sec2.getLotag() == 904) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    break;
                }
                case CHICKENATILE:
                case CHICKENBTILE:
                case CHICKENCTILE: {

                    if (!plantProcess) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (sec.getLotag() == 903) {
                        makeitfall(currentGame.getCON(), i);
                    }

                    int j = movesprite(i, (s.getXvel() * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14, (s.getXvel() * (EngineUtils.sin(s.getAng() & 2047))) >> 14, s.getZvel(), CLIPMASK0);
                    Sector sec2 = boardService.getSector(s.getSectnum());
                    if (sec2 != null) {
                        switch (sec2.getLotag()) {
                            case 901:
                                s.setPicnum(CHICKENBTILE);
                                break;
                            case 902:
                                s.setPicnum(CHICKENCTILE);
                                break;
                            case 903:
                                if ((sec2.getFloorz() - 2048) <= s.getZ()) {
                                    engine.deletesprite(i);
                                    continue;
                                }
                                break;
                            case 904:
                                engine.deletesprite(i);
                                continue;
                        }
                    }

                    if ((j & kHitTypeMask) == kHitWall || (j & kHitTypeMask) == kHitSprite) {
                        engine.deletesprite(i);
                        continue;
                    }
                    break;
                }
                case MOTORCYCLE: // RA
                    makeitfall(currentGame.getCON(), i);
                    getglobalz(i);
                    if (sec.getLotag() == 1) {
                        engine.setsprite(i, s.getX(), s.getY(), hittype[i].floorz + 4096);
                    }
                    break;
                case SWAMPBUGGY: // RA
                    makeitfall(currentGame.getCON(), i);
                    getglobalz(i);
                    break;
                case UFO1:
                case UFO2:
                case UFO3:
                case UFO4:
                case UFO5:
                case MINIONUFO: // RA
                {
                    getglobalz(i);

                    if ((sec.getCeilingstat() & 1) != 0) {
                        s.setShade(s.getShade() + ((sec.getCeilingshade() - s.getShade()) >> 1));
                    } else {
                        s.setShade(s.getShade() + ((sec.getFloorshade() - s.getShade()) >> 1));
                    }

                    if (s.getZ() < sec.getCeilingz() + (32 << 8)) {
                        s.setZ(sec.getCeilingz() + (32 << 8));
                    }

                    if (ud.multimode < 2) {
                        if (actor_tog != 0) {
                            s.setCstat((short) 32768);
                            continue;
                        }
                    }

                    if (ifhitbyweapon(i) >= 0) {
                        if (s.getExtra() < 0 && t[0] != -1) {
                            t[0] = -1;
                            s.setExtra(0);
                        }
                        RANDOMSCRAP(s, i);
                    }

                    if (t[0] == -1) {
                        s.setZ(s.getZ() + 1024);
                        t[2]++;
                        if ((t[2] & 3) == 0) {
                            spawn(i, EXPLOSION2);
                        }
                        getglobalz(i);
                        s.setAng(s.getAng() + 96);
                        s.setXvel(128);
                        int j = ssp(i, CLIPMASK0) ? 1 : 0;
                        if (j != 1 || s.getZ() > hittype[i].floorz) {
                            for (int l = 0; l < 16; l++) {
                                RANDOMSCRAP(s, i);
                            }
                            spritesound(LASERTRIP_EXPLODE, i);
                            if (ps[connecthead].isSwamp) {
                                spawn(i, MINION);
                            } else {
                                switch (s.getPicnum()) {
                                    case UFO1:
                                    case MINIONUFO: // RA
                                        spawn(i, HEN);
                                        break;
                                    case UFO2:
                                        spawn(i, COOT);
                                        break;
                                    case UFO3:
                                        spawn(i, COW);
                                        break;
                                    case UFO4:
                                        spawn(i, PIG);
                                        break;
                                    case UFO5:
                                        spawn(i, BILLYRAY);
                                        break;
                                }
                            }

                            ps[connecthead].actors_killed++;
                            engine.deletesprite(i);
                        }
                        continue;
                    } else {
                        if (s.getZ() > hittype[i].floorz - (48 << 8)) {
                            s.setZ(hittype[i].floorz - (48 << 8));
                        }
                    }

                    int p = findplayer(s);
                    int x = player_dist;
                    int j = s.getOwner();

                    // 3 = findplayerz, 4 = shoot

                    if (t[0] >= 4) {
                        t[2]++;
                        if ((t[2] & 15) == 0) {
                            int a = s.getAng();
                            s.setAng((short) hittype[i].tempang);
                            shoot(i, FIRELASER);
                            s.setAng((short) a);
                        }
                        if (t[2] > (26 * 3) || !engine.cansee(s.getX(), s.getY(), s.getZ() - (16 << 8), s.getSectnum(), ps[p].posx, ps[p].posy, ps[p].posz, ps[p].cursectnum)) {
                            t[0] = 0;
                            t[2] = 0;
                        } else {
                            hittype[i].tempang += getincangle(hittype[i].tempang, EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY())) / 3;
                        }
                    } else if (t[0] == 2 || t[0] == 3) {
                        t[3] = 0;
                        if (s.getXvel() > 0) {
                            s.setXvel(s.getXvel() - 16);
                        } else {
                            s.setXvel(0);
                        }

                        if (t[0] == 2) {
                            int l = ps[p].posz - s.getZ();
                            if (klabs(l) < (48 << 8)) {
                                t[0] = 3;
                            } else {
                                s.setZ(s.getZ() + (sgn(ps[p].posz - s.getZ()) << 10));
                            }
                        } else {
                            t[2]++;
                            if (t[2] > (26 * 3) || !engine.cansee(s.getX(), s.getY(), s.getZ() - (16 << 8), s.getSectnum(), ps[p].posx, ps[p].posy, ps[p].posz, ps[p].cursectnum)) {
                                t[0] = 1;
                                t[2] = 0;
                            } else if ((t[2] & 15) == 0) {
                                shoot(i, FIRELASER);
                            }
                        }
                        s.setAng(s.getAng() + (getincangle(s.getAng(), EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY())) >> 2));
                    }

                    if (t[0] != 2 && t[0] != 3) {
                        Sprite js = boardService.getSprite(j);
                        int l = js == null ? 0 : ldist(js, s), a;
                        if (l <= 1524) {
                            a = s.getAng();
                            s.setXvel(s.getXvel() >> 1);
                        } else {
                            a = EngineUtils.getAngle(js.getX() - s.getX(), js.getY() - s.getY());
                        }

                        if (t[0] == 1 || t[0] == 4) // Found a locator and going with it
                        {
                            l = js == null ? 0 : dist(js, s);
                            if (l <= 1524) {
                                if (t[0] == 1) {
                                    t[0] = 0;
                                } else {
                                    t[0] = 5;
                                }
                            } else {
                                // Control speed here
                                if (s.getXvel() < 256) {
                                    s.setXvel(s.getXvel() + 32);
                                }
                            }

                            if (t[0] < 2) {
                                t[2]++;
                            }

                            if (x < 6144 && t[0] < 2 && t[2] > (26 * 4)) {
                                t[0] = 2 + (engine.krand() & 2);
                                t[2] = 0;
                                hittype[i].tempang = s.getAng();
                            }
                        }

                        if (t[0] == 0 || t[0] == 5) {
                            if (t[0] == 0) {
                                t[0] = 1;
                            } else {
                                t[0] = 4;
                            }
                            s.setOwner(LocateTheLocator(s.getHitag(), -1));
                            j = s.getOwner();
                            if (j == -1) {
                                s.setHitag((j = hittype[i].temp_data[5]));
                                s.setOwner(LocateTheLocator(j, -1));
                                j = s.getOwner();
                                if (j == -1) {
                                    engine.deletesprite(i);
                                    continue;
                                }
                            } else {
                                s.setHitag(s.getHitag() + 1);
                            }
                        }

                        t[3] = getincangle(s.getAng(), a);
                        s.setAng(s.getAng() + (t[3] >> 3));

                        js = boardService.getSprite(j); // Attention!
                        if (js != null && s.getZ() < js.getZ()) {
                            s.setZ(s.getZ() + 1024);
                        } else {
                            s.setZ(s.getZ() - 1024);
                        }
                    }

                    if (Sound[457].getSoundOwnerCount() < 2) {
                        spritesound(457, i);
                    }

                    ssp(i, CLIPMASK0);

                    continue;
                }
            }

            if (ud.multimode < 2 && badguy(s)) {
                if (actor_tog == 1) {
                    s.setCstat((short) 32768);
                    continue;
                } else if (actor_tog == 2) {
                    s.setCstat(257);
                }
            }

            int p = findplayer(s);
            execute(currentGame.getCON(), i, p, player_dist);

            if (!IsOriginalDemo() && s.getPicnum() == COW && s.getExtra() <= 0 && (s.getCstat() & 256) != 0) {
                s.setCstat(s.getCstat() & ~256);
            }
        }

    }

    public static void moveexplosions() // STATNUM 5
    {
        ListNode<Sprite> ni = boardService.getStatNode(5), nexti;
        for (; ni != null; ni = nexti) {
            nexti = ni.getNext();
            final int i = ni.getIndex();

            int[] t = hittype[i].temp_data;
            final Sprite s = ni.get();
            int sect = s.getSectnum();

            if (sect < 0 || s.getXrepeat() == 0) {
                engine.deletesprite(i);
                continue;
            }

            game.pInt.setsprinterpolate(i, s);

            switch (s.getPicnum()) {
                case BLOODPOOL: {
                    Sector sec = boardService.getSector(sect);

                    if (t[0] == 0) {
                        t[0] = 1;
                        if (sec == null || (sec.getFloorstat() & 2) != 0) {
                            engine.deletesprite(i);
                            continue;
                        } else {
                            insertspriteq(i);
                        }
                    }

                    makeitfall(currentGame.getCON(), i);

                    int p = findplayer(s);
                    int x = player_dist;

                    s.setZ(hittype[i].floorz - (FOURSLEIGHT));

                    if (t[2] < 32) {
                        t[2]++;
                        if (hittype[i].picnum == TIRE) {
                            if (s.getXrepeat() < 64 && s.getYrepeat() < 64) {
                                s.setXrepeat(s.getXrepeat() + (engine.krand() & 3));
                                s.setYrepeat(s.getYrepeat() + (engine.krand() & 3));
                            }
                        } else {
                            if (s.getXrepeat() < 32 && s.getYrepeat() < 32) {
                                s.setXrepeat(s.getXrepeat() + (engine.krand() & 3));
                                s.setYrepeat(s.getYrepeat() + (engine.krand() & 3));
                            }
                        }
                    }

                    if (x < 844 && s.getXrepeat() > 6 && s.getYrepeat() > 6) {
                        if (s.getPal() == 0 && (engine.krand() & 255) < 16) {
                            if (ps[p].boot_amount > 0) {
                                ps[p].boot_amount--;
                            } else {
                                if (Sound[DUKE_LONGTERM_PAIN].getSoundOwnerCount() < 1) {
                                    spritesound(DUKE_LONGTERM_PAIN, ps[p].i);
                                }
                                ps[p].getPlayerSprite().setExtra(ps[p].getPlayerSprite().getExtra() - 1);
                                ps[p].pals_time = 32;
                                ps[p].pals[0] = 16;
                                ps[p].pals[1] = 0;
                                ps[p].pals[2] = 0;
                            }
                        }

                        if (t[1] == 1) {
                            continue;
                        }
                        t[1] = 1;

                        if (hittype[i].picnum == TIRE) {
                            ps[p].footprintcount = 10;
                        } else {
                            ps[p].footprintcount = 3;
                        }

                        ps[p].footprintpal = s.getPal();
                        ps[p].footprintshade = s.getShade();

                        if (t[2] == 32) {
                            s.setXrepeat(s.getXrepeat() - 6);
                            s.setYrepeat(s.getYrepeat() - 6);
                        }
                    } else {
                        t[1] = 0;
                    }

                    if (sec != null && sec.getLotag() == 800 && sec.getFloorz() - 2048 <= s.getZ()) {
                        engine.deletesprite(i);
                    }

                    continue;
                }
                case NEON1:
                case NEON2:
                case NEON3:
                case NEON4:
                case NEON5:
                case NEON6:
                    if ((global_random / (s.getLotag() + 1) & 31) > 4) {
                        s.setShade(-127);
                    } else {
                        s.setShade(127);
                    }
                    continue;

                case GLASSPIECES:
                case GLASSPIECES + 1:
                case GLASSPIECES + 2:
                case POPCORN: {

                    makeitfall(currentGame.getCON(), i);
                    Sector sec = boardService.getSector(sect);

                    if (s.getZvel() > 4096) {
                        s.setZvel(4096);
                    }

                    if (sec == null) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (s.getZ() == hittype[i].floorz - (FOURSLEIGHT) && t[0] < 3) {
                        s.setZvel((short) (-((3 - t[0]) << 8) - (engine.krand() & 511)));
                        if (sec.getLotag() == 2) {
                            s.setZvel(s.getZvel() >> 1);
                        }
                        s.setXrepeat(s.getXrepeat() >> 1);
                        s.setYrepeat(s.getYrepeat() >> 1);
                        if (rnd(96)) {
                            engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                        }
                        t[0]++;// Number of bounces
                    } else if (t[0] == 3) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (s.getXvel() > 0) {
                        s.setXvel(s.getXvel() - 2);
                        s.setCstat((short) ((s.getXvel() & 3) << 2));
                    } else {
                        s.setXvel(0);
                    }

                    ssp(i, CLIPMASK0);

                    continue;
                }

                case FEATHERS: {
                    s.setXvel((short) ((engine.krand() & 7) + (EngineUtils.sin(hittype[i].temp_data[0] & 2047) >> 9)));
                    hittype[i].temp_data[0] += (engine.krand() & 63);
                    if ((hittype[i].temp_data[0] & 2047) > 512 && (hittype[i].temp_data[0] & 2047) < 1596) {
                        Sector sec = boardService.getSector(sect);
                        if (sec != null && sec.getLotag() == 2) {
                            if (s.getZvel() < 64) {
                                s.setZvel(s.getZvel() + (currentGame.getCON().gc >> 5) + (engine.krand() & 7));
                            }
                        } else if (s.getZvel() < 144) {
                            s.setZvel(s.getZvel() + (currentGame.getCON().gc >> 5) + (engine.krand() & 7));
                        }
                    }

                    ssp(i, CLIPMASK0);

                    if ((engine.krand() & 3) == 0) {
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                    }

                    Sector sec = boardService.getSector(sect);
                    if (sec == null) {
                        engine.deletesprite(i);
                        continue;
                    }

                    int l = engine.getflorzofslope(s.getSectnum(), s.getX(), s.getY());
                    if (s.getZ() > l) {
                        s.setZ(l);

                        insertspriteq(i);
                        s.setPicnum(s.getPicnum() + 1);

                        ListNode<Sprite> node = boardService.getStatNode(5);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (sp.getPicnum() == BLOODPOOL) {
                                if (ldist(s, sp) < 348) {
                                    s.setPal(2);
                                    break;
                                }
                            }
                            node = node.getNext();
                        }
                    }

                    if (sec.getLotag() != 800) {
                        break;
                    }

                    if (sec.getFloorz() - 2048 > s.getZ()) {
                        break;
                    }

                    engine.deletesprite(i);
                    continue;
                }
                case FEATHERS + 1: {
                    s.setZ(engine.getflorzofslope(s.getSectnum(), s.getX(), s.getY()));
                    hittype[i].floorz = s.getZ();
                    Sector sec = boardService.getSector(sect);
                    if (sec != null && sec.getLotag() != 800) {
                        break;
                    }
                    engine.deletesprite(i);
                    continue;
                }
                case SHOTGUNSPRITE: {
                    Sector sec = boardService.getSector(sect);
                    if (sec != null && (sec.getLotag() != 800 || sec.getFloorz() - 2048 > s.getZ())) {
                        break;
                    }

                    engine.deletesprite(i);
                    continue;
                }
                case WATERSPLASH2:

                    t[0]++;
                    if (t[0] == 1) {
                        Sector sec = boardService.getSector(sect);
                        if (sec != null && sec.getLotag() != 1 && sec.getLotag() != 2) {
                            engine.deletesprite(i);
                            continue;
                        }

                        if (Sound[ITEM_SPLASH].getSoundOwnerCount() == 0) {
                            spritesound(ITEM_SPLASH, i);
                        }
                    }
                    if (t[0] == 3) {
                        t[0] = 0;
                        t[1]++;
                    }
                    if (t[1] == 5) {
                        engine.deletesprite(i);
                    }
                    continue;

                case BURNING:
                case EXPLOSION2:
                case EXPLOSION3:
                case WATERBUBBLE:
                case SMALLSMOKE:
                case BLOOD:
                case FORCERIPPLE:
                case TRANSPORTERSTAR:
                case TRANSPORTERBEAM: {
                    int p = findplayer(s);
                    int x = player_dist;
                    execute(currentGame.getCON(), i, p, x);
                    continue;
                }

                case JIBS1:
                case JIBS2:
                case JIBS3:
                case JIBS4:
                case JIBS5:
                case JIBS6:
                case LNRLEG:
                case LNRDTORSO:
                case LNRDGUN:
                case BILLYJIBA:
                case BILLYJIBB:
                case HULKJIBA:
                case HULKJIBB:
                case HULKJIBC:
                case MINJIBA:
                case MINJIBB:
                case MINJIBC:
                case COOTJIBA:
                case COOTJIBB:
                case COOTJIBC:

                case 2460: // RA green jibs1
                case 2465: // RA green jibs2
                case 5872: // RA motowheel
                case 5877: // RA mototank
                case 5882: // RA boarddebris
                case 6112: // RA bikenbody
                case 6117: // RA bikenhead
                case 6121: // RA bikerhead2
                case 6127: // RA bikerhand
                case 7000: // RA babahead
                case 7005: // RA bababody
                case 7010: // RA babafoot
                case 7015: // RA babahand
                case 7020: // RA debris
                case 7025: // RA debbris
                case 7387: // RA deepjibs
                case 7392: // RA deepjibs2
                case 7397: // RA deepjibs3
                case 8890: // RA deep2jibs
                case 8895: // RA deep2jibs2
                {
                    if (s.getXvel() > 0) {
                        s.setXvel(s.getXvel() - 1);
                    } else {
                        s.setXvel(0);
                    }

                    if (t[5] < 30 * 10) {
                        t[5]++;
                    } else {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (s.getZvel() > 1024 && s.getZvel() < 1280) {
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                        sect = s.getSectnum();
                    }

                    int l = engine.getflorzofslope(sect, s.getX(), s.getY());
                    int x = engine.getceilzofslope(sect, s.getX(), s.getY());
                    Sector sec = boardService.getSector(sect);
                    if (x == l || sec == null) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (s.getZ() < l - (2 << 8)) {
                        if (t[1] < 2) {
                            t[1]++;
                        } else if (sec.getLotag() != 2) {
                            t[1] = 0;
                            if (s.getPicnum() == LNRLEG || s.getPicnum() == LNRDTORSO || s.getPicnum() == LNRDGUN) {
                                if (t[0] > 6) {
                                    t[0] = 0;
                                } else {
                                    t[0]++;
                                }
                            } else {
                                if (t[0] > 2) {
                                    t[0] = 0;
                                } else {
                                    t[0]++;
                                }
                            }
                        }

                        if (s.getZvel() < 6144) {
                            if (sec.getLotag() == 2) {
                                if (s.getZvel() < 1024) {
                                    s.setZvel(s.getZvel() + 48);
                                } else {
                                    s.setZvel(1024);
                                }
                            } else {
                                s.setZvel(s.getZvel() + currentGame.getCON().gc - 50);
                            }
                        }

                        s.setX(s.getX() + ((s.getXvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 14));
                        s.setY(s.getY() + ((s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14));
                        s.setZ(s.getZ() + s.getZvel());

                        if (s.getZ() < sec.getFloorz()) {
                            if (sec.getLotag() == 800 && sec.getFloorz() - 2048 <= s.getZ()) {
                                engine.deletesprite(i);
                            }
                            continue;
                        }
                        engine.deletesprite(i);
                    } else {
                        if (s.getPicnum() == 2465 || s.getPicnum() == 2460) // RA
                        {
                            engine.deletesprite(i);
                            continue;
                        }

                        if (t[2] == 0) {
                            if ((sec.getFloorstat() & 2) != 0) {
                                engine.deletesprite(i);
                                continue;
                            }
                            t[2]++;
                        }

                        s.setZ(engine.getflorzofslope(s.getSectnum(), s.getX(), s.getY()) - (2 << 8));
                        s.setXvel(0);

                        if (s.getPicnum() == JIBS6) {
                            t[1]++;
                            if ((t[1] & 3) == 0 && t[0] < 7) {
                                t[0]++;
                            }
                            if (t[1] > 20) {
                                engine.deletesprite(i);
                                continue;
                            }
                            if (sec.getLotag() == 800 && sec.getFloorz() - 2048 <= s.getZ()) {
                                engine.deletesprite(i);
                            }

                            continue;
                        } else {
                            s.setPicnum(JIBS6);
                            t[0] = 0;
                            t[1] = 0;
                        }
                    }
                    continue;
                }
                case TONGUE:
                    engine.deletesprite(i);
                    continue;

                case INNERJAW:
                case INNERJAW + 1: {
                    int p = findplayer(s);
                    int x = player_dist;
                    if (x < 512) {
                        ps[p].pals_time = 32;
                        ps[p].pals[0] = 32;
                        ps[p].pals[1] = 0;
                        ps[p].pals[2] = 0;
                        ps[p].getPlayerSprite().setExtra(ps[p].getPlayerSprite().getExtra() - 4);
                    }
                }
                // break GDX 3.10.2018
                case FIRELASER:
                case UWHIP:
                case OWHIP:
                case DILDO:
                    if (s.getExtra() != 999) {
                        s.setExtra(999);
                    } else {
                        engine.deletesprite(i);
                        continue;
                    }
                    break;

                case MUD:
                    t[0]++;
                    if (t[0] == 1) {
                        Sector sec = boardService.getSector(s.getSectnum());
                        if (sec != null && sec.getFloorpicnum() == 3073) {
                            if (Sound[ITEM_SPLASH].getSoundOwnerCount() == 0) {
                                spritesound(ITEM_SPLASH, i);
                            }
                        } else {
                            engine.deletesprite(i);
                        }
                    }

                    if (t[0] == 3) {
                        t[0] = 0;
                        t[1]++;
                    }
                    if (t[1] == 5) {
                        engine.deletesprite(i);
                    }

                    continue;

                case SHELL:
                case SHOTGUNSHELL:

                    ssp(i, CLIPMASK0);
                    Sector sec = boardService.getSector(sect);
                    if (sec == null || (sec.getFloorz() + (24 << 8)) < s.getZ()) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (sec.getLotag() == 2) {
                        t[1]++;
                        if (t[1] > 8) {
                            t[1] = 0;
                            t[0]++;
                            t[0] &= 3;
                        }
                        if (s.getZvel() < 128) {
                            s.setZvel(s.getZvel() + (currentGame.getCON().gc / 13)); // 8
                        } else {
                            s.setZvel(s.getZvel() - 64);
                        }
                        if (s.getXvel() > 0) {
                            s.setXvel(s.getXvel() - 4);
                        } else {
                            s.setXvel(0);
                        }
                    } else {
                        t[1]++;
                        if (t[1] > 3) {
                            t[1] = 0;
                            t[0]++;
                            t[0] &= 3;
                        }
                        if (s.getZvel() < 512) {
                            s.setZvel(s.getZvel() + (currentGame.getCON().gc / 3)); // 52;
                        }
                        if (s.getXvel() > 0) {
                            s.setXvel(s.getXvel() - 1);
                        } else {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    continue;

                case BLOODSPLAT1:
                case BLOODSPLAT2:
                case BLOODSPLAT3:
                case BLOODSPLAT4:

                    if (t[0] == 7 * 26) {
                        continue;
                    }
                    s.setZ(s.getZ() + 16 + (engine.krand() & 15));
                    t[0]++;
                    if ((t[0] % 9) == 0) {
                        s.setYrepeat(s.getYrepeat() + 1);
                    }
                    continue;

                case FORCESPHERE: {
                    Sprite spo = boardService.getSprite(s.getOwner());

                    int l = s.getXrepeat();
                    if (t[1] > 0) {
                        t[1]--;
                        if (t[1] == 0) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }
                    if (hittype[s.getOwner()].temp_data[1] == 0) {
                        if (t[0] < 64) {
                            t[0]++;
                            l += 3;
                        }
                    } else if (t[0] > 64) {
                        t[0]--;
                        l -= 3;
                    }
                    if (spo != null) {
                        s.setX(spo.getX());
                        s.setY(spo.getY());
                        s.setZ(spo.getZ());
                        s.setAng(s.getAng() + hittype[s.getOwner()].temp_data[0]);
                    }

                    if (l > 64) {
                        l = 64;
                    } else if (l < 1) {
                        l = 1;
                    }

                    s.setXrepeat((short) l);
                    s.setYrepeat((short) l);
                    s.setShade((byte) ((l >> 1) - 48));

                    for (int j = (short) t[0]; j > 0; j--) {
                        ssp(i, CLIPMASK0);
                    }
                    continue;
                }
                case FRAMEEFFECT1:
                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo != null) {
                        t[0]++;

                        if (t[0] > 7) {
                            engine.deletesprite(i);
                            continue;
                        } else if (t[0] > 4) {
                            s.setCstat(s.getCstat() | 512 + 2);
                        } else if (t[0] > 2) {
                            s.setCstat(s.getCstat() | 2);
                        }
                        s.setXoffset(spo.getXoffset());
                        s.setYoffset(spo.getYoffset());
                    }
                    continue;
            }

            if (IFWITHIN(s, SCRAP6, SCRAP5 + 3)) {
                if (s.getXvel() > 0) {
                    s.setXvel(s.getXvel() - 1);
                } else {
                    s.setXvel(0);
                }

                if (s.getZvel() > 1024 && s.getZvel() < 1280) {
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                    sect = s.getSectnum();
                }

                Sector sec = boardService.getSector(sect);
                if (sec != null && s.getZ() < sec.getFloorz() - (2 << 8)) {
                    if (t[1] < 1) {
                        t[1]++;
                    } else {
                        t[1] = 0;

                        if (s.getPicnum() < SCRAP6 + 8) {
                            if (t[0] > 6) {
                                t[0] = 0;
                            } else {
                                t[0]++;
                            }
                        } else {
                            if (t[0] > 2) {
                                t[0] = 0;
                            } else {
                                t[0]++;
                            }
                        }
                    }
                    if (s.getZvel() < 4096) {
                        s.setZvel(s.getZvel() + currentGame.getCON().gc - 50);
                    }
                    s.setX(s.getX() + ((s.getXvel() * EngineUtils.sin((s.getAng() + 512) & 2047)) >> 14));
                    s.setY(s.getY() + ((s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14));
                    s.setZ(s.getZ() + s.getZvel());
                } else {
                    if (s.getPicnum() == SCRAP1 && s.getYvel() > 0) {
                        int j = spawn(i, s.getYvel());
                        engine.setsprite(j, s.getX(), s.getY(), s.getZ());
                        getglobalz(j);
                        Sprite js = boardService.getSprite(j);
                        if (js != null) {
                            js.setLotag(0);
                            js.setHitag(0);
                        }
                    }
                    engine.deletesprite(i);
                }
            }
        }
    }

    public static void moveeffectors() // STATNUM 3
    {
        fricxv = fricyv = 0;
        ListNode<Sprite> ni = boardService.getStatNode(3), nexti;

        BOLT:
        for (; ni != null; ni = nexti) {
            nexti = ni.getNext();
            Sprite s = ni.get();
            final int i = ni.getIndex();
            final Sector sc = boardService.getSector(s.getSectnum());

            int st = s.getLotag();
            int sh = s.getHitag();
            int[] t = hittype[i].temp_data;

            switch (st) {
                case 0: {
                    int zchange = 0;
                    final int j = s.getOwner();
                    final Sprite js = boardService.getSprite(j);

                    if (js == null || js.getLotag() == (short) 65535) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (sc == null) {
                        break;
                    }

                    int q = sc.getExtra() >> 3;
                    int l = 0;

                    if (sc.getLotag() == 30) {
                        q >>= 2;

                        if (s.getExtra() == 1) {
                            if (hittype[i].tempang < 256) {
                                hittype[i].tempang += 4;
                                if (hittype[i].tempang >= 256) {
                                    callsound(s.getSectnum(), i);
                                }
                                if (s.getClipdist() != 0) {
                                    l = 1;
                                } else {
                                    l = -1;
                                }
                            } else {
                                hittype[i].tempang = 256;
                            }

                            if (sc.getFloorz() > s.getZ()) // z's are touching
                            {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() - 512);
                                zchange = -512;
                                if (sc.getFloorz() < s.getZ()) {
                                    sc.setFloorz(s.getZ());
                                }
                            } else if (sc.getFloorz() < s.getZ()) // z's are touching
                            {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() + 512);
                                zchange = 512;
                                if (sc.getFloorz() > s.getZ()) {
                                    sc.setFloorz(s.getZ());
                                }
                            }
                        } else if (s.getExtra() == 3) {
                            if (hittype[i].tempang > 0) {
                                hittype[i].tempang -= 4;
                                if (hittype[i].tempang <= 0) {
                                    callsound(s.getSectnum(), i);
                                }
                                if (s.getClipdist() != 0) {
                                    l = -1;
                                } else {
                                    l = 1;
                                }
                            } else {
                                hittype[i].tempang = 0;
                            }

                            if (sc.getFloorz() > hittype[i].temp_data[3]) // z's are touching
                            {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() - 512);
                                zchange = -512;
                                if (sc.getFloorz() < hittype[i].temp_data[3]) {
                                    sc.setFloorz(hittype[i].temp_data[3]);
                                }
                            } else if (sc.getFloorz() < hittype[i].temp_data[3]) // z's are touching
                            {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() + 512);
                                zchange = 512;
                                if (sc.getFloorz() > hittype[i].temp_data[3]) {
                                    sc.setFloorz(hittype[i].temp_data[3]);
                                }
                            }
                        }

                    } else {
                        if (hittype[j].temp_data[0] == 0) {
                            break;
                        }
                        if (hittype[j].temp_data[0] == 2) {
                            engine.deletesprite(i);
                            continue;
                        }

                        if (js.getAng() > 1024) {
                            l = -1;
                        } else {
                            l = 1;
                        }
                        if (t[3] == 0) {
                            t[3] = ldist(s, js);
                        }
                        s.setXvel((short) t[3]);
                        s.setX(js.getX());
                        s.setY(js.getY());
                    }
                    s.setAng(s.getAng() + (l * q));
                    t[2] += (l * q);

                    if (l != 0 && (sc.getFloorstat() & 64) != 0) {
                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            if (ps[p].cursectnum == s.getSectnum() && ps[p].on_ground) {

                                ps[p].ang += (l * q);
                                ps[p].ang = BClampAngle(ps[p].ang);

                                ps[p].posz += zchange;

                                Point rp = EngineUtils.rotatepoint(js.getX(), js.getY(), ps[p].posx, ps[p].posy, (short) (q * l));

                                int x = rp.getX();
                                int y = rp.getY();

                                ps[p].bobposx += x - ps[p].posx;
                                ps[p].bobposy += y - ps[p].posy;

                                ps[p].posx = x;
                                ps[p].posy = y;

                                Sprite psp = boardService.getSprite(ps[p].i);
                                if (psp != null && psp.getExtra() <= 0) {
                                    psp.setX(x);
                                    psp.setY(y);
                                }
                            }
                        }

                        ListNode<Sprite> node = boardService.getSectNode(s.getSectnum());
                        while (node != null) {
                            int p = node.getIndex();
                            Sprite sp = node.get();
                            if (sp.getStatnum() != 3 && sp.getStatnum() != 4) {
                                if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                    node = node.getNext();
                                    continue;
                                }

                                game.pInt.setsprinterpolate(p, sp);

                                sp.setAng(sp.getAng() + (l * q));
                                sp.setAng(sp.getAng() & 2047);
                                sp.setZ(sp.getZ() + zchange);

                                Point rp = EngineUtils.rotatepoint(js.getX(), js.getY(), sp.getX(), sp.getY(), (short) (q * l));
                                sp.setX(rp.getX());
                                sp.setY(rp.getY());
                            }
                            node = node.getNext();
                        }
                    }

                    ms(i);
                }
                break;
                case 1: // Nothing for now used as the pivot
                    if (s.getOwner() == -1) // Init
                    {
                        s.setOwner(i);

                        ListNode<Sprite> node = boardService.getStatNode(3);
                        while (node != null) {
                            Sprite sp = node.get();
                            if (sp.getLotag() == 19 && sp.getHitag() == sh) {
                                t[0] = 0;
                                break;
                            }
                            node = node.getNext();
                        }
                    }

                    break;
                case 6: {
                    if (sc == null) {
                        break;
                    }

                    int k = sc.getExtra();
                    if (t[4] > 0) {
                        t[4]--;
                        if (t[4] >= (k - (k >> 3))) {
                            s.setXvel(s.getXvel() - (k >> 5));
                        }
                        if (t[4] > ((k >> 1) - 1) && t[4] < (k - (k >> 3))) {
                            s.setXvel(0);
                        }
                        if (t[4] < (k >> 1)) {
                            s.setXvel(s.getXvel() + (k >> 5));
                        }
                        if (t[4] < ((k >> 1) - (k >> 3))) {
                            t[4] = 0;
                            s.setXvel(k);

                            if (UFO_SpawnHulk > 0) {
                                UFO_SpawnHulk--;
                                Sprite sp = boardService.getSprite(spawn(i, HULK));
                                if (sp != null) {
                                    sp.setZ(sc.getCeilingz());
                                    sp.setPal(33);
                                }

                                if (UFO_SpawnHulk == 0) {
                                    sp = boardService.getSprite(EGS(s.getSectnum(), s.getX(), s.getY(), sc.getCeilingz() + 119428, 3677, -8, 16, 16, 0, 0, 0, i, (short) 5));
                                    if (sp != null) {
                                        sp.setCstat(514);
                                        sp.setYrepeat(255);
                                        sp.setPal(7);
                                        sp.setXrepeat(80);
                                    }

                                    sp = boardService.getSprite(spawn(i, 296));
                                    if (sp != null) {
                                        sp.setCstat((short) 32768);
                                        sp.setZ(sc.getFloorz() - 6144);
                                    }
                                    engine.deletesprite(i);
                                    continue;
                                }
                            }
                        }
                    } else {
                        s.setXvel(k);
                    }

                    int count = 0;
                    ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                    while (nj != null) {
                        Sprite sp = nj.get();
                        if (sp.getPicnum() == UFOBEAM && count != UFO_SpawnCount && ++UFO_SpawnTime == 64) {
                            UFO_SpawnTime = count;
                            --UFO_SpawnCount;
                            int spawntile = 0;
                            switch (engine.krand() & 3) {
                                case 0:
                                    spawntile = UFO1;
                                    break;
                                case 1:
                                    spawntile = UFO2;
                                    break;
                                case 2:
                                    spawntile = UFO3;
                                    break;
                                case 3:
                                    spawntile = UFO4;
                                    break;
                            }
                            if (currentGame.getCON().type == RRRA) {
                                spawntile = MINIONUFO;
                            }
                            int nSpawn = spawn(i, spawntile);
                            Sprite sp2 = boardService.getSprite(nSpawn);
                            if (sp2 != null) {
                                Sector sec2 = boardService.getSector(sp2.getSectnum());
                                if (sec2 != null) {
                                    sp2.setZ(sec2.getCeilingz());
                                }
                            }
                        }
                        nj = nj.getNext();
                    }

                    ListNode<Sprite> node = boardService.getStatNode(3);
                    while (node != null) {
                        Sprite sp = node.get();
                        int j = node.getIndex();
                        if ((sp.getLotag() == 14) && (sh == sp.getHitag()) && (hittype[j].temp_data[0] == t[0])) {
                            sp.setXvel(s.getXvel());

                            if (hittype[j].temp_data[5] == 0) {
                                hittype[j].temp_data[5] = dist(sp, s);
                            }
                            int x = sgn(dist(sp, s) - hittype[j].temp_data[5]);
                            if (sp.getExtra() != 0) {
                                x = -x;
                            }
                            s.setXvel(s.getXvel() + x);

                            hittype[j].temp_data[4] = t[4];
                        }
                        node = node.getNext();
                    }
                }
                case 14: {
                    if (s.getOwner() == -1) {
                        s.setOwner((short) LocateTheLocator((short) t[3], (short) t[0]));
                    }

                    if (s.getOwner() == -1) {
                        throw new AssertException("Could not find any locators for SE# 6 and 14 with a hitag of " + t[3]);
                    }

                    Sprite so = boardService.getSprite(s.getOwner());
                    if (so != null && ldist(so, s) < 1024) {
                        if (st == 6) {
                            if ((so.getHitag() & 1) != 0) {
                                t[4] = sc.getExtra(); // Slow it down
                            }
                        }
                        t[3]++;
                        s.setOwner((short) LocateTheLocator(t[3], t[0]));
                        if (s.getOwner() == -1) {
                            t[3] = 0;
                            s.setOwner((short) LocateTheLocator(0, t[0]));
                        }
                    }

                    if (sc != null && s.getXvel() != 0) {
                        so = boardService.getSprite(s.getOwner()); // Attension!
                        int x = so != null ? EngineUtils.getAngle(so.getX() - s.getX(), so.getY() - s.getY()) : s.getAng();
                        int q = getincangle(s.getAng(), x) >> 3;

                        t[2] += q;
                        s.setAng(s.getAng() + q);

                        if (s.getXvel() == sc.getExtra()) {
                            if (Sound[hittype[i].lastvx].getSoundOwnerCount() == 0) {
                                spritesound(hittype[i].lastvx, i);
                            }
                            if (!ud.monsters_off && sc.getFloorpal() == 0 && (sc.getFloorstat() & 1) != 0 && rnd(8)) {
                                int p = (short) findplayer(s);
                                x = player_dist;
                                if (x < 20480) {
                                    int j = s.getAng();
                                    s.setAng(EngineUtils.getAngle(s.getX() - ps[p].posx, s.getY() - ps[p].posy));
                                    shoot(i, CROSSBOW);
                                    s.setAng((short) j);
                                }
                            }
                        }

                        if (s.getXvel() <= 64 && (sc.getFloorstat() & 1) == 0 && (sc.getCeilingstat() & 1) == 0) {
                            stopsound(hittype[i].lastvx, i);
                        }

                        if ((sc.getFloorz() - sc.getCeilingz()) < (108 << 8)) {
                            if (!ud.clipping && s.getXvel() >= 192) {
                                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                    Sprite psp = boardService.getSprite(ps[p].i);
                                    if (psp != null && psp.getExtra() > 0) {
                                        int k = ps[p].cursectnum;
                                        k = engine.updatesector(ps[p].posx, ps[p].posy, k);
                                        if ((k == -1 && !ud.clipping) || (k == s.getSectnum() && ps[p].cursectnum != s.getSectnum())) {
                                            ps[p].posx = s.getX();
                                            ps[p].posy = s.getY();
                                            ps[p].cursectnum = s.getSectnum();

                                            engine.setsprite(ps[p].i, s.getX(), s.getY(), s.getZ());
                                            quickkill(ps[p]);
                                        }
                                    }
                                }
                            }
                        }

                        int m = (s.getXvel() * EngineUtils.sin((s.getAng() + 512) & 2047)) >> 14;
                        x = (s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14;

                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            Sector psec = boardService.getSector(ps[p].cursectnum);

                            if (psec != null && psec.getLotag() != 2) { // v0.751
                                if (po[p].os == s.getSectnum()) {
                                    po[p].ox += m;
                                    po[p].oy += x;
                                }

                                Sprite psp = boardService.getSprite(ps[p].i);
                                if (psp != null && s.getSectnum() == psp.getSectnum()) {
                                    Point out = EngineUtils.rotatepoint(s.getX(), s.getY(), ps[p].posx, ps[p].posy, (short) q);
                                    ps[p].posx = out.getX();
                                    ps[p].posy = out.getY();

                                    ps[p].posx += m;
                                    ps[p].posy += x;

                                    ps[p].bobposx += m;
                                    ps[p].bobposy += x;

                                    ps[p].ang += q;
                                    ps[p].ang = BClampAngle(ps[p].ang);

                                    if (numplayers > 1) {
                                        ps[p].oposx = ps[p].posx;
                                        ps[p].oposy = ps[p].posy;
                                    }
                                    if (psp.getExtra() <= 0) {
                                        ps[p].getPlayerSprite().setX(ps[p].posx);
                                        ps[p].getPlayerSprite().setY(ps[p].posy);
                                    }
                                }
                            }
                        }

                        ListNode<Sprite> node = boardService.getSectNode(s.getSectnum());
                        while (node != null) {
                            Sprite sp = node.get();
                            Sector sec = boardService.getSector(sp.getSectnum());

                            if (sec != null && sp.getStatnum() != 10 && sec.getLotag() != 2 && sp.getPicnum() != SECTOREFFECTOR && sp.getPicnum() != LOCATORS) {
                                Point out = EngineUtils.rotatepoint(s.getX(), s.getY(), sp.getX(), sp.getY(), q);

                                game.pInt.setsprinterpolate(node.getIndex(), sp);

                                sp.setX(out.getX());
                                sp.setY(out.getY());

                                sp.setX(sp.getX() + m);
                                sp.setY(sp.getY() + x);

                                sp.setAng(sp.getAng() + q);
                            }
                            node = node.getNext();
                        }

                        ms(i);
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());

                        if ((sc.getFloorz() - sc.getCeilingz()) < (108 << 8)) {
                            if (!ud.clipping && s.getXvel() >= 192) {
                                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                    Sprite psp = boardService.getSprite(ps[p].i);
                                    if (psp != null && psp.getExtra() > 0) {
                                        int k = ps[p].cursectnum;
                                        k = engine.updatesector(ps[p].posx, ps[p].posy, k);
                                        if ((k == -1 && !ud.clipping) || (k == s.getSectnum() && ps[p].cursectnum != s.getSectnum())) {
                                            ps[p].oposx = ps[p].posx = s.getX();
                                            ps[p].oposy = ps[p].posy = s.getY();
                                            ps[p].cursectnum = s.getSectnum();

                                            engine.setsprite(ps[p].i, s.getX(), s.getY(), s.getZ());
                                            quickkill(ps[p]);
                                        }
                                    }
                                }
                            }
                            Sprite spo = boardService.getSprite(s.getOwner());
                            if (spo != null) {
                                node = boardService.getSectNode(spo.getSectnum());
                                while (node != null) {
                                    ListNode<Sprite> next = node.getNext();
                                    Sprite sp = node.get();
                                    if (sp.getStatnum() == 1 && badguy(sp) && sp.getPicnum() != SECTOREFFECTOR && sp.getPicnum() != LOCATORS) {
                                        int k = sp.getSectnum();
                                        k = engine.updatesector(sp.getX(), sp.getY(), k);
                                        if (sp.getExtra() >= 0 && k == s.getSectnum()) {
                                            gutsdir(sp, JIBS6, 72, myconnectindex);
                                            spritesound(SQUISHED, i);
                                            engine.deletesprite(node.getIndex());
                                        }
                                    }
                                    node = next;
                                }
                            }
                        }
                    }

                    break;
                }
                case 30: {
                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo == null) {
                        t[3] ^= 1;
                        s.setOwner((short) LocateTheLocator(t[3], t[0]));
                    } else {
                        if (t[4] == 1) // Starting to go
                        {
                            if (ldist(spo, s) < (2048 - 128)) {
                                t[4] = 2;
                            } else {
                                if (s.getXvel() == 0) {
                                    operateactivators(s.getHitag() + (t[3] == 0 ? 1 : 0), -1);
                                }
                                if (s.getXvel() < 256) {
                                    s.setXvel(s.getXvel() + 16);
                                }
                            }
                        }
                        if (t[4] == 2) {
                            if (FindDistance2D(spo.getX() - s.getX(), spo.getY() - s.getY()) <= 128) {
                                s.setXvel(0);
                            }

                            if (s.getXvel() > 0) {
                                s.setXvel(s.getXvel() - 16);
                            } else {
                                s.setXvel(0);
                                operateactivators(s.getHitag() + (short) t[3], -1);
                                s.setOwner(-1);
                                s.setAng(s.getAng() + 1024);
                                t[4] = 0;
                                operateforcefields(i, s.getHitag());

                                ListNode<Sprite> node = boardService.getSectNode(s.getSectnum());
                                while (node != null) {
                                    Sprite sp = node.get();
                                    if (sp.getPicnum() != SECTOREFFECTOR && sp.getPicnum() != LOCATORS) {
                                        game.pInt.setsprinterpolate(node.getIndex(), sp);
                                    }
                                    node = node.getNext();
                                }
                            }
                        }
                    }

                    if (s.getXvel() != 0) {
                        int l = (s.getXvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 14;
                        int x = (s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14;

                        if (sc != null && (sc.getFloorz() - sc.getCeilingz()) < (108 << 8)) {
                            if (!ud.clipping) {
                                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                    Sprite psp = boardService.getSprite(ps[p].i);

                                    if (psp != null && psp.getExtra() > 0) {
                                        int k = ps[p].cursectnum;
                                        k = engine.updatesector(ps[p].posx, ps[p].posy, k);
                                        if ((k == -1 && !ud.clipping) || (k == s.getSectnum() && ps[p].cursectnum != s.getSectnum())) {
                                            ps[p].posx = s.getX();
                                            ps[p].posy = s.getY();
                                            ps[p].cursectnum = s.getSectnum();

                                            engine.setsprite(ps[p].i, s.getX(), s.getY(), s.getZ());
                                            quickkill(ps[p]);
                                        }
                                    }
                                }
                            }
                        }

                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            Sprite psp = boardService.getSprite(ps[p].i);
                            if (psp != null && psp.getSectnum() == s.getSectnum()) {
                                ps[p].posx += l;
                                ps[p].posy += x;

                                if (numplayers > 1) {
                                    ps[p].oposx = ps[p].posx;
                                    ps[p].oposy = ps[p].posy;
                                }

                                ps[p].bobposx += l;
                                ps[p].bobposy += x;
                            }

                            if (po[p].os == s.getSectnum()) {
                                po[p].ox += l;
                                po[p].oy += x;
                            }
                        }

                        ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                        while (nj != null) {
                            Sprite sp = nj.get();
                            if (sp.getPicnum() != SECTOREFFECTOR && sp.getPicnum() != LOCATORS) {

                                game.pInt.setsprinterpolate(nj.getIndex(), sp);

                                sp.setX(sp.getX() + l);
                                sp.setY(sp.getY() + x);
                            }
                            nj = nj.getNext();
                        }

                        ms(i);
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());

                        if (sc != null && (sc.getFloorz() - sc.getCeilingz()) < (108 << 8)) {
                            if (!ud.clipping) {
                                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                    Sprite psp = boardService.getSprite(ps[p].i);

                                    if (psp != null && psp.getExtra() > 0) {
                                        int k = ps[p].cursectnum;
                                        k = engine.updatesector(ps[p].posx, ps[p].posy, k);
                                        if ((k == -1 && !ud.clipping) || (k == s.getSectnum() && ps[p].cursectnum != s.getSectnum())) {
                                            ps[p].posx = s.getX();
                                            ps[p].posy = s.getY();

                                            ps[p].oposx = ps[p].posx;
                                            ps[p].oposy = ps[p].posy;

                                            ps[p].cursectnum = s.getSectnum();

                                            engine.setsprite(ps[p].i, s.getX(), s.getY(), s.getZ());
                                            quickkill(ps[p]);
                                        }
                                    }
                                }
                            }
                            spo = boardService.getSprite(s.getOwner());
                            if (spo != null) {
                                nj = boardService.getSectNode(spo.getSectnum());
                                while (nj != null) {
                                    ListNode<Sprite> nl = nj.getNext();
                                    Sprite sp = nj.get();
                                    if (sp.getStatnum() == 1 && badguy(sp) && sp.getPicnum() != SECTOREFFECTOR && sp.getPicnum() != LOCATORS) {
                                        int k = sp.getSectnum();
                                        k = engine.updatesector(sp.getX(), sp.getY(), k);
                                        if (sp.getExtra() >= 0 && k == s.getSectnum()) {
                                            gutsdir(sp, JIBS6, 24, myconnectindex);
                                            spritesound(SQUISHED, nj.getIndex());
                                            engine.deletesprite(nj.getIndex());
                                        }
                                    }
                                    nj = nl;
                                }
                            }
                        }
                    }

                    break;
                }
                case 2: { // Quakes
                    if (t[4] > 0 && t[0] == 0) {
                        if (t[4] < sh) {
                            t[4]++;
                        } else {
                            t[0] = 1;
                        }
                    }

                    if (t[0] > 0) {
                        t[0]++;

                        s.setXvel(3);

                        if (t[0] > 96) {
                            t[0] = -1; // Stop the quake
                            t[4] = -1;
                            engine.deletesprite(i);
                            continue;
                        } else {
                            if ((t[0] & 31) == 8) {
                                earthquaketime = 48;
                                spritesound(EARTHQUAKE, ps[screenpeek].i);
                            }
                            if (sc != null) {
                                if (klabs(sc.getFloorheinum() - t[5]) < 8) {
                                    sc.setFloorheinum((short) t[5]);
                                } else {
                                    sc.setFloorheinum(sc.getFloorheinum() + (sgn(t[5] - sc.getFloorheinum()) << 4));
                                }
                            }
                        }

                        int m = (s.getXvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 14;
                        int x = (s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14;

                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            if (ps[p].cursectnum == s.getSectnum() && ps[p].on_ground) {
                                ps[p].posx += m;
                                ps[p].posy += x;

                                ps[p].bobposx += m;
                                ps[p].bobposy += x;
                            }
                        }

                        ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                        while (nj != null) {
                            ListNode<Sprite> nextj = nj.getNext();
                            Sprite sp = nj.get();
                            int jj = nj.getIndex();

                            if (sp.getPicnum() != SECTOREFFECTOR) {
                                game.pInt.setsprinterpolate(jj, sp);

                                sp.setX(sp.getX() + m);
                                sp.setY(sp.getY() + x);
                                engine.setsprite(jj, sp.getX(), sp.getY(), sp.getZ());
                            }
                            nj = nextj;
                        }
                        ms(i);
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                    }
                    break;
                }
                // Flashing sector lights after reactor EXPLOSION2

                case 3: {
                    if (sc == null || t[4] == 0) {
                        break;
                    }
                    findplayer(s);

                    if ((global_random / (sh + 1) & 31) < 4 && t[2] == 0) {
                        sc.setCeilingpal((short) (s.getOwner() >> 8));
                        sc.setFloorpal((short) (s.getOwner() & 0xff));
                        t[0] = s.getShade() + (global_random & 15);
                    } else {
                        sc.setCeilingpal(s.getPal());
                        sc.setFloorpal(s.getPal());
                        t[0] = t[3];
                    }

                    sc.setCeilingshade((byte) t[0]);
                    sc.setFloorshade((byte) t[0]);

                    for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                        Wall wal = wn.get();
                        if (wal.getHitag() != 1) {
                            wal.setShade(t[0]);
                            Wall nw = boardService.getWall(wal.getNextwall());
                            if ((wal.getCstat() & 2) != 0 && nw != null) {
                                nw.setShade(wal.getShade());
                            }
                        }
                    }

                    break;
                }
                case 4: {
                    if (sc == null) {
                        break;
                    }

                    int j = 0;
                    if ((global_random / (sh + 1) & 31) < 4) {
                        t[1] = s.getShade() + (global_random & 15);// Got really bright
                        t[0] = s.getShade() + (global_random & 15);
                        sc.setCeilingpal((short) (s.getOwner() >> 8));
                        sc.setFloorpal((short) (s.getOwner() & 0xff));
                        j = 1;
                    } else {
                        t[1] = t[2];
                        t[0] = t[3];

                        sc.setCeilingpal(s.getPal());
                        sc.setFloorpal(s.getPal());
                    }

                    sc.setFloorshade((byte) t[1]);
                    sc.setCeilingshade((byte) t[1]);

                    for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                        Wall wal = wn.get();
                        if (j != 0) {
                            wal.setPal((short) (s.getOwner() & 0xff));
                        } else {
                            wal.setPal(s.getPal());
                        }

                        if (wal.getHitag() != 1) {
                            wal.setShade((byte) t[0]);
                            Wall nw = boardService.getWall(wal.getNextwall());
                            if ((wal.getCstat() & 2) != 0 && nw != null) {
                                nw.setShade(wal.getShade());
                            }
                        }
                    }

                    ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                    while (nj != null) {
                        Sprite sp = nj.get();
                        if ((sp.getCstat() & 16) != 0) {
                            if ((sc.getCeilingstat() & 1) != 0) {
                                sp.setShade(sc.getCeilingshade());
                            } else {
                                sp.setShade(sc.getFloorshade());
                            }
                        }

                        nj = nj.getNext();
                    }

                    if (t[4] != 0) {
                        engine.deletesprite(i);
                        continue;
                    }

                    break;
                }
                // BOSS
                case 5: {
                    int p = (short) findplayer(s);
                    int x = player_dist;
                    if (x < 8192) {
                        int j = s.getAng();
                        s.setAng(EngineUtils.getAngle(s.getX() - ps[p].posx, s.getY() - ps[p].posy));
                        shoot(i, FIRELASER);
                        s.setAng((short) j);
                    }

                    if (s.getOwner() == -1) // Start search
                    {
                        int q = 0;
                        t[4] = 0;
                        int l = 0x7fffffff;
                        while (true) // Find the shortest dist
                        {
                            s.setOwner((short) LocateTheLocator((short) t[4], -1)); // t[0]
                            // hold
                            // sectnum

                            if (s.getOwner() == -1) {
                                break;
                            }

                            Sprite psp = boardService.getSprite(ps[p].i);
                            Sprite spo = boardService.getSprite(s.getOwner());
                            if (psp != null && spo != null) {
                                int m = ldist(psp, spo);
                                if (l > m) {
                                    q = s.getOwner();
                                    l = m;
                                }
                            }

                            t[4]++;
                        }

                        Sprite qs = boardService.getSprite(q);
                        if (qs != null) {
                            s.setOwner(q);
                            s.setZvel((ksgn(qs.getZ() - s.getZ()) << 4));
                        }
                    }

                    if (sc == null) {
                        break;
                    }

                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo != null) {
                        if (ldist(spo, s) < 1024) {
                            short ta = s.getAng();
                            s.setAng(EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY()));
                            s.setAng(ta);
                            s.setOwner(-1);
                            continue;

                        } else {
                            s.setXvel(256);
                        }

                        x = EngineUtils.getAngle(spo.getX() - s.getX(), spo.getY() - s.getY());
                        int q = getincangle(s.getAng(), x) >> 3;
                        s.setAng(s.getAng() + q);

                        if (rnd(32)) {
                            t[2] += q;
                            sc.setCeilingshade(127);
                        } else {
                            t[2] += getincangle(t[2] + 512, EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY())) >> 2;
                            sc.setCeilingshade(0);
                        }
                    }

                    if (ifhitbyweapon(i) >= 0) {
                        t[3]++;
                        if (t[3] == 5) {
                            s.setZvel(s.getZvel() + 1024);
                            FTA(7, ps[myconnectindex]);
                        }
                    }

                    s.setZ(s.getZ() + s.getZvel());
                    game.pInt.setceilinterpolate(s.getSectnum(), sc);
                    sc.setCeilingz(sc.getCeilingz() + s.getZvel());
                    Sector tsec = boardService.getSector(t[0]);
                    if (tsec != null) {
                        game.pInt.setceilinterpolate(t[0], tsec);
                        tsec.setCeilingz(tsec.getCeilingz() + s.getZvel());
                    }
                    ms(i);
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                    break;
                }
                case 8:
                case 9: {

                    // work only if its moving

                    int j;

                    if (hittype[i].temp_data[4] != 0) {
                        hittype[i].temp_data[4]++;
                        if (hittype[i].temp_data[4] > 8) {
                            engine.deletesprite(i);
                            continue;
                        }
                        j = 1;
                    } else {
                        j = getanimationgoal(sc, CEILZ);
                    }

                    if (j >= 0) {
                        int x;
                        if ((sc != null && (sc.getLotag() & 0x8000) != 0) || hittype[i].temp_data[4] != 0) {
                            x = -t[3];
                        } else {
                            x = t[3];
                        }

                        if (st == 9) {
                            x = -x;
                        }

                        ListNode<Sprite> node = boardService.getStatNode(3);
                        while (node != null) {
                            Sprite sp = node.get();
                            j = node.getIndex();
                            if (((sp.getLotag()) == st) && (sp.getHitag()) == sh) {
                                final int sn = sp.getSectnum();
                                final Sector sec = boardService.getSector(sn);
                                int m = sp.getShade();
                                if (sec != null) {

                                    for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                                        Wall wal = wn.get();

                                        if (wal.getHitag() != 1) {
                                            wal.setShade(wal.getShade() + x);

                                            if (wal.getShade() < m) {
                                                wal.setShade((byte) m);
                                            } else if (wal.getShade() > hittype[j].temp_data[2]) {
                                                wal.setShade((byte) hittype[j].temp_data[2]);
                                            }

                                            Wall nwal = boardService.getWall(wal.getNextwall());
                                            if (nwal != null) {
                                                if (nwal.getHitag() != 1) {
                                                    nwal.setShade(wal.getShade());
                                                }
                                            }
                                        }
                                    }

                                    sec.setFloorshade(sec.getFloorshade() + x);
                                    sec.setCeilingshade(sec.getCeilingshade() + x);

                                    if (sec.getFloorshade() < m) {
                                        sec.setFloorshade((byte) m);
                                    } else if (sec.getFloorshade() > hittype[j].temp_data[0]) {
                                        sec.setFloorshade((byte) hittype[j].temp_data[0]);
                                    }

                                    if (sec.getCeilingshade() < m) {
                                        sec.setCeilingshade((byte) m);
                                    } else if (sec.getCeilingshade() > hittype[j].temp_data[1]) {
                                        sec.setCeilingshade((byte) hittype[j].temp_data[1]);
                                    }

                                    if (sec.getHitag() == 1) {
                                        sec.setCeilingshade((byte) hittype[j].temp_data[1]);
                                    }
                                }
                            }
                            node = node.getNext();
                        }
                    }
                    break;
                }
                case 10: {
                    if (sc == null) {
                        break;
                    }

                    if ((sc.getLotag() & 0xff) == 27 || (sc.getFloorz() > sc.getCeilingz() && (sc.getLotag() & 0xff) != 23) || sc.getLotag() == (short) 32791) {
                        int j = 1;

                        if ((sc.getLotag() & 0xff) != 27) {
                            for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                                if (sc.getLotag() != 30 && sc.getLotag() != 31 && sc.getLotag() != 0) {
                                    Sprite psp = boardService.getSprite(ps[p].i);
                                    if (psp != null && s.getSectnum() == psp.getSectnum()) {
                                        j = 0;
                                    }
                                }
                            }
                        }

                        if (j == 1) {
                            if (t[0] > sh) {
                                switch (sc.getLotag()) {
                                    case 20:
                                    case 21:
                                    case 22:
                                    case 26:
                                        if (currentGame.getCON().type != RRRA) { // GDX 3.10.2018
                                            if (getanimationgoal(sc, CEILZ) >= 0) {
                                                break;
                                            }
                                        }
                                    default:
                                        activatebysector(s.getSectnum(), i);
                                        t[0] = 0;
                                        break;
                                }
                            } else {
                                t[0]++;
                            }
                        }
                    } else {
                        t[0] = 0;
                    }
                    break;
                }
                case 11: { // Swingdoor

                    if (t[5] > 0) {
                        t[5]--;
                        break;
                    }

                    if (sc != null && t[4] != 0) {
                        for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                            int j = wn.getIndex();
                            ListNode<Sprite> node = boardService.getStatNode(1);
                            while (node != null) {
                                Sprite sp = node.get();
                                if (sp.getExtra() > 0 && badguy(sp) && engine.clipinsidebox(sp.getX(), sp.getY(), (short) j, 256) == 1) {
                                    continue BOLT;
                                }
                                node = node.getNext();
                            }

                            node = boardService.getStatNode(10);
                            while (node != null) {
                                Sprite sp = node.get();
                                if (sp.getOwner() >= 0 && engine.clipinsidebox(sp.getX(), sp.getY(), (short) j, 144) == 1) {
                                    t[5] = 8; // Delay
                                    int k = (short) ((s.getYvel() >> 3) * t[3]);
                                    t[2] -= k;
                                    t[4] -= k;
                                    ms(i);
                                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                                    continue BOLT;
                                }
                                node = node.getNext();
                            }
                        }

                        int k = (short) ((s.getYvel() >> 3) * t[3]);
                        t[2] += k;
                        t[4] += k;
                        ms(i);
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());

                        if (t[4] <= -511 || t[4] >= 512) {
                            t[4] = 0;
                            t[2] &= 0xffffff00;
                            ms(i);
                            engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                            break;
                        }
                    }
                    break;
                }
                case 12:
                case 47:
                case 48: {
                    if (sc == null) {
                        break;
                    }

                    if (t[0] == 3 || t[3] == 1) // Lights going off
                    {
                        sc.setFloorpal(0);
                        sc.setCeilingpal(0);
                        for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();

                            if (wal.getHitag() != 1) {
                                wal.setShade((byte) t[1]);
                                wal.setPal(0);
                            }
                        }

                        sc.setFloorshade((byte) t[1]);
                        sc.setCeilingshade((byte) t[2]);
                        t[0] = 0;

                        ListNode<Sprite> node = boardService.getSectNode(s.getSectnum());
                        while (node != null) {
                            Sprite sp = node.get();
                            if ((sp.getCstat() & 16) != 0) {
                                if ((sc.getCeilingstat() & 1) != 0) {
                                    sp.setShade(sc.getCeilingshade());
                                } else {
                                    sp.setShade(sc.getFloorshade());
                                }
                            }
                            node = node.getNext();
                        }

                        if (t[3] == 1) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    if (t[0] == 1) // Lights flickering on
                    {
                        if (st != 48) {
                            if (sc.getFloorshade() > s.getShade()) {
                                sc.setFloorpal(s.getPal());
                                sc.setFloorshade(sc.getFloorshade() - 2);
                                if (st != 47) {
                                    sc.setCeilingpal(s.getPal());
                                    sc.setCeilingshade(sc.getCeilingshade() - 2);
                                }

                                for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal = wn.get();
                                    if (wal.getHitag() != 1) {
                                        wal.setPal(s.getPal());
                                        wal.setShade(wal.getShade() - 2);
                                    }
                                }
                            } else {
                                t[0] = 2;
                            }
                        } else {
                            if (sc.getCeilingshade() > s.getShade()) {
                                sc.setCeilingpal(s.getPal());
                                sc.setCeilingshade(sc.getCeilingshade() - 2);

                                for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal = wn.get();
                                    if (wal.getHitag() != 1) {
                                        wal.setPal(s.getPal());
                                        wal.setShade(wal.getShade() - 2);
                                    }
                                }
                            } else {
                                t[0] = 2;
                            }
                        }

                        ListNode<Sprite> node = boardService.getSectNode(s.getSectnum());
                        while (node != null) {
                            Sprite sp = node.get();
                            if ((sp.getCstat() & 16) != 0) {
                                if ((sc.getCeilingstat() & 1) != 0) {
                                    sp.setShade(sc.getCeilingshade());
                                } else {
                                    sp.setShade(sc.getFloorshade());
                                }
                            }
                            node = node.getNext();
                        }
                    }
                    break;
                }
                case 13: {
                    if (sc != null && t[2] != 0) {
                        int j = (s.getYvel() << 5) | 1;

                        if (s.getAng() == 512) {
                            if (s.getOwner() != 0) {
                                game.pInt.setceilinterpolate(s.getSectnum(), sc);
                                if (klabs(t[0] - sc.getCeilingz()) >= j) {
                                    sc.setCeilingz(sc.getCeilingz() + sgn(t[0] - sc.getCeilingz()) * j);
                                } else {
                                    sc.setCeilingz(t[0]);
                                }
                            } else {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                if (klabs(t[1] - sc.getFloorz()) >= j) {
                                    sc.setFloorz(sc.getFloorz() + sgn(t[1] - sc.getFloorz()) * j);
                                } else {
                                    sc.setFloorz(t[1]);
                                }
                            }
                        } else {
                            game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                            game.pInt.setceilinterpolate(s.getSectnum(), sc);
                            if (klabs(t[1] - sc.getFloorz()) >= j) {
                                sc.setFloorz(sc.getFloorz() + sgn(t[1] - sc.getFloorz()) * j);
                            } else {
                                sc.setFloorz(t[1]);
                            }
                            if (klabs(t[0] - sc.getCeilingz()) >= j) {
                                sc.setCeilingz(sc.getCeilingz() + sgn(t[0] - sc.getCeilingz()) * j);
                            }
                            sc.setCeilingz(t[0]);
                        }

                        if (t[3] == 1) {
                            // Change the shades

                            t[3]++;
                            sc.setCeilingstat(sc.getCeilingstat() ^ 1);

                            if (s.getAng() == 512) {
                                for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal = wn.get();
                                    wal.setShade(s.getShade());
                                }

                                sc.setFloorshade(s.getShade());

                                Sector sec = boardService.getSector(ps[connecthead].one_parallax_sectnum);
                                if (sec != null) {
                                    sc.setCeilingpicnum(sec.getCeilingpicnum());
                                    sc.setCeilingshade(sec.getCeilingshade());
                                }
                            }
                        }
                        t[2]++;
                        if (t[2] > 256) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    if (t[2] == 4 && s.getAng() != 512) {
                        for (int x = 0; x < 7; x++) {
                            RANDOMSCRAP(s, i);
                        }
                    }
                    break;
                }
                case 15: {
                    if (t[4] != 0) {
                        s.setXvel(16);

                        if (t[4] == 1) // Opening
                        {
                            if (t[3] >= (s.getYvel() >> 3)) {
                                t[4] = 0; // Turn off the sliders
                                callsound(s.getSectnum(), i);
                                break;
                            }
                            t[3]++;
                        } else if (t[4] == 2) {
                            if (t[3] < 1) {
                                t[4] = 0;
                                callsound(s.getSectnum(), i);
                                break;
                            }
                            t[3]--;
                        }

                        ms(i);
                        engine.setsprite(i, s.getX(), s.getY(), s.getZ());
                    }
                    break;
                }
                case 16: // Reactor
                {
                    if (sc == null) {
                        break;
                    }

                    t[2] += 32;
                    if (sc.getFloorz() < sc.getCeilingz()) {
                        s.setShade(0);
                    } else if (sc.getCeilingz() < t[3]) {

                        // The following code check to see if
                        // there is any other sprites in the sector.
                        // If there isn't, then kill this sectoreffector
                        // itself.....

                        ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                        while (nj != null) {
                            Sprite sp = nj.get();
                            if (sp.getPicnum() == REACTOR || sp.getPicnum() == REACTOR2) {
                                break;
                            }
                            nj = nj.getNext();
                        }
                        if (nj == null) {
                            engine.deletesprite(i);
                            continue;
                        } else {
                            s.setShade(1);
                        }
                    }

                    game.pInt.setceilinterpolate(s.getSectnum(), sc);
                    if (s.getShade() != 0) {
                        sc.setCeilingz(sc.getCeilingz() + 1024);
                    } else {
                        sc.setCeilingz(sc.getCeilingz() - 512);
                    }

                    ms(i);
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());

                    break;
                }
                case 17: {
                    if (sc == null) {
                        break;
                    }

                    int q = t[0] * (s.getYvel() << 2);

                    game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                    game.pInt.setceilinterpolate(s.getSectnum(), sc);
                    sc.setCeilingz(sc.getCeilingz() + q);
                    sc.setFloorz(sc.getFloorz() + q);

                    ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                    while (nj != null) {
                        Sprite sp = nj.get();
                        int j = nj.getIndex();
                        if (sp.getStatnum() == 10 && sp.getOwner() >= 0) {
                            int p = sp.getYvel();

                            ps[p].posz += q;
                            ps[p].truefz += q;
                            ps[p].truecz += q;
                            if (numplayers > 1) {
                                ps[p].oposz = ps[p].posz;
                            }
                        }
                        if (sp.getStatnum() != 3) {
                            game.pInt.setsprinterpolate(j, sp);
                            sp.setZ(sp.getZ() + q);
                        }

                        hittype[j].floorz = sc.getFloorz();
                        hittype[j].ceilingz = sc.getCeilingz();

                        nj = nj.getNext();
                    }

                    if (t[0] != 0) // If in motion
                    {
                        if (klabs(sc.getFloorz() - t[2]) <= s.getYvel()) {
                            activatewarpelevators(i, 0);
                            break;
                        }

                        if (t[0] == -1) {
                            if (sc.getFloorz() > t[3]) {
                                break;
                            }
                        } else if (sc.getCeilingz() < t[4]) {
                            break;
                        }

                        if (t[1] == 0) {
                            break;
                        }
                        t[1] = 0;

                        int j = -1;
                        ListNode<Sprite> node = boardService.getStatNode(3);
                        while (node != null) {
                            Sprite sp = node.get();
                            j = node.getIndex();
                            if (i != j && (sp.getLotag()) == 17) {
                                Sector sec = boardService.getSector(sp.getSectnum());
                                if (sec != null && (sc.getHitag() - t[0]) == (sec.getHitag()) && sh == (sp.getHitag())) {
                                    break;
                                }
                            }
                            node = node.getNext();
                        }

                        if (node == null) {
                            break;
                        }

                        Sprite sprite = boardService.getSprite(j);
                        if (sprite == null) {
                            break;
                        }

                        Sector jsec = boardService.getSector(sprite.getSectnum());
                        if (jsec == null) {
                            break;
                        }

                        ListNode<Sprite> nk = boardService.getSectNode(s.getSectnum());
                        while (nk != null) {
                            Sprite sp = nk.get();
                            ListNode<Sprite> nextk = nk.getNext();
                            int k = nk.getIndex();

                            if (sp.getStatnum() == 10 && sp.getOwner() >= 0) {
                                int p = sp.getYvel();

                                ps[p].posx += sprite.getX() - s.getX();
                                ps[p].posy += sprite.getY() - s.getY();
                                ps[p].posz = jsec.getFloorz() - (sc.getFloorz() - ps[p].posz);

                                hittype[k].floorz = jsec.getFloorz();
                                hittype[k].ceilingz = jsec.getCeilingz();

                                ps[p].bobposx = ps[p].oposx = ps[p].posx;
                                ps[p].bobposy = ps[p].oposy = ps[p].posy;
                                ps[p].oposz = ps[p].posz;

                                ps[p].truefz = hittype[k].floorz;
                                ps[p].truecz = hittype[k].ceilingz;
                                ps[p].bobcounter = 0;

                                game.pInt.setsprinterpolate(ps[p].i, boardService.getSprite(ps[p].i));
                                engine.changespritesect(k, sprite.getSectnum());
                                ps[p].cursectnum = sprite.getSectnum();

                                ps[p].UpdatePlayerLoc();
                            } else if (sp.getStatnum() != 3) {
                                game.pInt.setsprinterpolate(k, sp);

                                sp.setX(sp.getX() + sprite.getX() - s.getX());
                                sp.setY(sp.getY() + sprite.getY() - s.getY());
                                sp.setZ(jsec.getFloorz() - (sc.getFloorz() - sp.getZ()));

                                engine.changespritesect(k, sprite.getSectnum());
                                engine.setsprite(k, sp.getX(), sp.getY(), sp.getZ());

                                hittype[k].floorz = jsec.getFloorz();
                                hittype[k].ceilingz = jsec.getCeilingz();

                            }
                            nk = nextk;
                        }
                    }
                    break;
                }
                case 18: {
                    if (sc != null && t[0] != 0) {
                        if (s.getPal() != 0) {
                            if (s.getAng() == 512) {
                                game.pInt.setceilinterpolate(s.getSectnum(), sc);
                                sc.setCeilingz(sc.getCeilingz() - sc.getExtra());
                                if (sc.getCeilingz() <= t[1]) {
                                    sc.setCeilingz(t[1]);
                                    engine.deletesprite(i);
                                    continue;
                                }
                            } else {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() + sc.getExtra());
                                ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                                while (nj != null) {
                                    Sprite sp = nj.get();
                                    int j = nj.getIndex();
                                    if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                        if (ps[sp.getYvel()].on_ground) {
                                            ps[sp.getYvel()].posz += sc.getExtra();
                                        }
                                    }
                                    if (sp.getZvel() == 0 && sp.getStatnum() != 3 && sp.getStatnum() != 4) {
                                        game.pInt.setsprinterpolate(j, sp);
                                        sp.setZ(sp.getZ() + sc.getExtra());
                                        hittype[j].floorz = sc.getFloorz();
                                    }
                                    nj = nj.getNext();
                                }
                                if (sc.getFloorz() >= t[1]) {
                                    sc.setFloorz(t[1]);
                                    engine.deletesprite(i);
                                    continue;
                                }
                            }
                        } else {
                            if (s.getAng() == 512) {
                                game.pInt.setceilinterpolate(s.getSectnum(), sc);
                                sc.setCeilingz(sc.getCeilingz() + sc.getExtra());
                                if (sc.getCeilingz() >= s.getZ()) {
                                    sc.setCeilingz(s.getZ());
                                    engine.deletesprite(i);
                                    continue;
                                }
                            } else {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(sc.getFloorz() - sc.getExtra());
                                ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                                while (nj != null) {
                                    Sprite sp = nj.get();
                                    int j = nj.getIndex();
                                    if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                        if (ps[sp.getYvel()].on_ground) {
                                            ps[sp.getYvel()].posz -= sc.getExtra();
                                        }
                                    }
                                    if (sp.getZvel() == 0 && sp.getStatnum() != 3 && sp.getStatnum() != 4) {
                                        game.pInt.setsprinterpolate(j, sp);
                                        sp.setZ(sp.getZ() - sc.getExtra());
                                        hittype[j].floorz = sc.getFloorz();
                                    }
                                    nj = nj.getNext();
                                }
                                if (sc.getFloorz() <= s.getZ()) {
                                    sc.setFloorz(s.getZ());
                                    engine.deletesprite(i);
                                    continue;
                                }
                            }
                        }

                        t[2]++;
                        if (t[2] >= s.getHitag()) {
                            t[2] = 0;
                            t[0] = 0;
                        }
                    }
                    break;
                }
                case 19: // Battlestar galactia shields
                {
                    if (sc != null && t[0] != 0) {
                        if (t[0] == 1) {
                            t[0] = 2;

                            for (ListNode<Wall> wn = sc.getWallNode(); wn != null; wn = wn.getNext()) {
                                Wall wal = wn.get();
                                if (wal.getOverpicnum() == BIGFORCE) {
                                    wal.setCstat(wal.getCstat() & (128 + 32 + 8 + 4 + 2));
                                    wal.setOverpicnum(0);
                                    Wall nwal = boardService.getWall(wal.getNextwall());
                                    if (nwal != null) {
                                        nwal.setOverpicnum(0);
                                        nwal.setCstat(nwal.getCstat() & (128 + 32 + 8 + 4 + 2));
                                    }
                                }
                            }
                        }

                        game.pInt.setceilinterpolate(s.getSectnum(), sc);
                        if (sc.getCeilingz() < sc.getFloorz()) {
                            sc.setCeilingz(sc.getCeilingz() + s.getYvel());
                        } else {
                            sc.setCeilingz(sc.getFloorz());

                            ListNode<Sprite> node = boardService.getStatNode(3);
                            while (node != null) {
                                Sprite sp = node.get();
                                if (sp.getLotag() == 0 && sp.getHitag() == sh) {
                                    Sprite spo = boardService.getSprite(sp.getOwner());
                                    if (spo != null) {
                                        int q = spo.getSectnum();
                                        Sector qsec = boardService.getSector(q);
                                        Sector sec = boardService.getSector(sp.getSectnum());
                                        if (sec != null && qsec != null) {
                                            int pal = qsec.getFloorpal();
                                            sec.setCeilingpal(pal);
                                            sec.setFloorpal(pal);
                                            int shade = qsec.getFloorshade();
                                            sec.setCeilingshade(shade);
                                            sec.setFloorshade(shade);

                                            hittype[sp.getOwner()].temp_data[0] = 2;
                                        }
                                    }
                                }
                                node = node.getNext();
                            }
                            engine.deletesprite(i);
                            continue;
                        }
                    } else // Not hit yet
                    {
                        if (ifhitsectors(s.getSectnum()) >= 0) {
                            FTA(8, ps[myconnectindex]);

                            ListNode<Sprite> node = boardService.getStatNode(3);
                            while (node != null) {
                                int l = node.getIndex();
                                Sprite sp = node.get();
                                int x = sp.getLotag() & 0x7fff;
                                switch (x) {
                                    case 0:
                                        if (sp.getHitag() == sh) {
                                            int q = sp.getSectnum();

                                            Sector sec = boardService.getSector(q);
                                            Sprite spo = boardService.getSprite(sp.getOwner());
                                            if (sec != null && spo != null) {
                                                int pal = spo.getPal();
                                                sec.setCeilingpal(pal);
                                                sec.setFloorpal(pal);
                                                int shade = spo.getShade();
                                                sec.setCeilingshade(shade);
                                                sec.setFloorshade(shade);
                                            }
                                        }
                                        break;

                                    case 1:
                                    case 12:
                                    case 19:

                                        if (sh == sp.getHitag()) {
                                            if (hittype[l].temp_data[0] == 0) {
                                                hittype[l].temp_data[0] = 1; // Shut
                                                // them
                                                // all
                                                // on
                                                sp.setOwner(i);
                                            }
                                        }

                                        break;
                                }
                                node = node.getNext();
                            }
                        }
                    }

                    break;
                }
                case 20: // Extend-o-bridge
                {
                    if (t[0] == 0) {
                        break;
                    }
                    if (t[0] == 1) {
                        s.setXvel(8);
                    } else {
                        s.setXvel(-8);
                    }

                    if (s.getXvel() != 0) // Moving
                    {
                        int x = (s.getXvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 14;
                        int l = (s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14;

                        t[3] += s.getXvel();

                        s.setX(s.getX() + x);
                        s.setY(s.getY() + l);

                        if (t[3] <= 0 || (t[3] >> 6) >= (s.getYvel() >> 6)) {
                            s.setX(s.getX() - x);
                            s.setY(s.getY() - l);
                            t[0] = 0;
                            callsound(s.getSectnum(), i);
                            break;
                        }

                        ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                        while (nj != null) {
                            Sprite sp = nj.get();
                            ListNode<Sprite> nextj = nj.getNext();
                            int j = nj.getIndex();

                            if (sp.getStatnum() != 3 && sp.getZvel() == 0) {
                                game.pInt.setsprinterpolate(j, sp);

                                sp.setX(sp.getX() + x);
                                sp.setY(sp.getY() + l);
                                engine.setsprite(j, sp.getX(), sp.getY(), sp.getZ());
                                Sector sec = boardService.getSector(sp.getSectnum());
                                if (sec != null && (sec.getFloorstat() & 2) != 0) {
                                    if (sp.getStatnum() == 2) {
                                        makeitfall(currentGame.getCON(), j);
                                    }
                                }
                            }
                            nj = nextj;
                        }

                        Wall w1 = boardService.getWall(t[1]);
                        Wall w2 = boardService.getWall(t[2]);

                        if (w1 != null) {
                            engine.dragpoint(t[1], w1.getX() + x, w1.getY() + l);
                        }

                        if (w2 != null) {
                            engine.dragpoint(t[2], w2.getX() + x, w2.getY() + l);
                        }

                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            if (ps[p].cursectnum == s.getSectnum() && ps[p].on_ground) {
                                ps[p].posx += x;
                                ps[p].posy += l;

                                engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy, ps[p].posz + PHEIGHT);
                            }
                        }

                        if (sc != null) {
                            sc.setFloorxpanning(sc.getFloorxpanning() - (x >> 3));
                            sc.setFloorypanning(sc.getFloorypanning() - (l >> 3));

                            sc.setCeilingxpanning(sc.getCeilingxpanning() - (x >> 3));
                            sc.setCeilingypanning(sc.getCeilingypanning() - (l >> 3));
                        }
                    }

                    break;
                }
                case 21: // Cascading effect
                {
                    if (sc == null || t[0] == 0) {
                        break;
                    }

                    int l;
                    if (s.getAng() == 1536) {
                        l = sc.getCeilingz();
                    } else {
                        l = sc.getFloorz();
                    }

                    if (t[0] == 1) // Decide if the s.sectnum should go up or down
                    {
                        s.setZvel((short) (ksgn(s.getZ() - l) * (s.getYvel() << 4)));
                        t[0]++;
                    }

                    if (sc.getExtra() == 0) {
                        l += s.getZvel();
                        if (s.getAng() == 1536) {
                            sc.setCeilingz(l);
                        } else {
                            sc.setFloorz(l);
                        }

                        if (klabs(l - s.getZ()) < 1024) {
                            l = s.getZ();
                            if (s.getAng() == 1536) {
                                game.pInt.setceilinterpolate(s.getSectnum(), sc);
                                sc.setCeilingz(l);
                            } else {
                                game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                                sc.setFloorz(l);
                            }
                            engine.deletesprite(i);
                            continue;
                        }
                    } else {
                        sc.setExtra(sc.getExtra() - 1);
                    }
                    break;
                }
                case 22: {
                    if (t[1] != 0) {
                        if (sc != null && getanimationgoal(boardService.getSector(t[0]), CEILZ) >= 0) {
                            game.pInt.setceilinterpolate(s.getSectnum(), sc);
                            sc.setCeilingz(sc.getCeilingz() + sc.getExtra() * 9);
                        } else {
                            t[1] = 0;
                        }
                    }
                    break;
                }
                case 24:
                case 34: {
                    if (t[4] != 0) {
                        break;
                    }

                    int x = (s.getYvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 18;
                    int l = (s.getYvel() * EngineUtils.sin(s.getAng() & 2047)) >> 18;

                    ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                    while (nj != null) {
                        Sprite sp = nj.get();
                        ListNode<Sprite> nextj = nj.getNext();
                        int j = nj.getIndex();
                        if (sp.getZvel() >= 0) {
                            switch (sp.getStatnum()) {
                                case 5:
                                    switch (sp.getPicnum()) {
                                        case BLOODPOOL:
                                        case FOOTPRINTS:
                                        case FOOTPRINTS2:
                                        case FOOTPRINTS3:
                                        case FOOTPRINTS4:
                                        case BULLETHOLE:
                                        case BLOODSPLAT1:
                                        case BLOODSPLAT2:
                                        case BLOODSPLAT3:
                                        case BLOODSPLAT4:
                                            sp.setXrepeat(0);
                                            sp.setYrepeat(0);
                                            nj = nextj;
                                            continue;
                                    }
                                case 6:
                                case 1:
                                case 0:
                                    if (sp.getPicnum() == BOLT1 || sp.getPicnum() == BOLT1 + 1 || sp.getPicnum() == BOLT1 + 2 || sp.getPicnum() == BOLT1 + 3 || wallswitchcheck(j)) {
                                        break;
                                    }

                                    if (!(sp.getPicnum() >= CRANE && sp.getPicnum() <= (CRANE + 3))) {
                                        if (sp.getZ() > (hittype[j].floorz - (16 << 8))) {
                                            game.pInt.setsprinterpolate(j, sp);

                                            sp.setX(sp.getX() + (x >> 2));
                                            sp.setY(sp.getY() + (l >> 2));

                                            engine.setsprite(j, sp.getX(), sp.getY(), sp.getZ());

                                            Sector sec = boardService.getSector(sp.getSectnum());
                                            if (sec != null && (sec.getFloorstat() & 2) != 0) {
                                                if (sp.getStatnum() == 2) {
                                                    makeitfall(currentGame.getCON(), j);
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                        nj = nextj;
                    }

                    int p = myconnectindex;
                    if (ps[p].cursectnum == s.getSectnum() && ps[p].on_ground) {
                        if (klabs(ps[p].posz - ps[p].truefz) < PHEIGHT + (9 << 8)) {
                            fricxv += x << 3;
                            fricyv += l << 3;
                        }
                    }

                    if (sc != null) {
                        sc.setFloorxpanning(sc.getFloorxpanning() + (s.getYvel() >> 7));
                    }
                    break;
                }
                case 35: {
                    if (sc == null) {
                        break;
                    }

                    if (sc.getCeilingz() > s.getZ()) {
                        for (int j = 0; j < 8; j++) {
                            s.setAng(s.getAng() + (engine.krand() & 511));
                            int k = (short) spawn(i, SMALLSMOKE);
                            Sprite sp = boardService.getSprite(k);
                            if (sp != null) {
                                sp.setXvel((96 + (engine.krand() & 127)));
                                ssp(k, CLIPMASK0);
                                engine.setsprite(k, sp.getX(), sp.getY(), sp.getZ());
                                if (rnd(16)) {
                                    spawn(i, EXPLOSION2);
                                }
                            }
                        }
                    }

                    switch (t[0]) {
                        case 0:
                            game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                            game.pInt.setceilinterpolate(s.getSectnum(), sc);

                            sc.setCeilingz(sc.getCeilingz() + s.getYvel());
                            if (sc.getCeilingz() > sc.getFloorz()) {
                                sc.setFloorz(sc.getCeilingz());
                            }
                            if (sc.getCeilingz() > s.getZ() + (32 << 8)) {
                                t[0]++;
                            }
                            break;
                        case 1:
                            game.pInt.setceilinterpolate(s.getSectnum(), sc);
                            sc.setCeilingz(sc.getCeilingz() - (s.getYvel() << 2));
                            if (sc.getCeilingz() < t[4]) {
                                sc.setCeilingz(t[4]);
                                t[0] = 0;
                            }
                            break;
                    }
                    break;
                }
                case 25: {

                    if (sc == null || t[4] == 0) {
                        break;
                    }

                    if (sc.getFloorz() <= sc.getCeilingz()) {
                        s.setShade(0);
                    } else if (sc.getCeilingz() <= t[3]) {
                        s.setShade(1);
                    }

                    game.pInt.setceilinterpolate(s.getSectnum(), sc);
                    if (s.getShade() != 0) {
                        sc.setCeilingz(sc.getCeilingz() + (s.getYvel() << 4));
                        if (sc.getCeilingz() > sc.getFloorz()) {
                            sc.setCeilingz(sc.getFloorz());
                            if (ps[screenpeek].field_601 != 0) {
                                spritesound(371, i);
                            }
                        }
                    } else {
                        sc.setCeilingz(sc.getCeilingz() - (s.getYvel() << 4));
                        if (sc.getCeilingz() < t[3]) { // XXX data[4] RA
                            sc.setCeilingz(t[3]);
                            if (ps[screenpeek].field_601 != 0) {
                                spritesound(167, i);
                            }
                        }
                    }

                    break;
                }
                case 26: {
                    if (sc == null) {
                        break;
                    }

                    s.setXvel(32);
                    int l = (s.getXvel() * EngineUtils.cos((s.getAng()) & 2047)) >> 14;
                    int x = (s.getXvel() * EngineUtils.sin(s.getAng() & 2047)) >> 14;

                    s.setShade(s.getShade() + 1);
                    game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                    if (s.getShade() > 7) {
                        s.setX(t[3]);
                        s.setY(t[4]);
                        sc.setFloorz(sc.getFloorz() - ((s.getZvel() * s.getShade()) - s.getZvel()));
                        s.setShade(0);
                    } else {
                        sc.setFloorz(sc.getFloorz() + s.getZvel());
                    }

                    ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                    while (nj != null) {
                        Sprite sp = nj.get();
                        ListNode<Sprite> nextj = nj.getNext();
                        int j = nj.getIndex();
                        if (sp.getStatnum() != 3 && sp.getStatnum() != 10) {
                            game.pInt.setsprinterpolate(j, sp);

                            sp.setX(sp.getX() + l);
                            sp.setY(sp.getY() + x);

                            sp.setZ(sp.getZ() + s.getZvel());
                            engine.setsprite(j, sp.getX(), sp.getY(), sp.getZ());
                        }
                        nj = nextj;
                    }

                    int p = myconnectindex;
                    if (ps[p].getPlayerSprite().getSectnum() == s.getSectnum() && ps[p].on_ground) {
                        fricxv += l << 5;
                        fricyv += x << 5;
                    }

                    for (p = connecthead; p >= 0; p = connectpoint2[p]) {
                        if (ps[p].getPlayerSprite().getSectnum() == s.getSectnum() && ps[p].on_ground) {
                            ps[p].posz += s.getZvel();
                        }
                    }

                    ms(i);
                    engine.setsprite(i, s.getX(), s.getY(), s.getZ());

                    break;
                }
                case 27: {
                    if (ud.recstat == DEMOSTAT_NULL) { // demo camera
                        break;
                    }

                    hittype[i].tempang = s.getAng();

                    int p = (short) findplayer(s);
                    int x = player_dist;
                    Sprite psp = boardService.getSprite(ps[p].i);

                    if (psp != null && psp.getExtra() > 0 && myconnectindex == screenpeek) {
                        if (t[0] < 0) {
                            ud.camerasprite = i;
                            t[0]++;
                        } else if (DemoScreen.isDemoPlaying() && ps[p].newowner == -1) {
                            if (engine.cansee(s.getX(), s.getY(), s.getZ(), s.getSectnum(), ps[p].posx, ps[p].posy, ps[p].posz, ps[p].cursectnum)) {
                                if (x < (sh & 0xFFFF)) {
                                    ud.camerasprite = i;
                                    t[0] = 999;
                                    s.setAng(s.getAng() + (getincangle(s.getAng(), EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY())) >> 3));
                                    s.setYvel((short) (100 + ((s.getZ() - ps[p].posz) / 257)));

                                } else if (t[0] == 999) {
                                    if (ud.camerasprite == i) {
                                        t[0] = 0;
                                    } else {
                                        t[0] = -10;
                                    }
                                    ud.camerasprite = i;

                                }
                            } else {
                                s.setAng(EngineUtils.getAngle(ps[p].posx - s.getX(), ps[p].posy - s.getY()));

                                if (t[0] == 999) {
                                    if (ud.camerasprite == i) {
                                        t[0] = 0;
                                    } else {
                                        t[0] = -20;
                                    }
                                    ud.camerasprite = i;
                                }
                            }
                        }
                    }
                    break;
                }
                case 29: {
                    s.setHitag(s.getHitag() + 64);
                    if (sc != null) {
                        sc.setFloorz(s.getZ() + mulscale(s.getYvel(), EngineUtils.sin(s.getHitag() & 2047), 12));
                    }
                    break;
                }
                case 31: {// True Drop Floor
                    if (t[0] == 1) {
                        // Choose dir

                        if (t[3] > 0) {
                            t[3]--;
                            break;
                        }

                        game.pInt.setfloorinterpolate(s.getSectnum(), sc);
                        ListNode<Sprite> nj = boardService.getSectNode(s.getSectnum());
                        while (nj != null) {
                            Sprite sp = nj.get();
                            game.pInt.setsprinterpolate(nj.getIndex(), sp);
                            nj = nj.getNext();
                        }

                        if (sc == null) {
                            break;
                        }

                        if (t[2] == 1) // Retract
                        {
                            if (s.getAng() != 1536) {
                                if (klabs(sc.getFloorz() - s.getZ()) < s.getYvel()) {
                                    sc.setFloorz(s.getZ());
                                    t[2] = 0;
                                    t[0] = 0;
                                    callsound(s.getSectnum(), i);
                                } else {
                                    int l = sgn(s.getZ() - sc.getFloorz()) * s.getYvel();
                                    sc.setFloorz(sc.getFloorz() + l);

                                    nj = boardService.getSectNode(s.getSectnum());
                                    while (nj != null) {
                                        Sprite sp = nj.get();
                                        if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                            if (ps[sp.getYvel()].on_ground) {
                                                ps[sp.getYvel()].posz += l;
                                            }
                                        }
                                        if (sp.getZvel() == 0 && sp.getStatnum() != 3) {
                                            sp.setZ(sp.getZ() + l);
                                            hittype[nj.getIndex()].floorz = sc.getFloorz();
                                        }
                                        nj = nj.getNext();
                                    }
                                }
                            } else {
                                if (klabs(sc.getFloorz() - t[1]) < s.getYvel()) {
                                    sc.setFloorz(t[1]);
                                    callsound(s.getSectnum(), i);
                                    t[2] = 0;
                                    t[0] = 0;
                                } else {
                                    int l = sgn(t[1] - sc.getFloorz()) * s.getYvel();
                                    sc.setFloorz(sc.getFloorz() + l);

                                    nj = boardService.getSectNode(s.getSectnum());
                                    while (nj != null) {
                                        Sprite sp = nj.get();
                                        if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                            if (ps[sp.getYvel()].on_ground) {
                                                ps[sp.getYvel()].posz += l;
                                            }
                                        }
                                        if (sp.getZvel() == 0 && sp.getStatnum() != 3) {
                                            sp.setZ(sp.getZ() + l);
                                            hittype[nj.getIndex()].floorz = sc.getFloorz();
                                        }
                                        nj = nj.getNext();
                                    }
                                }
                            }
                            break;
                        }

                        if ((s.getAng() & 2047) == 1536) {
                            if (klabs(s.getZ() - sc.getFloorz()) < s.getYvel()) {
                                callsound(s.getSectnum(), i);
                                t[0] = 0;
                                t[2] = 1;
                            } else {
                                int l = sgn(s.getZ() - sc.getFloorz()) * s.getYvel();
                                sc.setFloorz(sc.getFloorz() + l);

                                nj = boardService.getSectNode(s.getSectnum());
                                while (nj != null) {
                                    Sprite sp = nj.get();
                                    if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                        if (ps[sp.getYvel()].on_ground) {
                                            ps[sp.getYvel()].posz += l;
                                        }
                                    }
                                    if (sp.getZvel() == 0 && sp.getStatnum() != 3 && sp.getStatnum() != 4) {
                                        sp.setZ(sp.getZ() + l);
                                        hittype[nj.getIndex()].floorz = sc.getFloorz();
                                    }
                                    nj = nj.getNext();
                                }
                            }
                        } else {
                            if (klabs(sc.getFloorz() - t[1]) < s.getYvel()) {
                                t[0] = 0;
                                callsound(s.getSectnum(), i);
                                t[2] = 1;
                            } else {
                                int l = sgn(s.getZ() - t[1]) * s.getYvel();
                                sc.setFloorz(sc.getFloorz() - l);

                                nj = boardService.getSectNode(s.getSectnum());
                                while (nj != null) {
                                    Sprite sp = nj.get();
                                    if (sp.getPicnum() == APLAYER && sp.getOwner() >= 0) {
                                        if (ps[sp.getYvel()].on_ground) {
                                            ps[sp.getYvel()].posz -= l;
                                        }
                                    }
                                    if (sp.getZvel() == 0 && sp.getStatnum() != 3 && sp.getStatnum() != 4) {
                                        sp.setZ(sp.getZ() - l);
                                        hittype[nj.getIndex()].floorz = sc.getFloorz();
                                    }
                                    nj = nj.getNext();
                                }
                            }
                        }
                    }
                    break;
                }
                case 33:
                    if (earthquaketime > 0 && (engine.krand() & 7) == 0) {
                        RANDOMSCRAP(s, i);
                    }
                    break;

                case 32: {// True Drop Ceiling
                    if (sc != null && t[0] == 1) {
                        // Choose dir

                        game.pInt.setceilinterpolate(s.getSectnum(), sc);
                        if (t[2] == 1) // Retract
                        {
                            if (s.getAng() != 1536) {
                                if (klabs(sc.getCeilingz() - s.getZ()) < (s.getYvel() << 1)) {
                                    sc.setCeilingz(s.getZ());
                                    callsound(s.getSectnum(), i);
                                    t[2] = 0;
                                    t[0] = 0;
                                } else {
                                    sc.setCeilingz(sc.getCeilingz() + sgn(s.getZ() - sc.getCeilingz()) * s.getYvel());
                                }
                            } else {
                                if (klabs(sc.getCeilingz() - t[1]) < (s.getYvel() << 1)) {
                                    sc.setCeilingz(t[1]);
                                    callsound(s.getSectnum(), i);
                                    t[2] = 0;
                                    t[0] = 0;
                                } else {
                                    sc.setCeilingz(sc.getCeilingz() + sgn(t[1] - sc.getCeilingz()) * s.getYvel());
                                }
                            }
                            break;
                        }

                        if ((s.getAng() & 2047) == 1536) {
                            if (klabs(sc.getCeilingz() - s.getZ()) < (s.getYvel() << 1)) {
                                t[0] = 0;
                                t[2] ^= 1;
                                callsound(s.getSectnum(), i);
                                sc.setCeilingz(s.getZ());
                            } else {
                                sc.setCeilingz(sc.getCeilingz() + sgn(s.getZ() - sc.getCeilingz()) * s.getYvel());
                            }
                        } else {
                            if (klabs(sc.getCeilingz() - t[1]) < (s.getYvel() << 1)) {
                                t[0] = 0;
                                t[2] ^= 1;
                                callsound(s.getSectnum(), i);
                            } else {
                                sc.setCeilingz(sc.getCeilingz() - sgn(s.getZ() - t[1]) * s.getYvel());
                            }
                        }
                    }
                    break;
                }
                case 36: {
                    if (t[0] != 0) {
                        if (sc != null && t[0] == 1) {
                            shoot(i, sc.getExtra());
                        } else if (t[0] == 26 * 5) {
                            t[0] = 0;
                        }
                        t[0]++;
                    }
                }
                break;
                case 128: // SE to control glass breakage
                    Wall wal = boardService.getWall(t[2]);

                    //#GDX 30.06.2024: this check is always true
                    if (wal != null /*&& (wal.getCstat() | 32) != 0*/) {
                        wal.setCstat(wal.getCstat() & (255 - 32));
                        wal.setCstat(wal.getCstat() | 16);

                        Wall nwal = boardService.getWall(wal.getNextwall());
                        if (nwal != null) {
                            nwal.setCstat(nwal.getCstat() & (255 - 32));
                            nwal.setCstat(nwal.getCstat() | 16);
                        }
                    } else {
                        break;
                    }

                    wal.setOverpicnum(wal.getOverpicnum() + 1);
                    Wall nwal = boardService.getWall(wal.getNextwall());
                    if (nwal != null) {
                        nwal.setOverpicnum(nwal.getOverpicnum() + 1);
                    }

                    if (t[0] < t[1]) {
                        t[0]++;
                    } else {
                        wal.setCstat(wal.getCstat() & (128 + 32 + 8 + 4 + 2));
                        if (nwal != null) {
                            nwal.setCstat(nwal.getCstat() & (128 + 32 + 8 + 4 + 2));
                        }
                        engine.deletesprite(i);
                        continue;
                    }
                    break;

                case 130: {
                    if (t[0] > 80) {
                        engine.deletesprite(i);
                        continue;
                    } else {
                        t[0]++;
                    }

                    if (sc != null) {
                        int x = sc.getFloorz() - sc.getCeilingz();
                        if (rnd(64)) {
                            int k = (short) spawn(i, EXPLOSION2);
                            int size = (2 + (engine.krand() & 7));
                            Sprite ks = boardService.getSprite(k);
                            if (ks != null) {
                                ks.setXrepeat(size);
                                ks.setYrepeat(size);
                                ks.setZ(sc.getFloorz() - (engine.krand() % x));
                                ks.setAng(ks.getAng() + 256 - (engine.krand() % 511));
                                ks.setXvel((short) (engine.krand() & 127));
                                ssp(k, CLIPMASK0);
                            }
                        }
                    }
                    break;
                }
                case 131: {
                    if (t[0] > 40) {
                        engine.deletesprite(i);
                        continue;
                    } else {
                        t[0]++;
                    }

                    if (sc != null) {
                        int x = sc.getFloorz() - sc.getCeilingz();
                        if (rnd(32)) {
                            int k = (short) spawn(i, EXPLOSION2);
                            int size = (2 + (engine.krand() & 3));
                            Sprite ks = boardService.getSprite(k);
                            if (ks != null) {
                                ks.setXrepeat(size);
                                ks.setYrepeat(size);
                                ks.setZ(sc.getFloorz() - (engine.krand() % x));
                                ks.setAng(ks.getAng() + 256 - (engine.krand() % 511));
                                ks.setXvel((short) (engine.krand() & 127));
                                ssp(k, CLIPMASK0);
                            }
                        }
                    }
                    break;
                }
            }
        }

        for (ListNode<Sprite> node = boardService.getStatNode(3); node != null; node = node.getNext()) {
            Sprite s = node.get();
            if (s.getLotag() != 29) {
                continue;
            }

            Sector sc = boardService.getSector(s.getSectnum());
            if (sc == null || sc.getWallnum() != 4) {
                continue;
            }

            Wall wal = boardService.getWall(sc.getWallptr() + 2);
            if (wal == null) {
                continue;
            }

            Sector sec = boardService.getSector(wal.getNextsector());
            if (sec == null) {
                continue;
            }

            engine.alignflorslope(s.getSectnum(), wal.getX(), wal.getY(), sec.getFloorz());
        }
    }

    public static boolean BowlOpen(int sectorid) {
        Sector sec = boardService.getSector(sectorid);
        if (sec != null && getanimationgoal(boardService.getSector(sectorid), CEILZ) == -1) {
            int i = engine.nextsectorneighborz(sectorid, sec.getCeilingz(), -1, -1);
            Sector sec2 = boardService.getSector(i);
            if (sec2 == null) {
                return false;
            }
            setanimation(sectorid, sectorid, sec2.getCeilingz(), 64, CEILZ);
            return true;
        }

        return false;
    }

    public static int BowlCheck(int nSector) {
        Arrays.fill(pinstay, false);
        int pinums = 0, bowline = 0;
        for (ListNode<Sprite> node = boardService.getSectNode(nSector); node != null; node = node.getNext()) {
            Sprite sp = node.get();
            if (sp.getPicnum() == 3440) { // pin sprite
                pinstay[sp.getLotag()] = true;
                pinums++;
            }
            if (sp.getPicnum() == 280) {
                bowline = sp.getHitag();
            }
        }

        if (bowline != 0) {
            TileManager tileManager = engine.getTileManager();
            ArtEntry pic1 = tileManager.getTile(2024);
            DynamicArtEntry pic2 = tileManager.getDynamicTile(bowline + 2024);
            if (!pic2.hasSize()) {
                return 0;
            }

            tileManager.copytilepiece(pic1, 0, 0, 128, 64, pic2, 0, 0);
            for (int i = 0; i < 10; i++) {
                int x = 0, y = 0;
                if (pinstay[i]) {
                    switch (i) {
                        case 0:
                            x = 64;
                            y = 48;
                            break;
                        case 1:
                            x = 56;
                            y = 40;
                            break;
                        case 2:
                            x = 72;
                            y = 40;
                            break;
                        case 3:
                            x = 48;
                            y = 32;
                            break;
                        case 4:
                            x = 64;
                            y = 32;
                            break;
                        case 5:
                            x = 80;
                            y = 32;
                            break;
                        case 6:
                            x = 40;
                            y = 24;
                            break;
                        case 7:
                            x = 56;
                            y = 24;
                            break;
                        case 8:
                            x = 72;
                            y = 24;
                            break;
                        case 9:
                            x = 88;
                            y = 24;
                            break;
                    }
                    engine.getTileManager().copytilepiece(tileManager.getTile(2023), 0, 0, 8, 8, pic2, x - 4, y - 10);
                }
            }

            pic2.invalidate();
        }

        return pinums;
    }

    public static void BowlReset() {
        TileManager tileManager = engine.getTileManager();
        ArtEntry pic1 = tileManager.getTile(2024);

        for (int i = 0; i < 4; i++) {
            DynamicArtEntry pic2 = tileManager.getDynamicTile(i + 2025);
            engine.getTileManager().copytilepiece(pic1, 0, 0, 128, 64, pic2, 0, 0);
            for (int j = 0; j < 10; j++) {
                int x = 0, y = 0;
                switch (j) {
                    case 0:
                        x = 64;
                        y = 48;
                        break;
                    case 1:
                        x = 56;
                        y = 40;
                        break;
                    case 2:
                        x = 72;
                        y = 40;
                        break;
                    case 3:
                        x = 48;
                        y = 32;
                        break;
                    case 4:
                        x = 64;
                        y = 32;
                        break;
                    case 5:
                        x = 80;
                        y = 32;
                        break;
                    case 6:
                        x = 40;
                        y = 24;
                        break;
                    case 7:
                        x = 56;
                        y = 24;
                        break;
                    case 8:
                        x = 72;
                        y = 24;
                        break;
                    case 9:
                        x = 88;
                        y = 24;
                        break;
                }
                engine.getTileManager().copytilepiece(tileManager.getTile(2023), 0, 0, 8, 8, pic2, x - 4, y - 10);
            }
            pic2.invalidate();
        }
    }

    public static void BowlUpdate(int nSector) {
        for (ListNode<Sprite> node = boardService.getSectNode(nSector), nexti; node != null; node = nexti) {
            nexti = node.getNext();
            Sprite sp = node.get();
            if (sp.getPicnum() == 3440) { // pin sprite
                engine.deletesprite(node.getIndex());
            }
        }

        int bowline = 0;
        for (ListNode<Sprite> node = boardService.getSectNode(nSector); node != null; node = node.getNext()) {
            Sprite sp = node.get();
            if (sp.getPicnum() == 283) // pinid sprite
            {
                int spawnid = spawn(node.getIndex(), 3440);
                Sprite s = boardService.getSprite(spawnid);
                if (s != null) {
                    s.setLotag(sp.getLotag());
                    engine.krand();
                    s.setClipdist(48);

                    int rand = engine.krand();
                    s.setAng(s.getAng() - (((rand & 0x20) - (engine.krand() & 0x40)) & kAngleMask));
                }
            }

            if (sp.getPicnum() == 280) {
                bowline = sp.getHitag();
            }
        }

        if (bowline != -1) {
            TileManager tileManager = engine.getTileManager();
            ArtEntry pic1 = tileManager.getTile(2024);
            DynamicArtEntry pic2 = tileManager.getDynamicTile(bowline + 2024);
            if (!pic2.hasSize()) {
                return;
            }

            engine.getTileManager().copytilepiece(pic1, 0, 0, 128, 64, pic2, 0, 0);
            for (int i = 0; i < 10; i++) {
                int x = 0, y = 0;
                switch (i) {
                    case 0:
                        x = 64;
                        y = 48;
                        break;
                    case 1:
                        x = 56;
                        y = 40;
                        break;
                    case 2:
                        x = 72;
                        y = 40;
                        break;
                    case 3:
                        x = 48;
                        y = 32;
                        break;
                    case 4:
                        x = 64;
                        y = 32;
                        break;
                    case 5:
                        x = 80;
                        y = 32;
                        break;
                    case 6:
                        x = 40;
                        y = 24;
                        break;
                    case 7:
                        x = 56;
                        y = 24;
                        break;
                    case 8:
                        x = 72;
                        y = 24;
                        break;
                    case 9:
                        x = 88;
                        y = 24;
                        break;
                }
                engine.getTileManager().copytilepiece(tileManager.getTile(2023), 0, 0, 8, 8, pic2, x - 4, y - 10);
            }
            pic2.invalidate();
        }
    }

    public static void BowlClose(int nSprite) {
        Sprite bowlingBall = boardService.getSprite(nSprite);
        if (bowlingBall == null) {
            return;
        }

        for (ListNode<Sprite> node = boardService.getStatNode(105); node != null; node = node.getNext()) {
            Sprite sp = node.get();
            if (sp.getPicnum() == 281 && sp.getSectnum() == bowlingBall.getSectnum()) {
                for (ListNode<Sprite> nj = boardService.getStatNode(105); nj != null; nj = nj.getNext()) {
                    Sprite spj = nj.get();
                    if (spj.getPicnum() == 282 && sp.getHitag() == spj.getHitag()) {
                        spawn(nj.getIndex(), 3437);
                    }

                    if (spj.getPicnum() == 280 && spj.getHitag() == sp.getHitag() && spj.getLotag() == 0) {
                        spj.setLotag(100);
                        spj.setExtra(spj.getExtra() + 1);
                        int sectnum = spj.getSectnum();
                        Sector sec = boardService.getSector(sectnum);
                        if (sec != null) {
                            if (getanimationgoal(sec, CEILZ) == -1) {
                                setanimation(sectnum, sectnum, sec.getFloorz(), 64, CEILZ);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isPsychoSkill() {
        return currentGame.getCON().type != RRRA && ud.player_skill >= 5 || currentGame.getCON().type == RRRA && ud.player_skill >= 4;
    }
}
