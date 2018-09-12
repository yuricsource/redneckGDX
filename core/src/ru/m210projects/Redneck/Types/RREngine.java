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

import static java.lang.Math.*;
import static ru.m210projects.Redneck.Main.gpmanager;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Redneck.Controls.getinput;
import static ru.m210projects.Redneck.Redneck.gShowMenu;
import static ru.m210projects.Redneck.Globals.MOVEFIFOSIZ;
import static ru.m210projects.Redneck.Globals.TICSPERFRAME;
import static ru.m210projects.Redneck.Globals.TIMERUPDATESIZ;
import static ru.m210projects.Redneck.Globals.avgavel;
import static ru.m210projects.Redneck.Globals.avgbits;
import static ru.m210projects.Redneck.Globals.avgfvel;
import static ru.m210projects.Redneck.Globals.avghorz;
import static ru.m210projects.Redneck.Globals.avgsvel;
import static ru.m210projects.Redneck.Globals.bufferjitter;
import static ru.m210projects.Redneck.Globals.inputfifo;
import static ru.m210projects.Redneck.Globals.loc;
import static ru.m210projects.Redneck.Globals.movefifoend;
import static ru.m210projects.Redneck.Globals.movefifosendplc;
import static ru.m210projects.Redneck.Globals.movesperpacket;
import static ru.m210projects.Redneck.Globals.mymaxlag;
import static ru.m210projects.Redneck.Globals.myminlag;
import static ru.m210projects.Redneck.Globals.otherminlag;
import static ru.m210projects.Redneck.Globals.ototalclock;
import static ru.m210projects.Redneck.Globals.playerquitflag;
import static ru.m210projects.Redneck.Globals.ready2send;
import static ru.m210projects.Redneck.Globals.syncval;
import static ru.m210projects.Redneck.Globals.syncvalhead;
import static ru.m210projects.Redneck.Globals.syncvaltail;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Network.getpackets;
import static ru.m210projects.Redneck.Network.kPacketEmpty;
import static ru.m210projects.Redneck.Network.kPacketMasterFrame;
import static ru.m210projects.Redneck.Network.kPacketSlaveFrame;
import static ru.m210projects.Redneck.Network.packbuf;
import static ru.m210projects.Redneck.Player.computergetinput;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Audio.BAudio;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.Message;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Config;

public class RREngine extends Engine {

	private long timerskipticks;
	private long timernexttick;
	
