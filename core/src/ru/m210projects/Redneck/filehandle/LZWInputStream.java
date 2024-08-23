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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LZWInputStream extends InputStream {

    private static final int LZWSIZE = 16385;
    private final byte[] lzwbuf1;
    private final byte[] lzwbuf4;
    private final byte[] lzwbuf5;
    private final short[] lzwbuf2;
    private final short[] lzwbuf3;
    private final byte[] blockBuffer;
    private int blockPos;
    private final InputStream in;
    private int pos;
    private int count;

    public LZWInputStream(InputStream in, int blockSize) {
        this.in = in;
        this.blockBuffer = new byte[blockSize];
        this.blockPos = 0;

        lzwbuf1 = new byte[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf2 = new short[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf3 = new short[LZWSIZE + (LZWSIZE >> 4)];
        lzwbuf4 = new byte[LZWSIZE];
        lzwbuf5 = new byte[LZWSIZE + (LZWSIZE >> 4)];
    }

    protected void fill() throws IOException {
        int len = StreamUtils.readShort(in);
        StreamUtils.readBytes(in, lzwbuf5, len);

        if (pos <= (LZWSIZE - blockBuffer.length)) {
            flush();
        }

        count = lzwuncompress(lzwbuf5, len, lzwbuf4);
        pos = 0;
    }

    public void flush() {
        Arrays.fill(blockBuffer, (byte) 0);
    }

    @Override
    public synchronized int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count)
                return -1;
        }

        blockBuffer[blockPos] += lzwbuf4[blockPos + pos];
        int result = blockBuffer[blockPos++] & 0xFF;
        if (blockPos >= blockBuffer.length) {
            pos += blockBuffer.length;
            blockPos = 0;
        }
        return result;
    }

    private int lzwuncompress(byte[] lzwinbuf, int compleng, byte[] lzwoutbuf) {
        ByteBuffer inbuf = ByteBuffer.wrap(lzwinbuf);
        inbuf.order(ByteOrder.LITTLE_ENDIAN);

        int strtot = inbuf.getShort(2);

        inbuf.position(4);
        if (strtot == 0) {
            inbuf.get(lzwoutbuf, 0, (compleng - 4) + 3);
            return inbuf.getShort(0); // uncompleng
        }

        for (int i = 255; i >= 0; i--) {
            lzwbuf2[i] = (short) i;
            lzwbuf3[i] = (short) i;
        }
        int currstr = 256, bitcnt = (4 << 3), outbytecnt = 0;
        int numbits = 8, oneupnumbits = (1 << 8), intptr, dat, leng;
        do {
            intptr = inbuf.getInt(bitcnt >> 3);
            dat = ((intptr >> (bitcnt & 7)) & (oneupnumbits - 1));
            bitcnt += numbits;
            if ((dat & ((oneupnumbits >> 1) - 1)) > ((currstr - 1) & ((oneupnumbits >> 1) - 1))) {
                dat &= ((oneupnumbits >> 1) - 1);
                bitcnt--;
            }

            lzwbuf3[currstr] = (short) dat;

            for (leng = 0; dat >= 256; leng++, dat = lzwbuf3[dat])
                lzwbuf1[leng] = (byte) lzwbuf2[dat];

            lzwoutbuf[outbytecnt++] = (byte) dat;
            for (int i = leng - 1; i >= 0; i--)
                lzwoutbuf[outbytecnt++] = lzwbuf1[i];

            lzwbuf2[currstr - 1] = (short) dat;
            lzwbuf2[currstr] = (short) dat;
            currstr++;
            if (currstr > oneupnumbits) {
                numbits++;
                oneupnumbits <<= 1;
            }
        } while (currstr < strtot);
        return (inbuf.getShort(0)); // uncompleng
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
