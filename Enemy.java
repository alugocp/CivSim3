package civsim3;
import java.util.ArrayList;

public class Enemy extends Leader{
	private Brain brain=new Brain(this);
	public Enemy(int x,int y){
		super(x,y);
		Game.enemies.add(this);
	}
	public Enemy(City city){
		super(city);
		int i=Game.enemies.indexOf(city.leader);
		if(i==-1){
			i=0;
		}
		Game.enemies.add(i,this);
		city.changeLeader(this);
	}
	@Override
	public void turn(){
		City capital=Game.world.get(x,y).city;
		if(capital==null || capital.leader!=this){
			if(cities.size()>0){
				System.out.println("No capital ("+cities.size()+")");
				while(cities.size()>0){
					cities.get(0).destroy();
				}
			}
		}else{
			developCities();
			think();
		}
	}
	
	// AI
	private void think(){
		ArrayList<City> c=new ArrayList<>();
		c.addAll(cities);
		for(int a=0;a<c.size();a++){
			City city=c.get(a);
			if(city.leader==this){
				brain.setVars(city);
				brain.makeDecision(city);
			}
		}
	}
}
