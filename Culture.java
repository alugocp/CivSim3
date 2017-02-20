package civsim3;
import java.util.ArrayList;
//import java.util.Random;
import java.awt.Color;

public class Culture {
	static final int MAX_SKILLS=10;
	ArrayList<Skill> skills=new ArrayList<>();
	private Language language=new Language();
	private Color color=cultureColor();

	public Culture copy(){
		Culture c=new Culture();
		c.skills=new ArrayList<>();
		for(int a=0;a<skills.size();a++){
			c.skills.add(skills.get(a).copy());
		}
		c.color=cultureColor();
		c.language=language;
		return c;
	}
	
	// development
	public void upgrade(String skill,Integer amount){
		for(int a=0;a<skills.size();a++){
			if(skills.get(a).name.equals(skill)){
				int level=skills.get(a).amount+amount;
				if(level>30){
					level=30;
				}
				skills.get(a).amount=level;
				color=cultureColor();
				return;
			}
		}
		if(amount==10 || skills.size()<=MAX_SKILLS/2){
			skills.add(Game.skills.getSkill(skill).copy(10));
		}
		color=cultureColor();
	}
	public void upgrade(String skill){
		upgrade(skill,10);
	}
	public void merge(double pop,Culture culture,double theirPop){
		for(int a=0;a<culture.skills.size();a++){
			double ratio=theirPop/pop;
			upgrade(culture.skills.get(a).name,(int)(culture.skills.get(a).amount*ratio));
		}
	}
	/*public void alterLanguage(long s){
		language=language.altered(s);
		color=cultureColor();
	}*/
	/*public void mergeLanguage(double pop,Culture culture,double theirPop){
		Color color=Color.decode(langHex);
		Color color1=Color.decode(culture.langHex);
		double ratio=theirPop/pop;
		int red=color.getRed()+(int)(ratio*color1.getRed());
		while(red<0){
			red+=256;
		}
		int blue=color.getBlue()+(int)(ratio*color1.getBlue());
		while(blue<0){
			blue+=256;
		}
		int green=color.getGreen()+(int)(ratio*color1.getGreen());
		while(green<0){
			green+=256;
		}
		langHex="#"+Integer.toHexString(new Color(red%256,green%256,blue%256).getRGB()).substring(2).toUpperCase();
	}*/
	
	// color
	public Color getColor(){
		return color;
	}
	private Color cultureColor(){
		ArrayList<Color> colors=new ArrayList<>();
		for(int a=0;a<skills.size();a++){
			for(int b=0;b<skills.get(a).amount/10;b++){
				colors.add(new Color(skills.get(a).color.getRGB()));
			}
		}
		int red=0;
		int green=0;
		int blue=0;
		for(int a=0;a<colors.size();a++){
			red+=colors.get(a).getRed();
			green+=colors.get(a).getGreen();
			blue+=colors.get(a).getBlue();
		}
		if(colors.size()>0){
			red/=colors.size();
			green/=colors.size();
			blue/=colors.size();
		}
		red*=0.7;
		blue*=0.7;
		green*=0.7;
		double ratio=0.3;
		if(colors.size()==0){
			ratio=1.0;
		}
		red+=language.color.getRed()*ratio;
		green+=language.color.getGreen()*ratio;
		blue+=language.color.getBlue()*ratio;
		return new Color(red%256,green%256,blue%256);
	}
	
	// language
	public void setName(City city){
		city.name=language.formWord();
	}
}
