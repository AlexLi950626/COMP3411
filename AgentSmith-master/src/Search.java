import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;


public class Search {
	Map map;
	Inventory inventory;
	Node objective;
	
	public Search(Map _map, Inventory _inventory, Node _objective){
		map = _map;
		inventory = _inventory;
		objective = _objective;
	}
	
	/**
	 * Method that populates the nodes in the map with Heuristic values
	 */
	public void populateHeuristics(){
		Node[][] grid = map.getMap();
		int i = 0;
		int j = 0;
		
		int goalX = objective.getX();
		int goalY = objective.getY();
		
		for(i = 0; i < map.getMaxEdge(); i++){
			for(j = 0; j < map.getMaxEdge(); j++){
				grid[i][j].setH(Math.abs(goalX - j) + Math.abs(goalY - i));
			}
		}
	}
	
	/**
	 * Reset the heuristic values in the nodes to keep the map clean.
	 */
	public void resetHeuristics(){
		Node[][] grid = map.getMap();
		int i = 0;
		int j = 0;
		
		for(i = 0; i < map.getMaxEdge(); i++){
			for(j = 0; j < map.getMaxEdge(); j++){
				grid[i][j].setH(0);
			}
		}
	}
	
	public ArrayList<Node> findPath(){
		
		Node currNode;
		ArrayList<Node> adjacentNodes;
		ArrayList<Node> returnedPath = new ArrayList<Node>();
		Path newPath;
		
		//Flag if we have found the objective node
		boolean found = false;
		
		//The frontier of paths to visit
		PriorityQueue<Path> pathsToVisit = new PriorityQueue<Path>();
		
		//TODO Flood the map with heuristic values
		populateHeuristics();
		
		//Create a new path and add it to frontier
		Path currPath = new Path(map.getAgentNode(), map.getDirection(), inventory);
		pathsToVisit.add(currPath);
		
		//While there are still nodes to visit
		while(!pathsToVisit.isEmpty() && found == false){
			currPath = pathsToVisit.poll();	//Pop off frontier
			currNode = currPath.getCurrentNode();
			
			//Check if we have found a path to the goal
			if(currNode.equals(objective)){
				found = true;
				returnedPath = currPath.getCurrentPath();
			} 
				
			//If processing a node that is not the objective goal
			if(!found){
				//For each of the nodes
				adjacentNodes = map.getAdjacentNodes(currNode);
				
				for(Node n : adjacentNodes){
					if(!currPath.getCurrentPath().contains(n)){			//If it is a node that isn't in the path
						
						
						Inventory pathInventory = currPath.getInventory();
						
						if(n.getItem() == '^' || n.getItem() == '<' || n.getItem() == '>' || n.getItem() == 'v'){
							newPath = currPath.clone();
							newPath.addToPath(n);
							
							//Add to the frontier
							pathsToVisit.add(newPath);
						} else if(n.getItem() == ' '){							//If we encounter a blank tile		
							//If we're on a boat
							if(pathInventory.isOnBoat()){
								//never thought you'd be here did you.
								pathInventory.toggleBoat();
							}
							
							newPath = currPath.clone();
							newPath.addToPath(n);
							
							//Add to the frontier
							pathsToVisit.add(newPath);
							
						} else if(n.isObstacle()){						//Process what happens if we encountered an obstacle		
							
							if(n.getItem() == 'T'){						//If we encounter a tree and we have an axe, add to frontier
								
								if(pathInventory.containsAxe()){
									newPath = currPath.clone();			
									newPath.addToPath(n);
									
									//Add to the frontier
									pathsToVisit.add(newPath);
								}
								
							} else if(n.getItem() == '*'){				//If we encounter a wall and we have dynamite, add to frontier
								
								if(pathInventory.containsDynamite()){	
									//Use dynamite to expand and then clone
									pathInventory.useDynamite();
									newPath = currPath.clone();			
									newPath.addToPath(n);
									
									//Add to the frontier
									pathsToVisit.add(newPath);
								}
								
							} else {									//If we encounter water and we're on a boat, add to frontier
								if(pathInventory.isOnBoat()){
									newPath = currPath.clone();			
									newPath.addToPath(n);
									
									//Add to the frontier
									pathsToVisit.add(newPath);
								}
							}
						} else if(n.isHeuristic()){						//If we encounter a heuristic
							if(n.getItem() == 'a'){						//If we found axe
								newPath = currPath.clone();
								pathInventory.obtainedAxe();
							} else if(n.getItem() == 'd'){				//If we found dynamite
								newPath = currPath.clone();
								pathInventory.obtainedDynamite();		
							} else if(n.getItem() == 'B'){				//If we found Boat
								newPath = currPath.clone();
								pathInventory.toggleBoat();
							} else {									//If we found Gold (Non decisive. Expands as basic node)
								newPath = currPath.clone();
							}
							
							newPath.addToPath(n);
							pathsToVisit.add(newPath);				
						} 
					}
				}	
			}	
		}
					
		//Clean the map and return the path
		resetHeuristics();
		return returnedPath;
	}	
	
