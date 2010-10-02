package gobo.controller.restore;

import gobo.model.Control;
import gobo.util.SpreadsheetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		final String ssKey = asString("ssKey");
		final String[] wsIDs = request.getParameterValues("wsIDArray");
		final String token = sessionScope("token");

		SpreadsheetUtil service = new SpreadsheetUtil(token);
		Map<String, String> worksheetID2Title = service.worksheetID2Title(ssKey, wsIDs);

		// コントロールテーブルを用意
		Transaction tx = null;
		try {
			tx = Datastore.beginTransaction();

			// コントロールテーブルを準備
			Key controlId = Datastore.allocateId("restore");
			List<Control> list = new ArrayList<Control>();
			for (int i = 0; i < wsIDs.length; i++) {
				Control control = new Control();
				Key childKey = Datastore.createKey(controlId, Control.class, wsIDs[i]);
				control.setKey(childKey);
				control.setCount(0);
				list.add(control);
			}
			Datastore.put(tx, list);

			// タスクチェーンをワークシートごとにパラレルで起動
			for (int i = 0; i < wsIDs.length; i++) {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder
					.url("/tasks/restore")
					.param("token", token)
					.param("controlId", Datastore.keyToString(controlId))
					.param("ssKey", ssKey)
					.param("wsID", wsIDs[i])
					.param("wsTitle", worksheetID2Title.get(wsIDs[i]))
					.param("rowNum", "1")
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
