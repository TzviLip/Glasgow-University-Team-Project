package extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;


import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.CardClicked;
import events.EndTurnClicked;
import events.EventProcessor;
import events.TileClicked;
import play.libs.Json;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;


/* 
 * Replicates the functionality of the HumanPlayer, 
 * enabling it to play out a turn and pass priority back to the human player. 
 * A simple AI behaviour.
 */
public class AIPlayer extends GamePlayer{

		
	ArrayList<String> deck = new ArrayList<String>() {
		{
			add(StaticConfFiles.c_blaze_hound); //ID = 20,21
			add(StaticConfFiles.c_bloodshard_golem); //ID = 22,23
			add(StaticConfFiles.c_hailstone_golem); //ID = 24,25
			add(StaticConfFiles.c_planar_scout); //ID = 26,27
			add(StaticConfFiles.c_pyromancer); //ID = 28,29
			add(StaticConfFiles.c_rock_pulveriser); //ID = 30,31
			add(StaticConfFiles.c_serpenti); //ID = 32,33
			add(StaticConfFiles.c_windshrike); //ID = 34,35
			add(StaticConfFiles.c_entropic_decay); //ID = 36,37
			add(StaticConfFiles.c_staff_of_ykir); //ID = 38,39
		}
	};
	
	// To increment unique id - AI units start at ID 20
	int uniqueID = 20;
	
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

	ArrayList<Tile> whiteTiles = new ArrayList<Tile>();
	ArrayList<Tile> redTiles = new ArrayList<Tile>();
	ArrayList<PlayableUnit> humanUnits = new ArrayList<PlayableUnit>();
	ArrayList<PlayableUnit> aiUnits = new ArrayList<PlayableUnit>();

	HashMap<PlayableUnit, Integer> attackMap;

	//Constructor
	public AIPlayer(ActorRef out, GameState gameState) {
		super(20, 0, "AI Player"); //Starting Health = 20, Starting Mana = 0 (Not their turn)
        placeAvatar(out,gameState); //Instatiate the AI Avatar
        BasicCommands.setPlayer2Health(out, this); // set UI health to starting health
		BasicCommands.setPlayer2Mana(out, this); // set UI mana to starting mana
		Collections.shuffle(cardDeck); //Shuffle the deck
		drawCard(out); //Draw 3 cards
		drawCard(out);
		drawCard(out);

		/* testing */
		System.out.println("New AIPlayer created");
	}
	
	//Create an AI Avatar on a tile
    public void placeAvatar(ActorRef out, GameState gameState){
        Tile tile = gameState.getBoard().clickedTile(7,2); //Tile to instantiate on
    	setAvatar(Avatar.playAvatar(out, gameState, tile, 101)); //Get an instance of Avatar
    	getAvatar().setPlayer(this);
    	
    	/* testing */
		System.out.println("AIPlayer: Avatar created on start tile");
    }
    
