package AllGamer.AGBS;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * AGBS block listener
 * @author AllGamer
 */
public class AGBSBlockListener extends BlockListener {
    private final AGBS plugin;

    public AGBSBlockListener(final AGBS plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
}
