package gobo.controller.tasks;

import gobo.TaskQueueBase;
import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.model.GbControl;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class DumpController extends TaskQueueBase {

	final Integer RANGE = 25;

	private static final Logger logger = Logger.getLogger(DumpController.class.getName());

	@Override
	protected String runTask() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity control = datastore.get(controlKey);
		final String ssKey = (String) control.getProperty(GbControl.SPREADSHEET_KEY);
		final String kind = (String) control.getProperty(GbControl.KIND_NAME);
		String tableId = (String) control.getProperty(GbControl.TABLE_ID);
		final String cursor = (String) control.getProperty(GbControl.CURSOR);
		final Long rowNum = (Long) control.getProperty(GbControl.COUNT);
		final String token = (String) control.getProperty(GbControl.AUTH_SUB_TOKEN);
		logger.info("Dump:kind=" + kind + ":rowNum=" + rowNum);
		Queue queue = QueueFactory.getDefaultQueue();

		// Prepare table only at first chain.
		GbSpreadsheetService spreadsheetService = new GbSpreadsheetService(token);
		if (cursor == null) {
			List<GbProperty> properties = GbDatastoreService.getProperties(kind);
			spreadsheetService.updateWorksheetSize(ssKey, kind, properties.size());
			tableId = spreadsheetService.createTableInWorksheet(ssKey, kind, properties);
		}

		// Get data from datastore.
		PreparedQuery query = datastore.prepare(new Query(kind));
		FetchOptions fetchOptions = null;
		if (cursor == null) {
			fetchOptions = FetchOptions.Builder.withLimit(RANGE);
		} else {
			fetchOptions =
				FetchOptions.Builder.withStartCursor(Cursor.fromWebSafeString(cursor)).limit(RANGE);
		}
		QueryResultList<Entity> data = query.asQueryResultList(fetchOptions);

		// Call the last chain.
		if ((data == null) || (data.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/dumpEnd.gobo").param(
				"controlKey",
				KeyFactory.keyToString(controlKey)).method(Method.GET));
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
		control.setProperty(GbControl.CURSOR, data.getCursor().toWebSafeString());
		control.setProperty(GbControl.TABLE_ID, tableId);
		control.setProperty(GbControl.COUNT, rowNum + RANGE);
		control.setProperty(GbControl.UPDATE_DATE, new Date());
		datastore.put(control);

		// Call the next chain.
		queue.add(TaskOptions.Builder.url("/tasks/dump.gobo").param(
			"controlKey",
			KeyFactory.keyToString(controlKey)).method(Method.GET));

		return null;
	}
}
