// A simple parser for Interplay MVE multimedia files
// by Mike Melanson (mike at multimedia.cx)

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

//https://github.com/ubports/oxide_ffmpeg/blob/master/libavcodec/interplayvideo.c

package ru.m210projects.Redneck.Types;

import java.nio.ByteBuffer;
import ru.m210projects.Build.Types.LittleEndian;

public class MVEFile {

	private byte[] signature = ("Interplay MVE File" + (char) 0x1A + (char) 0x00).getBytes();
	private byte[] magic = { 0x1a, 0x00, 0x00, 0x01, 0x33, 0x11 };

	public class FramePacket {

		public int pts;
		public int stream_index;

		public byte frame_format;
		public boolean send_buffer;
		public short video_chunk_size;
		public byte[] video_chunk_data;
		public short decode_map_chunk_size;
		public byte[] decode_map_chunk_data;
		public short skip_map_chunk_size;
		public byte[] skip_map_chunk_data;
		public byte[] audio_chunk_data;

		public byte[] palette;
		public int pos;
	}

	private final int CHUNK_INIT_AUDIO = 0x0000;
	private final int CHUNK_AUDIO_ONLY = 0x0001;
	private final int CHUNK_INIT_VIDEO = 0x0002;
	private final int CHUNK_VIDEO = 0x0003;
	private final int CHUNK_SHUTDOWN = 0x0004;
	private final int CHUNK_END = 0x0005;
	private final int CHUNK_DONE = 0xFFFC;
	private final int CHUNK_EOF = 0xFFFE;
	private final int CHUNK_BAD = 0xFFFF;

	private final int OPCODE_END_OF_STREAM = 0x00;
	private final int OPCODE_END_OF_CHUNK = 0x01;
	private final int OPCODE_CREATE_TIMER = 0x02;
	private final int OPCODE_INIT_AUDIO_BUFFERS = 0x03;
	private final int OPCODE_START_STOP_AUDIO = 0x04;
	private final int OPCODE_INIT_VIDEO_BUFFERS = 0x05;
	private final int OPCODE_VIDEO_DATA_06 = 0x06;
	private final int OPCODE_SEND_BUFFER = 0x07;
	private final int OPCODE_AUDIO_FRAME = 0x08;
	private final int OPCODE_SILENCE_FRAME = 0x09;
	private final int OPCODE_INIT_VIDEO_MODE = 0x0A;
	private final int OPCODE_CREATE_GRADIENT = 0x0B;
	private final int OPCODE_SET_PALETTE = 0x0C;
	private final int OPCODE_SET_PALETTE_COMPRESSED = 0x0D;
	private final int OPCODE_SET_SKIP_MAP = 0x0E;
	private final int OPCODE_SET_DECODING_MAP = 0x0F;
	private final int OPCODE_VIDEO_DATA_10 = 0x10;
	private final int OPCODE_VIDEO_DATA_11 = 0x11;
	private final int OPCODE_UNKNOWN_12 = 0x12;
	private final int OPCODE_UNKNOWN_13 = 0x13;
	private final int OPCODE_UNKNOWN_14 = 0x14;
	private final int OPCODE_UNKNOWN_15 = 0x15;

	private final int AV_CODEC_ID_NONE = -1;
	private final int AV_CODEC_ID_INTERPLAY_DPCM = 1;
	private final int AV_CODEC_ID_PCM_S16LE = 2;
	private final int AV_CODEC_ID_PCM_U8 = 3;

