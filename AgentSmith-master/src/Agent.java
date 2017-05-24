/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2012
*/

/**
 *	Hi there!
 *
 *	Welcome to team James Virtue and Bruce Hely's wizzbang Bounty Bot!
 *	We call 'im 'Agent Smith' we did! It almost solves everything... if only we
 *	had a few more hours to get the aquatic searches done. Our agent initially will
 *	explore a map with no tools in sight. These tools are what we consider 'heuristics'.
 *	
 *	If a heuristic is seen, an AStar search is performed from the object, toward our
 *	agent (to keep the state space small). This rather quickly determines whether the heuristic
 *	has a path to our agent, and if a path is returned, we merely reverse the path and run it through
 *	our MoveGenerator, which will produce a string of commands which the bot will execute. Once the
 *	bot has run out of commands, we can assume that we have safely achieved the object.
 *
 *	Heuristics that are seen but cannot be accessed are put onto a priority list of unobtainable heuristics.
 *	Whenever the player is resting in between paths (issues by both Explore and through Running for objects), that is..
 *	the player has no more commands to be issued, it will reassess whether or not it can get these unobtainable heuristics,
 *	in the hope that they had achieved a better inventory/boat to make it to the object.
 *
 *	Once a heuristic is noted to be accessible, it will pop the most valuable heuristic to obtain first,
 *	and issue commands to achieve that object.
 *
 *	It returns naiively, not taking any chances to explore any additional terrain it has not visited.
 *
 */
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;

import com.sun.jmx.remote.internal.ArrayQueue;


public class Agent {

	//Code that agent uses to select an action

	Map map = new Map(80);
	Compass compass = new Compass();
	Inventory inventory = new Inventory();
	
	//Create a list of moves (in a queue) to follow until completion
	Queue<Character> moveList = new LinkedList<Character>();
	
	//Variables for inventory locations 
	boolean foundGold = false;
	Node goldNode = null;
	boolean foundAxe = false;
	Node axeNode = null;
	boolean foundBoat = false;
	Node boatNode = null;
	
	//List of nodes that contain dynamite
	ArrayList<Node> dynamite = new ArrayList<Node>();
	
	ArrayList<Node> heuristicsScanned = new ArrayList<Node>();
	ArrayList<Node> heuristicsSeen = new ArrayList<Node>();
	PriorityQueue<DiscoverableNode> heuristicsUnobtainable = new PriorityQueue<DiscoverableNode>();
	PriorityQueue<DiscoverableNode> heuristicsObtainable = new PriorityQueue<DiscoverableNode>();
	
	public char get_action( char view[][] ) {
		
		//Update the local copy of the map with the new view
		map.update(view, compass);
		map.printMap();
		
		char returnedMove;
		ArrayList<Node> nextMoves;
		String nextCommands;
		
		//If no moves to process
		if(moveList.isEmpty()){
			ArrayList<DiscoverableNode> tempList = new ArrayList<DiscoverableNode>();
			//Lets reassess whether we can visit the other heuristics in between all that decision making
			//Will be empty by the end of it
			while(!heuristicsUnobtainable.isEmpty()){
				Node n = heuristicsUnobtainable.poll();
				
				//If haven't seen this node before
				System.out.println("I'm still wanna go get that " + n.getItem() + "...");
				System.out.println("My inventory! Behond! " + inventory.toString());
				
				Search newSearch = new Search(map, inventory, n);		//Create a new search for the node
				ArrayList<Node> searchResults = newSearch.findReversePath();
					
				//Check if we got the path
				if(!searchResults.isEmpty()){
					Collections.reverse(searchResults);
					heuristicsObtainable.add(new DiscoverableNode(n, searchResults));	//If so, add to obtainables
				} else {
					tempList.add((DiscoverableNode)n);	//So duct tape...
				}
			}
			
			//Adds all the ones that didn't make the cut
			heuristicsUnobtainable.addAll(tempList);

			
			//Obtain a strategy to proceed with
			nextMoves = decideStrategy(map);
			
			//Make move generator
			MoveGenerator m = new MoveGenerator(nextMoves, compass);
			nextCommands = m.getMoves();
			
			//Add all the characters individually to the moveList
			for(char c : nextCommands.toCharArray()){
				moveList.add(c);
			}
		}

		//Pop a value off the moveList
		returnedMove = moveList.poll();
		
		//Update the local assets with the new move
		updateLocalAssets(returnedMove);
		
		// delay
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Interrupt Exception" + e);
		}
		
		//Print the move out for inspection
		System.out.println("Agent chose: " + returnedMove);
		
