package gobo.util;

import gobo.service.GbSpreadsheetService;

import java.net.URL;
import java.util.Arrays;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class SpreadsheetUtil {

	public static SpreadsheetEntry createSpreadsheet(String authSubToken, String[] kinds) {
		GbSpreadsheetService goboService = new GbSpreadsheetService(authSubToken);
		try {
			return goboService.createSpreadsheet(Arrays.asList(kinds));
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
