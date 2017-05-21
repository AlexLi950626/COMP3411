import java.util.*;

public class Search {
	//Max number
	public static final int CHAR_MAX = 10000;
	
    // board type
    public static final char TREE = 'T';
    public static final char DOOR = '-';
    public static final char WALL = '*';
    public static final char WATER = '~';
    public static final char AXE = 'a';
    public static final char KEY = 'k';
    public static final char DYNAMITE = 'd';
    public static final char TREASURE = '$';
    public static final char UNKNOW = 'u';
    public static final char BOUNDAY = '.';
    public static final char EMPTY = ' ';
    
    //commands
    public static final char TURN_LEFT = 'L';
    public static final char TURN_RIGHT = 'R';
    public static final char MOVE_FORWARD = 'F';
    public static final char CHOP_TREE = 'C';
    public static final char BLAST_WALL_TREE = 'B';
    public static final char UNLOCK_DOOR = 'U';
    
    // read directions
    public static final char NORTH = 0;
    public static final char SOUTH = 2;
    public static final char EAST = 1;
    public static final char WEST = 3;
    
    // board size
    public static final int BOARD_SIZE_ROW = 164;
    public static final int BOARD_SIZE_COL = 164;
    
    // queue for explore
    private ArrayList<State> exploreQ;
    private ArrayList<State> exploreSeen;
    private ArrayList<State> changeSeen;
    private ArrayList<State> charPath;
    
	public Search(){
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
		if( seen(exploreSeen, next) == false && vaild(view, next) == true){
			next.updatePrv(prv);
			exploreSeen.add(next);
			//ArrayList<State> path = new ArrayList<State>();
			for(int newCol = next.col()-2; newCol <= next.col()+2; newCol++){
				//check any unknown position in the 5x5 area for the next step
				if(view[next.row()-2][newCol] == UNKNOW){
					if(returnState == null){
						//System.out.println("N push step: " + next.row() + " " + next.col());
						returnState = next;
					}
                    break;
				}
			}
			if(returnState != null && seen(exploreQ, next) == false){
				//System.out.println("Queue N push step: " + next.row() + " " + next.col());
				exploreQ.add(next);
			}

		}
		
		//expand South row+1
		next = new State(prv);
		next.updateRow(prv.row()+1);
		next.updateCol(prv.col());
		
		// check if player allow to go forward in north direction
		if(seen(exploreSeen, next) == false && vaild(view, next) == true){
			next.updatePrv(prv);
			exploreSeen.add(next);
			for(int newCol = next.col()-2; newCol <= next.col()+2; newCol++){
				//check any unknown position in the 5x5 area for the next step
				if(view[next.row()+2][newCol] == UNKNOW){
					if(returnState == null){
						//System.out.println("S push step: " + next.row() + " " + next.col());
						returnState = next;
					}
                    break;
				}
			}
			if(returnState != null && seen(exploreQ, next) == false){
				//System.out.println("Queue S push step: " + next.row() + " " + next.col());
				exploreQ.add(next);
			}

		}
		
		//expand West col-1
		next = new State(prv);
		next.updateRow(prv.row());
		next.updateCol(prv.col()-1);
		
		// check if player allow to go forward in north direction
		if(seen(exploreSeen, next) == false && vaild(view, next) == true){
			next.updatePrv(prv);
			exploreSeen.add(next);
			for(int newRow = next.row()-2; newRow <= next.row()+2; newRow++){
				//check any unknown position in the 5x5 area for the next step
				if(view[newRow][next.col()-2] == UNKNOW){
					if(returnState == null){
						//System.out.println("W push step: " + next.row() + " " + next.col());
						returnState = next;
					}
                    break;
				}
			}
			if(returnState != null && seen(exploreQ, next) == false){
				//System.out.println("Queue W push step: " + next.row() + " " + next.col());
				exploreQ.add(next);
			}
		}
		
		
		//expand East col+1
		next = new State(prv);
		next.updateRow(prv.row());
		next.updateCol(prv.col()+1);
		// check if player allow to go forward in north direction
		if(seen(exploreSeen, next) == false && vaild(view, next) == true){
			next.updatePrv(prv);
			exploreSeen.add(next);
			for(int newRow = next.row()-2; newRow <= next.row()+2; newRow++){
				//check any unknown position in the 5x5 area for the next step
				if(view[newRow][next.col()+2] == UNKNOW){
					if(returnState == null){
						//System.out.println("E push step: " + next.row() + " " + next.col());
						returnState = next;
					}
                    break;
				}
			}
			if(returnState != null && seen(exploreQ, next) == false){
				//System.out.println("Queue E push step: " + next.row() + " " + next.col());
				exploreQ.add(next);
			}

		}
		if(exploreQ.size() != 0 && returnState == null){
			if(current.prv() != null && !(current.prv().row() == current.row() && current.prv().col() == current.col())){
	    		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!POP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				returnState = exploreQ.get(0);
				exploreQ.remove(0);
			}
		}
		return returnState;
	}
	
	public boolean seen(ArrayList<State> seen, State test){
		for(State s: seen){
			if(s.row() == test.row() && s.col() == test.col()){
				return true;
			}
		}
		return false;
	}
	
	
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

