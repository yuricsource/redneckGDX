// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import org.jetbrains.annotations.NotNull;
import ru.m210projects.Build.Board;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.filehandle.art.ArtEntry;
import ru.m210projects.Build.filehandle.art.DynamicArtEntry;
import ru.m210projects.Build.filehandle.fs.Directory;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Menus.MenuCorruptGame;
import ru.m210projects.Redneck.Types.*;
import ru.m210projects.Redneck.filehandle.EpisodeEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Redneck.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Player.setpal;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Screen.vscrn;
import static ru.m210projects.Redneck.RSector.setsectinterpolate;
import static ru.m210projects.Redneck.SoundDefs.DUKE_JETPACK_IDLE;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Types.ANIMATION.*;
import static ru.m210projects.Redneck.View.*;

public class LoadSave {

    public static final String savsign = "RGDX";
    public static final int gdxSave = 100;
    public static final int currentGdxSave = 103; // v1.16 102
    public static final int SAVETIME = 8;
    public static final int SAVENAME = 32;
    public static final int SAVESCREENSHOTSIZE = 160 * 100;

    public static final char[] filenum = new char[4];
    public static boolean gQuickSaving;
    public static boolean gAutosaveRequest;
    public static final LSInfo lsInf = new LSInfo();
    public static FileEntry lastload;
    public static int quickslot = 0;
    public static final SafeLoader loader = new SafeLoader();

