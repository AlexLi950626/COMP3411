/**
 * Created by shiyun on 11/05/17.
 */
public class State {
	//state info
	private int row;
	private int col;
	private int direction;
	
    // if the player has any of these tools
    private boolean axe;
    private boolean raft;
    private boolean key;
    private int dynamite;
    private boolean treasure;
    
    // read directions
    public static final char NORTH = 0;
    public static final char SOUTH = 2;
    public static final char EAST = 1;
    public static final char WEST = 3;
    
    public State(int row, int col, int direction){
    	this.row = row;
    	this.col = col;
    	this.direction = direction;
    	
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.dynamite = 0;
        this.treasure = false;
    }
    
    public void updateRow(int newRow){
    	this.row = newRow;
    }
    
    public void updateCol(int newCol){
    	this.col = newCol;
    }
    
    public void updateDirection(int newDirection){
    	this.direction = newDirection % 4;
        while(this.direction < 0){
            this.direction += 4;
        }
    }
    
    public void updateAxe(boolean newAxe){
    	this.axe = newAxe;
    }
    
    public void updateRaft(boolean newRaft){
    	this.raft = newRaft;
    }
    
    public void updateKey(boolean newKey){
    	this.key = newKey;
    }
    
    public void updateTreasure(boolean newTreasure){
    	this.treasure = newTreasure;
    }
    
    public void updateDynamite(int newDynamite){
    	this.dynamite = newDynamite;
    }
    
    public int row(){
    	return this.row;
    }
    
    public int col(){
    	return this.col;
    }
    
    public int direction(){
    	return this.direction;
    }
    
    public boolean axe(){
    	return this.axe;
    }
    
    public boolean raft(){
    	return this.raft;
    }
    
    public boolean key(){
    	return this.key;
    }
    
    public boolean treasure(){
    	return this.treasure;
    }
    
    public int dynamite(){
    	return this.dynamite;
    }
	
}
