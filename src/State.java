/**
 * Created by shiyun on 11/05/17.
 */
public class State implements Cloneable {
	//state info
	private int row;
	private int col;
	private int direction;
	private State preState;
	
    // if the current state has any of these tools
    private boolean axe;
    private boolean raft;
    private boolean key;
    private int dynamite;
    private boolean treasure;
    
    public State(int row, int col, int direction){
    	this.row = row;
    	this.col = col;
    	this.direction = direction;
    	this.preState = null;
    	
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.dynamite = 0;
        this.treasure = false;
    }
    
    public State(State newState){
    	this.row = newState.getRow();
    	this.col = newState.getCol();
    	this.direction = newState.getDirection();
    	this.preState = newState.getPreState();
    	
        this.axe = newState.getAxe();
        this.raft = newState.getRaft();
        this.key = newState.getKey();
        this.dynamite = newState.getDynamite();
        this.treasure = newState.getTreasure();
    }
    
    public void setRow(int newRow){
    	this.row = newRow;
    }
    
    public void setCol(int newCol){
    	this.col = newCol;
    }
    
    public void updateDirection(int newDirection){
    	this.direction = newDirection % 4;
        while(this.direction < 0){
            this.direction += 4;
        }
    }
    
    public void setAxe(boolean newAxe){
    	this.axe = newAxe;
    }
    
    public void setRaft(boolean newRaft){
    	this.raft = newRaft;
    }
    
    public void setKey(boolean newKey){
    	this.key = newKey;
    }
    
    public void setTreasure(boolean newTreasure){
    	this.treasure = newTreasure;
    }
    
    public void setDynamite(int newDynamite){
    	this.dynamite = newDynamite;
    }

    public void setPreState(State s){
    	this.preState = s;
    }
    
    public int getRow(){
    	return this.row;
    }
    
    public int getCol(){
    	return this.col;
    }
    
    public int getDirection(){
    	return this.direction;
    }
    
    public State getPreState(){
    	return this.preState;
    }
    
    public boolean getAxe(){
    	return this.axe;
    }
    
    public boolean getRaft(){
    	return this.raft;
    }
    
    public boolean getKey(){
    	return this.key;
    }
    
    public boolean getTreasure(){
    	return this.treasure;
    }
    
    public int getDynamite(){
    	return this.dynamite;
    }
    
    public int getForwardRow(){
        switch (this.getDirection()){
            case Constants.NORTH:
                return this.getRow() - 1;
            case Constants.SOUTH:
                return this.getRow() + 1;
            default:
                return this.getRow();
        }
    }

    public int getForwardCol(){
        switch (this.getDirection()){
            case Constants.WEST:
                return this.getCol() - 1;
            case Constants.EAST:
                return this.getCol() + 1;
            default:
                return this.getCol();
        }
    }
    
    public char getDirectionChar(){
        switch (this.getDirection()){
            case Constants.NORTH:
                return '^';
            case Constants.SOUTH:
                return 'v';
            case Constants.WEST:
                return '<';
            case Constants.EAST:
                return '>';
            default:
                return (char) 0;
        }
    }

    public State clone(){
        State newState = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);

        newState.row = this.row;
        newState.col = this.col;
        newState.direction = this.direction;
        newState.preState = this.preState;

        newState.axe = this.axe;
        newState.raft = this.raft;
        newState.key = this.key;
        newState.dynamite = this.dynamite;
        newState.treasure = this.treasure;

        return newState;
    }


    /**
     * typical getter that gets the position of the agent
     * @return position of the agent
     */
    public Position getCurrentPosition(){
        return new Position(row, col);
    }
    /*
     * print the current state info
     */
    public void printState(){
        System.out.println("player info:");
        System.out.println("getRow: " + this.getRow());
        System.out.println("getCol: " + this.getCol());
        if(this.getPreState() != null){
            System.out.println("getPreState node getRow: " + this.getPreState().getRow());
            System.out.println("getPreState node getCol: " + this.getPreState().getCol());
        }

        System.out.println("getAxe: " + this.getAxe());
        System.out.println("getKey: " + this.getKey());
        System.out.println("getRaft: " + this.getRaft());
        System.out.println("getDynamite: " + this.getDynamite());
        System.out.println("getTreasure: " + this.getTreasure());
    	
    }
}
