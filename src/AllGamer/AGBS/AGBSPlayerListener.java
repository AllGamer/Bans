package AllGamer.AGBS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.Server;

/**
 * Handle events for all Player related events
 * @author AllGamer
 */
public class AGBSPlayerListener extends PlayerListener {
	private final AGBS plugin;
	String message;
	String reason = "";

	public AGBSPlayerListener(AGBS instance) {
		plugin = instance;
	}

	public String make(String[] split, int startingIndex) {
		message = "";
		for (; startingIndex < split.length; startingIndex++) {
			if (startingIndex == 1)
				message += "" + split[startingIndex];
			else
				message += " " + split[startingIndex];
		}
		return message;
	}
	
	public boolean arraySearch(Player[] list, Player target) {
		for (Player p : list)
			if (p.equals(target)) {
				return true;
			}
		return false;
	}
	
	public String makeReason(String message) {
		if (message.contains("grf")) {
			reason += " Griefing";
		}
		if (message.contains("rac")) {
			reason += " Racism";
		}
		if (message.contains("hax")) {
			reason += " Hacking";
		}
		if (message.contains("thf")) {
			reason += " Theft";
		}
		if (message.contains("hmo")) {
			reason += " Homophobic Comments";
		}
		if (message.contains("ina")) {
			reason += " Inappropriate Comments";
		}
		if (message.contains("bld")) {
			reason += " Inappropriate Buildings";
		}
		return reason;
	}

	//Insert Player related code here
	public void onPlayerlogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		@SuppressWarnings("unused")
		String playerName = player.getName();
		//TODO: check against ban storage locations... maybe isBanned() should be created?
		
		
		//TODO: check against exempt file
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player player = null;
		Server server = plugin.getServer();
		String[] split = args;
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		player.sendMessage("Debug : " + player + server + split + onlinePlayers);
		if (commandLabel.equals("aban")) {
		//	if (split[0].equalsIgnoreCase("ban")) {
				if (AGBS.Permissions.has(player, "agbs.ban") || AGBS.Permissions.has(player, "agbs.*") ||  AGBS.Permissions.has(player, "*")) {
					if (split.length >= 2) {
						Player target = plugin.getServer().getPlayer(split[1]);
						if (arraySearch(onlinePlayers, target)) {
							message = make(split, 2);
							message = message.toLowerCase();
							reason = makeReason(message);
							target.kickPlayer("Banned by " + player + ". Reason: " + reason);
							reason = "";
							server.broadcastMessage("§c[AGBS]" + sender + " has banned " + target);
							// TODO: code for adding banned name to flatfile/sqlite/mysql here
							
							
							
							// TODO: code for sending ban info to the api
							// TODO: Discuss: should this be done once to clean up code maybe with the syntax banPlayer( target, reason, apikey); ? 
							
						} else {
							player.sendMessage("Cannot find the specified player! Check your spelling again.");
						}
					} else {
						player.sendMessage("Correct usage is /a ban [target] [reason]");
					}
				} else {
					player.sendMessage("You don't have access to this command.");
				}
		//	}
			if (split[0].equalsIgnoreCase("banip")) {
				if (AGBS.Permissions.has(player, "agbs.banip") || AGBS.Permissions.has(player, "agbs.*") || AGBS.Permissions.has(player, "*")) {
					if (split.length >= 2) {
						Player target = plugin.getServer().getPlayer(split[1]);	
						if (arraySearch(onlinePlayers, target)) {
							make(split, 2);
							message = message.toLowerCase();
							reason = makeReason(message);
							target.kickPlayer("Banned by " + player + ". Reason: " + reason);
							reason = "";
							server.broadcastMessage("§c[AGBS]" + sender + " has banned " + target);
							// TODO: code for adding banned name to flatfile/sqlite/mysql here
							
							
							
							// TODO: code for sending ban info to the api
							// Discuss: should this be done once to clean up code or every time we call it? 
							
						} else {
							player.sendMessage("Cannot find the specified player! Check your spelling again.");
						}
					} else {
						player.sendMessage("Correct usage is /a banip [target] [reason]");
					}
				} else {
					player.sendMessage("You don't have access to this command.");
				}
			}
			return true;
		}
		return true;
	}
}

