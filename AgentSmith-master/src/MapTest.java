import static org.junit.Assert.*;

import org.junit.Test;


public class MapTest {

	@Test
	public void testMapIntString() {
		Map testMap = new Map("src/s0.in");
		assertEquals(testMap.getAgentX(), 10);
		assertEquals(testMap.getAgentY(), 4);
		assertEquals(testMap.getAgentNode().getItem(), 'v');
		assertEquals(testMap.getDirection(), Compass.SOUTH);
		testMap.printTestMap();
	}

}
