package ru.m210projects.Redneck.Fonts;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Types.font.CharInfo;
import ru.m210projects.Build.Types.font.Font;

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.MINIFONT;

public class ConsoleFont extends Font {

    public ConsoleFont(Engine draw) {
        this.size = draw.getTile(MINIFONT).getHeight() + 2;
        this.setVerticalScaled(false);

        this.addCharInfo(' ', new CharInfo(this, -1, 1.0f, 8, 8, 0, 0));
        int nTile = MINIFONT;

        for (int i = 0; i < 64; i++) {
            char symbol = (char) (i + '!');
            if (draw.getTile(nTile + i).getWidth() != 0) {
                this.addChar(symbol, nTile + i);
            }
        }
        for (int i = 64; i < 90; i++) {
            char symbol = (char) (i + '!');
            if (draw.getTile(nTile + i - 32).getWidth() != 0) {
                this.addChar(symbol, nTile + i - 32);
            }
        }
        for (int i = 90; i < 95; i++) {
            char symbol = (char) (i + '!');
            if (draw.getTile(nTile + i).getWidth() != 0) {
                this.addChar(symbol, nTile + i);
            }
        }
    }

    protected void addChar(char ch, int nTile) {
        this.addCharInfo(ch, new CharInfo(this, nTile, 1.0f, 8, engine.getTile(nTile).getWidth(), 0, 0) {
            @Override
            public int getWidth() {
                return (int) ((engine.getTile(nTile).getWidth() + 1) * tileScale);
            }

            @Override
            public int getHeight() {
                return (int) ((engine.getTile(nTile).getHeight() + 1) * tileScale);
            }
        });
    }

}
