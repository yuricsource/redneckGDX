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

import com.badlogic.gdx.Input.Keys;
import ru.m210projects.Build.input.GameKey;
import ru.m210projects.Build.settings.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static ru.m210projects.Build.input.keymap.Keymap.*;
import static ru.m210projects.Redneck.Globals.ud;

public class Config extends GameConfig {

    public static final int[] defkeys = {
            Keys.W,            //Move_Forward 0
            Keys.S,                //Move_Backward 1
            Keys.LEFT,            //Turn_Left 2
            Keys.RIGHT,            //Turn_Right 3
            Keys.BACKSPACE,    //Turn_Around 4
            Keys.ALT_LEFT,        //Strafe 5
            Keys.A,                //Strafe_Left 6
            Keys.D,            //Strafe_Right 7
            Keys.SPACE,        //Jump 8
            Keys.CONTROL_LEFT,    //Crouch 9
            Keys.SHIFT_LEFT,    //Run 10
            Keys.E,                //Open 12
            Keys.CONTROL_RIGHT, //Weapon_Fire 13
            Keys.APOSTROPHE,//Next_Weapon 44
            Keys.SEMICOLON,    //Previous_Weapon 45
            Keys.PAGE_UP,        //Look_Up 18
            Keys.PAGE_DOWN,    //Look_Down 19
            Keys.TAB,            //Map_Toggle 35
            Keys.EQUALS,    //Enlarge_Screen 38
            Keys.MINUS,    //Shrink_Screen 37
            Keys.T,        //Send_Message 39
            Keys.U,            //Mouse_Aiming 42
            Keys.ESCAPE,    //Toggle_menu 53
            Keys.GRAVE,        //Show_Console 54

            Keys.CAPS_LOCK,        //AutoRun 11
            Keys.X,            //Quick_pee 14
            Keys.HOME,            //Aim_Up 15
            Keys.END,            //Aim_Down 16
            Keys.NUMPAD_5,        //Aim_Center 17
            Keys.INSERT,        //Tilt_Left 20
            Keys.FORWARD_DEL, //Tilt_Right 21
            Keys.NUM_1,    //Weapon_1 22
            Keys.NUM_2,        //Weapon_2 23
            Keys.NUM_3,        //Weapon_3 24
            Keys.NUM_4,        //Weapon_4 25
            Keys.NUM_5,        //Weapon_5 26
            Keys.NUM_6,        //Weapon_6 27
            Keys.NUM_7,        //Weapon_7 28
            Keys.NUM_8,        //Weapon_8 29
            Keys.NUM_9,        //Weapon_9 30
            Keys.NUM_0,        //Weapon_10 31
            Keys.ENTER,    //Inventory_Use 32
            Keys.LEFT_BRACKET, //Inventory_Left 33
            Keys.RIGHT_BRACKET, //Inventory_Right 34
            Keys.F,            //Map_Follow_Mode 36
            Keys.K,            //See_Coop_View 40
            Keys.F7,        //See_Chase_View 41
            Keys.I,            //Toggle_Crosshair 43
            Keys.BACKSLASH, //Holster_Weapon 46
            Keys.Y,        //Show_Opponents_Weapon 47
            Keys.H,            //Yeehaa 48
            Keys.B,            //Beer 49
            Keys.P,            //Cowpie 50
            Keys.M,            //Wiskey 51
            Keys.R,            //Moonshine 52
            Keys.F1,        //Show_HelpScreen 55
            Keys.F2,        //Show_Save	56
            Keys.F3,        //Show_Load 57
            Keys.F4,        //Show_Sounds 58
            Keys.F5,        //Show_Options 59
            Keys.F6,        //QuickSave	60
            0,        //ToggleMessages 61
            Keys.F9,        //QuickLoad 62
            Keys.F10,        //Quit 63
            Keys.F11,        //Gamma 64
            Keys.F12,        //MakeScreenshot 65
            Keys.Q,                //Last_Weapon_Switch
            0,                //Crouch_toggle
    };
    public static final int[] defclassickeys = {
            Keys.UP,            //Move_Forward 0
            Keys.DOWN,            //Move_Backward 1
            Keys.LEFT,            //Turn_Left 2
            Keys.RIGHT,            //Turn_Right 3
            Keys.BACKSPACE,    //Turn_Around 4
            Keys.ALT_LEFT,        //Strafe 5
            Keys.COMMA,            //Strafe_Left 6
            Keys.PERIOD,        //Strafe_Right 7
            Keys.A,            //Jump 8
            Keys.Z,            //Crouch 9
            Keys.SHIFT_LEFT,    //Run 10
            Keys.SPACE,                //Open 12
            Keys.CONTROL_LEFT, //Weapon_Fire 13
            Keys.APOSTROPHE,//Next_Weapon 44
            Keys.SEMICOLON,    //Previous_Weapon 45
            Keys.PAGE_UP,        //Look_Up 18
            Keys.PAGE_DOWN,    //Look_Down 19
            Keys.TAB,            //Map_Toggle 35
            Keys.EQUALS,    //Enlarge_Screen 38
            Keys.MINUS,    //Shrink_Screen 37
            Keys.T,        //Send_Message 39
            Keys.U,            //Mouse_Aiming 42
            Keys.ESCAPE,    //Open_menu 53
            0,        //Show_Console 54



            Keys.CAPS_LOCK,        //AutoRun 11
            Keys.GRAVE,        //Quick_pee 14
            Keys.HOME,            //Aim_Up 15
            Keys.END,            //Aim_Down 16
            Keys.NUMPAD_5,        //Aim_Center 17
            Keys.INSERT,        //Tilt_Left 20
            Keys.FORWARD_DEL, //Tilt_Right 21
            Keys.NUM_1,    //Weapon_1 22
            Keys.NUM_2,        //Weapon_2 23
            Keys.NUM_3,        //Weapon_3 24
            Keys.NUM_4,        //Weapon_4 25
            Keys.NUM_5,        //Weapon_5 26
            Keys.NUM_6,        //Weapon_6 27
            Keys.NUM_7,        //Weapon_7 28
            Keys.NUM_8,        //Weapon_8 29
            Keys.NUM_9,        //Weapon_9 30
            Keys.NUM_0,        //Weapon_10 31
            Keys.ENTER,    //Inventory_Use 32
            Keys.LEFT_BRACKET, //Inventory_Left 33
            Keys.RIGHT_BRACKET, //Inventory_Right 34
            Keys.F,            //Map_Follow_Mode 36
            Keys.K,            //See_Coop_View 40
            Keys.F7,        //See_Chase_View 41
            Keys.I,            //Toggle_Crosshair 43
            Keys.SCROLL_LOCK,  //Holster_Weapon 46
            Keys.E,        //Show_Opponents_Weapon 47
            Keys.Y,            //Yeehaa 48
            Keys.B,            //Beer 49
            Keys.C,            //Cowpie 50
            Keys.W,            //Wiskey 51
            Keys.M,            //Moonshine 52
            Keys.F1,        //Show_HelpScreen 55
            Keys.F2,        //Show_Save	56
            Keys.F3,        //Show_Load 57
            Keys.F4,        //Show_Sounds 58
            Keys.F5,        //Show_Options 59
            Keys.F6,        //QuickSave	60
            Keys.F8,        //ToggleMessages 61
            Keys.F9,        //QuickLoad 62
            Keys.F10,        //Quit 63
            Keys.F11,        //Gamma 64
            Keys.F12,        //MakeScreenshot 65
            0,                //Last_Weapon_Switch
            0,                //Crouch_toggle
    };
    public int gStatSize = 65536;
    public int gCrossSize = 65536;
    public int gShowStat = 1;
    public int showMapInfo = 1;
    public final boolean AmbienceToggle = true;
    public final boolean VoiceToggle = true;
    public int gDemoSeq = 0;
    public boolean gAutoAim = true;
    public int screen_size = 2;
    public int crosshair = 1;
    public int screen_tilting = 1;
    public int auto_run = 1;
    public int fta_on = 1;
    public boolean gColoredKeys;
    public boolean gShuffleMusic = false;
    public int weaponIndex = -1;


