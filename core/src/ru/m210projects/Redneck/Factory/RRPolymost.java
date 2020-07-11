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

import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Names.APLAYERTOP;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;

public class RRPolymost extends Polymost {

	public RRPolymost(Engine engine) {
		super(engine);
	}

	@Override
	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		int i, j, k, l = 0, x1, y1, x2 = 0, y2 = 0, ox, oy, xoff, yoff;
		int dax, day, sprx, spry;
		int z1, z2, startwall, endwall;
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

					Tile pic = engine.getTile(spr.picnum);
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

						xoff = (byte) (pic.getOffsetX()+spr.xoffset);
						yoff = (byte) (pic.getOffsetY()+spr.yoffset);
                        if ((spr.cstat&4) > 0) xoff = -xoff;
                        if ((spr.cstat&8) > 0) yoff = -yoff;

                        k = spr.ang;
                        cosang = sintable[(k+512)&2047]; sinang = sintable[k&2047];
                        xspan = pic.getWidth(); xrepeat = spr.xrepeat;
                        yspan = pic.getHeight(); yrepeat = spr.yrepeat;

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

				if (!engine.getTile(wal.picnum).hasSize())
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

				j = klabs(ps[p].truefz-ps[p].posz)>>8;
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
