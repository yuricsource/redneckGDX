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
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Types.INTERPOLATION.*;
import static ru.m210projects.Redneck.Main.*;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.INTERPOLATION;
import ru.m210projects.Redneck.Types.LOCATION;

public class Interpolation {

	public static int smoothratio = 0;
	public static final int MAXINTERPOLATIONS = 2048;
	public static LOCATION gPrevSpriteLoc[] = new LOCATION[MAXSPRITES];
	public static int InterpolationCount = 0;
	public static INTERPOLATION[] gInterpolationData = new INTERPOLATION[MAXINTERPOLATIONS];

	public static int gSpriteLoc[] = new int[MAXSPRITES << 3];
	public static int gWallLoc[] = new int[MAXWALLS << 3];
	public static int gSectorLoc[] = new int[MAXSECTORS << 3];
	
	public static void initinterpolations()
	{
		for(int i = 0; i < MAXINTERPOLATIONS; i++)
			gInterpolationData[i] = new INTERPOLATION();
		for(int i = 0; i < MAXSPRITES; i++)
			gPrevSpriteLoc[i] = new LOCATION();
	}
	
	public static void setinterpolation(Object obj, int type)
	{
		if ( InterpolationCount == MAXINTERPOLATIONS )
			System.err.println("Too many interpolations");
	  
		INTERPOLATION gInt = gInterpolationData[InterpolationCount++];
	
		gInt.ptr = obj;
		gInt.type = type;
		
		switch(type)
		{
			case WALLX:
				gInt.oldpos = ((WALL)obj).x;
				break;
			case WALLY:
				gInt.oldpos = ((WALL)obj).y;
				break;
			case FLOORZ:
				gInt.oldpos = ((SECTOR)obj).floorz;
				break;
			case CEILZ:
				gInt.oldpos = ((SECTOR)obj).ceilingz;
				break;
			case FLOORH:
				gInt.oldpos = ((SECTOR)obj).floorheinum;
				break;
		}
	}
	
	public static void stopinterpolation(Object obj, int type)
	{
		for(int i=InterpolationCount-1;i>=0;i--) {
			INTERPOLATION gInt = gInterpolationData[i];
			if (obj == gInt.ptr)
			{
				InterpolationCount--;
				gInterpolationData[i] = gInterpolationData[InterpolationCount];
			}
		}
	}
	
	public static void updateinterpolations()
	{
		engine.updatesmoothticks();
		InterpolationCount = 0;
		for(int i = 0; i < MAXSPRITES << 3; i++)
			gSpriteLoc[i] = 0;
		for(int i = 0; i < MAXWALLS << 3; i++)
			gWallLoc[i] = 0;
		for(int i = 0; i < MAXSECTORS << 3; i++)
			gSectorLoc[i] = 0;
	}
	
	public static void dointerpolations(int smoothratio)
	{
		for ( int i = 0; i < InterpolationCount; i++ )
		{
			INTERPOLATION gInt = gInterpolationData[i];
			Object obj = gInt.ptr;
			switch(gInt.type)
			{
				case WALLX:
					gInt.bakpos = ((WALL)obj).x;
					((WALL)obj).x = gInt.oldpos + mulscale((((WALL)obj).x - gInt.oldpos), smoothratio, 16);
					break;
				case WALLY:
					gInt.bakpos = ((WALL)obj).y;
					((WALL)obj).y = gInt.oldpos + mulscale((((WALL)obj).y - gInt.oldpos), smoothratio, 16);
					break;
				case FLOORZ:
					gInt.bakpos = ((SECTOR)obj).floorz;
					((SECTOR)obj).floorz = gInt.oldpos + mulscale((((SECTOR)obj).floorz - gInt.oldpos), smoothratio, 16);
					break;
				case CEILZ:
					gInt.bakpos = ((SECTOR)obj).ceilingz;
					((SECTOR)obj).ceilingz = gInt.oldpos + mulscale((((SECTOR)obj).ceilingz - gInt.oldpos), smoothratio, 16);
					break;
				case FLOORH:
					gInt.bakpos = ((SECTOR)obj).floorheinum;
					((SECTOR)obj).floorheinum = (short) (gInt.oldpos + mulscale((((SECTOR)obj).floorheinum - gInt.oldpos), smoothratio, 16));
					break;
			}
		}
	}
	
