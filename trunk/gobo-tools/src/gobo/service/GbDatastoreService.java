package gobo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slim3.datastore.Datastore;
import org.slim3.util.AppEngineUtil;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.users.User;
import com.google.gdata.model.gd.PhoneNumber;

public class GbDatastoreService {

	public static List<String> getKinds() {

		List<String> kinds = null;

		if (AppEngineUtil.isProduction()) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			List<Entity> list =
				datastore.prepare(new Query("__Stat_Kind__")).asList(
					FetchOptions.Builder.withOffset(0));
			kinds = new ArrayList<String>();
			for (Entity kind : list) {
				kinds.add((String) kind.getProperty("kind_name"));
			}
		} else {
			kinds = org.slim3.datastore.DatastoreUtil.getKinds();
		}

		return kinds;
	}

	public static Map<String, Object> getProperties(String kind) {

		Entity entity = Datastore.query(kind).limit(1).asSingleEntity();
		return entity.getProperties();
	}

	/**
	 * 
	 * @param wsTitle
	 * @param data
	 */
	public void restoreData(String wsTitle, String[][] data) {

		List<Entity> list = new ArrayList<Entity>();
		for (int row = 2; row < data.length; row++) {

			// parsing Key
			String keyValue = data[row][0];
			Entity entity = null;
			if ((keyValue == null) || (keyValue.length() == 0)) {
				entity = new Entity(wsTitle);
			} else {
				Key key = parseKey(keyValue);
				entity = new Entity(key);
			}

			// Properties
			for (int col = 1; col < data[row].length; col++) {
				final String propName = data[0][col];
				final String dataType = data[1][col];
				final String value = data[row][col];
				try {
					Object typedValue = asTypedValue(dataType, value);
					entity.setProperty(propName, typedValue);
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
				}
			}
			System.out.println(entity);
			if (entity.getProperties().size() <= 1) {
				break;
			}
			list.add(entity);
		}
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		datastoreService.put(list);
		return;
	}

	/**
	 * Key.toString() -> Key
	 * 
	 * @param keyValue
	 * @return
	 */
	Key parseKey(String keyValue) {

		String[] keypaths = keyValue.split("/");
		Key key = null;
		for (String path : keypaths) {
			String[] split = path.split("[()]");
			String kind = split[0];
			String keyVal = split[1];
			if (keyVal.startsWith("\"")) {
				keyVal = keyVal.replaceAll("\"", "");
				key = KeyFactory.createKey(key, kind, keyVal);
			} else {
				key = KeyFactory.createKey(key, kind, Integer.parseInt(keyVal));
			}
		}
		return key;
	}

	Object asTypedValue(String type, String value) {

		Object val = null;
		if ((type == null) || (type.length() == 0)) {
			val = value;
		} else if (type.equals(GbSpreadsheetService.STRING)) {
			val = value;
		} else if (type.equals(GbSpreadsheetService.LONG)) {
			val = new Long(value);
		} else if (type.equals(GbSpreadsheetService.DOUBLE)) {
			val = new Double(value);
		} else if (type.equals(GbSpreadsheetService.BOOLEAN)) {
			if (value != null) {
				val = new Boolean(value);
			}
		} else if (type.equals(GbSpreadsheetService.DATE)) {
			throw new RuntimeException("Date type is not supported. value=" + value);
		} else if (type.equals(GbSpreadsheetService.CATEGORY)) {
			val = new Category(value);
		} else if (type.equals(GbSpreadsheetService.EMAIL)) {
			val = new Email(value);
		} else if (type.equals(GbSpreadsheetService.PHONE_NUMBER)) {
			val = new PhoneNumber(value);
		} else if (type.equals(GbSpreadsheetService.POSTAL_ADDRESS)) {
			val = new PostalAddress(value);
		} else if (type.equals(GbSpreadsheetService.RATING)) {
			val = new Rating(Integer.parseInt(value));
		} else if (type.equals(GbSpreadsheetService.USER)) {
			String[] split = value.split(",");
			val = new User(split[0], split[1]);
		} else if (type.equals(GbSpreadsheetService.GEO_PT)) {
			String[] split = value.split(",");
			val = new GeoPt(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
		} else if (type.equals(GbSpreadsheetService.KEY)) {
			val = parseKey(value);
		} else {
			val = value;
		}
		return val;
	}

}