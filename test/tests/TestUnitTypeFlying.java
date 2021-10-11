package tests;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
//import org.junit.Ignore;
import org.junit.Test;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestUnitTypeFlying {

//	@Ignore
	@Test
	public void test() {

		/* check that units with ID in {34,35} (Windshrike) are provoke units */
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>(2);
		
		/* ID=34,35 - Windshrike */
		PlayableUnit test1 = factory.makeUnit(34, null);
		testCases.add(test1);
		PlayableUnit test2 = factory.makeUnit(35, null);
		testCases.add(test2);
	
		
		for ( PlayableUnit u : testCases) assertTrue (u.doesFly());
	}
}