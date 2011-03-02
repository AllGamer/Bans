package net.AllGamer.AGBS;


import java.util.logging.Logger;
import org.bukkit.event.player.PlayerListener;

/*
 * Handle events for all Player related events
 * @author AllGamer
 */
public class AGBSPlayerListener extends PlayerListener 
{
	@SuppressWarnings("unused")
	private final AGBS plugin;
	private final Logger log = Logger.getLogger("Minecraft");
	

	public AGBSPlayerListener(AGBS instance) 
	{
		plugin = instance;
	}
	
	
	//ONLY ADD LOGIN STUFF HERE

}

