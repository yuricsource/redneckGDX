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

import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;

import ru.m210projects.Redneck.Main;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuColorCorr;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoSetup;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class RMenuVideoSet extends MenuVideoSetup {

	public RMenuVideoSet(Main app) {
		super(app, 46, 25, 240, 12, app.getFont(1));
	
		mVideoMode.font = app.getFont(2);
		mColorMode.font = app.getFont(2);
		mColorMode.x -= 5;
		mColorMode.y += 10;

		sFov.y += 10;
		sFilter.y += 8;
		sAnisotropy.y += 8;
		sWidescreen.y += 8;	
		mMenuFPS.y += 8;
		sVSync.y += 8;
		UseVoxels.y += 6;
		UseModels.y += 6;
		Usehrp.y += 6;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public MenuColorCorr getColorCorrectionMenu(BuildGame app) {
		Main gameapp = (Main) app;
		return (MenuColorCorr) (gameapp.menu.mMenus[COLORCORR]);
	}

	@Override
	public MenuVideoMode getVideoModeMenu(BuildGame app) {
		return new RMenuVideoMode(app);
	}

}
