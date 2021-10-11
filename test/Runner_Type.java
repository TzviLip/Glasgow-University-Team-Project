import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import suites.UnitTypeTestSuite;

public class Runner_Type {

	public void main(String[] args) {
		Result result = JUnitCore.runClasses(UnitTypeTestSuite.class);
		for (Failure f : result.getFailures()) {
			System.out.println(f.toString());
		}
		System.out.println(result.wasSuccessful());
	}
}