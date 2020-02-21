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

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Redneck.Main;

public class SoundMenu extends MenuAudio {

	public SoundMenu(Main app)
	{
		super(app, 20,  30, 280, 12, 8, app.getFont(1));
		
		sSoundDrv.listFont = app.getFont(0);
		sSoundDrv.listShadow = true;
//		sMusicDrv.listFont = app.getFont(0);
//		sMusicDrv.listShadow = true;
		int pos = removeItem(sMusicDrv);
		for(int i = pos; i < m_nItems; i++)
			m_pItems[i].y -= 10;
		sMusicDrv = null;
		sResampler.listFont = app.getFont(0);
		sResampler.listShadow = true;
		
		mApplyChanges.font = app.getFont(2);
		mApplyChanges.y += 5;
	}
	
	@Override
	protected char[][] getMusicTypeList()
	{
		char[][] list = new char[1][];
		list[0] = "cd audio".toCharArray();
		
		return list;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public void soundPreDrvChange() {
		StopAllSounds();
	}

	@Override
	public void soundPostDrvChange() {
		if (game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen) || game.isCurrentScreen(gMenuScreen)) {
			sndStopMusic();
			if(game.isCurrentScreen(gMenuScreen)) { sndPlayMusic(currentGame.getCON().env_music_fn[0]); } 
			else sndPlayMusic(currentGame.getCON().music_fn[ud.volume_number][ud.level_number]);
		}
	}

	@Override
	public boolean soundRestart(int voices, int resampler) {
		return sndRestart(voices, resampler);
	}

	@Override
	public boolean musicRestart() {
		return midRestart();
	}

	@Override
	public void soundVolumeChange() {
		/* nothing */
	}

	@Override
	public void soundOn() {
		/* nothing */
	}

	@Override
	public void soundOff() {
		StopAllSounds();
	}
}
