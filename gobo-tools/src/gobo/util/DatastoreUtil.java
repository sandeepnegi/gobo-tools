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
					props.put(property.getName(), property.getValue().toString());
				}
				kindInfos.put(
					org.slim3.datastore.DatastoreUtil.getKind(entityProto.getKey()),
					props);
			}
		}
		return kindInfos;
	}
}