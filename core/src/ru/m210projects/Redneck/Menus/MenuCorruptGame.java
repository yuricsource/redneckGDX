package ru.m210projects.Redneck.Menus;

import com.badlogic.gdx.Gdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Redneck.Main;

public class MenuCorruptGame extends BuildMenu {

    private Runnable runnable;

    public MenuCorruptGame(final Main game) {
        super(game.pMenu);
        MenuText QuitQuestion = new MenuText(
                "The saved game is incompatible",
                game.getFont(1), 160, 90, 1) {
            @Override
            public void draw(MenuHandler handler) {
                super.draw(handler);
                Renderer renderer = handler.getRenderer();
                font.drawTextScaled(renderer, 160, y + 10, "but can be loaded at the level start.",1.0f, -128, pal, TextAlign.Center, Transparent.None, ConvertType.Normal, fontShadow);
                font.drawTextScaled(renderer, 160, y + 20, "Do you want to load?",1.0f, -128, pal, TextAlign.Center, Transparent.None, ConvertType.Normal, fontShadow);
            }
        };
        addItem(QuitQuestion, false);

        MenuVariants question = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(1), 160, 130) {
            @Override
            public void positive(MenuHandler menu) {
                if (runnable != null) {
                    Gdx.app.postRunnable(runnable);
                }
                menu.mClose();
            }
        };

        addItem(question, true);
    }

    public void setRunnable(Runnable run) {
        this.runnable = run;
    }
}
