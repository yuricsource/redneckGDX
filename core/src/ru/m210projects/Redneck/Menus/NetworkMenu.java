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

package ru.m210projects.Redneck.Menus;

import org.jetbrains.annotations.NotNull;
import ru.m210projects.Build.Pattern.MenuItems.*;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Build.Pattern.BuildNet.kPacketLevelStart;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.USERCONTENT;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;

public class NetworkMenu extends BuildMenu {

    private final Main app;

    private int mGameType = 1;
    private int mEpisodeId = 0;
    private int mLevelId = 0;
    private int mDifficulty = 2;
    private int mMonsters = 0;
    private int mFFire = 0;
    private int mMarkers = 1;
    private String mContent = "";
    private Object currentFile;

    private final int[] netEpisodeInfo = new int[nMaxEpisodes];

    private final MenuProc mLevelsUpdate;
    private final MenuConteiner mMenuLevel;
    private final MenuConteiner mMenuEpisode;
    private final MenuConteiner mMenuDifficulty;

    public NetworkMenu(final Main app) {
        super(app.pMenu);
        this.app = app;
        final RRMenuHandler menu = app.menu;
        addItem(new RRTitle("NETWORK GAME"), false);

        final MenuConteiner pItem = new MenuConteiner("Content", app.getFont(2), 20,
                45, 280, new String[]{""}, 0, null) {

            @Override
            public boolean callback(MenuHandler handler, MenuOpt opt) {
                switch (opt) {
                    case ENTER:
                    case LMB:
                        RUserContent usercont = (RUserContent) menu.mMenus[USERCONTENT];
                        if (!usercont.showmain) {
                            usercont.setShowMain(true);
                        }
                        StopAllSounds();
                        handler.mOpen(usercont, -1);
                        return false;
                    default:
                        return m_pMenu.mNavigation(opt);
                }
            }

            @Override
            public void open() {
                if (!app.isCurrentScreen(gGameScreen)) {
                    setEpisode(defGame);
                }
            }

            @Override
            public void draw(MenuHandler handler) {
                this.list[0] = mContent.toCharArray(); // the char[] length should be the same as for mContent
                super.draw(handler);
            }
        };
        pItem.listFont = app.getFont(1);

        MenuConteiner mMenuGame = new MenuConteiner("GAME TYPE", app.getFont(1), 20, 70, 280, null, 0, (handler, pItem18) -> {
            MenuConteiner item = (MenuConteiner) pItem18;
            mGameType = item.num;
        }) {

            @Override
            public void open() {
                if (this.list == null) {
                    this.list = new char[3][];
                    this.list[0] = "DUKEMATCH (SPAWN)".toCharArray();
                    this.list[1] = "COOPERATIVE PLAY".toCharArray();
                    this.list[2] = "DUKEMATCH (NO SPAWN)".toCharArray();
                }
                num = mGameType;
            }
        };

        mLevelsUpdate = (handler, pItem17) -> {
            MenuConteiner item = (MenuConteiner) pItem17;
            if (currentFile instanceof GameInfo) {
                GameInfo mGameInfo = (GameInfo) currentFile;
                int size = mGameInfo.episodes[netEpisodeInfo[mEpisodeId]].nMaps;
                if (item.list == null || item.list.length != size) {
                    item.list = new char[size][];
                }

                for (int i = 0; i < size; i++) {
                    item.list[i] = mGameInfo.episodes[netEpisodeInfo[mEpisodeId]].getMapTitle(i).toCharArray();
                }
            } else {
                item.list = new char[1][];
                item.list[0] = "None".toCharArray();
            }

            mLevelId = item.num = 0;
        };

        mMenuLevel = new MenuConteiner("LEVEL", app.getFont(1), 20, 90, 280, null, 0, (handler, pItem16) -> {
            MenuConteiner item = (MenuConteiner) pItem16;
            mLevelId = item.num;
        }) {

            @Override
            public void open() {
                num = mLevelId;
            }

            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(currentFile instanceof GameInfo);
                this.text = ("LEVEL " + (mLevelId + 1)).toCharArray();

                super.draw(handler);
            }
        };

