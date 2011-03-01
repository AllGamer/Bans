package AllGamer.AGBS;

import java.io.File;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

// permissions 2.4 imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * AGBS for Bukkit
 *
 * @author AllGamer
 */
@SuppressWarnings("unused")
public class AGBS extends JavaPlugin {
	private final AGBSPlayerListener playerListener = new AGBSPlayerListener(this);
	private final AGBSBlockListener blockListener = new AGBSBlockListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	// use permissions 2.4+
	public static PermissionHandler Permissions = null;


	// check to see if we have permissions, or fake permissions. Disable if we don't...
	public void setupPermissions() {
		Plugin agbs = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();

		if (AGBS.Permissions == null) {
			if (agbs!= null) {
				this.getServer().getPluginManager().enablePlugin(agbs);
				AGBS.Permissions = ((Permissions) agbs).getHandler();
			}
			else {
				System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " not enabled. Permissions not detected");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events

		setupPermissions();

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		{
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		}
	}

	public void onDisable() {
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled!" );
	}
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) 
			return debugees.get(player);

		return false;
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}

