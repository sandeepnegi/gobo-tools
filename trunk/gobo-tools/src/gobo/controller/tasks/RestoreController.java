package gobo.controller.tasks;

import gobo.model.Control;
import gobo.util.SpreadsheetUtil;

import java.util.ArrayList;
import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
		List<Entity> list = new ArrayList<Entity>();
		for (int row = 1; row < data.length; row++) {

			// parsing Key
			String keyValue = data[row][0];
			Entity entity = null;
			if ((keyValue == null) || (keyValue.length() == 0)) {
				entity = new Entity(wsTitle);
			} else {
				String[] keypaths = keyValue.split("/");
				Key key = null;
				for (String path : keypaths) {
					String[] split = path.split("[()]");
					String kind = split[0];
					String keyVal = split[1];
					if (keyVal.startsWith("\"")) {
						keyVal = keyVal.replaceAll("\"", "");
						key = KeyFactory.createKey(key, kind, keyVal);
					} else {
						key = KeyFactory.createKey(key, kind, Integer.parseInt(keyVal));
					}
				}
				entity = new Entity(key);
			}

			// Properties
			for (int col = 1; col < data[row].length; col++) {
				String value = data[row][col];
				entity.setProperty(data[0][col], value);
			}
			System.out.println(entity);
			list.add(entity);
		}
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		datastoreService.put(list);

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
