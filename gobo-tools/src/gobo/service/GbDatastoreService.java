package gobo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slim3.util.AppEngineUtil;
import org.slim3.util.ClassUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Property;

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
		for (int row = 2; row < data.length; row++) {

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
				final String propName = data[0][col];
				final String propType = data[1][col];
				final String value = data[row][col];
				//Object typedValue = asValueType(propType, value);
				//entity.setProperty(propName, typedValue);
				entity.setProperty(propName, value);
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

	Map<String, Class<?>> map = new HashMap<String, Class<?>>();
	{
		map.put("stringValue", String.class);
		map.put("int64Value", Long.class);
		map.put("doubleValue", Double.class);
		map.put("booleanValue", Boolean.class);
		map.put("UserValue", User.class);
		map.put("ReferenceValue", Key.class);
	}

	Object asValueType(String type, String value) {

		Class<?> clazz = map.get(type);
		Object instance = ClassUtil.newInstance(clazz);
		Object dataType = null;
		if (instance instanceof String) {
			dataType = value;
		} else if (instance instanceof Long) {
			dataType = new Long(value);
		} else if (instance instanceof Double) {
			dataType = new Double(value);
		} else if (instance instanceof Boolean) {
			dataType = new Boolean(value);
		} else if (instance instanceof User) {
			// dataType = new User(value);
			throw new RuntimeException("User type is not supported. value=" + value);
		} else if (instance instanceof Key) {
			// dataType = new User(value);
			throw new RuntimeException("ReferenceValue type is not supported." + value);
		}
		return dataType;
	}
}