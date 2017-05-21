import java.util.*;
public class Explore {
    
    // queue for explore
    private ArrayList<State> exploreQ;
    private ArrayList<State> exploreSeen;
    private ArrayList<State> changeSeen;
    private ArrayList<State> charPath;
    
	public Explore(){
		exploreQ = new ArrayList<State>();
		exploreSeen = new ArrayList<State>();
		changeSeen = new ArrayList<State>();
		charPath = new ArrayList<State>();
	}
	
	public void to_Gold(){
		
	}
	
	public void to_Origin(){
		
	}
	
	public void to_poi(){
		
	}
	
	/*
	 * This method is trying to explore the unknown node in the board by BFS
	 * It will check the valid point surround the current position (check NSWE getDirection)
	 * For the next valid point, check the 5x5 area, whether it exists any unknown point
	 * If exist unknown point add the next node to the queue
	 * It only return one state each time
	 */
	
	public State explore(char[][] view, State current){
		/*System.out.println("-----------------Queue-----------------");
		for(State s :  exploreQ){
			s.printState();
		}
		System.out.println("---------------------------------------");*/
		State prv = new State(current);
		State next = new State(prv);
		State returnState = null;

		//expand North getRow-1
		next.setRow(prv.getRow()-1);
		next.setCol(prv.getCol());
		// check if player allow to go forward in north getDirection
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.setPreState(prv);
			exploreSeen.add(next);
			if(returnState == null){
			//System.out.println("S push step: " + next.getRow() + " " + next.getCol());
				returnState = next;
			}else{
				exploreQ.add(next);
			}
		}
		
		//expand South getRow+1
		next = new State(prv);
		next.setRow(prv.getRow()+1);
		next.setCol(prv.getCol());
		
