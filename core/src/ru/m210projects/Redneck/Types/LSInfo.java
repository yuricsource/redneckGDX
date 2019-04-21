// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Kirill Klimenko-KLIMaka 
// and Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.currentGame;

import java.nio.ByteBuffer;

public class LSInfo {
	public int skill;
	public int episode;
	public int level;
	public String info;
	public String date;
	public String addonfile;
	
	public void read(ByteBuffer bb)
	{
		bb.getInt(); //ud.multimode
		episode = bb.getInt() + 1;
		level = bb.getInt() + 1;
		skill = bb.getInt();
		if(currentGame.getCON().type == RRRA) 
			skill++;
		update();
	}
	
	public void update()
	{
		info = "Episode:" + episode + " / Level:" + level + " / Skill:" + (skill != 6 ? skill : "CM");
	}
	
	public void clear()
	{
		skill = 0;
		episode = 0;
		level = 0;
		info = "Empty slot";
		date = null;
		addonfile = null;
	}
}
