package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import extensions.AIPlayer;
import extensions.Board;
import extensions.GamePlayer;
import extensions.HumanPlayer;
import extensions.PlayableUnit;
import extensions.SummonUnit;
import extensions.RangedUnit;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = "tileClicked" tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		evaluateGamestateToChooseTile(out, gameState , tilex, tiley);
	}
	
	//Evaluate the GameState variables to determine what action should take place
	private static void evaluateGamestateToChooseTile(ActorRef out, GameState gameState , int tilex, int tiley) {
		Board board = gameState.getBoard(); //Reference to Board
		Tile tile = board.clickedTile(tilex, tiley); //Reference to the Tile that was clicked
		Card lastClickedCard = gameState.getLastClicked(); //Reference to the Card that was last clicked
		PlayableUnit lastClickedUnit = (PlayableUnit) tile.getUnit(); //Reference to the PlayableUnit on the clicked tile or null if no unit
		
		System.out.println("Tile Mode: " + tile.getMode());
		
		//If we click on a tile which has a unit but have no hand card selected, save this unit as a state to lastClickedFriendlyUnit or lastClickedEnemyUnit
		if (lastClickedUnit != null && lastClickedCard == null) {
			saveUnit(gameState, lastClickedUnit, tile);
		}
		
		//If we have a friendly unit saved, we clicked a tile with a unit, and we haven't clicked a card in hand then highlight possible tiles to move to.
		if(gameState.getLastClickedUnit() != null && tile.getUnit() != null && lastClickedCard == null) {
			unitHighlight(out, gameState, lastClickedUnit);
		}
		
		//If we have a friendly unit saved, no card selected and we click on a tile that is highlighted (mode == 1), then unit should move
		if(gameState.getLastClickedUnit() != null && tile.getMode() == 1) {
			moveLastSelectedUnit(out, gameState, tile, gameState.getLastClickedUnit());
		}
		
		//If we have a friendly unit saved, no card selected and we click on a tile that is red (mode == 2), then unit should attack
		if(gameState.getLastClickedUnit() != null && tile.getMode() == 2) {
			PlayableUnit attackingUnit = gameState.getLastClickedUnit(); //Get the attacking Unit
			PlayableUnit targetUnit = (PlayableUnit)tile.getUnit(); //Get the target of the attack
			
			attackWithLastSelectedUnit(out, gameState, board, tile, attackingUnit, targetUnit); //attack
		}
		
		//If we have a unit selected and we select a tile that is not highlighted and the tile is empty or contains a unit that cannot be selected
		boolean deselect = gameState.getLastClickedUnit() != null
							&& tile.getMode() == 0 
							&& (lastClickedUnit == null 
							|| lastClickedUnit.isFriendly() == gameState.getCurrentPlayer() instanceof AIPlayer);
		
		if (deselect) { 
			gameState.resetGameStateUnitVariables(out);
		}
		

		//If we have a Card selected and press a valid tile then we should summon the unit or play the spell
		//If we have a Card selected but press an invalid tile, then reset card highlighting and state
		if (lastClickedCard != null && tile.getMode() != 0) {
			//play card
			int lastClickedPosition = gameState.getLastClickedPosition();
			playCard(out, gameState, tile, lastClickedPosition, lastClickedCard);
		}else if (lastClickedCard != null && tile.getMode() == 0) {
			//Invalid Square to play selected card
			System.out.println("Failed to play " + lastClickedCard.getCardname() + " from Hand");
			gameState.resetGameStateCardVariables(out);
		}
	}
	
	//Remove the currently selected card from hand
	private static void removeFromHand(ActorRef out, GameState gameState, int position, Card card) {
		System.out.println("Playing " + card.getCardname() + " from Hand");
		
		GamePlayer currentPlayer = (GamePlayer) gameState.getCurrentPlayer();

		//Remove the card from the players hand and reduce mana
		currentPlayer.removeCard(out, position);
		currentPlayer.reduceMana(out, gameState, card.getManacost());

	}
	
	//Play the correct card depending on its ID
	private static void playCard(ActorRef out, GameState gameState, Tile tile, int position, Card card) {
		int id = card.getId();
		
		removeFromHand(out, gameState, position, card);
		
		//Play the appropriate card according to ID with any effects or abilities
		switch (id) {
		
		case 0: case 1:
			//Azure Herald Unit - Has a summon ability
			if (tile.getMode() == 1) {
				PlayableUnit humanUnit = PlayableUnit.playUnit(out, gameState, tile, id);
				SummonUnit.azureSummon(out,gameState);
				gameState.getHumanFriendlyUnits().add(humanUnit);
			}
			break;
			
		case 2: case 3: case 4: case 5: case 6: case 7: case 8:
		case 9: case 10: case 11: case 12: case 13: case 14: case 15:
			//All other Player Units
			if (tile.getMode() == 1) {
				PlayableUnit humanUnit = PlayableUnit.playUnit(out, gameState, tile, id);
				gameState.getHumanFriendlyUnits().add(humanUnit);
			}
			break;
			
		case 16:	
		case 17:
			// Sundrop Elixir - increase health by 5 up to a maximum
			if (tile.getMode() == 2) {
				playSpell(out, gameState, tile, StaticConfFiles.f1_buff, "Elixir");
			}
			break;

		case 18:
		case 19:
			// Truestrike - deal 2 damage to a unit
			if (tile.getMode() == 2) {
				playSpell(out, gameState, tile, StaticConfFiles.f1_inmolation, "Truestrike");
			}
			break;
			
		case 20: case 21: 
			//Blaze Hound Unit - Has a summon ability
			if (tile.getMode() == 1) {
				PlayableUnit aiUnit = PlayableUnit.playUnit(out, gameState, tile, id);
				SummonUnit.blazeSummon(out,gameState);
				gameState.getAiFriendlyUnits().add(aiUnit);
			}
			break;
			
		case 22: case 23: case 24: case 25: case 26: case 27: case 28: 
		case 29: case 30: case 31: case 32: case 33: case 34: case 35: 
			//All other AI Units
			if (tile.getMode() == 1) {
				PlayableUnit aiUnit = PlayableUnit.playUnit(out, gameState, tile, id);
				gameState.getAiFriendlyUnits().add(aiUnit);
			}
			break;
		
		case 36:
		case 37: 
			// Entropic Decay (AI spell) - reduce a non-avatar unit to 0 health
			if (tile.getMode() == 2) {
				playSpell(out, gameState, tile, StaticConfFiles.f1_martyrdom, "Decay");
			}
			break;
		case 38:
		case 39: 
			// Staff of Y'Kir (AI spell) - increase attack by 2
			if (tile.getMode() == 2) {
				playSpell(out, gameState, tile, StaticConfFiles.f1_buff, "Staff Of Y'Kir");
			}
			break;
		}

		//After successfully playing a Card
		System.out.println("Played " + card.getCardname() + " from Hand");
		gameState.resetGameStateCardVariables(out);
	}
	
	//Attack an opposing unit with an allied unit
	public static void attackWithLastSelectedUnit(ActorRef out, GameState gameState, Board board, Tile tile, PlayableUnit attackingUnit, PlayableUnit targetUnit) {
		ArrayList<Tile> movementTiles = board.getAdjacentTiles(tile); //Get the tiles adjacent to the target Unit
		
		//If the unit does not have provoke, remove tiles that cannot be moved to
		if (!targetUnit.getProvoke()) {
			movementTiles = board.removeProvokeAdjacentTiles(movementTiles, attackingUnit);
		}
		
		//Get the board position of the attacking unit
		int x = attackingUnit.getPosition().getTilex();
		int y = attackingUnit.getPosition().getTiley();
		Tile attackingTile = board.clickedTile(x,y);

		//If attacking unit's Tile is adjacent to target unit's Tile OR attacking unit has ranged ability
		if (movementTiles.contains(attackingTile)||attackingUnit instanceof RangedUnit){ 
			
			//attack & counter-attack, if possible							
			attackingUnit.battle(out, attackingTile, tile, targetUnit, board, gameState);
				
		}else { //Unit can be attacked but isn't adjacent
			//Move and Attack
			//Find an adjacent Tile which is highlighted (mode == 1)
			//Move to this tile then battle
			double pythagDistance = 50;
			Tile toMoveTo = null;
			for (Tile adj : movementTiles) {
				if (adj.getMode() == 1) {
					int targetX = targetUnit.getCurrentTile().getTilex();
					int targetY = targetUnit.getCurrentTile().getTiley();
					int adjX = adj.getTilex();
					int adjY = adj.getTiley();
					
					double temp = Math.sqrt(Math.pow((targetX - adjX), 2) + Math.pow((targetY - adjY), 2));
					if (temp < pythagDistance) {
						pythagDistance = temp;
						toMoveTo = adj;
					}
				}
			}

			System.out.println("Shortest pythag distance is " + pythagDistance + " for tile " + toMoveTo);
			Boolean yfirst = gameState.getBoard().yFirst(out, attackingTile);
			attackingUnit.move(out, gameState, attackingTile, toMoveTo, yfirst); //move to the first possible tile in the ArrayList
			attackingUnit.battle(out, toMoveTo, tile, targetUnit, board, gameState); //attack as normal as Unit is now adjacent
		}
		
		System.out.println("Unit Attacking " + gameState.getLastClickedUnit().getName());
		
		//Attacking finished, reset gameState variables
		gameState.resetGameStateUnitVariables(out);
	}
	
	//Save a clicked unit to game state
	private static void saveUnit(GameState gameState, PlayableUnit unit, Tile tile) {
		if (correctFriendlyPerTurn(gameState, unit)) {
			gameState.saveUnit(unit, tile);
		}
	}
	
	//Highlight the tiles a unit can move to or attack
	private static void unitHighlight(ActorRef out, GameState gameState, PlayableUnit unit) {
		if (correctFriendlyPerTurn(gameState, unit)) { //Only highlight tiles if the unit is friendly and can move
			System.out.println("Dehighlighting");
			gameState.getBoard().deHighlightTiles(out); //Stop all tile highlighting to undo previous selected units
			System.out.println("Highlighting Tiles for " + gameState.getLastClickedUnit().getName());
			gameState.getBoard().highlightTiles(out, unit); //Highlight for selected unit
			gameState.getBoard().saveAllWhiteHighlightedTiles(gameState.getWhiteHighlightedTiles());
			gameState.getBoard().saveAllRedHighlightedTiles(gameState.getRedHighlightedTiles());

		}                          
	}
	
	//Return true if a unit is friendly on the Player's turn, or not friendly on the Ai's turn
	private static boolean correctFriendlyPerTurn(GameState gameState, PlayableUnit lastClickedUnit) {
		//If Human Players turn and Unit is friendly
		//or AI Players turn and Unit is not friendly
		boolean correct = gameState.getCurrentPlayer() instanceof HumanPlayer && lastClickedUnit.isFriendly()
					   || gameState.getCurrentPlayer() instanceof AIPlayer && !lastClickedUnit.isFriendly();
		
		return correct;
	}
	
	//Move a unit to a tile
	private static void moveLastSelectedUnit(ActorRef out, GameState gameState, Tile tile, PlayableUnit unit) {
		//The Tile to move to
		Tile oldTile = gameState.getLastClickedTile();

		//Horizontal or vertical first depending on enemies
		Boolean yfirst = gameState.getBoard().yFirst(out, oldTile);
		
		//Move the unit
		unit.move(out, gameState, oldTile, tile, yfirst);
					
		System.out.println(unit.getName() + " moving to " + tile.toString() + ", vertical first: " + yfirst);
		
		//Movement finished, reset gameState variables
		gameState.resetGameStateUnitVariables(out);
	}

	
	//Play the relevant spell depending on the name given
	private static void playSpell(ActorRef out, GameState gameState, Tile tile, String effect, String name) {
		//When clicking on a Tile which is in the correct highlight mode, get the unit on that tile
		//A PlayableUnit reference called selectedUnit is made, which references the unit on the clicked tile
		PlayableUnit selectedUnit = (PlayableUnit)tile.getUnit();
		System.out.println("Tile x: " + selectedUnit.getPosition().getTilex());
		System.out.println("Tile y: " + selectedUnit.getPosition().getTiley());
		
		//The effect animation is played 
		EffectAnimation ef = BasicObjectBuilders.loadEffect(effect);
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		if(name.equals("Elixir")) {
			//Heal the unit by 5 health (capped)
			System.out.println("Human played Sundrop Elixir spell on tile " + tile.toString());
			selectedUnit.takeHealing(5, out);
		}
		
		if(name.equals("Truestrike")) {
			//Deal 2 damage to the selected unit
			System.out.println("Human played Truestrike spell on tile "+ tile.toString());
			selectedUnit.takeDamage(2, out, gameState);
		}
		
		if(name.equals("Decay")) {
			//Deal damage to the unit equal to its health (i.e. set health to 0)
			System.out.println("AI played Entropic Decay spell on tile "+ tile.toString());
			selectedUnit.takeDamage(selectedUnit.getHealth(), out, gameState);
			// notify spellThiefUnit instances
			gameState.notifyObserver(out);
		}
		
		if(name.equals("Staff Of Y'Kir")) {
			//Add 2 attack to the Avatar
			selectedUnit.addAttack(2, out);		
			System.out.println("AI played Staff of Y'Kir spell on tile "+ tile.toString());
			// notify spellThiefUnit instances
			gameState.notifyObserver(out);
		}
	}
}

