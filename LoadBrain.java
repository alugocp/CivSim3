package civsim3;
import java.io.*;

public abstract class LoadBrain {
	private static final File file=new File("../civ3help/brains/alexander.brain");
	private static int[] indices;
	private static int scope;
	static double[][][][][] brain;
	public static void setBrain(){
		try{
			brain=getBrain();
		}catch(IOException crashed){
			System.out.println("Couldn't load brain due to I/O reasons");
			System.exit(0);
		}
	}
	private static double[][][][][] getBrain() throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(file));
		double[][][][][] brain=new double[4][][][][];
		brain[0]=new double[][][][]{citySynapses(),citySynapses(),citySynapses(),citySynapses(),citySynapses()};
		brain[1]=new double[][][][]{citySynapses(),citySynapses(),citySynapses(),citySynapses()};
		brain[2]=new double[][][][]{citySynapses(),citySynapses()};
		brain[3]=new double[][][][]{plotSynapses()};
		indices=new int[]{-1,-1,-1,-1,-1};
		scope=-1;
		String line;
		while((line=reader.readLine())!=null){
			if(line.equals("[")){
				changeScope(1);
			}else if(line.equals("]")){
				changeScope(-1);
			}else{
				try{
					brain[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]]=Double.parseDouble(line);
				}catch(ArrayIndexOutOfBoundsException uhOh){
					System.out.println(indices[0]+", "+indices[1]+", "+indices[2]+", "+indices[3]+", "+indices[4]);
					System.exit(0);
				}
				indices[4]++;
			}
		}
		reader.close();
		return brain;
	}
	private static void changeScope(int amount){
		scope+=amount;
		if(scope>=0){
			for(int a=scope;a<indices.length;a++){
				indices[a]=-1;
			}
			indices[scope]++;
		}
	}
	private static double[][][] citySynapses(){
		double[][][] syn=new double[Brain.CITY_VAR_LENGTH][][];
		int resLen=Tile.resources.length*2;
		for(int b=0;b<resLen;b++){
			syn[b]=new double[1][Brain.synSets];
		}
		syn[resLen]=new double[City.MAX_LOYALTY+1][Brain.synSets];
		syn[resLen+1]=new double[15][Brain.synSets];
		syn[resLen+2]=new double[20][Brain.synSets];
		syn[resLen+3]=new double[20][Brain.synSets];
		syn[resLen+4]=new double[2][Brain.synSets];
		return syn;
	}
	private static double[][][] plotSynapses(){
		double[][][] syn=new double[Brain.PLOT_VAR_LENGTH][][];
		
		for(int a=0;a<Tile.resources.length;a++){
			syn[a]=new double[1][Brain.synSets];
		}
		syn[Tile.resources.length]=new double[20][Brain.synSets];
		return syn;
	}
}
