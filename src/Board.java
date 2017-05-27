//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shiyun on 11/05/17.
 */
public class Board implements Cloneable {

    // these position variables are used to store the positions of items agent can interact with
    private int numTree;
    public ArrayList<Position> axe_positions;
    public ArrayList<Position> key_positions;
    public ArrayList<Position> dynamite_positions;
    public ArrayList<Position> door_positions;
    public ArrayList<Position> treasure_positions;

    // board information;
    private char[][] board;


    public Board(int rowSize, int colSize){
        numTree = 0;
        axe_positions = new ArrayList<>();
        key_positions = new ArrayList<>();
        dynamite_positions = new ArrayList<>();
        door_positions = new ArrayList<>();
        treasure_positions = new ArrayList<>();

        board = new char[rowSize][colSize];

        for(int i = 0; i < rowSize; i++){
            for(int j =0; j < colSize; j++){
                if(i == Constants.START_ROW && j == Constants.START_COL){
                    board[i][j] = Constants.EMPTY;
                } else {
                    board[i][j] = Constants.UNKNOW;
                }
            }
        }
    }
    
    /*
     * set up the board
     */
    public void setType(int row, int col, char represent){
    	this.board[row][col] =  represent;
    }
    
    /*
     * return current board
     */
    public char[][] getBoard(){
    	return this.board;
    }

    /**
     * get type of the given index
     * @param row
     * @param col
     * @return
     */
    public char getType(int row, int col){
    	return this.board[row][col];
    }