    public static void FindSaves(Directory dir) {
        for (Entry file : dir) {
            if (file.isExtension("sav") && file instanceof FileEntry) {
                try (InputStream is = file.getInputStream()) {
                    String signature = StreamUtils.readString(is, 4);
                    if (signature.isEmpty()) {
                        continue;
                    }

                    if (signature.equals(savsign)) {
                        int nVersion = StreamUtils.readShort(is);
                        if (nVersion >= gdxSave) {
                            long time = StreamUtils.readLong(is);
                            String savname = StreamUtils.readString(is, SAVENAME);
                            game.pSavemgr.add(savname, time, (FileEntry) file);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        game.pSavemgr.sort();
    }

    public static int lsReadLoadData(FileEntry file) {
        if (file.exists()) {
            ArtEntry pic = engine.getTile(SaveManager.Screenshot);
            if (!(pic instanceof DynamicArtEntry) || !pic.exists()) {
                pic = engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);
            }

            try (InputStream is = file.getInputStream()) {
                int nVersion = checkSave(is) & 0xFFFF;
                lsInf.clear();

                if (nVersion == currentGdxSave) {
                    lsInf.date = game.date.getDate(StreamUtils.readLong(is));
                    StreamUtils.skip(is, SAVENAME);

                    lsInf.read(is);
                    if (is.available() <= SAVESCREENSHOTSIZE) {
                        return -1;
                    }

                    ((DynamicArtEntry) pic).copyData(StreamUtils.readBytes(is, SAVESCREENSHOTSIZE));

                    lsInf.addonfile = null;
                    if (StreamUtils.readBoolean(is)) {
                        boolean isPacked = StreamUtils.readBoolean(is);
                        String fullname = StreamUtils.readDataString(is);
                        String ininame = FileUtils.getPath(fullname).getFileName().toString();
                        if (isPacked) {
                            ininame = ininame + ":" + FileUtils.getPath(StreamUtils.readDataString(is)).getFileName().toString();
                        }

                        if (!ininame.isEmpty()) {
                            lsInf.addonfile = "File: " + ininame;
                        }
                    }

                    return 1;
                } else {
                    lsInf.info = "Incompatible ver. " + nVersion + " != " + currentGdxSave;
                    return -1;
                }
            } catch (Exception e) {
                Console.out.println(e.toString(), OsdColor.RED);
            }
        }

        lsInf.clear();
        return -1;
    }

    public static String makeNum(int num) {
        filenum[3] = (char) ((num % 10) + 48);
        filenum[2] = (char) (((num / 10) % 10) + 48);
        filenum[1] = (char) (((num / 100) % 10) + 48);
        filenum[0] = (char) (((num / 1000) % 10) + 48);

        return new String(filenum);
    }

    public static int checkSave(InputStream is) throws IOException {
        String signature = StreamUtils.readString(is, 4);
        if (!signature.equals(savsign)) {
            return 0;
        }

        return StreamUtils.readShort(is);
    }

    public static void savegame(Directory dir, String savename, String filename) {
        if (isPsychoSkill()) {
            FTA(53, ps[myconnectindex]);
            return;
        }

        FileEntry file = dir.getEntry(filename);
        if (file.exists()) {
            if (!file.delete()) {
                addmessage("Game not saved. Access denied!");
                return;
            }
        }

        Path path = dir.getPath().resolve(filename);
        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(path))) {
            long time = game.date.getCurrentDate();
            save(os, savename, time);


            file = dir.addEntry(path);
            if (file.exists()) {
                game.pSavemgr.add(savename, time, file);
                lastload = file;
                addmessage("GAME SAVED");
            } else {
                throw new FileNotFoundException(filename);
            }
        } catch (Exception e) {
            addmessage("Game not saved! " + e);
        }

    }

    public static void MapSave(OutputStream os) throws IOException {
        if (boardfilename != null && boardfilename.exists()) {
            if (boardfilename instanceof FileEntry) {
                StreamUtils.writeString(os, ((FileEntry) boardfilename).getPath().toString(), 144);
            } else {
                StreamUtils.writeString(os, boardfilename.getName(), 144);
            }
        } else {
            StreamUtils.writeString(os, "", 144);
        }

        Board board = boardService.getBoard();
        Sector[] sectors = board.getSectors();
        StreamUtils.writeInt(os, sectors.length);
        for (Sector s : sectors) {
            s.writeObject(os);
        }

        Wall[] walls = board.getWalls();
        StreamUtils.writeInt(os, walls.length);
        for (Wall wal : walls) {
            wal.writeObject(os);
        }

        List<Sprite> sprites = board.getSprites();
        StreamUtils.writeInt(os, sprites.size());
        for (Sprite s : sprites) {
            s.writeObject(os);
        }

        StreamUtils.writeInt(os, rorcnt);
        for (int i = 0; i < 16; i++) {
            StreamUtils.writeShort(os, rorsector[i]);
            StreamUtils.writeByte(os, rortype[i]);
        }
    }

    public static void StuffSave(OutputStream os) throws IOException {
        StreamUtils.writeShort(os, numcyclers);
        for (int i = 0; i < MAXCYCLERS; i++) {
            for (int j = 0; j < 6; j++) {
                StreamUtils.writeShort(os, cyclers[i][j]);
            }
        }
        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i].writeObject(os);
        }
        for (int i = 0; i < MAXPLAYERS; i++) {
            po[i].writeObject(os);
        }
        StreamUtils.writeShort(os, numanimwalls);
        for (int i = 0; i < MAXANIMWALLS; i++) {
            StreamUtils.writeShort(os, animwall[i].wallnum);
            StreamUtils.writeInt(os, animwall[i].tag);
        }
        for (int i = 0; i < 2048; i++) {
            StreamUtils.writeInt(os, msx[i]);
        }
        for (int i = 0; i < 2048; i++) {
            StreamUtils.writeInt(os, msy[i]);
        }

        StreamUtils.writeShort(os, spriteqloc);
        StreamUtils.writeShort(os, currentGame.getCON().spriteqamount);
        for (int i = 0; i < 1024; i++) {
            StreamUtils.writeShort(os, spriteq[i]);
        }

        StreamUtils.writeShort(os, mirrorcnt);
        for (int i = 0; i < 64; i++) {
            StreamUtils.writeShort(os, mirrorwall[i]);
        }
        for (int i = 0; i < 64; i++) {
            StreamUtils.writeShort(os, mirrorsector[i]);
        }

        show2dsector.writeObject(os);
        shadeEffect.writeObject(os);

        StreamUtils.writeInt(os, numjaildoors);
        for (int i = 0; i < MAXJAILDOORS; i++) {
            StreamUtils.writeInt(os, jailspeed[i]);
            StreamUtils.writeInt(os, jaildistance[i]);
            StreamUtils.writeShort(os, jailsect[i]);
            StreamUtils.writeShort(os, jaildirection[i]);
            StreamUtils.writeShort(os, jailunique[i]);
            StreamUtils.writeShort(os, jailsound[i]);
            StreamUtils.writeShort(os, jailstatus[i]);
            StreamUtils.writeInt(os, jailcount2[i]);
        }

        StreamUtils.writeInt(os, numminecart);
        for (int i = 0; i < MAXMINECARDS; i++) {
            StreamUtils.writeInt(os, minespeed[i]);
            StreamUtils.writeInt(os, minefulldist[i]);
            StreamUtils.writeInt(os, minedistance[i]);
            StreamUtils.writeShort(os, minechild[i]);
            StreamUtils.writeShort(os, mineparent[i]);
            StreamUtils.writeShort(os, minedirection[i]);
            StreamUtils.writeShort(os, minesound[i]);
            StreamUtils.writeShort(os, minestatus[i]);
        }

        StreamUtils.writeInt(os, numtorcheffects);
        for (int i = 0; i < MAXTORCHES; i++) {
            StreamUtils.writeShort(os, torchsector[i]);
            StreamUtils.writeByte(os, torchshade[i]);
            StreamUtils.writeShort(os, torchflags[i]);
        }

        StreamUtils.writeInt(os, numlightnineffects);
        for (int i = 0; i < MAXLIGHTNINS; i++) {
            StreamUtils.writeShort(os, lightninsector[i]);
            StreamUtils.writeShort(os, lightninshade[i]);
        }

        StreamUtils.writeInt(os, numambients);
        for (int i = 0; i < MAXAMBIENTS; i++) {
            StreamUtils.writeShort(os, ambienttype[i]);
            StreamUtils.writeShort(os, ambientid[i]);
            StreamUtils.writeShort(os, ambienthitag[i]);
        }

        StreamUtils.writeInt(os, numgeomeffects);
        for (int i = 0; i < MAXGEOMETRY; i++) {
            StreamUtils.writeShort(os, geomsector[i]);
            StreamUtils.writeShort(os, geoms1[i]);
            StreamUtils.writeInt(os, geomx1[i]);
            StreamUtils.writeInt(os, geomy1[i]);
            StreamUtils.writeInt(os, geomz1[i]);

            StreamUtils.writeShort(os, geoms2[i]);
            StreamUtils.writeInt(os, geomx2[i]);
            StreamUtils.writeInt(os, geomy2[i]);
            StreamUtils.writeInt(os, geomz2[i]);
        }

        StreamUtils.writeShort(os, (short) UFO_SpawnCount);
        StreamUtils.writeShort(os, (short) UFO_SpawnTime);
        StreamUtils.writeShort(os, (short) UFO_SpawnHulk);

        StreamUtils.writeShort(os, (short) 0); // gEndFirstEpisode
        StreamUtils.writeShort(os, (short) 0); // gEndGame
        StreamUtils.writeByte(os, (byte) (plantProcess ? 1 : 0));

        StreamUtils.writeShort(os, BellTime);
        StreamUtils.writeInt(os, BellSound);
        StreamUtils.writeShort(os, word_119BE0);
        StreamUtils.writeInt(os, WindDir);
        StreamUtils.writeInt(os, WindTime);
        StreamUtils.writeInt(os, mamaspawn_count);
        StreamUtils.writeInt(os, fakebubba_spawn);
        StreamUtils.writeInt(os, dword_119C08);
    }

