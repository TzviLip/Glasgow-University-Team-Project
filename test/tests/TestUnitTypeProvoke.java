package tests;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestUnitTypeProvoke {

//	@Ignore
	@Test
	public void test() {

		/* check that units with ID in {10,11,14,15,30,21} 
		 * (Ironcliff Guardian, Silverguard Knight & Rock Pulveriser) 
		 * are provoke units */
		
		/* ignore silverguard knight - see trigger damage test unit class */
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>(6);
		
		/* ID=10,11 - Ironcliff Guardian */
		PlayableUnit test1 = factory.makeUnit(10, null);
		testCases.add(test1);
		PlayableUnit test2 = factory.makeUnit(11, null);
		testCases.add(test2);
//		/* ID=14,15 - Silverguard Knigt */
//		PlayableUnit test3 = factory.makeUnit(14, null);
//		testCases.add(test3);
//		PlayableUnit test4 = factory.makeUnit(15, null);
//		testCases.add(test4);
		/* ID=30,31 - Rock Pulveriser */
		PlayableUnit test5 = factory.makeUnit(30, null);
		testCases.add(test5);
		PlayableUnit test6 = factory.makeUnit(31, null);
		testCases.add(test6);
		
		for ( PlayableUnit u : testCases) assertTrue (u.getProvoke());
	}
}
