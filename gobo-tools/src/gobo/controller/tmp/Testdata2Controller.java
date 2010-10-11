package gobo.controller.tmp;

import gobo.ControllerBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class Testdata2Controller extends ControllerBase {

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

			// Byte, Blob, Text, ShortBlob, BlobKey
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
