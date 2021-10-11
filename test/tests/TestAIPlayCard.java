package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.AIPlayer;
import extensions.Board;
import extensions.HumanPlayer;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class TestAIPlayCard {
	
	/* This test requires refactoring of AIPlayer method playCardOnTile
	 * and under time constraints this was not possible
	 * 
	 * check AI play card logic for special cases
	 * 	unit ranged or fly - farthest tile
		unit provoke -  next to avatar
		spell - 36&37 highestPlayer, tested in its own class so not tested here		
	 */

	@Ignore
	@Test
	public void test() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		Board board = new Board(null, gameState);
		gameState.setBoard(board);
		UnitFactory factory = new UnitFactory(gameState);
		AIPlayer aiPlayer = new AIPlayer(null, gameState);
		HumanPlayer humanPlayer = new HumanPlayer(null, gameState);
		ArrayList<Tile> testTiles = new ArrayList<>();
		gameState.setHumanPlayer(humanPlayer);
		gameState.setAiPlayer(aiPlayer);
		gameState.setCurrentPlayer(aiPlayer);
		
		/* create ranged unit card Pyromancer - ID 28 */
		Card ranged = BasicObjectBuilders.loadCard(StaticConfFiles.c_pyromancer, 28, Card.class);
	
		aiPlayer.getHand().clear();
		aiPlayer.getHand().add(ranged);
		
		/* add all tiles on board to testTiles arraylist */
		Tile[][] allTiles = board.getBoard();
		for (int i=0; i<9; i++) {
			for (int j=0; j<5; j++) {
				Tile currenTile = allTiles[i][j];
				currenTile.setMode(1);
				testTiles.add(currenTile);
			}
		}
		
		/* make a human unit (Avatar) add to middle square of board (4,2) */
		Tile newTile = board.clickedTile(4, 2);
		PlayableUnit human = factory.makeUnit(100, newTile);
		
		/* make an arraylist of corner units */
		ArrayList<Tile> cornerTiles = new ArrayList<>();
		cornerTiles.add(board.clickedTile(0, 0));
		cornerTiles.add(board.clickedTile(8, 0));
		cornerTiles.add(board.clickedTile(0, 4));
		cornerTiles.add(board.clickedTile(8, 4));
		
		Tile chosenTile;
		gameState.addHumanUnit(human);
		gameState.setWhiteHighlightedTiles(testTiles);
		aiPlayer.makeAIReady(gameState);
		
		/* test 1 : ranged should go to one of the four corners of the board */
		gameState.setLastClicked(ranged);
		gameState.setLastClickedPosition(0);
		chosenTile = aiPlayer.playCardOnTile(null, gameState, testTiles);
		assertTrue (cornerTiles.contains(chosenTile));

		
	}
}
