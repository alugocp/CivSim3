package civsim3;
import java.util.ArrayList;

public abstract class Game{
	static ArrayList<Enemy> enemies=new ArrayList<>();
	static ArrayList<Leader> leaders=new ArrayList<>();
	static Skills skills=new Skills();
	static Player player;
	static final double sqrt3=Math.sqrt(3);
	static final int side=27;// works best as multiples of 3
	static World world;
	static GamePanel game;
	static MinimapPanel mini;
	static HistogramPanel histo;
	static CulturePanel culture;
	static final int PLAYER=-1;
	static int turn;
	public static void main(String[] args){
		world=new World();
		world.setup();
		new MainFrame(1200,600);
		for(int a=0;a<leaders.size();a++){
			histo.colors.add(leaders.get(a).color);
		}
		player.scrollTo();
		player.turn();
	}
	public static double distance(int[] first,int[] second){
		return Math.sqrt(Math.pow(first[0]-second[0],2)+Math.pow(first[1]-second[1],2));
	}
	public static int xDisHalf(){
		return (int)((sqrt3/2.0)*side);
	}
	public static int xDis(){
		return (int)(sqrt3*side);
	}
	public static int yDis(){
		return (int)((3.0/2.0)*side);
	}
	public static void redraw(boolean game,boolean histo,boolean mini,boolean culture){
		if(game){
			Game.game.repaint();
		}
		if(histo){
			Game.histo.repaint();
		}
		if(mini){
			Game.mini.repaint();
		}
		if(culture){
			Game.culture.repaint();
		}
	}
	public static void gameCycle(){
		for(int a=0;a<enemies.size();a++){
			turn=a;
			enemies.get(a).turn();
			if(enemies.get(a).cities.size()==0){
				enemies.remove(a);
				a--;
				// What happens if a leader loses their last city on another leader's turn? Shouldn't affect anything...
			}
		}
		histo.logHistogramData();
		//redraw(true,true,true,true);
		// check for player death
		player.turn();
		redraw(true,true,true,true);
		/*game.repaint();
		mini.repaint();
		if(histo.focus!=null){
			histo.repaint();
		}*/
	}
}
