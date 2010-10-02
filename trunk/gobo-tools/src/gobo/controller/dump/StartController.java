package gobo.controller.dump;

import gobo.model.Control;
import gobo.util.SpreadsheetUtil;

import java.util.ArrayList;
import java.util.Arrays;
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
		final String token = sessionScope("token");

		// dump先のspreadsheetを作成
		SpreadsheetUtil su = new SpreadsheetUtil(token);
		final String ssKey = su.createSpreadsheet(Arrays.asList(kinds));
		System.out.println("ssKey=" + ssKey);

		// コントロールテーブルを用意
		Transaction tx = null;
		try {
			tx = Datastore.beginTransaction();

			// コントロールテーブルを準備
			Key controlId = Datastore.allocateId("dump");
			List<Control> list = new ArrayList<Control>();
			for (int i = 0; i < kinds.length; i++) {
				Control control = new Control();
				Key childKey = Datastore.createKey(controlId, Control.class, kinds[i]);
				control.setKey(childKey);
				control.setCount(0);
				list.add(control);
			}
			Datastore.put(tx, list);

			// タスクチェーンをKindごとにパラレルで起動
			for (int i = 0; i < kinds.length; i++) {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder
					.url("/tasks/dump")
					.param("token", token)
					.param("controlId", Datastore.keyToString(controlId))
					.param("ssKey", ssKey)
					.param("kind", kinds[i])
					.param("tableId", String.valueOf(i))
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
