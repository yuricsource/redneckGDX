//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Redneck.Types.PlayerStruct;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Render.AbstractRenderer.DEFAULT_SCREEN_FADE;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;

public class Screen {

    public static int changepalette;
    public static boolean restorepalette;
    public static int screensize;
    public static int gViewYScaled;

    public static void vscrn(int size) {
        Renderer renderer = game.getRenderer();
        int xdim = renderer.getWidth();
        int ydim = renderer.getHeight();

        if (size < 0) {
            size = 0;
        } else if (size > 5) {
            size = 5;
        }

        int x1 = 0;
        int x2 = xdim - x1;
        int y1 = 0;
        int y2 = 200;

        if (size == 5) {
            y2 -= 41;
        }

        y1 = scale(y1, ydim, 200);
        y2 = scale(y2, ydim, 200);

        renderer.setview(x1, y1, x2 - 1, y2 - 1);
        screensize = size;
    }

    public static void setgamepalette(PlayerStruct player, byte[] pal) {
        if (player != ps[screenpeek]) {
            // another head
            player.palette = pal;
            return;
        }

        player.palette = pal;
        engine.setbrightness(cfg.getPaletteGamma(), pal);
        DEFAULT_SCREEN_FADE.set(0,0,0,0);
    }

    public static void palto(int r, int g, int b, int count) {
        DEFAULT_SCREEN_FADE.set(r, g, b, count);
    }

    public static void scrReset() {
        DEFAULT_SCREEN_FADE.set(0,0,0,0);
        setgamepalette(ps[myconnectindex], engine.getPaletteManager().getBasePalette());
    }

    public static void myospal(int x, int y, int scale, int tilenum, int shade, int orientation, int p) {
        short a = 0;
        if ((orientation & 4) != 0) {
            a = 1024;
        }
        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(x << 16, y << 16, scale, a, tilenum, shade, p, 10 | orientation);
    }

    public static void myospal(int x, int y, int tilenum, int shade, int orientation, int p) {
        short a = 0;
        if ((orientation & 4) != 0) {
            a = 1024;
        }
        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(x << 16, y << 16, 47040, a, tilenum, shade, p, 10 | orientation);
    }

    public static void myos(int x, int y, int tilenum, int shade, int orientation) {
        int a = 0;
        if ((orientation & 4) != 0) {
            a = 1024;
        }

        Sector sec = boardService.getSector(ps[screenpeek].cursectnum);
        int p = sec != null ? sec.getFloorpal() : 0;
        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(x << 16, y << 16, 65536, a, tilenum, shade, p, 10 | orientation);
    }

    public static void patchstatusbar(int x1, int y1, int x2, int y2) {
        Renderer renderer = game.getRenderer();
        int xdim = renderer.getWidth();
        int ydim = renderer.getHeight();

        if (ud.screen_size > 4) {
            ArtEntry pic = engine.getTile(BACKGROUND);
            if (pic.hasSize()) {
                int framesx = xdim / pic.getWidth();
                int framesy = ydim - scale((engine.getTile(BOTTOMSTATUSBAR).getHeight() + engine.getTile(1649).getHeight()) / 2, ydim, 200);

                int x = 0;
                for (int i = 0; i <= framesx; i++) {
                    renderer.rotatesprite(x << 16, framesy << 16, 0x10000, 0, BACKGROUND, 0, 0, 8 | 16 | 256, 0, 0, xdim - 1, ydim - 1);
                    x += renderer.getTile(2339).getWidth();
                }
            }
        }

        renderer.rotatesprite(160 << 16, 183 << 16, 0x8000, 0, BOTTOMSTATUSBAR, 4, 0, 10 + 64, scale(x1, xdim, 320), scale(y1, ydim, 200), scale(x2, xdim, 320) - 1, scale(y2, ydim, 200) - 1);

        if (!isSquareResolution(xdim, ydim)) {
            renderer.rotatesprite(8 << 16, 183 << 16, 0x8000, 0, WIDEHUD_LEFTSHADOW, 0, 0, 10 | 256, 0, 0, xdim - 1, ydim - 1);
            renderer.rotatesprite(311 << 16, 183 << 16, 0x8000, 0, WIDEHUD_RIGHTSHADOW, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);
        }
    }

