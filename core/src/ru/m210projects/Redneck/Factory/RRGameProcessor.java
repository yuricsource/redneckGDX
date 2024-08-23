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

package ru.m210projects.Redneck.Factory;

import com.badlogic.gdx.math.Vector2;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.settings.GameKeys;
import ru.m210projects.Build.input.GameProcessor;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Redneck.Config.RRKeys;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.PlayerStruct;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.net.Mmulti.myconnectindex;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.spritesound;
import static ru.m210projects.Redneck.View.FTA;

public class RRGameProcessor extends GameProcessor {

    public static final int MAXANGVEL = 127;
    public static final int MAXHORIZ = 127;
    public final int TURBOTURNTIME = (TICRATE / 8); // 7
    public final int NORMALTURN = 15;
    public final int PREAMBLETURN = 5;
    public final int NORMALKEYMOVE = 40;
    public final int MAXVEL = ((NORMALKEYMOVE * 2) + 10);
    public final int MAXSVEL = ((NORMALKEYMOVE * 2) + 10);
    public int turnheldtime; //MED
    private int lastcontroltime; //MED

    private short vel;
    private float horiz, angvel;
    private final RRPrompt rrPrompt;

    public RRGameProcessor(Main main) {
        super(main);
        this.rrPrompt = new RRPrompt();
        GameKeys.Run.setName("Run / Handbrake");
    }

    public RRPrompt getRRMessage() {
        return rrPrompt;
    }

