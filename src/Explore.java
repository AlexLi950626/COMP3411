import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

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
	 * It will check the valid point surround the current position (check NSWE direction)
	 * For the next valid point, check the 5x5 area, whether it exists any unknown point
	 * If exist unknown point add the next node to the queue
	 * It only return one state each time
	 */
	
	public State explore(int[][] view, State current){
		/*System.out.println("-----------------Queue-----------------");
		for(State s :  exploreQ){
			s.printState();
		}
		System.out.println("---------------------------------------");*/
		State prv = new State(current);
		State next = new State(prv);
		State returnState = null;

		//expand North row-1
		next.updateRow(prv.row()-1);
		next.updateCol(prv.col());
		// check if player allow to go forward in north direction
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.updatePrv(prv);
			exploreSeen.add(next);
			if(returnState == null){
			//System.out.println("S push step: " + next.row() + " " + next.col());
				returnState = next;
			}else{
				exploreQ.add(next);
			}
		}
		
		//expand South row+1
		next = new State(prv);
		next.updateRow(prv.row()+1);
		next.updateCol(prv.col());
		
		// check if player allow to go forward in north direction
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.updatePrv(prv);
			exploreSeen.add(next);
			if(returnState == null){
			//System.out.println("S push step: " + next.row() + " " + next.col());
				returnState = next;
			}else{
				exploreQ.add(next);
			}

		}
		
		//expand West col-1
		next = new State(prv);
		next.updateRow(prv.row());
		next.updateCol(prv.col()-1);
		
		// check if player allow to go forward in north direction
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.updatePrv(prv);
			exploreSeen.add(next);
			if(returnState == null){
				//System.out.println("W push step: " + next.row() + " " + next.col());
				returnState = next;
			}else{	
				exploreQ.add(next);
			}
		}
		
		
		//expand East col+1
		next = new State(prv);
		next.updateRow(prv.row());
		next.updateCol(prv.col()+1);
		// check if player allow to go forward in north direction
		if(!seen(exploreSeen, next) && valid(view, next)){
			next.updatePrv(prv);
			exploreSeen.add(next);
			if(returnState == null){
				//System.out.println("E push step: " + next.row() + " " + next.col());
				returnState = next;
			}else{
				exploreQ.add(next);
			}
				

		}

		//if all valid points surround this position are checked, pop first element from the queue
		if(exploreQ.size() != 0 && returnState == null){
			// If the node is changing the direction in current position then do not return any node 
			if(current.prv() != null && !(current.prv().row() == current.row() && current.prv().col() == current.col())){
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
			if(s.row() == test.row() && s.col() == test.col()){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * This method is used to translate the action to next valid point
	 * However, we are using BFS, the node from the queue will have different prv node
	 * Then we also need to calculate the path to go back to the prv node
	 */
	public void pathToChar(int[][] view, State path, State current, ArrayList<Character> output){
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
				//check the current node's prv position
				// if its position is different to current position
				// then we need to go back to the prv point first then go to the next point
				// this part only called when the output doesn't have any action anymore to get the newest current position
				State next1 = charPath.get(0).prv();
				if(!(current.row() == next1.row() && current.col()== next1.col())){
			    		//find the path to that point
						if(!seen(changeSeen, next1) && output.size() == 0){
							//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				    		changeSeen.add(next1);
				    		
		    					/*System.out.println("row: " + next1.row());
		    					System.out.println("col: " + next1.col());
		    					System.out.println("current row: " + current.row());
		    					System.out.println("current col: " + current.col());*/
		    					ArrayList<Character> a = pathReverse(view,next1,current);
		    					output.addAll(a);
				    		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						}
			    		
			 
			    	} else{
						// translate the path to next point to command
						State next = charPath.get(0);
						if(next.row() == current.row()-1 && next.col() == current.col()){
							//go north
							output.addAll(directionAction(Constants.NORTH, current.direction()));

						}else if(next.row() == current.row()+1 && next.col() == current.col()){
							//go south
							output.addAll(directionAction(Constants.SOUTH, current.direction()));

						}else if(next.row() == current.row() && next.col() == current.col()-1){
							//go west
							output.addAll(directionAction(Constants.WEST, current.direction()));


						}else if(next.row() == current.row() && next.col() == current.col()+1){
							//go east
							output.addAll(directionAction(Constants.EAST, current.direction()));

						} else {
							//bad path
							System.out.println("bad path");
							//next.printState();

						}

						if(view[next.row()][next.col()] == Constants.DOOR && next.key()){
							output.add(Constants.UNLOCK_DOOR);
						}
						if(view[next.row()][next.col()] == Constants.TREE && next.axe()){
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
	
	public ArrayList<Character> pathReverse(int[][] view, State target, State current){
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
			if(next.row() == target.row() && next.col() == target.col()){
				//return path
				//System.out.println("------------------------------------------------------------------");
				while(!(next.col() == current.col() && next.row() == current.row())){
					p.add(0,next);
					next = next.prv();
				}
				p.add(0,next);
				/*for(State s : p){
					System.out.println("------------------------------------------------------------------");
					s.printState();
					System.out.println("------------------------------------------------------------------");
				}*/
				break;
			}
			next.updateRow(prv.row()-1);
			next.updateCol(prv.col());
			// check if player allow to go forward in north direction
			if(!seen(visited, next) && valid(view, next)){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row()+1);
			next.updateCol(prv.col());
			// check if player allow to go forward in south direction
			if(!seen(visited, next) && valid(view, next)){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()-1);
			// check if player allow to go forward in west direction
			if(!seen(visited, next) && valid(view, next)){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()+1);
			// check if player allow to go forward in east direction
			if(!seen(visited, next) && valid(view, next)){
				next.updatePrv(prv);
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
			//System.out.println("direction: " + prv.direction());
			//next.printState();
			if(next.row() == prv.row()-1 && next.col() == prv.col()){
				//go north
				output.addAll(directionAction(Constants.NORTH, prv.direction()));
				p.get(i+1).updateDirection(Constants.NORTH);
				
			}else if(next.row() == prv.row()+1 && next.col() == prv.col()){
				//go south
				output.addAll(directionAction(Constants.SOUTH,prv.direction()));
				p.get(i+1).updateDirection(Constants.SOUTH);

				
			}else if(next.row() == prv.row() && next.col() == prv.col()-1){
				//go west
				output.addAll(directionAction(Constants.WEST,prv.direction()));
				p.get(i+1).updateDirection(Constants.WEST);



			}else if(next.row() == prv.row() && next.col() == prv.col()+1){
				//go east
				output.addAll(directionAction(Constants.EAST,prv.direction()));
				p.get(i+1).updateDirection(Constants.EAST);


			} else {
				//bad path
				System.out.println("bad path");
				//next.printState();

			}
			
	        if(view[next.row()][next.col()] == Constants.DOOR && next.key()){
	        	output.add(Constants.UNLOCK_DOOR);
	        }
	        if(view[next.row()][next.col()] == Constants.TREE && next.axe()){
	        	output.add(Constants.CHOP_TREE);
	        }
	       output.add(Constants.MOVE_FORWARD);
		}
		
		//System.out.println(output);

		return output;
	}
	
	/*
	 * Known the direction which current node need to go
	 * And known current node's direction
	 * Calculating which direction current node should turn around
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
	public boolean valid(int[][]view, State current){
		// view overflow or not
		if(current.row() >  Constants.BOARD_SIZE_ROW-1 || current.col() > Constants.BOARD_SIZE_COL-1){
			return false;
		}
		if(current.row() < 0 || current.col() < 0){
			return false;
		}
		//check boundary 5X% area
		if(current.row()+2 >  Constants.BOARD_SIZE_ROW-1 || current.row()-2 < 0){
			return false;
		}
		if(current.col()+2 >  Constants.BOARD_SIZE_COL-1 || current.col()-2 < 0){
			return false;
		}
		// it is a wall
		if(view[current.row()][current.col()] == Constants.WALL){// && current.dynamite() < 1){
			return false;
		}
		// it is over boundary '.'
		else if(view[current.row()][current.col()] == Constants.BOUNDARY){
			return false;
		}
		// it is water
		else if(view[current.row()][current.col()] == Constants.WATER ){//&& current.raft() == false){  currently dun add this for the BFS
			//check raft
			return false;
		}
		// it is tree
		else if (view[current.row()][current.col()] == Constants.TREE){
			//check axe or dynamite
			return current.axe() || current.dynamite() > 1;
		}
		// it is a door
		else return view[current.row()][current.col()] != Constants.DOOR || current.key();
	}
}
