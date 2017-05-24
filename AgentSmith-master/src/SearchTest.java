import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class SearchTest {

//	@Test
//	public void testSearch() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testFindPath() {
		Map testScenario0 = new Map("src/s0.in");
		
		//Grabbing the dynamite from 2 cells away
		Search testSearch0 = new Search(testScenario0, new Inventory(), testScenario0.getMap()[6][10]);
		ArrayList<Node> pathToObjective = testSearch0.findPath();
		
		assertFalse(pathToObjective.isEmpty());
		pathToObjective.clear();
		
		//Grabbing the axe behind a wall with a stick of dynamite on s0
		Map testScenario1 = new Map("src/s0t1.in");
		
		Inventory scenarioInventory = new Inventory(false, false, 1);
		Search testSearch1 = new Search(testScenario1, scenarioInventory, testScenario1.getMap()[2][3]); 
		
		pathToObjective = testSearch1.findReversePath();
		assertFalse(pathToObjective.isEmpty());
		
		
		
		//Solving s1.in
		Map testScenario2 = new Map("src/s1.in");
		
		Search testSearch2 = new Search(testScenario2, new Inventory(), testScenario2.getMap()[3][16]);
		pathToObjective = testSearch2.findPath();
		
		assertFalse(pathToObjective.isEmpty());
		pathToObjective.clear();
		
		
		
	}

}
