import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class SearchItem{

    private ArrayList<Position> AStar(Board currBoard, State currAgent, Position dest){
    	ArrayList<Position> p = null;
        PriorityQueue<SearchState> statePQ = new PriorityQueue<>();
        // push the first state into the Queue
        SearchState initialState = new SearchState(currBoard, currAgent, 0, null);
        initialState.sethCost(ManhattanHeuristic(initialState.getAgentCurrentPosition(),dest));
        statePQ.add(initialState);

        while(!statePQ.isEmpty()){

        }

    	return p;
    }


    /**
     * @param src
     * @param dest
     * @return calculate manhattan distance between tow position
     */
    private int ManhattanHeuristic(Position src, Position dest){
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }
}
