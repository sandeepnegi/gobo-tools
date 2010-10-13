package gobo.service;

import static org.junit.Assert.*;
import gobo.TestBase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;

public class GbSpreadsheetServiceTest extends TestBase {

	static String authSubToken = null;
	static String ssKey = null;
	SpreadsheetEntry testSpreadsheet = null;
	GbSpreadsheetService ss = null;

	@Before
	public void setUp() {
		super.setUp();
		ResourceBundle bundle = ResourceBundle.getBundle("authSub");
		authSubToken = bundle.getString("token");
		ss = new GbSpreadsheetService(authSubToken);
		try {
			testSpreadsheet = ss.createSpreadsheet(Arrays.asList(new String[] { "TestKind1" }));
			ssKey = testSpreadsheet.getKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		// TODO: docs api として削除
		super.tearDown();
	}

	@Test
	public void testGetAllSpreadSheets() throws IOException, ServiceException {
		List<Map<String, String>> allSpreadSheets = ss.getAllSpreadSheets();
		assertTrue(allSpreadSheets.size() > 1);
	}
}
