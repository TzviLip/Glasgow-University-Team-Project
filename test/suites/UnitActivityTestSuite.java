package suites;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import tests.TestAttack;
import tests.TestBattle;
import tests.TestHealing;
import tests.TestMove;

/*
 * TO RUN: .\sbt "testOnly *UnitActivityTestSuite"
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
   TestAttack.class,
   TestBattle.class,
   TestMove.class,
   TestHealing.class
})

/* 
 * This test suite runs all tests pertaining to unit activities such as movement and attack.
 */

public class UnitActivityTestSuite {

}
