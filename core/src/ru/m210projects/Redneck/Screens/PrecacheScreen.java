package ru.m210projects.Redneck.Screens;

import ru.m210projects.Build.Pattern.ScreenAdapters.DefaultPrecacheScreen;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.listeners.PrecacheListener;
import ru.m210projects.Build.Types.*;
import ru.m210projects.Build.Types.collections.ListNode;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Redneck.Main;

import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Premap.getsound;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;

public class PrecacheScreen extends DefaultPrecacheScreen {

    public PrecacheScreen(Runnable toLoad, PrecacheListener listener) {
        super(Main.game, toLoad, listener);

        addQueue("Preload sounds...", this::precachenecessarysounds);

        addQueue("Preload floor and ceiling tiles...", () -> {
            Sector[] sectors = boardService.getBoard().getSectors();
            for (Sector sec : sectors) {
                addTile(sec.getFloorpicnum());
                addTile(sec.getCeilingpicnum());
            }
            doprecache(0);

            for (Sector sec : sectors) {
                if ((sec.getCeilingstat() & 1) != 0) {
                    if (sec.getCeilingpicnum() == 1031) {
                        for (int j = 0; j < 5; j++) {
                            addTile(sec.getCeilingpicnum() + j);
                        }
                    } else {
                        addTile(sec.getCeilingpicnum());
                    }
                }
            }

            doprecache(1);
        });

        addQueue("Preload wall tiles...", () -> {
            for (Wall wall : boardService.getBoard().getWalls()) {
                addTile(wall.getPicnum());
                switch (wall.getPicnum()) {
                    case WATERTILE2:
                        for (int j = 0; j < 3; j++) {
                            addTile(wall.getPicnum() + j);
                        }
                        break;
                    case BUSTAWIN4A:
                    case BUSTAWIN4B:
                    case BUSTAWIN5A:
                    case BUSTAWIN5B:
                        addTile(wall.getPicnum());
                        break;
                    case SCREENBREAK6:
                    case SCREENBREAK7:
                    case SCREENBREAK8:
                        for (int k = SCREENBREAK6; k < SCREENBREAK8; k++) {
                            addTile(k);
                        }
                        break;
                }
                if (wall.getOverpicnum() >= 0) {
                    addTile(wall.getOverpicnum());
                    if (wall.getOverpicnum() == W_FORCEFIELD) {
                        for (int j = 0; j < 3; j++) {
                            addTile(W_FORCEFIELD + j);
                        }
                    }
                }
            }
            doprecache(0);
        });

        addQueue("Preload sprite tiles...", () -> {
            cachegoodsprites();
            for (int i = 0; i < boardService.getSectorCount(); i++) {
                ListNode<Sprite> j = boardService.getSectNode(i);
                while (j != null) {
                    Sprite spr = j.get();
                    if (spr.getXrepeat() != 0 && spr.getYrepeat() != 0 && (spr.getCstat() & 32768) == 0) {
                        cachespritenum(spr);
                    }
                    j = j.getNext();
                }
            }

            doprecache(1);
        });
    }

