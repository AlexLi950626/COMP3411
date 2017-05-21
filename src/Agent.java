/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {
    // used store information of the game
    private Board currBoard;

    // information about the Agent
    private State currAgent;
    private State prv;
    
    
    // Explore phase
    // search
    private Explore e;
    private ArrayList<Character> path;

    /**
     * constructor
     */
    public Agent(){
        currBoard = new Board();
        currAgent = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        prv = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        e = new Explore();
        path = new ArrayList<> ();

    }

   public static void main( String[] args )
   {
       InputStream in  = null;
       OutputStream out= null;
       Socket socket   = null;
       Agent  agent    = new Agent();
       
       char view[][] = new char[5][5];
       char action   = 'F';
       int port;
       int ch;
       int i,j;

       if( args.length < 2 ) {
          System.out.println("Usage: java Agent -p <port>\n");
          System.exit(-1);
       }

       port = Integer.parseInt( args[1] );

       try { // open socket to Game Engine
           socket = new Socket( "localhost", port );
           in  = socket.getInputStream();
           out = socket.getOutputStream();
       }
       catch( IOException e ) {
           System.out.println("Could not bind to port: "+port);
           System.exit(-1);
       }

       try { // scan 5-by-5 window around current location
           while( true ) {
               for( i=0; i < 5; i++ ) {
                   for( j=0; j < 5; j++ ) {
                       if( !(( i == 2 )&&( j == 2 ))) {
                           ch = in.read();
                           if( ch == -1 ) {
                               System.exit(-1);
                           }
                           view[i][j] = (char) ch;
                       }
                   }
               }

               action = agent.get_action( view );
               out.write( action );
           }
       }
       catch( IOException e ) {
           System.out.println("Lost connection to port: "+ port );
           System.exit(-1);
       }
       finally {
           try {
               socket.close();
           }
           catch( IOException e )
           {
               e.printStackTrace();
           }
       }
   }
   
   /**
    * method to search the best action for agent to find the goal
    * @param view
    * @return
    */
   public char get_action( char view[][] ) {

   	update(view); //update the view from what we have seen
       currBoard.printMap(currAgent); //print current map we have
       //agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
       char action = setAction();
       updateAction(action);

       return action;
   }
   
   /**
    * translate path to set of actions
    *
    */
   //private a;

   /**
    * print the view that is returned from the server
    * @param view
    */
   void print_view( char view[][] )
   {
       int i,j;

       System.out.println("\n+-----+");
       for( i=0; i < 5; i++ ) {
           System.out.print("|");
           for( j=0; j < 5; j++ ) {
               if(( i == 2 )&&( j == 2 )) {
                   System.out.print('^');
               }
               else {
                   System.out.print( view[i][j] );
               }
           }
           System.out.println("|");
       }
       System.out.println("+-----+");
   }
   
   /**
    * given view from the engine, update local map
    * @param view
    */
   public void update(char[][] view){
       if(currAgent.getDirection() == Constants.NORTH){
           //finding 0,0
           int viewRow = currAgent.getRow() - 2;
           int viewCol = currAgent.getCol() - 2;
           for(int row = 0; row < Constants.VIEW_ROW; row++){
               for(int col = 0; col < Constants.VIEW_COL; col++){
                   if(!(row == col && row == 2) && (currBoard.getChar(viewRow + row, viewCol + col) == Constants.UNKNOW)) {
                       currBoard.setChar(viewRow + row , viewCol + col, view[row][col]);
                       currBoard.board_update(viewRow + row, viewCol + col);
                   }
               }
           }
       } else if(currAgent.getDirection() == Constants.SOUTH){
           //finding 0,0
           int viewRow = currAgent.getRow() + 2;
           int viewCol = currAgent.getCol() + 2;
           for(int row = 0; row < Constants.VIEW_ROW; row++){
               for(int col = 0; col < Constants.VIEW_COL; col++){
                   if(!(row == col && row == 2) && (currBoard.getChar(viewRow - row, viewCol - col) == Constants.UNKNOW)) {
                	   currBoard.setChar(viewRow - row, viewCol - col, view[row][col]);
                	   currBoard.board_update(viewRow - row,viewCol - col);
                   }
               }
           }
       } else if(currAgent.getDirection() == Constants.EAST){
           //finding 0,0
           int viewRow = currAgent.getRow() - 2;
           int viewCol = currAgent.getCol() + 2;
           for(int row = 0; row < Constants.VIEW_ROW; row++){
               for(int col = 0; col < Constants.VIEW_COL; col++){
                   if(!(row == col && row == 2) && (currBoard.getChar(viewRow + col, viewCol - row) == Constants.UNKNOW)) {
                	   currBoard.setChar(viewRow + col, viewCol - row, view[row][col]);
                	   currBoard.board_update(viewRow + col, viewCol - row);
                   }
               }
           }
       } else if(currAgent.getDirection() == Constants.WEST){
           //finding 0,0
           int viewRow = currAgent.getRow() + 2;
           int viewCol = currAgent.getCol() - 2;
           for(int row = 0; row < Constants.VIEW_ROW; row++){
               for(int col = 0; col < Constants.VIEW_COL; col++){
                   if(!(row == col && row == 2) && (currBoard.getChar(viewRow - col,viewCol + row) == Constants.UNKNOW)) {
                	   currBoard.setChar(viewRow - col, viewCol + row, view[row][col]);
                       currBoard.board_update(viewRow - col, viewCol + row);
                   }
               }
           }
       }
   }
   
   
   /**
    * update the board when a action is executed
    * @param action
    */
   public void updateAction(char action){
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
                   	if(currBoard.isBoardUpdate(currAgent.getRow()-1, currAgent.getCol())){
                           currAgent.setRow(currAgent.getRow()-1);
                   	}
                       break;
                   case Constants.SOUTH:
                   	if(currBoard.isBoardUpdate(currAgent.getRow()+1, currAgent.getCol())){
                           currAgent.setRow(currAgent.getRow()+1);
                   	}
                       break;
                   case Constants.WEST:
                   	if(currBoard.isBoardUpdate(currAgent.getRow(), currAgent.getCol()-1)){
                           currAgent.setCol(currAgent.getCol()-1);
                   	}
                       break;
                   case Constants.EAST:
                   	if(currBoard.isBoardUpdate(currAgent.getRow(), currAgent.getCol()+1)){
                           currAgent.setCol(currAgent.getCol()+1);
                   	}
               }
               System.out.println((char)currBoard.getChar(currAgent.getRow(),currAgent.getCol()));
               //pick up things
               interact();
               break;
           case Constants.UNLOCK_DOOR:
               int door_col = currAgent.getForwardCol();
               int door_row = currAgent.getForwardRow();
               currBoard.setChar(door_row, door_col,Constants.EMPTY);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               break;
           case Constants.CHOP_TREE:
               int tree_col = currAgent.getForwardCol();
               int tree_row = currAgent.getForwardRow();
               currBoard.setChar(tree_row, tree_col, Constants.EMPTY);
               currAgent.setRaft(true);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               break;
           case Constants.BLAST_WALL_TREE:
               int wall_col = currAgent.getForwardCol();
               int wall_row = currAgent.getForwardRow();
               if(currBoard.getChar(wall_row,wall_col) == Constants.TREE){
                   currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               }
               currBoard.setChar(wall_row, wall_col,Constants.EMPTY);
               currAgent.setDynamite(currAgent.dynamite()-1);
       }
   	//if(getPreState == null){ //|| currAgent.getRow() != getPreState.getRow() || currAgent.getCol() != getPreState.getCol() ){
       	currAgent.setPreState(prv);
       	prv = new State(currAgent);
   	//}
   }

   
   
   public void interact(){
       switch (currBoard.getChar(currAgent.getRow(),currAgent.getCol())){
           case Constants.AXE:
               currAgent.setAxe(true);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               break;
           case Constants.KEY:
               currAgent.setKey(true);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               break;
           case Constants.DYNAMITE:
               currAgent.setDynamite(currAgent.dynamite()+1);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
               break;
           case Constants.TREASURE:
               currAgent.setTreasure(true);
               currBoard.board_remove(currAgent.getRow(),currAgent.getCol());
       }
       if(currBoard.getChar(currAgent.getRow(),currAgent.getCol()) == Constants.AXE ||currBoard.getChar(currAgent.getRow(),currAgent.getCol()) == Constants.KEY ||
    		   currBoard.getChar(currAgent.getRow(),currAgent.getCol()) == Constants.DYNAMITE || currBoard.getChar(currAgent.getRow(),currAgent.getCol()) == Constants.TREASURE){
    	   currBoard.setChar(currAgent.getRow(),currAgent.getCol() ,Constants.EMPTY);
       }
   } 
   
   
   
   // only for explore phase
   public char setAction(){
	   	//explore graph 
	   	//only the environment surrounding the current position, Using BFS
	   	//Currently, it won't explore the water or the graph in the other side
	   	State explore= e.explore(currBoard.getBoard(), currAgent);
	   	
	   	e.pathToChar(currBoard.getBoard(), explore,currAgent, path);
	   	System.out.println("command: " + path);
	   	char action = '0';
	   	//if the path is null then the explore is done do some other search
	   	if(path.size() != 0){
	       	action = path.get(0) ; //get the first element from the path
	        path.remove(0);
	   	} else {
	   		/***************TO BE IMPLEMENT*****************************/
	   		//explore water if currAgent can get into water
	   	}
   		return action;
   }
   
   
   
 
}
