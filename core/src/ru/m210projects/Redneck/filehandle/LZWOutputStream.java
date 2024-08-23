// This file is part of DukeGDX.
//
// "Build Engine & Tools" Copyright (c) 1993-1997 Ken Silverman
// Ken Silverman's official web site: "http://www.advsys.net/ken"
// See the included license file "BUILDLIC.TXT" for license info.
//
// Copyright (C) 2023  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.filehandle.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LZWOutputStream extends OutputStream {

    private static final int LZWSIZE = 16385;
    private final byte[] lzwbuf1;
    private final byte[] lzwbuf4;
    private final byte[] lzwbuf5;
    private final short[] lzwbuf2;
    private final short[] lzwbuf3;
    private final byte[] blockBuffer;
    private final OutputStream os;
    private int blockPos;
    private int pos;

    public LZWOutputStream(OutputStream os, int blockSize) {
        this.os = os;
        this.blockBuffer = new byte[blockSize];
        this.blockPos = 0;

        lzwbuf1 = new byte[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf2 = new short[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf3 = new short[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf4 = new byte[LZWSIZE];
        lzwbuf5 = new byte[LZWSIZE + (LZWSIZE >> 4)];
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        Arrays.fill(blockBuffer, (byte) 0);
    }

    private void flushBuffer() throws IOException {
        if (pos > 0) {
            int len = lzwcompress(lzwbuf4, pos, lzwbuf5);
            StreamUtils.writeShort(os, len);
            StreamUtils.writeBytes(os, lzwbuf5, len);
            pos = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        lzwbuf4[blockPos + pos] = (byte) (b - blockBuffer[blockPos]);
        blockBuffer[blockPos++] = (byte) b;
        if (blockPos >= blockBuffer.length) {
            pos += blockBuffer.length;
            blockPos = 0;
        }

        if (pos > LZWSIZE - blockBuffer.length) {
            flushBuffer();
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        os.close();
    }

    private int lzwcompress(byte[] lzwinbuf, int uncompleng, byte[] lzwoutbuf) {
        for (int i = 255; i >= 0; i--) {
            lzwbuf1[i] = (byte) i;
            lzwbuf3[i] = (short) ((i + 1) & 255);
        }
        Arrays.fill(lzwbuf2, 0, 256, (short) -1);
        Arrays.fill(lzwoutbuf, 0, ((uncompleng + 15) + 3), (byte) 0);

        ByteBuffer outbuf = ByteBuffer.wrap(lzwoutbuf);
        outbuf.order(ByteOrder.LITTLE_ENDIAN);

        short addrcnt = 256;
        int bytecnt1 = 0;
        int bitcnt = (4 << 3);
        int numbits = 8;
        int oneupnumbits = (1 << 8);
        short addr;
        do {
            addr = (short) (lzwinbuf[bytecnt1] & 0xFF);
            do {
                bytecnt1++;
                if (bytecnt1 == uncompleng) {
                    break;
                }

                if (lzwbuf2[addr] < 0) {
                    lzwbuf2[addr] = addrcnt;
                    break;
                }

                short newaddr = lzwbuf2[addr];
                while (lzwbuf1[newaddr] != lzwinbuf[bytecnt1]) {
                    short zx = lzwbuf3[newaddr];
                    if (zx < 0) {
                        lzwbuf3[newaddr] = addrcnt;
                        break;
                    }
                    newaddr = zx;
                }

                if (lzwbuf3[newaddr] == addrcnt) {
                    break;
                }

                addr = newaddr;
            } while (true); //addr >= 0);
            lzwbuf1[addrcnt] = lzwinbuf[bytecnt1];
            lzwbuf2[addrcnt] = -1;
            lzwbuf3[addrcnt] = -1;

            int intptr = outbuf.getInt(bitcnt >> 3);
            outbuf.putInt(bitcnt >> 3, intptr | (addr << (bitcnt & 7)));

            bitcnt += numbits;
            if ((addr & ((oneupnumbits >> 1) - 1)) > ((addrcnt - 1) & ((oneupnumbits >> 1) - 1))) {
                bitcnt--;
            }

            addrcnt++;
            if (addrcnt > oneupnumbits) {
                numbits++;
                oneupnumbits <<= 1;
            }
        } while ((bytecnt1 < uncompleng) && (bitcnt < (uncompleng << 3)));

        int intptr = outbuf.getInt(bitcnt >> 3);
        outbuf.putInt(bitcnt >> 3, intptr | (addr << (bitcnt & 7)));

        bitcnt += numbits;
        if ((addr & ((oneupnumbits >> 1) - 1)) > ((addrcnt - 1) & ((oneupnumbits >> 1) - 1))) {
            bitcnt--;
        }

        outbuf.putShort(0, (short) uncompleng);
        if (((bitcnt + 7) >> 3) < uncompleng) {
            outbuf.putShort(2, addrcnt);
            return ((bitcnt + 7) >> 3);
        }

        outbuf.putShort(2, (short) 0);
        for (int i = 0; i < uncompleng; i++) {
            outbuf.put(i + 4, lzwinbuf[i]);
        }

        return (uncompleng + 4);
    }
}
