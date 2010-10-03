package gobo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slim3.datastore.Datastore;
import org.slim3.util.AppEngineUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

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
	// public void restoreData(String wsTitle, String[][] data) {
	//
	// List<Entity> list = new ArrayList<Entity>();
	// for (int row = 2; row < data.length; row++) {
	//
	// // parsing Key
	// String keyValue = data[row][0];
	// Entity entity = null;
	// if ((keyValue == null) || (keyValue.length() == 0)) {
	// entity = new Entity(wsTitle);
	// } else {
	// Key key = parseKey(keyValue);
	// entity = new Entity(key);
	// }
	//
	// // Properties
	// for (int col = 1; col < data[row].length; col++) {
	// final String propName = data[0][col];
	// final String dataType = data[1][col];
	// final String value = data[row][col];
	// try {
	// Object typedValue = asTypedValue(dataType, value);
	// entity.setProperty(propName, typedValue);
	// } catch (RuntimeException e) {
	// System.err.println(e.getMessage());
	// }
	// }
	// System.out.println(entity);
	// if (entity.getProperties().size() <= 1) {
	// break;
	// }
	// list.add(entity);
	// }
	// DatastoreService datastoreService =
	// DatastoreServiceFactory.getDatastoreService();
	// datastoreService.put(list);
	// return;
	// }

	public void restoreData2(String wsTitle, List<GbEntity> data) {

		List<Entity> list = new ArrayList<Entity>();
		for (GbEntity gbEntity : data) {

			// parsing Key
			Entity entity = null;
			Key key = gbEntity.getKey();
			if (key == null) {
				entity = new Entity(wsTitle);
			} else {
				entity = new Entity(key);
			}

			// Properties
			for (GbProperty gbProperty : gbEntity.getProperties()) {
				try {
					entity.setProperty(gbProperty.getName(), gbProperty.asTypedValue());
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
				}
			}
			System.out.println(entity);
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
	public static Key parseKey(String keyValue) {

		if (keyValue == null) {
			return null;
		}

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

}