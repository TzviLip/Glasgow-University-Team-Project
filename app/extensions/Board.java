package extensions;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.GameState;
import structures.basic.Card;
import utils.BasicObjectBuilders;

/*
 * Board is a class that holds references to all Tiles on the board. 
 * And keeps track of which Units are on which Tile. 
 * Contains a reference to an array of type Tile.
 */

public class Board {

	private Tile[][] board = new Tile[9][5];
	
	GameState gameState;

	public Board(ActorRef out, GameState gameState) {
		makeBoard(out);
		this.gameState = gameState;
	}

	//Create a 9x5 board of tiles with normal highlighting 
	private void makeBoard(ActorRef out) {
		// Loop through all rows and columns and make a tile for each
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 5; y++) {
				Tile tile = BasicObjectBuilders.loadTile(x, y);
				board[x][y] = tile;
				highlightGrey(out, tile);
			}
		}
	}
	
	public Tile[][] getBoard() {
		return board;
	}

	//Given an x and y, find the Tile at this position and return null if not found
	public Tile clickedTile(int x, int y) {
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) //if tile exists in the board range return it, otherwise return null
			return board[x][y];
		
		return null;
	}
	
	//to check if two units belong to different players
	private boolean differentPlayers(PlayableUnit unitOne, PlayableUnit unitTwo) {
		if (unitOne.isFriendly() != unitTwo.isFriendly()) {
			return true;
		}
		return false;
	}
	
	/*to check if highlighting a tile should occur in red for a unit to attack it,
	which is when the unit on the tile belongs to the opponent
	*/
	private void compareUnits(ActorRef out, Tile tile, PlayableUnit unit) {
		PlayableUnit tileUnit = (PlayableUnit) tile.getUnit();
		if(tileUnit != null) {
			if(differentPlayers(unit, tileUnit)) {
				highlightRed(out, tile);
			}
		}
	}
	
	//Given a unit and its tile, if the unit can attack, highlight all adjacent enemy attack targets
	private void highlightAttackTiles(ActorRef out, Tile tile, PlayableUnit unit) {
		if(unit.getCanAttack()) {
			ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
			adjacentTiles = getAdjacentTiles(tile);
			
			ArrayList<PlayableUnit> provokes = getAdjacentProvokeUnits(tile, unit);	
			
			for(Tile adjTile : adjacentTiles) {
				PlayableUnit adjUnit = (PlayableUnit) adjTile.getUnit();
				if (adjUnit != null) {
					if (adjUnit.getProvoke() || provokes.size() == 0) {
						compareUnits(out, adjTile, unit);
					}
				}
			}
		}
	}
	
	//Given a ranged unit, if the unit can attack, highlight all enemy attack targets
	private void highlightRangedAttack(ActorRef out, PlayableUnit unit) {
		if(unit.getCanAttack()) {
			ArrayList<PlayableUnit> provokes = getAdjacentProvokeUnits(unit.getCurrentTile(), unit);
			if(provokes.size() == 0) {
				for(int i = 0; i < 9; i++) {
					for(int j = 0; j < 5; j++) {
						compareUnits(out, clickedTile(i, j), unit);
					}
				}
			}
			else {
				for(PlayableUnit provokeUnit : provokes) {
					compareUnits(out, provokeUnit.getCurrentTile(), unit);
				}
			}
		}
	}
	
	//Given a flying unit, if the unit can attack, highlight all enemy attack targets considering provoke possibilities
	private void highlightFlyAttack(ActorRef out, PlayableUnit unit) {
		if(unit.getCanAttack()) {
			for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 5; j++) {
					PlayableUnit currentUnit = (PlayableUnit) clickedTile(i, j).getUnit();
					if(currentUnit != null) {
						ArrayList<Tile> adjacentEnemyTiles = getAdjacentWhiteTiles(clickedTile(i, j));
						if(!currentUnit.getProvoke()) {
							for(int iterator = 0; iterator < adjacentEnemyTiles.size(); iterator++) {
								if(adjacentEnemyTiles.get(iterator).getMode() == 1) {
									ArrayList<PlayableUnit> provokes = getAdjacentProvokeUnits(adjacentEnemyTiles.get(iterator), unit);
									if(provokes.size() != 0) {
										adjacentEnemyTiles.remove(iterator);
										iterator--;
									}
								}
							}
						}
						if(adjacentEnemyTiles.size() != 0) {
							System.out.println("checking tile");
							System.out.println(clickedTile(i, j));
							for(Tile t : adjacentEnemyTiles) {
								System.out.println(t);
							}
							compareUnits(out, clickedTile(i, j), unit);
						}
						
					}
				}
			}
		}
	}
	
	//Helper: Highlight row tiles as red or white if a unit can move/attack
	private void highlightRows(ActorRef out, Tile tile, PlayableUnit unit) {
		Tile lastTileHighlighted = null;
		int x = tile.getTilex();
		int y = tile.getTiley();
		int newX;
		
		newX = x;
		for(int k = 1; k <= 2; k++) {
			tile = clickedTile(++newX, y);
			if(tile != null) {
				PlayableUnit otherUnit = (PlayableUnit) tile.getUnit();
				if(otherUnit != null) {
					if(differentPlayers(unit, otherUnit))
						break;
					continue;
				}
				lastTileHighlighted = tile;
				highlightWhite(out, lastTileHighlighted);
			}
		}
		if(lastTileHighlighted != null)
			highlightAttackTiles(out, lastTileHighlighted, unit);
		
		newX = x;
		for (int k = 1; k <= 2; k++) {
			tile = clickedTile(--newX, y);
			
			if(tile != null) {
				PlayableUnit otherUnit = (PlayableUnit) tile.getUnit();
				if(otherUnit != null) {
					if(differentPlayers(unit, otherUnit))
						break;
					continue;
				}
				lastTileHighlighted = tile;
				highlightWhite(out, lastTileHighlighted);
			}
		}
		if(lastTileHighlighted != null && !isRanged(unit))
			highlightAttackTiles(out, lastTileHighlighted, unit);
	}
	
	//Helper: Highlight column tiles as red or white if a unit can move/attack
	private void highlightColumns(ActorRef out, Tile tile, PlayableUnit unit) {
		Tile lastTileHighlighted = null;
		int x = tile.getTilex();
		int y = tile.getTiley();
		int newY;
		
		newY = y;
		for(int k = 1; k <= 2; k++) {
			tile = clickedTile(x, ++newY);
			
			if(tile != null) {
				PlayableUnit otherUnit = (PlayableUnit) tile.getUnit();
				if(tile.getUnit() != null) {
					if(differentPlayers(unit, otherUnit))
						break;
					continue;
				}
				lastTileHighlighted = tile;
				highlightWhite(out, lastTileHighlighted);
			}
		}
		if(lastTileHighlighted != null)
			highlightAttackTiles(out, lastTileHighlighted,unit);
		
		newY = y;
		for(int k = 1; k <= 2; k++) {
			tile = clickedTile(x, --newY);
			
			if(tile != null) {
				PlayableUnit otherUnit = (PlayableUnit) tile.getUnit();
				if(tile.getUnit() != null) {
					if(differentPlayers(unit, otherUnit))
						break;
					continue;
				}
				lastTileHighlighted = tile;
				highlightWhite(out, lastTileHighlighted);
			}
		}
		if(lastTileHighlighted != null && !isRanged(unit))
			highlightAttackTiles(out, lastTileHighlighted, unit);
	}
	
	//Helper: Highlight diagonal tiles as red or white if a unit can move/attack
	private void highlightDiagonals(ActorRef out, Tile tile, PlayableUnit unit) {
		int x = tile.getTilex();
		int y = tile.getTiley();
		boolean rowFlag = false, colFlag = false;
		
		// diagonal moving --> 1 forward 1 downward
		tile = clickedTile(x + 1, y);
		rowFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		tile = clickedTile(x, y + 1);
		colFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		if (rowFlag || colFlag) {
			tile = clickedTile(x + 1, y + 1);
			if (tile != null && tile.getUnit() == null) {
				highlightWhite(out, tile);
				if(!isRanged(unit))
					highlightAttackTiles(out, tile, unit);
			}
		}

		// diagonal moving --> 1 forward 1 upward
		tile = clickedTile(x + 1, y);
		rowFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		tile = clickedTile(x, y - 1);
		colFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		if (rowFlag || colFlag) {
			tile = clickedTile(x + 1, y - 1);
			if (tile != null && tile.getUnit() == null) {
				highlightWhite(out, tile);
				if(!isRanged(unit))
					highlightAttackTiles(out, tile, unit);
			}
		}

		// diagonal moving --> 1 backward 1 downward
		tile = clickedTile(x - 1, y);
		rowFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		tile = clickedTile(x, y + 1);
		colFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		if (rowFlag || colFlag) {
			tile = clickedTile(x - 1, y + 1);
			if (tile != null && tile.getUnit() == null) {
				highlightWhite(out, tile);
				if(!isRanged(unit))
					highlightAttackTiles(out, tile, unit);
			}
		}

		// diagonal moving --> 1 backward, 1 upward
		tile = clickedTile(x - 1, y);
		rowFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		tile = clickedTile(x, y - 1);
		colFlag = (tile != null && tile.getUnit() != null && differentPlayers(unit, (PlayableUnit) tile.getUnit())) ? false : true;
		if (rowFlag || colFlag) {
			tile = clickedTile(x - 1, y - 1);
			if (tile != null && tile.getUnit() == null) {
				highlightWhite(out, tile);
				if(!isRanged(unit))
					highlightAttackTiles(out, tile, unit);
			}
		}
	}
	
	//When a unit is clicked, highlight tiles for movement and attacking using the helper methods
	public void highlightTiles(ActorRef out, PlayableUnit unit) {
		int x = unit.getPosition().getTilex();
		int y = unit.getPosition().getTiley();

		Tile tile = clickedTile(x,y);
		
		ArrayList<PlayableUnit> adjacentProvoke = getAdjacentProvokeUnits(tile, unit);
		if (adjacentProvoke.size() == 0) { //if there are no adjacent provoke units
			if(!isRanged(unit)) //Not Ranged Unit
				highlightAttackTiles(out, tile, unit);
			else //Ranged Unit
				highlightRangedAttack(out, unit);
			
			if(unit.getCanMove() && !unit.doesFly()) { //Not flying but can still move
				highlightColumns(out, tile, unit);
				highlightRows(out, tile, unit);
				highlightDiagonals(out, tile, unit);
			}
			if(unit.getCanMove() && unit.doesFly()) { //Flying and can still move
				highlightAllEmptyTilesWhite(out);
				highlightFlyAttack(out, unit);
			}
		}else {
			highlightProvokeAttackTiles(out, tile, unit, adjacentProvoke);
		}
	}
	
	private boolean isRanged(PlayableUnit unit) {
		if (unit instanceof RangedUnit) {
			return true;
		}
		return false;
	}
	
	//Highlight only adjacent enemy units with provoke
	private void highlightProvokeAttackTiles(ActorRef out, Tile tile, PlayableUnit unit, ArrayList<PlayableUnit> adjacentProvoke) {
		if(unit.getCanAttack()) {
			ArrayList<Tile> adjacentTiles = getAdjacentTiles(tile);
			
			for(Tile adjTile : adjacentTiles) {
				PlayableUnit adjUnit = (PlayableUnit) adjTile.getUnit();
				if(adjUnit != null) {
					if (adjacentProvoke.contains(adjTile.getUnit())) {
						if(differentPlayers(unit, adjUnit)) {
							highlightRed(out, adjTile);
						}
					}
				}
			}
		}
	}
	
	//Return an ArrayList of all enemy adjacent units with the provoke ability
	private ArrayList<PlayableUnit> getAdjacentProvokeUnits(Tile tile, PlayableUnit unit) {
		ArrayList<Tile> toCheckProvoke = getAdjacentTiles(tile);
		ArrayList<PlayableUnit> provokeUnits = new ArrayList<PlayableUnit>();
		PlayableUnit clickedUnit = unit;
		for (Tile toCheck : toCheckProvoke) {
			if (toCheck.getUnit() != null && clickedUnit != null) { //Only check if the unit exists
				PlayableUnit unitToCheck = (PlayableUnit) toCheck.getUnit();
				if (differentPlayers(clickedUnit, unitToCheck) && unitToCheck.getProvoke()) { //D and has provoke
					provokeUnits.add(unitToCheck);
				}
			}
		}
		return provokeUnits;
	}
		
	//Deselect all tiles
	public void deHighlightTiles(ActorRef out) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (clickedTile(i, j).getMode() != 0) {
					highlightGrey(out, clickedTile(i, j));
				}
			}
		}
	}
	
	//Highlighting spell targets depending on card ID
	public void highlightSpellTargets(ActorRef out, Card spellCard) {
		int id = spellCard.getId();
		for (int i=0; i<9; i++) {
			for (int j=0; j<5; j++) {
				Tile tile = this.clickedTile(i, j); 
				if (tile.getUnit() != null) {
					PlayableUnit unit = (PlayableUnit) tile.getUnit();

					//Played on any unit
					// if Sundrop Elixir
					if (id==16||id==17) this.highlightRed(out, tile);
					
					// if Entropic Decay
					if ((id==36||id==37) && !(unit instanceof Avatar)) 
						this.highlightRed(out, tile);

					//Only Played on enemy units
					if (!unit.isFriendly()) {
						// if Truestrike
						if (id==18||id==19) this.highlightRed(out, tile);
						
						// if Staff of Y'Kir
						else if((id==38||id==39) && (unit instanceof Avatar)) this.highlightRed(out, tile);
					}
				}
			}
		}
	}
	
	//highlight a given tile red and set mode accordingly
	private void highlightRed(ActorRef out, Tile tile) {
		//Only highlight if Humans turn
		if (gameState.getCurrentPlayer() instanceof HumanPlayer)
			BasicCommands.drawTile(out, tile, 2);
		tile.setMode(2);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//highlight a given tile white and set mode accordingly
	private void highlightWhite(ActorRef out, Tile tile) {
		//Only highlight if Humans turn
		if (gameState.getCurrentPlayer() instanceof HumanPlayer)
			BasicCommands.drawTile(out, tile, 1);
		tile.setMode(1);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//highlight a given tile grey and set mode accordingly
	private void highlightGrey(ActorRef out, Tile tile) {
		BasicCommands.drawTile(out, tile, 0);
		tile.setMode(0);
		try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
	}

	//Return an ArrayList of all tiles adjacent to a given Tile, if they exist
	public ArrayList<Tile> getAdjacentTiles(Tile tile) {
		int x = tile.getTilex();
		int y = tile.getTiley();
		ArrayList<Tile> adjacent = new ArrayList<Tile>(8);
		if(clickedTile(x-1, y) != null)
			adjacent.add(clickedTile(x - 1, y)); // Left
		if(clickedTile(x+1, y) != null)
			adjacent.add(clickedTile(x + 1, y)); // Right
		if(clickedTile(x-1,y-1) != null)
			adjacent.add(clickedTile(x - 1, y - 1)); // Top Left
		if(clickedTile(x, y-1) != null)
			adjacent.add(clickedTile(x, y - 1)); // Top
		if(clickedTile(x+1, y-1) != null)
			adjacent.add(clickedTile(x + 1, y - 1)); // Top Right
		if(clickedTile(x-1, y+1) != null)
			adjacent.add(clickedTile(x - 1, y + 1)); // Bottom Left
		if(clickedTile(x, y+1) != null)
			adjacent.add(clickedTile(x, y + 1)); // Bottom
		if(clickedTile(x+1, y+1) != null)
			adjacent.add(clickedTile(x + 1, y + 1)); // Bottom Right
		return adjacent;
	}
	
	public ArrayList<Tile> getAdjacentWhiteTiles(Tile tile) {
		int x = tile.getTilex();
		int y = tile.getTiley();
		ArrayList<Tile> tiles = getAdjacentTiles(tile);
		ArrayList<Tile> whiteTiles = new ArrayList<Tile>();
		for(Tile t : tiles) {
			if(t.getMode() == 1)
				whiteTiles.add(t);
		}
		
		return whiteTiles;
	}

	//Highlight all possible summoning tiles for non-airdrop units
	//Get all tiles adjacent to friendly units which dont have an enemy on them
	public void checkTileSummon(ActorRef out, boolean friendlyHighlight) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				PlayableUnit unit = (PlayableUnit) clickedTile(i, j).getUnit();
				if (unit != null) {
					if (unit.isFriendly() == friendlyHighlight) {
						ArrayList<Tile> toChange = getAdjacentTiles(clickedTile(i, j));
						for (Tile tile : toChange) {
							if (tile != null && tile.getUnit() == null && tile.getMode() != 1) {
								highlightWhite(out, tile);
							}
						}
						try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();} //Stop game crashing with lots of units
					}
				}
			}
		}
	}
	
	//Highlight all tiles with no unit
	public void highlightAllEmptyTilesWhite(ActorRef out) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (clickedTile(i, j).getUnit() == null) {
					highlightWhite(out, clickedTile(i, j));
				}
			}
		}
	}

	//Return an ArrayList containing all friendly units
	public ArrayList<PlayableUnit> getAllFriendlyUnits() {
		ArrayList<PlayableUnit> friendlyUnits = new ArrayList<PlayableUnit>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				PlayableUnit unit = (PlayableUnit) clickedTile(i, j).getUnit();
				if (unit != null) {
					if (unit.isFriendly()) {
						friendlyUnits.add(unit);
					}
				}
			}
		}
		return friendlyUnits;
	}

	//Return an ArrayList containing all enemy units
	public ArrayList<PlayableUnit> getAllEnemyUnits() {
		ArrayList<PlayableUnit> enemyUnits = new ArrayList<PlayableUnit>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				PlayableUnit unit = (PlayableUnit) clickedTile(i, j).getUnit();
				if (unit != null) {
					if (!unit.isFriendly()) {
						enemyUnits.add(unit);
					}
				}
			}
		}
		return enemyUnits;
	}
	
	//Return a boolean depending on if a unit should move horizontally or vertically first when moving diagonally
	public boolean yFirst(ActorRef out, Tile tile) {
		Tile rightTile = clickedTile(tile.getTilex() + 1, tile.getTiley());
		Tile leftTile = clickedTile(tile.getTilex() - 1, tile.getTiley());
		PlayableUnit unit = (PlayableUnit) tile.getUnit();
		deHighlightTiles(out); //Stop all tile highlighting to undo previous selected units
		
		//Tests to check whether to move horizontal or vertical first
		if (rightTile != null) {
			PlayableUnit rightUnit = (PlayableUnit) rightTile.getUnit();
			if (rightUnit != null) {
				if (differentPlayers(unit, rightUnit)) {
					return true; //Right tile exists and has an enemy unit
				}
			}
		}
		if (leftTile != null) {
			PlayableUnit leftUnit = (PlayableUnit) leftTile.getUnit();
			if (leftUnit != null) {
				if(differentPlayers(unit, leftUnit)) {
					return true; //left tile exists and has an enemy unit
				}
			}
		}
		return false;
	}
		
	/*given an array of Tiles, this method saves those tiles which are highlighted in white
	 * this is later used in the CardClicked and TileClicked classes used by AI for highlighting tiles in white
	 */
	public void saveAllWhiteHighlightedTiles(ArrayList<Tile> array) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if(board[i][j].getMode() == 1)
					array.add(board[i][j]);
			}			
		}
	}
	
	/*given an array of Tiles, this method saves those tiles which are highlighted in red
	 * this is later used in the CardClicked and TileClicked classes used by AI for highlighting tiles in red
	 */
	public void saveAllRedHighlightedTiles(ArrayList<Tile> array){
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if(board[i][j].getMode() == 2)
					array.add(board[i][j]);
			}			
		}
	}
	
	//Remove tiles which cannot be moved to due to provoke units
	public ArrayList<Tile> removeProvokeAdjacentTiles(ArrayList<Tile> tiles, PlayableUnit unit) {
		System.out.println("Removing impossible tiles due to provoke");
		for (int i = 0; i < tiles.size(); i++) {
			ArrayList<PlayableUnit> provokes = getAdjacentProvokeUnits(tiles.get(i), unit);

			if (provokes.size() > 0) {
				System.out.println("Removing tile " + tiles.get(i));
				tiles.remove(tiles.get(i));
				i--;
			}
		}
		
		return tiles;
	}
	
}