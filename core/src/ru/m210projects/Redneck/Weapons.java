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
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Redneck.Factory.RRRenderer;
import ru.m210projects.Redneck.Types.PlayerStruct;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.net.Mmulti.myconnectindex;
import static ru.m210projects.Build.net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.FindDistance2D;
import static ru.m210projects.Redneck.Gameutils.rnd;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Player.checkavailweapon;
import static ru.m210projects.Redneck.Player.hits;
import static ru.m210projects.Redneck.Premap.shadeEffect;
import static ru.m210projects.Redneck.Screen.myos;
import static ru.m210projects.Redneck.Screen.myospal;
import static ru.m210projects.Redneck.RSector.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.View.*;

public class Weapons {

    public static final short[] weapon_sprites = {
            3340,
            FIRSTGUNSPRITE,
            SHOTGUNSPRITE,
            22,
            26,
            23,
            25,
            29,
            27,
            24,
            1409,
            25,
            3437
    };

    public static final short[] aimstats = {10, 13, 1, 2};
    public static final int[] rake_x = {580, 676, 310, 491, 356, 210, 310, 614, 0, 0};
    public static final int[] rake_y = {369, 363, 300, 323, 371, 400, 300, 440, 0, 0};
    public static final byte[] crowbar_frames = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
    public static final int[] crowbar_x = {310, 342, 364, 418, 350, 316, 282, 288, 0, 0};
    public static final int[] crowbar_y = {300, 362, 320, 268, 248, 248, 277, 420, 0, 0};
    public static final byte[] pistol_frames = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 6, 6, 6, 6, 5, 5, 4, 4, 3, 3, 0, 0};
    public static final int[] pistol_x = {194, 190, 185, 208, 215, 215, 216, 216, 201, 170};
    public static final int[] pistol_y = {256, 249, 248, 238, 228, 218, 208, 256, 245, 258};
    public static final byte[] rpistol_frames = {0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 0, 0,};
    public static final int[] rpistol_x = {244, 244, 244};
    public static final int[] rpistol_y = {256, 249, 248};
    public static final byte[] shotgun_frames = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 0, 0, 20, 20, 21, 21, 21, 21, 20, 20, 20, 20, 0, 0,};
    public static final byte[] shotgun_frames2 = {0, 0, 1, 1, 2, 2, 5, 5, 6, 6, 7, 7, 8, 8, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] shotgun_frames3 = {0, 0, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 0, 0, 20, 20, 21, 21, 21, 21, 20, 20, 20, 20, 0, 0};
    public static final short[] shotgun_x = {300, 300, 300, 300, 300, 330, 320, 310, 305, 306, 302};
    public static final short[] shotgun_y = {315, 300, 302, 305, 302, 302, 303, 306, 302, 404, 384,};
    public static final byte[] dynamite_flames = {1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6, 0};
    public static final byte[] weap5_frames = {0, 1, 1, 2, 2, 3, 2, 3, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7};
    public static final byte[] weap7_frames = {0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] weap8_frames = {1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static short fistsign;

    public static int aim(Sprite s, int aang) {
        int i, j;
        int xv, yv;
        int a = s.getAng();

        if (s.getPicnum() == APLAYER && (ps[s.getYvel()].auto_aim == 0
                && ps[s.getYvel()].curr_weapon != CHICKENBOW_WEAPON
                && ps[s.getYvel()].curr_weapon != MOTO_WEAPON
                && ps[s.getYvel()].curr_weapon != BOAT_WEAPON)) {
            return -1;
        }

        j = -1;

        int smax = 0x7fffffff;

        int dx1 = EngineUtils.sin((a + 512 - aang) & 2047);
        int dy1 = EngineUtils.sin((a - aang) & 2047);
        int dx2 = EngineUtils.sin((a + 512 + aang) & 2047);
        int dy2 = EngineUtils.sin((a + aang) & 2047);

        int dx3 = EngineUtils.sin((a + 512) & 2047);
        int dy3 = EngineUtils.sin(a & 2047);

        for (int k = 0; k < 4; k++) {
            if (j >= 0) {
                break;
            }
            for (ListNode<Sprite> node = boardService.getStatNode(aimstats[k]); node != null; node = node.getNext()) {
                i = node.getIndex();
                Sprite sp = node.get();
                if (sp.getXrepeat() > 0 && sp.getExtra() >= 0 && (sp.getCstat() & (257 + 32768)) == 257) {
                    if (badguy(sp) || k < 2) {
                        if (!badguy(sp) && sp.getPicnum() != APLAYER && sp.getPicnum() != 5501
                                || sp.getPicnum() != APLAYER || ud.ffire != 0 || ud.coop != 1 || s.getPicnum() != APLAYER || s == sp) {
                            xv = (sp.getX() - s.getX());
                            yv = (sp.getY() - s.getY());

                            if ((dy1 * xv) <= (dx1 * yv)) {
                                if ((dy2 * xv) >= (dx2 * yv)) {
                                    int sdist = mulscale(dx3, xv, 14) + mulscale(dy3, yv, 14);
                                    if (sdist > 512 && sdist < smax) {
                                        if (s.getPicnum() == APLAYER) {
                                            a = ((klabs(scale(sp.getZ() - s.getZ(), 10, sdist) - ((int) ps[s.getYvel()].horiz + ps[s.getYvel()].horizoff - 100)) < 100) ? 1 : 0);
                                        } else {
                                            a = 1;
                                        }

                                        if (a != 0 && engine.cansee(sp.getX(), sp.getY(), sp.getZ() - (32 << 8), sp.getSectnum(), s.getX(), s.getY(), s.getZ() - (32 << 8), s.getSectnum())) {
                                            smax = sdist;
                                            j = i;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return j;
    }

    private static void tracers(int x1, int y1, int z1, int x2, int y2, int z2, int n) {
        int i, xv, yv, zv;
        int sect = -1;

        i = n + 1;
        xv = (x2 - x1) / i;
        yv = (y2 - y1) / i;
        zv = (z2 - z1) / i;

        if ((klabs(x1 - x2) + klabs(y1 - y2)) < 3084) {
            return;
        }

        for (i = n; i > 0; i--) {
            x1 += xv;
            y1 += yv;
            z1 += zv;
            sect = engine.updatesector(x1, y1, sect);
            Sector sec = boardService.getSector(sect);
            if (sec != null) {
                if (sec.getLotag() == 2) {
                    int va = engine.krand() & 2047;
                    int vy = 4 + (engine.krand() & 3);
                    int vx = 4 + (engine.krand() & 3);
                    EGS(sect, x1, y1, z1, WATERBUBBLE, -32, vx, vy, va, 0, 0, ps[0].i, 5);
                } else {
                    EGS(sect, x1, y1, z1, SMALLSMOKE, -32, 14, 14, 0, 0, 0, ps[0].i, (short) 5);
                }
            }
        }
    }

    public static void addammo(int weapon, PlayerStruct p, int amount) {
        p.ammo_amount[weapon] += amount;

        if (p.ammo_amount[weapon] > currentGame.getCON().max_ammo_amount[weapon]) {
            p.ammo_amount[weapon] = currentGame.getCON().max_ammo_amount[weapon];
        }
    }

    public static void addweapon(PlayerStruct p, int weapon) {

        if (p.OnMotorcycle || p.OnBoat) {
            p.gotweapon[KNEE_WEAPON] = true;
            switch (weapon) {
                case THROWSAW_WEAPON:
                    p.gotweapon[BUZSAW_WEAPON] = true;
                    p.gotweapon[THROWSAW_WEAPON] = true;
                    p.ammo_amount[BUZSAW_WEAPON] = 1;
                    break;
                case CROSSBOW_WEAPON:
                    p.gotweapon[CROSSBOW_WEAPON] = true;
                    p.gotweapon[CHICKENBOW_WEAPON] = true;
                    break;
                case RATE_WEAPON:
                    p.gotweapon[RATE_WEAPON] = true;
                    break;
            }
            return;
        }

        if (!p.gotweapon[weapon]) {
            p.gotweapon[weapon] = true;
            if (weapon == THROWSAW_WEAPON) {
                p.gotweapon[THROWSAW_WEAPON] = true;
                p.gotweapon[BUZSAW_WEAPON] = true;
                p.ammo_amount[BUZSAW_WEAPON] = 1;
            }
            if (currentGame.getCON().type == RRRA) {
                if (weapon == CROSSBOW_WEAPON) {
                    p.gotweapon[CROSSBOW_WEAPON] = true;
                    p.gotweapon[CHICKENBOW_WEAPON] = true;
                }
                if (weapon == RATE_WEAPON) {
                    p.gotweapon[RATE_WEAPON] = true;
                }
            }
        }

//        if (weapon == DYNAMITE_WEAPON) {
//            p.last_weapon = -1;
//        } #GDX 03.07.2024

        p.random_club_frame = 0;

        if (p.holster_weapon == 0) {
            p.weapon_pos = -1;
            p.last_weapon = p.curr_weapon;
        } else {
            p.weapon_pos = 10;
            p.holster_weapon = 0;
            p.last_weapon = -1;
        }

        p.kickback_pic = 0;
        if (p.last_used_weapon != p.curr_weapon && p.curr_weapon != weapon) {
            p.last_used_weapon = (byte) p.curr_weapon;
        }

        p.curr_weapon = (short) weapon;

        switch (weapon) {
            case SHOTGUN_WEAPON:
                spritesound(169, p.i);
                break;
            case PISTOL_WEAPON:
                spritesound(5, p.i);
                break;
            case KNEE_WEAPON:
            case RATE_WEAPON:
            case DYNAMITE_WEAPON:
            case POWDERKEG_WEAPON:
            case HANDREMOTE_WEAPON:
                break;
            default:
                spritesound(4, p.i);
                break;
        }
    }

    public static void checkweapons(PlayerStruct p) {
        short cw = p.curr_weapon;
        if (cw < 1 || cw >= MAX_WEAPONS) {
            return;
        }

        spawn(p.i, weapon_sprites[cw]);
    }

    public static void shoot(final int i, int atwith) {
        final Sprite s = boardService.getSprite(i);
        if (s == null) {
            return;
        }

        final int sect = s.getSectnum();
        final Sector sec = boardService.getSector(sect);
        int vel, zvel = 0;

        if (sec == null) {
            return;
        }

        int sx, sy, sz, sa;
        int p;

        if (s.getPicnum() == APLAYER) {
            p = s.getYvel();

            sx = ps[p].posx;
            sy = ps[p].posy;
            sz = ps[p].posz + ps[p].pyoff + (4 << 8);
            sa = (short) ps[p].ang;

            ps[p].crack_time = 777;

        } else {
            p = -1;
            sa = s.getAng();
            sx = s.getX();
            sy = s.getY();
            sz = s.getZ() - ((s.getYrepeat() * engine.getTile(s.getPicnum()).getHeight()) << 1);
            sz -= (3 << 8);
            if (badguy(s)) {
                sx += (EngineUtils.sin((sa + 1024 + 96) & 2047) >> 7);
                sy += (EngineUtils.sin((sa + 512 + 96) & 2047) >> 7);
            }

        }

        switch (atwith) {
            case POWDERKEGSPRITE: {
                int j = spawn(i, atwith);
                Sprite sp = boardService.getSprite(j);
                if (sp != null) {
                    sp.setXvel(32);
                    sp.setAng(s.getAng());
                    sp.setZ(sp.getZ() - 1280);
                }
                break;
            }
            case BLOODSPLAT1:
            case BLOODSPLAT2:
            case BLOODSPLAT3:
            case BLOODSPLAT4:

                if (p >= 0) {
                    sa += 64 - (engine.krand() & 127);
                } else {
                    sa += 1024 + 64 - (engine.krand() & 127);
                }
                zvel = 1024 - (engine.krand() & 2047);
            case NEWCROWBAR:
            case BUZSAW:
            case 3510: {
                if (atwith == NEWCROWBAR || atwith == BUZSAW || atwith == 3510) {
                    if (p >= 0) {
                        zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) << 5;
                        sz += (6 << 8);
                        sa += 15;
                    } else {
                        int j = ps[findplayer(s)].i;
                        Sprite psp = boardService.getSprite(j);
                        if (psp == null) {
                            break;
                        }

                        int x = player_dist;
                        zvel = ((psp.getZ() - sz) << 8) / (x + 1);
                        sa = EngineUtils.getAngle(psp.getX() - sx, psp.getY() - sy);
                    }
                }

                engine.hitscan(sx, sy, sz, sect,
                        EngineUtils.cos((sa) & 2047),
                        EngineUtils.sin(sa & 2047), zvel << 6,
                        pHitInfo, CLIPMASK1);

                int hitsect = pHitInfo.hitsect;
                int hitsprite = pHitInfo.hitsprite;
                int hitwall = pHitInfo.hitwall;
                int hitx = pHitInfo.hitx;
                int hity = pHitInfo.hity;
                int hitz = pHitInfo.hitz;

                Sector hitsec = boardService.getSector(hitsect);

                if ((hitsec != null && (hitsec.getLotag() == 160 && zvel > 0 || hitsec.getLotag() == 161 && zvel < 0))
                        && hitsprite == -1
                        && hitwall == -1) {
                    for (int si = 0; si < boardService.getSpriteCount(); si++) {
                        Sprite ss = boardService.getSprite(si);

                        if (ss != null && ss.getSectnum() == hitsect && ss.getPicnum() == 1 && ss.getLotag() == 7) {
                            Sprite spo = boardService.getSprite(ss.getOwner());
                            if (spo != null) {
                                Sector ssec = boardService.getSector(spo.getSectnum());
                                if (ssec != null) {
                                    int z = ssec.getCeilingz();
                                    if (hitsec.getLotag() == 161) {
                                        z = ssec.getFloorz();
                                    }

                                    engine.hitscan(
                                            spo.getX() + ss.getX() + hitx,
                                            spo.getY() + ss.getY() + hity,
                                            z,
                                            spo.getSectnum(),
                                            EngineUtils.cos((sa) & 2047),
                                            EngineUtils.sin(sa & 2047), zvel << 6,
                                            pHitInfo, CLIPMASK1);

                                    hitsect = pHitInfo.hitsect;
                                    hitsprite = pHitInfo.hitsprite;
                                    hitwall = pHitInfo.hitwall;
                                    hitx = pHitInfo.hitx;
                                    hity = pHitInfo.hity;
                                    hitz = pHitInfo.hitz;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (atwith == BLOODSPLAT1 ||
                        atwith == BLOODSPLAT2 ||
                        atwith == BLOODSPLAT3 ||
                        atwith == BLOODSPLAT4) {
                    if (FindDistance2D(sx - hitx, sy - hity) < 1024) {
                        Wall hwal = boardService.getWall(hitwall);
                        if (hwal != null && hwal.getOverpicnum() != BIGFORCE) {
                            hitsec = boardService.getSector(hitsect);
                            Sector nextsec = boardService.getSector(hwal.getNextsector());
                            if ((nextsec != null && hitsec != null &&
                                    hitsec.getLotag() == 0 &&
                                    nextsec.getLotag() == 0 &&
                                    (hitsec.getFloorz() - nextsec.getFloorz()) > (16 << 8)) ||
                                    (nextsec == null && hitsec != null && hitsec.getLotag() == 0)) {
                                if ((hwal.getCstat() & 16) == 0) {
                                    if (nextsec != null) {
                                        for (ListNode<Sprite> node = boardService.getSectNode(hwal.getNextsector()); node != null; node = node.getNext()) {
                                            Sprite spr = node.get();
                                            if (spr.getStatnum() == 3 && spr.getLotag() == 13) {
                                                return;
                                            }
                                        }
                                    }

                                    Wall nextwal = boardService.getWall(hwal.getNextwall());
                                    if (nextwal != null && nextwal.getHitag() != 0) {
                                        return;
                                    }

                                    if (hwal.getHitag() == 0) {
                                        int k = spawn(i, atwith);
                                        Sprite sp = boardService.getSprite(k);
                                        if (sp != null) {
                                            sp.setXvel(-12);
                                            sp.setAng((EngineUtils.getAngle(
                                                    hwal.getX() - hwal.getWall2().getX(),
                                                    hwal.getY() - hwal.getWall2().getY()) + 512));
                                            sp.setX(hitx);
                                            sp.setY(hity);
                                            sp.setZ(hitz);
                                            sp.setCstat(sp.getCstat() | (engine.krand() & 4));
                                            ssp(k, CLIPMASK0);
                                            engine.setsprite(k, sp.getX(), sp.getY(), sp.getZ());
                                            if (s.getPicnum() == OOZFILTER) {
                                                sp.setPal(6);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return;
                }

                if (hitsect == -1) {
                    break;
                }

                if ((klabs(sx - hitx) + klabs(sy - hity)) < 1024) {
                    if (hitwall != -1 || hitsprite != -1) {
                        int j;
                        Sprite sp;
                        if (atwith == 3510) {
                            j = EGS(hitsect, hitx, hity, hitz, 3510, -15, 0, 0, sa, 32, 0, i, (short) 4);
                            sp = boardService.getSprite(j);
                            if (sp != null) {
                                sp.setExtra(sp.getExtra() + 50);
                            }
                        } else {
                            j = EGS(hitsect, hitx, hity, hitz, NEWCROWBAR, -15, 0, 0, sa, 32, 0, i, (short) 4);
                            sp = boardService.getSprite(j);
                            if (sp != null) {
                                sp.setExtra(sp.getExtra() + (engine.krand() & 7));
                            }
                        }

                        if (p >= 0) {
                            int k = spawn(j, SMALLSMOKE);
                            Sprite sp2 = boardService.getSprite(k);
                            if (sp2 != null) {
                                sp2.setZ(sp2.getZ() - (8 << 8));
                            }
                            if (atwith == NEWCROWBAR) {
                                spritesound(KICK_HIT, j);
                            }
                            if (atwith == 3510) {
                                spritesound(260, j);
                            }
                        }

                        if (sp != null && p >= 0 && ps[p].moonshine_amount > 0 && ps[p].moonshine_amount < 400) {
                            sp.setExtra(sp.getExtra() + (currentGame.getCON().max_player_health >> 2));
                        }

                        Sprite hsp = boardService.getSprite(hitsprite);
                        if (hsp != null && hsp.getPicnum() != 129 && hsp.getPicnum() != 82) {
                            checkhitsprite(hitsprite, j);
                            if (p >= 0) {
                                checkhitswitch(p, hitsprite, 1);
                            }
                        } else {
                            Wall hwal = boardService.getWall(hitwall);
                            if (hwal != null) {
                                if ((hwal.getCstat() & 2) != 0) {
                                    Sector nsec = boardService.getSector(hwal.getNextsector());
                                    if (nsec != null && hitz >= (nsec.getFloorz())) {
                                        hitwall = hwal.getNextwall();
                                    }
                                }

                                hwal = boardService.getWall(hitwall);
                                if (hwal != null && hwal.getPicnum() != 129 && hwal.getPicnum() != 82) {
                                    checkhitwall(j, hitwall, hitx, hity, hitz, atwith);
                                    if (p >= 0) {
                                        checkhitswitch(p, hitwall, 0);
                                    }
                                }
                            }
                        }
                    } else {
                        Sector hsec = boardService.getSector(hitsect);
                        if (hsec != null && p >= 0 && zvel > 0 && hsec.getLotag() == 1) {
                            int j = spawn(ps[p].i, WATERSPLASH2);
                            Sprite sp = boardService.getSprite(j);
                            if (sp != null) {
                                sp.setX(hitx);
                                sp.setY(hity);
                                sp.setAng((short) ps[p].ang); // Total tweek
                                sp.setXvel(32);
                                ssp(i, CLIPMASK0);
                                sp.setXvel(0);
                            }
                        }
                    }
                }

                break;
            }
            case SHOTSPARK1:
            case NEWSHOTGUN:
            case RIFLE: {
                if (s.getExtra() >= 0) {
                    s.setShade(-96);
                }

                if (p >= 0) {
                    int j = (short) aim(s, AUTO_AIM_ANGLE);
                    Sprite sp = boardService.getSprite(j);
                    if (sp != null) {
                        int dal = ((sp.getXrepeat() * engine.getTile(sp.getPicnum()).getHeight()) << 1) + (5 << 8);
                        Sprite psp = boardService.getSprite(ps[p].i);
                        if (psp != null) {
                            zvel = ((sp.getZ() - sz - dal) << 8) / ldist(psp, sp);
                            sa = EngineUtils.getAngle(sp.getX() - sx, sp.getY() - sy);
                        }
                    }

                    if (atwith == SHOTSPARK1) {
                        if (j == -1) {
                            sa += 16 - (engine.krand() & 31);
                            zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) << 5;
                            zvel += 128 - (engine.krand() & 255);
                        }
                    } else {
                        if (atwith == NEWSHOTGUN) {
                            sa += 64 - (engine.krand() & 127);
                        } else {
                            sa += 16 - (engine.krand() & 31);
                        }
                        if (j == -1) {
                            zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) << 5;
                        }
                        zvel += 128 - (engine.krand() & 255);
                    }
                    sz -= (2 << 8);
                } else {
                    int j = (short) findplayer(s);
                    sz -= (4 << 8);
                    Sprite psp = boardService.getSprite(ps[j].i);
                    if (psp != null) {
                        zvel = ((ps[j].posz - sz) << 8) / (ldist(psp, s));
                        if (s.getPicnum() != 4477) {
                            zvel += 128 - (engine.krand() & 255);
                            sa += 32 - (engine.krand() & 63);
                        } else {
                            zvel += 128 - (engine.krand() & 255);
                            sa = (short) (EngineUtils.getAngle(ps[j].posx - sx, ps[j].posy - sy) + 64 - (engine.krand() & 127));
                        }
                    }
                }

                s.setCstat(s.getCstat() & ~257);
                engine.hitscan(sx, sy, sz, sect,
                        EngineUtils.sin((sa + 512) & 2047),
                        EngineUtils.sin(sa & 2047),
                        zvel << 6, pHitInfo, CLIPMASK1);

                int hitsect = pHitInfo.hitsect;
                int hitsprite = pHitInfo.hitsprite;
                int hitwall = pHitInfo.hitwall;
                int hitx = pHitInfo.hitx;
                int hity = pHitInfo.hity;
                int hitz = pHitInfo.hitz;

                s.setCstat(s.getCstat() | 257);

                Sector hitsec = boardService.getSector(hitsect);
                if (hitsec == null) {
                    return;
                }

                if (atwith == NEWSHOTGUN && hitsec.getLotag() == 1 && (engine.krand() & 1) == 0) {
                    return;
                }

                if ((engine.krand() & 15) == 0 && hitsec.getLotag() == 2) {
                    tracers(hitx, hity, hitz, sx, sy, sz, 8 - (ud.multimode >> 1));
                }

                final int k;
                if (p >= 0) {
                    k = EGS(hitsect, hitx, hity, hitz, SHOTSPARK1, -15, 10, 10, sa, 0, 0, i, (short) 4);
                    final Sprite sp = boardService.getSprite(k);
                    if (sp == null) {
                        break;
                    }

                    sp.setExtra(currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]]);
                    sp.setExtra(sp.getExtra() + (engine.krand() % 6));

                    if (hitwall == -1 && hitsprite == -1) {
                        if (zvel < 0) {
                            if ((hitsec.getCeilingstat() & 1) != 0) {
                                sp.setXrepeat(0);
                                sp.setYrepeat(0);
                                return;
                            } else {
                                checkhitceiling(hitsect);
                            }
                        }
                        if (hitsec.getLotag() != 1) {
                            spawn(k, SMALLSMOKE);
                        }
                    }

                    Sprite hsp = boardService.getSprite(hitsprite);
                    if (hsp != null) {
                        if (hsp.getPicnum() == TORNADO) {
                            return;
                        }
                        checkhitsprite(hitsprite, k);
                        if (hsp.getPicnum() == APLAYER && (ud.coop != 1 || ud.ffire == 1)) {
                            sp.setXrepeat(0);
                            sp.setYrepeat(0);
                            int l = spawn(k, JIBS6);
                            Sprite sp2 = boardService.getSprite(l);
                            if (sp2 != null) {
                                sp2.setZ(sp2.getZ() + (4 << 8));
                                sp2.setXvel(16);
                                sp2.setXrepeat(24);
                                sp2.setYrepeat(24);
                                sp2.setAng(sp2.getAng() + 64 - (engine.krand() & 127));
                            }
                        } else {
                            spawn(k, SMALLSMOKE);
                        }

                        if (hsp.getPicnum() == 121 || hsp.getPicnum() == 122 || hsp.getPicnum() == 123 || hsp.getPicnum() == 124 || hsp.getPicnum() == 127 || hsp.getPicnum() == 128 || hsp.getPicnum() == 2249 || hsp.getPicnum() == 8660 || hsp.getPicnum() == 2250) {
                            checkhitswitch(p, hitsprite, 1);
                            return;
                        }
                    } else {
                        Wall hwal = boardService.getWall(hitwall);
                        if (hwal != null) {
                            spawn(k, SMALLSMOKE);

                            SKIPBULLETHOLE:
                            do {
                                if (isadoorwall(hwal.getPicnum()) && isadoorwall2(hwal.getPicnum())) {
                                    break; // goto SKIPBULLETHOLE;
                                }
                                if (hwal.getPicnum() == 121 || hwal.getPicnum() == 122 || hwal.getPicnum() == 123 || hwal.getPicnum() == 124 || hwal.getPicnum() == 127 || hwal.getPicnum() == 128 || hwal.getPicnum() == 2249 || hwal.getPicnum() == 8660 || hwal.getPicnum() == 2250) {
                                    checkhitswitch(p, hitwall, 0);
                                    return;
                                }

                                Wall nextwal = boardService.getWall(hwal.getNextwall());
                                if (hwal.getHitag() != 0 || (nextwal != null && nextwal.getHitag() != 0)) {
                                    break; // goto SKIPBULLETHOLE;
                                }

                                if (hitsec.getLotag() == 0) {
                                    if (hwal.getOverpicnum() != BIGFORCE) {
                                        Sector nextsec = boardService.getSector(hwal.getNextsector());
                                        if ((nextsec != null && nextsec.getLotag() == 0) ||
                                                (hwal.getNextsector() == -1 && hitsec.getLotag() == 0)) {
                                            if ((hwal.getCstat() & 16) == 0) {
                                                if (nextsec != null) {
                                                    for (ListNode<Sprite> node = boardService.getSectNode(hwal.getNextsector()); node != null; node = node.getNext()) {
                                                        Sprite spj = node.get();
                                                        if (spj.getStatnum() == 3 && spj.getLotag() == 13) {
                                                            break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;
                                                        }
                                                    }
                                                }

                                                for (ListNode<Sprite> node = boardService.getStatNode(5); node != null; node = node.getNext()) {
                                                    Sprite spj = node.get();
                                                    if (spj.getPicnum() == BULLETHOLE) {
                                                        if (dist(spj, sp) < (12 + (engine.krand() & 7))) {
                                                            break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;
                                                        }
                                                    }
                                                }

                                                int l = spawn(k, BULLETHOLE);
                                                Sprite hole = boardService.getSprite(l);
                                                if (hole != null) {
                                                    hole.setXvel(-1);
                                                    hole.setAng((EngineUtils.getAngle(
                                                            hwal.getX() - hwal.getWall2().getX(),
                                                            hwal.getY() - hwal.getWall2().getY()) + 512));
                                                    ssp(l, CLIPMASK0);
                                                }
                                            }
                                        }
                                    }
                                }
                            } while (false);

                            if ((hwal.getCstat() & 2) != 0) {
                                Sector nsec = boardService.getSector(hwal.getNextsector());
                                if (nsec != null) {
                                    if (hitz >= (nsec.getFloorz())) {
                                        hitwall = hwal.getNextwall();
                                    }
                                }
                            }

                            checkhitwall(k, hitwall, hitx, hity, hitz, SHOTSPARK1);
                        }
                    }
                } else {
                    k = EGS(hitsect, hitx, hity, hitz, SHOTSPARK1, -15, 24, 24, sa, 0, 0, i, (short) 4);
                    Sprite sp = boardService.getSprite(k);
                    if (sp != null) {
                        sp.setExtra(currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]]);

                        Sprite hsp = boardService.getSprite(hitsprite);
                        if (hsp != null) {
                            checkhitsprite(hitsprite, k);
                            if (hsp.getPicnum() != APLAYER) {
                                spawn(k, SMALLSMOKE);
                            } else {
                                sp.setXrepeat(0);
                                sp.setYrepeat(0);
                            }
                        } else if (hitwall != -1) {
                            checkhitwall(k, hitwall, hitx, hity, hitz, SHOTSPARK1);
                        }
                    }
                }

                if ((engine.krand() & 255) < 4) {
                    xyzsound(PISTOL_RICOCHET, k, hitx, hity, hitz);
                }

                return;
            }
            case OWHIP:
            case UWHIP: {

                if (s.getExtra() >= 0) {
                    s.setShade(-96);
                }

                int scount = 1;
                if (atwith == OWHIP) {
                    sz -= 3840;
                } else {
                    sz += 1024;
                }
                vel = 300;

                if (p >= 0) {
                    int j = aim(s, AUTO_AIM_ANGLE);
                    Sprite sp = boardService.getSprite(j);

                    sx += EngineUtils.sin((s.getAng() + 672) & 2047) >> 6;
                    sy += EngineUtils.sin((s.getAng() + 160) & 2047) >> 6;
                    if (sp != null) {
                        int dal = ((sp.getXrepeat() * engine.getTile(sp.getPicnum()).getHeight()) << 1) - (12 << 8);
                        Sprite psp = boardService.getSprite(ps[p].i);
                        if (psp != null) {
                            zvel = ((sp.getZ() - sz - dal) * vel) / ldist(psp, sp);
                        }
                        sa = EngineUtils.getAngle(sp.getX() - sx, sp.getY() - sy);
                    } else {
                        zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) * 98;
                    }
                } else {
                    int j = (short) findplayer(s);
                    Sprite psp = boardService.getSprite(ps[j].i);
                    if (psp != null) {
                        if (s.getPicnum() == VIXEN) {
                            sa -= engine.krand() & 16;
                        } else if (s.getPicnum() != UFOBEAM) {
                            sa += 16 - (engine.krand() & 31);
                        }
                        zvel = (((ps[j].oposz - sz + (3 << 8))) * vel) / ldist(psp, s);
                    }
                }

                int oldzvel = zvel;
                int sizx, sizy;
                if (p >= 0) {
                    sizx = 7;
                    sizy = 7;
                } else {
                    sizx = 8;
                    sizy = 8;
                }

                while (scount > 0) {
                    final int j = EGS(sect, sx, sy, sz, atwith, -127, sizx, sizy, sa, vel, zvel, i, (short) 4);
                    Sprite sp = boardService.getSprite(j);
                    if (sp != null) {
                        sp.setExtra(sp.getExtra() + (engine.krand() & 7));
                        sp.setCstat(128);
                        sp.setClipdist(4);

                        sa = (short) (s.getAng() + 32 - (engine.krand() & 63));
                        zvel = oldzvel + 512 - (engine.krand() & 1023);
                    }

                    scount--;
                }

                return;
            }
            case FIRELASER:
            case SHITBALL:
            case DILDO: {

                if (s.getExtra() >= 0) {
                    s.setShade(-96);
                }

                int scount = 1;
                if (atwith == SHITBALL) {
                    vel = 400;
                } else {
                    vel = 840;
                    sz -= (4 << 7);

                    if (s.getPicnum() == HULK) {
                        sx += EngineUtils.sin((sa + 768) & 2047) >> 6;
                        sy += EngineUtils.sin((sa + 256) & 2047) >> 6;
                        sz += 3072;
                    }
                    if (s.getPicnum() == VIXEN) {
                        sz -= 3072;
                    }
                }

                if (p >= 0) {
                    int j = aim(s, AUTO_AIM_ANGLE);
                    sx += EngineUtils.sin((s.getAng() + 672) & 2047) >> 6;
                    sy += EngineUtils.sin((s.getAng() + 160) & 2047) >> 6;
                    Sprite sp = boardService.getSprite(j);
                    if (sp != null) {
                        int dal = ((sp.getXrepeat() * engine.getTile(sp.getPicnum()).getHeight()) << 1) - (12 << 8);
                        Sprite psp = boardService.getSprite(ps[p].i);
                        if (psp != null) {
                            zvel = ((sp.getZ() - sz - dal) * vel) / ldist(psp, sp);
                            sa = EngineUtils.getAngle(sp.getX() - sx, sp.getY() - sy);
                        }
                    } else {
                        zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) * 98;
                    }
                } else {
                    int j = (short) findplayer(s);

                    if (s.getPicnum() == HULK) {
                        sa -= engine.krand() & 31;
                    } else if (s.getPicnum() == VIXEN) {
                        sa -= engine.krand() & 16;
                    } else if (s.getPicnum() != UFOBEAM) {
                        sa += 16 - (engine.krand() & 31);
                    }

                    Sprite psp = boardService.getSprite(ps[j].i);
                    if (psp != null) {
                        zvel = (((ps[j].oposz - sz + (3 << 8))) * vel) / ldist(psp, s);
                    }
                }

                int oldzvel = zvel;

                int sizx, sizy;
                if (atwith == SHITBALL) {
                    sizx = 18;
                    sizy = 18;
                    if (s.getPicnum() == 8705) {
                        sz -= (20 << 8);
                    } else {
                        sz -= (10 << 8);
                    }
                } else {
                    if (atwith == DILDO) {
                        sizx = 8;
                        sizy = 8;
                    } else {
                        if (p >= 0) {
                            sizx = 34;
                            sizy = 34;
                        } else {
                            sizx = 18;
                            sizy = 18;
                        }
                    }
                }

                if (p >= 0) {
                    sizx = 7;
                    sizy = 7;
                }

                while (scount > 0) {
                    final int j = EGS(sect, sx, sy, sz, atwith, -127, sizx, sizy, sa, vel, zvel, i, (short) 4);
                    Sprite sp = boardService.getSprite(j);

                    if (sp != null) {
                        sp.setExtra(sp.getExtra() + (engine.krand() & 7));

                        if (atwith == FIRELASER) {
                            sp.setXrepeat(8);
                            sp.setYrepeat(8);
                        }

                        sp.setCstat(128);
                        sp.setClipdist(4);

                        sa = (short) (s.getAng() + 32 - (engine.krand() & 63));
                        zvel = oldzvel + 512 - (engine.krand() & 1023);
                    }

                    scount--;
                }

                return;
            }
            case ALIENBLAST:
                sz += (3 << 8);
            case CROSSBOW:
            case CIRCLESAW:
            case CHIKENCROSSBOW:
            case 1790: { // RPG
                if (s.getExtra() >= 0) {
                    s.setShade(-96);
                }

                vel = 644;

                int j;
                int id = 0;
                if (p >= 0) {
                    j = aim(s, 48);
                    Sprite sp = boardService.getSprite(j);
                    if (sp != null) {
                        id = j;
                        if (atwith == CHIKENCROSSBOW) {
                            if (sp.getPicnum() == 4861 && sp.getPicnum() == 4862) {
                                id = ps[myconnectindex].i;
                            }
                        }
                        int dal = ((sp.getXrepeat() * engine.getTile(sp.getPicnum()).getHeight()) << 1) + (8 << 8);
                        Sprite psp = boardService.getSprite(ps[p].i);
                        if (psp != null) {
                            zvel = ((sp.getZ() - sz - dal) * vel) / ldist(psp, sp);
                            if (sp.getPicnum() != 4989) {
                                sa = EngineUtils.getAngle(sp.getX() - sx, sp.getY() - sy);
                            }
                        }
                    } else {
                        zvel = (100 - (int) ps[p].horiz - ps[p].horizoff) * 81;
                    }

                    switch (atwith) {
                        case CROSSBOW:
                            spritesound(RPG_SHOOT, i);
                            break;
                        case CHIKENCROSSBOW:
                            spritesound(244, i);
                            break;
                        case 1790:
                            spritesound(94, i);
                            break;
                    }
                } else {
                    j = (short) findplayer(s);

                    sa = EngineUtils.getAngle(ps[j].oposx - sx, ps[j].oposy - sy);
                    if (s.getPicnum() == 4607) {
                        sz -= (32 << 8);
                    } else if (s.getPicnum() == 4557) {
                        vel = 772;
                        sz += 24 << 8;
                    }
                    Sprite psp = boardService.getSprite(ps[j].i);
                    if (psp != null) {
                        int l = (short) ldist(psp, s);
                        zvel = ((ps[j].oposz - sz) * vel) / l;
                        if (badguy(s) && (s.getHitag() & face_player_smart) != 0) {
                            sa = (short) (s.getAng() + (engine.krand() & 31) - 16);
                        }
                    }
                }

                int l = -1;
                if (p >= 0 && j >= 0) {
                    l = j;
                }

                if (atwith == 1790) {
                    zvel = -2560;
                    vel *= 2;
                }

                j = EGS(sect,
                        sx + (EngineUtils.sin((348 + sa + 512) & 2047) / 448),
                        sy + (EngineUtils.sin((sa + 348) & 2047) / 448),
                        sz - (1 << 8), atwith, 0, 14, 14, sa, vel, zvel, i, (short) 4);

                Sprite sp = boardService.getSprite(j);
                if (sp != null) {
                    if (atwith == 1790) {
                        sp.setExtra(10);
                        sp.setZvel(-2560);
                    } else if (atwith == CHIKENCROSSBOW) {
                        sp.setLotag(id);
                        sp.setHitag(0);
                        lotsofmoney(sp, (engine.krand() & 3) + 1);
                    }

                    sp.setExtra(sp.getExtra() + (engine.krand() & 7));
                    if (atwith != ALIENBLAST) {
                        sp.setYvel(l);
                    } else {
                        sp.setYvel((short) currentGame.getCON().numfreezebounces);
                        sp.setXrepeat(sp.getXrepeat() >> 1);
                        sp.setYrepeat(sp.getYrepeat() >> 1);
                        sp.setZvel(sp.getZvel() - (2 << 4));
                    }

                    if (p == -1) {
                        if (s.getPicnum() == 4649) {
                            sp.setXrepeat(8);
                            sp.setYrepeat(8);
                        } else if (atwith != ALIENBLAST) {
                            sp.setXrepeat(30);
                            sp.setYrepeat(30);
                            sp.setExtra(sp.getExtra() >> 2);
                        }
                    } else if (ps[p].curr_weapon == TIT_WEAPON) {
                        sp.setExtra(sp.getExtra() >> 2);
                        sp.setAng(sp.getAng() + 16 - (engine.krand() & 31));
                        sp.setZvel(sp.getZvel() + 256 - (engine.krand() & 511));

                        if ((ps[p].hbomb_hold_delay) != 0) {
                            sp.setX(sp.getX() - EngineUtils.sin(sa & 2047) / 644);
                            sp.setY(sp.getY() - EngineUtils.sin((sa + 1024 + 512) & 2047) / 644);
                        } else {
                            sp.setX(sp.getX() + (EngineUtils.sin(sa & 2047) >> 8));
                            sp.setY(sp.getY() + (EngineUtils.sin((sa + 1024 + 512) & 2047) >> 8));
                        }
                        sp.setXrepeat(sp.getXrepeat() >> 1);
                        sp.setYrepeat(sp.getYrepeat() >> 1);
                    }

                    sp.setCstat(128);
                    if (atwith == CROSSBOW) {
                        sp.setClipdist(4);
                    } else if (atwith == CHIKENCROSSBOW) {
                        sp.setClipdist(4);
                    } else {
                        sp.setClipdist(40);
                    }
                }

                break;
            }
            case BOUNCEMINE:
            case MORTER:
            case 3464: {

                if (s.getExtra() >= 0) {
                    s.setShade(-96);
                }

                int j = ps[findplayer(s)].i;
                Sprite psp = boardService.getSprite(j);
                if (psp == null) {
                    break;
                }

                int x = ldist(psp, s);

                zvel = -x >> 1;

                if (zvel < -4096) {
                    zvel = -2048;
                }
                vel = x >> 4;

                int sizex = 32;
                int sizey = 32;
                if (atwith == 3464) {
                    sizex = 16;
                    sizey = 16;
                }

                EGS(sect,
                        sx + (EngineUtils.cos(sa + 512) >> 8),
                        sy + (EngineUtils.sin(sa + 512) >> 8),
                        sz + (6 << 8), atwith, -64, sizex, sizey, sa, vel, zvel, i, (short) 1);
                break;
            }
            case BOWLINGBALL: {
                int k = spawn(i, atwith);
                Sprite sp = boardService.getSprite(k);
                if (sp != null) {
                    sp.setXvel(250);
                    sp.setAng(s.getAng());
                    sp.setZ(sp.getZ() - 3840);
                }
                break;
            }
        }
    }

    public static void moveweapons() {
        RRRenderer renderer = game.getRenderer();
        for (ListNode<Sprite> node = boardService.getStatNode(4), nexti; node != null; node = nexti) {
            nexti = node.getNext();
            final Sprite s = node.get();
            final int i = node.getIndex();

            if (s.getSectnum() < 0) {
                engine.deletesprite(i);
                continue;
            }

            game.pInt.setsprinterpolate(i, s);

            switch (s.getPicnum()) {
                case RADIUSEXPLOSION:
                    engine.deletesprite(i);
                    continue;
                case TONGUE: {
                    hittype[i].temp_data[0] = EngineUtils.sin((hittype[i].temp_data[1]) & 2047) >> 9;
                    hittype[i].temp_data[1] += 32;
                    if (hittype[i].temp_data[1] > 2047) {
                        engine.deletesprite(i);
                        continue;
                    }

                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo == null) {
                        continue;
                    }

                    if (spo.getStatnum() == MAXSTATUS) {
                        if (!badguy(spo)) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }

                    s.setAng(spo.getAng());
                    s.setX(spo.getX());
                    s.setY(spo.getY());
                    if (spo.getPicnum() == APLAYER) {
                        s.setZ(spo.getZ() - (34 << 8));
                    }
                    int k;
                    for (k = 0; k < hittype[i].temp_data[0]; k++) {
                        int q = EGS(s.getSectnum(),
                                s.getX() + ((k * EngineUtils.sin((s.getAng() + 512) & 2047)) >> 9),
                                s.getY() + ((k * EngineUtils.sin(s.getAng() & 2047)) >> 9),
                                s.getZ() + ((k * ksgn(s.getZvel())) * klabs(s.getZvel() / 12)),
                                TONGUE, -40 + (k << 1), 8, 8, 0, 0, 0, i, (short) 5);
                        Sprite sp = boardService.getSprite(q);
                        if (sp != null) {
                            sp.setCstat(128);
                            sp.setPal(8);
                        }
                    }
                    int q = EGS(s.getSectnum(),
                            s.getX() + ((k * EngineUtils.cos((s.getAng()) & 2047)) >> 9),
                            s.getY() + ((k * EngineUtils.sin(s.getAng() & 2047)) >> 9),
                            s.getZ() + ((k * ksgn(s.getZvel())) * klabs(s.getZvel() / 12)),
                            INNERJAW, -40, 32, 32, 0, 0, 0, i, (short) 5);
                    Sprite sp = boardService.getSprite(q);
                    if (sp != null) {
                        sp.setCstat(128);
                        if (hittype[i].temp_data[1] > 512 && hittype[i].temp_data[1] < (1024)) {
                            sp.setPicnum(INNERJAW + 1);
                        }
                    }
                    continue;
                }
                case ALIENBLAST:
                    if (s.getYvel() < 1 || s.getExtra() < 2 || (s.getXvel() | s.getZvel()) == 0) {
                        int j = spawn(i, TRANSPORTERSTAR);
                        Sprite sp = boardService.getSprite(j);
                        if (sp != null) {
                            sp.setPal(1);
                            sp.setXrepeat(32);
                            sp.setYrepeat(32);
                            engine.deletesprite(i);
                        }
                        continue;
                    }
                case CROSSBOW:
                case CHIKENCROSSBOW:
                case FIRELASER:
                case SHITBALL:
                case CIRCLESAW:
                case UWHIP:
                case OWHIP:
                case DILDO:
                case 1790: {
                    int p;
                    int xvel, yvel;

                    Sector sec = boardService.getSector(s.getSectnum());
                    if (sec != null && s.getPicnum() == CROSSBOW && sec.getLotag() == 2) {
                        xvel = s.getXvel() >> 1;
                        yvel = s.getZvel() >> 1;
                    } else {
                        xvel = s.getXvel();
                        yvel = s.getZvel();
                    }

                    int dax = s.getX();
                    int day = s.getY();
                    int daz = s.getZ();

                    getglobalz(i);
                    sec = boardService.getSector(s.getSectnum());

                    int qq = CLIPMASK1;
                    switch (s.getPicnum()) {
                        case CROSSBOW:
                            if (sec != null && hittype[i].picnum != 4557 && s.getXrepeat() >= 10
                                    && sec.getLotag() != 2) {
                                int j = spawn(i, SMALLSMOKE);
                                Sprite sp = boardService.getSprite(j);
                                if (sp != null) {
                                    sp.setZ(sp.getZ() + (1 << 8));
                                }
                            }
                            break;
                        case CHIKENCROSSBOW:
                            s.setHitag(s.getHitag() + 1);
                            if (sec != null && hittype[i].picnum != 4557 && s.getXrepeat() >= 10 && sec.getLotag() != 2) {
                                int j = spawn(i, SMALLSMOKE);
                                Sprite sp = boardService.getSprite(j);
                                if (sp != null) {
                                    sp.setZ(sp.getZ() + (1 << 8));
                                    if ((engine.krand() & 0xF) == 2) {
                                        spawn(i, FEATHERS);
                                    }
                                }
                            }

                            Sprite lsp = boardService.getSprite(s.getLotag());
                            if (lsp != null) {
                                if (lsp.getExtra() <= 0) {
                                    s.setLotag(0);
                                }

                                if (s.getLotag() != 0 && s.getHitag() > 5) {
                                    int ang = EngineUtils.getAngle(lsp.getX() - s.getX(), lsp.getY() - s.getY());
                                    int seekang = ang - s.getAng();
                                    if (seekang >= 100) {
                                        if (seekang == 100) {
                                            s.setAng(ang);
                                        }
                                        if (klabs(seekang) <= 1023) {
                                            s.setAng(s.getAng() + 51);
                                        } else {
                                            s.setAng(s.getAng() - 51);
                                        }
                                    } else if (klabs(seekang) <= 1023) {
                                        s.setAng(s.getAng() - 51);
                                    } else {
                                        s.setAng(s.getAng() + 51);
                                    }

                                    if (s.getHitag() > 180 && s.getZvel() <= 0) {
                                        s.setZvel(s.getZvel() + 200);
                                    }
                                }
                            }
                            break;
                        case 1790:
                            if (s.getExtra() != 0) {
                                s.setZvel((short) (250 * s.getExtra()));
                                s.setZvel((short) -s.getZvel());
                                s.setExtra(s.getExtra() - 1);
                            } else {
                                makeitfall(currentGame.getCON(), i);
                            }

                            if (sec != null && s.getXrepeat() >= 10 && sec.getLotag() != 2) {
                                int j = spawn(i, SMALLSMOKE);
                                Sprite sp = boardService.getSprite(j);
                                if (sp != null) {
                                    sp.setZ(sp.getZ() + 256);
                                }
                            }
                            break;
                    }

                    int moveHit = movesprite(i, (xvel * (EngineUtils.sin((s.getAng() + 512) & 2047))) >> 14,
                            (xvel * (EngineUtils.sin(s.getAng() & 2047))) >> 14, yvel, qq);

                    Sprite ysp = boardService.getSprite(s.getYvel());
                    if (ysp != null) {
                        switch (s.getPicnum()) {
                            case CROSSBOW:
                            case CHIKENCROSSBOW:
                            case 1790:
                                if (FindDistance2D(s.getX() - ysp.getX(), s.getY()
                                        - ysp.getY()) < 256) {
                                    moveHit = kHitSprite + s.getYvel();
                                }
                                break;
                        }
                    }

                    sec = boardService.getSector(s.getSectnum());
                    if (sec == null) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if (sec.getFiller() == 800) {
                        engine.deletesprite(i);
                        continue;
                    } else if ((moveHit & kHitTypeMask) != kHitSprite) {
                        if (s.getPicnum() != ALIENBLAST) {
                            if (s.getZ() < hittype[i].ceilingz) {
                                moveHit = kHitSector + (s.getSectnum());
                                s.setZvel(-1);
                            } else if ((s.getZ() > hittype[i].floorz && sec.getLotag() != 1)
                                    || (s.getZ() > hittype[i].floorz + (16 << 8) && sec.getLotag() == 1)) {
                                moveHit = kHitSector + (s.getSectnum());
                                if (sec.getLotag() != 1) {
                                    s.setZvel(1);
                                }
                            }
                        }
                    }

                    if (s.getPicnum() == FIRELASER) {
                        int shade = -52;
                        for (int k = -3; k < 2; k++) {
                            int x = EGS(s.getSectnum(),
                                    s.getX()
                                            + ((k * EngineUtils.sin((s.getAng() + 512) & 2047)) >> 9),
                                    s.getY() + ((k * EngineUtils.sin(s.getAng() & 2047)) >> 9),
                                    s.getZ() + ((k * ksgn(s.getZvel())) * klabs(s.getZvel() / 24)),
                                    FIRELASER, shade, s.getXrepeat(), s.getYrepeat(), 0, 0,
                                    0, s.getOwner(), (short) 5);

                            Sprite sp = boardService.getSprite(x);
                            if (sp != null) {
                                sp.setCstat(128);
                                sp.setPal(s.getPal());
                                shade += 4;
                            }
                        }
                    } else if (s.getPicnum() == SHITBALL) {
                        if (s.getZvel() < 6144) {
                            s.setZvel(s.getZvel() + currentGame.getCON().gc - 112);
                        }
                    }

                    if (moveHit != 0) {
                        if ((moveHit & kHitTypeMask) == kHitSprite) {
                            moveHit &= kHitIndexMask;

                            Sprite hsp = boardService.getSprite(moveHit);
                            if (hsp != null) {
                                if (currentGame.getCON().type == RRRA) {
                                    if (hsp.getPicnum() == MINION
                                            && (s.getPicnum() == CROSSBOW || s.getPicnum() == CHIKENCROSSBOW)
                                            && hsp.getPal() == 19) {
                                        spritesound(9, i);
                                        Sprite sp =  boardService.getSprite(spawn(i, EXPLOSION2));
                                        if (sp != null) {
                                            sp.setX(s.getX());
                                            sp.setY(s.getY());
                                            sp.setZ(s.getZ());
                                        }
                                        continue;
                                    }
                                } else {
                                    if (s.getPicnum() == ALIENBLAST && hsp.getPal() == 1) {
                                        if (badguy(hsp) || hsp.getPicnum() == APLAYER) {
                                            Sprite sp =  boardService.getSprite(spawn(i, TRANSPORTERSTAR));
                                            if (sp != null) {
                                                sp.setPal(1);
                                                sp.setXrepeat(32);
                                                sp.setYrepeat(32);
                                            }

                                            engine.deletesprite(i);
                                            continue;
                                        }
                                    }
                                }

                                Sprite spo = boardService.getSprite(s.getOwner());
                                checkhitsprite((short) moveHit, i);

                                if (hsp.getPicnum() == APLAYER) {
                                    p = hsp.getYvel();
                                    spritesound(PISTOL_BODYHIT, moveHit);

                                    if (s.getPicnum() == SHITBALL) {
                                        if (spo != null && spo.getPicnum() == MAMAJACKOLOPE) {
                                            guts(s, 7387, 2, myconnectindex);
                                            guts(s, 7392, 2, myconnectindex);
                                            guts(s, 7397, 2, myconnectindex);
                                        }
                                        ps[p].horiz += 32;
                                        ps[p].return_to_center = 8;

                                        if (ps[p].loogcnt == 0) {
                                            if (Sound[DUKE_LONGTERM_PAIN].getSoundOwnerCount() < 1) {
                                                spritesound(DUKE_LONGTERM_PAIN, ps[p].i);
                                            }

                                            int j = 3 + (engine.krand() & 3);
                                            ps[p].numloogs = j;
                                            ps[p].loogcnt = 24 * 4;
                                            int xdim = renderer.getWidth();
                                            int ydim = renderer.getHeight();

                                            for (int x = 0; x < j; x++) {
                                                int lx = engine.krand() % xdim;
                                                int ly = engine.krand() % ydim;
                                                if (p == screenpeek) {
                                                    loogiex[x] = lx;
                                                    loogiey[x] = ly;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if ((moveHit & kHitTypeMask) == kHitWall) {
                            moveHit &= kHitIndexMask;
                            Wall hwal = boardService.getWall(moveHit);

                            if (hwal != null) {
                                Sprite spo = boardService.getSprite(s.getOwner());

                                if (spo != null && spo.getPicnum() == MAMAJACKOLOPE) {
                                    guts(s, 7387, 2, myconnectindex);
                                    guts(s, 7392, 2, myconnectindex);
                                    guts(s, 7397, 2, myconnectindex);
                                }


                                if (s.getPicnum() != CROSSBOW
                                        && s.getPicnum() != CHIKENCROSSBOW
                                        && s.getPicnum() != ALIENBLAST
                                        && s.getPicnum() != SHITBALL
                                        && s.getPicnum() != CIRCLESAW
                                        && (hwal.getOverpicnum() == MIRROR || hwal.getPicnum() == MIRROR)) {
                                    int k = hwal.getWallAngle();
                                    s.setAng((short) (((k << 1) - s.getAng()) & 2047));
                                    s.setOwner(i);
                                    spawn(i, TRANSPORTERSTAR);
                                    continue;
                                } else {
                                    engine.setsprite(i, dax, day, daz);
                                    checkhitwall(i, moveHit, s.getX(), s.getY(), s.getZ(), s.getPicnum());

                                    if (currentGame.getCON().type != RRRA) {
                                        if (s.getPicnum() == ALIENBLAST) {
                                            if (hwal.getOverpicnum() != MIRROR
                                                    && hwal.getPicnum() != MIRROR) {
                                                s.setExtra(s.getExtra() >> 1);
                                                s.setYvel(s.getYvel() - 1);
                                                if (s.getXrepeat() > 8) {
                                                    s.setXrepeat(s.getXrepeat() - 2);
                                                }
                                                if (s.getYrepeat() > 8) {
                                                    s.setYrepeat(s.getYrepeat() - 2);
                                                }
                                            }

                                            int k = hwal.getWallAngle();
                                            s.setAng((short) (((k << 1) - s.getAng()) & 2047));
                                            continue;
                                        }
                                    }

                                    if (s.getPicnum() == CIRCLESAW) {
                                        if (hwal.getPicnum() < 3643 || hwal.getPicnum() >= 3646) {
                                            if (s.getExtra() > 0) {
                                                if (hwal.getOverpicnum() != MIRROR && hwal.getPicnum() != MIRROR) {
                                                    s.setExtra(s.getExtra() - 20);
                                                    s.setYvel(s.getYvel() - 1);
                                                }
                                                int k = hwal.getWallAngle();
                                                s.setAng((short) (((k << 1) - s.getAng()) & 2047));
                                            } else {
                                                s.setX(s.getX() + (EngineUtils.sin((s.getAng() + 512) & 2047) >> 7));
                                                s.setY(s.getY() + (EngineUtils.sin((s.getAng()) & 2047) >> 7));
                                                if (spo != null && spo.getPicnum() != DAISYMAE && spo.getPicnum() != DAISYMAE + 1) {
                                                    int nspawn = spawn(i, CIRCLESTUCK);
                                                    Sprite sp = boardService.getSprite(nspawn);
                                                    if (sp != null) {
                                                        sp.setXrepeat(8);
                                                        sp.setYrepeat(8);
                                                        sp.setCstat(16);
                                                        sp.setAng((short) ((s.getAng() + 512) & 0x7FF));
                                                        sp.setClipdist(mulscale(engine.getTile(s.getPicnum()).getWidth(), s.getXrepeat(), 7));
                                                    }
                                                }
                                                engine.deletesprite(i);
                                            }
                                        } else {
                                            engine.deletesprite(i);
                                        }
                                        continue;
                                    }
                                }
                            }
                        } else if ((moveHit & kHitTypeMask) == kHitSector) {
                            engine.setsprite(i, dax, day, daz);
                            Sprite spo = boardService.getSprite(s.getOwner());
                            if (spo != null && spo.getPicnum() == MAMAJACKOLOPE) {
                                guts(s, 7387, 2, myconnectindex);
                                guts(s, 7392, 2, myconnectindex);
                                guts(s, 7397, 2, myconnectindex);
                            }

                            if (s.getZvel() < 0) {
                                sec = boardService.getSector(s.getSectnum());
                                if (sec != null && (sec.getCeilingstat() & 1) != 0) {
                                    if (sec.getCeilingpal() == 0) {
                                        engine.deletesprite(i);
                                        continue;
                                    }
                                }

                                checkhitceiling(s.getSectnum());
                            }

                            if (s.getPicnum() == ALIENBLAST) {
                                bounce(i);
                                ssp(i, qq);
                                s.setExtra(s.getExtra() >> 1);
                                if (s.getXrepeat() > 8) {
                                    s.setXrepeat(s.getXrepeat() - 2);
                                }
                                if (s.getYrepeat() > 8) {
                                    s.setYrepeat(s.getYrepeat() - 2);
                                }
                                s.setYvel(s.getYvel() - 1);
                                continue;
                            }
                        }

                        if (s.getPicnum() != SHITBALL) {
                            switch (s.getPicnum()) {
                                case CROSSBOW:
                                case CHIKENCROSSBOW:
                                case 1790: {
                                    if (s.getPicnum() == 1790) {
                                        s.setExtra(160);
                                    }
                                    int k = spawn(i, EXPLOSION2);
                                    Sprite sp = boardService.getSprite(k);
                                    if (sp != null) {
                                        sp.setX(dax);
                                        sp.setY(day);
                                        sp.setZ(daz);

                                        if (s.getXrepeat() < 10) {
                                            sp.setXrepeat(6);
                                            sp.setYrepeat(6);
                                        } else if ((moveHit & kHitTypeMask) == kHitSector) {
                                            sp.setCstat(sp.getCstat() | 8);
                                            sp.setZ(sp.getZ() + (48 << 8));
                                        }
                                    }
                                    break;
                                }
                                default:
                                    if (s.getPicnum() != CIRCLESAW
                                            && s.getPicnum() != ALIENBLAST
                                            && s.getPicnum() != FIRELASER) {
                                        int k = spawn(i, EXPLOSION2);
                                        Sprite sp = boardService.getSprite(k);
                                        if (sp != null) {
                                            int siz = (s.getXrepeat() >> 1);
                                            sp.setXrepeat(siz);
                                            sp.setYrepeat(siz);
                                            if ((moveHit & kHitTypeMask) == kHitSector) {
                                                if (s.getZvel() < 0) {
                                                    sp.setCstat(sp.getCstat() | 8);
                                                    sp.setZ(sp.getZ() + (72 << 8));
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }

                            switch (s.getPicnum()) {
                                case CROSSBOW:
                                case CHIKENCROSSBOW:
                                case 1790:
                                    if (s.getPicnum() != CHIKENCROSSBOW) {
                                        spritesound(RPG_EXPLODE, i);
                                    } else {
                                        spritesound(247, i);
                                    }
                                    if (s.getPicnum() == CHIKENCROSSBOW) {
                                        s.setExtra(150);
                                    }
                                    if (s.getPicnum() == 1790) {
                                        s.setExtra(160);
                                    }

                                    if (s.getXrepeat() >= 10) {
                                        int x = s.getExtra();
                                        hitradius(i, currentGame.getCON().crossbowblastradius, x >> 2, x >> 1, x
                                                - (x >> 2), x);
                                    } else {
                                        int x = s.getExtra() + (global_random & 3);
                                        hitradius(i, (currentGame.getCON().crossbowblastradius >> 1), x >> 2,
                                                x >> 1, x - (x >> 2), x);
                                    }
                                    break;
                            }
                        }

                        engine.deletesprite(i);
                        continue;

                    }

                    sec = boardService.getSector(s.getSectnum());
                    if (sec != null && (s.getPicnum() == CROSSBOW || s.getPicnum() == CHIKENCROSSBOW) && sec.getLotag() == 2
                            && s.getXrepeat() >= 10 && rnd(140)) {
                        spawn(i, WATERBUBBLE);
                    }
                    continue;
                }
                case SHOTSPARK1: {
                    int p = findplayer(s);
                    execute(currentGame.getCON(), i, p, player_dist);
                    break;
                }
            }
        }
    }

    public static void displayweapon(int snum) {
        RRRenderer renderer = game.getRenderer();

        int posx, posy;

        final PlayerStruct p = ps[snum];
        int kb = p.kickback_pic;
        Sprite psp = boardService.getSprite(p.i);
        if (psp == null) {
            return;
        }

        int o = 0;
        int looking_arc = klabs(p.look_ang) / 9;
        int gs = psp.getShade();
        if (shadeEffect.getBit(p.cursectnum)) {
            gs = 16;
        }

        if (gs > 24) {
            gs = 24;
        }

        if (p.newowner >= 0 || ud.camerasprite >= 0 || over_shoulder_on > 0 || (psp.getPal() != 1 && psp.getExtra() <= 0)) {
            return;
        }

        int gun_pos = 80 - p.weapon_pos * p.weapon_pos;
        int weapon_xoffset = (160) - 90;
        weapon_xoffset -= (EngineUtils.sin(((p.weapon_sway >> 1) + 512) & 2047) / (1024 + 512));
        weapon_xoffset -= 58 + p.weapon_ang;
        if (psp.getXrepeat() < 8) {
            gun_pos -= klabs(EngineUtils.sin((p.weapon_sway << 2) & 2047) >> 9);
        } else {
            gun_pos -= klabs(EngineUtils.sin((p.weapon_sway >> 1) & 2047) >> 10);
        }

        gun_pos -= (p.hard_landing << 3);

        if (ud.screen_size > 2) {
            gun_pos += engine.getTile(BOTTOMSTATUSBAR).getHeight() / 4;
        }

        if (ud.screen_size > 3) {
            gun_pos += engine.getTile(1649).getHeight() / 4;
        }

        int cw;
        if (p.last_weapon >= 0) {
            cw = p.last_weapon;
        } else {
            cw = p.curr_weapon;
        }

        int pal = 0;
        if (psp.getXrepeat() < 8) {
            if (p.jetpack_on == 0) {
                int i = psp.getXvel();
                looking_arc += 32 - (i >> 1);
                fistsign += (short) (i >> 1);
            }
            cw = weapon_xoffset;
            weapon_xoffset += EngineUtils.sin((fistsign) & 2047) >> 10;
            myos(weapon_xoffset + 250 - (p.look_ang >> 1),
                    looking_arc + 258 - (klabs(EngineUtils.sin((fistsign) & 2047) >> 8)),
                    1408, gs, o);
            weapon_xoffset = cw;
            weapon_xoffset -= EngineUtils.sin((fistsign) & 2047) >> 10;
            myos(weapon_xoffset + 40 - (p.look_ang >> 1),
                    looking_arc + 200 + (klabs(EngineUtils.sin((fistsign) & 2047) >> 8)),
                    1408, gs, o | 4);
        } else if (p.OnMotorcycle) {
            if (over_shoulder_on != 0) {
                return;
            }
            int pic = 7170;
            if (numplayers == 1) {
                if (kb != 0) {
                    gs = 0;
                    if (kb == 1) {
                        if ((engine.krand() & 1) == 1) {
                            pic = 7171;
                        } else {
                            pic = 7172;
                        }
                    } else if (kb == 4) {
                        if ((engine.krand() & 1) == 1) {
                            pic = 7173;
                        } else {
                            pic = 7174;
                        }
                    }
                }
            } else {
                if (kb != 0) {
                    gs = 0;
                    switch (kb) {
                        case 1:
                            pic = 7171;
                            break;
                        case 2:
                            pic = 7172;
                            break;
                        case 3:
                            pic = 7173;
                            break;
                        case 4:
                            pic = 7174;
                            break;
                    }
                }
            }

            if (psp.getPal() == 1) {
                pal = 1;
            } else {
                Sector psec = boardService.getSector(p.cursectnum);
                if (psec != null) {
                    pal = psec.getFloorpal();
                    if (pal == 0) {
                        pal = p.palookup;
                    }
                }
            }

            if (p.TiltStatus < 0) {
                renderer.rotatesprite((160 - (p.look_ang >> 1)) << 16, 174 << 16, 0x8800, 5 * p.TiltStatus + 2047, pic, gs, pal, 10);
            } else {
                renderer.rotatesprite((160 - (p.look_ang >> 1)) << 16, 174 << 16, 0x8800, 5 * p.TiltStatus, pic, gs, pal, 10);
            }
        } else if (p.OnBoat) {
            if (over_shoulder_on != 0) {
                return;
            }
            int pic = 7175;
            if (p.TiltStatus <= 0) {
                if (p.TiltStatus == 0) {
                    if (kb != 0) {
                        if (kb > 3) {
                            if (kb <= 6) {
                                pic = 7179;
                                gs = -96;
                            }
                        } else {
                            pic = 7178;
                            gs = -96;
                        }
                    }
                } else if (kb != 0) {
                    if (kb > 3) {
                        if (kb > 6) {
                            pic = 7177;
                        } else {
                            pic = 7183;
                            gs = -96;
                        }
                    } else {
                        pic = 7182;
                        gs = -96;
                    }
                } else {
                    pic = 7177;
                }
            } else if (kb != 0) {
                if (kb > 3) {
                    if (kb > 6) {
                        pic = 7176;
                    } else {
                        pic = 7181;
                        gs = -96;
                    }
                } else {
                    pic = 7180;
                    gs = -96;
                }
            } else {
                pic = 7176;
            }


            if (psp.getPal() == 1) {
                pal = 1;
            } else {
                Sector psec = boardService.getSector(p.cursectnum);
                if (psec != null) {
                    pal = psec.getFloorpal();
                    if (pal == 0) {
                        pal = p.palookup;
                    }
                }
            }

            posy = 170;
            if (p.NotOnWater == 0) {
                posy = (kb >> 2) + 170;
            }

            if (p.TiltStatus < 0) {
                renderer.rotatesprite((160 - (p.look_ang >> 1)) << 16, posy << 16, 0x10200, p.TiltStatus + 2047, pic, gs, pal, 10);
            } else {
                renderer.rotatesprite((160 - (p.look_ang >> 1)) << 16, posy << 16, 0x10200, p.TiltStatus, pic, gs, pal, 10);
            }

        } else {
            switch (cw) {
                case KNEE_WEAPON:
                case RATE_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                            if (pal == 0) {
                                pal = p.palookup;
                            }
                        }
                    }

                    if (cw == RATE_WEAPON) {
                        myospal(((rake_x[crowbar_frames[kb]] >> 1) - 12 + weapon_xoffset) - (p.look_ang >> 1) + 20,
                                looking_arc + 210 - (244 - rake_y[crowbar_frames[kb]]) - gun_pos - 80, 32768, 3510 + crowbar_frames[kb], gs, o, pal);
                    } else {
                        myospal(((crowbar_x[
                                        crowbar_frames[
                                                kb]] >> 1) - 12 + weapon_xoffset) - (p.look_ang >> 1),
                                looking_arc + 200 - (244 - crowbar_y[crowbar_frames[kb]]) - gun_pos, 32768, 3340 + crowbar_frames[kb], gs, o, pal);
                    }
                    break;

                case POWDERKEG_WEAPON:
                case BOWLING_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    posx = weapon_xoffset + 170;
                    posy = looking_arc + 214 - gun_pos;

                    weapon_xoffset += 8;

                    if (cw == 12) {
                        if (p.ammo_amount[12] != 0) {
                            myospal(posx - (p.look_ang >> 1), posy + 11 + ((kb) << 3), 32768, 3428, gs, o, pal);
                            return;
                        }
                    } else if (p.ammo_amount[POWDERKEG_WEAPON] != 0) {
                        myospal(weapon_xoffset + 180 - (p.look_ang >> 1), posy + ((kb) << 3), 36700, 3438, gs, o, pal);
                        myospal(weapon_xoffset + 90 - (p.look_ang >> 1), posy + ((kb) << 3), 36700, 3438, gs, o | 4, pal);
                        return;
                    }
                    myospal(posx - (p.look_ang >> 1), posy + 11, 36700, 3365, gs, o, pal);
                    break;

                case CROSSBOW_WEAPON:
                case CHICKENBOW_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    posx = weapon_xoffset + 210;
                    posy = looking_arc + 255 - gun_pos;

                    if (cw == CROSSBOW_WEAPON) {
                        if (kb != 0) {
                            switch (weap5_frames[kb]) {
                                case 2:
                                    myospal(posx - (p.look_ang >> 1) - 3, posy - 5, 36700, weap5_frames[kb] + 3452, gs, o, pal);
                                    break;
                                case 3:
                                case 1:
                                    myospal(posx - (p.look_ang >> 1) - 10, posy - 5, 36700, weap5_frames[kb] + 3452, gs, o, pal);
                                    break;
                                default:
                                    myospal(posx - (p.look_ang >> 1), posy, 36700, weap5_frames[kb] + 3452, gs, o, pal);
                                    break;
                            }
                        } else {
                            myospal(posx - (p.look_ang >> 1), posy, 36700, 3452, gs, o, pal);
                        }
                    }

                    if (cw == CHICKENBOW_WEAPON) {
                        posy -= 40;
                        if (kb != 0) {
                            switch (weap5_frames[kb]) {
                                case 2:
                                    myospal(posx - (p.look_ang >> 1) - 10, posy + 33, 36700, weap5_frames[kb] + 3482, gs, o, pal);
                                    break;
                                case 3:
                                    myospal(posx - (p.look_ang >> 1) - 7, posy + 48, 36700, weap5_frames[kb] + 3482, gs, o, pal);
                                    break;
                                case 1:
                                    myospal(posx - (p.look_ang >> 1) - 10, posy + 10, 36700, weap5_frames[kb] + 3482, gs, o, pal);
                                    break;
                                default:
                                    myospal(posx - (p.look_ang >> 1), posy, 36700, weap5_frames[kb] + 3482, gs, o, pal);
                                    break;
                            }
                        } else {
                            if (ud.multimode >= 2) {
                                myospal(posx - (p.look_ang >> 1), posy - 3, 36700, 3482, gs, o, pal);
                            } else {
                                if (p.chiken_phase != 0) {
                                    myospal(posx - (p.look_ang >> 1), posy - 3, 36700, 3489, gs, o, pal);
                                } else if (p.chiken_pic == 5) {
                                    myospal(posx - (p.look_ang >> 1), posy - 3, 36700, 3489, gs, o, pal);
                                    p.chiken_phase = 6;
                                    spritesound(327, p.i);
                                } else {
                                    myospal(posx - (p.look_ang >> 1), posy, 36700, 3482, gs, o, pal);
                                }
                            }
                        }
                    }
                    break;

                case SHOTGUN_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    if (p.shotgun_splitshot != 0) {
                        if (kb >= 26) //reload
                        {
                            if (shotgun_frames[kb] <= 0) {
                                posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
                                posy = 244 - shotgun_y[shotgun_frames[kb]];
                            } else {
                                posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb - 10]] >> 1) - 12;
                                posy = 244 - shotgun_y[shotgun_frames[kb - 10]];
                            }
                            posy = 180 - posy;

                            myospal(posx + 64 - (p.look_ang >> 1), looking_arc + posy - gun_pos, 32768, shotgun_frames[kb] + 3350, gs, 0, pal);
                            if (shotgun_frames[kb] == 21) {
                                myospal(posx + 96 - (p.look_ang >> 1), looking_arc + posy - gun_pos, 32768, 3372, gs, 0, pal);
                            }
                        } else {
                            if (shotgun_frames[kb] == 3 || shotgun_frames[kb] == 4) {
                                gs = 0;
                            }
                            posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
                            posy = 180 - (244 - shotgun_y[shotgun_frames[kb]]);
                            myospal(posx + 64 - (p.look_ang >> 1),
                                    looking_arc + posy - gun_pos, 32768,
                                    shotgun_frames[kb] + 3350, gs, 0, pal);
                        }
                    } else {
                        if (kb >= 16) //reload
                        {
                            if (p.shotgunstatus != 0) {
                                if (shotgun_frames3[kb] <= 0) {
                                    posx = weapon_xoffset + (shotgun_x[shotgun_frames3[kb]] >> 1) - 12;
                                    posy = 244 - shotgun_y[shotgun_frames3[kb]];
                                } else {
                                    posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
                                    posy = 244 - shotgun_y[shotgun_frames[kb]];
                                }
                                posy = 180 - posy;
                                if (kb == 23) {
                                    posy += 60;
                                }
                                if (kb == 24) {
                                    posy += 30;
                                }

                                myospal(posx + 64 - (p.look_ang >> 1), looking_arc + posy - gun_pos, 32768, shotgun_frames3[kb] + 3350, gs, 0, pal);
                                if (shotgun_frames3[kb] == 21) {
                                    myospal(posx + 96 - (p.look_ang >> 1), looking_arc + posy - gun_pos, 32768, 3372, gs, 0, pal);
                                }
                            }
                        } else if (p.shotgunstatus != 0) {
                            if (shotgun_frames3[kb] == 3 || shotgun_frames3[kb] == 4) {
                                gs = 0;
                            }
                            myospal((weapon_xoffset + (shotgun_x[shotgun_frames3[kb]] >> 1) - 12) + 64 - (p.look_ang >> 1),
                                    looking_arc + (180 - (244 - shotgun_y[shotgun_frames3[kb]])) - gun_pos,
                                    32768, shotgun_frames3[kb] + 3350, gs, 0, pal);
                        } else {
                            if (shotgun_frames2[kb] == 1 || shotgun_frames2[kb] == 2) {
                                gs = 0;
                            }
                            myospal((weapon_xoffset + (shotgun_x[shotgun_frames2[kb]] >> 1) - 12) + 64 - (p.look_ang >> 1),
                                    (180 - (244 - shotgun_y[shotgun_frames2[kb]])) + looking_arc - gun_pos,
                                    32768, shotgun_frames2[kb] + 3350, gs, 0, pal);
                        }
                    }
                    break;

                case RIFLEGUN_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    if (kb > 0) {
                        gun_pos -= EngineUtils.sin((kb) << 7) >> 12;
                    }

                    if (kb > 0 && psp.getPal() != 1) {
                        weapon_xoffset += 1 - (engine.rand() & 3); // displayweapon
                    }

                    if (kb == 0) {
                        myospal(weapon_xoffset + 178 - (p.look_ang >> 1) + 30, looking_arc + 233 - gun_pos + 5, 32768, 3380, gs, o, pal);
                    } else {
                        if (kb < 8) {
                            int i = engine.rand() & 7; // displayweapon
                            myospal(weapon_xoffset + 178 - (p.look_ang >> 1) + 30 + i, looking_arc + 233 - gun_pos + 5, 32768, 3381, gs, o, pal);
                        } else {
                            myospal(weapon_xoffset + 178 - (p.look_ang >> 1) + 30, looking_arc + 233 - gun_pos + 5, 32768, 3382, gs, o, pal);
                        }
                    }
                    break;

                case PISTOL_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    if ((kb) < 22) {
                        myospal(pistol_x[pistol_frames[kb]] - 12 - (p.look_ang >> 1) + weapon_xoffset, 244 - (244 - pistol_y[pistol_frames[kb]] + 10) + looking_arc - gun_pos, 36700, pistol_frames[kb] + 3328, gs, 0, pal);
                    } else {
                        int xoffset = 0, yoffset = 0;
                        switch (kb) {
                            case 28:
                            case 36:
                                yoffset = 10;
                                xoffset = 5;
                                break;
                            case 29:
                            case 35:
                                yoffset = 20;
                                xoffset = 10;
                                break;
                            case 30:
                            case 34:
                                yoffset = 30;
                                xoffset = 15;
                                break;
                            case 31:
                            case 33:
                                yoffset = 40;
                                xoffset = 20;
                                break;
                            case 32:
                                yoffset = 50;
                                xoffset = 25;
                                break;
                        }
                        myospal(rpistol_x[rpistol_frames[kb - 22]] - (p.look_ang >> 1) - xoffset,
                                looking_arc + (244 - (244 - rpistol_y[rpistol_frames[kb - 22]])) - gun_pos + yoffset,
                                36700, rpistol_frames[kb - 22] + 3336, gs, 0, pal);
                    }
                    break;

                case DYNAMITE_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    weapon_xoffset -= EngineUtils.sin((768 + ((kb) << 7)) & 2047) >> 11;
                    gun_pos -= 9 * kb;
                    myospal(weapon_xoffset + 190 - (p.look_ang >> 1), looking_arc + 260 - gun_pos, 36700, 3360, gs, o, pal);
                    break;

                case HANDREMOTE_WEAPON:
                    if (kb < 20) {
                        if (psp.getPal() == 1) {
                            pal = 1;
                        } else {
                            Sector psec = boardService.getSector(p.cursectnum);
                            if (psec != null) {
                                pal = psec.getFloorpal();
                            }
                        }
                        posx = weapon_xoffset + 290;
                        posy = looking_arc + 258 - gun_pos - 64;
                        if (kb < 5) {
                            myospal(posx - (p.look_ang >> 1) - 25, posy + p.detonate_count - 20, 36700, 1752, 0, o, pal);
                        }

                        if (kb != 0) {
                            myospal(posx - (p.look_ang >> 1), looking_arc + 258 - gun_pos - 20, dynamite_flames[kb] + 3360, gs, o, pal);
                        } else {
                            myospal(posx - (p.look_ang >> 1), looking_arc + 258 - gun_pos - 20, 36700, 3361, gs, o, pal);
                        }
                    }
                    break;

                case TIT_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    if (kb == 0) {
                        myospal((weapon_xoffset >> 1) + 150 - (p.look_ang >> 1), (looking_arc >> 1) + 266 - gun_pos, 3446, gs, o, pal);
                    } else {
                        myospal((weapon_xoffset >> 1) + 150 - (p.look_ang >> 1), (looking_arc >> 1) + 266 - gun_pos, 3445, 0, o, pal);
                    }

                    break;

                case ALIENBLASTER_WEAPON:
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }

                    posx = weapon_xoffset + 260;
                    posy = looking_arc + 215 - gun_pos;

                    if (kb != 0) {
                        myospal(posx - (p.look_ang >> 1), posy, 36700, weap7_frames[kb] + 3415, -32, o, pal);
                    } else {
                        myospal(posx - (p.look_ang >> 1), posy, 36700, 3415, gs, o, pal);
                    }
                    break;


                case THROWSAW_WEAPON:
                case BUZSAW_WEAPON:
                    weapon_xoffset += 28;
                    looking_arc += 18;
                    if (psp.getPal() == 1) {
                        pal = 1;
                    } else {
                        Sector psec = boardService.getSector(p.cursectnum);
                        if (psec != null) {
                            pal = psec.getFloorpal();
                        }
                    }
                    if ((kb) == 0) {
                        myospal(weapon_xoffset + 188 - (p.look_ang >> 1),
                                looking_arc + 240 - gun_pos, 44040, 3384, gs, o, pal);
                    } else {
                        if (psp.getPal() != 1) {
                            weapon_xoffset += engine.rand() & 3; // displayweapon
                            gun_pos += (engine.rand() & 3); // displayweapon
                        }

                        if (cw == BUZSAW_WEAPON) {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1),
                                    looking_arc + 240 - gun_pos, 44040, (kb & 2) + BUZSAW, gs,
                                    o, pal);
                        } else {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1),
                                    looking_arc + 240 - gun_pos, 44040, 3384 + weap8_frames[kb], gs,
                                    o, pal);
                        }
                    }
                    break;
                case MOTO_WEAPON:
                case BOAT_WEAPON:
            }
        }
    }

    public static void weaponprocess(int snum) {
        PlayerStruct p = ps[snum];
        final int pi = p.i;
        Sprite s = boardService.getSprite(pi);
        if (s == null) {
            return;
        }

        boolean shrunk = (s.getYrepeat() < 8);
        int sb_snum = 0;
        if (p.cheat_phase <= 0) {
            sb_snum = sync[snum].bits;
        }

        Sector psec = boardService.getSector(p.cursectnum);
        if (psec == null) {
            return;
        }

        int psectlotag = psec.getLotag();

        if (p.curr_weapon == THROWSAW_WEAPON || p.curr_weapon == BUZSAW_WEAPON) {
            p.random_club_frame += 64; // Glowing
        }
        if (p.curr_weapon == 8 || p.curr_weapon == 12) {
            p.random_club_frame += 64;
        }

        if (p.rapid_fire_hold == 1) {
            if ((sb_snum & (1 << 2)) != 0) {
                return;
            }
            p.rapid_fire_hold = 0;
        }

        if (!shrunk && p.tipincs == 0 && p.access_incs == 0) {
            if ((sb_snum & (1 << 2)) != 0 && (p.kickback_pic) == 0 && p.fist_incs == 0 &&
                    p.last_weapon == -1 && (p.weapon_pos == 0 || p.holster_weapon == 1)) {
                p.crack_time = 777;

                if (p.holster_weapon == 1) {
                    if (p.last_pissed_time <= (26 * 218) && p.weapon_pos == -9) {
                        p.holster_weapon = 0;
                        p.weapon_pos = 10;
                        FTA(74, p);
                    }
                } else {
                    switch (p.curr_weapon) {
                        case CROSSBOW_WEAPON:
                            if (p.ammo_amount[CROSSBOW_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;
                        case CHICKENBOW_WEAPON:
                            if (p.ammo_amount[CHICKENBOW_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;
                        case HANDREMOTE_WEAPON:
                            p.hbomb_hold_delay = 0;
                            (p.kickback_pic) = 1;
                            break;

                        case PISTOL_WEAPON:
                            if (p.ammo_amount[PISTOL_WEAPON] > 0) {
                                p.ammo_amount[PISTOL_WEAPON]--;
                                (p.kickback_pic) = 1;
                            }
                            break;


                        case RIFLEGUN_WEAPON:
                            if (p.ammo_amount[RIFLEGUN_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;

                        case SHOTGUN_WEAPON:
                            if (p.ammo_amount[SHOTGUN_WEAPON] > 0 && p.random_club_frame == 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;

                        case POWDERKEG_WEAPON:
                        case BOWLING_WEAPON:
                            if (p.curr_weapon == 12) {
                                if (p.ammo_amount[12] > 0) {
                                    p.kickback_pic = 1;
                                }
                            } else if (p.ammo_amount[8] > 0) {
                                p.kickback_pic = 1;
                            }
                            break;

                        case THROWSAW_WEAPON:
                        case BUZSAW_WEAPON:
                            if (p.curr_weapon == BUZSAW_WEAPON) {
                                if (p.ammo_amount[BUZSAW_WEAPON] > 0) {
                                    (p.kickback_pic) = 1;
                                    spritesound(431, pi);
                                }
                            } else if (p.ammo_amount[THROWSAW_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                                spritesound(SHRINKER_FIRE, pi);
                            }
                            break;

                        case TIT_WEAPON:
                            if (p.ammo_amount[TIT_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                                p.hbomb_hold_delay ^= 1;
                            }
                            break;
                        case ALIENBLASTER_WEAPON:
                            if (p.ammo_amount[ALIENBLASTER_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;

                        case DYNAMITE_WEAPON:
                            if (p.ammo_amount[DYNAMITE_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;
                        case KNEE_WEAPON:
                        case RATE_WEAPON:
                            if (p.quick_kick == 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;
                        case MOTO_WEAPON:
                            if (p.ammo_amount[MOTO_WEAPON] != 0) {
                                (p.kickback_pic) = 1;
                                p.hbomb_hold_delay ^= 1;
                            }
                            break;
                        case BOAT_WEAPON:
                            if (p.ammo_amount[BOAT_WEAPON] != 0) {
                                (p.kickback_pic) = 1;
                            }
                            break;
                    }
                }
            } else if (p.kickback_pic != 0) {
                switch (p.curr_weapon) {
                    case DYNAMITE_WEAPON:
                        if ((p.kickback_pic) == 1) {
                            sound(401);
                        }

                        if ((p.kickback_pic) == 6 && (sb_snum & (1 << 2)) != 0) {
                            p.rapid_fire_hold = 1;
                        }

                        (p.kickback_pic)++;
                        if ((p.kickback_pic) > 19) {
                            p.kickback_pic = 0;
                            p.curr_weapon = 10;
                            p.last_weapon = -1;
                            p.weapon_pos = 10;
                            p.field_57C = 45;
                            p.detonate_count = 1;
                            sound(402);
                        }
                        break;
                    case HANDREMOTE_WEAPON:

                        (p.kickback_pic)++;
                        if (p.field_57C < 0) {
                            p.hbomb_on = 0;
                        }
                        if (p.kickback_pic == 39) {
                            p.hbomb_on = 0;
                            p.field_290 = 0x2000;
                            sub_64EF0(snum);
                        }

                        if ((p.kickback_pic) == 12) {
                            p.ammo_amount[4]--;
                            if (p.ammo_amount[5] > 0) {
                                p.ammo_amount[5]--;
                            }

                            int i, k, j;
                            if (p.on_ground && (sb_snum & 2) != 0) {
                                k = 15;
                                i = (int) ((p.horiz + p.horizoff - 100) * 20);
                            } else {
                                k = 140;
                                i = (int) (-512 - ((p.horiz + p.horizoff - 100) * 20));
                            }

                            if (IsOriginalDemo()) {
                                j = EGS(p.cursectnum, p.posx + (EngineUtils.sin(((int) p.ang + 512) & 2047) >> 6),
                                        p.posy + (EngineUtils.sin((int) p.ang & 2047) >> 6), p.posz, DYNAMITE, -16, 9, 9,
                                        (short) p.ang, (k + (p.hbomb_hold_delay << 5)), i, pi, (short) 1);
                            } else {
                                j = EGS(p.cursectnum, (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 64.0f)),
                                        (int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 64.0f)), p.posz, DYNAMITE, -16, 9, 9,
                                        (short) p.ang, (k + (p.hbomb_hold_delay << 5)), i, pi, (short) 1);
                            }

                            Sprite sp = boardService.getSprite(j);
                            if (sp != null && k == 15) {
                                sp.setYvel(3);
                                sp.setZ(sp.getZ() + (8 << 8));
                            }

                            k = hits(pi);
                            if (sp != null && k < 512) {
                                sp.setAng(sp.getAng() + 1024);
                                sp.setZvel(sp.getZvel() / 3);
                                sp.setXvel(sp.getXvel() / 3);
                            }

                            p.hbomb_on = 1;

                        } else if ((p.kickback_pic) < 12 && (sb_snum & (1 << 2)) != 0) {
                            p.hbomb_hold_delay++;
                        }

                        if ((p.kickback_pic) == 40) {
                            (p.kickback_pic) = 0;
                            p.curr_weapon = 4;
                            p.last_weapon = -1;
                            p.detonate_count = 0;
                            p.field_57C = 45;
                            if (p.ammo_amount[4] <= 0) {
                                checkavailweapon(p);
                                break;
                            }
                            addweapon(p, 4);
                            p.weapon_pos = -9;
                        }

                        break;

                    case PISTOL_WEAPON:
                        if ((p.kickback_pic) == 1) {
                            shoot(pi, SHOTSPARK1);
                            spritesound(PISTOL_FIRE, pi);

                            lastvisinc = engine.getTotalClock() + 32;
                            if (snum == screenpeek) {
                                gVisibility = 0;
                            }
                            p.field_290 = 0x2000;
                            sub_64EF0(snum);

                            if (psectlotag != 857) {
                                p.posxv -= 16 * EngineUtils.sin(((int) p.ang + 512) & 2047);
                                p.posyv -= 16 * EngineUtils.sin((int) p.ang & 2047);
                            }
                        } else if ((p.kickback_pic) == 2 && p.ammo_amount[1] <= 0) {
                            p.kickback_pic = 0;
                            checkavailweapon(p);
                        }

                        (p.kickback_pic)++;

                        if ((p.kickback_pic) >= 22) {
                            if (p.ammo_amount[PISTOL_WEAPON] <= 0) {
                                (p.kickback_pic) = 0;
                                checkavailweapon(p);
                            } else {
                                if ((p.ammo_amount[1] % 6) != 0) {
                                    p.kickback_pic = 38;
                                } else {
                                    switch ((p.kickback_pic)) {
                                        case 24:
                                            spritesound(EJECT_CLIP, pi);
                                            break;
                                        case 30:
                                            spritesound(INSERT_CLIP, pi);
                                            break;
                                    }
                                }
                            }
                        }

                        if ((p.kickback_pic) == 38) {
                            (p.kickback_pic) = 0;
                            checkavailweapon(p);
                        }
                        break;

                    case SHOTGUN_WEAPON:

                        (p.kickback_pic)++;
                        if (p.kickback_pic == 6 && p.shotgunstatus == 0 && p.ammo_amount[2] > 1 && (sb_snum & (1 << 2)) != 0) {
                            p.shotgun_splitshot = 1;
                        }

                        if (p.kickback_pic == 4) {
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);
                            shoot(pi, NEWSHOTGUN);

                            p.ammo_amount[SHOTGUN_WEAPON]--;

                            spritesound(SHOTGUN_FIRE, pi);
                            p.field_290 = 0x2000;
                            sub_64EF0(snum);
                            lastvisinc = engine.getTotalClock() + 32;
                            if (snum == screenpeek) {
                                gVisibility = 0;
                            }
                        }

                        if (p.kickback_pic == 7) {
                            if (p.shotgun_splitshot != 0) {
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);
                                shoot(pi, NEWSHOTGUN);

                                p.ammo_amount[2]--;
                                spritesound(109, pi);
                            }

                            if (psectlotag != 857) {
                                p.posxv -= 32 * EngineUtils.sin(((int) p.ang + 512) & 2047);
                                p.posyv -= 32 * EngineUtils.sin((int) p.ang & 2047);
                            }
                        }

                        if (p.shotgunstatus != 0) {
                            switch (p.kickback_pic) {
                                case 16:
                                    checkavailweapon(p);
                                    break;
                                case 17:
                                    spritesound(SHOTGUN_COCK, pi);
                                    break;
                                case 28:
                                    p.kickback_pic = 0;
                                    p.shotgunstatus = 0;
                                    p.shotgun_splitshot = 0;
                                    break;
                            }
                        } else if (p.shotgun_splitshot != 0) {
                            switch (p.kickback_pic) {
                                case 26:
                                    checkavailweapon(p);
                                    break;
                                case 27:
                                    spritesound(SHOTGUN_COCK, pi);
                                    break;
                                case 38:
                                    p.kickback_pic = 0;
                                    p.shotgun_splitshot = 0;
                                    break;
                            }
                        } else {
                            if (p.kickback_pic == 16) {
                                checkavailweapon(p);
                                p.kickback_pic = 0;
                                p.shotgunstatus = 1;
                                p.shotgun_splitshot = 0;
                            }
                        }
                        break;
                    case RIFLEGUN_WEAPON:

                        (p.kickback_pic)++;
                        p.horiz++;
                        p.kickback++;

                        if (p.kickback_pic <= 12) {
                            if (((p.kickback_pic) % 3) == 0) {
                                p.ammo_amount[RIFLEGUN_WEAPON]--;

                                if ((p.kickback_pic % 3) == 0) {
                                    int j = spawn(pi, SHELL);

                                    Sprite sp = boardService.getSprite(j);
                                    if (sp != null) {
                                        sp.setAng(sp.getAng() + 1024);
                                        sp.setAng(sp.getAng() & 2047);
                                        sp.setXvel(sp.getXvel() + 32);
                                        sp.setZ(sp.getZ() + (3 << 8));
                                        ssp(j, CLIPMASK0);
                                    }
                                }

                                spritesound(CHAINGUN_FIRE, pi);
                                shoot(pi, RIFLE);
                                lastvisinc = engine.getTotalClock() + 32;
                                if (snum == screenpeek) {
                                    gVisibility = 0;
                                }
                                p.field_290 = 0x2000;
                                sub_64EF0(snum);
                                if (psectlotag != 857) {
                                    p.posxv -= 16 * EngineUtils.sin(((int) p.ang + 512) & 2047);
                                    p.posyv -= 16 * EngineUtils.sin((int) p.ang & 2047);
                                }

                                checkavailweapon(p);

                                if ((sb_snum & (1 << 2)) == 0) {
                                    p.kickback_pic = 0;
                                    break;
                                }
                            }
                        } else {
                            if ((sb_snum & (1 << 2)) != 0) {
                                p.kickback_pic = 1;
                            } else {
                                p.kickback_pic = 0;
                            }
                        }

                        break;

                    case THROWSAW_WEAPON:
                    case BUZSAW_WEAPON:

                        if (p.curr_weapon == BUZSAW_WEAPON) {
                            if ((p.kickback_pic) > 3) {
                                p.kickback_pic = 0;
                                shoot(pi, BUZSAW);
                                p.field_290 = 1024;
                                sub_64EF0(snum);
                                checkavailweapon(p);
                            } else {
                                (p.kickback_pic)++;
                            }
                        } else {
                            if ((p.kickback_pic) == 1) {
                                p.ammo_amount[THROWSAW_WEAPON]--;
                                shoot(pi, CIRCLESAW);
                                checkavailweapon(p);
                            }


                            (p.kickback_pic)++;
                            if ((p.kickback_pic) > 20) {
                                p.kickback_pic = 0;
                            }
                        }
                        break;

                    case TIT_WEAPON:
                        (p.kickback_pic)++;
                        if ((p.kickback_pic) == 2 || (p.kickback_pic) == 4) {
                            if (snum == screenpeek) {
                                gVisibility = 0;
                            }
                            lastvisinc = engine.getTotalClock() + 32;
                            spritesound(CHAINGUN_FIRE, pi);
                            shoot(pi, SHOTSPARK1);
                            p.field_290 = 0x4000;
                            sub_64EF0(snum);
                            p.ammo_amount[TIT_WEAPON]--;
                            checkavailweapon(p);
                        }
                        if (p.kickback_pic == 2) {
                            p.ang += 16;
                        }
                        if (p.kickback_pic == 4) {
                            p.ang -= 16;
                        }
                        if (p.kickback_pic > 4) {
                            p.kickback_pic = 1;
                        }
                        if ((sb_snum & (1 << 2)) == 0) {
                            p.kickback_pic = 0;
                        }
                        break;

                    case ALIENBLASTER_WEAPON:
                        (p.kickback_pic)++;
                        if (p.kickback_pic >= 7 && p.kickback_pic <= 11) {
                            shoot(pi, FIRELASER);
                        }
                        if (p.kickback_pic == 5) {
                            spritesound(10, pi);
                            p.field_290 = 2048;
                            sub_64EF0(snum);
                        } else if (p.kickback_pic == 9) {
                            p.ammo_amount[ALIENBLASTER_WEAPON]--;
                            if (snum == screenpeek) {
                                gVisibility = 0;
                            }
                            lastvisinc = engine.getTotalClock() + 32;
                            checkavailweapon(p);
                        } else if (p.kickback_pic == 12) {
                            p.posxv -= 16 * EngineUtils.sin(((int) p.ang + 512) & 2047);
                            p.posyv -= 16 * EngineUtils.sin((int) p.ang & 2047);
                            p.kickback += 20;
                            p.horiz += 20;
                        }
                        if (p.kickback_pic > 20) {
                            p.kickback_pic = 0;
                        }
                        break;
                    case POWDERKEG_WEAPON:
                    case BOWLING_WEAPON:
                        if (p.curr_weapon == POWDERKEG_WEAPON) {
                            if ((p.kickback_pic) == 3) {
                                p.gotweapon[POWDERKEG_WEAPON] = false;
                                p.ammo_amount[POWDERKEG_WEAPON]--;

                                int i, k;
                                if (p.on_ground && (sb_snum & 2) != 0) {
                                    k = 15;
                                    i = (int) ((p.horiz + p.horizoff - 100) * 20);
                                } else {
                                    k = 32;
                                    i = (int) (-512 - ((p.horiz + p.horizoff - 100) * 20));
                                }

                                EGS(p.cursectnum,
                                        p.posx + (EngineUtils.sin(((int) p.ang + 512) & 2047) >> 6),
                                        p.posy + (EngineUtils.sin((int) p.ang & 2047) >> 6),
                                        p.posz, 27, -16, 9, 9,
                                        (int) p.ang, 2 * k, i, pi, (short) 1);
                            }
                            p.kickback_pic++;
                            if ((p.kickback_pic) > 20) {
                                p.kickback_pic = 0;
                                checkavailweapon(p);
                            }
                        } else {
                            if (p.kickback_pic == 30) {
                                p.ammo_amount[12]--;
                                spritesound(354, pi);
                                shoot(pi, BOWLINGBALL);
                                p.field_290 = 1024;
                                sub_64EF0(snum);
                            }
                            if (p.kickback_pic < 30) {
                                p.posxv += 16 * EngineUtils.cos(((int) p.ang) & 2047);
                                p.posyv += 16 * EngineUtils.sin((int) p.ang & 2047);
                            }

                            p.kickback_pic++;
                            if (p.kickback_pic > 40) {
                                p.kickback_pic = 0;
                                p.gotweapon[12] = false;
                                checkavailweapon(p);
                            }
                        }
                        break;
                    case KNEE_WEAPON:
                        (p.kickback_pic)++;

                        if ((p.kickback_pic) == 3) {
                            spritesound(426, pi);
                        } else if ((p.kickback_pic) == 12) {
                            shoot(pi, NEWCROWBAR);
                            p.field_290 = 1024;
                            sub_64EF0(snum);
                        } else if ((p.kickback_pic) == 16) {
                            p.kickback_pic = 0;
                        }

                        if (p.wantweaponfire >= 0) {
                            checkavailweapon(p);
                        }
                        break;

                    case RATE_WEAPON:
                        (p.kickback_pic)++;

                        if ((p.kickback_pic) == 3) {
                            spritesound(252, pi);
                        } else if ((p.kickback_pic) == 8) {
                            shoot(pi, 3510);
                            p.field_290 = 1024;
                            sub_64EF0(snum);
                        } else if ((p.kickback_pic) == 16) {
                            p.kickback_pic = 0;
                        }

                        if (p.wantweaponfire >= 0) {
                            checkavailweapon(p);
                        }
                        break;
                    case CROSSBOW_WEAPON:
                    case CHICKENBOW_WEAPON:
                        (p.kickback_pic)++;
                        switch (p.kickback_pic) {
                            case 4:
                                if (p.curr_weapon == CROSSBOW_WEAPON) {
                                    p.ammo_amount[CROSSBOW_WEAPON]--;
                                    if (p.ammo_amount[DYNAMITE_WEAPON] != 0) {
                                        p.ammo_amount[DYNAMITE_WEAPON]--;
                                    }
                                    shoot(pi, CROSSBOW);
                                } else {
                                    p.ammo_amount[CHICKENBOW_WEAPON]--;
                                    shoot(pi, CHIKENCROSSBOW);
                                }

                                lastvisinc = engine.getTotalClock() + 32;
                                if (snum == screenpeek) {
                                    gVisibility = 0;
                                }

                                p.field_290 = 0x8000;
                                sub_64EF0(snum);
                                checkavailweapon(p);
                                break;
                            case 16:
                                spritesound(450, pi);
                                break;
                            case 34:
                                p.kickback_pic = 0;
                                break;
                        }
                        break;
                    case MOTO_WEAPON:
                        (p.kickback_pic)++;
                        if ((p.kickback_pic) == 2 || (p.kickback_pic) == 4) {
                            if (snum == screenpeek) {
                                gVisibility = 0;
                            }
                            lastvisinc = engine.getTotalClock() + 32;
                            spritesound(CHAINGUN_FIRE, pi);
                            shoot(pi, RIFLE);
                            p.field_290 = 0x4000;
                            sub_64EF0(snum);
                            p.ammo_amount[MOTO_WEAPON]--;
                            if (p.ammo_amount[MOTO_WEAPON] > 0) {
                                checkavailweapon(p);
                            } else {
                                p.kickback_pic = 0;
                                break;
                            }
                        }
                        if (p.kickback_pic == 2) {
                            p.ang += 4;
                        }
                        if (p.kickback_pic == 4) {
                            p.ang -= 4;
                        }
                        if (p.kickback_pic > 4) {
                            p.kickback_pic = 1;
                        }
                        if ((sb_snum & (1 << 2)) == 0) {
                            p.kickback_pic = 0;
                        }

                        break;
                    case BOAT_WEAPON:
                        if ((p.kickback_pic) == 3) {
                            p.CarSpeed -= 20;
                            shoot(pi, 1790);
                            p.ammo_amount[BOAT_WEAPON]--;
                        }
                        p.kickback_pic++;
                        if ((p.kickback_pic) > 20) {
                            p.kickback_pic = 0;
                            checkavailweapon(p);
                        }

                        if (p.ammo_amount[BOAT_WEAPON] <= 0) {
                            p.kickback_pic = 0;
                        } else {
                            checkavailweapon(p);
                        }
                        break;
                }
            }
        }
    }
}
