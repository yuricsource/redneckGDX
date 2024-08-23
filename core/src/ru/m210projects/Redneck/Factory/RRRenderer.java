package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Render.Renderer;

public interface RRRenderer extends Renderer {

    void setaspect(int daxrange, int daaspect);

    int getViewingRange();

    int getYXAspect();

}


