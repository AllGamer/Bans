package net.AllGamer.AGBS;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
import org.bukkit.util.config.ConfigurationNode;


// permissions 2.4 imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * AGBS for Bukkit
 *
 * @author AllGamer
 */
public class AGBS extends JavaPlugin 
{
	public final static Logger log = Logger.getLogger("Minecraft");
	public static String logPrefix = "[AGBS]";
	private final AGBSPlayerListener playerListener = new AGBSPlayerListener(this);
	//private final AGBSBlockListener blockListener = new AGBSBlockListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public static String message = "";
	public static String reason = "";
	public static Configuration config;
	public static Configuration configExempt;
	public static Configuration configBan;
	public static Configuration configBanIP;
	private AGBSConfiguration confSetup;
	public static PermissionHandler Permissions = null;
	int count = 0;
	
	heartbeat hb = new heartbeat();
	Thread t = new Thread( hb );

	public void configInit()
	{
		getDataFolder().mkdirs();
		config = new Configuration(new File(this.getDataFolder(), "config.yml"));
		configBan = new Configuration(new File(this.getDataFolder(), "bans.yml"));
		configExempt = new Configuration(new File(this.getDataFolder(), "exempt.yml"));
		configBanIP = new Configuration(new File(this.getDataFolder(), "banIP.yml"));
		confSetup = new AGBSConfiguration(this.getDataFolder(), this);

	}

	public void getSubscriptions()
	{
		config.load();
		Object apikey = config.getProperty("apikey");
		
	
		List<ConfigurationNode> sub = null;
		List<ConfigurationNode> subs = config.getNodeList("subscriptions", sub );

		for (ConfigurationNode s : subs)
		{
			try
			{
				// Construct data
				String data = URLEncoder.encode("subscribe", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
				data += "&" + URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode((String) apikey, "UTF-8");
				
				
				// Send data
				URL url = new URL("http://209.236.124.35/api/api.json");
				java.net.URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) 
				{
					// what to do, what to do, what to do...
				}
				wr.close();
				rd.close();
			} 
			catch (Exception e) 
			{
				log.severe(logPrefix + "an error has occured while obtaining the subscriptions");
				log.severe(logPrefix + " " + e);
			}
		}



	}
	
