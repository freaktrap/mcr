
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
    	boolean playerAuthInteract = plugin.PlayerRegionAuth(event);
    	
    	event.setCancelled(playerAuthInteract);
    }
    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	
        ItemStack heldItem = event.getPlayer().getItemInHand();

        //swords create conquest events
        if((heldItem.getType() == Material.GOLD_SWORD) ||
        		(heldItem.getType() == Material.STONE_SWORD)||
        		(heldItem.getType() == Material.DIAMOND_SWORD)||
        		(heldItem.getType() == Material.IRON_SWORD)||
        		(heldItem.getType() == Material.WOOD_SWORD)){
        	plugin.PlayerRegionConq(event.getPlayer());
        }

        //a pickaxe gives information on the region
        if((heldItem.getType() == Material.WOOD_PICKAXE) ||
        		(heldItem.getType() == Material.STONE_PICKAXE)||
        		(heldItem.getType() == Material.IRON_PICKAXE)||
        		(heldItem.getType() == Material.GOLD_PICKAXE)||
        		(heldItem.getType() == Material.DIAMOND_PICKAXE)){
        	plugin.PlayerRegionInfo(event.getPlayer());
        }
        
        //shovels let players harvest from regions
        
        //axes let players ...
        
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