	public ArrayList<Node> findReversePath(){
		Node tempNode = objective;
		objective = map.getAgentNode();
		map.setPosX(tempNode.getX());
		map.setPosY(tempNode.getY());
		
		ArrayList<Node> returnedList = findPath();
		
		tempNode = objective;
		objective = map.getAgentNode();
		map.setPosX(tempNode.getX());
		map.setPosY(tempNode.getY());
		
		return returnedList;
	}
	
//	/**
//	 * Lol! Sorry for the coded00p! In a rush~ :D
//	 * 
//	 * ~ Bruce
//	 * @return
//	 */
//	public ArrayList<Node> findReversePath(){
//		
//		Node currNode;
//		ArrayList<Node> adjacentNodes;
//		ArrayList<Node> returnedPath = new ArrayList<Node>();
//		Path newPath;
//		
//		//Flag if we have found the objective node
//		boolean found = false;
//		
//		//The frontier of paths to visit
//		PriorityQueue<Path> pathsToVisit = new PriorityQueue<Path>();
//		
//		//TODO Flood the map with heuristic values
//		populateHeuristics();
//		
//		//Create a new path and add it to frontier
//		Path currPath = new Path(objective, map.getDirection(), inventory);
//		pathsToVisit.add(currPath);
//		
//		//While there are still nodes to visit
//		while(!pathsToVisit.isEmpty() && found == false){
//			currPath = pathsToVisit.poll();	//Pop off frontier
//			currNode = currPath.getCurrentNode();
//			
//			//Check if we have found a path to the goal
//			if(currNode.equals(map.getAgentNode())){
//				found = true;
//				returnedPath = currPath.getCurrentPath();
//			} 
//				
//			//If processing a node that is not the objective goal
//			if(!found){
//				//For each of the nodes
//				adjacentNodes = map.getAdjacentNodes(currNode);
//				
//				for(Node n : adjacentNodes){
//					if(!currPath.getCurrentPath().contains(n)){			//If it is a node that isn't in the path
//						
//						
//						Inventory pathInventory = currPath.getInventory();
//						
//						
//						if(n.getItem() == ' '){							//If we encounter a blank tile		
//							//If we're on a boat
//							if(currPath.isInBoat()){
//								//never thought you'd be here did you.
//								currPath.toggleBoat();
//							}
//							
//							newPath = currPath.clone();
//							newPath.addToPath(n);
//							
//							//Add to the frontier
//							pathsToVisit.add(newPath);
//							
//						} else if(n.isObstacle()){						//Process what happens if we encountered an obstacle		
//							
//							if(n.getItem() == 'T'){						//If we encounter a tree and we have an axe, add to frontier
//								
//								if(pathInventory.containsAxe()){
//									newPath = currPath.clone();			
//									newPath.addToPath(n);
//									
//									//Add to the frontier
//									pathsToVisit.add(newPath);
//								}
//								
//							} else if(n.getItem() == '*'){				//If we encounter a wall and we have dynamite, add to frontier
//								
//								if(pathInventory.containsDynamite()){	
//									//Use dynamite to expand and then clone
//									pathInventory.useDynamite();
//									newPath = currPath.clone();			
//									newPath.addToPath(n);
//									
//									//Add to the frontier
//									pathsToVisit.add(newPath);
//								}
//								
//							} else {									//If we encounter water and we're on a boat, add to frontier
//								if(currPath.isInBoat()){
//									newPath = currPath.clone();			
//									newPath.addToPath(n);
//									
//									//Add to the frontier
//									pathsToVisit.add(newPath);
//								}
//							}
//						} else if(n.isHeuristic()){						//If we encounter a heuristic
//							if(n.getItem() == 'a'){						//If we found axe
//								newPath = currPath.clone();
//								pathInventory.obtainedAxe();
//							} else if(n.getItem() == 'd'){				//If we found dynamite
//								newPath = currPath.clone();
//								pathInventory.obtainedDynamite();		
//							} else if(n.getItem() == 'B'){				//If we found Boat
//								newPath = currPath.clone();
//								newPath.toggleBoat();
//								pathInventory.toggleBoat();
//							} else {									//If we found Gold (Non decisive. Expands as basic node)
//								newPath = currPath.clone();
//							}
//							
//							newPath.addToPath(n);
//							pathsToVisit.add(newPath);				
//						} 
//					}
//				}	
//			}	
//		}
//					
//		//Clean the map and return the path
//		resetHeuristics();
//		return returnedPath;
//	}	

	
}