	//For each chunk of DPCM data in an Interplay MVE file, the first 2 bytes comprise an initial predictor stored in a signed, 16-bit, little-endian format.
	//The remainder of the bytes are indices into the delta table. For each byte, fetch a signed delta and apply it to the appropriate predictor
	//Saturate the predictor to a signed 16-bit range after each delta is applied.
	private int interplay_dpcm_delta_table[] = { 
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
		20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 47, 51, 56,
		61, 66, 72, 79, 86, 94, 102, 112, 122, 133, 145, 158, 173, 189, 206, 225, 245, 267, 292, 318, 348, 379, 414,
		452, 493, 538, 587, 640, 699, 763, 832, 908, 991, 1081, 1180, 1288, 1405, 1534, 1673, 1826, 1993, 2175,
		2373, 2590, 2826, 3084, 3365, 3672, 4008, 4373, 4772, 5208, 5683, 6202, 6767, 7385, 8059, 8794, 9597, 10472,
		11428, 12471, 13609, 14851, 16206, 17685, 19298, 21060, 22981, 25078, 27367, 29864, 32589, -29973, -26728,
		-23186, -19322, -15105, -10503, -5481, -1, 1, 1, 5481, 10503, 15105, 19322, 23186, 26728, 29973, -32589,
		-29864, -27367, -25078, -22981, -21060, -19298, -17685, -16206, -14851, -13609, -12471, -11428, -10472,
		-9597, -8794, -8059, -7385, -6767, -6202, -5683, -5208, -4772, -4373, -4008, -3672, -3365, -3084, -2826,
		-2590, -2373, -2175, -1993, -1826, -1673, -1534, -1405, -1288, -1180, -1081, -991, -908, -832, -763, -699,
		-640, -587, -538, -493, -452, -414, -379, -348, -318, -292, -267, -245, -225, -206, -189, -173, -158, -145,
		-133, -122, -112, -102, -94, -86, -79, -72, -66, -61, -56, -51, -47, -43, -42, -41, -40, -39, -38, -37, -36,
		-35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15,
		-14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1 };

	private int audio_type;
	private int audio_sample_rate;
	private int audio_flags;
	private int audio_channels;
	private int audio_bits;
	private int audio_frame_count;

	private int width, height;
	private int frame_pts_inc;
	private int video_bpp;
	private byte frame_format;
	private byte[] palette = new byte[768];
	private boolean hasPalette;
	private boolean send_buffer;

	private int audio_chunk_offset;
	private int audio_chunk_size;
	private int skip_map_chunk_offset;
	private short skip_map_chunk_size;
	private int decode_map_chunk_offset;
	private short decode_map_chunk_size;
	private int video_chunk_offset;
	private short video_chunk_size;
	private int next_chunk_offset;

	private FramePacket pkt = new FramePacket();
	private int video_pts;

	public MVEFile(ByteBuffer bb) {
		byte[] data = new byte[20];
		bb.get(data);
		for (int i = 0; i < 20; i++)
			if (data[i] != signature[i])
				return;
		bb.get(data, 0, 6);
		for (int i = 0; i < 6; i++)
			if (data[i] != magic[i])
				return;

		next_chunk_offset = bb.position();
		/* process the first chunk which should be CHUNK_INIT_VIDEO */
		if (process_chunk(bb) != CHUNK_INIT_VIDEO)
			return; // AVERROR_INVALIDDATA

		/*
		 * peek ahead to the next chunk-- if it is an init audio chunk, process it; if
		 * it is the first video chunk, this is a silent file
		 */
		int chunk_type = bb.getShort(bb.position() + 2);
		if (chunk_type == CHUNK_VIDEO) {
			audio_type = 0; /* no audio */
		} else if (process_chunk(bb) != CHUNK_INIT_AUDIO)
			return; // AVERROR_INVALIDDATA;
		
		for(int i = 0; i < 100; i++) {
			process_chunk(bb);
			
			if(pkt.decode_map_chunk_size != 0)
			{
				System.err.println(pkt.stream_index);
				
			}
		}
	}

