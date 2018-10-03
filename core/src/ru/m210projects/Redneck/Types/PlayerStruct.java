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

import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;
import static ru.m210projects.Redneck.SoundDefs.DUKE_SCREAM;
import static ru.m210projects.Redneck.Sounds.spritesound;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Types.LittleEndian;

public class PlayerStruct {
	
	public static final int sizeof = 1756;
	
	public int zoom,exitx,exity,loogiex[] = new int[64],loogiey[] = new int[64],numloogs,loogcnt;
	public int posx, posy, posz, ohorizoff, invdisptime;
	public float horiz, ohoriz, ang, oang, angvel;
	public int bobposx,bobposy,oposx,oposy,oposz,pyoff,opyoff;
	public int posxv,posyv,poszv,last_pissed_time,truefz,truecz;
	public int player_par,visibility;
	public int bobcounter,weapon_sway;
	public int pals_time,randomflamex,crack_time;

	public int aim_mode, auto_aim;

	public short cursectnum,look_ang,last_extra,subweapon;
	public short wackedbyactor, frag, fraggedself;
	public int ammo_amount[] = new int[MAX_WEAPONSRA];
			
	public short curr_weapon, last_weapon, tipincs, horizoff, wantweaponfire;
	public short beer_amount,newowner,hurt_delay,hbomb_hold_delay;
	public short jumping_counter,airleft,knee_incs,access_incs;
	public short fta,ftq,access_wallnum,access_spritenum;
	public short kickback_pic,weapon_ang,whishkey_amount;
	public short somethingonplayer,on_crane,i,one_parallax_sectnum;
	public short over_shoulder_on,random_club_frame,fist_incs;
	public short one_eighty_count,cheat_phase;
	public short dummyplayersprite,extra_extra8,quick_kick;
	public short yeehaa_amount,actorsqu,timebeforeexit,customexitsound;

	public short weaprecs[] = new short[16],weapreccnt,interface_toggle_flag;

	public short rotscrnang,orotscrnang, dead_flag,show_empty_weapon;
	public short snorkle_amount,cowpie_amount,moonshine_amount,shield_amount;
	public short holoduke_on,pycount,weapon_pos,frag_ps;
	public short transporter_hold,last_full_weapon,footprintshade,boot_amount;

	public Source scream_voice;

	public short on_warping_sector,footprintcount;
	public short hbomb_on,jumping_toggle,rapid_fire_hold;
	public boolean on_ground;
	public String name;
	public int inven_icon, buttonpalette, jetpack_on;

	public short spritebridge,lastrandomspot;
	public short scuba_on,footprintpal,heat_on;

	public short  holster_weapon,falling_counter;
	public boolean  gotweapon[] = new boolean[MAX_WEAPONSRA],refresh_inventory;
	public byte[] palette;

	public short toggle_key_flag,knuckle_incs;
	public short walking_snd_toggle, palookup, hard_landing;
	public short max_secret_rooms,secret_rooms, pals[] = new short[3];
	public short max_actors_killed,actors_killed,return_to_center;
	
	public byte last_used_weapon;
	public byte crouch_toggle;

	public int field_280;  //XXX not used
	public short field_284;  //XXX not used
	public int field_X;
	public int field_Y;
	public short field_28E;
	public int field_290;
	
	public short gotkey[] = new short[5];
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
	public short Motospeed;
	public int SlotWin;
	public int field_5FD;
	public int field_5B9;
	public int field_5DD;
	public int field_601;
	public short field_607;
	public short field_609;
	public int fogtype;

