package ft.mcRisk;

//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
* Sample block listener
* @author Dinnerbone
*/
public class mcrBlockListener extends BlockListener {
    public final mcRisk plugin;

    public mcrBlockListener(final mcRisk plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        /*Block block = event.getBlock();

        if ((block.getType() == Material.SAND) || (block.getType() == Material.GRAVEL)) {
            Block above = block.getFace(BlockFace.UP);
            if (above.getType() == Material.IRON_BLOCK) {
                event.setCancelled(true);
            }
        }
        */
    }

    @Override
    public void onBlockCanBuild(BlockCanBuildEvent event) {
    	/*
        Material mat = event.getMaterial();

        if (mat.equals(Material.CACTUS)) {
            event.setBuildable(true);
        }
        */
    }
}

