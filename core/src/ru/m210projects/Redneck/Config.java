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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Input.Keymap.*;
import static ru.m210projects.Build.Net.Mmulti.NETPORT;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import ru.m210projects.Build.Input.ButtonMap;
import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.BConfig;
import ru.m210projects.Build.Types.BGraphics;
import ru.m210projects.Redneck.Types.IniFile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Redneck.Globals.ud;

public class Config extends BConfig {

	public static final int Move_Forward = 0;
	public static final int Move_Backward = 1;
	public static final int Turn_Left = 2;
	public static final int Turn_Right = 3;
	public static final int Turn_Around = 4;
	public static final int Strafe = 5;
	public static final int Strafe_Left = 6;
	public static final int Strafe_Right = 7;
	public static final int Jump = 8;
	public static final int Crouch = 9;
	public static final int Run = 10;
	public static final int AutoRun = 11;
	public static final int Open = 12;
	public static final int Weapon_Fire = 13;
	public static final int Last_Weapon_Switch = 14;
	public static final int Aim_Up = 15;
	public static final int Aim_Down = 16;
	public static final int Aim_Center = 17;
	public static final int Look_Up = 18;
	public static final int Look_Down = 19;
	public static final int Tilt_Left = 20;
	public static final int Tilt_Right = 21;
	public static final int Weapon_1 = 22;
	public static final int Weapon_2 = 23;
	public static final int Weapon_3 = 24;
	public static final int Weapon_4 = 25;
	public static final int Weapon_5 = 26;
	public static final int Weapon_6 = 27;
	public static final int Weapon_7 = 28;
	public static final int Weapon_8 = 29;
	public static final int Weapon_9 = 30;
	public static final int Weapon_10 = 31;
	public static final int Inventory_Use = 32;
	public static final int Inventory_Left = 33;
	public static final int Inventory_Right = 34;
	public static final int Yeehaa = 35;
	public static final int Quick_pee = 36;
	public static final int Map_Toggle = 37;
	public static final int Map_Follow_Mode = 38;
	public static final int Shrink_Screen = 39;
	public static final int Enlarge_Screen = 40;
	public static final int Send_Message = 41;
	public static final int See_Coop_View = 42;
	public static final int See_Chase_View = 43;
	public static final int Mouse_Aiming = 44;
	public static final int Toggle_Crosshair = 45;
	public static final int Next_Weapon = 46;
	public static final int Previous_Weapon = 47;
	public static final int Holster_Weapon = 48;
	public static final int Show_Opponents_Weapon = 49;
	public static final int Crouch_toggle = 50;
	public static final int Beer = 51;
	public static final int Cowpie = 52;
	public static final int Wiskey = 53;
	public static final int Moonshine = 54;
	public static final int Menu_open = 55;
	public static final int Show_Console = 56;
	public static final int Show_Help = 57;
	public static final int Show_Savemenu = 58;
	public static final int Show_Loadmenu = 59;
	public static final int Show_Sounds = 60;
	public static final int Show_Options = 61;
	public static final int Quicksave = 62;
	public static final int ToggleMessages = 63;
	public static final int Quickload = 64;
	public static final int Quit = 65;
	public static final int Gamma = 66;
	public static final int Screenshot = 67;
	
	
	public boolean AmbienceToggle = true;
	public float soundVolume = 1.00f;
	public boolean SoundToggle = true;
	public float musicVolume = 0.25f;
	public boolean MusicToggle = true;
	public int NumVoices = 32;
	public boolean VoiceToggle = true;
	public int resampler_num = 0;

	public  int widescreen = 1;
	public  int anisotropy = 0;
	public 	int gDemoSeq = 1;

	public  boolean gAutoAim	= true;

