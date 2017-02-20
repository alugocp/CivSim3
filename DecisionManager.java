package civsim3;
import java.io.*;

public class DecisionManager {
	private final File folder;
	public DecisionManager(String name){
		for(int a=0;a<name.length();a++){
			if(name.substring(a, a+1).equals(" ")){
				name=name.substring(0,a)+name.substring(a+1,a+2).toUpperCase()+name.substring(a+2,name.length());
			}
		}
		folder=new File("../civ3brains/"+name+"/");
		folder.mkdirs();
	}
}
