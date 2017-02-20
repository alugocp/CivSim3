package civsim3;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class HistogramPanel extends JPanel implements MouseListener{
	static final long serialVersionUID=1;
	static final int NONE=0;
	static final int TRADE=1;
	static final int ATTACK=2;
	static final int BUILD=3;
	private BufferedImage image;
	private final Font font;
	private String capital;
	private Leader leader;
	int[][] building={};
	int action=NONE;
	Trade trade;
	City focus;
	public HistogramPanel(int width,int height){
		Game.histo=this;
		setPreferredSize(new Dimension(width/2,height/4));
		font=new Font("serif",20,20);
		addMouseListener(this);
	}
	@Override
	public void paint(Graphics g){
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getWidth(),getHeight());
		if(focus==null){
			if(sizes.size()>0){
				drawHistogram((Graphics2D)g);
			}
		}else{
			drawFocus(g);
		}
	}
	
	// when there's a focus
	public void changeFocus(int x,int y){
		Tile tile=Game.world.get(x, y);
		focus=tile.city;
		trade=null;
		building=new int[][]{};
		action=NONE;
		/*if(focus!=null){
			leader=focus.leader;
			capital=Game.world.get(focus.leader.x,focus.leader.y).city.name;
		}*/
		refreshFocus();
	}
	public void refreshFocus(){
		if(focus!=null && leader!=focus.leader){
			capital=Game.world.get(focus.leader.x,focus.leader.y).city.name;
		}
	}
	private void drawFocus(Graphics g){
		g.setColor(loyaltyColor(focus.loyalty));
		g.fillRect(90,70,focus.loyalty*20,25);
		g.setColor(Color.WHITE);
		g.setFont(font);
		if(focus.isCapital() || focus.leader==Game.player){
			g.drawString("Name: "+focus.name,5,30);
		}else{
			g.drawString("Name: "+focus.name+" of "+capital,5,30);
		}
		g.drawString("Pop: "+focus.pop,5,60);
		g.drawString("Loyalty:",5,90);
		g.drawString(focus.loyalty+"/"+City.MAX_LOYALTY,105,90);
		if(focus.interest!=null && action!=TRADE){
			if(focus.wanting()){
				String w="";
				for(int a=0;a<focus.wants.length;a++){
					if(focus.wants[a]){
						if(w.equals("")){
							w="Wants: "+Tile.resources[a];//focus.wants[a];
						}else{
							w+=", "+Tile.resources[a];//focus.wants[a];
						}
					}
				}
				/*String w="Wants: "+focus.wants.get(0);
				for(int a=1;a<focus.wants.size();a++){
					w+=", "+focus.wants.get(a);
				}*/
				g.drawString(w,5,120);
			}
		}
		if(trade==null){
			drawCityOptions(g);
		}else{
			drawTrade(g);
		}
	}
	private void drawCityOptions(Graphics g){
		if(focus.leader==Game.player && Game.turn==Game.PLAYER){
			if(focus.pop>25){
				g.setColor(Color.BLUE);
				drawButton(g,getWidth()-105,10,100,30);
				g.setColor(Color.WHITE);
				g.drawString("Build",getWidth()-80,35);
			}
			if(focus.neighbors.size()>0 || focus.leader.cities.size()>1){
				g.setColor(Color.GREEN);
				drawButton(g,getWidth()-105,50,100,30);
				g.setColor(Color.WHITE);
				g.drawString("Trade",getWidth()-85,75);
				if(focus.neighbors.size()>0 && focus.pop>15){
					g.setColor(Color.RED);
					drawButton(g,getWidth()-105,90,100,30);
					g.setColor(Color.WHITE);
					g.drawString("Attack",getWidth()-85,115);
				}
			}
		}
	}
	private Color loyaltyColor(int loyalty){
		if(loyalty==5){
			return Color.GREEN;
		}else if(loyalty>2){
			return Color.ORANGE;
		}
		return Color.RED;
	}
	private void drawButton(Graphics g,int x,int y,int width,int height){
		g.fillRect(x+(height/2),y,width-height,height);
		g.fillOval(x,y,height,height);
		g.fillOval(x+width-height,y,height,height);
	}
	
	// histogram drawing
	//ArrayList<Color> colors=new ArrayList<>();
	private ArrayList<int[][]> sizes=new ArrayList<>();
	private int biggest=0;
	private void drawHistogram(Graphics2D g){
		g.scale(getWidth()/(double)sizes.size(),getHeight()/(double)biggest);
		for(int a=0;a<sizes.size();a++){
			int bottom=0;
			for(int b=0;b<sizes.get(a).length;b++){
				int[] data=sizes.get(a)[b];
				//g.setColor(colors.get(data[1]));
				g.setColor(new Color(data[1]));
				g.fillRect(a,biggest-bottom-data[0],1,data[0]);
				bottom+=data[0];
			}
		}
		g.drawImage(image,0,0,getWidth(),getHeight(),null);
	}
	public void logHistogramData(){
		int[][] data=new int[Game.enemies.size()+1][2];
		int totalCities=0;
		data[0][0]=Game.player.cities.size();
		data[0][1]=Game.player.color.getRGB();
		totalCities+=data[0][0];
		for(int a=1;a<data.length;a++){
			data[a][0]=Game.enemies.get(a-1).cities.size();//Game.leaders.get(a).cities.size();
			data[a][1]=Game.enemies.get(a-1).color.getRGB();//colors.indexOf(Game.leaders.get(a).color);
			totalCities+=data[a][0];
		}
		if(totalCities>biggest){
			biggest=totalCities;
		}
		sizes.add(data);
	}
	/*public void copyLastData(int times){
		for(int a=0;a<times;a++){
			sizes.add(sizes.get(sizes.size()-1));
		}
	}*/
	
	// commands
	public boolean hasBuildPoint(int x,int y){
		for(int a=0;a<building.length;a++){
			if(building[a][0]==x && building[a][1]==y){
				return true;
			}
		}
		return false;
	}
	private void drawTrade(Graphics g){
		if(!trade.possible()){
			return;
		}
		for(int a=0;a<trade.offers.length;a++){
			g.setColor(Color.WHITE);
			if(a==trade.selected){
				g.setColor(Color.YELLOW);
			}
			String offer="People";
			if(trade.offers[a]>=0){
				offer=Tile.resources[trade.offers[a]];
			}
			g.drawString(offer,getWidth()/2,(a+1)*25);
		}
		for(int a=0;a<trade.offers1.length;a++){
			g.setColor(Color.WHITE);
			if(a==trade.selected1){
				g.setColor(Color.YELLOW);
			}
			String offer="People";
			if(trade.offers1[a]>=0){
				offer=Tile.resources[trade.offers1[a]];
			}
			g.drawString(offer,(int)(getWidth()*0.75),(a+1)*25);
		}
		if(trade.possible()){
			g.setColor(Color.GREEN);
			drawButton(g,5,110,100,30);
			g.setColor(Color.WHITE);
			g.drawString("Trade",25,133);
		}
	}
	
	// mouse events
	@Override
	public void mouseClicked(MouseEvent event){
		if(Game.turn==Game.PLAYER){
			if(trade==null && event.getX()>=getWidth()-105 && event.getY()<=120){
				int index=(int)Math.floor(event.getY()/40);
				building=new int[][]{};
				if(index==0){
					building=focus.buildingSpots();
					action=BUILD;
				}else{
					if(index==2){
						action=ATTACK;
					}else if(index==1){
						action=TRADE;
						ArrayList<City> allies=focus.leader.cities;
						for(int a=0;a<allies.size();a++){
							if(allies.get(a)!=focus && !focus.neighbors.contains(allies.get(a))){
								focus.neighbors.add(allies.get(a));
							}
						}
					}
					Game.redraw(true,false,false,false);
				}
			}else if(trade!=null){
				if(event.getX()>=getWidth()/2){
					int index=(int)Math.floor(event.getY()/25);
					if(event.getX()<getWidth()*0.75 && index<trade.offers.length){
						trade.selected=index;
					}else if(event.getX()>=getWidth()*0.75 && index<trade.offers1.length){
						trade.selected1=index;
					}
				}else if(event.getX()<=105 && event.getY()>=110){
					trade.trade();
					changeFocus(trade.city1.x,trade.city1.y);
					trade=null;
					action=NONE;
				}
			}
		}
		Game.redraw(true,true,false,false);
	}
	public void mousePressed(MouseEvent event){}
	public void mouseReleased(MouseEvent event){}
	public void mouseEntered(MouseEvent event){}
	public void mouseExited(MouseEvent event){}
}
