package ru.m210projects.Redneck.Screens;

import ru.m210projects.Build.Architecture.MessageType;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MessageScreen;
import ru.m210projects.Build.Render.Renderer;

import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.Screen.scrReset;

public class RRMessageScreen extends MessageScreen {

    public RRMessageScreen(BuildGame game, String header, String message, MessageType type) {
        super(game, header, message, game.getFont(1), game.getFont(3), type);
    }

    @Override
    public void show() {
        super.show();
        scrReset();
    }

    @Override
    public void drawBackground(Renderer renderer) {
        renderer.rotatesprite(320 << 15, 200 << 15, 65536, 0, LOADSCREEN, 0, 0, 2 + 8 + 64);
    }
}
