package dstools.controller.tasks;

import gobo.util.SpreadsheetUtil;

import java.io.IOException;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.google.gdata.util.ServiceException;


public class DumpControllerTest extends AppEngineTestCase {

	@Test
	public void test() throws IOException, ServiceException {

		SpreadsheetUtil util = new SpreadsheetUtil("1/Oj-tynWztGh10GUeOPV_Tj3aPbXgiNjUl_2sG1vcXMI");
		// util.getData2("tZFql4GT-d4mnl9vX6_M58A", "dstools_20100928_163547");
		util.getData("tZFql4GT-d4mnl9vX6_M58A", "aaaa", 2, 5);
	}
}
