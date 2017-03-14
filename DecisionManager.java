package civsim3;
import java.io.*;

public class DecisionManager {
	private final File folder;
	private int decIndex=0;
	public DecisionManager(String name){
		for(int a=0;a<name.length();a++){
			if(name.substring(a, a+1).equals(" ")){
				name=name.substring(0,a)+name.substring(a+1,a+2).toUpperCase()+name.substring(a+2,name.length());
			}
		}
		folder=new File("../civ3help/civ3brains/"+name+"/");
		folder.mkdirs();
	}
	public void logDecision(int[] city,int[][][] data,int[] decision) throws IOException{
		String name="decision";
		if(decIndex>0){
			name+=decIndex;
		}
		name+=".dec";
		File dec=new File(folder,name);
		dec.createNewFile();
		BufferedWriter writer=new BufferedWriter(new FileWriter(dec));
		writer.write(decision[0]+","+decision[1]);
		writer.newLine();
		writeData(writer,data,city);
		writer.flush();
		writer.close();
		decIndex++;
	}
	private void writeData(BufferedWriter w,int[][][] data,int[] city) throws IOException{
		w.write("[");
		writeCity(w,city);
		w.write(",");
		for(int a=0;a<2;a++){
			w.write("[");
			for(int b=0;b<data[a].length;b++){
				writeCity(w,data[a][b]);
				if(b<data[a].length-1){
					w.write(",");
				}
			}
			w.write("],");
		}
		w.write("[");
		for(int a=0;a<data[2].length;a++){
			writePlot(w,data[2][a]);
			if(a<data[2].length-1){
				w.write(",");
			}
		}
		w.write("]]");
	}
	private void writeCity(BufferedWriter w,int[] city) throws IOException{
		int resLen=Tile.resources.length*2;
		w.write("[");
		for(int a=0;a<resLen;a++){
			w.write("["+city[a]+"],");
		}
		w.write(writeLoop(city,resLen,City.MAX_LOYALTY+1)+",");
		w.write(writeLoop(city,resLen+1,15)+",");
		w.write(writeLoop(city,resLen+2,20)+",");
		w.write(writeLoop(city,resLen+3,20)+",");
		w.write(writeLoop(city,resLen+4,2));
		w.write("]");
	}
	private void writePlot(BufferedWriter w,int[] plot) throws IOException{
		int resLen=Tile.resources.length;
		w.write("[");
		for(int a=0;a<resLen;a++){
			w.write("["+plot[a]+"],");
		}
		w.write(writeLoop(plot,resLen,20));
		w.write("]");
	}
	private String writeLoop(int[] array,int index,int length){
		String s="[";
		for(int a=0;a<length;a++){
			if(a==array[index]){
				s+="1";
			}else{
				s+="0";
			}
			if(a<length-1){
				s+=",";
			}
		}
		return s+"]";
	}
}
