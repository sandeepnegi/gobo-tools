package gobo.controller;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class IndexController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		
		UserService user = UserServiceFactory.getUserService();
		User currentUser = user.getCurrentUser();
		
		
		return forward("index.jsp");
	}

}
