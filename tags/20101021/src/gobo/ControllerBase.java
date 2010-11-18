package gobo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class ControllerBase {

	public static final String REDIRECT_FLAG = ";redirect=true";

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	protected abstract String run() throws Exception;

	protected String forward(String string) {
		return string;
	}

	protected String redirect(String string) {
		return string + REDIRECT_FLAG;
	}

	protected String sessionScope(String string) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (String) session.getAttribute(string);
	}

	protected void requestScope(String string, Object object) {
		request.setAttribute(string, object);
	}

	protected String asString(String string) {
		Object attribute = request.getParameter(string);
		if (attribute == null) {
			return null;
		}
		return (String) attribute;
	}

	protected Integer asInteger(String string) {
		Object attribute = request.getParameter(string);
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt((String) attribute);
	}

	protected Key asKey(String string) {
		Object attribute = request.getParameter(string);
		if (attribute == null) {
			return null;
		}
		return KeyFactory.stringToKey((String) attribute);
	}

}