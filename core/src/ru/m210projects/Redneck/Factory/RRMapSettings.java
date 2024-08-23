// This file is part of DukeGDX.
// Copyright (C) 2019-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;

import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.boardService;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.APLAYERTOP;

public class RRMapSettings implements IOverheadMapSettings {

    @Override
    public boolean isShowSprites(MapView view) {
        return view != MapView.Polygons;
    }

    @Override
    public boolean isShowFloorSprites() {
        return false;
    }

    @Override
    public boolean isShowRedWalls() {
        return true;
    }

    @Override
    public boolean isShowAllPlayers() {
        return ud.coop == 1;
    }

    @Override
    public boolean isSpriteVisible(MapView view, int index) {
        if (view == MapView.Polygons) {
            return true;
        }

        Sprite spr = boardService.getSprite(index);
        if (spr == null || spr.getCstat() == 257 || ps[screenpeek].i == index) {
            return false;
        }

        return (spr.getCstat() & 257) != 0;
    }

    @Override
    public boolean isWallVisible(int w, int s) {
        Wall wal = boardService.getWall(w);
        if (wal != null) // red wall
        {
            Sector sec = boardService.getSector(s);
            Sector nextsec = boardService.getSector(wal.getNextsector());
            if (nextsec != null && sec != null ) {
                Wall nextwal = boardService.getWall(wal.getNextwall());
                return (((nextsec.getCeilingz() != sec.getCeilingz() //
                        || nextsec.getFloorz() != sec.getFloorz() //
                        || nextwal != null && ((wal.getCstat() | nextwal.getCstat()) & (16 + 32)) != 0)
                        && (!isFullMap() && !show2dsector.getBit(wal.getNextsector()))));
            }
        }
        return true;
    }

    @Override
    public int getWallColor(int w, int s) {
        Wall wal = boardService.getWall(w);
        if (wal == null) {
            return 0;
        }

        Sector nextsec = boardService.getSector(wal.getNextsector());
        if (nextsec != null) {// red wall
            int col; // red
            if (!show2dsector.getBit(wal.getNextsector())) {
                col = 24;
            } else {
                col = -1;
            }
            return col;
        }

        return 24; // white wall
    }

    @Override
    public int getSpriteColor(int s) {
        Sprite spr = boardService.getSprite(s);
        if (spr == null) {
            return 0;
        }

        int col = 71; // cyan;
        if ((spr.getCstat() & 1) != 0) {
            col = 234; // magenta
        }

        return col;
    }

    @Override
    public int getPlayerSprite(int player) {
        if ((ud.scrollmode && player == screenpeek)) {
            return -1;
        }

        return ps[player].i;
    }

    @Override
    public int getPlayerZoom(int player, int czoom) {
        int j = klabs(ps[player].truefz - ps[player].posz) >> 8;
        Sprite psp = boardService.getSprite(ps[player].i);
        if (psp == null) {
            return j;
        }

        j = czoom * (psp.getYrepeat() + j);
        if (j < 22000) {
            j = 22000;
        } else if (j > (65536 << 1)) {
            j = (65536 << 1);
        }
        return j;
    }

    @Override
    public int getPlayerPicnum(int p) {
        if (ps[p].OnMotorcycle) {
            return 7169;
        } else if (ps[p].OnBoat) {
            return 7191;
        } else {
            if (ps[p].getPlayerSprite().getXvel() > 16 && ps[p].on_ground) {
                return APLAYERTOP + ((engine.getTotalClock() >> 4) & 3);
            }
        }
        return APLAYERTOP;
    }

    @Override
    public boolean isFullMap() {
        return false;
    }

    @Override
    public boolean isScrollMode() {
        return ud.scrollmode;
    }

    @Override
    public int getViewPlayer() {
        return screenpeek;
    }

    @Override
    public int getWallX(int w) {
        Wall wal = boardService.getWall(w);
        if (wal != null) {
            return wal.getX();
        }
        return 0;
    }

    @Override
    public int getWallY(int w) {
        Wall wal = boardService.getWall(w);
        if (wal != null) {
            return wal.getY();
        }
        return 0;
    }

    @Override
    public int getSpriteX(int spr) {
        Sprite sp = boardService.getSprite(spr);
        if (sp != null) {
            return sp.getX();
        }
        return 0;
    }

    @Override
    public int getSpriteY(int spr) {
        Sprite sp = boardService.getSprite(spr);
        if (sp != null) {
            return sp.getY();
        }
        return 0;
    }

    @Override
    public int getSpritePicnum(int spr) {
        Sprite sp = boardService.getSprite(spr);
        if (sp != null) {
            return sp.getPicnum();
        }
        return 0;
    }
}
