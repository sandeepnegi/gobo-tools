package gobo.util;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.service.GbSpreadsheetService;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class SpreadsheetUtil {

	public static SpreadsheetEntry createSpreadsheet(String authSubToken, String[] kinds) {
		GbSpreadsheetService goboService = new GbSpreadsheetService(authSubToken);
		try {
			SpreadsheetEntry createSpreadsheet =
				goboService.createSpreadsheet(Arrays.asList(kinds));
			for (String kind : kinds) {
				List<GbProperty> testPropList1 = TestDataUtil.entities2();
				goboService.prepareWorksheet(createSpreadsheet.getKey(), kind, testPropList1);
			}
			return createSpreadsheet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SpreadsheetEntry createAndDumpSpreadsheet(String authSubToken, String[] kinds) {
		GbSpreadsheetService goboService = new GbSpreadsheetService(authSubToken);
		try {
			SpreadsheetEntry createSpreadsheet =
				goboService.createSpreadsheet(Arrays.asList(kinds));
			int i = 0;
			for (String kind : kinds) {
				List<GbProperty> testPropList1 = TestDataUtil.entities2();
				goboService.prepareWorksheet(createSpreadsheet.getKey(), kind, testPropList1);
				List<GbEntity> list = TestDataUtil.entities(kind);
				goboService.dumpData(createSpreadsheet.getKey(), kind, String.valueOf(i++), list, false);
			}
			return createSpreadsheet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SpreadsheetEntry createBlunkSpreadsheet(String authSubToken, String[] kinds) {
		GbSpreadsheetService goboService = new GbSpreadsheetService(authSubToken);
		try {
			SpreadsheetEntry createSpreadsheet =
				goboService.createSpreadsheet(Arrays.asList(kinds));
			return createSpreadsheet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void deleteSpreadsheet(String authSubToken, SpreadsheetEntry testSpreadsheet) {
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
