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
    private Queue<State> exploreQ;
    private ArrayList<State> exploreSeen;
    private ArrayList<State> changeSeen;
    private ArrayList<State> charPath;
    
	public Search(){
		exploreQ = new LinkedList<State>();
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
		//exploreQ.add(current);
		//while(!exploreQ.isEmpty()){
			//System.out.println("----------------------------------");
			//for (State a : exploreQ) {
			//	  a.printState();
			//}
			//System.out.println("----------------------------------");
			State prv = current;
			State next = new State(prv);
			State returnState = null;
			//prv.printState();
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
						//current to the next position
						//State step = new State(next);
						//path.add(step);
                        /*while (step.col() != current.col() || step.row() != current.row()){
                        	if(step.prv() == null){
                        		path.add(0,step);
                        		break;
                        	} 
                        		step = step.prv();
                        	//if(step.prv().row() != step.row() ||step.prv().col() != step.col()){
                        		path.add(0,step);
                        	//}

                        }*/
                        //System.out.println("N push step: " + step.row() + " " + step.col());
						System.out.println("N push step: " + next.row() + " " + next.col());
						if(returnState == null){
							returnState = next;
						}
						//exploreQ.add(next);
                        break;
					}
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
				ArrayList<State> path = new ArrayList<State>();
				for(int newCol = next.col()-2; newCol <= next.col()+2; newCol++){
					//check any unknown position in the 5x5 area for the next step
					if(view[next.row()+2][newCol] == UNKNOW){
						//current to the next position
						//State step = new State(next);
						
						//path.add(step);
                        /*while (step.col() != current.col() || step.row() != current.row()){
                        	if(step.prv() == null){
                        		path.add(0,step);
                        		break;
                        	}
                        	step = step.prv();
                        	//if(step.prv().row() != step.row() ||step.prv().col() != step.col()){
                        		//step = step.prv();
                    		path.add(0,step);
                        	//}
                    		
                        }*/
                       // System.out.println("S push step: " + step.row() + " " + step.col());
						System.out.println("S push step: " + next.row() + " " + next.col());
						if(returnState == null){
							returnState = next;
						}else{
							exploreQ.add(next);
						}
                        break;
					}
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
				ArrayList<State> path = new ArrayList<State>();
				for(int newRow = next.row()-2; newRow <= next.row()+2; newRow++){
					//check any unknown position in the 5x5 area for the next step
					if(view[newRow][next.col()-2] == UNKNOW){
						//current to the next position
						//State step = new State(next);
						//path.add(step);
                        /*while (step.col() != current.col() || step.row() != current.row()){
                        	if(step.prv() == null){
                        		path.add(0,step);
                        		break;
                        	}
                        	step = step.prv();
                        	//if(step.prv().row() != step.row() ||step.prv().col() != step.col()){
                        		path.add(0,step);
                        	//}
                    		

                        }*/
                        //System.out.println("W push step: " + step.row() + " " + step.col());
						System.out.println("W push step: " + next.row() + " " + next.col());
						//exploreQ.add(next);
						if(returnState == null){
							returnState = next;
						}else{
							exploreQ.add(next);
						}
                        break;
					}
				}
				//return path;
			}
			
			
			//expand East col+1
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()+1);
			// check if player allow to go forward in north direction
			if(seen(exploreSeen, next) == false && vaild(view, next) == true){
				next.updatePrv(prv);
				exploreSeen.add(next);
				//System.out.println(vaild(view, next));
				//System.out.println((char)view[next.row()][next.col()]);
				ArrayList<State> path = new ArrayList<State>();
				for(int newRow = next.row()-2; newRow <= next.row()+2; newRow++){
					//check any unknown position in the 5x5 area for the next step
					if(view[newRow][next.col()+2] == UNKNOW){
						//current to the next position
						//State step = new State(next);
						//ArrayList<State> path = new ArrayList<State>();
						//path.add(step);
                        /*while (step.col() != current.col() || step.row() != current.row()){
                        	if(step.prv() == null){
                        		path.add(0,step);
                        		break;
                        	}
                        	//if(step.prv().row() != step.row() || step.prv().col() != step.col()){
                        		step = step.prv();
                        		path.add(0,step);
                        	//}
                        	//step = step.prv();
                        }*/
                        //System.out.println("E push step: " + step.row() + " " + step.col());
						System.out.println("E push step: " + next.row() + " " + next.col());
						if(returnState == null){
							returnState = next;
						}else{
							exploreQ.add(next);
						}
						//exploreQ.add(next);
                        break;
					}
				}

				//return  exploreQ.poll();
			}
		//}
		if(returnState == null){
			returnState = exploreQ.poll();
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
		//ArrayList<Character> output = new ArrayList<Character>();
		//charPath.add(path);
		System.out.println("----------------------------------");
		//for (State a : path) {
			  path.printState();
		//}
		System.out.println("----------------------------------");
		State next1 = path.prv();
		//if(next1 != null){
		if(!(current.row() == next1.row() && current.col()== next1.col())){
	    		//find the path to that point
				System.out.println("here!");
				if(!changeSeen.contains(next1)){
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    		changeSeen.add(next1);
		    		//current.printState();
		    		//next1.printState();
		    		checkloop:
		    		for(State s1 : current.path()){

		    			for(State s2: next1.path()){

		    				if(s1.row() == s2.row() && s1.col() == s2.col()){

		    					System.out.println("row: " + s1.row());
		    					System.out.println("col: " + s1.col());
		    					ArrayList<Character> a = pathReverse(view,s1,current);
		    					System.out.println(a);
		    					output.addAll(0, a);
		    					break checkloop;

		    				}
		    			}
		    		}
		    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
	    		
	 
	    	} else{
	    		
	    		//for(int i = path.size()-1; i >= 1; i--){
	    			/*if(i-1<0){
	    				break;
	    			}*/
	    			State next = path; 
	    			State prv = next.prv();
	    			System.out.println("direction: " + prv.direction());
	    			next.printState();
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
	    		//}
	    		//charPath.remove(0);
	    	//}
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
				System.out.println("------------------------------------------------------------------");

				while(!(next.col() == current.col() && next.row() == current.row())){
					p.add(0,next);
					next = next.prv();
				}
				for(State s : p){
					System.out.println("------------------------------------------------------------------");
					s.printState();
					System.out.println("------------------------------------------------------------------");
				}
				break;
			}
			next.updateRow(prv.row()-1);
			next.updateCol(prv.col());
			// check if player allow to go forward in north direction
			if(!visited.contains(next) && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row()+1);
			next.updateCol(prv.col());
			// check if player allow to go forward in north direction
			if(!visited.contains(next) && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()-1);
			// check if player allow to go forward in north direction
			if(!visited.contains(next) && vaild(view, next) == true){
				next.updatePrv(prv);
				visited.add(next);
				queue.add(next);
			}
			next = new State(prv);
			next.updateRow(prv.row());
			next.updateCol(prv.col()+1);
			// check if player allow to go forward in north direction
			if(!visited.contains(next) && vaild(view, next) == true){
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
			System.out.println("direction: " + prv.direction());
			next.printState();
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
		
		System.out.println(output);
		
		
		
		
		
		
		
		
		
		
		/*State prv = new State(current);
		while(target.row() != prv.row() || target.col() != prv.col()){	
			
			if(prv.prv().row() == prv.row()-1 && prv.prv().col() == prv.col()){
				//go north
				System.out.println("go n");

				output.addAll(directionAction(NORTH, prv.direction()));
		        output.add(MOVE_FORWARD);
		        output.addAll(directionAction(prv.prv().direction(),NORTH));
				//break;
			}else if(prv.prv().row() == prv.row()+1 && prv.prv().col() == prv.col()){
				//go south
				System.out.println("go s");

				output.addAll(directionAction(SOUTH,prv.direction()));
		        output.add(MOVE_FORWARD);
		        output.addAll(directionAction(prv.prv().direction(),SOUTH));
				//break;
			}else if(prv.prv().row() == prv.row() && prv.prv().col() == prv.col()-1){
				//go west
				System.out.println("go w");

				output.addAll(directionAction(WEST,prv.direction()));
		        output.add(MOVE_FORWARD);
		        output.addAll(directionAction(prv.prv().direction(),WEST));
		        //break;
			}else if(prv.prv().row() == prv.row() && prv.prv().col() == prv.col()+1){
				//go east
				System.out.println("go e");
				output.addAll(directionAction(EAST,prv.direction()));
		        output.add(MOVE_FORWARD);
		        output.addAll(directionAction(prv.prv().direction(),EAST));
		        //break;
			}else if (prv.prv().row() == prv.row() && prv.prv().col() == prv.col()){
		        output.addAll(directionAction(prv.prv().direction(),prv.direction()));

			}
			System.out.println("-----------------------------------------------------");
			System.out.println("current direction " + prv.direction());
			System.out.println("aim direction " + prv.prv().direction());

			System.out.println(prv.row() + " " + prv.col());
			System.out.println(prv.prv().row() + " " + prv.prv().col());
			System.out.println("-----------------------------------------------------");

			System.out.println(output);
	        prv = new State(prv.prv());
		}*/

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
				output.add(TURN_LEFT);
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
				output.add(TURN_LEFT);
				break;
			case SOUTH:
				output.add(TURN_RIGHT);
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
		else if(view[current.row()][current.col()] == WATER && current.raft() == false){
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
