package tests;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;
import structures.basic.Tile;

public class TestMove {

//	@Ignore
	@Test
	public void test() {
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		GameState gameState = new GameState();
		Tile tile = new Tile();
		tile.setTilex(5);
		tile.setTiley(2);
		Tile newTile = new Tile();
		newTile.setTilex(4);
		newTile.setTiley(1);
		
		PlayableUnit attacker = PlayableUnit.playUnit(null, gameState, tile, 0);
		System.out.println(attacker.getCurrentTile());	
		System.out.println("after");
		attacker.move(null, gameState, tile, newTile, false);
		assertTrue (attacker.getCurrentTile() == newTile);
	}
}
