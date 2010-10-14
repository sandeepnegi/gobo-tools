package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbProperty;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
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

		String[] kinds = new String[] { "TestKind1" };
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {

			List<Map<String, String>> bookList = goboService.getAllSpreadSheets();
			System.out.println(bookList);
			assertTrue(bookList.size() > 0);

			List<Map<String, String>> sheetList =
				goboService.getAllWorkSheets(spreadsheet.getKey());
			System.out.println(sheetList);
			assertThat(sheetList.size(), is(1));
			assertThat(sheetList.get(0).get("wsTitle"), is(kinds[0]));

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void createMultiWorksheetTest() throws IOException, ServiceException {

		String[] kinds = new String[] { "TestKind1", "TestKind2", "TestKind3" };
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {
			List<Map<String, String>> bookList = goboService.getAllSpreadSheets();
			System.out.println(bookList);
			assertTrue(bookList.size() > 0);

			List<Map<String, String>> sheetList =
				goboService.getAllWorkSheets(spreadsheet.getKey());
			System.out.println(sheetList);
			assertThat(sheetList.size(), is(3));
			assertThat(sheetList.get(0).get("wsTitle"), is(kinds[0]));
			assertThat(sheetList.get(1).get("wsTitle"), is(kinds[1]));
			assertThat(sheetList.get(2).get("wsTitle"), is(kinds[2]));

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void updateWorksheetTest() throws Exception {

		String[] kinds = new String[] { "TestKind1" };
		List<GbProperty> propList = Lists.newArrayList();

		GbProperty prop1 = new GbProperty();
		prop1.setName("prop1");
		prop1.setValue(new String());
		propList.add(prop1);

		GbProperty prop2 = new GbProperty();
		prop2.setName("prop2");
		prop2.setValue(new Long(1));
		propList.add(prop2);

		GbProperty prop3 = new GbProperty();
		prop3.setName("prop3");
		prop3.setValue(new Boolean(true));
		propList.add(prop3);

		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {
			goboService.updateWorksheetSize(spreadsheet.getKey(), kinds[0], propList.size());
			String tableId =
				goboService.createTableInWorksheet(spreadsheet.getKey(), kinds[0], propList);
			assertThat(tableId, is("0"));
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
