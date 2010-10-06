package gobo.service;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;
import gobo.meta.GbControlMeta;

import java.util.ArrayList;
import java.util.List;

import org.slim3.datastore.DatastoreUtil;
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
				String kindName = (String) kind.getProperty("kind_name");
				if ((kindName.startsWith("__") == false)
					&& (kindName.equals(GbControlMeta.get().getKind()) == false)) {
					kinds.add(kindName);
				}
			}
		} else {
			kinds = org.slim3.datastore.DatastoreUtil.getKinds();
		}

		return kinds;
	}

	public static List<GbProperty> getProperties(String kind) {

		// Entity entity = Datastore.query(kind).limit(1).asList().get(0);
		// Map<String, Object> properties = entity.getProperties();
		// List<GbProperty> list = new ArrayList<GbProperty>();
		// for (String name : properties.keySet()) {
		// GbProperty gbProperty = new GbProperty();
		// gbProperty.setName(name);
		// gbProperty.setValue(properties.get(name));
		// list.add(gbProperty);
		// }
		// return list;

		List<GbProperty> list = new ArrayList<GbProperty>();
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
		return list;
	}

	public void restoreData(String wsTitle, List<GbEntity> data) {

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
					entity.setProperty(gbProperty.getName(), gbProperty.asDatastoreValue());
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