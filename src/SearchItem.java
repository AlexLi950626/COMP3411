import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchItem{

    private ArrayList<Position> AStar(Board currBoard, Position dest){

    }


    /**
     * @param src
     * @param dest
     * @return calculate manhattan distance between tow position
     */
    private int ManhattanHeuristic(Position src, Position dest){
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }
}
