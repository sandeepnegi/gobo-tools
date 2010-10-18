package gobo;

import java.util.logging.Logger;

import gobo.model.GbControl;
import gobo.service.GbMailService;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public abstract class TaskQueueBase extends ControllerBase {

	static final int RETRY_MAX = 5;

	private static final Logger logger = Logger.getLogger(TaskQueueBase.class.getName());

	@Override
	protected String run() throws Exception {

		final String _retryCount = request.getHeader("X-AppEngine-TaskRetryCount");
		final Integer retryCount = (_retryCount == null) ? 0 : new Integer(_retryCount);
		if (retryCount > RETRY_MAX) {
			try {
				logger.severe("Over retry count.");

				// Error Mail
				final Key controlKey = asKey("controlKey");
				final Entity gbControl = datastore.get(controlKey);
				final Key parentKey = controlKey.getParent();
				final long controlId = parentKey.getId();
				final Object reportTo = gbControl.getProperty(GbControl.REPORT_TO);
				if (reportTo != null) {
					GbMailService.sendMail(
						(Email) reportTo,
						controlId,
						"Abort. Prease check the log.");
				}
				// Delete control row.
				datastore.delete(controlKey);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		return runTask();
	}

	protected abstract String runTask() throws Exception;
}
