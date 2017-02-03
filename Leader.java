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
	public Leader(int x,int y){
		id=currentId;
		currentId++;
		this.x=x;
		this.y=y;
		color=getColor();
		new City(this,x,y);
		//Game.leaders.add(this);
	}
	public Leader(City city){
		id=currentId;
		currentId++;
		this.x=city.x;
		this.y=city.y;
		color=getColor();
		//Game.leaders.add(Game.leaders.indexOf(city.leader),this);
		//Game.histo.colors.add(color);
	}
	private Color getColor(){
		int red=(47*(id+y))+(x/2);
		int green=(x+y)*(int)(id/2.0);
		int blue=(22+x+id)*y;
		float[] hsb=new float[3];
		hsb=Color.RGBtoHSB(red%255,green%255,blue%255,hsb);
		return Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
	}
	public abstract void turn();
	protected void developCities(){
		ArrayList<City> c=new ArrayList<>();
		c.addAll(cities);
		for(int a=0;a<c.size();a++){
			City city=c.get(a);
			if(city.leader==this){
				city.removeChaos();
				if(!city.isDead()){
					for(int b=0;b<city.neighbors.size();b++){
						if(city.neighbors.get(b).leader!=this && closeEnoughSize(city.neighbors.get(b).leader)){
							merge(city.neighbors.get(b).leader);
						}
					}
					city.actionable=true;
					city.appeased=first;
					if(first){
						first=false;
					}
					city.procreate();
					city.advanceSkill();
				}
			}
		}
		Game.redraw(true,Game.histo.focus!=null,false,false);
	}
	private boolean closeEnoughSize(Leader leader){
		int diff=(int)Math.abs(cities.size()-leader.cities.size());
		final double percent=0.07;
		return diff<=Math.ceil(cities.size()*percent) || diff<=Math.ceil(leader.cities.size()*percent);
	}
	public void periodOfWarringStates(City rebel){
		City cap=Game.world.get(rebel.leader.x,rebel.leader.y).city;
		if(cap==null || cap.leader!=rebel.leader){
			System.out.println("No capital for rebel!");
		}
		if(cities.size()==0){
			return;
		}
		int n=(int)Math.ceil(cities.size()/10.0);
		City[] capitals=new City[n];
		capitals[0]=rebel;
		for(int a=1;a<n;a++){
			int c=(int)Math.floor(Math.random()*cities.size());
			capitals[a]=cities.get(c);
			new Enemy(cities.get(c));
		}
		ArrayList <City> cityList=new ArrayList<>();
		cityList.addAll(cities);
		cityList.remove(rebel);
		while(cityList.size()>0){
			City c=cityList.get(0);
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
			cityList.remove(c);
		}
	}
	public void merge(Leader leader){
		/*for(int a=0;a<leader.cities.size();a++){
			leader.cities.get(a).changeLeader(this);
		}*/
		while(leader.cities.size()>0){
			leader.cities.get(0).changeLeader(this);
		}
	}
}
