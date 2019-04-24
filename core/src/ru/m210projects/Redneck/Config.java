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

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.*;

import ru.m210projects.Build.Input.ButtonMap;
import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildConfig;

import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Redneck.Globals.ud;

public class Config extends BuildConfig {

	public Config(String path, String name) {
		super(path, name);
	}

	public enum RRKeys implements KeyType {
		AutoRun,
		Quick_pee,
		Aim_Up,
		Aim_Down,
		Aim_Center,
		Tilt_Left,
		Tilt_Right,
		Weapon_1,
		Weapon_2,
		Weapon_3,
		Weapon_4,
		Weapon_5,
		Weapon_6,
		Weapon_7,
		Weapon_8,
		Weapon_9,
		Weapon_10,
		Inventory_Use,
		Inventory_Left,
		Inventory_Right,
		Map_Follow_Mode,
		See_Coop_View,
		See_Chase_View,
		Toggle_Crosshair,
		Holster_Weapon,
		Show_Opp_Weapon,
		Yeehaa,
		Beer,
		Cowpie,
		Wiskey,
		Moonshine,
		Show_Help,
		Show_Savemenu,
		Show_Loadmenu,
		Show_Sounds,
		Show_Options,
		Quicksave,
		Messages,
		Quickload,
		Quit,
		Gamma,
		Screenshot,
		Last_Weap_Switch,
		Crouch_toggle;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}

	public static final char[] defkeys = {
		Keys.W, 			//Move_Forward 0
		Keys.S,				//Move_Backward 1
		Keys.LEFT,			//Turn_Left 2
		Keys.RIGHT,			//Turn_Right 3
		Keys.BACKSPACE, 	//Turn_Around 4
		Keys.ALT_LEFT, 		//Strafe 5
		Keys.A,				//Strafe_Left 6
		Keys.D, 			//Strafe_Right 7
		Keys.SPACE, 		//Jump 8
		Keys.CONTROL_LEFT, 	//Crouch 9
		Keys.SHIFT_LEFT, 	//Run 10
		KEY_CAPSLOCK, 		//AutoRun 11
		Keys.E,		 		//Open 12
		Keys.CONTROL_RIGHT, //Weapon_Fire 13
		Keys.X, 			//Quick_pee 14
		Keys.HOME, 			//Aim_Up 15
		Keys.END,			//Aim_Down 16
		Keys.NUMPAD_5,		//Aim_Center 17
		Keys.PAGE_UP, 		//Look_Up 18
		Keys.PAGE_DOWN, 	//Look_Down 19
		Keys.INSERT,		//Tilt_Left 20
		Keys.FORWARD_DEL, //Tilt_Right 21
		Keys.NUM_1, 	//Weapon_1 22
		Keys.NUM_2,		//Weapon_2 23
		Keys.NUM_3,		//Weapon_3 24
		Keys.NUM_4,		//Weapon_4 25
		Keys.NUM_5,		//Weapon_5 26
		Keys.NUM_6,		//Weapon_6 27
		Keys.NUM_7,		//Weapon_7 28
		Keys.NUM_8,		//Weapon_8 29
		Keys.NUM_9,		//Weapon_9 30
		Keys.NUM_0,		//Weapon_10 31
		Keys.ENTER, 	//Inventory_Use 32
		Keys.LEFT_BRACKET, //Inventory_Left 33
		Keys.RIGHT_BRACKET, //Inventory_Right 34
		Keys.TAB,			//Map_Toggle 35
		Keys.F,			//Map_Follow_Mode 36
		Keys.MINUS, 	//Shrink_Screen 37
		Keys.EQUALS,	//Enlarge_Screen 38
		Keys.T, 		//Send_Message 39
		Keys.K,			//See_Coop_View 40
		Keys.F7,		//See_Chase_View 41
		Keys.U,			//Mouse_Aiming 42
		Keys.I,			//Toggle_Crosshair 43
		Keys.APOSTROPHE,//Next_Weapon 44
		Keys.SEMICOLON,	//Previous_Weapon 45
		Keys.BACKSLASH, //Holster_Weapon 46
		Keys.Y, 		//Show_Opponents_Weapon 47
		Keys.H,			//Yeehaa 48
		Keys.B,			//Beer 49
		Keys.P,			//Cowpie 50
		Keys.M,			//Wiskey 51
		Keys.R,			//Moonshine 52
		Keys.ESCAPE,	//Toggle_menu 53
		Keys.GRAVE,		//Show_Console 54
		Keys.F1,		//Show_HelpScreen 55
		Keys.F2,		//Show_Save	56
		Keys.F3,		//Show_Load 57
		Keys.F4,		//Show_Sounds 58
		Keys.F5,		//Show_Options 59
		Keys.F6,		//QuickSave	60
		Keys.F8,		//ToggleMessages 61
		Keys.F9,		//QuickLoad 62
		Keys.F10,		//Quit 63
		Keys.F11,		//Gamma 64
		Keys.F12,		//MakeScreenshot 65
		Keys.Q,				//Last_Weapon_Switch
		0,				//Crouch_toggle
	};
	
