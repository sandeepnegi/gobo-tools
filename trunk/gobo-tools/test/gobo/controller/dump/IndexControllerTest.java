package gobo.controller.dump;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import gobo.ControllerTester;
import gobo.TestBase;

public class IndexControllerTest extends TestBase {

	ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	final String authSubToken = bundle.getString("token");

	@SuppressWarnings("unchecked")
	@Test
	public void runTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/dump/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertNull(list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void runAuthWithoutDataTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

		String run = tester.start("/dump/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertThat(list.size(), is(0));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void runAuthWithDataTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		HttpSession session = tester.request.getSession(true);
		session.setAttribute("token", authSubToken);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity(KeyFactory.createKey("TestKind", 1));
		ds.put(entity);

		String run = tester.start("/dump/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertThat(list.size(), is(1));

		Map row = list.get(0);
		assertThat(row.get("name").toString(), equalTo("TestKind"));
		assertThat(row.get("count").toString(), equalTo("1"));
	}
}
