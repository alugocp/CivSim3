package civsim3;
import java.awt.Color;
import java.util.ArrayList;

public class City {
	static final int MAX_LOYALTY=5;
	int x,y,pop,loyalty,wanted;
	//private ArrayList<String> tradedResources=new ArrayList<>();
	ArrayList<City> neighbors=new ArrayList<>();
	Culture culture=new Culture();
	String name;
	String[] interest;//,resources;
	boolean[] resources=new boolean[Tile.resources.length];
	boolean[] wants=new boolean[Tile.resources.length];
	private int age;
	int[][] territories;
	Leader leader;
	boolean appeased=false,actionable=false;
	public City(Leader leader,int x,int y){
		this.leader=leader;
		this.x=x;
		this.y=y;
		pop=75;
		loyalty=MAX_LOYALTY;
		Tile tile=Game.world.get(x,y);
		tile.city=this;
		//tile.resource=null;
		leader.cities.add(this);
		culture.setName(this);
	}
	public City(City founder,int x,int y){
		this(founder.leader,x,y);
		setTerritories();
		culture=founder.culture.copy();
		culture.setName(this);
		if(founder.interest==null || culture.skills.size()>=Culture.MAX_SKILLS){
			interest=null;
		}
		pop=25;
		founder.pop-=25;
	}
	
	// city-to-city interaction
	public void attack(City target){
		if(pop<=15){
			return;
		}
		target.pop-=15;
		if(target.pop<=0){
			if(target.x==target.leader.x && target.y==target.leader.y){
				target.pop=75;
				Leader past=target.leader;
				target.changeLeader(leader);
				past.periodOfWarringStates(target);
			}else{
				target.destroy();
			}
			return;
		}
		pop-=15;
	}
	public int[][] buildingSpots(){
		ArrayList<int[]> building=new ArrayList<>();
		ArrayList<int[]> water=new ArrayList<>();
		for(int a=0;a<territories.length;a++){
			int[][] s=Tile.surroundings(territories[a][0],territories[a][1]);
			for(int b=0;b<s.length;b++){
				Tile tile=Game.world.get(s[b][0],s[b][1]);
				if(tile.environment==World.WATER){
					water.add(new int[]{s[b][0],s[b][1]});
				}else{
					if(tile.territory==null && tile.city==null){
						building.add(new int[]{s[b][0],s[b][1]});
					}
					int[][] t=territories(s[b][0],s[b][1]);
					for(int c=0;c<t.length;c++){
						building.add(new int[]{t[c][0],t[c][1]});
					}
				}
			}
		}
		for(int a=0;a<water.size();a++){
			int[] diff=new int[]{water.get(a)[0]-x,water.get(a)[1]-y};
			int newX=x+(diff[0]*2);
			int newY=y+(diff[1]*2);
			while(Tile.inWorld(newX,newY) && Game.world.get(newX,newY).environment==World.WATER){
				newX+=diff[0];
				newY+=diff[1];
			}
			if(Tile.inWorld(newX,newY)){
				Tile tile=Game.world.get(newX, newY);
				if(tile.city==null && tile.territory==null){
					building.add(new int[]{newX,newY});
				}
			}
		}
		for(int a=0;a<building.size()-1;a++){
			for(int b=a+1;b<building.size();b++){
				if(building.get(b)[0]==building.get(a)[0] && building.get(b)[1]==building.get(a)[1]){
					building.remove(b);
					b--;
				}
			}
		}
		return building.toArray(new int[building.size()][2]);
	}
	
	// resources
	private void setWants(){
		//ArrayList<Integer> w=new ArrayList<>();
		wanted=0;
		wants=new boolean[Tile.resources.length];
		if(interest!=null){
			//System.out.println(interest[interest.length-1]);
			for(int a=1;a<interest.length;a++){
				if(!hasResource(interest[a])){
					//w.add(Integer.parseInt(interest[a]));
					wants[Integer.parseInt(interest[a])]=true;
					wanted++;
				}
			}
		}
		//return w;
	}
	public boolean hasResource(int res){
		/*for(int a=0;a<resources.length;a++){
			if(resources[a].equals(res)){
				return true;
			}
		}
		return tradedResources.contains(res);//false;*/
		return resources[res];
	}
	private boolean hasResource(String res){
		return hasResource(Integer.parseInt(res));
	}
	public void getResource(int res){
		//tradedResources.add(res);
		resources[res]=true;
		if(wants[res]){
			wants[res]=false;
			wanted--;
		}
		//wants.remove(res);
	}
	
