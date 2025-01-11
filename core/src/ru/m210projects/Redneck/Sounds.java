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

import com.badlogic.gdx.audio.Music;
import ru.m210projects.Build.Architecture.common.audio.BuildAudio;
import ru.m210projects.Build.Architecture.common.audio.SoundData;
import ru.m210projects.Build.Architecture.common.audio.Source;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.FileUtils;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Build.filehandle.fs.Directory;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Types.Sample;
import ru.m210projects.Redneck.Types.SoundOwner;
import ru.m210projects.Redneck.filehandle.VOCDecoder;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.m210projects.Build.filehandle.fs.Directory.DUMMY_ENTRY;
import static ru.m210projects.Build.net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Gameutils.FindDistance3D;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.SoundDefs.*;

public class Sounds {

    public static final int LOUDESTVOLUME = 150;
    public static final int NUM_SOUNDS = 500;
    public static final List<Entry> cdTrackEntries = new ArrayList<>();
    private static final int[][] PitchTable = {
            {0x10000, 0x10097, 0x1012f, 0x101c7, 0x10260, 0x102f9, 0x10392, 0x1042c,
                    0x104c6, 0x10561, 0x105fb, 0x10696, 0x10732, 0x107ce, 0x1086a, 0x10907,
                    0x109a4, 0x10a41, 0x10adf, 0x10b7d, 0x10c1b, 0x10cba, 0x10d59, 0x10df8,
                    0x10e98},
            {0x10f38, 0x10fd9, 0x1107a, 0x1111b, 0x111bd, 0x1125f, 0x11302, 0x113a5,
                    0x11448, 0x114eb, 0x1158f, 0x11634, 0x116d8, 0x1177e, 0x11823, 0x118c9,
                    0x1196f, 0x11a16, 0x11abd, 0x11b64, 0x11c0c, 0x11cb4, 0x11d5d, 0x11e06,
                    0x11eaf},
            {0x11f59, 0x12003, 0x120ae, 0x12159, 0x12204, 0x122b0, 0x1235c, 0x12409,
                    0x124b6, 0x12563, 0x12611, 0x126bf, 0x1276d, 0x1281c, 0x128cc, 0x1297b,
                    0x12a2b, 0x12adc, 0x12b8d, 0x12c3e, 0x12cf0, 0x12da2, 0x12e55, 0x12f08,
                    0x12fbc},
            {0x1306f, 0x13124, 0x131d8, 0x1328d, 0x13343, 0x133f9, 0x134af, 0x13566,
                    0x1361d, 0x136d5, 0x1378d, 0x13846, 0x138fe, 0x139b8, 0x13a72, 0x13b2c,
                    0x13be6, 0x13ca1, 0x13d5d, 0x13e19, 0x13ed5, 0x13f92, 0x1404f, 0x1410d,
                    0x141cb},
            {0x1428a, 0x14349, 0x14408, 0x144c8, 0x14588, 0x14649, 0x1470a, 0x147cc,
                    0x1488e, 0x14951, 0x14a14, 0x14ad7, 0x14b9b, 0x14c5f, 0x14d24, 0x14dea,
                    0x14eaf, 0x14f75, 0x1503c, 0x15103, 0x151cb, 0x15293, 0x1535b, 0x15424,
                    0x154ee},
            {0x155b8, 0x15682, 0x1574d, 0x15818, 0x158e4, 0x159b0, 0x15a7d, 0x15b4a,
                    0x15c18, 0x15ce6, 0x15db4, 0x15e83, 0x15f53, 0x16023, 0x160f4, 0x161c5,
                    0x16296, 0x16368, 0x1643a, 0x1650d, 0x165e1, 0x166b5, 0x16789, 0x1685e,
                    0x16934},
            {0x16a09, 0x16ae0, 0x16bb7, 0x16c8e, 0x16d66, 0x16e3e, 0x16f17, 0x16ff1,
                    0x170ca, 0x171a5, 0x17280, 0x1735b, 0x17437, 0x17513, 0x175f0, 0x176ce,
                    0x177ac, 0x1788a, 0x17969, 0x17a49, 0x17b29, 0x17c09, 0x17cea, 0x17dcc,
                    0x17eae},
            {0x17f91, 0x18074, 0x18157, 0x1823c, 0x18320, 0x18406, 0x184eb, 0x185d2,
                    0x186b8, 0x187a0, 0x18888, 0x18970, 0x18a59, 0x18b43, 0x18c2d, 0x18d17,
                    0x18e02, 0x18eee, 0x18fda, 0x190c7, 0x191b5, 0x192a2, 0x19391, 0x19480,
                    0x1956f},
            {0x1965f, 0x19750, 0x19841, 0x19933, 0x19a25, 0x19b18, 0x19c0c, 0x19d00,
                    0x19df4, 0x19ee9, 0x19fdf, 0x1a0d5, 0x1a1cc, 0x1a2c4, 0x1a3bc, 0x1a4b4,
                    0x1a5ad, 0x1a6a7, 0x1a7a1, 0x1a89c, 0x1a998, 0x1aa94, 0x1ab90, 0x1ac8d,
                    0x1ad8b},
            {0x1ae89, 0x1af88, 0x1b088, 0x1b188, 0x1b289, 0x1b38a, 0x1b48c, 0x1b58f,
                    0x1b692, 0x1b795, 0x1b89a, 0x1b99f, 0x1baa4, 0x1bbaa, 0x1bcb1, 0x1bdb8,
                    0x1bec0, 0x1bfc9, 0x1c0d2, 0x1c1dc, 0x1c2e6, 0x1c3f1, 0x1c4fd, 0x1c609,
                    0x1c716},
            {0x1c823, 0x1c931, 0x1ca40, 0x1cb50, 0x1cc60, 0x1cd70, 0x1ce81, 0x1cf93,
                    0x1d0a6, 0x1d1b9, 0x1d2cd, 0x1d3e1, 0x1d4f6, 0x1d60c, 0x1d722, 0x1d839,
                    0x1d951, 0x1da69, 0x1db82, 0x1dc9c, 0x1ddb6, 0x1ded1, 0x1dfec, 0x1e109,
                    0x1e225},
            {0x1e343, 0x1e461, 0x1e580, 0x1e6a0, 0x1e7c0, 0x1e8e0, 0x1ea02, 0x1eb24,
                    0x1ec47, 0x1ed6b, 0x1ee8f, 0x1efb4, 0x1f0d9, 0x1f1ff, 0x1f326, 0x1f44e,
                    0x1f576, 0x1f69f, 0x1f7c9, 0x1f8f3, 0x1fa1e, 0x1fb4a, 0x1fc76, 0x1fda3,
                    0x1fed1}
    };
    private static final int MAXDETUNE = 25;
    public static char rtsplaying;
    public static int numenvsnds;
    public static Entry userMusicEntry;
    public static int currTrack = -1;
    public static Music currMusic;
    public static Entry currSongEntry;
    public static BuildAudio audio;

