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


import ru.m210projects.Build.Pattern.MenuItems.*;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.osd.Console;

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.BIGFNTCURSOR;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.Sounds.*;

public class TrackPlayerMenu extends BuildMenu {

    public TrackPlayerMenu() {
        super(game.pMenu);
        this.addItem(new RRTitle("8 Track Player"), false);

        this.addItem(new TrackItem(160, 90), true);

        this.addItem(new PowerSwitch(74, 109), false);

        this.addItem(new MenuSlider(game.pSlider, "Music volume", game.getFont(1), 45, 140, 230,
                (int) (cfg.getMusicVolume() * 256), 0, 256, 8, (handler, pItem) -> {
                    MenuSlider slider = (MenuSlider) pItem;
                    cfg.setMusicVolume(slider.value / 256.0f);
                }, false) {

            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(!cfg.isMuteMusic());
                super.draw(handler);
            }
        }, false);

        this.addItem(new MenuSwitch("Shuffle", game.getFont(1), 45, 155, 230, cfg.gShuffleMusic, (handler, pItem) -> {
            MenuSwitch sw = (MenuSwitch) pItem;
            cfg.gShuffleMusic = sw.value;
        }, null, null) {
            @Override
            public void draw(MenuHandler handler) {
                mCheckEnableItem(!cfg.isMuteMusic());
                super.draw(handler);
            }
        }, false);
    }

    private static class PowerSwitch extends MenuSwitch {

        public PowerSwitch(int x, int y) {
            super(null, null, x, y, 320, cfg.isMuteMusic(), null, null, null);
        }

        @Override
        public void draw(MenuHandler handler) {
            Renderer renderer = game.getRenderer();
            if (cfg.isMuteMusic()) {
                renderer.rotatesprite(x << 16, y << 16, 48000, 0, 372, 0, 0, 10);
            } else {
                renderer.rotatesprite(x << 16, y << 16, 48000, 0, 373, 0, 0, 10);
            }

            if (isFocused()) {
                renderer.rotatesprite(x - 50 << 16, (y + engine.getTile(BIGFNTCURSOR).getHeight() - 20) << 16, 8192, 0,
                        SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), 8 - (engine.getTotalClock() & 0x3F), 0, 2 | 8);
            }
        }

        @Override
        public boolean callback(MenuHandler handler, MenuOpt opt) {
            switch (opt) {
                case LMB:
                case ENTER:
                    if ((flags & 4) == 0) {
                        return false;
                    }

                    cfg.setMuteMusic(!cfg.isMuteMusic());
//                    cfg.setMusicVolume(!cfg.isMuteMusic() ? cfg.getMusicVolume() : 0);

                    sound(KICK_HIT);
                    break;
                default:
                    return m_pMenu.mNavigation(opt);
            }

            return false;
        }

        @Override
        public boolean mouseAction(int mx, int my) {
            if (mx > x - 15 && mx < x + 15) {
                return my > y - 15 && my < y + 10;
            }

            return false;
        }

    }

    private static class TrackItem extends MenuItem {

        final int x;
        final int y;

        final int[] tx;
        final int[] ty;

        int TrackFocus = -1;

        public TrackItem(int x, int y) {
            super(null, null);

            this.x = x;
            this.y = y;
            this.flags = 3 | 4;

            tx = new int[]{x - 101, x - 70, x - 41, x - 12};
            ty = new int[]{y - 33, y - 18};
        }

        @Override
        public void draw(MenuHandler handler) {
            Renderer renderer = game.getRenderer();
            renderer.rotatesprite(x << 16, y << 16, 48000, 0, 370, 0, 0, 10);

            if (!cfg.isMuteMusic()) {
                if (currMusic != null && currMusic.isPlaying()) {
                    int num = currTrack;
                    if (num >= 0 && num < 8) {
                        renderer.rotatesprite(tx[num % 4] << 16, ty[(num / 4) & 1] << 16, 48000, 0, 371, 0, 0, 10 | 16);
                    }
                }

                if (TrackFocus != -1 && (engine.getTotalClock() & 16) != 0 && isFocused()) {
                    renderer.rotatesprite(tx[TrackFocus % 4] << 16, ty[(TrackFocus / 4) & 1] << 16, 48000, 0, 371, 0, 0,
                            10 | 16);
                }
            }

            if (isFocused()) {
                renderer.rotatesprite(x - 136 << 16, (y + engine.getTile(BIGFNTCURSOR).getHeight() - 40) << 16, 8192, 0,
                        SPINNINGNUKEICON + ((engine.getTotalClock() >> 3) & 15), 8 - (engine.getTotalClock() & 0x3F), 0, 2 | 8);
            }
        }

        private void changeTrack(int num) {
            if (num != -1) {
                if (!sndPlayTrack(num)) {
                    Console.out.println("Music track not found!");
                }
            }
        }

        @Override
        public boolean callback(MenuHandler handler, MenuOpt opt) {
            int line;

            switch (opt) {
                case LMB:
                case ENTER:
                    if ((flags & 4) == 0) {
                        return false;
                    }

                    sound(PISTOL_BODYHIT);
                    changeTrack(TrackFocus);
                    break;
                case LEFT:
                    if ((flags & 4) == 0) {
                        return false;
                    }

                    sound(335);
                    TrackFocus--;
                    TrackFocus &= 7;
                    break;
                case RIGHT:
                    if ((flags & 4) == 0) {
                        return false;
                    }

                    sound(335);
                    TrackFocus++;
                    TrackFocus &= 7;
                    break;
                case UP:
                    line = (TrackFocus / 4);
                    if (!cfg.isMuteMusic() && line == 1) {
                        TrackFocus -= 4;
                        TrackFocus &= 7;
                    } else {
                        m_pMenu.mNavUp();
                    }
                    break;
                case DW:
                    line = (TrackFocus / 4);
                    if (!cfg.isMuteMusic() && line == 0) {
                        TrackFocus += 4;
                        TrackFocus &= 7;
                    } else {
                        m_pMenu.mNavDown();
                    }
                    break;
                default:
                    return m_pMenu.mNavigation(opt);
            }

            return false;
        }

        @Override
        public boolean mouseAction(int mx, int my) {

            for (int i = 0; i < 4; i++) {
                if (mx > tx[i] && mx < tx[i] + 10) {
                    for (int j = 0; j < 2; j++) {
                        if (my > ty[j] && my < ty[j] + 10) {
                            TrackFocus = i + 4 * j;
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        @Override
        public void open() {
            TrackFocus = currTrack;
        }

        @Override
        public void close() {
        }

    }
}
