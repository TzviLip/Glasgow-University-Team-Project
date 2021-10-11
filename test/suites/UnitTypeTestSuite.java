package suites;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import tests.TestAttackTwiceUnit;
import tests.TestOnDeathUnit;
import tests.TestUnitTypeAirdrop;
import tests.TestUnitTypeFlying;
import tests.TestUnitTypeProvoke;
import tests.TestUnitTypeRanged;
import tests.TestUnitTypeSummon;
import tests.TestUnitTypeTrigDamage;

/*
 * TO RUN: .\sbt "testOnly *UnitTypeTestSuite"
 * Note: TestUnitTypeTrigDamage requires action before running - see class for details
 * it is currently ignored
 */

@RunWith(Suite.class)

@SuiteClasses({
   TestUnitTypeRanged.class,
   TestUnitTypeSummon.class,
   TestUnitTypeTrigDamage.class,
   TestUnitTypeProvoke.class,
   TestUnitTypeFlying.class,
   TestUnitTypeAirdrop.class,
   TestOnDeathUnit.class,
   TestAttackTwiceUnit.class
})

/*
 * This test suite runs all tests pertaining to unit types e.g. ranged unit or 'upon summon' unit.
 */

public class UnitTypeTestSuite {   
}  