// This file is part of RedneckGDX.
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

package ru.m210projects.Redneck;

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Network.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Redneck.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Menus.RRMenu.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Types.RTS.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Config.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Input.KeyInput;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.VOC;

public class Controls {

	public static final int TURBOTURNTIME = (TICRATE/8); // 7
	public static final int NORMALTURN  = 15;
	public static final int PREAMBLETURN = 5;
	public static final int NORMALKEYMOVE = 40;
	public static final int MAXVEL     =  ((NORMALKEYMOVE*2)+10);
	public static final int MAXSVEL     = ((NORMALKEYMOVE*2)+10);
	public static final int MAXANGVEL  =  127;
	public static final int MAXHORIZ   =  127;
	
	public static int turnheldtime; //MED
	public static int lastcontroltime; //MED
	private static int nonsharedtimer;

	public static int oldPosX;
	public static int oldPosY;
	public static void resetMousePos()
	{
		Gdx.input.setCursorPosition(xdim / 2, 0);
		oldPosX = Gdx.input.getX();
		oldPosY = Gdx.input.getY();
	}
	
	public static boolean ctrlPadStatusOnce(int deviceIndex, int buttonCode)
	{
		return gpmanager.isValidDevice(deviceIndex) && gpmanager.buttonStatusOnce(deviceIndex, buttonCode);
	}
	
	public static boolean ctrlPadStatus(int deviceIndex, int buttonCode)
	{
		return gpmanager.isValidDevice(deviceIndex) && gpmanager.buttonStatus(deviceIndex, buttonCode);
	}
	
	public static boolean[] maxisstatus = new boolean[keynames.length];
	public static boolean ctrlAxisStatusOnce(int keyId)
	{
		if(keyId >= 0 && maxisstatus[keyId]) {
			maxisstatus[keyId] = false;
			return true;
		}
		return false;
	}
	
	public static boolean ctrlAxisStatus(int keyId)
	{
		if(keyId >= 0 && maxisstatus[keyId])
			return true;

		return false;
	}

	public static boolean ctrlGetInputKey(int keyName, boolean once) {
		final KeyInput input = getInput();
		final int key1 = cfg.primarykeys[keyName];
		final int key2 = cfg.secondkeys[keyName];
		final int keyM = cfg.mousekeys[keyName];
		final int keyG = cfg.gpadkeys[keyName];

		if (once) {
			return input.keyStatusOnce(key1)
					|| input.keyStatusOnce(key2)
					|| input.keyStatusOnce(keyM)
					|| ctrlAxisStatusOnce(keyName)
					|| keyName > Turn_Right && ctrlPadStatusOnce(cfg.gJoyDevice, keyG);
		} else {
			return input.keyStatus(key1)
					|| input.keyStatus(key2)
					|| input.keyStatus(keyM)
					|| ctrlAxisStatus(keyName)
					|| keyName > Turn_Right && ctrlPadStatus(cfg.gJoyDevice, keyG);
		}
	}
	
	public static boolean ctrlKeyStatusOnce(int keyId)
	{
		return getInput().keyStatusOnce(keyId);
	}
	
	public static boolean ctrlKeyStatus(int keyId)
	{
		return getInput().keyStatus(keyId);
	}

