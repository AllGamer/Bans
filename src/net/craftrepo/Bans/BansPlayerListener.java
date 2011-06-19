package net.craftrepo.Bans;


import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class BansPlayerListener extends PlayerListener 
{
	@SuppressWarnings("unused")
	private final Bans plugin;
	private final Logger log = Logger.getLogger("Minecraft");


	public BansPlayerListener(Bans instance) 
	{
		plugin = instance;
	}


	//ONLY ADD LOGIN STUFF HERE
	public void onPlayerPreLogin(PlayerPreLoginEvent event)
	{
		Bans.configBan.load();
		Bans.configBanIP.load();
		String ipBannedPlayers = Bans.configBanIP.getProperty("banned").toString();
		String bannedPlayers = Bans.configBan.getProperty("banned").toString();
		if (bannedPlayers.contains(event.getName().toLowerCase()))
		{
			event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "You are banned from this server!");
			log.info(Bans.logPrefix + " " + event.getName() + " tried to join again!");
		}
	}

}

