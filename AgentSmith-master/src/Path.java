import java.util.ArrayList;


/**
 * Objects for handling pathing information for A* search
 * 
 * @author Bruce Hely
 *
 */
public class Path implements Comparable<Path>{
	
	private ArrayList<Node> currentPath = new ArrayList<Node>();
	private Node currentNode;
	private int numNodes;
	private int numRotations;
	private Inventory availableTools;
	private int currDirection;
	private boolean onBoat;
	
	//	g(x) value
	private int pathValue;
	private int priorityValue;
	
	/**
	 * Constructor used for creating initial paths from an origin
	 * 
	 * @param startNode				The origin of the search
	 * @param direction				The direction agent is currently facing to get to startNode
	 * 
	 */
	public Path(Node startNode, int direction, Inventory playerInventory){
		
		//Add the agent's position to the list of paths, to ensure no backtracking occurs.
		currentNode = startNode;
		currentPath.add(startNode);
		
		currDirection = direction;
		availableTools = playerInventory.clone();
		onBoat = false;
		
		numNodes = 1;
		numRotations = 0;
		
		updatePathValue();
	}
	
	/**
	 * Constructor used for cloning new paths from old ones
	 * 
	 */
	private Path(ArrayList<Node> _currentPath, Node _currentNode, Inventory pathInventory, int _numRotations, int _currDirection, boolean _onBoat){
		for(Node n : _currentPath){
			currentPath.add(n);
		} 
		
		currentNode = _currentNode;
		numNodes = _currentPath.size();
		numRotations = _numRotations;
		currDirection = _currDirection;
		availableTools = pathInventory.clone();
		onBoat = _onBoat;
	}
	
	public ArrayList<Node> getCurrentPath(){
		return currentPath;
	}
	
	/**
	 * Method that should be run every time a new node is added to update this
	 * path's path value (g(x)). Returns 1 less than the number of nodes in the path,
	 */
	private void updatePathValue(){
		pathValue = (numNodes-1) + numRotations;
	}
	
	
	/**
	 * Method that updates this path's priority value calculated as
	 * f(x) = g(x) + h(x)
	 * 
	 * Where g(x) is the movement costs including rotations of the path taken so far
	 * and h(x) is the heuristic value of the current node added to the path 
	 * @param heuristicValue
	 */
	private void updatePriorityValue(int heuristicValue){
		priorityValue = pathValue + heuristicValue;
	}
	
	public int getPriorityValue(){
		return priorityValue;
	}
	
	/**
	 * Method for adding a node to the path
	 * 
	 * @precondition	currentPath cannot already contain newNode
	 * @precondition	newNode must be a node that is vertically or horizontally adjacent to currentNode in 2D Geometry.
	 */
	public void addToPath(Node newNode){
		int currX = currentNode.getX();		
		int currY = currentNode.getY();
		int nextX = newNode.getX();
		int nextY = newNode.getY();
		
		//Handles addition of rotations
		//Note that we will never have to increment rotations by 2, because backtracking is not allowed.
		if(currX == nextX){			//If we have moved Vertically, then the X Values are the same
			if(nextY < currY){		//Going north
				if(currDirection != Compass.NORTH){
					if(currDirection == Compass.WEST || currDirection == Compass.EAST){
						numRotations++;
					} else {
						numRotations+=2;
					}
				}
				currDirection = Compass.NORTH;
			} else {
				if(currDirection != Compass.SOUTH){
					if(currDirection == Compass.WEST || currDirection == Compass.EAST){
						numRotations++;
					} else {
						numRotations+=2;
					}
				}
				currDirection = Compass.SOUTH;
			}	
		} else {					//If we have moved Horizontally, then the X Values are different

			
			if(nextX < currX){		//Going West
				if(currDirection != Compass.WEST){
					if(currDirection == Compass.NORTH || currDirection == Compass.SOUTH){
						numRotations++;
					} else {
						numRotations+=2;
					}
				}
				currDirection = Compass.WEST;
			} else {
				if(currDirection != Compass.EAST){
					if(currDirection == Compass.NORTH || currDirection == Compass.SOUTH){
						numRotations++;
					} else {
						numRotations+=2;
					}
				}
				currDirection = Compass.EAST;
			}

		}
		
		//Add the node to path, and update the current number of nodes in the path
		currentPath.add(newNode);
		currentNode = newNode;
		numNodes++;
		
		//Update this path's g(x) cost and f(x) cost.
		updatePathValue();
		updatePriorityValue(newNode.getH());
	}
	
	public Inventory getInventory(){
		return availableTools;
	}
	
	public Node getCurrentNode(){
		return currentNode;
	}
	
	public boolean isInBoat(){
		return onBoat;
	}
	
	public void toggleBoat(){
		onBoat = onBoat ? false : true;
	}
	
	@Override
	public Path clone(){
		return new Path(currentPath, currentNode, availableTools, numRotations, currDirection, onBoat);
	}

	@Override
	public int compareTo(Path o) {
		final int LESSER = -1;
		final int EQUAL = 0;
		final int GREATER = 1;
		
		int classifier;
		
		if(this.getPriorityValue() == o.getPriorityValue()){
			classifier =  EQUAL;
		} else if(this.getPriorityValue() < o.getPriorityValue()){
			classifier = LESSER;
		} else {
			classifier = GREATER;
		}
		return classifier;
	}
}
