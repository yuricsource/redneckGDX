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

package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Premap;
import ru.m210projects.Redneck.Types.GameInfo;

import java.io.InputStream;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Types.SafeLoader.findAddon;

public class DemoFile {

    public int rcnt;
    public Input[][] recsync;
    public int reccnt;
    public int version;
    public int volume_number, level_number, player_skill;
    public int coop;
    public int ffire;
    public int multimode;
    public boolean monsters_off;
    public boolean respawn_monsters;
    public boolean respawn_items;
    public boolean respawn_inventory;
    public int playerai;
    public final String[] user_name = new String[MAXPLAYERS];
    public String boardfilename;
    public final int[] aim_mode = new int[MAXPLAYERS];
    public final int[] auto_aim = new int[MAXPLAYERS];

    public Premap.PlayerInfo[] playerInfos = new Premap.PlayerInfo[MAXPLAYERS];
    public GameInfo addon;

    public DemoFile(InputStream is) throws Exception {
        rcnt = 0;

        reccnt = StreamUtils.readInt(is);
        version = StreamUtils.readUnsignedByte(is);

        if (version != BYTEVERSIONRR && version != GDXBYTEVERSION) {
            throw new Exception("Wrong version!");
        }

        if (reccnt == 0) {
            throw new Exception("reccnt == 0");
        }

        volume_number = StreamUtils.readUnsignedByte(is);
        level_number = StreamUtils.readUnsignedByte(is);
        player_skill = StreamUtils.readUnsignedByte(is);

        coop = StreamUtils.readUnsignedByte(is);
        ffire = StreamUtils.readUnsignedByte(is);
        multimode = StreamUtils.readShort(is);
        monsters_off = StreamUtils.readShort(is) == 1;
        respawn_monsters = StreamUtils.readInt(is) == 1;
        respawn_items = StreamUtils.readInt(is) == 1;
        respawn_inventory = StreamUtils.readInt(is) == 1;
        playerai = StreamUtils.readInt(is);
        for (int i = 0; i < MAXPLAYERS; i++) {
            user_name[i] = StreamUtils.readString(is, 32);
        }

        if (version == GDXBYTEVERSION) {
            boolean isPacked = StreamUtils.readBoolean(is);
            String addonFileName = StreamUtils.readDataString(is).toLowerCase();
            String addonPackedConName = null;
            if (isPacked) {
                addonPackedConName = StreamUtils.readDataString(is).toLowerCase();
            }
            addon = findAddon(addonFileName, addonPackedConName);

            for (int i = 0; i < multimode; i++) {
                playerInfos[i] = new Premap.PlayerInfo();
                playerInfos[i].readObject(is);
            }
        }

        for (int i = 0; i < multimode; i++) {
            aim_mode[i] = StreamUtils.readUnsignedByte(is);
            if (version >= GDXBYTEVERSION) {
                auto_aim[i] = StreamUtils.readUnsignedByte(is);
            }
        }

        recsync = new Input[reccnt][MAXPLAYERS];
        LZWInputStream lis = new LZWInputStream(is, Input.sizeof(version) * multimode);
        for (int c = 0; c < reccnt; c++) {
            for (int i = 0; i < multimode; i++) {
                recsync[c][i] = new Input().readObject(lis, version);
            }
        }
    }

}
