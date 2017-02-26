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
		folder=new File("../civ3brains/"+name+"/");
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
		w.write("[[");
		for(int a=0;a<city.length;a++){
			w.write(""+city[a]);
			if(a<city.length-1){
				w.write(",");
			}
		}
		w.write("]");
		for(int a=0;a<data.length;a++){
			w.write(",[");
			for(int b=0;b<data[a].length;b++){
				w.write("[");
				for(int c=0;c<data[a][b].length;c++){
					w.write(""+data[a][b][c]);
					if(c<data[a][b].length-1){
						w.write(",");
					}
				}
				w.write("]");
				if(b<data[a].length-1){
					w.write(",");
				}
			}
			w.write("]");
		}
		w.write("]");
	}
}
