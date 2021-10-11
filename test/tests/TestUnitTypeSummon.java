package tests;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


import extensions.PlayableUnit;
import extensions.SummonUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestUnitTypeSummon {

//	@Ignore 
	@Test
	public void test() {
			
			/* check that units with ID in {0,1,14,15} are units with special 'upon summon' ability */
			GameState gameState = new GameState();
			UnitFactory factory = new UnitFactory(gameState);
			ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>();
			
			/* ID=0,1 - Azure Herald*/
			PlayableUnit test1 = factory.makeUnit(0, null);
			testCases.add(test1);
			PlayableUnit test2 = factory.makeUnit(1, null);
			testCases.add(test2);
			
			/* ID=20,21 - Blaze Hound*/
			PlayableUnit test3 = factory.makeUnit(20, null);
			testCases.add(test3);
			PlayableUnit test4 = factory.makeUnit(21, null);
			testCases.add(test4);

			for ( PlayableUnit u : testCases) assertTrue (u instanceof SummonUnit);
		}
	}