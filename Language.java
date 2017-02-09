package civsim3;
import java.util.Random;
import java.awt.Color;

public class Language extends Object{
	private static final String[] cons={"b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z"};
	private static final String[] vowels={"a","e","i","o","u"};
	private double[] constPercentages=new double[cons.length];
	private double[] vowelPercentages=new double[vowels.length];
	private int avgLen,dbVow,dbCon,startVow,endVow;
	private Random random;
	//private long seed;
	Color color;
	public Language(){/*String hex*/
		changeSeed(System.currentTimeMillis());
	}
	/*private Language(long seed){
		changeSeed(seed);
	}*/
	/*public Language copy(){
		return new Language(seed);
	}*/
	/*public Language altered(long s){
		return new Language(seed+s);
	}*/
	private void changeSeed(long seed){
		//this.seed=seed;
		this.random=new Random(seed);
		avgLen=random.nextInt(8)+3;
		dbVow=random.nextInt(100);
		dbCon=random.nextInt(100);
		startVow=random.nextInt(100);
		endVow=random.nextInt(100);
		setupPercentages();
		color=new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
	}
	private void setupPercentages(){
		double total=0;
		for(int a=0;a<constPercentages.length;a++){
			if(random.nextBoolean()){
				constPercentages[a]=random.nextInt(11);
			}else{
				constPercentages[a]=0;
			}
			total+=constPercentages[a];
		}
		for(int a=0;a<constPercentages.length;a++){
			constPercentages[a]=constPercentages[a]*(100/total);
		}
		total=0;
		for(int a=0;a<vowelPercentages.length;a++){
			vowelPercentages[a]=random.nextInt(11);
			total+=vowelPercentages[a];
		}
		for(int a=0;a<vowelPercentages.length;a++){
			vowelPercentages[a]=vowelPercentages[a]*(100/total);
		}
	}
	public String formWord(){
		Random r=new Random();
		String name="";
		boolean wasVow=r.nextInt(100)<startVow;
		if(wasVow){
			name+=getVowel().toUpperCase();//vowels[r.nextInt(vowels.length)].toUpperCase();
		}else{
			name+=getConst().toUpperCase();//cons[r.nextInt(cons.length)].toUpperCase();
		}
		while(name.length()<avgLen){
			if(wasVow){
				name+=getConst();//cons[r.nextInt(cons.length)];
				if(r.nextInt(100)<dbCon){
					name+=getConst();//cons[r.nextInt(cons.length)];
				}
			}else{
				name+=getVowel();//vowels[r.nextInt(vowels.length)];
				if(r.nextInt(100)<dbVow){
					name+=getVowel();//vowels[r.nextInt(vowels.length)];
				}
			}
			wasVow=!wasVow;
		}
		if(!wasVow && r.nextInt(100)<endVow){
			name+=getVowel();//vowels[r.nextInt(vowels.length)];
		}
		return name;
	}
	private String getConst(){
		double index=Math.random()*100;
		double total=0;
		for(int a=0;a<constPercentages.length;a++){
			total+=constPercentages[a];
			if(total>index){
				return cons[a];
			}
		}
		return cons[cons.length-1];
	}
	private String getVowel(){
		double index=Math.random()*100;
		double total=0;
		for(int a=0;a<vowelPercentages.length;a++){
			total+=vowelPercentages[a];
			if(total>index){
				return vowels[a];
			}
		}
		return vowels[vowels.length-1];
	}
	/*private long getSeed(String hex){
		String[] values={"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		int total=0;
		for(int a=1;a<hex.length();a++){
			int i=0;
			while(!values[i].equals(hex.substring(a,a+1))){
				i++;
			}
			total+=i;
		}
		return (long)(total/6.0);
	}*/
}
