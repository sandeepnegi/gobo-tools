package gobo.controller.dump;

import static org.junit.Assert.*;

import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import gobo.ControllerTester;
import gobo.TestBase;

import org.junit.Test;

public class StartedControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@Test
	public void singleTaskTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

		String run = tester.start("/dump/started");
		assertNotNull(run);
	}
}
