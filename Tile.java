package civsim3;

public class Tile{
	String resource;
	int[] territory;
	int environment;
	City city;
	public Tile(int environment){
		this.environment=environment;
		if((environment==World.COAST && Math.random()<0.5) || Math.random()<0.25){
			resource=resource(environment);
		}
	}
	public static String resource(int environment){
		String[] r=null;
		if(environment==World.WATER){
			return null;
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
		return r[(int)Math.floor(Math.random()*r.length)];
	}
	public static boolean isFood(String resource){
		String[] food={"Cows","Fruits","Birds","Cactus","Mammoths","Fish","Wild Game"};
		for(int a=0;a<food.length;a++){
			if(food[a]==resource){
				return true;
			}
		}
		return false;
	}
}
