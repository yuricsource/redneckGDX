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

import ru.m210projects.Build.Architecture.common.audio.Source;
import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.Serializable;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.filehandle.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;
import static ru.m210projects.Redneck.Main.boardService;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.SoundDefs.DUKE_SCREAM;
import static ru.m210projects.Redneck.Sounds.spritesound;

public class PlayerStruct implements Serializable<PlayerStruct> {

    public final PLocation prevView = new PLocation();
    public final int[] ammo_amount = new int[MAX_WEAPONSRA];
    public final short[] weaprecs = new short[16];
    public final boolean[] gotweapon = new boolean[MAX_WEAPONSRA];
    public final short[] pals = new short[3];
    public final short[] gotkey = new short[5];
    public int oposx, oposy, oposz;
    public int ohorizoff;
    public float ohoriz, oang;
    public int exitx, exity, numloogs, loogcnt;
    public int posx, posy, posz, invdisptime;
    public float horiz, ang, angvel;
    public int bobposx, bobposy, pyoff, opyoff;
    public int posxv, posyv, poszv, last_pissed_time, truefz, truecz;
    public int player_par;
    public int bobcounter, weapon_sway;
    public int pals_time, randomflamex, crack_time;
    public int aim_mode, auto_aim;
    public int cursectnum, look_ang, last_extra, subweapon;
    public short wackedbyactor, frag, fraggedself;
    public short curr_weapon, last_weapon, tipincs, horizoff, wantweaponfire;
    public short beer_amount, newowner, hurt_delay, hbomb_hold_delay;
    public short jumping_counter, airleft, knee_incs, access_incs;
    public short access_wallnum, access_spritenum;
    public short kickback_pic, weapon_ang, whishkey_amount;
    public short somethingonplayer, on_crane, i, one_parallax_sectnum;
    public short random_club_frame, fist_incs;
    public short one_eighty_count, cheat_phase;
    public short dummyplayersprite, extra_extra8, quick_kick;
    public short yeehaa_amount, actorsqu, timebeforeexit, customexitsound;
    public short weapreccnt;
    public short interface_toggle_flag;
    public short rotscrnang, dead_flag, show_empty_weapon;
    public short snorkle_amount, cowpie_amount, moonshine_amount, shield_amount;
    public short holoduke_on, pycount, weapon_pos, frag_ps;
    public short transporter_hold, last_full_weapon, footprintshade, boot_amount;
    public Source scream_voice;
    public short on_warping_sector, footprintcount;
    public short hbomb_on, jumping_toggle, rapid_fire_hold;
    public boolean on_ground;
    public int inven_icon, buttonpalette, jetpack_on;
    public short spritebridge, lastrandomspot;
    public short scuba_on, footprintpal, heat_on;
    public short holster_weapon, falling_counter;
    public boolean refresh_inventory;
    public byte[] palette;
    public short toggle_key_flag, knuckle_incs;
    public short walking_snd_toggle, palookup, hard_landing;
    public short max_secret_rooms;
    public short secret_rooms;
    public short max_actors_killed, actors_killed, return_to_center;
    public byte last_used_weapon;
    public byte crouch_toggle;
    public int field_280;
    public int field_X;
    public int field_Y;
    public short field_28E;
    public int field_290;
    public short field_57C;
    public int detonate_count;
    public short alcohol_meter;
    public short gut_meter;
    public short alcohol_amount;
    public short gut_amount;
    public int alcohol_count;
    public int gut_count;
    public byte drunk;

    public byte shotgunstatus;
    public byte shotgun_splitshot;
    public short kickback;
    public short field_count;

    //RA
    public boolean OnBoat;
    public boolean OnMotorcycle;
    public short CarSpeed;
    public boolean CarOnGround;
    public short SlotWin;
    public int CarVar6;
    public boolean isSwamp;
    public int CarVar1;
    public boolean isSea;
    public int field_601; //isMoto?
    public int chiken_phase;
    public byte chiken_pic;
    public short field_607; //not used
    public short MamaEnd;
    public int fogtype;
    public int TiltStatus;
    public short CarVar2;
    public short VBumpTarget;
    public short VBumpNow;
    public int CarVar3;
    public short TurbCount;
    public short CarVar5;
    public short CarVar4;
    public int NotOnWater;
    public int SeaSick;
    public int DrugMode;
    public short drug_type;
    public short drug_intensive;
    public short drug_timer;
    public int drug_aspect;

    public Sprite getPlayerSprite() {
        return boardService.getSprite(i);
    }

    public void UpdatePlayerLoc() {

        ILoc oldLoc = game.pInt.getsprinterpolate(i);
        if (oldLoc != null) {
            oldLoc.x = posx;
            oldLoc.y = posy;
            oldLoc.z = posz;
        }
        prevView.x = posx;
        prevView.y = posy;
        prevView.z = posz;
        prevView.ang = ang;
        prevView.lookang = look_ang;
        prevView.rotscrnang = rotscrnang;
        prevView.horizoff = horizoff;
        prevView.horiz = horiz;
    }

