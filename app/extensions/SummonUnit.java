package extensions;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/* 
 * SummonUnit is a subclass of PlayableUnit and is used to store common functionality for the trigger ‘when this unit is summoned’.
 */
public class SummonUnit extends PlayableUnit{

	//Azure Herald Summon
    public static void azureSummon(ActorRef out, GameState gameState){
		/* testing */
		System.out.println("On Summon ability triggered: +3 health to HumanPlayer Avatar");
    	
        //Play effect on Avatar
        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, ef, gameState.getHumanPlayer().getAvatar().currentTile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

        //Heal Avatar and Human Player by 3
        gameState.getHumanPlayer().getAvatar().takeHealing(3,out);
    }

    //Blaze Hound Summon
    public static void blazeSummon(ActorRef out,GameState gameState){
    	/* testing */
		System.out.println("On Summon ability triggered: Both players draw a card");
		
        //Draw Card for Human Player
        gameState.getHumanPlayer().drawCard(out);
        //Draw Card for AI Player
        gameState.getAiPlayer().drawCard(out);
    }
}
