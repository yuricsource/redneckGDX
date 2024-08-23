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

import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Redneck.Types.ANIMATION;

import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.boardService;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.RSector.callsound;
import static ru.m210projects.Redneck.Types.ANIMATION.*;

public class Animate {

    //These variables are for animating x, y, or z-coordinates of sectors,
    //walls, or sprites (They are NOT to be used for changing the [].picnum's)
    //See the setanimation(), and getanimategoal() functions for more details.
    public static final int MAXANIMATES = 512;
    public static final ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
    public static int gAnimationCount = 0;

    public static void initanimations() {
        for (int i = 0; i < MAXANIMATES; i++) {
            gAnimationData[i] = new ANIMATION();
        }
    }

    public static Object getobject(int index, int type) {
        Object object = null;
        switch (type) {
            case WALLX:
            case WALLY:
                object = boardService.getWall(index);
                break;
            case FLOORZ:
            case CEILZ:
                object = boardService.getSector(index);
                break;
        }

        return object;
    }

    public static int getanimationgoal(Object object, int type) {
        int j = -1;
        for (int i = gAnimationCount - 1; i >= 0; i--) {
            if (object == gAnimationData[i].ptr && type == gAnimationData[i].type) {
                j = i;
                break;
            }
        }
        return (j);
    }

    public static int setanimation(int sector, int animptr, int thegoal, int thevel, int type) {
        if (gAnimationCount >= MAXANIMATES) {
            return -1;
        }

        Object object = getobject(animptr, type);
        if (object == null) {
            return -1;
        }

        int j = getanimationgoal(object, type);
        if (j == -1) {
            j = gAnimationCount;
        }

        ANIMATION gAnm = gAnimationData[j];
        gAnm.sect = sector;
        gAnm.ptr = object;
        gAnm.id = (short) animptr;
        gAnm.goal = thegoal;
        gAnm.vel = thevel;
        gAnm.type = (byte) type;

        if (j == gAnimationCount) {
            gAnimationCount++;
        }

        return j;
    }

    public static int getValue(Object obj, int type) {
        int j = 0;

        switch (type) {
            case WALLX:
                j = ((Wall) obj).getX();
                break;
            case WALLY:
                j = ((Wall) obj).getY();
                break;
            case FLOORZ:
                j = ((Sector) obj).getFloorz();
                break;
            case CEILZ:
                j = ((Sector) obj).getCeilingz();
                break;
        }

        return j;
    }

    public static void doanimations() {
        for (int i = gAnimationCount - 1; i >= 0; i--) {
            Interpolation gInt = game.pInt;
            ANIMATION gAnm = gAnimationData[i];
            Object obj = gAnm.ptr;

            int j = getValue(obj, gAnm.type);
            if (j == gAnm.goal) {
                gAnimationCount--;
                if (i != gAnimationCount) {
                    gAnm.copy(gAnimationData[gAnimationCount]);
                }
                int dasect = gAnm.sect;
                Sector sec = boardService.getSector(dasect);
                if (sec != null) {
                    if (sec.getLotag() == 18 || sec.getLotag() == 19) {
                        if (gAnm.type == CEILZ) {
                            continue;
                        }
                    }

                    if ((sec.getLotag() & 0xff) != 22) {
                        callsound(dasect, -1);
                    }
                }

                continue;
            }

            switch (gAnm.type) {
                case WALLX:
                    gInt.setwallinterpolate(gAnm.id, (Wall) obj);
                    if (j < gAnm.goal) {
                        ((Wall) obj).setX(Math.min(j + gAnm.vel * TICSPERFRAME, gAnm.goal));
                    } else {
                        ((Wall) obj).setX(Math.max(j - gAnm.vel * TICSPERFRAME, gAnm.goal));
                    }
                    break;
                case WALLY:
                    gInt.setwallinterpolate(gAnm.id, (Wall) obj);
                    if (j < gAnm.goal) {
                        ((Wall) obj).setY(Math.min(j + gAnm.vel * TICSPERFRAME, gAnm.goal));
                    } else {
                        ((Wall) obj).setY(Math.max(j - gAnm.vel * TICSPERFRAME, gAnm.goal));
                    }
                    break;
                case FLOORZ:
                    gInt.setfloorinterpolate(gAnm.id, (Sector) obj);

                    int vel = gAnm.vel * TICSPERFRAME;
                    if (j < gAnm.goal) {
                        j = Math.min(j + vel, gAnm.goal);
                    } else {
                        j = Math.max(j - vel, gAnm.goal);
                        vel = -vel;
                    }

                    int dasect = gAnm.sect;
                    Sector sec = boardService.getSector(dasect);
                    if (sec != null) {
                        for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
                            if (ps[p].cursectnum == dasect) {
                                if ((sec.getFloorz() - ps[p].posz) < (64 << 8)) {
                                    Sprite psp = boardService.getSprite(ps[p].i);
                                    if (psp != null && psp.getOwner() >= 0) {
                                        ps[p].posz += vel;
                                        ps[p].poszv = 0;
                                        if (p == myconnectindex && numplayers > 1) {
                                            game.net.predict.z += vel;
                                            game.net.predict.zvel = 0;
                                        }
                                    }
                                }
                            }
                        }

                        for (ListNode<Sprite> node = boardService.getSectNode(dasect); node != null; node = node.getNext()) {
                            int k = node.getIndex();
                            Sprite sp = node.get();
                            if (sp.getStatnum() != 3) {
                                game.pInt.setsprinterpolate(k, sp);
                                sp.setZ(sp.getZ() + vel);
                                hittype[k].floorz = sec.getFloorz() + vel;
                            }
                        }

                        ((Sector) obj).setFloorz(j);
                    }

                    break;
                case CEILZ:
                    gInt.setceilinterpolate(gAnm.id, (Sector) obj);
                    if (j < gAnm.goal) {
                        ((Sector) obj).setCeilingz(Math.min(j + gAnm.vel * TICSPERFRAME, gAnm.goal));
                    } else {
                        ((Sector) obj).setCeilingz(Math.max(j - gAnm.vel * TICSPERFRAME, gAnm.goal));
                    }
                    break;
            }
        }
    }
}
