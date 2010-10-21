package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.util.TestDataUtil;

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
		List<GbProperty> propList = TestDataUtil.entities2();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {
			for (int i = 0; i < kinds.length; i++) {
				String tableId =
					goboService.prepareWorksheet(spreadsheet.getKey(), kinds[i], propList);
				assertThat(tableId, is(String.valueOf(i)));
			}
		} finally {
			deleteSpreadsheet(spreadsheet);
		}
	}

	@Test
	public void updateWorksheetTwiceForRetryTest() throws Exception {

		String[] kinds = new String[] { "TestKind1" };
		List<GbProperty> propList = TestDataUtil.entities2();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		try {

			goboService.prepareWorksheet(spreadsheet.getKey(), kinds[0], propList);
			goboService.prepareWorksheet(spreadsheet.getKey(), kinds[0], propList);

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

		List<GbProperty> propList = TestDataUtil.entities2();
		final SpreadsheetEntry spreadsheet = createSpreadsheet(kinds);
		String ssKey = spreadsheet.getKey();
		try {
			for (int i = 0; i < kinds.length; i++) {
				String tableId = goboService.prepareWorksheet(ssKey, kinds[i], propList);
				assertThat(tableId, is(String.valueOf(i)));
			}

			for (int i = 0; i < kinds.length; i++) {
				List<GbEntity> entityList = TestDataUtil.entities(kinds[i]);
				goboService.dumpData(ssKey, kinds[i], String.valueOf(i), entityList, false);
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
		List<GbProperty> entities = TestDataUtil.entities2();
		try {
			SpreadsheetEntry spreadsheet = goboService.createSpreadsheet(Arrays.asList(kinds));
			for (String kind : kinds) {
				goboService.prepareWorksheet(spreadsheet.getKey(), kind, entities);
			}
			return spreadsheet;
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

	@Test
	public void number2columnNameTest() {

		GbSpreadsheetService gss = new GbSpreadsheetService(authSubToken);
		assertThat(gss.number2columnName(0), is("A"));
		assertThat(gss.number2columnName(1), is("B"));
		assertThat(gss.number2columnName(2), is("C"));
		assertThat(gss.number2columnName(3), is("D"));
		assertThat(gss.number2columnName(4), is("E"));
		assertThat(gss.number2columnName(5), is("F"));
		assertThat(gss.number2columnName(6), is("G"));
		assertThat(gss.number2columnName(7), is("H"));
		assertThat(gss.number2columnName(8), is("I"));
		assertThat(gss.number2columnName(9), is("J"));
		assertThat(gss.number2columnName(10), is("K"));
		assertThat(gss.number2columnName(11), is("L"));
		assertThat(gss.number2columnName(12), is("M"));
		assertThat(gss.number2columnName(13), is("N"));
		assertThat(gss.number2columnName(14), is("O"));
		assertThat(gss.number2columnName(15), is("P"));
		assertThat(gss.number2columnName(16), is("Q"));
		assertThat(gss.number2columnName(17), is("R"));
		assertThat(gss.number2columnName(18), is("S"));
		assertThat(gss.number2columnName(19), is("T"));
		assertThat(gss.number2columnName(20), is("U"));
		assertThat(gss.number2columnName(21), is("V"));
		assertThat(gss.number2columnName(22), is("W"));
		assertThat(gss.number2columnName(23), is("X"));
		assertThat(gss.number2columnName(24), is("Y"));
		assertThat(gss.number2columnName(25), is("Z"));
		assertThat(gss.number2columnName(26), is("AA"));
		assertThat(gss.number2columnName(27), is("AB"));
		assertThat(gss.number2columnName(28), is("AC"));
		assertThat(gss.number2columnName(29), is("AD"));
		assertThat(gss.number2columnName(30), is("AE"));
		assertThat(gss.number2columnName(31), is("AF"));
		assertThat(gss.number2columnName(32), is("AG"));
		assertThat(gss.number2columnName(33), is("AH"));
		assertThat(gss.number2columnName(34), is("AI"));
		assertThat(gss.number2columnName(35), is("AJ"));
		assertThat(gss.number2columnName(36), is("AK"));
		assertThat(gss.number2columnName(37), is("AL"));
		assertThat(gss.number2columnName(38), is("AM"));
		assertThat(gss.number2columnName(39), is("AN"));
		assertThat(gss.number2columnName(40), is("AO"));
		assertThat(gss.number2columnName(41), is("AP"));
		assertThat(gss.number2columnName(42), is("AQ"));
		assertThat(gss.number2columnName(43), is("AR"));
		assertThat(gss.number2columnName(44), is("AS"));
		assertThat(gss.number2columnName(45), is("AT"));
		assertThat(gss.number2columnName(46), is("AU"));
		assertThat(gss.number2columnName(47), is("AV"));
		assertThat(gss.number2columnName(48), is("AW"));
		assertThat(gss.number2columnName(49), is("AX"));
		assertThat(gss.number2columnName(50), is("AY"));
		assertThat(gss.number2columnName(51), is("AZ"));
		assertThat(gss.number2columnName(52), is("BA"));
	}

}