    public static void searchCDtracks() {
        CueScript cueScript;

        cdTrackEntries.clear();
        Directory gameDir = game.getCache().getGameDirectory();

        // search cue scripts at first
        for (Entry file : gameDir.stream().filter(e -> e.isExtension("cue")).collect(Collectors.toList())) {
            cueScript = new CueScript(file.getName(), file);
            for (String track : cueScript.getTracks()) {
                Entry entry = game.getCache().getEntry(track, true);
                if (entry.exists()) {
                    cdTrackEntries.add(entry);
                }
            }

            if (!cdTrackEntries.isEmpty()) {
                break;
            }
        }

        // if not found try to find in ogg
        if (cdTrackEntries.isEmpty()) {
            cdTrackEntries.addAll(gameDir.stream().filter(e -> e.isExtension("ogg")).sorted(Entry::compareTo).collect(Collectors.toList()));
        }

        if (!cdTrackEntries.isEmpty()) {
            Console.out.println(cdTrackEntries.size() + " cd tracks found...");
        } else {
            Console.out.println("Cd tracks not found.");
        }
    }

    public static void check_fta_sounds(int i) {
        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return;
        }

        if (sp.getExtra() > 0) {
            switch (sp.getPicnum()) {
                case BILLYCOCK:
                case BILLYRAY:
                case 4249:
                    spritesound(121, i);
                    break;
                case COOT:
                    if (currentGame.getCON().type != RRRA || (engine.krand() & 3) == 2) {
                        spritesound(111, i);
                    }
                    break;
            }
        }
    }

    public static void sndHandlePause(boolean gPaused) {
        if (gPaused) {
            if (currMusic != null) {
                currMusic.pause();
            }

            StopAllSounds();
            clearsoundlocks();
        } else {
            if (!cfg.isMuteMusic() && currMusic != null) {
                currMusic.play();
            }
        }
    }

    public static void sndStopMusic() {
        if (currMusic != null) {
            currMusic.stop();
        }

        currMusic = null;
        currTrack++;
        if (currTrack >= cdTrackEntries.size()) {
            currTrack = 0;
        }
        currSongEntry = null;
    }

    public static void sndCheckMusic(Entry map) { // usermap music
        if (map != null && map.exists()) {
            String mMapName = map.getName();
            userMusicEntry = map.getParent().getEntry(mMapName.substring(0, mMapName.lastIndexOf('.')) + ".ogg");
        }
    }

    public static void sndPlayMusic(String unused) {
        if (cfg.isMuteMusic()) {
            return;
        }

        if (userMusicEntry != null) {
            if (currMusic != null && currMusic.isPlaying() && userMusicEntry.equals(currSongEntry)) {
                return;
            }

            sndStopMusic();
            if ((currMusic = newMusic(userMusicEntry)) != null) {
                currSongEntry = userMusicEntry;
                currMusic.setLooping(true);
                currMusic.play();
                return;
            }
        }

        if (cfg.gShuffleMusic) {
            currTrack = (int) (Math.random() * (cdTrackEntries.size() - 1));
        }
        sndPlayTrack(currTrack);
    }

    public static void checkTrack() {
        if (cfg.isMuteMusic()) {
            return;
        }

        if (currMusic != null && !currMusic.isPlaying()) {
            if (cfg.gShuffleMusic) {
                currTrack = (int) (Math.random() * (cdTrackEntries.size() - 1));
            } else {
                currTrack++;
            }

            if (currTrack < 0 || currTrack >= cdTrackEntries.size()) {
                currTrack = 0;
            }

            System.err.println("Change music to " + currTrack);
            sndPlayTrack(currTrack);
        } else if (currMusic == null) {
            int i = 0;
            if (cfg.gShuffleMusic) {
                i = (int) (Math.random() * (cdTrackEntries.size() - 1));
            }

            for (; i < cdTrackEntries.size(); i++) {
                if (sndPlayTrack(i)) {
                    System.err.println("Start music " + i);
                    return;
                }
            }
            Console.out.println("Music tracks not found!");
            cfg.setMuteMusic(true);
        }
    }

    public static boolean sndPlayTrack(int nTrack) {
        if (currMusic != null && currMusic.isPlaying() && currTrack == nTrack) {
            return true;
        }

        sndStopMusic();
        if (nTrack >= 0 && nTrack < cdTrackEntries.size()
                && (currMusic = newMusic(cdTrackEntries.get(nTrack))) != null) {

            System.err.println("Play track " + nTrack);
            currTrack = nTrack;
            currMusic.setLooping(false);
            currMusic.play();
            return true;
        }

        return false;
    }

    public static void SoundStartup() {
        cfg.setAudioDriver(cfg.getAudioDriver());
        cfg.setMidiDevice(cfg.getMidiDevice());
        Sounds.audio = cfg.getAudio();
        Sounds.audio.registerDecoder("VOC", new VOCDecoder());

        for (int i = 0; i < NUM_SOUNDS; i++) {
            Sound[i] = new Sample();
            Sound[i].setGlobalSound((currentGame.getCON().soundm[i] & 16) != 0);
        }
    }

    public static Entry getSampleEntry(int num) {
        if (num < 0 || num >= NUM_SOUNDS || currentGame.getCON().sounds[num] == null) {
            return DUMMY_ENTRY;
        }

        Path filename = FileUtils.getPath(currentGame.getCON().sounds[num]);
        return game.getCache().getEntry(filename, !loadfromgrouponly);
    }

    public static int loadsound(int num) {
        if (num < 0 || num >= NUM_SOUNDS || cfg.isNoSound()) {
            return 0;
        }

        Entry entry = getSampleEntry(num);
        if (!entry.exists()) {
            Console.out.println("Sound " + "(" + num + ") not found.");
            return 0;
        }

        soundsiz[num] = (int) entry.getSize();
        Sound[num].lock = 2;

        loadSample(entry, num);
        return 1;
    }

    public static Source xyzsound(int num, int i, int x, int y, int z) {
        Source voice;
        int pitch;

        if (num < 0 || num >= NUM_SOUNDS
                || ((currentGame.getCON().soundm[num] & 8) != 0 && ud.lockout != 0) || cfg.isNoSound() || Sound[num].getSoundOwnerCount() > 3
                || !isAvailable(currentGame.getCON().soundpr[num])
                || (ps[myconnectindex].timebeforeexit > 0 && ps[myconnectindex].timebeforeexit <= 26 * 3)
                || game.menu.gShowMenu) {
            return null;
        }

        if ((currentGame.getCON().soundm[num] & 128) != 0) {
            sound(num);
            return null;
        }

        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return null;
        }

        if ((currentGame.getCON().soundm[num] & 4) != 0) {
            if (!cfg.VoiceToggle || (ud.multimode > 1 && sp.getPicnum() == APLAYER && sp.getYvel() != screenpeek
                    && ud.coop != 1)) {
                return null;
            }

            for (int j = 0; j < NUM_SOUNDS; j++) {
                for (int k = 0; k < Sound[j].getSoundOwnerCount(); k++) {
                    if ((Sound[j].getSoundOwnerCount() > 0) && (currentGame.getCON().soundm[j] & 4) != 0) {
                        return null;
                    }
                }
            }
        }

        int cx = ps[screenpeek].oposx;
        int cy = ps[screenpeek].oposy;
        int cz = ps[screenpeek].oposz;
        int cs = ps[screenpeek].cursectnum;

        int sndist = FindDistance3D((cx - x), (cy - y), (cz - z) >> 4);

        Sector sec = boardService.getSector(sp.getSectnum());
        if (i >= 0 && sec != null && (currentGame.getCON().soundm[num] & 16) == 0 && sp.getPicnum() == MUSICANDSFX
                && sp.getLotag() < 999 && sec.getLotag() < 9) {
            sndist = divscale(sndist, (sp.getHitag() + 1), 14);
        }

        int pitchs = currentGame.getCON().soundps[num];
        int pitche = currentGame.getCON().soundpe[num];
        cx = klabs(pitche - pitchs);

        if (cx != 0) {
            if (pitchs < pitche) {
                pitch = pitchs + (engine.rand() % cx);
            } else {
                pitch = pitche + (engine.rand() % cx);
            }
        } else {
            pitch = pitchs;
        }

        sndist += currentGame.getCON().soundvo[num];
        if (sndist < 0) {
            sndist = 0;
        }
        if (sndist != 0 && sp.getPicnum() != MUSICANDSFX && !engine.cansee(cx, cy, cz - (24 << 8), cs, sp.getX(),
                sp.getY(), sp.getZ() - (24 << 8), sp.getSectnum())) {
            sndist += sndist >> 2;
        }

        Sector csec = boardService.getSector(cs);
        switch (num) {
            case PIPEBOMB_EXPLODE:
            case LASERTRIP_EXPLODE:
            case RPG_EXPLODE:
                if (sndist > (6144)) {
                    sndist = 6144;
                }
                if (csec != null && csec.getLotag() == 2) {
                    pitch -= 1024;
                }
                break;
            default:
                if (csec != null && csec.getLotag() == 2 && (currentGame.getCON().soundm[num] & 4) == 0) {
                    pitch = -768;
                }
                if (sndist > 31444 && sp.getPicnum() != MUSICANDSFX) {
                    return null;
                }
                break;
        }

        if (Sound[num].getSoundOwnerCount() > 0 && sp.getPicnum() != MUSICANDSFX) {
            if (Sound[num].getSoundOwner(0).i == i) {
                stopsound(num);
            } else if (Sound[num].getSoundOwnerCount() > 1) {
                stopsound(num);
            } else if (badguy(sp) && sp.getExtra() <= 0) {
                stopsound(num);
            }
        }

        if (sp.getPicnum() == APLAYER && sp.getYvel() == screenpeek) {
            sndist = 0;
        }

        if (Sound[num].ptr == null) {
            if (loadsound(num) == 0) {
                return null;
            }
        } else {
            if (Sound[num].lock < 200) {
                Sound[num].lock = 200;
            } else {
                Sound[num].lock++;
            }

            Sound[num].ptr.rewind();
        }

        if ((currentGame.getCON().soundm[num] & 16) != 0) {
            sndist = 0;
        }

        if (sndist < ((255 - LOUDESTVOLUME) << 6)) {
            sndist = ((255 - LOUDESTVOLUME) << 6);
        }

        boolean looping = false;
        float volume = calcVolume(sndist);
        if ((currentGame.getCON().soundm[num] & 1) != 0) {
            if (Sound[num].getSoundOwnerCount() > 0) {
                return null;
            }
            looping = true;
        }

        voice = newSound(Sound[num].ptr, Sound[num].rate, Sound[num].bits, currentGame.getCON().soundpr[num]);
        if (voice != null) {
            Sound[num].addSoundOwner(voice, i);
            voice.setListener(Sound[num]);
            voice.setPosition(x, z >> 4, y);
            voice.setPitch(PITCH_GetScale(pitch));
            if (looping) {
                voice.loop(volume);
            } else {
                voice.play(volume);
            }
        } else {
            Sound[num].lock--;
        }
        return (voice);
    }

    public static Source sound(int num) {
        Source voice;

        if (cfg.isNoSound()) {
            return null;
        }

        if (num < 0 || num >= NUM_SOUNDS) {
            return null;
        }

        if (!cfg.VoiceToggle && (currentGame.getCON().soundm[num] & 4) != 0) {
            return null;
        }
        if ((currentGame.getCON().soundm[num] & 8) != 0 && ud.lockout != 0) {
            return null;
        }
        if (!isAvailable(currentGame.getCON().soundpr[num])) {
            return null;
        }

        int pitch;
        int pitchs = currentGame.getCON().soundps[num];
        int pitche = currentGame.getCON().soundpe[num];
        int cx = klabs(pitche - pitchs);

        if (cx != 0) {
            if (pitchs < pitche) {
                pitch = pitchs + (engine.rand() % cx);
            } else {
                pitch = pitche + (engine.rand() % cx);
            }
        } else {
            pitch = pitchs;
        }

        if (Sound[num].ptr == null) {
            if (loadsound(num) == 0) {
                return null;
            }
        } else {
            if (Sound[num].lock < 200) {
                Sound[num].lock = 200;
            } else {
                Sound[num].lock++;
            }

            Sound[num].ptr.rewind();
        }

        voice = newSound(Sound[num].ptr, Sound[num].rate, Sound[num].bits, currentGame.getCON().soundpr[num]);
        if (voice != null) {
            voice.setPitch(PITCH_GetScale(pitch));
            voice.setListener(Sound[num]);
            if ((currentGame.getCON().soundm[num] & 1) != 0) {
                voice.loop(calcVolume(LOUDESTVOLUME));
            } else {
                voice.play(calcVolume(255 - LOUDESTVOLUME));
            }
            return voice;
        }
        Sound[num].lock--;

        return null;
    }

    public static void loadSample(Entry entry, int num) {
        String extension = entry.getExtension();
        try(InputStream is = entry.getInputStream()) {
            // Some people love provide in with VOC extension files
            switch (StreamUtils.readString(is, 3)) {
                case "RIF":
                    extension = "WAV";
                    break;
                case "Ogg":
                    extension = "OGG";
                    break;
            }
        } catch (Exception ignored) {
        }

        SoundData soundData = audio.getSoundDecoder(extension).decode(entry);
        if (soundData != null) {
            Sound[num].bits = soundData.getBits();
            Sound[num].rate = soundData.getRate();
            Sound[num].ptr = soundData.getData();
            return;
        }

        Console.out.println("Can't load sound[" + num + "]", OsdColor.RED);
        Sound[num].ptr = ByteBuffer.allocateDirect(0); // to avoid of load cycle
        Sound[num].rate = 0;
        Sound[num].bits = 8;
    }

    public static Source spritesound(int num, int i) {
        if (num < 0 || num >= NUM_SOUNDS) {
            return null;
        }

        Sprite sp = boardService.getSprite(i);
        if (sp == null) {
            return null;
        }

        return xyzsound(num, i, sp.getX(), sp.getY(), sp.getZ());
    }

    public static void stopsound(int num) {
        if (num < 0 || num >= NUM_SOUNDS) {
            return;
        }

        Sound[num].stopSound(-1);
    }

    public static void stopsound(int num, int i) {
        if (num < 0 || num >= NUM_SOUNDS) {
            return;
        }

        Sound[num].stopSound(i);
    }

    public static void StopAllSounds() {
        for (int i = 0; i < NUM_SOUNDS; i++) {
            stopsound(i);
        }
        stopAllSounds();
    }

    public static void stopenvsound(int num, int i) {
        if (num < 0 || num >= NUM_SOUNDS) {
            return;
        }

        Sound[num].stopenvsound(i);
    }

    public static void pan3dsound() {
        int sndist, sx, sy, sz, cx, cy, cz;
        int cs, ca;

        numenvsnds = 0;

        Sprite cam = boardService.getSprite(ud.camerasprite);
        if (cam == null) {
            cx = ps[screenpeek].oposx;
            cy = ps[screenpeek].oposy;
            cz = ps[screenpeek].oposz;
            cs = ps[screenpeek].cursectnum;
            ca = (short) (ps[screenpeek].ang + ps[screenpeek].look_ang);
        } else {
            cx = cam.getX();
            cy = cam.getY();
            cz = cam.getZ();
            cs = cam.getSectnum();
            ca = cam.getAng();
        }

        audio.setListener(cx, cz >> 4, cy, ca);

        for (int j = 0; j < NUM_SOUNDS; j++) {
            for (int k = 0; k < Sound[j].getSoundOwnerCount(); k++) {
                SoundOwner soundOwner = Sound[j].getSoundOwner(k);
                Sprite sprite = boardService.getSprite(soundOwner.i);
                if (sprite == null) {
                    continue;
                }

                sx = sprite.getX();
                sy = sprite.getY();
                sz = sprite.getZ();

                if (sprite.getPicnum() == APLAYER && sprite.getYvel() == screenpeek) {
                    sndist = 0;
                } else {
                    sndist = FindDistance3D((cx - sx), (cy - sy), (cz - sz) >> 4);
                    Sector sec = boardService.getSector(sprite.getSectnum());
                    if (sec != null) { // 0.751
                        if (soundOwner.i >= 0 && (currentGame.getCON().soundm[j] & 16) == 0 && sprite.getPicnum() == MUSICANDSFX
                                && sprite.getLotag() < 999 && (sec.getLotag() & 0xff) < 9) {
                            sndist = divscale(sndist, (sprite.getHitag() + 1), 14);
                        }
                    }
                }

                sndist += currentGame.getCON().soundvo[j];
                if (sndist < 0) {
                    sndist = 0;
                }

                if (sndist != 0 && sprite.getPicnum() != MUSICANDSFX
                        && !engine.cansee(cx, cy, cz - (24 << 8), cs, sx, sy, sz - (24 << 8), sprite.getSectnum())) {
                    sndist += sndist >> 5;
                }

                if (sprite.getPicnum() == MUSICANDSFX && sprite.getLotag() < 999) {
                    numenvsnds++;
                }

                switch (j) {
                    case PIPEBOMB_EXPLODE:
                    case LASERTRIP_EXPLODE:
                    case RPG_EXPLODE:
                        if (sndist > (6144)) {
                            sndist = (6144);
                        }
                        break;
                    default:
                        if (sndist > 31444 && sprite.getPicnum() != MUSICANDSFX) {
                            stopsound(j);
                            continue;
                        }
                }

                if (Sound[j].ptr == null && loadsound(j) == 0) {
                    continue;
                }
                if ((currentGame.getCON().soundm[j] & 16) != 0) {
                    sndist = 0;
                }

                if (sndist < ((255 - LOUDESTVOLUME) << 6)) {
                    sndist = ((255 - LOUDESTVOLUME) << 6);
                }

                soundOwner.voice.setPosition(sx, sz >> 4, sy);
                soundOwner.voice.setVolume(calcVolume(sndist));
            }
        }
    }

    private static float calcVolume(int dist) {
        float vol = (dist >> 6) / 255.0f;
        vol = Math.min(Math.max(vol, 0.0f), 1.0f);
        return 1.0f - vol;
    }

    public static void clearsoundlocks() {
        for (int i = 0; i < NUM_SOUNDS; i++) {
            if (Sound[i].lock >= 200) {
                Sound[i].lock = 199;
            }
        }
    }

    public static Source newSound(ByteBuffer buffer, int rate, int bits, int priority) {
        return (Source) audio.newSound(buffer, rate, bits, priority);
    }

    public static Music newMusic(Entry entry) {
        return audio.newMusic(entry);
    }

    public static boolean isAvailable(int priority) {
        return audio.canPlay(priority);
    }

    public static void stopAllSounds() {
        audio.stopAllSounds();
    }

    public static void setReverb(boolean enable, float delay) {
        audio.setReverb(enable, delay);
    }

    private static float PITCH_GetScale(int pitchoffset) {
        int scale;
        int octaveshift;
        int noteshift;
        int note;
        int detune;

        if (pitchoffset == 0) {
            return (PitchTable[0][0]) / 65536.0f;
        }

        noteshift = pitchoffset % 1200;
        if (noteshift < 0) {
            noteshift += 1200;
        }

        note = noteshift / 100;
        detune = (noteshift % 100) / (100 / MAXDETUNE);
        octaveshift = (pitchoffset - noteshift) / 1200;

        scale = PitchTable[note][detune];
        if (octaveshift < 0) {
            scale >>= -octaveshift;
        } else {
            scale <<= octaveshift;
        }

        return scale / 65536.0f;
    }
}
