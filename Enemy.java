package civsim3;
import java.util.ArrayList;

public class Enemy extends Leader{
	public Enemy(int x,int y){
		super(x,y);
		Game.enemies.add(this);
	}
	public Enemy(City city){
		super(city);
		Game.enemies.add(this);
	}
	@Override
	public void turn(){
		developCities();
		think();
	}
	
	// AI
	// [event type][inputs][value][output]
	private int[][][][] brain={
		{{{-2,2,2,-1},{0,1,2,-1},{2,0,0,0}},{{0,2,0,-2},{0,0,0,0},{0,0,0,0}},{{-2,2,-1,1},{1,1,0,1},{2,0,1,0}},{{2,0,0,-1},{1,0,0,1},{-2,1,0,1}}},
		{{{2,2,2,-2},{1,1,2,-1},{0,1,0,0}},{{0,0,0,0},{0,0,0,0},{0,0,0,0}},{{2,1,-1,-1},{1,1,0,0},{0,2,1,-1}},{{-2,2,0,1},{-1,1,0,0},{2,1,0,-1}},{{1,-1,2,2},{-2,2,0,-2}}},
		{{{1,1},{1,1},{1,1}},{{-2,2},{-1,1},{2,0}}}
	};
	private void think(){
		borderingEnemies();// fucked up
		cityWantsResources();
		whenever();
	}
	private void borderingEnemies(){
		City[][] pairs=getBorderingEnemies();
		for(int a=0;a<pairs.length;a++){
			City[] pair=pairs[a];
			String senses="0";
			// add to sensory stream based on passive senses
			senses+=generalLoyalty(pair[0]);
			senses+=generalLoyalty(pair[1]);
			senses+=generalPop(pair[0]);
			senses+=generalPop(pair[1]);
			// pass sensory stream into neural network to determine outcome
			int action=neuralNetAlg(senses);
			// implement outcome using short-term memory
			if(action==0){
				pair[0].attack(pair[1]);
			}else if(action==1){
				Trade trade=new Trade(pair[0],pair[1]);
				if(trade.possible()){
					trade.randomOffers();
					trade.trade();
				}
			}else if(action==2){
				int[][] spots=pair[0].buildingSpots();
				if(spots.length>0 && pair[0].pop>25){
					int i=(int)Math.floor(Math.random()*spots.length);
					new City(pair[0],spots[i][0],spots[i][1]);
				}
			}
		}
	}
	private void cityWantsResources(){
		CityWant[] wants=getCityWants();
		for(int a=0;a<wants.length;a++){
			for(int b=0;b<wants[a].city.neighbors.size();b++){
				City city=wants[a].city.neighbors.get(b);
				if(city.leader==wants[a].city.leader){
					String senses="1";
					senses+=generalLoyalty(wants[a].city);
					senses+=generalLoyalty(city);
					senses+=generalPop(wants[a].city);
					senses+=generalPop(city);
					senses+=hasResource(city,wants[a].resource);
					int action=neuralNetAlg(senses);
					if(action==0){
						city.attack(wants[a].city);
						break;
					}else if(action==1){
						Trade trade=new Trade(city,wants[a].city);
						if(trade.possible()){
							trade.randomOffers();
							trade.preferFirstOffer(wants[a].resource);
							trade.trade();
							break;
						}
					}else if(action==2){
						int[][] spots=city.buildingSpots();
						if(spots.length>0 && city.pop>25){
							int i=(int)Math.floor(Math.random()*spots.length);
							new City(city,spots[i][0],spots[i][1]);
						}
						break;
					}
				}
			}
		}
	}
	private void whenever(){
		for(int a=0;a<cities.size();a++){
			City city=cities.get(a);
			String senses="2";
			senses+=generalLoyalty(city);
			senses+=generalPop(city);
			int action=neuralNetAlg(senses);
			if(action==0){
				int[][] spots=city.buildingSpots();
				if(spots.length>0 && city.pop>25){
					int i=(int)Math.floor(Math.random()*spots.length);
					new City(city,spots[i][0],spots[i][1]);
				}
			}
		}
	}
	private int neuralNetAlg(String stream){
		int section=Integer.parseInt(stream.substring(0,1));
		int[][][] inputs=brain[section];
		int[] outputs=null;
		for(int a=0;a<inputs.length;a++){
			int value=Integer.parseInt(stream.substring(a+1,a+2));
			if(outputs==null){
				outputs=new int[inputs[a][value].length];
				for(int b=0;b<outputs.length;b++){
					outputs[b]=0;
				}
			}else{
				for(int b=0;b<inputs[a][value].length;b++){
					outputs[b]=inputs[a][value][b];
				}
			}
		}
		int index=0;
		int highest=outputs[0];
		for(int a=1;a<outputs.length;a++){
			if(outputs[a]>highest){
				highest=outputs[a];
				index=a;
			}
		}
		return index;
	}
	
	// event-based senses
	private City[][] getBorderingEnemies(){
		ArrayList<City[]> neighbors=new ArrayList<>();
		for(int a=0;a<cities.size();a++){
			City city=cities.get(a);
			for(int b=0;b<city.neighbors.size();b++){
				if(city.leader!=city.neighbors.get(b).leader){
					neighbors.add(new City[]{city,city.neighbors.get(b)});
				}
			}
		}
		return neighbors.toArray(new City[neighbors.size()][2]);
	}
	private CityWant[] getCityWants(){
		ArrayList<CityWant> wants=new ArrayList<>();
		for(int a=0;a<cities.size();a++){
			City city=cities.get(a);
			for(int b=0;b<city.wants.size();b++){
				wants.add(new CityWant(city,city.wants.get(b)));
			}
		}
		return wants.toArray(new CityWant[wants.size()]);
	}
	private class CityWant{
		String resource;
		City city;
		public CityWant(City city,String resource){
			this.resource=resource;
			this.city=city;
		}
	}
	
	// passive senses
	private char generalLoyalty(City city){
		if(city.loyalty>=4){
			return '2';
		}else if(city.loyalty>=2){
			return '1';
		}
		return '0';
	}
	private char generalPop(City city){
		if(city.pop>150){
			return '2';
		}else if(city.pop>50){
			return '1';
		}
		return '0';
	}
	private char hasResource(City city,String resource){
		if(city.hasResource(resource)){
			return '1';
		}
		return '0';
	}
}