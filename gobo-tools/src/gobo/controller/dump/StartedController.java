package gobo.controller.dump;

import gobo.AuthSubBase;

public class StartedController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {
		final String docURL = asString("docURL");
		requestScope("docURL", docURL);
		return forward("started.jsp");
	}
}
