package extensions;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;

public abstract class GamePlayer extends Player {
	
	private String name;
	private Avatar avatar;
	
	// To Store a hand
	ArrayList<Card> hand = new ArrayList<Card>(6);

	public GamePlayer(int health, int mana, String name) {
		super(health, mana);
		this.name = name;
	}
	
	/*ABSTRACT METHODS*/
		
    //Set Player Health to a value
	public abstract void setPlayerHealth(ActorRef out, int newHealth);

	//Draw a card
	public abstract void drawCard(ActorRef out);
	
	//Set Mana to a value
	public abstract void setCurrentMana(ActorRef out, GameState gameState, int mana);
	
	//Create an Avatar on a tile
    public abstract void placeAvatar(ActorRef out, GameState gameState);

    
    
    /*IMPLEMENTED METHODS*/
    
	// Return a card that is at a given position in hand
	public Card getCard(int position) {
		return hand.get(position);
	}
	
	// Return a reference to the player's hand
	public ArrayList<Card> getHand(){
		return hand;
	}
	
	// Remove a card from hand
	public void removeCard(ActorRef out, int position) { 
		hand.remove(position);
	}
	
	//Reduce Mana by a given integer
	public void reduceMana(ActorRef out, GameState gameState,int usedMana) {
		setCurrentMana(out, gameState, getMana() - usedMana);
	}
	
	//Return the Players Name
	public String getName() {
		return name;
	}
	
	@JsonIgnore //needed to avoid infinite recursion issues
	public Avatar getAvatar() {
		return avatar;
	}
	
	@JsonIgnore //needed to avoid infinite recursion issues
	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}
}