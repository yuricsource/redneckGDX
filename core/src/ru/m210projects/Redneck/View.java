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

import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Render.Mirror;
import ru.m210projects.Build.Render.RenderedSpriteList;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.*;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.Types.font.BitmapFont;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Factory.RRNetwork;
import ru.m210projects.Redneck.Factory.RRRenderer;
import ru.m210projects.Redneck.Menus.InterfaceMenu;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Render.AbstractRenderer.DEFAULT_SCREEN_FADE;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Actors.isPsychoSkill;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.HELP;
import static ru.m210projects.Redneck.Gamedef.getincangle;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.RSector.ldist;
import static ru.m210projects.Redneck.Weapons.displayweapon;

public class View {

    public static final String deathMessage = "or \"ENTER\" to load last saved game";
    public static final int MAXUSERQUOTES = 4;
    private static final char[] buffer = new char[256];
    private static final int[] tempsectorz = new int[MAXSECTORS];
    private static final short[] tempsectorpicnum = new short[MAXSECTORS];
    private static final PlayerOrig viewout = new PlayerOrig();
    public static int oyrepeat = -1;
    public static int gPlayerIndex = -1;
    public static int quotebot, quotebotgoal;
    public static final short[] user_quote_time = new short[MAXUSERQUOTES];
    public static final char[][] user_quote = new char[MAXUSERQUOTES][80];
    public static short fta, ftq, zoom, over_shoulder_on;
    public static final int[] loogiex = new int[64];
    public static final int[] loogiey = new int[64];
    public static int cameradist = 0, cameraclock = 0;
    public static int gNameShowTime;
    public static int oviewingrange;
    public static String lastmessage;
    public static int lastvisinc;

    public static void adduserquote(char[] daquote) {
        for (int i = MAXUSERQUOTES - 1; i > 0; i--) {
            System.arraycopy(user_quote[i - 1], 0, user_quote[i], 0, 80);
            user_quote_time[i] = user_quote_time[i - 1];
        }
        System.arraycopy(daquote, 0, user_quote[0], 0, Math.min(daquote.length, 80));

        int len = 0;
        while (len < daquote.length && daquote[len] != 0) {
            len++;
        }
        Console.out.println(new String(daquote, 0, len), OsdColor.GREEN);

        user_quote_time[0] = 180;
    }

