package extensions;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/*PlayableUnit is a subclass of Unit and stores some common implementation such as movement, attacking, and damage dealing. 
 * All PlayableUnits have a variable health associated with them, 
 * which will be updated when the unit takes damage or regains health. 
 * All PlayableUnits will have a method that will enable them to move from one Tile to another, 
 * to deal damage to the opponent’s PlayableUnits and to be removed from the board if their health reaches 0. 
 * A specific unit animation will be played according to the current action that the unit is performing.
 */
public class PlayableUnit extends Unit {

	Player player;
	Tile currentTile;
	int health; 
	int attack; 
	int maxHealth;
	String name;
	boolean friendly;
	boolean canMove;
	boolean canAttack;
	boolean provoke;
	boolean fly;

	//Constructor
	public PlayableUnit() {
	}
	
	//Create a Unit, set its UI values and summon it
	public static PlayableUnit playUnit(ActorRef out, GameState gameState, Tile tile, int ID) {
		//Play Summon FX
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//Instantiate Unit
		PlayableUnit unit = gameState.getUnitFactory().makeUnit(ID, tile);
		unit.setPositionByTile(tile);
		tile.setUnit(unit);
		
		//UI
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
		BasicCommands.setUnitAttack(out, unit, unit.getAttack());
		return unit;
	}
	
	//Method to check is a unit is friendly based on ID
	public static boolean isFriendlyUnit(int cardID) {		
		if ((cardID >= 0 && cardID <= 15) || cardID == 100) { //IDs of friendly unit cards
				return true;
		}
		return false;
	}
	
	//Method to check is a unit is an enemy based on ID
	public static boolean isEnemyUnit(int cardID) {		
		if ((cardID >= 20 && cardID <= 35) || cardID == 101) { //IDs of enemy unit cards
				return true;
		}
		return false;
	}
	
	//Attack an adjacent tile and initiate counter-attack if possible - calls Unit attack method
	public void battle(ActorRef out, Tile attackingTile, Tile opponentTile, PlayableUnit opponent, Board board, GameState gameState) {

		/* testing */
		System.out.println(this.getName() + ": attacking");
		
		this.attack(out, attackingTile, opponentTile, opponent, gameState);	
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//Counter attack if target health > 0
		if (opponent.getHealth()>0) {
			/* testing */
			System.out.println(opponent.getName() + ": counterattacking");
			
			opponent.attack(out, opponentTile, attackingTile, this, gameState);
		}
		
		this.setCanAttack(false); //Can no longer attack this turn
		this.setCanMove(false); //Can no longer move this turn
	}
	
	//Attack, i.e., reduce target unit health, play effect & update target health stat - calls Unit takeDamage method
	public void attack(ActorRef out, Tile attackingTile, Tile opponentTile, PlayableUnit opponent, GameState gameState) {
		
		//play attacking effect
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		
		//Dynamic wait time for attack based on number of animation frames and frames per second
		int numAttackFrames = getAnimations().getAttack().getFrameStartEndIndices()[1]-getAnimations().getAttack().getFrameStartEndIndices()[0];
		int fps = getAnimations().getAttack().getFps();
		int waitTime = (numAttackFrames / fps) * 1000 + 500;

		try {Thread.sleep(waitTime);} catch (InterruptedException e) {e.printStackTrace();}
		
		if (this instanceof RangedUnit) { //play projectile effect if RangedUnit
			EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
			BasicCommands.playProjectileAnimation(out, projectile, 0, attackingTile, opponentTile);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		//cause damage to opponent unit
		opponent.takeDamage(this.attack, out, gameState);		
		
		//Return unit to idle
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
	}	
	
	//Move a unit from one Tile to another
	public void move(ActorRef out, GameState gameState, Tile currentTile, Tile targetTile, boolean yfirst) {
		
		currentTile.setUnit(null); //Once clicked on new location, remove ID from old tiles
		this.currentTile = targetTile; //Set this units new Tile
		targetTile.setUnit(this);	//place this unit on the end tile
		canMove = false; //Can no longer move this turn
		
		//UI Move
		BasicCommands.moveUnitToTile(out,this,targetTile, yfirst);
		setPositionByTile(targetTile);
		
		//Calculate x and y distance to dynamically alter wait time
		int currentX = currentTile.getTilex();
		int currentY = currentTile.getTiley();
		int targetX = targetTile.getTilex();
		int targetY = targetTile.getTiley();
		
		int xDifference = Math.abs(currentX - targetX);
		int yDifference = Math.abs(currentY - targetY);
		int totalDifference = xDifference + yDifference;
		int waitTime = totalDifference * 1000 + 500;
				
		try {Thread.sleep(waitTime);} catch (InterruptedException e) {e.printStackTrace();} //dynamic wait time
	}
	
	//Reduce unit health by a value
	public void takeDamage(int damageTaken, ActorRef out, GameState gameState) {
		
		health -= damageTaken;
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}

		//if the health of that unit is 0 or lower after applying the effect
		if(health <= 0) 
			destroyUnit(out, gameState);
		
		/* testing */
		System.out.println(this.getName() + ": " + damageTaken + " damage taken");
	}
	
