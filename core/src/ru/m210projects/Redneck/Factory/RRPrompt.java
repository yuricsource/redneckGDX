package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.osd.OsdCommandPrompt;

import static ru.m210projects.Build.net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Globals.MODE_TYPE;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;

public class RRPrompt extends OsdCommandPrompt implements OsdCommandPrompt.ActionListener {

    public RRPrompt() {
        super(512, 32);
        setActionListener(this);
    }

    public void draw() {
        Renderer renderer = game.getRenderer();

        int y = 200 - 63;
        if (ud.screen_size <= 2) {
            y = 200 - 20;
        } else if (ud.screen_size == 3) {
            y = 200 - 55;
        }

        final String text = getTextInput();
        final int cursorPos = getCursorPosition();
        final Font font = game.getFont(1);
        final int nShade = 0;
        int pos = 160 - font.getWidth(text, 1.0f) / 2;

        int curX = pos;
        for (int i = 0; i < text.length(); i++) {
            pos += font.drawCharScaled(renderer, pos, y, text.charAt(i), 1.0f, nShade, 0, Transparent.None, ConvertType.Normal, false);
            if (i == cursorPos - 1) {
                curX = pos;
            }
        }

        int pal = isOsdOverType() ? 1 : 0;
        renderer.rotatesprite((curX + 4) << 16, (y + 6) << 16, 4096, 0, SPINNINGNUKEICON + (((engine.getTotalClock() >> 3)) & 15), 0, pal, 10);
    }

    @Override
    public void onEnter(String message) {
        setCaptureInput(false);
        MODE_TYPE = false;
        game.net.SendMessage(numplayers, message.toCharArray(), message.length());
    }


}