    @Override
    public void fillInput(NetInput input) {
        mouseDelta.x /= 8.0f;
        mouseDelta.y /= 4.0f;

        float daang;
        int tics;
        boolean running;
        int turnamount;
        int keymove;

        Input loc = (Input) input;
        loc.reset();

        short svel;
        if (game.pMenu.gShowMenu || Console.out.isShowing() || MODE_TYPE /*|| (game.gPaused && !ctrlKeyStatus(KEY_PAUSE))*/) {
            loc.fvel = vel = 0;
            loc.svel = 0;
            loc.avel = angvel = 0;
            loc.horz = horiz = 0;
            loc.bits = ((gamequit) << 26);

            loc.fvel += (short) fricxv;
            loc.svel += (short) fricyv;

            return;
        }

        if(isGameKeyPressed(GameKeys.Send_Message)) {
            spritesound(92, ps[myconnectindex].i);
            MODE_TYPE = true;
            rrPrompt.setCaptureInput(true);
        }

        if (multiflag == 1) {
            loc.bits = 1 << 17;
            loc.bits |= multiwhat << 18;
            loc.bits |= multipos << 19;
            multiflag = 0;
            return;
        }

        tics = engine.getTotalClock() - lastcontroltime;
        lastcontroltime = engine.getTotalClock();

        PlayerStruct p = ps[myconnectindex];

        svel = vel = 0;
        horiz = angvel = 0;

        if (p.OnMotorcycle) {
            motoinput(loc, p, tics);
            return;
        }

        if (p.OnBoat) {
            boatinput(loc, p, tics);
            return;
        }

        if (isGameKeyJustPressed(GameKeys.Mouse_Aiming)) {
            cfg.setgMouseAim(!cfg.isgMouseAim());
            FTA(44 + (cfg.isgMouseAim() ? 1 : 0), p);
        }

        loc.bits = isGameKeyPressed(GameKeys.Jump) ? 1 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Crouch) ? 2 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Weapon_Fire) ? 4 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Aim_Up) ? 8 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Aim_Down) ? 16 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Run) ? 32 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Tilt_Right) ? 128 : 0;

        if (p.alcohol_amount <= 88) {
            loc.bits |= isGameKeyPressed(RRKeys.Tilt_Left) ? 64 : 0;
        } else {
            loc.bits = loc.bits | 64;
        }

        if (p.alcohol_amount > 99) {
            loc.bits |= 1 << 14;
        }

        //if(!ctrlKeyStatus(Keys.ALT_LEFT) && !ctrlKeyStatus(Keys.SHIFT_LEFT)) {
        for (int i = 0; i < 10; i++) {
            if (isGameKeyJustPressed(cfg.getKeymap()[i + cfg.weaponIndex])) {
                loc.bits |= (i + 1) << 8;
            }
        }
        //}

        if (isGameKeyJustPressed(GameKeys.Previous_Weapon)) {
            loc.bits |= (11) << 8;
        }
        if (isGameKeyJustPressed(GameKeys.Next_Weapon)) {
            loc.bits |= (12) << 8;
        }
        if (isGameKeyJustPressed(RRKeys.Last_Weap_Switch)) {
            loc.bits |= (13) << 8;
        }

        loc.bits |= isGameKeyPressed(RRKeys.Moonshine) ? 1 << 12 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Look_Up) ? 1 << 13 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Look_Down) ? 1 << 14 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Yeehaa) ? 1 << 15 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Wiskey) ? 1 << 16 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Aim_Center) ? 1 << 18 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Holster_Weapon) ? 1 << 19 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Left) ? 1 << 20 : 0;
        loc.bits |= isKeyJustPressed(com.badlogic.gdx.Input.Keys.PAUSE) ? 1 << 21 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Quick_pee) ? 1 << 22 : 0;
        loc.bits |= cfg.isgMouseAim() ? 1 << 23 : 0;

        loc.bits |= isGameKeyPressed(RRKeys.Beer) ? 1 << 24 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Cowpie) ? 1 << 25 : 0;
        loc.bits |= gamequit << 26;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Right) ? 1 << 27 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Turn_Around) ? 1 << 28 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Open) ? 1 << 29 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Use) ? 1 << 30 : 0;
        loc.bits |= isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE) ? 1 << 31 : 0;

        if ((loc.bits & 2) != 0) {
            p.crouch_toggle = 0;
        }

        Sector psec = boardService.getSector(p.cursectnum);
        boolean CrouchMode;
        if (psec != null && psec.getLotag() != 2) {
            CrouchMode = isGameKeyJustPressed(RRKeys.Crouch_toggle);
        } else {
            CrouchMode = isGameKeyPressed(RRKeys.Crouch_toggle);
        }

        if (psec != null && psec.getLotag() == 2) {
            p.crouch_toggle = CrouchMode ? (byte) 1 : 0;
        } else if (CrouchMode) {
            p.crouch_toggle ^= 1;
        }

        if (p.crouch_toggle == 1) {
            loc.bits |= 2;
        }

        running = ((ud.auto_run == 0 && (loc.bits & 32) != 0) || ((loc.bits & 32) == 0 && ud.auto_run != 0));

        if (running) {
            turnamount = NORMALTURN << 1;
            keymove = NORMALKEYMOVE << 1;
        } else {
            turnamount = NORMALTURN;
            keymove = NORMALKEYMOVE;
        }

        if (isGameKeyPressed(GameKeys.Strafe)) {
            if (isGameKeyPressed(GameKeys.Turn_Left)) {
                svel -= (short) -keymove;
            }
            if (isGameKeyPressed(GameKeys.Turn_Right)) {
                svel -= (short) keymove;
            }
            svel = (short) BClipRange(svel - 20 * ctrlGetMouseStrafe(), -keymove, keymove);
        } else {
            if (isGameKeyPressed(GameKeys.Turn_Left)) {
                turnheldtime += tics;
                if (turnheldtime >= TURBOTURNTIME) {
                    angvel -= turnamount;
                } else {
                    angvel -= PREAMBLETURN;
                }
            } else if (isGameKeyPressed(GameKeys.Turn_Right)) {
                turnheldtime += tics;
                if (turnheldtime >= TURBOTURNTIME) {
                    angvel += turnamount;
                } else {
                    angvel += PREAMBLETURN;
                }
            } else {
                turnheldtime = 0;
            }
            angvel = BClipRange(angvel + ctrlGetMouseTurn(), -1024, 1024);
        }

        if (isGameKeyPressed(GameKeys.Strafe_Left)) {
            svel += (short) keymove;
        }
        if (isGameKeyPressed(GameKeys.Strafe_Right)) {
            svel -= (short) keymove;
        }

        if (p.alcohol_amount < 66 || p.alcohol_amount > 87) {
            if (isGameKeyPressed(GameKeys.Move_Forward)) {
                vel += (short) keymove;
            }
            if (isGameKeyPressed(GameKeys.Move_Backward)) {
                vel -= (short) keymove;
            }
        } else {
            if (isGameKeyPressed(GameKeys.Move_Forward)) {
                vel += (short) keymove;
                if ((p.alcohol_amount & 1) != 0) {
                    svel += (short) keymove;
                } else {
                    svel -= (short) keymove;
                }
            }
            if (isGameKeyPressed(GameKeys.Move_Backward)) {
                vel -= (short) keymove;
                if ((p.alcohol_amount & 1) != 0) {
                    svel -= (short) keymove;
                } else {
                    svel += (short) keymove;
                }
            }
        }

        Renderer renderer = game.getRenderer();
        int ydim = renderer.getHeight();
        if (cfg.isgMouseAim()) {
            horiz = BClipRange(ctrlGetMouseLook(!cfg.isgInvertmouse()), -(ydim >> 1), 100 + (ydim >> 1));
        } else {
            vel = (short) BClipRange(vel - ctrlGetMouseMove(), -4 * keymove, 4 * keymove);
        }

        Vector2 stick1 = ctrlGetStick(JoyStick.LOOKING);
        Vector2 stick2 = ctrlGetStick(JoyStick.MOVING);

        float lookx = stick1.x;
        float looky = stick1.y;

        if (looky != 0) {
            float k = 8.0f;
            horiz = BClipRange(horiz - k * looky * cfg.getJoyLookSpeed(), -(ydim >> 1), 100 + (ydim >> 1));
        }

        if (lookx != 0) {
            float k = 8.0f;
            angvel = BClipRange(angvel + k * lookx * cfg.getJoyTurnSpeed(), -1024, 1024);
        }

        if (stick2.y != 0) {
            vel = (short) BClipRange(vel - (keymove * stick2.y), -4 * keymove, 4 * keymove);
        }
        if (stick2.x != 0) {
            svel = (short) BClipRange(svel - (keymove * stick2.x), -4 * keymove, 4 * keymove);
        }

        if (vel < -MAXVEL) {
            vel = -MAXVEL;
        }
        if (vel > MAXVEL) {
            vel = MAXVEL;
        }
        if (svel < -MAXSVEL) {
            svel = -MAXSVEL;
        }
        if (svel > MAXSVEL) {
            svel = MAXSVEL;
        }
        if (angvel < -MAXANGVEL) {
            angvel = -MAXANGVEL;
        }
        if (angvel > MAXANGVEL) {
            angvel = MAXANGVEL;
        }
        if (horiz < -MAXHORIZ) {
            horiz = -MAXHORIZ;
        }
        if (horiz > MAXHORIZ) {
            horiz = MAXHORIZ;
        }

        if (ud.scrollmode && ud.overhead_on != 0) {
            ud.folfvel = vel;
            ud.folavel = angvel;
            loc.fvel = 0;
            loc.svel = 0;
            loc.avel = 0;
            loc.horz = 0;
            return;
        }

        daang = p.ang;

        int momx = (int) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
        int momy = (int) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

        momx += (int) (svel * BSinAngle(BClampAngle(daang)) / 512.0f);
        momy += (int) (svel * BCosAngle(BClampAngle(daang + 1024)) / 512.0f);

        momx += fricxv;
        momy += fricyv;

        loc.fvel = (short) momx;
        loc.svel = (short) momy;
        loc.avel = angvel;
        loc.horz = horiz;
    }

    public void motoinput(Input loc, PlayerStruct p, int tics) {
        loc.bits = isGameKeyPressed(GameKeys.Weapon_Fire) ? 4 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Moonshine) ? 1 << 12 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Yeehaa) ? 1 << 15 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Wiskey) ? 1 << 16 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Left) ? 1 << 20 : 0;
        loc.bits |= isKeyJustPressed(com.badlogic.gdx.Input.Keys.PAUSE) ? 1 << 21 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Beer) ? 1 << 24 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Cowpie) ? 1 << 25 : 0;
        loc.bits |= gamequit << 26;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Right) ? 1 << 27 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Open) ? 1 << 29 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Use) ? 1 << 30 : 0;

        boolean left = isGameKeyPressed(GameKeys.Turn_Left) || isGameKeyPressed(GameKeys.Strafe_Left);
        boolean right = isGameKeyPressed(GameKeys.Turn_Right) || isGameKeyPressed(GameKeys.Strafe_Right);

        Vector2 stick1 = ctrlGetStick(JoyStick.LOOKING);
        Vector2 stick2 = ctrlGetStick(JoyStick.MOVING);

        if (stick1.x != 0 && stick1.x < 0.5f) {
            left = true;
        }
        if (stick1.x > 0.5f) {
            right = true;
        }

        if (stick2.y != 0 && stick2.y < 0.5f) {
            loc.bits |= 1;
        }
        if (stick2.y > 0.5f) {
            loc.bits |= 8;
        }

        if (p.CarVar1 == 0) {
            loc.bits |= isGameKeyPressed(GameKeys.Move_Forward) ? 1 : 0;
            loc.bits |= isGameKeyPressed(GameKeys.Run) ? 2 : 0;
            loc.bits |= isGameKeyPressed(GameKeys.Move_Backward) ? 8 : 0;
        }

        if (left) {
            loc.bits |= 16;
        }
        if (right) {
            loc.bits |= 64;
        }

        boolean revers = (p.CarSpeed <= 0);
        if (p.CarSpeed != 0 && p.on_ground) {
            if (left || p.CarVar2 < 0) {
                turnheldtime += tics;
//	    		p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
                if (turnheldtime >= 15 && p.CarSpeed > 0) {
                    angvel -= 10;
                } else {
                    angvel -= 3 * (revers ? -1 : 1);
                }
            } else if (right || p.CarVar2 > 0) {
                turnheldtime += tics;
//	    		p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
                if (turnheldtime >= 15 && p.CarSpeed > 0) {
                    angvel += 10;
                } else {
                    angvel += 3 * (revers ? -1 : 1);
                }
            } else {
                turnheldtime = 0;
            }
        }

        vel += p.CarSpeed;
        if (vel < -15) {
            vel = -15;
        }
        if (vel > 120) {
            vel = 120;
        }
        if (angvel < -MAXANGVEL) {
            angvel = -MAXANGVEL;
        }
        if (angvel > MAXANGVEL) {
            angvel = MAXANGVEL;
        }

        if (ud.scrollmode && ud.overhead_on != 0) {
            ud.folfvel = vel;
            ud.folavel = angvel;
            loc.fvel = 0;
            loc.svel = 0;
            loc.avel = 0;
            loc.horz = 0;
            return;
        }

        float daang = p.ang;

        short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
        short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

        momx += (short) fricxv;
        momy += (short) fricyv;

        loc.fvel = momx;
        loc.svel = momy;


        loc.horz = horiz;

        if (p.CarSpeed < 80) {
            loc.carang = (byte) BClipRange(ctrlGetMouseTurn(), -127, 127);
        } else {
            angvel = BClipRange(angvel + ((int) ctrlGetMouseTurn() >> 2), -1024, 1024);
        }

        loc.avel = angvel;
    }

    public void boatinput(Input loc, PlayerStruct p, int tics) {
        loc.bits = isGameKeyPressed(GameKeys.Weapon_Fire) ? 4 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Moonshine) ? 1 << 12 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Yeehaa) ? 1 << 15 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Wiskey) ? 1 << 16 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Left) ? 1 << 20 : 0;
        loc.bits |= isKeyJustPressed(com.badlogic.gdx.Input.Keys.PAUSE) ? 1 << 21 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Beer) ? 1 << 24 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Cowpie) ? 1 << 25 : 0;
        loc.bits |= gamequit << 26;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Right) ? 1 << 27 : 0;
        loc.bits |= isGameKeyPressed(GameKeys.Open) ? 1 << 29 : 0;
        loc.bits |= isGameKeyPressed(RRKeys.Inventory_Use) ? 1 << 30 : 0;

        boolean left = isGameKeyPressed(GameKeys.Turn_Left) || isGameKeyPressed(GameKeys.Strafe_Left);
        boolean right = isGameKeyPressed(GameKeys.Turn_Right) || isGameKeyPressed(GameKeys.Strafe_Right);
        Vector2 stick1 = ctrlGetStick(JoyStick.LOOKING);
        Vector2 stick2 = ctrlGetStick(JoyStick.MOVING);

        if (stick1.x != 0 && stick1.x < 0.5f) {
            left = true;
        }
        if (stick1.x > 0.5f) {
            right = true;
        }

        if (stick2.y != 0 && stick2.y < 0.5f) {
            loc.bits |= 1;
        }
        if (stick2.y > 0.5f) {
            loc.bits |= 8;
        }

        if (p.CarVar1 == 0) {
            loc.bits |= isGameKeyPressed(GameKeys.Move_Forward) ? 1 : 0;
            loc.bits |= isGameKeyPressed(GameKeys.Run) ? 2 : 0;
            loc.bits |= isGameKeyPressed(GameKeys.Move_Backward) ? 8 : 0;
        }

        if (left) {
            loc.bits |= 16;
        }
        if (right) {
            loc.bits |= 64;
        }

        if (p.CarSpeed != 0) {
            if (left || p.CarVar2 < 0) {
                turnheldtime += tics;
                if (turnheldtime >= 15) {
                    if (p.NotOnWater != 0) {
                        angvel -= 3;
                    } else {
                        angvel -= 10;
                    }
                } else {
                    if (p.NotOnWater != 0) {
                        angvel--;
                    } else {
                        angvel -= 3;
                    }
                }
            } else if (right || p.CarVar2 > 0) {
                turnheldtime += tics;
                if (turnheldtime >= 15) {
                    if (p.NotOnWater != 0) {
                        angvel += 3;
                    } else {
                        angvel += 10;
                    }
                } else {
                    if (p.NotOnWater != 0) {
                        angvel++;
                    } else {
                        angvel += 3;
                    }
                }
            } else if (p.NotOnWater == 0) {
                turnheldtime = 0;
            }
        }

        vel += p.CarSpeed;
        if (vel < -15) {
            vel = -15;
        }
        if (vel > 120) {
            vel = 120;
        }
        if (angvel < -MAXANGVEL) {
            angvel = -MAXANGVEL;
        }
        if (angvel > MAXANGVEL) {
            angvel = MAXANGVEL;
        }

        if (ud.scrollmode && ud.overhead_on != 0) {
            ud.folfvel = vel;
            ud.folavel = angvel;
            loc.fvel = 0;
            loc.svel = 0;
            loc.avel = 0;
            loc.horz = 0;
            return;
        }

        float daang = p.ang;

        short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
        short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

        momx += (short) fricxv;
        momy += (short) fricyv;

        loc.fvel = momx;
        loc.svel = momy;
        loc.horz = horiz;

        if (p.CarSpeed < 40) {
            loc.carang = (byte) BClipRange(ctrlGetMouseTurn(), -127, 127);
        } else {
            angvel = BClipRange(angvel + ((int) ctrlGetMouseTurn() >> 2), -1024, 1024);
        }

        loc.avel = angvel;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (rrPrompt.isCaptured()) {
            if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                rrPrompt.setCaptureInput(false);
                rrPrompt.clear();
                MODE_TYPE = false;
            } else {
                rrPrompt.keyDown(keycode);
            }
            return true;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int i) {
        rrPrompt.keyUp(i);
        return super.keyUp(i);
    }

    @Override
    public boolean keyRepeat(int keycode) {
        if (rrPrompt.isCaptured()) {
            rrPrompt.keyRepeat(keycode);
            return true;
        }
        return super.keyRepeat(keycode);
    }

    @Override
    public boolean keyTyped(char i) {
        if (rrPrompt.isCaptured()) {
            rrPrompt.keyTyped(i);
            return true;
        }
        return super.keyTyped(i);
    }
}