    public void copy(PlayerStruct src) {
        this.exitx = src.exitx;
        this.exity = src.exity;
        this.numloogs = src.numloogs;
        this.loogcnt = src.loogcnt;
        this.posx = src.posx;
        this.posy = src.posy;
        this.posz = src.posz;
        this.horiz = src.horiz;
        this.ohoriz = src.ohoriz;
        this.ohorizoff = src.ohorizoff;
        this.invdisptime = src.invdisptime;
        this.bobposx = src.bobposx;
        this.bobposy = src.bobposy;
        this.oposx = src.oposx;
        this.oposy = src.oposy;
        this.oposz = src.oposz;
        this.pyoff = src.pyoff;
        this.opyoff = src.opyoff;
        this.posxv = src.posxv;
        this.posyv = src.posyv;
        this.poszv = src.poszv;
        this.last_pissed_time = src.last_pissed_time;
        this.truefz = src.truefz;
        this.truecz = src.truecz;
        this.player_par = src.player_par;
        this.bobcounter = src.bobcounter;
        this.weapon_sway = src.weapon_sway;
        this.pals_time = src.pals_time;
        this.randomflamex = src.randomflamex;
        this.crack_time = src.crack_time;
        this.aim_mode = src.aim_mode;
        this.ang = src.ang;
        this.oang = src.oang;
        this.angvel = src.angvel;
        this.cursectnum = src.cursectnum;
        this.look_ang = src.look_ang;
        this.last_extra = src.last_extra;
        this.subweapon = src.subweapon;
        this.wackedbyactor = src.wackedbyactor;
        this.frag = src.frag;
        this.fraggedself = src.fraggedself;
        System.arraycopy(src.ammo_amount, 0, this.ammo_amount, 0, MAX_WEAPONSRA);
        this.curr_weapon = src.curr_weapon;
        this.last_weapon = src.last_weapon;
        this.tipincs = src.tipincs;
        this.horizoff = src.horizoff;
        this.wantweaponfire = src.wantweaponfire;
        this.beer_amount = src.beer_amount;
        this.newowner = src.newowner;
        this.hurt_delay = src.hurt_delay;
        this.hbomb_hold_delay = src.hbomb_hold_delay;
        this.jumping_counter = src.jumping_counter;
        this.airleft = src.airleft;
        this.knee_incs = src.knee_incs;
        this.access_incs = src.access_incs;
        this.access_wallnum = src.access_wallnum;
        this.access_spritenum = src.access_spritenum;
        this.kickback_pic = src.kickback_pic;
        this.weapon_ang = src.weapon_ang;
        this.whishkey_amount = src.whishkey_amount;
        this.somethingonplayer = src.somethingonplayer;
        this.on_crane = src.on_crane;
        this.i = src.i;
        this.one_parallax_sectnum = src.one_parallax_sectnum;
        this.random_club_frame = src.random_club_frame;
        this.fist_incs = src.fist_incs;
        this.one_eighty_count = src.one_eighty_count;
        this.cheat_phase = src.cheat_phase;
        this.dummyplayersprite = src.dummyplayersprite;
        this.extra_extra8 = src.extra_extra8;
        this.quick_kick = src.quick_kick;
        this.yeehaa_amount = src.yeehaa_amount;
        this.actorsqu = src.actorsqu;
        this.timebeforeexit = src.timebeforeexit;
        this.customexitsound = src.customexitsound;
        System.arraycopy(src.weaprecs, 0, this.weaprecs, 0, 16);
        this.weapreccnt = src.weapreccnt;
        this.interface_toggle_flag = src.interface_toggle_flag;
        this.rotscrnang = src.rotscrnang;
        this.dead_flag = src.dead_flag;
        this.show_empty_weapon = src.show_empty_weapon;
        this.snorkle_amount = src.snorkle_amount;
        this.cowpie_amount = src.cowpie_amount;
        this.moonshine_amount = src.moonshine_amount;
        this.shield_amount = src.shield_amount;
        this.holoduke_on = src.holoduke_on;
        this.pycount = src.pycount;
        this.weapon_pos = src.weapon_pos;
        this.frag_ps = src.frag_ps;
        this.transporter_hold = src.transporter_hold;
        this.last_full_weapon = src.last_full_weapon;
        this.footprintshade = src.footprintshade;
        this.boot_amount = src.boot_amount;
        this.scream_voice = src.scream_voice;
        this.on_warping_sector = src.on_warping_sector;
        this.footprintcount = src.footprintcount;
        this.hbomb_on = src.hbomb_on;
        this.jumping_toggle = src.jumping_toggle;
        this.rapid_fire_hold = src.rapid_fire_hold;
        this.on_ground = src.on_ground;
        this.inven_icon = src.inven_icon;
        this.buttonpalette = src.buttonpalette;
        this.jetpack_on = src.jetpack_on;
        this.spritebridge = src.spritebridge;
        this.lastrandomspot = src.lastrandomspot;
        this.scuba_on = src.scuba_on;
        this.footprintpal = src.footprintpal;
        this.heat_on = src.heat_on;
        this.holster_weapon = src.holster_weapon;
        this.falling_counter = src.falling_counter;
        System.arraycopy(src.gotweapon, 0, this.gotweapon, 0, MAX_WEAPONSRA);
        this.refresh_inventory = src.refresh_inventory;
        this.palette = src.palette;
        this.toggle_key_flag = src.toggle_key_flag;
        this.knuckle_incs = src.knuckle_incs;
        this.walking_snd_toggle = src.walking_snd_toggle;
        this.palookup = src.palookup;
        this.hard_landing = src.hard_landing;
        this.max_secret_rooms = src.max_secret_rooms;
        this.secret_rooms = src.secret_rooms;
        System.arraycopy(src.pals, 0, this.pals, 0, 3);
        this.max_actors_killed = src.max_actors_killed;
        this.actors_killed = src.actors_killed;
        this.return_to_center = src.return_to_center;

        this.last_used_weapon = src.last_used_weapon;
        this.crouch_toggle = src.crouch_toggle;

        this.field_280 = src.field_280;
        this.field_X = src.field_X;
        this.field_Y = src.field_Y;
        this.field_28E = src.field_28E;
        this.field_290 = src.field_290;

        this.field_57C = src.field_57C;
        this.detonate_count = src.detonate_count;

        this.alcohol_meter = src.alcohol_meter;
        this.gut_meter = src.gut_meter;
        this.alcohol_amount = src.alcohol_amount;
        this.gut_amount = src.gut_amount;
        this.alcohol_count = src.alcohol_count;

        System.arraycopy(src.gotkey, 0, this.gotkey, 0, 5);

        this.gut_count = src.gut_count;
        this.drunk = src.drunk;
        this.shotgunstatus = src.shotgunstatus;
        this.shotgun_splitshot = src.shotgun_splitshot;
        this.kickback = src.kickback;
        this.field_count = src.field_count;

        this.OnBoat = src.OnBoat;
        this.OnMotorcycle = src.OnMotorcycle;
        this.CarSpeed = src.CarSpeed;
        this.CarOnGround = src.CarOnGround;
        this.SlotWin = src.SlotWin;
        this.CarVar6 = src.CarVar6;
        this.isSwamp = src.isSwamp;
        this.CarVar1 = src.CarVar1;
        this.isSea = src.isSea;
        this.field_601 = src.field_601;
        this.chiken_phase = src.chiken_phase;
        this.chiken_pic = src.chiken_pic;
        this.field_607 = src.field_607;
        this.MamaEnd = src.MamaEnd;
        this.fogtype = src.fogtype;
        this.TiltStatus = src.TiltStatus;
        this.CarVar2 = src.CarVar2;
        this.VBumpTarget = src.VBumpTarget;
        this.VBumpNow = src.VBumpNow;
        this.CarVar3 = src.CarVar3;
        this.TurbCount = src.TurbCount;
        this.CarVar5 = src.CarVar5;
        this.CarVar4 = src.CarVar4;
        this.NotOnWater = src.NotOnWater;
        this.SeaSick = src.SeaSick;
        this.DrugMode = src.DrugMode;
        this.drug_type = src.drug_type;
        this.drug_intensive = src.drug_intensive;
        this.drug_timer = src.drug_timer;
        this.drug_aspect = src.drug_aspect;
    }

