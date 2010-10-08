package other;

import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.AppEngineTestCase;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.IMHandle.Scheme;
import com.google.appengine.api.users.User;
import com.google.gdata.util.ServiceException;

public class ValueTypeTest extends AppEngineTestCase {

	@Test
	public void test() throws IOException, ServiceException {

		Entity entity = new Entity("test");
		entity.setProperty("String", "AAA");
		entity.setProperty("Integer", 1);
		entity.setProperty("Short", Short.valueOf("1"));
		entity.setProperty("Long", Long.valueOf("1"));
		entity.setProperty("Boolean", Boolean.valueOf("true"));
		entity.setProperty("Float", Float.valueOf("1"));
		entity.setProperty("Double", Double.valueOf("1"));
		entity.setProperty("Date", new Date());
		entity.setProperty("User", new User("test@example", "google.com"));
		entity.setProperty("Key", Datastore.createKey("test", 1));
		entity.setProperty("Category", new Category("test"));
		entity.setProperty("Email", new Email("test@example"));
		entity.setProperty("GeoPt", new GeoPt(1, 1));
		entity.setProperty("IMHandle", new IMHandle(Scheme.valueOf("sip"), "test"));
		entity.setProperty("Link", new Link("test"));
		entity.setProperty("PhoneNumber", new PhoneNumber("000"));
		entity.setProperty("PostalAddress", new PostalAddress("123"));
		entity.setProperty("Rating", new Rating(0));

		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		service.put(entity);

	}
	
	@After
	public void tearDown() throws Exception {
	}
}
