package extensions;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/* 
 * AvatarDamageUnit is a subclass of PlayableUnit and is used to store common functionality 
 * for the trigger ‘if your avatar is dealt damage’.
 */
public class TrigDamageUnit extends PlayableUnit implements Observer{

    Subject avatar;

    public TrigDamageUnit(){
        super();
    }

    //Set the Subject to the correct Avatar
    public void setSubjectAvatar(){
    	
        GamePlayer gamePlayer = (GamePlayer) getPlayer();
        avatar = gamePlayer.getAvatar();
        updateLinkedList();
        
    	/* testing */
    	System.out.println(getName() + " subject set: Avatar");
    }

    //Add unit to the observer list
    private void updateLinkedList(){
        avatar.registerObserver(this);
    }

    //The effect that should occur when notified
    @Override
    public void update(ActorRef out) {
        //Play effect on unit
        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, ef,currentTile);

        this.addAttack(2,out); //Silverguard Knight gains 2 attack
        
    	/* testing */
    	System.out.println("On Avatar Damage effect applied to " + getName());
    }

    //Override method to remove unit from the observer list if it dies
    public void destroyUnit(int damageTaken, ActorRef out, GameState gameState) {
        super.destroyUnit(out, gameState);
        avatar.removeObserver(this);
    }
}
