// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Types;

import java.util.Arrays;

import static ru.m210projects.Redneck.Globals.nMaxMaps;
import static ru.m210projects.Redneck.Globals.ud;

public class EpisodeInfo {

    public String Title;
    public int nMaps;
    private final MapInfo[] gMapInfo;

    public EpisodeInfo(String title) {
        this.Title = title;
        gMapInfo = new MapInfo[nMaxMaps + 1];
    }

    public boolean setMapInfo(int mapnum, MapInfo gMapInfo) {
        if (mapnum >= nMaxMaps + 1 || mapnum < 0) {
            return false;
        }
        this.gMapInfo[mapnum] = gMapInfo;
        return true;
    }

    public MapInfo getMapInfo(int mapnum) {
        if (ud.level_number < gMapInfo.length) {
            return gMapInfo[mapnum];
        }
        return null;
    }

    public String getMapTitle(int mapnum) {
        if (ud.level_number < gMapInfo.length && gMapInfo[mapnum] != null) {
            return gMapInfo[mapnum].getTitle();
        }
        return "null";
    }

    @Override
    public String toString() {
        return "EpisodeInfo {" +
                "Title='" + Title + '\'' +
                ", nMaps=" + nMaps +
                ", gMapInfo=" + Arrays.toString(gMapInfo) +
                '}';
    }
}
