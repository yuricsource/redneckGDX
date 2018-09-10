package ru.m210projects.Redneck.Types;

import ru.m210projects.Redneck.Config;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;
import static ru.m210projects.Redneck.Globals.ud;

public class UserDefs {
	public int warp_on,cashman,eog,showallmap;
	public boolean god,scrollmode,clipping;
	public String[] user_name = new String[MAXPLAYERS];
	public char[][] ridecule = new char[10][];
	public String[] savegame = new String[10];
	public char[] pwlockout = new char[128];
	public String rtsname;
	public int overhead_on,last_overhead,showweapons;

	public int pause_on,from_bonus;
	public int camerasprite = -1,last_camsprite;
	public int last_level,secretlevel;

	public int const_visibility;
	public int camera_time,folfvel,folx,foly,fola;
	public int reccnt;
	public float folavel;

	public int entered_name,screen_tilting = 1,shadows,fta_on = 1,executions,auto_run;
	public int coords,m_coop,coop,screen_size=2,lockout,crosshair=1, playerai;
	public int[][] wchoice = new int[MAXPLAYERS][MAX_WEAPONSRA];

	public int recstat,brightness, m_recstat, detail;
	public boolean monsters_off;
	public boolean respawn_monsters,respawn_items,respawn_inventory, m_respawn_items,m_respawn_monsters,m_respawn_inventory,m_monsters_off;
	public int m_ffire,ffire,m_player_skill,m_level_number,m_volume_number,multimode;
	public int player_skill,level_number,volume_number,m_marker,marker;
	
	public void setDefaults(Config cfg)
	{
		shadows = 1;
		detail = 1;
		lockout = 0;
		pwlockout[0] = '\0';
		m_marker = 1;
		m_ffire = 1;
		
		ud.rtsname = "REDNECK.RTS";

	    ridecule[0] = "An inspiration for birth control. \0".toCharArray();
	    ridecule[1] = "You're gonna die for that! \0".toCharArray();
	    ridecule[2] = "It hurts to be you. \0".toCharArray();
	    ridecule[3] = "Lucky Son of a Bitch. \0".toCharArray();
	    ridecule[4] = "Hmmm....Payback time. \0".toCharArray();
	    ridecule[5] = "You bottom dwelling scum sucker. \0".toCharArray();
	    ridecule[6] = "Damn = you're ugly. \0".toCharArray();
	    ridecule[7] = "Ha ha ha...Wasted! \0".toCharArray();
	    ridecule[8] = "You suck! \0".toCharArray();
	    ridecule[9] = "AARRRGHHHHH!!! \0".toCharArray();
	    
	    screen_size = cfg.screen_size;
	    crosshair = cfg.crosshair;
	    screen_tilting = cfg.screen_tilting;
	    auto_run = cfg.auto_run;
	    fta_on = cfg.fta_on;
	}
}
