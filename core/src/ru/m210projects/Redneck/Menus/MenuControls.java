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
import ru.m210projects.Build.settings.GameConfig;
import ru.m210projects.Redneck.Main;

public class MenuControls extends BuildMenu {

    public MenuControls(final Main app) {
        super(app.pMenu);
        addItem(new RRTitle("Controls Setup"), false);
        int pos = 39;

        MenuButton mMouseSet = new MenuButton("Mouse setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new RMenuMouse(app), -1, null, 0);
        MenuButton mJoySet = new MenuButton("Joystick setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new RMenuJoystick(app), -1, null, 0);
        MenuButton mKeySet = new MenuButton("Keyboard Setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new RMenuMenuKeyboard(app), -1, null, 0);
        MenuButton mKeyReset = new MenuButton("Reset to default", app.getFont(2), 0, pos += 30, 320, 1, 0, getResetDefaultMenu(app), -1, null, 0);
        MenuButton mKeyClassic = new MenuButton("Reset to classic", app.getFont(2), 0, pos + 20, 320, 1, 0, getResetClassicMenu(app), -1, null, 0);

        addItem(mMouseSet, true);
        addItem(mJoySet, false);
        addItem(mKeySet, false);
        addItem(mKeyReset, false);
        addItem(mKeyClassic, false);
    }

    private void mResetDefault(GameConfig cfg, MenuHandler menu) {
        cfg.resetInput(false);

        menu.mMenuBack();
    }

    private void mResetClassic(GameConfig cfg, MenuHandler menu) {
        cfg.resetInput(true);
        menu.mMenuBack();
    }

    private BuildMenu getResetDefaultMenu(final Main app) {
        BuildMenu menu = new BuildMenu(app.menu);

        int pos = 90;
        MenuText QuitQuestion = new MenuText("Do you really", app.getFont(1), 160, pos, 1);
        MenuText QuitQuestion2 = new MenuText("want to reset keys?", app.getFont(1), 160, pos += 10, 1);
        MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(1), 160, pos + 20) {
            @Override
            public void positive(MenuHandler menu) {
                mResetDefault(app.pCfg, menu);
            }
        };

        menu.addItem(QuitQuestion, false);
        menu.addItem(QuitQuestion2, false);
        menu.addItem(QuitVariants, true);

        return menu;
    }

    private BuildMenu getResetClassicMenu(final Main app) {
        BuildMenu menu = new BuildMenu(app.menu);

        int pos = 90;
        MenuText QuitQuestion = new MenuText("Do you really want", app.getFont(1), 160, pos, 1);
        MenuText QuitQuestion2 = new MenuText("reset to classic?", app.getFont(1), 160, pos += 10, 1);
        MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(1), 160, pos + 20) {
            @Override
            public void positive(MenuHandler menu) {
                mResetClassic(app.pCfg, menu);
            }
        };

        menu.addItem(QuitQuestion, false);
        menu.addItem(QuitQuestion2, false);
        menu.addItem(QuitVariants, true);

        return menu;
    }
}
