import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * the main planning class, search for the solution for the map
 */
public class SearchCompletedPath{
    private Board originalBoard;
    private State originalState;
    private Position origin;

    private int startCol;
    private int startRow;
    private int endCol;
    private int endRow;

    private HashMap<String, String> beenState;

    /**
     * constructor for the search item object to do path finding
     */
    public SearchCompletedPath(){
        beenState = new HashMap<>();
    }


    public void setOriginalBoard(Board snapshotBoard){
        originalBoard = snapshotBoard;
    }

    public void setOriginalState(State snapshotState){
        originalState = snapshotState;
    }

    public void setStartCol(int col){
        startCol = col;
    }

    public void setStartRow(int row){
        startRow = row;
    }

    public void setEndCol(int col){
        endCol = col;
    }

    public void setEndRow(int row){
        endRow = row;
    }

    public int getStartCol(){
        return startCol;
    }

    public int getStartRow(){
        return startRow;
    }

    public int getEndCol(){
        return endCol;
    }

    public int getEndRow(){
        return endRow;
    }

    private void addBeenState(SearchState sState){
        beenState.put(sState.toString(), "");
    }

    private boolean hasBeenState(SearchState sState){
        return beenState.containsKey(sState.toString());
    }

    /**
     * uses A star to do path finding with if in certain range don't use dynamite and use dynamite
     * @return
     */
    public ArrayList<Position> AStar(){
    	ArrayList<Position> path = null;
        PriorityQueue<SearchState> statePQ = new PriorityQueue<>();
        // push the first state into the Queue
        origin = new Position(Constants.START_ROW - startRow,
                Constants.START_COL - startCol);
        SearchState initialState = new SearchState(originalBoard, null, 0, originalState);
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
                break;
            }

