/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/

//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Agent {
    // used store information of the game
    private Board currBoard;

    // information about the Agent
    private State currAgent;
    private State prv;
    
    private Boolean firstExplore;
    private Boolean waterExplore;
    private Boolean landExplore;

    // Explore phase
    // search
    private Explore e;

    //the first point is always the place you want to go;
    private ArrayList<Character> getToItemPath;


    /**
     * constructor
     */
    public Agent(){
        currBoard = new Board(Constants.BOARD_SIZE_ROW, Constants.BOARD_SIZE_COL);
        currAgent = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        prv = new State(Constants.START_ROW, Constants.START_COL, Constants.NORTH);
        e = new Explore();
        getToItemPath = null;
        firstExplore = false;
        landExplore= true;
        waterExplore = false;
    }

   public static void main( String[] args )
   {
       InputStream in  = null;
       OutputStream out= null;
       Socket socket   = null;
       Agent  agent    = new Agent();
       
       char view[][] = new char[5][5];
       char action;
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
               //agent.print_view(view);
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
    * @param view which is given by server
    * @return a action that control the agent to achieve goal
    */
   public char get_action( char view[][] ) {

       //updateBoardFromGivenView the view from what we have seen
       currBoard.updateBoardFromGivenView(view, currAgent);
       char action = ' ';
       if (!firstExplore) {
    	   //try to explore the whole graph, including land and water
    	   //first search valid land then water
    	   //finished water then search valid land again
           action = e.checkExplore(currBoard.getBoard(), currAgent);
           if (action != ' ') {
               currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
               currBoard.printMap(currAgent);
               //currAgent.setPreState(prv);
               prv = new State(currAgent);
               return action;
           } else {
        	   //check anything can search
        	   //if there is nothing to search
        	   //set the firstExplore up
        	   //firstExplore == true;
        	   
               //firstLandExplore = true;
        	   if(waterExplore){
        		   //end of the water explore
        		   System.out.println("disable water");
        		   action = e.disableWaterExplore(currBoard.getBoard(),currAgent);
        		   if(action != ' '){
            		   currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
                       currAgent.setPreState(prv);
                       prv = new State(currAgent);
            		   waterExplore = false;
            		   landExplore = true;
        		   }else{
        			   firstExplore = true;
        			   waterExplore = false;
        		   }
        		   return action;
        	   } else if(landExplore){
        		   //end of the land explore
                   e.enableWaterExplore();
                   waterExplore = true;
                   landExplore = false;
        	  }
           }
       } else {
           // there is no path currently
           /*if (getToItemPath == null || getToItemPath.isEmpty()) {
               currBoard.printMap(currAgent);
               currAgent.printState();
               if((currBoard.treasure_positions != null && !currBoard.treasure_positions.isEmpty())
                       || currAgent.getTreasure()) {
                   if (searchCompletedPathProcedure()) {
                       action = getToItemPath.get(0);
                       getToItemPath.remove(0);
                       currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
                       return action;
                   }
               }
           } else { // currently there is a path
               action = getToItemPath.get(0);
               getToItemPath.remove(0);
               currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
           }
//           currBoard.printMap(currAgent);
//           currAgent.printState();
//           printActionPath();*/
       }
       return action;
   }

   /**
    * print the view that is returned from the server
    * @param view give by server
    */
   void print_view( char view[][] )
   {System.out.println("\n+-----+");

       int i,j;

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
     * Translate coordinates path to actual action path
     * @return the action path
     */
   public ArrayList<Character> getActionPathFromPosPath(ArrayList<Position> path){
       Board copyBoard = currBoard.clone();
       State copyState = currAgent.clone();

       ArrayList<Character> actionPath = new ArrayList<>();
       while(!path.isEmpty()){
           // if current point is where we want to go
           if(copyState.getCurrentPosition().equals(path.get(0))){
               path.remove(0);
           } else {
               Position goingToPos = path.get(0);
               Position currPos = copyState.getCurrentPosition();

               // make sure agent is at right direction to move
               if(currPos.getRow() == goingToPos.getRow()+1){ //go north
                   actionPath.addAll(e.directionAction(Constants.NORTH, copyState.getDirection()));
                   copyState.updateDirection(Constants.NORTH);
               }else if(currPos.getRow() == goingToPos.getRow()-1){ //go south
                   actionPath.addAll(e.directionAction(Constants.SOUTH, copyState.getDirection()));
                   copyState.updateDirection(Constants.SOUTH);
               }else if(currPos.getCol() == goingToPos.getCol()+1){ //go west
                   actionPath.addAll(e.directionAction(Constants.WEST, copyState.getDirection()));
                   copyState.updateDirection(Constants.WEST);
               }else if(currPos.getCol() == goingToPos.getCol()-1){ //go east
                   actionPath.addAll(e.directionAction(Constants.EAST, copyState.getDirection()));
                   copyState.updateDirection(Constants.EAST);
               } else {
                   //bad path
                   System.out.println("bad path");
               }

               //depend on the type of where we going do different action
               switch (copyBoard.getType(goingToPos.getRow(), goingToPos.getCol())) {
                   case Constants.TREE:
                       if (copyState.getAxe()) {
                           copyBoard.updateBoardAndStateFromGivenAction(Constants.CHOP_TREE, copyState);
                           actionPath.add(Constants.CHOP_TREE);
                       } else if (copyState.getDynamite() > 0) {
                           copyBoard.updateBoardAndStateFromGivenAction(Constants.BLAST_WALL_TREE, copyState);
                           actionPath.add(Constants.BLAST_WALL_TREE);
                       } else throw new RuntimeException();
                       break;
                   case Constants.WALL:
                       if (copyState.getDynamite() > 0) {
                           copyBoard.updateBoardAndStateFromGivenAction(Constants.BLAST_WALL_TREE, copyState);
                           actionPath.add(Constants.BLAST_WALL_TREE);
                       } else throw new RuntimeException();
                       break;
                   case Constants.DOOR:
                       if (copyState.getKey()) {
                           copyBoard.updateBoardAndStateFromGivenAction(Constants.UNLOCK_DOOR, copyState);
                           actionPath.add(Constants.UNLOCK_DOOR);
                       } else throw new RuntimeException();
                       break;
               }
               copyBoard.updateBoardAndStateFromGivenAction(Constants.MOVE_FORWARD, copyState);
               actionPath.add(Constants.MOVE_FORWARD);
           }
       }

       return actionPath;
   }

    /**
     * do all thing need to do a A star search and to the path for that search
     * @return a boolean represent if there is a path or not
     */
   public boolean searchCompletedPathProcedure(){
       SearchCompletedPath SCP = new SearchCompletedPath();
       Board snapshotBoard = currBoard.extractBoard(SCP);
       State snapshotAgent = new State(currAgent);
       snapshotAgent.setRow(currAgent.getRow()-SCP.getStartRow());
       snapshotAgent.setCol(currAgent.getCol()-SCP.getStartCol());
       snapshotAgent.setPreState(null);

       SCP.setOriginalBoard(snapshotBoard);
       SCP.setOriginalState(snapshotAgent);

       ArrayList<Position> path = SCP.AStar();
       getToItemPath = getActionPathFromPosPath(path);
       return getToItemPath != null;
   }

   public void printPath(ArrayList<Position> pos){
       for(Position p : pos){
           System.out.print(p.toString());
       }
       System.out.println();
   }
   public void printActionPath(){
        for(char c : getToItemPath){
            System.out.print(c + " ");
        }
        System.out.println();
    }
}

