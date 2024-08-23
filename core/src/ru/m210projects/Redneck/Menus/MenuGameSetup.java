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
import ru.m210projects.Redneck.Main;

import static ru.m210projects.Build.net.Mmulti.myconnectindex;
import static ru.m210projects.Build.net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Types.UserDefs.DEMOSTAT_NULL;
import static ru.m210projects.Redneck.Types.UserDefs.DEMOSTAT_RECORD;

public class MenuGameSetup extends BuildMenu {

    public MenuGameSetup(final Main app) {
        super(app.pMenu);
        RRTitle mTitle = new RRTitle("Game Setup");
        int pos = 40;

        MenuSwitch sAutoload = new MenuSwitch("Autoload folder", app.getFont(1), 46, pos += 12, 240, cfg.isAutoloadFolder(), (handler, pItem) -> {
            MenuSwitch sw = (MenuSwitch) pItem;
            cfg.setAutoloadFolder(sw.value);
        }, "Enabled", "Disabled");

        MenuSwitch sSlopeTilt = new MenuSwitch("SCREEN TILTING:", app.getFont(1), 46, pos += 12, 240, ud.screen_tilting == 1, (handler, pItem) -> {
            MenuSwitch sw = (MenuSwitch) pItem;
            ud.screen_tilting = sw.value ? 1 : 0;
        }, null, null);

        MenuSwitch sAutoAim = new MenuSwitch("AutoAim:", app.getFont(1), 46, pos += 12, 240, cfg.gAutoAim, (handler, pItem) -> {
            MenuSwitch sw = (MenuSwitch) pItem;
            cfg.gAutoAim = sw.value;
            ps[myconnectindex].auto_aim = cfg.gAutoAim ? 1 : 0;
            if (numplayers > 1) {
                app.net.getnames();
            }
        }, null, null);

        MenuSwitch sColoredKeys = new MenuSwitch("Colored keys:", app.getFont(1), 46, pos += 12, 240, cfg.gColoredKeys,
                (handler, pItem) -> {
                    MenuSwitch sw = (MenuSwitch) pItem;
                    cfg.gColoredKeys = sw.value;
                }, null, null);


        MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback:", app.getFont(1), 46, pos += 12, 240, null, 0,
                (handler, pItem) -> {
                    MenuConteiner item = (MenuConteiner) pItem;
                    cfg.gDemoSeq = item.num;
                }) {
            @Override
            public void open() {
                if (this.list == null) {
                    this.list = new char[3][];
                    this.list[0] = "Off".toCharArray();
                    this.list[1] = "In order".toCharArray();
                    this.list[2] = "Randomly".toCharArray();
                }
                num = cfg.gDemoSeq;
            }
        };

        MenuSwitch sRecord = new MenuSwitch("Record demo:", app.getFont(1), 46, pos += 12, 240, ud.m_recstat == DEMOSTAT_RECORD,
                (handler, pItem) -> {
                    MenuSwitch sw = (MenuSwitch) pItem;
                    ud.m_recstat = sw.value  ? DEMOSTAT_RECORD : DEMOSTAT_NULL;
                }, null, null) {

            @Override
            public void open() {
                value = gDemoScreen.isRecordEnabled();
                mCheckEnableItem(!app.isCurrentScreen(gGameScreen));
            }
        };

        MenuSwitch mTimer = new MenuSwitch("Game loop timer:", app.getFont(1), 46, pos + 12, 240, cfg.isLegacyTimer(),
                (handler, pItem) -> {
                    MenuSwitch sw = (MenuSwitch) pItem;
                    cfg.setLegacyTimer(sw.value);
                    engine.inittimer(cfg.isLegacyTimer(), TICRATE, TICSPERFRAME);
                }, "Legacy", "Gdx") {
        };

        addItem(mTitle, false);
        addItem(sAutoload, true);
        addItem(sSlopeTilt, false);
        addItem(sAutoAim, false);
        addItem(sColoredKeys, false);
        addItem(mPlayingDemo, false);
        addItem(sRecord, false);
        addItem(mTimer, false);
    }
}
