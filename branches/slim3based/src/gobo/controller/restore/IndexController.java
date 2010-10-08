package gobo.controller.restore;

import gobo.service.GbSpreadsheetService;

import java.util.List;
import java.util.Map;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;


public class IndexController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		GbSpreadsheetService service = new GbSpreadsheetService((String) sessionScope("token"));
		List<Map<String, String>> list = service.getAllSpreadSheets();
		requestScope("list", list);

		return forward("index.jsp");
	}

}
