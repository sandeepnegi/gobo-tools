package gobo.controller.restore;

import gobo.AuthSubBase;
import gobo.service.GbSpreadsheetService;

import java.util.List;
import java.util.Map;


public class SheetController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {

		final String ssKey = asString("ssKey");
		requestScope("ssKey", ssKey);
		
		GbSpreadsheetService service = new GbSpreadsheetService((String) sessionScope("token"));
		List<Map<String, String>> list = service.getAllWorkSheets(ssKey);
		requestScope("list", list);

		return forward("/gobo/restore/sheet.jsp");
	}
}
