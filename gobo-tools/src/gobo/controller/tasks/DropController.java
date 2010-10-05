package gobo.controller.tasks;

import gobo.model.GbControl;

import java.util.Date;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class DropController extends Controller {

	final Integer RANGE = 5;

	@Override
	protected Navigation run() throws Exception {

		final Key controlId = asKey("controlId");
		final String kind = asString("kind");
		final Integer rowNum = asInteger("rowNum");
		System.out.println("dump kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		List<Key> keyList = Datastore.query(kind).limit(RANGE).asKeyList();
		if ((keyList == null) || (keyList.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/dropEnd").param(
				"controlId",
				Datastore.keyToString(controlId)).param("kind", kind).method(Method.GET));
			return null;
		}

		// Delete rows.
		Datastore.delete(keyList);

		// Update the control table.
		Key childKey = Datastore.createKey(controlId, GbControl.class, kind);
		GbControl control = Datastore.get(GbControl.class, childKey);
		control.setCount(rowNum);
		control.setDate(new Date());
		Datastore.put(control);

		// Call the next chain.
		final String nextRuwNum = String.valueOf(rowNum + RANGE);
		queue.add(TaskOptions.Builder
			.url("/tasks/drop")
			.param("controlId", Datastore.keyToString(controlId))
			.param("kind", kind)
			.param("rowNum", nextRuwNum)
			.method(Method.GET));

		return null;
	}

}
