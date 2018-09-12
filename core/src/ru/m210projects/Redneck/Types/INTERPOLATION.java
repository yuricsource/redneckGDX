// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Kirill Klimenko-KLIMaka 
// and Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Types;

public class INTERPOLATION {
	
	public static final int WALLX = 1 << 0;
	public static final int WALLY = 1 << 1;
	public static final int FLOORZ = 1 << 2;
	public static final int CEILZ = 1 << 3;
	public static final int FLOORH = 1 << 4;
	
	public Object ptr;
	public int type;
	public int oldpos;
	public int bakpos;
}