        mMenuEpisode = new MenuConteiner("EPISODE", app.getFont(1), 20, 80, 280, null, 0, (handler, pItem15) -> {
            MenuConteiner item = (MenuConteiner) pItem15;
            mEpisodeId = item.num;
            mLevelsUpdate.run(menu, mMenuLevel);
        }) {

            @Override
            public void open() {
                num = mEpisodeId;
            }

            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(currentFile instanceof GameInfo);
                this.text = ("EPISODE " + (mEpisodeId + 1)).toCharArray();

                super.draw(handler);
            }
        };

        int pos = 90;
        mMenuDifficulty = new MenuConteiner("MONSTERS", app.getFont(1), 20, pos += 12, 280, null, 0, (handler, pItem14) -> {
            MenuConteiner item = (MenuConteiner) pItem14;
            if (item.num == 0) {
                mMonsters = 1;
            } else {
                mMonsters = 0;
            }

            mDifficulty = item.num - 1;
        }) {
            @Override
            public void open() {
                num = mDifficulty + 1;
            }

            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(currentFile instanceof GameInfo);
                super.draw(handler);
            }
        };

        MenuSwitch mMenuMarkers = new MenuSwitch("MARKERS", app.getFont(1), 20, pos += 12, 280, mMarkers == 1, (handler, pItem13) -> {
            MenuSwitch sw = (MenuSwitch) pItem13;
            mMarkers = sw.value ? 1 : 0;
        }, "Yes", "No");

        MenuSwitch mMenuFFire = new MenuSwitch("FRIENDLY FIRE", app.getFont(1), 20, pos += 12, 280, mFFire == 1, (handler, pItem12) -> {
            MenuSwitch sw = (MenuSwitch) pItem12;
            mFFire = sw.value ? 1 : 0;
        }, "Yes", "No") {

            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(mGameType == 1);
                super.draw(handler);
            }
        };

        final MenuButton mStart = getMenuButton(app, pos);

        addItem(pItem, true);
        addItem(mMenuGame, false);
        addItem(mMenuEpisode, false);
        addItem(mMenuLevel, false);
        addItem(mMenuDifficulty, false);
        addItem(mMenuMarkers, false);
        addItem(mMenuFFire, false);
        addItem(mStart, false);
    }

    @NotNull
    private MenuButton getMenuButton(Main app, int pos) {
        final MenuProc mNetStart = (handler, pItem1) -> {
            pNetInfo.nGameType = mGameType;
            if (currentFile instanceof Entry) {
                pNetInfo.nEpisode = mEpisodeId;
            } else {
                pNetInfo.nEpisode = netEpisodeInfo[mEpisodeId];
            }
            pNetInfo.nLevel = mLevelId;
            pNetInfo.nDifficulty = mDifficulty;
            pNetInfo.nMonsters = mMonsters;
            pNetInfo.nRespawnMonsters = 0;
            pNetInfo.nRespawnInventory = 0;
            pNetInfo.nRespawnItem = 0;
            pNetInfo.nMarkers = mMarkers;
            pNetInfo.nFriendlyFire = mFFire;

            System.err.println("nNetType " + pNetInfo.nGameType);
            System.err.println("nNetDifficulty " + pNetInfo.nDifficulty);
            System.err.println("nNetMonsters " + pNetInfo.nMonsters);
            System.err.println("nNetMarkers " + pNetInfo.nMarkers);
            System.err.println("nNetFFire " + pNetInfo.nFriendlyFire);

            if (numplayers >= 2) {
                byte[] packbuf = app.net.packbuf;
                packbuf[0] = kPacketLevelStart;
                int ptr = 1;

                LittleEndian.putInt(packbuf, ptr, myconnectindex);
                ptr += 4;
                LittleEndian.putInt(packbuf, ptr, app.net.nNetVersion);
                ptr += 4;

                System.arraycopy(pNetInfo.getBytes(), 0, packbuf, ptr, pNetInfo.sizeof);
                ptr += pNetInfo.sizeof;

                for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
                    if (i != myconnectindex) {
                        sendpacket(i, packbuf, ptr);
                    }
                    if ((myconnectindex != connecthead)) {
                        break; // slaves in M/S mode only send to master
                    }
                }
            }

            if (app.net.WaitForAllPlayers(0)) {
                gGameScreen.newgame(true, currentFile, netEpisodeInfo[mEpisodeId], mLevelId, mDifficulty);
            }
        };

        return new MenuButton("START GAME", app.getFont(2), 20, pos + 15, 280, 1, 0, null, -1, mNetStart, 0) {
            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(myconnectindex == connecthead && currentFile != null);
                super.draw(handler);
            }
        };
    }

    private void updateUserEpisodeList(GameInfo gInfo) {
        Arrays.fill(netEpisodeInfo, -1);
        int nEpisodes = 0;
        for (int i = 0; i < nMaxEpisodes; i++) {
            if (gInfo.episodes[i] != null && gInfo.episodes[i].nMaps != 0) {
                netEpisodeInfo[nEpisodes++] = i;
            }
        }
        if (mMenuEpisode.list == null || mMenuEpisode.list.length != nEpisodes) {
            mMenuEpisode.list = new char[nEpisodes][];
        }

        for (int i = 0; i < nEpisodes; i++) {
            mMenuEpisode.list[i] = gInfo.episodes[netEpisodeInfo[i]].Title.toCharArray();
        }

        if (mMenuDifficulty.list == null) {
            mMenuDifficulty.list = new char[5][];
        }
        mMenuDifficulty.list[0] = "NONE".toCharArray();
        for (int i = 0; i < 4; i++) {
            mMenuDifficulty.list[1 + i] = gInfo.skillnames[i].toCharArray();
        }
    }

    public Object getFile() {
        return currentFile;
    }

    public void setEpisode(GameInfo ini) {
        if (ini == null || currentFile == ini) {
            return;
        }

        Console.out.println("Send episode " + ini.getEpisodeEntry().getFileEntry().getRelativePath());

        if (myconnectindex == connecthead && !app.net.WaitForContentCheck(getEpisodeContentData(ini.getEpisodeEntry()), 0)) {
            final String msg = getString("  is missing content: ", ini.title);

            Console.out.println(msg, OsdColor.RED);
            if (!Console.out.isShowing()) {
                Console.out.onToggle();
            }

            currentFile = null;
            return;
        }

        currentFile = ini;
        mContent = ini.title;
        mEpisodeId = 0;
        mMenuEpisode.num = 0;

        updateUserEpisodeList(ini);
        mLevelsUpdate.run(app.pMenu, mMenuLevel);
    }

    private String getString(String x, String ini) {
        StringBuilder msg = new StringBuilder();
        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            if (app.net.gContentFound[i] != 1) {
                if (app.net.gContentFound[i] == 2) {
                    msg.append(ud.user_name[i]).append("(wrong checksum)").append(", ");
                } else {
                    msg.append(ud.user_name[i]).append(", ");
                }
            }
        }

        msg = new StringBuilder(msg.substring(0, msg.length() - 2));
        msg.append(x).append(ini);
        return msg.toString();
    }

    public void setMap(FileEntry map) {
        if (map == null || currentFile == map) {
            return;
        }

        if (myconnectindex == connecthead && !app.net.WaitForContentCheck(getUserMapContentData(map), 0)) {
            String msg = getString(" haven't content: ", map.getName());

            Console.out.println(msg, OsdColor.RED);
            if (!Console.out.isShowing()) {
                Console.out.onToggle();
            }

            currentFile = null;
            return;
        }

        currentFile = map;
        mContent = map.getName();

        if (mMenuEpisode.list != null) {
            mMenuEpisode.list[0] = "None".toCharArray();
        }
        mEpisodeId = mMenuEpisode.num = 0;
        if (mMenuLevel.list != null) {
            mMenuLevel.list[0] = "Usermap".toCharArray();
        }
        mLevelId = mMenuLevel.num = 0;

        if (mMenuDifficulty.list == null) {
            mMenuDifficulty.list = new char[5][];
        }
        mMenuDifficulty.list[0] = "NONE".toCharArray();
        for (int i = 0; i < 4; i++) {
            mMenuDifficulty.list[1 + i] = defGame.skillnames[i].toCharArray();
        }
    }

    private byte[] getEpisodeContentData(EpisodeEntry episodeEntry) {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            StreamUtils.writeBoolean(os, true);
            StreamUtils.writeDataString(os, episodeEntry.getFileEntry().getRelativePath().toString());
            StreamUtils.writeLong(os, numplayers > 1 ? episodeEntry.getFileEntry().getChecksum() : 0);

            boolean isPacked = episodeEntry.isPackageEpisode();
            StreamUtils.writeBoolean(os, isPacked);
            if (isPacked) {
                StreamUtils.writeDataString(os, episodeEntry.getConFile().getName());
            }

            return os.toByteArray();
        } catch (Exception e) {
            Console.out.println(e.toString(), OsdColor.RED);
            return new byte[0];
        }
    }

    private byte[] getUserMapContentData(FileEntry entry) {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            StreamUtils.writeBoolean(os, false);
            StreamUtils.writeDataString(os, entry.getRelativePath().toString());
            StreamUtils.writeLong(os, numplayers > 1 ? entry.getChecksum() : 0);
            return os.toByteArray();
        } catch (Exception e) {
            Console.out.println(e.toString(), OsdColor.RED);
            return new byte[0];
        }
    }

}
