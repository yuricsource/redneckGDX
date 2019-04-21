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

package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Redneck.Globals.RR66;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Main.gAnmScreen;
import static ru.m210projects.Redneck.Main.gStatisticScreen;
import static ru.m210projects.Redneck.Main.game;

import com.badlogic.gdx.Gdx;

public class EndScreen {
	
	public void episode1()
	{
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				String filename = "turdmov.anm";
				if(currentGame.getCON().type == RR66) 
					filename = "turd66.anm";

				if (gAnmScreen.init(filename, 6)) {
					gAnmScreen.setCallback(new Runnable() {
						@Override
						public void run() {
							game.changeScreen(gStatisticScreen);
						}
					});
					game.setScreen(gAnmScreen.escSkipping(true));
				} else 
					game.changeScreen(gStatisticScreen);
			}
		});
	}
	
	public void episode2()
	{
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				String filename = "rr_outro.anm";
				if(currentGame.getCON().type == RR66) 
					filename = "end66.anm";
		
				if (gAnmScreen.init(filename, 5)) {
					gAnmScreen.setCallback(new Runnable() {
						@Override
						public void run() {
							game.changeScreen(gStatisticScreen);
						}
					});
					game.setScreen(gAnmScreen.escSkipping(true));
				} else 
					game.changeScreen(gStatisticScreen);
			}
		});
	}
}
