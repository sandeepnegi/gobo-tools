package gobo.controller.restore;

import gobo.service.GbSpreadsheetService;

import java.util.List;
import java.util.Map;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;


public class SheetController extends Controller {

	@Override
	protected Navigation run() throws Exception {

		final String ssKey = asString("ssKey");
		
		GbSpreadsheetService service = new GbSpreadsheetService((String) sessionScope("token"));
		List<Map<String, String>> list = service.getAllWorkSheets(ssKey);
		requestScope("list", list);

		return forward("sheet.jsp");
	}
}