    public static void displayinventory(PlayerStruct p) {
        int y;
        Renderer renderer = game.getRenderer();

        int j = 0;
        int n = (p.cowpie_amount > 0) ? 1 << 3 : 0;
        if ((n & 8) != 0) {
            j++;
        }
        n |= (p.snorkle_amount > 0) ? 1 << 5 : 0;
        if ((n & 32) != 0) {
            j++;
        }
        n |= (p.moonshine_amount > 0) ? 1 << 1 : 0;
        if ((n & 2) != 0) {
            j++;
        }
        n |= (p.beer_amount > 0) ? 1 << 2 : 0;
        if ((n & 4) != 0) {
            j++;
        }
        n |= (p.whishkey_amount > 0) ? 1 : 0;
        if ((n & 1) != 0) {
            j++;
        }
        n |= (p.yeehaa_amount > 0) ? 1 << 4 : 0;
        if ((n & 16) != 0) {
            j++;
        }
        n |= (p.boot_amount > 0) ? 1 << 6 : 0;
        if ((n & 64) != 0) {
            j++;
        }

        int xoff = 160 - (j * 11);

        j = 0;

        if (ud.screen_size > 5) {
            y = 140; // 160
            if (ud.multimode > 1) {
                y = 156;
            }
            if (ud.multimode > 4) {
                y -= 4;
            }
        } else {
            y = 180;
        }

        if (ud.screen_size == 5) {
            xoff += 56;
        }

        while (j <= 9) {
            if ((n & (1 << j)) != 0) {
                switch (n & (1 << j)) {
                    case 1:
                        renderer.rotatesprite(xoff << 16, y << 16, 32768, 0, WHISHKEY_ICON, 0, 0, 2 + 16);
                        break;
                    case 2:
                        renderer.rotatesprite((xoff + 1) << 16, y << 16, 32768, 0, MOONSHINE_ICON, 0, 0, 2 + 16);
                        break;
                    case 4:
                        renderer.rotatesprite((xoff + 2) << 16, y << 16, 32768, 0, BEER_ICON, 0, 0, 2 + 16);
                        break;
                    case 8:
                        renderer.rotatesprite(xoff << 16, y << 16, 32768, 0, COWPIE_ICON, 0, 0, 2 + 16);
                        break;
                    case 16:
                        renderer.rotatesprite(xoff << 16, y << 16, 32768, 0, EMPTY_ICON, 0, 0, 2 + 16);
                        break;
                    case 32:
                        renderer.rotatesprite(xoff << 16, y << 16, 32768, 0, SNORKLE_ICON, 0, 0, 2 + 16);
                        break;
                    case 64:
                        renderer.rotatesprite(xoff << 16, (y - 1) << 16, 32768, 0, BOOT_ICON, 0, 0, 2 + 16);
                        break;
                }

                xoff += 22;

                if (p.inven_icon == j + 1) {
                    renderer.rotatesprite((xoff - 2) << 16, (y + 19) << 16, 32768, 1024, ARROW, -32, 0, 2 + 16);
                }
            }

            j++;
        }
    }

    public static void invennum(int x, int y, int num1, int ha, int sbits) {
        char[] dabuf = Globals.buf;
        Renderer renderer = game.getRenderer();

        ConvertType type = ConvertType.Normal;
        if ((sbits & 256) != 0) {
            type = ConvertType.AlignLeft;
        }
        if ((sbits & 512) != 0) {
            type = ConvertType.AlignRight;
        }

        Bitoa(num1, dabuf);
        if (num1 > 99) {
            renderer.rotatesprite(coordsConvertXScaled(x - 4, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits);
            renderer.rotatesprite(coordsConvertXScaled(x, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[1] - '0', ha, 0, sbits);
            renderer.rotatesprite(coordsConvertXScaled(x + 4, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[2] - '0', ha, 0, sbits);
        } else if (num1 > 9) {
            renderer.rotatesprite(coordsConvertXScaled(x, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits);
            renderer.rotatesprite(coordsConvertXScaled(x + 4, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[1] - '0', ha, 0, sbits);
        } else {
            renderer.rotatesprite(coordsConvertXScaled(x + 4, type) << 16, coordsConvertYScaled(y) << 16, gViewYScaled >> 1, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits);
        }
    }

    public static void digitalnumber(int x, int y, int n, int s, int cs) {
        int i, j, k, p, c;
        Renderer renderer = game.getRenderer();
        char[] b = Globals.buf;
        i = Bitoa(n, b);
        j = 0;

        for (k = 0; k < i; k++) {
            p = DIGITALNUM + b[k] - '0';
            j += (renderer.getTile(p).getWidth() >> 1) + 1;
        }
        c = x - (j >> 1);

        j = 0;
        for (k = 0; k < i; k++) {
            p = DIGITALNUM + b[k] - '0';
            renderer.rotatesprite((c + j) << 16, (y) << 16, 32768, 0, p, s, 0, cs);
            j += (renderer.getTile(p).getWidth() >> 1) + 1;
        }
    }
}