	private int process_chunk(ByteBuffer bb) {
		/* see if there are any pending packets */
		int chunk_type = load_ipmovie_packet(bb);
		if (chunk_type != CHUNK_DONE)
			return chunk_type;

		int chunk_size = bb.getShort();
		chunk_type = bb.getShort();

		switch (chunk_type) {
		case CHUNK_INIT_AUDIO:
			System.err.println("initialize audio");
			break;
		case CHUNK_AUDIO_ONLY:
			System.err.println("audio only");
			break;
		case CHUNK_INIT_VIDEO:
			System.err.println("initialize video");
			break;
		case CHUNK_VIDEO:
			System.err.println("video (and audio)");
			break;
		case CHUNK_SHUTDOWN:
			System.err.println("shutdown");
			break;
		case CHUNK_END:
			System.err.println("end");
			break;
		default:
			System.err.println("invalid chunk " + chunk_type);
			chunk_type = CHUNK_BAD;
			break;
		}

		while ((chunk_size > 0) && (chunk_type != CHUNK_BAD)) {
			int opcode_size = bb.getShort();
			int opcode_type = bb.get() & 0xFF;
			int opcode_version = bb.get() & 0xFF;

			chunk_size -= 4;
			chunk_size -= opcode_size;

			switch (opcode_type) {
			case OPCODE_END_OF_STREAM:
//				System.err.println("end of stream");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_END_OF_CHUNK:
//				System.err.println("end of chunk");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_CREATE_TIMER:
//				System.err.println("create timer");
				if ((opcode_version > 0) || (opcode_size != 6)) {
					System.err.println("bad create_timer opcode");
					chunk_type = CHUNK_BAD;
					break;
				}

				frame_pts_inc = bb.getInt(); // rate
				frame_pts_inc *= bb.getShort(); // subdivision
				break;
			case OPCODE_INIT_AUDIO_BUFFERS:
//				System.err.println("initialize audio buffers");
				if (opcode_version > 1 || opcode_size > 10 || opcode_size < 6) {
					System.err.println("bad init_audio_buffers opcode\n");
					chunk_type = CHUNK_BAD;
					break;
				}
				byte[] scratch = new byte[opcode_size];
				bb.get(scratch);

				audio_sample_rate = LittleEndian.getUShort(scratch, 4);
				audio_flags = LittleEndian.getUShort(scratch, 2);
				/* bit 0 of the flags: 0 = mono, 1 = stereo */
				audio_channels = (audio_flags & 1) + 1;
				/* bit 1 of the flags: 0 = 8 bit, 1 = 16 bit */
				audio_bits = (((audio_flags >> 1) & 1) + 1) * 8;
				/* bit 2 indicates compressed audio in version 1 opcode */
				if ((opcode_version == 1) && (audio_flags & 0x4) != 0)
					audio_type = AV_CODEC_ID_INTERPLAY_DPCM;
				else if (audio_bits == 16)
					audio_type = AV_CODEC_ID_PCM_S16LE;
				else
					audio_type = AV_CODEC_ID_PCM_U8;

				System.err.println("audio: " + audio_bits + " bits, " + audio_sample_rate + " Hz, "
						+ ((audio_channels == 2) ? "stereo" : "mono") + ", "
						+ ((audio_type == AV_CODEC_ID_INTERPLAY_DPCM) ? "Interplay audio" : "PCM"));
				break;
			case OPCODE_START_STOP_AUDIO:
//				System.err.println("start/stop audio");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_INIT_VIDEO_BUFFERS:
//				System.err.println("initialize video buffers");
				if ((opcode_version > 2) || (opcode_size > 8) || opcode_size < 4
						|| opcode_version == 2 && opcode_size < 8) {
					System.err.println("bad init_video_buffers opcode");
					chunk_type = CHUNK_BAD;
					break;
				}
				width = bb.getShort() * 8;
				height = bb.getShort() * 8;
				if (opcode_version == 2)
					bb.getShort();
				if (opcode_version < 2 || bb.getShort() == 0) {
					video_bpp = 8;
				} else {
					video_bpp = 16;
				}
				System.err.println("video resolution: " + width + " x " + height);
				break;
			case OPCODE_INIT_VIDEO_MODE:
//				System.err.println("initialize video mode");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SEND_BUFFER:
//				System.err.println("send buffer");
				bb.position(bb.position() + opcode_size);
				send_buffer = true;
				break;
			case OPCODE_AUDIO_FRAME:
//				System.err.println("audio frame\n");
				/* log position and move on for now */
				audio_chunk_offset = bb.position();
				audio_chunk_size = opcode_size;
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SILENCE_FRAME:
//				System.err.println("silence frame");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_CREATE_GRADIENT:
//				System.err.println("create gradient");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SET_PALETTE_COMPRESSED:
//				System.err.println("set palette compressed");
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SET_SKIP_MAP:
//				System.err.println("set skip map");
				/* log position and move on for now */
				skip_map_chunk_offset = bb.position();
				skip_map_chunk_size = (short) opcode_size;
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SET_DECODING_MAP:
//				System.err.println("set decoding map");
				/* log position and move on for now */
				decode_map_chunk_offset = bb.position();
				decode_map_chunk_size = (short) opcode_size;
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_VIDEO_DATA_06:
//				System.err.println("set video data format 0x06");
				frame_format = 0x06;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.position(bb.position() + opcode_size);
				break;

			case OPCODE_VIDEO_DATA_10:
//	        	System.err.println("set video data format 0x10");
				frame_format = 0x10;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.position(bb.position() + opcode_size);
				break;

			case OPCODE_VIDEO_DATA_11:
//	        	System.err.println("set video data format 0x11");
				frame_format = 0x11;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.position(bb.position() + opcode_size);
				break;
			case OPCODE_SET_PALETTE:
//				System.err.println("set palette");
				if (opcode_size > 0x304 || opcode_size < 4) {
					System.err.println("demux_ipmovie: set_palette opcode with invalid size");
					chunk_type = CHUNK_BAD;
					break;
				}
				/* load the palette into internal data structure */
				int first_color = bb.getShort() & 0xFF;
				int last_color = first_color + (bb.getShort() & 0xFF) - 1;
				/* sanity check (since they are 16 bit values) */
				if ((first_color > 0xFF) || (last_color > 0xFF)
						|| (last_color - first_color + 1) * 3 + 4 > opcode_size) {
					System.err.println("demux_ipmovie: set_palette indexes out of range (" + first_color + " . "
							+ last_color + ")");
					chunk_type = CHUNK_BAD;
					break;
				}

				for (int i = first_color; i <= last_color; i++) {
					palette[3 * i + 0] = (byte) (bb.get() * 4);
					palette[3 * i + 1] = (byte) (bb.get() * 4);
					palette[3 * i + 2] = (byte) (bb.get() * 4);
				}
				hasPalette = true;
				break;

			case OPCODE_UNKNOWN_12:
			case OPCODE_UNKNOWN_13:
			case OPCODE_UNKNOWN_14:
			case OPCODE_UNKNOWN_15:
//				System.err.println("unknown (but documented) opcode 0x" + Integer.toHexString(opcode_type));
				bb.position(bb.position() + opcode_size);
				break;
			default:
//		        System.err.println("*** unknown opcode type 0x" + Integer.toHexString(chunk_type));
				chunk_type = CHUNK_BAD;
				break;
			}
		}

//		if (s->avf->nb_streams == 1 && audio_type != 0)
//	        init_audio(s->avf);

		/* make a note of where the stream is sitting */
		next_chunk_offset = bb.position();

		/* dispatch the first of any pending packets */
		if ((chunk_type == CHUNK_VIDEO) || (chunk_type == CHUNK_AUDIO_ONLY))
			chunk_type = load_ipmovie_packet(bb);

		return chunk_type;
	}

