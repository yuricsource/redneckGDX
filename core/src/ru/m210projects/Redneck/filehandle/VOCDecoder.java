// This file is part of BuildGDX.
// Copyright (C) 2024  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.filehandle;

import ru.m210projects.Build.Architecture.common.audio.SoundData;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VOCDecoder implements SoundData.Decoder {

    @Override
    public SoundData decode(Entry entry) {
        try(InputStream is = entry.getInputStream()) {
            String signature = StreamUtils.readString(is, 20);
            if (!signature.equalsIgnoreCase("Creative Voice File\u001A")) {
                throw new Exception("Wrong signature " + signature);
            }

            StreamUtils.skip(is, 2);
            short version = StreamUtils.readShort(is);
            short checksum = StreamUtils.readShort(is);
            if (~version + 0x1234 != checksum) {
                throw new Exception("Version checksum error!");
            }

            ByteArrayOutputStream pcm = new ByteArrayOutputStream();

            int rate = 0, channels = 1, bits = 8;
            while (is.available() > 4) {
                int block = StreamUtils.readInt(is);
                int blocktype = block & 0xFF;
                if (blocktype == 0) {
                    break;
                }

                int blocklen = block >> 8;
                switch (blocktype) {
                    case 1: /* sound data begin block */
                        rate = 1000000 / (256 - StreamUtils.readUnsignedByte(is));
                        if (StreamUtils.readByte(is) != 0) {
                            /* only 8-bit files please */
                            throw new Exception("Only 8-bit files supported");
                        }

                        channels = 1;
                        bits = 8;
                        pcm.write(StreamUtils.readBytes(is, Math.min(is.available(), blocklen - 2)));
                        break;
                    case 2: /* sound continue */
                        pcm.write(StreamUtils.readBytes(is, Math.min(is.available(), blocklen)));
                        break;
                    case 8: /* sound attribute extension block */
                        rate = StreamUtils.readShort(is);
                        rate = (256000000 / (65536 - (rate & 0xFFFF)));
                        bits = 8;

                        // Codec
                        if (StreamUtils.readByte(is) != 0) {
                            /* only 8-bit files please */
                            throw new Exception("Only 8-bit files supported");
                        }

                        // Channels number
                        if (StreamUtils.readByte(is) == 1) {
                            rate >>= 1;
                            channels = 2;
                        } else {
                            channels = 1;
                        }
                        break;
                    case 9: /* sound data format 3 */
                        rate = StreamUtils.readInt(is);
                        bits = StreamUtils.readUnsignedByte(is);
                        if (bits == 0) {
                            bits = 8;
                        }
                        channels = StreamUtils.readUnsignedByte(is);
                        int codec = StreamUtils.readUnsignedShort(is);
                        if (codec != 0 && codec != 4) {
                            /* only PCM please */
                            throw new Exception("Only PCM files supported");
                        }
                        StreamUtils.skip(is, 4); // Reserved
                        pcm.write(StreamUtils.readBytes(is, Math.min(is.available(), blocklen - 12)));
                        break;
                    default:
                        StreamUtils.skip(is, blocklen);
                        break;
                }
            }

            int samplelength = pcm.size();
            if (bits != 8 && (samplelength % 2) == 1) {
                samplelength++;
            }

            ByteBuffer pcmData = ByteBuffer.allocateDirect(samplelength).order(ByteOrder.LITTLE_ENDIAN);
            pcmData.put(pcm.toByteArray());
            pcmData.rewind();

            return new SoundData(rate, channels, bits, pcmData);

        } catch (Exception e) {
            Console.out.print("VOC loader error: " + e, OsdColor.RED);
        }

        return null;
    }
}
