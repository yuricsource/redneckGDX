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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import ru.m210projects.Build.Architecture.common.audio.Source;

import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.input.GameKey;
import ru.m210projects.Build.input.GameProcessor;
import ru.m210projects.Build.input.InputListener;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.MapInfo;

import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Actors.isPsychoSkill;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.INGAMELNRDTHREEDEE;
import static ru.m210projects.Redneck.Names.MENUSCREEN;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;

public class StatisticScreen extends ScreenAdapter implements InputListener {

    private final int[] checkSound = {
            8, 23, 24, 26 //Bubba pain
    };
    protected final char[] bonusbuf = new char[128];
    protected int bonuscnt = 0;
    protected final Main app;
    private boolean disconnected;

    public StatisticScreen(Main app) {
        this.app = app;
    }

    @Override
    public void show() {
        engine.setbrightness(cfg.getPaletteGamma(), engine.getPaletteManager().getBasePalette());
        engine.getTimer().reset();

        for (int num : checkSound) {
            if (Sound[num].getSoundOwnerCount() == 0) {
                continue;
            }

            Source voice =  Sound[num].getSoundOwner(Sound[num].getSoundOwnerCount() - 1).voice;
            long startTime = System.currentTimeMillis();
            while (voice != null && voice.isActive()) {
                if (System.currentTimeMillis() - startTime > 2000) {
                    break;
                }
            }
        }

        StopAllSounds();
        sndStopMusic();
        clearsoundlocks();

        if (currentGame.getCON().type == RRRA) {
            if (ud.volume_number != 0) {
                switch (ud.level_number) {
                    case 1:
                        gAnmScreen.init("lvl8.anm", -1);
                        break;
                    case 2:
                        gAnmScreen.init("lvl9.anm", -1);
                        break;
                    case 3:
                        gAnmScreen.init("lvl10.anm", -1);
                        break;
                    case 4:
                        gAnmScreen.init("lvl11.anm", -1);
                        break;
                    case 5:
                        gAnmScreen.init("lvl12.anm", -1);
                        break;
                    case 6:
                        gAnmScreen.init("lvl13.anm", -1);
                        break;
                    default:
                        break;
                }
            } else {
                switch (ud.level_number) {
                    case 1:
                        gAnmScreen.init("lvl1.anm", -1);
                        break;
                    case 2:
                        gAnmScreen.init("lvl2.anm", -1);
                        break;
                    case 3:
                        gAnmScreen.init("lvl3.anm", -1);
                        break;
                    case 4:
                        gAnmScreen.init("lvl4.anm", -1);
                        break;
                    case 5:
                        gAnmScreen.init("lvl5.anm", -1);
                        break;
                    case 6:
                        gAnmScreen.init("lvl6.anm", -1);
                        break;
                    default:
                        gAnmScreen.init("lvl7.anm", -1);
                        break;
                }
            }
        }

        game.getProcessor().resetPollingStates();
        bonuscnt = 0;
    }

    @Override
    public void render(float delta) {
        game.getRenderer().clearview(0);
        if (numplayers > 1) {
            game.pNet.GetPackets();
        }

        dobonus(false);

        engine.nextpage(delta);
    }