	public static final char[] defclassickeys = {
			Keys.UP, 			//Move_Forward 0
			Keys.DOWN,			//Move_Backward 1
			Keys.LEFT,			//Turn_Left 2
			Keys.RIGHT,			//Turn_Right 3
			Keys.BACKSPACE, 	//Turn_Around 4
			Keys.ALT_LEFT, 		//Strafe 5
			Keys.COMMA,			//Strafe_Left 6
			Keys.PERIOD, 		//Strafe_Right 7
			Keys.A, 			//Jump 8
			Keys.Z, 			//Crouch 9
			Keys.SHIFT_LEFT, 	//Run 10
			KEY_CAPSLOCK, 		//AutoRun 11
			Keys.SPACE,		 		//Open 12
			Keys.CONTROL_LEFT, //Weapon_Fire 13
			Keys.GRAVE, 		//Quick_pee 14
			Keys.HOME, 			//Aim_Up 15
			Keys.END,			//Aim_Down 16
			Keys.NUMPAD_5,		//Aim_Center 17
			Keys.PAGE_UP, 		//Look_Up 18
			Keys.PAGE_DOWN, 	//Look_Down 19
			Keys.INSERT,		//Tilt_Left 20
			Keys.FORWARD_DEL, //Tilt_Right 21
			Keys.NUM_1, 	//Weapon_1 22
			Keys.NUM_2,		//Weapon_2 23
			Keys.NUM_3,		//Weapon_3 24
			Keys.NUM_4,		//Weapon_4 25
			Keys.NUM_5,		//Weapon_5 26
			Keys.NUM_6,		//Weapon_6 27
			Keys.NUM_7,		//Weapon_7 28
			Keys.NUM_8,		//Weapon_8 29
			Keys.NUM_9,		//Weapon_9 30
			Keys.NUM_0,		//Weapon_10 31
			Keys.ENTER, 	//Inventory_Use 32
			Keys.LEFT_BRACKET, //Inventory_Left 33
			Keys.RIGHT_BRACKET, //Inventory_Right 34
			Keys.TAB,			//Map_Toggle 35
			Keys.F,			//Map_Follow_Mode 36
			Keys.MINUS, 	//Shrink_Screen 37
			Keys.EQUALS,	//Enlarge_Screen 38
			Keys.T, 		//Send_Message 39
			Keys.K,			//See_Coop_View 40
			Keys.F7,		//See_Chase_View 41
			Keys.U,			//Mouse_Aiming 42
			Keys.I,			//Toggle_Crosshair 43
			Keys.APOSTROPHE,//Next_Weapon 44
			Keys.SEMICOLON,	//Previous_Weapon 45
			KEY_SCROLLOCK,  //Holster_Weapon 46
			Keys.E, 		//Show_Opponents_Weapon 47
			Keys.Y,			//Yeehaa 48
			Keys.B,			//Beer 49
			Keys.C,			//Cowpie 50
			Keys.W,			//Wiskey 51
			Keys.M,			//Moonshine 52
			Keys.ESCAPE,	//Open_menu 53
			0,		//Show_Console 54
			Keys.F1,		//Show_HelpScreen 55
			Keys.F2,		//Show_Save	56
			Keys.F3,		//Show_Load 57
			Keys.F4,		//Show_Sounds 58
			Keys.F5,		//Show_Options 59
			Keys.F6,		//QuickSave	60
			Keys.F8,		//ToggleMessages 61
			Keys.F9,		//QuickLoad 62
			Keys.F10,		//Quit 63
			Keys.F11,		//Gamma 64
			Keys.F12,		//MakeScreenshot 65
			0,				//Last_Weapon_Switch
			0,				//Crouch_toggle	
	};
	
