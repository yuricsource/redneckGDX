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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.*;

import java.util.Arrays;

import static ru.m210projects.Build.Strhandler.Bstrcasecmp;


public class Cheats {

	private static final int kCheatMax = 11;

	public static final String cheatCode[] = {
		/*0*/"SEBMM", // rdall
		/*1*/"SEDMJQ", // rdclip
		/*2*/"SEFMWJT", // rdelvis
		/*3*/"SEHVOT", // rdguns
		/*4*/"SEJOWFOUPSZ", // rdinventory
		/*5*/"SEJUFNT", // rditems
		/*6*/"SELFZT", // rdkeys
		/*7*/"SETLJMM", // rdskill
		/*8*/"SEVOMPDL", // rdunlock
		/*9*/"SENPOTUFS", // rdmonster
		/*10*/"SENFBEPX", // rdmeadow
	};
	
	public static boolean IsCheatCode(String message, int... opt)
	{
		for(int nCheatCode = 0; nCheatCode < kCheatMax; nCheatCode++)
		{
			if(Bstrcasecmp(message, cheatCode[nCheatCode]) == 0)
			{
				switch(nCheatCode)
				{
				case 0:
					for ( int weapon = PISTOL_WEAPON;weapon < MAX_WEAPONSRA;weapon++ )
	                       ps[myconnectindex].gotweapon[weapon]  = true;

                    for ( int weapon = PISTOL_WEAPON; weapon < (MAX_WEAPONSRA); weapon++ )
                        addammo( weapon, ps[myconnectindex], max_ammo_amount[weapon] );

                    ps[myconnectindex].moonshine_amount =         	400;
                    ps[myconnectindex].boot_amount      =    		200;
                    ps[myconnectindex].shield_amount 	=           100;
                    ps[myconnectindex].snorkle_amount 	=           6400;
                    ps[myconnectindex].beer_amount 		=         	2400;
                    ps[myconnectindex].cowpie_amount 	=          	600;
                    ps[myconnectindex].whishkey_amount 	=         	(short) max_player_health;

                    Arrays.fill(ps[myconnectindex].gotkey, (short)1);
                    FTA(5,ps[myconnectindex]);
                    ps[myconnectindex].inven_icon = 1;
					break;
				case 1:
					ud.clipping = !ud.clipping;
                    ps[myconnectindex].cheat_phase = 0;
                    FTA(112+(ud.clipping?1:0),ps[myconnectindex]);
					break;
				case 2:
					ud.god = !ud.god;

                    if(ud.god)
                    {
                        sprite[ps[myconnectindex].i].cstat = 257;

                        hittype[ps[myconnectindex].i].temp_data[0] = 0;
                        hittype[ps[myconnectindex].i].temp_data[1] = 0;
                        hittype[ps[myconnectindex].i].temp_data[2] = 0;
                        hittype[ps[myconnectindex].i].temp_data[3] = 0;
                        hittype[ps[myconnectindex].i].temp_data[4] = 0;
                        hittype[ps[myconnectindex].i].temp_data[5] = 0;

                        sprite[ps[myconnectindex].i].hitag = 0;
                        sprite[ps[myconnectindex].i].lotag = 0;
                        sprite[ps[myconnectindex].i].pal =
                            ps[myconnectindex].palookup;

                        FTA(17,ps[myconnectindex]);
                    }
                    else
                    {
                        ud.god = false;
                        sprite[ps[myconnectindex].i].extra = (short) max_player_health;
                        hittype[ps[myconnectindex].i].extra = -1;
                        ps[myconnectindex].last_extra = (short) max_player_health;
                        FTA(18,ps[myconnectindex]);
                    }

                    sprite[ps[myconnectindex].i].extra = (short) max_player_health;
                    hittype[ps[myconnectindex].i].extra = 0;
					break;
				}
				ps[myconnectindex].cheat_phase = 0;
				return true;
			}
		}
		
		return false;
	}
}