			State next1 = charPath.get(0).prv();
			if(!(current.row() == next1.row() && current.col()== next1.col())){
		    		//find the path to that point
					if(seen(changeSeen,next1) == false && output.size() == 0){
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
		    			State next = charPath.get(0);
		    			State prv = current;
		    			if(next.row() == prv.row()-1 && next.col() == prv.col()){
		    				//go north
		    				output.addAll(directionAction(NORTH, prv.direction()));
		    				
		    			}else if(next.row() == prv.row()+1 && next.col() == prv.col()){
		    				//go south
		    				output.addAll(directionAction(SOUTH,prv.direction()));
		    				
		    			}else if(next.row() == prv.row() && next.col() == prv.col()-1){
		    				//go west
		    				output.addAll(directionAction(WEST,prv.direction()));


		    			}else if(next.row() == prv.row() && next.col() == prv.col()+1){
		    				//go east
		    				output.addAll(directionAction(EAST,prv.direction()));

		    			} else {
		    				//bad path
		    				System.out.println("bad path");
		    				//next.printState();

		    			}
		    			
		                if(view[next.row()][next.col()] == DOOR && next.key() == true){
		                	output.add(UNLOCK_DOOR);
		                }
		                if(view[next.row()][next.col()] == TREE && next.axe() == true){
		                	output.add(CHOP_TREE);
		                }
		               output.add(MOVE_FORWARD);
		               
		               charPath.remove(0);
			}
		}


	}
	
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
			if(seen(visited,next) == false && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row()+1);
			next.updateCol(prv.col());
			// check if player allow to go forward in south direction
			if(seen(visited,next)== false && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()-1);
			// check if player allow to go forward in west direction
			if(seen(visited,next)== false && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()+1);
			// check if player allow to go forward in east direction
			if(seen(visited,next)== false && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			
		}
		
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
				output.addAll(directionAction(NORTH, prv.direction()));
				p.get(i+1).updateDirection(NORTH);
				
			}else if(next.row() == prv.row()+1 && next.col() == prv.col()){
				//go south
				output.addAll(directionAction(SOUTH,prv.direction()));
				p.get(i+1).updateDirection(SOUTH);

				
			}else if(next.row() == prv.row() && next.col() == prv.col()-1){
				//go west
				output.addAll(directionAction(WEST,prv.direction()));
				p.get(i+1).updateDirection(WEST);



			}else if(next.row() == prv.row() && next.col() == prv.col()+1){
				//go east
				output.addAll(directionAction(EAST,prv.direction()));
				p.get(i+1).updateDirection(EAST);


			} else {
				//bad path
				System.out.println("bad path");
				//next.printState();

			}
			
	        if(view[next.row()][next.col()] == DOOR && next.key() == true){
	        	output.add(UNLOCK_DOOR);
	        }
	        if(view[next.row()][next.col()] == TREE && next.axe() == true){
	        	output.add(CHOP_TREE);
	        }
	       output.add(MOVE_FORWARD);
		}
		
		//System.out.println(output);

		return output;
	}
	
	public ArrayList<Character> directionAction(int aim, int d){
		ArrayList<Character> output = new ArrayList<Character>();
		switch (aim){
		case NORTH:
			switch(d){
			case SOUTH:
				output.add(TURN_LEFT);
				output.add(TURN_LEFT);						
				break;
			case WEST:
				output.add(TURN_RIGHT);
				break;
			case EAST:
				output.add(TURN_LEFT);
				break;
			case NORTH:
				break;
			}
		break;
		case SOUTH:
			switch(d){
			case NORTH:
				output.add(TURN_LEFT);
				output.add(TURN_LEFT);
				break;
			case WEST:
				output.add(TURN_LEFT);
				break;
			case EAST:
				output.add(TURN_RIGHT);
				break;
			case SOUTH:
				break;
			}
		break;
		case WEST:
			switch(d){
			case NORTH:
				output.add(TURN_LEFT);
				break;
			case SOUTH:
				output.add(TURN_RIGHT);
				break;
			case EAST:
				output.add(TURN_LEFT);
				output.add(TURN_LEFT);
				break;
			case WEST:
				break;
			}
		break;
		case EAST:
			switch(d){
			case NORTH:
				output.add(TURN_RIGHT);
				break;
			case SOUTH:
				output.add(TURN_LEFT);
				break;
			case WEST:
				output.add(TURN_LEFT);
				output.add(TURN_LEFT);
				break;
			case EAST:
				break;
			}
		break;
		}
		return output;
	}
	
	public boolean vaild(int[][]view, State current){
		// view overflow or not
		if(current.row() >  BOARD_SIZE_ROW-1 || current.col() > BOARD_SIZE_COL-1){
			return false;
		}
		if(current.row() < 0 || current.col() < 0){
			return false;
		}
		//check boundary 5X% area
		if(current.row()+2 >  BOARD_SIZE_ROW-1 || current.row()-2 < 0){
			return false;
		}
		if(current.col()+2 >  BOARD_SIZE_COL-1 || current.col()-2 < 0){
			return false;
		}
		// it is a wall
		if(view[current.row()][current.col()] == WALL && current.dynamite() < 1){
			return false;
		}
		// it is over boundary '.'
		else if(view[current.row()][current.col()] == BOUNDAY){
			return false;
		}
		// it is water
		else if(view[current.row()][current.col()] == WATER ){//&& current.raft() == false){  currently dun add this for the BFS
			//check raft
			return false;
		}
		// it is tree
		else if (view[current.row()][current.col()] == TREE){
			//check axe or dynamite
			if(current.axe() == true || current.dynamite() > 1){
				return true;
			} else {
				return false;
			}
		}
		// it is a door
		else if (view[current.row()][current.col()] == DOOR && current.key() == false){
			// check key
			return false;
		}else{
			return true;
		}
	}
}
