// This file is part of RedneckGDX
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

package ru.m210projects.Redneck.Factory;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.beforedrawrooms;
import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.picanm;
import static ru.m210projects.Build.Engine.picsiz;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.pow2long;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.tilesizy;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.waloff;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.xyaspect;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.APLAYERTOP;

import java.util.Arrays;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class RRPolymost extends Polymost {

	public RRPolymost(Engine engine) {
		super(engine);
	}

	@Override
	public void drawmapview(int dax, int day, int zoome, int ang) {
		WALL wal;
		SECTOR sec = null;

		int i, j, x, y, bakx1, baky1;
		int s, w, ox, oy, startwall, cx1, cy1, cx2, cy2;
		int bakgxvect, bakgyvect, npoints;
		int xvect, yvect, xvect2, yvect2, daslope;
		
		int tilenum, xoff, yoff, k, l, cosang, sinang, xspan, yspan;
		int xrepeat, yrepeat, x1, y1, x2, y2, x3, y3, x4, y4;

		beforedrawrooms = 0;

		Arrays.fill(gotsector, (byte)0);

		cx1 = (windowx1 << 12);
		cy1 = (windowy1 << 12);
		cx2 = ((windowx2 + 1) << 12) - 1;
		cy2 = ((windowy2 + 1) << 12) - 1;
		zoome <<= 8;
		bakgxvect = (int) divscale(sintable[(1536 - ang) & 2047], zoome, 28);
		bakgyvect = (int) divscale(sintable[(2048 - ang) & 2047], zoome, 28);
		xvect = mulscale(sintable[(2048 - ang) & 2047], zoome, 8);
		yvect = mulscale(sintable[(1536 - ang) & 2047], zoome, 8);
		xvect2 = mulscale(xvect, yxaspect, 16);
		yvect2 = mulscale(yvect, yxaspect, 16);

		int sortnum = 0;

		for (s = 0; s < numsectors; s++) {
			sec = sector[s];

			if ((show2dsector[s >> 3] & pow2char[s & 7]) != 0) {
				npoints = 0;
				i = 0;
				startwall = sec.wallptr;

				j = startwall;
				if (startwall < 0)
					continue;
				for (w = sec.wallnum; w > 0; w--, j++) {
					wal = wall[j];
					if (wal == null)
						continue;
					ox = wal.x - dax;
					oy = wal.y - day;
					x = dmulscale(ox, xvect, -oy, yvect, 16)
							+ (xdim << 11);
					y = dmulscale(oy, xvect2, ox, yvect2, 16)
							+ (ydim << 11);
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2
							- y);
					rx1[npoints] = x;
					ry1[npoints] = y;
					xb1[npoints] = wal.point2 - startwall;
					if (xb1[npoints] < 0)
						xb1[npoints] = 0;

					npoints++;
				}

				if ((i & 0xf0) != 0xf0)
					continue;
				bakx1 = (int) rx1[0];
				baky1 = mulscale((int) ry1[0] - (ydim << 11),
						xyaspect, 16) + (ydim << 11);

				// Collect floor sprites to draw
				for(i=headspritesect[s];i>=0;i=nextspritesect[i])
					if ((sprite[i].cstat&48) == 32)
					{
						if(sortnum >= MAXSPRITESONSCREEN) continue;
						if ((sprite[i].cstat&(64+8)) == (64+8)) continue;
						if (tsprite[sortnum] == null)
							tsprite[sortnum] = new SPRITE();
						tsprite[sortnum].set(sprite[i]);
						
						tsprite[sortnum++].owner = (short) i;
					}

				gotsector[s >> 3] |= pow2char[s & 7];

				globalorientation = sec.floorstat;
				if ((globalorientation & 1) != 0)
					continue;
				globalpal = sec.floorpal;

				globalpicnum = sec.floorpicnum;
				if (globalpicnum >= MAXTILES)
					globalpicnum = 0;
				engine.setgotpic(globalpicnum);
				if ((tilesizx[globalpicnum] <= 0)
						|| (tilesizy[globalpicnum] <= 0))
					continue;

				if ((picanm[globalpicnum] & 192) != 0)
					globalpicnum += engine.animateoffs(globalpicnum, s); // FIXME
				if (waloff[globalpicnum] == null)
					engine.loadtile(globalpicnum);

				globalshade = max(min(sec.floorshade, numshades - 1), 0);
//				globvis = globalhisibility;
//				if (sec.visibility != 0) globvis = mulscale((int) globvis, sec.visibility + 16, 4);
//				globalpolytype = 0;
				if ((globalorientation & 64) == 0) {
					globalposx = dax;
					globalx1 = bakgxvect;
					globaly1 = bakgyvect;
					globalposy = day;
					globalx2 = bakgxvect;
					globaly2 = bakgyvect;
				} else {
					ox = wall[wall[startwall].point2].x
							- wall[startwall].x;
					oy = wall[wall[startwall].point2].y
							- wall[startwall].y;
					i = engine.ksqrt(ox * ox + oy * oy);
					if (i == 0)
						continue;
					i = 1048576 / i;
					globalx1 = mulscale(
							dmulscale(ox, bakgxvect, oy, bakgyvect,
									10), i, 10);
					globaly1 = mulscale(
							dmulscale(ox, bakgyvect, -oy,
									bakgxvect, 10), i, 10);
					ox = (bakx1 >> 4) - (xdim << 7);
					oy = (baky1 >> 4) - (ydim << 7);
					globalposx = dmulscale(-oy, (int) globalx1,
							-ox, (int) globaly1, 28);
					globalposy = dmulscale(-ox, (int) globalx1, oy,
							(int) globaly1, 28);
					globalx2 = -globalx1;
					globaly2 = -globaly1;

					daslope = sector[s].floorheinum;
					i = engine.ksqrt(daslope * daslope + 16777216);
					globalposy = mulscale(globalposy, i, 12);
					globalx2 = mulscale((int) globalx2, i, 12);
					globaly2 = mulscale((int) globaly2, i, 12);
				}
				int globalxshift = (8 - (picsiz[globalpicnum] & 15));
				int globalyshift = (8 - (picsiz[globalpicnum] >> 4));
				if ((globalorientation & 8) != 0) {
					globalxshift++;
					globalyshift++;
				}

				if ((globalorientation & 0x4) > 0) {
					i = globalposx;
					globalposx = -globalposy;
					globalposy = -i;
					i = (int) globalx2;
					globalx2 = globaly1;
					globaly1 = i;
					i = (int) globalx1;
					globalx1 = -globaly2;
					globaly2 = -i;
				}
				if ((globalorientation & 0x10) > 0) {
					globalx1 = -globalx1;
					globaly1 = -globaly1;
					globalposx = -globalposx;
				}
				if ((globalorientation & 0x20) > 0) {
					globalx2 = -globalx2;
					globaly2 = -globaly2;
					globalposy = -globalposy;
				}
				asm1 = (int) (globaly1 << globalxshift);
				asm2 = (int) (globalx2 << globalyshift);
				globalx1 <<= globalxshift;
				globaly2 <<= globalyshift;
				globalposx = (globalposx << (20 + globalxshift))
						+ ((sec.floorxpanning) << 24);
				globalposy = (globalposy << (20 + globalyshift))
						- ((sec.floorypanning) << 24);

				fillpolygon(npoints);
			}
		}
		
		//Sort sprite list
	    int gap = 1; while (gap < sortnum) gap = (gap<<1)+1;
	    for (gap>>=1; gap>0; gap>>=1)
	        for (i=0; i<sortnum-gap; i++)
	            for (j=i; j>=0; j-=gap)
	            {
	                if (sprite[tsprite[j].owner].z <= sprite[tsprite[j+gap].owner].z) break;
	                
	                short tmp = tsprite[j].owner;
	                tsprite[j].owner = tsprite[j+gap].owner;
	                tsprite[j+gap].owner = tmp;
	            }

	    for (s=sortnum-1; s>=0; s--)
	    {
	        SPRITE spr = sprite[tsprite[s].owner];
	        if ((spr.cstat&48) == 32)
	        {
	            npoints = 0;

	            tilenum = spr.picnum;
	            xoff = (byte)((picanm[tilenum]>>8)&255)+spr.xoffset;
	            yoff = (byte)((picanm[tilenum]>>16)&255)+spr.yoffset;
	            if ((spr.cstat&4) > 0) xoff = -xoff;
	            if ((spr.cstat&8) > 0) yoff = -yoff;

	            k = spr.ang&2047;  
	            cosang = sintable[(k+512)&2047]; sinang = sintable[k];
	            xspan = tilesizx[tilenum]; xrepeat = spr.xrepeat;
	            yspan = tilesizy[tilenum]; yrepeat = spr.yrepeat;

	            ox = ((xspan>>1)+xoff)*xrepeat; oy = ((yspan>>1)+yoff)*yrepeat;
	            x1 = spr.x + mulscale(sinang,ox,16) + mulscale(cosang,oy,16);
	            y1 = spr.y + mulscale(sinang,oy,16) - mulscale(cosang,ox,16);
	            l = xspan*xrepeat;
	            x2 = x1 - mulscale(sinang,l,16);
	            y2 = y1 + mulscale(cosang,l,16);
	            l = yspan*yrepeat;
	            k = -mulscale(cosang,l,16); x3 = x2+k; x4 = x1+k;
	            k = -mulscale(sinang,l,16); y3 = y2+k; y4 = y1+k;

	            xb1[0] = 1; xb1[1] = 2; xb1[2] = 3; xb1[3] = 0;
	            npoints = 4;

	            i = 0;

	            ox = x1 - dax; oy = y1 - day;
	            x = dmulscale(ox,xvect,-oy,yvect,16) + (xdim<<11);
	            y = dmulscale(oy,xvect2,ox,yvect2,16) + (ydim<<11);
	            i |= getclipmask(x-cx1,cx2-x,y-cy1,cy2-y);
	            rx1[0] = x; ry1[0] = y;

	            ox = x2 - dax; oy = y2 - day;
	            x = dmulscale(ox,xvect,-oy,yvect,16) + (xdim<<11);
	            y = dmulscale(oy,xvect2,ox,yvect2,16) + (ydim<<11);
	            i |= getclipmask(x-cx1,cx2-x,y-cy1,cy2-y);
	            rx1[1] = x; ry1[1] = y;

	            ox = x3 - dax; oy = y3 - day;
	            x = dmulscale(ox,xvect,-oy,yvect,16) + (xdim<<11);
	            y = dmulscale(oy,xvect2,ox,yvect2,16) + (ydim<<11);
	            i |= getclipmask(x-cx1,cx2-x,y-cy1,cy2-y);
	            rx1[2] = x; ry1[2] = y;

	            x = (int) (rx1[0]+rx1[2]-rx1[1]);
	            y = (int) (ry1[0]+ry1[2]-ry1[1]);
	            i |= getclipmask(x-cx1,cx2-x,y-cy1,cy2-y);
	            rx1[3] = x; ry1[3] = y;

	            if ((i&0xf0) != 0xf0) continue;
	            bakx1 = (int) rx1[0]; baky1 = mulscale((int)ry1[0]-(ydim<<11),xyaspect,16)+(ydim<<11);

	            globalpicnum = spr.picnum;
	            globalpal = spr.pal; // GL needs this, software doesn't
	            if (globalpicnum >= MAXTILES) globalpicnum = 0;
	            engine.setgotpic(globalpicnum);
	            if ((tilesizx[globalpicnum] <= 0) || (tilesizy[globalpicnum] <= 0)) continue;
	            if ((picanm[globalpicnum]&192) != 0) globalpicnum += engine.animateoffs(globalpicnum,s);
	            if (waloff[globalpicnum] == null) engine.loadtile(globalpicnum);
	       

	            // 'loading' the tile doesn't actually guarantee that it's there afterwards.
	            // This can really happen when drawing the second frame of a floor-aligned
	            // 'storm icon' sprite (4894+1)

	            if ((sector[spr.sectnum].ceilingstat&1) > 0)
	                globalshade = ((int)sector[spr.sectnum].ceilingshade);
	            else
	                globalshade = ((int)sector[spr.sectnum].floorshade);
	            globalshade = max(min(globalshade+spr.shade+6,numshades-1),0);

//	            globvis = globalhisibility;
//	            if (sec.visibility != 0) globvis = mulscale((int)globvis,(int)(sec.visibility+16), 4);
//	            globalpolytype = (char) (((spr.cstat&2)>>1)+1);

	            //relative alignment stuff
	            ox = x2-x1; oy = y2-y1;
	            i = ox*ox+oy*oy; if (i == 0) continue; i = (65536*16384)/i;
	            globalx1 = mulscale(dmulscale(ox,bakgxvect,oy,bakgyvect, 10),i, 10);
	            globaly1 = mulscale(dmulscale(ox,bakgyvect,-oy,bakgxvect, 10),i, 10);
	            ox = y1-y4; oy = x4-x1;
	            i = ox*ox+oy*oy; if (i == 0) continue; i = (65536*16384)/i;
	            globalx2 = mulscale(dmulscale(ox,bakgxvect,oy,bakgyvect, 10),i, 10);
	            globaly2 = mulscale(dmulscale(ox,bakgyvect,-oy,bakgxvect, 10),i, 10);

	            ox = picsiz[globalpicnum]; oy = ((ox>>4)&15); ox &= 15;
	            if (pow2long[ox] != xspan)
	            {
	                ox++;
	                globalx1 = mulscale(globalx1,xspan,ox);
	                globaly1 = mulscale(globaly1,xspan,ox);
	            }

	            bakx1 = (bakx1>>4)-(xdim<<7); baky1 = (baky1>>4)-(ydim<<7);
	            globalposx = dmulscale(-baky1,globalx1,-bakx1,globaly1,28);
	            globalposy = dmulscale(bakx1,globalx2,-baky1,globaly2,28);

	            if ((spr.cstat&0x4) > 0) { globalx1 = -globalx1; globaly1 = -globaly1; globalposx = -globalposx; }
	            asm1 = (int) (globaly1<<2); globalx1 <<= 2; globalposx <<= (20+2);
	            asm2 = (int) (globalx2<<2); globaly2 <<= 2; globalposy <<= (20+2);

	            // so polymost can get the translucency. ignored in software mode:
	            globalorientation = ((spr.cstat&2)<<7) | ((spr.cstat&512)>>2);

	            fillpolygon(npoints);
	        }
	    }
	}

	@Override
	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		int i, j, k, l = 0, x1, y1, x2 = 0, y2 = 0, ox, oy, xoff, yoff;
		int dax, day, sprx, spry;
		int z1, z2, startwall, endwall, tilenum;
		int xvect, yvect, xvect2, yvect2;
		char col;
		WALL wal, wal2;
		SPRITE spr;
		
		int cosang, sinang, xspan, yspan;
		int xrepeat, yrepeat, x3, y3, x4, y4;

		xvect = sintable[(-cang) & 2047] * czoom;
		yvect = sintable[(1536 - cang) & 2047] * czoom;
		xvect2 = mulscale(xvect, yxaspect, 16);
		yvect2 = mulscale(yvect, yxaspect, 16);

		// Draw red lines
		for (i = 0; i < numsectors; i++) {
			
			if ((show2dsector[i>>3]&(1<<(i&7))) == 0) continue;
			
			startwall = sector[i].wallptr;
			endwall = sector[i].wallptr + sector[i].wallnum;

			z1 = sector[i].ceilingz;
			z2 = sector[i].floorz;

			for (j = startwall; j < endwall; j++) {
				wal = wall[j];
				k = wal.nextwall;
				if (k < 0)
					continue;

				if(wal.nextsector < 0)
					continue;
				
				if (sector[wal.nextsector].ceilingz == z1)
					if (sector[wal.nextsector].floorz == z2)
						if (((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) == 0)
							continue;

				col = 139; //red
				if (((wal.cstat|wall[wal.nextwall].cstat)&1) != 0) col = 234; //magenta
                if ((show2dsector[wal.nextsector>>3]&(1<<(wal.nextsector&7))) == 0)
                        col = 24;
                else continue;
                
				ox = wal.x - cposx;
				oy = wal.y - cposy;
				x1 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
				y1 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

				wal2 = wall[wal.point2];
				ox = wal2.x - cposx;
				oy = wal2.y - cposy;
				x2 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
				y2 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

				drawline256(x1, y1, x2, y2, col);
			}
		}

		// Draw sprites
		k = ps[screenpeek].i;
		show2dsprite[k >> 3] |= (1 << (k & 7));
		for (i = 0; i < numsectors; i++)
		{
			if ((show2dsector[i>>3]&(1<<(i&7))) == 0) continue;
			
			for (j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
					spr = sprite[j];
					if (j == k || (spr.cstat & 0x8000) != 0 || spr.cstat == 257 || spr.xrepeat == 0)
						continue;
					col = 71; //cyan;
					if ((spr.cstat & 1) != 0)
						col = 234; //magenta

					sprx = spr.x;
					spry = spr.y;

					if( (spr.cstat&257) != 0) switch (spr.cstat & 48) {
					case 0:
						ox = sprx - cposx;
						oy = spry - cposy;
						x1 = dmulscale(ox, xvect, -oy, yvect, 16);
						y1 = dmulscale(oy, xvect2, ox, yvect2, 16);

						if (((gotsector[i >> 3] & (1 << (i & 7))) > 0) && (czoom > 96)) {
							int daang = (spr.ang - cang) & 2047;
							if (j == ps[0].i) {
								x1 = 0;
								y1 = 0;
								daang = 0;
							}
							rotatesprite(
									(x1 << 4) + (xdim << 15),
									(y1 << 4) + (ydim << 15),
									mulscale(czoom * spr.yrepeat, yxaspect,
											16), daang, spr.picnum,
									spr.shade, spr.pal,
									(spr.cstat & 2) >> 1, windowx1,
									windowy1, windowx2, windowy2);
						}
						break;
					case 16:
						break;
					case 32:

                        tilenum = spr.picnum;
                        xoff = (byte)((((picanm[tilenum]>>8)&255))+spr.xoffset);
                        yoff = (byte)((((picanm[tilenum]>>16)&255))+spr.yoffset);
                        if ((spr.cstat&4) > 0) xoff = -xoff;
                        if ((spr.cstat&8) > 0) yoff = -yoff;

                        k = spr.ang;
                        cosang = sintable[(k+512)&2047]; sinang = sintable[k];
                        xspan = tilesizx[tilenum]; xrepeat = spr.xrepeat;
                        yspan = tilesizy[tilenum]; yrepeat = spr.yrepeat;

                        dax = ((xspan>>1)+xoff)*xrepeat; day = ((yspan>>1)+yoff)*yrepeat;
                        x1 = sprx + dmulscale(sinang,dax,cosang,day,16);
                        y1 = spry + dmulscale(sinang,day,-cosang,dax,16);
                        l = xspan*xrepeat;
                        x2 = x1 - mulscale(sinang,l,16);
                        y2 = y1 + mulscale(cosang,l,16);
                        l = yspan*yrepeat;
                        k = -mulscale(cosang,l,16); x3 = x2+k; x4 = x1+k;
                        k = -mulscale(sinang,l,16); y3 = y2+k; y4 = y1+k;

                        ox = x1-cposx; oy = y1-cposy;
                        x1 = dmulscale(ox,xvect,-oy,yvect,16);
                        y1 = dmulscale(oy,xvect2,ox,yvect2,16);

                        ox = x2-cposx; oy = y2-cposy;
                        x2 = dmulscale(ox,xvect,-oy,yvect,16);
                        y2 = dmulscale(oy,xvect2,ox,yvect2,16);

                        ox = x3-cposx; oy = y3-cposy;
                        x3 = dmulscale(ox,xvect,-oy,yvect,16);
                        y3 = dmulscale(oy,xvect2,ox,yvect2,16);

                        ox = x4-cposx; oy = y4-cposy;
                        x4 = dmulscale(ox,xvect,-oy,yvect,16);
                        y4 = dmulscale(oy,xvect2,ox,yvect2,16);

                        drawline256(x1+(xdim<<11),y1+(ydim<<11),
                                                        x2+(xdim<<11),y2+(ydim<<11),col);

                        drawline256(x2+(xdim<<11),y2+(ydim<<11),
                                                        x3+(xdim<<11),y3+(ydim<<11),col);

                        drawline256(x3+(xdim<<11),y3+(ydim<<11),
                                                        x4+(xdim<<11),y4+(ydim<<11),col);

                        drawline256(x4+(xdim<<11),y4+(ydim<<11),
                                                        x1+(xdim<<11),y1+(ydim<<11),col);

                        break;
					}
				}
		}

		
		// Draw white lines
		for (i = 0; i < numsectors; i++) {
			
			if ((show2dsector[i>>3]&(1<<(i&7))) == 0) continue;
			
			startwall = sector[i].wallptr;
			endwall = sector[i].wallptr + sector[i].wallnum;

			k = -1;
			for (j = startwall; j < endwall; j++) {
				wal = wall[j];
				if (wal.nextwall >= 0)
					continue;

				if (tilesizx[wal.picnum] == 0)
					continue;
				if (tilesizy[wal.picnum] == 0)
					continue;

				if (j == k) {
					x1 = x2;
					y1 = y2;
				} else {
					ox = wal.x - cposx;
					oy = wal.y - cposy;
					x1 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y1 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
				}

				k = wal.point2;
				wal2 = wall[k];
				ox = wal2.x - cposx;
				oy = wal2.y - cposy;
				x2 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
				y2 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

				drawline256(x1, y1, x2, y2, 24);
			}
		}
		
		int daang;
		for(int p=connecthead;p >= 0;p=connectpoint2[p])
        {
			if(ud.scrollmode && p == screenpeek) continue;

			ox = sprite[ps[p].i].x-cposx; oy = sprite[ps[p].i].y-cposy;
			daang = (sprite[ps[p].i].ang-cang)&2047;
			if (p == screenpeek) { ox = 0; oy = 0; daang = 0; }
			x1 = mulscale(ox,xvect,16) - mulscale(oy,yvect,16);
			y1 = mulscale(oy,xvect2,16) + mulscale(ox,yvect2,16);

			if(p == screenpeek || ud.coop == 1 )
			{
				if (ps[p].OnMotorcycle )
					i = 7169;
		        else if ( ps[p].OnBoat )
		        	i = 7191;
		        else {
					if(sprite[ps[p].i].xvel > 16 && ps[p].on_ground)
						i = APLAYERTOP+((totalclock>>4)&3);
					else
						i = APLAYERTOP;
		        }

				j = (int) (klabs(ps[p].truefz-ps[p].posz)>>8);
				j = mulscale(czoom*(sprite[ps[p].i].yrepeat+j),yxaspect,16);

				if(j < 22000) j = 22000;
				else if(j > (65536<<1)) j = (65536<<1);

				rotatesprite((x1<<4)+(xdim<<15),(y1<<4)+(ydim<<15),j,
						daang,i,sprite[ps[p].i].shade,sprite[ps[p].i].pal,
						(sprite[ps[p].i].cstat&2)>>1,windowx1,windowy1,windowx2,windowy2);
			}
        }
	}
}
