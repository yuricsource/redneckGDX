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

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.ANIMATION;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Interpolation.*;
import static ru.m210projects.Redneck.Types.ANIMATION.CEILZ;
import static ru.m210projects.Redneck.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLX;
import static ru.m210projects.Redneck.Types.ANIMATION.WALLY;

public class Animate {
	
	//These variables are for animating x, y, or z-coordinates of sectors,
	//walls, or sprites (They are NOT to be used for changing the [].picnum's)
	//See the setanimation(), and getanimategoal() functions for more details.
	public static final int MAXANIMATES = 512;
	public static int gAnimationCount = 0;
	public static final ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	
	public static void initanimations()
	{
		for(int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
	}
	
	public static Object getobject(int index, int type)
	{
		Object object = null;
		switch(type)
		{
			case WALLX:
			case WALLY:
				object = wall[index];
				break;
			case FLOORZ:
			case CEILZ:
				object = sector[index];
				break;
		}

		return object;
	}
	
	public static int getanimationgoal(Object object, int type)
	{
		int j = -1;
		for(int i = gAnimationCount - 1; i >= 0; i--)
			if (object == gAnimationData[i].ptr && type == gAnimationData[i].type)
				{ j = i; break; }
		return(j);
	}
	
	public static int setanimation(int sector, int animptr, int thegoal, int thevel, int type)
	{
		if (gAnimationCount >= MAXANIMATES) return -1;
		
		Object object = getobject(animptr, type);
		if(object == null) return -1;
		
		int j = getanimationgoal(object, type);
		if(j == -1) j = gAnimationCount;

		ANIMATION gAnm = gAnimationData[j];
		gAnm.sect = sector;
		gAnm.ptr = object;
		gAnm.id = (short) animptr;
		gAnm.goal = thegoal;
		gAnm.vel = thevel;
		gAnm.type = (byte) type;

		if (j == gAnimationCount) gAnimationCount++;
		
		return j;
	}
	
	public static void doanimations()
	{
		int j = 0;
		for(int i = gAnimationCount - 1; i >= 0; i--)
		{
			ANIMATION gAnm = gAnimationData[i];
			Object obj = gAnm.ptr;
			switch(gAnm.type)
			{
				case WALLX:
					viewBackupWallLoc(gAnm.id, (WALL)obj);
					j = ((WALL)obj).x;
					if (j < gAnm.goal)
						((WALL)obj).x = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((WALL)obj).x = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
				case WALLY:
					viewBackupWallLoc(gAnm.id, (WALL)obj);
					j = ((WALL)obj).y;
					if (j < gAnm.goal)
						((WALL)obj).y = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((WALL)obj).y = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
				case FLOORZ:
					viewBackupFloorLoc(gAnm.id, (SECTOR)obj);
					j = ((SECTOR)obj).floorz;
					
					int vel = gAnm.vel*TICSPERFRAME;
					if (j < gAnm.goal)
						((SECTOR)obj).floorz = Math.min(j+vel, gAnm.goal);
					else {
						((SECTOR)obj).floorz = Math.max(j-vel, gAnm.goal);
						vel = -vel;
					}
					
					int dasect = gAnm.sect;
					for(int p=connecthead;p>=0;p=connectpoint2[p])
		                if (ps[p].cursectnum == dasect)
		                    if ((sector[dasect].floorz-ps[p].posz) < (64<<8))
		                        if (sprite[ps[p].i].owner >= 0)
		            {
		                ps[p].posz += vel;
		                ps[p].poszv = 0;
		                if (p == myconnectindex)
		                {
		                    myz += vel;
		                    myzvel = 0;
		                    myzbak[((movefifoplc-1)&(MOVEFIFOSIZ-1))] = ps[p].posz;
		                }
		            }

		            for(int k=headspritesect[dasect];k>=0;k=nextspritesect[k])
		                if (sprite[k].statnum != 3)
		                {
		                    hittype[k].bposz = sprite[k].z;
		                    sprite[k].z += vel;
		                    hittype[k].floorz = sector[dasect].floorz+vel;
		                }

					break;
				case CEILZ:
					viewBackupCeilingLoc(gAnm.id, (SECTOR)obj);
					j = ((SECTOR)obj).ceilingz;
					if (j < gAnm.goal)
						((SECTOR)obj).ceilingz = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((SECTOR)obj).ceilingz = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
			}

			if (j == gAnm.goal)
			{
				gAnimationCount--;
				if (i != gAnimationCount)
					gAnm.copy(gAnimationData[gAnimationCount]);
				int dasect = gAnm.sect;
				if( sector[dasect].lotag == 18 || sector[dasect].lotag == 19 )
		                if(gAnm.type == CEILZ)
		                    continue;

		        if( (sector[dasect].lotag&0xff) != 22 )
		        	callsound(dasect,-1);
			}
		}
	}
}
