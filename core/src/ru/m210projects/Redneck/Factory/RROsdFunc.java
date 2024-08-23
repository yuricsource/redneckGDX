package ru.m210projects.Redneck.Factory;

import com.badlogic.gdx.Gdx;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Types.PaletteManager;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.DefaultOsdFunc;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Main;

import java.util.Arrays;

import static ru.m210projects.Build.net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Cheats.IsCheatCode;
import static ru.m210projects.Redneck.Cheats.cheatCode;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.*;

public class RROsdFunc extends DefaultOsdFunc {

    private int PAL_INDEX = 100;

    public RROsdFunc() {
        super(game.getRenderer());

        BGTILE = BACKGROUND;
        BGCTILE = INGAMELNRDTHREEDEE;
        BORDTILE = VIEWBORDER;

        BITSTH = 1 + 8 + 16;
        BITSTL = 1 + 8 + 16;
        BITS = 8 + 16 + 64 + 4;
        BORDERANG = 512;
        SHADE = 10;
        PALETTE = 0;

        makePalookup(OsdColor.WHITE, 255, 255, 255);
        makePalookup(OsdColor.GREEN, 0, 255, 0);
        makePalookup(OsdColor.BLUE, 0, 0, 255);
        makePalookup(OsdColor.YELLOW, 255, 255, 0);
        makePalookup(OsdColor.RED, 255, 0, 0);
    }

    protected Font getFont() {
        return game.getFont(3);
    }

    private void makePalookup(OsdColor color, int red, int green, int blue) {
        PaletteManager manager = Main.engine.getPaletteManager();
        byte index = manager.getFastColorLookup().getClosestColorIndex(manager.getBasePalette(), red >> 2, green >> 2, blue >> 2);
        byte[] remapbuf = new byte[768];
        Arrays.fill(remapbuf, index);
        color.setPal(PAL_INDEX);
        manager.makePalookup(PAL_INDEX++, remapbuf, 0, 0, 0, 1);
    }

    @Override
    public void showOsd(boolean cursorCatched) {
        super.showOsd(cursorCatched);

        if (!(game.getScreen() instanceof InitScreen) && !game.pMenu.gShowMenu) {
            Gdx.input.setCursorCatched(!cursorCatched);
        }
    }

    @Override
    public boolean textHandler(String message) {
        if (numplayers > 1) {
            return false;
        }

        char[] lockeybuf = message.toCharArray();
        int i = 0;
        while (i < lockeybuf.length && lockeybuf[i] != 0) {
            lockeybuf[i++] += 1;
        }
        String cheat = new String(lockeybuf).toUpperCase();

        int ep = -1, lvl = -1;
        boolean wrap1 = false;
        boolean wrap2 = false;

//		System.err.println("/*" + cheatnum++ + "*/" + "\"" + cheat + "\", // " + message);

        boolean IsSkillCheat = cheat.startsWith(cheatCode[7]);
        boolean IsSkipMapCheat = cheat.startsWith(cheatCode[10]);

        if (IsSkillCheat || IsSkipMapCheat) {
            boolean bad = false;
            i = 0;
            while (i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ') {
                i++;
            }
            cheat = cheat.substring(0, i);
            message = message.replaceAll("[\\s]{2,}", " ");
            int startpos = ++i;
            while (i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ') {
                i++;
            }

            if (i <= message.length()) {
                String nEpisode = message.substring(startpos, i);
                nEpisode = nEpisode.replaceAll("[^0-9]", "");
                if (!nEpisode.isEmpty()) {
                    try {
                        ep = Integer.parseInt(nEpisode);
                        wrap1 = true;
                        startpos = ++i;
                        while (i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ') {
                            i++;
                        }
                        if (i <= message.length()) {
                            String nLevel = message.substring(startpos, i);
                            nLevel = nLevel.replaceAll("[^0-9]", "");
                            if (!nLevel.isEmpty()) {
                                lvl = Integer.parseInt(nLevel);
                                wrap2 = true;
                            }
                        } else if (IsSkipMapCheat) {
                            bad = true;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    bad = true;
                }
            } else {
                bad = true;
            }


            if (bad) {
                if (IsSkipMapCheat) {
                    Console.out.println("rdmeadow [episode] [level]");
                } else {
                    Console.out.println("rdskill [skill]");
                }
                return true;
            }
        }

        boolean isCheat = false;
        for (String s : cheatCode) {
            if (cheat.equalsIgnoreCase(s)) {
                isCheat = true;
                break;
            }
        }

        if (!game.isCurrentScreen(gGameScreen) && isCheat) {
            Console.out.println(message + ": not in a game");
            return true;
        }

        if (wrap1) {
            if (wrap2) {
                return IsCheatCode(cheat, ep, lvl);
            } else {
                return IsCheatCode(cheat, ep);
            }
        } else {
            return IsCheatCode(cheat);
        }
    }

}
