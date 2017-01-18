package civsim3;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class MainFrame extends JFrame{//remember, use mouse listeners and positioning on JPanels, not JFrames
	static final long serialVersionUID=1;
	private JPanel game,culture,minimap,histogram;
	
	public MainFrame(int width,int height){
		setTitle("Alex Lugo's CivSim 3");
		setResizable(false);
		
		GridBagLayout g=new GridBagLayout();
		g.columnWidths=new int[]{width};
		g.rowHeights=new int[]{height,height/4};
		setLayout(g);
		GridBagConstraints c=new GridBagConstraints();
		c.gridheight=1;
		game=new GamePanel(width,height);
		add(game,c);
		
		JPanel bottom=new JPanel();
		bottom.setLayout(new GridLayout(1,2));
		JPanel left=new JPanel();
		left.setLayout(new GridLayout(1,2));
		
		culture=new CulturePanel(width,height);
		left.add(culture,c);
		minimap=new MinimapPanel(width,height);
		left.add(minimap,c);
		bottom.add(left);
		
		histogram=new HistogramPanel(width,height);
		bottom.add(histogram,c);
		c.gridy=1;
		add(bottom,c);
		setSize(width,(height*5/4)+getInsets().top);
		pack();
		setVisible(true);
		
		((MinimapPanel)minimap).setScale();
		((CulturePanel)culture).setScale();
	}
}