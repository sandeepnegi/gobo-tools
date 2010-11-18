package gobo.controller;

import gobo.ControllerBase;

public class IndexController extends ControllerBase {

	@Override
	protected String run() throws Exception {
				
		return forward("/gobo/index.jsp");
	}

}
