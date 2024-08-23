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

package ru.m210projects.Redneck.Menus;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Types.ConvertType;
import ru.m210projects.Build.Types.Transparent;
import ru.m210projects.Build.Types.font.TextAlign;
import ru.m210projects.Redneck.Main;

public class CreditsMenu extends BuildMenu {

    public CreditsMenu(final Main app) {
        super(app.pMenu);
        MenuItem[] mPages = new MenuItem[24];

        mPages[0] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Original concept, design and direction", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Drew Markham",1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };
        mPages[1] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Produced by",1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Greg Goodrich", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };
        mPages[2] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Game programming",1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Rafael PAIZ", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };
        mPages[3] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "ART Directors", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Claire Praderie     Maxx Raufman",1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[4] = new MenuPage(160, 80, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Lead Level Designer", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 10, "Alex Mayberry", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 25, "Level Design", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 35, "Mal BlackWell", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 45, "Sverre Kvernmo", 1.0f,0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[5] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Senior animatior and artist", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Jason Hoover", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[6] = new MenuPage(160, 90, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Technical Director", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Barry Dempsey", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[7] = new MenuPage(160, 60, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Motion Capture Specialist and", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 10, "character animation", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 20, "Amit Doron", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 35, "A.I. Programming", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 45, "Arthur Donavan", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 60, "Additional animation", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 70, "George Karl", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[8] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Character design", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 10, "Corkey Lehmkuhl", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 25, "Map painters", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 35, "Viktor Antonov", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 45, "Matthias Beeguer", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 55, "Stephan Burle", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 70, "Sculptors", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 80, "George Engel", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 90, "Jake Garber", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 100, "Jeff Himmel", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[9] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Character voices", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 15, "Leonard", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 25, "Burton Gilliam", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 40, "Bubba. Billy Ray. Skinny Ol'Coot", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 50, "and the Turd Minion", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 60, "Drew Markham", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 75, "Sheriff Lester T.Hobbes", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 85, "Mojo Nixon", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 100, "Alien Vixen", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 110, "Peggy Jo Jacobs", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[10] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, y, "Sound Design", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 10, "Gary Bradfield", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 25, "Music", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 35, "Mojo Nixon", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 45, "The Beat Farmers", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 55, "The reverend Horton Heat", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 65, "Cement Pond", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 80, "Additional Sound Effects", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, y + 90, "Jim Spurgin", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[11] = new MenuPage(160, 70, -1) {
            @Override
            public void draw(MenuHandler handler) {
                int pos = y;
                Renderer renderer = handler.getRenderer();
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Motion capture actor", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "J.P. Manoux", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Motion capture vixen", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Shawn Wolfe", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[12] = new MenuPage(160, 40, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Production Assistance", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Minerva Mayberry", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Nuts and bolts", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Steve Goldberg", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Marcus Hutchinson", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Bean Counting", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Max Yoshikawa", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Administrative Assistance", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Serafin Lewis", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[13] = new MenuPage(160, 60, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Location Manager. Louisiana", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Rick Skinner", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Location Scout. Louisiana", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Brian Benos", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Photographer", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Carlos Serrao", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[14] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Additional 3D modelling by", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "3 name 3D", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "viewpoint datalabs international", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Audio Recorded at", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Pacific ocean pos. Santa Monica. C.A", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Cement pond tracks recorded at", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Dreamstate recording. Burbank. C.A.", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Recording engeneer", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Dave Ahlert", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[15] = new MenuPage(160, 70, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "3D Build Engine licensed from", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "3D Realms Intertainment", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Build Engine and related tools", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Created by Ken Silverman", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[16] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "For Interplay", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Lead tester", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Darrell Jones", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Testers", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Tim Anderson", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Erick Lujan", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Tien Tran", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[17] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Is techs", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Bill Delk", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Aaron Meyers", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Compatibility techs", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Marc Duran", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Dan Forsyth", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Derek Gibbs", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Aaron Olaiz", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Jack Parker", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[18] = new MenuPage(160, 60, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Director of compatibility", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Phuong Nguyen", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Assistant QA Director", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Colin Totman", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "QA Director", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Chad Allison", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[19] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Interplay Producer", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Bill Dugan", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Interplay Line Produces", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Chris Benson", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Product Manager", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Jim Veevaert", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Public Relations", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Erika Price", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[20] = new MenuPage(160, 50, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Special thanks", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Jim Gauger", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Paul Vais", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Scott Miller", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Todd Replogle", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Chuck Bueche", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Carter Lipscomb", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "John Conley", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Don Maggi", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[21] = new MenuPage(160, 80, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Extra special thanks", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Brian Fargo", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[22] = new MenuPage(160, 70, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Redneck Rampage", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "(C) 1997 Xatrix Entertainment, inc.", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                pos += 5;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Redneck Rampage is a Trademark of", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "Interplay productions", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        mPages[23] = new MenuPage(160, 80, -1) {
            @Override
            public void draw(MenuHandler handler) {
                Renderer renderer = handler.getRenderer();
                int pos = y;
                app.getFont(1).drawTextScaled(renderer, x, pos += 10, "Redneck Rampage GDX", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
                app.getFont(1).drawTextScaled(renderer, x, pos + 10, "(C) 2018 by [M210] (http://m210.duke4.net)", 1.0f, 0, 0, TextAlign.Center, Transparent.None, ConvertType.Normal, false);
            }
        };

        addItem(mPages[mPages.length - 1], true);
        mPages[mPages.length - 1].flags |= 10;
        for (int i = 0; i < mPages.length - 1; i++) {
            addItem(mPages[i], false);
            mPages[i].flags |= 10;
        }
    }
}
