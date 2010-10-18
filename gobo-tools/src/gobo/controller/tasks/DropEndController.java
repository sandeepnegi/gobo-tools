package gobo.controller.tasks;

import gobo.TaskQueueBase;
import gobo.model.GbControl;
import gobo.service.GbMailService;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class DropEndController extends TaskQueueBase {

	@Override
	protected String runTask() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity control = datastore.get(controlKey);

		final Key parentKey = controlKey.getParent();
		int count =
			datastore.prepare(new Query(GbControl.NAME).setAncestor(parentKey)).countEntities();
		if (count == 0) {
			// Mail
			final long controlId = parentKey.getId();
			Object reportTo = control.getProperty(GbControl.REPORT_TO);
			if (reportTo != null) {
				GbMailService.sendMail((Email) reportTo, controlId, "Drop");
			}
			System.out.println("Finished");
		}

		// Delete control row.
		datastore.delete(controlKey);
		return null;
	}

}
