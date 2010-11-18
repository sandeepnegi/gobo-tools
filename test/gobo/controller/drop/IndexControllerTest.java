package gobo.controller.drop;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import gobo.ControllerTester;
import gobo.TestBase;

public class IndexControllerTest extends TestBase {

	@SuppressWarnings("unchecked")
	@Test
	public void runWithoutDataTest() throws Exception {

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/drop/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertThat(list.size(), is(0));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void runWithDataTest() throws Exception {

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity(KeyFactory.createKey("TestKind", 1));
		ds.put(entity);

		ControllerTester tester = new ControllerTester();
		String run = tester.start("/drop/index");
		assertNotNull(run);
		List<Map<String, Object>> list = (List) tester.request.getAttribute("list");
		assertThat(list.size(), is(1));
		
		Map row = list.get(0);
		assertThat(row.get("name").toString(), equalTo("TestKind"));
		assertThat(row.get("count").toString(), equalTo("1"));
	}

}
