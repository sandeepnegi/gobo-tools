package gobo.controller.drop;

import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;

import org.junit.Test;

public class StartedControllerTest extends TestBase {

	@Test
	public void singleTaskTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/drop/started");
		assertNotNull(run);
	}
}
