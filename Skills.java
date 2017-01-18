package civsim3;
import java.awt.Color;
//import java.util.ArrayList;

public class Skills {
	/*public String[] getSkill(Integer[] environments){
		ArrayList<String[]> skills=new ArrayList<>();
		if(contains(environments,World.GRASS)){
			skills.add(getSkill("Cow Herding"));
			skills.add(getSkill("Bronze Working"));
		}
		if(contains(environments,World.FOREST)){
			skills.add(getSkill("Writing"));
		}
		if(contains(environments,World.MOUNTAIN)){
			skills.add(getSkill("Mining"));
			skills.add(getSkill("Gem Working"));
		}
		if(contains(environments,World.DESERT)){
			skills.add(getSkill("Glassblowing"));
		}
		if(contains(environments,World.SNOW)){
			skills.add(getSkill("Shivering"));
		}
		if(contains(environments,World.COAST)){
			skills.add(getSkill("Fishing"));
		}
		return skills.get((int)Math.floor(Math.random()*skills.size()));
	}*/
	public String[] getSkill(int environment){
		//return getSkill(new Integer[]{environment});
		String[] skills={""};
		if(environment==World.GRASS){
			skills=new String[]{"Cow Herding","Bronze Working"};
		}
		if(environment==World.FOREST){
			skills=new String[]{"Writing"};
		}
		if(environment==World.MOUNTAIN){
			skills=new String[]{"Mining","Gem Working"};
		}
		if(environment==World.DESERT){
			skills=new String[]{"Glassblowing"};
		}
		if(environment==World.SNOW){
			skills=new String[]{"Shivering"};
		}
		if(environment==World.COAST){
			skills=new String[]{"Fishing","Coastal Art"};
		}
		return getSkill(skills[(int)Math.floor(Math.random()*skills.length)]);
	}
	
	public String[] randomSkill(){
		return getSkill((int)Math.floor(Math.random()*skills.length),true);
	}
	public Color getColor(String name){
		String[] skill=getSkill(name,false);
		return Color.decode(skill[skill.length-1]);
	}
	
	private String[] getSkill(int index,boolean omitHex){
		if(!omitHex){
			return skills[index];
		}
		String[] skill=new String[skills[index].length-1];
		for(int a=0;a<skill.length;a++){
			skill[a]=skills[index][a];
		}
		return skill;
	}
	private String[] getSkill(String name){
		return getSkill(name,true);
	}
	private String[] getSkill(String name,boolean omitHex){
		for(int a=0;a<skills.length;a++){
			if(skills[a][0]==name){
				return getSkill(a,omitHex);
			}
		}
		return null;
	}
	
	/*private boolean contains(Integer[] env,int...check){
		for(int a=0;a<check.length;a++){
			for(int b=0;b<env.length;b++){
				if(env[b]==check[a]){
					break;
				}
				if(b==env.length-1){
					return false;
				}
			}
		}
		return true;
	}*/
	
	private String[][] skills={
			{"Cow Herding","Cows","Grass","#DEB887"},
			{"Writing","Timber","Fruits","#C2B280"},
			{"Mining","Iron","Marble","#4D4D4D"},
			{"Glassblowing","Glass","Oil","#E6E6E6"},
			{"Shivering","Snow","Furs","#FFFFFF"},
			{"Fishing","Fish","Pearls","#0000FF"},
			{"Gem Working","Ruby","Emerald","Sapphire","#B9F2FF"},
			{"Bronze Working","Copper","Tin","#CD7F32"},
			{"Coastal Art","Seashells","Pearls","#7EC0EE"}
	};
}
