package dstools.controller.tasks;

import gobo.service.GbSpreadsheetService;

import java.io.IOException;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.google.gdata.util.ServiceException;


public class DumpControllerTest extends AppEngineTestCase {

	@Test
	public void test() throws IOException, ServiceException {

		GbSpreadsheetService util = new GbSpreadsheetService("1/Oj-tynWztGh10GUeOPV_Tj3aPbXgiNjUl_2sG1vcXMI");
		// util.getData2("tZFql4GT-d4mnl9vX6_M58A", "dstools_20100928_163547");
		util.getDataOrNull("tZFql4GT-d4mnl9vX6_M58A", "aaaa", 2, 5);
	}
}
