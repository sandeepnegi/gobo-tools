package gobo.controller;

import static org.junit.Assert.*;

import org.junit.Test;

import gobo.ControllerTester;
import gobo.TestBase;

public class IndexControllerTest extends TestBase {

	@Test
	public void runTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/index");
		assertNotNull(run);
	}
}
