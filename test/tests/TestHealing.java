package tests;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;


/*
 * TO RUN: .\sbt "testOnly *UnitActivityTestSuite"
 */
public class TestHealing {

//	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		
		PlayableUnit unit = factory.makeUnit(0, null);
		int maxHealth = unit.getHealth();
		
//		test1: make health 0, add 1 with healing
		unit.setHealth(0);
		unit.takeHealing(1, null);
		int health1 = unit.getHealth();
		
//		test2: make health 0, add 20 with healing (should not go over max!)
		unit.setHealth(0);
		unit.takeHealing(20, null);
		int health2 = unit.getHealth();
		
		assertTrue (health1 == 1);
		assertTrue (health2 == maxHealth);
	}
}
