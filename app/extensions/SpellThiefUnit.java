package extensions;

import akka.actor.ActorRef;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/* 
 * SpellThiefUnit is an Observer class with Subject class GameState
 * 
 * Only SpellThief unit in this game is Deck 1 Pureblade Enforcer
 * +1 attack & +1 health when enemy plays a spell 
 * 
 */

public class SpellThiefUnit extends PlayableUnit implements Observer {
	
    Subject gameState;

    public SpellThiefUnit(){
        super();
    }

    /* Set Subject & add this instance as Observer in Subject class 
     * Register this instance as Observer in Subject class */
    public void setSubject(GameState currentGame){
    	gameState = currentGame;
    	gameState.registerObserver(this);
    	
    	/* testing */
    	System.out.println(getName() + " subject set: gameState");
    }
    

	/* Apply SpellThief effect 
	 * calls takeHealing & takeAttack methods
	 * (these methods play health & attack effects) */
	public void update(ActorRef out) {	
		/* Buff animation */
		EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
		BasicCommands.playEffectAnimation(out, buff, this.currentTile);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		/* Update health & attack */
		this.addHealth(1, out);
		this.addAttack(1, out);
		
		/* testing */
		System.out.println("SpellThief effect applied to " + getName());
	}

    /* Override destroyUnit method to remove this from Subject Observer list if it dies */
	public void destroyUnit(ActorRef out, GameState gameState) {
		//Call PlayableUnit destroyUnit method
		super.destroyUnit(out, gameState);
		
		/* Remove instance from Observer list in Subject class */
		gameState.removeObserver(this);
	}	
}