    public void dobonus(boolean disconnect) {
        if (game.getProcessor().isKeyJustPressed(Input.Keys.ANY_KEY)) {
            anyKeyPressed();
        }

        int t, gfx;
        int i, y, xfragtotal, yfragtotal;
        this.disconnected = disconnect;
        Renderer renderer = game.getRenderer();
        if (ud.multimode > 1 && ud.coop != 1 || disconnect) {
            renderer.rotatesprite(0, 0, 65536, 0, MENUSCREEN, 16, 0, 2 + 8 + 16 + 64);
            renderer.rotatesprite(160 << 16, 34 << 16, 65536, 0, INGAMELNRDTHREEDEE, 0, 0, 10);

            app.getFont(1).drawTextScaled(renderer, 160, 58 + 2, "MULTIPLAYER TOTALS", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            buildString(bonusbuf, 0, currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number));
            app.getFont(1).drawTextScaled(renderer, 160, 58 + 10, bonusbuf, 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);

            t = 0;
            app.getFont(0).drawTextScaled(renderer, 23, 80, "   NAME                                           KILLS", 1.0f, 0, 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            for (i = 0; i < numplayers; i++) {
                Bitoa(i + 1, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 92 + (i * 23), 80, bonusbuf, 1.0f, 0, 3, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }

            for (i = 0; i < numplayers; i++) {
                xfragtotal = 0;
                Bitoa(i + 1, bonusbuf);

                app.getFont(0).drawTextScaled(renderer, 30, 90 + t, bonusbuf, 1.0f, 0, 3, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(0).drawTextScaled(renderer, 38, 90 + t, ud.user_name[i], 1.0f, 0, ps[i].palookup, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                for (y = 0; y < numplayers; y++) {
                    if (i == y) {
                        Bitoa(ps[y].fraggedself, bonusbuf);
                        app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), 90 + t, bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                        xfragtotal -= ps[y].fraggedself;
                    } else {
                        Bitoa(frags[i][y], bonusbuf);
                        app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), 90 + t, bonusbuf, 1.0f, 0, 0, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                        xfragtotal += frags[i][y];
                    }
                }

                Bitoa(xfragtotal, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 101 + (8 * 23), 90 + t, bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                t += 7;
            }

            for (y = 0; y < numplayers; y++) {
                yfragtotal = 0;
                for (i = 0; i < numplayers; i++) {
                    if (i == y) {
                        yfragtotal += ps[i].fraggedself;
                    }
                    yfragtotal += frags[i][y];
                }
                Bitoa(yfragtotal, bonusbuf);
                app.getFont(0).drawTextScaled(renderer, 92 + (y * 23), 96 + (8 * 7), bonusbuf, 1.0f, 0, 2, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            }

            app.getFont(0).drawTextScaled(renderer, 45, 96 + (8 * 7), "DEATHS", 1.0f, 0, 8, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
            app.getFont(1).drawTextScaled(renderer, 160, 165, "PRESS ANY KEY TO CONTINUE", 1.0f, 0, 2, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }

        if (!disconnect && (ud.multimode < 2 || ud.coop == 1)) {
            MapInfo gMapInfo = currentGame.episodes[ud.volume_number].getMapInfo(ud.last_level - 1);
            if (engine.getTotalClock() >= (1000000000) && engine.getTotalClock() < (1000000320)) {
                if (((engine.getTotalClock() >> 4) % 15) == 0 && bonuscnt == 6) {
                    bonuscnt++;
                    sound(425);
                    Source voice = null;
                    switch (engine.rand() & 3) {
                        case 0:
                            voice = sound(BONUS_SPEECH1);
                            break;
                        case 1:
                            voice = sound(BONUS_SPEECH2);
                            break;
                        case 2:
                            voice = sound(BONUS_SPEECH3);
                            break;
                        case 3:
                            voice = sound(BONUS_SPEECH4);
                            break;
                    }
                    long startTime = System.currentTimeMillis();
                    while (voice != null && voice.isActive()) {
                        if (System.currentTimeMillis() - startTime > 2000) {
                            break;
                        }
                    }
                }
            } else if (engine.getTotalClock() > (10240 + 120)) {
                onSkip();
                return;
            }

            String lastmapname = "null";
            if (ud.volume_number == 2 && ud.last_level == 4 && boardfilename != null) {
                lastmapname = boardfilename.getName();
            } else if (gMapInfo != null) {
                lastmapname = gMapInfo.getTitle();
            }

            int pal = 0;
            if (currentGame.getCON().type != RRRA) {
                int level = ud.level_number;
                if (ud.volume_number != 0) {
                    gfx = 408 + level;
                } else {
                    if (level == 0) {
                        level = 1;
                    }
                    gfx = 402 + level;
                }

                if (mUserFlag == UserFlag.UserMap && boardfilename != null && ud.level_number == 3 && ud.volume_number == 2) {
                    renderer.rotatesprite(0, 0, 65536, 0, 403, 0, 0, 2 + 8 + 16 + 64);
                } else {
                    renderer.rotatesprite(0, 0, 65536, 0, gfx, 0, 0, 2 + 8 + 16 + 64);
                }
            } else {
                if (gAnmScreen.isInited()) {
                    gAnmScreen.play();
                } else {
                    renderer.rotatesprite(0, 0, 65536, 0, 403, 0, 0, 2 + 8 + 16 + 64);
                }
                pal = 2;
            }

            app.getFont(2).drawTextScaled(renderer, 160, 10 - 8, lastmapname, 1.0f, 0, pal, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            app.getFont(2).drawTextScaled(renderer, 160, 180, "PRESS ANY KEY TO CONTINUE", 1.0f, 0, pal, TextAlign.Center, Transparent.None, ConvertType.Normal, false);

            int pos = 30;
            if (engine.getTotalClock() > (60 * 3)) {
                app.getFont(2).drawTextScaled(renderer, 10, pos, "Yer Time:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(2).drawTextScaled(renderer, 10, pos += 19, "Par time:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(2).drawTextScaled(renderer, 10, pos + 19, "Xatrix Time:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                if (bonuscnt == 0) {
                    bonuscnt++;
                }

                if (engine.getTotalClock() > (60 * 4)) {
                    if (bonuscnt == 1) {
                        bonuscnt++;
                        sound(404);
                    }

                    pos = 30;
                    int num = Bitoa(ps[myconnectindex].player_par / (26 * 60), bonusbuf, 2);
                    buildString(bonusbuf, num, " : ", (ps[myconnectindex].player_par / 26) % 60, 2);
                    app.getFont(2).drawTextScaled(renderer, 211, pos, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                    if (gMapInfo != null) {
                        num = Bitoa(gMapInfo.partime / (26 * 60), bonusbuf, 2);
                        buildString(bonusbuf, num, " : ", (gMapInfo.partime / 26) % 60, 2);
                        app.getFont(2).drawTextScaled(renderer, 211, pos += 19, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                        num = Bitoa(gMapInfo.designertime / (26 * 60), bonusbuf, 2);
                        buildString(bonusbuf, num, " : ", (gMapInfo.designertime / 26) % 60, 2);
                        app.getFont(2).drawTextScaled(renderer, 211, pos + 19, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                    }
                }
            }
            if (engine.getTotalClock() > (60 * 6)) {
                pos = 95;
                app.getFont(2).drawTextScaled(renderer, 10, pos, "Varmints Killed:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(2).drawTextScaled(renderer, 10, pos + 19, "Varmints Left:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                if (bonuscnt == 2) {
                    bonuscnt++;
                }

                if (engine.getTotalClock() > (60 * 7)) {
                    if (bonuscnt == 3) {
                        bonuscnt++;
                        sound(422);
                    }

                    Bitoa(ps[connecthead].actors_killed, bonusbuf);
                    app.getFont(2).drawTextScaled(renderer, 251, pos, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                    if (isPsychoSkill()) {
                        buildString(bonusbuf, 0, "N/A");
                        app.getFont(2).drawTextScaled(renderer, 251, pos + 19, "N/A", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                    } else {
                        Bitoa(Math.max((ps[connecthead].max_actors_killed - ps[connecthead].actors_killed), 0), bonusbuf);
                        app.getFont(2).drawTextScaled(renderer, 251, pos + 19, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                    }
                }
            }
            if (engine.getTotalClock() > (60 * 9)) {
                pos = 133;
                app.getFont(2).drawTextScaled(renderer, 10, pos, "Secrets Found:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                app.getFont(2).drawTextScaled(renderer, 10, pos + 19, "Secrets Missed:", 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                if (bonuscnt == 4) {
                    bonuscnt++;
                }

                if (engine.getTotalClock() > (60 * 10)) {
                    if (bonuscnt == 5) {
                        bonuscnt++;
                        sound(404);
                    }
                    Bitoa(ps[connecthead].secret_rooms, bonusbuf);
                    app.getFont(2).drawTextScaled(renderer, 251, pos, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);

                    Bitoa(ps[connecthead].max_secret_rooms - ps[connecthead].secret_rooms, bonusbuf);
                    app.getFont(2).drawTextScaled(renderer, 251, pos + 19, bonusbuf, 1.0f, 0, pal, TextAlign.Left, Transparent.None, ConvertType.Normal, false);
                }
            }

            if (engine.getTotalClock() > 10240 && engine.getTotalClock() < 10240 + 10240) {
                engine.getTimer().setTotalClock(1024);
            }
        }
    }

    @Override
    public boolean gameKeyDown(GameKey gameKey) {
        return true;
    }

    public void onSkip() {
        Gdx.app.postRunnable(() -> {
            if (currentGame.getCON().type == RRRA) {
                gAnmScreen.skip();
            }

            if ((uGameFlags & MODE_END) != 0 || mUserFlag == UserFlag.UserMap) {
                if ((uGameFlags & MODE_END) != 0 && ud.volume_number == 0) {
                    ud.volume_number = 1;
                    ud.level_number = 0;
                    gGameScreen.enterlevel(gGameScreen.getTitle());
                    return;
                }

                game.show();
            } else {
                gGameScreen.enterlevel(gGameScreen.getTitle());
            }
        });
    }

    public void anyKeyPressed() {
        game.getProcessor().prepareNext();
        if (ud.multimode > 1 && ud.coop != 1 || disconnected) {
            if (engine.getTotalClock() > (60 * 2)) {
                onSkip();
            }
        } else {
            if (engine.getTotalClock() > (60 * 2)) {// JBF 20030809
                if (engine.getTotalClock() < (60 * 13)) {
                    engine.getTimer().setTotalClock(60 * 13);
                } else if (engine.getTotalClock() < (1000000000)) {
                    engine.getTimer().setTotalClock(1000000000);
                }
            }
        }
    }

    @Override
    public InputListener getInputListener() {
        return this;
    }

    @Override
    public void processInput(GameProcessor processor) {
        processor.prepareNext();
    }

}
