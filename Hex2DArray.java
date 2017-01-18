package civsim3;
import java.util.ArrayList;

public class Hex2DArray{
	final int width,height;
	private Tile[][] array;
	private int spaceX,spaceY,startX,startY;
	
	public Hex2DArray(int width,int height){
		this.width=width;
		this.height=height;
		this.array=new Tile[width][height];
	}
	
	// basic array stuff
	/*public Tile[] get(int index){
		return array[index];
	}*/
	public Tile get(int x,int y){
		return array[x][y];
	}
	public void set(int x,int y,Tile value){
		array[x][y]=value;
	}
	
	// hexagonal translation
	public int[] getCoords(int x,int y){
		int[] coords=new int[2];
		coords[0]=startX+(int)((x+(0.5*(y%2)))*spaceX);
		coords[1]=startY+(y*spaceY);
		return coords;
	}
	public void setDimensions(int startX,int startY,int spaceX,int spaceY){
		this.startX=startX;
		this.startY=startY;
		this.spaceX=spaceX;
		this.spaceY=spaceY;
	}
	public int[][] getAdjacents(int x,int y){
		ArrayList<int[]> surroundings=new ArrayList<>();
		surroundings.add(new int[]{x-1,y});
		surroundings.add(new int[]{x+1,y});
		surroundings.add(new int[]{x,y-1});
		surroundings.add(new int[]{x,y+1});
		if(y%2==0){
			surroundings.add(new int[]{x-1,y-1});
			surroundings.add(new int[]{x-1,y+1});
		}else{
			surroundings.add(new int[]{x+1,y-1});
			surroundings.add(new int[]{x+1,y+1});
		}
		for(int a=0;a<surroundings.size();a++){
			int[] point=surroundings.get(a);
			if(point[0]<0 || point[1]<0 || point[0]>=width || point[1]>=height){
				surroundings.remove(a);
				a--;
			}
		}
		return surroundings.toArray(new int[surroundings.size()][2]);
	}
}