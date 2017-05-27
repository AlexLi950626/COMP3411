//import com.sun.corba.se.impl.orbutil.closure.Constant;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.Collections;

public class SearchState implements Comparable{
    private SearchState preState;
    public Board board;

    private Position currAgentPosition;
    // if the current state has any of these tools
    private boolean axe;
    private boolean raft;
    private boolean key;
    private int dynamite;
    private boolean treasure;

    public ArrayList<Position> usedDynamite;

    private int gCost;
    private int hCost;

    /**
     * constructor from beginning of A star
     * @param currBoard
     * @param currAgent
     * @param gCost
     */
    public SearchState(Board currBoard, SearchState pre, int gCost, State currAgent){
        preState = pre;
        board = currBoard;
        currAgentPosition = new Position(currAgent.getRow(), currAgent.getCol());
        this.gCost = gCost;
        axe = currAgent.getAxe();
        raft = currAgent.getRaft();
        key = currAgent.getKey();
        dynamite = currAgent.getDynamite();
        treasure = currAgent.getTreasure();
        usedDynamite = new ArrayList<>();
    }

    /**
     * another constructor construct next state for search
     * @param currBoard
     * @param pre
     * @param
     */
    public SearchState(Board currBoard, SearchState pre, int gCost){
        preState = pre;
        board = currBoard;
        this.gCost = gCost;

        currAgentPosition = pre.getAgentPosition();
        axe = pre.hasAxe();
        raft = pre.hasRaft();
        key = pre.hasKey();
        dynamite = pre.numDynamite();
        treasure = pre.hasTreasure();
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
     * get Agent's position from currAgent
     * @return A position of the Agent
     */
    public Position getAgentPosition(){
        return currAgentPosition;
    }

    /**
     * set Agent's position
     * @param now
     */
    public void setAgentPostion(Position now){
        currAgentPosition = now;
    }

    /**
     * this is the method for comparable, this compare tow state
     * @param o object o is actually a state object
     * @return return 0 if two state has same F cost, negative if this has less F cost, otherwise positive
     */
    public int compareTo(Object o) {
        SearchState x = (SearchState) o ;
        //return this.hCost - x.hCost;
        return (4*this.hCost + 2*this.gCost) - (4*x.hCost + 2*x.gCost);
        //return (this.hCost + this.gCost) - (x.hCost + x.gCost);
    }

    /**
     * traversal alone the preState until null to find the path of the agent
     * @return return a ArrayList of string which represent the actual path start from Sydney to the current city.
     */
    public ArrayList<Position> getCurrentPath(){
        ArrayList<Position> returnList = new ArrayList<>();
        for(SearchState x = this; x != null; x = x.preState){
            returnList.add(x.getAgentPosition());
        }
        Collections.reverse(returnList);
        return returnList;
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
        return key;
    }

    /**
     * typical setter set if agent has key
     * @param b
     */
    public void setKey(boolean b){
        key = b;
    }

    /**
     * @return true if agent has a axe, false otherwise
     */
    public boolean hasAxe(){
        return axe;
    }

    /**
     * typical setter set if agent has axe
     * @param b
     */
    public void setAxe(boolean b){
        axe = b;
    }

    /**
     * @return true if agent has a raft, false otherwise
     */
    public boolean hasRaft(){
        return raft;
    }

    /**
     * set raft status
     * @param b
     */
    public void setRaft(boolean b){
        raft = b;
    }

    /**
     * @return number of Dynamite agent has
     */
    public int numDynamite(){
        return dynamite;
    }

    /**
     * set dynamite number
     * @param num
     */
    public void setDynamite(int num){
        dynamite = num;
    }

    /**
     * typical setter set if agent has treasure
     * @param b
     */
    public void setTreasure(boolean b){
        treasure = b;
    }
    /**
     * @return if agent has treasure return true, false otherwise
     */
    public boolean hasTreasure(){
        return treasure;
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
     * get type char at specific index in board
     * @param row
     * @param col
     * @return
     */
    public char getTypeInBoard(int row, int col){
        return board.getType(row, col);
    }

    /**
     * after action such as open door chop tree etc, we need to create total new state with new board, agent
     * @return new state deep copy everything
     */
    public SearchState deepCopy(){
        return new SearchState(board.clone(), this, gCost);

    }

    /**
     * shallow copy board, seen, agent
     * @return
     */
    public SearchState shallowCopy(){
        return new SearchState(board,this, gCost);
    }


    /**
     * check if we should blow up this position
     * @param blowSpot
     * @return boolean determine if we should add this state to priority queue
     */
    public boolean shouldIBlowUp(Position blowSpot){
        int range = 3 + dynamite*2;
        char[][] tempBoard = board.getBoard();
        int startRangeCol = blowSpot.getCol() - (range - 1)/2;
        int startRangeRow = blowSpot.getRow() - (range - 1)/2;
        int endRangeCol = blowSpot.getCol() + (range - 1)/2;
        int endRangeRow = blowSpot.getRow() + (range - 1)/2;

        if(startRangeCol < 0) startRangeCol = 0;
        if(startRangeRow < 0) startRangeRow = 0;
        if(endRangeCol > tempBoard[0].length - 1) endRangeCol = tempBoard[0].length - 1;
        if(endRangeRow > tempBoard.length - 1) endRangeRow = tempBoard.length - 1;

        for(int i = startRangeRow; i <= endRangeRow; i++){
            for(int j = startRangeCol; j <= endRangeCol; j++){
                if(tempBoard[i][j] == Constants.AXE ||
                        tempBoard[i][j] == Constants.DOOR ||
                        tempBoard[i][j] == Constants.DYNAMITE ||
                        tempBoard[i][j] == Constants.KEY ||
                        tempBoard[i][j] == Constants.TREASURE){
                    return true;
                }
            }
        }
        return false;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("currentPos: ").append(currAgentPosition.toString()).append(" ");
        s.append("hasAxe: ").append(axe).append(" ");
        s.append("hasRaft: ").append(raft).append(" ");
        s.append("hasKey: ").append(key).append(" ");
        s.append("numDynamite: ").append(dynamite).append(" ");
        s.append("usedDynamite: ");
        for(Position p : usedDynamite){
            s.append(p.toString()).append(" ");
        }
        s.append("Treasure: ").append(treasure).append(" ");
        s.append(board.itemsToString());

        return s.toString();
    }

    public void printSearchState(){
        System.out.println("-----------------------------------------------------------------------------------------");
        board.printExtractMap(currAgentPosition);
        if(preState != null){
            System.out.println("PrePosition is: " + preState.getAgentPosition().toString());
        } else {
            System.out.println("PrePosition is: null");
        }

        System.out.println("CurrPosition is: " + currAgentPosition.toString());
        System.out.println("Raft: " + hasRaft());
        System.out.println("Key: " + hasKey());
        System.out.println("Axe: " + hasAxe());
        System.out.println("Treasure: " + hasTreasure());
        System.out.println("Dynamite: " + numDynamite());
        System.out.println("gCost: " + gCost + " hCost: " + hCost);


        System.out.println("");
    }

    public void printStatePath(){
        ArrayList<SearchState> ss = new ArrayList<>();
        for(SearchState s = this; s != null; s = s.preState){
            ss.add(s);
        }
        Collections.reverse(ss);
        for(SearchState k : ss){
            k.printSearchState();
        }
    }
}