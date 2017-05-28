import java.util.*;
public class Explore {
    
    // queue for explore
    private ArrayList<State> exploreSeen;
    private ArrayList<State> waterSeen;
    private ArrayList<Character> path;
    private boolean inWater;
    private boolean waterFlag;
    private boolean hasWater;
    private boolean once;
    private boolean exploreLand;
    
	public Explore(){
		exploreSeen = new ArrayList<State>();
		waterSeen = new ArrayList<State>();
        path = new ArrayList<> ();
        inWater = false;
        hasWater = false; 
        once = false;
        waterFlag = false;
        exploreLand = false;
	}
	
	/**
	 * if current situation is allow the map to explore then it will start explore 
	 * if the map is finished exploring then it will return char '0' instead
	 * 
	 * it will store a path char and return char one by one
	 * each explore search or bfs search should wait until all
	 * char in path is finished. It is because it need to search from the agent current state.
	 * 
	 * @param view
	 * @param player
	 * @return
	 */
	public char checkExplore(Board view, State player){
		//explore graph 
	   	//only the environment surrounding the current position, Using BFS
	   	//Currently, it won't explore the water or the graph in the other side
	   	char action = ' ';
		if(inWater == false){
		   	explore(view, player);

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
	   			exploreWater(view,player,path);
		   	}
   			
   			if(path.size() != 0){
		       	action = path.get(0) ; //get the first element from the path
		        path.remove(0);
		   	}else{
		   		// for water testing to get axe and tree 
		   		// can be delete later
			   		if(player.getAxe() == false){
			   			path.addAll(findPoint(view,player,Constants.AXE));

			   			if(path.size() != 0){
					       	action = path.get(0) ; //get the first element from the path
					        path.remove(0);
					   	}
			   		}else{
			   			//find tree to cut
				   		path.addAll(findPoint(view,player,Constants.TREE));
				   		
   						if(path.size() != 0){
   					       	action = path.get(0) ; //get the first element from the path
   					        path.remove(0);
   					   	}
			   		}
			   		
			   	
		   	}
   			
   		}
   		return action;
	}
	
	/**
	 * Enable water search
	 * Need to call exploreCheck after enable
	 * Set flag on
	 */
	public void enableWaterExplore(){
		this.inWater = true;
	}
	
	
	/**
	 * Disable water explore
	 * Need to find the point which might be tree or empty and contain unknown position
	 * And use BFS to go there
	 * If it return ' ' then it means no new island can land on
	 * 
	 * @param board
	 * @param current
	 * @return
	 */
	public Character disableWaterExplore(Board board, State current){

		char[][] view = board.getBoard();
		char action = ' ';
    	/* Find the closest place which not explore yet to the land*/
		if(inWater == true && current.getRaft() == true){ // agent get into water and still in the water
			Queue<State> queue = new LinkedList<State>();
			ArrayList<State> visited = new ArrayList<State>();
			ArrayList<State> p = new ArrayList<State>();
			queue.add(current);
			while(!queue.isEmpty() ){
				State prv = new State(queue.poll());
				State next = new State(prv);
				if(!seen(exploreSeen, prv) && !(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
					//check the surrounding evn of this empty entry
					//it should have unknown mark 
					if(view[prv.getRow()][prv.getCol()] == Constants.TREE || view[prv.getRow()][prv.getCol()] == Constants.EMPTY ){
						boolean entry = false;
						breakLoop:
						for(int row = prv.getRow()-2; row <= prv.getRow()+2; row ++){
							for(int col = prv.getCol()-2; col <= prv.getCol()+2; col ++){
								if(view[row][col] == Constants.UNKNOW){
									entry = true;
									break breakLoop;
								}
							}
						}
						
						//check neighbor has water
						boolean any_water = false;
						if(view[prv.getRow()-1][prv.getCol()] == Constants.WATER){
							any_water = true;
							//next.setRow(next.getRow()-1);
						} else if (view[prv.getRow()+1][prv.getCol()] == Constants.WATER){
							any_water = true;
							//next.setRow(next.getRow()+1);

						} else if(view[prv.getRow()][prv.getCol()+1] == Constants.WATER){
							any_water = true;
							//next.setCol(next.getCol()-1);

						} else if (view[prv.getRow()][prv.getCol()-1] == Constants.WATER){
							any_water = true;
							//next.setCol(next.getCol()+1);
						}
						
						if(entry == true && any_water == true){
							//current is not water
							//have to find the entry close to the current agent pos
							if(prv.getRow() > current.getRow()){
								if(view[prv.getRow()-1][prv.getCol()] == Constants.WATER){
									next.setRow(next.getRow()-1);
									next.setCol(prv.getCol());
								}
							}else{
								if(view[prv.getRow()+1][prv.getCol()] == Constants.WATER){
									next.setRow(next.getRow()+1);
									next.setCol(prv.getCol());
								}
							}
							if(prv.getCol() > current.getCol()){
								if(view[prv.getRow()][prv.getCol()-1] == Constants.WATER){
									next.setRow(next.getRow());
									next.setRow(next.getCol()-1);
								}
							}else{
								if(view[prv.getRow()][prv.getCol()+1] == Constants.WATER){
									next.setRow(next.getRow());
									next.setRow(next.getCol()+1);
								}
							}
							
							//get the water path postion
							p = toNode(board,current,next,Constants.WATER);
							//put the land position on
							p.add(prv);
							
							break;
						}
					}

				}

				//BFS direction analyze

				next.setRow(prv.getRow()-1);
				next.setCol(prv.getCol());

				// check if player allow to go forward in north getDirection
				if(!seen(visited, next) && (validWater(board,next) || valid(board,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow()+1);
				next.setCol(prv.getCol());


				// check if player allow to go forward in south getDirection
				if(!seen(visited, next)&& (validWater(board,next) || valid(board,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()-1);

				// check if player allow to go forward in west getDirection
				if(!seen(visited, next)&& (validWater(board,next)|| valid(board,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
				next = new State(prv);
				next.setRow(prv.getRow());
				next.setCol(prv.getCol()+1);

				// check if player allow to go forward in east getDirection
				if(!seen(visited, next)&& (validWater(board,next)|| valid(board,next))){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}

			path.addAll(BFSpath(board,p));
			if(path.size() != 0){
				action = path.get(0) ; //get the first element from the path
		        path.remove(0);
			}


	        inWater = false;
	        hasWater = false; 
	        once = false;
	        waterFlag = false;
		}

        
        return action;
	}
	
	/**
	 * Use BFS to find the specific position
	 * it will return a arraylist contain state path
	 * @param board
	 * @param start
	 * @param target
	 * @param mode
	 * @return
	 */
	public ArrayList<State> toNode(Board board, State start, State target, char mode){
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> visited = new ArrayList<State>();
		ArrayList<State> p = new ArrayList<State>();
		
		queue.add(start);
		while(!queue.isEmpty() ){
			State prv = new State(queue.poll());
			State next = new State(prv);
			if(next.getRow() == target.getRow() && next.getCol() == target.getCol()){
				while(!(next.getCol() == start.getCol() && next.getRow() == start.getRow())){
					p.add(0,next);
					next = next.getPreState();
				}
				p.add(0,next);
			
			break;	
			}

			next.setRow(prv.getRow()-1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in north getDirection
			if(!seen(visited, next)){
				if(mode == Constants.WATER && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(mode != Constants.WATER && valid(board,next)){
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
				if(mode == Constants.WATER && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(mode != Constants.WATER && valid(board,next)){
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
				if(mode == Constants.WATER && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(mode != Constants.WATER && valid(board,next)){
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
				if(mode == Constants.WATER && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(mode != Constants.WATER && valid(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}
		}
		return p;
	}
	
	/**
	 * This method is trying to explore the unknown node in the board
	 * It will try to follow the current direction till some points that cannot move forward anymore
	 * If it cannot move anymore use BFS to find the next unknown point
	 * @param board
	 * @param current
	 */
	
	public void explore(Board board, State current){
		exploreLand = true;
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
		
		if(!seen(exploreSeen,next) && valid(board,next)){
			exploreSeen.add(prv);
			returnState = next;
			//check forward and put node to quque
		   	pathToChar(board, returnState, current, path);
		}else{
			//find the closest next ? mark
			if(path.size() == 0){
				path.addAll(findPoint(board,current,Constants.UNKNOW));
			}

		}
		exploreLand = false;

	}
	
	/*
	 * Explore water area
	 */
	public void exploreWater(Board view, State current, ArrayList<Character> output){
		char[][] board = view.getBoard();
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
		if(output.size() == 0){
			if(once == true && board[next.getRow()][next.getCol()] == Constants.WATER){
				hasWater = true;
			}
			//check player's forward position has water or not
			//find somewhere is water
			if(hasWater == false){
				ArrayList<Character> wayToWater = findPoint(view,current, Constants.WATER);
				hasWater = true;
				output.addAll(wayToWater);
			}
			
			if(once == false){
				if(output.size() == 0){
					once = true;
				}
			}
			
			if(hasWater == true && once == true){
				if(!seen(waterSeen,next) && validWater(view,next)){
					waterSeen.add(next);
					returnState = next;
				   	pathToChar(view, returnState, current, path);
				}else{
					//find the closest next ? mark or the place can find question mark
					if(path.size() == 0){
						path.addAll(findPoint(view,current, Constants.UNKNOW));
					}
			
				}
			}
		}

		

	}
	
	
	/**
	 * check current position is in the array list or not
	 * @param seen
	 * @param test
	 * @return
	 */
	public boolean seen(ArrayList<State> seen, State test){
		for(State s: seen){
			if(s.getRow() == test.getRow() && s.getCol() == test.getCol()){
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to translate the action to next valid point
	 * However, we are using BFS, the node from the queue will have different getPreState node
	 * Then we also need to calculate the path to go back to the getPreState node
	 * It allow to open the door and cut the tree
	 * @param board
	 * @param path
	 * @param current
	 * @param output
	 */
	public void pathToChar(Board board, State path, State current, ArrayList<Character> output){
		char[][] view = board.getBoard();
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

	/**
	 * Find the closest path to the given target by BFS in different mode
	 * it will try to find the valid point contain unexplore point
	 * @param board
	 * @param current
	 * @param target
	 * @return
	 */
	public ArrayList<Character> findPoint(Board board, State current, char target){
		char[][] view = board.getBoard();
		ArrayList<Character> output = new ArrayList<Character>();
		//write a bfs to water from current
		// BFS uses Queue data structure
		ArrayList<State> visited = new ArrayList<State>();
		ArrayList<State> p = new ArrayList<State>();
		Queue<State> queue = new LinkedList<State>();
		queue.add(current);
		breakloop:
		while(!queue.isEmpty()) {
			State prv = new State(queue.poll());
			State next = new State(prv);
			if(view[next.getRow()][next.getCol()] == target && !(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
				//return path
				if(target == Constants.WATER){
					boolean checkQuestion = false;
					Position origion =  new Position(next.getRow(),next.getCol());
					questionLoop:
					for(int row = origion.getRow()-2 ; row <= origion.getRow()+2; row++){
						for(int col = origion.getCol()-2 ; col <= origion.getCol()+2; col++){
							if(col > 0 && row >0 && col < Constants.BOARD_SIZE_COL && row < Constants.BOARD_SIZE_ROW){
								if(view[row][col] == Constants.UNKNOW && view[origion.getRow()][col] == Constants.WATER && view[row][origion.getCol()] != Constants.WATER){
									char t = Constants.BOUNDARY;
									if(checkForward(board,origion,t)){
										checkQuestion = true;
									}
									break questionLoop;
									
								}
								
							}
						}
					}
					if(checkQuestion){
						while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
							p.add(0,next);
							next = next.getPreState();
						}
						 p.add(0,next);
						for(State s : p){
							 if(view[s.getRow()][s.getCol()] == Constants.WATER){
								 waterSeen.add(s);
							 }
						}
						break breakloop;
					}
					
				} else {
					while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
						p.add(0,next);
						next = next.getPreState();
					}
					p.add(0,next);
					for(State s : p){
						if(inWater == false){
							exploreSeen.add(s);
						}else if(view[s.getRow()][s.getCol()] == Constants.WATER && inWater == true){
							waterSeen.add(s);
						}
					}
					break breakloop;
				}
			}
			
			if(!seen(exploreSeen, next) && !(next.getCol() == current.getCol() && next.getRow() == current.getRow()) && inWater == false){
				Position pos =  new Position(next.getRow(),next.getCol());
				char t = Constants.WATER;
				if(checkForward(board,pos,t)){
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
			
			if(!seen(waterSeen, next) && !(next.getCol() == current.getCol() && next.getRow() == current.getRow()) && hasWater == true){
				Position pos =  new Position(next.getRow(),next.getCol());
				char t = Constants.BOUNDARY;
				if(checkForward(board,pos,t)){
					while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
						p.add(0,next);
						next = next.getPreState();
					}
					 p.add(0,next);
					for(State s : p){
						 if(view[s.getRow()][s.getCol()] == Constants.WATER){
							 waterSeen.add(s);
						 }
					}
					break breakloop;
				}
			}
			
			
			next.setRow(prv.getRow()-1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in north getDirection
			if(!seen(visited, next)){
				if(hasWater == true && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(board, next)){
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
				if(hasWater == true && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(board, next)){
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
				if(hasWater == true && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(board, next)){
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
				if(hasWater == true && validWater(board,next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}else if(hasWater == false && valid(board, next)){
					next.setPreState(prv);
					visited.add(next);
					queue.add(next);
				}
			}
			
		}
		

		output = BFSpath(board, p);
		return output;
	}
	
	
	/**
	 * BFS path translate
	 * It will return a long path contain a lot commands
	 * @param board
	 * @param p
	 * @return
	 */
	public ArrayList<Character> BFSpath(Board board, ArrayList<State> p){
		char[][] view = board.getBoard();
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
			}
			
	        if(view[next.getRow()][next.getCol()] == Constants.DOOR){
	        	if(next.getKey()){
		        	output.add(Constants.UNLOCK_DOOR);
	        	}else if(next.getDynamite() >1){
		        	output.add(Constants.BLAST_WALL_TREE);
	        	}

	        }

	        if(view[next.getRow()][next.getCol()] == Constants.TREE && next.getAxe()){
	        	output.add(Constants.CHOP_TREE);
	        }
	       output.add(Constants.MOVE_FORWARD);
		}
		return output;
	}
	

	/**
	 * check 5X5 square surround the origin point
	 * then define the origin is worth to go 
	 * for land mode it will check it is a point surround water
	 * for water mode it will check it is a point surround boundary
	 * @param board
	 * @param origion
	 * @param target
	 * @return
	 */
	public boolean checkForward(Board board, Position origion, char target){
		char[][] view = board.getBoard();

		
		for(int row = origion.getRow()-2 ; row <= origion.getRow()+2; row++){
			for(int col = origion.getCol()-2 ; col <= origion.getCol()+2; col++){
				if(col > 0 && row >0 && col < Constants.BOARD_SIZE_COL && row < Constants.BOARD_SIZE_ROW){
					if(view[row][col] == Constants.UNKNOW && view[origion.getRow()][col] != target && view[row][origion.getCol()] != target){
						//unknow =  true;
						//check neighbor has water
						//break;
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	/**
	 * Known the getDirection which current node need to go
	 * And known current node's getDirection
	 * Calculating which getDirection current node should turn around
	 * @param aim
	 * @param d
	 * @return
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
	
	/**
	 *Check current position is valid or not
	 * @param board
	 * @param current
	 * @return
	 */
	public boolean valid(Board board , State current){
		char[][] view = board.getBoard();
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
			if(board.returnTreeNum() <=1 && exploreLand == true){
				return false;
			}else{
				return current.getAxe() || current.getDynamite() > 1;
			}


		} else if(view[current.getRow()][current.getCol()] == Constants.UNKNOW){
			return true;
		}
		// it is a door
		else if(view[current.getRow()][current.getCol()] == Constants.DOOR){
			if(current.getKey() == true || current.getDynamite() > 1){
				return true;
			}else{
				return false;
			}
		} else if(view[current.getRow()][current.getCol()] == Constants.DYNAMITE){
			return true;
		}
		
		return true;
	}

	/**
	 * Check current position is valid or not in the water mode
	 * @param board
	 * @param current
	 * @return
	 */
	public boolean validWater(Board board, State current){
		char[][] view = board.getBoard();
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
		if(view[current.getRow()][current.getCol()] == Constants.WATER && current.getRaft() == true){
			return true;
		} else if(view[current.getRow()][current.getCol()] == Constants.UNKNOW){
			return true;
		}
		return false;
	}
}