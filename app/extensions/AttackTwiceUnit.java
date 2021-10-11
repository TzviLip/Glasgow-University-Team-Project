package extensions;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;

/* 
 * AttackTwiceUnit is a subclass of PlayableUnit and is used to store common functionality 
 * for units which are able to attack twice per turn
 */

public class AttackTwiceUnit extends PlayableUnit{
	int counter = 0; //Track number of times this has attacked
	
	//to allow a unit to attack twice in one turn using the counter, if counter is 2 no more attacking in one turn is possible
	public void battle(ActorRef out, Tile attackingTile, Tile opponentTile, PlayableUnit opponent, Board board, GameState gameState) {
		
		this.attack(out, attackingTile, opponentTile, opponent, gameState);	
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//Counter attack if target health > 0
		if (opponent.getHealth()>0) {
			opponent.attack(out, opponentTile, attackingTile, this, gameState);
		}
		
		counter++;
		if(counter == 2) {
			setCanAttack(false); //Can no longer attack this turn
			setCanMove(false); //Can no longer move this turn
			
			/* testing */
			System.out.println("Attacked Twice");
		}
	}
}
