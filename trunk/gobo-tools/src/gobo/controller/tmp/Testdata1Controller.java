package gobo.controller.tmp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

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
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;

public class Testdata1Controller extends Controller {

	@SuppressWarnings("unchecked")
	@Override
	protected Navigation run() throws Exception {

		Integer count = asInteger("count");
		if (count == null) {
			count = 0;
		}
		if (count >= 100) {
			return null;
		}

		Integer max = asInteger("max");
		if (max == null) {
			max = 100;
		}

		String kind = asString("kind");
		if (kind == null) {
			kind = "Test";
		}

		List list = new ArrayList();
		List keyList = new ArrayList();
		for (int i = 0; i < max; i++) {

			Entity entity = new Entity(kind);
			entity.setProperty("String", RandomStringUtils.randomAlphabetic(10));
			entity.setProperty("Integer", new Integer(RandomUtils.nextInt()));
			entity.setProperty("Short", new Short(RandomStringUtils.randomNumeric(1)));
			entity.setProperty("Long", new Long(RandomUtils.nextLong()));
			entity.setProperty("Boolean", new Boolean(RandomUtils.nextBoolean()));
			entity.setProperty("Float", new Float(RandomUtils.nextFloat()));
			entity.setProperty("Double", new Double(RandomUtils.nextDouble()));
			entity.setProperty("Date", new Date(RandomUtils.nextLong()));
			entity.setProperty("User", new User("test@example", "google.com"));
			entity.setProperty("Key", Datastore.createKey("test", RandomStringUtils
				.randomAlphabetic(5)));
			entity.setProperty("Category", new Category(RandomStringUtils.randomAlphabetic(3)));
			entity.setProperty("Email", new Email("test@example"));
			entity.setProperty("GeoPt", new GeoPt(new Float(new Integer(RandomStringUtils
				.randomNumeric(2)) - 9), new Float(
				new Integer(RandomStringUtils.randomNumeric(2)) - 9)));
			entity.setProperty("IMHandle", new IMHandle(Scheme.valueOf("sip"), RandomStringUtils
				.randomAlphabetic(2)));
			entity.setProperty("Link", new Link("test"));
			entity.setProperty("PhoneNumber", new PhoneNumber(RandomStringUtils.randomNumeric(11)));
			entity.setProperty("PostalAddress", new PostalAddress(RandomStringUtils
				.randomNumeric(7)));
			entity.setProperty("Rating", new Rating(Integer.parseInt(RandomStringUtils
				.randomNumeric(2))));

			List<String> coll = new ArrayList<String>();
			coll.add(RandomStringUtils.randomAlphabetic(3));
			coll.add(RandomStringUtils.randomAlphabetic(3));
			coll.add(RandomStringUtils.randomAlphabetic(3));
			entity.setProperty("List", coll);

			Set<String> coll2 = new HashSet<String>();
			coll2.add(RandomStringUtils.randomAlphanumeric(3));
			coll2.add(RandomStringUtils.randomAlphanumeric(3));
			coll2.add(RandomStringUtils.randomAlphanumeric(3));
			entity.setProperty("Set", coll2);

			SortedSet<Integer> coll3 = new TreeSet<Integer>();
			coll3.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll3.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll3.add(new Integer(RandomStringUtils.randomNumeric(5)));
			entity.setProperty("SortedSet", coll3);

			// Make Null Property
			//if (i != 0) {
				if (keyList.size() == 0) {
					Iterator<String> iterator = entity.getProperties().keySet().iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
						keyList.add(key);
					}
				}
				int index = RandomUtils.nextInt(keyList.size());
				entity.removeProperty((String) keyList.get(index));
			//}
			
			list.add(entity);

		}
		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		service.put(list);

		Queue queue = QueueFactory.getDefaultQueue();
		// Call the next chain.
		queue.add(TaskOptions.Builder.url(request.getRequestURI()).param(
			"count",
			String.valueOf(count + 1)).param("max", max.toString()).param("kind", kind).method(
			Method.GET));

		return null;
	}
}
