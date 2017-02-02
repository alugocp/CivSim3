package civsim3;
import java.util.ArrayList;

public class Trade {
	City city,city1;
	Integer[] offers,offers1;
	int selected,selected1;
	public Trade(City city,City city1){
		this.city=city;
		this.city1=city1;
		offers=available(city1.wants,city1.wanted,city);
		offers1=available(city.wants,city.wanted,city1);
	}
	public boolean possible(){
		return offers.length>0 && offers1.length>0;
	}
	public void randomOffers(){
		selected=(int)Math.floor(Math.random()*offers.length);
		selected1=(int)Math.floor(Math.random()*offers1.length);
	}
	public void preferFirstOffer(int resource){
		for(int a=0;a<offers.length;a++){
			if(offers[a]==resource){
				selected=a;
				return;
			}
		}
	}
	public void trade(){
		int item=offers[selected];
		int item1=offers1[selected1];
		if(item1==-1){
			city.pop+=10;
			city1.pop-=10;
		}else{
			city.getResource(item1);
		}
		if(item==-1){
			city1.pop+=10;
			city.pop-=10;
		}else{
			city1.getResource(item);
		}
		if(item!=-1){
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
		//city.culture.mergeLanguage(city.pop,city1.culture,city1.pop);
		Game.game.updateWants();
	}
	private Integer[] available(boolean[] wants,int wanted,City partner){
		ArrayList<Integer> w=new ArrayList<>();
		if(partner.pop>10){
			w.add(-1);
		}
		if(wanted==0 && city.leader==city1.leader){
			for(int a=0;a<partner.resources.length;a++){
				/*if(!w.contains(partner.resources[a])){
					w.add(partner.resources[a]);
				}*/
				if(partner.resources[a]){
					w.add(a);
				}
			}
			return w.toArray(new Integer[w.size()]);
		}
		for(int a=0;a<wants.length;a++){
			if(wants[a] && partner.hasResource(a)){
				w.add(a);
			}
			/*if(!w.contains(wants.get(a)) && partner.hasResource(wants.get(a))){
				w.add(wants.get(a));
			}*/
		}
		return w.toArray(new Integer[w.size()]);
	}
}
