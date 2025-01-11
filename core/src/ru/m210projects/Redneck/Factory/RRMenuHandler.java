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

package ru.m210projects.Redneck.Factory;


import ru.m210projects.Build.Engine;
import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.*;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Redneck.Menus.InterfaceMenu;
import ru.m210projects.Redneck.Screens.MenuScreen;

import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Screen.screensize;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.sound;

public class RRMenuHandler extends MenuHandler {

    public static final int MAIN = 0;
    public static final int GAME = 1;
    public static final int NEWGAME = 2;
    public static final int SOUNDSET = 3;
    public static final int DIFFICULTY = 4;

    public static final int HELP = 6;
    public static final int LOADGAME = 7;
    public static final int SAVEGAME = 8;
    public static final int QUIT = 9;
    public static final int QUITTITLE = 10;
    public static final int NETWORKGAME = 11;
    public static final int MULTIPLAYER = 12;
    public static final int COLORCORR = 13;
    public static final int NEWADDON = 14;
    public static final int OPTIONS = 15;
    public static final int USERCONTENT = 16;
    public static final int CORRUPTLOAD = 17;
    public static final int TRACKPLAYER = 18;
    private final Engine engine;
    private final BuildGame app;

    public RRMenuHandler(BuildGame app) {
        super(app);
        mMenus = new BuildMenu[19];
        this.engine = app.pEngine;
        this.app = app;
    }

    @Override
    public void mDrawMenu() {
        if (screensize != 0) {
            vscrn(0);
        }
        Renderer renderer = game.getRenderer();
        if (!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof BuildMenuList) && !(app.pMenu.getCurrentMenu() instanceof InterfaceMenu)) {
            int tile = LOADSCREEN;
            ArtEntry pic = renderer.getTile(tile);
            int xdim = renderer.getWidth();
            int ydim = renderer.getHeight();

            float kt = xdim / (float) ydim;
            float kv = pic.getWidth() / (float) pic.getHeight();
            float scale;
            if (kv >= kt) {
                scale = (ydim + 1) / (float) pic.getHeight();
            } else {
                scale = (xdim + 1) / (float) pic.getWidth();
            }

            renderer.rotatesprite(0, 0, (int) (scale * 65536), 0, tile, 127, 4, 8 | 16 | 1);
        }

