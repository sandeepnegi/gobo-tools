package gobo.controller;

import gobo.ControllerBase;

import javax.servlet.http.HttpSession;

public class LogoutController extends ControllerBase {

	@Override
	protected String run() throws Exception {

		String referer = request.getHeader("Referer");
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		if (referer == null) {
			referer = "/";
		}
		return redirect(referer.substring(0, referer.indexOf('?')));
	}
}
