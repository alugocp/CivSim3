package civsim3;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Game{
	static DecisionManager decisions;
	static PlayerBrain brain;
	static ArrayList<Enemy> enemies=new ArrayList<>();
	static Skills skills=new Skills();
	static Player player;
	static final double sqrt3=Math.sqrt(3);
	static final int side=27;// works best as multiples of 3
	static World world;
	static GamePanel game;
	static MinimapPanel mini;
	static HistogramPanel histo;
	static CulturePanel culture;
	static boolean firstTurn=true;
	static boolean finished=false;
	static final int PLAYER=-1;
	static final int SIM=0;
	static int mode=PLAYER;
	static int turn;
	private static int t=0;
	public static void main(String[] args){
		System.out.println("Welcome to CivSim 3.0!");
		System.out.println("This program was developed by Alex Lugo");
		System.out.println("To learn more about the developer, please visit https://alugocp.github.io/resume\n");
		
		// determine mode
		System.out.print("Please select either player or sim mode: ");
		Scanner sc=new Scanner(System.in);
		String select=sc.nextLine();
		while(!select.equals("player") && !select.equals("sim")){
			System.out.println("Invalid response, please try again.");
			select=sc.nextLine();
		}
		if(select.equals("player")){
			System.out.print("Enter name for brain labeling: ");
			decisions=new DecisionManager(sc.nextLine());
			
		}else{
			mode=SIM;
		}
		sc.close();
		LoadBrain.setBrain();
		
		// initialize simulation
		System.out.println("Initializing new simulation...");
		if(mode==SIM){
			world=new World(160,90);
		}else{
			world=new World(60,60);
		}
		world.setup();
		new MainFrame();
		player.scrollTo();
		if(mode==PLAYER){
			player.turn();
		}else{
			simCycle();
		}
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
	private static void enemyTurn(){
		ArrayList<Enemy> e=new ArrayList<>();
		e.addAll(enemies);
		for(int a=0;a<e.size();a++){
			turn=a;
			if(e.get(a).cities.size()>0){
				e.get(a).turn();
			}
			if(e.get(a).cities.size()==0){
				enemies.remove(e.get(a));
			}
		}
	}
	public static void gameCycle(){
		enemyTurn();
		histo.logHistogramData();
		player.turn();
		redraw(true,true,true,true);
		firstTurn=false;
	}
	public static void simCycle(){
		player.cities.get(0).destroy();
		while(t<50){
			histo.refreshFocus();
			System.out.println("Year "+t);
			enemyTurn();
			histo.logHistogramData();
			Game.redraw(true,true,true,true);
			t++;
			try{
				Thread.sleep(450);
			}catch(InterruptedException interrupt){
				System.out.println("Whoops! The program terminated early! Maybe a meteor hit the planet?");
				break;
			}
			firstTurn=false;
		}
		System.out.println("All done!");
		finished=true;
	}
}
