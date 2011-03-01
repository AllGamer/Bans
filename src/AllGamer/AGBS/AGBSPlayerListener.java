package AllGamer.AGBS;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
/**
 * Handle events for all Player related events
 * @author AllGamer
 */
public class AGBSPlayerListener extends PlayerListener {
	private final AGBS plugin;

	public AGBSPlayerListener(AGBS instance) {
		plugin = instance;
	}

	public boolean arraySearch(Player[] list, Player target) {
		for (Player p : list)
			if (p.equals(target)) {
				return true;
			}
		return false;
	}

	//Insert Player related code here
	public void onPlayerlogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		//Player login things
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player player = null;
		String[] split = args;
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

		if(sender instanceof Player) {
			player = (Player) sender;
		}
		// /a ban [target]
		if (commandLabel.equals("a")) {
			if (split[0].equalsIgnoreCase("ban")) {
				if (AGBS.Permissions.has(player, "agbs.ban") || AGBS.Permissions.has(player, "agbs.*")) {
					if (split.length == 2) {
						Player target = plugin.getServer().getPlayer(split[1]);
						if (arraySearch(onlinePlayers, target)) {

						} else {
							player.sendMessage("Cannot find the specified player! Check your spelling again.");
						}
					} else {
						player.sendMessage("Correct usage is /a ban [target]");
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

