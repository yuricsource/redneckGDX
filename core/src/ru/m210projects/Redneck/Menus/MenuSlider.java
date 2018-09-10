package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Redneck.Globals.dassert;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.Sounds.sound;

import java.util.Arrays;

import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Build.Engine.tilesizx;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bitoa;

import com.badlogic.gdx.Gdx;

public class MenuSlider extends MenuItem
{
	public int value;
	public int min, max, step;
	public int background, slider;
	public MENUPROC callback;
	public boolean enable;
	public boolean digital;
	public float digitalMax;
	public char[] dbuff; 
	public boolean textShadow;
	
	private int touchX;
	private boolean isTouched;
	private static MenuSlider touchedObj;
	
	public MenuSlider(String text, int nFondId, boolean textShadow, int x, int y, int width, int value, int min, int max, 
			int step, MENUPROC callback, int background, int slider, boolean digital) 
	{
		this.flags = 3 | 4;
		this.m_pMenu = null;
		
		if(text != null)
			this.text = text.toCharArray();
		this.textStyle = nFondId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = ClipRange(value, min, max);
		this.callback = callback;
		dbuff = new char[10];
		
		this.background = SLIDEBAR;
		this.slider = 623;
		this.enable = true;
		this.digital = digital;
		digitalMax = 0;
		if(background >= 0)
			this.background = background;
		if(slider >= 0)
			this.slider = slider;
		
		this.textShadow = textShadow;
	}
	
	@Override
	public void draw() {
		mGetAlign(textStyle, text);
		int aly = aligny;
		int shade = 8;
		boolean focused = mGetFocusedItem(m_pMenu, this);
		if ( focused ) 
			shade = 8 - (totalclock & 0x3F);
		int pal = 0;

		int yoff = 0;
	    if(textStyle == 2) yoff = 12;
	    
		if(!enable) pal = 1;
		if ( text != null )
			mDrawText(textStyle, text, x, y, shade, pal, 0, 0);

		int scale = 32768;
		int cx = (x + width - tilesizx[background] / 2 + mulscale(10, scale, 16));
		if(textStyle != 2) cx += tilesizx[background] / 4;

		engine.rotatesprite((cx - mulscale(11, scale, 16)) << 16, aly / 3 - yoff + y - mulscale(1, scale, 16) << 16, scale, 0, background, 0, pal, 10, 0, 0, xdim - 1, ydim - 1);
		if(digital)
		{
			if(digitalMax == 0)
				Bitoa(value, dbuff);
			else {
				String val = Float.toString(value / digitalMax);
				int index = val.indexOf('.');
				buildString(dbuff, 0, val);
				Arrays.fill(dbuff, index + 4, dbuff.length, (char)0);
			}

			mGetAlign(textStyle, dbuff);
			mDrawText(textStyle, dbuff, x + width -  mulscale(tilesizx[background], scale, 16)  - alignx - mulscale(10, scale, 16), y, shade, pal, 0, 0);
		}

		int nRange = max - min;
		if(nRange <= 0) dassert("nRange > 0");
		int xrange = mulscale(tilesizx[background] - 20, scale, 16);
		int dx = xrange * (value - min) / nRange - xrange / 2;
		
		int yoffset = 0;
		if(textStyle == 2) yoffset = 3 - yoff;
		engine.rotatesprite(  (cx + dx - 6) << 16, (aly / 2 + y + yoffset) << 16, (int) (scale * 0.75f), 0, slider, 0, pal==0?2:pal, 10, 0, 0, xdim - 1, ydim - 1);

		scale = 4096;
		if(textStyle == 0) yoffset = -2;
		if(textStyle == 1) yoffset = -6;
		if(textStyle == 2) { yoffset = 4; scale = 8192; }
		if ( focused )
			engine.rotatesprite((x-10)<<16, (y - yoffset) << 16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		int val, out = 0;

		switch(opt) {
		case 2:
			mNavUp(pMenu);
			out = 0;
			break;
		case 3:
			mNavDown(pMenu);
			out = 0;
			break;
		case 4:
		case 17:
			if ( (flags & 4) == 0 ) return 0;
			if(value <= 0) {
				int dv = (value - step) % -step;
		        val = value - step - dv;
		        if ( dv < 0 )
		        	val += step;
			}
			else 
			{
		        int dv = (value - 1) % step;
		        val = value - 1 - dv;
		        if ( dv < 0 )
		        	val -= step;
			}
			value = ClipRange(val, min, max);
			if(callback != null) {
				callback.run(this);
			}
			sound(KICK_HIT);
			break;
		case 5:
		case 16:
			if ( (flags & 4) == 0 ) return 0;
			if ( value < 0 )
		    {
		        int dv = (value - 1) % -step;
		        val = value - 1 - dv;
		        if ( dv < 0 )
		        	val += step;
		    } else
		    {
		        int dv = (value + step) % step;
		        val = value + step - dv;
		        if ( dv < 0 )
		        	val -= step;
		    }
			value = ClipRange(val, min, max);
			if(callback != null) 
				callback.run(this);
			sound(KICK_HIT);
			break;
		case 6:
			if ( (flags & 4) == 0 ) return 0;
			if(callback != null) 
				callback.run(this);
			sound(KICK_HIT);
			break;
		case 11:
			if ( (flags & 4) == 0 ) return 0;
			if(touchedObj == this)
			{
				int scale = 16384;
				if(textStyle == 2) scale = 32768;
				int x1 = x + width - mulscale(tilesizx[background] + 2, scale, 16) - 10;
				float dr = (float)(touchX - x1) / (mulscale(tilesizx[background] - 2, scale, 16));
				value = (int) BClipRange(min + (dr * (max-min)), min, max);
				if(callback != null) 
					callback.run(this);
			} 
			return 0;
		default:
			out = mNavigation(pMenu, opt);
			break;
		}
		
		return out;
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}
	
	@Override
	public void close(MENU pMenu) {
		
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		touchX = mx;
		isTouched = false;

		if(!Gdx.input.isTouched() && touchedObj != this)
			touchedObj = null;
		int cy = y;
		if(textStyle == 2) cy -= 12;
		
		if(text != null)
		{
			mGetAlign(textStyle, text);
			if(mx > x && mx < x + alignx)
			{
				if(my > cy && my < cy + aligny) {
					return true;
				}
			}
		}

		int scale = 16384;
		if(textStyle == 2) scale = 32768;
		int cx = x + width - tilesizx[background] / 2 + mulscale(10, scale, 16);
		
		mGetAlign(textStyle, text);
		if(mx > cx && mx < cx + mulscale(tilesizx[background], scale, 15) )
			if(my > cy && my < cy + aligny) {
				isTouched = true;
				if(Gdx.input.isTouched()) {
					touchedObj = this;
				}
			}
		
		return isTouched;
	}
}