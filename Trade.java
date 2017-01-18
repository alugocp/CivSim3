package civsim3;
import java.util.ArrayList;

public class Trade {
	City city,city1;
	String[] offers,offers1;
	int selected,selected1;
	public Trade(City city,City city1){
		this.city=city;
		this.city1=city1;
		offers=available(city1.wants,city);
		offers1=available(city.wants,city1);
	}
	public boolean possible(){
		return offers.length>0 && offers1.length>0;
	}
	public void randomOffers(){
		selected=(int)Math.floor(Math.random()*offers.length);
		selected1=(int)Math.floor(Math.random()*offers1.length);
	}
	public void preferFirstOffer(String resource){
		for(int a=0;a<offers.length;a++){
			if(offers[a].equals(resource)){
				selected=a;
				return;
			}
		}
	}
	public void trade(){
		if(offers1[selected1]=="People"){
			city.pop+=10;
			city1.pop-=10;
		}else{
			city.getResource(offers1[selected1]);
		}
		if(offers[selected]=="People"){
			city1.pop+=10;
			city.pop-=10;
		}else{
			city1.getResource(offers[selected]);
		}
		if(offers[selected]!="People"){
			if(city.leader==city1.leader){
				city1.changeLoyalty(1);
			}else{
				city1.changeLoyalty(-1);
			}
		}
		city1.appeased=true;
		if(city1.loyalty<=0){
			Leader past=city1.leader;
			city1.changeLeader(city.leader);
			if(past.x==city1.x && past.y==city1.y){
				past.periodOfWarringStates(city1);
			}
		}		
		city.culture.merge(city.pop,city1.culture,city1.pop);
		Game.game.updateWants();
	}
	private String[] available(ArrayList<String> wants,City partner){
		ArrayList<String> w=new ArrayList<String>();
		if(partner.pop>10){
			w.add("People");
		}
		if(wants.size()==0 && city.leader==city1.leader){
			for(int a=0;a<partner.resources.length;a++){
				if(!w.contains(partner.resources[a])){
					w.add(partner.resources[a]);
				}
			}
			return w.toArray(new String[w.size()]);
		}
		for(int a=0;a<wants.size();a++){
			if(!w.contains(wants.get(a)) && partner.hasResource(wants.get(a))){
				w.add(wants.get(a));
			}
		}
		return w.toArray(new String[w.size()]);
	}
}
