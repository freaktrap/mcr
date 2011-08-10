package ft.mcRisk;

//bukkitmod: conquest
//each chunk is a control element
//placing a certain block-type creates a control center
//RISK type rules?
//each chunk has a chest at (a random height | just below sea level)
//the 'control chest' spawns a certain resource at certain intervals
	//Resource types: diamond, iron, gold, wood, dirt, cobble, coal,
		//redstone, glowdust, netherrack, obsidian, feathers, stone,
		//string, not3paD	not	
//to capture a control chest, players must have the resources to take it over

import java.util.Date;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Location;

/**
* Conquest/Risk plugin for bukkit, allows players to make claims on regions of land
*
* @author FreakTrap
*/
public class mcRisk extends JavaPlugin {
    private final mcrPlayerListener playerListener = new mcrPlayerListener(this);
    private final mcrBlockListener blockListener = new mcrBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final HashMap<Player, Date> riskCooldown = new HashMap<Player, Date>();
    private final HashMap<String, Player> regOwners = new HashMap<String, Player>();
    private final HashMap<String, Integer> regLevels = new HashMap<String, Integer>();
    

    // NOTE: There should be no need to define a constructor any more for more info on moving from
    // the old constructor see:
    // http://forums.bukkit.org/threads/too-long-constructor.5032/

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);

        // Register our commands
        //getCommand("pos").setExecutor(new SamplePosCommand(this));
        //getCommand("debug").setExecutor(new SampleDebugCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    public void PlayerRegionConq(Player actor){
    	//verify player isn't on cooldown
		Date now = new Date();
    	if(!riskCooldown.containsKey(actor)){
    		riskCooldown.put(actor, now);
    	}
    	Date riskReadyAt = riskCooldown.get(actor);
    	if(riskReadyAt.before(now)){
    		//player needs to wait
    		System.out.println("Player "+actor.getDisplayName()+" must wait for risk cooldown.");
    		return;
    	}
    	
    	String reg = LocToRegion(actor.getLocation());
    	Integer regLevel = regLevels.get(reg);
    	Player regOwner = regOwners.get(reg);
    	
    	//players may not interact with public lands
    	if(regLevel < 0){
    		System.out.println("Player "+actor.getDisplayName()+" attempted to capture public region "+reg+".");
    		return;
    	}
    	
    	//players may take over unclaimed regions
    	if(regOwner == null || regLevel == 0){
    		regLevels.remove(reg);
    		regLevels.put(reg, 1);
    		regOwners.remove(reg);
    		regOwners.put(reg, actor);
    		System.out.println("Player "+actor.getDisplayName()+" has claimed the free region of "+reg+".");
    		return;
    	}else{
    		//implies the region is owned AND has a positive region level
    		System.out.println("Player "+actor.getDisplayName()+" has assaulted the region of "+reg+" owned by "+regOwner.getDisplayName()+".");
    		regLevel -= 1;
    		if(regLevel == 0){
    			System.out.println("Region "+reg+" is now free.");
    			regOwners.remove(reg);
    			return;
    		}else{
    			System.out.println("Region "+reg+" has been downgraded to level "+regLevel+".");
    			regLevels.put(reg, regLevel);
    			return;
    		}
    	}
    	//System.out.println("Player "+actor.getDisplayName()+" performed an unhandled conquest.");
    }
    
    public String LocToRegion(Location loc){
    	int regX = (int)loc.getX()/32;
    	int regY = (int)loc.getY()/32;
    	int regZ = (int)loc.getZ()/32;
    	
    	return "("+Integer.toString(regX)+","+Integer.toString(regY)+","+Integer.toString(regZ)+")";
    }
    
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}