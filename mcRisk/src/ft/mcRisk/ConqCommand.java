package ft.mcRisk;

//import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.text.Normalizer;
//import java.text.Normalizer.Form;

/**
* Handler for the /pos sample command.
* @author SpaceManiac
*/
public class ConqCommand implements CommandExecutor {
	private final mcRisk plugin;

	public ConqCommand(mcRisk plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player actor = (Player)sender;
		
		// /conq -> display list of sub commands 
		if(split.length == 0){
			actor.sendMessage("Available conquest commands:");
			
			if(actor.isOp()){
				actor.sendMessage("    /conq set [PlayerName] [Level]");
				//actor.sendMessage("        Sets the owner and level of the current region.");
			}
			
			actor.sendMessage("    /conq alias [New Region Name]");
			//actor.sendMessage("        Sets the name of the current region.");
			actor.sendMessage("    /conq take [N]");
			//actor.sendMessage("        Collects up to N upgrades from the current region.");
			actor.sendMessage("    /conq use [N]");
			//actor.sendMessage("        Upgrades or attacks the current region with up to N previously collected upgrades.");
			actor.sendMessage("Enter a command without parameters for more information on its use.");
			
			return true;
		}
		
		String subCmd = split[0];
		
		if(split.length == 1){
			if(subCmd.equalsIgnoreCase("set") && actor.isOp()){
				actor.sendMessage("/conq set [PlayerName] [Level]");
				actor.sendMessage("  - Sets the owner and level of the current region.");
				return true;
			}
			
			if(subCmd.equalsIgnoreCase("alias")){
				actor.sendMessage("/conq alias [New Region Name]");
				actor.sendMessage("  - Sets the name of the current region.");
				return true;
			}
			
			if(subCmd.equalsIgnoreCase("take")){
				actor.sendMessage("/conq take [N]");
				actor.sendMessage("  - Collects up to N upgrades from the current region.");
				return true;
			}
			
			if(subCmd.equalsIgnoreCase("use")){
				actor.sendMessage("/conq use [N]");
				actor.sendMessage("  - Upgrades or attacks the current region with up to N previously collected upgrades.");
				return true;
			}
			
			actor.sendMessage("Command not recognized.");
			return true;
		}
		
		//check for sub commands like
		// /conq set [owner=UserName] [level=N]
			//Allows OPs to assign region owners and levels
		// /conq alias New Region Alias
			//Allows OPs or region owners to set a name for their territory 
		// /conq take N
			//Allows region owners to 'pick-up' region upgrades
		// /conq give [N]
			//allows players to attack/upgrade with all previously picked-up upgrades  
		
		mcrRegion actorReg = new mcrRegion(actor.getLocation());
		
		//set subcommand
		if(subCmd.equalsIgnoreCase("set") && actor.isOp()){
			
		}
		
		//alias subcommand
		if(subCmd.equalsIgnoreCase("alias")){
			String regName = "";
			for(int i = 1; i < split.length; i++){
				if(!regName.equals("")){
					regName += " ";
				}
				regName += split[i];
			}
			
			regName = SanaText(regName);
			
			if(regName.equals("")) return false;
			
			if(actor.isOp()){
				plugin.setRegionAlias(actorReg, regName);
				actor.sendMessage("Successfully aliased "+actorReg.toString()+" as '"+regName+"'.");
			}
			
			if(plugin.chkCanAlias(actorReg, actor.getName())){
				plugin.setRegionAlias(actorReg, regName);
				actor.sendMessage("Successfully aliased "+actorReg.toString()+" as '"+regName+"'.");
				return true;
			}else{
				actor.sendMessage("You do not have permission for this region.");
				return true;
			}
		}

		return false;
	}
	
	private String SanaText(String input){
		String ret = "temp";
		input = input.trim();
		input = Normalizer.normalize(input, Normalizer.Form.NFKC);
		//char inp[] = input.toCharArray();
		
		return ret;
	}
}

