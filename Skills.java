package civsim3;
//import java.awt.Color;
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
	/*public Skills(){
		setupColors();
	}*/
	public String getEnvSkill(int environment){
		//return getSkill(new Integer[]{environment});
		String[] skills={""};
		if(environment==World.GRASS){
			skills=new String[]{"Cow Herding","Bronze Working","Fertility Religion"};
		}
		if(environment==World.FOREST){
			skills=new String[]{"Writing","Planting","Forestry"};
		}
		if(environment==World.MOUNTAIN){
			skills=new String[]{"Mining","Gem Working"};
		}
		if(environment==World.DESERT){
			skills=new String[]{"Glassblowing"};
		}
		if(environment==World.SNOW){
			skills=new String[]{"Shivering","Skinning"};
		}
		if(environment==World.COAST){
			skills=new String[]{"Fishing","Coastal Art"};
		}
		return skills[(int)Math.floor(Math.random()*skills.length)];//getSkill(skills[(int)Math.floor(Math.random()*skills.length)])[0];
	}
	
	/*public String[] randomSkill(){
		return skills[(int)Math.floor(Math.random()*skills.length)];/*,true*/
	//}
	//public Color getColor(String name){
		/*String[] skill=getSkill(name);//,false
		System.out.print(skill.length+", ");
		System.out.print(skill[0]+", ");
		System.out.println(skill[skill.length-1]);*/
		/*System.out.println(name);
		return skillColors[getSkillIndex(name)];
	}*/
	/*private int getSkillIndex(String name){
		for(int a=0;a<skills.length;a++){
			if(skills[a][0].equals(name)){
				return a;
			}
		}
		return -1;
	}*/
	
	//private String[] getSkill(int index){/*,boolean omitHex*/
		//if(!omitHex){
			//return /*resIndexConvert(*/skills[index];//);
		//}
		/*String[] skill=new String[skills[index].length-1];
		for(int a=0;a<skill.length;a++){
			skill[a]=skills[index][a];
		}
		return skill;*///resIndexConvert(skill);
	//}
	/*private String[] resIndexConvert(String[] skill){
		for(int a=1;a<skill.length;a++){
			if(!skill[a].substring(0,1).equals("#")){
				skill[a]=Integer.toString(Tile.resIndex(skill[a]));
			}
		}
		return skill;
	}*/
	/*public String[] getSkill(String name){
		return getSkill(name,true);
	}*/
	public Skill getSkill(String name){//,boolean omitHex
		for(int a=0;a<skills.length;a++){
			if(skills[a].name.equals(name)){
				return skills[a];//,omitHex
			}
		}
		System.out.println("Null skill :(");
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
	
	// optimize later using static scope and pointers
	/*private String[][] skills={
			{"Cow Herding","Cows","Grass","#00FF00"},//DEB887
			{"Writing","Timber","Fruits","#C2B280"},
			{"Mining","Iron","Marble","#4D4D4D"},
			{"Glassblowing","Glass","Oil","#E6E6E6"},
			{"Shivering","Snow","Furs","#FFFFFF"},
			{"Fishing","Fish","Pearls","#0000FF"},
			{"Gem Working","Ruby","Emerald","Sapphire","#FF0000"},//B9F2FF
			{"Bronze Working","Copper","Tin","#CD7F32"},
			{"Coastal Art","Seashells","Pearls","#7EC0EE"},
			{"Planting","Timber","Fruits","#808000"},
			{"Forestry","Wild Game","Fruits","#228B22"},
			{"Skinning","Mammoths","Furs","#8B4513"},
			{"Fertility Religion","Cows","Copper","#FF69B4"}
	};*/
	private Skill[] skills={
			new Skill("Cow Herding","#00FF00","Cows","Grass"),
			new Skill("Writing","#C2B280","Timber","Fruits"),
			new Skill("Mining","#4D4D4D","Iron","Marble"),
			new Skill("Glassblowing","#E6E6E6","Glass","Oil"),
			new Skill("Shivering","#FFFFFF","Snow","Furs"),
			new Skill("Fishing","#0000FF","Fish","Pearls"),
			new Skill("Gem Working","#FF0000","Ruby","Emerald","Sapphire"),//B9F2FF
			new Skill("Bronze Working","#CD7F32","Copper","Tin"),
			new Skill("Coastal Art","#7EC0EE","Seashells","Pearls"),
			new Skill("Planting","#808000","Timber","Fruits"),
			new Skill("Forestry","#228B22","Wild Game","Fruits"),
			new Skill("Skinning","#8B4513","Mammoths","Furs"),
			new Skill("Fertility Religion","#FF69B4","Cows","Copper")
	};
}
