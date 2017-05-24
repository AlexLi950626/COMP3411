
/*
 * Written by Bruce Hely (GitHub: BraveHelyx), 2015
 * 
 * "It's not safe to go alone. Take this!" - Internet
 * 
 * An inventory class to handle the contents of the agent's inventory.
 * 
 * Written for assignment 3 for Artificial Intelligence
 * COMP3411
 * University of New South Wales
 * 
 * @precondition	 	numDynamite >= 0
 */
public class Inventory{
	private boolean axe;
	private boolean gold;
	private boolean onBoat;
	private Node boatLocation;
	
	private int numDynamite;	

	/*
	 * Constructor for inventory
	 */
	public Inventory(){
		axe = false;
		gold = false;
		onBoat = false;
		
		boatLocation = null;
		numDynamite = 0;
	}
	
	/*
	 * Overloaded constructor
	 */
	public Inventory(boolean hasAxe, boolean hasGold, int numDynamite){
		axe = hasAxe;
		gold = hasGold;
		this.numDynamite = numDynamite;
	}
	
	
	
	public boolean containsAxe(){
		return axe;
	}
	
	public void obtainedAxe(){
		axe = true;
	}
	
	public boolean containsGold(){
		return gold;
	}
	
	public void obtainedGold(){
		gold = true;
	}
	
	public void obtainedDynamite(){
		numDynamite++;
	}
	
	public void useDynamite(){
		numDynamite--;
	}
	
	public boolean isOnBoat(){
		return onBoat;
	}
	
	public void toggleBoat(){
		onBoat = onBoat ? false : true;
	}
	
	/*
	 * Method that lazily removes dynamite. Checks must be performed prior to this.
	 * @precondition numDynamite must be > 0
	 */
	public Boolean containsDynamite(){
		if(numDynamite > 0){
			return true;
		}
		return false;
	}
	
	public void setBoatLocation(Node n){
		boatLocation = n;
	}
	
	public Node getBoatLocation(){
		return boatLocation;
	}
	
	public int getNumDynamite(){
		return numDynamite;
	}
	
	@Override
	public Inventory clone(){
		return new Inventory(axe, gold, numDynamite);
	}
	
	@Override
	public String toString(){
		return "[a:" + axe + "][g:" + gold +"][d:" + numDynamite + "]";
	}
}