		return returnedMove;

	}

	public void updateLocalAssets(char move){
		switch( move ) { // if character is a valid action, return it
        case 'F':
        case 'f':
    		compass.setAgentPosition(compass.getForwardPosition());
    		break;
    		
        case 'L':
        case 'l':
        	compass.turnLeft();
        	break;
        	
        case 'R':
        case 'r':
        	compass.turnRight();
        	break;
        	
        case 'C':
        case 'c':
        	break;
        case 'B':	            	
        case 'b':
        	inventory.useDynamite();   	
        }
	}

	//Merely prints the game state to the console
	void print_view( char view[][] ){
		int i,j;

		System.out.println("\n+-----+");
		for( i=0; i < 5; i++ ) {
			System.out.print("|");
			for( j=0; j < 5; j++ ) {
				if(( i == 2 )&&( j == 2 )) {
					System.out.print('^');
				} else {
					System.out.print( view[i][j] );
				}
			}
			System.out.println("|");
		}
		System.out.println("+-----+");
		
	}

	public static void main( String[] args ) {
		InputStream in  = null;
		OutputStream out= null;
		Socket socket   = null;
		Agent  agent    = new Agent();
		char   view[][] = new char[5][5];
		char   action   = 'F';
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
		} catch( IOException e ) {
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
				agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
				action = agent.get_action( view );
				out.write( action );
			}
		}
		catch( IOException e ) {
			System.out.println("Lost connection to port: "+ port );
			System.exit(-1);
		} finally {
			try {
				socket.close();
			}
			catch( IOException e ) {}
		}
	}
	
	/**
	 * Method that decides which strategy to perform
	 * 
	 * @param map		The current map object to scan.
	 */
	ArrayList<Node> decideStrategy(Map map){
		
		ArrayList<Node> searchResults = null;

		ArrayList<Node> returnedMoves = null;
		
		//Return home if have gold
		if(inventory.containsGold()) {
			// Get home Node
			BountyPoint homePoint = new BountyPoint(map.getMaxEdge()/2,map.getMaxEdge()/2);
			Node homeNode = map.getNode(homePoint);
			
			// Create a new search for the home node
			Search newSearch = new Search(map, inventory, homeNode);
			returnedMoves = newSearch.findPath();
			
			// Return the moves straight away
			return returnedMoves;
		}
		
		//Adds all the nodes that are heuristics to the seen heuristics 
		scanForHeuristics(map);	
		
		//For every node in heuristicsSeen, can we obtain it? Classify everything
		if(!heuristicsScanned.isEmpty()){
			for(Node n : heuristicsScanned){
				
				//If haven't seen this node before
				if(!heuristicsSeen.contains(n)){
					System.out.println("I'm wanna go get that " + n.getItem());
					System.out.println("My inventory! Behond! " + inventory.toString());
					Search newSearch = new Search(map, inventory, n);		//Create a new search for the node
					searchResults = newSearch.findReversePath();
					
					//Check if we got a path
					if(!searchResults.isEmpty()){
						Collections.reverse(searchResults);
						heuristicsObtainable.add(new DiscoverableNode(n, searchResults));	//If so, add to obtainables
					} else {
						heuristicsUnobtainable.add(new DiscoverableNode(n));	//If not, add to unobtainable
					}	
					heuristicsSeen.add(n);
				}
	
			}
		}
		
		//We have classified all heuristics
		heuristicsScanned.clear();
		
		//Get moves to heuristic if obtainable ones exist
		if(!heuristicsObtainable.isEmpty()){
			System.out.println("GET SHINY!!");
			DiscoverableNode targetNode = heuristicsObtainable.poll();
			returnedMoves = targetNode.getPathToNode();
			
			//Preupdate the value fields as if we have already obtainedthe item
			if(targetNode.getItem() == 'g'){
				inventory.obtainedGold();
				targetNode.setItem(' ');
			} else if (targetNode.getItem() == 'a'){
				inventory.obtainedAxe();
				targetNode.setItem(' ');
			} else if (targetNode.getItem() == 'B'){
				inventory.toggleBoat();
			} else if (targetNode.getItem() == 'd'){
				inventory.obtainedDynamite();
			}

		} else {
			System.out.println("EXPLORE!!");
			Strategy explore = new ExploreStrategy();
			returnedMoves = explore.decideMove(map, inventory, compass);
		}
		
		
		return returnedMoves;

	}
	
	/**
	 * Method to categorize and sort the nodes into respectable fields that we can process
	 * 
	 * @param node		Node to be evaluated and categorized
	 */
	public void categorizeNode(Node node){
		//If a node turns out to be a gold node
		if(node.getItem() == 'g'){
			foundGold = true;
			goldNode = node.clone();
		} else if (node.getItem() == 'a'){
			foundAxe = true;
			axeNode = node.clone();
		} else if (node.getItem() == 'd'){
			dynamite.add(node.clone());
		} else if (node.getItem() == 'B'){
			foundBoat = true;
			boatNode = node.clone();
		}
	}
	/**
	 * Method that scans a map and adds all nodes that are heuristics to the 
	 * list of seen heuristics.
	 */
	public void scanForHeuristics(Map map){
		Node[][] currentMap = map.getMap();
		
		int i = 0;
		int j = 0;
		for(i = 0; i < map.getMaxEdge(); i++){
			for(j = 0; j < map.getMaxEdge(); j++){
				if(currentMap[i][j].isHeuristic()){
					heuristicsScanned.add(currentMap[i][j].clone());
				}
			}
		}
	}
	
	
}
