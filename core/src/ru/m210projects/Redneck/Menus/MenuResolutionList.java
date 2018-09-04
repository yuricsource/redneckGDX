package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Gameutils.ClipLow;
import static ru.m210projects.Redneck.Gameutils.toCharArray;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.Names.SPINNINGNUKEICON;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Menus.DukeMenu.*;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.Build.Gameutils.*;

import java.util.List;

import ru.m210projects.Build.Render.VideoMode;

import com.badlogic.gdx.Gdx;

public class MenuResolutionList extends MenuList {
	
	private int touchY;
	private int scrollX;
	public boolean scrollTouch;

	public MenuResolutionList(List<char[]> text, int x, int y,
			int width, int align, int nItemHeight, MENU nextMenu,
			MENUPROC specialCall, int nListItems) {
		super(text, 1, x, y, width, align, nItemHeight, nextMenu, specialCall,
				nListItems);
	}
	
	@Override
	public void open(MENU pMenu) {
		l_nFocus = -1;
		for (int m = 0; m < validmodes.size(); m++) {
			if ((validmodes.get(m).xdim == xdim)
					&& (validmodes.get(m).ydim == ydim)) {
				l_nFocus = m;
				break;
			}
		}
		if (l_nFocus != -1) {
			currentMode = validmodes.get(l_nFocus);
			choosedMode = currentMode;
			if (l_nFocus >= l_nMin + nListItems)
				l_nMin = l_nFocus - nListItems + 1;
		} else
			currentMode = new VideoMode(Gdx.graphics.getDisplayMode());
	}
	
	@Override
	public void draw() {
		engine.rotatesprite((x - 30) << 16, (y - 4) << 16, 65536, 0, LOADSCREEN, 128, 0, 10 + 16 + 1, 0, 0, coordsConvertXScaled(x+160, 0), coordsConvertYScaled(y+120));
		
		if(text.size() > 0) {
			mGetAlign(textStyle, null);
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
				int pal = this.pal;
				if(pal == 0 && textStyle < 2) pal = 10;
				int shade = 8;
				
				mGetAlign(textStyle, text.get(i));
				if ( i == l_nFocus ) {
					if(mGetFocusedItem(m_pMenu, this))
						shade = 8 - (totalclock & 0x3F);
					else { shade = 8; }

					engine.rotatesprite((x - 20)<<16,(py+5)<<16,4096,0,SPINNINGNUKEICON+((totalclock>>3)&15),0,0,10,0,0,xdim-1,ydim-1);
				}
			    if(align == 1) {
			        px = width / 2 + x - alignx / 2;
			    }
			    if(align == 2) {
			        px = x + width - 1 - alignx;
			    }
				mDrawText(textStyle, text.get(i), px, py, shade, pal, 0, 0);
				py += aligny + nItemHeight;
			}
		} else {
			int pal = this.pal;
			if(pal == 0 && textStyle < 2) pal = 10;
			int shade = 8;
			String text = "List is empty";
			mGetAlign(textStyle, toCharArray(text));
			int px = x, py = y;		
			if(align == 1) {
		    	mGetAlign(textStyle, toCharArray(text));
		        px = width / 2 + x - alignx / 2;
		    }
		    if(align == 2) {
		    	mGetAlign(textStyle, toCharArray(text));
		        px = x + width - 1 - alignx;
		    }	    
			mGetAlign(textStyle, null);
			if(mGetFocusedItem(m_pMenu, this))
				shade = 8 - (totalclock & 0x3F);
			mDrawText(textStyle, toCharArray(text), px, py, shade, pal, 0, 0);
		}

		//Scroller
		int nList = ClipLow(text.size() - nListItems, 1);
		int posy = (((nListItems) * aligny - 13)) * l_nMin / nList;

		scrollX = x + width + 40;
		mDrawSlider(scrollX, y-5, posy, 106, true);
	}
	
	@Override
	public int callback(MENU pMenu, int opt) {
		switch(opt)
		{
			case 16:
				if(l_nMin > 0)
					l_nMin--;
				sound(KICK_HIT);
				return 0;
			case 17:
				if(text != null)
					if(l_nMin < text.size() - nListItems)
						l_nMin++;
				sound(KICK_HIT);
				return 0;
			case 2:
				l_nFocus--;
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					if(l_nMin > 0) l_nMin--;
				if(l_nFocus < 0) {
					l_nFocus = text.size() - 1;
					l_nMin = text.size() - nListItems;
					if(l_nMin < 0) l_nMin = 0;
				}
				sound(KICK_HIT);
				return 0;
			case 3:
				l_nFocus++;
				if(l_nFocus >= l_nMin + nListItems && l_nFocus < text.size())
					l_nMin++;
				if(l_nFocus >= text.size()) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				sound(KICK_HIT);
				return 0;
			case 4: //left
				mNavUp(pMenu);
				return 0;
			case 5: //right
				mNavDown(pMenu);
				return 0;
			case 6: //enter
			case 11:
				if ( (flags & 4) == 0 ) return 0;
				if(opt == 11 && scrollTouch)
				{
					l_nFocus = -1;
					int nList = ClipLow(text.size() - nListItems, 1);
					int nRange = nListItems * aligny - 16;
					mGetAlign(textStyle, null);
					int py = y;
					float dr = (float)(touchY - py) / nRange;

					l_nMin = (int) BClipRange(dr * nList, 0, nList);
					
					return 0;
				}

				if(l_nFocus != -1 && text.size() > 0) {
					specialCall.run(this);
					if ( nextMenu != null )
				    	mOpen(nextMenu, -1);
				}
				getInput().resetKeyStatus();
				return 0;
			case 7: //esc
			case 18:
				//l_nFocus = l_nMin = 0;
				return 1;
			case MKPGUP:
				l_nFocus -= (nListItems - 1);
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					if(l_nMin > 0) l_nMin -= (nListItems - 1);
				if(l_nFocus < 0 || l_nMin < 0) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				return 0;
			case MKPGDW:
				l_nFocus += (nListItems - 1);
				if(l_nFocus >= l_nMin + nListItems && l_nFocus < text.size())
					l_nMin += (nListItems - 1);
				if(l_nFocus >= text.size() || l_nMin > text.size() - nListItems) {
					l_nFocus = text.size() - 1;
					if(text.size() >= nListItems)
						l_nMin = text.size() - nListItems;
					else l_nMin = text.size() - 1;
				}
				return 0;
			case MKHOME:
				l_nFocus = 0;
				l_nMin = 0;
				return 0;
			case MKEND:
				l_nFocus = text.size() - 1;
				if(text.size() >= nListItems)
					l_nMin = text.size() - nListItems;
				else l_nMin = text.size() - 1;
				return 0;
		}
		return 0;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		
		if(!Gdx.input.isTouched()) 
			scrollTouch= false;
		
		touchY = my;
		if(mx > scrollX && mx < scrollX + 14) 
		{
			if(Gdx.input.isTouched())
				scrollTouch = true;
			else scrollTouch = false;
			return true;
		}
		
		if(!scrollTouch && text.size() > 0) {
			mGetAlign(textStyle, null);
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
			    if(align == 1) {
			    	mGetAlign(textStyle, text.get(i));
			        px = width / 2 + x - alignx / 2;
			    }
			    if(align == 2) {
			    	mGetAlign(textStyle, text.get(i));
			        px = x + width - 1 - alignx;
			    }

			    if(mx > px && mx < px + alignx)
					if(my > py && my < py + aligny)
					{
						l_nFocus = i;
						return true;
					}
			    
				py += aligny + nItemHeight;
			}
		}
		return false;
	}
}
