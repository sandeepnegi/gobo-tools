package gobo.controller.restore;

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

		final String ssKey = asString("ssKey");
		final String[] wsTitles = request.getParameterValues("wsTitleArray");
		final String token = sessionScope("token");

		// コントロールテーブルを用意
		Transaction tx = null;
		try {
			tx = Datastore.beginTransaction();

			// コントロールテーブルを準備
			Key controlId = Datastore.allocateId("restore");
			List<GbControl> list = new ArrayList<GbControl>();
			for (int i = 0; i < wsTitles.length; i++) {
				GbControl control = new GbControl();
				Key childKey = Datastore.createKey(controlId, GbControl.class, wsTitles[i]);
				control.setKey(childKey);
				control.setKindName(wsTitles[i]);
				control.setCount(2); // ignore header!
				control.setAuthSubToken(token);
				control.setSsKey(ssKey);
				control.setDate(new Date());
				list.add(control);

				// タスクチェーンをワークシートごとにパラレルで起動
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder.url("/tasks/restore").param(
					"controlKey",
					Datastore.keyToString(childKey)).method(Method.GET));
			}
			Datastore.put(tx, list);
			Datastore.commit(tx);

		} catch (Exception e) {
			Datastore.rollback(tx);
			throw e;
		}

		return redirect("started");
	}
}
