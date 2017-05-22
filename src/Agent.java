/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/

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
        currBoard = new Board(Constants.BOARD_SIZE_ROW, Constants.BOARD_SIZE_COL);
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
       currBoard.updateBoardFromGivenView(view, currAgent);

       //print current map we have
       currBoard.printMap(currAgent);
       currAgent.printState();

       //agent.print_view( view );
       char action = e.checkExplore(currBoard.getBoard(), currAgent);
       if(action != '0'){
           currBoard.updateBoardAndStateFromGivenAction(action, currAgent);
           currAgent.setPreState(prv);
           prv = new State(currAgent);
           return action;
       } else {
           Board snapshotBoard = currBoard.extractBoard();
           State snapshotAgent = new State(currAgent.getRow()-snapshotBoard.getStartRow(),
                   currAgent.getCol()-snapshotBoard.getStartCol(), currAgent.getDirection());
           snapshotBoard.printExtractMap(snapshotAgent);

           Board cloneBoard = snapshotBoard.clone();
           State cloneAgent = snapshotAgent.clone();
           cloneBoard.printExtractMap(cloneAgent);

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
}
