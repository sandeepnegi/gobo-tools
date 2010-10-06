package gobo.controller;

import javax.servlet.http.HttpSession;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

public class LogoutController extends Controller {

	@Override
	protected Navigation run() throws Exception {

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
