package gobo.controller.tasks;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.meta.GbControlMeta;
import gobo.model.GbControl;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

	final Integer RANGE = 100;

	@Override
	protected Navigation run() throws Exception {

		final Key controlKey = asKey("controlKey");
		GbControl gbControl = Datastore.get(new GbControlMeta(), controlKey);
		final String ssKey = gbControl.getSsKey();
		final String kind = gbControl.getKindName();
		final String tableId = gbControl.getTableId();
		final String cursor = gbControl.getCursor();
		final Integer rowNum = gbControl.getCount();
		final String token = gbControl.getAuthSubToken();
		System.out.println("dump kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();
		// final String retryCount =
		// request.getHeader("X-AppEngine-TaskRetryCount");

		// Prepare table only at first chain.
		GbSpreadsheetService spreadsheetService = new GbSpreadsheetService(token);
		if (cursor == null) {
			List<GbProperty> properties = GbDatastoreService.getProperties(kind);
			spreadsheetService.updateWorksheetSize(ssKey, kind, properties.size());
			spreadsheetService.createTableInWorksheet(ssKey, kind, properties);
		}

		// Get data from datastore.
		EntityQuery query = Datastore.query(kind);
		if (cursor != null) {
			query = query.encodedStartCursor(cursor);
		}
		QueryResultList<Entity> data = query.limit(RANGE).asQueryResultList();

		// Call the last chain.
		if ((data == null) || (data.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/dumpEnd").param(
				"controlKey",
				Datastore.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Re-package.
		List<GbEntity> list = new ArrayList<GbEntity>();
		for (Entity entity : data) {
			GbEntity gbEntity = new GbEntity();
			gbEntity.setKey(entity.getKey());
			Set<String> propNames = entity.getProperties().keySet();
			for (String propName : propNames) {
				GbProperty gbProperty = new GbProperty();
				gbProperty.setName(propName);
				gbProperty.setValue(entity.getProperty(propName));
				gbEntity.addProperty(gbProperty);
			}
			list.add(gbEntity);
		}

		// Add to Spreadsheet.
		spreadsheetService.dumpData(ssKey, kind, tableId, list);

		// Update the control table.
		gbControl.setCursor(data.getCursor().toWebSafeString());
		gbControl.setCount(rowNum + RANGE);
		gbControl.setDate(new Date());
		Datastore.put(gbControl);

		// Call the next chain.
		queue.add(TaskOptions.Builder.url("/tasks/dump").param(
			"controlKey",
			Datastore.keyToString(controlKey)).method(Method.GET));

		return null;
	}
}
