package civsim3;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Culture {
	static final int MAX_SKILLS=10;
	ArrayList<String[]> skills=new ArrayList<>();
	private String langHex=language();
	private Color color=cultureColor();
	private Language language=new Language(langHex);
	
	public Culture copy(){
		Culture c=new Culture();
		c.skills=new ArrayList<>();
		c.skills.addAll(skills);
		c.color=cultureColor();
		c.langHex=langHex;//alterLanguage(langHex);
		c.language=language;
		return c;
	}
	
	// development
	public void upgrade(String skill,Integer amount){
		for(int a=0;a<skills.size();a++){
			if(skills.get(a)[0].equals(skill)){
				Integer level=Integer.parseInt(skills.get(a)[1])+amount;
				if(level>30){
					level=30;
				}
				skills.get(a)[1]=level.toString();
				color=cultureColor();
				return;
			}
		}
		if(amount==10 || skills.size()<=MAX_SKILLS/2){
			skills.add(new String[]{skill,amount.toString()});
		}
		color=cultureColor();
	}
	public void upgrade(String skill){
		upgrade(skill,10);
	}
	public void merge(double pop,Culture culture,double theirPop){
		for(int a=0;a<culture.skills.size();a++){
			int skill=Integer.parseInt(culture.skills.get(a)[1]);
			double ratio=theirPop/pop;
			upgrade(culture.skills.get(a)[0],(int)(skill*ratio));
		}
		//mergeLanguage(pop,culture,theirPop);
	}
	private void mergeLanguage(double pop,Culture culture,double theirPop){
		Color color=Color.decode(langHex);
		Color color1=Color.decode(culture.langHex);
		double ratio=theirPop/pop;
		int red=color.getRed()+(int)(ratio*color1.getRed());
		int blue=color.getBlue()+(int)(ratio*color1.getBlue());
		int green=color.getGreen()+(int)(ratio*color1.getGreen());
		langHex="#"+Integer.toHexString(new Color(red%256,green%256,blue%256).getRGB()).substring(2).toUpperCase();
	}
	
	// color
	public Color getColor(){
		return color;
	}
	private Color cultureColor(){
		ArrayList<Color> colors=new ArrayList<>();
		for(int a=0;a<skills.size();a++){
			//System.out.println(skills.get(a)[0]);
			int times=Integer.parseInt(skills.get(a)[1]);
			for(int b=0;b<times/10;b++){
				colors.add(Game.skills.getColor(skills.get(a)[0]));
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
		Color c=Color.decode(langHex);
		red+=c.getRed()*0.3;
		green+=c.getGreen()*0.3;
		blue+=c.getBlue()*0.3;
		/*if(colors.size()>0){
			red/=2;
			blue/=2;
			green/=2;
		}*/
		return new Color(red%256,green%256,blue%256);
	}
	
	// language
	public void setName(City city){
		city.name=language.formWord();
	}
	private String language(){
		Random r=new Random();
		String[] letters={"A","B","C","D","E","F"};
		String l="#";
		for(int a=0;a<6;a++){
			int index=r.nextInt(16);
			if(index>9){
				l+=letters[index-10];
			}else{
				l+=index;
			}
		}
		return l;
	}
	private String alterLanguage(String lang){
		Random r=new Random();
		Color c=Color.decode(lang);
		int red=c.getRed()+r.nextInt(51)-25;
		int green=c.getGreen()+r.nextInt(51)-25;
		int blue=c.getBlue()+r.nextInt(51)-25;
		if(red>255){
			red-=255;
		}
		if(red<0){
			red+=255;
		}
		if(blue>255){
			blue-=255;
		}
		if(blue<0){
			blue+=255;
		}
		if(green>255){
			green-=255;
		}
		if(green<0){
			green+=255;
		}
		c=new Color(red,blue,green);
		return "#"+Integer.toHexString(c.getRGB()).substring(2).toUpperCase();
	}
}
