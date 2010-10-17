package gobo.controller.restore;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.ControllerTester;
import gobo.TestBase;
import gobo.util.SpreadsheetUtil;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class IndexControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@SuppressWarnings("unchecked")
	@Test
	public void runTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/restore/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertNull(list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void runAuthWithDataTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

		String[] kinds = new String[] { "TestKind1" };
		final SpreadsheetEntry spreadsheet = SpreadsheetUtil.createSpreadsheet(authSubToken, kinds);

		String run = tester.start("/restore/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertThat(list.size(), not(0));

		SpreadsheetUtil.deleteSpreadsheet(authSubToken, spreadsheet);
	}

}
