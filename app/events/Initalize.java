package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import demo.CommandDemo;
import extensions.AIPlayer;
import extensions.Board;
import extensions.HumanPlayer;
import structures.GameState;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = "initalize"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		Board board = new Board(out, gameState); //Create a new board
		gameState.setBoard(board);
		
		//Create Players and store in GameState
		HumanPlayer humanPlayer = new HumanPlayer(out, gameState);
		AIPlayer aiPlayer = new AIPlayer(out, gameState);
		gameState.setCurrentPlayer(humanPlayer); //Human Player always starts
		gameState.setHumanPlayer(humanPlayer);
		gameState.setAiPlayer(aiPlayer);
	}

}