    public static void ConSave(OutputStream os) throws IOException {
        StreamUtils.writeInt(os, currentGame.getCON().actortype.length);
        for (short s : currentGame.getCON().actortype) {
            StreamUtils.writeByte(os, (byte) s);
        }

        StreamUtils.writeInt(os, MAXSCRIPTSIZE);
        for (int i = 0; i < MAXSCRIPTSIZE; i++) {
            StreamUtils.writeInt(os, currentGame.getCON().script[i]);
        }

        StreamUtils.writeInt(os, currentGame.getCON().actorscrptr.length);
        for (int i : currentGame.getCON().actorscrptr) {
            StreamUtils.writeInt(os, i);
        }

        StreamUtils.writeInt(os, hittype.length);
        for (Weaponhit weaponhit : hittype) {
            weaponhit.writeObject(os);
        }
    }

    public static void GameInfoSave(OutputStream os) throws IOException {
        StreamUtils.writeShort(os, pskybits);
        Renderer renderer = game.getRenderer();
        StreamUtils.writeInt(os, renderer.getParallaxScale());
        for (int i = 0; i < MAXPSKYTILES; i++) {
            StreamUtils.writeShort(os, pskyoff[i]);
        }

        StreamUtils.writeShort(os, earthquaketime);
        StreamUtils.writeShort(os, (short) ud.from_bonus);
        StreamUtils.writeShort(os, (short) ud.secretlevel);
        StreamUtils.writeByte(os, ud.respawn_monsters ? (byte) 1 : 0);
        StreamUtils.writeByte(os, ud.respawn_items ? (byte) 1 : 0);
        StreamUtils.writeByte(os, ud.respawn_inventory ? (byte) 1 : 0);
        StreamUtils.writeByte(os, ud.god ? (byte) 1 : 0);
        StreamUtils.writeInt(os, ud.auto_run);
        StreamUtils.writeInt(os, ud.crosshair);
        StreamUtils.writeByte(os, ud.monsters_off ? (byte) 1 : 0);
        StreamUtils.writeInt(os, ud.last_level);
        StreamUtils.writeInt(os, ud.eog);
        StreamUtils.writeInt(os, ud.coop);
        StreamUtils.writeInt(os, ud.marker);
        StreamUtils.writeInt(os, ud.ffire);
        StreamUtils.writeShort(os, camsprite);

        StreamUtils.writeShort(os, connecthead);
        for (int i = 0; i < MAXPLAYERS; i++) {
            StreamUtils.writeShort(os, connectpoint2[i]);
        }
        StreamUtils.writeShort(os, numplayersprites);

        for (int i = 0; i < MAXPLAYERS; i++) {
            for (int j = 0; j < MAXPLAYERS; j++) {
                StreamUtils.writeShort(os, frags[i][j]);
            }
        }
        StreamUtils.writeInt(os, engine.getrand());
        StreamUtils.writeShort(os, global_random);
    }

