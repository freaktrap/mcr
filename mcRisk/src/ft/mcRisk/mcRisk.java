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

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
* Sample plugin for Bukkit
*
* @author Dinnerbone
*/
public class mcRisk extends JavaPlugin {
    private final mcrPlayerListener playerListener = new mcrPlayerListener(this);
    private final mcrBlockListener blockListener = new mcrBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

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