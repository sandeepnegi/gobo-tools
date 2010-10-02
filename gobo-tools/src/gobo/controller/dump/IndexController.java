package gobo.controller.dump;

import gobo.util.DatastoreUtil;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;


public class IndexController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		
		List<String> kinds = DatastoreUtil.getKinds();		
		requestScope("list", kinds);
		return forward("index.jsp");
	}

}
