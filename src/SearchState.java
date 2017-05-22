import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchState implements Comparable{
    private SearchState preState;
    private Board board;
    private State agent;
    private HashMap<Position, String> been;
    private Position currAgentPosition;


    private int gCost;
    private int hCost;

    /**
     * constructor
     * @param currBoard
     * @param currAgent
     * @param gCost
     */
    public SearchState(Board currBoard, State currAgent, int gCost, SearchState pre, HashMap<Position, String> seen){
        preState = pre;
        board = currBoard;
        agent = currAgent;
        been = seen;
        currAgentPosition = new Position(currAgent.getRow(), currAgent.getCol());
        this.gCost = gCost;

    }

    /**
     * set g cost
     * @param cost
     */
    public void setgCost(int cost){
        gCost = cost;
    }

    /**
     * set h cost
     * @param cost
     */
    public void sethCost(int cost){
        hCost = cost;
    }

    /**
     * get g cost
     * @return return gcost
     */
    public int getgCost() {
        return gCost;
    }

    /**
     * get h cost
     * @return return h cost
     */
    public int gethCost(){
        return hCost;
    }

    /**
     * get Agent's position from currAgent
     * @return A position of the Agent
     */
    public Position getAgentPosition(){
        return currAgentPosition;
    }

    /**
     * this is the method for comparable, this compare tow state
     * @param o object o is actually a state object
     * @return return 0 if two state has same F cost, negative if this has less F cost, otherwise positive
     */
    public int compareTo(Object o) {
        SearchState x = (SearchState) o ;
        return (this.hCost + this.gCost) - (x.hCost + x.gCost);
    }

    /**
     * traversal alone the preState until null to find the path of the agent
     * @return return a ArrayList of string which represent the actual path start from Sydney to the current city.
     */
    public ArrayList<Position> getCurrentPath(){
        ArrayList<Position> returnList = new ArrayList<>();
        for(SearchState x = this; x != null; x = x.preState){
            returnList.add(x.getAgentCurrentPosition());
        }
        Collections.reverse(returnList);
        return returnList;
    }

    /**
     * add position to been hashmap
     * @param beenTo
     */
    public void addBeenPosition(Position beenTo){
        been.put(beenTo, "");
    }

    /**
     * @return been hashmap
     */
    public HashMap<Position, String> getBeen(){
        return been;
    }

    /**
     *
     * @param row
     * @param col
     * @return check if we have been to the state, if true we don't want to go there anymore
     */
    public boolean beenThere(int row, int col){
        return been.containsKey(new Position(row, col));
    }

    /**
     * get the agent's position at this searchState
     * @return return the path from beginning to current
     */
    public Position getAgentCurrentPosition(){
        return new Position(agent.getRow(), agent.getCol());
    }

    /**
     * find the all possible positions around the give position
     * @param agentPos
     * @return arraylist of possible positions
     */
    public ArrayList<Position> possiblePositions(Position agentPos){
        return board.possiblePositions(agentPos);
    }

    /**
     * @return true if agent has a key, false otherwise
     */
    public boolean hasKey(){
        return agent.getKey();
    }

    /**
     * @return true if agent has a axe, false otherwise
     */
    public boolean hasAxe(){
        return agent.getAxe();
    }

    /**
     * @return true if agent has a raft, false otherwise
     */
    public boolean hasRaft(){
        return agent.getRaft();
    }

    /**
     * agent got a raft by chopping tree
     */
    public void gainRaft(){
        agent.setRaft(true);
    }

    /**
     * @return number of Dynamite agent has
     */
    public int numDynamite(){
        return agent.getDynamite();
    }

    /**
     * remove certain element in the board make it become EMPTY
     * @param row
     * @param col
     */
    public void removeItemInBoard(int row, int col){
        board.removeItem(row, col);
    }

    /**
     * set tile type in the board
     * @param row
     * @param col
     * @param type
     */
    public void setTypeInBoard(int row, int col, char type){
        board.setType(row, col, type);
    }

    /**
     * after action such as open door chop tree etc, we need to create total new state with new board, agent
     * @return new state deep copy everything
     */
    public SearchState deepCopy(){
        return new SearchState(board.clone(), agent.clone(), gCost, null, new HashMap<>());

    }

    /**
     * shallow copy board, seen, agent
     * @return
     */
    public SearchState shallowCopy(){
        return new SearchState(board, agent, gCost, null, been);
    }
}
