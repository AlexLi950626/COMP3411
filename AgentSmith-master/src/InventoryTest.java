import static org.junit.Assert.*;

import org.junit.Test;


public class InventoryTest {

	@Test
	public void testInventory() {
		//Test correctly initialisation
		Inventory testInventory = new Inventory();
		assertFalse(testInventory.containsAxe());
		assertFalse(testInventory.containsGold());
		assertFalse(testInventory.containsDynamite());
		assertEquals(testInventory.getNumDynamite(), 0);
		
		//Check axe logic
		testInventory.obtainedAxe();
		assertTrue(testInventory.containsAxe());
		
		//Check gold logic
		testInventory.obtainedGold();
		assertTrue(testInventory.containsGold());
		
		//Check dynamite logic
		testInventory.obtainedDynamite();	//Add 1 stick
		assertTrue(testInventory.containsDynamite());
		assertEquals(testInventory.getNumDynamite(), 1);
		
		testInventory.obtainedDynamite();	//Add another
		assertTrue(testInventory.containsDynamite());
		assertEquals(testInventory.getNumDynamite(), 2);
		
		testInventory.useDynamite();		//Use one
		assertTrue(testInventory.containsDynamite());
		assertEquals(testInventory.getNumDynamite(), 1);
		
		testInventory.useDynamite();		//Use the last
		assertFalse(testInventory.containsDynamite());
		assertEquals(testInventory.getNumDynamite(), 0);
		
		//Test that clone is successful
		testInventory.obtainedDynamite();
		Inventory clonedInventory = testInventory.clone();
		
		//Checks that the references to each object are not the same
		assertFalse(clonedInventory.equals(testInventory));
		assertTrue(clonedInventory.containsAxe());
		assertTrue(clonedInventory.containsGold());
		assertTrue(clonedInventory.containsDynamite());
		assertEquals(clonedInventory.getNumDynamite(), 1);
	}

}
