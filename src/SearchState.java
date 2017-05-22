import java.util.ArrayList;
import java.util.Collections;

public class SearchState implements Comparable{
    private SearchState preState;
    private Board board;
    private State agent;



    private int gCost;
    private int hCost;

    /**
     * constructor
     * @param currBoard
     * @param currAgent
     * @param gCost
     */
    public SearchState(Board currBoard, State currAgent, int gCost, SearchState pre){
        board = currBoard.clone();
        agent = currAgent.clone();
        this.gCost = gCost;
        preState = pre;
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
     * @return
     */
    public int getgCost() {
        return gCost;
    }

    /**
     * get h cost
     * @return
     */
    public int gethCost(){
        return hCost;
    }

    /**
     * this is the method for comparable, this compare tow state
     * @param o
     * @pre object o is actually a state object
     * @post return 0 if two state has same F cost, negative if this has less F cost, otherwise positive
     */
    public int compareTo(Object o) {
        SearchState x = (SearchState) o ;
        return (this.hCost + this.gCost) - (x.hCost + x.gCost);
    }

    /**
     * traversal alone the preState until null to find the path of the agent
     * @post return a ArrayList of string which represent the actual path start from Sydney to the current city.
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
     * get the agent's position at this searchState
     * @return
     */
    public Position getAgentCurrentPosition(){
        return new Position(agent.getRow(), agent.getCol());
    }
}
