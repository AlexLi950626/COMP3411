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


    /**
     * constructor
     */
    public Agent(){
        currBoard = new Board();
        currAgent = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        prv = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        e = new Explore();

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

       //updateBoardFromGivenView the view from what we have seen
       updateBoardFromGivenView(view);
       //print current map we have
       currBoard.printMap(currAgent);
       //agent.print_view( view );
       char action = e.checkExplore(currBoard.getBoard(), currAgent);
       if(action != '0'){
           updateBoardAndStateFromGivenAction(action);
           return action;
       }
       //if finished explore if will only return '0'
       //need to do sth else here
       
       
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
     * updateBoardFromGivenView the board when a action is executed
     * @param action
     */
    public void updateBoardAndStateFromGivenAction(char action){
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
                System.out.println((char)currBoard.getType(currAgent.getRow(),currAgent.getCol()));
                //pick up things
                interact();
                break;
            case Constants.UNLOCK_DOOR:
                int door_col = currAgent.getForwardCol();
                int door_row = currAgent.getForwardRow();
                currBoard.setType(door_row, door_col,Constants.EMPTY);
                currBoard.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.CHOP_TREE:
                int tree_col = currAgent.getForwardCol();
                int tree_row = currAgent.getForwardRow();
                currBoard.setType(tree_row, tree_col, Constants.EMPTY);
                currAgent.setRaft(true);
                currBoard.removeItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.BLAST_WALL_TREE:
                int wall_col = currAgent.getForwardCol();
                int wall_row = currAgent.getForwardRow();
                if(currBoard.getType(wall_row,wall_col) == Constants.TREE){
                    currBoard.removeItem(currAgent.getRow(),currAgent.getCol());
                }
                currBoard.setType(wall_row, wall_col,Constants.EMPTY);
                currAgent.setDynamite(currAgent.dynamite()-1);
        }
        //if(getPreState == null){ //|| currAgent.getRow() != getPreState.getRow() || currAgent.getCol() != getPreState.getCol() ){
        currAgent.setPreState(prv);
        prv = new State(currAgent);
        //}
    }


    /**
     * given view from the engine, updateBoardFromGivenView local map
     * @param view
     */
    public void updateBoardFromGivenView(char[][] view){
        if(currAgent.getDirection() == Constants.NORTH){
            //finding 0,0
            int viewRow = currAgent.getRow() - 2;
            int viewCol = currAgent.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (currBoard.getType(viewRow + row, viewCol + col) == Constants.UNKNOW)) {
                        currBoard.setType(viewRow + row , viewCol + col, view[row][col]);
                        currBoard.updateItem(viewRow + row, viewCol + col);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.SOUTH){
            //finding 0,0
            int viewRow = currAgent.getRow() + 2;
            int viewCol = currAgent.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (currBoard.getType(viewRow - row, viewCol - col) == Constants.UNKNOW)) {
                        currBoard.setType(viewRow - row, viewCol - col, view[row][col]);
                        currBoard.updateItem(viewRow - row,viewCol - col);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.EAST){
            //finding 0,0
            int viewRow = currAgent.getRow() - 2;
            int viewCol = currAgent.getCol() + 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (currBoard.getType(viewRow + col, viewCol - row) == Constants.UNKNOW)) {
                        currBoard.setType(viewRow + col, viewCol - row, view[row][col]);
                        currBoard.updateItem(viewRow + col, viewCol - row);
                    }
                }
            }
        } else if(currAgent.getDirection() == Constants.WEST){
            //finding 0,0
            int viewRow = currAgent.getRow() + 2;
            int viewCol = currAgent.getCol() - 2;
            for(int row = 0; row < Constants.VIEW_ROW; row++){
                for(int col = 0; col < Constants.VIEW_COL; col++){
                    if(!(row == col && row == 2) && (currBoard.getType(viewRow - col,viewCol + row) == Constants.UNKNOW)) {
                        currBoard.setType(viewRow - col, viewCol + row, view[row][col]);
                        currBoard.updateItem(viewRow - col, viewCol + row);
                    }
                }
            }
        }
    }

    /**
     * update Board and Agent while a action is carried out
     */
    public void interact(){
        switch (currBoard.getType(currAgent.getRow(),currAgent.getCol())){
            case Constants.AXE:
                currAgent.setAxe(true);
                currBoard.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.KEY:
                currAgent.setKey(true);
                currBoard.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.DYNAMITE:
                currAgent.setDynamite(currAgent.dynamite()+1);
                currBoard.updateItem(currAgent.getRow(),currAgent.getCol());
                break;
            case Constants.TREASURE:
                currAgent.setTreasure(true);
                currBoard.updateItem(currAgent.getRow(),currAgent.getCol());
        }
        if(currBoard.getType(currAgent.getRow(),currAgent.getCol()) == Constants.AXE ||currBoard.getType(currAgent.getRow(),currAgent.getCol()) == Constants.KEY ||
    		    currBoard.getType(currAgent.getRow(),currAgent.getCol()) == Constants.DYNAMITE || currBoard.getType(currAgent.getRow(),currAgent.getCol()) == Constants.TREASURE){
    	    currBoard.setType(currAgent.getRow(),currAgent.getCol() ,Constants.EMPTY);
       }
   }
 
}
