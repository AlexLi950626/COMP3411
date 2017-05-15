import java.util.*;
public class Search {
	
    // read directions
    public static final char NORTH = 0;
    public static final char SOUTH = 2;
    public static final char EAST = 1;
    public static final char WEST = 3;
    
    // queue for explore
    private Queue<State> exploreQ;
    
	public Search(){
		exploreQ = new LinkedList<State>();
	}
	
	public void to_Gold(){
		
	}
	
	public void to_Origin(){
		
	}
	
	public void to_poi(){
		
	}
	
	public void explore(int[][] view, State current){
		//expand North
		//expand South
		//expand East
		//expand West
		
	}
}
