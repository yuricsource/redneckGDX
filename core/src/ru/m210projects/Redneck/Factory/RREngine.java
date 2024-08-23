// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import ru.m210projects.Build.BoardService;
import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Types.PaletteManager;

import java.io.IOException;

import static ru.m210projects.Redneck.Globals.TICRATE;
import static ru.m210projects.Redneck.Globals.TICSPERFRAME;

public class RREngine extends Engine {

    public RREngine(BuildGame game) throws Exception {
        super(game);
        inittimer(game.pCfg.isLegacyTimer(), TICRATE, TICSPERFRAME);
    }

    @Override
    protected BoardService createBoardService() {
        return new RRBoardService();
    }

    @Override
    public PaletteManager loadpalette() throws IOException {
        return new RRPaletteManager(this, game.getCache().getEntry("palette.dat", true));
    }

    @Override
    public RRPaletteManager getPaletteManager() {
        return (RRPaletteManager) paletteManager;
    }
}
