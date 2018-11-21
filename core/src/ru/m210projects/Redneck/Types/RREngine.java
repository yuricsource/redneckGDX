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

import static ru.m210projects.Redneck.Main.gpmanager;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildMessage;
import ru.m210projects.Build.Audio.BAudio;
import ru.m210projects.Redneck.Config;

public class RREngine extends Engine {

	private long timerskipticks;
	private long timernexttick;
	
	public RREngine(BuildMessage message, BAudio audio, boolean releasedEngine)
			throws Exception {
		super(message, audio, releasedEngine);
		compatibleMode = true;
//		SETSPRITEZ = 1;
	}
	
	public void inittimer(int tickspersecond) {
		super.inittimer(tickspersecond);
		
		timerskipticks = (timerfreq / timerticspersec) * TICSPERFRAME;
		updatesmoothticks();
	}
	
	static boolean key = false;
	public void sampletimer() {
		if (timerfreq == 0)
			return;

		long n = (getticks() * timerticspersec / timerfreq) - timerlastsample;  
		if (n > 0) {
			totalclock += n;
			
//			if(gm == MODE_DEMO) {
//				if(Gdx.input.isKeyPressed(Keys.W)) {
//					if(!key) {
//						totalclock += 4;
////						if(totalclock < 3540) totalclock = 3540;
//						if(Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))
//							key = true;
//					}
//				} else key = false;
//			}
				
			timerlastsample += n;
		}
	}
	
	public int getsmoothratio()
	{
		return (int) (((System.currentTimeMillis() - timernexttick) / (float) timerskipticks) * 65536);
	}
	
	public void updatesmoothticks()
	{
		timernexttick = getticks();
	}
	
	@Override
	public void faketimerhandler() {}
	
	public void timerhandler()
	{
		if(totalclock >= ototalclock && ready2send) {
	    	ototalclock += TICSPERFRAME;
	    	
	    	handleevents();
			if(gpmanager != null)
				gpmanager.handler();
			
	    	input();
	    }
	}

	
	public void setanisotropy(Config cfg, int anisotropy)
	{
		glanisotropy = anisotropy;
		render.gltexapplyprops();
		cfg.anisotropy = glanisotropy;
	}
	
	public void setwidescreen(Config cfg, boolean widescreen)
	{
		r_usenewaspect = widescreen ? 1 : 0;
		setaspect_new();
		cfg.widescreen = r_usenewaspect;
	}

}