	private int load_ipmovie_packet(ByteBuffer bb) {
		int chunk_type;
		if (audio_chunk_offset != 0 && audio_channels != 0 && audio_bits != 0) {
			if (audio_type == AV_CODEC_ID_NONE) {
				System.err.println("Can not read audio packet before audio codec is known");
				return CHUNK_BAD;
			}

			/* adjust for PCM audio by skipping chunk header */
			if (audio_type != AV_CODEC_ID_INTERPLAY_DPCM) {
				audio_chunk_offset += 6;
				audio_chunk_size -= 6;
			}

			bb.position(audio_chunk_offset);
			audio_chunk_offset = 0;

			pkt.audio_chunk_data = new byte[audio_chunk_size];
			if (bb.position() + audio_chunk_size >= bb.capacity())
				return CHUNK_EOF;
			bb.get(pkt.audio_chunk_data);

//		    pkt.stream_index = audio_stream_index;
			pkt.pts = audio_frame_count;

			/* audio frame maintenance */
			if (audio_type != AV_CODEC_ID_INTERPLAY_DPCM)
				audio_frame_count += (audio_chunk_size / audio_channels / (audio_bits / 8));
			else
				audio_frame_count += (audio_chunk_size - 6 - audio_channels) / audio_channels;

//			System.err.println("sending audio frame with pts " + pkt.pts + " (" + audio_frame_count + " audio frames)");
			chunk_type = CHUNK_VIDEO;

		} else if (frame_format != 0) {
			/*
			 * send the frame format, decode map, the video data, skip map, and the
			 * send_buffer flag together
			 */
			pkt.video_chunk_data = new byte[video_chunk_size];
			pkt.decode_map_chunk_data = new byte[decode_map_chunk_size];
			pkt.skip_map_chunk_data = new byte[skip_map_chunk_size];

			if (hasPalette) {
				pkt.palette = new byte[768];
				System.arraycopy(palette, 0, pkt.palette, 0, 768);
				hasPalette = false;
			}
			pkt.frame_format = frame_format;
			pkt.send_buffer = send_buffer;
			pkt.video_chunk_size = video_chunk_size;
			pkt.decode_map_chunk_size = decode_map_chunk_size;
			pkt.skip_map_chunk_size = skip_map_chunk_size;

			frame_format = 0;
			send_buffer = false;

			pkt.pos = video_chunk_offset;
			bb.position(video_chunk_offset);
			video_chunk_offset = 0;

			if (bb.position() + video_chunk_size >= bb.capacity())
				return CHUNK_EOF;
			bb.get(pkt.video_chunk_data);

			if (decode_map_chunk_size != 0) {
				pkt.pos = decode_map_chunk_offset;
				bb.position(decode_map_chunk_offset);
				decode_map_chunk_offset = 0;

				if (bb.position() + decode_map_chunk_size >= bb.capacity())
					return CHUNK_EOF;
				bb.get(pkt.decode_map_chunk_data);
			}

			if (skip_map_chunk_size != 0) {
				pkt.pos = skip_map_chunk_offset;
				bb.position(skip_map_chunk_offset);
				skip_map_chunk_offset = 0;

				if (bb.position() + skip_map_chunk_size >= bb.capacity())
					return CHUNK_EOF;
				bb.get(pkt.skip_map_chunk_data);
			}

			video_chunk_size = 0;
			decode_map_chunk_size = 0;
			skip_map_chunk_size = 0;

//		    pkt.stream_index = video_stream_index;
			pkt.pts = video_pts;
//			System.err.println("sending video frame with pts " + pkt.pts);

			video_pts += frame_pts_inc;

			chunk_type = CHUNK_VIDEO;
		} else {
			bb.position(next_chunk_offset);
			chunk_type = CHUNK_DONE;
		}

		return chunk_type;
	}
	
	
	
