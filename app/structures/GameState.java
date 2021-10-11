package structures;

import extensions.HumanPlayer;
import extensions.Observer;
import extensions.PlayableUnit;
import extensions.Subject;
import extensions.UnitFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import extensions.AIPlayer;
import extensions.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState implements Subject{
	
	
	Player currentPlayer; //To track the current player
	HumanPlayer humanPlayer; //Reference to the Human Player
	AIPlayer aiPlayer; //Reference to the AI Player
	Board board; //Reference to the game board
	int turn = 1; //Current turn - increments after both players have a turn
	Card lastClicked = null; //Track the last hand card that was clicked
	int lastClickedPosition = -1; //Track the position of the last hand card that was clicked
	UnitFactory unitFactory = new UnitFactory(this); //Used to instantiate different types of units
	PlayableUnit lastClickedUnit = null; //Track the last friendly unit clicked on the board
	Tile lastClickedTile = null; //Track the last tile clicked on the board
	
	/* ArrayList of IDs pertaining to spell cards */
	ArrayList<Integer> spellCardIDs = new ArrayList<Integer>(Arrays.asList(16, 17, 18, 19, 36, 37, 38, 39));
	
	/* GameState is a Subject of SpellThiefUnit Observer class
	 * ArrayList of SpellThief units - updates as Pureblade Enforcer units are instantiated and 'destroyed' */
	LinkedList<Observer> spellThieves = new LinkedList<Observer>(); 
	
	/*@JsonIgnore
	HashMap<Tile, Integer> whiteHighlightedTiles = new HashMap<>();
	@JsonIgnore
	HashMap<Tile, Integer> redHighlightedTiles = new HashMap<>();*/
	
	ArrayList<Tile> whiteHighlightedTiles = new ArrayList<Tile>();
	ArrayList<Tile> redHighlightedTiles = new ArrayList<Tile>();
	ArrayList<PlayableUnit> humanFriendlyUnits = new ArrayList<PlayableUnit>();
	ArrayList<PlayableUnit> aiFriendlyUnits = new ArrayList<PlayableUnit>();

	public void resetVariables(ActorRef out) {
		setLastClicked(null); //No Card should be selected
		setLastClickedPosition(-1); //No Clicked Position
		
		setLastClickedUnit(null); //No Board unit should be selected
		setLastClickedTile(null); //No Tile is clicked
		
		getBoard().deHighlightTiles(out); //No Tiles should be selected
	}
	
	//Reset lastClicked, the board, and the players hand
	public void resetGameStateCardVariables(ActorRef out) {
		System.out.println("Reset GameState: Card");
		setLastClicked(null);
		getHumanPlayer().redrawHand(out);
		getBoard().deHighlightTiles(out);
	}
	
	//Reset lastClickedFriendlyUnit, lastClickedEnemyUnit lastClickedTile, and the board
	public void resetGameStateUnitVariables(ActorRef out) {
		System.out.println("Reset GameState: Units");
		setLastClickedUnit(null);
		setLastClickedTile(null); //Save a reference to the units tile
		getBoard().deHighlightTiles(out); //Stop all tile highlighting to undo previous selected units
	}
	
	public void resetHighlightingTilesAI(ActorRef out) {
		System.out.println("Reset GameState : TilesAI");
		whiteHighlightedTiles.clear();
		redHighlightedTiles.clear();
	}
	
	public void saveUnit(PlayableUnit unit, Tile tile){
		setLastClickedUnit(unit); //Save a reference to the unit
		setLastClickedTile(tile); //Save a reference to the unit's tile
		System.out.println("Saved Unit " + getLastClickedUnit().getName());
	}
	
	public ArrayList<Integer> getSpellCardIDs() {
		return spellCardIDs;
	}

	public Tile getLastClickedTile() {
		return lastClickedTile;
	}

	public void setLastClickedTile(Tile lastClickedTile) {
		this.lastClickedTile = lastClickedTile;
	}

	public PlayableUnit getLastClickedUnit() {
		return lastClickedUnit;
	}

	public void setLastClickedUnit(PlayableUnit lastClickedUnit) {
		this.lastClickedUnit = lastClickedUnit;
	}

	public UnitFactory getUnitFactory() {
		return unitFactory;
	}
	
	public int getLastClickedPosition() {
		return lastClickedPosition;
	}

	public void setLastClickedPosition(int lastClickedPosition) {
		this.lastClickedPosition = lastClickedPosition;
	}	
	
	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Card getLastClicked() {
		return lastClicked;
	}

	public void setLastClicked(Card lastClicked) {
		this.lastClicked = lastClicked;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;	
	}

	public AIPlayer getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(AIPlayer aiPlayer) {
		this.aiPlayer = aiPlayer;
	}

	public void setHumanPlayer(HumanPlayer humanPlayer) {
		this.humanPlayer = humanPlayer;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}
	
	public void setWhiteHighlightedTiles(ArrayList<Tile> whiteHighlightedTiles) {
		this.whiteHighlightedTiles = whiteHighlightedTiles;
	}
	public void setRedHighlightedTiles(ArrayList<Tile> redHighlightedTiles) {
		this.redHighlightedTiles = redHighlightedTiles;
	}
	public ArrayList<Tile> getWhiteHighlightedTiles(){
		return whiteHighlightedTiles;
	}
	public ArrayList<Tile> getRedHighlightedTiles(){
		return redHighlightedTiles;
	}
	
	public ArrayList<PlayableUnit> getHumanFriendlyUnits(){
		return humanFriendlyUnits;
	}

	
	public ArrayList<PlayableUnit> getAiFriendlyUnits(){
		return aiFriendlyUnits;
	}
	
    /* for testing */
	public void addHumanUnit(PlayableUnit unit) {
		humanFriendlyUnits.add(unit);
	}
	
	/* 
	 * Methods to register, remove and notify SpellThief Observer instances.
	 */
	
	public void registerObserver(Observer o) {
		spellThieves.add(o);
		
		/* testing */
		System.out.println("GameState: Observer registered");
	}

	public void removeObserver(Observer o) {
		spellThieves.remove(o);
		
		/* testing */
		System.out.println("GameState: Observer removed from list");
	}

	public void notifyObserver(ActorRef out) {
		for (Observer o : spellThieves) {
			o.update(out);
		}
		
		/* testing */
		System.out.println("GameState: Observer notified");
	}
	
}
