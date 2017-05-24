import java.util.ArrayList;
import java.util.Iterator;


public class MoveGenerator {
	
	Compass compass;
	ArrayList<Node> path;
	String moves;
	
	MoveGenerator(ArrayList<Node> path_, Compass compass_){
		path = path_;
		compass = compass_.clone();
		moves = "";
		generateMoves();
	}
	
	public void addMove(String move){
		String newString = moves + move;
		moves = newString;
	}

	public void generateMoves() {		
		Iterator<Node> pathIterator = path.iterator();
		Node nextNode =  pathIterator.next(); // gets the first node (which is origin of search)
		BountyPoint nextPoint;
		while(pathIterator.hasNext()){
			nextNode = pathIterator.next(); // gets actual next node
			nextPoint = nextNode.getPoint();
			if(compass.isForward(nextPoint)){
				if(nextNode.getItem() == '*'){
					addMove("BF");
				} else if(nextNode.getItem() == 'T'){
					addMove("CF");
				} else {
					addMove("F");
				}
			} else if(compass.isRight(nextPoint)){
				if(nextNode.getItem() == '*'){
					addMove("RBF");
				} else if(nextNode.getItem() == 'T'){
					addMove("RCF");
				} else {
					addMove("RF");
				}
				compass.turnRight();
			} else if(compass.isLeft(nextPoint)){
				if(nextNode.getItem() == '*'){
					addMove("LBF");
				} else if(nextNode.getItem() == 'T'){
					addMove("LCF");
				} else {
					addMove("LF");
				}
				compass.turnLeft();
			} else {
				if(nextNode.getItem() == '*'){
					addMove("RRBF");
				} else if(nextNode.getItem() == 'T'){
					addMove("RRCF");
				} else {
					addMove("RRF");
				}
				compass.turnRight();
				compass.turnRight();
				
			}
			compass.setAgentPosition(compass.getForwardPosition());
		}		
	}
	
	public String getMoves(){
		return moves;
	}
	
}
