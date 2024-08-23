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

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Redneck.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;

public class Script {

    public int type = RR;

    public final int[] actorscrptr = new int[MAXTILES];
    public final short[] actortype = new short[MAXTILES];
    public final int[] script = new int[MAXSCRIPTSIZE];

    public final char[][] level_names = new char[nMaxMaps * nMaxEpisodes][33];
    public final char[][] level_file_names = new char[nMaxMaps * nMaxEpisodes][128];
    public final int[] partime = new int[nMaxMaps * nMaxEpisodes];
    public final int[] designertime = new int[nMaxMaps * nMaxEpisodes];
    public final char[][] volume_names = new char[nMaxMaps][33];
    public final char[][] skill_names = new char[nMaxSkills][33];

    public int nEpisodes;
    public int nSkills;
    public final int[] nMaps = new int[nMaxEpisodes];

    public final char[][] fta_quotes = new char[NUMOFFIRSTTIMEACTIVE][64];
    public char[][] key_quotes;
    public final String[][] music_fn = new String[5][11];
    public final String[] env_music_fn = new String[5];
    public final short[] soundps = new short[NUM_SOUNDS];
    public final short[] soundpe = new short[NUM_SOUNDS];
    public final short[] soundvo = new short[NUM_SOUNDS];
    public final short[] soundm = new short[NUM_SOUNDS];
    public final short[] soundpr = new short[NUM_SOUNDS];
    public final String[] sounds = new String[NUM_SOUNDS];
    public final char[] betaname = new char[80];

    public int const_visibility;
    public int impact_damage;
    public int gc = 176;
    public int max_player_health;
    public int max_armour_amount;
    public final int[] max_ammo_amount = new int[MAX_WEAPONSRA];
    public int respawnactortime = 768, respawnitemtime = 768;
    public int dukefriction = 0xcc00;
    public int numfreezebounces = 3, crossbowblastradius, tntblastradius, bouncemineblastradius, shrinkerblastradius, morterblastradius, powderblastradius, seenineblastradius;
    public char camerashitable, freezerhurtowner = 0, dildoblase;
    public short spriteqamount = 64;
}
