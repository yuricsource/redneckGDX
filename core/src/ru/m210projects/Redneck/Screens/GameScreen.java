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
import ru.m210projects.Build.Architecture.common.audio.SoundData;
import ru.m210projects.Build.Architecture.common.audio.Source;
import ru.m210projects.Build.Board;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.*;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.input.GameKey;
import ru.m210projects.Build.input.GameProcessor;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.settings.GameKeys;
import ru.m210projects.Redneck.Config.RRKeys;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Sounds;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.PlayerStruct;

import java.util.Arrays;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.automapping;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Animate.doanimations;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Factory.RRNetwork.kPacketMessage;
import static ru.m210projects.Redneck.Factory.RRNetwork.kPacketSound;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.DYNAMITE;
import static ru.m210projects.Redneck.Names.HURTRAIL;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.ResourceHandler.checkEpisodeResources;
import static ru.m210projects.Redneck.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.RSector.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.addweapon;
import static ru.m210projects.Redneck.Weapons.moveweapons;

public class GameScreen extends GameAdapter {

    private final Main game;
    boolean shiftPressed = false;
    boolean ctrlPressed = false;
    private int nonsharedtimer;

    public GameScreen(Main game) {
        super(game, gLoadingScreen);
        this.game = game;
        for (int i = 0; i < MAXPLAYERS; i++) {
            sync[i] = new Input();
        }
    }

    @Override
    public void PostFrame(BuildNet net) {
        if (gQuickSaving) {
            if (captBuffer != null) {
                savegame(game.getUserDirectory(), "[quicksave_" + quickslot + "]", "quicksav" + quickslot + ".sav");
                quickslot ^= 1;
                gQuickSaving = false;
            } else {
                gGameScreen.capture(160, 100);
            }
        }

        if (gAutosaveRequest) {
            if (captBuffer != null) {
                savegame(game.getUserDirectory(), "[autosave]", "autosave.sav");
                gAutosaveRequest = false;
            } else {
                gGameScreen.capture(160, 100);
            }
        }
    }

    @Override
    public void ProcessFrame(BuildNet net) {
        ud.camerasprite = -1;
        everyothertime++;

        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            sync[i].Copy(net.gFifoInput[net.gNetFifoTail & 0xFF][i]);
        }
        net.gNetFifoTail++;

        int j = -1;
        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            cheatkeys(i);
            if (DemoScreen.isDemoScreen(this) || (sync[i].bits & (1 << 26)) == 0) {
                j = i;
                continue;
            }

            gDemoScreen.onStopRecord();

            if (i == myconnectindex) {
                game.gExit = true;
            }
            if (screenpeek == i) {
                screenpeek = connectpoint2[i];
                if (screenpeek < 0) {
                    screenpeek = connecthead;
                }
            }

            if (i == connecthead) {
                connecthead = connectpoint2[connecthead];
            } else {
                connectpoint2[j] = connectpoint2[i];
            }

            numplayers--;
            ud.multimode--;

            if (numplayers < 2) {
                sound(GENERIC_AMBIENCE17);
            }

            quickkill(ps[i]);
            engine.deletesprite(ps[i].i);

            buildString(buf, 0, ud.user_name[i], " is history!");
            adduserquote(buf);

            vscrn(ud.screen_size);