	//Unit dies, play animation and remove from the board
	protected void destroyUnit(ActorRef out, GameState gameState) {
		
		//Death animation 
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Remove instance from tile 
		currentTile.setUnit(null);
		this.setPosition(null);
		currentTile = null;	//Set the current tile to null to take the unit off the tile
		
		// Delete unit from UI
		BasicCommands.deleteUnit(out, this);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
		
		if (!this.isFriendly()) gameState.getAiFriendlyUnits().remove(this);
		else gameState.getHumanFriendlyUnits().remove(this);
		
		/* testing */
		System.out.println(this.getName() + ": dead");
	}
	
	//Increase unit health by a value
	public void takeHealing(int heal, ActorRef out) {
		health += heal; 		
		if(health >= maxHealth) { //Cannot increase above max health
			health = maxHealth;
		}
		
		//set the new health of that unit on the UI
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
		
		/* testing */
		System.out.println(this.getName() + ": healing, +" + heal);
	}
	
	//Increase attack value of unit
	public void addAttack(int add, ActorRef out) {
		int newAttack = this.attack + add;
		this.setAttack(newAttack);
		System.out.println(this.getName() + ": new attack = " + attack);
		
		//set the new attack value of that unit on the UI
		BasicCommands.setUnitAttack(out, this, this.attack);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//Increase health value of a unit - Can go over the maximum
	protected void addHealth(int add, ActorRef out) {
		int newHealth = this.health + add; 
		this.setHealth(newHealth);
		System.out.println(this.getName() + ": new health = " + health);
		
		//set the new health of that unit on the UI
		BasicCommands.setUnitHealth(out, this, this.health);
		try {Thread.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//get distance between the 2 units (for AI to get the closest enemy available and make its way through towards it)
	protected double getDistance(Tile tile) {
		double enemyX = tile.getTilex() + 1;
		double enemyY = tile.getTiley() + 1;
		
		double unitX = this.currentTile.getTilex() + 1;
		double unitY = this.currentTile.getTiley() + 1;
		
		double distance = Math.sqrt(Math.pow((enemyX - unitX), 2) + Math.pow((enemyY - unitY), 2));
		return distance;
	}
	
	
	/* GETTERS & SETTERS */

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean doesFly() {
		return fly;
	}

	public void makeItFly(boolean fly) {
		this.fly = fly;
	}

	public boolean getProvoke() {
		return provoke;
	}

	public void setProvoke(boolean provoke) {
		this.provoke = provoke;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean getCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean getCanAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}

	public boolean isFriendly() {
		return friendly;
	}

	public void setFriendly(boolean friendly) {
		this.friendly = friendly;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	//needed to avoid infinite recursion issues
	@JsonIgnore
	public Tile getCurrentTile() {
		return currentTile;
	}

	//needed to avoid infinite recursion issues
	@JsonIgnore
	public void setCurrentTile(Tile currentTile) {
		this.currentTile = currentTile;
	}

}
