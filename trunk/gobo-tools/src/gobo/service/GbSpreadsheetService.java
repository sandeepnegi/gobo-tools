package gobo.service;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.google.apphosting.api.ApiProxy;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.RecordQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;
import com.google.gdata.data.spreadsheet.Field;
import com.google.gdata.data.spreadsheet.Header;
import com.google.gdata.data.spreadsheet.RecordEntry;
import com.google.gdata.data.spreadsheet.RecordFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.TableEntry;
import com.google.gdata.data.spreadsheet.TableFeed;
import com.google.gdata.data.spreadsheet.Worksheet;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.data.spreadsheet.Data.InsertionMode;
import com.google.gdata.util.ServiceException;

public class GbSpreadsheetService {

	private static final String VALUE_TYPE = "TYPE";
	private static final String VALUE_TYPE_NOT_SET = "*";
	private String authSubToken;
	private SpreadsheetService ss;
	private DocsService cs;

	public GbSpreadsheetService(String authSubToken) {
		this.authSubToken = authSubToken;
		ss = new SpreadsheetService("dstools");
		ss.setAuthSubToken(this.authSubToken);
		cs = new DocsService("dstools");
		cs.setAuthSubToken(this.authSubToken);
	}

	/**
	 * Get All Spreadsheets
	 * 
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<Map<String, String>> getAllSpreadSheets() throws IOException, ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		SpreadsheetQuery spreadsheetQuery =
			new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
		SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
		List<SpreadsheetEntry> entries = spreadsheetFeed.getEntries();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (SpreadsheetEntry spreadSheet : entries) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("key", spreadSheet.getKey());
			row.put("title", spreadSheet.getTitle().getPlainText());
			list.add(row);
		}
		return list;
	}

	/**
	 * Get All worksheets in the spreadsheet.
	 * 
	 * @param ssKey
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public List<Map<String, String>> getAllWorkSheets(String ssKey) throws IOException,
			ServiceException {

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		WorksheetQuery worksheetQuery =
			new WorksheetQuery(urlFactory.getWorksheetFeedUrl(ssKey, "private", "values"));
		WorksheetFeed worksheetFeed = ss.query(worksheetQuery, WorksheetFeed.class);
		List<WorksheetEntry> entries = worksheetFeed.getEntries();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (WorksheetEntry workSheet : entries) {
			Map<String, String> row = new HashMap<String, String>();
			// row.put("wsID", workSheet.getId());
			row.put("wsTitle", workSheet.getTitle().getPlainText());
			row.put("rowCount", String.valueOf(workSheet.getRowCount()));
			list.add(row);
		}
		return list;
	}

	public List<GbEntity> getDataOrNull(String ssKey, String kind, Integer startIndex,
			Integer maxRows) throws IOException, ServiceException {

		/*
		 * This code doesn't use list-base feed but cell-base feed. because The
		 * columnNames are case-insensitive in list-base feed.
		 */

		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		WorksheetQuery worksheetQuery =
			new WorksheetQuery(urlFactory.getWorksheetFeedUrl(ssKey, "private", "values"));
		worksheetQuery.setTitleQuery(kind);
		WorksheetFeed spreadsheetFeed = ss.query(worksheetQuery, WorksheetFeed.class);
		WorksheetEntry workSheet = spreadsheetFeed.getEntries().get(0);
		final int colCount = workSheet.getColCount();
		String[] columnTitle = new String[colCount];
		String[] dataType = new String[colCount];

		URL cellFeedUrl = workSheet.getCellFeedUrl();
		CellQuery query = new CellQuery(cellFeedUrl);

		// Title & Type
		query.setMinimumRow(1);
		query.setMaximumRow(2);
		CellFeed feed = ss.query(query, CellFeed.class);
		for (CellEntry cell : feed.getEntries()) {
			final String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
			// System.out.println(shortId + ":" + cell.getCell().getValue());
			int row = Integer.parseInt(shortId.substring(1, shortId.lastIndexOf('C')));
			int col = Integer.parseInt(shortId.substring(shortId.lastIndexOf('C') + 1));
			if (row == 1) {
				columnTitle[col - 1] = cell.getCell().getValue();
			} else {
				dataType[col - 1] = cell.getCell().getValue();
			}
		}

