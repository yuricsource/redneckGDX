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

package ru.m210projects.Redneck.Menus;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Sounds;

import static ru.m210projects.Redneck.Factory.RRMenuHandler.TRACKPLAYER;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.*;

public class SoundMenu extends MenuAudio {

    public SoundMenu(Main app) {
        super(app, 20, 30, 280, 12, 8, app.getFont(1));

        AudioListener listener = new AudioListener() {
            @Override
            public void PreDrvChange() {
                SoundOff();
                MusicOff();
            }

            @Override
            public void PostDrvChange() {
                SoundStartup();
                MusicOn();
            }

            @Override
            public void SoundOff() {
                StopAllSounds();
            }

            @Override
            public void MusicOn() {
                if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen) || game.isCurrentScreen(gMenuScreen)) {
                    if (!game.isCurrentScreen(gGameScreen)) {
                        sndPlayMusic(currentGame.getCON().env_music_fn[0]);
                    } else {
                        sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);
                    }
                }
            }

            @Override
            public void MusicOff() {
                sndStopMusic();
            }
        };
        setListener(listener);

        removeItem(sMusicDrv);
        sMusicDrv = null;
        removeItem(sMusic);
        sMusic = null;
        removeItem(sMusicSwitch);
        sMusicSwitch = null;

        for (int i = 0; i < m_nItems; i++) {
            if (m_pItems[i] == sMusicType) {
                m_pItems[i] = new MenuButton("8 Track player", app.getFont(2), 0, sMusicType.y, 320, 1, 0,
                        app.menu.mMenus[TRACKPLAYER], -1, null, 0) {
                    @Override
                    public void draw(MenuHandler handler) {
                        mCheckEnableItem(!Sounds.cdTrackEntries.isEmpty());
                        super.draw(handler);
                    }
                };
                m_pItems[i].m_pMenu = this;
                sMusicType = null;
            }
        }

        sSoundDrv.listFont = app.getFont(0);
        sSoundDrv.listShadow = true;
        sResampler.listFont = app.getFont(0);
        sResampler.listShadow = true;
        mApplyChanges.font = app.getFont(2);
        mApplyChanges.y += 5;
    }

    @Override
    public int removeItem(MenuItem pItem) {
        if (pItem == null) {
            return -1;
        }

        for (int i = 0; i < m_nItems; i++) {
            if (m_pItems[i] == pItem) {
                int pos = i;
                while (pos < m_nItems) {
                    m_pItems[pos] = ++pos < m_nItems ? m_pItems[pos] : null;
                    if (m_pItems[pos] != null) {
                        m_pItems[pos].y -= 10;
                    }
                }
                m_nItems--;
                return i;
            }
        }

        return -1;
    }

    @Override
    protected char[][] getMusicTypeList() {
        char[][] list = new char[1][];
        list[0] = "cd audio".toCharArray();

        return list;
    }

    @Override
    public MenuTitle getTitle(BuildGame app, String text) {
        return new RRTitle(text);
    }

}
