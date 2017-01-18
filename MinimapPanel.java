package civsim3;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class MinimapPanel extends JPanel implements MouseListener{
	static final long serialVersionUID=1;
	private double[] scale=new double[2];
	public MinimapPanel(int width,int height){
		Game.mini=this;
		setPreferredSize(new Dimension(width/4,height/4));
		addMouseListener(this);
	}
	@Override
	public void paint(Graphics g){
		g.clearRect(0, 0, getWidth(), getHeight());
		Graphics2D g2=(Graphics2D)g;
		g2.scale(scale[0],scale[1]);
		for(int x=0;x<Game.world.width;x++){
			for(int y=0;y<Game.world.height;y++){
				g2.setColor(Game.world.getColor(x,y));
				g2.fillRect(x,y,1,1);
			}
		}
		drawViewer(g2);
	}
	private void drawViewer(Graphics2D g){
		int[] coords=new int[]{Game.game.scroll()[0],Game.game.scroll()[1]};
		coords[0]/=-Game.xDis();
		coords[1]/=-Game.yDis();
		g.setColor(Color.BLACK);
		g.drawRect(coords[0],coords[1],Game.game.getWidth()/Game.xDis(),Game.game.getHeight()/Game.yDis());
	}
	public void setScale(){
		scale[0]=round(getWidth()/(double)Game.world.width,3);
		scale[1]=round(getHeight()/(double)Game.world.height,3);
	}
	private double round(double input,int places){
		return (double)Math.round(Math.pow(10,places)*input)/Math.pow(10,places);
	}
	@Override
	public void mouseClicked(MouseEvent event){
		int x=(int)(event.getX()*Game.xDis()/scale[0]);
		int y=(int)(event.getY()*Game.yDis()/scale[1]);
		Game.game.scrollTo(-x+Game.game.getWidth()/2,-y+Game.game.getHeight()/2);
		Game.redraw(false,false,true,false);
	}
	public void mouseReleased(MouseEvent event){}
	public void mousePressed(MouseEvent event){}
	public void mouseEntered(MouseEvent event){}
	public void mouseExited(MouseEvent event){}
}
