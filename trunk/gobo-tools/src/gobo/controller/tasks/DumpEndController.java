package gobo.controller.tasks;

import java.util.logging.Logger;

import gobo.TaskQueueBase;
import gobo.model.GbControl;
import gobo.service.GbMailService;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class DumpEndController extends TaskQueueBase {

	private static final Logger logger = Logger.getLogger(DumpEndController.class.getName());

	@Override
	protected String runTask() throws Exception {

		final Key controlKey = asKey("controlKey");
		Entity gbControl = datastore.get(controlKey);

		final Key parentKey = controlKey.getParent();
		int count =
			datastore.prepare(new Query(GbControl.NAME).setAncestor(parentKey)).countEntities(
				FetchOptions.Builder.withDefaults());
		if (count == 0) {
			// Mail
			final long controlId = parentKey.getId();
			Object reportTo = gbControl.getProperty(GbControl.REPORT_TO);
			if (reportTo != null) {
				GbMailService.sendMail(
					(Email) reportTo,
					controlId,
					"[Dump] successfully completed.");
			}
			logger.info("Finished");
		}

		// Delete control row.
		datastore.delete(controlKey);
		return null;
	}

}
