package ft.mcRisk;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.Material;

public class mcrLootTable {
	private HashMap<Material, Double> LootShares = new HashMap<Material, Double>();
	private Double LootNorm = 0.0;
	
	public mcrLootTable(){
		//building materials
		AddLoot(Material.BRICK, 10.0);
		AddLoot(Material.DIRT, 10.0);
		AddLoot(Material.WOOL, 3.0);
		AddLoot(Material.GLOWSTONE, 3.0);
		
		//ores
		AddLoot(Material.IRON_BLOCK, 3.0);
		AddLoot(Material.DIAMOND_BLOCK, 1.0);
		
		//tools/arms
		AddLoot(Material.GOLD_CHESTPLATE, 1.0);
		
		//etc
		AddLoot(Material.COAL, 3.0);
		AddLoot(Material.TNT, 1.0);
		AddLoot(Material.APPLE, 3.0);
	}
	
	private void AddLoot(Material type, Double prob){
		LootNorm += prob;
		LootShares.put(type, prob);
		
	}
	
	public Material LootRoll(){
		Random lootRoll = new Random();
		Double lootRollVal = lootRoll.nextDouble()*LootNorm;
		
		for(Material mat : LootShares.keySet()){
			lootRollVal -= LootShares.get(mat);
			if(lootRollVal <= 0.00){
				return mat;
			}
		}
		
		return Material.AIR;
	}
}
