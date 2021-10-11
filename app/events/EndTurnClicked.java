package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import extensions.AIPlayer;
import extensions.HumanPlayer;
import extensions.PlayableUnit;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = "endTurnClicked"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//temporary code
		//Cast Player to human Player and then draw a card
		//No turn switching
		
		HumanPlayer humanPlayer = gameState.getHumanPlayer();
		AIPlayer aiPlayer = gameState.getAiPlayer();
		changeTurn(out, gameState, humanPlayer, aiPlayer);
	}

	//Swap turns between Human Player and AI Player
	private static void changeTurn(ActorRef out, GameState gameState, HumanPlayer humanPlayer, AIPlayer aiPlayer) {
		gameState.resetGameStateCardVariables(out); //No cards should be saved
		gameState.resetGameStateUnitVariables(out); //No units or tiles should be saved
		
		if (gameState.getCurrentPlayer() == humanPlayer) {
			setAITurn(out, gameState, humanPlayer, aiPlayer);
		}else {
			setHumanTurn(out, gameState, humanPlayer, aiPlayer);
		}
	}
	
	//Set the turn for Human Player
	private static void setHumanTurn(ActorRef out, GameState gameState, HumanPlayer humanPlayer, AIPlayer aiPlayer) {
		aiPlayer.setCurrentMana(out, gameState, 0); //Set AI Player mana to zero
		aiPlayer.drawCard(out); //AI Draws a Card as their turn ends
		gameState.setCurrentPlayer(humanPlayer); //Current Player is Human
		
		gameState.setTurn(gameState.getTurn() + 1); //Increment the turn counter as both players have had a turn
		
		//Available Mana is current turn + 1, capped at 9
		if(gameState.getTurn() <= 8) 
			humanPlayer.setCurrentMana(out, gameState, gameState.getTurn() + 1);
		else {
			humanPlayer.setCurrentMana(out, gameState, 9);
		}
		
		resetUnitMoveAttack(gameState, humanPlayer);
		
		BasicCommands.addPlayer1Notification(out, "Human Turn", 1);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//Set the turn for the AI Player
	private static void setAITurn(ActorRef out, GameState gameState, HumanPlayer humanPlayer, AIPlayer aiPlayer) {
		humanPlayer.setCurrentMana(out, gameState, 0); //Set mana to zero
		humanPlayer.drawCard(out); //Human Draws a Card as their turn ends
		gameState.setCurrentPlayer(aiPlayer); //Current Player is AI

		//Available Mana is current turn + 1, capped at 9
		if(gameState.getTurn() <= 8) 
			aiPlayer.setCurrentMana(out, gameState, gameState.getTurn() + 1);
		else {
			aiPlayer.setCurrentMana(out, gameState, 9);
		}
		
		resetUnitMoveAttack(gameState, humanPlayer);
		
		BasicCommands.addPlayer1Notification(out, "Enemy Turn", 1);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		System.out.println("Currently AI turn");
		aiPlayer.takeTurn(out, gameState); //Start AI turn
		}
	
	//Allow current player's units to attack and move again
	private static void resetUnitMoveAttack(GameState gameState, HumanPlayer humanPlayer) {
		ArrayList<PlayableUnit> resetMoveAttack = null;
		
		if (gameState.getCurrentPlayer() == humanPlayer) {
			resetMoveAttack = gameState.getBoard().getAllFriendlyUnits();
			System.out.println("Reset Friendly Units to allow them to attack and move");
		}else {
			resetMoveAttack = gameState.getBoard().getAllEnemyUnits();
			System.out.println("Reset Enemy Units to allow them to attack and move");
		}
				
		for (PlayableUnit unit : resetMoveAttack) {
			unit.setCanAttack(true);
			unit.setCanMove(true);
		}
	}
}
