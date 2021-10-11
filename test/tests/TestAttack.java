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

public class TestAttack {

//	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		UnitFactory factory = new UnitFactory(gameState);
		
		/* ID=0 - Azure Herald (Human)
		 * ID=20 - Blaze Hound (AI) */
		PlayableUnit attacker = factory.makeUnit(0, null);
		PlayableUnit attackee = factory.makeUnit(20, null);
		
		int attack = attacker.getAttack();
		int startHealth = attackee.getHealth();
		attacker.attack(null, null, null, attackee, gameState);
		int endHealth = attackee.getHealth();
		
		assertTrue (endHealth == startHealth - attack);
		
		
	}
}
