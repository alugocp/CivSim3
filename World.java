package civsim3;
import java.util.Random;
import java.awt.Color;

public class World extends Hex2DArray{
	static final int WATER=0;
	static final int GRASS=1;
	static final int FOREST=2;
	static final int DESERT=3;
	static final int SNOW=4;
	static final int MOUNTAIN=5;
	static final int COAST=6;
	private final Random r=new Random();
	public World(int width,int height){
		super(width,height);
		setDimensions(0,0,Game.xDis(),Game.yDis());
	}
	public void setup(){
		generate();
		terraform();
		Player.newPlayer();
		for(int a=0;a<Game.enemies.size();a++){
			Game.enemies.get(a).cities.get(0).setTerritories();
		}
		Game.player.cities.get(0).setTerritories();
	}
	private void generate(){
		final int[][] seeds=new int[200][3];
		for(int a=0;a<seeds.length;a++){
			int x=r.nextInt(width);
			int y=r.nextInt(height);
			seeds[a][0]=x;
			seeds[a][1]=y;
			int type=WATER;
			if(r.nextInt(100)<chanceGround(x,y)){
				if(y>height*0.85 || y<height*0.15){
					type=SNOW;
				}else{
					type=GRASS;
				}
			}
			seeds[a][2]=type;
			set(x,y,new Tile(type));
		}
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				if(get(x,y)==null){
					double shortest=Game.distance(getCoords(x,y),getCoords(seeds[0][0],seeds[0][1]));
					int index=0;
					for(int a=0;a<seeds.length;a++){
						double distance=Game.distance(getCoords(x,y),getCoords(seeds[a][0],seeds[a][1]));
						if(distance<shortest){
							shortest=distance;
							index=a;
						}
					}
					int type=seeds[index][2];
					if(type!=WATER){
						if(y>height*0.85 || y<height*0.15){
							type=SNOW;
						}else{
							type=GRASS;
						}
						if(x>0 && !isLand(x-1,y)){
							get(x-1,y).environment=COAST;
							if(get(x-1,y).resource!=-1 || Math.random()<0.5){
								get(x-1,y).resource=Tile.resource(COAST);
							}
						}
						if(y>0 && !isLand(x,y-1)){
							get(x,y-1).environment=COAST;
							if(get(x,y-1).resource!=-1 || Math.random()<0.5){
								get(x,y-1).resource=Tile.resource(COAST);
							}
						}
						if(x>0 && y>0 && y%2==0 && !isLand(x-1,y-1)){
							get(x-1,y-1).environment=COAST;
							if(get(x-1,y-1).resource!=-1 || Math.random()<0.5){
								get(x-1,y-1).resource=Tile.resource(COAST);
							}
						}
						if(x<width-1 && y>0 && y%2==1 && !isLand(x+1,y-1)){
							get(x+1,y-1).environment=COAST;
							if(get(x+1,y-1).resource!=-1 || Math.random()<0.5){
								get(x+1,y-1).resource=Tile.resource(COAST);
							}
						}
					}else if((x>0 && isLand(x-1,y)) || (y>0 && isLand(x,y-1)) || (x>0 && y>0 && y%2==0 && isLand(x-1,y-1)) || (x<width-1 && y>0 && y%2==1 && isLand(x+1,y-1))){
						type=COAST;
					}
					set(x,y,new Tile(type));
					if(type!=WATER && Math.random()<0.005){
						new Enemy(x,y);
					}
				}
			}
		}
	}
	private boolean isLand(int x,int y){
		return (get(x,y).environment==GRASS || get(x,y).environment==SNOW);
	}
	private void terraform(){
		for(int a=0;a<25+r.nextInt(10);a++){
			addSeed(r.nextInt(width),r.nextInt(height),FOREST,4+r.nextInt(4),0);
			addSeed(r.nextInt(width),r.nextInt(height/10)+(int)(0.45*height),DESERT,4+r.nextInt(4),0);
			addSeed(r.nextInt(width),r.nextInt(height),MOUNTAIN,4+r.nextInt(4),0);
		}
		for(int a=0;a<15;a++){
			addSeed((width/4)+r.nextInt(width/2),r.nextInt(height),MOUNTAIN,3,0);
			addSeed((width/4)+r.nextInt(width/2),r.nextInt(height/10)+(int)(0.45*height),DESERT,3,0);
			addSeed((width/4)+r.nextInt(width/2),r.nextInt(height),FOREST,3,0);
		}
	}
	private void addSeed(int x,int y,int type,int maxChain,int index){
		Tile t=get(x,y);
		if(t.environment==GRASS){
			t.environment=type;
			if(t.resource!=-1){
				t.resource=Tile.resource(type);
			}
		}
		int[][] adj=getAdjacents(x,y);
		if(index<maxChain){
			for(int a=0;a<adj.length;a++){
				if(get(adj[a][0],adj[a][1]).environment==GRASS){
					addSeed(adj[a][0],adj[a][1],type,maxChain,index+1);
				}
			}
		}
		for(int a=0;a<adj.length;a++){
			Tile tile=get(adj[a][0],adj[a][1]);
			if(tile.environment==GRASS && r.nextInt(10)<10-index){
				tile.environment=type;
				if(tile.resource!=-1){
					tile.resource=Tile.resource(type);
				}
			}
		}
		
	}
	private double chanceGround(int x,int y){
		x=Math.abs((width/2)-x);
		y=Math.abs((height/2)-y);
		double chance=Math.sqrt(Math.pow(x*height,2)+Math.pow(y*width,2))*200;
		chance/=width*height;
		return 100-chance;
	}
	
	public Color getColor(int x,int y){
		Tile tile=get(x,y);
		if(tile.city!=null){
			return tile.city.getColor();
		}else if(tile.territory!=null){
			City c=get(tile.territory[0],tile.territory[1]).city;
			if(c==null){
				tile.territory=null;
			}else{
				return c.getColor();
			}
		}
		return environmentColor(tile.environment);
	}
	public Color environmentColor(int tile){
		if(tile==WATER){
			return Color.BLUE;
		}else if(tile==COAST){
			return new Color(0,191,255);// deep sky blue
		}else if(tile==GRASS){
			return Color.GREEN;
		}else if(tile==DESERT){
			return Color.YELLOW;
		}else if(tile==MOUNTAIN){
			return Color.GRAY;
		}else if(tile==SNOW){
			return Color.WHITE;
		}else if(tile==FOREST){
			return Color.getHSBColor(1.2f,1f,0.5f);// dark green
		}
		return Color.BLACK;
	}
}