	public static  void getinput(int snum)
	{
		float daang;
	    int tics;
	    boolean running;
	    int turnamount;
	    int keymove;
	    
	    PlayerStruct p = ps[snum];
	    
	    float mousx = 0, mousy = 0;

	    if( gShowMenu || Console.IsShown() || MODE_TYPE || (ud.pause_on != 0 && !ctrlKeyStatus(KEY_PAUSE)) )
	    {
	    	loc.fvel = vel = 0;
	    	loc.svel = svel = 0;
	    	loc.avel = angvel = 0;
	    	loc.horz = horiz = 0;
	    	loc.bits = ((gamequit)<<26);
	    	
	    	if(Console.IsShown())
	    		MODE_TYPE = false;
	         
	    	if(MODE_TYPE)
	    	{
	    		int input = getInput().putMessage(getInput().getMessageBuffer().length, false, false, true);
	 	    	if(input != 0) MODE_TYPE = false;
	 	    	if(input == 1) {
	 	    		SendMessage(getInput().getMessageBuffer(), getInput().getMessageLength());
	 	    	}
	    	}

	    	return;
	    }

	    int dx = Gdx.input.getX() - oldPosX;
		int dy = Gdx.input.getY() - oldPosY;
		
		float sensscale = cfg.gSensitivity / 65536.0f;
		float xscale = sensscale / 2;
		float yscale = sensscale;

		mousx = dx * xscale;
		mousy = dy * yscale;

		resetMousePos();

	    tics = totalclock-lastcontroltime;
	    lastcontroltime = totalclock;
	    
	    if(ctrlGetInputKey(Mouse_Aiming, true))
	    {
	    	cfg.gMouseAim = !cfg.gMouseAim;
    		FTA(44+(cfg.gMouseAim?1:0),p);
	    }

	    if(multiflag == 1)
	    {
	        loc.bits =   1<<17;
	        loc.bits |=   multiwhat<<18;
	        loc.bits |=   multipos<<19;
	        multiflag = 0;
	        return;
	    }

	    loc.bits =   ctrlGetInputKey(Jump, false)?1:0;
	    loc.bits |=   ctrlGetInputKey(Crouch, false)?2:0;
	    loc.bits |=   ctrlGetInputKey(Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(Aim_Up, false)?8:0;
	    loc.bits |=   ctrlGetInputKey(Aim_Down, false)?16:0;
	    loc.bits |=   ctrlGetInputKey(Run, false)?32:0;
	    loc.bits |=   ctrlGetInputKey(Tilt_Right, false)?128:0;
	    
	    if ( ps[snum].alcohol_amount <= 88 )
	    	loc.bits |= ctrlGetInputKey(Tilt_Left, false)?64:0;
	    else loc.bits = loc.bits | 64;
	    
	    if ( ps[snum].alcohol_amount > 99 )
	    	loc.bits |= 1 << 14;
	   
	    for(int i = 0; i < 10; i++)
			if(ctrlGetInputKey(i + Weapon_1, false))
				loc.bits |= (i + 1)<<8;
	    if(ctrlGetInputKey(Previous_Weapon, true)) 
	    	loc.bits |= (11)<<8;
	    if(ctrlGetInputKey(Next_Weapon, true)) 
	    	loc.bits |= (12)<<8;
	    if(ctrlGetInputKey(Last_Weapon_Switch, true)) 
	    	loc.bits |= (13)<<8;
	    
	    loc.bits |=   ctrlGetInputKey(Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(Look_Up, false)? 1 << 13 : 0;
	    loc.bits |=   ctrlGetInputKey(Look_Down, false)? 1 << 14 : 0;
	    loc.bits |=   ctrlGetInputKey(Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(Wiskey, false)? 1 << 16 : 0;
	    loc.bits |=   ctrlGetInputKey(Aim_Center, false)? 1 << 18 : 0;
	    loc.bits |=   ctrlGetInputKey(Holster_Weapon, false)? 1 << 19 : 0;
	    loc.bits |=   ctrlGetInputKey(Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   getInput().keyStatus(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(Quick_pee, false)? 1 << 22 : 0;
	    loc.bits |=   cfg.gMouseAim? 1 << 23 : 0;
	    loc.bits |=   ctrlGetInputKey(Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(Turn_Around, false)? 1 << 28 : 0;
	    loc.bits |=   ctrlGetInputKey(Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(Inventory_Use, false)? 1 << 30 : 0;
	    loc.bits |=   getInput().keyStatus(Keys.ESCAPE)? 1 << 31 : 0;

	    if((loc.bits&2) != 0) p.crouch_toggle = 0;
	    if(ctrlGetInputKey(Crouch_toggle, true))
	    	p.crouch_toggle ^= 1;
	    if(p.crouch_toggle == 1)
	    	loc.bits |= 2;
	    
	    running = ((ud.auto_run == 0 && (loc.bits&32) != 0) || ((loc.bits&32) == 0 && ud.auto_run != 0));

	    svel = vel = 0;
	    horiz = angvel = 0;

	    if (running)
	    {
	        turnamount = NORMALTURN<<1;
	        keymove = NORMALKEYMOVE<<1;
	    }
	    else
	    {
	        turnamount = NORMALTURN;
	        keymove = NORMALKEYMOVE;
	    }

	    if (ctrlGetInputKey(Strafe, false))
	    {
	        if ( ctrlGetInputKey(Turn_Left, false))
	           svel -= -keymove;
	        if ( ctrlGetInputKey(Turn_Right, false))
	           svel -= keymove; 
	        svel = (short) BClipRange(svel - (mousx * cfg.gMouseStrafeSpeed / 65536f), -keymove, keymove);
	    }
	    else
	    {
	    	angvel = BClipRange(angvel + (mousx * cfg.gMouseTurnSpeed / 65536f), -1024, 1024);
	        if ( ctrlGetInputKey(Turn_Left, false) ) {
	           turnheldtime += tics;
	           if (turnheldtime>=TURBOTURNTIME)
	              angvel -= turnamount;
	           else
	              angvel -= PREAMBLETURN;  
	        }
	        else if ( ctrlGetInputKey(Turn_Right, false) )
	        {
	           turnheldtime += tics;
	           if (turnheldtime>=TURBOTURNTIME)
	              angvel += turnamount;
	           else
	              angvel += PREAMBLETURN;
	        } else turnheldtime=0;
	           
	    }

	    if ( ctrlGetInputKey(Strafe_Left, false) )
	        svel += keymove;
	    if ( ctrlGetInputKey(Strafe_Right, false) )
	        svel += -keymove;

	    if ( ps[snum].alcohol_amount < 66 || ps[snum].alcohol_amount > 87)
	    {
		    if ( ctrlGetInputKey(Move_Forward, false) )
		        vel += keymove;
		    if ( ctrlGetInputKey(Move_Backward, false) )
		        vel += -keymove;
	    } else {
	    	if ( ctrlGetInputKey(Move_Forward, false) ) 
	    	{
		        vel += keymove;
		        if ( (ps[snum].alcohol_amount & 1) != 0 )
		        	svel += keymove;
		        else svel -= keymove;
	    	}
		    if ( ctrlGetInputKey(Move_Backward, false) ) 
		    {
		        vel += -keymove;
		        if ( (ps[snum].alcohol_amount & 1) != 0 )
		        	svel -= keymove;
		        else svel += keymove;
		    }
	    }
	    
	    if(cfg.gMouseAim) {
			horiz = BClipRange(horiz-(mousy * cfg.gMouseLookSpeed / 65536f), -(ydim>>1), 100+(ydim>>1));
            if ( cfg.gInvertmouse )
            	horiz = -horiz;
        } else 
        	vel =  (short) BClipRange(vel - (mousy * cfg.gMouseMoveSpeed / 65536f), -4 * keymove, 4 * keymove);
	    
	    if(gpmanager.isValidDevice(cfg.gJoyDevice)) {
			Vector2 stick1 = gpmanager.getStickValue(cfg.gJoyDevice, cfg.gJoyTurnAxis, cfg.gJoyLookAxis);
			float lookx = stick1.x;
			float looky = stick1.y;
			if(cfg.gJoyInvert) looky *= -1;
			
			if(looky != 0) {
				float k = 1.0f;
				horiz = BClipRange(horiz - k * looky * cfg.gJoyLookSpeed / 65536f, -(ydim>>1), 100+(ydim>>1));
			}
			
			if(lookx != 0) {
				float k = 64;
				angvel = BClipRange(angvel + k * lookx * cfg.gJoyTurnSpeed / 65536f, -1024, 1024);
			}

			Vector2 stick2 = gpmanager.getStickValue(cfg.gJoyDevice, cfg.gJoyStrafeAxis, cfg.gJoyMoveAxis);
			float plrx = stick2.x;
			float plry = stick2.y;

			if(plry != 0) {
				vel = (short) BClipRange(vel - (80 * plry), -4 * keymove, 4 * keymove);
			}
			if(plrx != 0) {
				svel = (short) BClipRange(svel - (80 * plrx), -4 * keymove, 4 * keymove);
			}
        }

	    if(vel < -MAXVEL) vel = -MAXVEL;
	    if(vel > MAXVEL) vel = MAXVEL;
	    if(svel < -MAXSVEL) svel = -MAXSVEL;
	    if(svel > MAXSVEL) svel = MAXSVEL;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;
	    if(horiz < -MAXHORIZ) horiz = -MAXHORIZ;
	    if(horiz > MAXHORIZ) horiz = MAXHORIZ;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    if( numplayers > 1 )
	        daang = myang;
	    else daang = p.ang;

	    int momx = (int) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    int momy = (int) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += (int) (svel * BSinAngle(BClampAngle(daang )) / 512.0f);
	    momy += (int) (svel * BCosAngle(BClampAngle(daang + 1024)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = (short) momx;
	    loc.svel = (short) momy;
	    loc.avel = angvel;
	    loc.horz = horiz;
	}
	
	public static void motoinput(int snum)
	{
		float daang;
	    int tics;

	    PlayerStruct p = ps[snum];

	    if( gShowMenu || Console.IsShown() || MODE_TYPE || (ud.pause_on != 0 && !ctrlKeyStatus(KEY_PAUSE)) )
	    {
	    	loc.fvel = vel = 0;
	    	loc.svel = svel = 0;
	    	loc.avel = angvel = 0;
	    	loc.horz = horiz = 0;
	    	loc.bits = ((gamequit)<<26);
	    	
	    	if(Console.IsShown())
	    		MODE_TYPE = false;
	         
	    	if(MODE_TYPE)
	    	{
	    		int input = getInput().putMessage(getInput().getMessageBuffer().length, false, false, true);
	 	    	if(input != 0) MODE_TYPE = false;
	 	    	if(input == 1) {
	 	    		SendMessage(getInput().getMessageBuffer(), getInput().getMessageLength());
	 	    	}
	    	}

	    	return;
	    }
	    
	    tics = totalclock-lastcontroltime;
	    lastcontroltime = totalclock;
	    
	    loc.bits =   ctrlGetInputKey(Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(Wiskey, false)? 1 << 16 : 0;
	    
	    if(multiflag == 1)
	    {
	        loc.bits =   1<<17;
	        loc.bits |=   multiwhat<<18;
	        loc.bits |=   multipos<<19;
	        multiflag = 0;
	        return;
	    }
	    
	    loc.bits |=   ctrlGetInputKey(Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   getInput().keyStatus(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(Inventory_Use, false)? 1 << 30 : 0;
	    
	    angvel = 0;
	    horiz = 0;
	    vel = 0;
	    svel = 0;
	    
	    boolean left = ctrlGetInputKey(Turn_Left, false) || ctrlGetInputKey(Strafe_Left, false);
	    boolean right = ctrlGetInputKey(Turn_Right, false) || ctrlGetInputKey(Strafe_Right, false);
	    int bike_turn = 0; 
	    if(gpmanager.isValidDevice(cfg.gJoyDevice)) {
	    	Vector2 stick1 = gpmanager.getStickValue(cfg.gJoyDevice, cfg.gJoyTurnAxis, cfg.gJoyLookAxis);
	    	bike_turn = (int) stick1.x;
	    }
	    if ( bike_turn > 0 ) left = true;
	    if ( bike_turn < 0 ) right = true;
	    
	    if ( p.CarVar1 == 0 )
	    {
	    	loc.bits |=   ctrlGetInputKey(Move_Forward, false)?1:0;
	    	loc.bits |=   ctrlGetInputKey(Run, false)?2:0;
	    	loc.bits |=   ctrlGetInputKey(Move_Backward, false)?8:0;
	    }
	    
	    if(left) loc.bits |= 16;
	    if(right) loc.bits |= 64;
	    
	    boolean revers = (p.CarSpeed <= 0);
	    if ( p.CarSpeed != 0 && p.on_ground )
	    {
	    	if ( left || p.CarVar2 < 0 )
	        {
	    		turnheldtime += tics;
	    		p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
	    		if ( turnheldtime >= 15 && p.CarSpeed > 0 )
	            {
	    			if ( bike_turn != 0 )
	                	angvel -= 20;
	                else angvel -= 10; 
	            }
	    		else {
	    			if ( bike_turn != 0 )
		    			angvel -= 10 * (revers ? -1 : 1);
		            else angvel -= 3 * (revers ? -1 : 1);
	    		}
	        }
	    	else if ( right || p.CarVar2 > 0 )
	    	{
	    		turnheldtime += tics;
	    		p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
	    		if ( turnheldtime >= 15 && p.CarSpeed > 0 )
	            {
	    			if ( bike_turn != 0 )
	                	angvel += 20;
	                else angvel += 10; 
	            }
	    		else {
	    			if ( bike_turn != 0 )
		    			angvel += 10 * (revers ? -1 : 1);
		            else angvel += 3 * (revers ? -1 : 1);
	    		}
	    	}
	    	else
	        {
	    		turnheldtime = 0;
	    		if ( p.TiltStatus < 0 ) p.TiltStatus++;
	    		if ( p.TiltStatus > 0 ) p.TiltStatus--;
	        }
	    }
	    else if ( left )
	        p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
	    else if ( right )
	    	p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
	    
	    if ( p.CarVar1 != 0 )
	    	p.CarSpeed = 0;
	    
	    vel += p.CarSpeed;
	    if ( vel < -15 ) vel = -15;
	    if ( vel > 120 ) vel = 120;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    if( numplayers > 1 )
	        daang = myang;
	    else daang = p.ang;
	    
	    short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = momx;
	    loc.svel = momy;

	    loc.avel = angvel;
	    loc.horz = horiz;
	    
	    if(p.CarSpeed < 80) {
		    int dx = Gdx.input.getX() - oldPosX;
			
			float sensscale = cfg.gSensitivity / 65536.0f;
			float xscale = sensscale / 2;
			float mousx = dx * xscale;
		    p.look_ang += mousx;
	    }
	    resetMousePos();
	}
	
	public static void boatinput(int snum)
	{
		float daang;
	    int tics;

	    PlayerStruct p = ps[snum];

	    if( gShowMenu || Console.IsShown() || MODE_TYPE || (ud.pause_on != 0 && !ctrlKeyStatus(KEY_PAUSE)) )
	    {
	    	loc.fvel = vel = 0;
	    	loc.svel = svel = 0;
	    	loc.avel = angvel = 0;
	    	loc.horz = horiz = 0;
	    	loc.bits = ((gamequit)<<26);
	    	
	    	if(Console.IsShown())
	    		MODE_TYPE = false;
	         
	    	if(MODE_TYPE)
	    	{
	    		int input = getInput().putMessage(getInput().getMessageBuffer().length, false, false, true);
	 	    	if(input != 0) MODE_TYPE = false;
	 	    	if(input == 1) {
	 	    		SendMessage(getInput().getMessageBuffer(), getInput().getMessageLength());
	 	    	}
	    	}

	    	return;
	    }
	    
	    tics = totalclock-lastcontroltime;
	    lastcontroltime = totalclock;
	    
	    loc.bits =   ctrlGetInputKey(Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(Wiskey, false)? 1 << 16 : 0;
	    
	    if(multiflag == 1)
	    {
	        loc.bits =   1<<17;
	        loc.bits |=   multiwhat<<18;
	        loc.bits |=   multipos<<19;
	        multiflag = 0;
	        return;
	    }
	    
	    loc.bits |=   ctrlGetInputKey(Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   getInput().keyStatus(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(Inventory_Use, false)? 1 << 30 : 0;
	    
	    angvel = 0;
	    horiz = 0;
	    vel = 0;
	    svel = 0;
	    
	    boolean left = ctrlGetInputKey(Turn_Left, false) || ctrlGetInputKey(Strafe_Left, false);
	    boolean right = ctrlGetInputKey(Turn_Right, false) || ctrlGetInputKey(Strafe_Right, false);
	    int bike_turn = 0; 
	    if(gpmanager.isValidDevice(cfg.gJoyDevice)) {
	    	Vector2 stick1 = gpmanager.getStickValue(cfg.gJoyDevice, cfg.gJoyTurnAxis, cfg.gJoyLookAxis);
	    	bike_turn = (int) stick1.x;
	    }
	    if ( bike_turn > 0 ) left = true;
	    if ( bike_turn < 0 ) right = true;
	    
	    if ( p.CarVar1 == 0 )
	    {
	    	loc.bits |=   ctrlGetInputKey(Move_Forward, false)?1:0;
	    	loc.bits |=   ctrlGetInputKey(Run, false)?2:0;
	    	loc.bits |=   ctrlGetInputKey(Move_Backward, false)?8:0;
	    }
	    
	    if(left) loc.bits |= 16;
	    if(right) loc.bits |= 64;
	    
	    if ( p.CarSpeed != 0 )
	    {
	    	if ( left || p.CarVar2 < 0 )
	        {
	    		turnheldtime += tics;
	    		if ( p.NotOnWater == 0)
	    			p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
	    		if ( turnheldtime >= 15 && p.CarSpeed != 0 )
	            {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel -= 6;
	    				else angvel -= 3; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel -= 20;
		                else angvel -= 10; 
	    			}
	            }
	    		else if ( turnheldtime < 15 && p.CarSpeed != 0 ) {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel -= 2;
	    				else angvel--; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel -= 6;
		                else angvel -= 3; 
	    			}
	    		}
	        }
	    	else if ( right || p.CarVar2 > 0 )
	    	{
	    		turnheldtime += tics;
	    		if ( p.NotOnWater == 0)
	    			p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
	    		if ( turnheldtime >= 15 && p.CarSpeed != 0 )
	            {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel += 6;
	    				else angvel += 3; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel += 20;
		                else angvel += 10; 
	    			}
	            }
	    		else if ( turnheldtime < 15 && p.CarSpeed != 0 ) {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel += 2;
	    				else angvel++; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel += 6;
		                else angvel += 3; 
	    			}
	    		}
	    	}
	    	else if ( p.NotOnWater == 0)
	        {
	    		turnheldtime = 0;
	    		if ( p.TiltStatus < 0 ) p.TiltStatus++;
	    		if ( p.TiltStatus > 0 ) p.TiltStatus--;
	        }
	    }
	    else if ( p.NotOnWater == 0) {
	    	if ( left )
	    		p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
	    	else if ( right )
	    		p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
	    }

	    vel += p.CarSpeed;
	    if ( vel < -15 ) vel = -15;
	    if ( vel > 120 ) vel = 120;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    if( numplayers > 1 )
	        daang = myang;
	    else daang = p.ang;
	    
	    short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = momx;
	    loc.svel = momy;

	    loc.avel = angvel;
	    loc.horz = horiz;
	    
	    if(p.CarSpeed < 80) {
		    int dx = Gdx.input.getX() - oldPosX;
			
			float sensscale = cfg.gSensitivity / 65536.0f;
			float xscale = sensscale / 2;
			float mousx = dx * xscale;
		    p.look_ang += mousx;
	    }
	    resetMousePos();
	}
	
	public static void nonsharedkeys()
	{
		if(gShowMenu || Console.IsShown() || MODE_TYPE) return;
		
		if(ctrlKeyStatus(Keys.SHIFT_LEFT) && ctrlKeyStatusOnce(Keys.F5))
		{
			int music_select = 11 * musicvolume + musiclevel;
			music_select++;
			if(music_select >= 44) 
				music_select = 0;
			
			musicvolume = music_select / 11;
			musiclevel = music_select % 11;
			
			buildString(currentGame.getCON().fta_quotes[26], 0, "PLAYING ", currentGame.getCON().music_fn[musicvolume][musiclevel]);
			sndPlayMusic(currentGame.getCON().music_fn[musicvolume][musiclevel]);
            FTA(26, ps[myconnectindex]);
            return;
		}
		
		if(ctrlKeyStatus(Keys.ALT_LEFT) || ctrlKeyStatus(Keys.SHIFT_LEFT))
		{
			if(cfg.SoundToggle && ( RTS_NumSounds() > 0 ) && rtsplaying == 0 && cfg.VoiceToggle)
			{
				int fkey = -1;
				for(int i = 0; i < 10; i++)
				{
					if(ctrlKeyStatusOnce(i + Keys.NUM_1)) {
						fkey = i;
						break;
					}
				}
				if(ctrlKeyStatusOnce(Keys.NUM_0)) fkey = 9;
				
				if(fkey >= 0) {
					if(ctrlKeyStatus(Keys.ALT_LEFT)) {
						byte[] rtsptr = RTS_GetSound(fkey);
						if(rtsptr != null && rtsptr.length > 0) {
							if (rtsptr[0] == 'C') {
					    		VOC voc = new VOC(rtsptr);
					    		Source voice = engine.getAudio().newSound(voc.sampledata, voc.samplerate, voc.samplesize, 255);
					    		if(voice != null)
					    		{
					    			voice.setGlobal(1);
					    			voice.play(1.0f); 
					    		}
					    	}
					    	else {
								try {
									WAVLoader wav = new WAVLoader(rtsptr);
									Source voice = engine.getAudio().newSound(wav.sampledata, wav.samplerate, wav.samplebits, 255);
									if(voice != null)
									{
										voice.setGlobal(1);
										voice.play(1.0f); 
									}
								} catch (Exception e) {}
					    	}
							
							rtsplaying = 7;
							
							if(ud.multimode > 1)
			                {
			                    tempbuf[0] = kPacketSound;
			                    tempbuf[1] = (byte) fkey;
			                    		
			                    sendtoall(tempbuf, 2);
			                }
						}
					}
					
					if(ctrlKeyStatus(Keys.SHIFT_LEFT))
					{
						adduserquote(ud.ridecule[fkey]);

		                if(ud.multimode > 1) 
		                {
			                tempbuf[0] = kPacketMessage;
							tempbuf[1] = (byte) 255;
			                tempbuf[2] = 0;
			                
			                for(int i = 0; i < ud.ridecule[fkey].length; i++)
			                	tempbuf[2 + i] = (byte) ud.ridecule[fkey][i];

			                sendtoall(tempbuf, 2 + ud.ridecule[fkey].length);
		                }
					}
				}
			}
		}
		

		if (ctrlGetInputKey(Show_Help, true))
			mOpen(mMenus[HELP], -1);
		
		if (ctrlGetInputKey(Show_Savemenu, true)) {
			if(numplayers > 1 || mFakeMultiplayer) return;
			if (sprite[ps[myconnectindex].i].extra > 0) {
				gScreenCapture = true;
				mOpen(mMenus[SAVEGAME], -1);
			}
		}

		if (ctrlGetInputKey(Show_Loadmenu, true)) {
			if(numplayers > 1 || mFakeMultiplayer) return;
			mOpen(mMenus[LOADGAME], -1);
		}
		
		if ( ctrlGetInputKey(See_Chase_View, true) )
		{
			if( ps[myconnectindex].over_shoulder_on != 0 )
                ps[myconnectindex].over_shoulder_on = 0;
            else
            {
                ps[myconnectindex].over_shoulder_on = 1;
                cameradist = 0;
                cameraclock = totalclock;
            }
            FTA(109+ps[myconnectindex].over_shoulder_on,ps[myconnectindex]);
		}
		
		if( ud.overhead_on != 0)
		{
            int j = totalclock-nonsharedtimer; nonsharedtimer += j;
            if ( ctrlGetInputKey(Enlarge_Screen, false) )
                ps[myconnectindex].zoom += mulscale(j,Math.max(ps[myconnectindex].zoom,256), 6);
            if ( ctrlGetInputKey(Shrink_Screen, false) )
                ps[myconnectindex].zoom -= mulscale(j,Math.max(ps[myconnectindex].zoom,256), 6);

            if( (ps[myconnectindex].zoom > 2048) )
                ps[myconnectindex].zoom = 2048;
            if( (ps[myconnectindex].zoom < 48) )
                ps[myconnectindex].zoom = 48;
            
            if( ctrlGetInputKey(Map_Follow_Mode, true) ) {
	   	    	 ud.scrollmode = !ud.scrollmode;
	   	    	 
	   	    	 if(ud.scrollmode)
	   	         {
	   	             ud.folx = ps[myconnectindex].oposx;
	   	             ud.foly = ps[myconnectindex].oposy;
	   	             ud.fola = (int) ps[myconnectindex].oang;
	   	         }
	   	    	 FTA(83+(ud.scrollmode?1:0),ps[myconnectindex]); 
            }
		} else {
			 if ( ctrlGetInputKey(Enlarge_Screen, true) )
			 {
				 if(ud.screen_size > 0) {
					 sound(THUD);
					 ud.screen_size--;
					 if(ud.screen_size < 0) ud.screen_size = 0;
					 vscrn(ud.screen_size);
				 }
			 }
			 if ( ctrlGetInputKey(Shrink_Screen, true) )
			 {
				 if(ud.screen_size < 4) {
					 sound(THUD);
					 ud.screen_size++;
					 if(ud.screen_size > 5) ud.screen_size = 5;
					 vscrn(ud.screen_size);
				 }
			 }
		}
		
		if( ctrlGetInputKey(Map_Toggle, true) )
	    {
	        if( ud.last_overhead != ud.overhead_on && ud.last_overhead != 0)
	        {
	            ud.overhead_on = ud.last_overhead;
	            ud.last_overhead = 0;
	        }
	        else
	        {
	            ud.overhead_on++;
	            if(ud.overhead_on == 3 ) ud.overhead_on = 0;
	            ud.last_overhead = ud.overhead_on;
	        }
	    }
		
		if( ctrlGetInputKey(AutoRun, true)  )
	    {
	        ud.auto_run ^= 1;
	        FTA(85+ud.auto_run, ps[myconnectindex]);
	    }
		
		if( ctrlGetInputKey(Toggle_Crosshair, true)  )
	    {
			ud.crosshair ^= 1;
	        FTA(21-ud.crosshair,ps[screenpeek]);
	    }
		
		if( ctrlGetInputKey(Show_Opponents_Weapon, true)  )
	    {
			ud.showweapons ^= 1;
	        FTA(82-ud.showweapons,ps[screenpeek]);
	    }
		
		if (ctrlGetInputKey(Show_Sounds, true))
			mOpen(mMenus[SOUNDST], -1);

		if (ctrlGetInputKey(Show_Options, true))
			mOpen(mMenus[OPTIONS], -1);
		
		if (ctrlGetInputKey(Gamma, true))
			mOpen(mMenus[COLORCORR], -1);

		if (ctrlGetInputKey(Quicksave, true)) { // quick save
			quicksave();
		}

		if (ctrlGetInputKey(ToggleMessages, true)) {
			ud.fta_on ^= 1;
			if(ud.fta_on != 0) FTA(23,ps[myconnectindex]);
			else
			{
				ud.fta_on = 1;
				FTA(24,ps[myconnectindex]);
				ud.fta_on = 0;
			}
		}
		
		if(ctrlGetInputKey(Send_Message, false))
    	{
			MODE_TYPE = true;
        	getInput().initMessageInput(null);
    	}

		if (ctrlGetInputKey(Quickload, true)) { // quick load
			quickload();
		}
		
		if(ctrlGetInputKey(See_Coop_View, true)) 
		{
			if(ud.coop == 1 || mFakeMultiplayer)
			{
				screenpeek = connectpoint2[screenpeek];
				if (screenpeek < 0) screenpeek = connecthead;
				
				changepalette = 1; //if player has other palette
			}
		}

		if (ctrlGetInputKey(Quit, true))
			mOpen(mMenus[QUIT], -1);
	}	
}
