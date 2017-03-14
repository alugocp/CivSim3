package civsim3;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerBrain extends Brain{
	private City c;
	public PlayerBrain(Player p){
		super(p);
	}
	public void getDecision(City city,int action,City target){
		c=city;
		setVars(city);
		try{
			Game.decisions.logDecision(this.city,new int[][][]{neighbors,allies,plots},getOption(action,target));
		}catch(IOException io){
			System.out.println("Error: decision log aborted");
		}
	}
	public void getDecision(City city,int[] target){
		c=city;
		setVars(city);
		try{
			Game.decisions.logDecision(this.city,new int[][][]{neighbors,allies,plots},getOption(target));
		}catch(IOException io){
			System.out.println("Error: decision log aborted");
		}
	}
	
	private int[] getOption(int action,City target){
		if(c==target){
			return new int[]{0,0};
		}
		int a=c.neighbors.indexOf(target);
		if(a==-1){
			ArrayList<City> allies=new ArrayList<>();
			allies.addAll(c.leader.cities);
			allies.remove(c);
			allies.removeAll(c.neighbors);
			a=allies.indexOf(target);
			return new int[]{1+c.neighbors.size()+a,0};
		}
		int dec=0;
		if(action==HistogramPanel.ATTACK){
			dec=1;
		}
		return new int[]{1+a,dec};
	}
	private int[] getOption(int[] target){
		int[][] plots=c.buildingSpots();
		int index=0;
		for(int a=0;a<plots.length;a++){
			if(plots[a][0]==target[0] && plots[a][1]==target[1]){
				index=a;
				break;
			}
		}
		return new int[]{1+neighbors.length+allies.length+index,0};
	}
	
	@Override
	protected void addEffect(Option option,double[][][] synapses,int[] vars){
		// does nothing for optimization
	}
}
