package gobo.controller.tasks;

import java.util.Date;
import java.util.List;

import gobo.dto.GbEntity;
import gobo.meta.GbControlMeta;
import gobo.model.GbControl;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;

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

		final Key controlKey = asKey("controlKey");
		GbControl gbControl = Datastore.get(new GbControlMeta(), controlKey);
		final String ssKey = gbControl.getSsKey();
		final String kind = gbControl.getKindName();
		final Integer rowNum = gbControl.getCount();
		final String token = gbControl.getAuthSubToken();
		System.out.println("Restoring kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		// Spreadsheetからデータを取得
		GbSpreadsheetService service = new GbSpreadsheetService(token);
		List<GbEntity> data2 = service.getDataOrNull(ssKey, kind, rowNum + 1, RANGE);

		if (data2 == null) {
			// Call the final task
			queue.add(TaskOptions.Builder.url("/tasks/restoreEnd").param(
				"controlKey",
				Datastore.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Restoring to Datastore.
		GbDatastoreService datastoreUtil = new GbDatastoreService();
		datastoreUtil.restoreData(kind, data2);

		// Update control row.
		gbControl.setCount(rowNum + RANGE);
		gbControl.setDate(new Date());
		Datastore.put(gbControl);

		// タスクチェーンを継続
		queue.add(TaskOptions.Builder.url("/tasks/restore").param(
			"controlKey",
			Datastore.keyToString(controlKey)).method(Method.GET));

		return null;
	}
}
