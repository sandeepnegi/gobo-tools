package gobo.controller.tasks;

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

		final Key controlId = asKey("controlId");
		final String kind = asString("kind");

		// Delete control row.
		Key childKey = Datastore.createKey(controlId, GbControl.class, kind);
		Datastore.delete(childKey);

		List<GbControl> list = Datastore.query(GbControl.class, controlId).asList();
		if ((list == null) || (list.size() == 0)) {
			
			// Mail
			GbMailService.sendMail(controlId.getId(), "Dump");
			System.out.println("終了");
		}

		return null;
	}

}
