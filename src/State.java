/**
 * Created by shiyun on 11/05/17.
 * this state is used in exploration, it is used to store the state of the agent
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

    /**
     * constructor
     * @param row
     * @param col
     * @param direction
     */
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

    /**
     * another constructor
     * @param newState
     */
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

    /**
     * setter which set row
     * @param newRow
     */
    public void setRow(int newRow){
    	this.row = newRow;
    }

    /**
     * setter which set the col
     * @param newCol
     */
    public void setCol(int newCol){
    	this.col = newCol;
    }
    
    public void updateDirection(int newDirection){
    	this.direction = newDirection % 4;
        while(this.direction < 0){
            this.direction += 4;
        }
    }

    /**
     * setter which set if current agent has axe
     * @param newAxe
     */
    public void setAxe(boolean newAxe){
    	this.axe = newAxe;
    }

    /**
     * setter if current agent has raft
     * @param newRaft
     */
    public void setRaft(boolean newRaft){
    	this.raft = newRaft;
    }

    /**
     * setter set if current agent has key
     * @param newKey
     */
    public void setKey(boolean newKey){
    	this.key = newKey;
    }

    /**
     * setter set if current agent has treasure
     * @param newTreasure
     */
    public void setTreasure(boolean newTreasure){
    	this.treasure = newTreasure;
    }

    /**
     * setter set how many dynamite current agent has
     * @param newDynamite
     */
    public void setDynamite(int newDynamite){
    	this.dynamite = newDynamite;
    }

    /**
     * setter set the pre state of the current state
     * @param s
     */
    public void setPreState(State s){
    	this.preState = s;
    }

    /**
     * get row of the state
     * @return
     */
    public int getRow(){
    	return this.row;
    }

    /**
     * get col of the state
     * @return
     */
    public int getCol(){
    	return this.col;
    }

    /**
     * @return get direction of the state
     */
    public int getDirection(){
    	return this.direction;
    }

    /**
     * @return boolean get previous state from current state
     */
    public State getPreState(){
    	return this.preState;
    }

    /**
     * @return boolean check if current state agent has axe
     */
    public boolean getAxe(){
    	return this.axe;
    }

    /**
     * @return boolean check if current agent has a raft
     */
    public boolean getRaft(){
    	return this.raft;
    }

    /**
     * @return boolean check if current agent has a key
     */
    public boolean getKey(){
    	return this.key;
    }

    /**
     * @return boolean check if current agent has a treasure
     */
    public boolean getTreasure(){
    	return this.treasure;
    }

    /**
     * @return number of dynamite current agent has
     */
    public int getDynamite(){
    	return this.dynamite;
    }

    /**
     * @return get the row if current agent go forward
     */
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

    /**
     * @return get the col if current agent go forward
     */
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

    /**
     * @return a deep copy of the state
     */
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
}
