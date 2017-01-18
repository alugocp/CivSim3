package civsim3;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Dimension;

public class CulturePanel extends JPanel{
	static final long serialVersionUID=1;
	private double[] scale;
	public CulturePanel(int width,int height){
		Game.culture=this;
		setPreferredSize(new Dimension(width/4,height/4));
	}
	public void setScale(){
		scale=new double[2];
		scale[0]=round(getWidth()/(double)Game.world.width,3);
		scale[1]=round(getHeight()/(double)Game.world.height,3);
	}
	private double round(double input,int places){
		return (double)Math.round(Math.pow(10,places)*input)/Math.pow(10,places);
	}
	@Override
	public void paint(Graphics g){
		g.clearRect(0, 0, getWidth(),getHeight());
		Graphics2D g2=(Graphics2D)g;
		g2.scale(scale[0],scale[1]);
		for(int x=0;x<Game.world.width;x++){
			for(int y=0;y<Game.world.height;y++){
				g.setColor(Color.BLACK);
				Tile tile=Game.world.get(x,y);
				if(tile.city==null){
					if(tile.territory!=null){
						g.setColor(Game.world.get(tile.territory[0],tile.territory[1]).city.culture.getColor());
					}
				}else{
					g.setColor(tile.city.culture.getColor());
				}
				g.fillRect(x,y,1,1);
			}
		}
	}
}
