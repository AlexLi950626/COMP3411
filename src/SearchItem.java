import javax.print.attribute.standard.MediaName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SearchItem{
    Board originalBoard;
    State originalState;
    Position dest;

    /**
     * constructor for the search item object to do path finding
     * @param currBoard
     * @param currAgent
     * @param dest
     */
    public SearchItem(Board currBoard, State currAgent, Position dest){
        originalBoard = currBoard;
        originalState = currAgent;
        this.dest = dest;
    }

    /**
     * uses A star to do path finding
     * @return
     */
    public ArrayList<Position> AStar(){
    	ArrayList<Position> path = null;
        PriorityQueue<SearchState> statePQ = new PriorityQueue<>();
        // push the first state into the Queue
        SearchState initialState = new SearchState(originalBoard, new HashMap<>(), null, 0, originalState);
        initialState.sethCost(ManhattanHeuristic(initialState.getAgentPosition()));
        statePQ.add(initialState);

        while(!statePQ.isEmpty()){
            // get a state from queue
            SearchState currentState = statePQ.poll();
            // if destination is reached return path
            Position currPosition = currentState.getAgentPosition();
            char currPositionType = currentState.getTypeInBoard(currPosition.getRow(), currPosition.getCol());
            if(dest.equals(currPosition)){
                path = currentState.getCurrentPath();
                break;
            }

            //put currPosition to been represent we been this position
            currentState.addBeenPosition(currPosition);

            ArrayList<Position> pos = currentState.possiblePositions(currPosition);

            SearchState newState;

            for(Position p : pos){
                char movePositionType = currentState.getTypeInBoard(p.getRow(), p.getCol());
                switch (movePositionType){
                    case Constants.TREE:
                        // as if you are in water you can't get raft
                        if(currentState.hasAxe() && currPositionType != Constants.WATER){
                            newState = currentState.deepCopy();
                            newState.setgCost(currentState.getgCost() + 1);
                            newState.removeItemInBoard(p.getRow(),p.getCol()); //remove tree
                            newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY); //replace by EMPTY
                            newState.setRaft(true);
                            newState.setAgentPostion(new Position(p.getRow(), p.getCol()));
                            newState.sethCost(ManhattanHeuristic(p));
                            newState.addBeenPosition(p);
                            statePQ.add(newState);
                        } else if (currentState.numDynamite() > 0 && currPositionType != Constants.WATER){
                            newState = currentState.deepCopy();
                            newState.setgCost(currentState.getgCost() + 1);
                            newState.removeItemInBoard(p.getRow(), p.getCol());// remove tree
                            newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY);
                            newState.setDynamite(newState.numDynamite()-1);
                            newState.setAgentPostion(p);
                            newState.sethCost(ManhattanHeuristic(p));
                            newState.addBeenPosition(p);
                            statePQ.add(newState);
                        }
                        break;
                    case Constants.DOOR:
                        if(currentState.hasKey()){
                            newState = currentState.deepCopy();
                            newState.setgCost(currentState.getgCost() + 1);
                            newState.removeItemInBoard(p.getRow(),p.getCol()); //remove door
                            newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY); //replace by EMPTY
                            newState.setAgentPostion(p);
                            newState.sethCost(ManhattanHeuristic(p));
                            newState.addBeenPosition(p);
                            statePQ.add(newState);
                        }
                        break;
                    case Constants.WALL:
                        if (currentState.numDynamite() > 0){
                            newState = currentState.deepCopy();
                            newState.setgCost(currentState.getgCost() + 1);
                            newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY);
                            newState.setDynamite(newState.numDynamite()-1);
                            newState.setAgentPostion(p);
                            newState.sethCost(ManhattanHeuristic(p));
                            newState.addBeenPosition(p);
                            statePQ.add(newState);
                        }
                        break;
                    case Constants.WATER:
                        if(!currentState.beenThere(p)){
                            if(currPositionType == Constants.WATER){
                                newState = currentState.shallowCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.sethCost(ManhattanHeuristic(p));
                                newState.setAgentPostion(p);
                                // if next move is water, we are current in water and we haven't been there before
                                // add it to been
                                newState.addBeenPosition(p);
                                statePQ.add(newState);
                            } else if (currentState.hasRaft()) {
                                //current at land EMPTY, if we want to add this situation make sure we have raft
                                //going to land will result a new complete new state
                                newState = currentState.deepCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.setRaft(false);
                                newState.setAgentPostion(p);
                                newState.sethCost(ManhattanHeuristic(p));
                                newState.addBeenPosition(p);
                                statePQ.add(newState);
                            } else {
                                System.out.println("next move is water " + "from " + currPositionType + "haven't been there, with no raft" );
                            }
                        } else {
                            System.out.println("next move is water " + "from " + currPositionType + "been there" );

                        }
                        break;
                    case Constants.EMPTY:
                        if(!currentState.beenThere(p)){
                            if(currPositionType == Constants.EMPTY) {
                                newState = currentState.shallowCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.setAgentPostion(p);
                                newState.sethCost(ManhattanHeuristic(p));
                                newState.addBeenPosition(p);
                                statePQ.add(newState);
                            } else if(currPositionType == Constants.WATER){
                                newState = currentState.deepCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.setAgentPostion(p);
                                newState.sethCost(ManhattanHeuristic(p));
                                newState.addBeenPosition(p);
                                statePQ.add(newState);
                            } else {
                                System.out.println("next move is EMPTY " + "from " + currPositionType + "haven't been there" );
                            }
                        } else {
                            System.out.println("next move is EMPTY " + "from " + currPositionType + "been there" );
                        }
                        break;
                    case Constants.AXE:
                        newState = currentState.deepCopy();
                        newState.setgCost(currentState.getgCost() + 1);
                        newState.setAxe(true);
                        newState.removeItemInBoard(p.getRow(), p.getCol());
                        newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY);
                        newState.setAgentPostion(p);
                        newState.sethCost(ManhattanHeuristic(p));
                        newState.addBeenPosition(p);
                        statePQ.add(newState);
                        break;
                    case Constants.DYNAMITE:
                        newState = currentState.deepCopy();
                        newState.setgCost(currentState.getgCost() + 1);
                        newState.setDynamite(newState.numDynamite()+1);
                        newState.removeItemInBoard(p.getRow(), p.getCol());
                        newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY);
                        newState.setAgentPostion(p);
                        newState.sethCost(ManhattanHeuristic(p));
                        newState.addBeenPosition(p);
                        statePQ.add(newState);
                        break;
                    case Constants.TREASURE:
                        newState = currentState.deepCopy();
                        newState.setgCost(currentState.getgCost() + 1);
                        newState.setTreasure(true);
                        newState.removeItemInBoard(p.getRow(), p.getCol());
                        newState.setTypeInBoard(p.getRow(), p.getCol(), Constants.EMPTY);
                        newState.setAgentPostion(p);
                        newState.sethCost(ManhattanHeuristic(p));
                        newState.addBeenPosition(p);
                        statePQ.add(newState);
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
     * @return calculate manhattan distance between tow position
     */
    private int ManhattanHeuristic(Position src){
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }

}
