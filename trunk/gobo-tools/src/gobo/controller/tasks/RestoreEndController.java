package gobo.controller.tasks;

import gobo.model.Control;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;


public class RestoreEndController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final Key controlId = asKey("controlId");
		final String wsTitle = asString("wsTitle");

		// コントロールテーブルから該当ワークシートの行を削除
		Key childKey = Datastore.createKey(controlId, Control.class, wsTitle);
		Datastore.delete(childKey);

		List<Control> list = Datastore.query(Control.class, controlId).asList();
		if ((list == null) || (list.size() == 0)) {
			// TODO:mail
			System.out.println("終了:" + wsTitle);
		}

		return null;
	}

}
