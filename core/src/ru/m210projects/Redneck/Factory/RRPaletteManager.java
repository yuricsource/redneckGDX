package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Types.Color;
import ru.m210projects.Build.Types.DefaultPaletteLoader;
import ru.m210projects.Build.Types.DefaultPaletteManager;
import ru.m210projects.Build.Types.PaletteManager;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Redneck.Globals.ANIM_PAL;
import static ru.m210projects.Redneck.Globals.tempbuf;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Player.*;

public class RRPaletteManager extends DefaultPaletteManager {

    private boolean fogInited = false;
    private final byte[][] opalookup = new byte[5][];
    private final Color[] opalookupfog = new Color[5];


    public RRPaletteManager(Engine engine, Entry entry) throws IOException {
        super(engine, new DefaultPaletteLoader(entry));
        genspriteremaps();
    }

    private void genspriteremaps() throws IOException {
        Entry entry = game.getCache().getEntry("lookup.dat", true);
        if (!entry.exists()) {
            throw new FileNotFoundException("\nERROR: File 'LOOKUP.DAT' not found.");
        }

        try (InputStream is = entry.getInputStream()) {
            int numl = StreamUtils.readUnsignedByte(is);

            for (int j = 0; j < numl; j++) {
                int look_pos = StreamUtils.readUnsignedByte(is);
                StreamUtils.readBytes(is, tempbuf, 256);
                makePalookup(look_pos, tempbuf, 0, 0, 0, 1);
                if (look_pos == 8)
                    makePalookup(54, tempbuf, 32, 32, 32, 1);
            }

            StreamUtils.readBytes(is, waterpal);
            StreamUtils.readBytes(is, slimepal);
            StreamUtils.readBytes(is, titlepal);
            StreamUtils.readBytes(is, drealms);
            StreamUtils.readBytes(is, endingpal);

            basePalette[765] = basePalette[766] = basePalette[767] = 0;
            slimepal[765] = slimepal[766] = slimepal[767] = 0;
            waterpal[765] = waterpal[766] = waterpal[767] = 0;
        }
        System.arraycopy(basePalette, 1, drugpal, 0, 767);

        for (int i = 0; i < 768; i++) {
            tempbuf[i] = (byte) i;
        }
        for (int i = 0; i < 32; i++) {
            tempbuf[i] = (byte) (i + 32);
        }

        makePalookup(7, tempbuf, 0, 0, 0, 1);
        for (int i = 0; i < 768; i++) {
            tempbuf[i] = (byte) i;
        }

        makePalookup(30, tempbuf, 0, 0, 0, 1);
        makePalookup(31, tempbuf, 0, 0, 0, 1);
        makePalookup(32, tempbuf, 0, 0, 0, 1);
        makePalookup(33, tempbuf, 0, 0, 0, 1);
        makePalookup(105, tempbuf, 0, 0, 0, 1);

        int col = 63;
        for (int i = 64; i < 80; i++) {
            tempbuf[i] = (byte) (--col);
            tempbuf[i + 16] = (byte) (i - 24);
        }
        for (int i = 0; i < 32; i++) {
            tempbuf[i] = (byte) (i + 32);
        }
        makePalookup(34, tempbuf, 0, 0, 0, 1);

        for (int i = 0; i < 768; i++) {
            tempbuf[i] = (byte) i;
        }
        for (int i = 0; i < 16; i++) {
            tempbuf[i] = (byte) (i - 127);
        }
        for (int i = 16; i < 32; i++) {
            tempbuf[i] = (byte) (i - 64);
        }
        makePalookup(35, tempbuf, 0, 0, 0, 1);

        palookup[ANIM_PAL] = new byte[shadesCount << 8];
        int len = Math.min(palookup[ANIM_PAL].length, MAXPALOOKUPS);
        for (int i = 0; i < len; i++) {
            palookup[ANIM_PAL][i] = (byte) i;
        }
    }

    public void applyfog(int type) {
        PaletteManager paletteManager = engine.getPaletteManager();
        if (!fogInited) {
            opalookup[0] = palookup[0];
            opalookup[1] = palookup[8];
            opalookup[2] = palookup[23];
            opalookup[3] = palookup[30];
            opalookup[4] = palookup[33];

            opalookupfog[0] = palookupfog[0];
            opalookupfog[1] = palookupfog[8];
            opalookupfog[2] = palookupfog[23];
            opalookupfog[3] = palookupfog[30];
            opalookupfog[4] = palookupfog[33];

            for (int i = 0; i < 256; i++) {
                tempbuf[i] = (byte) i;
            }
            paletteManager.makePalookup(50, tempbuf, 12, 12, 12, 1);
            paletteManager.makePalookup(51, tempbuf, 12, 12, 12, 1);

            fogInited = true;
        }

        if (type == 2) {
            palookup[0] = palookup[50];
            palookup[30] = palookup[51];
            palookup[33] = palookup[51];
            palookup[23] = palookup[51];
            palookup[8] = palookup[54];

            palookupfog[0] = palookupfog[50];
            palookupfog[30] = palookupfog[51];
            palookupfog[33] = palookupfog[51];
            palookupfog[23] = palookupfog[51];
            palookupfog[8] = palookupfog[54];
        }

        if (type == 0) {
            palookup[0] = opalookup[0];
            palookup[30] = opalookup[3];
            palookup[33] = opalookup[4];
            palookup[23] = opalookup[2];
            palookup[8] = opalookup[1];

            palookupfog[0] = opalookupfog[0];
            palookupfog[30] = opalookupfog[3];
            palookupfog[33] = opalookupfog[4];
            palookupfog[23] = opalookupfog[2];
            palookupfog[8] = opalookupfog[1];
        }

        listener.onPalookupChanged(0);
        listener.onPalookupChanged(30);
        listener.onPalookupChanged(33);
        listener.onPalookupChanged(23);
        listener.onPalookupChanged(8);
    }
}
