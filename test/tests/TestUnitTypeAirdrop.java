package tests;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
//import org.junit.Ignore;
import org.junit.Test;

import events.CardClicked;
import extensions.PlayableUnit;
import extensions.UnitFactory;
import structures.GameState;

public class TestUnitTypeAirdrop {

//	@Ignore
	@Test
	public void test() {

		/* check that units with ID in {10,11,26,27} (Ironcliff Guardian & Planar Scout) have airdrop */;
		ArrayList<Integer> IDs = new ArrayList<>();
		
		IDs.add(10); IDs.add(11); IDs.add(26); IDs.add(27);
		
		/* check for airdrop is in card clicked as ability applies when it is a card not a unit */
		for (Integer id : IDs) assertTrue (CardClicked.hasAirdrop(id));
	}
}