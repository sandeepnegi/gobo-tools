package gobo.controller.dump;

import gobo.model.GbControl;
import gobo.service.GbSpreadsheetService;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class StartController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final String[] kinds = request.getParameterValues("kindArray");
		final String token = sessionScope("token");

		// Craete new spreadsheet
		GbSpreadsheetService su = new GbSpreadsheetService(token);
		SpreadsheetEntry createSpreadsheet = su.createSpreadsheet(Arrays.asList(kinds));
		final String ssKey = createSpreadsheet.getKey();
		System.out.println("ssKey=" + ssKey);

		Transaction tx = null;
		try {
			tx = Datastore.beginTransaction();

			// Prepare control table.
			Key controlId = Datastore.allocateId("dump");
			List<GbControl> list = new ArrayList<GbControl>();
			for (int i = 0; i < kinds.length; i++) {
				GbControl control = new GbControl();
				Key childKey = Datastore.createKey(controlId, GbControl.class, kinds[i]);
				control.setKey(childKey);
				control.setKindName(kinds[i]);
				control.setCount(0);
				control.setAuthSubToken(token);
				control.setSsKey(ssKey);
				control.setTableId(String.valueOf(i));
				control.setDate(new Date());
				list.add(control);

				// Call the "task chain" for each kind
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder.url("/tasks/dump").param(
					"controlKey",
					Datastore.keyToString(childKey)).method(Method.GET));
			}
			Datastore.put(tx, list);
			Datastore.commit(tx);

		} catch (Exception e) {
			Datastore.rollback(tx);
			throw e;
		}

		return redirect("started?docURL="
			+ response.encodeRedirectURL(createSpreadsheet.getHtmlLink().getHref()));
		// return redirect(createSpreadsheet.getHtmlLink().getHref());
	}

}
