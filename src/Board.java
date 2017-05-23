import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shiyun on 11/05/17.
 */
public class Board implements Cloneable {
    // if the known board has any of these tools
    private boolean board_axe;
    private boolean board_key;
    private int board_dynamite;
    private int board_tree;
    private int board_door;
    private boolean board_treasure;

    // these position variables are used to store the positions of items agent can interact with
    private ArrayList<Position> axe_positions;
    private ArrayList<Position> key_positions;
    private ArrayList<Position> dynamite_positions;
    private ArrayList<Position> door_positions;
    private ArrayList<Position> treasure_positions;
    private ArrayList<Position> tree_positions;

    // board information;
    private char[][] board;
    private int startCol;
    private int startRow;
    private int endCol;
    private int endRow;

//    /**
//     * constructor for A star search board
//     * @param currBoard
//     */
//    public Board(Board currBoard){
//    }

    public Board(int rowSize, int colSize){
    	//initialize the board info
        board_axe = false;
        board_key = false;
        board_tree = 0;
        board_door = 0;
        board_dynamite = 0;
        board_treasure = false;

        // A* use
        axe_positions = new ArrayList<>();
        key_positions = new ArrayList<>();
        dynamite_positions = new ArrayList<>();
        door_positions = new ArrayList<>();
        treasure_positions = new ArrayList<>();
        tree_positions = new ArrayList<>();

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
                switch (currAgent.getDirection()){
                    case Constants.NORTH:
                        if(this.isBoardUpdate(currAgent.getRow()-1, currAgent.getCol())){
                            currAgent.setRow(currAgent.getRow()-1);
                        }
                        break;
                    case Constants.SOUTH:
                        if(this.isBoardUpdate(currAgent.getRow()+1, currAgent.getCol())){
                            currAgent.setRow(currAgent.getRow()+1);
                        }
                        break;
                    case Constants.WEST:
                        if(this.isBoardUpdate(currAgent.getRow(), currAgent.getCol()-1)){
                            currAgent.setCol(currAgent.getCol()-1);
                        }
                        break;
                    case Constants.EAST:
                        if(this.isBoardUpdate(currAgent.getRow(), currAgent.getCol()+1)){
                            currAgent.setCol(currAgent.getCol()+1);
                        }
                }
                System.out.println(this.getType(currAgent.getRow(),currAgent.getCol()));
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
    		board_axe = true;
            if(!containPosition(axe_positions, k)){
                axe_positions.add(k);
            }
    		break;
    	case Constants.KEY:
    		board_key = true;
    		if(!containPosition(key_positions, k)){
    		    key_positions.add(k);
            }
    		break;
    	case Constants.TREE:
    		board_tree ++;
    		if(!containPosition(tree_positions, k)){
    		    tree_positions.add(k);
            }
    		break;
    	case Constants.DYNAMITE:
    		board_dynamite ++;
            if(!containPosition(dynamite_positions, k)){
                dynamite_positions.add(k);
            }
    		break;
    	case Constants.TREASURE:
    		board_treasure = true;
            if(!containPosition(treasure_positions, k)){
                treasure_positions.add(k);
            }
    		break;
    	case Constants.DOOR:
    		board_door++;
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
    		board_axe = false;
            axe_positions.remove(k);
    		break;
    	case Constants.KEY:
    		board_key = false;
    		key_positions.remove(k);
    		break;
    	case Constants.TREE:
    		board_tree --;
    		tree_positions.remove(k);
    		break;
    	case Constants.DYNAMITE:
    		board_dynamite --;
            dynamite_positions.remove(k);
    		break;
    	case Constants.TREASURE:
    		board_treasure = false;
            treasure_positions.remove(k);
    		break;
    	case Constants.DOOR:
    		board_door--;
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
        for(int row = 40; row < 120; row++){
            flag = false;
            for(int col = 40; col < 120; col++){
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
        //System.out.print(player.getDirection());
        System.out.println("board info:");
        System.out.println("board_axe: " + board_axe);
        System.out.println("board_key: " + board_key);
        System.out.println("board_tree: " + board_tree);
        System.out.println("board_door: " + board_door);
        System.out.println("board_dynamite: " + board_dynamite);
        System.out.println("board_treasure: " + board_treasure);
    }

    /**
     * create a deep copy of the game
     * @return
     */
    public Board clone(){
        Board newBoard = new Board(this.board.length, this.board[0].length);

        newBoard.board_axe = this.board_axe;
        newBoard.board_key = this.board_key;
        newBoard.board_dynamite = this.board_dynamite;
        newBoard.board_tree = this.board_tree;
        newBoard.board_door = this.board_door;
        newBoard.board_treasure = this.board_treasure;

        newBoard.axe_positions = new ArrayList<>();
        newBoard.key_positions = new ArrayList<>();
        newBoard.dynamite_positions = new ArrayList<>();
        newBoard.door_positions = new ArrayList<>();
        newBoard.treasure_positions = new ArrayList<>();
        newBoard.tree_positions = new ArrayList<>();

        newBoard.axe_positions.addAll(this.axe_positions);
        newBoard.key_positions.addAll(this.key_positions);
        newBoard.dynamite_positions.addAll(this.dynamite_positions);
        newBoard.door_positions.addAll(this.door_positions);
        newBoard.treasure_positions.addAll(this.treasure_positions);
        newBoard.tree_positions.addAll(this.tree_positions);

        newBoard.startCol = this.startCol;
        newBoard.startRow = this.startRow;
        newBoard.endCol = this.endCol;
        newBoard.endRow = this.endRow;

        newBoard.board = new char[this.board.length][this.board[0].length];

        for(int i = 0; i < newBoard.board.length; i++){
            newBoard.board[i] = Arrays.copyOf(this.board[i], this.board[i].length);
        }

        return newBoard;
    }

    /**
     * @return extract snapshot of the board and return that board
     */
    public Board extractBoard(){
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

        newBoard.board_axe = this.board_axe;
        newBoard.board_key = this.board_key;
        newBoard.board_dynamite = this.board_dynamite;
        newBoard.board_tree = this.board_tree;
        newBoard.board_door = this.board_door;
        newBoard.board_treasure = this.board_treasure;

        newBoard.axe_positions = new ArrayList<>();
        newBoard.key_positions = new ArrayList<>();
        newBoard.dynamite_positions = new ArrayList<>();
        newBoard.door_positions = new ArrayList<>();
        newBoard.treasure_positions = new ArrayList<>();
        newBoard.tree_positions = new ArrayList<>();

        newBoard.startRow = upRow;
        newBoard.startCol = leftCol;
        newBoard.endRow = bottomRow;
        newBoard.endCol = rightCol;

        for(Position p : this.axe_positions){
            newBoard.axe_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }
        for(Position p : this.key_positions){
            newBoard.key_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }
        for(Position p : this.dynamite_positions){
            newBoard.dynamite_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }
        for(Position p : this.door_positions){
            newBoard.door_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }
        for(Position p : this.treasure_positions){
            newBoard.treasure_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }
        for(Position p : this.tree_positions){
            newBoard.tree_positions.add(new Position(p.getRow()-newBoard.startRow, p.getCol()-newBoard.startCol));
        }

        for(int row = 0; row < newBoard.board.length; row++){
            for(int col = 0; col < newBoard.board[row].length; col++){
                newBoard.board[row][col] = this.board[row + newBoard.startRow][col + newBoard.startCol];
            }
        }
        return newBoard;
    }

    /**
     * start col represent where this snapshot of board begin
     * @return startCol of the board
     */
    public int getStartCol() {
        return startCol;
    }

    /**
     * start row represent where this snapshot of board begin
     * @return startRow
     */
    public int getStartRow() {
        return startRow;
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
     * @param player
     */
    public void printExtractMap(State player){
        boolean flag;
        for(int row = 0; row < this.board.length; row++){
            flag = false;
            for(int col = 0; col < this.board[0].length; col++){
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
        //System.out.print(player.getDirection());
        System.out.println("board info:");
        System.out.println("board_axe: " + board_axe);
        System.out.println("board_key: " + board_key);
        System.out.println("board_tree: " + board_tree);
        System.out.println("board_door: " + board_door);
        System.out.println("board_dynamite: " + board_dynamite);
        System.out.println("board_treasure: " + board_treasure);
    }
}