    //Set AI Player Health to a value
	public void setPlayerHealth(ActorRef out, int newHealth) {
		setHealth(newHealth);
		BasicCommands.setPlayer2Health(out, this); // set UI health to new health value
		
		if (getHealth() <= 0) {
			// AI Player health reached zero - AI loses game
			System.out.println("Human Wins");
			BasicCommands.addPlayer1Notification(out, "Human Wins", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		/* testing */
		System.out.println("AIPlayer health updated: " + newHealth);
	}
    
	//Draw a card
	public void drawCard(ActorRef out) {
		if (cardDeck.size() > 0) { // Only if deck has cards left
			Card toDraw = cardDeck.remove(0); // remove the top card of the deck
			if (hand.size() < 6) { // if a hand has space for more cards
				hand.add(toDraw); // add the card to players hand
			}
		}else {
			// Ai Player has no cards left - AI loses game
			System.out.println("Human Wins");
			BasicCommands.addPlayer1Notification(out, "Human Wins", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		/* testing */
		System.out.println("AIPlayer: card drawn");
		//Print number of cards left for testing
		System.out.println("AI Hand Size: " + hand.size());
		System.out.println("AI Deck Size: " + cardDeck.size());
	}
	
	//Set Mana to a value
	public void setCurrentMana(ActorRef out, GameState gameState, int mana) {
		setMana(mana);
		BasicCommands.setPlayer2Mana(out, this); // set UI mana to new mana value
		
		/* testing */
		System.out.println("AIPlayer mana updated: " + mana);
	}
    
	//set up AI
	public void makeAIReady(GameState gameState) {
		aiUnits = gameState.getAiFriendlyUnits();
		humanUnits = gameState.getHumanFriendlyUnits();
		whiteTiles = gameState.getWhiteHighlightedTiles();
		redTiles = gameState.getRedHighlightedTiles();
		
		System.out.println("Done making AI ready!");
	}

	
	
	/* get the human unit on board with highest health
	 * for use by entropic decay spell
	 */
	public PlayableUnit getHighestPlayer(ArrayList<PlayableUnit> friendlyUnits){

		PlayableUnit unit = null;
		ArrayList<PlayableUnit> units = null;

			units = (ArrayList<PlayableUnit>) friendlyUnits.stream()
					.filter(u -> u.getId() < 99)
					.sorted(Comparator.comparing(u -> u.getHealth()))
					.collect(Collectors.toList());

			if(!units.isEmpty()){
				unit = units.get(units.size()-1);
			}

			System.out.println(unit.getName() + " has the highest health for Entropic Decay: " + unit.getHealth());
			
		return unit;
	}
	
	/*logic for playing a card*/
	public void playCardOnTile(ActorRef out, GameState gameState, ArrayList<Tile> appropriateTiles) {
		// unit
		//unit is ranged or fly --> furthest tile possible
		if(gameState.getLastClicked().isUnitAI() && 
		  (gameState.getLastClicked().getId() == 28 || gameState.getLastClicked().getId() == 29 ||
		   gameState.getLastClicked().getId() == 34 || gameState.getLastClicked().getId() == 35)) {
			Tile furthestTile = null;
			for(PlayableUnit humanFriendlyUnit : humanUnits) {
				furthestTile = getFurthestFutureTileToUnit(humanFriendlyUnit);
			}
			sendTileClickedMessage(out, gameState, furthestTile.getTilex(), furthestTile.getTiley());
		//unit has provoke ability --> play it next to avatar
		}else if(gameState.getLastClicked().isUnitAI() && 
				(gameState.getLastClicked().getId() == 30 || gameState.getLastClicked().getId() == 31)) {
			Tile closestAITile = null;
			closestAITile = getClosestFutureTileToUnit(getAvatar());
			sendTileClickedMessage(out, gameState, closestAITile.getTilex(), closestAITile.getTiley());
		//unit play it offensive --> close to closest unit
		}else if(gameState.getLastClicked().isUnitAI()){
			Tile closestTile = null;
			for(PlayableUnit humanFriendlyUnit : humanUnits) {
				closestTile = getClosestFutureTileToUnit(humanFriendlyUnit);
			}
			sendTileClickedMessage(out, gameState, closestTile.getTilex(), closestTile.getTiley());
		}else {
			// spell card
			if(gameState.getLastClicked().getId() == 36 || gameState.getLastClicked().getId() == 37) {
				PlayableUnit victim = getHighestPlayer(humanUnits);
				Tile unitTile = victim.getCurrentTile();
				sendTileClickedMessage(out, gameState, unitTile.getTilex(), unitTile.getTiley());
			}else {
				sendTileClickedMessage(out, gameState, redTiles.get(0).getTilex(), redTiles.get(0).getTiley());
			}
		}
		gameState.resetHighlightingTilesAI(out);
		
	}
	
	/* moving and attacking - includes logic for moving to most appropriate tile,
	 * calls chooseVictim method which hosts attack logic */
	public void playUnit(ActorRef out, GameState gameState, PlayableUnit unit) {
		if(attackExist()) {
				PlayableUnit chosenVictim = chooseVictim(unit, gameState);
				Tile victimTile = chosenVictim.getCurrentTile();
				sendTileClickedMessage(out, gameState, victimTile.getTilex(), victimTile.getTiley());
		}else {
			//keep provoking units close to Avatar
			if(unit.getCanMove()) {
				if(gameState.getLastClickedUnit().getId() == 30 || gameState.getLastClickedUnit().getId() == 31) {
					Tile closestAITile = null;
					for(Tile whiteTile : whiteTiles) {
						closestAITile = getClosestFutureTileToUnit(getAvatar());
					}
					if(exist(closestAITile))
						sendTileClickedMessage(out, gameState, closestAITile.getTilex(), closestAITile.getTiley());
				}
				//play anything else offensive
				else {
					PlayableUnit opposedClosestUnit = getClosestUnit(unit);
					Tile closestTileToClosestEnemy = getClosestFutureTileToUnit(opposedClosestUnit);
					if(exist(closestTileToClosestEnemy)) {
						sendTileClickedMessage(out, gameState, closestTileToClosestEnemy.getTilex(), closestTileToClosestEnemy.getTiley());
					}
				System.out.println("Red tiles number: "+redTiles.size());
				System.out.println("White tiles number: "+whiteTiles.size());
				}
			}
		//attack or move based on our logic	
		}
		gameState.resetHighlightingTilesAI(out);
	}
	
	//get closest unit with respect to highlighted white tiles (movement strategy, move towards the closest unit)
	private PlayableUnit getClosestUnit(PlayableUnit unit) {
		double minimalDistance, temp;
		PlayableUnit closestUnit;
		
		if(whiteTiles.size() != 0) {
			System.out.println("white tiles: ");
			for(Tile t : whiteTiles) {
				System.out.println(t.getTilex() + " , " + t.getTiley());
			}
		}
		//Logic for getting the closest enemy unit for a specific unit
		
		minimalDistance = unit.getDistance(humanUnits.get(0).getCurrentTile());
		closestUnit = humanUnits.get(0);
		for(int i = 1; i < humanUnits.size(); i++) {
			temp = unit.getDistance(humanUnits.get(i).getCurrentTile());
			if(temp <= minimalDistance) {
				minimalDistance = temp;
				closestUnit = humanUnits.get(i);
			}
		}
		
		System.out.println(closestUnit.getName() + " is the closest unit to " + unit.getName());
		return closestUnit;
	}
	
	//get the furthest tile with respect to units existed on the board (keep the ranged units as far as possible so they are out of enemies' reach
	private Tile getFurthestFutureTileToUnit(PlayableUnit unit) {
		double maximalTileDistance;
		Tile closestTile = null;
		
		if(whiteTiles.size() != 0) {
			maximalTileDistance = unit.getDistance(whiteTiles.get(0));
			closestTile = whiteTiles.get(0);
			for(int i = 1; i < whiteTiles.size(); i++) {
			double temp = unit.getDistance(whiteTiles.get(i));
			if(maximalTileDistance <= temp) {
				maximalTileDistance = temp;
				closestTile = whiteTiles.get(i);
				}
			}
		}
		return closestTile;
	}
	
	//get the future closest tile to the closest unit
	private Tile getClosestFutureTileToUnit(PlayableUnit opposedClosestUnit) {
		double minimalTileDistance;
		Tile closestTile = null;
		
		if(whiteTiles.size() != 0) {
			minimalTileDistance = opposedClosestUnit.getDistance(whiteTiles.get(0));
			closestTile = whiteTiles.get(0);
			for(int i = 1; i < whiteTiles.size(); i++) {
			double temp = opposedClosestUnit.getDistance(whiteTiles.get(i));
			if(minimalTileDistance >= temp) {
				minimalTileDistance = temp;
				closestTile = whiteTiles.get(i);
				}
			}
		}
		return closestTile;
	}
	
	/* methods to returned properties of current game state */
	private boolean cardCanBePlayed(GameState gameState) {
		return (gameState.getLastClicked() != null && this.getMana() >= gameState.getLastClicked().getManacost());
	}
	private boolean unitsOnFieldExist() {
		return (aiUnits.size() != 0);
	}
	private boolean attackExist() {
		return (redTiles.size() != 0);
	}
	private boolean exist(Tile tile) {
		return (tile != null);
	}
	
	//Start AI Player turn
    public void takeTurn(ActorRef out, GameState gameState) {  	
    	/* testing */
    	System.out.println("AI playing");		
    	makeAIReady(gameState);
    	    	
    	while (getMana() > 0 ) {
    		int positionToPlay = aiPicksCardFromHand();
    		if (positionToPlay<0) 
    			break;
			sendCardClickedMessage(out, gameState, positionToPlay);
			if(cardCanBePlayed(gameState)) {
				ArrayList<Tile> appropriateTiles = new ArrayList<Tile>();
				System.out.println(gameState.getLastClicked().getId());
				
				appropriateTiles = gameState.getLastClicked().isUnitAI() ? whiteTiles : redTiles;	
				playCardOnTile(out, gameState, appropriateTiles);
			}
    	}
		
		if(unitsOnFieldExist()) {			
			//get the closest enemy for every unit, make that unit move close to it or even attack it if possible
			for(int iterator = 0; iterator < aiUnits.size() && gameState.getHumanPlayer().getHealth() > 0 ; iterator++) {
				System.out.println("Selected unit: " + aiUnits.get(iterator).getName());
				while(true) {
					sendTileClickedMessage(out, gameState, aiUnits.get(iterator).getCurrentTile().getTilex(), aiUnits.get(iterator).getCurrentTile().getTiley());
					if (whiteTiles.size()==0 && redTiles.size()==0) break;
					playUnit(out, gameState, aiUnits.get(iterator));
					if (iterator>=aiUnits.size()) break;
				}
			}
				//This highlights all the possible movement/attack tiles for a specific unit
		}
		sendEndTurnMessage(out, gameState);
    }
    
    /* method to choose best enemy unit to attack */
    private PlayableUnit chooseVictim(PlayableUnit unit, GameState gameState) {
    	HashMap<PlayableUnit, Integer> attackScores = new HashMap<>();
    	PlayableUnit enemyUnit = null;
    	int score;
    	List<PlayableUnit> topScoreUnits;
    	
    	/* add all valid enemy unit targets to hashmap with initial score 0 */
    	for (Tile t : redTiles) {
    		enemyUnit = (PlayableUnit) t.getUnit();
    		attackScores.put(enemyUnit, 0);
    	}
    	
    	for (Map.Entry<PlayableUnit, Integer> element : attackScores.entrySet()) {
    		enemyUnit = element.getKey();
    		score = element.getValue();
    		
    		/* if AI unit is ranged, choose human avatar if unit is outwith its attack range*/
    		if (enemyUnit instanceof Avatar && unit instanceof RangedUnit) {
    			List<Tile> humanAviTargets = gameState.getBoard().getAdjacentTiles(enemyUnit.getCurrentTile());
    			if (humanAviTargets.contains(unit.getCurrentTile())) return enemyUnit;
    		}
    		
    		/* if enemy has health < my unit's attack, i.e., my unit will kill it, score +2 */
    		else if (enemyUnit.getHealth() < unit.getAttack()) attackScores.put(enemyUnit, score+2);
    		
    		/* if not, if my unit's health > enemy attack, i.e., my unit won't die after a counter attack, score +2 */
    		else if (unit.getHealth() > enemyUnit.getAttack()) attackScores.put(enemyUnit, score+2);
    		
    		/* if unit has ability triggered by their Avatar damage, score +1 */
    		if (enemyUnit instanceof TrigDamageUnit) attackScores.put(enemyUnit, score+1);
    		
    		/* if unit has spell thief ability, score +1 */
    		if (enemyUnit instanceof SpellThiefUnit) attackScores.put(enemyUnit, score+1);
    		
    		/* if enemy has provoke, score -1 */
    		if (enemyUnit.getProvoke()) attackScores.put(enemyUnit, score-1);
    		
    		/* if unit has ability triggered by death, score -1 */
    		if (enemyUnit instanceof OnDeathUnit) attackScores.put(enemyUnit, score-1);
    	
    	}
    	
    	int maxScore = Collections.max(attackScores.values());
    	
    	/* get enemy with highest score */
    	topScoreUnits = attackScores.entrySet().stream()
    											.filter(entry -> entry.getValue() == maxScore)
    											.map(entry -> entry.getKey())
    											.collect(Collectors.toList());
    	
    	System.out.println("Testing Victim code");
    	for (PlayableUnit u : attackScores.keySet()) System.out.println(u.getName() + ": " + attackScores.get(u));
    	System.out.println("Top score: " + maxScore);
    	return enemyUnit = topScoreUnits.get(0);
    }
    
    //Call EndTurnClicked event to pass priority to the Human Player
    public void sendEndTurnMessage(ActorRef out, GameState gameState) {
    	//Create a new EndTurnClicked event and Json message
		EventProcessor processor = new EndTurnClicked();
		ObjectNode endTurnMessage = Json.newObject();
		endTurnMessage.put("AImessagetype", "endturnclickedAI");
		processor.processEvent(out, gameState, endTurnMessage); // process the event
		
		/* testing */
		System.out.println("AIPlayer: turn ended");
    }
    
    //Call TileClicked event to allow AI to interact with a tile
    private void sendTileClickedMessage(ActorRef out, GameState gameState, int tilex, int tiley) {
    	//Create a new TileClicked event and Json message
		EventProcessor processor = new TileClicked();
		ObjectNode tileClickedMessage = Json.newObject();
		tileClickedMessage.put("messagetype", "tileclicked");
		tileClickedMessage.put("tilex", Integer.toString(tilex));
		tileClickedMessage.put("tiley", Integer.toString(tiley));
		processor.processEvent(out, gameState, tileClickedMessage); // process the event
		
		/* testing */
		System.out.println("AIPlayer: clicked Tile " + tilex + ", " + tiley);
    }
    
    //Call CardClicked event to allow AI to interact with a card in hand
    private void sendCardClickedMessage(ActorRef out, GameState gameState, int position) {
    	//Create a new CardClicked event and Json message
		EventProcessor processor = new CardClicked();
		ObjectNode cardClickedMessage = Json.newObject();
		cardClickedMessage.put("messagetype", "cardclicked");
		cardClickedMessage.put("position", Integer.toString(position));
		processor.processEvent(out, gameState, cardClickedMessage); // process the event
		
		/* testing */
		System.out.println("AIPlayer clicked Card: " + getCard(position).getCardname());
    }
    
    /*method to return the position in the hand ArrayList 
     *of the card with highest mana cost and best score which AI has in hand*/
    private int aiPicksCardFromHand() {
    	int pos = -1;
    	Card temp = null;
    		for(int i = 0;i<hand.size();i++) {
    			if(temp == null) { //if we haven't yet saved a card and we have enough mana for this card
    				if(hand.get(i).getManacost() <= getMana()) {
        				pos = i; //maxManaCardPos becomes temp
        				temp = hand.get(i); //  temp becomes that card	

    				}
    			}    			
    			else if(calculateScore(temp) < calculateScore(hand.get(i)) && hand.get(i).getManacost() <= getMana()) { // if the manacost of the temp card is more than 0 initially & we have enough mana    				
    				pos = i; //maxManaCardPos becomes temp
    				temp = hand.get(i);
    		}
    	}
		return pos; 
    }
    
    /*method to calculate score for each AI card
     * this method is used in the aiPicksCardFromHand() method
     */
    private float calculateScore(Card c) {
    	float score;
    	float manaCost = c.getManacost();
    	float attack = Math.abs(c.getBigCard().getAttack());
    	float health = Math.abs(c.getBigCard().getHealth());
    	float ability = mapForAbilities.get(c.getCardname());
    
    	if(getHealth() >= 10) { //play offensively 
	    	manaCost *= 0.1f;
	    	attack *= 0.4f;
	    	health *= 0.2f;
	    	ability *= 0.3f;
	    	
    	}
    	else { //play defensively 
	    	manaCost *= 0.1f;
	    	attack *= 0.1f;
	    	health *= 0.50f;
	    	ability *= 0.3f;
    	}
    	score = manaCost + attack + health + ability;

		return score;

    }
	
   //map with card name String ability score as Float
   /*put all AI cards into a map with a score for their ability 
    *this ability is taken into account when calculating overall score in caluculateScore method
    */
   Map<String, Float> mapForAbilities = Map.of(
		   "Planar Scout", 5.0f,
		   "Rock Pulveriser", 5.0f,
		   "Pyromancer", 8.0f,
		   "Bloodshard Golem", 0.0f,
		   "Blaze Hound", 1.0f,
		   "WindShrike", 7.0f,
		   "Hailstone Golem", 0.0f,
		   "Serpenti", 9.8f,
		   "Staff of Y'Kir'", 10.0f,
		   "Entropic Decay" , 10.0f
   );   
}
