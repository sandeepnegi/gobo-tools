package gobo.controller.drop;

import gobo.model.GbControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class StartController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final String[] kinds = request.getParameterValues("kindArray");

		Transaction tx = null;
		try {
			tx = Datastore.beginTransaction();

			// Prepare control table.
			Key controlId = Datastore.allocateId("drop");
			List<GbControl> list = new ArrayList<GbControl>();
			for (int i = 0; i < kinds.length; i++) {
				GbControl control = new GbControl();
				Key childKey = Datastore.createKey(controlId, GbControl.class, kinds[i]);
				control.setKey(childKey);
				control.setKindName(kinds[i]);
				control.setCount(0);
				control.setDate(new Date());
				list.add(control);
			}
			Datastore.put(tx, list);

			// Call the "task chain" for each kind
			for (int i = 0; i < kinds.length; i++) {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder
					.url("/tasks/drop")
					.param("controlId", Datastore.keyToString(controlId))
					.param("kind", kinds[i])
					.param("rowNum", "0")
					.method(Method.GET));
			}
			Datastore.commit(tx);

		} catch (Exception e) {
			Datastore.rollback(tx);
			throw e;
		}

		return redirect("started");
	}

}
