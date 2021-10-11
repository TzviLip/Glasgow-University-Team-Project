package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import extensions.AIPlayer;
import extensions.HumanPlayer;
import extensions.PlayableUnit;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a card. The event returns the position in the player's hand the card
 * resides within.
 * 
 * { messageType = "cardClicked" position = <hand index position [1-6]> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int handPosition = message.get("position").asInt();
		gameState.getBoard().deHighlightTiles(out);

		selectCard(out, gameState, gameState.getHumanPlayer(), gameState.getAiPlayer(), handPosition);
	}

	//Select a card to play based on hand position and conditions
	public static void selectCard(ActorRef out, GameState gameState, HumanPlayer humanPlayer, AIPlayer aiPlayer, int handPosition) {
		// check the card that was clicked and compare to player's mana
		// if the player can play it, highlight it, if not - we can't play it

		// No Board units should be selected
		gameState.resetGameStateUnitVariables(out);
		
		/*CurrentPlayer Select Card*/
		Card card;

		//Get the card from the correct players hand
		if (gameState.getCurrentPlayer() == humanPlayer) {
			// redraw hand if another card clicked to dehighlight others
			humanPlayer.redrawHand(out);
			card = humanPlayer.getCard(handPosition);
		}else {
			card = aiPlayer.getCard(handPosition);
		}
		
		// get the card's ID
		int cardID = card.getId();
		
		//If the current Player has enough mana
		if (card.getManacost() <= gameState.getCurrentPlayer().getMana()) {
			// Set the last clicked card to this card to save it
			gameState.setLastClicked(card);
			gameState.setLastClickedPosition(handPosition);

			//Highlight the card in hand if its the human players turn
			if (gameState.getCurrentPlayer() instanceof HumanPlayer)
				BasicCommands.drawCard(out, card, handPosition, 1);

			if (hasAirdrop(cardID)) { // Airdrop Ironcliff Guardian and Planar Scout
				System.out.println("Highlight Airdrop Tiles");
				gameState.getBoard().highlightAllEmptyTilesWhite(out);
				gameState.getBoard().saveAllWhiteHighlightedTiles(gameState.getWhiteHighlightedTiles());
			}else {
				if (PlayableUnit.isFriendlyUnit(cardID)) {// Friendly Unit Summon Tiles Highlighting
					gameState.getBoard().checkTileSummon(out, true);
				}else if (PlayableUnit.isEnemyUnit(cardID)) {// Enemy Unit Summon Tiles Highlighting
					gameState.getBoard().checkTileSummon(out, false);
					gameState.getBoard().saveAllWhiteHighlightedTiles(gameState.getWhiteHighlightedTiles());
				}
			}
			

			// If card has an ID in spellCardIDs array, it is a spell card
			// so call highlightSpellTargets method in Board class
			if (gameState.getSpellCardIDs().contains(card.getId())) {
				System.out.println("Highlight Spell Targets");
				gameState.getBoard().highlightSpellTargets(out, card);
				gameState.getBoard().saveAllRedHighlightedTiles(gameState.getRedHighlightedTiles());
			}
			
			/* testing */
			System.out.println("Card Clicked");
			System.out.println("Card name: " + card.getCardname());
			System.out.println("Card ID: " + card.getId());
			System.out.println("Card Mana Cost: " + card.getManacost());
		}
	}
	
	//Return true if a unit has the Airdrop ability
	public static boolean hasAirdrop(int cardID) {
		if (cardID == 10 || cardID == 11) { //ID of Ironcliff Guardian
			System.out.println("Ironcliff Guardian has Airdrop");
			return true;
		}
		if (cardID == 26 || cardID == 27) { //ID of Planar Scout
			System.out.println("Planar Scout has Airdrop");
			return true;
		}
		return false;
	}

}