		// check if player allow to go forward in north getDirection
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.setPreState(prv);
			exploreSeen.add(next);
			if(returnState == null){
			//System.out.println("S push step: " + next.getRow() + " " + next.getCol());
				returnState = next;
			}else{
				exploreQ.add(next);
			}

		}
		
		//expand West getCol-1
		next = new State(prv);
		next.setRow(prv.getRow());
		next.setCol(prv.getCol()-1);
		
		// check if player allow to go forward in north getDirection
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.setPreState(prv);
			exploreSeen.add(next);
			if(returnState == null){
				//System.out.println("W push step: " + next.getRow() + " " + next.getCol());
				returnState = next;
			}else{	
				exploreQ.add(next);
			}
		}
		
		
		//expand East getCol+1
		next = new State(prv);
		next.setRow(prv.getRow());
		next.setCol(prv.getCol()+1);
		// check if player allow to go forward in north getDirection
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.setPreState(prv);
			exploreSeen.add(next);
			if(returnState == null){
				//System.out.println("E push step: " + next.getRow() + " " + next.getCol());
				returnState = next;
			}else{
				exploreQ.add(next);
			}
				

		}

		//if all valid points surround this position are checked, pop first element from the queue
		if(exploreQ.size() != 0 && returnState == null){
			// If the node is changing the getDirection in current position then do not return any node
			if(current.getPreState() != null && !(current.getPreState().getRow() == current.getRow() && current.getPreState().getCol() == current.getCol())){
	    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!POP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				returnState = exploreQ.get(0);
				exploreQ.remove(0);
			}
		}
		return returnState;
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
			/*System.out.println("-----------------Recieve----------------");
			for(State s: charPath){
				s.printState();
			}
			System.out.println("----------------------------------------");
			*/
		if((path != null)|| output.size() == 0){
			if(output.isEmpty()){
				changeSeen.clear();
			}
			if(path!= null){
				charPath.add(path);
			}
			
			if(charPath.size() != 0){
				//check the current node's getPreState position
				// if its position is different to current position
				// then we need to go back to the getPreState point first then go to the next point
				// this part only called when the output doesn't have any action anymore to get the newest current position
				State next1 = charPath.get(0).getPreState();
				if(!(current.getRow() == next1.getRow() && current.getCol()== next1.getCol())){
			    		//find the path to that point
						if(!seen(changeSeen, next1) && output.size() == 0){
							//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				    		changeSeen.add(next1);
				    		
		    					/*System.out.println("getRow: " + next1.getRow());
		    					System.out.println("getCol: " + next1.getCol());
		    					System.out.println("current getRow: " + current.getRow());
		    					System.out.println("current getCol: " + current.getCol());*/
		    					ArrayList<Character> a = pathReverse(view,next1,current);
		    					output.addAll(a);
				    		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						}
			    		
			 
			    	} else{
						// translate the path to next point to command
						State next = charPath.get(0);
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
							//next.printState();

						}

						if(view[next.getRow()][next.getCol()] == Constants.DOOR && next.getKey()){
							output.add(Constants.UNLOCK_DOOR);
						}
						if(view[next.getRow()][next.getCol()] == Constants.TREE && next.getAxe()){
							output.add(Constants.CHOP_TREE);
						}
					   output.add(Constants.MOVE_FORWARD);

					   charPath.remove(0);
				}
			}
		}

	}
	
	/*
	 * This method is used to find the path to target position from current by BFS
	 */
	
	public ArrayList<Character> pathReverse(char[][] view, State target, State current){
		ArrayList<Character> output = new ArrayList<Character>();
		//write a bfs to target from current
		// BFS uses Queue data structure
		Queue<State> queue = new LinkedList<State>();
		ArrayList<State> visited = new ArrayList<State>();
		ArrayList<State> p = new ArrayList<State>();
		queue.add(current);
		visited.add(current);
		while(!queue.isEmpty()) {

			State prv = queue.poll();
			State next = new State(prv);
			if(next.getRow() == target.getRow() && next.getCol() == target.getCol()){
				//return path
				//System.out.println("------------------------------------------------------------------");
				while(!(next.getCol() == current.getCol() && next.getRow() == current.getRow())){
					p.add(0,next);
					next = next.getPreState();
				}
				p.add(0,next);
				/*for(State s : p){
					System.out.println("------------------------------------------------------------------");
					s.printState();
					System.out.println("------------------------------------------------------------------");
				}*/
				break;
			}
			next.setRow(prv.getRow()-1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in north getDirection
			if(!seen(visited, next) && valid(view, next)){
				next.setPreState(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.setRow(prv.getRow()+1);
			next.setCol(prv.getCol());
			// check if player allow to go forward in south getDirection
			if(!seen(visited, next) && valid(view, next)){
				next.setPreState(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.setRow(prv.getRow());
			next.setCol(prv.getCol()-1);
			// check if player allow to go forward in west getDirection
			if(!seen(visited, next) && valid(view, next)){
				next.setPreState(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.setRow(prv.getRow());
			next.setCol(prv.getCol()+1);
			// check if player allow to go forward in east getDirection
			if(!seen(visited, next) && valid(view, next)){
				next.setPreState(prv);
				visited.add(next);
				queue.add(next);
			}
			
		}
		
		// translate the path to the target to command
		for(int i = 0; i < p.size()-1; i++){
			if(i+1 > p.size()-1){
				break;
			}
			State prv = p.get(i);
			State next = p.get(i+1);
			//System.out.println("getDirection: " + getPreState.getDirection());
			//next.printState();
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
		
		//System.out.println(output);

		return output;
	}
	
	/*
	 * Known the getDirection which current node need to go
	 * And known current node's getDirection
	 * Calculating which getDirection current node should turn around
	 */
	public ArrayList<Character> directionAction(int aim, int d){
		ArrayList<Character> output = new ArrayList<Character>();
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
			return false;
		}
		// it is tree
		else if (view[current.getRow()][current.getCol()] == Constants.TREE){
			//check getAxe or dynamite
			return current.getAxe() || current.dynamite() > 1;
		}
		// it is a door
		else return view[current.getRow()][current.getCol()] != Constants.DOOR || current.getKey();
	}
}
