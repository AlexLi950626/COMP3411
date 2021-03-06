/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
 *
 *  @Auther Shiyun Zhang(z5055944) && Siying Li(z5011996)
 *
 *  In our Agent, There are tow different Phases, the first phase is the explore phase, the whole point of this phase is
 *  just try to explore as many region as possible, therefore Planning Phase can calculate the path to get the Treasure
 *  and back to origin.
 *
 *  We also used different classes to help us to implement our agent:
 *      - Agent Class is the main class this store the our game view and the current status of the agent and decide what
 *      to do at current situation
 *      - Board Class is used to store the actually map of the game, it is responsible for providing all information of
 *      the game, tell the agent where is certain item and it is also used to update map from the view we received from
 *      the server, the actual board is stored as matrix, because at beginning we don't know where we are at the map,
 *      therefore we create a matrix with size 164 X 164 to handle all cases.
 *      index are row and column it stores the type of specific row and col, all items are stored separately in ArrayList.
 *      - State Class is used to store the actually information of the agent, e.g current position, does agent has a axe
 *      does agent has key etc.
 *      - Constants Class is used to store all the magic number used in other classes
 *      - Explore is the class used to help agent to check if map is fully explored, can we explore more etc
 *      - Position is the class used to store Row and Column, as for each method we can only return one variable
 *      - SearchCompletedPath is the class used to search path from current position to get the goal and also from goal
 *      to the origin point.
 *      - SearchState is the class used to store the information about the board and the agent during searching
 *
 *  1. Explore Phase:
 *  	-Land Explore:
 *  	Land explore mainly using greedy and BFS. Firstly Agent will follow current direction to go until it hit some 
 *  	invalid position like water and wall. Then agent will will find the closest the position which haven't explored 
 *  	yet OR find the valid position which its 5X5 region has unexplored position using BFS.
 *     
 *     -Water Explore:
 *     After current land exploring is finished which return no more character commands. Agent will check it condition 
 *    	whether it can go into water or not. Before agent get into water, agent will try to get axe and raft through BFS search. 
 *      If there is no axe and validable tree in the current view then agent will start A* search. Otherwise, agent will go into 
 *      water and use the same method to explore all the water. During the water explore, agent will detect the boundary and pruning
 *      some meaningless case.
 *      
 *      -Back to land:
 *      After water exploring, agent might find another island and try to land on it. So it will try to search the valid position
 *      or tree which contain unknown position and use BFS to get there. If there is no such position, that means explore is finished.
 *      Current view should be shown the whole board
 *
 *  2. Planning Phase:
 *  After explore the map, we should have seen all tool's positions and the position of the goal, which means there should
 *  be at least one possible solution.
 *  In the planning phase, we used state based greedy search algorithm to search the solution, to find out if there is a path from agent's
 *  current position to the goal and from the goal back to the start point.
 *  our function for f cost is:
 *      f cost = 2 * g cost + 4 * h cost
 *  which means we value our heuristic twice more than the actual current spend cost.
 *  I used this is because, in order to solve the problem we don't need a shortest path, we just need a path, but we also
 *  should consider the current cost to avoid agent just wonder around.
 *  our heuristic for the algorithm is the manhattan distance from current position to the closest tool (exclude goal),
 *  and from that closest tool point add the manhattan to the next closest tool position until there is no more tool.
 *  at end of add closest tool manhattan distance, our current position should be at the last added tool's position.
 *  then we add the manhattan distance between the current position and goal position, and manhattan distance between
 *  goal position to the origin.
 *  our search algorithm eventually simulate all possibilities of actions can be execute to predicate the future environment
 *  and but because the agent is guided by the heuristic therefore, this is faster than uniform cost search.
 *
 *  There are several different data structures are used to help in planning:
 *      - Priority queue is used to make sure the f cost of state are in order during greedy search
 *      - Hash Map is used to store the search state we have already been in order to avoid repeated state to add to our priority queue
 *      - ArrayList is also used normally for storing positions of some kind of tools.
 *      - Primitive types are used to store information such as does agent has raft, how many tree on the map etc.
 *
 *  design decision:
 *      - we store the board (reduce size board) to in each of the search state, which means this will take lots of memory, but the advantages will be
 *      reduce unnecessary computations, which make our greedy search run fast, but easily blow up the memory space, we also tried to reduce the effect, by
 *      use shallow copy of the board in some case, and uses reduced size board.
 *      - our heuristic is also another problem, because it is obvious not admissible, currently it add the shortest distance between each tool
 *      but in some case not all tools need to be used. therefore sometimes it is slower than usually.
 *      - we also decide when to use dynamite, during search, we check if there is any tool around that block we want to blow, if nothing around
 *      we don't expand this search state. this speed up our search, but it miss few cases.
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
	       action = e.checkExplore(currBoard, currAgent);
	       if (action != ' ') {
	           currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
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
	    		   action = e.disableWaterExplore(currBoard,currAgent);
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
           if (getToItemPath == null || getToItemPath.isEmpty()) {
               currBoard.updateNumTree();
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
       }
       return action;
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
                       } else if (copyState.getDynamite() > 0) {
                           copyBoard.updateBoardAndStateFromGivenAction(Constants.BLAST_WALL_TREE, copyState);
                           actionPath.add(Constants.BLAST_WALL_TREE);
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
     * do all thing need to do a greedy A star search and to the path for that search
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
}