    @Override
    public void draw(String title, int i) {
        Renderer renderer = game.getRenderer();
        renderer.clearview(129);
        renderer.rotatesprite(320 << 15, 200 << 15, 65536, 0, LOADSCREEN, 0, 0, 2 + 8 + 64);

        if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
            game.getFont(2).drawTextScaled(renderer, 160, 90, "Entering ", 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            if (currentGame.episodes[ud.volume_number] != null) {
                game.getFont(2).drawTextScaled(renderer, 160, 90 + 16 + 8, currentGame.episodes[ud.volume_number].getMapTitle(ud.level_number), 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        } else {
            game.getFont(2).drawTextScaled(renderer, 160, 90, "Entering usermap", 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            game.getFont(2).drawTextScaled(renderer, 160, 90 + 16 + 8, boardfilename.getName(), 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
        }

        game.getFont(1).drawTextScaled(renderer, 160, 90 + 48 + 8, title, 1.0f, -128, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
    }

    private void precachenecessarysounds() {
        for (int i = 0; i < NUM_SOUNDS; i++) {
            if (Sound[i].ptr == null) {
                getsound(i);
            }
        }
    }

    private void cachespritenum(Sprite sp) {
        short j;

        if (ud.monsters_off && badguy(sp)) {
            return;
        }

        int maxc = 1;
        switch (sp.getPicnum()) {
            case APLAYER:
                maxc = 0;
                if (ud.multimode > 1) {
                    maxc = 5;
                    for (j = 1420; j < 1420 + 106; j++) {
                        addTile(j);
                    }
                }
                break;
            case 2121:
            case 2122:
                break;
            case BILLYRAY:
                for (j = BILLYWALK; j < BILLYJIBB + 4; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case DOGRUN:
                for (j = DOGATTACK; j <= 4095; j++) {
                    addTile(j);
                }
                for (j = DOGRUN; j <= 4340; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case LTH:
                for (j = LTH; j < 4457; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case BUBBASTAND:
                for (j = BUBBASCRATCH; j < 4511; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case HULK:
                for (j = (short) (sp.getPicnum() - 41); j < sp.getPicnum() - 1; j++) {
                    addTile(j);
                }
                for (j = HULKJIBA; j <= HULKJIBC + 4; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case 4770: // BUBBAELVIS
                for (j = 4770; j <= 4799; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case BIKERRIDE:
            case BIKERRIDE + 1:
                for (j = BIKERRIDE; j <= 5994; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case BIKERSTAND:
                for (j = BIKERSTAND; j <= 6111; j++) {
                    addTile(j);
                }
                for (j = 6145; j <= 6249; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case BIKERRIDEDAISY:
                for (j = BIKERRIDEDAISY; j <= 6484; j++) {
                    addTile(j);
                }
                for (j = 6558; j <= 6641; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case DAISYMAE:
            case DAISYMAE + 1:
                for (j = DAISYMAE; j <= 6702; j++) {
                    addTile(j);
                }
                for (j = 6705; j <= 6916; j++) {
                    addTile(j);
                }
                for (j = 6920; j <= 6992; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case BANJOCOOTER:
                for (j = BANJOCOOTER; j <= 7034; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case GUITARBILLY:
                for (j = GUITARBILLY; j <= 7037; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case JACKOLOPE:
                for (j = 7280; j <= 7334; j++) {
                    addTile(j);
                }
                for (j = 7336; j <= 7385; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case MAMAJACKOLOPE:
                for (j = 8705; j <= 8783; j++) {
                    addTile(j);
                }
                for (j = 8785; j <= 8792; j++) {
                    addTile(j);
                }
                for (j = 8795; j <= 8889; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case MINION:
                for (j = sp.getPicnum(); j < sp.getPicnum() + 141; j++) {
                    addTile(j);
                }
                for (j = MINJIBA; j <= MINJIBC + 4; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case 5317: // TCOW:
                maxc = 56;
                break;
            case COOT:
                for (j = COOT; j <= 8783; j++) {
                    addTile(j);
                }
                for (j = COOTJIBA; j < 5620; j++) {
                    addTile(j);
                }
                maxc = 0;
                break;
            case ECLAIRHEALTH:
                maxc = 14;
                break;
            case VIXEN:
                maxc = 214;
                break;
            case MOSQUITO:
                maxc = 6;
                break;
            case HEN:
                maxc = 34;
                break;
            case PIG:
            case PIGSTAYPUT:
                maxc = 68;
                break;
            case FORCERIPPLE:
                maxc = 9;
                break;
            case SEENINE:
            case OOZFILTER:
                maxc = 3;
                break;
            case TORNADO:
                maxc = 7;
                break;
            case UFO1:
            case UFO2:
            case UFO3:
            case UFO4:
            case UFO5:
                maxc = 4;
                break;
        }

        for (j = sp.getPicnum(); j < (sp.getPicnum() + maxc); j++) {
            addTile(j);
        }
    }

    private void cachegoodsprites() {
        short i;

        // HUD
        addTile(BOTTOMSTATUSBAR);
        if (ud.multimode > 1) {
            addTile(FRAGBAR);
            addTile(KILLSICON);
        }
        for (i = DIGITALNUM; i < DIGITALNUM + 10; i++) {
            addTile(i);
        }

        for (i = 3374; i < 3379; i++) // MASK
        {
            addTile(i);
        }

        addTile(ARROW);
        addTile(INVENTORYBOX);
        addTile(HEALTHBOX);
        addTile(AMMOBOX);
        addTile(WHISHKEY_ICON);
        addTile(BOOT_ICON);
        addTile(COWPIE_ICON);
        addTile(SNORKLE_ICON);
        addTile(MOONSHINE_ICON);
        addTile(BEER_ICON);
        addTile(ACCESS_ICON);
        addTile(NEWCROSSHAIR);
        addTile(CROSSHAIR);
        addTile(FRAGBAR - 1);

        for (i = 920; i < 924; i++) {
            addTile(i);
        }
        for (i = 930; i < 939; i++) {
            addTile(i);
        }

        // FONTS
        for (i = STARTALPHANUM; i < ENDALPHANUM + 1; i++) {
            addTile(i);
        }
        for (i = BIGALPHANUM; i < BIGALPHANUM + 82; i++) {
            addTile(i);
        }
        for (i = MINIFONT; i < MINIFONT + 63; i++) {
            addTile(i);
        }

        // WEAPONS
        for (i = NEWCROWBAR; i < NEWCROWBAR + 8; i++) {
            addTile(i);
        }
        for (i = NEWPISTOL; i < NEWPISTOL + 11; i++) {
            addTile(i);
        }
        for (i = NEWSHOTGUN; i < NEWSHOTGUN + 9; i++) {
            addTile(i);
        }
        for (i = 3370; i < 3373; i++) {
            addTile(i);
        }
        for (i = RIFLE; i < RIFLE + 3; i++) {
            addTile(i);
        }
        for (i = SHELL; i < SHELL + 2; i++) // Dynamite
        {
            addTile(i);
        }
        for (i = 1752; i < 1757; i++) // Dynamite
        {
            addTile(i);
        }
        for (i = NEWDYNAMITE; i < NEWDYNAMITE + 7; i++) {
            addTile(i);
        }
        for (i = CIRCLESTUCK - 5; i < CIRCLESTUCK; i++) {
            addTile(i);
        }
        for (i = BUZSAW; i < BUZSAW + 3; i++) {
            addTile(i);
        }
        for (i = 3415; i < 3419; i++) {
            addTile(i);
        }
        for (i = 3427; i < 3429; i++) {
            addTile(i);
        }
        addTile(3438);
        for (i = 3445; i < 3448; i++) {
            addTile(i);
        }
        for (i = 3452; i < 3459; i++) {
            addTile(i);
        }

        // PICKUPS
        for (i = FIRSTGUNSPRITE; i <= ALIENARMGUN; i++) {
            addTile(i);
        }
        for (i = AMMO; i <= BOOTS + 1; i++) {
            addTile(i);
        }
        addTile(TEATAMMO);

        for (i = SHOTSPARK1; i <= SHOTSPARK1 + 3; i++) {
            addTile(i);
        }
        for (i = FOOTPRINTS; i < FOOTPRINTS + 3; i++) {
            addTile(i);
        }
        for (i = BURNING; i < BURNING + 14; i++) {
            addTile(i);
        }
        for (i = BURNING2; i < BURNING2 + 14; i++) {
            addTile(i);
        }
        for (i = EXPLOSION2; i < EXPLOSION2 + 21; i++) {
            addTile(i);
        }
        addTile(BULLETHOLE);
        for (i = JIBS1; i < (JIBS5 + 5); i++) {
            addTile(i);
        }
        for (i = JIBS6; i < (JIBS6 + 8); i++) {
            addTile(i);
        }
        for (i = SCRAP6; i < (SCRAP1 + 19); i++) {
            addTile(i);
        }
        for (i = SMALLSMOKE; i < (SMALLSMOKE + 4); i++) {
            addTile(i);
        }
        for (i = BLOOD; i < (BLOOD + 4); i++) {
            addTile(i);
        }
    }

}