            if (j < 0) {
                game.show();
                Console.out.println(" \nThe 'MASTER/First player' just quit the game.  All\nplayers are returned from the game.");
                return;
            }
        }

        net.CalcChecksum();

        int TICSPERFRAME = engine.getTimer().getFrameTicks(); // TODO: Temporaly code to reset interpolation in LegacyTimer
        lockclock += TICSPERFRAME;

        if (game.gPaused || !DemoScreen.isDemoPlaying() && !DemoScreen.isDemoScreen(this) && ud.multimode < 2 && (game.menu.gShowMenu || Console.out.isShowing())) {
            return;
        }

        gDemoScreen.onRecord();

        if (earthquaketime > 0) {
            earthquaketime--;
        }
        if (rtsplaying > 0) {
            rtsplaying--;
        }

        for (int i = 0; i < MAXUSERQUOTES; i++) {
            if (user_quote_time[i] != 0) {
                user_quote_time[i]--;
            }
        }

        if ((klabs(quotebotgoal - quotebot) <= 16) && (ud.screen_size <= 3)) {
            quotebot += ksgn(quotebotgoal - quotebot);
        } else {
            quotebot = quotebotgoal;
        }

        if (fta > 0) {
            fta--;
            if (fta == 0) {
                ftq = 0;
            }
        }

        if (ps[screenpeek].fogtype == 0) {
            if (engine.getTotalClock() < lastvisinc) {
                if (klabs(gVisibility - currentGame.getCON().const_visibility) > 8) {
                    gVisibility += (currentGame.getCON().const_visibility - gVisibility) >> 2;
                }
            } else {
                gVisibility = currentGame.getCON().const_visibility;
            }
        }

        global_random = (short) engine.krand();
        movedummyplayers();//ST 13

        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            ps[i].UpdatePlayerLoc();
            processinput(i);
            checksectors(i);
        }

        movefta();//ST 2
        moveweapons();          //ST 5 (must be last)
        movetransports();       //ST 9

        moveplayers();          //ST 10
        movefallers();          //ST 12

        moveexplosions();       //ST 4

        moveactors();           //ST 1
        moveeffectors();        //ST 3
        movestandables();       //ST 6
        doanimations();
        movefx();               //ST 11

        if (numtorcheffects != 0) {
            torchesprocess();
        }

        net.CorrectPrediction();

        if ((everyothertime & 1) == 0) {
            animatewalls();
            movecyclers();
            pan3dsound();
        }

        if ((uGameFlags & MODE_EOL) == MODE_EOL) {
            game.pNet.ready2send = false;
            if (DemoScreen.isDemoScreen(this)) {
                return;
            }

            if (ud.eog == 1) {
                ud.eog = 0;
                uGameFlags |= MODE_END;
                switch (ud.volume_number) {
                    case 0:
                        gEndScreen.episode1();
                        break;
                    case 1:
                        gEndScreen.episode2();
                        break;
                    default:
                        Gdx.app.postRunnable(game::show);
                        break;
                }
            } else {
                Gdx.app.postRunnable(() -> game.changeScreen(gStatisticScreen));
            }
        }
    }

    @Override
    public void DrawWorld(float smooth) {
        displayrooms(screenpeek, (int) smooth);
    }

    @Override
    public void DrawHud(float smooth) {
        Renderer renderer = game.getRenderer();
        displayrest((int) smooth);

        if (game.net.bOutOfSync) {
            game.getFont(1).drawTextScaled(renderer, 160, 20, "Out of sync!", 1.0f, 0, 12, TextAlign.Center, Transparent.None, ConvertType.Normal, false);

            switch (game.net.bOutOfSyncByte / 4) {
                case 0: // bseed
                    game.getFont(1).drawTextScaled(renderer, 160, 30, "seed checksum error", 1.0f, 0, 12, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                    break;
                case 1: // player
                    game.getFont(1).drawTextScaled(renderer, 160, 30, "player struct checksum error", 1.0f, 0, 12, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                    break;
                case 2: // sprite
                    game.getFont(1).drawTextScaled(renderer, 160, 30, "player sprite checksum error", 1.0f, 0, 12, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                    break;
            }
        }
    }

    @Override
    public void processInput(GameProcessor processor) {
        if (ud.overhead_on != 0) {
            int j = engine.getTotalClock() - nonsharedtimer;
            nonsharedtimer += j;
            if (processor.isGameKeyPressed(GameKeys.Enlarge_Screen)) {
                zoom += (short) mulscale(j, Math.max(zoom, 256), 6);
            }
            if (processor.isGameKeyPressed(GameKeys.Shrink_Screen)) {
                zoom -= (short) mulscale(j, Math.max(zoom, 256), 6);
            }

            if ((zoom > 2048)) {
                zoom = 2048;
            }
            if ((zoom < 48)) {
                zoom = 48;
            }
        }
    }

    protected boolean gameKeyDownCommon(GameKey gameKey, boolean inGame) {
        // the super has console button handling
        if (super.gameKeyDown(gameKey)) {
            return true;
        }

        RRMenuHandler menu = game.menu;
        if (GameKeys.Menu_Toggle.equals(gameKey)) {
            if (inGame) {
                menu.mOpen(menu.mMenus[GAME], -1);
            } else {
                menu.mOpen(menu.mMenus[MAIN], -1);
            }
            return true;
        }

        if (RRKeys.Show_Loadmenu.equals(gameKey)) {
            if (numplayers > 1 || mFakeMultiplayer) {
                return false;
            }
            menu.mOpen(menu.mMenus[LOADGAME], -1);
        }

        if (RRKeys.Show_Sounds.equals(gameKey)) {
            menu.mOpen(menu.mMenus[SOUNDSET], -1);
        }

        if (RRKeys.Show_Options.equals(gameKey)) {
            menu.mOpen(menu.mMenus[OPTIONS], -1);
        }

        if (RRKeys.Show_Help.equals(gameKey)) {
            menu.mOpen(menu.mMenus[HELP], -1);
        }

        if (RRKeys.Gamma.equals(gameKey)) {
            openGamma(menu);
        }

        if (RRKeys.Messages.equals(gameKey)) {
            ud.fta_on ^= 1;
            if (ud.fta_on != 0) {
                FTA(23, ps[myconnectindex]);
            } else {
                ud.fta_on = 1;
                FTA(24, ps[myconnectindex]);
                ud.fta_on = 0;
            }
        }

        if (RRKeys.Quit.equals(gameKey)) {
            menu.mOpen(menu.mMenus[QUIT], -1);
        }

        if (RRKeys.Screenshot.equals(gameKey)) {
            makeScreenshot();
        }

        if (RRKeys.See_Coop_View.equals(gameKey)) {
            if (ud.coop == 1 || mFakeMultiplayer) {
                screenpeek = connectpoint2[screenpeek];
                if (screenpeek < 0) {
                    screenpeek = connecthead;
                }

                changepalette = 1; // if player has other palette
            }
        }

        return false;
    }

    @Override
    public boolean gameKeyDown(GameKey gameKey) {
        if (shiftPressed || ctrlPressed) {
            return false;
        }

        if (gameKeyDownCommon(gameKey, true)) {
            return true;
        }

        RRMenuHandler menu = game.menu;
        if (RRKeys.Show_Savemenu.equals(gameKey)) {
            if (numplayers > 1 || mFakeMultiplayer) {
                return false;
            }
            if (ps[myconnectindex].getPlayerSprite().getExtra() > 0) {
                gGameScreen.capture(160, 100);
                menu.mOpen(menu.mMenus[SAVEGAME], -1);
                return true;
            }
        }

        if (RRKeys.See_Chase_View.equals(gameKey)) {
            if (over_shoulder_on != 0) {
                over_shoulder_on = 0;
            } else {
                over_shoulder_on = 1;
                cameradist = 0;
                cameraclock = engine.getTotalClock();
            }
            FTA(109 + over_shoulder_on, ps[myconnectindex]);
            return true;
        }

        if (ud.overhead_on != 0) {
            if (RRKeys.Map_Follow_Mode.equals(gameKey)) {
                ud.scrollmode = !ud.scrollmode;

                if (ud.scrollmode) {
                    ud.folx = ps[myconnectindex].oposx;
                    ud.foly = ps[myconnectindex].oposy;
                    ud.fola = (int) ps[myconnectindex].oang;
                }
                FTA(83 + (ud.scrollmode ? 1 : 0), ps[myconnectindex]);
                return true;
            }
        } else {
            if (GameKeys.Enlarge_Screen.equals(gameKey)) {
                if (ud.screen_size > 0) {
                    sound(THUD);
                    enlargeScreen();
                }
                return true;
            }
            if (GameKeys.Shrink_Screen.equals(gameKey)) {
                if (ud.screen_size < 4) {
                    sound(THUD);
                    shrinkScreen();
                }
                return true;
            }
        }

        if (GameKeys.Map_Toggle.equals(gameKey)) {
            if (ud.last_overhead != ud.overhead_on && ud.last_overhead != 0) {
                ud.overhead_on = ud.last_overhead;
                ud.last_overhead = 0;
            } else {
                ud.overhead_on++;
                if (ud.overhead_on == 3) {
                    ud.overhead_on = 0;
                }
                ud.last_overhead = ud.overhead_on;
            }
            return true;
        }

        if (RRKeys.AutoRun.equals(gameKey)) {
            ud.auto_run ^= 1;
            FTA(85 + ud.auto_run, ps[myconnectindex]);
            return true;
        }

        if (RRKeys.Toggle_Crosshair.equals(gameKey)) {
            ud.crosshair ^= 1;
            FTA(21 - ud.crosshair, ps[myconnectindex]);
            return true;
        }

        if (RRKeys.Show_Opp_Weapon.equals(gameKey)) {
            ud.showweapons ^= 1;
            FTA(82 - ud.showweapons, ps[myconnectindex]);
            return true;
        }

        if (RRKeys.Quicksave.equals(gameKey)) { // quick save
            quicksave();
            return true;
        }

        if (RRKeys.Quickload.equals(gameKey)) { // quick load
            quickload();
            return true;
        }

        return false;
    }

    protected void openGamma(RRMenuHandler menu) {
        menu.mOpen(menu.mMenus[COLORCORR], -1);
    }

    @Override
    public void sndHandlePause(boolean pause) {
        Sounds.sndHandlePause(pause);
    }

    @Override
    protected boolean prepareboard(Entry entry) {
        gNameShowTime = 500;
        shiftPressed = false;
        ctrlPressed = false;

        checknextlevel();

        try {
            Board board = engine.loadboard(entry);

            boardfilename = entry;
            BuildPos out = board.getPos();
            ps[0].posx = out.x;
            ps[0].posy = out.y;
            ps[0].posz = out.z;
            ps[0].ang = out.ang;
            ps[0].cursectnum = out.sectnum;

            Arrays.fill(rorsector, (short) -1);
            Arrays.fill(rortype, (byte) -1);
            rorcnt = 0;

            prelevel();
            allignwarpelevators();
            resetpspritevars();

            automapping = 0;
            ftq = 0;
            fta = 0;
            Arrays.fill(loogiex, 0);
            Arrays.fill(loogiey, 0);


            for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
                Sprite psp = boardService.getSprite(ps[i].i);
                if (psp == null || psp.getSectnum() == 1024) {
                    continue;
                }
                Sector psec = boardService.getSector(psp.getSectnum());
                if (psec == null) {
                    continue;
                }

                if (psec.getFloorpicnum() == HURTRAIL) {
                    resetweapons(i);
                    resetinventory(i);
                    ps[i].gotweapon[PISTOL_WEAPON] = false;
                    ps[i].ammo_amount[PISTOL_WEAPON] = 0;
                    ps[i].curr_weapon = KNEE_WEAPON;
                    ps[i].kickback_pic = 0;
                }

                if ((currentGame.getCON().type == RRRA && ud.level_number == 2 && ud.volume_number == 0)
                        || (currentGame.getCON().type != RRRA && ud.level_number == 1 && ud.volume_number == 1)) {
                    resetweapons(i);
                    ps[i].gotweapon[PISTOL_WEAPON] = false;
                    ps[i].ammo_amount[PISTOL_WEAPON] = 0;
                    ps[i].curr_weapon = KNEE_WEAPON;
                    ps[i].kickback_pic = 0;
                }
            }

            gDemoScreen.onPrepareboard(this);

            if (!DemoScreen.isDemoPlaying()) {
                sndStopMusic();
                musicvolume = ud.volume_number;
                musiclevel = ud.level_number;
                sndPlayMusic(currentGame.getCON().music_fn[musicvolume][musiclevel]);
            }

            //PREMAP.C - replace near the my's at the end of the file

            ps[myconnectindex].palette = engine.getPaletteManager().getBasePalette();

            setpal(ps[myconnectindex]);

            everyothertime = 0;
            global_random = 0;

            ud.last_level = ud.level_number + 1;

            changepalette = 1;

            game.net.WaitForAllPlayers(0);
            engine.getTimer().reset();

            palto(0, 0, 0, 0);
            if (!game.menu.gShowMenu) {
                vscrn(ud.screen_size);
            }
            game.getRenderer().clearview(0);

            over_shoulder_on = 0;

            Arrays.fill(user_quote_time, (short) 0);

            game.net.predict.reset();
            clearfrags();

            System.err.println("New level " + entry);

            if ((uGameFlags & MODE_EOL) == MODE_EOL && game.nNetMode == NetMode.Single) {
                gAutosaveRequest = true;
            }

            uGameFlags &= ~(MODE_EOL | MODE_END);

            return true;
        } catch (Exception e) {
            Console.out.println("Load map exception: " + e);
            if (e.getMessage() != null) {
                game.GameMessage("Load map exception:\n" + e.getMessage());
            } else {
                game.GameMessage("Can't load the map " + entry);
            }
        }
        return false;
    }

    protected void makeScreenshot() {
        String name = "scrxxxx.png";
        Entry map = boardfilename;
        if (mUserFlag == UserFlag.UserMap && map.exists()) {
            name = "scr-" + map.getName() + "-xxxx.png";
        }
        if (mUserFlag != UserFlag.UserMap && currentGame != null) {
            name = "scr-e" + (ud.volume_number + 1) + "m" + (ud.level_number + 1) + "[" + currentGame.getEpisodeEntry().getFileEntry().getName() + "]-xxxx.png";
        }

        Renderer renderer = game.getRenderer();
        String filename = renderer.screencapture(game.getUserDirectory(), name);
        if (filename != null) {
            buildString(currentGame.getCON().fta_quotes[103], 0, filename, " saved");
        } else {
            buildString(currentGame.getCON().fta_quotes[103], 0, "Screenshot not saved. Access denied!");
        }
        FTA(103, ps[myconnectindex]);
    }

    /**
     * @param item should be GameInfo or FileEntry (map)
     */
    public void newgame(final boolean isMultiplayer, final Object item, final int nEpisode, final int nLevel, final int nDifficulty) {
        gDemoScreen.onStopRecord();

        pNet.ready2send = false;
        game.changeScreen(load); //checkEpisodeResources is slow, so we make other loading screens
        load.init(() -> {
            if (!isMultiplayer) {
                ud.multimode = 1;
                mFakeMultiplayer = false;
                if (numplayers > 1) {
                    pNet.NetDisconnect(myconnectindex);
                }

                ud.monsters_off = false;
                ud.respawn_monsters = false;
                ud.respawn_items = false;
                ud.respawn_inventory = false;

                connecthead = 0;
                connectpoint2[0] = -1;

                game.nNetMode = NetMode.Single;
            } else {
                if (mFakeMultiplayer) {
                    ud.multimode = nFakePlayers;
                    connecthead = 0;
                    for (short i = 0; i < MAXPLAYERS; i++) {
                        connectpoint2[i] = (short) (i + 1);
                    }
                    connectpoint2[ud.multimode - 1] = -1;
                } else {
                    ud.multimode = numplayers;
                }

                if (!DemoScreen.isDemoScreen(this)) {
                    ud.coop = pNetInfo.nGameType;
                    ud.monsters_off = pNetInfo.nMonsters == 1;
                    ud.respawn_monsters = false;
                    ud.respawn_inventory = true;
                    ud.respawn_items = ud.coop == 0;
                    ud.marker = pNetInfo.nMarkers;
                    ud.ffire = pNetInfo.nFriendlyFire;
                }

                ud.god = false;
                game.nNetMode = NetMode.Multiplayer;

                for (int c = connecthead; c >= 0; c = connectpoint2[c]) {
                    resetweapons(c);
                    resetinventory(c);
                }
            }

            UserFlag flag = UserFlag.None;
            if (item instanceof GameInfo && !item.equals(defGame)) {
                flag = UserFlag.Addon;
                GameInfo game = (GameInfo) item;
                checkEpisodeResources(game);
                Console.out.println("Start user episode: " + game.title);
            } else {
                resetEpisodeResources();
            }

            if (item instanceof Entry) {
                flag = UserFlag.UserMap;
                boardfilename = (Entry) item;
                ud.level_number = 3;
                ud.volume_number = 2;
                Console.out.println("Start user map: " + ((Entry) item).getName());
            }
            mUserFlag = flag;

            if (!DemoScreen.isDemoScreen(this)) {
                if (currentGame.getCON().type != RRRA) {
                    ud.player_skill = nDifficulty + 1;
                } else {
                    ud.player_skill = nDifficulty;
                }

                if (!isMultiplayer) {
                    Source skillvoice = null;
                    switch (nDifficulty) {
                        case 0:
                            skillvoice = sound(427);
                            break;
                        case 1:
                            skillvoice = sound(428);
                            break;
                        case 2:
                            skillvoice = sound(196);
                            break;
                        case 3:
                            skillvoice = sound(195);
                            break;
                        case 4:
                            skillvoice = sound(197);
                            break;
                    }

                    long startTime = System.currentTimeMillis();
                    while (skillvoice != null && skillvoice.isActive()) {
                        if (System.currentTimeMillis() - startTime > 2000) {
                            break;
                        }
                    }
                }

                if (mUserFlag != UserFlag.UserMap) {
                    ud.level_number = nLevel;
                    ud.volume_number = nEpisode;
                }

                ud.respawn_monsters = isPsychoSkill();
            }

            uGameFlags = 0;
            ud.secretlevel = 0;
            ud.from_bonus = 0;
            Renderer renderer = game.getRenderer();
            renderer.setParallaxScale(0);
            ud.last_level = -1;
            lastload = null;

            if (!isMultiplayer) {
                PlayerStruct p = ps[myconnectindex];
                if (ud.coop != 1) {
                    p.curr_weapon = PISTOL_WEAPON;
                    p.gotweapon[PISTOL_WEAPON] = true;
                    p.gotweapon[KNEE_WEAPON] = true;
                    p.ammo_amount[PISTOL_WEAPON] = 48;
                    p.gotweapon[HANDREMOTE_WEAPON] = true;
                    if (currentGame.getCON().type == RRRA) {
                        p.gotweapon[RATE_WEAPON] = true;
                    }
                    p.last_weapon = -1;
                }
                p.last_used_weapon = 0;
            }

            display_mirror = 0;
            zoom = 768;

            enterlevel(getTitle());
        });
    }

    public boolean enterlevel(String title) {
        if (title == null) {
            return false;
        }


        if (mUserFlag != UserFlag.UserMap) {
            boardfilename = game.getCache().getEntry(currentGame.episodes[ud.volume_number].getMapInfo(ud.level_number).getPath(), true);
        }

        loadboard(boardfilename, null).setTitle(title);
        return true;
    }

    public String getTitle() {
        String title = "null";
        if (mUserFlag != UserFlag.UserMap) {
            if (ud.volume_number < nMaxEpisodes && currentGame.episodes[ud.volume_number].getMapInfo(ud.level_number) != null) {
                title = currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number);
            }
        } else {
            Entry file = boardfilename;
            if (file.exists()) {
                title = file.getName();
            }
        }
        return title;
    }

    public void cheatkeys(int snum) {
        short dainv;
        int j;

        int sb_snum = sync[snum].bits;
        PlayerStruct p = ps[snum];

        if (p.cheat_phase == 1) {
            return;
        }

        final int aim = p.aim_mode;
        p.aim_mode = (sb_snum >> 23) & 1;
        if (p.aim_mode < aim && (game.nNetMode != NetMode.Single || !pMenu.gShowMenu)) {
            p.return_to_center = 9;
        }
        Sprite psp = boardService.getSprite(p.i);
        if (psp == null) {
            return;
        }

        if ((sb_snum & 1 << 22) != 0 && p.last_pissed_time == 0 && psp.getExtra() > 0) {
            p.last_pissed_time = 4000;
            if (ud.lockout == 0) {
                spritesound(437, p.i);
            }

            if (psp.getExtra() > currentGame.getCON().max_player_health - currentGame.getCON().max_player_health / 10) {
                if (psp.getExtra() < currentGame.getCON().max_player_health) {
                    psp.setExtra((short) currentGame.getCON().max_player_health);
                }
            } else {
                psp.setExtra(psp.getExtra() + 2);
                p.last_extra = psp.getExtra();
            }
        }

        if ((sb_snum & ((15 << 8) | (1 << 12) | (1 << 15) | (1 << 16) | (1 << 22) | (1 << 19) | (1 << 20) | (1 << 21) | (1 << 24) | (1 << 25) | (1 << 27) | (1 << 28) | (1 << 29) | (1 << 30) | (1 << 31))) == 0) {
            p.interface_toggle_flag = 0;
        } else if (p.interface_toggle_flag == 0 && (sb_snum & (1 << 17)) == 0) {
            p.interface_toggle_flag = 1;

            if ((sb_snum & (1 << 21)) != 0) {
                game.gPaused = !game.gPaused;
//	            if( game.gPaused && (sb_snum&(1<<5)) != 0 ) ud.pause_on = 2;
                sndHandlePause(game.gPaused);
            }

            if (game.gPaused) {
                return;
            }

            if (psp.getExtra() <= 0) {
                return;
            }

            if ((sb_snum & (1 << 30)) != 0 && p.newowner == -1) {
                switch (p.inven_icon) {
                    case 4:
                        sb_snum |= (1 << 25);
                        break;
                    case 3:
                        sb_snum |= (1 << 24);
                        break;
                    case 5:
                        sb_snum |= (1 << 15);
                        break;
                    case 1:
                        sb_snum |= (1 << 16);
                        break;
                    case 2:
                        sb_snum |= (1 << 12);
                        break;
                }
            }

            if ((sb_snum & (1 << 12)) != 0) {
                if (p.moonshine_amount == 400) {
                    p.moonshine_amount = 399;
                    spritesound(DUKE_TAKEPILLS, p.i);
                    p.inven_icon = 2;
                    FTA(12, p);
                }
                return;
            }

            if (p.newowner == -1) {
                if ((sb_snum & (1 << 20)) != 0 || (sb_snum & (1 << 27)) != 0 || p.refresh_inventory) {
                    p.invdisptime = 26 * 2;

                    int k = 0;
                    if ((sb_snum & (1 << 27)) != 0) {
                        k = 1;
                    }

                    if (p.refresh_inventory) {
                        p.refresh_inventory = false;
                    }
                    dainv = (short) p.inven_icon;

                    int i = 0;

                    boolean CHECKINV;
                    do {
                        CHECKINV = false;
                        if (i < 9) {
                            i++;

                            switch (dainv) {
                                case 4:
                                    if (p.cowpie_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 5;
                                    } else {
                                        dainv = 3;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 6:
                                    if (p.snorkle_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 7;
                                    } else {
                                        dainv = 5;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 2:
                                    if (p.moonshine_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 3;
                                    } else {
                                        dainv = 1;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 3:
                                    if (p.beer_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 4;
                                    } else {
                                        dainv = 2;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 0:
                                case 1:
                                    if (p.whishkey_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 2;
                                    } else {
                                        dainv = 7;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 5:
                                    if (p.yeehaa_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 6;
                                    } else {
                                        dainv = 4;
                                    }
                                    CHECKINV = true;
                                    break;
                                case 7:
                                    if (p.boot_amount > 0 && i > 1) {
                                        break;
                                    }
                                    if (k != 0) {
                                        dainv = 1;
                                    } else {
                                        dainv = 6;
                                    }
                                    CHECKINV = true;
                                    break;
                            }
                        } else {
                            dainv = 0;
                        }
                        p.inven_icon = dainv;
                    } while (CHECKINV);

                    switch (dainv) {
                        case 1:
                            FTA(3, p);
                            break;
                        case 2:
                            FTA(90, p);
                            break;
                        case 3:
                            FTA(91, p);
                            break;
                        case 4:
                        case 5:
                            FTA(88, p);
                            break;
                        case 6:
                            FTA(89, p);
                            break;
                        case 7:
                            FTA(6, p);
                            break;
                    }
                }
            }

            j = ((sb_snum & (15 << 8)) >> 8) - 1;
            if (j != 1 && p.kickback_pic > 0) {
                if (IsOriginalGame()) {
                    p.wantweaponfire = (short) j; //GDX 23.03.2020 Disable random weapon switch
                }
            }

            if (p.last_pissed_time <= (26 * 218)
                    && p.show_empty_weapon == 0
                    && p.kickback_pic == 0
                    && p.quick_kick == 0 && psp.getXrepeat() > 8 && p.access_incs == 0 && p.knee_incs == 0) {
                if (!IsOriginalGame() || (p.weapon_pos == 0 || (p.holster_weapon != 0 && p.weapon_pos == -9))) //quick weapon switch
                {
                    if (j == 12) //last used weapon
                    {
                        j = p.curr_weapon;
                        if (p.last_used_weapon == 0 || p.last_used_weapon == 15) {
                            j = p.last_used_weapon;
                        } else if (p.gotweapon[p.last_used_weapon] && p.ammo_amount[p.last_used_weapon] > 0) {
                            j = p.last_used_weapon;
                        }
                    }

                    if (j == 10 || j == 11) //next prev weapon
                    {
                        int k = p.curr_weapon;
                        switch (k) {
                            case CHICKENBOW_WEAPON:
                                k = CROSSBOW_WEAPON;
                                break;
                            case BUZSAW_WEAPON:
                                k = THROWSAW_WEAPON;
                                break;
                            case RATE_WEAPON:
                                k = KNEE_WEAPON;
                                break;
                        }

                        j = (j == 10 ? -1 : 1);

                        int i = 0;
                        while ((k >= 0 && k < 10) /*|| ( k == BUZSAW_WEAPON && (p.subweapon&(1<<BUZSAW_WEAPON) ) != 0 )*/) {
                            k += j;

                            if (k == -1) {
                                k = 9;
                            } else if (k == 10) {
                                k = 0;
                            }

                            if (p.gotweapon[k] && p.ammo_amount[k] > 0) {
                                j = k;
                                break;
                            }

                            i++;
                            if (i == 10) {
                                addweapon(p, KNEE_WEAPON);
                                break;
                            }
                        }
                    }

                    int k = -1;

                    if (j == DYNAMITE_WEAPON && p.ammo_amount[DYNAMITE_WEAPON] == 0) {
                        for (ListNode<Sprite> node = boardService.getStatNode(1); node != null; node = node.getNext()) {
                            Sprite sp = node.get();
                            k = node.getIndex();
                            if (sp.getPicnum() == DYNAMITE && sp.getOwner() == p.i) {
                                p.gotweapon[DYNAMITE_WEAPON] = true;
                                j = HANDREMOTE_WEAPON;
                                break;
                            }
                        }
                    }

                    if (currentGame.getCON().type == RRRA && j == CROSSBOW_WEAPON) {
                        if (p.curr_weapon != CROSSBOW_WEAPON && p.ammo_amount[CROSSBOW_WEAPON] != 0) {
                            if ((p.subweapon & 4) != 0 || p.ammo_amount[CHICKENBOW_WEAPON] == 0) {
                                p.subweapon = 0;
                            }
                        } else {
                            p.subweapon = 4;
                            j = CHICKENBOW_WEAPON;
                        }
                    }

                    if (j == THROWSAW_WEAPON) {
                        if (p.curr_weapon != THROWSAW_WEAPON && p.ammo_amount[THROWSAW_WEAPON] != 0) //v0.751
                        {
                            if ((p.subweapon & (1 << BUZSAW_WEAPON)) != 0 || p.ammo_amount[BUZSAW_WEAPON] == 0) {
                                p.subweapon = 0;
                            }
                        } else {
                            p.subweapon = (1 << BUZSAW_WEAPON);
                            j = BUZSAW_WEAPON;
                        }
                    }

                    if (j == POWDERKEG_WEAPON) {

                        if (p.curr_weapon != POWDERKEG_WEAPON && p.ammo_amount[POWDERKEG_WEAPON] != 0) {
                            if ((p.subweapon & (1 << BOWLING_WEAPON)) != 0 || p.ammo_amount[BOWLING_WEAPON] == 0) {
                                p.subweapon = 0;
                            }
                        } else {
                            j = BOWLING_WEAPON;
                            p.subweapon = (1 << BOWLING_WEAPON);
                        }
                    }

                    if (currentGame.getCON().type == RRRA && j == KNEE_WEAPON) {

                        if (p.curr_weapon != KNEE_WEAPON) {
                            if ((p.subweapon & 2) != 0) {
                                p.subweapon = 0;
                            }
                        } else {
                            j = RATE_WEAPON;
                            p.subweapon = 2;
                        }
                    }


                    if (p.holster_weapon != 0) {
                        sb_snum |= 1 << 19;
                        p.weapon_pos = -9;
                    } else if (j >= 0 && j < MAX_WEAPONSRA && p.gotweapon[j] && p.curr_weapon != j) {
                        switch (j) {
                            case KNEE_WEAPON:
                                addweapon(p, KNEE_WEAPON);
                                break;
                            case PISTOL_WEAPON:
                                if (p.ammo_amount[PISTOL_WEAPON] == 0) {
                                    if (p.show_empty_weapon == 0) {
                                        p.last_full_weapon = p.curr_weapon;
                                        p.show_empty_weapon = 32;
                                    }
                                }
                                addweapon(p, PISTOL_WEAPON);
                                break;
                            case SHOTGUN_WEAPON:
                                if (p.ammo_amount[SHOTGUN_WEAPON] == 0 && p.show_empty_weapon == 0) {
                                    p.last_full_weapon = p.curr_weapon;
                                    p.show_empty_weapon = 32;
                                }
                                addweapon(p, SHOTGUN_WEAPON);
                                break;
                            case RIFLEGUN_WEAPON:
                                if (p.ammo_amount[RIFLEGUN_WEAPON] == 0 && p.show_empty_weapon == 0) {
                                    p.last_full_weapon = p.curr_weapon;
                                    p.show_empty_weapon = 32;
                                }
                                addweapon(p, RIFLEGUN_WEAPON);
                                break;
                            case DYNAMITE_WEAPON:
                                if (p.ammo_amount[DYNAMITE_WEAPON] == 0) {
                                    if (p.show_empty_weapon == 0) {
                                        p.last_full_weapon = p.curr_weapon;
                                        p.show_empty_weapon = 32;
                                    }
                                }
                                addweapon(p, DYNAMITE_WEAPON);
                                break;
                            case ALIENBLASTER_WEAPON:
                                if (p.ammo_amount[ALIENBLASTER_WEAPON] == 0 && p.show_empty_weapon == 0) {
                                    p.last_full_weapon = p.curr_weapon;
                                    p.show_empty_weapon = 32;
                                }
                                addweapon(p, ALIENBLASTER_WEAPON);
                                break;
                            case TIT_WEAPON:
                                if (p.ammo_amount[TIT_WEAPON] == 0 && p.show_empty_weapon == 0) {
                                    p.last_full_weapon = p.curr_weapon;
                                    p.show_empty_weapon = 32;
                                }
                                addweapon(p, TIT_WEAPON);
                                break;
                            case BUZSAW_WEAPON:
                            case THROWSAW_WEAPON:
                            case POWDERKEG_WEAPON:
                            case BOWLING_WEAPON:
                            case CHICKENBOW_WEAPON:
                                if (p.ammo_amount[j] == 0 && p.show_empty_weapon == 0) {
                                    p.show_empty_weapon = 32;
                                    p.last_full_weapon = p.curr_weapon;
                                }

                                addweapon(p, j);
                                break;
                            case HANDREMOTE_WEAPON:
                                if (k >= 0) // Found in list of [1]'s
                                {
                                    p.curr_weapon = HANDREMOTE_WEAPON;
                                    p.last_weapon = -1;
                                    p.weapon_pos = 10;
                                }
                                break;
                            case CROSSBOW_WEAPON:
                                if (p.ammo_amount[CROSSBOW_WEAPON] > 0 && p.gotweapon[CROSSBOW_WEAPON]) {
                                    addweapon(p, CROSSBOW_WEAPON);
                                }
                                break;
                            case MOTO_WEAPON:
                            case BOAT_WEAPON:
                                if (p.ammo_amount[j] == 0 && p.show_empty_weapon == 0) {
                                    p.show_empty_weapon = 32;
                                }
                                addweapon(p, j);
                                break;
                            case RATE_WEAPON:
                                spritesound(496, p.i);
                                addweapon(p, j);
                                break;
                        }
                    }
                }

                if ((sb_snum & (1 << 19)) != 0) {
                    if (p.curr_weapon > KNEE_WEAPON) {
                        if (p.holster_weapon == 0 && p.weapon_pos == 0) {
                            p.holster_weapon = 1;
                            p.weapon_pos = -1;
                            FTA(73, p);
                        } else if (p.holster_weapon == 1 && p.weapon_pos == -9) {
                            p.holster_weapon = 0;
                            p.weapon_pos = 10;
                            FTA(74, p);
                        }
                    }
                }
            }

            if ((sb_snum & (1 << 24)) != 0 && p.beer_amount > 0 && psp.getExtra() < currentGame.getCON().max_player_health) {
                p.beer_amount -= 400;
                psp.setExtra(psp.getExtra() + 5);
                p.inven_icon = 3;

                if (psp.getExtra() > currentGame.getCON().max_player_health) {
                    psp.setExtra((short) currentGame.getCON().max_player_health);
                }
                p.alcohol_amount += 5;
                if (p.beer_amount == 0) {
                    checkavailinven(p);
                }
                if (p.alcohol_amount < 99 && Sound[425].getSoundOwnerCount() == 0) {
                    spritesound(425, p.i);
                }
            }

            if ((sb_snum & (1 << 15)) != 0) {
                if (p.newowner == -1 && p.field_count == 0) {
                    p.field_count = 126;
                    spritesound(390, p.i);
                    p.field_290 = 0x4000;
                    sub_64EF0(snum);
                    Sector psec = boardService.getSector(p.cursectnum);
                    if (psec != null && psec.getLotag() == 857) {
                        if (psp.getExtra() < currentGame.getCON().max_player_health) {
                            psp.setExtra(psp.getExtra() + 10);
                            if (psp.getExtra() > currentGame.getCON().max_player_health) {
                                psp.setExtra((short) currentGame.getCON().max_player_health);
                            }
                        }
                    } else {
                        if (psp.getExtra() + 1 <= currentGame.getCON().max_player_health) {
                            psp.setExtra(psp.getExtra() + 1);
                        }
                    }
                }
            }

            if ((sb_snum & (1 << 16)) != 0) {
                if (p.whishkey_amount > 0 && psp.getExtra() < currentGame.getCON().max_player_health) {
                    if (p.whishkey_amount > 10) {
                        p.whishkey_amount -= 10;
                        psp.setExtra(psp.getExtra() + 10);
                        p.inven_icon = 1;
                    } else {
                        psp.setExtra(psp.getExtra() + p.whishkey_amount);
                        p.whishkey_amount = 0;
                        checkavailinven(p);
                    }
                    if (psp.getExtra() > currentGame.getCON().max_player_health) {
                        psp.setExtra((short) currentGame.getCON().max_player_health);
                    }

                    p.alcohol_amount += 10;
                    if (p.alcohol_amount <= 100 && Sound[DUKE_USEMEDKIT].getSoundOwnerCount() == 0) {
                        spritesound(DUKE_USEMEDKIT, p.i);
                    }
                }
            }

            if ((sb_snum & (1 << 25)) != 0) {
                if (p.cowpie_amount > 0) {
                    if (psp.getExtra() < currentGame.getCON().max_player_health) {
                        if (Sound[429].getSoundOwnerCount() == 0) {
                            spritesound(429, p.i);
                        }
                        p.cowpie_amount -= 100;
                        if (p.alcohol_amount > 0) {
                            p.alcohol_amount -= 5;
                            if (p.alcohol_amount < 0) {
                                p.alcohol_amount = 0;
                            }
                        }
                        if (p.gut_amount < 100) {
                            p.gut_amount += 5;
                            if (p.gut_amount > 100) {
                                p.gut_amount = 100;
                            }
                        }

                        psp.setExtra(psp.getExtra() + 5);
                        if (psp.getExtra() > currentGame.getCON().max_player_health) {
                            psp.setExtra((short) currentGame.getCON().max_player_health);
                        }
                        p.inven_icon = 4;
                        if (p.cowpie_amount <= 0) {
                            checkavailinven(p);
                        }
                    }
                }
            }

            if ((sb_snum & (1 << 28)) != 0 && p.one_eighty_count == 0) {
                p.one_eighty_count = -1024;
            }
        }
    }

    public void shrinkScreen() {
        if (ud.screen_size < 4) {
            sound(THUD);
            ud.screen_size++;
            if (ud.screen_size > 5) {
                ud.screen_size = 5;
            }
            vscrn(ud.screen_size);
        }
    }

    public void enlargeScreen() {
        if (ud.screen_size > 0) {
            sound(THUD);
            ud.screen_size--;
            if (ud.screen_size < 0) {
                ud.screen_size = 0;
            }
            vscrn(ud.screen_size);
        }

    }

    public boolean IsOriginalGame() {
        return gDemoScreen.isDemoRecording() && ud.rec != null && ud.rec.recversion <= BYTEVERSIONRR;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.SHIFT_LEFT) {
            shiftPressed = true;
            return true;
        }
        if (keycode == com.badlogic.gdx.Input.Keys.CONTROL_LEFT) {
            ctrlPressed = true;
            return true;
        }

        if (shiftPressed || ctrlPressed) {
            int fkey = -1;
            for (int i = 0; i < 10; i++) {
                if (keycode == (i + com.badlogic.gdx.Input.Keys.F1)) {
                    fkey = i;
                    break;
                }
            }

            if (!cfg.isNoSound() && (RTS_File != null && RTS_File.getSize() > 0) && rtsplaying == 0 && cfg.VoiceToggle) {
                if (fkey >= 0) {
                    if (ctrlPressed) {
                        Entry entry = RTS_File.getEntry(fkey);
                        if (entry.exists()) {
                            SoundData data = Sounds.audio.getSoundDecoder("VOC").decode(entry);
                            if (data != null) {
                                Source voice = Sounds.newSound(data.getData(), data.getRate(), data.getBits(), 255);
                                if (voice != null) {
                                    voice.play(1.0f);
                                }

                                rtsplaying = 7;

                                if (ud.multimode > 1) {
                                    tempbuf[0] = kPacketSound;
                                    tempbuf[1] = (byte) fkey;

                                    game.net.sendtoall(tempbuf, 2);
                                }
                            }
                        }
                    }

                    if (shiftPressed) {
                        adduserquote(ud.ridecule[fkey]);

                        if (ud.multimode > 1) {
                            tempbuf[0] = kPacketMessage;
                            tempbuf[1] = (byte) 255;
                            tempbuf[2] = 0;

                            for (int i = 0; i < ud.ridecule[fkey].length; i++) {
                                tempbuf[2 + i] = (byte) ud.ridecule[fkey][i];
                            }

                            game.net.sendtoall(tempbuf, 2 + ud.ridecule[fkey].length);
                        }
                    }
                    return true;
                }
            }
        }

        if (keycode == (com.badlogic.gdx.Input.Keys.F5) && !cfg.isMuteMusic()) {
            buildString(currentGame.getCON().fta_quotes[26], 0, currentGame.getCON().music_fn[musicvolume][musiclevel], ".  USE SHIFT-F5 TO CHANGE.");
            FTA(26, ps[myconnectindex]);
        }


        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.SHIFT_LEFT) {
            shiftPressed = false;
            return true;
        }

        if (keycode == com.badlogic.gdx.Input.Keys.CONTROL_LEFT) {
            ctrlPressed = false;
            return true;
        }
        return super.keyUp(keycode);
    }
}

