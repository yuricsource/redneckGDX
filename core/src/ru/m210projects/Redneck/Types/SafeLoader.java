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

package ru.m210projects.Redneck.Types;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.BitMap;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.filehandle.fs.FileEntry;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_ENTRY;
import static ru.m210projects.Redneck.Animate.MAXANIMATES;
import static ru.m210projects.Redneck.Gamedef.MAXSCRIPTSIZE;
import static ru.m210projects.Redneck.Globals.MAXANIMWALLS;
import static ru.m210projects.Redneck.Globals.MAXCYCLERS;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.ResourceHandler.episodeManager;

public class SafeLoader {

    public final int MAXJAILDOORS = 32;
    public final int MAXMINECARDS = 16;
    public final int MAXTORCHES = 64;
    public final int MAXLIGHTNINS = 64;
    public final int MAXAMBIENTS = 64;
    public final int MAXGEOMETRY = 64;
    public Entry boardfilename;
    public GameInfo addon;
    public String addonFileName;
    public String addonPackedConName;
    public byte warp_on;
    public final short[] spriteq = new short[1024];
    public short spriteqloc;
    public short spriteqamount = 64;
    public short numanimwalls;
    public final int[] msx = new int[2048];
    public final int[] msy = new int[2048];
    public final short[][] cyclers = new short[MAXCYCLERS][6];
    public short numcyclers;
    public final short[] mirrorwall = new short[64];
    public final short[] mirrorsector = new short[64];
    public short mirrorcnt;
    public final Animwalltype[] animwall = new Animwalltype[MAXANIMWALLS];
    public final PlayerOrig[] po = new PlayerOrig[MAXPLAYERS];
    public final PlayerStruct[] ps = new PlayerStruct[MAXPLAYERS];
    public final Array<Weaponhit> hittype = new Array<>(true, MAXSPRITESV7, Weaponhit.class);
    public final BitMap show2dsector = new BitMap(MAXSECTORSV7);
    public final BitMap shadeEffect = new BitMap(MAXSECTORSV7);

    public final int[] jailspeed = new int[MAXJAILDOORS];
    public final int[] jaildistance = new int[MAXJAILDOORS];
    public final short[] jailsect = new short[MAXJAILDOORS];
    public final short[] jaildirection = new short[MAXJAILDOORS];
    public final short[] jailunique = new short[MAXJAILDOORS];
    public final short[] jailsound = new short[MAXJAILDOORS];
    public final short[] jailstatus = new short[MAXJAILDOORS];
    public final int[] jailcount2 = new int[MAXJAILDOORS];
    public final int[] minespeed = new int[MAXMINECARDS];
    public final int[] minefulldist = new int[MAXMINECARDS];
    public final int[] minedistance = new int[MAXMINECARDS];
    public final short[] minechild = new short[MAXMINECARDS];
    public final short[] mineparent = new short[MAXMINECARDS];
    public final short[] minedirection = new short[MAXMINECARDS];
    public final short[] minesound = new short[MAXMINECARDS];
    public final short[] minestatus = new short[MAXMINECARDS];
    public final short[] torchsector = new short[MAXTORCHES];
    public final byte[] torchshade = new byte[MAXTORCHES];
    public final short[] torchflags = new short[MAXTORCHES];
    public final short[] lightninsector = new short[MAXLIGHTNINS];
    public final short[] lightninshade = new short[MAXLIGHTNINS];
    public final short[] ambienttype = new short[MAXAMBIENTS];
    public final short[] ambientid = new short[MAXAMBIENTS];
    public final short[] ambienthitag = new short[MAXAMBIENTS];
    public final short[] geomsector = new short[MAXGEOMETRY];
    public final short[] geoms1 = new short[MAXGEOMETRY];
    public final int[] geomx1 = new int[MAXGEOMETRY];
    public final int[] geomy1 = new int[MAXGEOMETRY];
    public final int[] geomz1 = new int[MAXGEOMETRY];
    public final short[] geoms2 = new short[MAXGEOMETRY];
    public final int[] geomx2 = new int[MAXGEOMETRY];
    public final int[] geomy2 = new int[MAXGEOMETRY];
    public final int[] geomz2 = new int[MAXGEOMETRY];

    public boolean plantProcess = false;

    public int numlightnineffects, numtorcheffects, numgeomeffects, numjaildoors, numminecart, numambients;

    public int UFO_SpawnCount;
    public int UFO_SpawnTime;
    public int UFO_SpawnHulk;

    public short gEndGame;
    public short gEndFirstEpisode;

