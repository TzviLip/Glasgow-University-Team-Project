package extensions;

import akka.actor.ActorRef;
import structures.GameState;

public class OnDeathUnit extends PlayableUnit {
	
	//Override method for when unit is destroyed to allow player to draw a card - Windstrike
	public void destroyUnit(ActorRef out, GameState gameState) {
		super.destroyUnit(out, gameState);
		
		GamePlayer player = (GamePlayer) getPlayer();
		player.drawCard(out);
			
		/* testing */
		System.out.println("On Death ability triggered: " + player.getName() + " draws a card");
	}
}
