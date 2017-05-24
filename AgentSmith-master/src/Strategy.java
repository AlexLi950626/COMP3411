import java.util.ArrayList;

/*
 * Strategy interface
 */
public interface Strategy {
	public ArrayList<Node> decideMove(Map map, Inventory inventory, Compass compass);
}
