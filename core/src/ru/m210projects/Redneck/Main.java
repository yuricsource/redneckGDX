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

package ru.m210projects.Redneck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ByteArray;
import org.jetbrains.annotations.NotNull;
import ru.m210projects.Build.Architecture.MessageType;
import ru.m210projects.Build.BoardService;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.LogSender;
import ru.m210projects.Build.Pattern.ScreenAdapters.DefaultPrecacheScreen;
import ru.m210projects.Build.Pattern.ScreenAdapters.MessageScreen;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.listeners.PrecacheListener;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.Group;
import ru.m210projects.Build.filehandle.fs.Directory;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.input.GameProcessor;
import ru.m210projects.Build.osd.CommandResponse;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Build.osd.commands.OsdCallback;
import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Factory.*;
import ru.m210projects.Redneck.Fonts.GameFont;
import ru.m210projects.Redneck.Fonts.MenuFont;
import ru.m210projects.Redneck.Menus.*;
import ru.m210projects.Redneck.Screens.*;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Weaponhit;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;
import ru.m210projects.Redneck.filehandle.rts.RTSFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.filehandle.CacheResourceMap.CachePriority.NORMAL;
import static ru.m210projects.Redneck.Animate.initanimations;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Gamedef.compilecons;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Player.InitPlayers;
import static ru.m210projects.Redneck.Premap.LeaveMap;
import static ru.m210projects.Redneck.Premap.packbuf;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Screen.gViewYScaled;
import static ru.m210projects.Redneck.Sounds.*;

public class Main extends BuildGame {

    public static final String appdef = "rrgdx.def";

    public static AnmScreen gAnmScreen;
    public static MVEScreen gMveScreen;
    public static MenuScreen gMenuScreen;
    public static LoadingScreen gLoadingScreen;
    public static GameScreen gGameScreen;
    public static DemoScreen gDemoScreen;
    public static StatisticScreen gStatisticScreen;
    public static EndScreen gEndScreen;
    public static NetScreen gNetScreen;
    public static DisconnectScreen gDisconnectScreen;
    public static UserFlag mUserFlag = UserFlag.None;

    public static Main game;
    public static RREngine engine;
    public static BoardService boardService;
    public static Config cfg;
    public static RTSFile RTS_File;
    public RRMenuHandler menu;
    public RRNetwork net;
    private final Runnable rMenu = new Runnable() {
        @Override
        public void run() {
            StopAllSounds();
            ud.level_number = 0;
            ud.multimode = 1;
            mFakeMultiplayer = false;

            if (!menu.gShowMenu) {
                menu.mOpen(menu.mMenus[MAIN], -1);
            }

            if (numplayers > 1 || gDemoScreen.demofiles.isEmpty() || cfg.gDemoSeq == 0 || !gDemoScreen.showDemo(cache.getGameDirectory())) {
                changeScreen(gMenuScreen);
            }
        }
    };
    public String mainGrp;

    public Main(List<String> args, GameConfig dcfg, String name, String version, boolean isRelease) throws IOException {
        super(args, dcfg, name, version, isRelease);
        game = this;
        cfg = (Config) dcfg;
        mainGrp = "redneck.grp";
    }

    public static boolean IsOriginalDemo() {
        ScreenAdapter screen = (ScreenAdapter) game.getScreen();
        if (screen instanceof DemoScreen) {
            return ((DemoScreen) screen).IsOriginalGame();
        }
        if (screen instanceof GameScreen) {
            return ((GameScreen) screen).IsOriginalGame();
        }

        return false;
    }

    @Override
    protected MessageScreen createMessage(String header, String text, MessageType type) {
        return new RRMessageScreen(this, header, text, type);
    }

    @Override
    public void onDropEntry(FileEntry entry) {
        if (!entry.isExtension("map")) {
            return;
        }

        Console.out.println("Start dropped map: " + entry.getName());
        gGameScreen.newgame(false, entry, 0, 0, ud.player_skill);
    }

    @NotNull
    @Override
    public RRRenderer getRenderer() {
        Renderer renderer = super.getRenderer();
        if (renderer instanceof RRRenderer) {
            return (RRRenderer) renderer;
        }
        return new RRDummyRenderer();
    }

    @Override
    public GameProcessor createGameProcessor() {
        return new RRGameProcessor(this);
    }

    @Override
    public RRGameProcessor getProcessor() {
        return (RRGameProcessor) super.getProcessor();
    }

    @Override
    public BuildFactory getFactory() {
        return new RRFactory(this);
    }

