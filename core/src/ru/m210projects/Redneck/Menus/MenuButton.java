package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Menus.MENU.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Screen.*;

public class MenuButton extends MenuItem
{
	public int align;
	public MENU nextMenu;
	public int nItem;
	public MENUPROC specialCall;
	public int specialOpt;

	public MenuButton(Object text, int textStyle, int x, int y, int width, int align, int pal, MENU nextMenu, int nItem, MENUPROC specialCall, int specialOpt) {
		this.flags = 3 | 4;
		this.m_pMenu = null;
		
		this.pal = pal;
		if(text != null) {
			if(text instanceof String) 
				this.text = ((String)text).toCharArray();
			if(text instanceof char[])
				this.text = (char[]) text;
		}
		this.textStyle = textStyle;
		this.x = x;
		this.y = y;
		this.width = width;
		this.align = align;
		this.nextMenu = nextMenu;
		this.nItem = nItem;
		this.specialCall = specialCall;
		this.specialOpt = specialOpt;
	}

	@Override
	public void draw() {
		if ( text != null )
		{
		    int shade = 8;
		    if ( mGetFocusedItem(m_pMenu, this) )
		     	shade = 8 - (totalclock & 0x3F);
		   
		    int px = x;
		    int pal = this.pal;
		    if(pal == 0 && textStyle < 2) pal = 10;
		    if(align == 1) {
		    	mGetAlign(textStyle, text);
		        px = width / 2 + x - alignx / 2;
		    }
		    if(align == 2) {
		    	mGetAlign(textStyle, text);
		        px = x + width - 1 - alignx;
		    }
		    
		    int yoff = 0;
		    if(textStyle == 2) yoff = 13;

		    mDrawText(textStyle, text, px, y + yoff, shade, pal, 0, 0);
		    
		    if ( mGetFocusedItem(m_pMenu, this) ) {

		    	int scale = 4096;
		    	int yoffset = -4;
				if(textStyle == 1) { yoffset = -6; }
				if(textStyle == 2) { yoffset = 1 - yoff; scale = 8192; }
		    	if(align == 1) {
			    	int centre = 320>>2;
				    engine.rotatesprite(((320>>1)+(centre>>1)+70)<<16,(y-yoffset)<<16,scale,0,SPINNINGNUKEICON+15-((15+(totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
				    engine.rotatesprite(((320>>1)-(centre>>1)-70)<<16,(y-yoffset)<<16,scale,0,SPINNINGNUKEICON+((totalclock>>3)&15),shade,0,10,0,0,xdim-1,ydim-1);
		    	} else if(align == 0) engine.rotatesprite((px-tilesizx[BIGFNTCURSOR]-4)<<16,(y-4)<<16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))&15),shade,0,10,0,0,xdim-1,ydim-1);
		    }
		}
	}

	@Override
	public int callback(MENU pMenu, int opt) {
		if ( (flags & 4) != 0 && (opt == 6 || opt == 11))
		{
			if ( specialCall != null )
				specialCall.run(this);
		    if ( nextMenu != null )
		    	mOpen(nextMenu, nItem);
		}
		else {
			return mNavigation(m_pMenu, opt);
		}
		return 0;
	}
	
	@Override
	public void open(MENU pMenu) {
		
	}
	
	@Override
	public void close(MENU pMenu) {
		
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			mGetAlign(textStyle, text);
			int px = x;
			if(align == 1) 
		        px = width / 2 + x - alignx / 2;
		    
			if(align == 2) 
				px = x + width - 1 - alignx;

			if(mx > px && mx < px + alignx)
				if(my > y && my < y + aligny)
					return true;
		}
		return false;
	}
}