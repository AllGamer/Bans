package net.AllGamer.AGBS;


import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.util.config.Configuration;

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
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		AGBS.configBan.load();
		String[] x = AGBS.configBan.getString("banned").split(",");
		String bannedPlayers = AGBS.make(x, 0);
		if (bannedPlayers.contains(player.getDisplayName().toLowerCase()))
		{
			event.disallow(PlayerLoginEvent.Result.KICK_FULL, "You are banned from this server!");
		}
	}

}

