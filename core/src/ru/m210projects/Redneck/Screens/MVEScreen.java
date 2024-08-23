package ru.m210projects.Redneck.Screens;

import ru.m210projects.Build.EngineUtils;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.Font;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Build.osd.Console;
import ru.m210projects.Build.osd.OsdColor;
import ru.m210projects.Redneck.Sounds;
import ru.m210projects.Redneck.filehandle.MVEFile;

import java.io.FileNotFoundException;

import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.*;

public class MVEScreen extends MovieScreen {

    public MVEScreen(BuildGame game) {
        super(game, TILE_ANIM);
        nFlags |= 4;
    }

    @Override
    protected MovieFile GetFile(String file) {
        try {
            return new MVEFile(file);
        } catch (FileNotFoundException fnf) {
            Console.out.println(file + " is not found!", OsdColor.RED);
            return null;
        } catch (Exception e) {
            Console.out.println("Error: " + e, OsdColor.RED);
            return null;
        }
    }

    @Override
    protected void StopAllSounds() {
        Sounds.StopAllSounds();
        Sounds.sndStopMusic();
    }

    @Override
    protected byte[] DoDrawFrame(int num) {
        boolean endOfMovie = ((MVEFile) mvfil).update();
        byte[] buf = mvfil.getFrame(frame);
        if (endOfMovie) {
            skip();
        }
        return buf;
    }

    @Override
    protected Font GetFont() {
        return game.getFont(0);
    }

    @Override
    protected void DrawEscText(Font font, int pal) {
        Renderer renderer = game.getRenderer();
        int shade = 16 + mulscale(16, EngineUtils.sin((20 * engine.getTotalClock()) & 2047), 16);
        font.drawTextScaled(renderer, 160, 5, "Press ESC to skip", 1.0f, shade, ANIM_PAL - 1, TextAlign.Center, Transparent.None, ConvertType.Normal, true);
    }
}
