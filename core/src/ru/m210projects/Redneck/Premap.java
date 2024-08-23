//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.Sector;
import ru.m210projects.Build.Types.Sprite;
import ru.m210projects.Build.Types.Wall;
import ru.m210projects.Build.Types.collections.BitMap;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.exceptions.WarningException;
import ru.m210projects.Build.filehandle.Entry;
import ru.m210projects.Build.filehandle.StreamUtils;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Types.PlayerStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.net.Mmulti.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Animate.gAnimationCount;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Player.checkavailweapon;
import static ru.m210projects.Redneck.Player.setpal;
import static ru.m210projects.Redneck.ResourceHandler.InitSpecialTextures;
import static ru.m210projects.Redneck.Sounds.getSampleEntry;
import static ru.m210projects.Redneck.Sounds.loadSample;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.View.gNameShowTime;

public class Premap {

    public static final int MAXJAILDOORS = 32;
    public static final int MAXMINECARDS = 16;
    public static final int MAXTORCHES = 64;
    public static final int MAXLIGHTNINS = 64;
    public static final int MAXAMBIENTS = 64;
    public static final int MAXGEOMETRY = 64;
    public static final short[] lotags = new short[65];
    public static final short[] rorsector = new short[16];
    public static final byte[] rortype = new byte[16];
    public static final BitMap shadeEffect = new BitMap(MAXSECTORSV7);
    public static final int[] jailspeed = new int[MAXJAILDOORS];
    public static final int[] jaildistance = new int[MAXJAILDOORS];
    public static final short[] jailsect = new short[MAXJAILDOORS];
    public static final short[] jaildirection = new short[MAXJAILDOORS];
    public static final short[] jailunique = new short[MAXJAILDOORS];
    public static final short[] jailsound = new short[MAXJAILDOORS];
    public static final short[] jailstatus = new short[MAXJAILDOORS];
    public static final int[] jailcount2 = new int[MAXJAILDOORS];
    public static final int[] minespeed = new int[MAXMINECARDS];
    public static final int[] minefulldist = new int[MAXMINECARDS];
    public static final int[] minedistance = new int[MAXMINECARDS];
    public static final short[] minechild = new short[MAXMINECARDS];
    public static final short[] mineparent = new short[MAXMINECARDS];
    public static final short[] minedirection = new short[MAXMINECARDS];
    public static final short[] minesound = new short[MAXMINECARDS];
    public static final short[] minestatus = new short[MAXMINECARDS];
    public static final short[] torchsector = new short[MAXTORCHES];
    public static final byte[] torchshade = new byte[MAXTORCHES];
    public static final short[] torchflags = new short[MAXTORCHES];
    public static final short[] lightninsector = new short[MAXLIGHTNINS];
    public static final short[] lightninshade = new short[MAXLIGHTNINS];
    public static final short[] ambienttype = new short[MAXAMBIENTS];
    public static final short[] ambientid = new short[MAXAMBIENTS];
    public static final short[] ambienthitag = new short[MAXAMBIENTS];
    public static final short[] geomsector = new short[MAXGEOMETRY];
    public static final short[] geoms1 = new short[MAXGEOMETRY];
    public static final int[] geomx1 = new int[MAXGEOMETRY];
    public static final int[] geomy1 = new int[MAXGEOMETRY];
    public static final int[] geomz1 = new int[MAXGEOMETRY];
    public static final short[] geoms2 = new short[MAXGEOMETRY];
    public static final int[] geomx2 = new int[MAXGEOMETRY];
    public static final int[] geomy2 = new int[MAXGEOMETRY];
    public static final int[] geomz2 = new int[MAXGEOMETRY];
    public static final byte[] packbuf = new byte[576];
    private static final PlayerInfo[] info = new PlayerInfo[MAXPLAYERS];
    public static int rorcnt;
    public static boolean plantProcess = false;
    public static int numlightnineffects, numtorcheffects, numgeomeffects, numjaildoors, numminecart, numambients;
    public static short which_palookup = 9;

    public static void getsound(int num) {
        if (cfg.isNoSound()) {
            return;
        }

        Entry entry = getSampleEntry(num);
        if (!entry.exists()) {
            return;
        }

        soundsiz[num] = (int) entry.getSize();
        // if( (ud.level_number == 0 && ud.volume_number == 0 && (num == 189 || num ==
        // 232 || num == 99 || num == 233 || num == 17 ) ) || ( l < 12288 ) )
        {
            Sound[num].lock = 2;
            loadSample(entry, num);
        }
    }

    public static void pickrandomspot(int snum) {

        PlayerStruct p = ps[snum];

        int i;
        if (ud.multimode > 1 && ud.coop != 1) {
            i = engine.krand() % numplayersprites;
        } else {
            i = snum;
        }

        p.bobposx = p.oposx = p.posx = po[i].ox;
        p.bobposy = p.oposy = p.posy = po[i].oy;
        p.oposz = p.posz = po[i].oz;
        p.ang = po[i].oa;
        p.cursectnum = po[i].os;
    }

