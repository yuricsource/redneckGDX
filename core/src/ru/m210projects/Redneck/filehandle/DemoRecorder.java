package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.filehandle.fs.Directory;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Premap;
import ru.m210projects.Redneck.Types.PlayerStruct;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.WRITE;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.net.Mmulti.connecthead;
import static ru.m210projects.Build.net.Mmulti.connectpoint2;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;

public class DemoRecorder {

    private final OutputStream os;
    private final Path filepath;
    public int reccnt;
    public int totalreccnt;
    public int recversion;

    public DemoRecorder(FileOutputStream os, Path filepath, int nVersion) throws IOException {
        this.filepath = filepath;
        StreamUtils.writeInt(os, 0);
        StreamUtils.writeByte(os, nVersion);

        StreamUtils.writeByte(os, ud.volume_number);
        StreamUtils.writeByte(os, ud.level_number);
        StreamUtils.writeByte(os, ud.player_skill);
        StreamUtils.writeByte(os, ud.coop);
        StreamUtils.writeByte(os, ud.ffire);
        StreamUtils.writeShort(os, ud.multimode);
        StreamUtils.writeShort(os, ud.monsters_off ? 1 : 0);
        StreamUtils.writeInt(os, ud.respawn_monsters ? 1 : 0);
        StreamUtils.writeInt(os, ud.respawn_items ? 1 : 0);
        StreamUtils.writeInt(os, ud.respawn_inventory ? 1 : 0);
        StreamUtils.writeInt(os, ud.playerai);

        for (int i = 0; i < MAXPLAYERS; i++) {
            StreamUtils.writeString(os, ud.user_name[i], 32);
        }

        if (nVersion >= GDXBYTEVERSION) {
            if (mUserFlag == Main.UserFlag.Addon && currentGame != null) {
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

            for (int i = 0; i < ud.multimode; i++) {
                Premap.PlayerInfo info = new Premap.PlayerInfo();
                info.set(ps[i]);
                info.writeObject(os);
            }
        }

        for (int i = 0; i < ud.multimode; i++) {
            StreamUtils.writeByte(os, ps[i].aim_mode);
            if (nVersion >= GDXBYTEVERSION) // JBF 20031126
            {
                StreamUtils.writeByte(os, ps[i].auto_aim);
            }
        }

        totalreccnt = 0;
        reccnt = 0;
        recversion = nVersion;
        this.os = new LZWOutputStream(new BufferedOutputStream(os, RECSYNCBUFSIZ * Input.sizeof(BYTEVERSION)), Input.sizeof(BYTEVERSION));
    }

    public void record() {
        for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
            try {
                sync[i].writeObject(os, recversion);
                reccnt++;
                if (reccnt >= RECSYNCBUFSIZ) {
                    os.flush();
                    reccnt = 0;
                }
                totalreccnt++;
            } catch (Exception e) {
                Console.out.println(e.toString(), OsdColor.RED);
                close();
            }
        }
    }

    public void close() {
        try {
            os.close();
            try (OutputStream out = Files.newOutputStream(filepath, WRITE)) {
                StreamUtils.writeInt(out, totalreccnt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Console.out.println("Stop recording");
        Directory dir = game.getCache().getGameDirectory();

        Entry entry = dir.addEntry(filepath);
        if (entry.exists()) {
            List<Entry> demos = gDemoScreen.demofiles.computeIfAbsent(dir, e -> new ArrayList<>());
            demos.add(entry);
        }
    }
}
