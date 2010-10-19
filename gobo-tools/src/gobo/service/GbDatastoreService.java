package gobo.service;

import gobo.dto.GbEntity;
import gobo.dto.GbEntityList;
import gobo.dto.GbProperty;
import gobo.model.GbControl;
import gobo.slim3.AppEngineUtil;
import gobo.slim3.DatastoreUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Property;

public class GbDatastoreService {

	private static final Logger logger = Logger.getLogger(GbDatastoreService.class.getName());

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
			List<String> propNameList = Lists.newArrayList();
			for (Entity _kind : _list) {
				if (_kind.getProperty("kind_name").equals(kind)) {
					String propName = (String) _kind.getProperty("property_name");
					GbProperty gbProperty = new GbProperty();
					gbProperty.setName(propName);

					// Different type and same name properties are returned in
					// production! and here comparing only property_name.
					if (propNameList.contains(propName) == false) {
						list.add(gbProperty);
						propNameList.add(propName);
					}
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
			if (targetEntity == null) {
				throw new RuntimeException("The specified kind has no property");
			}
			List<Property> propertys = targetEntity.propertys();
			for (Property property : propertys) {
				GbProperty gbProperty = new GbProperty();
				gbProperty.setName(property.getName());
				list.add(gbProperty);
			}

		}
		logger.fine(list.toString());
		return list;
	}

	public static GbEntityList<GbEntity> getData(Cursor cursor, String kind, Integer range) {

		// Get Data
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery query = datastore.prepare(new Query(kind));
		FetchOptions fetchOptions = null;
		if (cursor == null) {
			fetchOptions = FetchOptions.Builder.withLimit(range);
		} else {
			fetchOptions = FetchOptions.Builder.withStartCursor(cursor).limit(range);
		}
		QueryResultList<Entity> data = query.asQueryResultList(fetchOptions);

		// Re-package.
		GbEntityList<GbEntity> list = new GbEntityList<GbEntity>();
		for (Entity entity : data) {
			GbEntity gbEntity = new GbEntity();
			gbEntity.setKey(entity.getKey());
			Set<String> propNames = entity.getProperties().keySet();
			for (String propName : propNames) {
				GbProperty gbProperty = new GbProperty();
				gbProperty.setName(propName);
				gbProperty.setValue(entity.getProperty(propName));
				gbEntity.addProperty(gbProperty);
			}
			list.add(gbEntity);
		}

		// Set cursor
		list.setCursor(data.getCursor());

		return list;
	}

	public static void restoreData(String wsTitle, List<GbEntity> data) {

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
					logger.warning(e.getMessage());
				}
			}
			logger.fine(entity.toString());
			newList.add(entity);
		}

		// Merge
		List<Entity> mergedList = Lists.newArrayList();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		final Map<Key, Entity> orgEntityMap = ds.get(orgKeyList);
		for (Entity newEntity : newList) {
			if (orgEntityMap.containsKey(newEntity.getKey())) {
				Entity orgEntity = orgEntityMap.get(newEntity.getKey());
				final Map<String, Object> newProps = newEntity.getProperties();
				Iterator<String> it = newProps.keySet().iterator();
				while (it.hasNext()) {
					final String newProp = it.next();
					orgEntity.setProperty(newProp, newProps.get(newProp));
				}
				mergedList.add(orgEntity);
			} else {
				mergedList.add(newEntity);
			}
		}

		ds.put(mergedList);
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