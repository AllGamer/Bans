package AllGamer.AGBS;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.*;

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
	private final Logger log = Logger.getLogger("Minecraft");
	public static String logPrefix = "[AGBS]";
	private final AGBSPlayerListener playerListener = new AGBSPlayerListener(this);
	//private final AGBSBlockListener blockListener = new AGBSBlockListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	String message = "";
	String reason = "";

	// use latest permissions
	public static PermissionHandler Permissions = null;
	public Configuration config;
	public Configuration configExempt;
	public Configuration configBan;

	private AGBSConfiguration confSetup;

	// in case we need it more than once ;)
	public void configInit()
	{
		getDataFolder().mkdirs();
		config = new Configuration(new File(this.getDataFolder(), "config.yml"));
		configExempt = new Configuration(new File(this.getDataFolder(), "bans.yml"));
		configBan = new Configuration(new File(this.getDataFolder(), "exempt.yml"));
		confSetup = new AGBSConfiguration(this.getDataFolder(), this);

	}
	
	// check to see if we have permissions, or fake permissions. Disable if we don't...
	public void setupPermissions() 
	{
		Plugin agbs = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (AGBS.Permissions == null) 
		{
			if (agbs != null) {
				this.getServer().getPluginManager().enablePlugin(agbs);
				AGBS.Permissions = ((Permissions) agbs).getHandler();
			}
			else 
			{
				System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " not enabled. Permissions not detected");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public void onEnable() 
	{
		setupPermissions();
		configInit();
		confSetup.setupConfigs();
		registerListeners();
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled!");
		
	}

	public void onDisable() 
	{
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Disabled!");
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
	}
	
	public String make(String[] split, int startingIndex) 
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
			if (p.equals(target)) 
			{
				return true;
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
						server.broadcastMessage("§c" + AGBS.logPrefix + player.getDisplayName() + " has banned " + target.getDisplayName());
						target.kickPlayer("Banned by " + player.getDisplayName() + ". Reason:" + reason);
						reason = "";
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
					player.sendMessage("Correct usage is /aban [target] [reason]");
				}
			} 
			else 
			{
				player.sendMessage("You don't have access to this command.");
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
							server.broadcastMessage("§c[AGBS] " + player.getDisplayName() + " has banned " + target.getDisplayName());
							target.kickPlayer("Banned by " + player.getDisplayName() + ". Reason:" + reason);
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
				}
				return true;
		}
		if (commandLabel.equalsIgnoreCase("check"))
		{
			if (AGBS.Permissions.has(player, "agbs.check") || AGBS.Permissions.has(player, "agbs.*") || AGBS.Permissions.has(player, "*"))
			{
				if (split.length >= 2) 
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
					player.sendMessage("Correct usage is /check [target]");
				}
			} 
			else
			{
				player.sendMessage("You don't have access to this command.");
			}
			return true;
		}
		return true;
	}
}
