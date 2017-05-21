import java.util.ArrayList;

/**
 * Created by shiyun on 11/05/17.
 */
public class Board {
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
    private int[][] board;

    // Explore phase
    // search
    private Explore e;
    private ArrayList<Character> path;
    
    private State prv;
    
    // player state
    private State player;

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


        prv = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        
        e = new Explore();
        path = new ArrayList<> ();
        player = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);

        board = new int[Constants.BOARD_SIZE_ROW][Constants.BOARD_SIZE_COL];

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

    /**
     * given view from the engine, update local map
     * @param view
     */
    public void update(char[][] view){
        if(player.getDirection() == Constants.NORTH){
            //finding 0,0
            int viewRow = player.getRow() - 2;
            int viewCol = player.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + row][viewCol + col] == Constants.UNKNOW)) {
                        board[viewRow + row][viewCol + col] = view[row][col];
                        board_update(viewRow + row, viewCol + col);
                    }
                }
            }
        } else if(player.getDirection() == Constants.SOUTH){
            //finding 0,0
            int viewRow = player.getRow() + 2;
            int viewCol = player.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - row][viewCol - col] == Constants.UNKNOW)) {
                        board[viewRow - row][viewCol - col] = view[row][col];
                        board_update(viewRow - row,viewCol - col);
                    }
                }
            }
        } else if(player.getDirection() == Constants.EAST){
            //finding 0,0
            int viewRow = player.getRow() - 2;
            int viewCol = player.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow + col][viewCol - row] == Constants.UNKNOW)) {
                        board[viewRow + col][viewCol - row] = view[row][col];
                        board_update(viewRow + col, viewCol - row);
                    }
                }
            }
        } else if(player.getDirection() == Constants.WEST){
            //finding 0,0
            int viewRow = player.getRow() + 2;
            int viewCol = player.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (board[viewRow - col][viewCol + row] == Constants.UNKNOW)) {
                        board[viewRow - col][viewCol + row] = view[row][col];
                        board_update(viewRow - col, viewCol + row);
                    }
                }
            }
        }
    }

    /**
     * update number of items in the board and store the positions of these items
     * @param row
     * @param col
     */
    public void board_update(int row, int col){
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

    public char getDirection(){
        switch (player.getDirection()){
            case Constants.NORTH:
                return '^';
            case Constants.SOUTH:
                return 'v';
            case Constants.WEST:
                return '<';
            case Constants.EAST:
                return '>';
            default:
                return (char) 0;
        }
    }

    // only for explore phase
    public char setAction(){
    	//explore graph 
    	//only the environment surrounding the current position, Using BFS
    	//Currently, it won't explore the water or the graph in the other side
    	State explore= e.explore(board, player);
    	
    	e.pathToChar(board, explore, player, path);
    	System.out.println("command: " + path);
    	char action = '0';
    	//if the path is null then the explore is done do some other search
    	if(path.size() != 0){
        	action = path.get(0) ; //get the first element from the path
           	path.remove(0);
    	}
    	return action;
    }

    /**
     * update the board when a action is executed
     * @param action
     */
    public void updateAction(char action){

        switch (action){
            case Constants.TURN_LEFT:
            	player.updateDirection(player.getDirection()-1);
                break;
            case Constants.TURN_RIGHT:
            	player.updateDirection(player.getDirection()+1);
                break;
            case Constants.MOVE_FORWARD:
                switch (player.getDirection()){
                    case Constants.NORTH:
                    	if(isBoardUpdate(player.getRow()-1, player.getCol())){
                            player.setRow(player.getRow()-1);
                    	}
                        break;
                    case Constants.SOUTH:
                    	if(isBoardUpdate(player.getRow()+1, player.getCol())){
                            player.setRow(player.getRow()+1);
                    	}
                        break;
                    case Constants.WEST:
                    	if(isBoardUpdate(player.getRow(), player.getCol()-1)){
                            player.setCol(player.getCol()-1);
                    	}
                        break;
                    case Constants.EAST:
                    	if(isBoardUpdate(player.getRow(), player.getCol()+1)){
                            player.setCol(player.getCol()+1);
                    	}
                }
                System.out.println((char)board[player.getRow()][player.getCol()]);
                //pick up things
                interact();
                break;
            case Constants.UNLOCK_DOOR:
                int door_col = getForwardCol();
                int door_row = getForwardRow();
                board[door_row][door_col] = Constants.EMPTY;
                board_door--;
                break;
            case Constants.CHOP_TREE:
                int tree_col = getForwardCol();
                int tree_row = getForwardRow();
                board[tree_row][tree_col] = Constants.EMPTY;
                player.setRaft(true);
                board_tree--;
                break;
            case Constants.BLAST_WALL_TREE:
                int wall_col = getForwardCol();
                int wall_row = getForwardRow();
                if(board[wall_row][wall_col] == Constants.TREE){
            		board_tree--;
                }
                board[wall_row][wall_col] = Constants.EMPTY;
                player.setDynamite(player.dynamite()-1);
        }
    	//if(getPreState == null){ //|| player.getRow() != getPreState.getRow() || player.getCol() != getPreState.getCol() ){
        	player.setPreState(prv);
        	prv = new State(player);
    	//}
    }

    /*
     * check it is allow the current position to go to the next position
     */
    public boolean isBoardUpdate(int row, int col){
    	if(board[row][col] == Constants.WALL || board[row][col] == Constants.DOOR || board[row][col] == Constants.TREE){
    		return false;
    	}
    	return true;
    }
    
    
    public void interact(){
        switch (board[player.getRow()][player.getCol()]){
            case Constants.AXE:
                player.setAxe(true);
                board_axe = false;
                break;
            case Constants.KEY:
                player.setKey(true);
                board_key = false;
                break;
            case Constants.DYNAMITE:
                player.setDynamite(player.dynamite()+1);
                board_dynamite--;
                break;
            case Constants.TREASURE:
                player.setTreasure(true);
                board_treasure =  false;
        }
        if(board[player.getRow()][player.getCol()] == Constants.AXE || board[player.getRow()][player.getCol()] == Constants.KEY ||
                board[player.getRow()][player.getCol()] == Constants.DYNAMITE || board[player.getRow()][player.getCol()] == Constants.TREASURE){
            board[player.getRow()][player.getCol()] = Constants.EMPTY;
        }
    }

    public int getForwardRow(){
        switch (player.getDirection()){
            case Constants.NORTH:
                return player.getRow() - 1;
            case Constants.SOUTH:
                return player.getRow() + 1;
            default:
                return player.getRow();
        }
    }

    public int getForwardCol(){
        switch (player.getDirection()){
            case Constants.WEST:
                return player.getCol() - 1;
            case Constants.EAST:
                return player.getCol() + 1;
            default:
                return player.getCol();
        }
    }

    public void printMap(){
        boolean flag;
        for(int row = 40; row < 120; row++){
            flag = false;
            for(int col = 40; col < 120; col++){
                if(row == player.getRow() && col == player.getCol()){
                    System.out.print(getDirection());
                } else {
                    System.out.print((char)board[row][col]);
                    flag = true;
                }
            }
            if(flag){
                System.out.print('\n');
            }
        }
        System.out.print(player.getDirection());
        System.out.println("board info:");
        System.out.println("board_axe: " + board_axe);
        System.out.println("board_key: " + board_key);
        System.out.println("board_tree: " + board_tree);
        System.out.println("board_door: " + board_door);
        System.out.println("board_dynamite: " + board_dynamite);
        System.out.println("board_treasure: " + board_treasure);
    }

}