	// under-the-hood
	public void changeLeader(Leader l){
		leader.cities.remove(this);
		l.cities.add(this);
		leader=l;
		loyalty=MAX_LOYALTY;
	}
	public void changeLoyalty(int boost){
		loyalty+=boost;
		if(loyalty>MAX_LOYALTY){
			loyalty=MAX_LOYALTY;
		}else if(loyalty<0){
			loyalty=0;
		}
	}
	public void destroy(){
		for(int a=0;a<territories.length;a++){
			Game.world.get(territories[a][0],territories[a][1]).territory=null;
		}
		leader.cities.remove(this);
		Game.world.get(x,y).city=null;
		Game.world.get(x, y).territory=null;
		if(Game.histo.focus==this){
			Game.histo.focus=null;
		}
		for(int a=0;a<neighbors.size();a++){
			neighbors.get(a).neighbors.remove(this);
		}
		age=-1;
	}
	public boolean isDead(){
		return age==-1;
	}
	private int randomEnviron(){
		if(territories.length==0){
			return Game.world.get(x,y).environment;
		}
		int[] coords=territories[(int)Math.floor(Math.random()*territories.length)];
		return Game.world.get(coords[0],coords[1]).environment;
	}
	
	// per-turn
	public void removeChaos(){
		if(neighbors.size()==0){
			return;
		}
		for(int a=0;a<neighbors.size();a++){
			if(neighbors.get(a).leader==leader){
				return;
			}
		}
		destroy();
		if(leader.x==x && leader.y==y && leader.cities.size()>0){
			int index=(int)Math.floor(Math.random()*leader.cities.size());
			City capital=leader.cities.get(index);
			new Enemy(capital);
			leader.periodOfWarringStates(capital);
		}
	}
	public void procreate(){
		pop+=10;//(int)Math.ceil(pop/10.0)*(food-1);
		if(pop>300){
			pop=300;
		}
	}
	public void advanceSkill(){
		if(interest==null){
			age++;
			if(age==10){
				age=0;
				if(culture.skills.size()<Culture.MAX_SKILLS){
					setNewInterest();
				}
			}
		}else{
			for(int a=1;a<interest.length;a++){
				if(!hasResource(interest[a])){
					if(!appeased){
						changeLoyalty(-1);
						if(loyalty<=0){
							rebel();
						}
					}
					return;
				}
			}
			culture.upgrade(interest[0]);
			interest=null;
			//wants.clear();
			wants=new boolean[Tile.resources.length];
		}
	}
	private void setNewInterest(){
		//if(culture.skills.size()<=Math.ceil(Culture.MAX_SKILLS/2)){
			interest=Game.skills.getSkill(randomEnviron());
		/*}else{
			interest=Game.skills.randomSkill();
		}*/
		setWants();
	}
	
	// affiliation/territory
	private void rebel(){
		Leader past=leader;
		new Enemy(this);
		if(past.cities.size()>0){
			if(past.x==x && past.y==y){
				past.periodOfWarringStates(this);
			}else{
				City capital=Game.world.get(past.x,past.y).city;
				if(neighbors.contains(capital)){
					past.periodOfWarringStates(this);
				}else{
					for(int a=0;a<neighbors.size();a++){
						if(neighbors.get(a).leader==past){
							neighbors.get(a).changeLeader(leader);
						}
					}
				}
			}
		}
		if(leader.cities.size()==1){
			destroy();
			//Game.enemies.remove(leader);
		}
	}
	public Color getColor(){
		return leader.color;
	}
	public Color getTerritoryColor(){
		Color color=getColor();
		return new Color(color.getRed(),color.getGreen(),color.getBlue(),127);
	}
	public void setTerritories(){
		territories=territories(x,y);
		//ArrayList<String> r=new ArrayList<>();
		for(int a=0;a<territories.length;a++){
			Tile tile=Game.world.get(territories[a][0],territories[a][1]);
			tile.territory=new int[]{x,y};
			if(tile.resource!=-1){
				//r.add(tile.resource);
				getResource(tile.resource);
			}
			int[][] s=Tile.surroundings(territories[a][0],territories[a][1]);
			for(int b=0;b<s.length;b++){
				Tile t=Game.world.get(s[b][0],s[b][1]);
				if(t.territory!=null){
					City c=Game.world.get(t.territory[0],t.territory[1]).city;
					if(c==null){
						t.territory=null;
					}else if(c!=this && !neighbors.contains(c)){
						neighbors.add(c);
						c.neighbors.add(this);
					}
				}
			}
		}
		//resources=r.toArray(new String[r.size()]);
		interest=Game.skills.getSkill(randomEnviron());
		setWants();
	}
	private int[][] territories(int x,int y){
		ArrayList<int[]> points=Tile.twoTileSurroundings(x,y);
		for(int a=0;a<points.size();a++){
			Tile tile=Game.world.get(points.get(a)[0],points.get(a)[1]);
			if(tile.city!=null || tile.territory!=null || tile.environment==World.WATER){
				points.remove(a);
				a--;
			}
		}
		return points.toArray(new int[points.size()][2]);
	}
}