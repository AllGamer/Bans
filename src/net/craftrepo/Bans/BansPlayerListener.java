package net.craftrepo.Bans;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

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
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		if (Bans.engine.contains("flatfile"))
		{
			Bans.configBan.load();
			Bans.configBanIP.load();
			String ipBannedPlayers = Bans.configBanIP.getProperty("banned").toString();
			String bannedPlayers = Bans.configBan.getProperty("banned").toString();
			if (bannedPlayers.contains(player.getName().toLowerCase()))
			{
				event.disallow(PlayerLoginEvent.Result.KICK_FULL, "You are banned from this server!");
				log.info(Bans.logPrefix + " " + player.getName() + " tried to join again!");
			}
		}
		if (Bans.engine.contains("sqlite"))
		{
			if (sqliteConnection.sql("SELECT * FROM player_bans WHERE name = '" + player.getName() + "';"))
			{
				event.disallow(PlayerLoginEvent.Result.KICK_FULL, "You are banned from this server!");
				log.info(Bans.logPrefix + " " + player.getName() + " tried to join again!");
			}
		}
		if (Bans.engine.contains("mysql"))
		{
			if (MySQLConnection.sql("SELECT * FROM player_bans WHERE name = '" + player.getName() + "';"))
			{
				event.disallow(PlayerLoginEvent.Result.KICK_FULL, "You are banned from this server!");
				log.info(Bans.logPrefix + " " + player.getName() + " tried to join again!");
			}
		}
	}
}

