package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Globals.*;

public class Script {

	public int type;
	
	public int actorscrptr[] = new int[MAXTILES];
	public short actortype[] = new short[MAXTILES];
	public int script[] = new int[MAXSCRIPTSIZE];
	
	public char[][] level_names = new char[nMaxMaps * nMaxEpisodes][33],level_file_names = new char[nMaxMaps * nMaxEpisodes][128];
	public int[] partime = new int[nMaxMaps * nMaxEpisodes],designertime = new int[nMaxMaps * nMaxEpisodes];
	public char[][] volume_names = new char[nMaxMaps][33];
	public char[][] skill_names = new char[nMaxSkills][33];
	
	public int nEpisodes;
	public int nSkills;
	public int nMaps[] = new int[nMaxEpisodes];
	
	public char[][] fta_quotes = new char[NUMOFFIRSTTIMEACTIVE][64];
	public char[][] key_quotes;
	public String music_fn[][] = new String[5][11];
	public String env_music_fn[] = new String[5];
	public short[] soundps = new short[NUM_SOUNDS],soundpe = new short[NUM_SOUNDS],soundvo = new short[NUM_SOUNDS];
	public short[] soundm = new short[NUM_SOUNDS],soundpr = new short[NUM_SOUNDS];
	public String[] sounds = new String[NUM_SOUNDS];
	public char[] betaname = new char[80];
	
	public int const_visibility;
	public int impact_damage;
	public int gc=176,max_player_health,max_armour_amount, max_ammo_amount[] = new int[MAX_WEAPONSRA];
	public int respawnactortime=768, respawnitemtime=768;
	public int dukefriction = 0xcc00;
	public int numfreezebounces=3,crossbowblastradius,tntblastradius,bouncemineblastradius,shrinkerblastradius,morterblastradius,powderblastradius,seenineblastradius;
	public char camerashitable,freezerhurtowner=0,dildoblase;
	public short spriteqamount=64;
	
	private long crc32 = -1;
	public long getCRC32()
	{
		if(crc32 == -1)
		{
			//calc crc32 TODO:
		}
		return crc32;
	}
}
