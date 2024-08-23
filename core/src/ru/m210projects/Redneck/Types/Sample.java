// This file is part of RedneckGDX
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

package ru.m210projects.Redneck.Types;

import ru.m210projects.Build.Architecture.common.audio.Source;
import ru.m210projects.Build.Architecture.common.audio.SourceListener;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Redneck.Names;

import java.nio.ByteBuffer;

import static ru.m210projects.Redneck.Globals.hittype;
import static ru.m210projects.Redneck.Main.boardService;

public class Sample implements SourceListener {
    public final SoundOwner[] SoundOwner = {new SoundOwner(), new SoundOwner(), new SoundOwner(), new SoundOwner()};
    public ByteBuffer ptr;
    public int lock;
    public int bits, rate;
    private int num = 0;
    private boolean globalSound = false;

    public boolean isGlobalSound() {
        return globalSound;
    }

    public void setGlobalSound(boolean globalSound) {
        this.globalSound = globalSound;
    }

    public void addSoundOwner(Source source, int i) {
        SoundOwner[num].i = i;
        SoundOwner[num].voice = source;
        num++;
    }

    public SoundOwner getSoundOwner(int index) {
        return SoundOwner[index];
    }

    public int getSoundOwnerCount() {
        return num;
    }

    public void stopSound(int spriteId) {
        if (num > 0 && (spriteId == -1 || spriteId == SoundOwner[num - 1].i)) {
            SoundOwner[num - 1].voice.stop();
            onStop();
        }
    }

    public void stopenvsound(int spriteId) {
        if (num > 0) {
            for (int j = 0; j < num; j++) {
                if (SoundOwner[j].i == spriteId) {
                    SoundOwner[j].voice.stop();
                    break;
                }
            }
        }
    }

    @Override
    public void onStop() {
        final int ownerCount = num;
        if (ownerCount > 0) {
            if (!isGlobalSound()) {
                for (int j = 0; j < ownerCount; j++) {
                    SoundOwner soundOwner = getSoundOwner(j);
                    Sprite spr = boardService.getSprite(soundOwner.i);
                    if (spr != null && spr.getPicnum() == Names.MUSICANDSFX && spr.getLotag() < 999) {
                        Sector sec = boardService.getSector(spr.getSectnum());
                        if (sec != null && sec.getLotag() < 3) {
                            hittype[soundOwner.i].temp_data[0] = 0;
                            if ((j + 1) < ownerCount) {
                                soundOwner.voice = getSoundOwner(ownerCount - 1).voice;
                                soundOwner.i = getSoundOwner(ownerCount - 1).i;
                            }
                            break;
                        }
                    }
                }

                num--;
                getSoundOwner(ownerCount - 1).i = -1;
            }
            lock--;
        }
    }
}
