package gobo.controller;

import gobo.ControllerBase;

import javax.servlet.http.HttpSession;

public class LogoutController extends ControllerBase {

	@Override
	protected String run() throws Exception {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return redirect("/");
	}
}
