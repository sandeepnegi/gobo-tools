package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.util.DataUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

		List<String> kindList = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			kindList.add("TestKind" + i);
		}
		String[] kinds = kindList.toArray(new String[0]);
		List<GbProperty> propList = DataUtil.getPropList();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {
			for (int i = 0; i < kinds.length; i++) {
				goboService.updateWorksheetSize(spreadsheet.getKey(), kinds[i], propList.size());
				String tableId =
					goboService.createTableInWorksheet(spreadsheet.getKey(), kinds[i], propList);
				assertThat(tableId, is(String.valueOf(i)));
			}
		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void updateWorksheetTwiceForRetryTest() throws Exception {

		String[] kinds = new String[] { "TestKind1" };
		List<GbProperty> propList = DataUtil.getPropList();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {

			goboService.updateWorksheetSize(spreadsheet.getKey(), kinds[0], propList.size());
			goboService.createTableInWorksheet(spreadsheet.getKey(), kinds[0], propList);
			goboService.updateWorksheetSize(spreadsheet.getKey(), kinds[0], propList.size());
			goboService.createTableInWorksheet(spreadsheet.getKey(), kinds[0], propList);

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void dumpAndGetDataTest() throws Exception {

		List<String> kindList = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			kindList.add("TestKind" + i);
		}
		String[] kinds = kindList.toArray(new String[0]);

		List<GbProperty> propList = DataUtil.getPropList();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		String ssKey = spreadsheet.getKey();
		try {
			for (int i = 0; i < kinds.length; i++) {
				goboService.updateWorksheetSize(ssKey, kinds[i], propList.size());
				String tableId = goboService.createTableInWorksheet(ssKey, kinds[i], propList);
				assertThat(tableId, is(String.valueOf(i)));
			}

			for (int i = 0; i < kinds.length; i++) {
				List<GbEntity> entityList = DataUtil.getEntityList(kinds[i]);
				goboService.dumpData(ssKey, kinds[i], String.valueOf(i), entityList);
			}
			for (int i = 0; i < kinds.length; i++) {
				List<GbEntity> dataOrNull = goboService.getDataOrNull(ssKey, kinds[i], 3, 10);
				System.out.println(dataOrNull);
			}

		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	SpreadsheetEntry createSpreadsheet(String[] kinds) {
		goboService = new GbSpreadsheetService(authSubToken);
		try {
			return goboService.createSpreadsheet(Arrays.asList(kinds));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	void deleteSpreadsheet(SpreadsheetEntry testSpreadsheet) {
		try {
			final String createdEntiry = testSpreadsheet.getTitle().getPlainText();
			DocsService client = new DocsService("yourCo-yourAppName-v1");
			client.setAuthSubToken(authSubToken);
			URL feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri, DocumentListFeed.class);
			for (DocumentListEntry entry : feed.getEntries()) {
				final String searchedEntiryTitle = entry.getTitle().getPlainText();
				if (createdEntiry.equals(searchedEntiryTitle)) {
					entry.delete();
					System.out.println("deleted file:" + searchedEntiryTitle);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
