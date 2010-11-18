package gobo.controller.restore;

import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.TaskQueueUtil;
import gobo.util.TestDataUtil;

import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.junit.Test;

public class StartControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@Test
	public void runAuthSingleTaskTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

		tester.request.setParameter("wsTitleArray", TestDataUtil.TEST_KIND);
		String run = tester.start("/restore/start");
		assertNotNull(run);

		TaskQueueUtil.removeTasks();
	}

	@Test
	public void runAuthMoreThan5TasksTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

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
		tester.request.setParameter("wsTitleArray", kinds);

		String run = tester.start("/restore/start");
		assertNotNull(run);
		
		TaskQueueUtil.removeTasks();
	}
}
