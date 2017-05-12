import org.omg.CORBA.UNKNOWN;

import java.rmi.activation.UnknownObjectException;

/**
 * Created by shiyun on 11/05/17.
 */
public class Player {
    // type
    public static final char TREE = 'T';
    public static final char DOOR = '-';
    public static final char WALL = '*';
    public static final char WATER = '~';
    public static final char AXE = 'a';
    public static final char KEY = 'k';
    public static final char DYNAMITE = 'd';
    public static final char TREASURE = '$';
    public static final char UNKNOW = 'u';
    public static final char BOUNDAY = '.';
    public static final char EMPTY = ' ';

    // read directions
    public static final char NORTH = 0;
    public static final char SOUTH = 2;
    public static final char EAST = 1;
    public static final char WEST = 3;

    //start position of the player
    public static final int START_ROW = 81;
    public static final int START_COL = 81;
    public static final int BOARD_SIZE_ROW = 164;
    public static final int BOARD_SIZE_COL = 164;

    //commands
    public static final char TURN_LEFT = 'L';
    public static final char TURN_RIGHT = 'R';
    public static final char MOVE_FORWARD = 'F';
    public static final char CHOP_TREE = 'C';
    public static final char BLAST_WALL_TREE = 'B';
    public static final char UNLOCK_DOOR = 'U';

    // view size
    public static final int VIEW_ROW = 5;
    public static final int VIEW_COL = 5;

    // if the player has any of these tools
    private boolean axe;
    private boolean raft;
    private boolean key;
    private int dynamite;
    private boolean treasure;

    // player position and direction
    // x is row, y is col
    private int playerRow;
    private int playerCol;
    private int direction;

    // board information;
    int[][] board;

    public Player(){
        axe = false;
        raft = false;
        key = false;
        dynamite = 0;
        treasure = false;

        playerRow = START_ROW;
        playerCol = START_COL;
        direction = NORTH;

        board = new int[BOARD_SIZE_ROW][BOARD_SIZE_COL];

        for(int i = 0; i < BOARD_SIZE_ROW; i++){
            for(int j =0; j < BOARD_SIZE_COL; j++){
                if(i == START_ROW && j == START_COL){
                    board[i][j] = EMPTY;
                } else {
                    board[i][j] = UNKNOW;
                }
            }
        }
    }

    public void update(char[][] view){
        if(direction == NORTH){
            //finding 0,0
            int viewRow = playerRow - 2;
            int viewCol = playerCol - 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + row][viewCol + col] == UNKNOW)) {
                        board[viewRow + row][viewCol + col] = view[row][col];
                    }
                }
            }
        } else if(direction == SOUTH){
            //finding 0,0
            int viewRow = playerRow + 2;
            int viewCol = playerCol + 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - row][viewCol - col] == UNKNOW)) {
                        board[viewRow - row][viewCol - col] = view[row][col];
                    }
                }
            }
        } else if(direction == EAST){
            //finding 0,0
            int viewRow = playerRow - 2;
            int viewCol = playerCol + 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + col][viewCol - row] == UNKNOW)) {
                        board[viewRow + col][viewCol - row] = view[row][col];
                    }
                }
            }
        } else if(direction == WEST){
            //finding 0,0
            int viewRow = playerRow + 2;
            int viewCol = playerCol - 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - col][viewCol + row] == UNKNOW)) {
                        board[viewRow - col][viewCol + row] = view[row][col];
                    }
                }
            }
        }
    }

    public char getDirection(){
        switch (direction){
            case NORTH:
                return '^';
            case SOUTH:
                return 'v';
            case WEST:
                return '<';
            case EAST:
                return '>';
            default:
                return (char) 0;
        }
    }

    public void updateAction(char action){
        switch (action){
            case TURN_LEFT:
                direction = (direction - 1) % 4;
                while(direction < 0){
                    direction += 4;
                }
                break;
            case TURN_RIGHT:
                direction = (direction + 1) % 4;
                while(direction < 0){
                    direction += 4;
                }
                break;
            case MOVE_FORWARD:
                switch (direction){
                    case NORTH:
                    	if(isBoardUpdate(playerRow-1, playerCol)){
                            playerRow--;
                    	}
                        break;
                    case SOUTH:
                    	if(isBoardUpdate(playerRow+1, playerCol)){
                            playerRow++;
                    	}
                        break;
                    case WEST:
                    	if(isBoardUpdate(playerRow, playerCol-1)){
                            playerCol--;
                    	}
                        break;
                    case EAST:
                    	if(isBoardUpdate(playerRow, playerCol+1)){
                            playerCol++;
                    	}
                }
                System.out.println((char)board[playerRow][playerCol]);
                //pick up things
                interact();
                break;
            case UNLOCK_DOOR:
                int door_col = getForwardCol();
                int door_row = getForwardRow();
                board[door_row][door_col] = EMPTY;
                break;
            case CHOP_TREE:
                int tree_col = getForwardCol();
                int tree_row = getForwardRow();
                board[tree_row][tree_col] = EMPTY;
                raft = true;
                break;
            case BLAST_WALL_TREE:
                int wall_col = getForwardCol();
                int wall_row = getForwardRow();
                board[wall_row][wall_col] = EMPTY;
                dynamite--;
        }
    }

    public boolean isBoardUpdate(int row, int col){
    	if(board[row][col] == WALL || board[row][col] == DOOR || board[row][col] == TREE){
    		return false;
    	}
    	return true;
    }
    
    
    public void interact(){
        switch (board[playerRow][playerCol]){
            case AXE:
                axe = true;
                break;
            case KEY:
                key = true;
                break;
            case DYNAMITE:
                dynamite++;
                break;
            case TREASURE:
                treasure = true;
        }
        if(board[playerRow][playerCol] == AXE || board[playerRow][playerCol] == KEY ||
                board[playerRow][playerCol] == DYNAMITE || board[playerRow][playerCol] == TREASURE){
            board[playerRow][playerCol] = EMPTY;
        }
    }

    public int getForwardRow(){
        switch (direction){
            case NORTH:
                return playerRow - 1;
            case SOUTH:
                return playerRow + 1;
            default:
                return playerRow;
        }
    }

    public int getForwardCol(){
        switch (direction){
            case WEST:
                return playerCol - 1;
            case EAST:
                return playerCol + 1;
            default:
                return playerCol;
        }
    }

    public void printMap(){
        boolean flag;
        for(int row = 40; row < 120; row++){
            flag = false;
            for(int col = 40; col < 120; col++){
                if(row == playerRow && col == playerCol){
                    System.out.print(getDirection());
                } else {//if(board[row][col] != UNKNOW) {
                    System.out.print((char)board[row][col]);
                    flag = true;
                }
            }
            if(flag){
                System.out.print('\n');
            }
        }
        System.out.print(direction);
    }

}
