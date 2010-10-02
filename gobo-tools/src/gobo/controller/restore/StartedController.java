package gobo.controller.restore;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class StartedController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		return forward("started.jsp");
	}
}