    public static void AnimationSave(OutputStream os) throws IOException {
        for (int i = 0; i < MAXANIMATES; i++) {
            StreamUtils.writeShort(os, gAnimationData[i].id);
            StreamUtils.writeByte(os, gAnimationData[i].type);
            StreamUtils.writeInt(os, gAnimationData[i].goal);
            StreamUtils.writeInt(os, gAnimationData[i].vel);
            StreamUtils.writeShort(os, gAnimationData[i].sect);
        }
        StreamUtils.writeInt(os, gAnimationCount);
    }

    public static void SaveVersion(OutputStream os, int nVersion) throws IOException {
        StreamUtils.writeString(os, savsign);
        StreamUtils.writeShort(os, nVersion);
    }

    public static void SaveHeader(OutputStream os, String savename, long time) throws IOException {
        SaveVersion(os, currentGdxSave);

        StreamUtils.writeLong(os, time);
        StreamUtils.writeString(os, savename, SAVENAME);

        StreamUtils.writeInt(os, ud.multimode);
        StreamUtils.writeInt(os, ud.volume_number);
        StreamUtils.writeInt(os, ud.level_number);
        StreamUtils.writeInt(os, ud.player_skill);
    }

    public static void SaveScreenshot(OutputStream os) throws IOException {
        if (gGameScreen.captBuffer != null) {
            StreamUtils.writeBytes(os, gGameScreen.captBuffer);
        } else {
            StreamUtils.writeBytes(os, new byte[SAVESCREENSHOTSIZE]);
        }
        gGameScreen.captBuffer = null;
    }

