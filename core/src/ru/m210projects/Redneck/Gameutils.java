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

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Spawn.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import static ru.m210projects.Redneck.Interpolation.viewBackupWallLoc;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.numtiles;
import static ru.m210projects.Build.Engine.picanm;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.waloff;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;

import ru.m210projects.Build.Types.SPRITE;


public class Gameutils {

	public static int neartag(int xs, int ys, int zs, int sectnum, int ange, int neartagrange, int tagsearch)
	{
		int out = engine.neartag(xs,ys,zs, (short)sectnum, (short)ange,neartag,neartagrange,tagsearch);
    	neartagsprite = (short) neartag.tagsprite;
    	neartagwall = (short) neartag.tagwall;
    	neartagsector = (short) neartag.tagsector;
    	neartaghitdist = neartag.taghitdist;
    	return out;
	}
	
	public static void DragPoint(int pointhighlight, int dax, int day) {

		short cnt, tempshort;
		viewBackupWallLoc(pointhighlight, wall[pointhighlight]);
		wall[pointhighlight].x = dax;
		wall[pointhighlight].y = day;

		cnt = (short) MAXWALLS;
		tempshort = (short) pointhighlight;    //search points CCW
		do
		{
			if (wall[tempshort].nextwall >= 0)
			{
				tempshort = wall[wall[tempshort].nextwall].point2;
				viewBackupWallLoc(tempshort, wall[tempshort]);
				wall[tempshort].x = dax;
				wall[tempshort].y = day;
			}
			else
			{
				tempshort = (short) pointhighlight;    //search points CW if not searched all the way around
				do
				{
					if (wall[engine.lastwall(tempshort)].nextwall >= 0)
					{
						tempshort = wall[engine.lastwall(tempshort)].nextwall;
						viewBackupWallLoc(tempshort, wall[tempshort]);
						wall[tempshort].x = dax;
						wall[tempshort].y = day;
					}
					else
					{
						break;
					}
					cnt--;
				}
				while ((tempshort != pointhighlight) && (cnt > 0));
				break;
			}
			cnt--;
		}
		while ((tempshort != pointhighlight) && (cnt > 0));
	}
	
	public static int FindDistance2D(int dx, int dy)
	{
		dx = (int) klabs(dx);
		dy = (int) klabs(dy);
		if (dx == 0) return(dy);
		if (dy == 0) return(dx);
		if (dy < dx) { int i = dx; dx = dy; dy = i; } //swap x, y
		dx += (dx>>1);
		return ((dx>>6)+(dx>>2)+dy-(dy>>5)-(dy>>7)); //handle 1 octant
		//return engine.ksqrt(dx*dx + dy*dy);
	}
	
	public static int FindDistance3D(int dx, int dy, int dz)
	{
		dx = (int) klabs(dx);
		dy = (int) klabs(dy);
		dz = (int) klabs(dz);

		if (dx < dy) { int i = dx; dx = dy; dy = i; } //swap x, y
		if (dx < dz) { int i = dx; dx = dz; dz = i; } //swap x, z

		int t = dy + dz;

		return (dx - (dx>>4) + (t>>2) + (t>>3));
		//return engine.ksqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static boolean rnd(int X)
	{
		return (engine.krand()>>8)>=(255-(X));
	}
	
	public static void RANDOMSCRAP(SPRITE s, int i) {
		int vz = -512-(engine.krand()&2047);
		int ve = (engine.krand()&63)+64;
		int va = engine.krand()&2047;
		int pn = SCRAP6+(engine.krand()&15);
		int sz = s.z-(8<<8)-(engine.krand()&8191);
		int sy = s.y+(engine.krand()&255)-128;
		int sx = s.x+(engine.krand()&255)-128;
		EGS(s.sectnum,sx,sy,sz,pn,-8,16,16,va,ve,vz,i,(short)5);
	}
	
	public static boolean IFWITHIN(SPRITE s, int B, int E) {
		return (s.picnum)>=(B) && (s.picnum)<=(E);
	}
	
	public static boolean AFLAMABLE(int X) {
		return (X==1191||X==1193||X==1230||X==3062);
	}
	
	public static int sgn(int val)
	{
		return ((val > 0)?1:0) - ((val < 0)?1:0);
	}
	
	public static int ClipRange(int value, int min, int max) {
		if(value < min)
			value = min;
		if(value > max)
			value = max;
		
		return value;
	}
	
	public static int ClipLow(int value, int min) {
		if(value < min)
			value = min;
		
		return value;
	}
	
	public static int ClipHigh(int value, int max) {
		if(value > max)
			value = max;
		
		return value;
	}

	
	public static float ClipLow(float value, int min) {
		if(value < min)
			value = min;
		
		return value;
	}
	
	public static float ClipHigh(float value, int max) {
		if(value > max)
			value = max;
		
		return value;
	}
	
	public static char[] toCharArray(String... text)
	{
		buildString(buf, 0, text);
		
		return buf;
	}
	
	public static char[] toCharArray(String text, int num)
	{
		buildString(buf, 0, text, num);
		
		return buf;
	}
	
	public static final int[][] replace = {
		{ 3363, 9217 }, 
		{ 3364, 9218 }, 
		{ 3415, 9219 }, 
		{ 3416, 9220 }, 
		{ 3417, 9221 }, 
		{ 3418, 9222 }, 
		{ 3453, 9223 },
		{ 3454, 9224 },
		{ 3455, 9225 },
		{ 3456, 9226 },
		{ 3457, 9227 },
		{ 3458, 9228 },
	};

	public static void LoadUserRes()
	{
		FileHandle fil = Gdx.files.internal("RedneckGDX.ART");
		if(fil != null)
		{
			ByteBuffer bb = ByteBuffer.wrap(fil.readBytes());
	    	bb.order( ByteOrder.LITTLE_ENDIAN);

			int artversion = bb.getInt();
			if (artversion != 1)
				return;
			
			numtiles = bb.getInt();
			int localtilestart = bb.getInt();
			int localtileend = bb.getInt();
			if(localtilestart >= MAXTILES || localtileend >= MAXTILES)
				return;
			
			for (int i = localtilestart; i <= localtileend; i++) 
				tilesizx[i] = bb.getShort();
			for (int i = localtilestart; i <= localtileend; i++) 
				tilesizy[i] = bb.getShort();
			for (int i = localtilestart; i <= localtileend; i++)
				picanm[i] = bb.getInt();
			
			for (int tilenume = localtilestart; tilenume <= localtileend; tilenume++) {
				if(bb.position() == bb.capacity())
					break;
				int dasiz = tilesizx[tilenume] * tilesizy[tilenume];
				waloff[tilenume] = new byte[dasiz];
				bb.get(waloff[tilenume]);
			}
			bb.clear();
			bb = null;
			
			for(int i = 0; i < replace.length; i++)
			{
				int tilenume = replace[i][0];
				int newtile = replace[i][1];
				waloff[tilenume] = new byte[tilesizx[newtile] * tilesizy[newtile]];
				System.arraycopy(waloff[newtile], 0, waloff[tilenume], 0, waloff[tilenume].length);
				tilesizx[tilenume] = tilesizx[newtile];
				tilesizy[tilenume] = tilesizy[newtile];
				picanm[tilenume] = picanm[newtile];
			}
		}
	}
}
