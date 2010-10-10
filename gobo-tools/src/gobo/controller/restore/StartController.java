package gobo.controller.restore;

import gobo.AuthSubBase;
import gobo.model.GbControl;

import java.util.ArrayList;
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

public class StartController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {

		final String ssKey = asString("ssKey");
		final String[] wsTitles = request.getParameterValues("wsTitleArray");
		final String token = sessionScope("token");
		final UserService user = UserServiceFactory.getUserService();
		final User currentUser = user.getCurrentUser();

		// コントロールテーブルを用意
		Transaction tx = null;
		try {
			tx = datastore.beginTransaction();

			// コントロールテーブルを準備
			Key controlId = datastore.allocateIds("restore", 1).getStart();
			List<Entity> list = new ArrayList<Entity>();
			for (int i = 0; i < wsTitles.length; i++) {
				Key childKey = KeyFactory.createKey(controlId, GbControl.NAME, wsTitles[i]);
				Entity control = new Entity(childKey);
				control.setProperty(GbControl.KIND_NAME, wsTitles[i]);
				control.setProperty(GbControl.COUNT, 2); // ignore header!
				if (currentUser != null) {
					control.setProperty(GbControl.REPORT_TO, new Email(currentUser.getEmail()));
				}
				control.setProperty(GbControl.AUTH_SUB_TOKEN, token);
				control.setProperty(GbControl.SPREADSHEET_KEY, ssKey);
				control.setProperty(GbControl.UPDATE_DATE, new Date());
				list.add(control);

				// Start task queue chain for each kind.
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(tx, TaskOptions.Builder.url("/tasks/Restore.gobo").param(
					"controlKey",
					KeyFactory.keyToString(childKey)).countdownMillis(10000).method(Method.GET));
			}
			datastore.put(tx, list);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		return redirect("Started.gobo");
	}
}
