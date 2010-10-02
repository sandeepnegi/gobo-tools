package gobo.controller.tasks;

import gobo.model.Control;
import gobo.util.SpreadsheetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityQuery;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class DumpController extends Controller {

	final Integer RANGE = 5;

	@Override
	protected Navigation run() throws Exception {

		final Key controlId = asKey("controlId");
		final String ssKey = asString("ssKey");
		final String kind = asString("kind");
		final String tableId = asString("tableId");
		final String cursor = asString("cursor");
		final Integer rowNum = asInteger("rowNum");
		final String token = asString("token");
		System.out.println("dump kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		// Datastoreからデータを取得
		EntityQuery query = Datastore.query(kind);
		if (cursor != null) {
			query = query.encodedStartCursor(cursor);
		}
		QueryResultList<Entity> data = query.limit(RANGE).asQueryResultList();

		if ((data == null) || (data.size() == 0)) {
			// チェーンの最終タスクを呼んで終了
			queue.add(TaskOptions.Builder.url("/tasks/dumpEnd").param(
				"controlId",
				Datastore.keyToString(controlId)).param("kind", kind).method(Method.GET));
			return null;
		}

		// 詰め替え
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Entity row : data) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Entity.KEY_RESERVED_PROPERTY, row.getKey().toString());
			Set<String> keySet = row.getProperties().keySet();
			for (String key : keySet) {
				map.put(key, row.getProperty(key));
			}
			list.add(map);
		}

		// Spreadsheetに追記
		SpreadsheetUtil service = new SpreadsheetUtil(token);
		service.addTableRow(ssKey, kind, tableId, list);

		// コントロールテーブルを更新
		Key childKey = Datastore.createKey(controlId, Control.class, kind);
		Control control = Datastore.get(Control.class, childKey);
		control.setCount(rowNum);
		Datastore.put(control);

		// タスクチェーンを継続
		final String nextRuwNum = String.valueOf(rowNum + RANGE);
		queue.add(TaskOptions.Builder
			.url("/tasks/dump")
			.param("token", token)
			.param("controlId", Datastore.keyToString(controlId))
			.param("ssKey", ssKey)
			.param("kind", kind)
			.param("tableId", tableId)
			.param("rowNum", nextRuwNum)
			.param("cursor", data.getCursor().toWebSafeString())
			.method(Method.GET));

		return null;
	}
}