    public static void resetplayerstats(int snum) {
        PlayerStruct p = ps[snum];

        ud.showallmap = 0;
        p.dead_flag = 0;
        p.wackedbyactor = -1;
        p.falling_counter = 0;
        p.quick_kick = 0;
        p.subweapon = 0;
        p.last_full_weapon = 0;
        p.tipincs = 0;
        p.buttonpalette = 0;
        p.actorsqu = -1;
        p.invdisptime = 0;
        p.refresh_inventory = false;
        p.last_pissed_time = 0;
        p.holster_weapon = 0;
        p.pycount = 0;
        p.pyoff = 0;
        p.opyoff = 0;
        p.loogcnt = 0;
        p.angvel = 0;
        p.weapon_sway = 0;
        p.extra_extra8 = 0;
        p.show_empty_weapon = 0;
        p.dummyplayersprite = -1;
        p.crack_time = 0;
        p.hbomb_hold_delay = 0;
        p.transporter_hold = 0;
        p.wantweaponfire = -1;
        p.hurt_delay = 0;
        p.footprintcount = 0;
        p.footprintpal = 0;
        p.footprintshade = 0;
        p.jumping_toggle = 0;
        p.ohoriz = p.horiz = 140;
        p.horizoff = 0;
        p.bobcounter = 0;
        p.on_ground = false;
        p.player_par = 0;
        p.return_to_center = 9;
        p.airleft = 15 * 26;
        p.rapid_fire_hold = 0;
        p.toggle_key_flag = 0;
        p.access_spritenum = -1;
        p.random_club_frame = 0;
        p.on_warping_sector = 0;
        p.spritebridge = 0;
        p.palette = engine.getPaletteManager().getBasePalette();

        if (p.moonshine_amount < 400) {
            p.moonshine_amount = 0;
            p.inven_icon = 0;
        }
        p.heat_on = 0;
        p.holoduke_on = -1;

        p.look_ang = (short) (512 - ((ud.level_number & 1) << 10));

        p.rotscrnang = 0;
        p.newowner = -1;
        p.jumping_counter = 0;
        p.hard_landing = 0;
        p.posxv = 0;
        p.posyv = 0;
        p.poszv = 0;
        fricxv = 0;
        fricyv = 0;
        p.somethingonplayer = -1;
        p.one_eighty_count = 0;
        p.cheat_phase = 0;

        p.on_crane = -1;

        if (p.curr_weapon == PISTOL_WEAPON) {
            p.kickback_pic = 22;
        } else {
            p.kickback_pic = 0;
        }

        p.weapon_pos = 6;
        p.walking_snd_toggle = 0;
        p.weapon_ang = 0;

        p.knuckle_incs = 1;
        p.fist_incs = 0;
        p.knee_incs = 0;
        p.jetpack_on = 0;
        setpal(p);

        p.field_280 = 0;
        p.field_X = 0;
        p.field_Y = 0;
        p.field_28E = 0;
        p.field_290 = 0;
        if (ud.multimode <= 1 || ud.coop == 1) {
            p.gotkey[0] = 0;
            p.gotkey[1] = 0;
            p.gotkey[2] = 0;
            p.gotkey[3] = 0;
            p.gotkey[4] = 0;
        } else {
            p.gotkey[0] = 1;
            p.gotkey[1] = 1;
            p.gotkey[2] = 1;
            p.gotkey[3] = 1;
            p.gotkey[4] = 1;
        }
        p.alcohol_meter = 1647;
        p.gut_meter = 1647;
        p.alcohol_amount = 0;
        p.gut_amount = 0;
        p.alcohol_count = 4096;
        p.gut_count = 4096;
        p.drunk = 0;
        p.shotgunstatus = 0;
        p.shotgun_splitshot = 0;
        p.field_57C = 0;
        p.kickback = 0;
        p.field_count = 0;
        LeonardCrack = 0;
        p.detonate_count = 0;

        // RA
        p.chiken_phase = 0;
        p.chiken_pic = 0;
        if (p.OnMotorcycle) {
            p.OnMotorcycle = false;
            p.gotweapon[MOTO_WEAPON] = false;
            p.curr_weapon = RATE_WEAPON;
            p.kickback_pic = 0;
            checkavailweapon(p);
        }

        p.CarVar6 = 0;
        p.CarOnGround = true;
        p.CarVar1 = 0;
        p.CarSpeed = 0;
        p.TiltStatus = 0;
        p.CarVar2 = 0;
        p.VBumpTarget = 0;
        p.VBumpNow = 0;
        p.CarVar3 = 0;
        p.TurbCount = 0;
        p.CarVar5 = 0;
        p.CarVar4 = 0;
        if (p.OnBoat) {
            p.OnBoat = false;
            p.gotweapon[BOAT_WEAPON] = false;
            p.curr_weapon = RATE_WEAPON;
            p.kickback_pic = 0;
            checkavailweapon(p);
        }
        p.NotOnWater = 0;
        p.SeaSick = 0;
        p.DrugMode = 0;
        p.drug_type = 0;
        p.drug_intensive = 0;
        p.drug_timer = 0;
        p.drug_aspect = 0;

        // last_extra check
        p.numloogs = 0; // GDX 31.10.2018 XXX
        p.truefz = 0;
        p.truecz = 0;
        p.randomflamex = 0;
        p.access_incs = 0;
        p.access_wallnum = 0;
        p.interface_toggle_flag = 0;
        p.scream_voice = null;
        p.crouch_toggle = 0;
        p.exitx = 0;
        p.exity = 0;
        p.last_used_weapon = 0;
        p.ohorizoff = 0;
    }

