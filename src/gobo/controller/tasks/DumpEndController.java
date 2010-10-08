package gobo.controller.tasks;

import gobo.meta.GbControlMeta;
import gobo.model.GbControl;
import gobo.service.GbMailService;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

public class DumpEndController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final Key controlKey = asKey("controlKey");
		GbControl gbControl = Datastore.get(new GbControlMeta(), controlKey);

		// Delete control row.
		Datastore.delete(controlKey);

		final Key parentKey = controlKey.getParent();
		List<GbControl> list = Datastore.query(GbControl.class, parentKey).asList();
		if ((list == null) || (list.size() == 0)) {

			// Mail
			final long controlId = parentKey.getId();
			if (gbControl.getReportTo() != null) {
				GbMailService.sendMail(gbControl.getReportTo(), controlId, "Dump");
			}
			System.out.println("Finished");
		}

		return null;
	}

}
