package civsim3;
import java.awt.Color;

public class Skill {
	String[] resources;
	String name;
	Color color;
	int amount=10;
	public Skill(String name,String color,String...resources){
		this(name,Color.decode(color),resources);
	}
	private Skill(String name,Color color,String...resources){
		this.name=name;
		this.color=color;
		this.resources=resources;
	}
	public Skill copy(){
		Skill s=new Skill(name,color,resources);
		s.amount=amount;
		return s;
	}
	public Skill copy(int amount){
		Skill s=copy();
		s.amount=amount;
		return s;
	}
}
