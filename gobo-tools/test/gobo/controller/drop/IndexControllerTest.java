package gobo.controller.drop;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import gobo.ControllerTester;
import gobo.TestBase;

public class IndexControllerTest extends TestBase {

	@SuppressWarnings("unchecked")
	@Test
	public void runTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		
		String run = tester.start("/drop/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		for(Map row : list) {
			assertNotNull(row.get("name"));
			assertNotNull(row.get("count"));
		}
	}
}
