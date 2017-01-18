package civsim3;
import java.awt.Color;
import java.util.ArrayList;

public abstract class Leader {
	static int currentId=0;
	boolean first=true;
	private int id;
	ArrayList<City> cities=new ArrayList<>();
	Color color;
	int x,y;
	public Leader(int x,int y){// when a new leader is spawned, it needs to add its color to the histogram's list
		id=currentId;
		currentId++;
		this.x=x;
		this.y=y;
		color=getColor();
		new City(this,x,y);
		Game.leaders.add(this);
	}
	public Leader(City city){
		id=currentId;
		currentId++;
		this.x=city.x;
		this.y=city.y;
		color=getColor();
		Game.leaders.add(Game.leaders.indexOf(city.leader),this);
		city.changeLeader(this);
		Game.histo.colors.add(color);
	}
	private Color getColor(){
		int red=(47*id)+(x/2);
		int green=(int)Math.pow(id,3)+y;
		int blue=(120+x+y)*id;
		float[] hsb=new float[3];
		hsb=Color.RGBtoHSB(red%255,green%255,blue%255,hsb);
		return Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
	}
	public abstract void turn();
	protected void developCities(){
		for(int a=0;a<cities.size();a++){
			City city=cities.get(a);
			for(int b=0;b<city.neighbors.size();b++){
				if(city.neighbors.get(b).leader!=this && closeEnoughSize(city.neighbors.get(b).leader)){
					merge(city.neighbors.get(b).leader);
					//break;
				}
			}
			city.appeased=first;
			if(first){
				first=false;
			}
			city.procreate();
			city.advanceSkill();
		}
		Game.redraw(true,Game.histo.focus!=null,false,false);
	}
	private boolean closeEnoughSize(Leader leader){
		int diff=(int)Math.abs(cities.size()-leader.cities.size());
		return diff<=Math.ceil(cities.size()*0.05) || diff<=Math.ceil(leader.cities.size()*0.05);
	}
	public void periodOfWarringStates(City rebel){
		int n=(int)Math.ceil(cities.size()/10.0);
		if(n==0){
			n++;
		}
		City[] capitals=new City[n];
		capitals[0]=rebel;
		for(int a=1;a<n;a++){
			int c=(int)Math.floor(Math.random()*cities.size());
			capitals[a]=cities.get(c);
			new Enemy(cities.get(c));
		}
		while(cities.size()>0){
			City c=cities.get(0);
			int[] cCoords=Game.world.getCoords(c.x,c.y);
			double distance=Game.distance(cCoords,Game.world.getCoords(capitals[0].x,capitals[0].y));
			int index=0;
			for(int a=1;a<capitals.length;a++){
				double dis=Game.distance(cCoords,Game.world.getCoords(capitals[a].x,capitals[a].y));
				if(dis<distance){
					distance=dis;
					index=a;
				}
			}
			c.changeLeader(capitals[index].leader);
		}
	}
	public void merge(Leader leader){
		for(int a=0;a<leader.cities.size();a++){
			leader.cities.get(a).changeLeader(this);
		}
	}
}