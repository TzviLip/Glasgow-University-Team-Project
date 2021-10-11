package tests;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import extensions.PlayableUnit;
import extensions.TrigDamageUnit;
import extensions.UnitFactory;
import structures.GameState;

/* before running, comment out:
 * 	trigDamageUnit.setSubjectAvatar(); 
 * in UnitFactory trigDamageUnitReg method
 * and comment out @Ignore */

public class TestUnitTypeTrigDamage {
	
	@Ignore
	@Test
	public void test() {
	
		
		/* check that units with ID in {14,15} are units with 'when avatar damaged' ability trigger*/
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>();
		
		/* ID=14,15 - Silverguard Knight*/
		PlayableUnit test1 = factory.makeUnit(14, null);
		testCases.add(test1);
		PlayableUnit test2 = factory.makeUnit(15, null);
		testCases.add(test2);

		for ( PlayableUnit u : testCases) assertTrue (u instanceof TrigDamageUnit);
	}

}
