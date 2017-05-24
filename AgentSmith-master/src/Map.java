import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/*
 * Written by James Virtue, modified by Bruce Hely, 2015
 * 
 * Map consisting of a character array of each possible item.
 * Unknown elements are denoted as ` to start with.
 * 
 * The initial Map is populated with ` characters.
 * On the first update, all values in view are placed on the map.
 * Subsequent updates only update the first 5 elements in front of the agent
 *  as well as directly in front (in case an item is used),
 *  and directly behind (to clear last agent position or set down boat).
 *   
 */
public class Map {
	
	private Node itemMap[][];
	private int maxEdge;
	private int posX;
	private int posY;
	private int facing;
	
	//Flags
	private boolean initUpdate;
	
	/**
	 * Constructor
	 * @param gridLength		The length of the local map to generate
	 */
	public Map(int gridLength) {
		maxEdge = gridLength;
		init();
		initUpdate = false;
	}
	
	/**
	 * Constructor for creating a predesigned map
	 * @param gridLength		The length of the predesigned map
	 * @param inputMap
	 */
	public Map(String mapName){
		initRecreatedMap(mapName);
	}
	
	private void init() {
		
		//Sets the agent's initial position
		posX = maxEdge/2;
		posY = maxEdge/2;
		
		itemMap = new Node[maxEdge][maxEdge];
		
		//Initialise the map with nodes containing undiscovered symbols
		for(int i = 0; i < maxEdge; i++) {
			for(int j = 0; j < maxEdge; j++) {
				itemMap[i][j] = new Node(j, i, '`', 0);
			}
		}
	}
	
	private void initRecreatedMap( String mapName ) {

		BufferedReader in;
		boolean agent_here;
		int r,c;

		char[][] map = new char[1024][];

		r=-1;
	      
		try {
			in = new BufferedReader(new FileReader(mapName));
	        String oneLine = in.readLine();
	        while(( oneLine != null )&&( oneLine.length() > 0 )) {
	        	map[++r] = new char[oneLine.length()];
	            for( c=0; c < oneLine.length(); c++ ) {
	            	map[r][c] = oneLine.charAt(c);
	            	agent_here = true;
	            	switch( map[r][c] ) {
	            	case '^': facing = Compass.NORTH; break;
	                case '>': facing = Compass.EAST;  break;
	                case 'v': facing = Compass.SOUTH; break;
	                case '<': facing = Compass.WEST;  break;
	                default:  agent_here = false;
	            	}
	            	if( agent_here ) {
	            		posX = c;
	            		posY = r;
	            	}
	            }
	            oneLine = in.readLine();
	        }
		}
		catch( FileNotFoundException fnfe ) {
			fnfe.printStackTrace();
		}
		catch( IOException ioe ) {
			ioe.printStackTrace();
		}

		int gridHeight = r+1; // number of rows
		int gridWidth  = map[0].length; // Length of initial row
		
		maxEdge = Math.max(gridHeight, gridWidth);
		
		//Create the map with maximum dimensions
		itemMap = new Node[maxEdge][maxEdge];
		int i = 0;
		int j = 0;
		
		//Fill the map with nodes containing items in char map
		for(i = 0; i < maxEdge; i++){
			for(j = 0; j < maxEdge; j++){
				if(i < gridHeight && j < gridWidth){
					itemMap[i][j] = new Node(j, i, map[i][j], 0);
				} else {
					itemMap[i][j] = new Node(j, i, '.', 0);
				}	 
			}
		}
		
		printMap();
		
    }

	
	public Node[][] getMap() {
		return itemMap;
	}
	
