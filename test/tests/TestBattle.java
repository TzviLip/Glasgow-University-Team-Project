package tests;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;

/*NOTES changed attack to public for testing
 * 
 */

public class TestBattle {

//	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		
		
		/* chose units that won't die to avoid null pointers (tile is null -> unit dies -> destroyUnit tries tile.setUnit -> null pointer error)
		/* ID=0 - Azure Herald (Human) - attack 1 health 4
		 * ID=30 - Rock Pulveriser (AI) - attack 1 health 4*/
		PlayableUnit attacker = factory.makeUnit(0, null);
		PlayableUnit attackee = factory.makeUnit(30, null);
		
		int attack1 = attacker.getAttack();
		int startHealth1 = attacker.getHealth();
		
		int attack2 = attackee.getAttack();
		int startHealth2 = attackee.getHealth();
		
		attacker.battle(null, null, null, attackee, null, gameState);
		int endHealth1 = attacker.getHealth();
		int endHealth2 = attackee.getHealth();
		
		/* assert that both units lose health = attack of their opponent
		 * as evidence that attack and counter attack occurred */
		assertTrue (endHealth2 == startHealth2 - attack1);
		assertTrue (endHealth1 == startHealth1 - attack2);
		
		
	}
}