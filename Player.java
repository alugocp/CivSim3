package civsim3;

public class Player extends Leader{
	private Player(int x,int y){
		super(x,y);
		Game.player=this;
		cities.get(0).name="Alexandria";
	}
	public static void newPlayer(){
		int x=(int)Math.floor(Math.random()*Game.world.width);
		int y=(int)Math.floor(Math.random()*Game.world.height);
		Tile tile=Game.world.get(x,y);
		while(tile.environment==World.WATER || tile.city!=null){
			x=(int)Math.floor(Math.random()*Game.world.width);
			y=(int)Math.floor(Math.random()*Game.world.height);
			tile=Game.world.get(x,y);
		}
		new Player(x,y);
	}
	public void scrollTo(){
		int[] coords=Game.world.getCoords(x,y);
		Game.game.scrollTo(-coords[0]+Game.game.getWidth()/2,-coords[1]+Game.game.getHeight()/2);
		Game.histo.changeFocus(x,y);
		Game.redraw(false,true,true,false);
	}
	@Override
	public void turn(){
		Game.turn=Game.PLAYER;
		developCities();
		Game.game.updateWants();
		if(Game.enemies.size()==0){
			Game.turn++;
			System.out.println("You've conquered the world!");
		}
		if(cities.size()==0){
			Game.turn++;
			System.out.println("Sorry, but you lost :(");
		}
	}
}
