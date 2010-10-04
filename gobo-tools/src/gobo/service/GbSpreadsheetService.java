package gobo.service;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.users.User;
import com.google.apphosting.api.ApiProxy;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
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
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.TableEntry;
import com.google.gdata.data.spreadsheet.Worksheet;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.data.spreadsheet.Data.InsertionMode;
import com.google.gdata.util.ServiceException;

public class GbSpreadsheetService {

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
	 * スプレッドシート(ブック)一覧
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
	 * ワークシート一覧
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

	public List<GbEntity> getData(String ssKey, String kind, Integer startIndex, Integer maxRows)
			throws IOException, ServiceException {

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
		// final int rowCount = maxRows + 2;
		String[] columnTitle = new String[colCount];
		String[] dataType = new String[colCount];

		URL cellFeedUrl = workSheet.getCellFeedUrl();
		CellQuery query = new CellQuery(cellFeedUrl);

		// Title & Type
		query.setMinimumRow(1);
		query.setMaximumRow(2);
		CellFeed feed = ss.query(query, CellFeed.class);
		for (CellEntry cell : feed.getEntries()) {
			String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
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
		int prevRow = -1;
		GbEntity gbEntity = null;
		List<GbEntity> data = new ArrayList<GbEntity>();
		for (CellEntry cell : feed.getEntries()) {
			String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
			
			int row = Integer.parseInt(shortId.substring(1, shortId.lastIndexOf('C')));
			if (prevRow != row) {
				prevRow = row;
				gbEntity = new GbEntity();
				gbEntity.setKeyString(cell.getCell().getValue());
				data.add(gbEntity);
				continue;
			}
			
			int col = Integer.parseInt(shortId.substring(shortId.lastIndexOf('C') + 1));
			GbProperty gbProperty = new GbProperty();
			gbProperty.setName(columnTitle[col - 1]);
			gbProperty.setValueType(dataType[col - 1]);
			gbProperty.setValue(cell.getCell().getValue());
			gbEntity.addProperty(gbProperty);
		}
		return data;
	}

	/**
	 * 
	 * @param targetKinds
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public String createSpreadsheet(List<String> targetKinds) throws MalformedURLException,
			IOException, ServiceException {

		// Create "docs".SpreadsheetEntry
		final String appId = ApiProxy.getCurrentEnvironment().getAppId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String fileName = appId + "_" + sdf.format(new Date());
		System.out.println("created new file:" + fileName);
		DocumentListEntry entry = new com.google.gdata.data.docs.SpreadsheetEntry();
		entry.setTitle(new PlainTextConstruct(fileName));
		cs.insert(new URL("https://docs.google.com/feeds/default/private/full/"), entry);

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
		// Map<String, Object> properties =
		// GbDatastoreService.getProperties(targetKinds.get(0));
		// defaultWorksheet.setColCount(properties.size() + 1);
		defaultWorksheet.update();

		// Adding Worksheets
		for (int i = 1; i < targetKinds.size(); i++) {
			WorksheetEntry newWorksheet = new WorksheetEntry();
			newWorksheet.setTitle(new PlainTextConstruct(targetKinds.get(i)));
			newWorksheet.setRowCount(2);
			// properties =
			// GbDatastoreService.getProperties(targetKinds.get(i));
			// newWorksheet.setColCount(properties.size() + 1);
			ss.insert(worksheetFeedUrl, newWorksheet);
		}

		return spreadsheetEntry.getKey();
	}

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
	 * @param columns
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void createTableInWorksheet(String ssKey, String kind, Map<String, Object> columns)
			throws IOException, ServiceException {

		// Add Table
		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL tableFeedUrl = factory.getTableFeedUrl(ssKey);

		TableEntry tableEntry = new TableEntry();
		tableEntry.setTitle(new PlainTextConstruct(kind));
		tableEntry.setWorksheet(new Worksheet(kind));
		tableEntry.setHeader(new Header(1));

		Data tableData = new Data();
		tableData.setNumberOfRows(0);
		tableData.setStartIndex(2);
		tableData.setInsertionMode(InsertionMode.INSERT);

		// Create a title row
		tableData.addColumn(new Column("A", Entity.KEY_RESERVED_PROPERTY));
		Object[] keys = columns.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String index = number2columnName(i + 1);
			String columnName = (String) keys[i];
			tableData.addColumn(new Column(index, columnName));
		}
		tableEntry.setData(tableData);
		TableEntry inserted = ss.insert(tableFeedUrl, tableEntry);

		// Add a "type" row
		String[] split = inserted.getId().split("/");
		final String tableId = split[split.length - 1];
		URL recordFeedUrl = factory.getRecordFeedUrl(ssKey, tableId);
		RecordEntry newEntry = new RecordEntry();
		for (int i = 0; i < keys.length; i++) {
			String columnName = (String) keys[i];
			// String type = (String) columns.get(columnName);
			String type = asDataType(columns.get(columnName));
			newEntry.addField(new Field(null, columnName, type));
		}
		ss.insert(recordFeedUrl, newEntry);
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
	public void dumpData(String ssKey, String kind, String tableId, List<Map<String, Object>> list)
			throws IOException, ServiceException {

		// Adding new rows to table
		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL recordFeedUrl = factory.getRecordFeedUrl(ssKey, tableId);
		for (Map<String, Object> row : list) {
			RecordEntry newEntry = new RecordEntry();
			for (String key : row.keySet()) {
				if (row.get(key) != null) {
					Object val = row.get(key);
					String value = asStringValue(val);
					newEntry.addField(new Field(null, key, value));
				}
			}
			ss.insert(recordFeedUrl, newEntry);
		}
		return;
	}

	public final static String STRING = "String";
	public final static String INTEGER = "Integer";
	public final static String SHORT = "Short";
	public final static String LONG = "Long";
	public final static String BOOLEAN = "Boolean";
	public final static String FLOAT = "Float";
	public final static String DOUBLE = "Double";
	public final static String DATE = "Date";
	public final static String USER = "User";
	public final static String KEY = "Key";
	public final static String CATEGORY = "Category";
	public final static String EMAIL = "Email";
	public final static String GEO_PT = "GeoPt";
	public final static String IMHANDLE = "IMHandle";
	public final static String LINK = "Link";
	public final static String PHONE_NUMBER = "PhoneNumber";
	public final static String POSTAL_ADDRESS = "PostalAddress";
	public final static String RATING = "Rating";

	String asDataType(Object clazz) {
		String type = null;
		if (clazz instanceof String) {
			type = STRING;
		} else if (clazz instanceof Integer) {
			type = INTEGER;
		} else if (clazz instanceof Short) {
			type = SHORT;
		} else if (clazz instanceof Long) {
			type = LONG;
		} else if (clazz instanceof Boolean) {
			type = BOOLEAN;
		} else if (clazz instanceof Float) {
			type = FLOAT;
		} else if (clazz instanceof Double) {
			type = DOUBLE;
		} else if (clazz instanceof Date) {
			type = DATE;
		} else if (clazz instanceof User) {
			type = USER;
		} else if (clazz instanceof Key) {
			type = KEY;
		} else if (clazz instanceof Category) {
			type = CATEGORY;
		} else if (clazz instanceof Email) {
			type = EMAIL;
		} else if (clazz instanceof GeoPt) {
			type = GEO_PT;
		} else if (clazz instanceof IMHandle) {
			type = IMHANDLE;
		} else if (clazz instanceof Link) {
			type = LINK;
		} else if (clazz instanceof PhoneNumber) {
			type = PHONE_NUMBER;
		} else if (clazz instanceof PostalAddress) {
			type = POSTAL_ADDRESS;
		} else if (clazz instanceof Rating) {
			type = RATING;
		}
		return type;
	}

	String asStringValue(Object val) {

		String value = null;
		if (val instanceof PostalAddress) {
			value = ((PostalAddress) val).getAddress();
		} else if (val instanceof PhoneNumber) {
			value = ((PhoneNumber) val).getNumber();
		} else if (val instanceof Category) {
			value = ((Category) val).getCategory();
		} else if (val instanceof Email) {
			value = ((Email) val).getEmail();
		} else if (val instanceof Rating) {
			value = String.valueOf(((Rating) val).getRating());
		} else {
			value = val.toString();
		}
		return value;
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