	public byte[] decode_frame()
	{
//		const uint8_t *buf = avpkt->data;
//	    int buf_size = avpkt->size;
//	    IpvideoContext *s = avctx->priv_data;
//	    AVFrame *frame = data;
//	    int ret;
//
//	    if (av_packet_get_side_data(avpkt, AV_PKT_DATA_PARAM_CHANGE, NULL)) {
//	        av_frame_unref(s->last_frame);
//	        av_frame_unref(s->second_last_frame);
//	    }
//
//	    if (buf_size < 2)
//	        return AVERROR_INVALIDDATA;
//
//	    /* decoding map contains 4 bits of information per 8x8 block */
//	    s->decoding_map_size = AV_RL16(avpkt->data);
//
//	    /* compressed buffer needs to be large enough to at least hold an entire
//	     * decoding map */
//	    if (buf_size < s->decoding_map_size + 2)
//	        return buf_size;
//
//
//	    s->decoding_map = buf + 2;
//	    bytestream2_init(&s->stream_ptr, buf + 2 + s->decoding_map_size,
//	                     buf_size - s->decoding_map_size);
//
//	    if ((ret = ff_get_buffer(avctx, frame, AV_GET_BUFFER_FLAG_REF)) < 0)
//	        return ret;
//
//	    if (!s->is_16bpp) {
//	        int size;
//	        const uint8_t *pal = av_packet_get_side_data(avpkt, AV_PKT_DATA_PALETTE, &size);
//	        if (pal && size == AVPALETTE_SIZE) {
//	            frame->palette_has_changed = 1;
//	            memcpy(s->pal, pal, AVPALETTE_SIZE);
//	        } else if (pal) {
//	            av_log(avctx, AV_LOG_ERROR, "Palette size %d is wrong\n", size);
//	        }
//	    }
//
//	    ipvideo_decode_opcodes(s, frame);
//
//	    *got_frame = 1;
//
//	    /* shuffle frames */
//	    av_frame_unref(s->second_last_frame);
//	    FFSWAP(AVFrame*, s->second_last_frame, s->last_frame);
//	    if ((ret = av_frame_ref(s->last_frame, frame)) < 0)
//	        return ret;
//
//	    /* report that the buffer was completely consumed */
	    return null;
	}
}
