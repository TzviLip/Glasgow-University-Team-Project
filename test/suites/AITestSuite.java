package suites;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import tests.TestAIDrawCard;
import tests.TestAIGetHighestPlayer;
import tests.TestAIPlayCard;


/*
 * TO RUN: .\sbt "testOnly *AITestSuite"
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
   TestAIDrawCard.class,
   TestAIGetHighestPlayer.class,
   TestAIPlayCard.class
})

/* 
 * This test suite runs all tests pertaining to AI functionality as this is more difficult to trace in the game
 */

public class AITestSuite {

}
