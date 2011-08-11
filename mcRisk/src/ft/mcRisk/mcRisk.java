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

import javax.management.timer.Timer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;
import java.util.ArrayList;

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
    
    private long ConqDelayTime = 10*1000; //10 seconds

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
    
    public void PlayerRegionInfo(Player actor){
    	String reg = LocToRegion(actor.getLocation());
    	Integer regLevel = regLevels.get(reg);
    	Player regOwner = regOwners.get(reg);
    	String regOwnerName = "undefined";
    	
    	if(regLevel == null){
    		regLevel = 0;
    		regOwnerName = "none";
    	}else if(regOwner == null){
    		if(regLevel < 0){
    			regLevel = -1;
    			regOwnerName = "Server";
    		}else{
        		regLevel = 0;
        		regOwnerName = "none";
    		}
    	}else{
    		regOwnerName = regOwner.getDisplayName();
    		
    	}
    	
    	actor.sendMessage("Region:"+reg+"; Owner:"+regOwnerName+"; Level:"+regLevel);
    }
    
    private void PlayerConqEffect(Player actor){
    	Snowball nSnowball = null;
    	String reg = LocToRegion(actor.getLocation());

		System.out.println("Snowballs for player in reg"+reg+" Loc:"+actor.getLocation().toString());
		System.out.println("Bounds:");
		System.out.println("\t"+((float)((int)actor.getLocation().getX()/32)*32.0)
							+","+((float)((int)actor.getLocation().getX()/32)*32.0 + 8*4.0));
    	
    	ArrayList<Snowball> regEffect = new ArrayList();
    	Location effSpLoc = null;
    	Vector effVeloc = new Vector(0.0,+0.5,0.0);
    	
    	for(int i = 0; i <= 8; i++){
    		for(int j = 0; j <= 8; j++){
    			effSpLoc = new Location(actor.getWorld(),
    					(float)((int)actor.getLocation().getX()/32)*32.0 + i*4.0,
    					(float)actor.getLocation().getY() + 1.0,
    					(float)((int)actor.getLocation().getZ()/32)*32.0 + j*4.0);
    			
    			//System.out.println(""+effSpLoc.toString());
    			
    			regEffect.add(i*9+j, actor.getWorld().spawn(effSpLoc, Snowball.class));
    			regEffect.get(i*9+j).setVelocity(effVeloc);
    		}
    	}
    	
    	Snowball fb = null;
    	
    	Vector fbVeloc = actor.getEyeLocation().getDirection().multiply(1);
    	Vector fbStVec = new Vector(actor.getLocation().getX() + fbVeloc.getX(),
    			actor.getLocation().getY() + fbVeloc.getY(),
    			actor.getLocation().getZ() + fbVeloc.getZ());
    	
    	Location fbStLoc = actor.getLocation();
    	fbStLoc.setX(fbStLoc.getX() + (float)1.0);
    	fbStLoc.setY(fbStLoc.getY() + (float)5.0);
    	fbStLoc.setZ(fbStLoc.getZ() + (float)3.0);
    	
    	fb = actor.getWorld().spawn(fbStLoc, Snowball.class);
    	
    	fb.setVelocity(fbVeloc);
    	//fb.setYield(0);
    	
    	//create 
    	
    }
    
    public void PlayerRegionConq(Player actor){
    	PlayerConqEffect(actor);
    	//verify player isn't on cooldown
		Date now = new Date();
    	if(!riskCooldown.containsKey(actor)){
    		riskCooldown.put(actor, new Date(now.getTime() - Timer.ONE_SECOND));
    	}
    	Date riskReadyAt = riskCooldown.get(actor);
    	if(!now.after(riskReadyAt)){
    		//player needs to wait
    		Integer cdTime = (int)(riskReadyAt.getTime() - now.getTime())/1000;
    		actor.sendMessage("Please wait "+cdTime.toString()+" seconds before attempting another conquest.");
    		//System.out.println("Player "+actor.getDisplayName()+" must wait for risk cooldown.");
    		return;
    	}
    	
    	String reg = LocToRegion(actor.getLocation());
    	Integer regLevel = regLevels.get(reg);
    	Player regOwner = regOwners.get(reg);
    	
    	if(regLevel == null){
	    	regLevel = 0;
    	}
    	
    	//players may not interact with public lands
    	if(regLevel < 0){
    		actor.sendMessage("Sorry, region "+reg+" is public land.");
    		return;
    	}
    	
    	//players may take over unclaimed regions
    	if(regOwner == null || regLevel == 0){
    		regLevels.remove(reg);
    		regLevels.put(reg, 1);
    		regOwners.remove(reg);
    		regOwners.put(reg, actor);
    		riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));

    		actor.sendMessage("You now own region "+reg+" (Level 1)!");
    		
    		World world = actor.getWorld();
    		Snowball snowball;
    		snowball = world.spawn(actor.getLocation(), Snowball.class);
    		
    		return;
    		
    	}else{
    		if(regOwner.equals(actor)){
    			regLevel += 1;
    			
    			regLevels.put(reg, regLevel);
        		riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
    			
        		actor.sendMessage("You have upgraded this region! It is now level "+regLevel+".");
        		
    		}else{
    			//System.out.println("Player "+actor.getDisplayName()+" has assaulted the region of "+reg+" owned by "+regOwner.getDisplayName()+".");
	    		
    			regLevel -= 1;
	    		
	    		if(regLevel == 0){
	        		actor.sendMessage("You have assaulted this region! It is now available for capture.");
	        		
	    			regOwners.remove(reg);
	        		riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
	        		
	    			return;
	    		}else{
	    			//System.out.println("Region "+reg+" has been downgraded to level "+regLevel+".");
	        		actor.sendMessage("You have assaulted this region! It is now level "+regLevel+".");
	        		
	    			regLevels.put(reg, regLevel);
	        		riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
	        		
	    			return;
	    		}
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