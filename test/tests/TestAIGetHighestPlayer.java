package tests;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;
//import org.junit.Ignore;
import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.AIPlayer;
import extensions.Board;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestAIGetHighestPlayer {

	/* test AIPlayer getHighestPlayer method 
	 * given an ArrayList of playable units, it should return the one with the highest health
	 */
	
//	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		Board board = new Board(null, gameState);
		gameState.setBoard(board);
		UnitFactory factory = new UnitFactory(gameState);
		ArrayList<PlayableUnit> testUnits = new ArrayList<>();
		AIPlayer aiPlayer = new AIPlayer(null,gameState);
		
		PlayableUnit test1, test2, test3;
		
		/* (1) make azure herald - health 4 */
		test1 = factory.makeUnit(0, null);
		testUnits.add(test1);
		
		/* (2) make azurite lion - health 3 */
		test2 = factory.makeUnit(2, null);
		testUnits.add(test2);
		
		/* check that azure herald is returned as it has higher health */
		assertTrue (aiPlayer.getHighestPlayer(testUnits) == test1);
		
		/* (3) make hailston golem - health 6 */
		test3 = factory.makeUnit(8,null);
		testUnits.add(test3);
		
		/* check that hailstone golem is returned as it has highest health */
		assertTrue (aiPlayer.getHighestPlayer(testUnits) == test3);
		
		
	}
}