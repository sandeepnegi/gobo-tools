package gobo.controller.dump;

import gobo.AuthSubBase;
import gobo.model.GbControl;
import gobo.service.GbSpreadsheetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class StartController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {

		final String[] kinds = request.getParameterValues("kindArray");
		final String token = sessionScope("token");
		final UserService user = UserServiceFactory.getUserService();
		final User currentUser = user.getCurrentUser();

		// Craete new spreadsheet
		GbSpreadsheetService su = new GbSpreadsheetService(token);
		SpreadsheetEntry createSpreadsheet = su.createSpreadsheet(Arrays.asList(kinds));
		final String ssKey = createSpreadsheet.getKey();
		System.out.println("ssKey=" + ssKey);

		Transaction tx = null;
		try {
			tx = datastore.beginTransaction();

			// Prepare control table.
			Key controlId = datastore.allocateIds("dump", 1).getStart();
			List<Entity> list = new ArrayList<Entity>();
			for (int i = 0; i < kinds.length; i++) {
				Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, kinds[i]);
				Entity control = new Entity(childKey);
				control.setProperty(GbControl.KIND_NAME, kinds[i]);
				control.setProperty(GbControl.COUNT, 0);
				if (currentUser != null) {
					control.setProperty(GbControl.REPORT_TO, new Email(currentUser.getEmail()));
				}
				control.setProperty(GbControl.AUTH_SUB_TOKEN, token);
				control.setProperty(GbControl.SPREADSHEET_KEY, ssKey);
				control.setProperty(GbControl.UPDATE_DATE, new Date());
				list.add(control);

				// Call the "task chain" for each kind
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder.url("/tasks/Dump.gobo").param(
					"controlKey",
					KeyFactory.keyToString(childKey)).method(Method.GET));
			}
			datastore.put(tx, list);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		return redirect("Started.gobo?docURL="
			+ response.encodeRedirectURL(createSpreadsheet.getHtmlLink().getHref()));
	}
}