    public static void displayrest(int smoothratio) {
        int a, i, j;

        int cposx, cposy;
        PlayerStruct pp = ps[screenpeek];

        float cang;

        int cr = 0, cg = 0, cb = 0, cf = 0;
        boolean dotint = false;

        Renderer renderer = game.getRenderer();
        if (changepalette != 0) {
            setgamepalette(pp, pp.palette);
            changepalette = 0;
        }

        if (pp.pals_time > 0 && pp.loogcnt == 0) {
            dotint = true;
            cr = pp.pals[0];
            cg = pp.pals[1];
            cb = pp.pals[2];
            cf = pp.pals_time;

            if (game.isSoftwareRenderer()) //software render
            {
                restorepalette = true;
            }
        } else if (restorepalette) {
            setgamepalette(pp, pp.palette);

            dotint = true;
            restorepalette = false;
        }

        if (dotint && !game.menu.gShowMenu) {
            palto(cr, cg, cb, cf | 128);
            game.getRenderer().showScreenFade(DEFAULT_SCREEN_FADE);
        }

        i = pp.cursectnum;

        Sector psec = boardService.getSector(i);
        if (psec != null) {
            show2dsector.setBit(i);

            for (ListNode<Wall> wn = psec.getWallNode(); wn != null; wn = wn.getNext()) {
                Wall wal = wn.get();

                Sector sec = boardService.getSector(wal.getNextsector());
                if (sec == null) {
                    continue;
                }

                if ((wal.getCstat() & 0x0071) != 0) {
                    continue;
                }

                Wall nextwal = boardService.getWall(wal.getNextwall());
                if (nextwal == null || (nextwal.getCstat() & 0x0071) != 0) {
                    continue;
                }
                if (sec.getLotag() == 32767) {
                    continue;
                }
                if (sec.getCeilingz() >= sec.getFloorz()) {
                    continue;
                }
                show2dsector.setBit(i);
            }
        }

        boolean isMenuShowing = game.menu.gShowMenu && !(game.menu.getCurrentMenu() instanceof InterfaceMenu);

        if (ud.camerasprite == -1) {
            if (ud.overhead_on != 2 && pp.newowner < 0) {
                displayweapon(screenpeek);
                if (over_shoulder_on == 0 && !isMenuShowing) {
                    displaymasks(screenpeek);
                }
            }

            if (ud.overhead_on > 0) {
                if (!ud.scrollmode) {
                    if (pp.newowner == -1) {
                        if (screenpeek == myconnectindex && numplayers > 1) {
                            RRNetwork net = game.net;
                            cposx = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
                            cposy = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
                            cang = net.predictOld.ang + (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio / 65536.0f;
                        } else {
                            cposx = pp.oposx + mulscale((pp.posx - pp.oposx), smoothratio, 16);
                            cposy = pp.oposy + mulscale((pp.posy - pp.oposy), smoothratio, 16);
                            cang = pp.oang + (BClampAngle(pp.ang + 1024 - pp.oang) - 1024) * smoothratio / 65536.0f;
                        }
                    } else {
                        cposx = pp.oposx;
                        cposy = pp.oposy;
                        cang = pp.oang;
                    }
                } else {

                    ud.fola += (int) (ud.folavel / 8.0f);
                    ud.folx += (ud.folfvel * EngineUtils.sin((512 + 2048 - ud.fola) & 2047)) >> 14;
                    ud.foly += (ud.folfvel * EngineUtils.sin((512 + 1024 - 512 - ud.fola) & 2047)) >> 14;

                    cposx = ud.folx;
                    cposy = ud.foly;
                    cang = ud.fola;
                }

                if (ud.overhead_on == 2) {
                    renderer.clearview(0);
                    renderer.drawmapview(cposx, cposy, zoom, (short) cang);
                }
                renderer.drawoverheadmap(cposx, cposy, zoom, (short) cang);

                if (ud.overhead_on == 2) {
                    if (ud.screen_size > 0) {
                        a = 145;
                    } else {
                        a = 182;
                    }

                    Arrays.fill(buffer, (char) 0);
                    buildString(buffer, 0, currentGame.episodes[ud.volume_number].Title);
                    game.getFont(0).drawTextScaled(renderer, 5, a, buffer, 1.0f, -128, 0, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);
                    Arrays.fill(buffer, (char) 0);
                    buildString(buffer, 0, currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number));
                    game.getFont(0).drawTextScaled(renderer, 5, a + 6, buffer, 1.0f, -128, 0, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);

                    if (cfg.gShowStat != 0) {
                        int k = 0;
                        if (ud.screen_size > 0 && ud.multimode > 1) {
                            j = 0;
                            k = 20;
                            for (i = connecthead; i >= 0; i = connectpoint2[i]) {
                                if (i > j) {
                                    j = i;
                                }
                            }

                            if (j >= 4 && j <= 8) {
                                k += 20;
                            } else if (j > 8 && j <= 12) {
                                k += 40;
                            } else if (j > 12) {
                                k += 60;
                            }
                        }
                        viewDrawStats(5, 5 + k, cfg.gStatSize, true);
                    }
                }
            }
        }

        if (isMenuShowing) {
            return;
        }

        if (ps[myconnectindex].newowner == -1 && ud.overhead_on == 0 && ud.crosshair != 0 && ud.camerasprite == -1) {
            renderer.rotatesprite((160 - (ps[myconnectindex].look_ang >> 1)) << 16, 100 << 16, cfg.gCrossSize, 0, CROSSHAIR, 0, 0, 2 + 1);
        }

        coolgaugetext(screenpeek);
        operatefta();
        Sprite psp = boardService.getSprite(ps[myconnectindex].i);

        if (!isPsychoSkill()) {
            if (fta > 1 && psp != null && psp.getExtra() <= 0 && myconnectindex == screenpeek && ud.multimode < 2 && lastload != null && lastload.exists() && !DemoScreen.isDemoPlaying()) {
                int k = getftacoord();
                game.getFont(1).drawTextScaled(renderer, 320 >> 1, k + 10, deathMessage, 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        }

        if (ud.screen_size > 0 && cfg.gShowStat == 1 && ud.overhead_on != 2) {
            int y = 202;
            if (ud.screen_size == 1) {
                y = 162;
            }
            if (ud.screen_size == 2 || ud.screen_size == 3) {
                y = 158;
            }
            if (ud.screen_size >= 4) {
                y = 148;
            }
            viewDrawStats(10, y, cfg.gStatSize, false);
        }

        if (game.isCurrentScreen(gGameScreen) && engine.getTotalClock() < gNameShowTime) {
            Transparent transp = Transparent.None;
            if (engine.getTotalClock() > gNameShowTime - 20) {
                transp = Transparent.Bit1;
            }
            if (engine.getTotalClock() > gNameShowTime - 10) {
                transp = Transparent.Bit2;
            }

            if (cfg.showMapInfo != 0 && !game.menu.gShowMenu) {
                if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
                    if (ud.volume_number < nMaxEpisodes && currentGame.episodes[ud.volume_number] != null ) {
                        game.getFont(2).drawTextScaled(renderer, 160, 114, currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number), 1.0f, -128, 0, TextAlign.Center, transp, ConvertType.Normal, false);
                    }
                } else {
                    game.getFont(2).drawTextScaled(renderer, 160, 114, boardfilename.getName(), 1.0f, -128, 0, TextAlign.Center, transp, ConvertType.Normal, false);
                }
            }
        }

        if (game.getProcessor().getRRMessage().isCaptured()) {
            game.getProcessor().getRRMessage().draw();
        }

        if (game.gPaused && !game.menu.gShowMenu) {
            game.getFont(2).drawTextScaled(renderer, 160, 100, "GAME PAUSED", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }

        if (gPlayerIndex != -1 && gPlayerIndex != myconnectindex) {
            int len;
            if (ud.user_name[gPlayerIndex] == null || ud.user_name[gPlayerIndex].isEmpty()) {
                len = buildString(buf, 0, "Player ", gPlayerIndex + 1);
            } else {
                len = buildString(buf, 0, ud.user_name[gPlayerIndex]);
            }
            len = buildString(buf, len, " (", ps[gPlayerIndex].last_extra);
            buildString(buf, len, "hp)");
            int shade = 16 - (engine.getTotalClock() & 0x3F);

            int ydim = renderer.getHeight();

            int windowy1 = 0;
            int y = scale(windowy1, 200, ydim) + 100;
            if (ud.screen_size <= 4) //XXX
            {
                y += (engine.getTile(BOTTOMSTATUSBAR).getHeight() + engine.getTile(1649).getHeight()) / 4;
            }

            game.getFont(1).drawTextScaled(renderer, 160, y, buf, 1.0f, shade, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }

        if (ud.coords != 0) {
            coords(screenpeek);
        }
    }

    public static int getftacoord() {
        int k;
        if (ud.screen_size > 0 && ud.multimode > 1) {
            int j = 0;
            k = 8;
            for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
                if (i > j) {
                    j = i;
                }
            }

            if (j >= 4 && j <= 8) {
                k += 8;
            } else if (j > 8 && j <= 12) {
                k += 16;
            } else if (j > 12) {
                k += 24;
            }
        } else {
            k = 0;
        }

        if (ftq == 115 || ftq == 116) {
            k = quotebot;
            for (int i = 0; i < MAXUSERQUOTES; i++) {
                if (user_quote_time[i] <= 0) {
                    break;
                }
                k -= 8;
            }
            k -= 4;
        }

        return k;
    }

    public static void operatefta() {
        Renderer renderer = game.getRenderer();
        int i, j, k;

        if (ud.screen_size <= 2) {
            j = 200 - 20;
        } else if (ud.screen_size == 3) {
            j = 200 - 55;
        } else {
            j = 200 - 63;
        }

        quotebot = Math.min(quotebot, j);
        if (MODE_TYPE) {
            j -= 10;
        }
        quotebotgoal = j;
        j = quotebot;
        for (i = 0; i < MAXUSERQUOTES; i++) {
            k = user_quote_time[i];
            if (k <= 0) {
                break;
            }
            if (k > 4) {
                game.getFont(1).drawTextScaled(renderer, 320 >> 1, j, user_quote[i], 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            } else if (k > 2) {
                game.getFont(1).drawTextScaled(renderer, 320 >> 1, j, user_quote[i], 1.0f, 0, 0, TextAlign.Center, Transparent.Bit1, ConvertType.Normal, false);
            } else {
                game.getFont(1).drawTextScaled(renderer, 320 >> 1, j, user_quote[i], 1.0f, 0, 0, TextAlign.Center, Transparent.Bit2, ConvertType.Normal, false);
            }
            j -= 10;
        }

        if (fta <= 1) {
            return;
        }

        k = getftacoord();

        j = fta;
        if (j > 4) {
            game.getFont(1).drawTextScaled(renderer, 320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        } else if (j > 2) {
            game.getFont(1).drawTextScaled(renderer, 320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 1.0f, 0, 0, TextAlign.Center, Transparent.Bit1, ConvertType.Normal, false);
        } else {
            game.getFont(1).drawTextScaled(renderer, 320 >> 1, k, currentGame.getCON().fta_quotes[ftq], 1.0f, 0, 0, TextAlign.Center, Transparent.Bit2, ConvertType.Normal, false);
        }
    }

    public static void displayfragbar(int yoffset, boolean showpalette) {
        int row = (ud.multimode - 1) / 4;
        Renderer renderer = game.getRenderer();
        if (row >= 0) {
            if (yoffset > 0) {
                yoffset -= 9 * row;
            }
            for (int r = 0; r <= row; r++) {
                renderer.rotatesprite(0, yoffset + (r * engine.getTile(FRAGBAR).getHeight()) << 16, 34000, 0, FRAGBAR, 0, 0, 2 + 8 + 16 + 64);
            }

            for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
                if (ud.user_name[i] == null || ud.user_name[i].isEmpty()) {
                    buildString(buffer, 0, "Player ", i + 1);
                } else {
                    buildString(buffer, 0, ud.user_name[i]);
                }


                int pal = 0;
                if (showpalette && boardService.getBoard() != null) {
                    Sprite psp = boardService.getSprite(ps[i].i);
                    if (psp != null) {
                        pal = psp.getPal();
                    }
                }

                game.getFont(0).drawTextScaled(renderer, 26 + (73 * (i & 3)), yoffset + 2 + ((i & 28) << 1), buffer, 1.0f, 0, showpalette ? pal : 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                buildString(buffer, 0, "", ps[i].frag - ps[i].fraggedself);
                game.getFont(0).drawTextScaled(renderer, 23 + 50 + (73 * (i & 3)), yoffset + 2 + ((i & 28) << 1), buffer, 1.0f, 0, showpalette ? pal : 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }
        }
    }

    public static void displaymeters(int snum, int flags) {
        PlayerStruct p = ps[snum];
        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(257 << 16, 181 << 16, 0x8000, p.alcohol_meter, 62, 0, 0, 10 | flags);
        renderer.rotatesprite(293 << 16, 181 << 16, 0x8000, p.gut_meter, 62, 0, 0, 10 | flags);

        int x, pic;
        if (p.alcohol_amount >= 0 && p.alcohol_amount <= 30) {
            x = 239 << 16;
            pic = 920;
        } else if (p.alcohol_amount >= 31 && p.alcohol_amount <= 65) {
            x = 248 << 16;
            pic = 921;
        } else if (p.alcohol_amount >= 66 && p.alcohol_amount <= 87) {
            x = 256 << 16;
            pic = 922;
        } else {
            x = 265 << 16;
            pic = 923;
        }
        renderer.rotatesprite(x, 190 << 16, 0x8000, 0, pic, 0, 0, 10 | 16 | flags);

        if (p.gut_amount >= 0 && p.gut_amount <= 30) {
            x = 276 << 16;
            pic = 920;
        } else if (p.gut_amount >= 31 && p.gut_amount <= 65) {
            x = 285 << 16;
            pic = 921;
        } else if (p.gut_amount >= 66 && p.gut_amount <= 87) {
            x = 294 << 16;
            pic = 922;
        } else {
            x = 302 << 16;
            pic = 923;
        }
        renderer.rotatesprite(x, 190 << 16, 0x8000, 0, pic, 0, 0, 10 | 16 | flags);
    }

    @SuppressWarnings("unused")
    public static void debuginfo(int x, int y) {
        Renderer renderer = game.getRenderer();
        BitmapFont largeFont = EngineUtils.getLargeFont();

        buildString(buffer, 0, "totalclock= ", engine.getTotalClock());
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "global_random= ", global_random);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "randomseed= ", engine.getrand());
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "posx= ", ps[0].posx);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "posy= ", ps[0].posy);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "posz= ", ps[0].posz);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);

        y += 10;

        buildString(buffer, 0, "ang= ", Float.toString(ps[0].ang));
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "horiz= ", Float.toString(ps[0].horiz));
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "xvel= ", ps[0].posxv);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "yvel= ", ps[0].posyv);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarSpeed= ", ps[0].CarSpeed);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "VBumpTarget= ", ps[0].VBumpTarget);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "VBumpNow= ", ps[0].VBumpNow);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar1= ", ps[0].CarVar1);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar2= ", ps[0].CarVar2);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar3= ", ps[0].CarVar3);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar4= ", ps[0].CarVar4);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar5= ", ps[0].CarVar5);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        y += 10;

        buildString(buffer, 0, "CarVar6= ", ps[0].CarVar6);
        largeFont.drawText(renderer, x, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
    }

    public static void coolgaugetext(int snum) {
        int i, o, ss;

        Renderer renderer = game.getRenderer();
        PlayerStruct p = ps[snum];
        Sprite psp = boardService.getSprite(p.i);
        if (psp == null) {
            return;
        }

//	    debuginfo(20, 40);

        if (p.invdisptime > 0) {
            displayinventory(p);
        }

        if (screenpeek != myconnectindex) {
            if (ud.user_name[screenpeek] == null || ud.user_name[screenpeek].isEmpty()) {
                buildString(buf, 0, "View from player ", screenpeek + 1);
            } else {
                buildString(buf, 0, "View from ", ud.user_name[screenpeek]);
            }
            int shade = 16 - (engine.getTotalClock() & 0x3F);
            int ydim = renderer.getHeight();
            int windowy1 = 0;
            game.getFont(1).drawTextScaled(renderer, 160, scale(windowy1, 200, ydim) + 10, buf, 1.0f, shade, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }

        ss = ud.screen_size;
        if (ss < 1) {
            return;
        }

        if (ud.multimode > 1 || mFakeMultiplayer) {
            displayfragbar(0, true);
        }

        if (ss == 1)   //DRAW MINI STATUS BAR:
        {
            renderer.rotatesprite(0x20000, 11272192, 0x8000, 0, HEALTHBOX, 0, 21, 26 | 256);

            if (psp.getPal() == 1 && p.last_extra < 2) {
                digitalnumber(20, 200 - 17, 1, -16, 10 + 16 + 256);
            } else {
                digitalnumber(20, 200 - 17, p.last_extra, -16, 10 + 16 + 256);
            }


            int x = 41;
            if (p.curr_weapon == HANDREMOTE_WEAPON) {
                i = CROSSBOW_WEAPON;
            } else {
                i = p.curr_weapon;
            }
            if (p.ammo_amount[i] != 0) {
                renderer.rotatesprite(x << 16, (200 - 28) << 16, 0x8000, 0, AMMOBOX, 0, 21, 26 | 256);
                digitalnumber(x + 16, 200 - 17, p.ammo_amount[i], -16, 10 + 16 + 256);
                x += engine.getTile(AMMOBOX).getWidth() / 2 + 2;
            }

            if ((p.gotkey[1] | p.gotkey[2] | p.gotkey[3]) != 0) {
                renderer.rotatesprite(x << 16, (200 - 28) << 16, 0x8000, 0, AMMOBOX, 0, 21, 26 | 256);
                renderer.rotatesprite(x << 16, (200 - 28) << 16, 0x8000, 0, KEYSIGN, 0, 21, 10 + 16 + 256);

                if (p.gotkey[3] != 0) {
                    int pal = 23;
                    if (cfg.gColoredKeys) {
                        pal = 7;
                    }
                    renderer.rotatesprite(x + 5 << 16, 182 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
                if (p.gotkey[2] != 0) {
                    int pal = 21;
                    if (cfg.gColoredKeys) {
                        pal = 2;
                    }
                    renderer.rotatesprite(x + 18 << 16, 182 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
                if (p.gotkey[1] != 0) {
                    int pal = 0;
                    if (cfg.gColoredKeys) {
                        pal = 1;
                    }
                    renderer.rotatesprite(x + 11 << 16, 189 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
                x += engine.getTile(KEYSIGN).getWidth() / 2 + 2;
            }

            { //my gutmeter
                renderer.rotatesprite(225 << 16, 172 << 16, 0x9000, 0, GUTSMETTER, 0, 21, 26 | 512);
                renderer.rotatesprite(251 << 16, 189 << 16, 0x9000, p.alcohol_meter, 62, 0, 0, 10 | 512);
                renderer.rotatesprite(293 << 16, 189 << 16, 0x9000, p.gut_meter, 62, 0, 0, 10 | 512);
            }

            if (p.inven_icon != 0) {
                renderer.rotatesprite(x << 16, (200 - 30) << 16, 0x8000, 0, INVENTORYBOX, 0, 21, 26 | 256);
                buf[0] = '%';
                buf[1] = 0;
                switch (p.inven_icon) {
                    case 1:
                        i = 1645;
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignLeft, false);
                        break;
                    case 2:
                        i = 1654;
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignLeft, false);
                        break;
                    case 3:
                        i = 1655;
                        break;
                    case 4:
                        i = 1652;
                        break;
                    case 5:
                        i = 1646;
                        break;
                    case 6:
                        i = 1653;
                        // #GDX 09.02.2024
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignLeft, false);
                        break;
                    case 7:
                        i = BOOT_ICON;
                        // #GDX 09.02.2024
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignLeft, false);
                        break;
                    default:
                        i = -1;
                }
                if (i >= 0) {
                    renderer.rotatesprite((x + 6) << 16, (200 - 21) << 16, 0x8000, 0, i, 0, 0, 26 | 256);
                }

                if (p.inven_icon >= 6) {
                    game.getFont(0).drawTextScaled(renderer, x + 22, 180, "AUTO", 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);
                }
                switch (p.inven_icon) {
                    case 1:
                        i = p.whishkey_amount;
                        break;
                    case 2:
                        i = ((p.moonshine_amount + 3) >> 2);
                        break;
                    case 3:
                        i = ((p.beer_amount) / 400);
                        break;
                    case 4:
                        i = ((p.cowpie_amount) / 100);
                        break;
                    case 5:
                        i = p.yeehaa_amount / 12;
                        break;
                    case 6:
                        i = ((p.snorkle_amount + 63) >> 6);
                        break;
                    case 7:
                        i = (p.boot_amount / 10 >> 1);
                        break;
                }
                invennum(x + 27, 192, i, 0, 8 | 256);
            }

            return;
        }

        if (ss == 2) //GDX'S STATUS BAR
        {
            //left part
            renderer.rotatesprite(81 << 16, 183 << 16, 0x8000, 0, WIDEHUD_PART1, 0, 0, 10 | 256);

            //health
            if (psp.getPal() == 1 && p.last_extra < 2) {
                digitalnumber(59, 200 - 20, 1, -16, 10 + 16 + 256);
            } else {
                digitalnumber(59, 200 - 20, p.last_extra, -16, 10 + 16 + 256);
            }

            //ammo
            if (p.curr_weapon == HANDREMOTE_WEAPON) {
                i = CROSSBOW_WEAPON;
            } else {
                i = p.curr_weapon;
            }
            if (p.ammo_amount[i] != 0) {
                digitalnumber(96, 200 - 20, p.ammo_amount[i], -16, 10 + 16 + 256);
            }

            if (ud.multimode > 1 && ud.coop != 1) {
                if (engine.getTile(KILLSSIGN).exists()) {
                    renderer.rotatesprite(118 << 16, (168) << 16, 32768, 0, KILLSSIGN, 0, 0, 10 + 16);
                } else {
                    renderer.rotatesprite(126 << 16, (169) << 16, 65536, 0, KILLSICON, 0, 0, 10 + 16);
                }
            }

            if (ud.multimode > 1 && ud.coop != 1) {
                digitalnumber(135, 200 - 20, max(p.frag - p.fraggedself, 0), -16, 10 + 16);
            } else {
                //keys
                int x = 117;
                if (p.gotkey[3] != 0) {
                    int pal = 23;
                    if (cfg.gColoredKeys) {
                        pal = 7;
                    }
                    renderer.rotatesprite(x + 5 << 16, 180 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
                if (p.gotkey[2] != 0) {
                    int pal = 21;
                    if (cfg.gColoredKeys) {
                        pal = 2;
                    }
                    renderer.rotatesprite(x + 18 << 16, 180 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
                if (p.gotkey[1] != 0) {
                    int pal = 0;
                    if (cfg.gColoredKeys) {
                        pal = 1;
                    }
                    renderer.rotatesprite(x + 11 << 16, 187 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16 + 256);
                }
            }

            //right part
            renderer.rotatesprite(244 << 16, 183 << 16, 0x8000, 0, WIDEHUD_PART2, 0, 0, 10 | 512);

            //inventory
            int x = 185;
            if (p.inven_icon != 0) {
                o = 179 << 16;
                buf[0] = '%';
                buf[1] = 0;
                switch (p.inven_icon) {
                    case 1:
                        i = 1645;
                        o = 178 << 16;
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 189, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignRight, false);
                        break;
                    case 2:
                        i = 1654;
                        o = 178 << 16;
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 189, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignRight, false);
                        break;
                    case 3:
                        i = 1655;
                        break;
                    case 4:
                        i = 1652;
                        break;
                    case 5:
                        i = 1646;
                        break;
                    case 6:
                        i = 1653;
                        o = 176 << 16;
                        // #GDX 09.02.2024
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 189, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignRight, false);
                        break;
                    case 7:
                        i = BOOT_ICON;
                        o = 178 << 16;
                        // #GDX 09.02.2024
                        game.getFont(0).drawCharScaled(renderer, (x + 37), 189, '%', 1.0f, 0, 6, Transparent.None, ConvertType.AlignRight, false);
                        break;
                    default:
                        i = -1;
                }
                if (i >= 0) {
                    renderer.rotatesprite((x + 6) << 16, o, 0x8000, 0, i, 0, 0, 26 | 512);
                }

                if (p.inven_icon >= 6) {
                    game.getFont(0).drawTextScaled(renderer, x + 22, 180, "AUTO", 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.AlignRight, false);
                }
                switch (p.inven_icon) {
                    case 1:
                        i = p.whishkey_amount;
                        break;
                    case 2:
                        i = ((p.moonshine_amount + 3) >> 2);
                        break;
                    case 3:
                        i = ((p.beer_amount) / 400);
                        break;
                    case 4:
                        i = ((p.cowpie_amount) / 100);
                        break;
                    case 5:
                        i = p.yeehaa_amount / 12;
                        break;
                    case 6:
                        i = ((p.snorkle_amount + 63) >> 6);
                        break;
                    case 7:
                        i = (p.boot_amount / 10 >> 1);
                        break;
                }
                invennum(x + 27, 191, i, 0, 8 | 512);
            }

            displaymeters(screenpeek, 512);
            return;
        }

        //DRAW/UPDATE FULL STATUS BAR:

        patchstatusbar(0, 0, 320, 200);

        if (ss > 3) {
            renderer.rotatesprite(0, 10354688, 0x8020, 0, 1649, 0, 0, 10 + 16);
            int wpic = 930;
            for (int w = 0; w < 9; w++, wpic++) {
                if (w == 4 && p.curr_weapon == CHICKENBOW_WEAPON) {
                    renderer.rotatesprite((32 * w + 18) << 16, 10485760, 0x8020, 0, 940, 0, 0, 10 + 16);
                    invennum(32 * w + 38, 160, p.ammo_amount[CHICKENBOW_WEAPON], 0, 8 + 16);
                } else {
                    if (p.gotweapon[w + 1]) {
                        renderer.rotatesprite((32 * w + 18) << 16, 10485760, 0x8020, 0, wpic, 0, 0, 10 + 16);
                    }
                    invennum(32 * w + 38, 159, p.ammo_amount[w + 1], 0, 8 + 16);
                }
            }
        }

        if (ud.multimode > 1 && ud.coop != 1) {
            if (renderer.getTile(KILLSSIGN).exists()) {
                renderer.rotatesprite(133 << 16, (168) << 16, 32768, 0, KILLSSIGN, 0, 0, 10 + 16);
            } else {
                renderer.rotatesprite(142 << 16, (169) << 16, 65536, 0, KILLSICON, 0, 0, 10 + 16);
            }
        }

        if (ud.multimode > 1 && ud.coop != 1) {
            digitalnumber(150, 180, max(p.frag - p.fraggedself, 0), -16, 10 + 16);
        } else {
            int x = 134;
            if (p.gotkey[3] != 0) {
                int pal = 23;
                if (cfg.gColoredKeys) {
                    pal = 7;
                }
                renderer.rotatesprite(x + 5 << 16, 180 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16);
            }
            if (p.gotkey[2] != 0) {
                int pal = 21;
                if (cfg.gColoredKeys) {
                    pal = 2;
                }
                renderer.rotatesprite(x + 18 << 16, 180 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16);
            }
            if (p.gotkey[1] != 0) {
                int pal = 0;
                if (cfg.gColoredKeys) {
                    pal = 1;
                }
                renderer.rotatesprite(x + 11 << 16, 187 << 16, 0x8000, 0, 1656, 0, pal, 10 + 16);
            }
        }

        if (psp.getPal() == 1 && p.last_extra < 2) {
            digitalnumber(64, 200 - 20, 1, -16, 10 + 16);
        } else {
            digitalnumber(64, 200 - 20, p.last_extra, -16, 10 + 16);
        }

        if (p.curr_weapon != KNEE_WEAPON) {
            if (p.curr_weapon == HANDREMOTE_WEAPON) {
                i = CROSSBOW_WEAPON;
            } else {
                i = p.curr_weapon;
            }
            digitalnumber(107, 200 - 20, p.ammo_amount[i], -16, 10 + 16);
        }

        i = 0;
        if (p.inven_icon != 0) {
            o = 179 << 16;
            buf[0] = '%';
            buf[1] = 0;
            switch (p.inven_icon) {
                case 1:
                    i = 1645;
                    o = 178 << 16;
                    game.getFont(0).drawCharScaled(renderer, 216, 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.Normal, false);
                    break;
                case 2:
                    i = 1654;
                    o = 178 << 16;
                    game.getFont(0).drawCharScaled(renderer, 216, 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.Normal, false);
                    break;
                case 3:
                    i = 1655;
                    break;
                case 4:
                    i = 1652;
                    break;
                case 5:
                    i = 1646;
                    break;
                case 6:
                    i = 1653;
                    o = 176 << 16;
                    // #GDX 09.02.2024
                    game.getFont(0).drawCharScaled(renderer, 216, 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.Normal, false);
                    break;
                case 7:
                    i = BOOT_ICON;
                    o = 178 << 16;
                    // #GDX 09.02.2024
                    game.getFont(0).drawCharScaled(renderer, 216, 190, '%', 1.0f, 0, 6, Transparent.None, ConvertType.Normal, false);
                    break;
            }
            renderer.rotatesprite(11993088, o, 32768, 0, i, 0, 0, 10 + 16);
            if (p.inven_icon >= 6) {
                game.getFont(0).drawTextScaled(renderer, 201, 180, "AUTO", 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }

            switch (p.inven_icon) {
                case 1:
                    i = p.whishkey_amount;
                    break;
                case 2:
                    i = ((p.moonshine_amount + 3) >> 2);
                    break;
                case 3:
                    i = ((p.beer_amount) / 400);
                    break;
                case 4:
                    i = ((p.cowpie_amount) / 100);
                    break;
                case 5:
                    i = p.yeehaa_amount / 12;
                    break;
                case 6:
                    i = ((p.snorkle_amount + 63) >> 6);
                    break;
                case 7:
                    i = (p.boot_amount / 10 >> 1);
                    break;
            }
            invennum(206, 192, i, 0, 8);

        }
        displaymeters(screenpeek, 0);
    }

    public static void displayrooms(int snum, int smoothratio) {
        int dst, j;
        short k;
        int tposx, tposy, i;
        short tang;

        PlayerStruct p = ps[snum];
        Sprite psp = boardService.getSprite(p.i);
        if (psp == null) {
            return;
        }

        gPlayerIndex = -1;

        RRRenderer renderer = game.getRenderer();
        if ((!game.menu.gShowMenu && ud.overhead_on == 2) || game.menu.isOpened(game.menu.mMenus[HELP])) {
            return;
        }

        if (p.fogtype != 0) {
            gVisibility = currentGame.getCON().const_visibility;
        }
        visibility = gVisibility;

        int cposx = p.posx;
        int cposy = p.posy;
        int cposz = p.posz;
        float cang = p.ang;
        float choriz = p.horiz + p.horizoff;
        int sect = p.cursectnum;

        Sector psec = boardService.getSector(sect);
        if (psec == null) {
            return;
        }

        Sprite s = boardService.getSprite(ud.camerasprite);
        if (s != null) {
            if (s.getYvel() < 0) {
                s.setYvel(-100);
            } else if (s.getYvel() > 199) {
                s.setYvel(300);
            }

            cang = (hittype[ud.camerasprite].tempang + mulscale((((s.getAng() + 1024 - hittype[ud.camerasprite].tempang) & 2047) - 1024), smoothratio, 16));

            se40code(s.getX(), s.getY(), s.getZ(), cang, s.getYvel(), smoothratio);

            renderer.drawrooms(s.getX(), s.getY(), s.getZ() - (4 << 8), cang, s.getYvel(), s.getSectnum());
            animatesprites(s.getX(), s.getY(), s.getZ() - (4 << 8), (short) cang, smoothratio);
            renderer.drawmasks();
        } else {
            i = divscale(1, psp.getYrepeat() + 28, 22);
            if (i != oyrepeat) {
                oyrepeat = i;
                vscrn(ud.screen_size);
            }

            if (oviewingrange != renderer.getViewingRange() && p.DrugMode == 0) {
                renderer.setaspect();
                oviewingrange = renderer.getViewingRange();
            }

            if (p.DrugMode > 0 && !MODE_TYPE && !game.gPaused) {
                renderer.setaspect(p.drug_aspect, renderer.getYXAspect());
            }

            if ((snum == myconnectindex) && (numplayers > 1)) {
                RRNetwork net = game.net;
                cposx = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
                cposy = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
                cposz = net.predictOld.z + mulscale((net.predict.z - net.predictOld.z), smoothratio, 16);
                cang = net.predictOld.ang + (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio / 65536.0f;
                cang += net.predictOld.lookang + (BClampAngle(net.predict.lookang + 1024 - net.predictOld.lookang) - 1024) * smoothratio / 65536.0f;
                choriz = net.predictOld.horiz + net.predictOld.horizoff + (((net.predict.horiz + net.predict.horizoff - net.predictOld.horiz - net.predictOld.horizoff) * smoothratio) / 65536.0f);
                sect = net.predict.sectnum;

                if ((ud.screen_tilting != 0 && p.rotscrnang != 0)) {
                    renderer.settiltang(net.predictOld.rotscrnang + mulscale(((net.predict.rotscrnang - net.predictOld.rotscrnang + 1024) & 2047) - 1024, smoothratio, 16));
                } else {
                    renderer.settiltang(0);
                }
            } else {
                cposx = p.prevView.x + mulscale((cposx - p.prevView.x), smoothratio, 16);
                cposy = p.prevView.y + mulscale((cposy - p.prevView.y), smoothratio, 16);
                cposz = p.prevView.z + mulscale((cposz - p.prevView.z), smoothratio, 16);
                cang = p.prevView.ang + (BClampAngle(cang + 1024 - p.prevView.ang) - 1024) * smoothratio / 65536.0f;
                cang += p.prevView.lookang + (BClampAngle(p.look_ang + 1024 - p.prevView.lookang) - 1024) * smoothratio / 65536.0f;
                choriz = (p.prevView.horiz + p.prevView.horizoff + ((choriz - p.prevView.horiz - p.prevView.horizoff) * smoothratio) / 65536.0f);

                if ((ud.screen_tilting != 0 && p.rotscrnang != 0)) {
                    renderer.settiltang(p.prevView.rotscrnang + mulscale(((p.rotscrnang - p.prevView.rotscrnang + 1024) & 2047) - 1024, smoothratio, 16));
                } else {
                    renderer.settiltang(0);
                }
            }

            Sprite spo = boardService.getSprite(p.newowner);
            if (spo != null) {
                cang = (short) (p.ang + p.look_ang);
                choriz = p.horiz + p.horizoff;
                cposx = p.posx;
                cposy = p.posy;
                cposz = p.posz;
                sect = spo.getSectnum();
                smoothratio = 65536;
            } else if (over_shoulder_on == 0) {
                cposz += p.opyoff + mulscale((p.pyoff - p.opyoff), smoothratio, 16);
            } else {
                view(p, cposx, cposy, cposz, sect, cang, choriz);

                cposx = viewout.ox;
                cposy = viewout.oy;
                cposz = viewout.oz;
                sect = viewout.os;
            }

            cz.set(hittype[p.i].ceilingz);
            fz.set(hittype[p.i].floorz);

            if (earthquaketime > 0 && p.on_ground) {
                cposz += 256 - (((earthquaketime) & 1) << 9);
                cang += (2 - ((earthquaketime) & 2)) << 2;
            }

            if (psp.getPal() == 1) {
                cposz -= (18 << 8);
            }

            Sprite newspo = boardService.getSprite(p.newowner);
            if (newspo != null) {
                choriz = (short) (100 + newspo.getShade());
            } else if (p.spritebridge == 0) {
                if (cposz < (p.truecz + (4 << 8))) {
                    cposz = cz.get() + (4 << 8);
                } else if (cposz > (p.truefz - (4 << 8))) {
                    cposz = fz.get() - (4 << 8);
                }
            }

            if (sect >= 0) {
                engine.getzsofslope(sect, cposx, cposy, fz, cz);
                if (cposz < cz.get() + (4 << 8)) {
                    cposz = cz.get() + (4 << 8);
                }
                if (cposz > fz.get() - (4 << 8)) {
                    cposz = fz.get() - (4 << 8);
                }
            }

            if (choriz > 299) {
                choriz = 299;
            } else if (choriz < -99) {
                choriz = -99;
            }

            se40code(cposx, cposy, cposz, cang, choriz, smoothratio);

            byte[] gotpic = renderer.getRenderedPics();
            if ((gotpic[MIRROR >> 3] & (1 << (MIRROR & 7))) > 0) {
                dst = 0x7fffffff;
                i = 0;
                for (k = 0; k < mirrorcnt; k++) {
                    Wall mwal = boardService.getWall(mirrorwall[k]);
                    if (mwal == null) {
                        continue;
                    }

                    j = klabs(mwal.getX() - cposx);
                    j += klabs(mwal.getY() - cposy);
                    if (j < dst) {
                        dst = j;
                        i = k;
                    }
                }

                Wall mwal = boardService.getWall(mirrorwall[i]);
                if (mwal != null && mwal.getOverpicnum() == MIRROR) {
                    Mirror mirror = renderer.preparemirror(cposx, cposy, cposz, cang, choriz, mirrorwall[i], mirrorsector[i]);

                    tposx = (int) mirror.getX();
                    tposy = (int) mirror.getY();
                    tang = (short) mirror.getAngle();

                    j = visibility;
                    visibility = (j >> 1) + (j >> 2);

                    renderer.drawrooms(tposx, tposy, cposz, tang, choriz, (short) (mirrorsector[i] + boardService.getSectorCount()));

                    display_mirror = 1;
                    animatesprites(tposx, tposy, cposz, tang, smoothratio);
                    display_mirror = 0;

                    renderer.drawmasks();
                    renderer.completemirror();   //Reverse screen x-wise in this function
                    visibility = j;
                }
                gotpic[MIRROR >> 3] &= ~(1 << (MIRROR & 7));
            }

            renderer.drawrooms(cposx, cposy, cposz, cang, choriz, sect);
            animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
            renderer.drawmasks();

            displaygeom3d(sect, cposx, cposy, cposz, choriz, cang, sect, smoothratio);
        }
    }

    public static void FTA(int q, PlayerStruct p) {
        if (q >= currentGame.getCON().fta_quotes.length) {
            Console.out.println("Invalid quote " + q, OsdColor.RED);
            return;
        }

        if (ud.fta_on == 1 && p == ps[screenpeek]) {
            if (fta > 0 && q != 115 && q != 116) {
                if (ftq == 115 || ftq == 116) {
                    return;
                }
            }

            fta = 100;

            if (ftq != q || q == 26) {
                ftq = (short) q;
            }

            char[] quote = currentGame.getCON().fta_quotes[ftq];
            int len = 0;
            while (len < quote.length && quote[len] != 0) {
                len++;
            }

            String message = new String(quote, 0, len);
            if (!message.equals(lastmessage)) {
                Console.out.println(message);
                lastmessage = message;
            }
        }
    }

    public static void animatesprites(int x, int y, int z, short a, int smoothratio) {
        int i, j, k, p;
        int l, t1, t3, t4;

        RRRenderer renderer = game.getRenderer();
        RenderedSpriteList renderedSpriteList = renderer.getRenderedSprites();
        for (j = 0; j < renderedSpriteList.getSize(); j++) {
            TSprite t = renderedSpriteList.get(j);
            i = t.getSpriteNum();
            Sprite s = boardService.getSprite(i);
            if (s == null)  {
                continue;
            }

            Sector sec = boardService.getSector(t.getSectnum());
            if (sec == null) {
                continue;
            }

            switch (t.getPicnum()) {
                case DOORKEY:
                    if (cfg.gColoredKeys) {
                        switch (t.getLotag()) {
                            case 100: //got1
                                t.setPal(1);
                                break;
                            case 101: //got2
                                t.setPal(2);
                                break;
                            case 102: //got3
                                t.setPal(7);
                                break;
                            default:
                                t.setPal(6);
                                break;
                        }
                    }
                    t.setShade(-128);
                    break;
                case BLOODPOOL:
                case FOOTPRINTS:
                case FOOTPRINTS2:
                case FOOTPRINTS3:
                case FOOTPRINTS4:
                    if (t.getShade() == 127) {
                        continue;
                    }
                    break;

                case BLOODSPLAT1:
                case BLOODSPLAT2:
                case BLOODSPLAT3:
                case BLOODSPLAT4:
                    if (ud.lockout != 0) {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                    } else if (t.getPal() == 6) {
                        t.setShade(-127);
                        continue;
                    }
                case BULLETHOLE:
                case CRACK1:
                case CRACK2:
                case CRACK3:
                case CRACK4:
                    t.setShade(16);
                    continue;
                case 1152:
                    break;
                default:
                    if (((t.getCstat() & 16) != 0) || (badguy(t) && t.getExtra() > 0) || t.getStatnum() == 10) {
                        continue;
                    }
            }

            if ((sec.getCeilingstat() & 1) != 0) {
                l = sec.getCeilingshade();
            } else {
                l = sec.getFloorshade();
            }

            if (l < -127) {
                l = -127;
            }
            t.setShade(l);
        }

        for (j = 0; j < renderedSpriteList.getSize(); j++)  //Between drawrooms() and drawmasks()
        { //is the perfect time to animate sprites
            TSprite t = renderedSpriteList.get(j);
            i = t.getOwner();
            Sprite s = boardService.getSprite(i);
            if (s == null) {
                continue;
            }

            Sector sec = boardService.getSector(s.getSectnum());
            if (sec == null) {
                continue;
            }

            switch (s.getPicnum()) {
                case SECTOREFFECTOR:
                    if (t.getLotag() == 27 && gDemoScreen.isDemoRecording()) {
                        t.setPicnum((short) (11 + ((engine.getTotalClock() >> 3) & 1)));
                        t.setCstat(t.getCstat() | 128);
                    } else {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                    }
                    break;
                case 1097:
                case 1106:
                case 1115:
                case 1168:
                case 1174:
                case 1175:
                case 1176:
                case 1178:
                case 1225:
                case 1226:
                case 1529:
                case 1530:
                case 1531:
                case 1532:
                case 1533:
                case 1534:
                case 2231:
                case 5581:
                case 5583:
                    if (ud.lockout != 0) {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                        continue;
                    }
            }

            if (t.getStatnum() == 99) {
                continue;
            }
            if (s.getStatnum() != 1 && s.getPicnum() == APLAYER && ps[s.getYvel()].newowner == -1 && s.getOwner() >= 0) {
                t.setX(t.getX() - mulscale(65536 - smoothratio, ps[s.getYvel()].posx - ps[s.getYvel()].oposx, 16));
                t.setY(t.getY() - mulscale(65536 - smoothratio, ps[s.getYvel()].posy - ps[s.getYvel()].oposy, 16));
                t.setZ(ps[s.getYvel()].oposz + mulscale(smoothratio, ps[s.getYvel()].posz - ps[s.getYvel()].oposz, 16));
                t.setZ(t.getZ() + (40 << 8));

                s.setXrepeat(24);
                s.setYrepeat(17);
            } else if ((s.getStatnum() == 0 && s.getPicnum() != 1298) || s.getStatnum() == 10 || s.getStatnum() == 6 || s.getStatnum() == 4 || s.getStatnum() == 5 || s.getStatnum() == 1 || s.getStatnum() == 116 || s.getStatnum() == 117 || s.getStatnum() == 118 || s.getStatnum() == 121) //GDX 21.04.2019 - RA airplane interpolation
            {
                // only interpolate certain moving things
                ILoc oldLoc = game.pInt.getsprinterpolate(t.getOwner());
                if (oldLoc != null) {
                    int ox = oldLoc.x;
                    int oy = oldLoc.y;
                    int oz = oldLoc.z;
                    short nAngle = oldLoc.ang;

                    // interpolate sprite position
                    ox += mulscale(t.getX() - oldLoc.x, smoothratio, 16);
                    oy += mulscale(t.getY() - oldLoc.y, smoothratio, 16);
                    oz += mulscale(t.getZ() - oldLoc.z, smoothratio, 16);
                    nAngle += (short) mulscale(((t.getAng() - oldLoc.ang + 1024) & kAngleMask) - 1024, smoothratio, 16);

                    t.setX(ox);
                    t.setY(oy);
                    t.setZ(oz);
                    t.setAng(nAngle);
                }
            }

            t1 = hittype[i].temp_data[1];
            t3 = hittype[i].temp_data[3];
            t4 = hittype[i].temp_data[4];

            switch (s.getPicnum()) {
                case BURNING: {
                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo == null) {
                        break;
                    }

                    if (spo.getStatnum() == 10) {
                        if (display_mirror == 0 && spo.getYvel() == screenpeek && over_shoulder_on == 0) {
                            t.setXrepeat(0);
                        } else {
                            t.setAng(EngineUtils.getAngle(x - t.getX(), y - t.getY()));
                            t.setX(spo.getX());
                            t.setY(spo.getY());
                            t.setX(t.getX() + (EngineUtils.cos((t.getAng()) & 2047) >> 10));
                            t.setY(t.getY() + (EngineUtils.sin(t.getAng() & 2047) >> 10));
                        }
                    }
                    break;
                }
                case 5300:
                case 5295:
                case 5290:
                    if (t.getPal() == 19) {
                        t.setShade(-127);
                    }
                case 4041:
                case 4046:
                case 4055:
                case 4235:
                case 4244:
                case 4748:
                case 4753:
                case 4758:
                case 5602:
                case 5607:
                case 5616:
                case JIBS1:
                case JIBS2:
                case JIBS3:
                case JIBS4:
                case JIBS5:
                case JIBS6:
                    //RA
                case 2460:
                case 2465:
                case 5872: //RA motowheel
                case 5877: //RA mototank
                case 5882: //RA boarddebris
                case 6112: //RA bikenbody
                case 6117: //RA bikenhead
                case 6121: //RA bikerhead2
                case 6127: //RA bikerhand
                case 7000: //RA babahead
                case 7005: //RA bababody
                case 7010: //RA babafoot
                case 7015: //RA babahand
                case 7020: //RA debris
                case 7025: //RA debbris
                case 7387: //RA deepjibs
                case 7392: //RA deepjibs2
                case 7397: //RA deepjibs3
                case 8890: //RA deep2jibs
                case 8895: //RA deep2jibs2
                    if (ud.lockout != 0) {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                        continue;
                    }
                    if (t.getPal() == 6) {
                        t.setShade(-120);
                    }
                    if (shadeEffect.getBit(s.getSectnum())) {
                        t.setShade(16);
                    }

                case SCRAP1: //464
                case SCRAP2:
                case SCRAP3:
                case SCRAP4:
                case SCRAP5:
                case SCRAP6:
                case SCRAP6 + 1:
                case SCRAP6 + 2:
                case SCRAP6 + 3:
                case SCRAP6 + 4:
                case SCRAP6 + 5:
                case SCRAP6 + 6:
                case SCRAP6 + 7:
                    if (t.getPicnum() == SCRAP1 && s.getYvel() > 0) {
                        t.setPicnum(s.getYvel());
                    } else {
                        t.setPicnum(t.getPicnum() + hittype[i].temp_data[0]);
                    }

                    if (sec.getFloorpal() != 0) {
                        t.setPal(sec.getFloorpal());
                    }
                    break;

                case CHIKENCROSSBOW:
                    k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                    k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                    if (k > 6) {
                        k = (short) (12 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }

                    t.setPicnum((short) (CHIKENCROSSBOW + k));
                    break;
                case 3464:
                    t.setPicnum((short) (((engine.getTotalClock() >> 4) & 3) + 3464));
                    break;

                case BLOODPOOL:
                case FOOTPRINTS:
                case FOOTPRINTS2:
                case FOOTPRINTS3:
                case FOOTPRINTS4:
                    if (t.getPal() == 6) {
                        t.setShade(-127);
                    }
                case FEATHERS:
                case 1311:
                    if (ud.lockout != 0 && s.getPal() == 2) {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                        continue;
                    }
                    break;
                case RESPAWNMARKERRED:
                case 876:
                case 886:
                    t.setPicnum((short) (((engine.getTotalClock() >> 4) & 0xD) + 861));
                    if (s.getPicnum() == RESPAWNMARKERRED) {
                        t.setPal(0);
                    } else if (s.getPicnum() == 876) {
                        t.setPal(1);
                    } else {
                        t.setPal(2);
                    }
                    if (ud.marker == 0) {
                        t.setXrepeat(0);
                        t.setYrepeat(0);
                    }
                    break;
                case 46:
                    t.setShade((byte) (EngineUtils.sin((engine.getTotalClock() << 4) & 2047) >> 10));
                    continue;
                case WATERBUBBLE:
                    if (sec.getFloorpicnum() == FLOORSLIME) {
                        t.setPal(7);
                        break;
                    }
                default:
                    if (sec.getFloorpal() != 0) {
                        t.setPal(sec.getFloorpal());
                    }
                    break;
                case 4989:
                    k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                    k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);

                    if (k > 6) {
                        k = (short) (12 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }

                    if (klabs(t3) > 64) {
                        k += 7;
                    }
                    t.setPicnum((short) (4989 + k));
                    break;
                case ECLAIRHEALTH:
                    t.setZ(t.getZ() - (4 << 8));
                    break;
                case CROSSBOW:
                    k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                    k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                    if (k > 6) {
                        k = (short) (12 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }
                    t.setPicnum((short) (CROSSBOW + k));
                    break;
                case SHITBALL: {
                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo == null) {
                        break;
                    }

                    if (spo.getPicnum() == 5120 && spo.getPal() == 8) {
                        t.setPicnum((short) ((engine.getTotalClock() >> 4) % 6 + 3500));
                    } else if (spo.getPicnum() == 5120 && spo.getPal() == 19) {
                        t.setPicnum((short) (((engine.getTotalClock() >> 4) & 3) + 5090));
                        t.setShade(-127);
                    } else if (spo.getPicnum() == 8705) {
                        k = (short) ((((s.getAng() + 3072 + 128 - a) & 2047) >> 8) & 7);
                        if (k > 4) {
                            k = (short) (8 - k);
                            t.setCstat(t.getCstat() | 4);
                        } else {
                            t.setCstat(t.getCstat() & ~4);
                        }

                        if (sec.getLotag() == 2) {
                            k += 1795 - 1405;
                        } else if ((hittype[i].floorz - s.getZ()) > (64 << 8)) {
                            k += 60;
                        }

                        t.setPicnum((short) (7274 + k));
                    } else {
                        t.setPicnum((short) (((engine.getTotalClock() >> 4) & 3) + SHITBALL));
                    }
                    break;
                }
                case CIRCLESAW: {
                    Sprite spo = boardService.getSprite(s.getOwner());
                    if (spo == null) {
                        break;
                    }

                    if (spo.getPicnum() != DAISYMAE && spo.getPicnum() != DAISYMAE + 1) {
                        t.setPicnum((short) (((engine.getTotalClock() >> 4) & 7) + CIRCLESAW));
                    } else {
                        t.setPicnum((short) (((engine.getTotalClock() >> 4) & 3) + 3460));
                        t.setShade(-127);
                    }

                    break;
                }
                case FORCESPHERE:
                    if (t.getStatnum() == 5) {
                        Sprite spo = boardService.getSprite(s.getOwner());
                        if (spo == null) {
                            continue;
                        }

                        int sqa = EngineUtils.getAngle(spo.getX() - ps[screenpeek].posx, spo.getY() - ps[screenpeek].posy);
                        int sqb = EngineUtils.getAngle(spo.getX() - t.getX(), spo.getY() - t.getY());

                        if (klabs(getincangle(sqa, sqb)) > 512) {
                            Sprite psp = boardService.getSprite(ps[screenpeek].i);
                            if (psp != null && ldist(spo, t) < ldist(psp, spo)) {
                                t.setXrepeat(0);
                                t.setYrepeat(0);
                            }
                        }
                    }
                    continue;
                case LNRDLYINGDEAD:
                    s.setXrepeat(24);
                    s.setYrepeat(17);
                    if (s.getExtra() > 0) {
                        t.setZ(t.getZ() + 1536);
                    }
                    break;

                case APLAYER:

                    p = s.getYvel();

                    if (t.getPal() == 1) {
                        t.setZ(t.getZ() - (18 << 8));
                    }

                    if (over_shoulder_on > 0 && ps[p].newowner < 0) {
                        t.setCstat(t.getCstat() | 2);
                        if (ps[myconnectindex] == ps[p] && numplayers >= 2) {
                            RRNetwork net = game.net;
                            int tx = net.predictOld.x + mulscale((net.predict.x - net.predictOld.x), smoothratio, 16);
                            int ty = net.predictOld.y + mulscale((net.predict.y - net.predictOld.y), smoothratio, 16);
                            int tz = net.predictOld.z + mulscale((net.predict.z - net.predictOld.z), smoothratio, 16) + (40 << 8);
                            t.update(tx, ty, tz, net.predict.sectnum);
                            t.setAng((short) (net.predictOld.ang + (BClampAngle(net.predict.ang + 1024 - net.predictOld.ang) - 1024) * smoothratio / 65536.0f));
                        }
                    }

                    if ((display_mirror == 1 || screenpeek != p || s.getOwner() == -1) && ud.multimode > 1 && ud.showweapons != 0 && ps[p].getPlayerSprite().getExtra() > 0 && ps[p].curr_weapon > 0) {
                        TSprite tsp = renderedSpriteList.obtain();
                        tsp.set(t);
                        tsp.setStatnum(99);
                        tsp.setYrepeat((short) (t.getYrepeat() >> 3));
                        if (t.getYrepeat() < 4) {
                            t.setYrepeat(4);
                        }
                        tsp.setShade(t.getShade());
                        tsp.setCstat(0);

                        switch (ps[p].curr_weapon) {
                            case 1:
                                tsp.setPicnum(21);
                                break;
                            case 2:
                                tsp.setPicnum(28);
                                break;
                            case 3:
                                tsp.setPicnum(22);
                                break;
                            case 10:
                            case 4:
                                tsp.setPicnum(26);
                                break;
                            case 5:
                                tsp.setPicnum(23);
                                break;
                            case 11:
                            case 6:
                                tsp.setPicnum(3400);
                                break;
                            case 7:
                                tsp.setPicnum(29);
                                break;
                            case 8:
                                tsp.setPicnum(27);
                                break;
                            case 9:
                                tsp.setPicnum(24);
                                break;
                            case 12:
                                tsp.setPicnum(3437);
                                break;
                        }

                        if (s.getOwner() >= 0) {
                            tsp.setZ(ps[p].posz - (12 << 8));
                        } else {
                            tsp.setZ(s.getZ() - (51 << 8));
                        }
                        if (ps[p].curr_weapon == 4) {
                            tsp.setXrepeat(10);
                            tsp.setYrepeat(10);
                        } else {
                            tsp.setXrepeat(16);
                            tsp.setYrepeat(16);
                        }
                        tsp.setPal(0);
                    }

                    if (s.getOwner() == -1) {
                        k = (short) ((((s.getAng() + 3072 + 128 - a) & 2047) >> 8) & 7);
                        if (k > 4) {
                            k = (short) (8 - k);
                            t.setCstat(t.getCstat() | 4);
                        } else {
                            t.setCstat(t.getCstat() & ~4);
                        }

                        if (sec.getLotag() == 2) {
                            k += 1795 - 1405;
                        } else if ((hittype[i].floorz - s.getZ()) > (64 << 8)) {
                            k += 60;
                        }

                        t.setPicnum(t.getPicnum() + k);
                        t.setPal(ps[p].palookup);

                        if (sec.getFloorpal() != 0) {
                            t.setPal(sec.getFloorpal());
                        }

                        continue;
                    }

                    if (ps[p].on_crane == -1 && (sec.getLotag() & 0x7ff) != 1) {
                        l = s.getZ() - hittype[ps[p].i].floorz + (3 << 8);
                        if (l > 1024 && s.getYrepeat() > 32 && s.getExtra() > 0) {
                            t.setYoffset((short) (l / (t.getYrepeat() << 2))); //GDX 24.10.2018 multiplayer unsync
                        } else {
                            t.setYoffset(0);
                        }
                    }

                    if (ps[p].newowner > -1) {
                        t4 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER] + 1];
                        t3 = 0;
                        t1 = currentGame.getCON().script[currentGame.getCON().actorscrptr[APLAYER] + 2];
                    }

                    if (ud.camerasprite == -1 && ps[p].newowner == -1) {
                        if (s.getOwner() >= 0 && display_mirror == 0 && over_shoulder_on == 0) {
                            if (ud.multimode < 2 || p == screenpeek) {
                                t.setOwner(-1);
                                t.setXrepeat(0);
                                t.setYrepeat(0);
                                continue;
                            }
                        }
                    }

                    if (sec.getFloorpal() != 0) {
                        t.setPal(sec.getFloorpal());
                    }

                    if (s.getOwner() == -1) {
                        continue;
                    }

                    if (t.getZ() > hittype[i].floorz && t.getXrepeat() < 32) {
                        t.setZ(hittype[i].floorz);
                    }

                /*if ( ps[p].OnMotorcycle && p == screenpeek )
                {
                	t.picnum = 7219;
                	t.xrepeat = 18;
                	t.yrepeat = 18;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }
                else */
                    if (ps[p].OnMotorcycle) {
                        k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                        k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                        if (k > 6) {
                            k = (short) (12 - k);
                            t.setCstat(t.getCstat() | 4);
                        } else {
                            t.setCstat(t.getCstat() & ~4);
                        }

                        t.setPicnum((short) (7213 + k));
                        t.setXrepeat(18);
                        t.setYrepeat(18);
                        t4 = 0;
                        t3 = 0;
                        t1 = 0;
                    }
                /*else if ( ps[p].OnBoat && p == screenpeek )
                {
                	t.picnum = 7190;
                	t.xrepeat = 32;
                	t.yrepeat = 32;
                	t4 = 0;
                	t3 = 0;
                	t1 = 0;
                }
                else */
                    if (ps[p].OnBoat) {
                        k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                        k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                        if (k > 6) {
                            k = (short) (12 - k);
                            t.setCstat(t.getCstat() | 4);
                        } else {
                            t.setCstat(t.getCstat() & ~4);
                        }

                        t.setPicnum((short) (7184 + k));
                        t.setXrepeat(32);
                        t.setYrepeat(32);
                        t4 = 0;
                        t3 = 0;
                        t1 = 0;
                    }

                    int tx = t.getX() - x;
                    int ty = t.getY() - y;
                    int angle = ((1024 + EngineUtils.getAngle(tx, ty) - a) & kAngleMask) - 1024;
                    long dist = EngineUtils.qdist(tx, ty);

                    if (klabs(mulscale(angle, dist, 14)) < 4) {
                        int horizoff = (int) (100 - ps[screenpeek].horiz);
                        long z1 = mulscale(dist, horizoff, 3) + z;

                        int zTop = t.getZ();
                        int zBot = zTop;
                        ArtEntry pic = engine.getTile(APLAYER);

                        int yoffs = pic.getOffsetY();
                        zTop -= (yoffs + pic.getHeight()) * (t.getYrepeat() << 2);
                        zBot += -yoffs * (t.getYrepeat() << 2);

                        if ((z1 < zBot) && (z1 > zTop)) {
                            Sprite psp = boardService.getSprite(ps[screenpeek].i);
                            if (psp != null && engine.cansee(x, y, z, psp.getSectnum(), t.getX(), t.getY(), t.getZ(), t.getSectnum())) {
                                gPlayerIndex = t.getYvel();
                            }
                        }
                    }

                    break;
                case 27:
                    continue;
                case MOTORCYCLE:
                    k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                    k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                    if (k > 6) {
                        k = (short) (12 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }

                    t.setPicnum((short) (MOTORCYCLE + k));
                    break;
                case SWAMPBUGGY:
                    k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                    k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                    if (k > 6) {
                        k = (short) (12 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }
                    t.setPicnum((short) (SWAMPBUGGY + k));
                    break;
            }

            if (currentGame.getCON().actorscrptr[s.getPicnum()] != 0 && (t.getCstat() & 0x30) != 48) {
                if (t4 != 0) {
                    l = currentGame.getCON().script[t4 + 2];
                    switch (l) {
                        case 2:
                            k = (short) ((((s.getAng() + 3072 + 128 - a) & 2047) >> 8) & 1);
                            break;

                        case 3:
                        case 4:
                            k = (short) ((((s.getAng() + 3072 + 128 - a) & 2047) >> 7) & 7);
                            if (k > 3) {
                                t.setCstat(t.getCstat() | 4);
                                k = (short) (7 - k);
                            } else {
                                t.setCstat(t.getCstat() & ~4);
                            }
                            break;

                        case 5:
                            k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                            k = (short) ((((s.getAng() + 3072 + 128 - k) & 2047) >> 8) & 7);
                            if (k > 4) {
                                k = (short) (8 - k);
                                t.setCstat(t.getCstat() | 4);
                            } else {
                                t.setCstat(t.getCstat() & ~4);
                            }
                            break;
                        case 7:
                            k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                            k = (short) (((s.getAng() + 3072 + 128 - k) & 2047) / 170);
                            if (k > 6) {
                                k = (short) (12 - k);
                                t.setCstat(t.getCstat() | 4);
                            } else {
                                t.setCstat(t.getCstat() & ~4);
                            }
                            break;
                        case 8:
                            k = (short) ((((s.getAng() + 3072 + 128 - a) & 2047) >> 8) & 7);
                            t.setCstat(t.getCstat() & ~4);
                            break;
                        default:
                            if (badguy(s) && s.getStatnum() == 2 && s.getExtra() > 0) {
                                k = EngineUtils.getAngle(s.getX() - x, s.getY() - y);
                                k = (short) ((((s.getAng() + 3072 + 128 - k) & 2047) >> 8) & 7);
                                if (k > 4) {
                                    k = (short) (8 - k);
                                    t.setCstat(t.getCstat() | 4);
                                } else {
                                    t.setCstat(t.getCstat() & ~4);
                                }
                            } else {
                                k = 0;
                            }
                            break;
                    }

                    t.setPicnum(t.getPicnum() + (k + (currentGame.getCON().script[t4]) + l * t3));

                    if (l > 0) {
                        while (t.getPicnum() > 0 && t.getPicnum() < MAXTILES && engine.getTile(t.getPicnum()).getWidth() == 0) {
                            t.setPicnum(t.getPicnum() - l);       //Hack, for actors
                        }
                    }

                    if (hittype[i].dispicnum >= 0) {
                        hittype[i].dispicnum = t.getPicnum();
                    }
                } else if (display_mirror == 1) {
                    t.setCstat(t.getCstat() | 4);
                }
            }
            if (s.getPicnum() == 5015) {
                t.setShade(-127);
            }

            if (s.getStatnum() == 13 || badguy(s) || (s.getPicnum() == APLAYER && s.getOwner() >= 0)) {
                if ((s.getCstat() & 0x30) == 0 && t.getStatnum() != 99 && s.getPicnum() != EXPLOSION2 && s.getPicnum() != 1080 && s.getPicnum() != TORNADO && s.getPicnum() != EXPLOSION3 && s.getPicnum() != 5015) {
                    if (hittype[i].dispicnum < 0) {
                        hittype[i].dispicnum++;
                        continue;
                    } else if (ud.shadows != 0 && renderedSpriteList.getSize() < (MAXSPRITESONSCREEN - 2)) {
                        int daz, xrep, yrep;
                        if (sec.getLotag() == 160) {
                            continue;
                        }

                        if ((sec.getLotag() & 0xff) > 2 || s.getStatnum() == 4 || s.getStatnum() == 5 || s.getPicnum() == MOSQUITO) {
                            daz = sec.getFloorz();
                        } else {
                            daz = hittype[i].floorz;
                        }

                        if ((s.getZ() - daz) < (8 << 8)) {
                            if (ps[screenpeek].posz < daz) {
                                Sprite tspr = renderedSpriteList.obtain();
                                tspr.set(t);
                                int camangle = EngineUtils.getAngle(x - tspr.getX(), y - tspr.getY());
                                tspr.setX(tspr.getX() - mulscale(EngineUtils.sin((camangle + 512) & 2047), 10, 16));
                                tspr.setY(tspr.getY() + mulscale(EngineUtils.sin((camangle + 1024) & 2047), 10, 16));
                                tspr.setStatnum(99);

                                tspr.setYrepeat((short) (t.getYrepeat() >> 3));
                                if (t.getYrepeat() < 4) {
                                    t.setYrepeat(4);
                                }

                                tspr.setShade(127);
                                tspr.setCstat(tspr.getCstat() | 2);

                                tspr.setZ(daz);
                                xrep = tspr.getXrepeat();
                                tspr.setXrepeat((short) xrep);
                                tspr.setPal(4);

                                yrep = tspr.getYrepeat();
                                tspr.setYrepeat((short) yrep);
                            }
                        }
                    }
                }
            }

            Sprite spo = boardService.getSprite(s.getOwner());

            switch (s.getPicnum()) {
                case 2944:
                    t.setShade(-127);
                    t.setPicnum((short) (((engine.getTotalClock() >> 2) & 4) + 2944));
                    break;
                case MUD:
                    t.setPicnum((short) (t1 + MUD));
                    break;
                case FRAMEEFFECT1: {
                    if (spo != null && spo.getStatnum() < MAXSTATUS) {
                        if (spo.getPicnum() == APLAYER) {
                            if (ud.camerasprite == -1) {
                                if (screenpeek == spo.getYvel() && display_mirror == 0) {
                                    t.setOwner(-1);
                                    break;
                                }
                            }
                        }
                        if ((spo.getCstat() & 32768) == 0) {
                            if (spo.getPicnum() == APLAYER) {
                                t.setPicnum(1554);
                            } else {
                                t.setPicnum((short) hittype[s.getOwner()].dispicnum);
                            }
                            t.setPal(spo.getPal());
                            t.setShade(spo.getShade());
                            t.setAng(spo.getAng());
                            t.setCstat((short) (2 | spo.getCstat()));
                        }
                    }
                    break;
                }
                case PLAYERONWATER:
                    k = (short) ((((t.getAng() + 3072 + 128 - a) & 2047) >> 8) & 7);
                    if (k > 4) {
                        k = (short) (8 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }

                    t.setPicnum((short) (s.getPicnum() + k + ((hittype[i].temp_data[0] < 4) ? 5 : 0)));
                    if (spo != null) {
                        t.setShade(spo.getShade());
                    }

                    break;
                case 1409:
                case EXPLOSION2:
                case 1442:
                case CROSSBOW:
                case 2095:
                case 3380:
                case CIRCLESAW:
                case FIRELASER:
                case 3471:
                case 3475:
                case 5595:
                    if (t.getPicnum() == EXPLOSION2) {
                        gVisibility = -127;
                        lastvisinc = engine.getTotalClock() + 32;
                        t.setPal(0);
                    } else if (t.getPicnum() == FIRELASER) {
                        t.setPicnum((short) (((engine.getTotalClock() >> 2) & 5) + FIRELASER));
                    }

                    t.setShade(-127);
                    break;
                case 1878:
                case 1952:
                case 1953:
                case 1990:
                case 2050:
                case 2056:
                case 2072:
                case 2075:
                case 2083:
                case 2097:
                case 2156:
                case 2157:
                case 2158:
                case 2159:
                case 2160:
                case 2161:
                case 2175:
                case 2176:
                case 2357:
                case 2564:
                case 2573:
                case 2574:
                case 2583:
                case 2604:
                case 2689:
                case 2893:
                case 2894:
                case 2915:
                case 2945:
                case 2946:
                case 2947:
                case 2948:
                case 2949:
                case 2977:
                case 2978:
                case 3116:
                case 3171:
                case 3216:
                case 3720:

                    //RA
                case 3668:
                case 3795:
                case 5035:
                case 7505:
                case 7506:
                case 7533:
                case 8216:
                case 8218:
                case 8220:
                    t.setShade(-127);
                    break;
                case UFOBEAM:
                case 297: //GDX 23.04.2019 UFO TELE B
                case 3586:
                case 3587:
                    t.setCstat(t.getCstat() | 32768);
                    s.setCstat(s.getCstat() | 32768);
                    break;
                case 36:
                    t.setCstat(t.getCstat() | 32768);
                    break;
                case 1107:
                    t.setPicnum((short) (s.getPicnum() + hittype[i].temp_data[2]));
                    break;
                case CAMERA1:
                case RAT:
                    k = (short) ((((t.getAng() + 3072 + 128 - a) & 2047) >> 8) & 7);
                    if (k > 4) {
                        k = (short) (8 - k);
                        t.setCstat(t.getCstat() | 4);
                    } else {
                        t.setCstat(t.getCstat() & ~4);
                    }
                    t.setPicnum((short) (s.getPicnum() + k));
                    break;
                case 2034:
                    t.setPicnum((short) ((engine.getTotalClock() & 1) + 2034));
                    break;
                case WATERSPLASH2:
                    t.setPicnum((short) (WATERSPLASH2 + t1));
                    break;
                case BURNING:
                case FIRE:
                    if (spo != null && spo.getPicnum() != 1191 && spo.getPicnum() != 1193) {
                        t.setZ(sec.getFloorz());
                    }
                    t.setShade(-127);
                    break;
                case SHELL:
                case SHOTGUNSHELL:
                    t.setPicnum((short) (s.getPicnum() + (hittype[i].temp_data[0] & 1)));
                    break;

                //RA
                case BILLYRAY:
                case BILLYRAYSTAYPUT:
                    if (t.getPicnum() >= 4167 && t.getPicnum() <= 4171) {
                        t.setShade(-127);
                    }
                    break;
                case MINION:
                    if (t.getPal() == 19) {
                        t.setShade(-127);
                    }
                    break;
                case BIKERSTAND:
                    if (t.getPicnum() >= 6049 && t.getPicnum() <= 6053) {
                        t.setShade(-127);
                    } else if (t.getPicnum() >= 6079 && t.getPicnum() <= 6083) {
                        t.setShade(-127);
                    }
                    break;
                case DAISYMAE:
                    if (t.getPicnum() >= 6760 && t.getPicnum() <= 6809) {
                        t.setShade(-127);
                    }
                    break;
            }

            hittype[i].dispicnum = t.getPicnum();
            if (sec.getFloorpicnum() == MIRROR) {
                t.setXrepeat(0);
                t.setYrepeat(0);
            }
        }
    }

    public static void se40code(int x, int y, int z, float a, float h, int smoothratio) {
        for (ListNode<Sprite> node = boardService.getStatNode(15); node != null; node = node.getNext()) {
            switch (node.get().getLotag()) {
                case 150: //floor
                case 151: //ceiling
                    SE40_Draw(x, y, z, a, h, smoothratio);
                    break;
            }
        }
    }

    private static void SE40_Old(final int spnum, int x, int y, int z, float a, float h, int smoothratio) {
        int fofmode = 0;
        long offx, offy;

        Sprite s = boardService.getSprite(spnum);
        if (s == null) {
            return;
        }

        if (s.getAng() != 512) {
            return;
        }

        RRRenderer renderer = game.getRenderer();
        byte[] gotpic = renderer.getRenderedPics();
        int tile = 13;    //Effect TILE
        if ((gotpic[tile >> 3] & (1 << (tile & 7))) == 0) {
            return;
        }
        gotpic[tile >> 3] &= (byte) ~(1 << (tile & 7));

        if (s.getLotag() == 152) {
            fofmode = 150;
        }

        if (s.getLotag() == 153) {
            fofmode = 151;
        }

        Sprite floor1 = s;
        for (ListNode<Sprite> node = boardService.getStatNode(15); node != null; node = node.getNext()) {
            Sprite sp = node.get();
            if (sp.getPicnum() == 1 && sp.getLotag() == fofmode && sp.getHitag() == floor1.getHitag()) {
                floor1 = sp;
                fofmode = sp.getLotag();
                break;
            }
        }

        int k;
        if (fofmode == 150) {
            k = 151;
        } else {
            k = 150;
        }

        Sprite floor2 = null;
        for (ListNode<Sprite> node = boardService.getStatNode(15); node != null; node = node.getNext()) {
            Sprite sp = node.get();
            if (sp.getPicnum() == 1 && sp.getLotag() == k && sp.getHitag() == floor1.getHitag()) {
                floor2 = sp;
                break;
            }
        }

        if (floor2 == null) {
            return;
        }

        Sector sec1 = boardService.getSector(floor1.getSectnum());
        Sector sec2 = boardService.getSector(floor2.getSectnum());
        if (sec1 == null || sec2 == null) {
            return;
        }

        for (ListNode<Sprite> node = boardService.getStatNode(15); node != null; node = node.getNext())  // raise ceiling or floor
        {
            Sprite sp = node.get();
            Sector sec = boardService.getSector(sp.getSectnum());

            if (sp.getPicnum() == 1 && sp.getLotag() == k + 2 && sp.getHitag() == floor1.getHitag()) {
                if (sec != null && k == 150) {
                    tempsectorz[sp.getSectnum()] = sec.getFloorz();
                    sec.setFloorz(sec.getFloorz() + (((z - sec.getFloorz()) / 32768) + 1) * 32768);
                    tempsectorpicnum[sp.getSectnum()] = sec.getFloorpicnum();
                    sec.setFloorpicnum(13);
                }
                if (sec != null && k == 151) {
                    tempsectorz[sp.getSectnum()] = sec.getCeilingz();
                    sec.setCeilingz(sec.getCeilingz() + (((z - sec.getCeilingz()) / 32768) - 1) * 32768);
                    tempsectorpicnum[sp.getSectnum()] = sec.getCeilingpicnum();
                    sec.setCeilingpicnum(13);
                }
            }
        }

        offx = x - floor1.getX();
        offy = y - floor1.getY();

        renderer.drawrooms(offx + floor2.getX(), offy + floor2.getY(), z, a, h, floor2.getSectnum());
        animatesprites(x, y, z, (short) a, smoothratio);
        renderer.drawmasks();

        for (ListNode<Sprite> node = boardService.getStatNode(15); node != null; node = node.getNext())  // restore ceiling or floor
        {
            Sprite sp = node.get();
            Sector sec = boardService.getSector(sp.getSectnum());

            if (sp.getPicnum() == 1 && sp.getLotag() == k + 2 && sp.getHitag() == floor1.getHitag()) {
                if (sec != null && k == 150) {
                    sec.setFloorz(tempsectorz[sp.getSectnum()]);
                    sec.setFloorpicnum(tempsectorpicnum[sp.getSectnum()]);
                }
                if (sec != null && k == 151) {
                    sec.setCeilingz(tempsectorz[sp.getSectnum()]);
                    sec.setCeilingpicnum(tempsectorpicnum[sp.getSectnum()]);
                }
            }// end if
        }// end for
    } // end SE40

    public static void SE40_Draw(int x, int y, int z, float a, float h, int smoothratio) {
        ListNode<Sprite> node = boardService.getStatNode(15);
        while (node != null) {
            Sprite sp = node.get();
            switch (sp.getLotag()) {
                case 152:
                case 153:
                    if (ps[screenpeek].cursectnum == sp.getSectnum()) {
                        SE40_Old(node.getIndex(), x, y, z, a, h, smoothratio);
                    }
                    break;
            }
            node = node.getNext();
        }
    }

    public static void addmessage(String message) {
        buildString(currentGame.getCON().fta_quotes[122], 0, message);
        FTA(122, ps[myconnectindex]);
    }

    public static void viewDrawStats(int x, int y, int zoom, boolean topAligned) {
        Renderer renderer = game.getRenderer();
        if (cfg.gShowStat == 0) {
            return;
        }

        float viewzoom = (zoom / 65536.0f);

        buildString(buffer, 0, "kills:   ");
        int alignx = game.getFont(1).getWidth(buffer, viewzoom);

        if (!topAligned) {
            int yoffset = (int) (3f * game.getFont(1).getSize() * viewzoom);
            y -= yoffset;
        }

        int staty = y;

        game.getFont(1).drawTextScaled(renderer, x, staty, buffer, viewzoom, 0, 2, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);

        int offs = Bitoa(ps[connecthead].actors_killed, buffer);
        buildString(buffer, offs, " /   ", ps[connecthead].max_actors_killed);
        game.getFont(1).drawTextScaled(renderer, x + (alignx + 6), staty, buffer, viewzoom, 0, 15, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);

        staty = y + (int) (12 * viewzoom);

        buildString(buffer, 0, "secrets:    ");
        game.getFont(1).drawTextScaled(renderer, x, staty, buffer, viewzoom, 0, 2, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);
        alignx = game.getFont(1).getWidth(buffer, viewzoom);
        offs = Bitoa(ps[connecthead].secret_rooms, buffer);
        buildString(buffer, offs, " /   ", ps[connecthead].max_secret_rooms);
        game.getFont(1).drawTextScaled(renderer, x + (alignx + 6), staty, buffer, viewzoom, 0, 15, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);

        staty = y + (int) (22 * viewzoom);

        buildString(buffer, 0, "time:    ");
        game.getFont(1).drawTextScaled(renderer, x, staty, buffer, viewzoom, 0, 2, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);
        alignx = game.getFont(1).getWidth(buffer, viewzoom);

        int minutes = ps[myconnectindex].player_par / (26 * 60);
        int sec = (ps[myconnectindex].player_par / 26) % 60;

        offs = Bitoa(minutes, buffer, 2);
        buildString(buffer, offs, " :   ", sec, 2);

        game.getFont(1).drawTextScaled(renderer, x + (alignx + 6), staty, buffer, viewzoom, 0, 15, TextAlign.Left, Transparent.None, ConvertType.AlignLeft, false);
    }

    public static void view(PlayerStruct pp, int vx, int vy, int vz, int vsectnum, float ang, float horiz) {
        viewout.ox = vx;
        viewout.oy = vy;
        viewout.oz = vz;
        viewout.os = vsectnum;

        int nx = (int) (-BCosAngle(ang) / 16.0f);
        int ny = (int) (-BSinAngle(ang) / 16.0f);
        int nz = (int) ((horiz - 100) * 128 - 4096);

        Sprite sp = boardService.getSprite(pp.i);
        if (sp == null) {
            return;
        }

        short bakcstat = sp.getCstat();
        sp.setCstat(sp.getCstat() & ~0x101);

        vsectnum = engine.updatesectorz(vx, vy, vz, vsectnum);

        engine.hitscan(vx, vy, vz, vsectnum, nx, ny, nz, pHitInfo, CLIPMASK1);
        int hitx = pHitInfo.hitx, hity = pHitInfo.hity;

        if (vsectnum < 0) {
            sp.setCstat(bakcstat);
            return;
        }

        int hx = hitx - (vx);
        int hy = hity - (vy);
        if ((klabs(hx) + klabs(hy)) - (klabs(nx) + klabs(ny)) < 1024) {
            int wx = 1;
            if (nx < 0) {
                wx = -1;
            }
            int wy = 1;
            if (ny < 0) {
                wy = -1;
            }

            hx -= wx << 9;
            hy -= wy << 9;

            int dist = 0;
            if (nx != 0 && ny != 0) {
                if (klabs(nx) > klabs(ny)) {
                    dist = divscale(hx, nx, 16);
                } else {
                    dist = divscale(hy, ny, 16);
                }
            }

            if (dist < cameradist) {
                cameradist = dist;
            }
        }

        vx += mulscale(nx, cameradist, 16);
        vy += mulscale(ny, cameradist, 16);
        vz += mulscale(nz, cameradist, 16);

        cameradist = min(cameradist + ((engine.getTotalClock() - cameraclock) << 10), 65536);
        cameraclock = engine.getTotalClock();

        vsectnum = engine.updatesectorz(vx, vy, vz, vsectnum);

        sp.setCstat(bakcstat);

        viewout.ox = vx;
        viewout.oy = vy;
        viewout.oz = vz;
        viewout.os = vsectnum;
    }

    public static void coords(short snum) {
        short y = 0;

        Renderer renderer = game.getRenderer();
        if (ud.coop != 1) {
            if (ud.multimode > 1 && ud.multimode < 5) {
                y = 8;
            } else if (ud.multimode > 4) {
                y = 16;
            }
        }

        BitmapFont smallFont = EngineUtils.getSmallFont();
        buildString(buffer, 0, "X= ", ps[snum].posx);
        smallFont.drawText(renderer, 250, y, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "Y= ", ps[snum].posy);
        smallFont.drawText(renderer, 250, y + 7, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "Z= ", ps[snum].posz);
        smallFont.drawText(renderer, 250, y + 14, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "A= ", (int) ps[snum].ang);
        smallFont.drawText(renderer, 250, y + 21, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "ZV= ", ps[snum].poszv);
        smallFont.drawText(renderer, 250, y + 28, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "OG= ", ps[snum].on_ground ? 1 : 0);
        smallFont.drawText(renderer, 250, y + 35, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "LFW= ", ps[snum].last_full_weapon);
        smallFont.drawText(renderer, 250, y + 50, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        Sector sec = boardService.getSector(ps[snum].cursectnum);
        buildString(buffer, 0, "SECTL= ", sec != null ? sec.getLotag() : 0);
        smallFont.drawText(renderer, 250, y + 57, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "SEED= ", engine.getrand());
        smallFont.drawText(renderer, 250, y + 64, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
        buildString(buffer, 0, "THOLD= ", ps[snum].transporter_hold);
        smallFont.drawText(renderer, 250, y + 71, buffer, 1.0f, 0, 31, TextAlign.Left, Transparent.None, false);
    }

    public static void displaygeom3d(int sectnum, int cposx, int cposy, int cposz, float choriz, float cang, int csect, int smoothratio) {
        RRRenderer renderer = game.getRenderer();
        Sector sec = boardService.getSector(sectnum);
        if (sec != null && sec.getLotag() == 848) {
            int geomsect = 0;

            for (int i = 0; i < numgeomeffects; i++) {
                ListNode<Sprite> node = boardService.getSectNode(geomsector[i]);
                while (node != null) {
                    ListNode<Sprite> nextk = node.getNext();
                    int k = node.getIndex();
                    Sprite sp = node.get();
                    engine.changespritesect(k, geoms1[i]);
                    engine.setsprite(k, sp.getX() + geomx1[i], sp.getY() + geomy1[i], sp.getZ());
                    node = nextk;
                }
                if (csect == geomsector[i]) {
                    geomsect = i;
                }
            }

            renderer.drawrooms(cposx - geomx1[geomsect], cposy - geomy1[geomsect], cposz, cang, choriz, geomsect);

            for (short i = 0; i < numgeomeffects; i++) {
                ListNode<Sprite> node = boardService.getSectNode(geoms1[i]);
                while (node != null) {
                    ListNode<Sprite> nextk = node.getNext();
                    int k = node.getIndex();
                    Sprite sp = node.get();
                    engine.changespritesect(k, geomsector[i]);
                    engine.setsprite(k, sp.getX() - geomx1[i], sp.getY() - geomy1[i], sp.getZ());
                    node = nextk;
                }
            }

            animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
            renderer.drawmasks();

            for (short i = 0; i < numgeomeffects; i++) {
                ListNode<Sprite> node = boardService.getSectNode(geomsector[i]);
                while (node != null) {
                    ListNode<Sprite> nextk = node.getNext();
                    int k = node.getIndex();
                    Sprite sp = node.get();
                    engine.changespritesect(k, geoms2[i]);
                    engine.setsprite(k, sp.getX() + geomx2[i], sp.getY() + geomy2[i], sp.getZ());
                    node = nextk;
                }
                if (csect == geomsector[i]) {
                    geomsect = i;
                }
            }

            renderer.drawrooms(cposx - geomx2[geomsect], cposy - geomy2[geomsect], cposz, cang, choriz, geomsect);

            for (short i = 0; i < numgeomeffects; i++) {
                ListNode<Sprite> node = boardService.getSectNode(geoms2[i]);
                while (node != null) {
                    ListNode<Sprite> nextk = node.getNext();
                    int k = node.getIndex();
                    Sprite sp = node.get();
                    engine.changespritesect(k, geomsector[i]);
                    engine.setsprite(k, sp.getX() - geomx2[i], sp.getY() - geomy2[i], sp.getZ());
                    node = nextk;
                }
            }

            animatesprites(cposx, cposy, cposz, (short) cang, smoothratio);
            renderer.drawmasks();
        }
    }

    public static void displayinventory(PlayerStruct p) {
        RRRenderer renderer = game.getRenderer();

        int n, j, xoff, y;

        j = 0;

        n = (p.cowpie_amount > 0) ? 1 << 3 : 0;
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

        xoff = 160 - (j * 11);

        j = 0;

        y = 134;

        if ((p.gotkey[0] | p.gotkey[1] | p.gotkey[2]) != 0) {
            xoff += renderer.getTile(KEYSIGN).getWidth() / 4;
        }

        int windowx1 = 0;
        int windowy1 = 0;
        int windowx2 = renderer.getWidth();
        int windowy2 = renderer.getHeight();

        while (j <= 9) {
            if ((n & (1 << j)) != 0) {
                switch (n & (1 << j)) {
                    case 1:
                        renderer.rotatesprite((xoff + 4) << 16, y << 16, 32768, 0, WHISHKEY_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 2:
                        renderer.rotatesprite((xoff + 4) << 16, y << 16, 32768, 0, MOONSHINE_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 4:
                        renderer.rotatesprite((xoff) << 16, (y + 2) << 16, 32768, 0, BEER_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 8:
                        renderer.rotatesprite((xoff - 2) << 16, (y + 5) << 16, 32768, 0, COWPIE_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 16:
                        renderer.rotatesprite((xoff - 2) << 16, y << 16, 32768, 0, EMPTY_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 32:
                        renderer.rotatesprite((xoff) << 16, y << 16, 32768, 0, SNORKLE_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                    case 64:
                        renderer.rotatesprite((xoff + 4) << 16, (y - 1) << 16, 32768, 0, BOOT_ICON, 0, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                        break;
                }

                xoff += 22;

                if (p.inven_icon == j + 1) {
                    renderer.rotatesprite((xoff) << 16, (y + 20) << 16, 32768, 1024, ARROW, -32, 0, 10 + 16, windowx1, windowy1, windowx2, windowy2);
                }
            }

            j++;
        }
    }

    public static void displaymasks(short snum) {
        RRRenderer renderer = game.getRenderer();
        Sector psec = boardService.getSector(ps[snum].cursectnum);
        Sprite psp = boardService.getSprite(ps[snum].i);
        if (psp == null || psec == null) {
            return;
        }

        int windowx1 = 0;
        int windowy1 = 0;
        int windowx2 = renderer.getWidth();
        int windowy2 = renderer.getHeight();

        int p = psec.getFloorpal();
        if (psp.getPal() == 1) {
            p = 1;
        }

        if (ps[snum].scuba_on != 0) {
            ArtEntry pic = renderer.getTile(3374);
            renderer.rotatesprite((320 - (pic.getWidth() >> 1) - 15) << 16, (200 - (pic.getHeight() >> 1) + (EngineUtils.sin(engine.getTotalClock() & 0x7FF) >> 10)) << 16, 49152, 0, 3374, 0, p, 10 | 16 | 512, windowx1, windowy1, windowx2, windowy2);

            int xdim = renderer.getWidth();
            pic = renderer.getTile(3377);
            int framesx = xdim / pic.getWidth();

            int x = -pic.getWidth() / 2;
            for (int i = 0; i <= framesx; i++) {
                renderer.rotatesprite(x << 16, (-1) << 16, 65536, 0, 3377, 0, p, 10 | 16);
                renderer.rotatesprite(x << 16, 200 << 16, 65536, 0, 3377, 0, p, 4 | 10 | 16);
                x += pic.getWidth() - 1;
            }

            pic = renderer.getTile(3378);
            renderer.rotatesprite((320 - (pic.getWidth())) << 16, (200 - (pic.getHeight())) << 16, 65536, 0, 3378, 0, p, 10 | 16 | 512, windowx1, windowy1, windowx2, windowy2);
            renderer.rotatesprite(pic.getWidth() << 16, (200 - (pic.getHeight())) << 16, 65536, 1024, 3378, 0, p, 4 | 10 | 16 | 256, windowx1, windowy1, windowx2, windowy2);

        }
    }
}
