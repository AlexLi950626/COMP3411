import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SearchItem{
    private Board originalBoard;
    private State originalState;
    private Position dest;
    private Position origin;

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
     * uses A star to do path finding with if in certain range don't use dynamite and use dynamite
     * @return
     */
    public ArrayList<Position> AStar(){
    	ArrayList<Position> path = null;
        PriorityQueue<SearchState> statePQ = new PriorityQueue<>();
        // push the first state into the Queue
        origin = new Position(Constants.START_ROW - originalBoard.getStartRow(),
                Constants.START_COL - originalBoard.getStartCol());
        SearchState initialState = new SearchState(originalBoard, new HashMap<String,Position>(), null, 0, originalState);
        initialState.sethCost(ManhattanHeuristic(initialState));
        statePQ.add(initialState);

        while(!statePQ.isEmpty()){
            // get a state from queue
            SearchState currentState = statePQ.poll();
            // if destination is reached return path
            Position currPosition = currentState.getAgentPosition();
            char currPositionType = currentState.getTypeInBoard(currPosition.getRow(), currPosition.getCol());
            if(currentState.hasTreasure() && origin.equals(currPosition)){
                path = currentState.getCurrentPath();
                currentState.printStatePath();
                break;
            }

            //put currPosition to been represent we been this position
            currentState.addBeenPosition(currPosition);

            ArrayList<Position> pos = currentState.possiblePositions(currPosition);

            SearchState newState = null;

            for(Position p : pos){
                char movePositionType = currentState.getTypeInBoard(p.getRow(), p.getCol());
                switch (currPositionType){
                    case Constants.WATER:
                        switch (movePositionType){
                            case Constants.WATER:
                                // curr water, next water, check if been
                                // if hasn't been add to queue shallow copy add been list
                                if(!currentState.beenThere(p)){
                                    newState = initialiseShallowCopyNewState(currentState, p);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.EMPTY:
                                // curr water, next Empty, deep copy but should not set current position to EMpty
                                // set raft false
                                newState = currentState.deepCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.setAgentPostion(p);
                                newState.addBeenPosition(p);
                                newState.setRaft(false);
                                newState.sethCost(ManhattanHeuristic(newState));
                                statePQ.add(newState);
                                break;
                            case Constants.TREE:
                                //curr water, next tree, if has axe can chop the tree, but can't add raft, set raft to false
                                if(currentState.hasAxe()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    statePQ.add(newState);
                                } else if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    //curr water, next tree, if has dynamite can blow up, but check should we blow up first
                                    // and if we have enough dynamite, set raft to false
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    newState.setRaft(false);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.DOOR:
                                //curr water, next door, if has key, open door, deep copy, set raft to false
                                if(currentState.hasKey()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.WALL:
                                //curr water, next Wall, if has enough dynamite check should we blow up, set raft to false
                                if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.AXE:
                                //curr water, next axe, deep copy, set raft false, get axe
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setAxe(true);
                                statePQ.add(newState);
                                break;
                            case Constants.KEY:
                                //curr water, next key, deep copy, set raft false, get key
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setKey(true);
                                statePQ.add(newState);
                                break;
                            case Constants.DYNAMITE:
                                //curr water, next dynamite, deep copy, set raft false, get dynamite
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setDynamite(newState.numDynamite()+1);
                                statePQ.add(newState);
                                break;
                            case Constants.TREASURE:
                                //curr water, next treasure, deep copy, set raft false, get treasure
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setTreasure(true);
                                statePQ.add(newState);
                                break;
                            default:
                                System.out.println("Unexpected movePositionType: " + movePositionType);
                                throw new RuntimeException();
                        }
                        break;
                    case Constants.EMPTY:
                        switch (movePositionType){
                            case Constants.WATER:
                                //curr empty, next water, need to have raft first
                                //deep copy, nothing else change
                                //should not change the type of the next tile
                                if(currentState.hasRaft()){
                                    newState = currentState.deepCopy();
                                    newState.setgCost(currentState.getgCost() + 1);
                                    newState.setAgentPostion(p);
                                    newState.addBeenPosition(p);
                                    newState.sethCost(ManhattanHeuristic(newState));
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.EMPTY:
                                //curr Empty, next empty
                                //shallow copy check if been
                                if(!currentState.beenThere(p)){
                                    newState = initialiseShallowCopyNewState(currentState, p);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.TREE:
                                //curr empty, next tree
                                //check axe, deep copy, set raft
                                if(currentState.hasAxe()) {
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(true);
                                    statePQ.add(newState);
                                }  else if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    //curr empty, next tree, if has dynamite can blow up, but check should we blow up first
                                    // and if we have enough dynamite
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.DOOR:
                                //curr empty, next door, check if has key,
                                if(currentState.hasKey()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.WALL:
                                //curr empty, next wall,
                                //check if has enough dynamite, should we blow up?
                                if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.AXE:
                                //curr empty, next axe
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setAxe(true);
                                statePQ.add(newState);
                                break;
                            case Constants.KEY:
                                //curr empty, next key
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setKey(true);
                                statePQ.add(newState);
                                break;
                            case Constants.DYNAMITE:
                                //curr empty, next dynamite
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setDynamite(newState.numDynamite()+1);
                                statePQ.add(newState);
                                break;
                            case Constants.TREASURE:
                                //curr empty, next treasure
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setTreasure(true);
                                statePQ.add(newState);
                                break;
                            default:
                                System.out.println("Unexpected movePositionType: " + movePositionType);
                                throw new RuntimeException();
                        }
                        break;
                    default:
                        //as we should only be stand on either Empty or on Water
                        System.out.println("Unexpected currPositionType: " + currPositionType);
                        throw new RuntimeException();
                }
            }
        }
    	return convertCoordinates(path);
    }

    public SearchState initialiseDeepCopyNewState(SearchState currentState, Position nextMove){
        SearchState newState = currentState.deepCopy();
        newState.setgCost(currentState.getgCost() + 1);
        newState.removeItemInBoard(nextMove.getRow(),nextMove.getCol()); //remove tree
        newState.setTypeInBoard(nextMove.getRow(), nextMove.getCol(), Constants.EMPTY); //replace by EMPTY
        newState.setAgentPostion(nextMove);
        newState.addBeenPosition(nextMove);
        newState.sethCost(ManhattanHeuristic(newState));
        return newState;
    }

    public SearchState initialiseShallowCopyNewState(SearchState currentState, Position nextMove){
        SearchState newState = currentState.shallowCopy();
        newState.setgCost(currentState.getgCost() + 1);
        newState.setAgentPostion(nextMove);
        newState.addBeenPosition(nextMove);
        newState.sethCost(ManhattanHeuristic(newState));
        return newState;
    }


    /**
     * @param state
     * @return calculate manhattan distance between tow position
     */
    private int ManhattanHeuristic(SearchState state){
        //return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
        Position currPosition = state.getAgentPosition();
        if(state.hasTreasure()){
            return Math.abs(currPosition.getRow() - origin.getRow()) + Math.abs(currPosition.getCol() - origin.getCol());
        } else {
            int h = 0;
            // first go to axe
            if(!state.board.axe_positions.isEmpty()){
                Position axe = state.board.axe_positions.get(0);
                h += Math.abs(currPosition.getRow() - axe.getRow()) + Math.abs(currPosition.getCol() - axe.getCol());
                currPosition = axe;
            }
            //second find the keys
            if(!state.board.key_positions.isEmpty()){
                Position key = state.board.key_positions.get(0);
                h += Math.abs(currPosition.getRow() - key.getRow()) + Math.abs(currPosition.getCol() - key.getCol());
                currPosition = key;
            }
            //thrid find the doors
            if(!state.board.door_positions.isEmpty()){
                ArrayList<Position> doors = new ArrayList<>();
                doors.addAll(state.board.door_positions);
                while(!doors.isEmpty()){
                    int minIndex = 0;
                    int minDis = Math.abs(currPosition.getRow() - doors.get(0).getRow()) + Math.abs(currPosition.getCol() - doors.get(0).getCol());
                    int tempDist;
                    for(int i = 0; i < doors.size(); i++){
                        tempDist = Math.abs(currPosition.getRow() - doors.get(i).getRow()) + Math.abs(currPosition.getCol() - doors.get(i).getCol());
                        if(minDis > tempDist){
                            minIndex = i;
                            minDis = tempDist;
                        }
                    }
                    h += minDis;
                    currPosition = doors.get(minIndex);

                    doors.remove(minIndex);
                }
            }
            //fourth find the dynamites
            if(!state.board.dynamite_positions.isEmpty()){
                ArrayList<Position> dynamite = new ArrayList<>();
                dynamite.addAll(state.board.dynamite_positions);
                while(!dynamite.isEmpty()){
                    int minDis = Math.abs(currPosition.getRow() - dynamite.get(0).getRow())
                            + Math.abs(currPosition.getCol() - dynamite.get(0).getCol());
                    int minIndex = 0;
                    int tempDist;
                    for(int i = 0; i < dynamite.size(); i++){
                        tempDist = Math.abs(currPosition.getRow() - dynamite.get(i).getRow())
                                + Math.abs(currPosition.getCol() - dynamite.get(i).getCol());
                        if(minDis > tempDist){
                            minIndex = i;
                            minDis = tempDist;
                        }
                    }
                    h += minDis;
                    currPosition = dynamite.get(minIndex);

                    dynamite.remove(minIndex);
                }
            }
            // treasure to original spot
            if(!state.board.treasure_positions.isEmpty()){
                Position treas = state.board.treasure_positions.get(0);
                h += Math.abs(treas.getRow() - currPosition.getRow()) + Math.abs(treas.getCol() - currPosition.getCol());
                h += Math.abs(treas.getRow() - origin.getRow()) + Math.abs(treas.getCol() - origin.getCol());
            }
            return h;
        }
    }
    /**
     * convert path from snapshot map coordinate to global map coordinate
     * @param path
     * @return converted path
     */
    private ArrayList<Position> convertCoordinates(ArrayList<Position> path){
        for (Position pos : path) {
            pos.setRow(pos.getRow()+originalBoard.getStartRow());
            pos.setCol(pos.getCol()+originalBoard.getStartCol());
        }
        return path;
    }
}