	public RREngine(Message message, BAudio audio, boolean releasedEngine)
			throws Exception {
		super(message, audio, releasedEngine);
		compatibleMode = true;
		SETSPRITEZ = 1;
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

		int n = (int) ((getticks() * timerticspersec / timerfreq) - timerlastsample);  
		if (n > 0) {
			totalclock += n;
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
	public void faketimerhandler() {
		Input[] osyn, nsyn;

	    if ((totalclock < ototalclock+TICSPERFRAME) || !ready2send) return;
	    ototalclock += TICSPERFRAME;

	    getpackets(); if (getoutputcirclesize() >= 16) return;
	    
	    if((gShowMenu || Console.IsShown()) && ud.multimode < 2) return;

	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	        if (i != myconnectindex)
	            if (movefifoend[i] < movefifoend[myconnectindex]-200) return;

	    handleevents();
		if(gpmanager != null)
			gpmanager.handler();
		getinput(myconnectindex);

		avgfvel += loc.fvel;
		avgsvel += loc.svel;
		avgavel += loc.avel;
		avghorz += loc.horz;
		avgbits |= loc.bits;

		if ((movefifoend[myconnectindex]&(movesperpacket-1)) != 0)
		{
			inputfifo[movefifoend[myconnectindex]&(MOVEFIFOSIZ-1)][myconnectindex].
			copy(inputfifo[(movefifoend[myconnectindex]-1)&(MOVEFIFOSIZ-1)][myconnectindex]);
			movefifoend[myconnectindex]++;
			return;
		}
	     
	     nsyn = inputfifo[movefifoend[myconnectindex]&(MOVEFIFOSIZ-1)];
	     nsyn[myconnectindex].fvel = (short) (avgfvel/movesperpacket);
	     nsyn[myconnectindex].svel = (short) (avgsvel/movesperpacket);
	     nsyn[myconnectindex].avel = (avgavel/movesperpacket);
	     nsyn[myconnectindex].horz = avghorz/movesperpacket;
	     nsyn[myconnectindex].bits = avgbits;
	     avgfvel = avgsvel = avgbits = 0;
	     avghorz = avgavel = 0;
	     movefifoend[myconnectindex]++;

	     if (numplayers < 2)
	     {
	          if (ud.multimode > 1) for(int i=connecthead;i>=0;i=connectpoint2[i])
	              if(i != myconnectindex)
	              {
	                  if(ud.playerai != 0)
	                      computergetinput(i,inputfifo[movefifoend[i]&(MOVEFIFOSIZ-1)][i]);
	                  movefifoend[i]++;
	              }
	          return;
	     }

	    int i;
	    for(i=connecthead;i>=0;i=connectpoint2[i])
	        if (i != myconnectindex)
	        {
	            int k = (movefifoend[myconnectindex]-1)-movefifoend[i];
	            myminlag[i] = min(myminlag[i],k);
	            mymaxlag = max(mymaxlag,k);
	        }

	    if (((movefifoend[myconnectindex]-1)&(TIMERUPDATESIZ-1)) == 0)
	    {
	        i = mymaxlag-bufferjitter; mymaxlag = 0;
	        if (i > 0) bufferjitter += ((3+i)>>2);
	        else if (i < 0) bufferjitter -= ((1-i)>>2);
	    }
	    
	    if (myconnectindex != connecthead)   //Slave
	    {
	            //Fix timers and buffer/jitter value
	        if (((movefifoend[myconnectindex]-1)&(TIMERUPDATESIZ-1)) == 0)
	        {
	            i = myminlag[connecthead]-otherminlag;
	            if (klabs(i) > 8) i >>= 1;
	            else if (klabs(i) > 2) i = ksgn(i);
	            else i = 0;

	            totalclock -= TICSPERFRAME*i;
	            /*myminlag[connecthead] -= i;*/ otherminlag += i;

	            for(i=connecthead;i>=0;i=connectpoint2[i])
	                myminlag[i] = 0x7fffffff;
	        }

	        packbuf[0] = kPacketSlaveFrame; packbuf[1] = 0; int j = 2;

	        osyn = inputfifo[(movefifoend[myconnectindex]-2)&(MOVEFIFOSIZ-1)];
	        nsyn = inputfifo[(movefifoend[myconnectindex]-1)&(MOVEFIFOSIZ-1)];

	        if (nsyn[myconnectindex].fvel != osyn[myconnectindex].fvel)
	        {
	            packbuf[j++] = (byte) nsyn[myconnectindex].fvel;
	            packbuf[j++] = (byte)(nsyn[myconnectindex].fvel>>8);
	            packbuf[1] |= 1;
	        }
	        if (nsyn[myconnectindex].svel != osyn[myconnectindex].svel)
	        {
	            packbuf[j++] = (byte) nsyn[myconnectindex].svel;
	            packbuf[j++] = (byte)(nsyn[myconnectindex].svel>>8);
	            packbuf[1] |= 2;
	        }
	        if (nsyn[myconnectindex].avel != osyn[myconnectindex].avel)
	        {
	        	LittleEndian.putFloat(packbuf, j, nsyn[myconnectindex].avel);
	        	j += 4;
	            packbuf[1] |= 4;
	        }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x000000ff) != 0) { packbuf[j++] = (byte) (nsyn[myconnectindex].bits&255); packbuf[1] |= 8; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x0000ff00) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>8)&255); packbuf[1] |= 16; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0x00ff0000) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>16)&255); packbuf[1] |= 32; }
	        if (((nsyn[myconnectindex].bits^osyn[myconnectindex].bits)&0xff000000) != 0) { packbuf[j++] = (byte) ((nsyn[myconnectindex].bits>>24)&255); packbuf[1] |= 64; }
	        if (nsyn[myconnectindex].horz != osyn[myconnectindex].horz)
	        {
	            LittleEndian.putFloat(packbuf, j, nsyn[myconnectindex].horz);
	        	j += 4;
	            packbuf[1] |= 128;
	        }

	        while (syncvalhead[myconnectindex] != syncvaltail)
	        {
	            packbuf[j++] = syncval[myconnectindex][syncvaltail&(MOVEFIFOSIZ-1)];
	            syncvaltail++;
	        }

	        sendpacket(connecthead,packbuf,j);
	        return;
	    }

			  //This allows packet resends
	    for(i=connecthead;i>=0;i=connectpoint2[i])
	        if (movefifoend[i] <= movefifosendplc)
	        {
	            packbuf[0] = kPacketEmpty;
	            for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	               {
				   sendpacket(i,packbuf,1);
				   }
	            return;
	        }

	    while (true)  //Master 
	    {
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	            if (playerquitflag[i] != 0 && (movefifoend[i] <= movefifosendplc)) return;

	        osyn = inputfifo[(movefifosendplc-1)&(MOVEFIFOSIZ-1)];
	        nsyn = inputfifo[(movefifosendplc  )&(MOVEFIFOSIZ-1)];
	        
	            //MASTER -> SLAVE packet
	        packbuf[0] = kPacketMasterFrame; int j = 1;

	            //Fix timers and buffer/jitter value
	        if ((movefifosendplc&(TIMERUPDATESIZ-1)) == 0)
	        {
	            for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	                if (playerquitflag[i] != 0)
	                packbuf[j++] = (byte) min(max(myminlag[i],-128),127);

	            for(i=connecthead;i>=0;i=connectpoint2[i])
	                myminlag[i] = 0x7fffffff;
	        }

	        int k = j;
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	           j += playerquitflag[i];
	        for(i=connecthead;i>=0;i=connectpoint2[i])
	        {
	            if (playerquitflag[i] == 0) continue;

	            packbuf[k] = 0;
	            if (nsyn[i].fvel != osyn[i].fvel)
	            {
	                packbuf[j++] = (byte)nsyn[i].fvel;
	                packbuf[j++] = (byte)(nsyn[i].fvel>>8);
	                packbuf[k] |= 1;
	            }
	            if (nsyn[i].svel != osyn[i].svel)
	            {
	                packbuf[j++] = (byte)nsyn[i].svel;
	                packbuf[j++] = (byte)(nsyn[i].svel>>8);
	                packbuf[k] |= 2;
	            }
	            if (nsyn[i].avel != osyn[i].avel)
	            {
	                LittleEndian.putFloat(packbuf, j, nsyn[i].avel);
		        	j += 4;
	                packbuf[k] |= 4;
	            }
	            if (((nsyn[i].bits^osyn[i].bits)&0x000000ff) != 0) { packbuf[j++] = (byte) (nsyn[i].bits&255); packbuf[k] |= 8; }
	            if (((nsyn[i].bits^osyn[i].bits)&0x0000ff00) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>8)&255); packbuf[k] |= 16; }
	            if (((nsyn[i].bits^osyn[i].bits)&0x00ff0000) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>16)&255); packbuf[k] |= 32; }
	            if (((nsyn[i].bits^osyn[i].bits)&0xff000000) != 0) { packbuf[j++] = (byte) ((nsyn[i].bits>>24)&255); packbuf[k] |= 64; }
	            if (nsyn[i].horz != osyn[i].horz)
	            {
	                LittleEndian.putFloat(packbuf, j, nsyn[i].horz);
		        	j += 4;
	                packbuf[k] |= 128;
	            }
	            k++;
	        }

	      	while (syncvalhead[myconnectindex] != syncvaltail)
	        {
	            packbuf[j++] = syncval[myconnectindex][syncvaltail&(MOVEFIFOSIZ-1)];
	            syncvaltail++;
	        }

	        for(i=connectpoint2[connecthead];i>=0;i=connectpoint2[i])
	            if (playerquitflag[i] != 0)
	            {
	                 sendpacket(i,packbuf,j);
	                 if ((nsyn[i].bits&(1<<26)) != 0)
	                    playerquitflag[i] = 0;
	            }

	        movefifosendplc += movesperpacket;
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