    @Override
    public boolean init() throws Exception {
        net = (RRNetwork) pNet;
        boardService = engine.getBoardService();

        ConsoleInit();

        compilecons();

        InitSpecialTextures();

        InitUserDefs();

        Entry rtsEntry = cache.getEntry(ud.rtsname, true);
        if (rtsEntry.exists()) {
            RTSFile rts = new RTSFile(ud.rtsname, rtsEntry::getInputStream);
            if (rts.getSize() != 0) {
                Console.out.println("Using .RTS file:" + ud.rtsname);
                Main.RTS_File = rts;
            }
        }

        SoundStartup();
        searchCDtracks();

        initanimations();
        FindSaves(getUserDirectory());

        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i] = new PlayerStruct();
            po[i] = new PlayerOrig();
        }

        InitPlayers();

        for (int i = 0; i < MAXANIMWALLS; i++) {
            animwall[i] = new Animwalltype();
        }

        for (int i = 0; i < MAXSPRITES; i++) {
            hittype[i] = new Weaponhit();
        }

        Console.out.println("Initializing def-scripts...");
        cache.loadGdxDef(baseDef, appdef, "rrgdx.dat");

        Directory gameDir = cache.getGameDirectory();
        if (cfg.isAutoloadFolder()) {
            Console.out.println("Initializing autoload folder");
            for (Entry file : gameDir.getDirectory(gameDir.getEntry("autoload"))) {
                switch (file.getExtension()) {
                    case "PK3":
                    case "ZIP": {
                        Group group = cache.newGroup(file);
                        Entry def = group.getEntry(appdef);
                        if (def.exists()) {
                            cache.addGroup(group, NORMAL); // HIGH?
                            baseDef.loadScript(file.getName(), def);
                        }
                    }
                    break;
                    case "DEF":
                        baseDef.loadScript(file);
                        break;
                }
            }
        }

        FileEntry filgdx = gameDir.getEntry(appdef);
        if (filgdx.exists()) {
            baseDef.loadScript(filgdx);
        }
        this.setDefs(baseDef);

        gAnmScreen = new AnmScreen(this);
        gMveScreen = new MVEScreen(this);
        gLoadingScreen = new LoadingScreen(this);
        gGameScreen = new GameScreen(this);
        gDemoScreen = new DemoScreen(this);
        gStatisticScreen = new StatisticScreen(this);
        gEndScreen = new EndScreen();
        gNetScreen = new NetScreen(this);
        gDisconnectScreen = new DisconnectScreen(this);

        menu.mMenus[TRACKPLAYER] = new TrackPlayerMenu();
        menu.mMenus[MAIN] = new MainMenu(this);
        menu.mMenus[GAME] = new GameMenu(this);
        menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);
        gMenuScreen = new MenuScreen(this);

        gDemoScreen.checkDemoEntry(gameDir);

        return true;
    }

    @Override
    public boolean setDefs(DefScript script) {
        if (super.setDefs(script)) {
            ((GameFont) this.getFont(1)).update();
            ((MenuFont) this.getFont(2)).update();
            return true;
        }

        return false;
    }

    @Override
    public void show() {
        if (!args.isEmpty()) {
            // Handle arguments at the first launch. args should be clear after handle
            parseArgumentsCommon();
            String netmode = args.getOrDefault("-netmode", "");
            String players = args.getOrDefault("-players", "");
            args.clear();

//            if (!file.isEmpty() && netmode.isEmpty()) {
//                onFilesDropped(new String[] {file});
//                return;
//            }

            if (!netmode.isEmpty()) {
                Console.out.println("Starting multiplayer as " + netmode, OsdColor.YELLOW);
                if (netmode.equalsIgnoreCase("master")) {
                    String[] param = { "-n0:" + (players.isEmpty() ? 2 : players), "-p " + cfg.getPort() };
                    ((RMenuMultiplayer) menu.mMenus[MULTIPLAYER]).getMenuCreate(this).createGame(0, false, param);
                    return;
                } else if (netmode.equalsIgnoreCase("slave")) {
                    String[] param = new String[]{ "-n0", cfg.getmAddress(), "-p " + cfg.getPort() };
                    ((RMenuMultiplayer) menu.mMenus[MULTIPLAYER]).getMenuJoin(this).joinGame(param);
                    return;
                }
            }
        }

        uGameFlags = 0;
        if (usecustomarts) {
            resetEpisodeResources();
        }

        gDemoScreen.onStopRecord();

        if (currentGame.getCON().type == RRRA && gMveScreen.init("redint.mve")) {
            gMveScreen.setCallback(rMenu);
            changeScreen(gMveScreen.escSkipping(true));
        } else if (gAnmScreen.init("rr_intro.anm", 0)) {
            gAnmScreen.setCallback(() -> {
                if (gAnmScreen.init("redneck.anm", 1)) {
                    gAnmScreen.setCallback(() -> {
                        if (gAnmScreen.init("xatlogo.anm", 2)) {
                            changeScreen(gAnmScreen.setCallback(rMenu).escSkipping(false));
                        }
                    });
                    changeScreen(gAnmScreen.escSkipping(false));
                }
            }).setSkipping(rMenu);
            changeScreen(gAnmScreen.escSkipping(false));
        } else {
            changeScreen(gLoadingScreen);
            Gdx.app.postRunnable(rMenu);
        }
    }

    @Override
    public DefaultPrecacheScreen getPrecacheScreen(Runnable readyCallback, PrecacheListener listener) {
        return new PrecacheScreen(readyCallback, listener);
    }

    public void InitUserDefs() {
        ud.setDefaults(cfg);
        ud.god = false;
        ud.cashman = 0;
        ud.player_skill = 2;
    }

    public void ConsoleInit() {
        Console.out.println("Initializing on-screen display system");
        Console.out.getPrompt().setVersion(getTitle(), OsdColor.YELLOW, 10);

        Console.out.registerCommand(new OsdCallback("restart", "restart", argv -> {
            LeaveMap();
            return CommandResponse.SILENT_RESPONSE;
        }));

        Console.out.registerCommand(new OsdCallback("net_player", "net_player", argv -> {
            if (argv.length != 1) {
                Console.out.println("net_player: num");
                return CommandResponse.SILENT_RESPONSE;
            }
            try {
                String num = argv[0];

                int pnum = Integer.parseInt(num);
                int p = game.net.PutPacketByte(packbuf, 0, RRNetwork.kPacketPlayer);
                p = game.net.PutPacketByte(packbuf, p, pnum);

                int trail = game.net.gNetFifoTail;
                if (myconnectindex == connecthead) {
                    trail += 1;
                }

                LittleEndian.putInt(packbuf, p, trail);
                p += 4;
                game.net.PlayerSyncRequest = pnum;

                game.net.sendtoall(packbuf, p);
            } catch (Exception ignored) {
            }
            return CommandResponse.SILENT_RESPONSE;
        }));

        Console.out.registerCommand(new OsdCallback("quicksave", "quicksave: performs a quick save", argv -> {
            if (ud.multimode != 1 || numplayers > 1) {
                Console.out.println("quicksave: Single player only");
                return CommandResponse.SILENT_RESPONSE;
            }

            if (isCurrentScreen(gGameScreen)) {
                quicksave();
            } else {
                Console.out.println("quicksave: not in a game");
            }
            return CommandResponse.SILENT_RESPONSE;
        }));

        Console.out.registerCommand(new OsdCallback("quickload", "quickload: performs a quick load", argv -> {
            if (ud.multimode != 1 || numplayers > 1) {
                Console.out.println("quickload: Single player only");
                return CommandResponse.SILENT_RESPONSE;
            }

            if (isCurrentScreen(gGameScreen)) {
                quickload();
            } else {
                Console.out.println("quickload: not in a game");
            }
            return CommandResponse.SILENT_RESPONSE;
        }));
    }

    public void Disconnect() {
        gDemoScreen.onStopRecord();
        if (gDisconnectScreen != null) {
            changeScreen(gDisconnectScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
//        gViewXScaled = (width << 16) / 320;
        gViewYScaled = (height << 16) / 200;
    }

    @Override
    public void dispose() {
        gDemoScreen.onStopRecord();
        super.dispose();
    }

    @Override
    public LogSender getLogSender() {
        return new LogSender(this) {
            @Override
            public byte[] reportData() {
                byte[] out;

                String text = "Mapname: " + boardfilename;
                text += "\r\n";
                text += "UserFlag: " + mUserFlag;
                text += "\r\n";

                if (mUserFlag == UserFlag.Addon && currentGame != null) {
                    try {
                        EpisodeEntry addon = currentGame.getEpisodeEntry();
                        text += "Episode filename: " + addon.getHashKey() + "\r\n";
                        text += "Episode title: " + currentGame.title + "\r\n";
                        text += "Episode name: " + currentGame.getCurrentEpisode() + "\r\n";
                        text += "\r\n";
                    } catch (Exception e) {
                        text += "Episode filename get error \r\n";
                    }
                }

                text += "volume " + (ud.volume_number + 1);
                text += "\r\n";
                text += "level " + (ud.level_number + 1);
                text += "\r\n";
                text += "nDifficulty: " + ud.player_skill;
                text += "\r\n";

                text += "PlayerX: " + ps[myconnectindex].posx;
                text += "\r\n";
                text += "PlayerY: " + ps[myconnectindex].posy;
                text += "\r\n";
                text += "PlayerZ: " + ps[myconnectindex].posz;
                text += "\r\n";
                text += "PlayerAng: " + ps[myconnectindex].ang;
                text += "\r\n";
                text += "PlayerHoriz: " + ps[myconnectindex].horiz;
                text += "\r\n";
                text += "PlayerSect: " + ps[myconnectindex].cursectnum;
                text += "\r\n";

                if (mUserFlag == UserFlag.UserMap && boardfilename != null && boardfilename.exists()) {
                    ByteArray array = new ByteArray();
                    byte[] data = boardfilename.getBytes();
                    text += "\r\n<------Start Map data------->\r\n";
                    array.addAll(text.getBytes());
                    array.addAll(data);
                    array.addAll("\r\n<------End Map data------->\r\n".getBytes());

                    out = Arrays.copyOf(array.items, array.size);
                } else {
                    out = text.getBytes();
                }

                return out;
            }
        };
    }

    public enum UserFlag {
        None, UserMap, Addon
    }
}