	public  boolean useJoystick = true;
	public  int gJoyMoveAxis = 0; //Stick1Y
	public  int gJoyStrafeAxis = 1; //Stick1X
	public  int gJoyLookAxis = 2; //Stick2Y
	public  int gJoyTurnAxis = 3; //Stick2X
	public  int gJoyTurnSpeed = 16384;
	public  int gJoyLookSpeed = 1048576;
	public  int gJoyDeadZone = 8192;
	public  boolean gJoyInvert = false;
	public  int gJoyDevice = -1;
	
	public String pName = "LEONARD";
	public String mAddress = "localhost";
	public int mPort = NETPORT;

	public boolean gPlayVideos = true;

	public int screen_size = 2;
	public int crosshair = 1;
	public int screen_tilting = 1;
	public int auto_run = 1;
	public int fta_on = 1;

	public static final String[] keynames =
	{
		"Move_Forward",
		"Move_Backward",
		"Turn_Left",
		"Turn_Right",
		"Turn_Around",
		"Strafe",
		"Strafe_Left",
		"Strafe_Right",
		"Jump",
		"Crouch",
		"Run",
		"AutoRun",
		"Open",
		"Weapon_Fire",
		"Last_Used_Weapon",
		"Aim_Up",
		"Aim_Down",
		"Aim_Center",
		"Look_Up",
		"Look_Down",
		"Tilt_Left",
		"Tilt_Right",
		"Weapon_1",
		"Weapon_2",
		"Weapon_3",
		"Weapon_4",
		"Weapon_5",
		"Weapon_6",
		"Weapon_7",
		"Weapon_8",
		"Weapon_9",
		"Weapon_10",
		"Inventory_Use",
		"Inventory_Left",
		"Inventory_Right",
		"Yeehaa",
		"Quick_pee",
		"Map_Toggle",
		"Map_Follow_Mode",
		"Shrink_Screen",
		"Enlarge_Screen",
		"Send_Message",
		"See_Coop_View",
		"See_Chase_View",
		"Mouse_Aiming",
		"Toggle_Crosshair",
		"Next_Weapon",
		"Previous_Weapon",
		"Holster_Weapon",
		"Show_Opponents_Weapon",
		"Crouch_toggle",
		"Beer",
		"Cowpie",
		"Wiskey",
		"Moonshine",
		"Open/Close_menu",
		"Show_Console",
		"Show_HelpScreen",
		"Show_SaveMenu",
		"Show_LoadMenu",
		"Show_SoundSetup",
		"Show_Options",
		"Quicksave",
		"Toggle_messages",
		"Quickload",
		"Quit",
		"Gamma",
		"Make_Screenshot",
	};

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
		Keys.Q, 			//Last_Weapon_Switch 14
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
		Keys.H,				//Yeehaa 35
		Keys.X,				//Quick_pee 36
		Keys.TAB,			//Map_Toggle 37
		Keys.F,			//Map_Follow_Mode 38
		Keys.MINUS, 	//Shrink_Screen 39
		Keys.EQUALS,	//Enlarge_Screen 40
		Keys.T, 		//Send_Message 41
		Keys.K,			//See_Coop_View 42
		Keys.F7,		//See_Chase_View 43
		Keys.U,			//Mouse_Aiming 44
		Keys.I,			//Toggle_Crosshair 45
		Keys.APOSTROPHE,//Next_Weapon 46
		Keys.SEMICOLON,	//Previous_Weapon 47
		Keys.BACKSLASH, //Holster_Weapon 48
		Keys.Y, 		//Show_Opponents_Weapon 49
		0, 				//Crouch_toggle 50
		Keys.B,			//Beer 51
		Keys.P,			//Cowpie 52
		Keys.M,			//Wiskey 53
		Keys.R,			//Moonshine 54
		Keys.ESCAPE,	//Open_menu 55
		Keys.GRAVE,		//Show_Console 56
		Keys.F1,		//Show_HelpScreen 57
		Keys.F2,		//Show_Save	58
		Keys.F3,		//Show_Load 59
		Keys.F4,		//Show_Sounds 60
		Keys.F5,		//Show_Options 61
		Keys.F6,		//QuickSave	62
		Keys.F8,		//ToggleMessages 63
		Keys.F9,		//QuickLoad 64
		Keys.F10,		//Quit 65
		Keys.F11,		//Gamma 66
		Keys.F12,		//MakeScreenshot 67
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
			Keys.SPACE,		 	//Open 12
			Keys.CONTROL_LEFT, //Weapon_Fire 13
			Keys.Q, 			//Last_Weapon_Switch 14
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
			Keys.Y,				//Yeehaa 35
			Keys.GRAVE,			//Quick_pee 36
 			Keys.TAB,			//Map_Toggle 37
			Keys.F,			//Map_Follow_Mode 38
			Keys.MINUS, 	//Shrink_Screen 39
			Keys.EQUALS,	//Enlarge_Screen 40
			Keys.T, 		//Send_Message 41
			Keys.K,			//See_Coop_View 42
			Keys.F7,		//See_Chase_View 43
			Keys.U,			//Mouse_Aiming 44
			Keys.I,			//Toggle_Crosshair 45
			Keys.APOSTROPHE,//Next_Weapon 46
			Keys.SEMICOLON,	//Previous_Weapon 47
			KEY_SCROLLOCK,  //Holster_Weapon 48
			Keys.E, 		//Show_Opponents_Weapon 49
			0,				//Crouch_toggle 50
			Keys.B,			//Beer 51
			Keys.C,			//Cowpie 52
			Keys.W,			//Wiskey 53
			Keys.M,			//Moonshine 54
			Keys.ESCAPE,	//Open_menu 55
			0,				//Show_Console 56
			Keys.F1,		//Show_HelpScreen 57
			Keys.F2,		//Show_Save	58
			Keys.F3,		//Show_Load 59
			Keys.F4,		//Show_Sounds 60
			Keys.F5,		//Show_Options 61
			Keys.F6,		//QuickSave	62
			Keys.F8,		//ToggleMessages 63
			Keys.F9,		//QuickLoad 64
			Keys.F10,		//Quit 65
			Keys.F11,		//Gamma 66
			Keys.F12,		//MakeScreenshot 67
	};
	
	public int[] primarykeys = new int[keynames.length];
	public int[] secondkeys = new int[keynames.length];
	public int[] mousekeys = new int[keynames.length];
	public int[] gpadkeys = new int[keynames.length];
	
	public static final int AXISLEFT = 0;
	public static final int AXISRIGHT = 1;
	public static final int AXISUP = 2;
	public static final int AXISDOWN = 3;
	public int[] mouseaxis = new int[4];
	
	public  boolean menuMouse = true;
	
	public  int gSensitivity = 69632;
	public  int gMouseTurnSpeed = 65536;
	public  int gMouseLookSpeed = 65536;
	public  int gMouseMoveSpeed = 65536;
	public  int gMouseStrafeSpeed = 131072;
	public 	int gMouseCursor = 0;
	public 	int gMouseCursorSize = 65536;
	
	public int cfg_texturemode = 0;
	
	public  boolean gShowFPS	= true;
	public  boolean useMouse = true;
	public  boolean gInvertmouse = false;
	public  boolean gMouseAim = true;

	public  int gInterpolation = 1;

	public  int gStatSize = 8192;
	public  int gCrossSize = 8192;
	public int gShowStat = 1;
	public boolean gColoredKeys = false;

	public int showMapInfo = 1;
	public IniFile RRcfg;
	public String cfgname;
	public int musicType = 2;
	
	public int getKeyIndex(String keyname)
	{
		keyname = keyname.replaceAll("[^a-zA-Z_-]", "");
		for(int i = 0; i < keynames.length; i++)
		{
			if(keyname.equals(keynames[i]))
				return i;
		}	
		return -1;
	}
	
	public Config(String path, String cfgname)
	{
		FilePath = path;
		this.cfgname = cfgname;
		Arrays.fill(mouseaxis, -1);
		Arrays.fill(gpadkeys, -1);
		
		byte[] data = null;
		
		try {
			RandomAccessFile raf = new RandomAccessFile(FilePath + cfgname, "r");
			data = new byte[(int)raf.length()];
			raf.read(data);
			raf.close();
		} catch(Exception e) { 
			Console.Println("File not found: " + FilePath + cfgname, OSDTEXT_YELLOW);
		}

		if(data != null)
		{
			RRcfg = new IniFile(data, cfgname, null);
			if(RRcfg.set("Main")) {
				startup = RRcfg.GetKeyInt("Startup") == 1;
				String respath = RRcfg.GetKeyString("Path");
				if(respath != null && !respath.isEmpty())
					this.path = respath;
				int check = RRcfg.GetKeyInt("CheckNewVersion");
				if(check != -1)
					checkVersion = (check == 1);
				int afolder = RRcfg.GetKeyInt("AutoloadFolder");
				if(afolder != -1)
					autoloadFolder = (afolder == 1);
				int ufolder = RRcfg.GetKeyInt("Use_Userhome_folder");
				if(ufolder != -1)
					userfolder = (ufolder == 1);
			}
			
			if(RRcfg.set("ScreenSetup")) {
				fullscreen = RRcfg.GetKeyInt("Fullscreen");
				ScreenWidth = RRcfg.GetKeyInt("ScreenWidth");
				ScreenHeight = RRcfg.GetKeyInt("ScreenHeight");
				screen_size = RRcfg.GetKeyInt("Size");
				crosshair = RRcfg.GetKeyInt("Crosshair");
				
				gVSync = RRcfg.GetKeyInt("VSync") == 1;
				fpslimit = RRcfg.GetKeyInt("FPSLimit");
				borderless = RRcfg.GetKeyInt("BorderlessMode") == 1;
				
				cfg_texturemode = RRcfg.GetKeyInt("GLFilterMode"); 
				int anisotr = RRcfg.GetKeyInt("GLAnisotropy");
				if(anisotr != -1) anisotropy = anisotr;
				int ws = RRcfg.GetKeyInt("WideScreen");
				if(ws != -1) widescreen = ws;
				
				int gm = RRcfg.GetKeyInt("Gamma");
				if( gm != -1) gamma = gm / 4096.0f;
				int bg = RRcfg.GetKeyInt("Brightness");
				if( bg != -1) brightness = bg / 4096.0f;
				int ct = RRcfg.GetKeyInt("Contrast");
				if( ct != -1) contrast = ct / 4096.0f;
			}
			
			if(RRcfg.set("SoundSetup")) {
				SoundToggle = RRcfg.GetKeyInt("SoundToggle") == 1;
				MusicToggle = RRcfg.GetKeyInt("MusicToggle") == 1;
				int snd = RRcfg.GetKeyInt("SoundDriver");
				if(snd != -1) snddrv = snd;
				soundVolume = RRcfg.GetKeyInt("SoundVolume") / 256.0f;
				NumVoices = RRcfg.GetKeyInt("MaxVoices");
				musicVolume = RRcfg.GetKeyInt("MusicVolume") / 256.0f;
				int mid = RRcfg.GetKeyInt("MidiDriver");
				if(mid != -1) middrv = mid;
				midiSynth = RRcfg.GetKeyString("MidiSynth");
				int type = RRcfg.GetKeyInt("MusicType");
				if(type != -1) musicType = type;
				int resampler = RRcfg.GetKeyInt("Resampler_num"); 
				if(resampler != -1)
					resampler_num = resampler;
			}
			
			if(RRcfg.set("KeyDefinitions")) {
				for(int i = 0; i < keynames.length; i++) {
					primarykeys[i] = defkeys[i];
					secondkeys[i] = 0;
					mousekeys[i] = 0;
					gpadkeys[i] = -1;
					
					String primary = RRcfg.GetKeyString(keynames[i], 0);
					String secondary = RRcfg.GetKeyString(keynames[i], 1);
					String mouse = RRcfg.GetKeyString(keynames[i], 2);
					String joystick = RRcfg.GetKeyString(keynames[i], 3);
					
					if(primary != null) 
						primarykeys[i] = Keymap.valueOf(primary);
					if(secondary != null)
						secondkeys[i] = Keymap.valueOf(secondary);
					if(mouse != null)
						mousekeys[i] = Keymap.valueOf(mouse);
					if(joystick != null && joystick.startsWith("JOY")) 
						gpadkeys[i] = ButtonMap.valueOf(joystick);
				}
				
				String left = RRcfg.GetKeyString("MouseDigitalAxes0_0");
				if(left != null)
					mouseaxis[AXISLEFT] = getKeyIndex(left);
				String right = RRcfg.GetKeyString("MouseDigitalAxes0_1");
				if(right != null)
					mouseaxis[AXISRIGHT] = getKeyIndex(right);
				String up = RRcfg.GetKeyString("MouseDigitalAxes1_0");
				if(up != null)
					mouseaxis[AXISUP] = getKeyIndex(up);
				String down = RRcfg.GetKeyString("MouseDigitalAxes1_1");
				if(down != null)
					mouseaxis[AXISDOWN] = getKeyIndex(down);
			}
			
			if(RRcfg.set("Controls")) {
				int value = RRcfg.GetKeyInt("UseMouse");
				if(value != -1) useMouse = value == 1;
				value = RRcfg.GetKeyInt("UseMouseInMenu");
				if(value != -1) menuMouse = value == 1;
				value = RRcfg.GetKeyInt("MouseSensitivity");
				if(value != -1) gSensitivity = value;
				value = RRcfg.GetKeyInt("MouseAiming");
				if(value != -1) 
					gMouseAim = value == 1;
				value = RRcfg.GetKeyInt("MouseAimingFlipped");
				if(value != -1) gInvertmouse = value == 1;
				value = RRcfg.GetKeyInt("MouseTurnSpeed");
				if(value != -1) gMouseTurnSpeed = value;
				value = RRcfg.GetKeyInt("MouseLookSpeed");
				if(value != -1) gMouseLookSpeed = value;
				value = RRcfg.GetKeyInt("MouseMoveSpeed");
				if(value != -1) gMouseMoveSpeed = value;
				value = RRcfg.GetKeyInt("MouseStrafeSpeed");
				if(value != -1) gMouseStrafeSpeed = value;
				value = RRcfg.GetKeyInt("MouseCursor");
				if(value != -1) gMouseCursor = value;
				value = RRcfg.GetKeyInt("MouseCursorSize");
				if(value != -1) gMouseCursorSize = value;
				value = RRcfg.GetKeyInt("UseJoystick");
				if(value != -1) useJoystick = value == 1;
				value = RRcfg.GetKeyInt("JoyTurnAxis");
				if(value != -1) gJoyTurnAxis = value;
				value = RRcfg.GetKeyInt("JoyMoveAxis");
				if(value != -1) gJoyMoveAxis = value;
				value = RRcfg.GetKeyInt("JoyStrafeAxis");
				if(value != -1) gJoyStrafeAxis = value;
				value = RRcfg.GetKeyInt("JoyLookAxis");
				if(value != -1) gJoyLookAxis = value;
				value = RRcfg.GetKeyInt("JoyTurnSpeed");
				if(value != -1) gJoyTurnSpeed = value;
				value = RRcfg.GetKeyInt("JoyLookSpeed");
				if(value != -1) gJoyLookSpeed = value;
				value = RRcfg.GetKeyInt("JoyInvertLook");
				if(value != -1) gJoyInvert = value == 1;
				value = RRcfg.GetKeyInt("JoyDeadZone");
				if(value != -1) gJoyDeadZone = value;
				value = RRcfg.GetKeyInt("JoyDevice");
				if (value != -1) gJoyDevice = value;
			}
			
			if(RRcfg.set("Options")) {
				gAutoAim = RRcfg.GetKeyInt("Autoaim") == 1;
				screen_tilting = RRcfg.GetKeyInt("Tilt");
				auto_run = RRcfg.GetKeyInt("AutoRun");
				gInterpolation = RRcfg.GetKeyInt("Interpolation");
				gShowFPS = RRcfg.GetKeyInt("ShowFPS") == 1;
				fta_on = RRcfg.GetKeyInt("MessageState");
				
				gStatSize = RRcfg.GetKeyInt("StatSize");
				if(gStatSize < 16384) gStatSize = 16384;
				gCrossSize = RRcfg.GetKeyInt("CrossSize");
				if(gCrossSize < 16384) gCrossSize = 16384;
				gShowStat = RRcfg.GetKeyInt("ShowStat");
				showMapInfo = RRcfg.GetKeyInt("showMapInfo");
				int scale = RRcfg.GetKeyInt("OSDTextScale");
				if(scale != -1)
					Console.setTextScale(scale);
				
				int voxels = RRcfg.GetKeyInt("UseVoxels");
				if(voxels != -1)
					usevoxels = (voxels == 1);
				int models = RRcfg.GetKeyInt("UseModels");
				if(models != -1)
					usemodels = (models == 1);
				int hires = RRcfg.GetKeyInt("UseHightiles");
				if(hires != -1)
					usehightile = (hires == 1);
				
				int demos = RRcfg.GetKeyInt("DemoSequence");
				if(demos != -1)
					gDemoSeq = demos;
				
				String name = RRcfg.GetKeyString("Player_name");
				if(name != null)
					pName = name;
				
				String ip = RRcfg.GetKeyString("IP_Address");
				if(ip != null)
					mAddress = ip;
				int port = RRcfg.GetKeyInt("Port");
				if(port != -1)
					mPort = port;

				int coloredk = RRcfg.GetKeyInt("Colored_keys");
				if(coloredk != -1)
					gColoredKeys = coloredk != 0;

				// NOTE this assumes true when not set explicitly (which is the case in default config)
				gPlayVideos = RRcfg.GetKeyInt("PlayVideos") != 0;
			}
			RRcfg.close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keynames.length; i++)
				primarykeys[i] = defkeys[i];
			
			mousekeys[Weapon_Fire] = MOUSE_LBUTTON;
			mousekeys[Last_Weapon_Switch] = MOUSE_RBUTTON;
			mousekeys[Open] = MOUSE_MBUTTON;
			mousekeys[Next_Weapon] = MOUSE_WHELLUP;
			mousekeys[Previous_Weapon] = MOUSE_WHELLDN;
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
			mousekeys[Weapon_Fire] = MOUSE_LBUTTON;
			mousekeys[Last_Weapon_Switch] = MOUSE_RBUTTON;
			mousekeys[Open] = MOUSE_MBUTTON;
			mousekeys[Next_Weapon] = MOUSE_WHELLUP;
			mousekeys[Previous_Weapon] = MOUSE_WHELLDN;
		}
	}
	
	public void saveBoolean(int fil, String name, boolean var)
	{
		String line =  name + " = " + (var?1:0) +"\r\n";
		Bwrite(fil, line.toCharArray(), line.length());
	}
	
	public void saveInteger(int fil, String name, int var)
	{
		String line =  name + " = " + var +"\r\n";
		Bwrite(fil, line.toCharArray(), line.length());
	}
	
	public void saveString(int fil, String text)
	{
		Bwrite(fil, text.toCharArray(), text.length());
	}

	public void save(int fil, String path)
	{
		if(fil != -1)
		{
			saveString(fil, "[Main]\r\n");
			String line =  "; Always show configuration options on startup\r\n" +
					";   0 - No\r\n" +
					";   1 - Yes\r\n";
			saveString(fil, line);
			saveBoolean(fil, "Startup", startup);
			saveBoolean(fil, "CheckNewVersion", checkVersion);
			saveBoolean(fil, "AutoloadFolder", autoloadFolder);
			saveString(fil, "Path = ");
				byte[] buf = path.getBytes(); //without this path can be distorted
				Bwrite(fil, buf, buf.length);
			saveString(fil, "\r\n;\r\n;\r\n");
			saveString(fil, "[ScreenSetup]\r\n");
			saveInteger(fil, "Fullscreen", fullscreen);
			saveInteger(fil, "ScreenWidth", ScreenWidth);
			saveInteger(fil, "ScreenHeight", ScreenHeight);
			saveInteger(fil, "Size", ud.screen_size);
			saveInteger(fil, "Crosshair", ud.crosshair);
			saveBoolean(fil, "VSync", gVSync);
			saveInteger(fil, "FPSLimit", fpslimit);
			saveBoolean(fil, "BorderlessMode", borderless);
			if(Console.IsInited())
				saveInteger(fil, "GLFilterMode", Console.Geti("r_texturemode"));
			else saveInteger(fil, "GLFilterMode", cfg_texturemode);
			saveInteger(fil, "GLAnisotropy", anisotropy);
			saveInteger(fil, "WideScreen", widescreen);
			saveInteger(fil, "Gamma", (int) (gamma * 4096));
			saveInteger(fil, "Brightness", (int) (brightness * 4096));
			saveInteger(fil, "Contrast", (int) (contrast * 4096));
			saveString(fil, ";\r\n;\r\n");
			
			saveString(fil, "[SoundSetup]\r\n");
				//Sound Setup
			saveBoolean(fil, "SoundToggle", SoundToggle);
			saveBoolean(fil, "MusicToggle", MusicToggle);
			saveInteger(fil, "SoundDriver", snddrv);
			saveInteger(fil, "SoundVolume", (int)(soundVolume * 256.0f));
			saveInteger(fil, "MaxVoices", NumVoices);
			saveInteger(fil, "MusicVolume", (int)(musicVolume * 256.0f));
			saveInteger(fil, "MidiDriver", middrv);
			saveString(fil, "MidiSynth = " + midiSynth +"\r\n");
			saveInteger(fil, "MusicType", musicType);
			saveInteger(fil, "Resampler_num", resampler_num);
			saveString(fil, ";\r\n;\r\n");

			saveString(fil, "[KeyDefinitions]\r\n");
			for(int i = 0; i < keynames.length; i++) {
				line = keynames[i] + " = \"" + Keymap.toString(primarykeys[i]) +  "\", \"" + Keymap.toString(secondkeys[i]) +  "\", \"" + Keymap.toString(mousekeys[i])  +  "\", \"" + ButtonMap.buttonName(gpadkeys[i]) + "\"\r\n";
				saveString(fil, line);
			}
			saveString(fil, "MouseDigitalAxes0_0 " + ((mouseaxis[AXISLEFT] != -1)?("= " + keynames[mouseaxis[AXISLEFT]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes0_1 " + ((mouseaxis[AXISRIGHT] != -1)?("= " + keynames[mouseaxis[AXISRIGHT]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes1_0 " + ((mouseaxis[AXISUP] != -1)?("= " + keynames[mouseaxis[AXISUP]]):"= \"N/A\"") +"\r\n");
			saveString(fil, "MouseDigitalAxes1_1 " + ((mouseaxis[AXISDOWN] != -1)?("= " + keynames[mouseaxis[AXISDOWN]]):"= \"N/A\"") +"\r\n");
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
			saveBoolean(fil, "UseJoystick", useJoystick);
			saveInteger(fil, "JoyTurnAxis", gJoyTurnAxis);
			saveInteger(fil, "JoyMoveAxis", gJoyMoveAxis);
			saveInteger(fil, "JoyStrafeAxis", gJoyStrafeAxis);
			saveInteger(fil, "JoyLookAxis", gJoyLookAxis);
			saveInteger(fil, "JoyTurnSpeed", gJoyTurnSpeed);
			saveInteger(fil, "JoyLookSpeed", gJoyLookSpeed);
			saveBoolean(fil, "JoyInvertLook", gJoyInvert);
			saveInteger(fil, "JoyDeadZone", gJoyDeadZone);
			saveInteger(fil, "JoyDevice", gJoyDevice);
			saveString(fil, ";\r\n;\r\n");
		
			saveString(fil, "[Options]\r\n");	
				//Options

			saveBoolean(fil, "Autoaim", gAutoAim);
			saveInteger(fil, "Tilt", ud.screen_tilting);
			saveInteger(fil, "AutoRun", ud.auto_run);
			saveInteger(fil, "Interpolation", gInterpolation);
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
			saveBoolean(fil, "PlayVideos", gPlayVideos);

			Bclose(fil);
		}
	}
	
	public void saveString(int fil, String name, String var)
	{
		String line =  name + " = " + var +"\r\n";
		Bwrite(fil, line.toCharArray(), line.length());
	}

	public void saveConfig(String path)
	{
		File file = Bcheck(FileUserdir+cfgname, "R");
		if(file != null) 
			file.delete();
		save(Bopen(FileUserdir+cfgname, "RW"), path);
	}
	
	public void setButton(int index, int buttonId)
	{
		gpadkeys[index] = buttonId;
		if(index < Turn_Around)
		{
			for(int i = 0; i < Turn_Around; i++)
			{
				if(i != index && buttonId == gpadkeys[i]) 
					gpadkeys[i] = -1;
			}
		} else {
			for(int i = Turn_Around; i < gpadkeys.length; i++)
			{
				if(i != index && buttonId == gpadkeys[i]) 
					gpadkeys[i] = -1;
			}
		}
	}
	
	public void setKey(int index, int keyId)
	{
		if(primarykeys[index] == 0 && secondkeys[index] == 0)
			primarykeys[index] = keyId;
		else if(primarykeys[index] != 0 && secondkeys[index] == 0 ) {
			if(keyId != primarykeys[index]) { 
				secondkeys[index] = primarykeys[index];
				primarykeys[index] = keyId;
			} else secondkeys[index] = 0;
		} 
		else
		{
			if(keyId == primarykeys[index] || keyId == secondkeys[index]) {
				primarykeys[index] = keyId;
				secondkeys[index] = 0;
			} else {
				secondkeys[index] = primarykeys[index];
				primarykeys[index] = keyId;
			}
		}
		
		for(int i = 0; i < primarykeys.length; i++)
		{
			if(i != index && keyId == primarykeys[i]) {
				if(primarykeys[i] != 0 && secondkeys[i] != 0 ) {
					primarykeys[i] = secondkeys[i];
					secondkeys[i] = 0;
				} else primarykeys[i] = 0;
			}
		}
		
		for(int i = 0; i < secondkeys.length; i++)
		{
			if(i != index && keyId == secondkeys[i]) {
				secondkeys[i] = 0;
			}
		}
	}
	
	public int checkFps(int value)
	{
		int num = -1;
		switch(fpslimit) {
			case 0: num = 0; break;
			case 30: num = 1; break;
			case 60: num = 2; break;
			case 120: num = 3; break;
			case 144: num = 4; break;
		}
		
		if(num < 0 || num >= 5) {
			num = 0;
			fpslimit = 0;
			if(Gdx.graphics instanceof BGraphics)
				((BGraphics)Gdx.graphics).setMaxFramerate(0);
		}
		
		return num;
	}
}