	public Node getNode(BountyPoint point){
		int x = point.getX();
		int y = point.getY();
		return itemMap[y][x];
	}
	
	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int x) {
		posX = x;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public void setPosY(int y) {
		posY = y;
	}
	
	public int getFacing() {
		return facing;
	}
	
	/*
	 * Method that processes the very first view.
	 */
	private void initUpdate(char[][] view) {
		int x, y;

		for(int i = 0; i < 5; i++) {
			y = posY- 2 + i;
			for(int j = 0; j < 5; j++) {
				x = posX - 2 + j;
				if((i == 2) && (j == 2)){
					itemMap[y][x].setItem('^');			// put the agent icon in the middle
				} else {
					itemMap[y][x].setItem(view[i][j]);	// view provides row, col.
				}
			}
		}
	}
	
	public void update(char[][] view, Compass compass) {
		if(!initUpdate) {	//Run Once
			initUpdate(view);
			compass.setAgentPosition(posX, posY);
			initUpdate = true;
		} else {
			//Get Agent direction
			facing = compass.getAgentDirection();
			
			//Get agent coordinate
			posX = compass.getAgentPosition().getX();
			posY = compass.getAgentPosition().getY();
			int x, y;
			
			//Decide where to update
			switch(facing){
			case 0:		//NORTH
				for(int i = 0; i < 5; i++) {
					x = posX - 2 + i;
					y = posY - 2;
					itemMap[y][x].setItem(view[0][i]);
				}
				
				itemMap[posY-1][posX].setItem(view[1][2]);	// update directly in front
				itemMap[posY][posX].setItem('^');			// set agent position
				itemMap[posY+1][posX].setItem(view[3][2]);	// update directly behind
				break;
				
			case 1:		// EAST
				for(int j = 0; j < 5; j++) {
					x = posX + 2;
					y = posY - 2 + j;
					itemMap[y][x].setItem(view[0][j]);
				}
				
				itemMap[posY][posX+1].setItem(view[1][2]);	// update directly in front
				itemMap[posY][posX].setItem('>');			// set agent position
				itemMap[posY][posX-1].setItem(view[3][2]);	// update directly behind
				break;
				
			case 2:		// SOUTH
				for(int i = 0; i < 5; i++) {
					x = posX + 2 - i;
					y = posY + 2;
					itemMap[y][x].setItem(view[0][i]);
				}
				
				itemMap[posY+1][posX].setItem(view[1][2]);	// update directly in front
				itemMap[posY][posX].setItem('v');			// set agent position
				itemMap[posY-1][posX].setItem(view[3][2]);	// update directly behind
				break;
			
			case 3:		// WEST
				for(int j = 0; j < 5; j++) {
					x = posX - 2;
					y = posY + 2 - j;
					itemMap[y][x].setItem(view[0][j]);
				}
				
				itemMap[posY][posX-1].setItem(view[1][2]);	// update directly in front
				itemMap[posY][posX].setItem('<');			// set agent position
				itemMap[posY][posX+1].setItem(view[3][2]);	// update directly behind
				break;
			}
		}
	}
	
	public Node getAgentNode(){
		return itemMap[posY][posX];
	}
	public int getAgentX(){
		return posX;
	}
	
	public int getAgentY(){
		return posY;
	}
	
	/**
	 * Method that returns a list containing all the adjacent nodes
	 * 
	 * @param currNode			The node from which we are to obtain the adjacent nodes from.
	 * @return					A list of adjacent nodes.
	 */
	public ArrayList<Node> getAdjacentNodes(Node currNode){
		ArrayList<Node> adjacentNodes = new ArrayList<Node>();
		int x = currNode.getX();
		int y = currNode.getY();
		adjacentNodes.add(itemMap[y][x-1]);
		adjacentNodes.add(itemMap[y+1][x]);
		adjacentNodes.add(itemMap[y][x+1]);
		adjacentNodes.add(itemMap[y-1][x]);
		
		return adjacentNodes;
	}
	
	public int getDirection(){
		return facing;
	}
	
	public int getMaxEdge(){
		return maxEdge;
	}
	
	public void printMap() {
		for(int i = 0; i < maxEdge; i++) {
			for(int j = 0; j < maxEdge; j++) {
				System.out.print(itemMap[i][j].getItem());
			}
			System.out.print('\n');
		}
	}
	   
	public void printTestMap() {
		char currItem;
		for(int i = 0; i < maxEdge; i++) {
			for(int j = 0; j < maxEdge; j++) {
				currItem = itemMap[i][j].getItem();
				if(currItem != '.'){
					System.out.print(itemMap[i][j].getItem());
				}
			}
			System.out.print('\n');
		}
	}
}
