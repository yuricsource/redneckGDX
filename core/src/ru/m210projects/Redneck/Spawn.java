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
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.exceptions.WarningException;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Types.CommonPart;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Gamedef.getglobalz;
import static ru.m210projects.Redneck.Gamedef.makeitfall;
import static ru.m210projects.Redneck.Gameutils.FindDistance2D;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.RSector.*;
import static ru.m210projects.Redneck.SoundDefs.SUBWAY;
import static ru.m210projects.Redneck.Sounds.check_fta_sounds;

public class Spawn {

    public static int tempwallptr;

    @CommonPart
    public static int EGS(int whatsect, int s_x, int s_y, int s_z, int s_pn, int s_s, int s_xr, int s_yr, int s_a, int s_ve, int s_zv, int s_ow, int s_ss) {
        if (boardService.getSector(whatsect) == null) {
            throw new WarningException("Wrong sector! " + whatsect);
        }

        int i = engine.insertsprite(whatsect, s_ss);
        Sprite s = boardService.getSprite(i);
        if (s == null) {
            throw new WarningException("Too many sprites spawned.");
        }

        s.setX(s_x);
        s.setY(s_y);
        s.setZ(s_z);
        s.setCstat(0);
        s.setPicnum(s_pn);
        s.setShade(s_s);
        s.setXrepeat(s_xr);
        s.setYrepeat(s_yr);

        s.setAng(s_a);
        s.setXvel(s_ve);
        s.setZvel(s_zv);
        s.setOwner(s_ow);
        s.setXoffset(0);
        s.setYoffset(0);
        s.setYvel(0);
        s.setClipdist(0);
        s.setPal(0);
        s.setLotag(0);

        Sprite spo = boardService.getSprite(s_ow);
        if (spo != null) {
            hittype[i].picnum = spo.getPicnum();
        }

        hittype[i].lastvx = 0;
        hittype[i].lastvy = 0;

        hittype[i].timetosleep = 0;
        hittype[i].actorstayput = -1;
        hittype[i].extra = -1;
        hittype[i].owner = s_ow;
        hittype[i].cgg = 0;
        hittype[i].movflag = 0;
        hittype[i].tempang = 0;
        hittype[i].dispicnum = 0;
        if (s_ow != -1) {
            hittype[i].floorz = hittype[s_ow].floorz;
            hittype[i].ceilingz = hittype[s_ow].ceilingz;
        }

        hittype[i].temp_data[0] = hittype[i].temp_data[2] = hittype[i].temp_data[3] = hittype[i].temp_data[5] = 0;
        if (s_pn < MAXTILES && currentGame.getCON().actorscrptr[s_pn] != 0) {
            s.setExtra((short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]]);
            hittype[i].temp_data[4] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 1];
            hittype[i].temp_data[1] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 2];
            s.setHitag((short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 3]);
        } else {
            hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
            s.setExtra(0);
            s.setHitag(0);
        }

        if (show2dsector.getBit(s.getSectnum())) {
            show2dsprite.setBit(i);
        } else {
            show2dsprite.clearBit(i);
        }

        game.pInt.clearspriteinterpolate(i);
        game.pInt.setsprinterpolate(i, s);

        return (i);
    }

    public static int spawn(final int j, int pn) {
        int clostest = 0;
        int x, y, d;
        Sprite spawner = boardService.getSprite(j);

        final int i;
        if (spawner != null) {
            i = EGS(spawner.getSectnum(), spawner.getX(), spawner.getY(), spawner.getZ(), pn, 0, 0, 0, 0, 0, 0, j, (short) 0);
            hittype[i].picnum = spawner.getPicnum();
        } else {
            i = pn;
            Sprite s = boardService.getSprite(i);
            if (s == null) {
                return -1;
            }

            hittype[i].picnum = s.getPicnum();
            hittype[i].timetosleep = 0;
            hittype[i].extra = -1;

            hittype[i].owner = i;
            s.setOwner(i);
            Sector sec = boardService.getSector(s.getSectnum());

            hittype[i].cgg = 0;
            hittype[i].movflag = 0;
            hittype[i].tempang = 0;
            hittype[i].dispicnum = 0;
            if (sec != null) {
                hittype[i].floorz = sec.getFloorz();
                hittype[i].ceilingz = sec.getCeilingz();
            }

            hittype[i].lastvx = 0;
            hittype[i].lastvy = 0;
            hittype[i].actorstayput = -1;

            hittype[i].temp_data[0] = hittype[i].temp_data[1] = hittype[i].temp_data[2] = hittype[i].temp_data[3] = hittype[i].temp_data[4] = hittype[i].temp_data[5] = 0;

            if ((s.getCstat() & 48) != 0) {
                if (!(s.getPicnum() >= CRACK1 && s.getPicnum() <= CRACK4)) {
                    game.pInt.setsprinterpolate(i, s);
                    if (s.getShade() == 127) {
                        return i;
                    }
                    if (wallswitchcheck(i) && (s.getCstat() & 16) != 0) {
                        if (s.getPicnum() != 82 && s.getPicnum() != 129 && s.getPal() != 0) {
                            if (ud.multimode < 2 || ud.coop == 1) {
                                s.setXrepeat(0);
                                s.setYrepeat(0);
                                s.setCstat(0);
                                s.setLotag(0);
                                s.setHitag(0);
                                return i;
                            }
                        }
                        s.setCstat(s.getCstat() | 257);
                        if (s.getPal() != 0 && s.getPicnum() != 82 && s.getPicnum() != 129) {
                            s.setPal(0);
                        }
                        return i;
                    }

                    if (s.getHitag() != 0) {
                        engine.changespritestat(i, (short) 12);
                        s.setCstat(s.getCstat() | 257);
                        s.setExtra((short) currentGame.getCON().impact_damage);
                        return i;
                    }
                }
            }

            final int actorscrptr = currentGame.getCON().actorscrptr[s.getPicnum()];

            if ((s.getCstat() & 1) != 0) {
                s.setCstat(s.getCstat() | 256);
            }

            if (actorscrptr != 0) {
                s.setExtra((short) currentGame.getCON().script[actorscrptr]);
                hittype[i].temp_data[4] = currentGame.getCON().script[actorscrptr + 1];
                hittype[i].temp_data[1] = currentGame.getCON().script[actorscrptr + 2];
                if (currentGame.getCON().script[actorscrptr + 3] != 0 && s.getHitag() == 0) {
                    s.setHitag((short) currentGame.getCON().script[actorscrptr + 3]);
                }
            } else {
                hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
            }
        }

        game.pInt.clearspriteinterpolate(i);

        final Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return -1;
        }

        Sector sec = boardService.getSector(sp.getSectnum());
        if (sec == null) {
            return -1;
        }

        final int sect = sp.getSectnum();

        switch (sp.getPicnum()) {
            default:

                if (currentGame.getCON().actorscrptr[sp.getPicnum()] != 0) {
                    if (spawner == null && sp.getLotag() > ud.player_skill) {
                        sp.setXrepeat(0);
                        sp.setYrepeat(0);
                        engine.changespritestat(i, (short) 5);
                        break;
                    }

                    //  Init the size
                    if (sp.getXrepeat() == 0 || sp.getYrepeat() == 0) {
                        sp.setXrepeat(1);
                        sp.setYrepeat(1);
                    }

                    if ((currentGame.getCON().actortype[sp.getPicnum()] & 3) != 0) {
                        if (ud.monsters_off) {
                            sp.setXrepeat(0);
                            sp.setYrepeat(0);
                            engine.changespritestat(i, (short) 5);
                            break;
                        }

                        makeitfall(currentGame.getCON(), i);

                        if ((currentGame.getCON().actortype[sp.getPicnum()] & 2) != 0) {
                            hittype[i].actorstayput = sp.getSectnum();
                        }

                        if (checkaddkills(sp)) {
                            ps[connecthead].max_actors_killed++;
                        }

                        sp.setClipdist(80);
                        if (spawner != null) {
                            if (spawner.getPicnum() == RESPAWN) {
                                hittype[i].tempang = spawner.getPal();
                                sp.setPal(hittype[i].tempang);
                            }
                            engine.changespritestat(i, (short) 1);
                        } else {
                            engine.changespritestat(i, (short) 2);
                        }
                    } else {
                        sp.setClipdist(40);
                        sp.setOwner(i);
                        engine.changespritestat(i, (short) 1);
                    }

                    hittype[i].timetosleep = 0;

                    if (spawner != null) {
                        sp.setAng(spawner.getAng());
                    }
                }
                break;
            case BLOODPOOL: {
                int s1 = engine.updatesector(sp.getX() + 108, sp.getY() + 108, sp.getSectnum());
                Sector sec1 = boardService.getSector(s1);
                if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                    s1 = engine.updatesector(sp.getX() - 108, sp.getY() - 108, s1);
                    sec1 = boardService.getSector(s1);
                    if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                        s1 = engine.updatesector(sp.getX() + 108, sp.getY() - 108, s1);
                        sec1 = boardService.getSector(s1);
                        if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                            s1 = engine.updatesector(sp.getX() - 108, sp.getY() + 108, s1);
                            sec1 = boardService.getSector(s1);
                            if (sec1 != null && sec1.getFloorz() != sec.getFloorz()) {
                                sp.setXrepeat(0);
                                sp.setYrepeat(0);
                                engine.changespritestat(i, (short) 5);
                                break;
                            }
                        } else {
                            sp.setXrepeat(0);
                            engine.changespritestat(i, (short) 5);
                            break;
                        }
                    } else {
                        sp.setXrepeat(0);
                        sp.setYrepeat(0);
                        engine.changespritestat(i, (short) 5);
                        break;
                    }
                } else {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }

                if (sec.getLotag() == 1) {
                    engine.changespritestat(i, (short) 5);
                    break;
                }

                if (spawner != null) {
                    if (spawner.getPal() == 1) {
                        sp.setPal(1);
                    } else if (spawner.getPal() != 6 && spawner.getPicnum() != 1304 && spawner.getPicnum() != TIRE) {
                        sp.setPal(2);
                    } else {
                        sp.setPal(0);
                    }

                    if (spawner.getPicnum() == TIRE) {
                        sp.setShade(127);
                    }
                }
                sp.setCstat(sp.getCstat() | 32);
            }
            case BLOODSPLAT1:
            case BLOODSPLAT2:
            case BLOODSPLAT3:
            case BLOODSPLAT4:
                sp.setCstat(sp.getCstat() | 16);
                sp.setXrepeat((short) (7 + (engine.krand() & 7)));
                sp.setYrepeat((short) (7 + (engine.krand() & 7)));
                sp.setZ(sp.getZ() - (16 << 8));
                if (spawner != null && spawner.getPal() == 6) {
                    sp.setPal(6);
                }
                insertspriteq(i);
                engine.changespritestat(i, (short) 5);
                break;

            case BULLETHOLE:
                sp.setXrepeat(3);
                sp.setYrepeat(3);
                sp.setCstat((short) (16 + (engine.krand() & 12)));
                insertspriteq(i);
            case FEATHERS:
                if (sp.getPicnum() == FEATHERS) {
                    hittype[i].temp_data[0] = engine.krand() & 2047;
                    sp.setCstat((short) (engine.krand() & 12));
                    sp.setXrepeat(8);
                    sp.setYrepeat(8);
                    sp.setAng((short) (engine.krand() & 2047));
                }
                engine.changespritestat(i, (short) 5);
                break;

            case EXPLOSION2:
            case EXPLOSION3:
            case BURNING:
            case SMALLSMOKE:
                if (spawner != null) {
                    sp.setAng(spawner.getAng());
                    sp.setShade(-64);
                    sp.setCstat((short) (128 | (engine.krand() & 4)));
                }

                switch (sp.getPicnum()) {
                    case EXPLOSION2:
                        sp.setXrepeat(48);
                        sp.setYrepeat(48);
                        sp.setShade(-127);
                        sp.setCstat(sp.getCstat() | 128);
                        break;
                    case EXPLOSION3:
                        sp.setXrepeat(128);
                        sp.setYrepeat(128);
                        sp.setShade(-127);
                        sp.setCstat(sp.getCstat() | 128);
                        break;
                    case SMALLSMOKE:
                        sp.setXrepeat(12);
                        sp.setYrepeat(12);
                        break;
                    case BURNING:
                        sp.setXrepeat(4);
                        sp.setYrepeat(4);
                        break;
                }

                if (spawner != null) {
                    x = engine.getflorzofslope(sp.getSectnum(), sp.getX(), sp.getY());
                    if (sp.getZ() > x - (12 << 8)) {
                        sp.setZ(x - (12 << 8));
                    }
                }

                engine.changespritestat(i, (short) 5);
                break;

            case 1268:
            case 1309:
                sp.setExtra(0);
            case 1187:
            case 1251:
            case 1304:
            case 1305:
            case 1306:
            case 1315:
            case 1317:
            case 1388:
                if (spawner != null) {
                    sp.setXrepeat(32);
                    sp.setYrepeat(32);
                }
                sp.setClipdist(72);
                makeitfall(currentGame.getCON(), i);
                if (spawner != null) {
                    sp.setOwner(j);
                } else {
                    sp.setOwner(i);
                }
            case 1147:
                if (ud.monsters_off && sp.getPicnum() == 1147) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                } else {
                    if (sp.getPicnum() == 1147) {
                        sp.setClipdist(24);
                    }
                    sp.setCstat((short) (257 | (engine.krand() & 4)));
                    engine.changespritestat(i, (short) 2);
                }
                break;
            case 1272:
                sp.setLotag(9999);
                engine.changespritestat(i, (short) 6);
                break;
            case CRANE: {

                sp.setCstat(sp.getCstat() | (64 | 257));

                sp.setPicnum(sp.getPicnum() + 2);
                sp.setZ(sec.getCeilingz() + (48 << 8));
                hittype[i].temp_data[4] = tempwallptr;

                msx[tempwallptr] = sp.getX();
                msy[tempwallptr] = sp.getY();
                msx[tempwallptr + 2] = sp.getZ();

                ListNode<Sprite> ns = boardService.getStatNode(0);
                while (ns != null) {
                    int s = ns.getIndex();
                    Sprite spr = ns.get();
                    if (spr.getPicnum() == 1298 && sp.getHitag() == (spr.getHitag())) {
                        msy[tempwallptr + 2] = s;

                        hittype[i].temp_data[1] = spr.getSectnum();

                        spr.setXrepeat(48);
                        spr.setYrepeat(128);

                        msx[tempwallptr + 1] = spr.getX();
                        msy[tempwallptr + 1] = spr.getY();

                        spr.setX(sp.getX());
                        spr.setY(sp.getY());
                        spr.setZ(sp.getZ());
                        spr.setShade(sp.getShade());

                        engine.setsprite(s, spr.getX(), spr.getY(), spr.getZ());
                        break;
                    }
                    ns = ns.getNext();
                }

                tempwallptr += 3;
                sp.setOwner(-1);
                sp.setExtra(8);
                engine.changespritestat(i, (short) 6);
                break;
            }
            case FANSPRITEWORK:
            case 234:
            case 1066:
            case 1067:
            case 1085:
            case 1086:
            case 1114:
            case 1117:
            case 1121:
            case 1123:
            case 1124:
            case 1141:
            case 1150:
            case 1152:
            case 1157:
            case 1158:
            case 1163:
            case 1164:
            case 1165:
            case 1166:
            case 1172:
            case 1174:
            case 1175:
            case 1176:
            case 1178:
            case 1180:
            case 1194:
            case 1203:
            case 1215:
            case 1216:
            case 1217:
            case 1218:
            case 1219:
            case 1220:
            case 1221:
            case 1222:
            case 1228:
            case 1232:
            case 1233:
            case 1234:
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
            case 2215:
                sp.setClipdist(32);
                sp.setCstat(sp.getCstat() | 257);
                engine.changespritestat(i, (short) 0);
                break;
            case WATERSPLASH2:
            case MUD:
                if (spawner != null) {
                    engine.setsprite(i, spawner.getX(), spawner.getY(), spawner.getZ());
                    int size = (8 + (engine.krand() & 7));
                    sp.setXrepeat(size);
                    sp.setYrepeat(size);
                } else {
                    int size = 16 + (engine.krand() & 15);
                    sp.setXrepeat(size);
                    sp.setYrepeat(size);
                }

                sp.setShade(-16);
                sp.setCstat(sp.getCstat() | 128);

                if (spawner != null) {
                    Sector ssec = boardService.getSector(spawner.getSectnum());
                    if (ssec != null) {
                        if (ssec.getLotag() == 2) {
                            sp.setZ(engine.getceilzofslope(sp.getSectnum(), sp.getX(), sp.getY()) + (16 << 8));
                            sp.setCstat(sp.getCstat() | 8);
                        } else if (ssec.getLotag() == 1) {
                            sp.setZ(engine.getflorzofslope(sp.getSectnum(), sp.getX(), sp.getY()));
                        }
                    }
                }

                if (sec.getFloorpicnum() == FLOORSLIME || sec.getCeilingpicnum() == FLOORSLIME) {
                    sp.setPal(7);
                }
            case 1080:
            case NEON1:
            case NEON2:
            case NEON3:
            case NEON4:
            case NEON5:
            case NEON6:
                if (sp.getPicnum() != WATERSPLASH2) {
                    sp.setCstat(sp.getCstat() | 257);
                }
                if (sp.getPicnum() == 1080) {
                    sp.setCstat(sp.getCstat() | 257);
                }
            case JIBS1:
            case JIBS2:
            case JIBS3:
            case JIBS4:
            case JIBS5:
            case JIBS6:
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
            case 2460: //RA green jibs1
            case 2465: //RA green jibs2
            case 5872: //RA motowheel
            case 5877: //RA mototank
            case 5882: //RA boarddebris
            case 6112: //RA bikenbody
            case 6117: //RA bikenhead
            case 6121: //RA bikerhead2
            case 6127: //RA bikerhand
            case 7000: //RA babahead
            case 7005: //RA bababody
            case 7010: //RA babafoot
            case 7015: //RA babahand
            case 7020: //RA debris
            case 7025: //RA debbris
            case 7387: //RA deepjibs
            case 7392: //RA deepjibs2
            case 7397: //RA deepjibs3
            case 8890: //RA deep2jibs
            case 8895: //RA deep2jibs2

                if (currentGame.getCON().type != RRRA && (sp.getPicnum() == 2460 || sp.getPicnum() == 2465 || sp.getPicnum() == 5872 || sp.getPicnum() == 5877 || sp.getPicnum() == 5882 || sp.getPicnum() == 6112 || sp.getPicnum() == 6117 || sp.getPicnum() == 6121 || sp.getPicnum() == 6127 || sp.getPicnum() == 7000 || sp.getPicnum() == 7005 || sp.getPicnum() == 7010 || sp.getPicnum() == 7015 || sp.getPicnum() == 7020 || sp.getPicnum() == 7025 || sp.getPicnum() == 7387 || sp.getPicnum() == 7392 || sp.getPicnum() == 7397 || sp.getPicnum() == 8890 || sp.getPicnum() == 8895)) {
                    break;
                }


                switch (sp.getPicnum()) {
                    case JIBS6:
                        sp.setXrepeat(sp.getXrepeat() >> 1);
                        sp.setYrepeat(sp.getYrepeat() >> 1);
                        break;
                    case 7387:
                        sp.setXrepeat(18);
                        sp.setYrepeat(18);
                        break;
                    case 7392:
                        sp.setXrepeat(36);
                        sp.setYrepeat(36);
                        break;
                    case 7397:
                        sp.setXrepeat(54);
                        sp.setYrepeat(54);
                        break;
                }
                engine.changespritestat(i, (short) 5);
                break;
            case 1196:
                sp.setShade(-16);
                engine.changespritestat(i, (short) 6);
                break;
            case 1247:
                hittype[i].temp_data[0] = sp.getX();
                hittype[i].temp_data[1] = sp.getY();
                break;
            case WATERFOUNTAIN:
                sp.setLotag(1);
            case 1191:
            case 1193:
            case TIRE:
                sp.setCstat(257); // Make it hitable
                sp.setExtra(1);
                engine.changespritestat(i, (short) 6);
                break;

            case LNRDLYINGDEAD:
                if (spawner != null && spawner.getPicnum() == APLAYER) {
                    sp.setXrepeat(spawner.getXrepeat());
                    sp.setYrepeat(spawner.getYrepeat());
                    sp.setShade(spawner.getShade());
                    sp.setPal(ps[spawner.getYvel()].palookup);
                }
                sp.setCstat(0);
                sp.setExtra(1);
                sp.setXvel(292);
                sp.setZvel(360);
            case RESPAWNMARKERRED:
                if (sp.getPicnum() == RESPAWNMARKERRED) {
                    sp.setXrepeat(8);
                    sp.setYrepeat(8);
                    if (spawner != null) {
                        sp.setZ(hittype[j].floorz);
                    }
                } else {
                    sp.setCstat(sp.getCstat() | 257);
                    sp.setClipdist(128);
                }
            case 1170:
                if (sp.getPicnum() == 1170) {
                    sp.setYvel(sp.getHitag());
                }
                engine.changespritestat(i, (short) 1);
                break;
            case FOOTPRINTS:
            case FOOTPRINTS2:
            case FOOTPRINTS3:
            case FOOTPRINTS4:
                if (spawner != null) {
                    int s1 = engine.updatesector(sp.getX() + 84, sp.getY() + 84, sp.getSectnum());
                    Sector sec1 = boardService.getSector(s1);
                    if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                        s1 = engine.updatesector(sp.getX() - 84, sp.getY() - 84, s1);
                        sec1 = boardService.getSector(s1);
                        if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                            s1 = engine.updatesector(sp.getX() + 84, sp.getY() - 84, s1);
                            sec1 = boardService.getSector(s1);
                            if (sec1 != null && sec1.getFloorz() == sec.getFloorz()) {
                                s1 = engine.updatesector(sp.getX() - 84, sp.getY() + 84, s1);
                                sec1 = boardService.getSector(s1);
                                if (sec1 != null && sec1.getFloorz() != sec.getFloorz()) {
                                    sp.setXrepeat(0);
                                    sp.setYrepeat(0);
                                    engine.changespritestat(i, (short) 5);
                                    break;
                                }
                            } else {
                                sp.setXrepeat(0);
                                sp.setYrepeat(0);
                                break;
                            }
                        } else {
                            sp.setXrepeat(0);
                            sp.setYrepeat(0);
                            break;
                        }
                    } else {
                        sp.setXrepeat(0);
                        sp.setYrepeat(0);
                        break;
                    }

                    sp.setCstat((short) (32 + ((ps[spawner.getYvel()].footprintcount & 1) << 2)));
                    sp.setAng(spawner.getAng());
                }

                sp.setZ(sec.getFloorz());
                if (sec.getLotag() != 1 && sec.getLotag() != 2) {
                    sp.setXrepeat(32);
                    sp.setYrepeat(32);
                }

                insertspriteq(i);
                engine.changespritestat(i, (short) 5);
                break;
            case BOLT1:
            case BOLT1 + 1:
            case BOLT1 + 2:
            case BOLT1 + 3:
                hittype[i].temp_data[0] = sp.getXrepeat();
                hittype[i].temp_data[1] = sp.getYrepeat();
            case MASTERSWITCH:
                if (sp.getPicnum() == MASTERSWITCH) {
                    sp.setCstat(sp.getCstat() | 32768);
                }
                sp.setYvel(0);
                engine.changespritestat(i, (short) 6);
                break;
            case WATERDRIP:
                if (spawner != null && (spawner.getStatnum() == 10 || spawner.getStatnum() == 1)) {
                    sp.setShade(32);
                    if (spawner.getPal() != 1) {
                        sp.setPal(2);
                        sp.setZ(sp.getZ() - (18 << 8));
                    } else {
                        sp.setZ(sp.getZ() - (13 << 8));
                    }
                    sp.setAng(EngineUtils.getAngle(ps[connecthead].posx - sp.getX(), ps[connecthead].posy - sp.getY()));
                    sp.setXvel((short) (48 - (engine.krand() & 31)));
                    ssp(i, CLIPMASK0);
                } else if (spawner == null) {
                    sp.setZ(sp.getZ() + (4 << 8));
                    hittype[i].temp_data[0] = sp.getZ();
                    hittype[i].temp_data[1] = engine.krand() & 127;
                }
            case TRASH:
                if (sp.getPicnum() != WATERDRIP) {
                    sp.setAng((short) (engine.krand() & 2047));
                }
                sp.setXrepeat(24);
                sp.setYrepeat(24);
                engine.changespritestat(i, (short) 6);
                break;

            case CRACK1:
            case CRACK2:
            case CRACK3:
            case CRACK4:
                sp.setCstat(sp.getCstat() | 17);
                sp.setExtra(1);
                if (ud.multimode < 2 && sp.getPal() != 0) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }
                sp.setPal(0);
                sp.setOwner(i);
                engine.changespritestat(i, (short) 6);
                sp.setXvel(8);
                ssp(i, CLIPMASK0);
                break;
            case 1097:
            case 1106:
                sp.setCstat(sp.getCstat() & ~257);
                engine.changespritestat(i, (short) 0);
                break;
            case 285:
            case 286:
            case 287:
            case 288:
            case 289:
            case 290:
            case 291:
            case 292:
            case 293:
                sp.setCstat(0);
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                sp.setClipdist(0);
                sp.setCstat(sp.getCstat() | 32768);
                sp.setLotag(0);
                engine.changespritestat(i, 106);
                break;
            case BOWLLINE:
            case 281:
            case 282:
            case 283:
            case 2025:
            case 2026:
            case 2027:
            case 2028:
                sp.setCstat(0);
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                sp.setClipdist(0);
                sp.setExtra(0);
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, 105);
                break;
            case 63:
                sp.setXrepeat(1);
                sp.setYrepeat(1);
                sp.setClipdist(1);
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, (short) 100);
                break;
            case 295:
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, (short) 107);
                break;
            case 296:
            case 297:
                sp.setXrepeat(64);
                sp.setYrepeat(64);
                sp.setClipdist(64);
                engine.changespritestat(i, (short) 108);
                break;
            case WATERBUBBLE:
                if (spawner != null && spawner.getPicnum() == APLAYER) {
                    sp.setZ(sp.getZ() - (16 << 8));
                }
                if (sp.getPicnum() == WATERBUBBLE) {
                    if (spawner != null) {
                        sp.setAng(spawner.getAng());
                    }
                    sp.setXrepeat(4);
                    sp.setYrepeat(4);
                } else {
                    sp.setXrepeat(32);
                    sp.setYrepeat(32);
                }

                engine.changespritestat(i, (short) 5);
                break;
            case TOUCHPLATE:
                hittype[i].temp_data[2] = sec.getFloorz();
                if (sec.getLotag() != 1 && sec.getLotag() != 2) {
                    sec.setFloorz(sp.getZ());
                }
                if (sp.getPal() != 0 && ud.multimode > 1) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }
            case WATERBUBBLEMAKER:
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, (short) 6);
                break;

            case 14: //RA
            case FIRSTGUNSPRITE:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 37:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 59:
            case 61:
            case 78:
            case 1350:
            case 3437:
            case 5595:
            case 8460: //RA BOX
                if (spawner != null) {
                    sp.setLotag(0);
                    if (sp.getPicnum() == BOWLINGBALLSPRITE) {
                        sp.setZvel(0);
                    } else {
                        sp.setZvel(-1024);
                        sp.setZ(sp.getZ() - (32 << 8));
                    }
                    ssp(i, CLIPMASK0);
                    sp.setCstat((short) (engine.krand() & 4));
                } else {
                    sp.setOwner(i);
                    sp.setCstat(0);
                }

                if ((ud.multimode < 2 && sp.getPal() != 0) || (sp.getLotag() > ud.player_skill)) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }

                sp.setPal(0);
                sp.setShade(sec.getFloorshade());

            case DOORKEY:

                if (sp.getPicnum() == ECLAIRHEALTH) {
                    sp.setCstat(sp.getCstat() | 128);
                }

                if (ud.multimode > 1 && ud.coop != 1 && sp.getPicnum() == DOORKEY) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                } else {
                    if (sp.getPicnum() == AMMO) {
                        sp.setXrepeat(16);
                        sp.setYrepeat(16);
                    } else {
                        sp.setXrepeat(32);
                        sp.setYrepeat(32);
                    }
                }

                if (sp.getPicnum() == DOORKEY) {
                    sp.setShade(-17);
                }

                if (spawner != null) {
                    engine.changespritestat(i, (short) 1);
                } else {
                    engine.changespritestat(i, (short) 2);
                    makeitfall(currentGame.getCON(), i);
                }

                switch (sp.getPicnum()) {
                    case 78:
                        sp.setXrepeat(23);
                        sp.setYrepeat(23);
                        break;
                    case FIRSTGUNSPRITE:
                        sp.setXrepeat(16);
                        sp.setYrepeat(16);
                        break;
                    case CROSSBOWSPRITE:
                    case 8460:
                        sp.setXrepeat(16);
                        sp.setYrepeat(14);
                        break;
                    case TEATGUN:
                        sp.setXrepeat(17);
                        sp.setYrepeat(16);
                        break;
                    case BUZSAWSPRITE:
                        sp.setXrepeat(22);
                        sp.setYrepeat(13);
                        break;
                    case POWDERKEGSPRITE:
                    case 3437:
                        sp.setXrepeat(11);
                        sp.setYrepeat(11);
                        if (sp.getPicnum() == 27) {
                            sp.setXvel(32);
                            sp.setYvel(4);
                        }
                        break;
                    case ALIENARMGUN:
                    case SHOTGUNAMMO:
                        sp.setXrepeat(18);
                        sp.setYrepeat(17);
                        break;
                    case TEATAMMO:
                    case ALIENBLASTERAMMO:
                        sp.setXrepeat(10);
                        sp.setYrepeat(9);
                        break;
                    case AMMO:
                        sp.setXrepeat(9);
                        sp.setYrepeat(9);
                        break;
                    case RIFLEAMMO:
                        sp.setXrepeat(15);
                        sp.setYrepeat(15);
                        break;
                    case BLADEAMMO:
                        sp.setXrepeat(12);
                        sp.setYrepeat(7);
                        break;
                    case BEER:
                        sp.setXrepeat(5);
                        sp.setYrepeat(4);
                        break;
                    case WHISKEY:
                    case 5595:
                        sp.setXrepeat(8);
                        sp.setYrepeat(8);
                        break;
                    case PORKBALLS:
                    case MOONSHINE:
                        sp.setXrepeat(13);
                        sp.setYrepeat(9);
                        break;
                    case SNORKLE:
                        sp.setXrepeat(19);
                        sp.setYrepeat(16);
                        break;
                    case COWPIE:
                        sp.setXrepeat(8);
                        sp.setYrepeat(6);
                        break;
                    case 59:
                        sp.setXrepeat(6);
                        sp.setYrepeat(4);
                        break;
                    case DOORKEY:
                        sp.setXrepeat(11);
                        sp.setYrepeat(12);
                        break;
                    case 14:
                        sp.setXrepeat(20);
                        sp.setYrepeat(20);
                        break;
                }
                break;
            case DOORSHOCK:
                sp.setCstat(sp.getCstat() | 1 + 256);
                sp.setShade(-12);
                engine.changespritestat(i, (short) 6);
                break;
            case SOUNDFX:
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, (short) 2);
                break;
            case LOCATORS:
                sp.setCstat(sp.getCstat() | 32768);
                engine.changespritestat(i, (short) 7);
                break;
            case DYNAMITE:
                sp.setYvel(4);
                sp.setOwner(i);
                sp.setXrepeat(9);
                sp.setYrepeat(9);
            case REACTOR2:
            case REACTOR:
            case 4989:
                if (sp.getPicnum() == 4989) {
                    if (sp.getLotag() > ud.player_skill) {
                        sp.setXrepeat(0);
                        sp.setYrepeat(0);
                        engine.changespritestat(i, (short) 5);
                        game.pInt.setsprinterpolate(i, sp);
                        return i;
                    }
                    if (checkaddkills(sp)) {
                        ps[connecthead].max_actors_killed++;
                    }

                    hittype[i].temp_data[5] = 0;
                    if (ud.monsters_off) {
                        sp.setXrepeat(0);
                        sp.setYrepeat(0);
                        engine.changespritestat(i, (short) 5);
                        break;
                    }
                    sp.setExtra(130);
                }

                if (sp.getPicnum() == REACTOR || sp.getPicnum() == REACTOR2) {
                    sp.setExtra((short) currentGame.getCON().impact_damage);
                }

                sp.setCstat(sp.getCstat() | 257); // Make it hitable

                if (ud.multimode < 2 && sp.getPal() != 0) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }
                sp.setPal(0);
                sp.setShade(-17);

                engine.changespritestat(i, (short) 2);
                break;
            case RESPAWN:
                sp.setExtra(66 - 13);
            case MUSICANDSFX:
                if (ud.multimode < 2 && sp.getPal() == 1) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                }
                sp.setCstat((short) 32768);
                engine.changespritestat(i, (short) 11);
                break;
            case ACTIVATORLOCKED:
            case ACTIVATOR:
                sp.setCstat(sp.getCstat() | 32768);
                if (sp.getPicnum() == ACTIVATORLOCKED) {
                    sec.setLotag(sec.getLotag() | 16384);
                }
                engine.changespritestat(i, (short) 8);
                break;
            case SECTOREFFECTOR:
                sp.setYvel(sec.getExtra());
                sp.setCstat(sp.getCstat() | 32768);
                sp.setXrepeat(0);
                sp.setYrepeat(0);

                switch (sp.getLotag()) {
                    case 7: // Transporters!!!!
                    case 23:// XPTR END
                        if (sp.getLotag() != 23) {
                            int owner;
                            for (owner = 0; owner < boardService.getSpriteCount(); owner++) {
                                Sprite spo = boardService.getSprite(owner);
                                if (spo != null && (spo.getStatnum() < MAXSTATUS && spo.getPicnum() == SECTOREFFECTOR
                                        && (spo.getLotag() == 7 || spo.getLotag() == 23) && i != owner
                                        && spo.getHitag() == sp.getHitag())) {
                                    sp.setOwner((short) owner);
                                    break;
                                }
                            }
                        } else {
                            sp.setOwner(i);
                        }

                        hittype[i].temp_data[4] = (sec.getFloorz() == sp.getZ()) ? 1 : 0;
                        sp.setCstat(0);
                        engine.changespritestat(i, (short) 9);
                        game.pInt.setsprinterpolate(i, sp);
                        return i;
                    case 1:
                        sp.setOwner(-1);
                        hittype[i].temp_data[0] = 1;
                        break;
                    case 18:

                        if (sp.getAng() == 512) {
                            hittype[i].temp_data[1] = sec.getCeilingz();
                            if (sp.getPal() != 0) {
                                sec.setCeilingz(sp.getZ());
                            }
                        } else {
                            hittype[i].temp_data[1] = sec.getFloorz();
                            if (sp.getPal() != 0) {
                                sec.setFloorz(sp.getZ());
                            }
                        }

                        sp.setHitag(sp.getHitag() << 2);
                        break;

                    case 25: // Pistons
                        hittype[i].temp_data[3] = sec.getCeilingz();
                        hittype[i].temp_data[4] = 1;
                        sec.setCeilingz(sp.getZ());
                        game.pInt.setceilinterpolate(sect, sec); //ceilinz
                        break;
                    case 35:
                        sec.setCeilingz(sp.getZ());
                        break;
                    case 27:
                        if (gDemoScreen.isDemoRecording()) { // camera
                            sp.setXrepeat(64);
                            sp.setYrepeat(64);
                            sp.setCstat(sp.getCstat() & 32767);
                        }
                        break;
                    case 12:
                    case 47:
                    case 48: //RA
                        hittype[i].temp_data[1] = sec.getFloorshade();
                        hittype[i].temp_data[2] = sec.getCeilingshade();
                        break;

                    case 13:

                        hittype[i].temp_data[0] = sec.getCeilingz();
                        hittype[i].temp_data[1] = sec.getFloorz();

                        if (klabs(hittype[i].temp_data[0] - sp.getZ()) < klabs(hittype[i].temp_data[1] - sp.getZ())) {
                            sp.setOwner(1);
                        } else {
                            sp.setOwner(0);
                        }

                        if (sp.getAng() == 512) {
                            if (sp.getOwner() != 0) {
                                sec.setCeilingz(sp.getZ());
                            } else {
                                sec.setFloorz(sp.getZ());
                            }
                        } else {
                            int z = sp.getZ();
                            sec.setFloorz(z);
                            sec.setCeilingz(z);
                        }

                        if ((sec.getCeilingstat() & 1) != 0) {
                            sec.setCeilingstat(sec.getCeilingstat() ^ 1);
                            hittype[i].temp_data[3] = 1;

                            if (sp.getOwner() == 0 && sp.getAng() == 512) {
                                sec.setCeilingstat(sec.getCeilingstat() ^ 1);
                                hittype[i].temp_data[3] = 0;
                            }

                            sec.setCeilingshade(sec.getFloorshade());

                            if (sp.getAng() == 512) {
                                for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                                    Wall wal = wn.get();
                                    Sector nextsec = boardService.getSector(wal.getNextsector());
                                    if (nextsec != null) {
                                        if ((nextsec.getCeilingstat() & 1) == 0) {
                                            sec.setCeilingpicnum(nextsec.getCeilingpicnum());
                                            sec.setCeilingshade(nextsec.getCeilingshade());
                                            break; // Leave earily
                                        }
                                    }
                                }
                            }
                        }

                        break;

                    case 17:

                        hittype[i].temp_data[2] = sec.getFloorz(); //Stopping loc

                        Sector s1 = boardService.getSector(engine.nextsectorneighborz(sect, sec.getFloorz(), -1, -1));
                        if (s1 != null) {
                            hittype[i].temp_data[3] = s1.getCeilingz();
                        }

                        Sector s2 = boardService.getSector(engine.nextsectorneighborz(sect, sec.getCeilingz(), 1, 1));
                        if (s2 != null) {
                            hittype[i].temp_data[4] = s2.getFloorz();
                        }

                        if (numplayers < 2) {
                            game.pInt.setceilinterpolate(sect, sec);
                            game.pInt.setfloorinterpolate(sect, sec);
                        }
                        break;

                    case 24:
                        sp.setYvel(sp.getYvel() << 1);
                    case 36:
                        break;
                    case 88:
                        hittype[i].temp_data[0] = sec.getFloorshade();
                        hittype[i].temp_data[1] = sec.getCeilingshade();


                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if (wal.getShade() > hittype[i].temp_data[2]) {
                                hittype[i].temp_data[2] = wal.getShade();
                            }
                        }

                        hittype[i].temp_data[3] = 1;
                        break;

                    case 20: {
                        // find the two most clostest wall x's and y's
                        long q = 0x7fffffff;

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            x = wal.getX();
                            y = wal.getY();

                            d = FindDistance2D(sp.getX() - x, sp.getY() - y);
                            if (d < q) {
                                q = d;
                                clostest = wn.getIndex();
                            }
                        }

                        hittype[i].temp_data[1] = clostest;

                        q = 0x7fffffff;

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            int s = wn.getIndex();
                            x = wal.getX();
                            y = wal.getY();
                            d = FindDistance2D(sp.getX() - x, sp.getY() - y);

                            if (d < q && s != hittype[i].temp_data[1]) {
                                q = d;
                                clostest = s;
                            }
                        }

                        hittype[i].temp_data[2] = clostest;
                    }

                    break;

                    case 3:

                        hittype[i].temp_data[3] = sec.getFloorshade();

                        sec.setFloorshade(sp.getShade());
                        sec.setCeilingshade(sp.getShade());

                        sp.setOwner((short) (sec.getCeilingpal() << 8));
                        sp.setOwner(sp.getOwner() | sec.getFloorpal());

                        //fix all the walls;

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if ((wal.getHitag() & 1) == 0) {
                                wal.setShade(sp.getShade());
                            }

                            Wall w2 = boardService.getWall(wal.getNextwall());
                            if ((wal.getCstat() & 2) != 0 && w2 != null) {
                                w2.setShade(sp.getShade());
                            }
                        }
                        break;

                    case 31:
                        hittype[i].temp_data[1] = sec.getFloorz();
                        if (sp.getAng() != 1536) {
                            sec.setFloorz(sp.getZ());
                        }

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if (wal.getHitag() == 0) {
                                wal.setHitag(9999);
                            }
                        }

                        game.pInt.setfloorinterpolate(sect, sec);  //floorz

                        break;
                    case 32:
                        hittype[i].temp_data[1] = sec.getCeilingz();
                        hittype[i].temp_data[2] = sp.getHitag();
                        if (sp.getAng() != 1536) {
                            sec.setCeilingz(sp.getZ());
                        }

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if (wal.getHitag() == 0) {
                                wal.setHitag(9999);
                            }
                        }

                        game.pInt.setceilinterpolate(sect, sec);  //ceiling

                        break;

                    case 4: //Flashing lights

                        hittype[i].temp_data[2] = sec.getFloorshade();

                        sp.setOwner((short) (sec.getCeilingpal() << 8));
                        sp.setOwner(sp.getOwner() | sec.getFloorpal());

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if (wal.getShade() > hittype[i].temp_data[3]) {
                                hittype[i].temp_data[3] = wal.getShade();
                            }
                        }

                        break;

                    case 9:
                        if (sec.getLotag() != 0 && klabs(sec.getCeilingz() - sp.getZ()) > 1024) {
                            sec.setLotag(sec.getLotag() | 32768); // If its open
                        }
                    case 8:
                        //First, get the ceiling-floor shade

                        if (sec.getLotag() != 0) {
                            if ((sec.getCeilingz() - sp.getZ()) > 1024) {
                                sec.setLotag(sec.getLotag() | 0x8000);
                            }
                        }

                        hittype[i].temp_data[0] = sec.getFloorshade();
                        hittype[i].temp_data[1] = sec.getCeilingshade();

                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            if (wal.getShade() > hittype[i].temp_data[2]) {
                                hittype[i].temp_data[2] = wal.getShade();
                            }
                        }

                        hittype[i].temp_data[3] = 1; //Take Out;

                        break;

                    case 11://Pivitor rotater
                        if (sp.getAng() > 1024) {
                            hittype[i].temp_data[3] = 2;
                        } else {
                            hittype[i].temp_data[3] = -2;
                        }
                    case 0:
                    case 2://Earthquakemakers
                    case 5://Boss Creature
                    case 6://Subway
                    case 14://Caboos
                    case 15://Subwaytype sliding door
                    case 16://That rotating blocker reactor thing
                    case 26://ESCELATOR
                    case 30://No rotational subways

                        if (sp.getLotag() == 0) {
                            if (sec.getLotag() == 30) {
                                if (sp.getPal() != 0) {
                                    sp.setClipdist(1);
                                } else {
                                    sp.setClipdist(0);
                                }
                                hittype[i].temp_data[3] = sec.getFloorz();
                                sec.setHitag(i);
                            }

                            int owner;
                            for (owner = 0; owner < boardService.getSpriteCount(); owner++) {
                                Sprite spo = boardService.getSprite(owner);
                                if (spo != null && spo.getStatnum() < MAXSTATUS) {
                                    if (spo.getPicnum() == SECTOREFFECTOR && spo.getLotag() == 1
                                            && spo.getHitag() == sp.getHitag()) {
                                        if (sp.getAng() == 512) {
                                            sp.setX(spo.getX());
                                            sp.setY(spo.getY());
                                        }
                                        break;
                                    }
                                }
                            }

                            if (owner == boardService.getSpriteCount()) {
                                Console.out.println("Found lonely Sector Effector (lotag 0) at (" + sp.getX() + "," + sp.getY() + ")",
                                        OsdColor.RED);
                                break;
                            }
                            sp.setOwner(owner);
                        }

                        hittype[i].temp_data[1] = tempwallptr;
                        for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                            Wall wal = wn.get();
                            msx[tempwallptr] = wal.getX() - sp.getX();
                            msy[tempwallptr] = wal.getY() - sp.getY();
                            tempwallptr++;
                            if (tempwallptr > 2047) {
                                Console.out.println("Too many moving sectors at (" + wal.getX() + "," + wal.getY() + ")",
                                        OsdColor.RED);
                                break;
                            }
                        }
                        if (sp.getLotag() == 30 || sp.getLotag() == 6 || sp.getLotag() == 14 || sp.getLotag() == 5) {

                            if (sec.getHitag() == -1) {
                                sp.setExtra(0);
                            } else {
                                sp.setExtra(1);
                            }

                            sec.setHitag(i);

                            int found = 0;
                            int s = -1;
                            for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                                Wall wal = wn.get();
                                s = wn.getIndex();

                                Sector ns = boardService.getSector(wal.getNextsector());
                                if (ns != null
                                        && ns.getHitag() == 0
                                        && (ns.getLotag() < 3
                                        || ns.getLotag() == 160)) {
                                    s = wal.getNextsector();
                                    found = 1;
                                    break;
                                }
                            }

                            if (found == 0) {
                                Console.out.println("Subway found no zero'd sectors with locators\nat (" + sp.getX() + "," + sp.getY() + ")",
                                        OsdColor.RED);
                                break;
                            }

                            sp.setOwner(-1);
                            hittype[i].temp_data[0] = s;

                            if (sp.getLotag() != 30) {
                                hittype[i].temp_data[3] = sp.getHitag();
                            }
                        } else if (sp.getLotag() == 16) {
                            hittype[i].temp_data[3] = sec.getCeilingz();
                        } else if (sp.getLotag() == 26) {
                            hittype[i].temp_data[3] = sp.getX();
                            hittype[i].temp_data[4] = sp.getY();
                            if (sp.getShade() == sec.getFloorshade()) //UP
                            {
                                sp.setZvel(-256);
                            } else {
                                sp.setZvel(256);
                            }

                            sp.setShade(0);
                        } else if (sp.getLotag() == 2) {
                            hittype[i].temp_data[5] = sec.getFloorheinum();
                            sec.setFloorheinum(0);
                        }
                }

                switch (sp.getLotag()) {
                    case 6:
                    case 14:
                        int si = callsound(sect, i);
                        if (si == -1) {
                            if (sec.getFloorpal() == 7) {
                                si = 456;
                            } else {
                                si = SUBWAY;
                            }
                        }
                        hittype[i].lastvx = si;
                    case 30:
                        if (numplayers > 1 || mFakeMultiplayer) {
                            break;
                        }
                    case 0:
                    case 1:
                    case 5:
                    case 11:
                    case 15:
                    case 16:
                    case 26:
                        setsectinterpolate(i);
                        break;
                }

                switch (sp.getLotag()) {
                    case 150:
                    case 151:

                    case 152:
                    case 153:
                        engine.changespritestat(i, (short) 15);
                        break;
                    default:
                        engine.changespritestat(i, (short) 3);
                        break;
                }

                break;

            case SEENINE:
            case OOZFILTER:

                sp.setShade(-16);
                if (sp.getXrepeat() <= 8) {
                    sp.setCstat((short) 32768);
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                } else {
                    sp.setCstat(1 + 256);
                }
                sp.setExtra((short) (currentGame.getCON().impact_damage << 2));
                sp.setOwner(i);

                engine.changespritestat(i, (short) 6);
                break;

            case HENSTAND:
                sp.setCstat(257);
                sp.setClipdist(48);
                sp.setXrepeat(21);
                sp.setYrepeat(15);
                engine.changespritestat(i, (short) 2);
                break;

            case 4770: //RA elvisbubba
            case 6659: //RA baba
                if (currentGame.getCON().type != RRRA) {
                    break;
                }
            case 4163:
            case 4249:
            case 4504:
            case 4650:
            case 4862:
            case 4946:
            case 5015:
            case 5121:
            case 5377:
                hittype[i].actorstayput = sp.getSectnum();
            case 256:
            case 264:
            case 1344:
            case 1930:
            case 4147:
            case 4162:
            case 4260:
            case 4352:
            case 4649:
            case 4861:
            case MOSQUITO:
            case 4945:
            case MINION:
            case 5270:
            case 5274:
            case 5278:
            case 5282:
            case 5286:
            case 5317:
            case 5376:
            case 5501:
            case 5635:
            case MINIONUFO: //RA
            case 5890: //RA biker
            case 5891: //RA biker
            case 5995: //RA biker
            case 6225: //RA biker + baba
            case 6401: //RA biker + baba + moto
            case 6658: //RA baba
            case 7030: //RA banjocoot
            case 7035: //RA banjobilly
            case 7192: //RA board
            case 7199: //RA henboard
            case 7206: //RA bababoard
            case 7280: //RA deep
            case 8035: //RA rock
            case 8036: //RA rock2
            case 8663: //RA green rock
            case 8705: //RA deep2

                if (currentGame.getCON().type != RRRA && (sp.getPicnum() == MINIONUFO || sp.getPicnum() == 5890 || sp.getPicnum() == 5891 || sp.getPicnum() == 5995 || sp.getPicnum() == 6225 || sp.getPicnum() == 6401 || sp.getPicnum() == 6658 || sp.getPicnum() == 7030 || sp.getPicnum() == 7035 || sp.getPicnum() == 7192 || sp.getPicnum() == 7199 || sp.getPicnum() == 7206 || sp.getPicnum() == 7280 || sp.getPicnum() == 8035 || sp.getPicnum() == 8036 || sp.getPicnum() == 8663 || sp.getPicnum() == 8705)) {
                    break;
                }

                ArtEntry pic = engine.getTile(sp.getPicnum());
                switch (sp.getPicnum()) {
                    case 5376:
                    case 5377:
                    case 7030:
                        sp.setXrepeat(24);
                        sp.setYrepeat(18);
                        sp.setClipdist(4 * (pic.getWidth() * sp.getXrepeat() >> 7));
                        break;
                    case 5635:
                        if (sp.getPal() == 34) {
                            sp.setXrepeat(22);
                            sp.setYrepeat(21);
                        } else {
                            sp.setXrepeat(22);
                            sp.setYrepeat(20);
                        }
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 5270:
                    case 5274:
                    case 5278:
                    case 5282:
                    case 5286:
                        sp.setExtra(50);
                    case 5317:
                    case 4409:
                    case 4410:
                    case 4429:
                    case 4649:
                    case 4650:
                        sp.setXrepeat(32);
                        sp.setYrepeat(32);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case MOSQUITO:
                        sp.setXrepeat(14);
                        sp.setYrepeat(7);
                        sp.setClipdist(128);
                        break;
                    case 5015:
                    case 7199:
                        sp.setXrepeat(48);
                        sp.setYrepeat(48);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 1930:
                        sp.setXrepeat(64);
                        sp.setYrepeat(128);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        sp.setCstat(2);
                        break;
                    case 4260:
                    case 4945:
                    case MINION:
                    case MINION + 1:
                        sp.setXrepeat(16);
                        sp.setYrepeat(16);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        if (sp.getPicnum() == MINION || sp.getPicnum() == MINION + 1) {
                            if (ps[connecthead].isSwamp) {
                                sp.setPal(8);
                            }
                        }
                        break;
                    case 4352:
                        sp.setXrepeat(24);
                        sp.setYrepeat(22);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 4861:
                    case 4862:
                    case 4897:
                        if (sp.getPal() != 35) {
                            sp.setXrepeat(21);
                            sp.setYrepeat(15);
                            sp.setClipdist(64);
                            break;
                        }
                        sp.setXrepeat(42);
                        sp.setYrepeat(30);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 4147:
                    case 4162:
                    case 4163:
                    case 4249:
                    case 4504:
                    case 7035:
                    case 4770:
                        sp.setXrepeat(25);
                        sp.setYrepeat(21);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 6658:
                    case 6659:
                        sp.setXrepeat(20);
                        sp.setYrepeat(20);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 7206:
                        sp.setXrepeat(32);
                        sp.setYrepeat(32);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 7280:
                        sp.setXrepeat(18);
                        sp.setYrepeat(18);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 8035:
                    case 8036:
                        sp.setXrepeat(64);
                        sp.setYrepeat(64);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 8663:
                        sp.setXrepeat(64);
                        sp.setYrepeat(64);
                        sp.setCstat(514);
                        sp.setX(sp.getX() + (engine.krand() & 0x7FF) - 1024);
                        sp.setY(sp.getY() + (engine.krand() & 0x7FF) - 1024);
                        sp.setZ(sp.getZ() + (engine.krand() & 0x7FF) - 1024);
                        break;
                    case 8705:
                        if (sp.getPal() == 30) {
                            sp.setXrepeat(26);
                            sp.setYrepeat(26);
                            sp.setClipdist(75);
                        } else if (sp.getPal() == 31) {
                            sp.setXrepeat(36);
                            sp.setYrepeat(36);
                            sp.setClipdist(100);
                        } else {
                            sp.setXrepeat(50);
                            sp.setYrepeat(50);
                            sp.setClipdist(100);
                        }
                        break;
                    case 7192:
                        sp.setXrepeat(16);
                        sp.setYrepeat(16);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 5890:
                    case 5891:
                    case 6401:
                        sp.setXrepeat(28);
                        sp.setYrepeat(22);
                        sp.setClipdist(72);
                        break;
                    case 5995:
                        sp.setXrepeat(28);
                        sp.setYrepeat(22);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    case 6225:
                        sp.setXrepeat(26);
                        sp.setYrepeat(26);
                        sp.setClipdist((pic.getWidth() * sp.getXrepeat()) >> 7);
                        break;
                    default:
                        sp.setXrepeat(40);
                        sp.setYrepeat(40);
                        break;
                }

                if (spawner != null) {
                    sp.setLotag(0);
                }

                if ((sp.getLotag() > ud.player_skill) || ud.monsters_off) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                    break;
                } else {
                    makeitfall(currentGame.getCON(), i);

                    if (sp.getPicnum() == RAT) {
                        sp.setAng((short) (engine.krand() & 2047));
                        sp.setXrepeat(48);
                        sp.setYrepeat(48);
                        sp.setCstat(0);
                    } else {
                        sp.setCstat(sp.getCstat() | 257);

                        if (sp.getPicnum() != 5501 && checkaddkills(sp)) {
                            ps[connecthead].max_actors_killed++;
                        }
                    }

                    if (spawner != null) {
                        hittype[i].timetosleep = 0;
                        check_fta_sounds(i);
                        engine.changespritestat(i, (short) 1);
                    } else {
                        engine.changespritestat(i, (short) 2);
                    }
                }
                break;
            case TONGUE:
                if (spawner != null) {
                    sp.setAng(spawner.getAng());
                }
                sp.setZ(sp.getZ() - (38 << 8));
                sp.setZvel((short) (256 - (engine.krand() & 511)));
                sp.setXvel((short) (64 - (engine.krand() & 127)));
                engine.changespritestat(i, (short) 4);
                break;
            case OOZ:
                sp.setShade(-12);

                if (spawner != null) {
                    if (spawner.getPicnum() == 1304) {
                        sp.setPal(8);
                    }
                    insertspriteq(i);
                }

                engine.changespritestat(i, (short) 1);

                getglobalz(i);

                int z = (hittype[i].floorz - hittype[i].ceilingz) >> 9;

                sp.setYrepeat(z);
                sp.setXrepeat((short) (25 - (z >> 1)));
                sp.setCstat(sp.getCstat() | (engine.krand() & 4));

                break;
            case STEAM:
                if (spawner != null) {
                    sp.setAng(spawner.getAng());
                    sp.setCstat(16 + 128 + 2);
                    sp.setXrepeat(1);
                    sp.setYrepeat(1);
                    sp.setXvel(-8);
                    ssp(i, CLIPMASK0);
                }
            case CEILINGSTEAM:
                engine.changespritestat(i, (short) 6);
                break;
            case TRANSPORTERSTAR:
            case TRANSPORTERBEAM:
                if (spawner == null) {
                    break;
                }

                if (sp.getPicnum() == TRANSPORTERBEAM) {
                    sp.setXrepeat(31);
                    sp.setYrepeat(1);
                    Sector ssec = boardService.getSector(spawner.getSectnum());
                    if (ssec != null) {
                        sp.setZ(ssec.getFloorz() - (40 << 8));
                    }
                } else {
                    if (spawner.getStatnum() == 4) {
                        sp.setXrepeat(8);
                        sp.setYrepeat(8);
                    } else {
                        sp.setXrepeat(48);
                        sp.setYrepeat(64);
                        if (spawner.getStatnum() == 10 || badguy(spawner)) {
                            sp.setZ(sp.getZ() - (32 << 8));
                        }
                    }
                }

                sp.setShade(-127);
                sp.setCstat(128 | 2);
                sp.setAng(spawner.getAng());

                sp.setXvel(128);
                engine.changespritestat(i, (short) 5);
                ssp(i, CLIPMASK0);
                engine.setsprite(i, sp.getX(), sp.getY(), sp.getZ());
                break;
            case BLOOD:
                sp.setXrepeat(4);
                sp.setYrepeat(4);
                sp.setZ(sp.getZ() - (26 << 8));
                engine.changespritestat(i, (short) 5);
                break;
            case 2264:
                int cs = sp.getCstat() & 60;
                sp.setCstat(cs | 1);
                engine.changespritestat(i, (short) 0);
                break;
            case FRAMEEFFECT1:
                if (spawner != null) {
                    sp.setXrepeat(spawner.getXrepeat());
                    sp.setYrepeat(spawner.getYrepeat());
                    if (spawner.getPicnum() == APLAYER) {
                        hittype[i].temp_data[1] = 1554;
                    } else {
                        hittype[i].temp_data[1] = spawner.getPicnum();
                    }
                } else {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                }

                engine.changespritestat(i, (short) 5);
                break;
            case 1098:
            case 1100:
            case 2121:
            case 2122:
                sp.setLotag(1);
                sp.setClipdist(8);
                sp.setOwner(i);
                sp.setCstat(sp.getCstat() | 257);
                break;
            case FORCESPHERE:
                if (spawner == null) {
                    sp.setCstat((short) 32768);
                    engine.changespritestat(i, (short) 2);
                } else {
                    sp.setXrepeat(1);
                    sp.setYrepeat(1);
                    engine.changespritestat(i, (short) 5);
                }
                break;
            case SHELL: //From the player
            case SHOTGUNSHELL:
                if (spawner != null) {
                    short snum, a;

                    if (spawner.getPicnum() == APLAYER) {
                        snum = spawner.getYvel();
                        a = (short) (ps[snum].ang - (engine.krand() & 63) + 8);  //Fine tune

                        hittype[i].temp_data[0] = engine.krand() & 1;
                        if (sp.getPicnum() == SHOTGUNSHELL) {
                            sp.setZ((6 << 8) + ps[snum].pyoff + ps[snum].posz - (((ps[snum].horizoff + (int) ps[snum].horiz - 100) << 4) - 1600));
                        } else {
                            sp.setZ((3 << 8) + ps[snum].pyoff + ps[snum].posz - (((ps[snum].horizoff + (int) ps[snum].horiz - 100) << 4) - 1600));
                        }
                        sp.setZvel((short) -(engine.krand() & 255));
                    } else {
                        a = sp.getAng();
                        sp.setZ(spawner.getZ() - 8448);
                    }

                    sp.setX(spawner.getX() + (EngineUtils.sin((a + 512) & 2047) >> 7));
                    sp.setY(spawner.getY() + (EngineUtils.sin(a & 2047) >> 7));

                    sp.setShade(-8);

                    sp.setAng((short) (a - 512));
                    sp.setXvel(20);
                    if (sp.getPicnum() == SHELL) {
                        sp.setXrepeat(2);
                        sp.setYrepeat(2);
                    } else {
                        sp.setXrepeat(4);
                        sp.setYrepeat(4);
                    }

                    engine.changespritestat(i, (short) 5);
                }
                break;
            case LOAFTILE:
                sp.setCstat(257);
                sp.setClipdist(8);
                sp.setXrepeat(12);
                sp.setYrepeat(10);
                sp.setXvel(32);
                engine.changespritestat(i, (short) 1);
                break;
            case BONELESSTILE:
                sp.setCstat(257);
                sp.setClipdist(8);
                sp.setXrepeat(17);
                sp.setYrepeat(12);
                sp.setXvel(32);
                engine.changespritestat(i, (short) 1);
                break;
            case HEAD1TILE:
                sp.setCstat(257);
                sp.setClipdist(8);
                sp.setXrepeat(13);
                sp.setYrepeat(10);
                sp.setXvel(0);
                engine.changespritestat(i, (short) 1);
                break;
            case CHICKENATILE:
            case CHICKENBTILE:
            case CHICKENCTILE:
                sp.setCstat(257);
                sp.setClipdist(8);
                sp.setXrepeat(32);
                sp.setYrepeat(26);
                sp.setXvel(32);
                engine.changespritestat(i, (short) 1);
                break;
            case NUGGETTILE:
                sp.setCstat(257);
                sp.setClipdist(2);
                sp.setXrepeat(8);
                sp.setYrepeat(6);
                sp.setXvel(16);
                engine.changespritestat(i, (short) 1);
                break;
            case BROASTEDTILE:
                sp.setCstat(257);
                sp.setClipdist(8);
                sp.setXrepeat(13);
                sp.setYrepeat(13);
                sp.setXvel(16);
                engine.changespritestat(i, (short) 1);
                break;
            case 3410:
                sp.setExtra(0);
                engine.changespritestat(i, (short) 107);
                break;
            case BOWLINGBALL:
                sp.setCstat(256);
                sp.setClipdist(64);
                sp.setXrepeat(11);
                sp.setYrepeat(9);
                engine.changespritestat(i, (short) 2);
                break;
            case 3440:
                sp.setCstat(257);
                sp.setClipdist(48);
                sp.setXrepeat(23);
                sp.setYrepeat(23);
                engine.changespritestat(i, (short) 2);
                break;
            case APLAYER: {
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                int coop = ud.coop;
                if (coop == 2) {
                    coop = 0;
                }

                if (ud.multimode < 2 || coop != sp.getLotag()) {
                    engine.changespritestat(i, (short) 5);
                } else {
                    engine.changespritestat(i, (short) 10);
                }
                break;
            }
            case PLAYERONWATER:
                if (spawner != null) {
                    sp.setXrepeat(spawner.getXrepeat());
                    sp.setYrepeat(spawner.getYrepeat());
                    sp.setZvel(128);
                    if (sec.getLotag() != 2) {
                        sp.setCstat(sp.getCstat() | 32768);
                    }
                }
                engine.changespritestat(i, (short) 13);
                break;
            case 1115:
            case 1168:
            case 5581:
            case 5583:
                sp.setYvel(sp.getHitag());
                sp.setHitag(-1);
            case QUEBALL:
            case STRIPEBALL:
                if (sp.getPicnum() == QUEBALL || sp.getPicnum() == STRIPEBALL) {
                    sp.setCstat(256);
                    sp.setClipdist(8);
                } else {
                    sp.setCstat(sp.getCstat() | 257);
                    sp.setClipdist(32);
                }

                engine.changespritestat(i, (short) 2);
                break;

            //RA
            case MOTORCYCLE:
                if (ud.multimode < 2 && sp.getPal() == 1) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                } else {
                    sp.setPal(0);
                    sp.setXrepeat(18);
                    sp.setYrepeat(18);
                    sp.setClipdist(engine.getTile(sp.getPicnum()).getWidth() * sp.getXrepeat() >> 7);
                    sp.setOwner(100);
                    sp.setCstat(sp.getCstat() | 257);
                    sp.setLotag(1);
                    engine.changespritestat(i, (short) 1);
                }
                break;
            case SWAMPBUGGY:
                if (ud.multimode < 2 && sp.getPal() == 1) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                } else {
                    sp.setPal(0);
                    sp.setXrepeat(32);
                    sp.setYrepeat(32);
                    sp.setClipdist(engine.getTile(sp.getPicnum()).getWidth() * sp.getXrepeat() >> 7);
                    sp.setOwner(20);
                    sp.setCstat(sp.getCstat() | 257);
                    sp.setLotag(1);
                    engine.changespritestat(i, (short) 1);
                }
                break;
            case AIRPLANE:
                sp.setXrepeat(64);
                sp.setYrepeat(64);
                sp.setExtra(sp.getLotag());
                sp.setCstat(sp.getCstat() | 257);
                engine.changespritestat(i, (short) 116);
                break;
            case 8192:
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                    ps[p].isSwamp = true;
                }
                break;
            case 1083:
            case 1134:
            case 1135:
            case 1136:
            case 1137:
            case 1138:
                sp.setExtra(1);
                if (currentGame.getCON().camerashitable != 0) {
                    sp.setCstat(257);
                } else {
                    sp.setCstat(0);
                }
                if (ud.multimode < 2 && sp.getPal() != 0) {
                    sp.setXrepeat(0);
                    sp.setYrepeat(0);
                    engine.changespritestat(i, (short) 5);
                } else {
                    sp.setPal(0);
                    if (sp.getPicnum() != 1083) {
                        sp.setPicnum(1134);
                        engine.changespritestat(i, (short) 1);
                    }
                }
                break;
            case 6144:
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                    ps[p].isSea = true;
                }
                break;
            case 4956:
                sp.setXrepeat(16);
                sp.setYrepeat(16);
                sp.setClipdist(0);
                sp.setExtra(0);
                sp.setCstat(0);
                engine.changespritestat(i, (short) 121);
                break;
            case 7424:
                sp.setExtra(0);
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                engine.changespritestat(i, (short) 11);
                break;
            case 7936:
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                engine.getPaletteManager().applyfog(2);
                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                    ps[p].fogtype = 2;
                }
                break;
            case 8099:
                sp.setLotag(5);
                sp.setClipdist(0);
                engine.changespritestat(i, (short) 123);
                break;
            case 8165:
                sp.setLotag(1);
                sp.setClipdist(0);
                sp.setOwner(i);
                sp.setExtra(0);
                engine.changespritestat(i, (short) 115);
                break;
            case 8193:
                sp.setXrepeat(0);
                sp.setYrepeat(0);
                for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                    ps[p].field_601 = 1;
                }
                break;
            case 8448:
            case 8704:
                sp.setLotag(1);
                sp.setClipdist(0);
                break;
            case 8487:
            case 8489:
                sp.setXrepeat(32);
                sp.setYrepeat(32);
                sp.setExtra(0);
                sp.setCstat(sp.getCstat() | 257);
                sp.setHitag(0);
                engine.changespritestat(i, (short) 117);
                break;
            case 8593:
                sp.setLotag(1);
                sp.setClipdist(0);
                sp.setOwner(i);
                sp.setExtra(0);
                engine.changespritestat(i, (short) 122);
                break;
        }
        game.pInt.setsprinterpolate(i, sp);
        return i;
    }

    public static void rr_lotsofglass(final int i, int wallnum, int n) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        Wall wal = boardService.getWall(wallnum);
        if (wal == null) {
            for (int j = n - 1; j >= 0; j--) {
                int a = (sp.getAng() - 256 + (engine.krand() & 511) + 1024);
                int vz = 1024 - (engine.krand() & 1023);
                EGS(sp.getSectnum(), sp.getX(), sp.getY(), sp.getZ(), GLASSPIECES + (j % 3), -32, 36, 36, a,
                        32 + (engine.krand() & 63), vz, i, 5);
            }
            return;
        }

        int j = n + 1;

        int x1 = wal.getX();
        int y1 = wal.getY();
        int xv = wal.getWall2().getX() - x1;
        int yv = wal.getWall2().getY() - y1;

        x1 -= ksgn(yv);
        y1 += ksgn(xv);

        xv /= j;
        yv /= j;

        int sect = -1;
        for (j = n; j > 0; j--) {
            x1 += xv;
            y1 += yv;

            sect = engine.updatesector(x1, y1, sect);
            Sector sec = boardService.getSector(sect);
            if (sec != null) {
                int z = sec.getFloorz() - (engine.krand() & (klabs(sec.getCeilingz() - sec.getFloorz())));
                if (z < -(32 << 8) || z > (32 << 8)) {
                    z = sp.getZ() - (32 << 8) + (engine.krand() & ((64 << 8) - 1));
                }
                int a = (sp.getAng() - 1024);
                int vz = -(engine.krand() & 1023);
                EGS(sp.getSectnum(), x1, y1, z, 2021, -32, 36, 36, a, 32 + (engine.krand() & 63), vz, i, 5);
            }
        }
    }

    @CommonPart
    public static void lotsofglass(int i, int wallnum, int n) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        Wall wal = boardService.getWall(wallnum);
        if (wal == null) {
            for (int j = n - 1; j >= 0; j--) {
                int a = (sp.getAng() - 256 + (engine.krand() & 511) + 1024);
                int vz = 1024 - (engine.krand() & 1023);
                EGS(sp.getSectnum(), sp.getX(), sp.getY(), sp.getZ(), GLASSPIECES + (j % 3), -32, 36, 36, a,
                        32 + (engine.krand() & 63), vz, i, 5);

            }
            return;
        }

        int j = n + 1;

        int x1 = wal.getX();
        int y1 = wal.getY();
        int xv = wal.getWall2().getX() - x1;
        int yv = wal.getWall2().getY() - y1;

        x1 -= ksgn(yv);
        y1 += ksgn(xv);

        xv /= j;
        yv /= j;

        int sect = -1;
        for (j = n; j > 0; j--) {
            x1 += xv;
            y1 += yv;

            sect = engine.updatesector(x1, y1, sect);
            Sector sec = boardService.getSector(sect);
            if (sec != null) {
                int z = sec.getFloorz() - (engine.krand() & (klabs(sec.getCeilingz() - sec.getFloorz())));
                if (z < -(32 << 8) || z > (32 << 8)) {
                    z = sp.getZ() - (32 << 8) + (engine.krand() & ((64 << 8) - 1));
                }

                int a = (sp.getAng() - 1024);
                int vz = -(engine.krand() & 1023);
                EGS(sp.getSectnum(), x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, 32 + (engine.krand() & 63), vz, i, 5);
            }
        }
    }

    @CommonPart
    public static void spriteglass(final int i, int n) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        for (int j = n; j > 0; j--) {
            int a = engine.krand() & 2047;
            int z = sp.getZ() - ((engine.krand() & 16) << 8);
            int vz = -512 - (engine.krand() & 2047);
            int ve = 32 + (engine.krand() & 63);
            int k = EGS(sp.getSectnum(), sp.getX(), sp.getY(), z, GLASSPIECES + (j % 3), engine.krand() & 15, 36, 36,
                    a, ve, vz, i, 5);
            Sprite s = boardService.getSprite(k);
            if (s != null) {
                s.setPal(sp.getPal());
            }
        }
    }

    @CommonPart
    public static void ceilingglass(int i, int sectnum, int n) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        Sector sec = boardService.getSector(sectnum);
        if (sec == null) {
            return;
        }


        int startwall = sec.getWallptr();
        int endwall = (startwall + sec.getWallnum());

        for (int s = startwall; s < (endwall - 1); s++) {
            Wall w1 = boardService.getWall(s);
            Wall w2 = boardService.getWall(s + 1);
            if (w1 == null || w2 == null) {
                continue;
            }

            int x1 = w1.getX();
            int y1 = w1.getY();

            int xv = (w2.getX() - x1) / (n + 1);
            int yv = (w2.getY() - y1) / (n + 1);

            for (int j = n; j > 0; j--) {
                x1 += xv;
                y1 += yv;
                int a = (engine.krand() & 2047);
                int z = sec.getCeilingz() + ((engine.krand() & 15) << 8);
                EGS(sectnum, x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, (engine.krand() & 31), 0, i, 5);
            }
        }
    }

    @CommonPart
    public static void lotsofcolourglass(int i, int wallnum, int n) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        Wall wal = boardService.getWall(wallnum);
        if (wal == null) {
            for (int j = n - 1; j >= 0; j--) {
                int a =  (engine.krand() & 2047);
                int vz = 1024 - (engine.krand() & 2047);
                int ve = 32 + (engine.krand() & 63);
                int k = EGS(sp.getSectnum(), sp.getX(), sp.getY(), sp.getZ() - (engine.krand() & (63 << 8)), GLASSPIECES + (j % 3), -32, 36, 36, a, ve, vz, i, 5);

                Sprite s = boardService.getSprite(k);
                if (s != null) {
                    s.setPal((short) (engine.krand() & 15));
                }
            }
            return;
        }

        int j = n + 1;
        int x1 = wal.getX();
        int y1 = wal.getY();
        int xv = (wal.getWall2().getX() - wal.getX()) / j;
        int yv = (wal.getWall2().getY() - wal.getY()) / j;
        int sect = -1;

        for (j = n; j > 0; j--) {
            x1 += xv;
            y1 += yv;

            sect = engine.updatesector(x1, y1, sect);
            Sector sec = boardService.getSector(sect);
            if (sec == null) {
                continue;
            }

            int z = sec.getFloorz() - (engine.krand() & (klabs(sec.getCeilingz() - sec.getFloorz())));
            if (z < -(32 << 8) || z > (32 << 8)) {
                z = sp.getZ() - (32 << 8) + (engine.krand() & ((64 << 8) - 1));
            }
            int a = (sp.getAng() - 1024);
            int zv = -(engine.krand() & 2047);
            int k = EGS(sp.getSectnum(), x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, 32 + (engine.krand() & 63), zv,
                    i, 5);

            Sprite s = boardService.getSprite(k);
            if (s != null) {
                s.setPal((short) (engine.krand() & 7));
            }
        }
    }

    public static void lotsofmoney(Sprite s, int n) {
        for (int i = n; i > 0; i--) {
            int ang = engine.krand() & 2047;
            int sz = s.getZ() - (engine.krand() % (47 << 8));
            int j = EGS(s.getSectnum(), s.getX(), s.getY(), sz, FEATHERS, -32, 8, 8, ang, 0, 0, 0, (short) 5);
            Sprite sp = boardService.getSprite(j);
            if (sp != null) {
                sp.setCstat((engine.krand() & 12));
            }
        }
    }

    public static void guts(Sprite s, int gtype, int n, int p) {
        if (boardService.getSector(s.getSectnum()) == null) {
            return;
        }

        char sx, sy;
        if (badguy(s) && s.getXrepeat() < 16) {
            sx = sy = 8;
        } else {
            sx = sy = 32;
        }

        int gutz = s.getZ() - (8 << 8);
        int floorz = engine.getflorzofslope(s.getSectnum(), s.getX(), s.getY());

        if (gutz > (floorz - (8 << 8))) {
            gutz = floorz - (8 << 8);
        }

        int pal = 0;
        if (badguy(s) && s.getPal() == 6) {
            pal = 6;
        } else {
            if (currentGame.getCON().type == RRRA) {
                if (s.getPicnum() == MINION && s.getPal() == 8) {
                    pal = 8;
                } else if (s.getPicnum() == MINION && s.getPal() == 19) {
                    pal = 19;
                }
            }
        }

        for (int j = 0; j < n; j++) {
            int a = engine.krand() & 2047;
            int zv = -512 - (engine.krand() & 2047);
            int ve = 48 + (engine.krand() & 31);

            int esz = gutz - (engine.krand() & 8191);
            int esy = s.getY() + (engine.krand() & 255) - 128;
            int esx = s.getX() + (engine.krand() & 255) - 128;

            int i = EGS(s.getSectnum(), esx, esy, esz, gtype, -32, sx >> 1, sy >> 1, a, ve, zv, ps[p].i, (short) 5);

            Sprite sp = boardService.getSprite(i);
            if (sp != null) {
                switch (pal) {
                    case 6:
                        sp.setPal(6);
                        break;
                    case 8:
                        sp.setPal(8);
                        break;
                    case 19:
                        sp.setPal(19);
                        break;
                }
            }
        }
    }

    public static void gutsdir(Sprite s, int gtype, int n, int p) {
        char sx, sy;
        if (badguy(s) && s.getXrepeat() < 16) {
            sx = sy = 8;
        } else {
            sx = sy = 32;
        }

        int gutz = s.getZ() - (8 << 8);
        int floorz = engine.getflorzofslope(s.getSectnum(), s.getX(), s.getY());

        if (gutz > (floorz - (8 << 8))) {
            gutz = floorz - (8 << 8);
        }

        for (int j = 0; j < n; j++) {
            int a = engine.krand() & 2047;
            EGS(s.getSectnum(), s.getX(), s.getY(), gutz, gtype, -32, sx, sy, a, 256 + (engine.krand() & 127), -512 - (engine.krand() & 2047), ps[p].i, (short) 5);
        }
    }
}
