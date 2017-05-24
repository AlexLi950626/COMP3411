import static org.junit.Assert.*;

import org.junit.Test;


public class CompassTest {

	@Test
	public void testCompass() {
		
		//Test initialisation
		Compass testMap = new Compass();
		assertEquals(testMap.getAgentPosition().getX(), 0);
		assertEquals(testMap.getAgentPosition().getY(), 0);
		assertEquals(testMap.getAgentDirection(), 0);
		
		//Change position from default
		testMap.setAgentPosition(testMap.getForwardPosition()); //Set testMap to (0, 1)
		testMap.turnLeft();
		testMap.setAgentPosition(testMap.getForwardPosition());
		
		//Final resting position for testMap (-1, 1)
		assertEquals(testMap.getAgentPosition().getX(), -1);
		assertEquals(testMap.getAgentPosition().getY(), 1);
		
		//Test clone
		Compass clonedMap = testMap.clone();
		
		//Check that the reference for the two objects are not the same
		assertFalse(clonedMap.equals(testMap)); 
		
		assertEquals(clonedMap.getAgentPosition().getX(), -1);
		assertEquals(clonedMap.getAgentPosition().getY(), 1);
		
		
		
	}

	@Test
	public void testGetForwardPosition() {
		Compass testMap = new Compass();
		BountyPoint currPoint = testMap.getAgentPosition();

		assertEquals(currPoint.getX(), 0);
		assertEquals(currPoint.getY(), 0);
		assertEquals(testMap.getAgentDirection(), 0);
		
		//Test going north works
		currPoint = testMap.getForwardPosition();
		
		assertEquals(currPoint.getX(), 0);
		assertEquals(currPoint.getY(), 1);
		
		//Test going east works
		testMap.setAgentPosition(currPoint);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 1);
		currPoint = testMap.getForwardPosition();
		assertEquals(currPoint.getX(), 1);
		assertEquals(currPoint.getY(), 1);
		
		//Test going south works
		testMap.setAgentPosition(currPoint);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 2);
		currPoint = testMap.getForwardPosition();
		assertEquals(currPoint.getX(), 1);
		assertEquals(currPoint.getY(), 0);
		
		//Test going west works
		testMap.setAgentPosition(currPoint);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 3);
		currPoint = testMap.getForwardPosition();
		assertEquals(currPoint.getX(), 0);
		assertEquals(currPoint.getY(), 0);
		
		testMap.setAgentPosition(currPoint);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 0);
		testMap.turnLeft();
		assertEquals(testMap.getAgentDirection(), 3);
		
		
		//Test for consistency within negative coordinates
		currPoint = testMap.getForwardPosition();
		assertEquals(currPoint.getX(), -1);
		assertEquals(currPoint.getY(), 0);
		testMap.setAgentPosition(currPoint);
		
		testMap.turnLeft();
		currPoint = testMap.getForwardPosition();
		assertEquals(currPoint.getX(), -1);
		assertEquals(currPoint.getY(), -1);
		
		//Final Check to see if Map updated
		testMap.setAgentPosition(currPoint);
		assertEquals(testMap.getAgentPosition().getX(), -1);
		assertEquals(testMap.getAgentPosition().getY(), -1);
		
				
		
	}

	@Test
	public void testTurnLeft() {
		Compass testMap = new Compass();
		
		assertEquals(testMap.getAgentDirection(), 0);
		testMap.turnLeft();
		assertEquals(testMap.getAgentDirection(), 3);
		testMap.turnLeft();
		assertEquals(testMap.getAgentDirection(), 2);
	}

	@Test
	public void testTurnRight() {
		Compass testMap = new Compass();
		
		assertEquals(testMap.getAgentDirection(), 0);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 1);
		testMap.turnRight();
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 3);
		testMap.turnRight();
		assertEquals(testMap.getAgentDirection(), 0);
	}

}