    public static void SaveGDXBlock(OutputStream os) throws IOException {
        SaveScreenshot(os);

        byte warp_on = 0;
        if (mUserFlag == UserFlag.Addon) {
            warp_on = 1;
        }
        if (mUserFlag == UserFlag.UserMap) {
            warp_on = 2;
        }

        StreamUtils.writeByte(os, warp_on);
        if (warp_on == 1) { // user episode
            if (currentGame != null) {
                EpisodeEntry episodeEntry = currentGame.getEpisodeEntry();
                boolean isPacked = episodeEntry.isPackageEpisode();
                StreamUtils.writeBoolean(os, isPacked);
                StreamUtils.writeDataString(os, episodeEntry.getFileEntry().getRelativePath().toString());
                if (isPacked) {
                    StreamUtils.writeDataString(os, episodeEntry.getConFile().getName());
                }
            } else {
                StreamUtils.writeBoolean(os, false); // packed
                StreamUtils.writeInt(os, 0); // name length
            }
        }
    }

    public static void save(OutputStream os, String savename, long time) throws IOException {
        SaveHeader(os, savename, time);
        SaveGDXBlock(os);

        MapSave(os);
        StuffSave(os);
        ConSave(os);
        AnimationSave(os);
        GameInfoSave(os);

        os.flush();
        System.gc();
    }

    public static void quicksave() {
        if (numplayers > 1 || mFakeMultiplayer) {
            return;
        }

        Sprite psp = boardService.getSprite(ps[myconnectindex].i);
        if (psp != null && psp.getExtra() > 0) {
            gQuickSaving = true;
        }
    }

