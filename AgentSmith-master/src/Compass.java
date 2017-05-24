
/*
 * Written by Bruce Hely (GitHub: BraveHelyx), 2015
 * 
 * "I solemnly swear I am up to no good." - Words to reveal the Marauders Map, Harry Potter
 * 
 * The compass class is one that handles an agent's directional data and coordinate system.
 * The compass should be used to map coordinates to the memory map.
 * 
 * @param agentDir		Integer represent agent direction N, E, S, W as 0, 1, 2, 3 respectively
 * @param agentPos		Coordinate with current X and Y position of the agent within the local map
 * 
 * @precondition 		0 <= agentDir <= 3
 * 
 * @postcondition 		0 <= agentDir <= 3
 * 
 */
public class Compass {
	private BountyPoint agentPos;
	private int agentDir;
	public final static int NORTH = 0;
	public final static int EAST = 1;
	public final static int SOUTH = 2;
	public final static int WEST = 3;
	
	// Constructor for new maps
	public Compass(){
		agentPos = new BountyPoint(40, 40);
		agentDir = NORTH;
	}
	
	// Overloaded constructor for cloning maps
	public Compass(BountyPoint agentPosition, int agentDirection){
		agentPos = new BountyPoint(agentPosition.getX(), agentPosition.getY());
		agentDir = agentDirection;
	}
	
	//Method that returns the new position after forward command
	public BountyPoint getForwardPosition(){
		int newX = agentPos.getX();
		int newY = agentPos.getY();
		
		if(agentDir == 0){
			//North
			newY--;
		} else if(agentDir == 1){
			//East
			newX++;
		} else if(agentDir == 2){
			//South
			newY++;
		} else {
			//West
			newX--;
		}
		
		return new BountyPoint(newX, newY);
	}
	
	public boolean isForward(BountyPoint nextPoint){
		if(nextPoint.equals(getForwardPosition())){
			return true;
		}
		return false;
	}
	
	public BountyPoint getLeftPosition(){
		int newX = agentPos.getX();
		int newY = agentPos.getY();
		
		if(agentDir == 0){
			//North
			newX--;
		} else if(agentDir == 1){
			//East
			newY--;
		} else if(agentDir == 2){
			//South
			newX++;
		} else {
			//West
			newY++;
		}
		
		return new BountyPoint(newX, newY);
	}
	
	public boolean isLeft(BountyPoint nextPoint){
		if(nextPoint.equals(getLeftPosition())){
			return true;
		}
		return false;
	}
	
	public BountyPoint getRightPosition(){
		int newX = agentPos.getX();
		int newY = agentPos.getY();
		
		if(agentDir == 0){
			//North
			newX++;
		} else if(agentDir == 1){
			//East
			newY++;
		} else if(agentDir == 2){
			//South
			newX--;
		} else {
			//West
			newY--;
		}
		
		return new BountyPoint(newX, newY);
	}
	
	public boolean isRight(BountyPoint nextPoint){
		if(nextPoint.equals(getRightPosition())){
			return true;
		}
		return false;
	}
	
	public BountyPoint getAgentPosition(){
		return agentPos;
	}
	
	public void printAgentPosition(){
		System.out.print(agentPos.toString());
	}
	
	public void setAgentPosition(int x, int y){
		agentPos.setPoint(x, y);
	}
	
	//Overloaded method for setting agent position
	public void setAgentPosition(BountyPoint newPosition){
		agentPos.setPoint(newPosition.getX(), newPosition.getY());
	}
	
	public int getAgentDirection(){
		return agentDir;
	}
	
	/*
	 * Method that turns the agent to the left
	 * 
	 * @postcondition 		0 <= agentDir <= 3 
	 */
	public void turnLeft(){
		if(agentDir > 0){
			agentDir--;
		} else {
			agentDir = 3;
		}
	}
	
	/*
	 * Method that turns the agent to the right
	 * 
	 * @postcondition 		0 <= agentDir <= 3 
	 */
	public void turnRight(){
		if(agentDir < 3){
			agentDir++;
		} else {
			agentDir = 0;
		}
	}	
	
	@Override
	public Compass clone(){
		return new Compass(agentPos, agentDir);
	}
}
