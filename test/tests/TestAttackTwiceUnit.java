package tests;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import extensions.PlayableUnit;
import extensions.AttackTwiceUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestAttackTwiceUnit {

    @Test
    public void test() {

        /* check that units with ID in {32 and 33} (Serpenti) are AttackTwiceUnits */
        GameState gameState = new GameState();
        UnitFactory factory = new UnitFactory(gameState);
        ArrayList<PlayableUnit> testCases = new ArrayList<PlayableUnit>();


        PlayableUnit test3 = factory.makeUnit(32, null);
        testCases.add(test3);
        PlayableUnit test4 = factory.makeUnit(33, null);
        testCases.add(test4);

        for ( PlayableUnit u : testCases) assertTrue (u instanceof AttackTwiceUnit);
    }
}