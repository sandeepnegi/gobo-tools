package gobo.controller.drop;

import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.TaskQueueUtil;
import gobo.util.TestDataUtil;

import org.junit.Test;

public class StartControllerTest extends TestBase {

	@Test
	public void runSingleTaskTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		tester.request.setParameter("kindArray", TestDataUtil.TEST_KIND);

		String run = tester.start("/drop/start");
		assertNotNull(run);
		
		TaskQueueUtil.removeTasks();
	}

	@Test
	public void runMoreThan5TasksTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String[] kinds =
			{
				"Kind1",
				"Kind2",
				"Kind3",
				"kind4",
				"Kind5",
				"Kind6",
				"Kind7",
				"Kind8",
				"Kind9",
				"Kind10" };
		tester.request.setParameter("kindArray", kinds);

		String run = tester.start("/drop/start");
		assertNotNull(run);
		
		TaskQueueUtil.removeTasks();
	}
}
