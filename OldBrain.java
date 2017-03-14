package civsim3;
import java.util.ArrayList;

public class OldBrain {
	static final int CITY_VAR_LENGTH=(Tile.resources.length*2)+5;
	static final int PLOT_VAR_LENGTH=Tile.resources.length+1;
	private double[][][] citySynapses,neighborSynapses,allySynapses,plotSynapses;
	private final Leader leader;
	protected int[][] neighbors,allies,plots;
	protected int[] city;
	private Option[][] options;
	private Option highest;
	
	public OldBrain(Leader leader){
		randomBrain();
		this.leader=leader;
	}
	
	public void setVars(City city){
		highest=null;
		
		// setup lists
		int[][] plots=city.buildingSpots();
		ArrayList<City> allies=new ArrayList<>();
		allies.addAll(city.leader.cities);
		allies.remove(city);
		Option[][] o=new Option[city.neighbors.size()+1][];
		
		this.city=cityVars(city);
		o[0]=new Option[]{new NothingOption()};
		addEffect(o[0][0],citySynapses[0],this.city);
		
		neighbors=new int[city.neighbors.size()][];
		for(int a=0;a<city.neighbors.size();a++){
			City neighbor=city.neighbors.get(a);
			neighbors[a]=cityVars(neighbor);
			o[1+a]=new Option[]{new TradeOption(neighbor),new AttackOption(neighbor)};
			addEffect(o[1+a][0],citySynapses[1],this.city);
			addEffect(o[1+a][0],neighborSynapses[0],neighbors[a]);
			addEffect(o[1+a][1],citySynapses[2],this.city);
			addEffect(o[1+a][1],neighborSynapses[1],neighbors[a]);
			allies.remove(neighbor);
		}
		
		// transition
		options=new Option[o.length+allies.size()+plots.length][];
		for(int a=0;a<o.length;a++){
			options[a]=o[a];
		}
		o=null;
		
		// finish list setup
		int optionStart=1+neighbors.length;
		this.allies=new int[allies.size()][];
		for(int a=0;a<allies.size();a++){
			this.allies[a]=cityVars(allies.get(a));
			options[optionStart+a]=new Option[]{new TradeOption(allies.get(a))};
			addEffect(options[optionStart+a][0],citySynapses[3],this.city);
			addEffect(options[optionStart+a][0],allySynapses[0],this.allies[a]);
		}
		
		optionStart+=this.allies.length;
		this.plots=new int[plots.length][];
		for(int a=0;a<plots.length;a++){
			this.plots[a]=plotVars(plots[a][0],plots[a][1]);
			options[optionStart+a]=new Option[]{new BuildOption(plots[a][0],plots[a][1])};
			addEffect(options[optionStart+a][0],citySynapses[4],this.city);
			addEffect(options[optionStart+a][0],plotSynapses[0],this.plots[a]);
		}
		
		for(int a=0;a<neighbors.length;a++){
			optionStart=1+neighbors.length;
			for(int b=0;b<this.allies.length;b++){
				addEffect(options[optionStart+b][0],neighborSynapses[2],neighbors[a]);
			}
			optionStart+=this.allies.length;
			for(int b=0;b<this.plots.length;b++){
				addEffect(options[optionStart+b][0],neighborSynapses[3],neighbors[a]);
			}
		}
		optionStart=1+neighbors.length+this.allies.length;
		for(int a=0;a<this.allies.length;a++){
			for(int b=0;b<this.plots.length;b++){
				addEffect(options[optionStart+b][0],allySynapses[1],this.allies[a]);
			}
		}
	}
	
	// information gathering
	private int toInt(boolean b){
		if(b){
			return 1;
		}
		return 0;
	}
	private int popIndex(int pop){
		if(pop<0){
			pop=0;
		}
		if(pop<=100){
			return pop/10;
		}else if(pop<300){
			return ((pop-100)/50)+10;
		}
		return 14;
	}
	private int[] cityVars(City city){
		int[] vars=new int[CITY_VAR_LENGTH];
		for(int a=0;a<city.resources.length;a++){
			vars[a]=toInt(city.resources[a]);
		}
		for(int a=0;a<city.wants.length;a++){
			vars[a+city.resources.length]=toInt(city.wants[a]);
		}
		vars[Tile.resources.length*2]=city.loyalty;
		vars[(Tile.resources.length*2)+1]=popIndex(city.pop);
		int friends=0;
		for(int a=0;a<city.neighbors.size();a++){
			if(city.neighbors.get(a).leader==city.leader){
				friends++;
			}
		}
		vars[(Tile.resources.length*2)+2]=friends;
		vars[(Tile.resources.length*2)+3]=city.neighbors.size()-friends;
		vars[(Tile.resources.length*2)+4]=toInt(city.leader==leader);
		return vars;
	}
	private int[] plotVars(int x,int y){
		int[] vars=new int[PLOT_VAR_LENGTH];
		ArrayList<int[]> terr=Tile.twoTileSurroundings(x,y);
		int open=0;
		for(int a=0;a<terr.size();a++){
			int[] coords=terr.get(a);
			Tile tile=Game.world.get(coords[0],coords[1]);
			if(tile.territory==null){
				open++;
				if(tile.resource!=-1){
					vars[tile.resource]=1;
				}
			}
		}
		vars[Tile.resources.length]=open;
		// bordering cities vars
		return vars;
	}
	