            //put currState to been represent we been this state
            addBeenState(currentState);

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
                                newState = initialiseShallowCopyNewState(currentState, p);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.EMPTY:
                                // curr water, next Empty, deep copy but should not set current position to EMpty
                                // set raft false
                                newState = currentState.deepCopy();
                                newState.setgCost(currentState.getgCost() + 1);
                                newState.setAgentPostion(p);
                                newState.setRaft(false);
                                newState.usedDynamite = new ArrayList<>();
                                newState.usedDynamite.addAll(currentState.usedDynamite);
                                newState.sethCost(ManhattanHeuristic(newState));
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.TREE:
                                //curr water, next tree, if has axe can chop the tree, but can't add raft, set raft to false
                                if(currentState.hasAxe()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                } else if(currentState.numDynamite() > 0 && (currentState.shouldIBlowUp(p))){
                                    //curr water, next tree, if has dynamite can blow up, but check should we blow up first
                                    // and if we have enough dynamite, set raft to false
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    newState.usedDynamite.add(p);
                                    newState.setRaft(false);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.DOOR:
                                //curr water, next door, if has key, open door, deep copy, set raft to false
                                if(currentState.hasKey()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                } else if(currentState.numDynamite() > 0  && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(false);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    newState.usedDynamite.add(p);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.WALL:
                                //curr water, next Wall, if has enough dynamite check should we blow up, set raft to false
                                if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.usedDynamite.add(p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    newState.setRaft(false);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.AXE:
                                //curr water, next axe, deep copy, set raft false, get axe
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setAxe(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.KEY:
                                //curr water, next key, deep copy, set raft false, get key
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setKey(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.DYNAMITE:
                                //curr water, next dynamite, deep copy, set raft false, get dynamite
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setDynamite(newState.numDynamite()+1);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.TREASURE:
                                //curr water, next treasure, deep copy, set raft false, get treasure
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setRaft(false);
                                newState.setTreasure(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            default:

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
                                    newState.sethCost(ManhattanHeuristic(newState));
                                    newState.usedDynamite = new ArrayList<>();
                                    newState.usedDynamite.addAll(currentState.usedDynamite);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.EMPTY:
                                //curr Empty, next empty
                                //shallow copy check if been
                                newState = initialiseShallowCopyNewState(currentState, p);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.TREE:
                                //curr empty, next tree
                                //check axe, deep copy, set raft
                                if(currentState.hasAxe()) {
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setRaft(true);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }  else if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    //curr empty, next tree, if has dynamite can blow up, but check should we blow up first
                                    // and if we have enough dynamite
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    newState.usedDynamite.add(p);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.DOOR:
                                //curr empty, next door, check if has key, or dynamite
                                if(currentState.hasKey()){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                } else if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.usedDynamite.add(p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.WALL:
                                //curr empty, next wall,
                                //check if has enough dynamite, should we blow up?
                                if(currentState.numDynamite() > 0 && currentState.shouldIBlowUp(p)){
                                    newState = initialiseDeepCopyNewState(currentState, p);
                                    newState.usedDynamite.add(p);
                                    newState.setDynamite(newState.numDynamite()-1);
                                    if(!hasBeenState(newState)){
                                        addBeenState(newState);
                                        statePQ.add(newState);
                                    }
                                }
                                break;
                            case Constants.AXE:
                                //curr empty, next axe
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setAxe(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.KEY:
                                //curr empty, next key
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setKey(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.DYNAMITE:
                                //curr empty, next dynamite
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setDynamite(newState.numDynamite()+1);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            case Constants.TREASURE:
                                //curr empty, next treasure
                                newState = initialiseDeepCopyNewState(currentState, p);
                                newState.setTreasure(true);
                                if(!hasBeenState(newState)){
                                    addBeenState(newState);
                                    statePQ.add(newState);
                                }
                                break;
                            default:
                                throw new RuntimeException();
                        }
                        break;
                    default:
                        //as we should only be stand on either Empty or on Water
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
        newState.usedDynamite = new ArrayList<>();
        newState.usedDynamite.addAll(currentState.usedDynamite);
        newState.sethCost(ManhattanHeuristic(newState));
        return newState;
    }

    public SearchState initialiseShallowCopyNewState(SearchState currentState, Position nextMove){
        SearchState newState = currentState.shallowCopy();
        newState.setgCost(currentState.getgCost() + 1);
        newState.setAgentPostion(nextMove);
        newState.usedDynamite = currentState.usedDynamite;
        newState.sethCost(ManhattanHeuristic(newState));
        return newState;
    }


    /**
     * @param state
     * @return calculate manhattan distance between tow position
     */
    private int ManhattanHeuristic(SearchState state){
        Position currPosition = state.getAgentPosition();
        if(state.hasTreasure()){
            return Math.abs(currPosition.getRow() - origin.getRow()) + Math.abs(currPosition.getCol() - origin.getCol());
        } else {
            int h = 0;

            ArrayList<Position> allItems = new ArrayList<>();
            // first go to axe
            allItems.addAll(state.board.axe_positions);
            //second find the keys
            allItems.addAll(state.board.key_positions);
            //thrid find the doors
            allItems.addAll(state.board.door_positions);
            //fourth find the dynamites
            allItems.addAll(state.board.dynamite_positions);

            if(!allItems.isEmpty()){
                while(!allItems.isEmpty()){
                    int minDis = Math.abs(currPosition.getRow() - allItems.get(0).getRow())
                            + Math.abs(currPosition.getCol() - allItems.get(0).getCol());
                    int minIndex = 0;
                    int tempDist;
                    for(int i = 0; i < allItems.size(); i++){
                        tempDist = Math.abs(currPosition.getRow() - allItems.get(i).getRow())
                                + Math.abs(currPosition.getCol() - allItems.get(i).getCol());
                        if(minDis > tempDist){
                            minIndex = i;
                            minDis = tempDist;
                        }
                    }
                    h += minDis;
                    currPosition = allItems.get(minIndex);

                    allItems.remove(minIndex);
                }
            }
            // treasure to original spot
            if(!state.board.treasure_positions.isEmpty()){
                Position treas = state.board.treasure_positions.get(0);
                //from current point to treasure
                h += Math.abs(treas.getRow() - currPosition.getRow()) + Math.abs(treas.getCol() - currPosition.getCol());
                //from treasure to origin
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
            pos.setRow(pos.getRow()+ startRow);
            pos.setCol(pos.getCol()+ startCol);
        }
        return path;
    }
}