    @Override
    public PlayerStruct readObject(InputStream is) throws IOException {
        exitx = StreamUtils.readInt(is);
        exity = StreamUtils.readInt(is);
        numloogs = StreamUtils.readInt(is);
        loogcnt = StreamUtils.readInt(is);
        posx = StreamUtils.readInt(is);
        posy = StreamUtils.readInt(is);
        posz = StreamUtils.readInt(is);
        horiz = StreamUtils.readFloat(is);
        ohoriz = StreamUtils.readFloat(is);
        ohorizoff = StreamUtils.readInt(is);
        invdisptime = StreamUtils.readInt(is);
        bobposx = StreamUtils.readInt(is);
        bobposy = StreamUtils.readInt(is);
        oposx = StreamUtils.readInt(is);
        oposy = StreamUtils.readInt(is);
        oposz = StreamUtils.readInt(is);
        pyoff = StreamUtils.readInt(is);
        opyoff = StreamUtils.readInt(is);
        posxv = StreamUtils.readInt(is);
        posyv = StreamUtils.readInt(is);
        poszv = StreamUtils.readInt(is);
        last_pissed_time = StreamUtils.readInt(is);
        truefz = StreamUtils.readInt(is);
        truecz = StreamUtils.readInt(is);
        player_par = StreamUtils.readInt(is);
        bobcounter = StreamUtils.readInt(is);
        weapon_sway = StreamUtils.readInt(is);
        pals_time = StreamUtils.readInt(is);
        randomflamex = StreamUtils.readInt(is);
        crack_time = StreamUtils.readInt(is);
        aim_mode = StreamUtils.readInt(is);
        auto_aim = StreamUtils.readUnsignedByte(is);
        ang = StreamUtils.readFloat(is);
        oang = StreamUtils.readFloat(is);
        angvel = StreamUtils.readFloat(is);
        cursectnum = StreamUtils.readShort(is);
        look_ang = StreamUtils.readShort(is);
        last_extra = StreamUtils.readShort(is);
        subweapon = StreamUtils.readShort(is);
        wackedbyactor = StreamUtils.readShort(is);
        frag = StreamUtils.readShort(is);
        fraggedself = StreamUtils.readShort(is);
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            ammo_amount[i] = StreamUtils.readShort(is);
        }
        curr_weapon = StreamUtils.readShort(is);


        last_weapon = StreamUtils.readShort(is);
        tipincs = StreamUtils.readShort(is);
        horizoff = StreamUtils.readShort(is);
        wantweaponfire = StreamUtils.readShort(is);
        beer_amount = StreamUtils.readShort(is);
        newowner = StreamUtils.readShort(is);
        hurt_delay = StreamUtils.readShort(is);
        hbomb_hold_delay = StreamUtils.readShort(is);
        jumping_counter = StreamUtils.readShort(is);
        airleft = StreamUtils.readShort(is);
        knee_incs = StreamUtils.readShort(is);
        access_incs = StreamUtils.readShort(is);
        access_wallnum = StreamUtils.readShort(is);
        access_spritenum = StreamUtils.readShort(is);
        kickback_pic = StreamUtils.readShort(is);
        weapon_ang = StreamUtils.readShort(is);
        whishkey_amount = StreamUtils.readShort(is);
        somethingonplayer = StreamUtils.readShort(is);
        on_crane = StreamUtils.readShort(is);
        i = StreamUtils.readShort(is);
        one_parallax_sectnum = StreamUtils.readShort(is);
        random_club_frame = StreamUtils.readShort(is);
        fist_incs = StreamUtils.readShort(is);
        one_eighty_count = StreamUtils.readShort(is);
        cheat_phase = StreamUtils.readShort(is);
        dummyplayersprite = StreamUtils.readShort(is);
        extra_extra8 = StreamUtils.readShort(is);
        quick_kick = StreamUtils.readShort(is);
        yeehaa_amount = StreamUtils.readShort(is);
        actorsqu = StreamUtils.readShort(is);
        timebeforeexit = StreamUtils.readShort(is);
        customexitsound = StreamUtils.readShort(is);
        for (int i = 0; i < 16; i++) {
            weaprecs[i] = StreamUtils.readShort(is);
        }

