package extensions;

import java.util.ArrayList;
import java.util.Collections;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/*The HumanPlayer class contains the Human Player’s available mana and current health. 
 * It has methods containing the logic of how to play and draw cards such as: 
 * - play a Card; 
 * - update mana (i.e. decreased when a spell is used or increased once a new turn started); 
 * - draw a card etc. 
 * It contains a reference to the ArrayList of type Card the player has in their deck and also a 
 * ArrayList of type Card in their hand.
 */

public class HumanPlayer extends GamePlayer {
	
	ArrayList<String> deck = new ArrayList<String>() {
		{
			add(StaticConfFiles.c_azure_herald); // ID = 0,1
			add(StaticConfFiles.c_azurite_lion); // ID = 2,3
			add(StaticConfFiles.c_comodo_charger); // ID = 4,5
			add(StaticConfFiles.c_fire_spitter); // ID = 6,7
			add(StaticConfFiles.c_hailstone_golem); // ID = 8,9
			add(StaticConfFiles.c_ironcliff_guardian); // ID = 10,11
			add(StaticConfFiles.c_pureblade_enforcer); // ID = 12,13
			add(StaticConfFiles.c_silverguard_knight); // ID = 14,15
			add(StaticConfFiles.c_sundrop_elixir); // ID = 16,17
			add(StaticConfFiles.c_truestrike); // ID = 18,19
		}
	};
	
	// To increment unique id - Human units start at ID 0
	int uniqueID = 0;
	
	// 2 of each unique card make up a deck
	ArrayList<Card> cardDeck = new ArrayList<Card>() {
		{
			for(String s : deck) {
				Card toAdd1 = BasicObjectBuilders.loadCard(s, uniqueID, Card.class);
				add(toAdd1);
				uniqueID++;
				Card toAdd2 = BasicObjectBuilders.loadCard(s, uniqueID, Card.class);
				add(toAdd2);
				uniqueID++;
			}
		}
	};
	
	public HumanPlayer(ActorRef out, GameState gameState) {
		super(20, 2, "Human Player"); //Starting Health = 20, Starting Mana = 2
        placeAvatar(out,gameState); //Instatiate the Human Avatar
		BasicCommands.setPlayer1Health(out, this); // set UI health to starting health
		BasicCommands.setPlayer1Mana(out, this); // set UI mana to starting mana
		Collections.shuffle(cardDeck); //Shuffle the deck
		drawCard(out); //Draw 3 cards
		drawCard(out);
		drawCard(out);

		/* testing */
		System.out.println("New HumanPlayer created");
	}
	
	//Set Human Player Health to a value
	public void setPlayerHealth(ActorRef out, int newHealth) {
		setHealth(newHealth);
		BasicCommands.setPlayer1Health(out, this); // set UI health to new health value
		
		if (getHealth() <= 0) {
			// Human Player health reached zero - Human loses game
			System.out.println("Human Loses");
			BasicCommands.addPlayer1Notification(out, "Human Loses", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		/* testing */
		System.out.println("HumanPlayer health updated: " + newHealth);
	}

	//Draw a card
	public void drawCard(ActorRef out) {
		if (cardDeck.size() > 0) { // Only if deck has cards left
			BasicCommands.addPlayer1Notification(out, "Cards Left: " + cardDeck.size(), 2);
			Card toDraw = cardDeck.remove(0); // remove the top card of the deck
			if (hand.size() < 6) { // if a hand has space for more cards
				hand.add(toDraw); // add the card to players hand
			}
		}else {
			// Human Player has no cards left - Human loses game
			System.out.println("Human Loses");
			BasicCommands.addPlayer1Notification(out, "Human Loses", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		redrawHand(out); //Fix the UI hand
		
		/* testing */
		System.out.println("HumanPlayer: card drawn");
		//Print number of cards left for testing
		System.out.println("Human Hand Size: " + hand.size());
		System.out.println("Human Deck Size: " + cardDeck.size());
	}

	// Redraw the hand to the UI
	public void redrawHand(ActorRef out) {
		for (int i = 0; i < hand.size(); i++) {
			BasicCommands.drawCard(out, hand.get(i), i, 0);
		}
	}
	
	// Remove a card from hand
	public void removeCard(ActorRef out, int position) { 
		for (int i = 0; i<hand.size();i++) {
			BasicCommands.deleteCard(out, i);
		}
		super.removeCard(out, position);
		redrawHand(out);
	}

	//Set Mana to a value
	public void setCurrentMana(ActorRef out, GameState gameState, int mana) {
		setMana(mana);
		BasicCommands.setPlayer1Mana(out, this);  // set UI mana to new mana value
		
		/* testing */
		System.out.println("HumanPlayer mana updated: " + mana);
	}
	
	//Create a Human Avatar on a tile
    public void placeAvatar(ActorRef out, GameState gameState){
        Tile tile = gameState.getBoard().clickedTile(1,2);  //Tile to instantiate on
        setAvatar(Avatar.playAvatar(out, gameState, tile, 100));  //Get an instance of Avatar
        getAvatar().setPlayer(this);
    	
    	/* testing */
		System.out.println("HumanPlayer: Avatar created on start tile");
    }

}
