import java.util.ArrayList;


public class DiscoverableNode extends Node implements Comparable<DiscoverableNode>{
	
	private int objectHeuristic;
	private ArrayList<Node> pathToNode;
	
	public DiscoverableNode(Node oldNode, ArrayList<Node> _pathToNode){
		super(oldNode.getX(), oldNode.getY(), oldNode.getItem(), 0);
		
		//Set priority
		if(oldNode.getItem() == 'g'){
			objectHeuristic = 2;
		} else if (oldNode.getItem() == 'a'){
			objectHeuristic = 1;
		} else if (oldNode.getItem() == 'B'){
			objectHeuristic = 0;
		} else {
			objectHeuristic = -1;
		}
		
		pathToNode = _pathToNode;
	}
	
	public DiscoverableNode(Node oldNode){
		super(oldNode.getX(), oldNode.getY(), oldNode.getItem(), 0);
		
		//Set priority
		if(oldNode.getItem() == 'g'){
			objectHeuristic = 2;
		} else if (oldNode.getItem() == 'a'){
			objectHeuristic = 1;
		} else if (oldNode.getItem() == 'B'){
			objectHeuristic = 0;
		} else {
			objectHeuristic = -1;
		}
	}
	
	public ArrayList<Node> getPathToNode(){
		return pathToNode;
	}
	
	public void setPathToNode(ArrayList<Node> path){
		pathToNode = path;
	}
	
	public int getObjectHeuristic(){
		return objectHeuristic;
	}

	@Override
	public int compareTo(DiscoverableNode other) {
		final int LESSER = -1;
		final int EQUAL = 0;
		final int GREATER = 1;
		
		if(objectHeuristic > other.getObjectHeuristic()){
			return GREATER;
		} if (objectHeuristic == other.getObjectHeuristic()){
			return EQUAL;
		} else {
			return LESSER;
		}

	}

}
