package gobo.total;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.service.GbDatastoreService;
import gobo.service.GbSpreadsheetService;
import gobo.util.TestDataUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class TotalTest extends TestBase {

	static ResourceBundle bundle = ResourceBundle.getBundle("authSub");
	static String authSubToken = bundle.getString("token");

	@Test
	public void runTest() throws Exception {

		// Preapre Data
		final int COUNT = 10;
		final String KIND_NAME = "TestKind";
		final List<Entity> orgData = TestDataUtil.bulkData(KIND_NAME, COUNT);
		final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(orgData);

		final String[] kinds = { KIND_NAME };

		// Create Spreadsheet
		GbSpreadsheetService gss = new GbSpreadsheetService(authSubToken);
		SpreadsheetEntry spreadsheet = gss.createSpreadsheet(Arrays.asList(kinds));
		try {

			// Get Data from Datastore.
			final List<GbEntity> srcData = GbDatastoreService.getData(null, KIND_NAME, COUNT);

			// Dump Data to Spreadsheet.
			List<GbProperty> properties = GbDatastoreService.getProperties(KIND_NAME);
			final String tableId = gss.prepareWorksheet(spreadsheet.getKey(), KIND_NAME, properties);
			gss.dumpData(spreadsheet.getKey(), KIND_NAME, tableId, srcData, false);

			// (Retry) Dump Data to Spreadsheet.
			gss.dumpData(spreadsheet.getKey(), KIND_NAME, tableId, srcData, true);

			// Get Data from Spreadsheet
			final List<GbEntity> midData =
				gss.getDataOrNull(spreadsheet.getKey(), KIND_NAME, 3, COUNT);

			// Compare
			for (int i = 0; i < srcData.size(); i++) {
				final GbEntity src = srcData.get(i);
				for (int j = 0; j < midData.size(); j++) {
					final GbEntity dest = midData.get(j);
					if (src.getKey().equals(dest.getKey())) {
						List<GbProperty> srcProps = src.getProperties();
						List<GbProperty> destProps = dest.getProperties();
						for (GbProperty srcProp : srcProps) {
							final String srcName = srcProp.getName();
							for (GbProperty destProp : destProps) {
								final String destName = destProp.getName();
								if (srcName.equals(destName)) {

									if (destProp.getValueType().equals(GbProperty.SHORT_BLOB)) {
										assertThat(
											destProp.getValue().toString(),
											is("'" + GbProperty.NOT_SUPPORTED));
									} else {
										assertThat(destProp.asDatastoreValue(), equalTo(srcProp
											.getValue()));
									}

								}
							}
						}
					}
				}
			}

			// Restore Data to Datastore
			GbDatastoreService.restoreData(KIND_NAME, midData);

			// Get Data from Datastore.
			final List<GbEntity> destData = GbDatastoreService.getData(null, KIND_NAME, COUNT);

			// Compare
			for (int i = 0; i < srcData.size(); i++) {
				final GbEntity src = srcData.get(i);
				for (int j = 0; j < destData.size(); j++) {
					final GbEntity dest = destData.get(j);
					if (src.getKey().equals(dest.getKey())) {
						List<GbProperty> srcProps = src.getProperties();
						List<GbProperty> destProps = dest.getProperties();
						for (GbProperty srcProp : srcProps) {
							final String srcName = srcProp.getName();
							for (GbProperty destProp : destProps) {
								final String destName = destProp.getName();
								if (srcName.equals(destName)) {
									assertThat(destProp.getValue(), equalTo(srcProp.getValue()));
								}
							}
						}
					}
				}
			}

		} finally {
			// SpreadsheetUtil.deleteSpreadsheet(authSubToken, spreadsheet);
		}
	}
}