	public void setupPermissions() 
	{
		Plugin agbs = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (AGBS.Permissions == null) 
		{
			if (agbs != null) {
				this.getServer().getPluginManager().enablePlugin(agbs);
				AGBS.Permissions = ((Permissions) agbs).getHandler();
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
		setupPermissions();
		configInit();
		confSetup.setupConfigs();
		getSubscriptions();
		registerListeners();
		t.start();
		log.info(logPrefix + " version " + this.getDescription().getVersion() + " enabled!");
		
	}

	public void onDisable() 
	{
		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		log.info(logPrefix + " version " + this.getDescription().getVersion() + " disabled!");
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

	public boolean onCommand(CommandSender sender, Command commandArg, String commandLabel, String[] args) 
	{
		Player player = (Player) sender;
		Server server = getServer();
		String command = commandArg.getName().toLowerCase();
		String[] split = args;
		Player[] onlinePlayers = getServer().getOnlinePlayers();

		if (command.equalsIgnoreCase("aban")) 
		{
			if (AGBS.Permissions.has(player, "agbs.ban") || AGBS.Permissions.has(player, "agbs.*") ||  AGBS.Permissions.has(player, "*")) 
			{
				if (split.length >= 2) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (arraySearch(onlinePlayers, target)) 
					{
						message = make(split, 1);
						message = message.toLowerCase();
						reason = makeReason(message);
						server.broadcastMessage(AGBS.logPrefix + " " + player.getDisplayName() + " has banned " + target.getDisplayName());
						target.kickPlayer("Banned by " + player.getDisplayName() + ". Reason:" + reason);
						configBan.setProperty("banned", target);
						reason = "";
						AGBS.configBan.load();
						AGBS.configBan.setProperty("banned", target.getDisplayName().toLowerCase());
						AGBS.configBan.save();
						// TODO: code for sending ban info to the api 

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
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access." );
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("abanip")) 
		{
			if (AGBS.Permissions.has(player, "agbs.banip") || AGBS.Permissions.has(player, "agbs.*") || AGBS.Permissions.has(player, "*")) 
			{
				if (split.length >= 2) 
				{
					Player target = getServer().getPlayer(split[0]);	
					if (arraySearch(onlinePlayers, target)) 
					{
						message = make(split, 1);
						message = message.toLowerCase();
						reason = makeReason(message);
						server.broadcastMessage(AGBS.logPrefix + " " + player.getDisplayName() + " has banned " + target.getDisplayName() + ".");
						target.kickPlayer("Banned by " + player.getDisplayName() + ". Reason:" + reason);
						configBan.setProperty("banned", target.getDisplayName().toLowerCase());
						reason = "";
						// TODO: code for adding banned name to flatfile/sqlite/mysql here



						// TODO: code for sending ban info to the api
						// Discuss: should this be done once to clean up code or every time we call it? 

					} 
					else 
					{
						player.sendMessage("Cannot find the specified player! Check your spelling again.");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /abanip [target] [reason]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access." );
			}
			return true;
		}
		if (command.equalsIgnoreCase("aexempt")) 
		{
			if (AGBS.Permissions.has(player, "agbs.exempt") || AGBS.Permissions.has(player, "agbs.*") ||  AGBS.Permissions.has(player, "*")) 
			{
				if (split.length >= 2) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (arraySearch(onlinePlayers, target)) 
					{
						for (Player p: onlinePlayers) { 
							if (AGBS.Permissions.has(p, "agbs.notify.*") || AGBS.Permissions.has(p, "agbs.*") || AGBS.Permissions.has(p, "*") || AGBS.Permissions.has(p, "agbs.notify.exempt")) {
								p.sendMessage(ChatColor.RED + AGBS.logPrefix + " " + player.getDisplayName() + " has exempted " + target.getDisplayName() + ".");
							}
						}
						server.broadcastMessage(AGBS.logPrefix + " " + player.getDisplayName() + " has exempted " + target.getDisplayName() + ".");
						configExempt.setProperty("exempt", target.getDisplayName().toLowerCase());
						// TODO: code for adding banned name to flatfile/sqlite/mysql here



						// TODO: code for sending ban info to the api
						// TODO: Discuss: should this be done once to clean up code maybe with the syntax banPlayer( target, reason, apikey); ? 

					} 
					else 
					{
						player.sendMessage("Cannot find the specified player! Check your spelling again.");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /aexempt [target]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access." );
			}
			return true;
		}
		if (command.equalsIgnoreCase("aunban")) 
		{
			if (AGBS.Permissions.has(player, "agbs.unban") || AGBS.Permissions.has(player, "agbs.*") ||  AGBS.Permissions.has(player, "*")) 
			{
				if (split.length >= 2) 
				{
					Player target = getServer().getPlayer(split[0]);
					if (arraySearch(onlinePlayers, target)) 
					{
						server.broadcastMessage(AGBS.logPrefix + " " + player.getDisplayName() + " has unbanned " + target.getDisplayName() + ".");
						configBan.removeProperty("banned." + target.getDisplayName().toLowerCase());
						// TODO: code for adding banned name to flatfile/sqlite/mysql here



						// TODO: code for sending ban info to the api
						// TODO: Discuss: should this be done once to clean up code maybe with the syntax unbanPlayer( target, apikey ); ? 

					} 
					else 
					{
						player.sendMessage("Cannot find the specified player! Check your spelling again.");
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
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access." );
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("acheck"))
		{
			if (AGBS.Permissions.has(player, "agbs.check") || AGBS.Permissions.has(player, "agbs.*") || AGBS.Permissions.has(player, "*"))
			{
				if (split.length == 2) 
				{
					Player target = getServer().getPlayer(split[0]);	
					if (arraySearch(onlinePlayers, target)) 
					{
						// TODO: code for checking target name against api here


					}
					else 
					{
						player.sendMessage("Cannot find the specified player! Check your spelling again.");
					}
				} 
				else 
				{
					player.sendMessage("Correct usage is /acheck [target]");
				}
			} 
			else
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access." );
			}
			return true;
		}
		if (commandLabel.equalsIgnoreCase("aunbanip"))
		{
			if (AGBS.Permissions.has(player, "agbs.unbanip") || AGBS.Permissions.has(player, "agbs.*") || AGBS.Permissions.has(player, "*"))
			{
				if (split.length == 2) 
				{
					int ip = 0;
					String name = null;
					try 
					{
						ip = Integer.parseInt(split[0]);
					} catch (NumberFormatException nfe)
					{
						name = split[0];
					}
					if (ip == 0)
					{
						AGBS.configBanIP.removeProperty("banned." + name.toLowerCase());
					} else {
						AGBS.configBanIP.removeProperty("banned." + ip);
					}
				}
				else
				{
					player.sendMessage(AGBS.logPrefix + " Correct usage is /aunbanip [ip|name]");
				}
			}
			else
			{
				player.sendMessage("You don't have access to this command.");
				log.info(logPrefix + " " + player.getDisplayName() + " tried to use command " + command + "! Denied access.");
			}
			return true;
		}

		return true;
	}
}
