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

package ru.m210projects.Redneck.Screens;


import com.badlogic.gdx.Gdx;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Redneck.Main;

import java.util.ArrayList;
import java.util.List;

import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.INGAMELNRDTHREEDEE;
import static ru.m210projects.Redneck.Names.MENUSCREEN;

public class DisconnectScreen extends StatisticScreen {

    public final List<Integer> playerList;

    public DisconnectScreen(Main app) {
        super(app);
        playerList = new ArrayList<>();
    }

    public void updateList() {
        playerList.clear();
        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            playerList.add(i);
        }
    }

    @Override
    public void hide() {
        updateList();
    }

    @Override
    public void render(float delta) {
        game.getRenderer().clearview(0);

        if (numplayers > 1) {
            game.pNet.GetPackets();
        }

        dobonus(true);

        engine.nextpage(delta);
    }

    @Override
    public void onSkip() {
        Gdx.app.postRunnable(() -> game.show());
    }

    @Override
    public void dobonus(boolean disconnect) {
        int i, y, xfragtotal, yfragtotal;

        Renderer renderer = game.getRenderer();
        renderer.rotatesprite(0, 0, 65536, 0, MENUSCREEN, 16, 0, 2 + 8 + 16 + 64);
        renderer.rotatesprite(160 << 16, 34 << 16, 32768, 0, INGAMELNRDTHREEDEE, 0, 0, 10);

        app.getFont(1).drawTextScaled(renderer, 160, 58 + 5, "MULTIPLAYER TOTALS", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        app.getFont(1).drawTextScaled(renderer, 160, 58 + 15, currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number), 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);

        int pos = 90;
        int t = 0;
        app.getFont(0).drawTextScaled(renderer, 23, pos, "   NAME                                           KILLS", 1.0f, 0, 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
        if (ud.coop != 1) {
            for (int num = 0; num < playerList.size(); num++) {
                Bitoa(playerList.get(num) + 1, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 92 + (num * 23), pos, bonusbuf, 1.0f, 0, 3, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }

            for (int num = 0; num < playerList.size(); num++) {
                xfragtotal = 0;
                i = playerList.get(num);
                Bitoa(i + 1, bonusbuf);

                app.getFont(0).drawTextScaled(renderer, 30, pos + 10 + t, bonusbuf, 1.0f, 0, 3, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(0).drawTextScaled(renderer, 38, pos + 10 + t, ud.user_name[i], 1.0f, 0, ps[i].palookup, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                for (Integer integer : playerList) {
                    y = integer;
                    if (i == y) {
                        Bitoa(ps[y].fraggedself, bonusbuf);
                        app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), pos + 10 + t, bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                        xfragtotal -= ps[y].fraggedself;
                    } else {
                        Bitoa(frags[i][y], bonusbuf);
                        app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), pos + 10 + t, bonusbuf, 1.0f, 0, 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                        xfragtotal += frags[i][y];
                    }
                }

                Bitoa(xfragtotal, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 101 + (8 * 23), pos + 10 + t, bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                t += 7;
            }

            for (int num1 = 0; num1 < playerList.size(); num1++) {
                y = playerList.get(num1);
                yfragtotal = 0;
                for (Integer integer : playerList) {
                    i = integer;
                    if (i == y) {
                        yfragtotal += ps[i].fraggedself;
                    }
                    yfragtotal += frags[i][y];
                }
                Bitoa(yfragtotal, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), pos + 16 + (8 * 7), bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }

            app.getFont(0).drawTextScaled(renderer, 45, pos + 16 + (8 * 7), "DEATHS", 1.0f, 0, 8, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
        } else {
            pos += 10;
            for (Integer integer : playerList) {
                i = integer;
                Bitoa(i + 1, bonusbuf);

                app.getFont(0).drawTextScaled(renderer, 30, pos + t, bonusbuf, 1.0f, 0, 3, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(0).drawTextScaled(renderer, 38, pos + t, ud.user_name[i], 1.0f, 0, ps[i].palookup, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                Bitoa(ps[i].frag, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 101 + (8 * 23), pos + t, bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                t += 7;
            }
        }
        app.getFont(1).drawTextScaled(renderer, 160, 175, "PRESS ANY KEY TO CONTINUE", 1.0f, 0, 2, TextAlign.Center, Transparent.None, ConvertType.Normal, false);

        if (engine.getTotalClock() > (60 * 2)) {
            onSkip();
        }
    }

    @Override
    public void anyKeyPressed() {
        onSkip();
    }
}
