package tests;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import extensions.PlayableUnit;
import extensions.OnDeathUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestOnDeathUnit {

    @Test
    public void test() {

        /* check that units with ID in {34 and 35} (Windshrike) are OnDeathUnits*/
        GameState gameState = new GameState();
        UnitFactory factory = new UnitFactory(gameState);
        ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>();


        PlayableUnit test3 = factory.makeUnit(34, null);
        testCases.add(test3);
        PlayableUnit test4 = factory.makeUnit(35, null);
        testCases.add(test4);

        for ( PlayableUnit u : testCases) assertTrue (u instanceof OnDeathUnit);
    }
}