    public Config(Path path) {
        super(path);
    }

    @Override
    public void load(Properties prop) {
        setpName("Leonard");
        super.load(prop);
    }

    @Override
    protected InputContext createDefaultInputContext() {
        return new InputContext(getKeyMap(), defkeys, defclassickeys) {

            @Override
            protected void clearInput() {
                super.clearInput();
                weaponIndex = IntStream.range(0, keymap.length).filter(i -> keymap[i].equals(RRKeys.Weapon_1)).findFirst().orElse(-1);
            }

            @Override
            public void resetInput(boolean classicKeys) {
                super.resetInput(classicKeys);

                weaponIndex = IntStream.range(0, keymap.length).filter(i -> keymap[i].equals(RRKeys.Weapon_1)).findFirst().orElse(-1);
                primarykeys[MOUSE_KEYS_INDEX][RRKeys.Last_Weap_Switch.getNum()] = MOUSE_RBUTTON;
            }
        };
    }

    @Override
    protected ConfigContext createDefaultGameContext() {
        return new ConfigContext() {
            @Override
            public void load(Properties prop) {
                if (prop.setContext("Options")) {
                    screen_size = prop.getIntValue("Size", screen_size);
                    crosshair = prop.getIntValue("Crosshair", crosshair);
                    fta_on = prop.getIntValue("MessageState", fta_on);
                    gAutoAim = prop.getBooleanValue("Autoaim", gAutoAim);
                    screen_tilting = prop.getIntValue("Tilt", screen_tilting);
                    auto_run = prop.getIntValue("AutoRun", auto_run);
                    gStatSize = Math.max(prop.getIntValue("StatSize", gStatSize), 16384);
                    gCrossSize = Math.max(prop.getIntValue("CrossSize", gCrossSize), 16384);
                    gShowStat = prop.getIntValue("ShowStat", gShowStat);
                    showMapInfo = prop.getIntValue("showMapInfo", showMapInfo);
                    gDemoSeq = prop.getIntValue("DemoSequence", gDemoSeq);

                    gColoredKeys = prop.getBooleanValue("Colored_keys", gColoredKeys);
                    gShuffleMusic = prop.getBooleanValue("ShuffleMusic", gShuffleMusic);
                }
            }

            @Override
            public void save(OutputStream os) throws IOException {
                putString(os, "[Options]\r\n");
                //Options
                putInteger(os, "Size", ud.screen_size);
                putInteger(os, "Crosshair", ud.crosshair);
                putInteger(os, "MessageState", ud.fta_on);
                putBoolean(os, "Autoaim", gAutoAim);
                putInteger(os, "Tilt", ud.screen_tilting);
                putInteger(os, "AutoRun", ud.auto_run);
                putInteger(os, "MessageState", ud.fta_on);
                putInteger(os, "StatSize", gStatSize);
                putInteger(os, "CrossSize", gCrossSize);
                putInteger(os, "ShowStat", gShowStat);
                putInteger(os, "showMapInfo", showMapInfo);
                putInteger(os, "DemoSequence", gDemoSeq);

                putBoolean(os, "Colored_keys", gColoredKeys);
                putBoolean(os, "ShuffleMusic", gShuffleMusic);
            }
        };
    }

    public GameKey[] getKeyMap() {
        return new GameKey[]{
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
                GameKeys.Menu_Toggle,
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

    }

    public enum RRKeys implements GameKey {
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

        public int getNum() {
            return GameKeys.values().length + ordinal();
        }

        public String getName() {
            return name();
        }

    }

}
