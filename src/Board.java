import org.omg.CORBA.UNKNOWN;

import java.rmi.activation.UnknownObjectException;

/**
 * Created by shiyun on 11/05/17.
 */
public class Board {
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
    /*private boolean axe;
    private boolean raft;
    private boolean key;
    private int dynamite;
    private boolean treasure;*/
    
    // if the known board has any of these tools
    private boolean board_axe;
    private boolean board_key;
    private int board_dynamite;
    private int board_tree;
    private int board_door;
    private int board_treasure;
    
    // player position and direction
    // x is row, y is col
    //private int playerRow;
    //private int playerCol;
    //private int direction;

    // board information;
    int[][] board;
    
    // search
    private Search s;
    
    // player state
    private State player;

    public Board(){
        /*axe = false;
        raft = false;
        key = false;
        dynamite = 0;
        treasure = false;*/
        
        board_axe = false;
        board_key = false;
        board_tree = 0;
        board_door = 0;
        board_dynamite = 0;
        board_treasure = 0;

        //playerRow = START_ROW;
        //playerCol = START_COL;
        //direction = NORTH;
        
        s = new Search();
        player = new State(START_ROW, START_COL, NORTH);

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
        if(player.direction() == NORTH){
            //finding 0,0
            int viewRow = player.row() - 2;
            int viewCol = player.col() - 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + row][viewCol + col] == UNKNOW)) {
                        board[viewRow + row][viewCol + col] = view[row][col];
                        board_update(view[row][col]);
                    }
                }
            }
        } else if(player.direction() == SOUTH){
            //finding 0,0
            int viewRow = player.row() + 2;
            int viewCol = player.col() + 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - row][viewCol - col] == UNKNOW)) {
                        board[viewRow - row][viewCol - col] = view[row][col];
                        board_update(view[row][col]);
                    }
                }
            }
        } else if(player.direction() == EAST){
            //finding 0,0
            int viewRow = player.row() - 2;
            int viewCol = player.col() + 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + col][viewCol - row] == UNKNOW)) {
                        board[viewRow + col][viewCol - row] = view[row][col];
                        board_update(view[row][col]);
                    }
                }
            }
        } else if(player.direction() == WEST){
            //finding 0,0
            int viewRow = player.row() + 2;
            int viewCol = player.col() - 2;
            for(int row = 0; row < VIEW_ROW; row++){
                for(int col = 0; col < VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - col][viewCol + row] == UNKNOW)) {
                        board[viewRow - col][viewCol + row] = view[row][col];
                        board_update(view[row][col]);
                    }
                }
            }
        }
    }
    
    public void board_update(char board){
    	switch(board){
    	case AXE:
    		board_axe = true;
    		break;
    	case KEY:
    		board_key = true;
    		break;
    	case TREE:
    		board_tree ++;
    		break;
    	case DYNAMITE:
    		board_dynamite ++;
    		break;
    	case TREASURE:
    		board_treasure ++;
    		break;
    	case DOOR:
    		board_door ++;
    		break;
    	default:
    		break;
    	}
    }

    public char getDirection(){
        switch (player.direction()){
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
    
    public char setAction(char action){
    	char path = 0;
    	//check player holding treasure or not
    	if(player.treasure()){
    		//search path to original point
    	}
    	//check known board holding treasure or not
    	if(board_treasure > 0){
    		//search path to treasure
    	}
    		
    	//check known board has tools or barrier or not
    		//search path to tools or barrier
    	//explore graph
    	s.explore(board, player);
    	
    	return path;
    }

    public void updateAction(char action){
        switch (action){
            case TURN_LEFT:
            	player.updateDirection(player.direction()-1);
                break;
            case TURN_RIGHT:
            	player.updateDirection(player.direction()+1);
                break;
            case MOVE_FORWARD:
                switch (player.direction()){
                    case NORTH:
                    	if(isBoardUpdate(player.row()-1, player.col())){
                            player.updateRow(player.row()-1);
                    	}
                        break;
                    case SOUTH:
                    	if(isBoardUpdate(player.row()+1, player.col())){
                            player.updateRow(player.row()+1);
                    	}
                        break;
                    case WEST:
                    	if(isBoardUpdate(player.row(), player.col()-1)){
                            player.updateCol(player.col()-1);
                    	}
                        break;
                    case EAST:
                    	if(isBoardUpdate(player.row(), player.col()+1)){
                            player.updateCol(player.col()+1);
                    	}
                }
                System.out.println((char)board[player.row()][player.col()]);
                //pick up things
                interact();
                break;
            case UNLOCK_DOOR:
                int door_col = getForwardCol();
                int door_row = getForwardRow();
                board[door_row][door_col] = EMPTY;
                board_door--;
                break;
            case CHOP_TREE:
                int tree_col = getForwardCol();
                int tree_row = getForwardRow();
                board[tree_row][tree_col] = EMPTY;
                player.updateRaft(true);
                board_tree --;
                break;
            case BLAST_WALL_TREE:
                int wall_col = getForwardCol();
                int wall_row = getForwardRow();
                board[wall_row][wall_col] = EMPTY;
                player.updateDynamite(player.dynamite()-1);
                board_dynamite --;
        }
    }

    public boolean isBoardUpdate(int row, int col){
    	if(board[row][col] == WALL || board[row][col] == DOOR || board[row][col] == TREE){
    		return false;
    	}
    	return true;
    }
    
    
    public void interact(){
        switch (board[player.row()][player.col()]){
            case AXE:
                player.updateAxe(true);
                break;
            case KEY:
                player.updateKey(true);
                break;
            case DYNAMITE:
                player.updateDynamite(player.dynamite()+1);
                break;
            case TREASURE:
                player.updateTreasure(true);
        }
        if(board[player.row()][player.col()] == AXE || board[player.row()][player.col()] == KEY ||
                board[player.row()][player.col()] == DYNAMITE || board[player.row()][player.col()] == TREASURE){
            board[player.row()][player.col()] = EMPTY;
        }
    }

    public int getForwardRow(){
        switch (player.direction()){
            case NORTH:
                return player.row() - 1;
            case SOUTH:
                return player.row() + 1;
            default:
                return player.row();
        }
    }

    public int getForwardCol(){
        switch (player.direction()){
            case WEST:
                return player.col() - 1;
            case EAST:
                return player.col() + 1;
            default:
                return player.col();
        }
    }

    public void printMap(){
        boolean flag;
        for(int row = 40; row < 120; row++){
            flag = false;
            for(int col = 40; col < 120; col++){
                if(row == player.row() && col == player.col()){
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
        System.out.print(player.direction());
        System.out.println("board info:");
        System.out.println("board_axe: " + board_axe);
        System.out.println("board_key: " + board_key);
        System.out.println("board_tree: " + board_tree);
        System.out.println("board_door: " + board_door);
        System.out.println("board_dynamite: " + board_dynamite);
        System.out.println("board_treasure: " + board_treasure);
        
        System.out.println("player info:");
        System.out.println("axe: " + player.axe());
        System.out.println("key: " + player.key());
        System.out.println("raft: " + player.raft());
        System.out.println("dynamite: " + player.dynamite());
        System.out.println("treasure: " + player.treasure());
    }

}
