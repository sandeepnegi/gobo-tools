package gobo.slim3;

import java.util.ArrayList;
import java.util.List;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb.GetSchemaRequest;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Reference;
import com.google.storage.onestore.v3.OnestoreEntity.Path.Element;

/**
 * This file is a part of copied from Slim3 by kenji.ishii77 in 2010/10/09.
 * 
 */
public class DatastoreUtil {

	private static final String DATASTORE_SERVICE = "datastore_v3";

	private static final String GET_SCHEMA_METHOD = "GetSchema";

	/**
	 * Returns a schema.
	 * 
	 * @return a schema
	 * @throws IllegalStateException
	 *             if this method is called on production server
	 */
	public static Schema getSchema() throws IllegalStateException {
		if (AppEngineUtil.isProduction()) {
			throw new IllegalStateException("This method does not work on production server.");
		}
		GetSchemaRequest req = new GetSchemaRequest();
		req.setApp(ApiProxy.getCurrentEnvironment().getAppId());
		byte[] resBuf =
			ApiProxy.makeSyncCall(DATASTORE_SERVICE, GET_SCHEMA_METHOD, req.toByteArray());
		Schema schema = new Schema();
		schema.mergeFrom(resBuf);
		return schema;
	}

	/**
	 * Returns a list of kinds.
	 * 
	 * @return a list of kinds
	 * @throws IllegalStateException
	 *             if this method is called on production server
	 */
	public static List<String> getKinds() throws IllegalStateException {
		if (AppEngineUtil.isProduction()) {
			throw new IllegalStateException("This method does not work on production server.");
		}
		Schema schema = getSchema();
		List<EntityProto> entityProtoList = schema.kinds();
		List<String> kindList = new ArrayList<String>(entityProtoList.size());
		for (EntityProto entityProto : entityProtoList) {
			kindList.add(getKind(entityProto.getKey()));
		}
		return kindList;
	}

	/**
	 * Returns a leaf kind.
	 * 
	 * @param key
	 *            the key
	 * @return a list of kinds
	 */
	public static String getKind(Reference key) {
		List<Element> elements = key.getPath().elements();
		return elements.get(elements.size() - 1).getType();
	}

}
