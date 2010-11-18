package gobo.controller.tasks;

import gobo.TaskQueueBase;
import gobo.dto.GbEntity;
import gobo.dto.GbEntityList;
import gobo.dto.GbProperty;
import gobo.model.GbControl;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class DumpController extends TaskQueueBase {

	final Integer RANGE = 5;

	private static final Logger logger = Logger.getLogger(DumpController.class.getName());

	@Override
	protected String runTask() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity control = datastore.get(controlKey);
		final String ssKey = (String) control.getProperty(GbControl.SPREADSHEET_KEY);
		final String kind = (String) control.getProperty(GbControl.KIND_NAME);
		String tableId = (String) control.getProperty(GbControl.TABLE_ID);
		final String _cursor = (String) control.getProperty(GbControl.CURSOR);
		final Long rowNum = (Long) control.getProperty(GbControl.COUNT);
		final String token = (String) control.getProperty(GbControl.AUTH_SUB_TOKEN);
		final GbSpreadsheetService gss = new GbSpreadsheetService(token);
		logger.info("Dump:kind=" + kind + ":rowNum=" + rowNum);

		// Prepare table only at first chain.
		final Cursor cursor = (_cursor == null) ? null : Cursor.fromWebSafeString(_cursor);
		if (cursor == null) {
			List<GbProperty> properties = GbDatastoreService.getProperties(kind);
			tableId = gss.prepareWorksheet(ssKey, kind, properties);
		}

		// Get data from datastore.
		GbEntityList<GbEntity> list = GbDatastoreService.getData(cursor, kind, RANGE);

		// Call the last chain.
		Queue queue = QueueFactory.getDefaultQueue();
		if ((list == null) || (list.size() == 0)) {
			queue.add(TaskOptions.Builder.url("/tasks/dumpEnd.gobo").param(
				"controlKey",
				KeyFactory.keyToString(controlKey)).method(Method.GET));
			return null;
		}

		// Add to Spreadsheet.
		gss.dumpData(ssKey, kind, tableId, list, retry);

		// Update the control table.
		control.setProperty(GbControl.CURSOR, list.getCursor().toWebSafeString());
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
