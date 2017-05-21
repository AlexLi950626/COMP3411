import java.util.ArrayList;

/**
 * Created by shiyun on 11/05/17.
 */
public class State {
	//state info
	private int row;
	private int col;
	private int direction;
	private State preState;
	private ArrayList<State> path;
	
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
    	this.path =  new ArrayList<State>();
    	
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.dynamite = 0;
        this.treasure = false;
    }
    
    public State(State newState){
    	this.row = newState.row();
    	this.col = newState.col();
    	this.direction = newState.direction();
    	this.preState = newState.prv();
    	this.path = newState.path();
    	
        this.axe = newState.axe();
        this.raft = newState.raft();
        this.key = newState.key();
        this.dynamite = newState.dynamite();
        this.treasure = newState.treasure();
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
    
    public void updatePrv(State s){
    	this.preState = s;
    	addPath(s);
    }
    
    public void addPath(State s){
    	this.path.add(s);
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
    
    public State prv(){
    	return this.preState;
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
    public ArrayList<State> path(){
    	return this.path;
    }
	
    
    /*
     * print the current state info
     */
    public void printState(){
        System.out.println("player info:");
        System.out.println("row: " + this.row());
        System.out.println("col: " + this.col());
        if(this.prv() != null){
            System.out.println("prv node row: " + this.prv().row());
            System.out.println("prv node col: " + this.prv().col());
        }

        System.out.println("axe: " + this.axe());
        System.out.println("key: " + this.key());
        System.out.println("raft: " + this.raft());
        System.out.println("dynamite: " + this.dynamite());
        System.out.println("treasure: " + this.treasure());
    	
    }
}