		// Data (start from line no.3)
		query.setMinimumRow(startIndex);
		final int nextMax = startIndex + maxRows - 1;
		final int maxRowCount = workSheet.getRowCount();
		final int maxRow = (nextMax > maxRowCount) ? maxRowCount : nextMax;
		System.out.println(startIndex + "〜" + maxRow);
		if (startIndex >= maxRow) {
			return null;
		}
		query.setMaximumRow(maxRow);
		feed = ss.query(query, CellFeed.class);
		GbEntity gbEntity = null;
		List<GbEntity> data = new ArrayList<GbEntity>();
		for (CellEntry cell : feed.getEntries()) {
			final String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
			int col = Integer.parseInt(shortId.substring(shortId.lastIndexOf('C') + 1));
			if (col == 1) {
				gbEntity = new GbEntity();
				gbEntity.setKeyString(cell.getCell().getValue());
				data.add(gbEntity);
				continue;
			}

			GbProperty gbProperty = new GbProperty();
			gbProperty.setName(columnTitle[col - 1]);
			gbProperty.setValueType(dataType[col - 1]);
			gbProperty.setValue(cell.getCell().getValue());
			gbEntity.addProperty(gbProperty);
		}
		return data;
	}

	/**
	 * Create Spreadsheet to dump.
	 * 
	 * @param targetKinds
	 * @return
	 * @throws Exception
	 */
	public SpreadsheetEntry createSpreadsheet(List<String> targetKinds) throws Exception {

		// Create "docs".SpreadsheetEntry
		final String appId = ApiProxy.getCurrentEnvironment().getAppId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String fileName = appId + "_" + sdf.format(new Date());
		System.out.println("created new file:" + fileName);
		DocumentListEntry entry = new com.google.gdata.data.docs.SpreadsheetEntry();		
		entry.setTitle(new PlainTextConstruct(fileName));
		DocumentListEntry newSpreadSheet =
			cs.insert(new URL("https://docs.google.com/feeds/default/private/full/"), entry);

		try {
			// Re-get in "spreadsheet".SpreadsheetEntry
			FeedURLFactory urlFactory = FeedURLFactory.getDefault();
			SpreadsheetQuery spreadsheetQuery =
				new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
			spreadsheetQuery.setTitleQuery(fileName);
			SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
			SpreadsheetEntry spreadsheetEntry = spreadsheetFeed.getEntries().get(0);

			// Modifying a default worksheet
			URL worksheetFeedUrl = spreadsheetEntry.getWorksheetFeedUrl();
			WorksheetFeed worksheetFeed = ss.getFeed(worksheetFeedUrl, WorksheetFeed.class);
			WorksheetEntry defaultWorksheet = worksheetFeed.getEntries().get(0);
			defaultWorksheet.setTitle(new PlainTextConstruct(targetKinds.get(0)));
			defaultWorksheet.setRowCount(2);
			defaultWorksheet.setColCount(1);
			defaultWorksheet.update();

			// Adding Worksheets
			for (int i = 1; i < targetKinds.size(); i++) {
				WorksheetEntry newWorksheet = new WorksheetEntry();
				newWorksheet.setTitle(new PlainTextConstruct(targetKinds.get(i)));
				newWorksheet.setRowCount(2);
				newWorksheet.setColCount(1);
				ss.insert(worksheetFeedUrl, newWorksheet);
			}

			return spreadsheetEntry;

		} catch (Exception e) {
			if (newSpreadSheet != null) {
				try {
					newSpreadSheet.delete();
				} catch (Exception e2) {
					System.err.println(e2);
				}
			}
			throw e;
		}
	}

	/**
	 * Update worksheet's column size
	 * 
	 * @param ssKey
	 * @param kind
	 * @param columnSize
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void updateWorksheetSize(String ssKey, String kind, Integer columnSize)
			throws IOException, ServiceException {

		// Search Spreadsheet
		FeedURLFactory urlFactory = FeedURLFactory.getDefault();
		SpreadsheetQuery spreadsheetQuery =
			new SpreadsheetQuery(urlFactory.getSpreadsheetsFeedUrl());
		SpreadsheetFeed spreadsheetFeed = ss.query(spreadsheetQuery, SpreadsheetFeed.class);
		SpreadsheetEntry spreadsheetEntry = null;
		for (SpreadsheetEntry entry : spreadsheetFeed.getEntries()) {
			if (ssKey.equals(entry.getKey())) {
				spreadsheetEntry = entry;
				break;
			}
		}

		// Modifying a worksheet column size
		URL worksheetFeedUrl = spreadsheetEntry.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = ss.getFeed(worksheetFeedUrl, WorksheetFeed.class);
		WorksheetEntry worksheetEntry = null;
		for (WorksheetEntry worksheet : worksheetFeed.getEntries()) {
			if (kind.equals(worksheet.getTitle().getPlainText())) {
				worksheetEntry = worksheet;
				break;
			}
		}
		worksheetEntry.setColCount(columnSize + 1);
		worksheetEntry.update();
	}

	/**
	 * Create table(table-based feed)
	 * 
	 * @param ssKey
	 * @param kind
	 * @param properties
	 * @throws Exception
	 */
	public String createTableInWorksheet(String ssKey, String kind, List<GbProperty> properties)
			throws Exception {

		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL tableFeedUrl = factory.getTableFeedUrl(ssKey);

		// Check if already exists.
		TableEntry tableEntry = null;
		TableFeed feed = ss.getFeed(tableFeedUrl, TableFeed.class);
		for (TableEntry entry : feed.getEntries()) {
			if (entry.getTitle().getPlainText().equals(kind)) {
				tableEntry = entry;
				break;
			}
		}

		// Add Table
		if (tableEntry == null) {
			tableEntry = new TableEntry();
			tableEntry.setTitle(new PlainTextConstruct(kind));
			tableEntry.setWorksheet(new Worksheet(kind));
			tableEntry.setHeader(new Header(1));

			Data tableData = new Data();
			tableData.setNumberOfRows(0);
			tableData.setStartIndex(2);
			tableData.setInsertionMode(InsertionMode.INSERT);

			// Create a title row
			tableData.addColumn(new Column("A", Entity.KEY_RESERVED_PROPERTY));
			for (int i = 0; i < properties.size(); i++) {
				String index = number2columnName(i + 1);
				String columnName = properties.get(i).getName();
				tableData.addColumn(new Column(index, columnName));
			}
			tableEntry.setData(tableData);
			tableEntry = ss.insert(tableFeedUrl, tableEntry);
		}
		String[] split = tableEntry.getId().split("/");
		final String tableId = split[split.length - 1];

		// Add a "valueType" row (as "*" to be replaced)
		int numberOfRows = tableEntry.getData().getNumberOfRows();
		if (numberOfRows == 0) {
			RecordEntry newEntry = new RecordEntry();
			newEntry.addField(new Field(null, Entity.KEY_RESERVED_PROPERTY, VALUE_TYPE));
			for (int i = 0; i < properties.size(); i++) {
				GbProperty gbProperty = properties.get(i);
				String columnName = gbProperty.getName();
				// String type = gbProperty.asSpreadsheetValueType();
				// newEntry.addField(new Field(null, columnName, type));
				newEntry.addField(new Field(null, columnName, VALUE_TYPE_NOT_SET));
			}
			URL recordFeedUrl = factory.getRecordFeedUrl(ssKey, tableId);
			ss.insert(recordFeedUrl, newEntry);
		}
		return tableId;
	}

	/**
	 * Add dump data to spreadsheet
	 * 
	 * @param ssKey
	 * @param tableId
	 * @param list
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void dumpData(String ssKey, String kind, String tableId, List<GbEntity> list)
			throws IOException, ServiceException {

		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL recordFeedUrl = factory.getRecordFeedUrl(ssKey, tableId);

		// Get "valueType" row for update.
		RecordQuery query = new RecordQuery(recordFeedUrl);
		query.setSpreadsheetQuery(Entity.KEY_RESERVED_PROPERTY + "=" + VALUE_TYPE);
		RecordEntry valueTypeRow = ss.query(query, RecordFeed.class).getEntries().get(0);
		Map<String, Field> valueTypeRowMap = new HashMap<String, Field>();
		boolean valueTypeNotSet = false;
		for (Field field : valueTypeRow.getFields()) {
			valueTypeRowMap.put(field.getName(), field);
			if (field.getValue().equals(VALUE_TYPE_NOT_SET)) {
				valueTypeNotSet = true;
			}
		}

		// Adding new rows to table
		for (GbEntity gbEntity : list) {
			RecordEntry newEntry = new RecordEntry();
			newEntry.addField(new Field(null, Entity.KEY_RESERVED_PROPERTY, gbEntity
				.getKey()
				.toString()));
			for (GbProperty gbProperty : gbEntity.getProperties()) {
				String value = gbProperty.asSpreadsheetValue();
				if (value != null) {
					newEntry.addField(new Field(null, gbProperty.getName(), value));

					// Update valueType Cell
					if (valueTypeNotSet) {
						Field valueTypeCell = valueTypeRowMap.get(gbProperty.getName());
						// TODO if statistics is not updated, valueTypeCell would be null.
						valueTypeCell.setValue(gbProperty.asSpreadsheetValueType());
					}
				}
			}
			ss.insert(recordFeedUrl, newEntry);
		}

		// Update valueType row.
		if (valueTypeNotSet) {
			valueTypeRow.update();
		}
		return;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	String number2columnName(int i) {
		final String[] alpha =
			{
				"A",
				"B",
				"C",
				"D",
				"E",
				"F",
				"G",
				"H",
				"I",
				"J",
				"K",
				"L",
				"M",
				"N",
				"O",
				"P",
				"Q",
				"R",
				"S",
				"T",
				"U",
				"V",
				"W",
				"X",
				"Y",
				"Z" };
		int devided = i / 26;
		int amari = i % 26;
		String name = (devided == 0) ? alpha[amari] : alpha[devided] + alpha[amari];
		return name;
	}
}