	public void copy(PlayerStruct src)
	{
		this.zoom = src.zoom;
		this.exitx = src.exitx;
		this.exity = src.exity;
		System.arraycopy(src.loogiex, 0, this.loogiex, 0, 64);
		System.arraycopy(src.loogiey, 0, this.loogiey, 0, 64);
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
		this.visibility = src.visibility;
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
		this.fta = src.fta;
		this.ftq = src.ftq;
		this.access_wallnum = src.access_wallnum;
		this.access_spritenum = src.access_spritenum;
		this.kickback_pic = src.kickback_pic;
		this.weapon_ang = src.weapon_ang;
		this.whishkey_amount = src.whishkey_amount;
		this.somethingonplayer = src.somethingonplayer;
		this.on_crane = src.on_crane;
		this.i = src.i;
		this.one_parallax_sectnum = src.one_parallax_sectnum;
		this.over_shoulder_on = src.over_shoulder_on;
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
		this.name = src.name;
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
		this.field_284 = src.field_284;
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
	}
	private byte[] buf = new byte[sizeof];
	public byte[] getBytes() {
		int ptr = 0;
		LittleEndian.putInt(buf, ptr, zoom); ptr+=4;
		LittleEndian.putInt(buf, ptr, exitx); ptr+=4;
		LittleEndian.putInt(buf, ptr, exity); ptr+=4;
		
		for(int i = 0; i < 64; i++)
		{
			LittleEndian.putInt(buf, ptr, loogiex[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, loogiey[i]); ptr+=4;
		}
		
		LittleEndian.putInt(buf, ptr, numloogs); ptr+=4;
		LittleEndian.putInt(buf, ptr, loogcnt); ptr+=4;
		LittleEndian.putInt(buf, ptr, posx); ptr+=4;
		LittleEndian.putInt(buf, ptr, posy); ptr+=4;
		LittleEndian.putInt(buf, ptr, posz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, horiz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, ohoriz); ptr+=4;
		LittleEndian.putInt(buf, ptr, ohorizoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, invdisptime); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobposx); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobposy); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposx); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposy); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposz); ptr+=4;
		LittleEndian.putInt(buf, ptr, pyoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, opyoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, posxv); ptr+=4;
		LittleEndian.putInt(buf, ptr, posyv); ptr+=4;
		LittleEndian.putInt(buf, ptr, poszv); ptr+=4;
		LittleEndian.putInt(buf, ptr, last_pissed_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, truefz); ptr+=4;
		LittleEndian.putInt(buf, ptr, truecz); ptr+=4;
		LittleEndian.putInt(buf, ptr, player_par); ptr+=4;
		LittleEndian.putInt(buf, ptr, visibility); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobcounter); ptr+=4;
		LittleEndian.putInt(buf, ptr, weapon_sway); ptr+=4;
		LittleEndian.putInt(buf, ptr, pals_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, randomflamex); ptr+=4;
		LittleEndian.putInt(buf, ptr, crack_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, aim_mode); ptr+=4;
		buf[ptr++] = (byte) auto_aim;
		LittleEndian.putFloat(buf, ptr, ang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, oang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, angvel); ptr+=4;
		LittleEndian.putShort(buf, ptr, cursectnum); ptr+=2;
		LittleEndian.putShort(buf, ptr, look_ang); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_extra); ptr+=2;
		LittleEndian.putShort(buf, ptr, subweapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, wackedbyactor); ptr+=2;
		LittleEndian.putShort(buf, ptr, frag); ptr+=2;
		LittleEndian.putShort(buf, ptr, fraggedself); ptr+=2;
		for(int i = 0; i < MAX_WEAPONSRA; i++)
		{
			LittleEndian.putShort(buf, ptr, (short)ammo_amount[i]); ptr+=2;
		}
		LittleEndian.putShort(buf, ptr, curr_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_weapon); ptr+=2;	
		LittleEndian.putShort(buf, ptr, tipincs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, horizoff); ptr+=2;	
		LittleEndian.putShort(buf, ptr, wantweaponfire); ptr+=2;	
		LittleEndian.putShort(buf, ptr, beer_amount); ptr+=2;	
		LittleEndian.putShort(buf, ptr, newowner); ptr+=2;	
		LittleEndian.putShort(buf, ptr, hurt_delay); ptr+=2;	
		LittleEndian.putShort(buf, ptr, hbomb_hold_delay); ptr+=2;	
		LittleEndian.putShort(buf, ptr, jumping_counter); ptr+=2;	
		LittleEndian.putShort(buf, ptr, airleft); ptr+=2;	
		LittleEndian.putShort(buf, ptr, knee_incs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_incs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, fta); ptr+=2;	
		LittleEndian.putShort(buf, ptr, ftq); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_wallnum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_spritenum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, kickback_pic); ptr+=2;		
		LittleEndian.putShort(buf, ptr, weapon_ang); ptr+=2;	
		LittleEndian.putShort(buf, ptr, whishkey_amount); ptr+=2;	
		LittleEndian.putShort(buf, ptr, somethingonplayer); ptr+=2;	
		LittleEndian.putShort(buf, ptr, on_crane); ptr+=2;	
		LittleEndian.putShort(buf, ptr, i); ptr+=2;	
		LittleEndian.putShort(buf, ptr, one_parallax_sectnum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, over_shoulder_on); ptr+=2;
		LittleEndian.putShort(buf, ptr, random_club_frame); ptr+=2;
		LittleEndian.putShort(buf, ptr, fist_incs); ptr+=2;
		LittleEndian.putShort(buf, ptr, one_eighty_count); ptr+=2;
		LittleEndian.putShort(buf, ptr, cheat_phase); ptr+=2;
		LittleEndian.putShort(buf, ptr, dummyplayersprite); ptr+=2;
		LittleEndian.putShort(buf, ptr, extra_extra8); ptr+=2;
		LittleEndian.putShort(buf, ptr, quick_kick); ptr+=2;
		LittleEndian.putShort(buf, ptr, yeehaa_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, actorsqu); ptr+=2;
		LittleEndian.putShort(buf, ptr, timebeforeexit); ptr+=2;
		LittleEndian.putShort(buf, ptr, customexitsound); ptr+=2;
		for(int i = 0; i < 16; i++)
		{
			LittleEndian.putShort(buf, ptr, (short)weaprecs[i]); ptr+=2;
		}
		LittleEndian.putShort(buf, ptr, weapreccnt); ptr+=2;
		LittleEndian.putShort(buf, ptr, interface_toggle_flag); ptr+=2;
		LittleEndian.putShort(buf, ptr, rotscrnang); ptr+=2;
		LittleEndian.putShort(buf, ptr, dead_flag); ptr+=2;
		LittleEndian.putShort(buf, ptr, show_empty_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, snorkle_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, cowpie_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, moonshine_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, shield_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, holoduke_on); ptr+=2;
		LittleEndian.putShort(buf, ptr, pycount); ptr+=2;
		LittleEndian.putShort(buf, ptr, weapon_pos); ptr+=2;
		LittleEndian.putShort(buf, ptr, frag_ps); ptr+=2;
		LittleEndian.putShort(buf, ptr, transporter_hold); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_full_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, footprintshade); ptr+=2;
		LittleEndian.putShort(buf, ptr, boot_amount); ptr+=2;
		buf[ptr++] = scream_voice != null?(byte)1:0;
		buf[ptr++] = (byte)on_warping_sector;
		buf[ptr++] = (byte)footprintcount;
		buf[ptr++] = (byte)hbomb_on;
		buf[ptr++] = (byte)jumping_toggle;
		buf[ptr++] = (byte)rapid_fire_hold;
		buf[ptr++] = on_ground?(byte)1:0;
		Arrays.fill(buf, ptr, ptr+32, (byte)0);
		if(name != null)
			System.arraycopy(name.getBytes(), 0, buf, ptr, name.length()); 
		ptr += 32;
		buf[ptr++] = (byte)inven_icon;		
		buf[ptr++] = (byte)buttonpalette;		
		buf[ptr++] = (byte)jetpack_on;		
		buf[ptr++] = (byte)spritebridge;	
		buf[ptr++] = (byte)lastrandomspot;	
		buf[ptr++] = (byte)scuba_on;	
		buf[ptr++] = (byte)footprintpal;	
		buf[ptr++] = (byte)heat_on;	
		buf[ptr++] = (byte)holster_weapon;	
		buf[ptr++] = (byte)falling_counter;	
		for(int i = 0; i < MAX_WEAPONSRA; i++)
			buf[ptr++] = gotweapon[i]?(byte)1:0;
		buf[ptr++] = refresh_inventory?(byte)1:0;
		System.arraycopy(palette, 0, buf, ptr, 768); ptr += 768;
		buf[ptr++] = (byte)toggle_key_flag;	
		buf[ptr++] = (byte)knuckle_incs;	
		buf[ptr++] = (byte)walking_snd_toggle;	
		buf[ptr++] = (byte)palookup;	
		buf[ptr++] = (byte)hard_landing;
		LittleEndian.putShort(buf, ptr, max_secret_rooms); ptr+=2;
		LittleEndian.putShort(buf, ptr, secret_rooms); ptr+=2;
		for(int i = 0; i < 3; i++) 
			buf[ptr++] = (byte)pals[i];
		LittleEndian.putShort(buf, ptr, max_actors_killed); ptr+=2;
		LittleEndian.putShort(buf, ptr, actors_killed); ptr+=2;
		buf[ptr++] = (byte)return_to_center;	
		
		buf[ptr++] = last_used_weapon;
		buf[ptr++] = crouch_toggle;

		LittleEndian.putInt(buf, ptr, field_280); ptr+=4;
		LittleEndian.putShort(buf, ptr, field_284); ptr+=2;
		LittleEndian.putInt(buf, ptr, field_X); ptr+=4;
		LittleEndian.putInt(buf, ptr, field_Y); ptr+=4;
		LittleEndian.putShort(buf, ptr, field_28E); ptr+=2;
		LittleEndian.putInt(buf, ptr, field_290); ptr+=4;
		
		LittleEndian.putShort(buf, ptr, field_57C); ptr+=2;
		LittleEndian.putInt(buf, ptr, detonate_count); ptr+=4;
	
		LittleEndian.putShort(buf, ptr, alcohol_meter); ptr+=2;
		
		LittleEndian.putShort(buf, ptr, gut_meter); ptr+=2;
		LittleEndian.putShort(buf, ptr, alcohol_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, gut_amount); ptr+=2;
		LittleEndian.putInt(buf, ptr, alcohol_count); ptr+=4;
		LittleEndian.putInt(buf, ptr, gut_count); ptr+=4;
		
		for(int i = 0; i < 5; i++)
		{
			LittleEndian.putShort(buf, ptr, gotkey[i]); 
			ptr+=2;
		}

		buf[ptr++] = drunk;
		buf[ptr++] = shotgunstatus;
		buf[ptr++] = shotgun_splitshot;

		LittleEndian.putShort(buf, ptr, kickback); ptr+=2;
		LittleEndian.putShort(buf, ptr, field_count); ptr+=2;

		return buf;
	}
	
	public void set(ByteBuffer bb)
	{
		zoom = bb.getInt();
		exitx = bb.getInt();
		exity = bb.getInt();

		for(int i = 0; i < 64; i++)
		{
			loogiex[i] = bb.getInt();
			loogiey[i] = bb.getInt();
		}
		
		numloogs = bb.getInt();
		loogcnt = bb.getInt();
		posx = bb.getInt();
		posy = bb.getInt();
		posz = bb.getInt();
		horiz = bb.getFloat();
		ohoriz = bb.getFloat();
		ohorizoff = bb.getInt();
		invdisptime = bb.getInt();
		bobposx = bb.getInt();
		bobposy = bb.getInt();
		oposx = bb.getInt();
		oposy = bb.getInt();
		oposz = bb.getInt();
		pyoff = bb.getInt();
		opyoff = bb.getInt();
		posxv = bb.getInt();
		posyv = bb.getInt();
		poszv = bb.getInt();
		last_pissed_time = bb.getInt();
		truefz = bb.getInt();
		truecz = bb.getInt();
		player_par = bb.getInt();
		visibility = bb.getInt();
		bobcounter = bb.getInt();
		weapon_sway = bb.getInt();
		pals_time = bb.getInt();
		randomflamex = bb.getInt();
		crack_time = bb.getInt();
		aim_mode = bb.getInt();
		auto_aim = bb.get() & 0xFF;
		ang = bb.getFloat();
		oang = bb.getFloat();
		angvel = bb.getFloat();
		cursectnum = bb.getShort();
		look_ang = bb.getShort();
		last_extra = bb.getShort();
		subweapon = bb.getShort();
		wackedbyactor = bb.getShort();
		frag = bb.getShort();
		fraggedself = bb.getShort();
		for(int i = 0; i < MAX_WEAPONSRA; i++)
			ammo_amount[i] = bb.getShort();
		curr_weapon = bb.getShort();
		
		
		last_weapon = bb.getShort();	
		tipincs = bb.getShort();	
		horizoff = bb.getShort();	
		wantweaponfire = bb.getShort();	
		beer_amount = bb.getShort();	
		newowner = bb.getShort();	
		hurt_delay = bb.getShort();	
		hbomb_hold_delay = bb.getShort();	
		jumping_counter = bb.getShort();	
		airleft = bb.getShort();	
		knee_incs = bb.getShort();	
		access_incs = bb.getShort();	
		fta = bb.getShort();	
		ftq = bb.getShort();	
		access_wallnum = bb.getShort();	
		access_spritenum = bb.getShort();	
		kickback_pic = bb.getShort();	
		weapon_ang = bb.getShort();	
		whishkey_amount = bb.getShort();	
		somethingonplayer = bb.getShort();	
		on_crane = bb.getShort();	
		i = bb.getShort();	
		one_parallax_sectnum = bb.getShort();	
		over_shoulder_on = bb.getShort();
		random_club_frame = bb.getShort();
		fist_incs = bb.getShort();
		one_eighty_count = bb.getShort();
		cheat_phase = bb.getShort();
		dummyplayersprite = bb.getShort();
		extra_extra8 = bb.getShort();
		quick_kick = bb.getShort();
		yeehaa_amount = bb.getShort();
		actorsqu = bb.getShort();
		timebeforeexit = bb.getShort();
		customexitsound = bb.getShort();
		for(int i = 0; i < 16; i++)
			weaprecs[i] = bb.getShort();
		
		weapreccnt = bb.getShort();
		interface_toggle_flag = bb.getShort();
		rotscrnang = bb.getShort();
		dead_flag = bb.getShort();
		show_empty_weapon = bb.getShort();
		snorkle_amount = bb.getShort();
		cowpie_amount = bb.getShort();
		moonshine_amount = bb.getShort();
		shield_amount = bb.getShort();
		holoduke_on = bb.getShort();
		pycount = bb.getShort();
		weapon_pos = bb.getShort();
		frag_ps = bb.getShort();
		transporter_hold = bb.getShort();
		last_full_weapon = bb.getShort();
		footprintshade = bb.getShort();
		boot_amount = bb.getShort();
		boolean svoice = bb.get() == 1;
		if(svoice) 
			scream_voice = spritesound(DUKE_SCREAM, i);
		on_warping_sector = bb.get();
		footprintcount = bb.get();
		hbomb_on = bb.get();
		jumping_toggle = bb.get();
		rapid_fire_hold = bb.get();
		on_ground = bb.get() == 1;
		
		byte[] namebuf = new byte[32];
		bb.get(namebuf);
		name = new String(namebuf).trim();
		inven_icon  = bb.get();	
		buttonpalette  = bb.get();		
		jetpack_on  = bb.get();	
		spritebridge = bb.get();	
		lastrandomspot = bb.get();
		scuba_on = bb.get();
		footprintpal = bb.get();	
		heat_on = bb.get();	
		holster_weapon = bb.get();
		falling_counter = bb.get();	
		for(int i = 0; i < MAX_WEAPONSRA; i++)
			gotweapon[i] = bb.get() == 1;
		refresh_inventory = bb.get() == 1;
		this.palette = new byte[768];
		bb.get(this.palette);
		toggle_key_flag = bb.get();	
		knuckle_incs = bb.get();
		walking_snd_toggle = bb.get();
		palookup = bb.get();
		hard_landing = bb.get();	
		max_secret_rooms = bb.getShort();
		secret_rooms = bb.getShort();
		for(int i = 0; i < 3; i++) 
			pals[i] = bb.get();
		max_actors_killed = bb.getShort();
		actors_killed = bb.getShort();
		return_to_center = bb.get();
		
		last_used_weapon = bb.get(); 
		crouch_toggle = bb.get();
		
		field_280 = bb.getInt();
		field_284 = bb.getShort();
		field_X = bb.getInt();
		field_Y = bb.getInt();
		field_28E = bb.getShort();
		field_290 = bb.getInt();

		field_57C = bb.getShort(); //detonate ticks
		detonate_count = bb.getInt();

		alcohol_meter = bb.getShort();
		gut_meter = bb.getShort();
		alcohol_amount = bb.getShort();
		gut_amount = bb.getShort();
		alcohol_count = bb.getInt();
		gut_count = bb.getInt();
		
		for(int i = 0; i < 5; i++)
			gotkey[i] = bb.getShort();
		
		drunk = bb.get(); 
		shotgunstatus = bb.get(); 
		shotgun_splitshot = bb.get(); 

		kickback = bb.getShort(); //weapon horiz
		field_count = bb.getShort();
	}
}