	public int gStatSize;
	public int gCrossSize;
	public int gShowStat = 1;
	public int showMapInfo = 1;
	public boolean AmbienceToggle = true;
	public boolean VoiceToggle = true;
	public int gDemoSeq = 0;
	public boolean gAutoAim	= true;
	public int screen_size = 2;
	public int crosshair = 1;
	public int screen_tilting = 1;
	public int auto_run = 1;
	public int fta_on = 1;
	public boolean gColoredKeys;
	
	
	
	
	@Override
	public void SaveConfig(int fil) {
		if(fil != -1)
		{
			saveString(fil, "[ScreenSetup]\r\n");
				//Screen Setup	
		
			saveInteger(fil, "Size", ud.screen_size);
			saveInteger(fil, "Crosshair", crosshair);
			saveBoolean(fil, "VSync", gVSync);
			saveInteger(fil, "MessageState", ud.fta_on);
			saveInteger(fil, "FpsScale", (int)(gFpsScale * 65536.0f));
			
			saveInteger(fil, "Gamma", (int) ((1 - gamma) * 4096));
			saveInteger(fil, "Brightness", (int) (brightness * 4096));
			saveInteger(fil, "Contrast", (int) (contrast * 4096));
		
			saveString(fil, "[SoundSetup]\r\n");
				//Sound Setup
			
			saveInteger(fil, "SoundVolume", (int)(soundVolume * 256.0f));
			saveInteger(fil, "MaxVoices", maxvoices);
			saveInteger(fil, "Resampler", resampler_num);
			saveInteger(fil, "MusicVolume", (int)(musicVolume * 256.0f));
			saveString(fil, ";\r\n;\r\n");

			saveString(fil, "[KeyDefinitions]\r\n");
			for(int i = 0; i < keymap.length; i++) {
				String line = keymap[i] + " = \"" + Keymap.toString(primarykeys[i]) +  "\", \"" + Keymap.toString(secondkeys[i]) +  "\", \"" + Keymap.toString(mousekeys[i]) +  "\", \"" + ButtonMap.buttonName(gpadkeys[i]) + "\"\r\n";
				saveString(fil, line);
			}
			saveString(fil, ";\r\n");
			saveString(fil, "MouseDigitalAxes0_0 " + ((mouseaxis[AXISLEFT] != -1)?("= " + keymap[mouseaxis[AXISLEFT]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes0_1 " + ((mouseaxis[AXISRIGHT] != -1)?("= " + keymap[mouseaxis[AXISRIGHT]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes1_0 " + ((mouseaxis[AXISUP] != -1)?("= " + keymap[mouseaxis[AXISUP]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes1_1 " + ((mouseaxis[AXISDOWN] != -1)?("= " + keymap[mouseaxis[AXISDOWN]]):"= \"N/A\"") +"\r\n");
			saveString(fil, ";\r\n");
			saveString(fil, "[JoyDefinitions]\r\n");
			for(int i = 0; i < joymap.length; i++) {
				String line = joymap[i] + " = \"" + ButtonMap.buttonName(gJoyMenukeys[((MenuKeys)joymap[i]).getJoyNum()]) + "\"\r\n";
				saveString(fil, line);
			}
			saveString(fil, ";\r\n;\r\n");
			
			saveString(fil, "[Controls]\r\n");
				//Controls
			saveBoolean(fil, "UseMouse", useMouse);
			saveBoolean(fil, "UseMouseInMenu", menuMouse);
			saveInteger(fil, "MouseSensitivity", gSensitivity);
			saveBoolean(fil, "MouseAiming", gMouseAim);
			saveBoolean(fil, "MouseAimingFlipped", gInvertmouse);
			saveInteger(fil, "MouseTurnSpeed", gMouseTurnSpeed);
			saveInteger(fil, "MouseLookSpeed", gMouseLookSpeed);
			saveInteger(fil, "MouseMoveSpeed", gMouseMoveSpeed);
			saveInteger(fil, "MouseStrafeSpeed", gMouseStrafeSpeed);
			saveInteger(fil, "MouseCursor", gMouseCursor);
			saveInteger(fil, "MouseCursorSize", gMouseCursorSize);
			saveInteger(fil, "JoyDevice", gJoyDevice);
			saveInteger(fil, "JoyTurnAxis", gJoyTurnAxis);
			saveInteger(fil, "JoyMoveAxis", gJoyMoveAxis);
			saveInteger(fil, "JoyStrafeAxis", gJoyStrafeAxis);
			saveInteger(fil, "JoyLookAxis", gJoyLookAxis);
			saveInteger(fil, "JoyTurnSpeed", gJoyTurnSpeed);
			saveInteger(fil, "JoyLookSpeed", gJoyLookSpeed);
			saveBoolean(fil, "JoyInvertLook", gJoyInvert);
			saveInteger(fil, "JoyDeadZone", gJoyDeadZone);
			saveString(fil, ";\r\n;\r\n");
			
			saveString(fil, "[Options]\r\n");	
				//Options

			saveBoolean(fil, "Autoaim", gAutoAim);
			saveInteger(fil, "Tilt", ud.screen_tilting);
			saveInteger(fil, "AutoRun", ud.auto_run);
			saveBoolean(fil, "ShowFPS", gShowFPS);	
			saveInteger(fil, "MessageState", ud.fta_on);	
			saveInteger(fil, "StatSize", gStatSize);	
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveInteger(fil, "OSDTextScale", Console.getTextScale());
			saveBoolean(fil, "UseVoxels", usevoxels);
			saveBoolean(fil, "UseModels", usemodels);
			saveBoolean(fil, "UseHightiles", usehightile);
			saveInteger(fil, "DemoSequence", gDemoSeq);
			saveString(fil,  "Player_name", pName);	
			saveString(fil,  "IP_Address", mAddress);	
			saveInteger(fil, "Port", mPort);
			saveBoolean(fil, "Colored_keys", gColoredKeys);
		}
	}

	@Override
	public boolean InitConfig(boolean isDefault) {
		gStatSize = 65536;
		gCrossSize = 32768;
		gShowStat = 1;
		showMapInfo = 1;
		AmbienceToggle = true;
		VoiceToggle = true;
		gDemoSeq = 1;
		gAutoAim	= true;
		screen_size = 2;
		crosshair = 1;
		screen_tilting = 1;
		auto_run = 1;
		fta_on = 1;
		pName = "Leonard";
		gColoredKeys = false;

		if(!isDefault)
		{
			if(set("ScreenSetup")) {
				screen_size = GetKeyInt("Size");
				crosshair = GetKeyInt("Crosshair");
				gVSync = GetKeyInt("VSync") == 1;
				fta_on = GetKeyInt("MessageState");
				int fpssize = GetKeyInt("FpsScale");
				if(fpssize != -1) gFpsScale = fpssize / 65536.0f;
				
				int gm = GetKeyInt("Gamma");
				if( gm != -1) gamma = 1.0f - (gm / 4096.0f);
				int bg = GetKeyInt("Brightness");
				if( bg != -1) brightness = bg / 4096.0f;
				int ct = GetKeyInt("Contrast");
				if( ct != -1) contrast = ct / 4096.0f;
			}
			
			if(set("SoundSetup")) {
				soundVolume = GetKeyInt("SoundVolume") / 256.0f;
				maxvoices = GetKeyInt("MaxVoices");
				int resampler = GetKeyInt("Resampler"); 
				if(resampler != -1) resampler_num = resampler;
				musicVolume = GetKeyInt("MusicVolume") / 256.0f;
			}
			
			if(set("KeyDefinitions")) {
				for(int i = 0; i < keymap.length; i++) {
					primarykeys[i] = defkeys[i];
					secondkeys[i] = 0;
					mousekeys[i] = 0;
					gpadkeys[i] = -1;

					String primary = GetKeyString(keymap[i].getName(), 0);
					String secondary = GetKeyString(keymap[i].getName(), 1);
					String mouse = GetKeyString(keymap[i].getName(), 2);
					String joystick = GetKeyString(keymap[i].getName(), 3);

					if(primary != null) 
						primarykeys[i] = Keymap.valueOf(primary);
					if(secondary != null) 
						secondkeys[i] = Keymap.valueOf(secondary);
					if(mouse != null)
						mousekeys[i] = Keymap.valueOf(mouse);
					if(joystick != null)
						gpadkeys[i] = ButtonMap.valueOf(joystick);
				}
				if(primarykeys[MenuKeys.Menu_Toggle.getNum()] == 0)
					primarykeys[MenuKeys.Menu_Toggle.getNum()] = defclassickeys[MenuKeys.Menu_Toggle.getNum()];

				String left = GetKeyString("MouseDigitalAxes0_0");
				if(left != null)
					mouseaxis[AXISLEFT] = getKeyIndex(left);
				String right = GetKeyString("MouseDigitalAxes0_1");
				if(right != null)
					mouseaxis[AXISRIGHT] = getKeyIndex(right);
				String up = GetKeyString("MouseDigitalAxes1_0");
				if(up != null)
					mouseaxis[AXISUP] = getKeyIndex(up);
				String down = GetKeyString("MouseDigitalAxes1_1");
				if(down != null)
					mouseaxis[AXISDOWN] = getKeyIndex(down);
			}
			
			if(set("JoyDefinitions")) {
				for(int i = 0; i < joymap.length; i++) { 
					gJoyMenukeys[((MenuKeys)joymap[i]).getJoyNum()] = -1;
					String joymenu = GetKeyString(joymap[i].getName(), 0);
					if(joymenu != null)
						gJoyMenukeys[((MenuKeys)joymap[i]).getJoyNum()] = ButtonMap.valueOf(joymenu);
				}
			}
			
			if(set("Controls")) {
				int value = GetKeyInt("UseMouse");
				if(value != -1) useMouse = value == 1;
				value = GetKeyInt("UseMouseInMenu");
				if(value != -1) menuMouse = value == 1;
				value = GetKeyInt("MouseSensitivity");
				if(value != -1) gSensitivity = value;
				value = GetKeyInt("MouseAiming");
				if(value != -1) gMouseAim = value == 1;
				value = GetKeyInt("MouseAimingFlipped");
				if(value != -1) gInvertmouse = value == 1;
				value = GetKeyInt("MouseTurnSpeed");
				if(value != -1) gMouseTurnSpeed = value;
				value = GetKeyInt("MouseLookSpeed");
				if(value != -1) gMouseLookSpeed = value;
				value = GetKeyInt("MouseMoveSpeed");
				if(value != -1) gMouseMoveSpeed = value;
				value = GetKeyInt("MouseStrafeSpeed");
				if(value != -1) gMouseStrafeSpeed = value;
				value = GetKeyInt("MouseCursor");
				if(value != -1) gMouseCursor = value;
				value = GetKeyInt("MouseCursorSize");
				if(value != -1) gMouseCursorSize = value;
				value = GetKeyInt("JoyDevice");
				if (value != -1) gJoyDevice = value;
				value = GetKeyInt("JoyTurnAxis");
				if(value != -1) gJoyTurnAxis = value;
				value = GetKeyInt("JoyMoveAxis");
				if(value != -1) gJoyMoveAxis = value;
				value = GetKeyInt("JoyStrafeAxis");
				if(value != -1) gJoyStrafeAxis = value;
				value = GetKeyInt("JoyLookAxis");
				if(value != -1) gJoyLookAxis = value;
				value = GetKeyInt("JoyTurnSpeed");
				if(value != -1) gJoyTurnSpeed = value;
				value = GetKeyInt("JoyLookSpeed");
				if(value != -1) gJoyLookSpeed = value;
				value = GetKeyInt("JoyInvertLook");
				if(value != -1) gJoyInvert = value == 1;
				value = GetKeyInt("JoyDeadZone");
				if(value != -1) gJoyDeadZone = value;
			}
			
			if(set("Options")) {
				gAutoAim = GetKeyInt("Autoaim") == 1;
				screen_tilting = GetKeyInt("Tilt");
				auto_run = GetKeyInt("AutoRun");
				gShowFPS = GetKeyInt("ShowFPS") == 1;
				fta_on = GetKeyInt("MessageState");
				
				gStatSize = GetKeyInt("StatSize");
				if(gStatSize < 16384) gStatSize = 16384;
				gCrossSize = GetKeyInt("CrossSize");
				if(gCrossSize < 8192) gCrossSize = 8192;
				gShowStat = GetKeyInt("ShowStat");
				showMapInfo = GetKeyInt("showMapInfo");
				int scale = GetKeyInt("OSDTextScale");
				if(scale != -1)
					Console.setTextScale(scale);
				
				int voxels = GetKeyInt("UseVoxels");
				if(voxels != -1)
					usevoxels = (voxels == 1);
				int models = GetKeyInt("UseModels");
				if(models != -1)
					usemodels = (models == 1);
				int hires = GetKeyInt("UseHightiles");
				if(hires != -1)
					usehightile = (hires == 1);
				
				int demos = GetKeyInt("DemoSequence");
				if(demos != -1) gDemoSeq = demos;
				
				String name = GetKeyString("Player_name");
				if(name != null)
					pName = name;
				
				String ip = GetKeyString("IP_Address");
				if(ip != null)
					mAddress = ip;
				int port = GetKeyInt("Port");
				if(port != -1)
					mPort = port;
				
				int coloredk = GetKeyInt("Colored_keys");
				if(coloredk != -1)
					gColoredKeys = coloredk != 0;
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
	
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[RRKeys.Quick_pee.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		boolean mouseset = true;
		for(int i = 0; i < mousekeys.length; i++) {
			if(mousekeys[i] != 0) {
				mouseset = false;
				break;
			}
		}
		if(mouseset)
		{
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[RRKeys.Quick_pee.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		return true;
	}

	@Override
	public KeyType[] getKeyMap() {
		KeyType[] keymap = {
			GameKeys.Move_Forward,
			GameKeys.Move_Backward,
			GameKeys.Turn_Left,
			GameKeys.Turn_Right,
			GameKeys.Turn_Around,
			GameKeys.Strafe,
			GameKeys.Strafe_Left,
			GameKeys.Strafe_Right,
			GameKeys.Jump,
			GameKeys.Crouch,
			GameKeys.Run,
			RRKeys.AutoRun,
			GameKeys.Open,
			GameKeys.Weapon_Fire,
			RRKeys.Quick_pee,
			RRKeys.Aim_Up,
			RRKeys.Aim_Down,
			RRKeys.Aim_Center,
			GameKeys.Look_Up,
			GameKeys.Look_Down,
			RRKeys.Tilt_Left,
			RRKeys.Tilt_Right,
			RRKeys.Weapon_1,
			RRKeys.Weapon_2,
			RRKeys.Weapon_3,
			RRKeys.Weapon_4,
			RRKeys.Weapon_5,
			RRKeys.Weapon_6,
			RRKeys.Weapon_7,
			RRKeys.Weapon_8,
			RRKeys.Weapon_9,
			RRKeys.Weapon_10,
			RRKeys.Inventory_Use,
			RRKeys.Inventory_Left,
			RRKeys.Inventory_Right,
			GameKeys.Map_Toggle,
			RRKeys.Map_Follow_Mode,
			GameKeys.Shrink_Screen,
			GameKeys.Enlarge_Screen,
			GameKeys.Send_Message,
			RRKeys.See_Coop_View,
			RRKeys.See_Chase_View,
			GameKeys.Mouse_Aiming,
			RRKeys.Toggle_Crosshair,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			RRKeys.Holster_Weapon,
			RRKeys.Show_Opp_Weapon,
			RRKeys.Yeehaa,
			RRKeys.Beer,
			RRKeys.Cowpie,
			RRKeys.Wiskey,
			RRKeys.Moonshine,
			MenuKeys.Menu_Toggle,
			GameKeys.Show_Console,
			RRKeys.Show_Help,
			RRKeys.Show_Savemenu,
			RRKeys.Show_Loadmenu,
			RRKeys.Show_Sounds,
			RRKeys.Show_Options,
			RRKeys.Quicksave,
			RRKeys.Messages,
			RRKeys.Quickload,
			RRKeys.Quit,
			RRKeys.Gamma,
			RRKeys.Screenshot,
			RRKeys.Last_Weap_Switch,
			RRKeys.Crouch_toggle
		};
		return keymap;
	}

}
