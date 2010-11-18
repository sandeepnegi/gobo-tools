package gobo.controller.tmp;

import gobo.ControllerBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.IMHandle.Scheme;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;

public class TestdataController extends ControllerBase {

	private static final int BATCH_PUT_SIZE = 100;

	@SuppressWarnings("unchecked")
	@Override
	protected String run() throws Exception {

		Integer count = asInteger("count");
		if (count == null) {
			count = 0;
		}

		Integer max = asInteger("max");
		if (max == null) {
			max = 1000;
		}
		if ((count * BATCH_PUT_SIZE) >= max) {
			return null;
		}

		String kind = asString("kind");
		if (kind == null) {
			kind = "Test";
		}

		List list = new ArrayList();
		List keyList = new ArrayList();
		for (int i = 0; i < BATCH_PUT_SIZE; i++) {

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
			entity.setProperty("Key", KeyFactory.createKey("test", RandomStringUtils
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
			entity.setProperty("List<String>", coll);

			List<Integer> coll2 = new ArrayList<Integer>();
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			entity.setProperty("List<Integer>", coll2);

			entity.setProperty("Byte", new Byte(
				RandomStringUtils.randomAlphanumeric(10).getBytes()[0]));
			entity.setProperty("Blob", new Blob(RandomStringUtils
				.randomAlphanumeric(1000)
				.getBytes()));
			entity.setProperty("Text", new Text(RandomStringUtils.randomAlphanumeric(1000)));
			entity.setProperty("ShortBlob", new ShortBlob(RandomStringUtils
				.randomAlphanumeric(500)
				.getBytes()));
			entity.setProperty("BlobKey", new BlobKey(RandomStringUtils.randomAlphanumeric(10)));

			// Make Null Property
			if (keyList.size() == 0) {
				Iterator<String> iterator = entity.getProperties().keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					keyList.add(key);
				}
			}
			int index = RandomUtils.nextInt(keyList.size());
			entity.removeProperty((String) keyList.get(index));

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
