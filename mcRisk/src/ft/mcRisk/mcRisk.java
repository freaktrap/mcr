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
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;
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
	
	private final mcrLootTable lootTbl = new mcrLootTable();
	private long ConqDelayTime = 10*1000; //10 seconds

	public void onDisable() {
		// TODO: Place any custom disable code here
		System.out.println("MCR Successfully Disabled");
	}

    public void onEnable() {
        //Register events
    	
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Low, this);
		pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);

		//Register commands
		//getCommand("pos").setExecutor(new SamplePosCommand(this));
		//getCommand("debug").setExecutor(new SampleDebugCommand(this));

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
		Material lootTest = null;
		lootTest = lootTbl.LootRoll();System.out.println("Test Roll: "+lootTest.name());
		lootTest = lootTbl.LootRoll();System.out.println("Test Roll: "+lootTest.name());
		lootTest = lootTbl.LootRoll();System.out.println("Test Roll: "+lootTest.name());
		lootTest = lootTbl.LootRoll();System.out.println("Test Roll: "+lootTest.name());
		lootTest = lootTbl.LootRoll();System.out.println("Test Roll: "+lootTest.name());
		
	}
    
    public void PlayerLootTick(Player actor){
		//verify player isn't on cooldown
		Date now = new Date();
		if(!riskCooldown.containsKey(actor)){
			riskCooldown.put(actor, new Date(now.getTime() - Timer.ONE_SECOND));
		}
		Date riskReadyAt = riskCooldown.get(actor);
		if(!now.after(riskReadyAt)){
			//player needs to wait
			Integer cdTime = 1 + (int)(riskReadyAt.getTime() - now.getTime())/1000;
			actor.sendMessage("Please wait "+cdTime.toString()+" seconds before attempting to loot.");
			return;
		}
    	
		//spawn one item per owned region
		World plrWorld = actor.getWorld();
		Location plrLocation = actor.getEyeLocation();
		ItemStack itmSpawn = null;
		Material itmLoot = null;
		
		Integer lootN = 0;
		for(String regStr : regOwners.keySet()){
			if(regOwners.get(regStr) == actor){
				itmLoot = lootTbl.LootRoll();
				itmSpawn = new ItemStack(itmLoot, 1);
				
				plrWorld.dropItem(plrLocation, itmSpawn);
				
				lootN++;
			}
		}
		if(itmLoot != null){
			actor.sendMessage("You looted a total of "+ChatColor.GREEN+lootN+ChatColor.WHITE+" items!");
			riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
		}
    }
    
    public void AnnounceRegionEntry(mcrRegion regSrc, mcrRegion regDes, Player actor){
    	//announce when entering terriroty with a different owner
    	String regSrcOwner = "";
    	String regDesOwner = "";
    	Integer regSrcLevel = regLevels.get(regSrc.toString());
    	Integer regDesLevel = regLevels.get(regDes.toString());

    	if(regSrcLevel == null) regSrcLevel = 0;
    	if(regDesLevel == null) regDesLevel = 0;
    	
    	if(regOwners.containsKey(regSrc.toString()))
    		regSrcOwner = regOwners.get(regSrc.toString()).getName();
    	else if(regSrcLevel < 0)
    		regSrcOwner = "public";
    	else
    		regSrcOwner = "unclaimed";
    		
    	if(regOwners.containsKey(regDes.toString())){
    		regDesOwner = regOwners.get(regDes.toString()).getName();
    	}else if(regDesLevel < 0){
    		regDesOwner = "public";
    	}else{
    		regDesOwner = "unclaimed";
    	}
    	
    	if(regDesOwner != regSrcOwner){
    		if(regDesOwner == "public")
    			actor.sendMessage("You have entered protected territory.");
    		else if(regDesOwner == "unclaimed")
    			actor.sendMessage("You have entered unclaimed territory.");
    		else
    			actor.sendMessage("You have entered the territory of "+regDesOwner+".");
    		
    	}
    }
	
    public boolean PlayerRegionAuth(PlayerInteractEvent event){
    	//Disallow player interactions for damaging blocks
    		//in regions level 10+ owned by other players
    		//or in 'public' regions
    	
    	if(event.getClickedBlock() == null){ return true; }
		mcrRegion region = new mcrRegion(event.getClickedBlock().getLocation());
		Integer regLevel = regLevels.get(region.toString());

		//unowned regions
		if(regLevel == null){
			return true;
		}else if(regLevel == 0){
			return true;
		}

		//public regions
		if(regLevel < 0){
			event.getPlayer().sendMessage("You may not interact with public regions.");
			return false;
		}
		
		if(regLevel > 10){
			String regOwner = regOwners.get(region.toString()).getName();
			if(regOwner == event.getPlayer().getName()){
				return true;
			}else{
				event.getPlayer().sendMessage("This region is too high level to interact with.");
				return false;
			}
		}
    	
    	return true;
    }
    
	public void PlayerRegionInfo(Player actor){
		mcrRegion region = new mcrRegion(actor.getLocation());
		
		Integer regLevel = regLevels.get(region.toString());
		Player regOwner = regOwners.get(region.toString());
		
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
			regOwnerName = regOwner.getName();
			
		}
		
		actor.sendMessage(""+region.toString()+" Owner:"+regOwnerName+", Level:"+regLevel);
	}
	
	private void PlayerConqEffect(Player actor){
		//Snowball nSnowball = null;
		mcrRegion region = new mcrRegion(actor.getLocation());
		
		Location regionBase = new Location(actor.getWorld(),
				region.BaseVector().getX(),
				//region.BaseVector().getY(),
				actor.getEyeLocation().getY(),
				region.BaseVector().getZ());
		Location tempLoc = null;
		
		//System.out.println("Snowballs for player in reg"+region.toString());
		//System.out.println("Bounds:");
		//System.out.println("	"+regionBase.toString());
		//System.out.println("	"+regionBase.add(32.0,0.0,32.0).toString());
		
		ArrayList<Snowball> regEffect = new ArrayList<Snowball>();
		//Location effSpLoc = null;
		Vector effVeloc = new Vector(0.0,+0.5,0.0);
		
		for(int i = 0; i <= 8; i++){
			for(int j = 0; j <= 8; j++){
				/*
				effSpLoc = new Location(actor.getWorld(),
						(float)((int)actor.getLocation().getX()/32)*32.0 + i*4.0,
						(float)actor.getLocation().getY() + 1.0,
						(float)((int)actor.getLocation().getZ()/32)*32.0 + j*4.0);
				*/
				//System.out.println(""+effSpLoc.toString());
				tempLoc = regionBase.clone();
				tempLoc = tempLoc.add(4.00*(float)i,0.00,4.00*(float)j);
				
				regEffect.add(i*9+j, actor.getWorld().spawn(tempLoc, Snowball.class));

				//System.out.print("["+i+","+j+","+tempLoc.getX()+","+tempLoc.getZ()+"]");
				//System.out.print("[base:"+regionBase.getX()+","+regionBase.getZ()+"]");
				
				regEffect.get(i*9+j).setVelocity(effVeloc);
			}
		}
		
		/*
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
		 */
		
	}
	
	public void PlayerRegionConq(Player actor){
		//verify player isn't on cooldown
		Date now = new Date();
		if(!riskCooldown.containsKey(actor)){
			riskCooldown.put(actor, new Date(now.getTime() - Timer.ONE_SECOND));
		}
		Date riskReadyAt = riskCooldown.get(actor);
		if(!now.after(riskReadyAt)){
			//player needs to wait
			Integer cdTime = 1 + (int)(riskReadyAt.getTime() - now.getTime())/1000;
			actor.sendMessage("Please wait "+cdTime.toString()+" seconds before attempting another conquest.");
			//System.out.println("Player "+actor.getName()+" must wait for risk cooldown.");
			return;
		}
		
		mcrRegion region = new mcrRegion(actor.getLocation());
		
		Integer regLevel = regLevels.get(region.toString());
		Player regOwner = regOwners.get(region.toString());
		
		if(regLevel == null){
			regLevel = 0;
		}
		
		//players may not interact with public lands
		if(regLevel < 0){
			actor.sendMessage("Sorry, "+region.toString()+" is public land.");
			return;
		}
		
		//players may take over unclaimed regions
		if(regOwner == null || regLevel == 0){
			regLevels.remove(region.toString());
			regLevels.put(region.toString(), 1);
			
			regOwners.remove(region.toString());
			regOwners.put(region.toString(), actor);
			
			riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
			
			PlayerConqEffect(actor);
			
			actor.sendMessage("You now own "+region.toString()+"! (Level 1)!");
			
			return;
		}else{
			if(regOwner.equals(actor)){
				regLevel += 1;
				
				regLevels.put(region.toString(), regLevel);
				
				riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
				
				PlayerConqEffect(actor);
				
				actor.sendMessage("You have upgraded "+region.toString()+"! (Level "+regLevel+")");
			}else{
				//System.out.println("Player "+actor.getName()+" has assaulted the region of "+reg+" owned by "+regOwner.getName()+".");
				
				regLevel -= 1;
				
				if(regLevel == 0){
					PlayerConqEffect(actor);
					
					actor.sendMessage("You have assaulted "+region.toString()+"! It is now available for capture.");
					regOwner.sendMessage(region.toString()+" has been lost! ");
					
					regOwners.remove(region.toString());
					riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
					
					return;
				}else{
					PlayerConqEffect(actor);
					
					//System.out.println("Region "+region.toString()+" has been downgraded to level "+regLevel+".");
					actor.sendMessage("You have assaulted "+region.toString()+"! (Now Level "+regLevel+")");
					regOwner.sendMessage(region.toString()+" has been attacked! ");
					
					regLevels.put(region.toString(), regLevel);
					riskCooldown.put(actor, new Date(now.getTime() + ConqDelayTime));
					
					return;
				}
			}
		}
		//System.out.println("Player "+actor.getName()+" performed an unhandled conquest.");
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