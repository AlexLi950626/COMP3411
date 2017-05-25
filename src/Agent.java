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
    
    private Boolean firstLandExplore;

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
        firstLandExplore = false;
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
       if (!firstLandExplore) {
           action = e.checkExplore(currBoard.getBoard(), currAgent);
           if (action != '0') {
               currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
               currAgent.setPreState(prv);
               prv = new State(currAgent);
               return action;
           } else {
               firstLandExplore = true;
           }

       } else {
           currBoard.printMap(currAgent);
           currAgent.printState();
           // there is no path currently
           if (getToItemPath == null || getToItemPath.isEmpty()) {
               //if agent has the goal just find the way back to original
               if (currAgent.getTreasure()) {
                   searchProcedure(new Position(Constants.START_ROW, Constants.START_COL));
                   action = getToItemPath.get(0);
                   getToItemPath.remove(0);
                   currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
                   return action;
               }
               // Axe and key are first priority we want to get
               if (currBoard.axe_positions != null && !currBoard.axe_positions.isEmpty()) {
                   if (searchProcedure(currBoard.axe_positions.get(0))) {
                       action = getToItemPath.get(0);
                       getToItemPath.remove(0);
                       currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
                       return action;
                   }
               } else if (currBoard.key_positions != null && !currBoard.key_positions.isEmpty()) {
                   if (searchProcedure(currBoard.key_positions.get(0))) {
                       action = getToItemPath.get(0);
                       getToItemPath.remove(0);
                       currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
                       return action;
                   }
               } else if (currBoard.treasure_positions != null && !currBoard.treasure_positions.isEmpty()) {
                   if (searchProcedure(currBoard.treasure_positions.get(0))) {
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
           currBoard.printMap(currAgent);
           currAgent.printState();
           printActionPath();
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
     * @param dest where the destination is for this search
     * @return a boolean represent if there is a path or not
     */
   public boolean searchProcedure(Position dest){
       Board snapshotBoard = currBoard.extractBoard();
       State snapshotAgent = new State(currAgent);
       snapshotAgent.setRow(currAgent.getRow()-snapshotBoard.getStartRow());
       snapshotAgent.setCol(currAgent.getCol()-snapshotBoard.getStartCol());
       snapshotAgent.setPreState(null);

       // convert destination coordinate to snapshot coordinate
       Position newDest = dest.clone();
       newDest.setRow(dest.getRow()-snapshotBoard.getStartRow());
       newDest.setCol(dest.getCol()-snapshotBoard.getStartCol());

       SearchItem SI = new SearchItem(snapshotBoard, snapshotAgent, newDest);
       ArrayList<Position> path = SI.AStar();
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

