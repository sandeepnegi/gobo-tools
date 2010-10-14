package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;

public class GbSpreadsheetServiceTest extends TestBase {

	static String authSubToken = null;
	static GbSpreadsheetService goboService = null;

	@Before
	public void setUp() {
		super.setUp();
		ResourceBundle bundle = ResourceBundle.getBundle("authSub");
		authSubToken = bundle.getString("token");
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void createSingleWorksheetTest() throws IOException, ServiceException {

		final SpreadsheetEntry spreadsheet = createSpreadsheet(new String[] { "TestKind1" });
		try {

			List<Map<String, String>> bookList = goboService.getAllSpreadSheets();
			System.out.println(bookList);
			assertTrue(bookList.size() > 0);

			List<Map<String, String>> sheetList =
				goboService.getAllWorkSheets(spreadsheet.getKey());
			System.out.println(sheetList);
			assertThat(sheetList.size(), is(1));
			assertThat(sheetList.get(0).get("wsTitle"), is("TestKind1"));

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void createMultiWorksheetTest() throws IOException, ServiceException {

		final SpreadsheetEntry spreadsheet =
			createSpreadsheet(new String[] { "TestKind1", "TestKind2", "TestKind3" });
		try {
			List<Map<String, String>> bookList = goboService.getAllSpreadSheets();
			System.out.println(bookList);
			assertTrue(bookList.size() > 0);

			List<Map<String, String>> sheetList =
				goboService.getAllWorkSheets(spreadsheet.getKey());
			System.out.println(sheetList);
			assertThat(sheetList.size(), is(3));
			assertThat(sheetList.get(0).get("wsTitle"), is("TestKind1"));
			assertThat(sheetList.get(1).get("wsTitle"), is("TestKind2"));
			assertThat(sheetList.get(2).get("wsTitle"), is("TestKind3"));

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	private SpreadsheetEntry createSpreadsheet(String[] kinds) {
		goboService = new GbSpreadsheetService(authSubToken);
		try {
			return goboService.createSpreadsheet(Arrays.asList(kinds));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void deleteSpreadsheet(SpreadsheetEntry testSpreadsheet) {
		try {
			final String createdEntiry = testSpreadsheet.getTitle().getPlainText();
			DocsService client = new DocsService("yourCo-yourAppName-v1");
			client.setAuthSubToken(authSubToken);
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				final String searchedEntiryTitle = entry.getTitle().getPlainText();
				if (createdEntiry.equals(searchedEntiryTitle)) {
					System.out.println("deleting file:" + searchedEntiryTitle);
					entry.delete();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
