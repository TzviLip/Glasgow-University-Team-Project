package extensions;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import java.util.ArrayList;
import java.util.LinkedList;

/*
 * An Avatar is a special type of PlayableUnit, in that it shares its health with the HumanPlayer. 
 * Avatar is a subclass of PlayableUnit and will therefore have access to common functionality such as movement and attacking. 
 * An Avatar is played once, at the start of the game, at which point it is placed onto the relevant Tile on the Board.
 */
public class Avatar extends PlayableUnit implements Subject {
	
	LinkedList<Observer> observers = new LinkedList<Observer>(); //Stores Units which require a trigger

	public Avatar() {
		super();
	}
	
	//Create an Avatar Unit, set its UI values and summon it
	public static Avatar playAvatar(ActorRef out, GameState gameState, Tile tile, int ID) {
		//Play Summon FX
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//Instantiate Avatar
		Avatar avatar = (Avatar) gameState.getUnitFactory().makeUnit(ID, tile);
		avatar.setPositionByTile(tile);
		tile.setUnit(avatar);

		//UI
		BasicCommands.playUnitAnimation(out, avatar, UnitAnimationType.idle);
		BasicCommands.drawUnit(out, avatar, tile);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
		BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());
		
		if(ID == 100)
			gameState.getHumanFriendlyUnits().add(avatar);
		else
			gameState.getAiFriendlyUnits().add(avatar);
		return avatar;
	}
	
	//Override Parent damage method to include matching health with player and notifying observers - Silverguard Knight
	public void takeDamage(int damageTaken, ActorRef out, GameState gameState) {
		super.takeDamage(damageTaken, out, gameState);
		
		GamePlayer gamePlayer = (GamePlayer) player;
		gamePlayer.setPlayerHealth(out, health);
		//Notify any observers that Avatar has taken damage
		notifyObserver(out);
	}
	
	//Override Parent healing method to include matching health with player
	public void takeHealing(int heal, ActorRef out) {
		super.takeHealing(heal, out);
		
		GamePlayer gamePlayer = (GamePlayer) player;
		gamePlayer.setPlayerHealth(out, health);
	}

	@Override
	//to register the Avatar as the subject for the appropriate observer
	public void registerObserver(Observer o) {
		observers.add(o);
		
		/* testing */
		System.out.println("Avatar: Observer registered");
	}

	@Override
	//to remove the Avatar as the subject for the appropriate observer
	public void removeObserver(Observer o) {
		observers.remove(o);
		
		/* testing */
		System.out.println("Avatar: Observer removed");
	}

	@Override
	//to notify the observer about e.g. Avatar taking damage in order to buff up silverguard knight
	public void notifyObserver(ActorRef out) {
		for (Observer o : observers) {
			o.update(out);
		}
		
		/* testing */
		System.out.println("GameState: Observer notified");
	}
}
