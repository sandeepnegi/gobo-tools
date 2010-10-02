package gobo.controller.tasks;

import gobo.model.Control;
import gobo.util.DatastoreUtil;
import gobo.util.SpreadsheetUtil;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class RestoreController extends Controller {

	final Integer RANGE = 5;

	@Override
	protected Navigation run() throws Exception {

		final Key controlId = asKey("controlId");
		final String ssKey = asString("ssKey");
		final String wsTitle = asString("wsTitle");
		final Integer rowNum = asInteger("rowNum");
		final String token = asString("token");
		System.out.println("Restoring wsTitle=" + wsTitle + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		// Spreadsheetからデータを取得
		SpreadsheetUtil service = new SpreadsheetUtil(token);
		String[][] data = service.getData(ssKey, wsTitle, rowNum + 1, RANGE);
		if (data == null) {
			// チェーンの最終タスクを呼んで終了
			queue.add(TaskOptions.Builder.url("/tasks/restoreEnd").param(
				"controlId",
				Datastore.keyToString(controlId)).param("wsTitle", wsTitle).method(Method.GET));
			return null;
		}

		// Restoring to Datastore.
		DatastoreUtil datastoreUtil = new DatastoreUtil();
		datastoreUtil.restoreData(wsTitle, data);
		
		// コントロールテーブルを更新
		Key childKey = Datastore.createKey(controlId, Control.class, wsTitle);
		Control control = Datastore.get(Control.class, childKey);
		control.setCount(rowNum);
		Datastore.put(control);

		// タスクチェーンを継続
		final String nextRuwNum = String.valueOf(rowNum + RANGE);
		queue.add(TaskOptions.Builder
			.url("/tasks/restore")
			.param("token", token)
			.param("controlId", Datastore.keyToString(controlId))
			.param("ssKey", ssKey)
			.param("wsTitle", wsTitle)
			.param("rowNum", nextRuwNum)
			.method(Method.GET));

		return null;
	}
}