        weapreccnt = StreamUtils.readShort(is);
        interface_toggle_flag = StreamUtils.readShort(is);
        rotscrnang = StreamUtils.readShort(is);
        dead_flag = StreamUtils.readShort(is);
        show_empty_weapon = StreamUtils.readShort(is);
        snorkle_amount = StreamUtils.readShort(is);
        cowpie_amount = StreamUtils.readShort(is);
        moonshine_amount = StreamUtils.readShort(is);
        shield_amount = StreamUtils.readShort(is);
        holoduke_on = StreamUtils.readShort(is);
        pycount = StreamUtils.readShort(is);
        weapon_pos = StreamUtils.readShort(is);
        frag_ps = StreamUtils.readShort(is);
        transporter_hold = StreamUtils.readShort(is);
        last_full_weapon = StreamUtils.readShort(is);
        footprintshade = StreamUtils.readShort(is);
        boot_amount = StreamUtils.readShort(is);
        boolean svoice = StreamUtils.readBoolean(is);
        if (svoice) {
            scream_voice = spritesound(DUKE_SCREAM, i);
        }
        on_warping_sector = StreamUtils.readByte(is);
        footprintcount = StreamUtils.readByte(is);
        hbomb_on = StreamUtils.readByte(is);
        jumping_toggle = StreamUtils.readByte(is);
        rapid_fire_hold = StreamUtils.readByte(is);
        on_ground = StreamUtils.readBoolean(is);
        inven_icon = StreamUtils.readByte(is);
        buttonpalette = StreamUtils.readByte(is);
        jetpack_on = StreamUtils.readByte(is);
        spritebridge = StreamUtils.readByte(is);
        lastrandomspot = StreamUtils.readByte(is);
        scuba_on = StreamUtils.readByte(is);
        footprintpal = StreamUtils.readByte(is);
        heat_on = StreamUtils.readByte(is);
        holster_weapon = StreamUtils.readByte(is);
        falling_counter = StreamUtils.readByte(is);
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            gotweapon[i] = StreamUtils.readBoolean(is);
        }
        refresh_inventory = StreamUtils.readBoolean(is);
        this.palette = StreamUtils.readBytes(is, 768);
        toggle_key_flag = StreamUtils.readByte(is);
        knuckle_incs = StreamUtils.readByte(is);
        walking_snd_toggle = StreamUtils.readByte(is);
        palookup = StreamUtils.readByte(is);
        hard_landing = StreamUtils.readByte(is);
        max_secret_rooms = StreamUtils.readShort(is);
        secret_rooms = StreamUtils.readShort(is);
        for (int i = 0; i < 3; i++) {
            pals[i] = StreamUtils.readByte(is);
        }
        max_actors_killed = StreamUtils.readShort(is);
        actors_killed = StreamUtils.readShort(is);
        return_to_center = StreamUtils.readByte(is);

        last_used_weapon = StreamUtils.readByte(is);
        crouch_toggle = StreamUtils.readByte(is);

        field_280 = StreamUtils.readInt(is);
        field_X = StreamUtils.readInt(is);
        field_Y = StreamUtils.readInt(is);
        field_28E = StreamUtils.readShort(is);
        field_290 = StreamUtils.readInt(is);

        field_57C = StreamUtils.readShort(is); //detonate ticks
        detonate_count = StreamUtils.readInt(is);

        alcohol_meter = StreamUtils.readShort(is);
        gut_meter = StreamUtils.readShort(is);
        alcohol_amount = StreamUtils.readShort(is);
        gut_amount = StreamUtils.readShort(is);
        alcohol_count = StreamUtils.readInt(is);
        gut_count = StreamUtils.readInt(is);

        for (int i = 0; i < 5; i++) {
            gotkey[i] = StreamUtils.readShort(is);
        }

        drunk = StreamUtils.readByte(is);
        shotgunstatus = StreamUtils.readByte(is);
        shotgun_splitshot = StreamUtils.readByte(is);

        kickback = StreamUtils.readShort(is); //weapon horiz
        field_count = StreamUtils.readShort(is);

        //RA
        OnBoat = StreamUtils.readBoolean(is);
        OnMotorcycle = StreamUtils.readBoolean(is);
        CarSpeed = StreamUtils.readShort(is);
        CarOnGround = StreamUtils.readBoolean(is);
        SlotWin = StreamUtils.readShort(is);
        CarVar6 = StreamUtils.readInt(is);
        isSwamp = StreamUtils.readBoolean(is);
        CarVar1 = StreamUtils.readInt(is);
        isSea = StreamUtils.readBoolean(is);
        field_601 = StreamUtils.readInt(is);
        chiken_phase = StreamUtils.readInt(is);
        chiken_pic = StreamUtils.readByte(is);
        field_607 = StreamUtils.readShort(is);
        MamaEnd = StreamUtils.readShort(is);
        fogtype = StreamUtils.readInt(is);
        TiltStatus = StreamUtils.readInt(is);
        CarVar2 = StreamUtils.readShort(is);
        VBumpTarget = StreamUtils.readShort(is);
        VBumpNow = StreamUtils.readShort(is);
        CarVar3 = StreamUtils.readInt(is);
        TurbCount = StreamUtils.readShort(is);
        CarVar5 = StreamUtils.readShort(is);
        CarVar4 = StreamUtils.readShort(is);
        NotOnWater = StreamUtils.readInt(is);
        SeaSick = StreamUtils.readInt(is);
        DrugMode = StreamUtils.readInt(is);
        drug_type = StreamUtils.readShort(is);
        drug_intensive = StreamUtils.readShort(is);
        drug_timer = StreamUtils.readShort(is);
        drug_aspect = StreamUtils.readInt(is);

        return this;
    }

    @Override
    public PlayerStruct writeObject(OutputStream os) throws IOException {
        StreamUtils.writeInt(os, exitx);
        StreamUtils.writeInt(os, exity);
        StreamUtils.writeInt(os, numloogs);
        StreamUtils.writeInt(os, loogcnt);
        StreamUtils.writeInt(os, posx);
        StreamUtils.writeInt(os, posy);
        StreamUtils.writeInt(os, posz);
        StreamUtils.writeFloat(os, horiz);
        StreamUtils.writeFloat(os, ohoriz);
        StreamUtils.writeInt(os, ohorizoff);
        StreamUtils.writeInt(os, invdisptime);
        StreamUtils.writeInt(os, bobposx);
        StreamUtils.writeInt(os, bobposy);
        StreamUtils.writeInt(os, oposx);
        StreamUtils.writeInt(os, oposy);
        StreamUtils.writeInt(os, oposz);
        StreamUtils.writeInt(os, pyoff);
        StreamUtils.writeInt(os, opyoff);
        StreamUtils.writeInt(os, posxv);
        StreamUtils.writeInt(os, posyv);
        StreamUtils.writeInt(os, poszv);
        StreamUtils.writeInt(os, last_pissed_time);
        StreamUtils.writeInt(os, truefz);
        StreamUtils.writeInt(os, truecz);
        StreamUtils.writeInt(os, player_par);
        StreamUtils.writeInt(os, bobcounter);
        StreamUtils.writeInt(os, weapon_sway);
        StreamUtils.writeInt(os, pals_time);
        StreamUtils.writeInt(os, randomflamex);
        StreamUtils.writeInt(os, crack_time);
        StreamUtils.writeInt(os, aim_mode);
        StreamUtils.writeByte(os, (byte) auto_aim);
        StreamUtils.writeFloat(os, ang);
        StreamUtils.writeFloat(os, oang);
        StreamUtils.writeFloat(os, angvel);
        StreamUtils.writeShort(os, (short) cursectnum);
        StreamUtils.writeShort(os, (short) look_ang);
        StreamUtils.writeShort(os, (short) last_extra);
        StreamUtils.writeShort(os, (short) subweapon);
        StreamUtils.writeShort(os, wackedbyactor);
        StreamUtils.writeShort(os, frag);
        StreamUtils.writeShort(os, fraggedself);
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            StreamUtils.writeShort(os, (short) ammo_amount[i]);
        }
        StreamUtils.writeShort(os, curr_weapon);
        StreamUtils.writeShort(os, last_weapon);
        StreamUtils.writeShort(os, tipincs);
        StreamUtils.writeShort(os, horizoff);
        StreamUtils.writeShort(os, wantweaponfire);
        StreamUtils.writeShort(os, beer_amount);
        StreamUtils.writeShort(os, newowner);
        StreamUtils.writeShort(os, hurt_delay);
        StreamUtils.writeShort(os, hbomb_hold_delay);
        StreamUtils.writeShort(os, jumping_counter);
        StreamUtils.writeShort(os, airleft);
        StreamUtils.writeShort(os, knee_incs);
        StreamUtils.writeShort(os, access_incs);
        StreamUtils.writeShort(os, access_wallnum);
        StreamUtils.writeShort(os, access_spritenum);
        StreamUtils.writeShort(os, kickback_pic);
        StreamUtils.writeShort(os, weapon_ang);
        StreamUtils.writeShort(os, whishkey_amount);
        StreamUtils.writeShort(os, somethingonplayer);
        StreamUtils.writeShort(os, on_crane);
        StreamUtils.writeShort(os, i);
        StreamUtils.writeShort(os, one_parallax_sectnum);
        StreamUtils.writeShort(os, random_club_frame);
        StreamUtils.writeShort(os, fist_incs);
        StreamUtils.writeShort(os, one_eighty_count);
        StreamUtils.writeShort(os, cheat_phase);
        StreamUtils.writeShort(os, dummyplayersprite);
        StreamUtils.writeShort(os, extra_extra8);
        StreamUtils.writeShort(os, quick_kick);
        StreamUtils.writeShort(os, yeehaa_amount);
        StreamUtils.writeShort(os, actorsqu);
        StreamUtils.writeShort(os, timebeforeexit);
        StreamUtils.writeShort(os, customexitsound);
        for (int i = 0; i < 16; i++) {
            StreamUtils.writeShort(os, weaprecs[i]);
        }
        StreamUtils.writeShort(os, weapreccnt);
        StreamUtils.writeShort(os, interface_toggle_flag);
        StreamUtils.writeShort(os, rotscrnang);
        StreamUtils.writeShort(os, dead_flag);
        StreamUtils.writeShort(os, show_empty_weapon);
        StreamUtils.writeShort(os, snorkle_amount);
        StreamUtils.writeShort(os, cowpie_amount);
        StreamUtils.writeShort(os, moonshine_amount);
        StreamUtils.writeShort(os, shield_amount);
        StreamUtils.writeShort(os, holoduke_on);
        StreamUtils.writeShort(os, pycount);
        StreamUtils.writeShort(os, weapon_pos);
        StreamUtils.writeShort(os, frag_ps);
        StreamUtils.writeShort(os, transporter_hold);
        StreamUtils.writeShort(os, last_full_weapon);
        StreamUtils.writeShort(os, footprintshade);
        StreamUtils.writeShort(os, boot_amount);
        StreamUtils.writeByte(os, scream_voice != null ? (byte) 1 : 0);
        StreamUtils.writeByte(os, (byte) on_warping_sector);
        StreamUtils.writeByte(os, (byte) footprintcount);
        StreamUtils.writeByte(os, (byte) hbomb_on);
        StreamUtils.writeByte(os, (byte) jumping_toggle);
        StreamUtils.writeByte(os, (byte) rapid_fire_hold);
        StreamUtils.writeByte(os, on_ground ? (byte) 1 : 0);
        StreamUtils.writeByte(os, (byte) inven_icon);
        StreamUtils.writeByte(os, (byte) buttonpalette);
        StreamUtils.writeByte(os, (byte) jetpack_on);
        StreamUtils.writeByte(os, (byte) spritebridge);
        StreamUtils.writeByte(os, (byte) lastrandomspot);
        StreamUtils.writeByte(os, (byte) scuba_on);
        StreamUtils.writeByte(os, (byte) footprintpal);
        StreamUtils.writeByte(os, (byte) heat_on);
        StreamUtils.writeByte(os, (byte) holster_weapon);
        StreamUtils.writeByte(os, (byte) falling_counter);
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            StreamUtils.writeByte(os, gotweapon[i] ? (byte) 1 : 0);
        }
        StreamUtils.writeByte(os, refresh_inventory ? (byte) 1 : 0);
        StreamUtils.writeBytes(os, palette);
        StreamUtils.writeByte(os, (byte) toggle_key_flag);
        StreamUtils.writeByte(os, (byte) knuckle_incs);
        StreamUtils.writeByte(os, (byte) walking_snd_toggle);
        StreamUtils.writeByte(os, (byte) palookup);
        StreamUtils.writeByte(os, (byte) hard_landing);
        StreamUtils.writeShort(os, max_secret_rooms);
        StreamUtils.writeShort(os, secret_rooms);
        for (int i = 0; i < 3; i++) {
            StreamUtils.writeByte(os, (byte) pals[i]);
        }
        StreamUtils.writeShort(os, max_actors_killed);
        StreamUtils.writeShort(os, actors_killed);
        StreamUtils.writeByte(os, (byte) return_to_center);
        StreamUtils.writeByte(os, last_used_weapon);
        StreamUtils.writeByte(os, crouch_toggle);
        StreamUtils.writeInt(os, field_280);
        StreamUtils.writeInt(os, field_X);
        StreamUtils.writeInt(os, field_Y);
        StreamUtils.writeShort(os, field_28E);
        StreamUtils.writeInt(os, field_290);
        StreamUtils.writeShort(os, field_57C);
        StreamUtils.writeInt(os, detonate_count);
        StreamUtils.writeShort(os, alcohol_meter);
        StreamUtils.writeShort(os, gut_meter);
        StreamUtils.writeShort(os, alcohol_amount);
        StreamUtils.writeShort(os, gut_amount);
        StreamUtils.writeInt(os, alcohol_count);
        StreamUtils.writeInt(os, gut_count);
        for (int i = 0; i < 5; i++) {
            StreamUtils.writeShort(os, gotkey[i]);
        }
        StreamUtils.writeByte(os, drunk);
        StreamUtils.writeByte(os, shotgunstatus);
        StreamUtils.writeByte(os, shotgun_splitshot);
        StreamUtils.writeShort(os, kickback);
        StreamUtils.writeShort(os, field_count);

        //RA
        StreamUtils.writeByte(os, OnBoat ? (byte) 1 : 0);
        StreamUtils.writeByte(os, OnMotorcycle ? (byte) 1 : 0);
        StreamUtils.writeShort(os, CarSpeed);
        StreamUtils.writeByte(os, CarOnGround ? (byte) 1 : 0);
        StreamUtils.writeShort(os, SlotWin);
        StreamUtils.writeInt(os, CarVar6);
        StreamUtils.writeByte(os, isSwamp ? (byte) 1 : 0);
        StreamUtils.writeInt(os, CarVar1);
        StreamUtils.writeByte(os, isSea ? (byte) 1 : 0);
        StreamUtils.writeInt(os, field_601);
        StreamUtils.writeInt(os, chiken_phase);
        StreamUtils.writeByte(os, chiken_pic);
        StreamUtils.writeShort(os, field_607);
        StreamUtils.writeShort(os, MamaEnd);
        StreamUtils.writeInt(os, fogtype);
        StreamUtils.writeInt(os, TiltStatus);
        StreamUtils.writeShort(os, CarVar2);
        StreamUtils.writeShort(os, VBumpTarget);
        StreamUtils.writeShort(os, VBumpNow);
        StreamUtils.writeInt(os, CarVar3);
        StreamUtils.writeShort(os, TurbCount);
        StreamUtils.writeShort(os, CarVar5);
        StreamUtils.writeShort(os, CarVar4);
        StreamUtils.writeInt(os, NotOnWater);
        StreamUtils.writeInt(os, SeaSick);
        StreamUtils.writeInt(os, DrugMode);
        StreamUtils.writeShort(os, drug_type);
        StreamUtils.writeShort(os, drug_intensive);
        StreamUtils.writeShort(os, drug_timer);
        StreamUtils.writeInt(os, drug_aspect);

        return this;
    }

    public void reset() {
        this.exitx = 0;
        this.exity = 0;
        this.numloogs = 0;
        this.loogcnt = 0;
        this.posx = 0;
        this.posy = 0;
        this.posz = 0;
        this.horiz = 0;
        this.ohoriz = 0;
        this.ohorizoff = 0;
        this.invdisptime = 0;
        this.bobposx = 0;
        this.bobposy = 0;
        this.oposx = 0;
        this.oposy = 0;
        this.oposz = 0;
        this.pyoff = 0;
        this.opyoff = 0;
        this.posxv = 0;
        this.posyv = 0;
        this.poszv = 0;
        this.last_pissed_time = 0;
        this.truefz = 0;
        this.truecz = 0;
        this.player_par = 0;
        this.bobcounter = 0;
        this.weapon_sway = 0;
        this.pals_time = 0;
        this.randomflamex = 0;
        this.crack_time = 0;
        this.aim_mode = 0;
        this.ang = 0;
        this.oang = 0;
        this.angvel = 0;
        this.cursectnum = 0;
        this.look_ang = 0;
        this.last_extra = 0;
        this.subweapon = 0;
        this.wackedbyactor = 0;
        this.frag = 0;
        this.fraggedself = 0;
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            ammo_amount[i] = 0;
        }
        this.curr_weapon = 0;
        this.last_weapon = 0;
        this.tipincs = 0;
        this.horizoff = 0;
        this.wantweaponfire = 0;
        this.beer_amount = 0;
        this.newowner = 0;
        this.hurt_delay = 0;
        this.hbomb_hold_delay = 0;
        this.jumping_counter = 0;
        this.airleft = 0;
        this.knee_incs = 0;
        this.access_incs = 0;
        this.access_wallnum = 0;
        this.access_spritenum = 0;
        this.kickback_pic = 0;
        this.weapon_ang = 0;
        this.whishkey_amount = 0;
        this.somethingonplayer = 0;
        this.on_crane = 0;
        this.i = 0;
        this.one_parallax_sectnum = 0;
        this.random_club_frame = 0;
        this.fist_incs = 0;
        this.one_eighty_count = 0;
        this.cheat_phase = 0;
        this.dummyplayersprite = 0;
        this.extra_extra8 = 0;
        this.quick_kick = 0;
        this.yeehaa_amount = 0;
        this.actorsqu = 0;
        this.timebeforeexit = 0;
        this.customexitsound = 0;
        for (int i = 0; i < 16; i++) {
            weaprecs[i] = 0;
        }
        this.weapreccnt = 0;
        this.interface_toggle_flag = 0;
        this.rotscrnang = 0;
        this.dead_flag = 0;
        this.show_empty_weapon = 0;
        this.snorkle_amount = 0;
        this.cowpie_amount = 0;
        this.moonshine_amount = 0;
        this.shield_amount = 0;
        this.holoduke_on = 0;
        this.pycount = 0;
        this.weapon_pos = 0;
        this.frag_ps = 0;
        this.transporter_hold = 0;
        this.last_full_weapon = 0;
        this.footprintshade = 0;
        this.boot_amount = 0;
        this.scream_voice = null;
        this.on_warping_sector = 0;
        this.footprintcount = 0;
        this.hbomb_on = 0;
        this.jumping_toggle = 0;
        this.rapid_fire_hold = 0;
        this.on_ground = false;
        this.inven_icon = 0;
        this.buttonpalette = 0;
        this.jetpack_on = 0; //XXX
        this.spritebridge = 0;
        this.lastrandomspot = 0;
        this.scuba_on = 0;
        this.footprintpal = 0;
        this.heat_on = 0;
        this.holster_weapon = 0;
        this.falling_counter = 0;
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            gotweapon[i] = false;
        }
        this.refresh_inventory = false;
        this.palette = null;
        this.toggle_key_flag = 0;
        this.knuckle_incs = 0;
        this.walking_snd_toggle = 0;
        this.palookup = 0;
        this.hard_landing = 0;
        this.max_secret_rooms = 0;
        this.secret_rooms = 0;
        for (int i = 0; i < 3; i++) {
            pals[i] = 0;
        }
        this.max_actors_killed = 0;
        this.actors_killed = 0;
        this.return_to_center = 0;

        this.last_used_weapon = 0;
        this.crouch_toggle = 0;

        this.field_280 = 0;
        this.field_X = 0;
        this.field_Y = 0;
        this.field_28E = 0;
        this.field_290 = 0;

        this.field_57C = 0;
        this.detonate_count = 0;

        this.alcohol_meter = 0;
        this.gut_meter = 0;
        this.alcohol_amount = 0;
        this.gut_amount = 0;
        this.alcohol_count = 0;
        for (int i = 0; i < 5; i++) {
            this.gotkey[i] = 0;
        }

        this.gut_count = 0;
        this.drunk = 0;
        this.shotgunstatus = 0;
        this.shotgun_splitshot = 0;
        this.kickback = 0;
        this.field_count = 0;

        this.OnBoat = false;
        this.OnMotorcycle = false;
        this.CarSpeed = 0;
        this.CarOnGround = false;
        this.SlotWin = 0;
        this.CarVar6 = 0;
        this.isSwamp = false;
        this.CarVar1 = 0;
        this.isSea = false;
        this.field_601 = 0;
        this.chiken_phase = 0;
        this.chiken_pic = 0;
        this.field_607 = 0;
        this.MamaEnd = 0;
        this.fogtype = 0;
        this.TiltStatus = 0;
        this.CarVar2 = 0;
        this.VBumpTarget = 0;
        this.VBumpNow = 0;
        this.CarVar3 = 0;
        this.TurbCount = 0;
        this.CarVar5 = 0;
        this.CarVar4 = 0;
        this.NotOnWater = 0;
        this.SeaSick = 0;
        this.DrugMode = 0;
        this.drug_type = 0;
        this.drug_intensive = 0;
        this.drug_timer = 0;
        this.drug_aspect = 0;
    }

    public String toString() {
        StringBuilder out = new StringBuilder("exitx " + exitx + " \r\n");
        out.append("exity ").append(exity).append(" \r\n");
        out.append("numloogs ").append(numloogs).append(" \r\n");
        out.append("loogcnt ").append(loogcnt).append(" \r\n");
        out.append("posx ").append(posx).append(" \r\n");
        out.append("posy ").append(posy).append(" \r\n");
        out.append("posz ").append(posz).append(" \r\n");
        out.append("horiz ").append(horiz).append(" \r\n");
        out.append("ohoriz ").append(ohoriz).append(" \r\n");
        out.append("ohorizoff ").append(ohorizoff).append(" \r\n");
        out.append("invdisptime ").append(invdisptime).append(" \r\n");
        out.append("bobposx ").append(bobposx).append(" \r\n");
        out.append("bobposy ").append(bobposy).append(" \r\n");
        out.append("oposx ").append(oposx).append(" \r\n");
        out.append("oposy ").append(oposy).append(" \r\n");
        out.append("oposz ").append(oposz).append(" \r\n");
        out.append("pyoff ").append(pyoff).append(" \r\n");
        out.append("opyoff ").append(opyoff).append(" \r\n");
        out.append("posxv ").append(posxv).append(" \r\n");
        out.append("posyv ").append(posyv).append(" \r\n");
        out.append("poszv ").append(poszv).append(" \r\n");
        out.append("last_pissed_time ").append(last_pissed_time).append(" \r\n");
        out.append("truefz ").append(truefz).append(" \r\n");
        out.append("truecz ").append(truecz).append(" \r\n");
        out.append("player_par ").append(player_par).append(" \r\n");
        out.append("bobcounter ").append(bobcounter).append(" \r\n");
        out.append("weapon_sway ").append(weapon_sway).append(" \r\n");
        out.append("pals_time ").append(pals_time).append(" \r\n");
        out.append("randomflamex ").append(randomflamex).append(" \r\n");
        out.append("crack_time ").append(crack_time).append(" \r\n");
        out.append("aim_mode ").append(aim_mode).append(" \r\n");
        out.append("auto_aim ").append(auto_aim).append(" \r\n");
        out.append("ang ").append(ang).append(" \r\n");
        out.append("oang ").append(oang).append(" \r\n");
        out.append("angvel ").append(angvel).append(" \r\n");
        out.append("cursectnum ").append(cursectnum).append(" \r\n");
        out.append("look_ang ").append(look_ang).append(" \r\n");
        out.append("last_extra ").append(last_extra).append(" \r\n");
        out.append("subweapon ").append(subweapon).append(" \r\n");
        out.append("wackedbyactor ").append(wackedbyactor).append(" \r\n");
        out.append("frag ").append(frag).append(" \r\n");
        out.append("fraggedself ").append(fraggedself).append(" \r\n");
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            out.append("ammo_amount[").append(i).append("] ").append(ammo_amount[i]).append(" \r\n");
        }
        out.append("curr_weapon ").append(curr_weapon).append(" \r\n");
        out.append("last_weapon ").append(last_weapon).append(" \r\n");
        out.append("tipincs ").append(tipincs).append(" \r\n");
        out.append("horizoff ").append(horizoff).append(" \r\n");
        out.append("wantweaponfire ").append(wantweaponfire).append(" \r\n");
        out.append("beer_amount ").append(beer_amount).append(" \r\n");
        out.append("newowner ").append(newowner).append(" \r\n");
        out.append("hurt_delay ").append(hurt_delay).append(" \r\n");
        out.append("hbomb_hold_delay ").append(hbomb_hold_delay).append(" \r\n");
        out.append("jumping_counter ").append(jumping_counter).append(" \r\n");
        out.append("airleft ").append(airleft).append(" \r\n");
        out.append("knee_incs ").append(knee_incs).append(" \r\n");
        out.append("access_incs ").append(access_incs).append(" \r\n");
        out.append("access_wallnum ").append(access_wallnum).append(" \r\n");
        out.append("access_spritenum ").append(access_spritenum).append(" \r\n");
        out.append("kickback_pic ").append(kickback_pic).append(" \r\n");
        out.append("weapon_ang ").append(weapon_ang).append(" \r\n");
        out.append("whishkey_amount ").append(whishkey_amount).append(" \r\n");
        out.append("somethingonplayer ").append(somethingonplayer).append(" \r\n");
        out.append("on_crane ").append(on_crane).append(" \r\n");
        out.append("i ").append(i).append(" \r\n");
        out.append("one_parallax_sectnum ").append(one_parallax_sectnum).append(" \r\n");
        out.append("random_club_frame ").append(random_club_frame).append(" \r\n");
        out.append("fist_incs ").append(fist_incs).append(" \r\n");
        out.append("one_eighty_count ").append(one_eighty_count).append(" \r\n");
        out.append("cheat_phase ").append(cheat_phase).append(" \r\n");
        out.append("dummyplayersprite ").append(dummyplayersprite).append(" \r\n");
        out.append("extra_extra8 ").append(extra_extra8).append(" \r\n");
        out.append("quick_kick ").append(quick_kick).append(" \r\n");
        out.append("yeehaa_amount ").append(yeehaa_amount).append(" \r\n");
        out.append("actorsqu ").append(actorsqu).append(" \r\n");
        out.append("timebeforeexit ").append(timebeforeexit).append(" \r\n");
        out.append("customexitsound ").append(customexitsound).append(" \r\n");
        for (int i = 0; i < 16; i++) {
            out.append("weaprecs[").append(i).append("] ").append(weaprecs[i]).append(" \r\n");
        }
        out.append("weapreccnt ").append(weapreccnt).append(" \r\n");
        out.append("interface_toggle_flag ").append(interface_toggle_flag).append(" \r\n");
        out.append("rotscrnang ").append(rotscrnang).append(" \r\n");
        out.append("dead_flag ").append(dead_flag).append(" \r\n");
        out.append("show_empty_weapon ").append(show_empty_weapon).append(" \r\n");
        out.append("snorkle_amount ").append(snorkle_amount).append(" \r\n");
        out.append("cowpie_amount ").append(cowpie_amount).append(" \r\n");
        out.append("moonshine_amount ").append(moonshine_amount).append(" \r\n");
        out.append("shield_amount ").append(shield_amount).append(" \r\n");
        out.append("holoduke_on ").append(holoduke_on).append(" \r\n");
        out.append("pycount ").append(pycount).append(" \r\n");
        out.append("weapon_pos ").append(weapon_pos).append(" \r\n");
        out.append("frag_ps ").append(frag_ps).append(" \r\n");
        out.append("transporter_hold ").append(transporter_hold).append(" \r\n");
        out.append("last_full_weapon ").append(last_full_weapon).append(" \r\n");
        out.append("footprintshade ").append(footprintshade).append(" \r\n");
        out.append("boot_amount ").append(boot_amount).append(" \r\n");
        out.append("scream_voice ").append(scream_voice).append(" \r\n");

        out.append("on_warping_sector ").append(on_warping_sector).append(" \r\n");
        out.append("footprintcount ").append(footprintcount).append(" \r\n");
        out.append("hbomb_on ").append(hbomb_on).append(" \r\n");
        out.append("jumping_toggle ").append(jumping_toggle).append(" \r\n");
        out.append("rapid_fire_hold ").append(rapid_fire_hold).append(" \r\n");
        out.append("on_ground ").append(on_ground).append(" \r\n");
        out.append("inven_icon ").append(inven_icon).append(" \r\n");
        out.append("buttonpalette ").append(buttonpalette).append(" \r\n");
        out.append("jetpack_on ").append(jetpack_on).append(" \r\n");
        out.append("spritebridge ").append(spritebridge).append(" \r\n");
        out.append("lastrandomspot ").append(lastrandomspot).append(" \r\n");
        out.append("scuba_on ").append(scuba_on).append(" \r\n");
        out.append("footprintpal ").append(footprintpal).append(" \r\n");
        out.append("heat_on ").append(heat_on).append(" \r\n");
        out.append("holster_weapon ").append(holster_weapon).append(" \r\n");
        out.append("falling_counter ").append(falling_counter).append(" \r\n");
        for (int i = 0; i < MAX_WEAPONSRA; i++) {
            out.append("gotweapon[").append(i).append("] ").append(gotweapon[i]).append(" \r\n");
        }
        out.append("refresh_inventory ").append(refresh_inventory).append(" \r\n");
        out.append("palette ").append(CRC32.getChecksum(palette)).append(" \r\n");
        out.append("toggle_key_flag ").append(toggle_key_flag).append(" \r\n");
        out.append("knuckle_incs ").append(knuckle_incs).append(" \r\n");
        out.append("walking_snd_toggle ").append(walking_snd_toggle).append(" \r\n");
        out.append("palookup ").append(palookup).append(" \r\n");
        out.append("hard_landing ").append(hard_landing).append(" \r\n");
        out.append("max_secret_rooms ").append(max_secret_rooms).append(" \r\n");
        out.append("secret_rooms ").append(secret_rooms).append(" \r\n");
        for (int i = 0; i < 3; i++) {
            out.append("pals[").append(i).append("] ").append(pals[i]).append(" \r\n");
        }
        out.append("max_actors_killed ").append(max_actors_killed).append(" \r\n");
        out.append("actors_killed ").append(actors_killed).append(" \r\n");
        out.append("return_to_center ").append(return_to_center).append(" \r\n");

        out.append("last_used_weapon ").append(last_used_weapon).append(" \r\n");
        out.append("crouch_toggle ").append(crouch_toggle).append(" \r\n");

        out.append("field_280 ").append(field_280).append(" \r\n");
        out.append("field_X ").append(field_X).append(" \r\n");
        out.append("field_Y ").append(field_Y).append(" \r\n");
        out.append("field_28E ").append(field_28E).append(" \r\n");
        out.append("field_290 ").append(field_290).append(" \r\n");

        out.append("field_57C ").append(field_57C).append(" \r\n");
        out.append("detonate_count ").append(detonate_count).append(" \r\n");

        out.append("alcohol_meter ").append(alcohol_meter).append(" \r\n");
        out.append("gut_meter ").append(gut_meter).append(" \r\n");
        out.append("alcohol_amount ").append(alcohol_amount).append(" \r\n");
        out.append("gut_amount ").append(gut_amount).append(" \r\n");
        out.append("alcohol_count ").append(alcohol_count).append(" \r\n");
        out.append("gut_count ").append(gut_count).append(" \r\n");

        for (int i = 0; i < 3; i++) {
            out.append("gotkey[").append(i).append("] ").append(gotkey[i]).append(" \r\n");
        }

        out.append("drunk ").append(drunk).append(" \r\n");
        out.append("shotgunstatus ").append(shotgunstatus).append(" \r\n");
        out.append("shotgun_splitshot ").append(shotgun_splitshot).append(" \r\n");

        out.append("kickback ").append(kickback).append(" \r\n");
        out.append("field_count ").append(field_count).append(" \r\n");

        //RA
        out.append("OnBoat ").append(OnBoat).append(" \r\n");
        out.append("OnMotorcycle ").append(OnMotorcycle).append(" \r\n");
        out.append("CarSpeed ").append(CarSpeed).append(" \r\n");
        out.append("CarOnGround ").append(CarOnGround).append(" \r\n");
        out.append("SlotWin ").append(SlotWin).append(" \r\n");
        out.append("CarVar6 ").append(CarVar6).append(" \r\n");
        out.append("isSwamp ").append(isSwamp).append(" \r\n");
        out.append("CarVar1 ").append(CarVar1).append(" \r\n");
        out.append("isSea ").append(isSea).append(" \r\n");
        out.append("field_601 ").append(field_601).append(" \r\n");
        out.append("chiken_phase ").append(chiken_phase).append(" \r\n");
        out.append("chiken_pic ").append(chiken_pic).append(" \r\n");
        out.append("field_607 ").append(field_607).append(" \r\n");
        out.append("MamaEnd ").append(MamaEnd).append(" \r\n");
        out.append("fogtype ").append(fogtype).append(" \r\n");
        out.append("TiltStatus ").append(TiltStatus).append(" \r\n");
        out.append("CarVar2 ").append(CarVar2).append(" \r\n");
        out.append("VBumpTarget ").append(VBumpTarget).append(" \r\n");
        out.append("VBumpNow ").append(VBumpNow).append(" \r\n");
        out.append("CarVar3 ").append(CarVar3).append(" \r\n");
        out.append("TurbCount ").append(TurbCount).append(" \r\n");
        out.append("CarVar5 ").append(CarVar5).append(" \r\n");
        out.append("CarVar4 ").append(CarVar4).append(" \r\n");
        out.append("NotOnWater ").append(NotOnWater).append(" \r\n");
        out.append("SeaSick ").append(SeaSick).append(" \r\n");
        out.append("DrugMode ").append(DrugMode).append(" \r\n");
        out.append("drug_type ").append(drug_type).append(" \r\n");
        out.append("drug_intensive ").append(drug_intensive).append(" \r\n");
        out.append("drug_timer ").append(drug_timer).append(" \r\n");
        out.append("drug_aspect ").append(drug_aspect).append(" \r\n");

        return out.toString();
    }
}