	// brain setup
	private double randomSynapse(){
		double decimal=(2*Math.random())-1;
		return Math.round(decimal*1000)/1000.0;
	}
	private void randomBrain(){
		citySynapses=new double[5][CITY_VAR_LENGTH][];
		citySynapses[0]=cityVarSynapses();
		citySynapses[1]=cityVarSynapses();
		citySynapses[2]=cityVarSynapses();
		citySynapses[3]=cityVarSynapses();
		citySynapses[4]=cityVarSynapses();
		
		neighborSynapses=new double[4][CITY_VAR_LENGTH][];
		neighborSynapses[0]=cityVarSynapses();
		neighborSynapses[1]=cityVarSynapses();
		neighborSynapses[2]=cityVarSynapses();
		neighborSynapses[3]=cityVarSynapses();
		
		allySynapses=new double[2][CITY_VAR_LENGTH][];
		allySynapses[0]=cityVarSynapses();
		allySynapses[1]=cityVarSynapses();
		
		plotSynapses=new double[1][PLOT_VAR_LENGTH][];
		plotSynapses[0]=plotVarSynapses();
	}
	private double[][] cityVarSynapses(){
		double[][] synapses=new double[CITY_VAR_LENGTH][];
		int resLen=Tile.resources.length*2;
		for(int b=0;b<resLen;b++){
			synapses[b]=new double[]{randomSynapse()};
		}
		synapses[resLen]=new double[City.MAX_LOYALTY+1];
		for(int b=0;b<synapses[resLen].length;b++){
			synapses[resLen][b]=randomSynapse();
		}
		synapses[resLen+1]=new double[15];
		for(int b=0;b<synapses[resLen+1].length;b++){
			synapses[resLen+1][b]=randomSynapse();
		}
		synapses[resLen+2]=new double[20];
		synapses[resLen+3]=new double[20];
		for(int b=0;b<synapses[resLen+2].length;b++){
			synapses[resLen+2][b]=randomSynapse();
			synapses[resLen+3][b]=randomSynapse();
		}
		synapses[resLen+4]=new double[]{randomSynapse(),randomSynapse()};
		return synapses;
	}
	private double[][] plotVarSynapses(){
		double[][] synapses=new double[PLOT_VAR_LENGTH][];
		for(int a=0;a<Tile.resources.length;a++){
			synapses[a]=new double[]{randomSynapse()};
		}
		synapses[Tile.resources.length]=new double[20];
		for(int a=0;a<20;a++){
			synapses[Tile.resources.length][a]=randomSynapse();
		}
		return synapses;
	}
	
	// decision-making
	public void makeDecision(City city){
		for(int a=0;a<options.length;a++){
			for(int b=0;b<options[a].length;b++){
				if(highest==null || options[a][b].clout>=highest.clout){
					highest=options[a][b];
				}
			}
		}
		if(highest instanceof AttackOption){
			city.attack(((AttackOption)highest).target);
		}else if(highest instanceof TradeOption){
			Trade trade=new Trade(city,((TradeOption)highest).target);
			if(trade.possible()){
				trade.randomOffers();
				trade.trade();
			}
		}else if(city.pop>25 && highest instanceof BuildOption){
			BuildOption build=(BuildOption)highest;
			new City(city,build.x,build.y);
		}
	}
	protected void addEffect(Option option,double[][] synapses,int[] vars){
		//double effect=0;
		for(int a=0;a<synapses.length;a++){
			if(synapses[a].length==1){
				if(vars[a]==1){
					/*effect*/option.clout+=synapses[a][0];
				}
			}else{
				/*effect*/option.clout+=synapses[a][vars[a]];
			}
		}
		//option.clout+=effect;
	}
	
	protected abstract class Option{
		int clout;
	}
	private class AttackOption extends Option{
		City target;
		public AttackOption(City target){
			this.target=target;
		}
	}
	private class TradeOption extends Option{
		City target;
		public TradeOption(City target){
			this.target=target;
		}
	}
	private class BuildOption extends Option{
		int x,y;
		public BuildOption(int x,int y){
			this.x=x;
			this.y=y;
		}
	}
	private class NothingOption extends Option{}
}
