package gobo.controller.tasks;

import gobo.meta.GbControlMeta;
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

	final Integer RANGE = 100;

	@Override
	protected Navigation run() throws Exception {

		final Key controlKey = asKey("controlKey");
		GbControl control = Datastore.get(new GbControlMeta(), controlKey);
		final String kind = control.getKindName();
		final Integer rowNum = control.getCount();
		System.out.println("drop kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		List<Key> keyList = Datastore.query(kind).limit(RANGE).asKeyList();
		if ((keyList == null) || (keyList.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/dropEnd").param(
				"controlKey",
				Datastore.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Delete rows.
		Datastore.delete(keyList);

		// Update the control table.
		control.setCount(rowNum + RANGE);
		control.setDate(new Date());
		Datastore.put(control);

		// Call the next chain.
		queue.add(TaskOptions.Builder.url("/tasks/drop").param(
			"controlKey",
			Datastore.keyToString(controlKey)).method(Method.GET));

		return null;
	}

}
