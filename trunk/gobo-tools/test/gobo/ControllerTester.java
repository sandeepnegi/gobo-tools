package gobo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.tester.MockHttpServletRequest;
import org.slim3.tester.MockHttpServletResponse;
import org.slim3.tester.MockServletContext;

public class ControllerTester {

	public HttpServletResponse response;
	public HttpServletRequest request;

	@SuppressWarnings("unchecked")
	public String start(String uri) throws Exception {

		uri = uri.replaceAll("/", ".");
		int pos = uri.lastIndexOf('.');
		String subPackage = uri.substring(0, pos + 1);
		String className = uri.substring(pos + 1);
		char chars[] = className.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		className = new String(chars);
		final String fullName = "gobo.controller" + subPackage + className + "Controller";

		Class clazz = Class.forName(fullName);
		ControllerBase test = (ControllerBase) clazz.newInstance();

		test.request = new MockHttpServletRequest(new MockServletContext());
		test.response = new MockHttpServletResponse();

		String run = test.run();
		return run;
	}

}
