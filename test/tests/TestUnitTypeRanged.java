package tests;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import extensions.PlayableUnit;
import extensions.RangedUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestUnitTypeRanged {

//	@Ignore
	@Test
	public void test() {

		/* check that units with ID in {6,7,28,29} (FireSpitter & Pryomancer) are ranged units */
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>(4);
		
		/* ID=6,7 - FireSpitter */
		PlayableUnit test1 = factory.makeUnit(6, null);
		testCases.add(test1);
		PlayableUnit test2 = factory.makeUnit(7, null);
		testCases.add(test2);
		/* ID=28,29 - Pyromancer */
		PlayableUnit test3 = factory.makeUnit(28, null);
		testCases.add(test3);
		PlayableUnit test4 = factory.makeUnit(29, null);
		testCases.add(test4);
		
		for ( PlayableUnit u : testCases) assertTrue (u instanceof RangedUnit);
	}
}

