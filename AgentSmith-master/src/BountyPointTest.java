import static org.junit.Assert.*;

import org.junit.Test;


public class BountyPointTest {

	@Test
	public void testBountyPoint() {
		BountyPoint testPoint = new BountyPoint(0, 0);
		assertEquals(testPoint.getX(), 0);
		assertEquals(testPoint.getY(), 0);
		
		testPoint.setX(10);
		assertEquals(testPoint.getX(), 10);
		
		testPoint.setX(-99);
		assertEquals(testPoint.getX(), -99);
		
		testPoint.setY(10);
		assertEquals(testPoint.getY(), 10);
		
		testPoint.setY(-99);
		assertEquals(testPoint.getY(), -99);
		
		testPoint.setPoint(42, -42);
		assertEquals(testPoint.getX(), 42);
		assertEquals(testPoint.getY(), -42);
	}

}
