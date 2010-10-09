package gobo.controller.drop;

import gobo.ControllerBase;

public class StartedController extends ControllerBase {

	@Override
	protected String run() throws Exception {
		return forward("started.jsp");
	}
}
