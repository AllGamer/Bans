package net.craftrepo.Bans;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;

//bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

//permissions 2.5.x or greater imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//SQL connections
import net.craftrepo.Bans.sqliteConnection;

/**
 * CraftRepo Bans for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

public class Bans extends JavaPlugin 
{
	public final static Logger log = Logger.getLogger("Minecraft");
	public static String logPrefix = "[Bans]";
	private final BansPlayerListener playerListener = new BansPlayerListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public static String message = "";
	public static String reason = "";
	public static String engine = "";
	public static Configuration config;
	public static Configuration configExempt;
	public static Configuration configBan;
	public static Configuration configBanIP;
	private BansConfiguration confSetup;
	public static PermissionHandler Permissions = null;
	boolean sqliteconnection;
	boolean mysqlconnection;

	heartbeat hb;
	Thread t;
	subscription sc;
	Thread s;

	public void configInit()
	{
		getDataFolder().mkdirs();
		config = new Configuration(new File(this.getDataFolder(), "config.yml"));
		configBan = new Configuration(new File(this.getDataFolder(), "bans.yml"));
		configExempt = new Configuration(new File(this.getDataFolder(), "exempt.yml"));
		configBanIP = new Configuration(new File(this.getDataFolder(), "banIP.yml"));
		confSetup = new BansConfiguration(this.getDataFolder(), this);
	}

	public String pickStorageEngine()
	{
		config.load();
		Object mysql = config.getProperty("mysql");
		Object sqlite = config.getProperty("sqlite");
		Object flatfiles = config.getProperty("flatfiles");
		if (mysql.toString().contains("true"))
		{
			mysqlconnection = new MySQLConnection().initialize();
			return "mysql";
		}
		if (sqlite.toString().contains("true"))
		{
			sqliteconnection = new sqliteConnection().initialize();
			return "sqlite";
		}
		if (flatfiles.toString().contains("true"))
		{
			return "flatfiles";
		}
		return "";
	}

	public static String getAPIKEY()
	{
		config.load();
		Object apikey = config.getProperty("apikey");
		return (String)apikey;
	}

	public void notifyPlayers(String node, String message, Player player, Player target) 
	{
		for (Player p: getServer().getOnlinePlayers()) 
		{ 
			if (Bans.Permissions.has(p, node) || Bans.Permissions.has(p, "agbs.*") || Bans.Permissions.has(p, "*")) 
			{
				p.sendMessage(ChatColor.RED + Bans.logPrefix + " " + player.getDisplayName() + " has " + message + target.getDisplayName() + ".");
			}
		}
	}

	public void setupPermissions() 
	{
		Plugin bans = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (Bans.Permissions == null) 
		{
			if (bans != null)
			{
				this.getServer().getPluginManager().enablePlugin(bans);
				Bans.Permissions = ((Permissions) bans).getHandler();
				log.info(logPrefix + " version " + pdfFile.getVersion() + " Permissions detected...");
			}
			else 
			{
				log.severe(logPrefix + " version " + pdfFile.getVersion() + " not enabled. Permissions not detected");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public void onEnable() 
	{
		configInit();
		confSetup.setupConfigs();
		setupPermissions();
		registerListeners();
		config.load();
		engine = pickStorageEngine();
		hb = new heartbeat( this );
		t = new Thread(hb);
		sc = new subscription();
		s = new Thread(sc);
		t.start();
		s.start();
		log.info(logPrefix + " version " + getDescription().getVersion() + " enabled!");
	}

	public void onDisable() 
	{
		try 
		{
			if ( t != null )
			{
				t.interrupt();
			}
			if ( s != null )
			{
				s.interrupt();
			}
		}
		catch (Exception e)
		{
		}
		PluginDescriptionFile pdfFile = getDescription();
		log.info(logPrefix + " version " + pdfFile.getVersion() + " disabled!");
	}

	public boolean isDebugging(final Player player) 
	{
		if (debugees.containsKey(player)) 
			return debugees.get(player);

		return false;
	}

	public void setDebugging(final Player player, final boolean value) 
	{
		debugees.put(player, value);
	}

	public void registerListeners() 
	{
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
	}

	public static String make(String[] split, int startingIndex) 
	{
		message = "";
		for (; startingIndex < split.length; startingIndex++) 
		{
			if (startingIndex == 1)
				message += "" + split[startingIndex];
			else
				message += " " + split[startingIndex];
		}
		return message;
	}

	public static String makesubs(String[] split, int startingIndex) 
	{
		message = "";
		for (; startingIndex < split.length; startingIndex++) 
		{
			message += "" + split[startingIndex];
		}
		return message;
	}

	public String getPlayers() 
	{
		Player[] players = getServer().getOnlinePlayers();
		String playerNames = "";
		for (Player p1 : players) 
		{
			if (playerNames.equals("")) 
			{
				playerNames += p1.getDisplayName().toLowerCase();
			} 
			else 
			{
				playerNames += "," + p1.getDisplayName();
			}
		}
		return playerNames;
	}

	public boolean arraySearch(Player[] list, Player target) 
	{
		for (Player p : list)
		{
			if (p.equals(target)) 
			{
				return true;
			}
		}
		return false;
	}

	public String makeReason(String message) 
	{
		if (message.contains("grf")) 
		{
			reason += " Griefing";
		}
		if (message.contains("hax")) 
		{
			reason += " Hacking";
		}
		if (message.contains("thf")) 
		{
			reason += " Theft";
		}
		if (message.contains("dis")) 
		{
			reason += " Discriminatory Comments";
		}
		if (message.contains("lan")) 
		{
			reason += " Foul Language (Cursing)";
		}
		if (message.contains("bld")) 
		{
			reason += " Inappropriate Buildings";
		}
		return reason;
	}

	public void banPlayer(Player target, String reason)
	{
		try
		{
			String key = getAPIKEY();
			String data = URLEncoder.encode("player", "UTF-8") + "=" + URLEncoder.encode(target.getName(), "UTF-8");
			data += "&" + URLEncoder.encode("reason", "UTF-8") + "=" + URLEncoder.encode(reason, "UTF-8");
			data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");

			// Send data
			URL url = new URL("http://209.236.124.35/api/ban_player.json");
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.close();
			BufferedReader rd;
			if (conn.getResponseCode() == 200)
			{
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			else
			{
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			String line = rd.readLine();
			if (line.contains("ok"))
			{
				return;
			}
		}
		catch (Exception e)
		{
		}
	}

	public boolean onCommand(CommandSender sender, Command commandArg, String commandLabel, String[] args) 
	{
		Player player = (Player) sender;
		Server server = getServer();
		String command = commandArg.getName().toLowerCase();
		String[] split = args;
		Player[] onlinePlayers = getServer().getOnlinePlayers();
		if (command.equalsIgnoreCase("aban")) 
		{
			if (Bans.Permissions.has(player, "Bans.ban") || Bans.Permissions.has(player, "Bans.*") ||  Bans.Permissions.has(player, "*")) 
			{
				if (split.length >= 1) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (arraySearch(onlinePlayers, target)) 
					{
						reason = makeReason(make(split, 1).toLowerCase());
						if (engine.contains("flatfiles"))
						{
							configBan.load();
							configExempt.load();
							if (configExempt.getProperty("exempt").toString().toLowerCase().contains(target.getName().toLowerCase()))
							{
								player.sendMessage(logPrefix + " This player is exempted from bans!");
								return true;
							}
							else
							{
								server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has banned " + target.getName());
								target.kickPlayer("Banned by " + player.getName() + ". Reason:" + reason);
								configBan.setProperty("banned", configBan.getProperty("banned").toString() + " " + target.getName().toLowerCase());
							}
							configBan.save();
						}
						if (engine.contains("sqlite"))
						{
							if (sqliteConnection.sql("SELECT * FROM exempt WHERE name = '" + target.getName().toLowerCase() + "';"))
							{
								player.sendMessage(logPrefix + " This player is exempted from bans!");
								return true;
							}
							else
							{
								server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has banned " + target.getName());
								target.kickPlayer("Banned by " + player.getName() + ". Reason:" + reason);
								sqliteConnection.sql("INSERT INTO player_bans (id,name) VALUES (null," + target.getName() + ");");
							}
						}
						if (engine.contains("mysql"))
						{
							if (MySQLConnection.sql("SELECT * FROM exempt WHERE name = '" + target.getName().toLowerCase() + "';"))
							{
								player.sendMessage(logPrefix + " This player is exempted from bans!");
								return true;
							}
							else
							{
								server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has banned " + target.getName());
								target.kickPlayer("Banned by " + player.getName() + ". Reason:" + reason);
								MySQLConnection.sql("INSERT INTO player_bans (id,name) VALUES (null," + target.getName() + ");");
							}
						}
						banPlayer(target,reason);
						reason = "";
					}
					else 
					{
						player.sendMessage("Cannot find the specified player! Check your spelling again.");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /aban [target] [reason]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access." );
			}
			return true;
		}
		if (command.equalsIgnoreCase("abanip")) 
		{
			if (Bans.Permissions.has(player, "Bans.banip") || Bans.Permissions.has(player, "Bans.*") || Bans.Permissions.has(player, "*")) 
			{
				if (split.length >= 1) 
				{
					Player target = getServer().getPlayer(split[0]);
					InetSocketAddress ip = target.getAddress();
					String[] socketsplit = ip.toString().split(":");
					String[] ipsplit = socketsplit[0].toString().split(".");
					if (engine.contains("flatfiles"))
					{
						configBanIP.load();
						configBanIP.setProperty("banned", configBanIP.getProperty("banned").toString() + " " + ipsplit[0]);
						configBanIP.save();
					}
					else if (engine.contains("sqlite"))
					{
						sqliteConnection.sql("INSERT INTO ip_bans (id,ip1,ip2,ip3,ip4) VALUES (null," + ipsplit[0] + "," + ipsplit[1] + "," + ipsplit[2] + "," + ipsplit[3] + ");");
					}
					else if (engine.contains("mysql"))
					{
						MySQLConnection.sql("INSERT INTO ip_bans (id,ip1,ip2,ip3,ip4) VALUES (null," + ipsplit[0] + "," + ipsplit[1] + "," + ipsplit[2] + "," + ipsplit[3] + ");");
					}
					reason = makeReason(make(split, 1).toLowerCase());
					target.kickPlayer("Banned by " + player.getDisplayName() + ". Reason:" + reason);
					server.broadcastMessage(Bans.logPrefix + " " + player.getDisplayName() + " has banned " + target + ".");
					reason = "";
				} 
				else 
				{
					player.sendMessage("Correct usage is /abanip [target] [reason]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access." );
			}
			return true;
		}
		if (command.equalsIgnoreCase("aexempt")) 
		{
			if (Bans.Permissions.has(player, "Bans.exempt") || Bans.Permissions.has(player, "Bans.*") ||  Bans.Permissions.has(player, "*")) 
			{
				if (split.length == 1) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (engine.contains("flatfiles"))
					{
						configExempt.load();
						configExempt.setProperty("exempt", configExempt.getProperty("exempt") + " " + target.getName());
						configExempt.save();
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has exempted " + target.getName() + ".");
					}
					if (engine.contains("sqlite"))
					{
						sqliteConnection.sql("INSERT INTO exempt (id,name) VALUES (null," + target.getName() + ");");
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has exempted " + target.getName() + ".");
					}
					if (engine.contains("mysql"))
					{
						MySQLConnection.sql("INSERT INTO exempt (id,name) VALUES (null," + target.getName() + ");");
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has exempted " + target.getName() + ".");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /aexempt [target]");
					return true;
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access." );
				return true;
			}
			return true;
		}
		if (command.equalsIgnoreCase("aunban")) 
		{
			if (Bans.Permissions.has(player, "Bans.unban") || Bans.Permissions.has(player, "Bans.*") ||  Bans.Permissions.has(player, "*")) 
			{
				if (split.length == 1) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (engine.contains("flatfiles"))
					{
						configBan.load();
						if (!configBan.getProperty("banned").toString().toLowerCase().contains(target.getName().toLowerCase()))
						{
							player.sendMessage(logPrefix + " The player '" + target.getName() + "' is not banned!");
							return false;
						}
						String old = configBan.getProperty("banned").toString().toLowerCase();
						String next = old.replace(target.getName().toLowerCase(), "");
						configBan.setProperty("banned", next);
						configBan.save();
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has unbanned " + target.getName() + ".");
					}
					if (engine.contains("sqlite"))
					{
						if (!sqliteConnection.sql("SELECT * FROM player_bans WHERE name = '" + target.getName() + ");"))
						{
							player.sendMessage(logPrefix + " The player '" + target.getName() + "' is not banned!");
							return false;
						}
						sqliteConnection.sql("DELETE FROM player_bans WHERE name = '" + target.getName() + "');");
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has unbanned " + target.getName() + ".");
					}
					if (engine.contains("mysql"))
					{
						if (!MySQLConnection.sql("SELECT * FROM player_bans WHERE name = '" + target.getName() + ");"))
						{
							player.sendMessage(logPrefix + " The player '" + target.getName() + "' is not banned!");
							return false;
						}
						MySQLConnection.sql("DELETE FROM player_bans WHERE name = '" + target.getName() + "');");
						server.broadcastMessage(Bans.logPrefix + " " + player.getName() + " has unbanned " + target.getName() + ".");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /aunban [target]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access." );
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("acheck"))
		{
			if (Bans.Permissions.has(player, "Bans.check") || Bans.Permissions.has(player, "Bans.*") || Bans.Permissions.has(player, "*"))
			{
				if (split.length == 1) 
				{
					Player target = getServer().getPlayer(split[0]);	
					if (engine.contains("flatfiles"))
					{
						configBan.load();
						configBanIP.load();
						if (configBan.getProperty("banned").toString().contains(target.getName().toLowerCase()))
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
						else
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is NOT banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
					}
					if (engine.contains("sqlite"))
					{
						if (sqliteConnection.sql("SELECT * FROM player_bans WHERE name = '" + target.getName() + "';"))
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
						else
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is NOT banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
					}
					if (engine.contains("mysql"))
					{
						if (MySQLConnection.sql("SELECT * FROM player_bans WHERE name = '" + target.getName() + "';"))
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
						else
						{
							player.sendMessage(logPrefix + " The user '" + target.getName() + "' is NOT banned!");
							log.info(logPrefix + player.getName() + " checked '" + target.getName() + "'s ban status.");
						}
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /acheck [name | ip]");
				}
			} 
			else
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access." );
			}
			return true;
		}
		if (command.equalsIgnoreCase("aunbanip"))
		{
			if (Bans.Permissions.has(player, "Bans.unbanip") || Bans.Permissions.has(player, "Bans.*") || Bans.Permissions.has(player, "*"))
			{
				if (split.length == 1) 
				{
					int ip = 0;
					try 
					{
						ip = Integer.parseInt(split[0]);
					} catch (NumberFormatException nfe)
					{
						player.sendMessage(logPrefix + " '" + split[0] + "' is not a valid number.");
					}
					if (ip != 0)
					{
						if (engine.contains("flatfiles"))
						{
							configBanIP.load();
							if (!configBanIP.getProperty("banned").toString().toLowerCase().contains(String.valueOf(ip)))
							{
								player.sendMessage(logPrefix + " The IP '" + ip + "' is not banned!");
								return false;
							}
							String old = configBanIP.getProperty("banned").toString().toLowerCase();
							String next = old.replace(String.valueOf(ip), "");
							configBanIP.setProperty("banned", next);
							configBanIP.save();
						}
						if (engine.contains("sqlite"))
						{
							String[] ipsplit = String.valueOf(ip).split(".");
							if (!sqliteConnection.sql("SELECT * FROM ip_bans WHERE ip1 = '" + ipsplit[1] + "' AND ip2 = '" + ipsplit[2] + "' AND ip3 = '" + ipsplit[3] + "' AND ip4 = '" + ipsplit[4] + ");"))
							{
								player.sendMessage(logPrefix + " The IP '" + ip + "' is not banned!");
								return false;
							}
							sqliteConnection.sql("DELETE FROM IP_TABLE WHERE ip1 = '" + ipsplit[1] + "' AND ip2 = '" + ipsplit[2] + "' AND ip3 = '" + ipsplit[3] + "' AND ip4 = '" + ipsplit[4] + ");");
						}
						if (engine.contains("mysql"))
						{
							String[] ipsplit = String.valueOf(ip).split(".");
							if (!MySQLConnection.sql("SELECT * FROM ip_bans WHERE ip1 = '" + ipsplit[1] + "' AND ip2 = '" + ipsplit[2] + "' AND ip3 = '" + ipsplit[3] + "' AND ip4 = '" + ipsplit[4] + ");"))
							{
								player.sendMessage(logPrefix + " The IP '" + ip + "' is not banned!");
								return false;
							}
							MySQLConnection.sql("DELETE FROM IP_TABLE WHERE ip1 = '" + ipsplit[1] + "' AND ip2 = '" + ipsplit[2] + "' AND ip3 = '" + ipsplit[3] + "' AND ip4 = '" + ipsplit[4] + ");");
						}
					}
				}
				else
				{
					player.sendMessage("Correct usage is /aunbanip [ip|name]");
				}
			}
			else
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getName() + " tried to use command /" + command + "! Denied access.");
			}
			return true;
		}
		return true;
	}
}
