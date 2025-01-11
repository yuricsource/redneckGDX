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

import java.io.FileOutputStream;
import java.io.InputStream;

import com.badlogic.gdx.Screen;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.settings.GameKeys;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.input.GameKey;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.filehandle.DemoFile;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.filehandle.DemoRecorder;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_ENTRY;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.LoadSave.lastload;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Player.InitPlayers;
import static ru.m210projects.Redneck.ResourceHandler.levelGetEpisode;
import static ru.m210projects.Redneck.SoundDefs.THUD;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Types.UserDefs.*;
import static ru.m210projects.Redneck.View.operatefta;

public class DemoScreen extends GameScreen {

    public int nDemonum = -1;
    public final Map<Group, List<Entry>> demofiles = new HashMap<>();
    public DemoFile demfile;
    protected final Entry lastDemoEntry = DUMMY_ENTRY;

    public DemoScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        lastload = null;
    }

    @Override
    public void hide() {
        ud.user_name[myconnectindex] = cfg.getpName();
    }

    public boolean showDemo(Entry entry, Entry ini) {
        onStopRecord();

        demfile = null;
        try (InputStream is = entry.getInputStream()) {
            demfile = new DemoFile(is);
        } catch (Exception e) {
            Console.out.println("Can't play the demo file: " + entry.getName(), OsdColor.RED);
            return false;
        }

        InitPlayers();
        mFakeMultiplayer = demfile.multimode > 1;
        if (mFakeMultiplayer) {
            nFakePlayers = demfile.multimode;
        }

        if (numplayers > 1) {
            game.pNet.NetDisconnect(myconnectindex);
        }

        ud.volume_number = demfile.volume_number;
        ud.level_number = demfile.level_number;
        ud.player_skill = demfile.player_skill;

        ud.coop = demfile.coop;
        ud.ffire = demfile.ffire;
        ud.multimode = demfile.multimode;
        ud.monsters_off = demfile.monsters_off;
        ud.respawn_monsters = demfile.respawn_monsters;
        ud.respawn_items = demfile.respawn_items;
        ud.respawn_inventory = demfile.respawn_inventory;
        ud.playerai = demfile.playerai;
        System.arraycopy(demfile.user_name, 0, ud.user_name, 0, MAXPLAYERS);

        boardfilename = game.getCache().getEntry(demfile.boardfilename, true);

        for (int i = 0; i < ud.multimode; i++) {
            ps[i].aim_mode = demfile.aim_mode[i];
            if (demfile.version >= GDXBYTEVERSION) {
                ps[i].auto_aim = demfile.auto_aim[i];
            } else {
                ps[i].auto_aim = 1;
            }
        }

        ud.god = false;
        ud.cashman = ud.eog = ud.showallmap = 0;
        ud.clipping = ud.scrollmode = false;
        ud.overhead_on = 0;
        ud.recstat = DEMOSTAT_PLAYING;

        GameInfo addon = levelGetEpisode(ini);
        if (demfile.addon != null) {
            addon = demfile.addon;
        }

        gDemoScreen.newgame(mFakeMultiplayer, addon, ud.volume_number, ud.level_number, ud.player_skill);

        Console.out.println("Playing demo " + entry.getName());

        return true;
    }

    public List<Entry> checkDemoEntry(Group group) {
        if (demofiles.containsKey(group)) {
            return demofiles.get(group);
        }

        nDemonum = -1;
        List<Entry> demos = group.stream()
                .filter(this::isDemoFile)
                .sorted(Entry::compareTo)
                .collect(Collectors.toList());

        if (demos.isEmpty()) { //try to find it in mainGrp
            int k;
            int which_demo = 1;
            do {
                k = which_demo;

                char[] d = "demo_.dmo".toCharArray();
                if (which_demo == 10) {
                    d[4] = 'x';
                } else {
                    d[4] = (char) ('0' + which_demo);
                }
                String name = String.copyValueOf(d);
                Entry entry = game.getCache().getEntry(name, true);
                if (isDemoFile(entry)) {
                    demos.add(entry);
                } else {
                    break;
                }
                which_demo++;
            } while (k != which_demo);
        }

        demofiles.put(group, demos);
        Console.out.println("There are " + demos.size() + " demo(s) in the loop", OsdColor.YELLOW);

        if (cfg.gDemoSeq == 2) {
            int nextnum = nDemonum;
            if (demos.size() > 1) {
                while (nextnum == nDemonum) nextnum = (int) (Math.random() * (demos.size()));
            }
            nDemonum = nextnum;
        }

        return demos;
    }

    @Override
    protected void startboard(final Runnable startboard) {
        game.doPrecache(() -> {
            startboard.run(); //call faketimehandler
            pNet.ResetTimers(); //reset ototalclock
            lockclock = 0;
            pNet.ready2send = false;
        });
    }

    @Override
    public boolean gameKeyDown(GameKey gameKey) {
        if (gameKeyDownCommon(gameKey, false)) {
            return true;
        }

        if (GameKeys.Enlarge_Screen.equals(gameKey)) {
            if (ud.screen_size > 0) {
                sound(THUD);
                enlargeScreen();
            }
            return true;
        }
        if (GameKeys.Shrink_Screen.equals(gameKey)) {
            if (ud.screen_size < 3) {
                sound(THUD);
                shrinkScreen();
            }
            return true;
        }
        return true;
    }

    @Override
    public void render(float delta) {
        if (numplayers > 1) {
            pNet.GetPackets();
        }

        DemoRender();

        float smoothratio = 65536;
        if (!game.gPaused) {
            smoothratio = pEngine.getTimer().getsmoothratio(delta);
            if (smoothratio < 0 || smoothratio > 0x10000) {
                smoothratio = BClipRange(smoothratio, 0, 0x10000);
            }
        }

        game.pInt.dointerpolations(smoothratio);
        DrawWorld(smoothratio);

        DrawHud(smoothratio);
        game.pInt.restoreinterpolations();

        operatefta();

        if (ud.last_camsprite != ud.camerasprite) {
            ud.last_camsprite = ud.camerasprite;
        }

        if (pMenu.gShowMenu) {
            pMenu.mDrawMenu();
        }

        PostFrame(pNet);
        pEngine.nextpage(delta);
    }


    private void DemoRender() {
        pNet.ready2send = false;

        if (!game.isCurrentScreen(this)) {
            return;
        }

        if (!game.gPaused && demfile != null) {
            while (engine.getTotalClock() >= (lockclock + TICSPERFRAME)) {
                for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
                    pNet.gFifoInput[pNet.gNetFifoHead[j] & 0xFF][j].Copy(demfile.recsync[demfile.rcnt][j]);
                    pNet.gNetFifoHead[j]++;
                    demfile.reccnt--;
                }

                if (demfile.reccnt <= 0) {
                    demfile = null;

                    Group group = lastDemoEntry.getParent();
                    if (!showDemo(group)) {
                        game.changeScreen(gMenuScreen);
                    }
                    return;
                }

                demfile.rcnt++;
                game.pInt.clearinterpolations();
                ProcessFrame(pNet);
            }
        } else {
            lockclock = engine.getTotalClock();
        }
    }

    public boolean showDemo(Group group) {
        List<Entry> list = checkDemoEntry(group);
        switch (cfg.gDemoSeq) {
            case 0: //OFF
                return false;
            case 1: //Consistently
                if (nDemonum < (list.size() - 1)) nDemonum++;
                else {
                    nDemonum = 0;
                    // throw new RuntimeException("Done");
                }
                break;
            case 2: //Accidentally
                int nextnum = nDemonum;
                if (list.size() > 1) {
                    while (nextnum == nDemonum) {
                        nextnum = (int) (Math.random() * (list.size()));
                    }
                }
                nDemonum = BClipRange(nextnum, 0, list.size() - 1); // #GDX 28.12.2024
                break;
        }

        if (!list.isEmpty()) {
            boolean result = showDemo(list.get(nDemonum), null);
            if (!result) {
                list.remove(nDemonum);
                return showDemo(group);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean IsOriginalGame() {
        return (demfile.version <= BYTEVERSIONRR);
    }

    public boolean isDemoFile(Entry file) {
        if (file.exists()) {
            if (file.isExtension("dmo")) {
                try (InputStream is = file.getInputStream()) {
                    StreamUtils.skip(is, 4); //rcnt
                    int version = StreamUtils.readUnsignedByte(is);
                    if (version == BYTEVERSIONRR || version == GDXBYTEVERSION) {
                        return true;
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return false;
    }

    public boolean isRecordEnabled() {
        return ud.m_recstat == DEMOSTAT_RECORD;
    } // ok

    public void onPrepareboard(GameScreen screen) {
        if (screen != this && isDemoPlaying()) {
            gDemoScreen.onStopPlaying();
        }

        if (isDemoPlaying()) {
            if (demfile != null && demfile.version >= GDXBYTEVERSION) {
                for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
                    PlayerStruct p = ps[i];
                    demfile.playerInfos[i].restore(p);
                }
            }
        }

        if (screen != this && gDemoScreen.isRecordEnabled()) {
            ud.m_recstat = DEMOSTAT_NULL;

            int a, b, c, d, democount = 0;
            do {
                a = ((democount / 1000) % 10);
                b = ((democount / 100) % 10);
                c = ((democount / 10) % 10);
                d = (democount % 10);

                String fn = "demo" + a + b + c + d + ".dmo";
                if (!game.getCache().getGameDirectory().getEntry(fn).exists()) {
                    try {
                        Path path = game.getCache().getGameDirectory().getPath().resolve(fn);
                        ud.rec = new DemoRecorder(new FileOutputStream(path.toFile()), path, GDXBYTEVERSION); // JFBYTEVERSION
                        Console.out.println("Start recording to " + fn);
                        ud.recstat = DEMOSTAT_RECORD;
                    } catch (Exception e) {
                        Console.out.println("Can't start demo record: " + e, OsdColor.RED);
                    }
                    break;
                }

                democount++;
            } while (democount < 9999);
        }
    } // ok

    public static boolean isDemoPlaying() { // ok
        return ud.recstat == DEMOSTAT_PLAYING;
    }

    public static boolean isDemoScreen(Screen screen) { // ok
        return screen == gDemoScreen;
    }

    public boolean isDemoRecording() { // ok
        return ud.recstat == DEMOSTAT_RECORD;
    }

    public void onLoad() {
        onStopRecord();
        demfile = null;
        ud.recstat = DEMOSTAT_NULL;
    }

    public void onStopPlaying() {
        demfile = null;
        ud.recstat = DEMOSTAT_NULL;
    }

    public void onRecord() { // ok
        if (ud.rec != null) {
            ud.rec.record();
        }
    }

    public void onStopRecord() {
        if (ud.rec == null) {
            return;
        }

//        CompareService.close();
        ud.rec.close();
        ud.rec = null;
        ud.recstat = DEMOSTAT_NULL;
    }

}
