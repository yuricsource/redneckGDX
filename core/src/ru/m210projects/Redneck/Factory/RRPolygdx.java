// This file is part of RedneckGDX
// Copyright (C) 2017-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.GdxRender.GDXOrtho;
import ru.m210projects.Build.Render.GdxRender.GDXRenderer;
import ru.m210projects.Build.settings.GameConfig;

import static ru.m210projects.Redneck.Names.GRID;
import static ru.m210projects.Redneck.Names.MIRROR;

public class RRPolygdx extends GDXRenderer implements RRRenderer {

    public RRPolygdx(GameConfig config) {
        super(config);
    }

    @Override
    protected GDXOrtho allocOrphoRenderer(Engine engine) {
        return new GDXOrtho(this, new RRMapSettings());
    }

    @Override
    protected int[] getMirrorTextures() {
        return new int[]{GRID, 13, MIRROR};
    }

    @Override
    public void setaspect(int daxrange, int daaspect) {
        super.setaspect(daxrange, daaspect);
    }

    @Override
    public int getViewingRange() {
        return viewingrange;
    }

    @Override
    public int getYXAspect() {
        return yxaspect;
    }


}