    /**
     * given view from the engine, updateBoardFromGivenView local map
     * @param view
     * @param currAgent which is the state for the current agent
     */
    public void updateBoardFromGivenView(char[][] view, State currAgent){
        if(currAgent.getDirection() == Constants.NORTH){
            //finding 0,0
            int viewRow = currAgent.getRow() - 2;
            int viewCol = currAgent.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (this.getType(viewRow + row, viewCol + col) == Constants.UNKNOW)) {
                        this.setType(viewRow + row , viewCol + col, view[row][col]);
                        this.updateItem(viewRow + row, viewCol + col);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.SOUTH){
            //finding 0,0
            int viewRow = currAgent.getRow() + 2;
            int viewCol = currAgent.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (this.getType(viewRow - row, viewCol - col) == Constants.UNKNOW)) {
                        this.setType(viewRow - row, viewCol - col, view[row][col]);
                        this.updateItem(viewRow - row,viewCol - col);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.EAST){
            //finding 0,0
            int viewRow = currAgent.getRow() - 2;
            int viewCol = currAgent.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (this.getType(viewRow + col, viewCol - row) == Constants.UNKNOW)) {
                        this.setType(viewRow + col, viewCol - row, view[row][col]);
                        this.updateItem(viewRow + col, viewCol - row);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.WEST){
            //finding 0,0
            int viewRow = currAgent.getRow() + 2;
            int viewCol = currAgent.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (this.getType(viewRow - col,viewCol + row) == Constants.UNKNOW)) {
                        this.setType(viewRow - col, viewCol + row, view[row][col]);
                        this.updateItem(viewRow - col, viewCol + row);
                    }
                }
            }
        }
    }

    /**
     * updateBoardFromGivenView the board when a action is executed
     * @param action
     */
    public void updateBoardAndStateFromGivenAction(char action, State currAgent){
        switch (action){
            case Constants.TURN_LEFT:
                currAgent.updateDirection(currAgent.getDirection()-1);
                break;
            case Constants.TURN_RIGHT:
                currAgent.updateDirection(currAgent.getDirection()+1);
                break;
            case Constants.MOVE_FORWARD:
                int agentCurrRow = currAgent.getRow(), agentCurrCol = currAgent.getCol();
                int nextMoveRow = agentCurrRow, nextMoveCol = agentCurrCol;
                switch (currAgent.getDirection()){
                    case Constants.NORTH:
                        if(this.isBoardUpdate(currAgent.getRow()-1, currAgent.getCol())){
                            nextMoveRow = currAgent.getRow()-1;
                            currAgent.setRow(currAgent.getRow()-1);
                        }
                        break;
                    case Constants.SOUTH:
                        if(this.isBoardUpdate(currAgent.getRow()+1, currAgent.getCol())){
                            nextMoveRow = currAgent.getRow()+1;
                            currAgent.setRow(currAgent.getRow()+1);
                        }
                        break;
                    case Constants.WEST:
                        if(this.isBoardUpdate(currAgent.getRow(), currAgent.getCol()-1)){
                            nextMoveCol = currAgent.getCol()-1;
                            currAgent.setCol(currAgent.getCol()-1);
                        }
                        break;
                    case Constants.EAST:
                        if(this.isBoardUpdate(currAgent.getRow(), currAgent.getCol()+1)){
                            nextMoveCol = currAgent.getCol()+1;
                            currAgent.setCol(currAgent.getCol()+1);
                        }
                        break;
                    default:
                        throw new RuntimeException();

                }

                // go to land from water set raft to false
                if(getType(agentCurrRow, agentCurrCol) == Constants.WATER
                        && getType(nextMoveRow, nextMoveCol) != Constants.WATER){
                    currAgent.setRaft(false);
                }
                //pick up things
                interact(currAgent);
                break;
            case Constants.UNLOCK_DOOR:
                int door_col = currAgent.getForwardCol();
                int door_row = currAgent.getForwardRow();
                this.removeItem(door_row,door_col);
                this.setType(door_row, door_col,Constants.EMPTY);
                break;
            case Constants.CHOP_TREE:
                int tree_col = currAgent.getForwardCol();
                int tree_row = currAgent.getForwardRow();
                currAgent.setRaft(true);
                this.removeItem(tree_row,tree_col);
                this.setType(tree_row, tree_col, Constants.EMPTY);
                break;
            case Constants.BLAST_WALL_TREE:
                int wall_col = currAgent.getForwardCol();
                int wall_row = currAgent.getForwardRow();
                if(this.getType(wall_row,wall_col) == Constants.TREE){
                    this.removeItem(wall_row,wall_col);
                }
                this.setType(wall_row, wall_col,Constants.EMPTY);
                currAgent.setDynamite(currAgent.getDynamite()-1);
        }
    }

    /**
     * update Board and Agent while a action is carried out
     */
    public void interact(State currAgent){
        switch (this.getType(currAgent.getRow(),currAgent.getCol())){
            case Constants.AXE:
                currAgent.setAxe(true);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.KEY:
                currAgent.setKey(true);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.DYNAMITE:
                currAgent.setDynamite(currAgent.getDynamite()+1);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.TREASURE:
                currAgent.setTreasure(true);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
        }
        if(this.getType(currAgent.getRow(),currAgent.getCol()) == Constants.AXE ||this.getType(currAgent.getRow(),currAgent.getCol()) == Constants.KEY ||
                this.getType(currAgent.getRow(),currAgent.getCol()) == Constants.DYNAMITE || this.getType(currAgent.getRow(),currAgent.getCol()) == Constants.TREASURE){
            this.setType(currAgent.getRow(),currAgent.getCol() ,Constants.EMPTY);
        }
    }

    /**
     * updateBoardFromGivenView number of items in the board and store the positions of these items
     * @param row
     * @param col
     */
    public void updateItem(int row, int col){
        Position k = new Position(row, col);
    	switch(board[row][col]){
    	case Constants.AXE:
            if(!containPosition(axe_positions, k)){
                axe_positions.add(k);
            }
    		break;
    	case Constants.KEY:
    		if(!containPosition(key_positions, k)){
    		    key_positions.add(k);
            }
    		break;
    	case Constants.TREE:
    		numTree ++;
    		break;
    	case Constants.DYNAMITE:
            if(!containPosition(dynamite_positions, k)){
                dynamite_positions.add(k);
            }
    		break;
    	case Constants.TREASURE:
            if(!containPosition(treasure_positions, k)){
                treasure_positions.add(k);
            }
    		break;
    	case Constants.DOOR:
            if(!containPosition(door_positions, k)){
                door_positions.add(k);
            }
    		break;
    	default:
    		break;
    	}
    }
    
    /**
     * player get the item
     * board need to remove number of items in the board and store the positions of these items
     * @param row
     * @param col
     */
    public void removeItem(int row, int col){
        Position k = new Position(row, col);
    	switch(board[row][col]){
    	case Constants.AXE:
            axe_positions.remove(k);
    		break;
    	case Constants.KEY:
    		key_positions.remove(k);
    		break;
    	case Constants.TREE:
    		numTree --;
    		break;
    	case Constants.DYNAMITE:
            dynamite_positions.remove(k);
    		break;
    	case Constants.TREASURE:
            treasure_positions.remove(k);
    		break;
    	case Constants.DOOR:
            door_positions.remove(k);
    		break;
    	default:
    		break;
    	}
    }
    
    /**
     * check if given arraylist contains certain Position
     * @param al
     * @param k
     * @return
     */
    private boolean containPosition(ArrayList<Position> al, Position k){
        for(Position i:al){
            if(i.getRow() == k.getRow() && i.getCol() == k.getCol()){
                return true;
            }
        }
        return false;
    }

    /**
     * check it is allow the current position to go to the next position
     * @param row
     * @param col
     * @return
     */
    public boolean isBoardUpdate(int row, int col){
        if(board[row][col] == Constants.WALL || board[row][col] == Constants.DOOR || board[row][col] == Constants.TREE){
            return false;
        }
        return true;
    }

    public void printMap(State player){
        boolean flag;
        System.out.println("-------------------------------board info-------------------------------");
        for(int row = 50; row < 140; row++){
            flag = false;
            for(int col = 70; col < 120; col++){
                if(row == player.getRow() && col == player.getCol()){
                    System.out.print(player.getDirectionChar());
                } else {
                    System.out.print(board[row][col]);
                    flag = true;
                }
            }
            if(flag){
                System.out.print('\n');
            }
        }
        System.out.print('\n');
        System.out.println("board_axe: " + axe_positions.size());
        System.out.println("board_key: " + key_positions.size());
        System.out.println("board_tree: " + numTree);
        System.out.println("board_door: " + door_positions.size());
        System.out.println("board_dynamite: " + dynamite_positions.size());
    }

    /**
     * create a deep copy of the game
     * @return
     */
    public Board clone(){
        Board newBoard = new Board(this.board.length, this.board[0].length);

        newBoard.numTree = this.numTree;
        newBoard.axe_positions = new ArrayList<>();
        newBoard.key_positions = new ArrayList<>();
        newBoard.dynamite_positions = new ArrayList<>();
        newBoard.door_positions = new ArrayList<>();
        newBoard.treasure_positions = new ArrayList<>();

        newBoard.axe_positions.addAll(this.axe_positions);
        newBoard.key_positions.addAll(this.key_positions);
        newBoard.dynamite_positions.addAll(this.dynamite_positions);
        newBoard.door_positions.addAll(this.door_positions);
        newBoard.treasure_positions.addAll(this.treasure_positions);

        newBoard.board = new char[this.board.length][this.board[0].length];

        for(int i = 0; i < newBoard.board.length; i++){
            newBoard.board[i] = Arrays.copyOf(this.board[i], this.board[i].length);
        }

        return newBoard;
    }

    /**
     * @return extract snapshot of the board and return that board
     */
    public Board extractBoard(SearchCompletedPath SCP){
        //find left bound col
        int leftCol = 0;
        for(int col = 0; col < this.board[0].length; col++){
            for (char[] aBoard : this.board) {
                if (aBoard[col] != Constants.UNKNOW && aBoard[col] != Constants.BOUNDARY) {
                    leftCol = col;
                    break;
                }
            }
            if (leftCol != 0) {
                break;
            }
        }

        //find right bound col
        int rightCol = Constants.BOARD_SIZE_COL - 1;
        for(int col = Constants.BOARD_SIZE_COL - 1; col >= 0; col--){
            for(char[] aBoard : this.board){
                if (aBoard[col] != Constants.UNKNOW && aBoard[col] != Constants.BOUNDARY) {
                    rightCol = col;
                    break;
                }
            }
            if (rightCol != Constants.BOARD_SIZE_COL - 1){
                break;
            }
        }

        //find the up bound row
        int upRow = 0;
        for(int row = 0; row < this.board.length; row++){
            for(int col = 0; col < this.board[row].length; col++){
                if (this.board[row][col] != Constants.UNKNOW && this.board[row][col] != Constants.BOUNDARY){
                    upRow = row;
                }
            }
            if (upRow != 0){
                break;
            }
        }

        //find the bottom bound row
        int bottomRow = Constants.BOARD_SIZE_ROW - 1;
        for(int row = Constants.BOARD_SIZE_ROW - 1; row >= 0; row--){
            for(int col = 0; col < this.board[row].length; col++){
                if (this.board[row][col] != Constants.UNKNOW && this.board[row][col] != Constants.BOUNDARY){
                    bottomRow = row;
                }
            }
            if (bottomRow != Constants.BOARD_SIZE_ROW - 1){
                break;
            }
        }

        Board newBoard = new Board(bottomRow - upRow + 1, rightCol - leftCol + 1);

        newBoard.numTree = this.numTree;

        newBoard.axe_positions = new ArrayList<>();
        newBoard.key_positions = new ArrayList<>();
        newBoard.dynamite_positions = new ArrayList<>();
        newBoard.door_positions = new ArrayList<>();
        newBoard.treasure_positions = new ArrayList<>();

        SCP.setStartRow(upRow);
        SCP.setStartCol(leftCol);
        SCP.setEndRow(bottomRow);
        SCP.setEndCol(rightCol);

        for(Position p : this.axe_positions){
            newBoard.axe_positions.add(new Position(p.getRow()-SCP.getStartRow(), p.getCol()-SCP.getStartCol()));
        }
        for(Position p : this.key_positions){
            newBoard.key_positions.add(new Position(p.getRow()-SCP.getStartRow(), p.getCol()-SCP.getStartCol()));
        }
        for(Position p : this.dynamite_positions){
            newBoard.dynamite_positions.add(new Position(p.getRow()-SCP.getStartRow(), p.getCol()-SCP.getStartCol()));
        }
        for(Position p : this.door_positions){
            newBoard.door_positions.add(new Position(p.getRow()-SCP.getStartRow(), p.getCol()-SCP.getStartCol()));
        }
        for(Position p : this.treasure_positions){
            newBoard.treasure_positions.add(new Position(p.getRow()-SCP.getStartRow(), p.getCol()-SCP.getStartCol()));
        }

        for(int row = 0; row < newBoard.board.length; row++){
            for(int col = 0; col < newBoard.board[row].length; col++){
                newBoard.board[row][col] = this.board[row + SCP.getStartRow()][col + SCP.getStartCol()];
            }
        }
        return newBoard;
    }

    /**
     * check around the Agent find all possible position the agent can potential go
     * @param agentPosition
     * @return ArrayList of all possible Positions
     */
    public ArrayList<Position> possiblePositions(Position agentPosition) {
        ArrayList<Position> posiPos = new ArrayList<>();

        int row = agentPosition.getRow();
        int col = agentPosition.getCol();

        // down direction
        if(row+1 < board.length && board[row+1][col] != Constants.UNKNOW && board[row+1][col] != Constants.BOUNDARY){
            posiPos.add(new Position(row+1, col));
        }
        if(col+1 < board[0].length && board[row][col+1] != Constants.UNKNOW && board[row][col+1] != Constants.BOUNDARY){
            posiPos.add(new Position(row, col+1));
        }
        if(row-1 >= 0 && board[row-1][col] != Constants.UNKNOW && board[row-1][col] != Constants.BOUNDARY){
            posiPos.add(new Position(row-1, col));
        }
        if(col-1 >= 0 && board[row][col-1] != Constants.UNKNOW && board[row][col-1] != Constants.BOUNDARY){
            posiPos.add(new Position(row, col-1));
        }
        return posiPos;
    }

    /**
     * print extract map
     * @param agentPos
     */
    public void printExtractMap(Position agentPos){
        boolean flag;
        for(int row = 0; row < this.board.length; row++){
            flag = false;
            for(int col = 0; col < this.board[0].length; col++){
                if(row == agentPos.getRow() && col == agentPos.getCol()){
                    System.out.print('P');
                } else {
                    System.out.print(board[row][col]);
                    flag = true;
                }
            }
            if(flag){
                System.out.print('\n');
            }
        }
        System.out.println("board info:");
        System.out.println("board_axe: " + axe_positions.size());
        System.out.println("board_key: " + key_positions.size());
        System.out.println("board_tree: " + numTree);
        System.out.println("board_door: " + door_positions.size());
        System.out.println("board_dynamite: " + dynamite_positions.size());
        System.out.println("board_treasure: " + treasure_positions.size());
    }

    public String itemsToString(){
        StringBuilder s = new StringBuilder();
        s.append("Axe: ");
        for(Position a : axe_positions){
            s.append(a.toString()).append(" ");
        }
        s.append("Key : ");
        for(Position k : key_positions){
            s.append(k.toString()).append(" ");
        }
        s.append("Dynamite: ");
        for(Position d : dynamite_positions){
            s.append(d.toString()).append(" ");
        }
        s.append("Door: ");
        for(Position dd : door_positions){
            s.append(dd.toString()).append(" ");
        }
        s.append("Treasure: ");
        for(Position t : treasure_positions){
            s.append(t).append(" ");
        }
        s.append("Num Tree: ");
        s.append(numTree);
        return s.toString();
    }
}
