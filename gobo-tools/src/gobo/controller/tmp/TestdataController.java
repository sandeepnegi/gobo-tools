package gobo.controller.tmp;

import java.util.Date;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.IMHandle.Scheme;
import com.google.appengine.api.users.User;

public class TestdataController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		
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
		//entity.setProperty("GeoPt", new GeoPt());
		entity.setProperty("IMHandle", new IMHandle(Scheme.valueOf("sip"), "test"));
		entity.setProperty("Link", new Link("test"));
		entity.setProperty("PhoneNumber", new PhoneNumber("000"));
		entity.setProperty("PostalAddress", new PostalAddress("123"));
		entity.setProperty("Rating", new Rating(0));

		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		service.put(entity);

//		GbDatastoreService gds = new GbDatastoreService();
//		Map<String, Map<String, Object>> kindInfos = gds.getKindInfos();
		return null;
	}
}