    public static boolean canLoad(FileEntry fil) {
        if (fil.exists()) {
            try (InputStream is = fil.getInputStream()) {
                int nVersion = checkSave(is) & 0xFFFF;
                if (nVersion != currentGdxSave) {
                    if (nVersion >= gdxSave) {
                        final GameInfo addon = loader.LoadGDXHeader(is);

                        if (loader.level_number <= nMaxMaps && loader.volume_number < nMaxEpisodes && loader.player_skill >= 0 && loader.player_skill < nMaxSkills && loader.warp_on != 2) {
                            final MenuCorruptGame menu = getMenuCorruptGame(addon);
                            game.menu.mOpen(menu, -1);
                        }
                    }
                }
                return nVersion == currentGdxSave;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @NotNull
    private static MenuCorruptGame getMenuCorruptGame(GameInfo addon) {
        MenuCorruptGame menu = (MenuCorruptGame) game.menu.mMenus[CORRUPTLOAD];
        menu.setRunnable(() -> {
            GameInfo game = addon != null ? addon : defGame;
            int nEpisode = loader.volume_number;
            int nLevel = loader.level_number;
            int nSkill = loader.player_skill - 1;
            gGameScreen.newgame(false, game, nEpisode, nLevel, nSkill);
        });
        return menu;
    }

    public static void quickload() {
        if (numplayers > 1 || mFakeMultiplayer) {
            return;
        }
        if (isPsychoSkill()) {
            FTA(53, ps[myconnectindex]);
            return;
        }
        final FileEntry loadFile = game.pSavemgr.getLast();
        if (canLoad(loadFile)) {
            game.changeScreen(gLoadingScreen.setTitle(loadFile.getName()));
            gLoadingScreen.init(() -> {
                if (!loadgame(loadFile)) {
                    game.setPrevScreen();
                }
            });
        }
    }

    public static void AnimationLoad(SafeLoader bb) {
        for (int i = 0; i < MAXANIMATES; i++) {
            gAnimationData[i].id = bb.gAnimationData[i].id;
            gAnimationData[i].type = bb.gAnimationData[i].type;
            gAnimationData[i].ptr = bb.gAnimationData[i].ptr;
            gAnimationData[i].goal = bb.gAnimationData[i].goal;
            gAnimationData[i].vel = bb.gAnimationData[i].vel;
            gAnimationData[i].sect = bb.gAnimationData[i].sect;
        }
        gAnimationCount = bb.gAnimationCount;

        for (int i = gAnimationCount - 1; i >= 0; i--) {
            ANIMATION gAnm = gAnimationData[i];
            Object object = (gAnm.ptr = getobject(gAnm.id, gAnm.type));
            switch (gAnm.type) {
                case WALLX:
                case WALLY:
                    game.pInt.setwallinterpolate(gAnm.id, (Wall) object);
                    break;
                case FLOORZ:
                    game.pInt.setfloorinterpolate(gAnm.id, (Sector) object);
                    break;
                case CEILZ:
                    game.pInt.setceilinterpolate(gAnm.id, (Sector) object);
                    break;
            }
        }
    }

    public static void ConLoad(SafeLoader loader) {
        System.arraycopy(loader.actortype.items, 0, currentGame.getCON().actortype, 0, loader.actortype.size);
        System.arraycopy(loader.script, 0, currentGame.getCON().script, 0, MAXSCRIPTSIZE);
        System.arraycopy(loader.actorscrptr.items, 0, currentGame.getCON().actorscrptr, 0, loader.actorscrptr.size);

        for (int i = 0; i < loader.hittype.size; i++) {
            hittype[i].set(loader.hittype.items[i]);
        }
    }

    public static void GameInfoLoad(SafeLoader bb) {
        pskybits = bb.pskybits;
        Renderer renderer = game.getRenderer();
        renderer.setParallaxScale(bb.parallaxyscale);
        System.arraycopy(bb.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
        System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

        earthquaketime = bb.earthquaketime;
        ud.from_bonus = bb.from_bonus;
        ud.secretlevel = bb.secretlevel;
        ud.respawn_monsters = bb.respawn_monsters;
        ud.respawn_items = bb.respawn_items;
        ud.respawn_inventory = bb.respawn_inventory;
        ud.god = bb.god;
        ud.auto_run = bb.auto_run;
        ud.crosshair = bb.crosshair;
        ud.monsters_off = bb.monsters_off;
        ud.last_level = bb.last_level;
        ud.eog = bb.eog;
        ud.coop = bb.coop;
        ud.marker = bb.marker;
        ud.ffire = bb.ffire;
        camsprite = bb.camsprite;

        connecthead = bb.connecthead;
        System.arraycopy(bb.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);
        numplayersprites = bb.numplayersprites;

        for (int i = 0; i < MAXPLAYERS; i++) {
            System.arraycopy(bb.frags[i], 0, frags[i], 0, MAXPLAYERS);
        }

        engine.srand(bb.randomseed);
        global_random = bb.global_random;
    }

    public static void StuffLoad(SafeLoader bb) {
        numcyclers = bb.numcyclers;
        for (int i = 0; i < MAXCYCLERS; i++) {
            System.arraycopy(bb.cyclers[i], 0, cyclers[i], 0, 6);
        }
        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i].copy(bb.ps[i]);
        }
        for (int i = 0; i < MAXPLAYERS; i++) {
            po[i].copy(bb.po[i]);
        }

        numanimwalls = bb.numanimwalls;
        for (int i = 0; i < MAXANIMWALLS; i++) {
            animwall[i].wallnum = bb.animwall[i].wallnum;
            animwall[i].tag = bb.animwall[i].tag;
        }

        System.arraycopy(bb.msx, 0, msx, 0, 2048);
        System.arraycopy(bb.msy, 0, msy, 0, 2048);

        spriteqloc = bb.spriteqloc;
        currentGame.getCON().spriteqamount = bb.spriteqamount;
        System.arraycopy(bb.spriteq, 0, spriteq, 0, 1024);

        mirrorcnt = bb.mirrorcnt;
        System.arraycopy(bb.mirrorwall, 0, mirrorwall, 0, 64);
        System.arraycopy(bb.mirrorsector, 0, mirrorsector, 0, 64);
        show2dsector.copy(bb.show2dsector);
        shadeEffect.copy(bb.shadeEffect);

        numjaildoors = bb.numjaildoors;
        for (int i = 0; i < MAXJAILDOORS; i++) {
            jailspeed[i] = bb.jailspeed[i];
            jaildistance[i] = bb.jaildistance[i];
            jailsect[i] = bb.jailsect[i];
            jaildirection[i] = bb.jaildirection[i];
            jailunique[i] = bb.jailunique[i];
            jailsound[i] = bb.jailsound[i];
            jailstatus[i] = bb.jailstatus[i];
            jailcount2[i] = bb.jailcount2[i];
        }

        numminecart = bb.numminecart;
        for (int i = 0; i < MAXMINECARDS; i++) {
            minespeed[i] = bb.minespeed[i];
            minefulldist[i] = bb.minefulldist[i];
            minedistance[i] = bb.minedistance[i];
            minechild[i] = bb.minechild[i];
            mineparent[i] = bb.mineparent[i];
            minedirection[i] = bb.minedirection[i];
            minesound[i] = bb.minesound[i];
            minestatus[i] = bb.minestatus[i];
        }

        numtorcheffects = bb.numtorcheffects;
        for (int i = 0; i < MAXTORCHES; i++) {
            torchsector[i] = bb.torchsector[i];
            torchshade[i] = bb.torchshade[i];
            torchflags[i] = bb.torchflags[i];
        }

        numlightnineffects = bb.numlightnineffects;
        for (int i = 0; i < MAXLIGHTNINS; i++) {
            lightninsector[i] = bb.lightninsector[i];
            lightninshade[i] = bb.lightninshade[i];
        }

        numambients = bb.numambients;
        for (int i = 0; i < MAXAMBIENTS; i++) {
            ambienttype[i] = bb.ambienttype[i];
            ambientid[i] = bb.ambientid[i];
            ambienthitag[i] = bb.ambienthitag[i];
        }

        numgeomeffects = bb.numgeomeffects;
        for (int i = 0; i < MAXGEOMETRY; i++) {
            geomsector[i] = bb.geomsector[i];
            geoms1[i] = bb.geoms1[i];
            geomx1[i] = bb.geomx1[i];
            geomy1[i] = bb.geomy1[i];
            geomz1[i] = bb.geomz1[i];

            geoms2[i] = bb.geoms2[i];
            geomx2[i] = bb.geomx2[i];
            geomy2[i] = bb.geomy2[i];
            geomz2[i] = bb.geomz2[i];
        }

        UFO_SpawnCount = bb.UFO_SpawnCount;
        UFO_SpawnTime = bb.UFO_SpawnTime;
        UFO_SpawnHulk = bb.UFO_SpawnHulk;

        InitSpecialTextures();

        BowlReset();
        plantProcess = bb.plantProcess;

        BellTime = bb.BellTime;
        BellSound = bb.BellSound;
        word_119BE0 = bb.word_119BE0;
        WindDir = bb.WindDir;
        WindTime = bb.WindTime;
        mamaspawn_count = bb.mamaspawn_count;
        fakebubba_spawn = bb.fakebubba_spawn;
        dword_119C08 = bb.dword_119C08;
    }

    public static void MapLoad(SafeLoader bb) {
        boardfilename = loader.boardfilename;
        boardService.setBoard(new Board(null, loader.sector, loader.wall, loader.sprite));

        rorcnt = bb.rorcnt;
        System.arraycopy(bb.rorsector, 0, rorsector, 0, 16);
        System.arraycopy(bb.rortype, 0, rortype, 0, 16);
    }

    public static void LoadGDXBlock() {
        if (loader.warp_on == 0) {
            mUserFlag = UserFlag.None;
        }
        if (loader.warp_on == 1) {
            mUserFlag = UserFlag.Addon;
        }
        if (loader.warp_on == 2) {
            mUserFlag = UserFlag.UserMap;
        }

        if (mUserFlag == UserFlag.Addon) {
            GameInfo ini = loader.addon;
            checkEpisodeResources(ini);
        } else {
            resetEpisodeResources();
        }
    }

    public static boolean checkfile(InputStream is) throws IOException {
        int nVersion = checkSave(is);
        if (nVersion != currentGdxSave) {
            return false;
        }

        return loader.load(is);
    }

    public static void load() {
        gDemoScreen.onLoad();

        ud.multimode = loader.multimode;
        ud.volume_number = loader.volume_number;
        ud.level_number = loader.level_number;
        ud.player_skill = loader.player_skill;

        LoadGDXBlock();
        MapLoad(loader);
        StuffLoad(loader);
        ConLoad(loader);
        AnimationLoad(loader);
        GameInfoLoad(loader);

        if (over_shoulder_on != 0) {
            cameradist = 0;
            cameraclock = 0;
            over_shoulder_on = 1;
        }

        screenpeek = myconnectindex;

        if (ps[myconnectindex].fogtype == 2) {
            engine.getPaletteManager().applyfog(2);
        } else {
            engine.getPaletteManager().applyfog(0);
        }

        if (ud.lockout == 0) {
            for (int x = 0; x < numanimwalls; x++) {
                Wall wal = boardService.getWall(animwall[x].wallnum);
                if (wal != null && wal.getExtra() >= 0) {
                    wal.setPicnum(wal.getExtra());
                }
            }
        }

        ListNode<Sprite> n = boardService.getStatNode(3);
        while (n != null) {
            Sprite sp = n.get();
            switch (sp.getLotag()) {
                case 31:
                case 32:
                case 25:
                case 17:
                    game.pInt.setfheinuminterpolate(sp.getSectnum(), boardService.getSector(sp.getSectnum()));
                    break;
                case 0:
                case 5:
                case 6:
                case 11:
                case 14:
                case 15:
                case 16:
                case 26:
                case 30:
                    setsectinterpolate(n.getIndex());
                    break;
            }

            n = n.getNext();
        }

        fta = 0;
        everyothertime = 0;

        game.doPrecache(() -> {
            InitSpecialTextures();
            clearsoundlocks();

            userMusicEntry = null;
            if (boardfilename != null) {
                Entry file = boardfilename;
                if (file.exists()) {
                    sndCheckMusic(file);
                }
            }

            musicvolume = ud.volume_number;
            musiclevel = ud.level_number;
            sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);

            if (ps[myconnectindex].jetpack_on != 0) {
                spritesound(DUKE_JETPACK_IDLE, ps[myconnectindex].i);
            }

            setpal(ps[myconnectindex]);
            vscrn(ud.screen_size);
            Sounds.setReverb(false, 0);

            game.net.predict.reset();
            game.gPaused = false;

            game.nNetMode = NetMode.Single;

            Sector sec = boardService.getSector(ps[myconnectindex].one_parallax_sectnum);
            if (sec != null) {
                setupbackdrop(sec.getCeilingpicnum());
            }

            game.changeScreen(gGameScreen);
            game.pNet.ResetTimers();
            game.pNet.WaitForAllPlayers(0);
            game.pNet.ready2send = true;

            StopAllSounds();

            System.gc();
        });
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean loadgame(FileEntry fil) {
        if (fil.exists()) {
            try (InputStream is = fil.getInputStream()) {
                Console.out.println("debug: start loadgame()", OsdColor.BLUE);
                boolean status = checkfile(is);
                if (status) {
                    load();
                    if (lastload == null || !lastload.exists()) {
                        lastload = fil;
                    }

                    if (loader.getMessage() != null) {
                        addmessage(loader.getMessage());
                    }

                    return true;
                }

                addmessage("Incompatible version of saved game found!");
                return false;
            } catch (Exception e) {
                Console.out.println(e.toString(), OsdColor.RED);
            }
        }

        addmessage("Can't access to file or file not found!");
        return false;
    }
}
