package javax.time;

import org.testng.TestNG;

public class AllTCK {

	public static void main(String[] args) {
		TestNG testNG = AllTest.getTestSuite();
		testNG.setGroups("tck");
		testNG.run();
	}
}
