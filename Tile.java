package civsim3;
import java.util.ArrayList;
//import java.util.Hashtable;

public class Tile{
	//static Hashtable<String,Integer> resources=new Hashtable<>();
	static String[] resources={
		"Cows","Grass","Copper","Tin",
		"Timber","Fruits","Wild Game",
		"Iron","Marble","Birds","Ruby","Emerald","Sapphire",
		"Glass","Oil","Cactus","Bones",
		"Snow","Furs","Mammoths",
		"Fish","Seashells","Pearls"
	};
	int resource=-1;
	int[] territory;
	int environment;
	City city;
	public Tile(int environment){
		this.environment=environment;
		if((environment==World.COAST && Math.random()<0.5) || Math.random()<0.25){
			resource=resource(environment);
		}
	}
	public static int[][] surroundings(int x,int y){
		ArrayList<int[]> points=new ArrayList<>();
		if(inWorld(x+1,y)){
			points.add(new int[]{x+1,y});
		}
		if(inWorld(x-1,y)){
			points.add(new int[]{x-1,y});
		}
		if(inWorld(x,y+1)){
			points.add(new int[]{x,y+1});
		}
		if(inWorld(x,y-1)){
			points.add(new int[]{x,y-1});
		}
		int side=1;
		if(y%2==0){
			side=-1;
		}
		if(inWorld(x+side,y-1)){
			points.add(new int[]{x+side,y-1});
		}
		if(inWorld(x+side,y+1)){
			points.add(new int[]{x+side,y+1});
		}
		return points.toArray(new int[points.size()][2]);
	}
	public static ArrayList<int[]> twoTileSurroundings(int x,int y){
		ArrayList<int[]> points=new ArrayList<>();
		for(int ox=-1;ox<=1;ox++){
			for(int oy=-2;oy<=2;oy++){
				if(inWorld(x+ox,y+oy)){
					points.add(new int[]{x+ox,y+oy});
				}
			}
		}
		if(inWorld(x-2,y)){
			points.add(new int[]{x-2,y});
		}
		if(inWorld(x+2,y)){
			points.add(new int[]{x+2,y});
		}
		int side=2;
		if(y%2==0){
			side*=-1;
		}
		if(inWorld(x+side,y-1)){
			points.add(new int[]{x+side,y-1});
		}
		if(inWorld(x+side,y+1)){
			points.add(new int[]{x+side,y+1});
		}
		return points;
	}
	public static boolean inWorld(int x,int y){
		return (x>=0 && y>=0 && x<Game.world.width && y<Game.world.height);
	}
	public static int resource(int environment){
		String[] r=null;
		if(environment==World.WATER){
			return -1;
		}
		if(environment==World.GRASS){
			r=new String[]{"Cows","Grass","Copper","Tin"};
		}
		if(environment==World.FOREST){
			r=new String[]{"Timber","Fruits","Wild Game"};
		}
		if(environment==World.MOUNTAIN){
			r=new String[]{"Iron","Marble","Birds","Ruby","Emerald","Sapphire"};
		}
		if(environment==World.DESERT){
			r=new String[]{"Glass","Oil","Cactus","Bones"};
		}
		if(environment==World.SNOW){
			r=new String[]{"Snow","Furs","Mammoths"};
		}
		if(environment==World.COAST){
			r=new String[]{"Fish","Seashells","Pearls"};
		}
		return resIndex(r[(int)Math.floor(Math.random()*r.length)]);
	}
	public static int resIndex(String res){
		for(int a=0;a<resources.length;a++){
			if(resources[a].equals(res)){
				return a;
			}
		}
		//System.out.println("Unknown resource: "+res);
		return Integer.parseInt(res);//-1;
	}
	/*public static int getResIndex(String resource){
		return resources.get(resource);
	}
	public static void setupResources(){
		resources.put("Cows",0);
		resources.put("Grass",1);
		resources.put("Copper",2);
		resources.put("Tin",3);
		resources.put("Timber",4);
		resources.put("Fruits",5);
		resources.put("Wild Game",6);
		resources.put("Iron",7);
		resources.put("Marble",8);
		resources.put("Birds",9);
		resources.put("Ruby",10);
		resources.put("Emerald",11);
		resources.put("Sapphire",12);
		resources.put("Glass",13);
		resources.put("Oil",14);
		resources.put("Cactus",15);
		resources.put("Bones",16);
		resources.put("Snow",17);
		resources.put("Furs",18);
		resources.put("Mammoths",19);
		resources.put("Fish",20);
		resources.put("Seashells",21);
		resources.put("Pearls",22);
	}*/
}
