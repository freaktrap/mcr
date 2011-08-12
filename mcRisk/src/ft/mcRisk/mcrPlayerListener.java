
package ft.mcRisk;

//import org.bukkit.Location;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

/**
* Handle events for all Player related events
* @author Dinnerbone
*/
public class mcrPlayerListener extends PlayerListener {
    private final mcRisk plugin;
    
    private final HashMap<String, mcrRegion> playerLastRegionTracks = new HashMap<String, mcrRegion>();

    public mcrPlayerListener(mcRisk instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println(event.getPlayer().getName() + " joined the server! :D");
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
    	//handle conquest interactions
    	ItemStack heldItem = event.getPlayer().getItemInHand();
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            //swords create conquest events
            if((heldItem.getType() == Material.GOLD_SWORD) ||
            		(heldItem.getType() == Material.STONE_SWORD)||
            		(heldItem.getType() == Material.DIAMOND_SWORD)||
            		(heldItem.getType() == Material.IRON_SWORD)||
            		(heldItem.getType() == Material.WOOD_SWORD)){
            	plugin.PlayerRegionConq(event.getPlayer());
            	return;
            }
            //a pickaxe gives information on the region
            if((heldItem.getType() == Material.WOOD_PICKAXE) ||
            		(heldItem.getType() == Material.STONE_PICKAXE)||
            		(heldItem.getType() == Material.IRON_PICKAXE)||
            		(heldItem.getType() == Material.GOLD_PICKAXE)||
            		(heldItem.getType() == Material.DIAMOND_PICKAXE)){
            	plugin.PlayerRegionInfo(event.getPlayer());
            	return;
            }
            //shovels let players harvest from regions
            //axes let players ...
    	}
    	
    	//players wielding a sword will always be able to interact with any targets
        if((heldItem.getType() == Material.GOLD_SWORD) ||
        		(heldItem.getType() == Material.STONE_SWORD)||
        		(heldItem.getType() == Material.DIAMOND_SWORD)||
        		(heldItem.getType() == Material.IRON_SWORD)||
        		(heldItem.getType() == Material.WOOD_SWORD)){
        	return;
        }
    	
    	//for all other interactions...
    	boolean playerAuthInteract = plugin.PlayerRegionAuth(event);
    	
    	event.setCancelled(!playerAuthInteract);
    }
    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
    	mcrRegion regTo = new mcrRegion(event.getTo());
    	String playerName = event.getPlayer().getName();
    	
    	if(!playerLastRegionTracks.containsKey(playerName)){
    		playerLastRegionTracks.put(playerName, regTo);
    	}
    	
    	mcrRegion regFrom = playerLastRegionTracks.get(playerName);
    	
    	if(!regFrom.equals(regTo))
    		//event.setCancelled(true);
    		plugin.AnnounceRegionEntry(regFrom, regTo, event.getPlayer());
    	
    	
		playerLastRegionTracks.put(playerName, regTo);
    }
}
