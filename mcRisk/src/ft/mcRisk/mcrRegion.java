package ft.mcRisk;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class mcrRegion {
	private Integer xLoc,yLoc,zLoc;
	
	public mcrRegion(Location regFromLoc){
		if(regFromLoc.getX() > 0)
			xLoc = (int)Math.ceil(regFromLoc.getX() / 32.0000000);
		else
			xLoc = (int)Math.floor(regFromLoc.getX() / 32.0000000);
		
		if(regFromLoc.getY() > 0)
			yLoc = (int)Math.ceil(regFromLoc.getY() / 32.0000000);
		else
			yLoc = (int)Math.floor(regFromLoc.getY() / 32.0000000);
		
		if(regFromLoc.getZ() > 0)
			zLoc = (int)Math.ceil(regFromLoc.getZ() / 32.0000000);
		else
			zLoc = (int)Math.floor(regFromLoc.getZ() / 32.0000000);
	}
	
	public String toString(){
		return "[Region:"+xLoc+","+yLoc+","+zLoc+"]";
	}
	
	public Vector BaseVector(){
		Vector ret = new Vector(
				32.0*(xLoc - (xLoc>0?1:0)),
				32.0*(yLoc - (yLoc>0?1:0)),
				32.0*(zLoc - (zLoc>0?1:0)));
		//System.out.print("BaseCalc: ["+xLoc+","+ret.getX()+";"+yLoc+","+ret.getY()+";"+zLoc+","+ret.getZ()+"]");
		return ret;
	}
	
	public boolean equals(mcrRegion lhs){
		if(lhs == null) return false;
		
		if(		(lhs.xLoc == xLoc) &&
				(lhs.yLoc == yLoc) &&
				(lhs.zLoc == zLoc))
			return true;
		
		return false;
	}
}
