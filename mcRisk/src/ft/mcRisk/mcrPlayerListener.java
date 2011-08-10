
package ft.mcRisk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

/**
* Handle events for all Player related events
* @author Dinnerbone
*/
public class mcrPlayerListener extends PlayerListener {
    private final mcRisk plugin;

    public mcrPlayerListener(mcRisk instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println(event.getPlayer().getName() + " joined the server! :D");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        System.out.println(event.getPlayer().getName() + " - interaction");
        ItemStack heldItem = event.getPlayer().getItemInHand();
        if((heldItem.getType() == Material.GOLD_SWORD) ||
        		(heldItem.getType() == Material.STONE_SWORD)||
        		(heldItem.getType() == Material.DIAMOND_SWORD)||
        		(heldItem.getType() == Material.IRON_SWORD)){
        	plugin.PlayerRegionConq(event.getPlayer());
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.isDebugging(event.getPlayer())) {
            Location from = event.getFrom();
            Location to = event.getTo();

            System.out.println(String.format("From %.2f,%.2f,%.2f to %.2f,%.2f,%.2f", from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));
        }
    }
}
