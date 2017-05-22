import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SearchItem{

    public ArrayList<Position> AStar(Board currBoard, State currAgent, Position dest){
    	ArrayList<Position> path = null;
        PriorityQueue<SearchState> statePQ = new PriorityQueue<>();
        // push the first state into the Queue
        SearchState initialState = new SearchState(currBoard, currAgent, 0, null, new HashMap<>());
        initialState.sethCost(ManhattanHeuristic(initialState.getAgentCurrentPosition(),dest));
        statePQ.add(initialState);

        while(!statePQ.isEmpty()){
            // get a state from queue
            SearchState currentState = statePQ.poll();
            // if destination is reached return path
            Position currPosition = currentState.getAgentPosition();
            if(dest.equals(currPosition)){
                path = currentState.getCurrentPath();
                break;
            }

            //put currPosition to been represent we been this position
            currentState.addBeenPosition(currPosition);

            ArrayList<Position> pos = currentState.possiblePositions(currPosition);

            SearchState newState;

            for(Position p : pos){
                char move = currBoard.getType(p.getRow(), p.getCol());
                switch (move){
                    case Constants.TREE:
                        if(currentState.hasAxe()){
                            newState = currentState.deepCopy();
                            newState.setgCost(currentState.getgCost() + 1);
                            newState.removeItemInBoard(p.getRow(),p.getCol()); //remove tree
                            newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY); //replace by EMPTY
                            currAgent.setRaft(true);

                            newState.

                        } else if (currentState.numDynamite() > 0){

                        }
                        break;
                    case Constants.DOOR:
                        break;
                    case Constants.WALL:
                        break;
                    case Constants.WATER:
                        break;
                    case Constants.EMPTY:
                        break;
                    case Constants.AXE:
                        break;
                    case Constants.DYNAMITE:
                        break;
                    case Constants.TREASURE:
                        break;
                    default:
                        System.out.println("Unexpected Case during A* ");
                }
            }



        }

    	return path;
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
