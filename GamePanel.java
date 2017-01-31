package civsim3;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GamePanel extends JPanel implements MouseListener,MouseMotionListener{
	static final long serialVersionUID=1;
	private final Polygon hex=hexagon();
	private int[] mouse=new int[2];
	private int[] scroll=new int[2];
	private Color highlighted=new Color(255,192,103,127);//pink
	private int wants;
	private boolean moving=false;
	public GamePanel(int width,int height){
		Game.game=this;
		setPreferredSize(new Dimension(width,height));
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	@Override
	public void paint(Graphics g){
		g.clearRect(0, 0, getWidth(), getHeight());
		g.translate(scroll[0],scroll[1]);
		int left=(int)Math.floor(-scroll[0]/(double)Game.xDis());
		int top=(int)Math.floor(-scroll[1]/(double)Game.yDis());
		int right=left+(int)Math.ceil(getWidth()/(double)Game.xDis())+1;
		int bottom=top+(int)Math.ceil(getHeight()/(double)Game.yDis())+1;
		for(int x=left;x<=right;x++){
			for(int y=top;y<=bottom;y++){
				if(x<Game.world.width && y<Game.world.height){
					g.setColor(getColor(x,y));				
					int[] c=Game.world.getCoords(x,y);
					g.translate(c[0],c[1]);
					g.fillPolygon(hex);
					g.setColor(Color.BLACK);
					Tile tile=Game.world.get(x, y);
					if(Game.mode==Game.PLAYER && tile.resource!=-1 && tile.city==null){
						g.drawString(Tile.resources[tile.resource],-Game.xDisHalf(),0);
					}
					if(tile.city!=null){
						if(x==tile.city.leader.x && y==tile.city.leader.y){
							int radius=(3*Game.xDisHalf())/4;
							g.fillOval(-radius,-radius,radius*2,radius*2);
							g.setColor(Color.WHITE);
						}
						if(tile.city.interest!=null && tile.city.wants!=null && tile.city.wanted>0){
							g.drawString("!",0,0);
						}
						g.setColor(highlighted);
						if((Game.histo.action==HistogramPanel.ATTACK || Game.histo.action==HistogramPanel.TRADE) && Game.histo.focus.neighbors.contains(tile.city)){
							g.fillPolygon(hex);
						}
					}
					g.setColor(highlighted);
					if(Game.histo.building.length>0 && Game.histo.hasBuildPoint(x,y)){
						g.fillPolygon(hex);
					}
					g.setColor(Color.BLACK);
					g.drawPolygon(hex);
					g.translate(-c[0],-c[1]);
				}
			}
		}
		g.translate(-scroll[0],-scroll[1]);
		if(Game.turn==Game.PLAYER){
			g.setColor(Color.BLACK);
			g.fillRect(getWidth()-150,0,150,140);
			g.setColor(Color.WHITE);
			g.drawString("End Turn",getWidth()-145,25);
			g.drawString("("+wants+" cities unsatisfied)",getWidth()-145,50);
			g.drawString("Scroll to capital",getWidth()-145,100);
		}
	}
	public void updateWants(){
		wants=0;
		for(int a=0;a<Game.player.cities.size();a++){
			if(Game.player.cities.get(a).wanted>0){
				wants++;
			}
		}
	}
	private Polygon hexagon(){
		int xdis=Game.xDisHalf();
		int s=Game.side;
		Polygon hex=new Polygon();
		hex.addPoint(0,s);
		hex.addPoint(xdis,s/2);
		hex.addPoint(xdis,-s/2);
		hex.addPoint(0,-s);
		hex.addPoint(-xdis,-s/2);
		hex.addPoint(-xdis,s/2);
		return hex;
	}
	private Color getColor(int x,int y){
		Tile tile=Game.world.get(x,y);
		if(tile.city!=null){
			return tile.city.getColor();
		}else if(tile.territory!=null){
			City territory=Game.world.get(tile.territory[0],tile.territory[1]).city;
			if(territory==null){
				tile.territory=null;
			}else if(territory==Game.histo.focus){
				return territory.getTerritoryColor();
			}
		}
		return Game.world.environmentColor(tile.environment);
	}
	public int[] scroll(){
		return scroll;
	}
	private void scroll(int x,int y){
		scroll[0]+=x;
		scroll[1]+=y;
		if(scroll[0]>0){
			scroll[0]=0;
		}
		if(scroll[1]>0){
			scroll[1]=0;
		}
		int[] coords=Game.world.getCoords(Game.world.width-1,Game.world.height-1);
		int limitX=-coords[0]+getWidth();
		if(scroll[0]<limitX){
			scroll[0]=limitX;
		}
		int limitY=-coords[1]+getHeight();
		if(scroll[1]<limitY){
			scroll[1]=limitY;
		}
		/*repaint();
		Game.mini.repaint();*/
		if(Game.finished || Game.mode==Game.PLAYER){
			Game.redraw(true,false,false,false);
		}
	}
	public void scrollTo(int x,int y){
		scroll(-scroll[0]+x,-scroll[1]+y);
	}
	private int modulus(int input){
		if(input==0){
			return 0;
		}
		return (int)(((double)input)%2);
	}
	@Override
	public void mousePressed(MouseEvent event){
		mouse[0]=event.getX();
		mouse[1]=event.getY();
		moving=false;
	}
	@Override
	public void mouseDragged(MouseEvent event){
		scroll(event.getX()-mouse[0],event.getY()-mouse[1]);
		mouse[0]=event.getX();
		mouse[1]=event.getY();
		moving=true;
	}
	@Override
	public void mouseClicked(MouseEvent event){
		if(Game.turn==Game.PLAYER && event.getX()>=getWidth()-150 && event.getY()<=140){
			if(event.getY()<=70){
				Game.gameCycle();
				return;
			}
			Game.player.scrollTo();
			return;
		}
		int mouseX=event.getX()-scroll[0];
		int mouseY=event.getY()-scroll[1];
		int yIndex=(int)Math.floor((mouseY+(Game.side*0.5))/Game.yDis());
		int xIndex=(int)(Math.floor(mouseX+(Game.xDisHalf()*modulus(yIndex+1)))/Game.xDis());
		int y=(int)((yIndex*Game.yDis())-(Game.side*0.5));
		if(mouseY-y<=Game.side){
			clicked(xIndex,yIndex);
		}else{
			int h=mouseY-y-Game.side;
			int fh=(int)(Math.sqrt(3)*((Game.side*0.5)-h));
			int xcoord=Game.world.getCoords(xIndex,yIndex)[0];
			if(xcoord-fh>=mouseX){
				clicked(xIndex-((yIndex+1)%2),yIndex+1);
			}else if(xcoord+fh<=mouseX){
				clicked(xIndex+1-((yIndex+1)%2),yIndex+1);
			}else{
				clicked(xIndex,yIndex);
			}
		}
		Game.redraw(true,true,false,false);
	}
	private void clicked(int x,int y){
		Tile tile=Game.world.get(x,y);
		if(Game.histo.action==HistogramPanel.TRADE && tile.city!=null && Game.histo.focus.neighbors.contains(tile.city)){
			Game.histo.trade=new Trade(Game.histo.focus,tile.city);
		}else{
			if(Game.histo.action==HistogramPanel.ATTACK && tile.city!=null && Game.histo.focus.neighbors.contains(tile.city)){
				Game.histo.focus.attack(tile.city);
			}else if(Game.histo.action==HistogramPanel.BUILD && Game.histo.hasBuildPoint(x,y)){
				new City(Game.histo.focus,x,y);
			}
			Game.histo.changeFocus(x,y);
		}
		Game.redraw(true,Game.histo.focus!=null,false,false);
	}
	public void mouseMoved(MouseEvent event){}
	public void mouseEntered(MouseEvent event){}
	public void mouseExited(MouseEvent event){}
	@Override
	public void mouseReleased(MouseEvent event){
		if(moving){
			Game.redraw(false,false,true,false);
			moving=false;
		}
	}
}
