package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
//import org.junit.Ignore;
import org.junit.Test;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.AIPlayer;
import extensions.Board;
import structures.GameState;
import structures.basic.Card;

public class TestAIDrawCard {

	/* check AI drawCard method 
	 * check overdrawn condition - if AI draws card when hand is full i.e. hand = 6
	 * hand should not change, remaining deck should reduce in size by 1
	 */
	
//	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		Board board = new Board(null, gameState);
		gameState.setBoard(board);
		
		AIPlayer aiPlayer = new AIPlayer(null, gameState);
		ArrayList<Card> hand = aiPlayer.getHand();
		
		/* draw 3 cards to get full hand (starts with 3) */
		for (int i=0; i<3; i++) aiPlayer.drawCard(null);

		/* copy elements in AI hand currently to an arraylist */
		ArrayList<Card> hand1 = new ArrayList<Card>();
		for (Card card : hand) hand1.add(card);
		
		/* check hand size = 6 i.e. 3 cards have been drawn */
		assertTrue (hand1.size() == 6);
		
		/* draw another card when hand is full */
		aiPlayer.drawCard(null);
		
		/* check hand (after overdrawing) = hand1 */
		assertTrue (hand.equals(hand1));
		
	}
}