	public static void restoreinterpolations()
	{
		for ( int i = 0; i < InterpolationCount; i++ )
		{
			INTERPOLATION gInt = gInterpolationData[i];
			Object obj = gInt.ptr;
			switch(gInt.type)
			{
				case WALLX:
					((WALL)obj).x = gInt.bakpos;
					break;
				case WALLY:
					((WALL)obj).y = gInt.bakpos;
					break;
				case FLOORZ:
					((SECTOR)obj).floorz = gInt.bakpos;
					break;
				case CEILZ:
					((SECTOR)obj).ceilingz = gInt.bakpos;
					break;
				case FLOORH:
					((SECTOR)obj).floorheinum = (short) gInt.bakpos;
					break;
			}
		}
	}
	
	public static void setsectinterpolate(int i) {
		int j, k, startwall, endwall;

		startwall = sector[sprite[i].sectnum].wallptr;
		endwall = startwall + sector[sprite[i].sectnum].wallnum;

		for (j = startwall; j < endwall; j++) {
			viewBackupWallLoc(j, wall[j]);
			k = wall[j].nextwall;
			if (k >= 0) {
				viewBackupWallLoc(k, wall[k]);
				k = wall[k].point2;
				viewBackupWallLoc(k, wall[k]);
			}
		}
	}

	public static void clearsectinterpolate(int i) {
		int startwall = sector[sprite[i].sectnum].wallptr;
		int endwall = startwall + sector[sprite[i].sectnum].wallnum;
		for (int j = startwall; j < endwall; j++) {
			viewStopWallLoc(j, wall[j]);
			if (wall[j].nextwall >= 0)
				viewStopWallLoc(wall[j].nextwall, wall[wall[j].nextwall]);
		}
	}
	
	public static void viewBackupSpriteLoc( int nSprite, SPRITE pSprite )
	{
		if((gSpriteLoc[nSprite >> 3] & (1 << (nSprite & 7))) == 0) {
			LOCATION pLocation = gPrevSpriteLoc[nSprite];
			pLocation.x = pSprite.x;
			pLocation.y = pSprite.y;
			pLocation.z = pSprite.z;
			pLocation.ang = pSprite.ang;
			gSpriteLoc[nSprite >> 3] |= 1 << (nSprite & 7);
		}
	}
	
	public static void viewBackupWallLoc( int nWall, WALL pWall )
	{
		if((gWallLoc[nWall >> 3] & (1 << (nWall & 7))) == 0) {
			setinterpolation(pWall, WALLX);
			setinterpolation(pWall, WALLY);
			gWallLoc[nWall >> 3] |= 1 << (nWall & 7);
		}
	}
	
	public static void viewStopWallLoc( int nWall, WALL pWall )
	{
		if((gWallLoc[nWall >> 3] & (1 << (nWall & 7))) != 0) {
			stopinterpolation(pWall, WALLX);
			stopinterpolation(pWall, WALLY);
			gWallLoc[nWall >> 3] &= ~(1 << (nWall & 7));
		}
	}
	
	public static void viewBackupSectorLoc( int nSector, SECTOR pSector )
	{
		if((gSectorLoc[nSector >> 3] & (1 << (nSector & 7))) == 0) {
			setinterpolation(pSector, FLOORZ);
			setinterpolation(pSector, CEILZ);
			setinterpolation(pSector, FLOORH);
		    gSectorLoc[nSector >> 3] |= 1 << (nSector & 7);
		}
	}
	
	public static void viewStopSectorLoc( int nSector, SECTOR pSector )
	{
		if((gSectorLoc[nSector >> 3] & (1 << (nSector & 7))) != 0) {
			stopinterpolation(pSector, FLOORZ);
			stopinterpolation(pSector, CEILZ);
			stopinterpolation(pSector, FLOORH);
		    gSectorLoc[nSector >> 3] &= ~(1 << (nSector & 7));
		}
	}

}