        super.mDrawMenu();
    }

    @Override
    public void mClose() {
        super.mClose();
        vscrn(ud.screen_size);
    }

    @Override
    public int getShade(MenuItem item) {
        int shade = 8;
        if (item != null && item.isFocused()) {
            shade = 8 + mulscale(16, EngineUtils.sin((32 * engine.getTotalClock()) & 2047), 16);
        }

        return shade;
    }

    @Override
    public int getPal(Font font, MenuItem item) {
        if (item instanceof MenuFileBrowser) {
            return 12;
        }

        if (item != null) {
            if (!item.isEnabled()) {
                return 1;
            }

            return item.pal;
        }

        return 10;
    }

    @Override
    public void mPostDraw(MenuItem item) {
        int shade = 8 - (engine.getTotalClock() & 0x3F);
        Renderer renderer = game.getRenderer();

        if (item.isFocused()) {
            if (item instanceof MenuButton) {
                int scale = 8192;
                int px = item.x;
                int py = item.y;

                int xoff = 0, yoff = 0;
                if (item.font.equals(app.getFont(1))) {
                    scale /= 2;
                    xoff = -20;
                    yoff = 6;
                }
                if (item.font.equals(app.getFont(2))) {
                    xoff = (engine.getTile(BIGFNTCURSOR).getWidth() - 4);
                    yoff = 12;
                }

                if (item.align == 1) {
                    int centre = 320 >> 2;
                    renderer.rotatesprite(((320 >> 1) + (centre >> 1) + 70) << 16, (py + yoff) << 16, scale, 0, SPINNINGNUKEICON + 15 - ((15 + (engine.getTotalClock() >> 3)) & 15), shade, 0, 10);
                    renderer.rotatesprite(((320 >> 1) - (centre >> 1) - 70) << 16, (py + yoff) << 16, scale, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                } else {
                    renderer.rotatesprite((px + xoff) << 16, (py + yoff) << 16, scale, 0, SPINNINGNUKEICON + (((engine.getTotalClock() >> 3)) % 7), shade, 0, 10);
                }
            }

            if (item instanceof MenuVariants) {
                renderer.rotatesprite(160 << 16, (item.y + engine.getTile(BIGFNTCURSOR).getHeight()) << 16, 8192, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), 8 - (engine.getTotalClock() & 0x3F), 0, 2 | 8);
            }
            if (item instanceof MenuSwitch || item instanceof MenuSlider || item instanceof MenuTextField || item instanceof MenuConteiner) {
                int scale = 8192;
                int px = item.x;
                int py = item.y;

                int xoff = 0, yoff = 0;
                if (item.font.equals(app.getFont(1))) {
                    scale /= 2;
                    xoff = -10;
                    yoff = 6;
                }
                if (item.font.equals(app.getFont(2))) {
                    xoff = -20;
                    yoff = 12;
                }

                renderer.rotatesprite((px + xoff) << 16, (py + yoff) << 16, scale, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
            }


            if (item instanceof MenuList) {
                if (item instanceof MenuJoyList) {
                    MenuList list = (MenuList) item;

                    int px = list.x;

                    int focus = list.l_nFocus;
                    if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.rowCount) {
                        return;
                    }

                    int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

                    renderer.rotatesprite((px - 10) << 16, (py + 3) << 16, 4096, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                } else if (item instanceof MenuKeyboardList) {
                    MenuList list = (MenuList) item;

                    int px = list.x;

                    int focus = list.l_nFocus;
                    if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.rowCount) {
                        return;
                    }

                    int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

                    renderer.rotatesprite((px - 10) << 16, (py + 3) << 16, 4096, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                } else if (item instanceof MenuSlotList) {
                    MenuSlotList list = (MenuSlotList) item;
                    if (list.deleteQuestion) {
                        renderer.rotatesprite(160 << 16, 135 << 16, 8192, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                    } else {
                        int px = list.x;
                        int focus = list.l_nFocus;
                        if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.rowCount) {
                            return;
                        }

                        int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
                        renderer.rotatesprite((px + 123) << 16, (py + 3) << 16, 4096, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                    }
                } else if (item instanceof MenuResolutionList) {
                    MenuList list = (MenuList) item;

                    int px = list.x + 4;

                    int focus = list.l_nFocus;
                    if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.rowCount) {
                        return;
                    }

                    int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

                    renderer.rotatesprite((px) << 16, (py + 6) << 16, 8192, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                } else //select skill list
                {
                    MenuList list = (MenuList) item;
                    if (list.l_nFocus == -1 || list.l_nFocus < list.l_nMin || list.l_nFocus >= list.l_nMin + list.rowCount) {
                        return;
                    }

                    int px = list.x;
                    int py = list.y + (list.l_nFocus - list.l_nMin) * (list.mFontOffset()) + 12;

                    int centre = 320 >> 2;
                    renderer.rotatesprite((px + (320 >> 1) + (centre >> 1) + 70) << 16, (py) << 16, 8192, 0, SPINNINGNUKEICON + 15 - ((15 + (engine.getTotalClock() >> 3)) & 15), shade, 0, 10);
                    renderer.rotatesprite((px + (320 >> 1) - (centre >> 1) - 70) << 16, (py) << 16, 8192, 0, SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), shade, 0, 10);
                }
            } else if (item instanceof MenuFileBrowser) {
                MenuFileBrowser list = (MenuFileBrowser) item;
                int px = list.x;

                int focus = list.getFocus();
                if (focus == -1 || focus < list.getMin() || focus >= list.getMin() + list.getRowCount()) {
                    return;
                }

                int py = list.y + (focus - list.getMin()) * (list.mFontOffset()) + 23;
                int column = list.getColumn();

                String value = list.getText(column, focus);
                int textWidth = list.font.getWidth(value, 1.0f);
                if (column == 1) { // FILE
                    px += list.width - textWidth - 24;
                } else {
                    px += textWidth + 24;
                }

                renderer.rotatesprite(px << 16, py + 3 << 16, 3000, 0, SPINNINGNUKEICON + 15 - ((15 + (engine.getTotalClock() >> 3)) & 15), 0, 0, 10);
            }
        }
    }

    @Override
    public void mDrawMouse(int x, int y) {
        if (!app.pCfg.isMenuMouse()) {
            return;
        }

        Renderer renderer = game.getRenderer();
        int xdim = renderer.getWidth();
        int ydim = renderer.getHeight();

        int zoom = scale(0x10000, ydim, 200);
        int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.getgMouseCursorSize(), 16), 16);
        int xoffset = 0; //mulscale(16, czoom, 16);
        int yoffset = 0; //mulscale(16, czoom, 16);
        int ang = 0; //1800;

        renderer.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim - 1, ydim - 1);
    }

    @Override
    public void mDrawBackButton() {
        if (!app.pCfg.isMenuMouse()) {
            return;
        }

        Renderer renderer = game.getRenderer();
        int ydim = renderer.getHeight();

        int zoom = scale(16384, ydim, 200);
        if (mCount > 1) {
            //Back button
            ArtEntry pic = engine.getTile(BACKBUTTON);

            int shade = 16 + mulscale(16, EngineUtils.sin((20 * engine.getTotalClock()) & 2047), 16);
            renderer.rotatesprite(0, (ydim - mulscale(pic.getHeight(), zoom, 16)) << 16, zoom, 0, BACKBUTTON, shade, 0, 8 | 16, 0, 0, mulscale(zoom, pic.getWidth() - 1, 16), ydim - 1);
        }
    }

    @Override
    public boolean mCheckBackButton(int mx, int my) {
        Renderer renderer = game.getRenderer();
        int ydim = renderer.getHeight();

        int zoom = scale(16384, ydim, 200);
        ArtEntry pic = engine.getTile(BACKBUTTON);

        int size = mulscale(pic.getWidth(), zoom, 16);
        int bx = 0;
        int by = ydim - size;
        if (mx >= bx && mx < bx + size) {
            return my >= by && my < by + size;
        }

        return false;
    }

    @Override
    public void mSound(MenuItem item, MenuOpt opt) {
        switch (opt) {
            case Open:
//			if(mCount > 1)
                sound(PISTOL_BODYHIT);
//		    else
//				sound(JIBBED_ACTOR6);
                break;
            case Close:
                sound(EXITMENUSOUND);
                break;
            case UP:
            case DW:
                sound(335);
                break;
            case LEFT:
            case RIGHT:
            case ENTER:
                if (opt == MenuOpt.ENTER && item instanceof MenuConteiner
                        && item.getClass().getEnclosingClass() == MenuVideoMode.class) {
                    break;
                }

                if (item instanceof MenuSlider
                        || item instanceof MenuSwitch
                        || item instanceof MenuConteiner) {
                    sound(KICK_HIT);
                }

                if (item instanceof MenuFileBrowser && opt != MenuOpt.ENTER) {
                    sound(KICK_HIT);
                }

                break;
            default:
                break;
        }
    }

}
