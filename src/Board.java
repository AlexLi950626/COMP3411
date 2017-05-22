import java.util.ArrayList;

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

    // player state
    //private State player;

    public Board(){
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

        board = new char[Constants.BOARD_SIZE_ROW][Constants.BOARD_SIZE_COL];

        for(int i = 0; i < Constants.BOARD_SIZE_ROW; i++){
            for(int j =0; j < Constants.BOARD_SIZE_COL; j++){
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
                System.out.println((char)this.getType(currAgent.getRow(),currAgent.getCol()));
                //pick up things
                interact(currAgent);
                break;
            case Constants.UNLOCK_DOOR:
                int door_col = currAgent.getForwardCol();
                int door_row = currAgent.getForwardRow();
                this.setType(door_row, door_col,Constants.EMPTY);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.CHOP_TREE:
                int tree_col = currAgent.getForwardCol();
                int tree_row = currAgent.getForwardRow();
                this.setType(tree_row, tree_col, Constants.EMPTY);
                currAgent.setRaft(true);
                this.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.BLAST_WALL_TREE:
                int wall_col = currAgent.getForwardCol();
                int wall_row = currAgent.getForwardRow();
                if(this.getType(wall_row,wall_col) == Constants.TREE){
                    this.removeItem(currAgent.getRow(),currAgent.getCol());
                }
                this.setType(wall_row, wall_col,Constants.EMPTY);
                currAgent.setDynamite(currAgent.dynamite()-1);
        }
    }

    /**
     * update Board and Agent while a action is carried out
     */
    public void interact(State currAgent){
        switch (this.getType(currAgent.getRow(),currAgent.getCol())){
            case Constants.AXE:
                currAgent.setAxe(true);
                this.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.KEY:
                currAgent.setKey(true);
                this.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.DYNAMITE:
                currAgent.setDynamite(currAgent.dynamite()+1);
                this.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.TREASURE:
                currAgent.setTreasure(true);
                this.updateItem(currAgent.getRow(),currAgent.getCol());
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
                    System.out.print((char)board[row][col]);
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
