package gobo;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GoboServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		processReguest(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		processReguest(req, resp);
	}

	@SuppressWarnings("unchecked")
	private void processReguest(HttpServletRequest request, HttpServletResponse response) {
		try {
			String uri = request.getRequestURI();
			uri = uri.replaceAll(".gobo", "");
			uri = uri.replaceAll("/", ".");
			int pos = uri.lastIndexOf('.');
			String subPackage = uri.substring(0, pos + 1);
			String className = uri.substring(pos + 1);
			char chars[] = className.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			className = new String(chars);
			final String fullName = "gobo.controller" + subPackage + className + "Controller";
			System.out.println(fullName);

			Class clazz = Class.forName(fullName);
			ControllerBase controller = (ControllerBase) clazz.newInstance();

			controller.request = request;
			controller.response = response;
			String reternString = controller.run();

			if (reternString != null) {
				if (reternString.endsWith(ControllerBase.REDIRECT_FLAG)) {
					reternString = reternString.replace(ControllerBase.REDIRECT_FLAG, "");
					response.sendRedirect(reternString);
				} else {
					RequestDispatcher dispatcher = request.getRequestDispatcher(reternString);
					dispatcher.forward(request, response);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
