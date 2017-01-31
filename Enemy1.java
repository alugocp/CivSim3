package civsim3;

public class Enemy1 extends Leader{
	public Enemy1(int x,int y){
		super(x,y);
		/*Game.enemies.add(this);
		randomBrain();*/
	}
	public Enemy1(City city){
		super(city);
		int i=Game.enemies.indexOf(city.leader);
		if(i==-1){
			i=0;
		}
		//Game.enemies.add(i,this);
		
		city.changeLeader(this);
		//randomBrain();
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
		for(int a=0;a<cities.size();a++){
			
		}
	}
}
