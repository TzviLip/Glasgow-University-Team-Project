package extensions;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

/*RangedUnit is a subclass of PlayableUnit and is used to store common functionality for units with the ‘Ranged’ keyword – 
 *can attack any enemy on the board.
 */
public class RangedUnit extends PlayableUnit{

	public RangedUnit() {
		super();
	}
		
	//Attack any tile and initiate counter-attack if possible - calls attack method
	public void battle(ActorRef out, Tile attackingTile, Tile opponentTile, PlayableUnit opponent, Board board, GameState gameState) {

		/* testing */
		System.out.println(getName() + ": attacking");
		
		//attack target opponent
		this.attack(out, attackingTile, opponentTile, opponent, gameState);	
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//get tiles adjacent to target opponent
		ArrayList<Tile> adjacentTiles = board.getAdjacentTiles(opponentTile);
		
		/*Counter attack if target health > 0 and at least one of following satisfied:
		(1) target is adjacent to attacking unit 
		(2) target has ranged ability */
		if (opponent.getHealth()>0 && (adjacentTiles.contains(attackingTile) || opponent instanceof RangedUnit)) {
			
			/* testing */
			System.out.println(opponent.getName() + " can counterattack as it is ranged or adjacent");
			System.out.println(opponent.getName() + ": counterattacking");
			
			//play attacking effect for opponent attack
			BasicCommands.playUnitAnimation(out, opponent, UnitAnimationType.attack);
			
			//counter-attack
			opponent.attack(out, opponentTile, attackingTile, this, gameState);
		}
		
		//Unit can no longer move or attack
		this.setCanAttack(false);
		this.setCanMove(false);
	}
}