    public static void resetweapons(int snum) {
        int weapon;
        PlayerStruct p = ps[snum];

        for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONSRA; weapon++) {
            p.gotweapon[weapon] = false;
        }
        for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONSRA; weapon++) {
            p.ammo_amount[weapon] = 0;
        }

        Arrays.fill(p.weaprecs, (short) 0);

        p.weapon_pos = 6;
        p.kickback_pic = 5;
        p.curr_weapon = PISTOL_WEAPON;
        p.gotweapon[PISTOL_WEAPON] = true;
        p.gotweapon[KNEE_WEAPON] = true;
        p.ammo_amount[PISTOL_WEAPON] = 48;
        p.gotweapon[HANDREMOTE_WEAPON] = true;
        if (currentGame.getCON().type == RRRA) {
            p.gotweapon[RATE_WEAPON] = true;
        }
        p.last_weapon = -1;

        p.show_empty_weapon = 0;
        p.last_pissed_time = 0;
        p.holster_weapon = 0;

        p.OnMotorcycle = false;
        p.OnBoat = false;
        p.CarVar1 = 0;
    }

    public static void resetinventory(int snum) {
        PlayerStruct p = ps[snum];

        p.inven_icon = 0;
        p.boot_amount = 0;
        p.scuba_on = 0;
        p.snorkle_amount = 0;
        p.yeehaa_amount = 0;
        p.heat_on = 0;
        p.jetpack_on = 0;
        p.cowpie_amount = 0;
        p.shield_amount = (short) currentGame.getCON().max_armour_amount;
        p.holoduke_on = -1;
        p.beer_amount = 0;
        p.whishkey_amount = 0;
        p.moonshine_amount = 0;
        p.inven_icon = 0;

        if (ud.multimode <= 1 || ud.coop == 1) //v0.751
        {
            p.gotkey[0] = 0;
            p.gotkey[1] = 0;
            p.gotkey[2] = 0;
            p.gotkey[3] = 0;
            p.gotkey[4] = 0;
        } else {
            p.gotkey[0] = 1;
            p.gotkey[1] = 1;
            p.gotkey[2] = 1;
            p.gotkey[3] = 1;
            p.gotkey[4] = 1;
        }

        p.alcohol_meter = 1647;
        p.gut_meter = 1647;
        p.alcohol_amount = 0;
        p.gut_amount = 0;
        p.alcohol_count = 0;
        p.gut_count = 0;
        p.drunk = 0;
        p.shotgunstatus = 0;
        p.shotgun_splitshot = 0;
        p.field_57C = 0;
        p.detonate_count = 0;
        p.kickback = 0;
        p.field_count = 0;
    }

    public static void resetprestat(int snum) {
        PlayerStruct p = ps[snum];

        spriteqloc = 0;
        for (int i = 0; i < currentGame.getCON().spriteqamount; i++) {
            spriteq[i] = -1;
        }

        p.hbomb_on = 0;
        p.cheat_phase = 0;
        p.pals_time = 0;
        p.toggle_key_flag = 0;
        p.secret_rooms = 0;
        p.max_secret_rooms = 0;
        p.actors_killed = 0;
        p.max_actors_killed = 0;
        p.lastrandomspot = 0;
        p.weapon_pos = 6;
        p.kickback_pic = 5;
        p.last_weapon = -1;
        p.weapreccnt = 0;
        p.show_empty_weapon = 0;
        p.holster_weapon = 0;
        p.last_pissed_time = 0;

        p.one_parallax_sectnum = -1;

        screenpeek = myconnectindex;
        numanimwalls = 0;
        numcyclers = 0;
        gAnimationCount = 0;
        parallaxtype = 0;
        engine.srand(17);
        game.gPaused = false;
        ud.camerasprite = -1;
        ud.eog = 0;
        tempwallptr = 0;
        camsprite = -1;
        earthquaketime = 0;
        WindTime = 0;
        WindDir = 0;
        fakebubba_spawn = 0;
        BellTime = 0;

        if (((uGameFlags & MODE_EOL) != MODE_EOL && numplayers < 2) || (ud.coop != 1 && numplayers > 1)) {
            resetweapons(snum);
            resetinventory(snum);
        } else if (p.curr_weapon == HANDREMOTE_WEAPON) {
            p.ammo_amount[4]++;
            p.curr_weapon = 4;
        }

        p.timebeforeexit = 0;
        p.customexitsound = 0;

        Arrays.fill(p.pals, (short) 0);

        p.field_280 = 0;
        p.field_X = 0x20000;
        p.field_Y = 0x20000;
        p.field_28E = 0;
        p.field_290 = 0;

        if (ud.multimode <= 1 || ud.coop == 1) //v0.751
        {
            p.gotkey[0] = 0;
            p.gotkey[1] = 0;
            p.gotkey[2] = 0;
            p.gotkey[3] = 0;
            p.gotkey[4] = 0;
        } else {
            p.gotkey[0] = 1;
            p.gotkey[1] = 1;
            p.gotkey[2] = 1;
            p.gotkey[3] = 1;
            p.gotkey[4] = 1;
        }

        p.alcohol_meter = 1647;
        p.gut_meter = 1647;
        p.alcohol_amount = 0;
        p.gut_amount = 0;
        p.alcohol_count = 0;
        p.gut_count = 0;
        p.drunk = 0;
        p.shotgunstatus = 0;
        p.shotgun_splitshot = 0;
        p.field_57C = 0;
        p.detonate_count = 0;
        p.kickback = 0;
        p.field_count = 0;

        if (numplayers >= 2) {
            UFO_SpawnCount = 32;
            UFO_SpawnTime = 0;
            UFO_SpawnHulk = 2;
        } else {
            UFO_SpawnCount = (ud.player_skill << 2) + 1;
            if (UFO_SpawnCount > 32) {
                UFO_SpawnCount = 32;
            }
            UFO_SpawnTime = 0;
            UFO_SpawnHulk = ud.player_skill + 1;
        }
    }

    public static void setupbackdrop(short sky) {
        Arrays.fill(pskyoff, (short) 0);

        Renderer renderer = game.getRenderer();
        renderer.setParallaxScale(32768);

        switch (sky) {
            case 1022:
                pskyoff[6] = 1;
                pskyoff[1] = 2;
                pskyoff[4] = 2;
                pskyoff[2] = 3;
                break;
            case 1026:
                pskyoff[5] = 1;
                pskyoff[6] = 2;
                pskyoff[7] = 3;
                pskyoff[2] = 4;
                break;
            case 1031:
                renderer.setParallaxScale(16384 + 1024);
                pskyoff[0] = 1;
                pskyoff[1] = 2;
                pskyoff[2] = 1;
                pskyoff[3] = 3;
                pskyoff[4] = 4;
                pskyoff[5] = 0;
                pskyoff[6] = 2;
                pskyoff[7] = 3;
                break;
        }

        Arrays.fill(zeropskyoff, (short) 0);
        System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

        pskybits = 2;
    }

    public static void prelevel() {
        Arrays.fill(lotags, (short) 0);

        show2dsector.clear();
        show2dwall.clear();
        show2dsprite.clear();

        shadeEffect.clear(); // #GDX Arrays.fill(shadeEffect, false);
        Arrays.fill(geoms1, (short) -1);
        Arrays.fill(geoms2, (short) -1);
        Arrays.fill(ambienttype, (short) -1);
        Arrays.fill(ambientid, (short) -1);
        Arrays.fill(ambienthitag, (short) -1);

        // RA
        ps[0].fogtype = 0;
        engine.getPaletteManager().applyfog(0);
        ps[0].isSea = false;
        ps[0].isSwamp = false;
        ps[0].field_601 = 0;
        ps[0].SlotWin = 0;
        ps[0].field_607 = 0;
        ps[0].MamaEnd = 0;
        BellSound = 0;
        mamaspawn_count = 15;
        if (ud.level_number != 3 || ud.volume_number != 0) {
            if (ud.level_number == 2 && ud.volume_number == 1) {
                mamaspawn_count = 10;
            } else if (ud.level_number == 4 && ud.volume_number == 1) {
                ps[myconnectindex].moonshine_amount = 0;
            }
        } else {
            mamaspawn_count = 5;
        }

        resetprestat(0);
        gVisibility = currentGame.getCON().const_visibility;

        numlightnineffects = 0;
        numtorcheffects = 0;
        numgeomeffects = 0;
        numjaildoors = 0;
        numminecart = 0;
        numambients = 0;
        int haveLigthning = 0;
        plantProcess = false;

        BowlReset();

        fakebubba_spawn = 0;
        mamaspawn_count = 15;
        BellTime = 0;

        List<Sprite> sprites = boardService.getBoard().getSprites();
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sp = sprites.get(i);
            if (sp.getPal() == 100) {
                if (numplayers <= 1) {
                    sp.setPal(0);
                } else {
                    engine.deletesprite(i);
                }
            } else if (sp.getPal() == 101) {
                sp.setExtra(0);
                sp.setHitag(1);
                sp.setPal(0);
                engine.changespritestat(i, (short) 118);
            }
        }

        int distance = 0, speed = 0, sound = 0;
        Sector[] sectors = boardService.getBoard().getSectors();
        for (int i = 0; i < sectors.length; i++) {
            Sector sec = sectors[i];
            sec.setExtra(256);

            if (sec.getCeilingpicnum() == STARSKY2) {
                haveLigthning = 1;
            }

            switch (sec.getLotag()) {
                case 20:
                case 22:
                    if (sec.getFloorz() > sec.getCeilingz()) {
                        sec.setLotag(sec.getLotag() | 32768);
                    }
                    continue;
                case 41: {
                    ListNode<Sprite> node = boardService.getSectNode(i);
                    while (node != null) {
                        int j = node.getIndex();
                        ListNode<Sprite> nexti = node.getNext();
                        Sprite sp = node.get();
                        if (sp.getPicnum() == JAILDOOR) {
                            distance = sp.getLotag() << 4;
                            speed = sp.getHitag();
                            engine.deletesprite(j);
                        }
                        if (sp.getPicnum() == JAILSOUND) {
                            sound = sp.getLotag();
                            engine.deletesprite(j);
                        }
                        node = nexti;
                    }

                    for (int j = 0; j < sectors.length; j++) {
                        Sector sec2 = sectors[j];
                        if (sec.getHitag() == sec2.getHitag() && i != j) {
                            if (numjaildoors > MAXJAILDOORS) {
                                throw new WarningException("Too many jaildoor sectors");
                            }

                            int num = numjaildoors;
                            jailspeed[num] = speed;
                            jaildistance[num] = distance;
                            jailsect[num] = (short) j;
                            jaildirection[num] = sec2.getLotag();
                            jailunique[num] = sec.getHitag();
                            jailsound[num] = (short) sound;
                            jailstatus[num] = 0;
                            jailcount2[num] = 0;

                            numjaildoors++;
                        }
                    }
                    break;
                }
                case 42:
                    ListNode<Sprite> node = boardService.getSectNode(i);
                    while (node != null) {
                        int j = node.getIndex();
                        ListNode<Sprite> nexti = node.getNext();
                        Sprite sp = node.get();
                        if (sp.getPicnum() == 64) {
                            distance = sp.getLotag() << 4;
                            speed = sp.getHitag();

                            for (short k = 0; k < sprites.size(); k++) {
                                Sprite sp2 = sprites.get(k);
                                if (sp2.getPicnum() == 66) {
                                    if (sp2.getLotag() == sp.getSectnum()) {
                                        minechild[numminecart] = sp2.getSectnum();
                                        engine.deletesprite(k);
                                    }
                                }
                            }
                            engine.deletesprite(j);
                        }
                        if (sp.getPicnum() == 65) {
                            sound = sp.getLotag();
                            engine.deletesprite(j);
                        }
                        node = nexti;
                    }

                    if (numminecart > MAXMINECARDS) {
                        throw new WarningException("Too many minecart sectors");
                    }

                    int num = numminecart;
                    minespeed[num] = speed;
                    mineparent[num] = (short) i;
                    minedirection[num] = sec.getHitag();
                    minefulldist[num] = distance;
                    minedistance[num] = distance;
                    minesound[num] = (short) sound;
                    minestatus[num] = 1;
                    numminecart++;
                    break;
            }

            if ((sec.getCeilingstat() & 1) != 0) {
                setupbackdrop(sec.getCeilingpicnum());

                if (ps[0].one_parallax_sectnum == -1) {
                    ps[0].one_parallax_sectnum = (short) i;
                }
            }

            if (sec.getLotag() == 32767) // Found a secret room
            {
                ps[0].max_secret_rooms++;
                continue;
            }

            if (sec.getLotag() == -1) {
                Wall wal = boardService.getWall(sec.getWallptr());
                if (wal != null) {
                    ps[0].exitx = wal.getX();
                    ps[0].exity = wal.getY();
                }
            }
        }

        ListNode<Sprite> node = boardService.getStatNode(0);
        while (node != null) {
            ListNode<Sprite> nexti = node.getNext();
            Sprite sp = node.get();
            int i = node.getIndex();

            if (sp.getLotag() == -1 && (sp.getCstat() & 16) != 0) {
                ps[0].exitx = sp.getX();
                ps[0].exity = sp.getY();
            } else {
                Sector sec = boardService.getSector(sp.getSectnum());
                if (sec != null) {
                    switch (sp.getPicnum()) {
                        case GPSPEED:
                            sec.setExtra(sp.getLotag());
                            engine.deletesprite(i);
                            break;

                        case CYCLER:
                            if (numcyclers >= MAXCYCLERS) {
                                throw new WarningException("\nToo many cycling sectors.");
                            }
                            cyclers[numcyclers][0] = sp.getSectnum();
                            cyclers[numcyclers][1] = sp.getLotag();
                            cyclers[numcyclers][2] = sp.getShade();
                            cyclers[numcyclers][3] = sec.getFloorshade();
                            cyclers[numcyclers][4] = sp.getHitag();
                            cyclers[numcyclers][5] = (short) ((sp.getAng() == 1536) ? 1 : 0);
                            numcyclers++;
                            engine.deletesprite(i);
                            break;
                        case TORCH:
                            if (numtorcheffects >= MAXTORCHES) {
                                throw new WarningException("Too many torch effects.");
                            }

                            int num = numtorcheffects;
                            torchsector[num] = sp.getSectnum();
                            torchshade[num] = sec.getFloorshade();
                            torchflags[num] = sp.getLotag();
                            numtorcheffects++;
                            engine.deletesprite(i);
                            break;

                        case LIGHTNIN:
                            if (numlightnineffects >= MAXLIGHTNINS) {
                                throw new WarningException("Too many lightnin effects.");
                            }

                            int lnum = numlightnineffects;
                            lightninsector[lnum] = sp.getSectnum();
                            lightninshade[lnum] = sp.getLotag();
                            numlightnineffects++;
                            engine.deletesprite(i);
                            break;

                        case MINECARTKILLER:
                            sp.setCstat(sp.getCstat() | 32768);
                            break;

                        case SHADESECTOR:
                            shadeEffect.setBit(sp.getSectnum()); // #GDX  shadeEffect[sp.getSectnum()] = true;
                            engine.deletesprite(i);
                            break;

                        case SOUNDFX:
                            if (numambients >= MAXAMBIENTS) {
                                throw new WarningException("Too many ambient effects.");
                            }

                            int anum = numambients;
                            ambientid[anum] = (short) i;
                            ambienttype[anum] = sp.getLotag();
                            ambienthitag[anum] = sp.getHitag();
                            sp.setAng((short) numambients);
                            sp.setLotag(0);
                            sp.setHitag(0);
                            numambients++;
                            break;

                        case 94:
                            plantProcess = true;
                            break;
                    }
                }
            }
            node = nexti;
        }

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sp = sprites.get(i);
            if (sp.getPicnum() == 19) {
                if (numgeomeffects >= MAXGEOMETRY) {
                    throw new WarningException("Too many geometry effects.");
                }

                if (sp.getHitag() == 0) {
                    geomsector[numgeomeffects] = sp.getSectnum();
                    for (int k = 0; k < sprites.size(); k++) {
                        Sprite sp2 = sprites.get(k);
                        if (sp2.getLotag() == sp.getLotag() && i != k && sp2.getPicnum() == 19) {
                            if (sp2.getHitag() == 1) {
                                geoms1[numgeomeffects] = sp2.getSectnum();
                                geomx1[numgeomeffects] = sp.getX() - sp2.getX();
                                geomy1[numgeomeffects] = sp.getY() - sp2.getY();
                                geomz1[numgeomeffects] = sp.getZ() - sp2.getZ();
                            }
                            if (sp2.getHitag() == 2) {
                                geoms2[numgeomeffects] = sp2.getSectnum();
                                geomx2[numgeomeffects] = sp.getX() - sp2.getX();
                                geomy2[numgeomeffects] = sp.getY() - sp2.getY();
                                geomz2[numgeomeffects] = sp.getZ() - sp2.getZ();
                            }
                        }
                    }
                    numgeomeffects++;
                }
            }
        }

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sp = sprites.get(i);
            if (sp.getStatnum() < MAXSTATUS) {
                if (sp.getPicnum() == SECTOREFFECTOR && sp.getLotag() == 14) {
                    continue;
                }
                spawn(-1, i);
            }
        }

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sp = sprites.get(i);
            if (sp.getStatnum() < MAXSTATUS) {
                if (sp.getPicnum() == SECTOREFFECTOR && sp.getLotag() == 14) {
                    spawn(-1, i);
                }

                if (sp.getPicnum() == 19) {
                    engine.deletesprite(i);
                }

                if (sp.getPicnum() == DOORKEYS) {
                    Sector sec = boardService.getSector(sp.getSectnum());
                    if (sec != null) {
                        sec.setFiller(sp.getLotag());
                    }
                    engine.deletesprite(i);
                }
            }
        }

        int lotaglist = 0;
        for (ListNode<Sprite> n = boardService.getStatNode(0); n != null; n = n.getNext()) {
            Sprite sp = n.get();
            switch (sp.getPicnum()) {
                case 85:
                case 87:
                case 89:
                case 91:
                case 93:
                case 94:
                case 95:
                case 122:
                case 124:
                case LIGHTSWITCH2 + 1:
                case 2223:
                case LOCKSWITCH1 + 1:
                case 2227:
                case 2250:
                case 2255: {
                    int j;
                    for (j = 0; j < lotaglist; j++) {
                        if (sp.getLotag() == lotags[j]) {
                            break;
                        }
                    }

                    if (j == lotaglist) {
                        lotags[lotaglist] = sp.getLotag();
                        lotaglist++;
                        if (lotaglist > 64) {
                            throw new WarningException("\nToo many switches (64 max).");
                        }

                        ListNode<Sprite> nj = boardService.getStatNode(3);
                        while (nj != null) {
                            j = nj.getIndex();

                            if (sp.getLotag() == 12 && sp.getHitag() == sp.getLotag()) {
                                hittype[j].temp_data[0] = 1;
                            }
                            nj = nj.getNext();
                        }
                    }
                    break;
                }
            }
        }

        mirrorcnt = 0;
        Wall[] walls = boardService.getBoard().getWalls();
        for (int i = 0; i < walls.length; i++) {
            Wall wal = walls[i];

            if (wal.getOverpicnum() == MIRROR && (wal.getCstat() & 32) != 0) {
                if (mirrorcnt > 63) {
                    throw new WarningException("\nToo many mirrors (64 max.)");
                }
                Sector nextsector = boardService.getSector(wal.getNextsector());
                if ((nextsector != null) && nextsector.getCeilingpicnum() != MIRROR) {
                    nextsector.setFloorpicnum(MIRROR);
                    mirrorwall[mirrorcnt] = (short) i;
                    mirrorsector[mirrorcnt] = wal.getNextsector();
                    mirrorcnt++;
                    continue;
                }
            }

            if (numanimwalls >= MAXANIMWALLS) {
                throw new WarningException("\nToo many 'anim' walls (max 512.)");
            }

            animwall[numanimwalls].tag = 0;
            animwall[numanimwalls].wallnum = 0;

            switch (wal.getOverpicnum()) {
                case FANSPRITEWORK:
                    Wall wal0 = boardService.getWall(0); // original typo wall->cstat |= 65 instead of wal->cstat |= 65;
                    if (wal0 != null) {
                        wal0.setCstat(wal0.getCstat() | 65);
                    }
                    animwall[numanimwalls].wallnum = (short) i;
                    numanimwalls++;
                    break;
                case BIGFORCE:
                    animwall[numanimwalls].wallnum = (short) i;
                    numanimwalls++;
                    continue;
            }

            wal.setExtra(-1);

            if (wal.getPicnum() == SCREENBREAK6) {
                animwall[numanimwalls].wallnum = (short) i;
                animwall[numanimwalls].tag = -1;
                numanimwalls++;
            }
        }

        // Invalidate textures in sector behind mirror
        for (int i = 0; i < mirrorcnt; i++) {
            Sector sec = boardService.getSector(mirrorsector[i]);
            if (sec == null) {
                continue;
            }

            for (ListNode<Wall> wn = sec.getWallNode(); wn != null; wn = wn.getNext()) {
                Wall wall = wn.get();
                wall.setPicnum(MIRROR);
                wall.setOverpicnum(MIRROR);
            }
        }

        if (haveLigthning == 0) {
//XXX	    	engine.setbrightness((ud.brightness >> 2) & 0xFF, palette);
            visibility = gVisibility;
        }

        InitSpecialTextures();

        gNameShowTime = 500;
    }

    public static void checknextlevel() {
        if (ud.level_number >= currentGame.episodes[ud.volume_number].nMaps) {
            if (ud.volume_number == 0) {
                ud.level_number = 0;
                ud.volume_number = 1;
            } else {
                ud.level_number = 0;
            }
        }
    }

    public static void LeaveMap() {
        System.err.println("LeaveMap");
        if (numplayers > 1 && game.pNet.bufferJitter >= 0 && myconnectindex == connecthead) {
            for (int i = 0; i <= game.pNet.bufferJitter; i++) {
                game.pNet.GetNetworkInput(); // wait for other player before level end
            }
        }

        if (!game.pNet.WaitForAllPlayers(5000)) {
            game.pNet.NetDisconnect(myconnectindex);
            return;
        }

        uGameFlags |= MODE_EOL;
        if (mUserFlag != UserFlag.UserMap && (uGameFlags & MODE_END) == 0
                && ud.level_number >= currentGame.episodes[ud.volume_number].nMaps) {
            uGameFlags |= MODE_END;
        }

        if (currentGame.getCON().type == RRRA && ud.volume_number == 0 && ud.level_number == 6) {
            uGameFlags |= MODE_END;
        }

        gDemoScreen.onStopRecord();
    }

    public static void resetpspritevars() {
        EGS(ps[0].cursectnum, ps[0].posx, ps[0].posy, ps[0].posz, APLAYER, 0, 0, 0, (short) ps[0].ang, 0, 0, 0,
                (short) 10);

        if (!DemoScreen.isDemoPlaying()) {
            for (int i = 0; i < MAXPLAYERS; i++) {
                if (info[i] == null) {
                    info[i] = new PlayerInfo();
                }
                info[i].set(ps[i]);
            }
        }

        resetplayerstats(0);

        for (int i = 1; i < MAXPLAYERS; i++) {
            ps[i].copy(ps[0]);
        }

        if (!DemoScreen.isDemoPlaying()) {
            for (int i = 0; i < MAXPLAYERS; i++) {
                info[i].restore(ps[i]);
                if (ps[i].curr_weapon == PISTOL_WEAPON) {
                    ps[i].kickback_pic = 22;
                } else {
                    ps[i].kickback_pic = 0;
                }
            }
        }

        numplayersprites = 0;

        which_palookup = 9;
        int j = connecthead;
        ListNode<Sprite> node = boardService.getStatNode(10);
        while (node != null) {
            int i = node.getIndex();
            ListNode<Sprite> nexti = node.getNext();
            Sprite s = node.get();

            if (numplayersprites == MAXPLAYERS) {
                throw new WarningException("\nToo many player sprites (max 16.)");
            }

            po[numplayersprites].ox = s.getX();
            po[numplayersprites].oy = s.getY();
            po[numplayersprites].oz = s.getZ();
            po[numplayersprites].oa = s.getAng();
            po[numplayersprites].os = s.getSectnum();

            numplayersprites++;
            if (j >= 0) {
                s.setOwner(i);
                s.setShade(0);
                s.setXrepeat(24);
                s.setYrepeat(17);
                s.setCstat(1 + 256);
                s.setXoffset(0);
                s.setClipdist(64);

                if ((uGameFlags & MODE_EOL) != MODE_EOL || ps[j].last_extra == 0) {
                    ps[j].last_extra = (short) currentGame.getCON().max_player_health;
                    s.setExtra((short) currentGame.getCON().max_player_health);
                } else {
                    s.setExtra(ps[j].last_extra);
                }

                s.setYvel(j);

                if (s.getPal() == 0) {
                    s.setPal(ps[j].palookup = which_palookup);
                    which_palookup++;
                    if (which_palookup >= 17) {
                        which_palookup = 9;
                    }
                } else {
                    ps[j].palookup = s.getPal();
                }

                ps[j].i = (short) i;
                ps[j].frag_ps = (short) j;
                hittype[i].owner = i;

                ps[j].bobposx = ps[j].oposx = ps[j].posx = s.getX();
                ps[j].bobposy = ps[j].oposy = ps[j].posy = s.getY();
                ps[j].oposz = ps[j].posz = s.getZ();
                ps[j].oang = ps[j].ang = s.getAng();

                ps[j].cursectnum = engine.updatesector(s.getX(), s.getY(), ps[j].cursectnum);

                j = connectpoint2[j];

            } else {
                engine.deletesprite(i);
            }
            node = nexti;
        }
    }

    public static void clearfrags() {
        for (int i = 0; i < MAXPLAYERS; i++) {
            ps[i].frag = ps[i].fraggedself = 0;
            Arrays.fill(frags[i], (short) 0);
        }
    }


    public static class PlayerInfo {
        public final int[] ammo_amount = new int[MAX_WEAPONSRA];
        public final boolean[] gotweapon = new boolean[MAX_WEAPONSRA];
        public int aimmode;
        public int autoaim;
        public short shield_amount;
        public short curr_weapon;
        public int inven_icon;

        public short whishkey_amount;
        public short moonshine_amount;
        public short beer_amount;
        public short cowpie_amount;
        public short empty_amount;
        public short snorkle_amount;
        public short boot_amount;
        public short last_extra;

        public void readObject(InputStream is) throws IOException {
            aimmode = StreamUtils.readUnsignedByte(is);
            autoaim = StreamUtils.readUnsignedByte(is);

            for (int j = 0; j < MAX_WEAPONSRA; j++) {
                ammo_amount[j] = StreamUtils.readShort(is);
                gotweapon[j] = StreamUtils.readBoolean(is);
            }
            shield_amount = StreamUtils.readShort(is);
            curr_weapon = StreamUtils.readShort(is);
            inven_icon = StreamUtils.readInt(is);

            whishkey_amount = StreamUtils.readShort(is);
            moonshine_amount = StreamUtils.readShort(is);
            beer_amount = StreamUtils.readShort(is);
            cowpie_amount = StreamUtils.readShort(is);
            empty_amount = StreamUtils.readShort(is);
            snorkle_amount = StreamUtils.readShort(is);
            boot_amount = StreamUtils.readShort(is);
            last_extra = StreamUtils.readShort(is);
        }

        public void writeObject(OutputStream os) throws IOException {
            StreamUtils.writeByte(os, aimmode);
            StreamUtils.writeByte(os, autoaim);

            for (int j = 0; j < MAX_WEAPONSRA; j++) {
                StreamUtils.writeShort(os, ammo_amount[j]);
                StreamUtils.writeBoolean(os, gotweapon[j]);
            }
            StreamUtils.writeShort(os, shield_amount);
            StreamUtils.writeShort(os, curr_weapon);
            StreamUtils.writeInt(os, inven_icon);

            StreamUtils.writeShort(os, whishkey_amount);
            StreamUtils.writeShort(os, moonshine_amount);
            StreamUtils.writeShort(os, beer_amount);
            StreamUtils.writeShort(os, cowpie_amount);
            StreamUtils.writeShort(os, empty_amount);
            StreamUtils.writeShort(os, snorkle_amount);
            StreamUtils.writeShort(os, boot_amount);
            StreamUtils.writeShort(os, last_extra);
        }

        public void set(PlayerStruct p) {
            aimmode = p.aim_mode;
            autoaim = p.auto_aim;

            if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
                for (int j = 0; j < MAX_WEAPONSRA; j++) {
                    ammo_amount[j] = p.ammo_amount[j];
                    gotweapon[j] = p.gotweapon[j];
                }
                shield_amount = p.shield_amount;
                curr_weapon = p.curr_weapon;
                inven_icon = p.inven_icon;

                whishkey_amount = p.whishkey_amount;
                moonshine_amount = p.moonshine_amount;
                beer_amount = p.beer_amount;
                cowpie_amount = p.cowpie_amount;
                empty_amount = p.yeehaa_amount;
                snorkle_amount = p.snorkle_amount;
                boot_amount = p.boot_amount;
                last_extra = (short) p.last_extra;
            }
        }

        public void restore(PlayerStruct p) {
            p.aim_mode = aimmode;
            p.auto_aim = autoaim;

            if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
                for (int j = 0; j < MAX_WEAPONSRA; j++) {
                    p.ammo_amount[j] = ammo_amount[j];
                    p.gotweapon[j] = gotweapon[j];
                }
                p.shield_amount = shield_amount;
                p.curr_weapon = curr_weapon;
                p.inven_icon = inven_icon;

                p.whishkey_amount = whishkey_amount;
                p.moonshine_amount = moonshine_amount;
                p.beer_amount = beer_amount;
                p.cowpie_amount = cowpie_amount;
                p.yeehaa_amount = empty_amount;
                p.snorkle_amount = snorkle_amount;
                p.boot_amount = boot_amount;
                p.last_extra = last_extra;
            }
        }
    }

}