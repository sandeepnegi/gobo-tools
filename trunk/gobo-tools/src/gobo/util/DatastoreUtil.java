package gobo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slim3.util.AppEngineUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Property;

public class DatastoreUtil {

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

	/**
	 * Get kind and property info.
	 * 
	 * @return
	 */
	public static Map<String, Map<String, Object>> getKindInfos() {

		Map<String, Map<String, Object>> kindInfos = new HashMap<String, Map<String, Object>>();
		if (AppEngineUtil.isProduction()) {

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			List<Entity> list =
				datastore.prepare(new Query("__Stat_PropertyType_PropertyName_Kind__")).asList(
					FetchOptions.Builder.withOffset(0));
			for (Entity kind : list) {
				kindInfos.put((String) kind.getProperty("kind_name"), kind.getProperties());
			}

		} else {

			Schema schema = org.slim3.datastore.DatastoreUtil.getSchema();
			List<EntityProto> entityProtoList = schema.kinds();
			for (EntityProto entityProto : entityProtoList) {
				Map<String, Object> props = new HashMap<String, Object>();
				for (Property property : entityProto.propertys()) {
					final String name = property.getName();
					final String type = property.getValue().toString().split(":")[0];
					props.put(name, type);
				}
				final String kindName =
					org.slim3.datastore.DatastoreUtil.getKind(entityProto.getKey());
				kindInfos.put(kindName, props);
			}
		}
		return kindInfos;
	}

	/**
	 * 
	 * @param wsTitle
	 * @param data
	 */
	public void restoreData(String wsTitle, String[][] data) {

		List<Entity> list = new ArrayList<Entity>();
		for (int row = 1; row < data.length; row++) {

			// parsing Key
			String keyValue = data[row][0];
			Entity entity = null;
			if ((keyValue == null) || (keyValue.length() == 0)) {
				entity = new Entity(wsTitle);
			} else {
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
				entity = new Entity(key);
			}

			// Properties
			for (int col = 1; col < data[row].length; col++) {
				String value = data[row][col];
				entity.setProperty(data[0][col], value);
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

}