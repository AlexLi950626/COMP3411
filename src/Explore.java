import java.util.*;
public class Explore {
    
    // queue for explore
    private ArrayList<State> exploreSeen;
    private ArrayList<State> known;
    private ArrayList<State> waterSeen;
    private ArrayList<Character> path;
    private boolean inWater;
    private boolean waterFlag;
    private boolean hasWater;
    private boolean once;
    
	public Explore(){
		exploreSeen = new ArrayList<State>();
		waterSeen = new ArrayList<State>();
		known = new ArrayList<State>();
        path = new ArrayList<> ();
        inWater = false;
        hasWater = false; 
        once = false;
        waterFlag = false;
	}
	
	/*
	 * if current situation is allow the map to explore then it will start explore 
	 * if the map is finished exploring then it will return char '0' instead
	 */
	public char checkExplore(char[][] board, State player){
		//explore graph 
	   	//only the environment surrounding the current position, Using BFS
	   	//Currently, it won't explore the water or the graph in the other side
	   	char action = ' ';
		if(inWater == false){
		   	explore(board, player);

		   	//System.out.println("command: " + path);

		   	//if the path is null then the explore is done do some other search
		   	if(path.size() != 0){
		       	action = path.get(0) ; //get the first element from the path
		        path.remove(0);
		   	}
		}

   		//explore water if currAgent can get into water
   		if(inWater == true){ // allow to go into the water
   			//if the current forward position is going to be water
   			if(player.getRaft() == true){
   				waterFlag = true;
	   			exploreWater(board,player,path);
		   	}
   			
   			if(path.size() != 0){
		       	action = path.get(0) ; //get the first element from the path
		        path.remove(0);
		   	}else{
		   		// for water testing to get axe and tree 
		   		// can be delete later
			   		if(player.getAxe() == false){
			   			path.addAll(findPoint(board,player,Constants.AXE));

		   				State prv = new State(player);
		   				State next = new State(prv);
		   				//walk until current direction doesn't have path anymore
   						next.setRow(prv.getRow()-1);
   						next.setCol(prv.getCol());

   						if(board[next.getRow()][next.getCol()] == Constants.AXE){
   							path.addAll(directionAction(Constants.NORTH,prv.getDirection()));
   							path.add(Constants.MOVE_FORWARD);
   						}
   						next.setRow(prv.getRow()+1);
   						next.setCol(prv.getCol());

   						if(board[next.getRow()][next.getCol()] == Constants.AXE){
   							path.addAll(directionAction(Constants.SOUTH,prv.getDirection()));
   							path.add(Constants.MOVE_FORWARD);
   						}
   						
   						next.setRow(prv.getRow());
   						next.setCol(prv.getCol()-1);

   						if(board[next.getRow()][next.getCol()] == Constants.AXE){
   							path.addAll(directionAction(Constants.WEST,prv.getDirection()));
   							path.add(Constants.MOVE_FORWARD);
   						}
   						
   						next.setRow(prv.getRow());
   						next.setCol(prv.getCol()+1);
   						if(board[next.getRow()][next.getCol()] == Constants.AXE){
   							path.addAll(directionAction(Constants.EAST,prv.getDirection()));
   							path.add(Constants.MOVE_FORWARD);
   						}
			   					
			   			
			   		}else{
				   		path.addAll(findPoint(board,player,Constants.TREE));
				   		State prv = new State(player);
		   				State next = new State(prv);
		   				//walk until current direction doesn't have path anymore
   						next.setRow(prv.getRow()-1);
   						next.setCol(prv.getCol());

   						if(board[next.getRow()][next.getCol()] == Constants.TREE){
   							path.addAll(directionAction(Constants.NORTH,prv.getDirection()));
   							path.add(Constants.CHOP_TREE);
   						}
   						next.setRow(prv.getRow()+1);
   						next.setCol(prv.getCol());

   						if(board[next.getRow()][next.getCol()] == Constants.TREE){
   							path.addAll(directionAction(Constants.SOUTH,prv.getDirection()));
   							path.add(Constants.CHOP_TREE);
   						}
   						
   						next.setRow(prv.getRow());
   						next.setCol(prv.getCol()-1);

   						if(board[next.getRow()][next.getCol()] == Constants.TREE){
   							path.addAll(directionAction(Constants.WEST,prv.getDirection()));
   							path.add(Constants.CHOP_TREE);
   						}
   						
   						next.setRow(prv.getRow());
   						next.setCol(prv.getCol()+1);
   						if(board[next.getRow()][next.getCol()] == Constants.TREE){
   							path.addAll(directionAction(Constants.EAST,prv.getDirection()));
   							path.add(Constants.CHOP_TREE);
   						}
			   		}

			   	
		   	}
   			
   		}
//	   	System.out.println("command: " + path);
   		return action;
	}
	
	
	/*
	 * Enable water search
	 * Need to call exploreCheck after enable
	 */
	public void enableWaterExplore(){
		this.inWater = true;
	}
	
	
	/*
	 * Enable water search
	 */
	public Character disableWaterExplore(char[][] view, State current){
		char action = ' ';
		if(inWater == true && current.getRaft() == true){
			Queue<State> queue = new LinkedList<State>();
			ArrayList<State> visited = new ArrayList<State>();
			ArrayList<State> p = new ArrayList<State>();
			queue.add(current);
			visited.add(current);
			while(!queue.isEmpty()){
				State prv = queue.poll();
				State next = new State(prv);
				if(!seen(exploreSeen, prv) && (view[prv.getRow()][prv.getCol()] == Constants.EMPTY || view[prv.getRow()][prv.getCol()] == Constants.TREE )){
					//check the surrounding evn of this empty entry
					//it should have unknown mark 
					boolean entry = false;
					for(int row = prv.getRow()-2; row <= prv.getRow()+2; row ++){
						for(int col = prv.getCol()-2; col <= prv.getCol()+2; col ++){
							if(view[row][col] == Constants.UNKNOW){
								entry = true;
								break;
							}
						}
					}
					
					//check neighbor has water
					boolean any_water = false;
					if(view[prv.getRow()-1][prv.getCol()] == Constants.WATER){
						any_water = true;
					} else if (view[prv.getRow()+1][prv.getCol()] == Constants.WATER){
						any_water = true;
					} else if(view[prv.getRow()][prv.getCol()+1] == Constants.WATER){
						any_water = true;

					} else if (view[prv.getRow()][prv.getCol()-1] == Constants.WATER){
						any_water = true;

					}
					
					if(entry == true && any_water == true){
						while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
							next.printState();
							p.add(0,next);
							next = next.getPreState();

						}
						p.add(0,next);
						break;
					}
					
				}

				next.setRow(prv.getRow()-1);
				next.setCol(prv.getCol());
				// check if player allow to go forward in north getDirection
				if(!seen(visited, next) && (validWater(view,next) || valid(view,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow()+1);
				next.setCol(prv.getCol());
				// check if player allow to go forward in south getDirection
				if(!seen(visited, next)&& (validWater(view,next) || valid(view,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()-1);
				// check if player allow to go forward in west getDirection
				if(!seen(visited, next)&& (validWater(view,next) || valid(view,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()+1);
				// check if player allow to go forward in east getDirection
				if(!seen(visited, next)&& (validWater(view,next) || valid(view,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}

			path.addAll(BFSpath(view,p));
			if(path.size() != 0){
				action = path.get(0) ; //get the first element from the path
		        path.remove(0);
			}
			System.out.println("path -----------------------: " + path);
	    	/* Find the closest place which not explore yet to the land*/
	        inWater = false;
	        hasWater = false; 
	        once = false;
	        waterFlag = false;
		}

        
        return action;
	}
	

	
	
	
	/*
	 * This method is trying to explore the unknown node in the board
	 * It will try to follow the current direction till some points that cannot move forward anymore
	 * the it will use BFS to search the closest unknown point
	 */
	
	public void explore(char[][] view, State current){
		State returnState = null;
		//exploreSeen.add(current);
		State prv = new State(current);
		State next = new State(prv);
		//walk until current direction doesn't have path anymore
		switch(current.getDirection()){
			case Constants.NORTH:
				next.setRow(prv.getRow()-1);
				next.setCol(prv.getCol());
				break;
			case Constants.SOUTH:
				next.setRow(prv.getRow()+1);
				next.setCol(prv.getCol());
				break;
			case Constants.WEST:
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()-1);
				break;
			case Constants.EAST:
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()+1);
				break;
		}
		
		if(!seen(exploreSeen,next) && valid(view,next)){

			exploreSeen.add(prv);
			returnState = next;
		   	pathToChar(view, returnState, current, path);
		}else{
			//find the closest next ? mark
			if(path.size() == 0){
				path.addAll(findPoint(view,current,Constants.UNKNOW));
				//System.out.println("-----------------PATH"+ path +"-----------------");
			}

		}
		

	}
	
	/*
	 * Explore water area
	 */
	public void exploreWater(char[][] board, State current, ArrayList<Character> output){
		State prv = new State(current);
		State next = new State(prv);
		State returnState = null;

		//walk until current direction doesn't have path anymore
		switch(current.getDirection()){
			case Constants.NORTH:
				next.setRow(prv.getRow()-1);
				next.setCol(prv.getCol());
				break;
			case Constants.SOUTH:
				next.setRow(prv.getRow()+1);
				next.setCol(prv.getCol());
				break;
			case Constants.WEST:
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()-1);
				break;
			case Constants.EAST:
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()+1);
				break;
			}
			
		if(board[next.getRow()][next.getCol()] == Constants.WATER){
			hasWater = true;
		}
		//check player's forward position has water or not
		//find somewhere is water
		if(hasWater == false && once == false){
			once = true;
			ArrayList<Character> wayToWater = findPoint(board,current, Constants.WATER);
			output.addAll(wayToWater);
		}
		

		if(hasWater == true){
			if(!seen(waterSeen,next) && validWater(board,next)){
				waterSeen.add(next);
				returnState = next;
			   	pathToChar(board, returnState, current, path);
			}else{
				//find the closest next ? mark
				if(path.size() == 0){
					path.addAll(findPoint(board,current, Constants.UNKNOW));
					//System.out.println("-----------------PATH"+ path +"-----------------");
				}
		
			}
		}
		

	}
	
	/*
	 * check current position is in the array list or not
	 */
	public boolean seen(ArrayList<State> seen, State test){
		for(State s: seen){
			if(s.getRow() == test.getRow() && s.getCol() == test.getCol()){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * This method is used to translate the action to next valid point
	 * However, we are using BFS, the node from the queue will have different getPreState node
	 * Then we also need to calculate the path to go back to the getPreState node
	 */
	public void pathToChar(char[][] view, State path, State current, ArrayList<Character> output){
		if(output.size() == 0){
			// translate the path to next point to command
			State next = new State(path);
			if(next.getRow() == current.getRow()-1 && next.getCol() == current.getCol()){
				//go north
				output.addAll(directionAction(Constants.NORTH, current.getDirection()));

			}else if(next.getRow() == current.getRow()+1 && next.getCol() == current.getCol()){
				//go south
				output.addAll(directionAction(Constants.SOUTH, current.getDirection()));

			}else if(next.getRow() == current.getRow() && next.getCol() == current.getCol()-1){
				//go west
				output.addAll(directionAction(Constants.WEST, current.getDirection()));


			}else if(next.getRow() == current.getRow() && next.getCol() == current.getCol()+1){
				//go east
				output.addAll(directionAction(Constants.EAST, current.getDirection()));

			} else {
				//bad path
				System.out.println("bad path");

			}

			if(view[next.getRow()][next.getCol()] == Constants.DOOR && next.getKey()){
				output.add(Constants.UNLOCK_DOOR);
			}
			if(view[next.getRow()][next.getCol()] == Constants.TREE && next.getAxe()){
				output.add(Constants.CHOP_TREE);
			}
		   	output.add(Constants.MOVE_FORWARD);
		}

	}
	
	/*
	 * Find the closest path to the given target by BFS
	 */
	public ArrayList<Character> findPoint(char[][] view, State current, char target){
		ArrayList<Character> output = new ArrayList<Character>();
		//write a bfs to water from current
		// BFS uses Queue data structure
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> visited = new ArrayList<State>();
		ArrayList<State> p = new ArrayList<State>();
		queue.add(current);
		visited.add(current);
		breakloop:
		while(!queue.isEmpty()) {

			State prv = queue.poll();
			State next = new State(prv);
			if(view[next.getRow()][next.getCol()] == target){
				//return path
				while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
					next = next.getPreState();
					p.add(0,next);
				}
				//p.add(0,next);
				for(State s : p){
					exploreSeen.add(s);	
				}
				break breakloop;
			}
			
			if(!seen(exploreSeen, next) && !(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
				Position pos =  new Position(next.getRow(),next.getCol());
				if(checkForward(view,pos)){
					while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
						p.add(0,next);
						next = next.getPreState();
					}
					p.add(0,next);
					for(State s : p){
						exploreSeen.add(s);
					}
					break breakloop;
				}
			}
			
			
			next.setRow(prv.getRow()-1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in north getDirection
			
			if(!seen(visited, next)){
				if(hasWater == true && validWater(view,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(view, next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				
			}
			next = new State(prv);
			next.setRow(prv.getRow()+1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in south getDirection
			if(!seen(visited, next)){
				if(hasWater == true && validWater(view,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(view, next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}
			next = new State(prv);
			next.setRow(prv.getRow());
			next.setCol(prv.getCol()-1);
			// check if player allow to go forward in west getDirection
			if(!seen(visited, next)){
				if(hasWater == true && validWater(view,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(view, next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}
			next = new State(prv);
			next.setRow(prv.getRow());
			next.setCol(prv.getCol()+1);
			// check if player allow to go forward in east getDirection
			if(!seen(visited, next)){
				if(hasWater == true && validWater(view,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(view, next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}
			
		}
		

		output = BFSpath(view, p);
		//System.out.println(output);

		return output;
	}
	
	/*
	 * BFS path translate
	 */
	
	public ArrayList<Character> BFSpath(char[][] view, ArrayList<State> p){
		ArrayList<Character> output = new ArrayList<Character>();
		// translate the path to the target to command
		for(int i = 0; i < p.size()-1; i++){
			if(i+1 > p.size()-1){
				break;
			}
			State prv = p.get(i);
			State next = p.get(i+1);
			if(next.getRow() == prv.getRow()-1 && next.getCol() == prv.getCol()){
				//go north
				output.addAll(directionAction(Constants.NORTH, prv.getDirection()));
				p.get(i+1).updateDirection(Constants.NORTH);
				
			}else if(next.getRow() == prv.getRow()+1 && next.getCol() == prv.getCol()){
				//go south
				output.addAll(directionAction(Constants.SOUTH,prv.getDirection()));
				p.get(i+1).updateDirection(Constants.SOUTH);

				
			}else if(next.getRow() == prv.getRow() && next.getCol() == prv.getCol()-1){
				//go west
				output.addAll(directionAction(Constants.WEST,prv.getDirection()));
				p.get(i+1).updateDirection(Constants.WEST);



			}else if(next.getRow() == prv.getRow() && next.getCol() == prv.getCol()+1){
				//go east
				output.addAll(directionAction(Constants.EAST,prv.getDirection()));
				p.get(i+1).updateDirection(Constants.EAST);


			} else {
				//bad path
				System.out.println("bad path");
				//next.printState();

			}
			
	        if(view[next.getRow()][next.getCol()] == Constants.DOOR && next.getKey()){
	        	output.add(Constants.UNLOCK_DOOR);
	        }
	        if(view[next.getRow()][next.getCol()] == Constants.TREE && next.getAxe()){
	        	output.add(Constants.CHOP_TREE);
	        }
	       output.add(Constants.MOVE_FORWARD);
		}
		return output;
	}
	
	
	
	
	
	/*
	 * check surround allow to go forward or not
	 */
	public boolean checkForward(char[][] view, Position origion){
		for(int row = origion.getRow()-2 ; row <= origion.getCol()+2; row++){
			for(int col = origion.getRow()-2 ; col <= origion.getCol()+2; col++){
				if(col > 0 && row >0 && col < Constants.BOARD_SIZE_COL && row < Constants.BOARD_SIZE_ROW){
					if(view[row][col] == Constants.UNKNOW && view[origion.getRow()][col] != Constants.WATER && view[row][origion.getCol()] != Constants.WATER){
						return true;
					}
					
				}
			}
		}
		
		return false;
	}
	
	
	/*
	 * Known the getDirection which current node need to go
	 * And known current node's getDirection
	 * Calculating which getDirection current node should turn around
	 */
	public ArrayList<Character> directionAction(int aim, int d){
		ArrayList<Character> output = new ArrayList<>();
		switch (aim){
		case Constants.NORTH:
			switch(d){
			case Constants.SOUTH:
				output.add(Constants.TURN_LEFT);
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.WEST:
				output.add(Constants.TURN_RIGHT);
				break;
			case Constants.EAST:
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.NORTH:
				break;
			}
		break;
		case Constants.SOUTH:
			switch(d){
			case Constants.NORTH:
				output.add(Constants.TURN_LEFT);
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.WEST:
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.EAST:
				output.add(Constants.TURN_RIGHT);
				break;
			case Constants.SOUTH:
				break;
			}
		break;
		case Constants.WEST:
			switch(d){
			case Constants.NORTH:
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.SOUTH:
				output.add(Constants.TURN_RIGHT);
				break;
			case Constants.EAST:
				output.add(Constants.TURN_LEFT);
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.WEST:
				break;
			}
		break;
		case Constants.EAST:
			switch(d){
			case Constants.NORTH:
				output.add(Constants.TURN_RIGHT);
				break;
			case Constants.SOUTH:
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.WEST:
				output.add(Constants.TURN_LEFT);
				output.add(Constants.TURN_LEFT);
				break;
			case Constants.EAST:
				break;
			}
		break;
		}
		return output;
	}
	
	/*
	 * Check current position is valid or not
	 */
	public boolean valid(char[][]view, State current){
		// view overflow or not
		if(current.getRow() >  Constants.BOARD_SIZE_ROW-1 || current.getCol() > Constants.BOARD_SIZE_COL-1){
			return false;
		}
		if(current.getRow() < 0 || current.getCol() < 0){
			return false;
		}
		//check boundary 5X% area
		if(current.getRow()+2 >  Constants.BOARD_SIZE_ROW-1 || current.getRow()-2 < 0){
			return false;
		}
		if(current.getCol()+2 >  Constants.BOARD_SIZE_COL-1 || current.getCol()-2 < 0){
			return false;
		}
		// it is a wall
		if(view[current.getRow()][current.getCol()] == Constants.WALL){// && current.dynamite() < 1){
			return false;
		}
		// it is over boundary '.'
		else if(view[current.getRow()][current.getCol()] == Constants.BOUNDARY){
			return false;
		}
		// it is water
		else if(view[current.getRow()][current.getCol()] == Constants.WATER ){//&& current.getRaft() == false){  currently dun add this for the BFS
			//check getRaft
			if(waterFlag == true && current.getRaft() == true){
				return true;
			}
			return false;
		}
		// it is tree
		else if (view[current.getRow()][current.getCol()] == Constants.TREE){
			//check getAxe or dynamite
			return current.getAxe() || current.getDynamite() > 1;
		} else if(view[current.getRow()][current.getCol()] == Constants.UNKNOW){
			return true;
		}
		// it is a door
		else if(view[current.getRow()][current.getCol()] == Constants.DOOR && !current.getKey()){
			return false;
		} else if(view[current.getRow()][current.getCol()] == Constants.DYNAMITE){
			return true;
		}
		
		return true;
	}
	
	/*
	 * Check current position is valid or not in the water mode
	 */
	public boolean validWater(char[][] view, State current){
		if(view[current.getRow()][current.getCol()] == Constants.WATER){
			return true;
		} else if(view[current.getRow()][current.getCol()] == Constants.UNKNOW){
			return true;
		}
		return false;
	}
}
