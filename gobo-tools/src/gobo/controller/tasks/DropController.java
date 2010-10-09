package gobo.controller.tasks;

import gobo.ControllerBase;
import gobo.model.GbControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class DropController extends ControllerBase {

	final Integer RANGE = 100;

	@Override
	protected String run() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity control = datastore.get(controlKey);
		final String kind = (String) control.getProperty(GbControl.KIND_NAME);
		final Long rowNum = (Long) control.getProperty(GbControl.COUNT);
		System.out.println("drop kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		FetchOptions withLimit = FetchOptions.Builder.withLimit(RANGE);
		List<Entity> keyList = datastore.prepare(new Query(kind).setKeysOnly()).asList(withLimit);

		if ((keyList == null) || (keyList.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/DropEnd.gobo").param(
				"controlKey",
				KeyFactory.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Delete rows.
		List<Key> delKeyList = new ArrayList<Key>();
		for (Entity entity : keyList) {
			delKeyList.add(entity.getKey());
		}
		datastore.delete(delKeyList);

		// Update the control table.
		control.setProperty(GbControl.COUNT, rowNum + RANGE);
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		datastore.put(control);

		// Call the next chain.
		queue.add(TaskOptions.Builder.url("/tasks/Drop.gobo").param(
			"controlKey",
			KeyFactory.keyToString(controlKey)).method(Method.GET));

		return null;
	}
}
