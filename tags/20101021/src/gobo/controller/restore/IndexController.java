package gobo.controller.restore;

import gobo.AuthSubBase;
import gobo.service.GbSpreadsheetService;

import java.util.List;
import java.util.Map;


public class IndexController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {

		GbSpreadsheetService service = new GbSpreadsheetService((String) sessionScope("token"));
		List<Map<String, String>> list = service.getAllSpreadSheets();
		requestScope("list", list);
		return forward("/gobo/restore/index.jsp");
	}
}
