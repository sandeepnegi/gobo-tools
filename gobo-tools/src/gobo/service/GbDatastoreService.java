package gobo.service;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.model.GbControl;
import gobo.slim3.AppEngineUtil;
import gobo.slim3.DatastoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class GbDatastoreService {

	public static List<String> getKinds() {

		List<String> kinds = new ArrayList<String>();

		if (AppEngineUtil.isProduction()) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			List<Entity> list =
				datastore.prepare(new Query("__Stat_Kind__")).asList(
					FetchOptions.Builder.withOffset(0));
			for (Entity kind : list) {
				String kindName = (String) kind.getProperty("kind_name");
				if ((kindName.startsWith("_") == false)
					&& (kindName.equals(GbControl.NAME) == false)) {
					kinds.add(kindName);
				}
			}
		} else {
			List<String> list = DatastoreUtil.getKinds();
			for (String kind : list) {
				if (kind.startsWith("_") == false) {
					kinds.add(kind);
				}
			}
		}

		return kinds;
	}

	public static List<GbProperty> getProperties(String kind) {

		List<GbProperty> list = new ArrayList<GbProperty>();
		if (AppEngineUtil.isProduction()) {

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			List<Entity> _list =
				datastore.prepare(new Query("__Stat_PropertyType_PropertyName_Kind__")).asList(
					FetchOptions.Builder.withOffset(0));
			for (Entity _kind : _list) {
				// System.out.println(_kind);
				if (_kind.getProperty("kind_name").equals(kind)) {
					GbProperty gbProperty = new GbProperty();
					gbProperty.setName((String) _kind.getProperty("property_name"));
					list.add(gbProperty);
				}
			}

		} else {

			Schema schema = DatastoreUtil.getSchema();
			List<EntityProto> entityProtoList = schema.kinds();
			EntityProto targetEntity = null;
			for (EntityProto entityProto : entityProtoList) {
				String kindName = DatastoreUtil.getKind(entityProto.getKey());
				if (kind.equals(kindName)) {
					targetEntity = entityProto;
					break;
				}
			}
			List<Property> propertys = targetEntity.propertys();
			for (Property property : propertys) {
				GbProperty gbProperty = new GbProperty();
				gbProperty.setName(property.getName());
				list.add(gbProperty);
			}
		}
		return list;
	}

	public void restoreData(String wsTitle, List<GbEntity> data) {

		List<Entity> newList = new ArrayList<Entity>();
		List<Key> orgKeyList = new ArrayList<Key>();
		for (GbEntity gbEntity : data) {

			// parsing Key
			Entity entity = null;
			Key key = gbEntity.getKey();
			if (key == null) {
				entity = new Entity(wsTitle);
			} else {
				entity = new Entity(key);
				orgKeyList.add(key);
			}

			// Properties
			for (GbProperty gbProperty : gbEntity.getProperties()) {
				try {
					entity.setProperty(gbProperty.getName(), gbProperty.asDatastoreValue());
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
				}
			}
			System.out.println(entity);
			newList.add(entity);
		}

		// Merge
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Map<Key, Entity> orgEntities = ds.get(orgKeyList);
		for (Entity newEntity : newList) {
			Key key = newEntity.getKey();
			if (orgEntities.containsKey(key)) {
				Entity orgEntity = orgEntities.get(key);
				newEntity.setPropertiesFrom(orgEntity);
			}
		}
		ds.put(newList);
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
				key = KeyFactory.createKey(key, kind, Long.parseLong(keyVal));
			}
		}
		return key;
	}

}