    //RA
    public short BellTime;
    public int BellSound;
    public short word_119BE0;
    public int WindDir;
    public int WindTime;
    public int mamaspawn_count;
    public int fakebubba_spawn;
    public int dword_119C08;

    public final IntArray actorscrptr = new IntArray(MAXTILES);
    public final ShortArray actortype = new ShortArray(MAXTILES);
    public final int[] script = new int[MAXSCRIPTSIZE];

    public int gAnimationCount = 0;
    public final ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];

    public final short[] pskyoff = new short[MAXPSKYTILES];
    public short pskybits;
    public short earthquaketime;
    public int parallaxyscale;

    public short camsprite, numplayersprites;
    public short connecthead;
    public final short[] connectpoint2 = new short[MAXPLAYERS];
    public final short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];

    public int randomseed;
    public short global_random;

    //UserDef
    public int multimode;
    public int volume_number;
    public int level_number;
    public int player_skill;
    public short from_bonus;
    public short secretlevel;
    public boolean respawn_monsters, respawn_items, respawn_inventory;
    public int eog;
    public boolean god;
    public int auto_run, crosshair;
    public boolean monsters_off;
    public int last_level, coop, marker, ffire;

    public final short[] rorsector = new short[16];
    public final byte[] rortype = new byte[16];
    public int rorcnt;

    public Sector[] sector;
    public Wall[] wall;
    public List<Sprite> sprite;

    private String message;

    public SafeLoader() {
        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i] = new PlayerStruct();
            po[i] = new PlayerOrig();
        }

        for (int i = 0; i < MAXANIMATES; i++) {
            gAnimationData[i] = new ANIMATION();
        }

        for (int i = 0; i < MAXANIMWALLS; i++) {
            animwall[i] = new Animwalltype();
        }
    }

    public boolean load(InputStream is) {
        message = null;

        hittype.clear();
        actorscrptr.clear();
        actortype.clear();

        try {
            addon = LoadGDXHeader(is);
            MapLoad(is);
            StuffLoad(is);
            ConLoad(is);
            AnimationLoad(is);
            GameInfoLoad(is);

            if (warp_on == 1 && addon == null) { // try to find addon
                message = "Can't find user episode file: " + addonFileName;
                warp_on = 2;

                volume_number = 0;
                level_number = 7;
            }

            if (is.available() == 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void AnimationLoad(InputStream is) throws IOException {
        for (int i = 0; i < MAXANIMATES; i++) {
            short index = StreamUtils.readShort(is);
            byte type = StreamUtils.readByte(is);
            gAnimationData[i].id = index;
            gAnimationData[i].type = type;
            gAnimationData[i].ptr = null;
            gAnimationData[i].goal = StreamUtils.readInt(is);
            gAnimationData[i].vel = StreamUtils.readInt(is);
            gAnimationData[i].sect = StreamUtils.readShort(is);
        }
        gAnimationCount = StreamUtils.readInt(is);
    }

    public void ConLoad(InputStream is) throws IOException {
        int len = StreamUtils.readInt(is);
        for (int i = 0; i < len; i++) {
            actortype.add((short) (StreamUtils.readUnsignedByte(is)));
        }

        len = StreamUtils.readInt(is);
        for (int i = 0; i < len; i++) {
            script[i] = StreamUtils.readInt(is);
        }

        len = StreamUtils.readInt(is);
        for (int i = 0; i < len; i++) {
            actorscrptr.add(StreamUtils.readInt(is));
        }

        len = StreamUtils.readInt(is);
        for (int i = 0; i < len; i++) {
            Weaponhit weaponhit = new Weaponhit();
            weaponhit.readObject(is);
            hittype.add(weaponhit);
        }
    }

    public void GameInfoLoad(InputStream is) throws IOException {
        pskybits = StreamUtils.readShort(is);
        parallaxyscale = StreamUtils.readInt(is);
        for (int i = 0; i < MAXPSKYTILES; i++) {
            pskyoff[i] = StreamUtils.readShort(is);
        }

        earthquaketime = StreamUtils.readShort(is);
        from_bonus = StreamUtils.readShort(is);
        secretlevel = StreamUtils.readShort(is);
        respawn_monsters = StreamUtils.readBoolean(is);
        respawn_items = StreamUtils.readBoolean(is);
        respawn_inventory = StreamUtils.readBoolean(is);
        god = StreamUtils.readBoolean(is);
        auto_run = (StreamUtils.readInt(is) == 1) ? 1 : 0;
        crosshair = (StreamUtils.readInt(is) == 1) ? 1 : 0;
        monsters_off = StreamUtils.readBoolean(is);
        last_level = StreamUtils.readInt(is);
        eog = StreamUtils.readInt(is);
        coop = StreamUtils.readInt(is);
        marker = StreamUtils.readInt(is);
        ffire = StreamUtils.readInt(is);
        camsprite = StreamUtils.readShort(is);

        connecthead = StreamUtils.readShort(is);
        for (int i = 0; i < MAXPLAYERS; i++) {
            connectpoint2[i] = StreamUtils.readShort(is);
        }
        numplayersprites = StreamUtils.readShort(is);

        for (int i = 0; i < MAXPLAYERS; i++) {
            for (int j = 0; j < MAXPLAYERS; j++) {
                frags[i][j] = StreamUtils.readShort(is);
            }
        }

        randomseed = StreamUtils.readInt(is);
        global_random = StreamUtils.readShort(is);
    }

    public void StuffLoad(InputStream is) throws IOException {
        numcyclers = StreamUtils.readShort(is);
        for (int i = 0; i < MAXCYCLERS; i++) {
            for (int j = 0; j < 6; j++) {
                cyclers[i][j] = StreamUtils.readShort(is);
            }
        }

        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i].readObject(is);
        }
        for (int i = 0; i < MAXPLAYERS; i++) {
            po[i].readObject(is);
        }

        numanimwalls = StreamUtils.readShort(is);
        for (int i = 0; i < MAXANIMWALLS; i++) {
            animwall[i].wallnum = StreamUtils.readShort(is);
            animwall[i].tag = StreamUtils.readInt(is);
        }
        for (int i = 0; i < 2048; i++) {
            msx[i] = StreamUtils.readInt(is);
        }
        for (int i = 0; i < 2048; i++) {
            msy[i] = StreamUtils.readInt(is);
        }

        spriteqloc = StreamUtils.readShort(is);
        spriteqamount = StreamUtils.readShort(is);
        for (int i = 0; i < 1024; i++) {
            spriteq[i] = StreamUtils.readShort(is);
        }

        mirrorcnt = StreamUtils.readShort(is);
        for (int i = 0; i < 64; i++) {
            mirrorwall[i] = StreamUtils.readShort(is);
        }
        for (int i = 0; i < 64; i++) {
            mirrorsector[i] = StreamUtils.readShort(is);
        }

        show2dsector.readObject(is);
        shadeEffect.readObject(is);

        numjaildoors = StreamUtils.readInt(is);
        for (int i = 0; i < MAXJAILDOORS; i++) {
            jailspeed[i] = StreamUtils.readInt(is);
            jaildistance[i] = StreamUtils.readInt(is);
            jailsect[i] = StreamUtils.readShort(is);
            jaildirection[i] = StreamUtils.readShort(is);
            jailunique[i] = StreamUtils.readShort(is);
            jailsound[i] = StreamUtils.readShort(is);
            jailstatus[i] = StreamUtils.readShort(is);
            jailcount2[i] = StreamUtils.readInt(is);
        }

        numminecart = StreamUtils.readInt(is);
        for (int i = 0; i < MAXMINECARDS; i++) {
            minespeed[i] = StreamUtils.readInt(is);
            minefulldist[i] = StreamUtils.readInt(is);
            minedistance[i] = StreamUtils.readInt(is);
            minechild[i] = StreamUtils.readShort(is);
            mineparent[i] = StreamUtils.readShort(is);
            minedirection[i] = StreamUtils.readShort(is);
            minesound[i] = StreamUtils.readShort(is);
            minestatus[i] = StreamUtils.readShort(is);
        }

        numtorcheffects = StreamUtils.readInt(is);
        for (int i = 0; i < MAXTORCHES; i++) {
            torchsector[i] = StreamUtils.readShort(is);
            torchshade[i] = StreamUtils.readByte(is);
            torchflags[i] = StreamUtils.readShort(is);
        }

        numlightnineffects = StreamUtils.readInt(is);
        for (int i = 0; i < MAXLIGHTNINS; i++) {
            lightninsector[i] = StreamUtils.readShort(is);
            lightninshade[i] = StreamUtils.readShort(is);
        }

        numambients = StreamUtils.readInt(is);
        for (int i = 0; i < MAXAMBIENTS; i++) {
            ambienttype[i] = StreamUtils.readShort(is);
            ambientid[i] = StreamUtils.readShort(is);
            ambienthitag[i] = StreamUtils.readShort(is);
        }

        numgeomeffects = StreamUtils.readInt(is);
        for (int i = 0; i < MAXGEOMETRY; i++) {
            geomsector[i] = StreamUtils.readShort(is);
            geoms1[i] = StreamUtils.readShort(is);
            geomx1[i] = StreamUtils.readInt(is);
            geomy1[i] = StreamUtils.readInt(is);
            geomz1[i] = StreamUtils.readInt(is);

            geoms2[i] = StreamUtils.readShort(is);
            geomx2[i] = StreamUtils.readInt(is);
            geomy2[i] = StreamUtils.readInt(is);
            geomz2[i] = StreamUtils.readInt(is);
        }

        UFO_SpawnCount = StreamUtils.readShort(is);
        UFO_SpawnTime = StreamUtils.readShort(is);
        UFO_SpawnHulk = StreamUtils.readShort(is);

        gEndFirstEpisode = StreamUtils.readShort(is);
        gEndGame = StreamUtils.readShort(is);

        plantProcess = StreamUtils.readBoolean(is);


        BellTime = StreamUtils.readShort(is);
        BellSound = StreamUtils.readInt(is);
        word_119BE0 = StreamUtils.readShort(is);
        WindDir = StreamUtils.readInt(is);
        WindTime = StreamUtils.readInt(is);
        mamaspawn_count = StreamUtils.readInt(is);
        fakebubba_spawn = StreamUtils.readInt(is);
        dword_119C08 = StreamUtils.readInt(is);
    }

    public void MapLoad(InputStream is) throws Exception {
        boardfilename = DUMMY_ENTRY;
        String name = StreamUtils.readString(is, 144);
        if (!name.isEmpty()) {
            boardfilename = game.getCache().getEntry(name, true);
        }

        sector = new Sector[StreamUtils.readInt(is)];
        for (int i = 0; i < sector.length; i++) {
            sector[i] = new Sector().readObject(is);
        }

        wall = new Wall[StreamUtils.readInt(is)];
        for (int i = 0; i < wall.length; i++) {
            wall[i] = new Wall().readObject(is);
        }

        int numSprites = StreamUtils.readInt(is);
        sprite = new ArrayList<>(numSprites * 2);

        for (int i = 0; i < numSprites; i++) {
            sprite.add(new Sprite().readObject(is));
        }

        rorcnt = StreamUtils.readInt(is);
        for (int i = 0; i < 16; i++) {
            rorsector[i] = StreamUtils.readShort(is);
            rortype[i] = StreamUtils.readByte(is);
        }
    }

    public String getMessage() {
        return message;
    }

    public GameInfo LoadGDXHeader(InputStream is) throws IOException {
        volume_number = -1;
        level_number = -1;
        player_skill = -1;
        warp_on = 0;
        addon = null;
        addonFileName = null;
        addonPackedConName = null;

        StreamUtils.skip(is, SAVETIME + SAVENAME);

        multimode = StreamUtils.readInt(is);
        volume_number = StreamUtils.readInt(is);
        level_number = StreamUtils.readInt(is);
        player_skill = StreamUtils.readInt(is);

        StreamUtils.skip(is, SAVESCREENSHOTSIZE);

        LoadGDXBlock(is);
        if (warp_on == 1) { // try to find addon
            addon = findAddon(addonFileName, addonPackedConName);
        }

        return addon;
    }

    public void LoadGDXBlock(InputStream is) throws IOException {
        warp_on = StreamUtils.readByte(is);
        if (warp_on == 1) {
            boolean isPacked = StreamUtils.readBoolean(is);
            addonFileName = StreamUtils.readDataString(is).toLowerCase();
            if (isPacked) {
                addonPackedConName = StreamUtils.readDataString(is).toLowerCase();
            }
        }
    }

    public static GameInfo findAddon(String addonFileName, String conName) {
        try {
            FileEntry addonEntry = game.getCache().getGameDirectory().getEntry(FileUtils.getPath(addonFileName));
            if (addonEntry.exists()) {
                if (conName == null) {
                    conName = addonEntry.getName();
                }

                String finalIniName = conName;
                return episodeManager.getEpisodeEntries(addonEntry)
                        .stream()
                        .filter(e -> e.getConFile().getName().equalsIgnoreCase(finalIniName))
                        .map(episodeManager::getEpisode)
                        .findAny().orElse(null);
            }
        } catch (Exception e) {
            Console.out.println(e.toString(), OsdColor.RED);
        }
        return null;
    }
}
