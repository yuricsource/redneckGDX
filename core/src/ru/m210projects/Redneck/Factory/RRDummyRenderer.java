package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Render.DummyRenderer;

public class RRDummyRenderer extends DummyRenderer implements RRRenderer {

    public RRDummyRenderer() {
    }

    @Override
    public void setaspect(int daxrange, int daaspect) {

    }

    @Override
    public int getViewingRange() {
        return 0;
    }

    @Override
    public int getYXAspect() {
        return 0;
    }
}
