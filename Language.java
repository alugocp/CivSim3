package civsim3;
import java.util.Random;

public class Language {
	/*public static String name(){
		String[] names={
				"Tenochtitlán","Córdova","Washington","London","Rome",
				"Sparta","Athens","Sumer","Akkadia","Cairo",
				"Cuzco","Chichén Itzá","Delhi","Calcutta","Beijing"
		};
		return names[(int)Math.floor(Math.random()*names.length)];
	}*/
	public static String name(String hex){
		Random r=new Random(getSeed(hex));
		return formWord(r.nextInt(8)+3,r.nextInt(100),r.nextInt(100),r.nextInt(100),r.nextInt(100));
	}
	private static String formWord(int avgLen,int dbVow,int dbCon,int startVow,int endVow){
		Random r=new Random();
		String[] cons={"b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z"};
		String[] vowels={"a","e","i","o","u"};
		String name="";
		boolean wasVow=r.nextInt(100)<startVow;
		if(wasVow){
			name+=vowels[r.nextInt(vowels.length)].toUpperCase();
		}else{
			name+=cons[r.nextInt(cons.length)].toUpperCase();
		}
		while(name.length()<avgLen){
			if(wasVow){
				name+=cons[r.nextInt(cons.length)];
				if(r.nextInt(100)<dbCon){
					name+=cons[r.nextInt(cons.length)];
				}
			}else{
				name+=vowels[r.nextInt(vowels.length)];
				if(r.nextInt(100)<dbVow){
					name+=vowels[r.nextInt(vowels.length)];
				}
			}
			wasVow=!wasVow;
		}
		if(!wasVow && r.nextInt(100)<endVow){
			name+=vowels[r.nextInt(vowels.length)];
		}
		return name;
	}
	private static long getSeed(String hex){
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
	}
}
