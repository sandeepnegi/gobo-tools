package gobo.controller.restore;

import gobo.AuthSubBase;

public class StartedController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {
		return forward("/gobo/restore/started.jsp");
	}
}
