package gobo.dto;

import gobo.service.GbDatastoreService;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class GbEntity {

	private Key key;
	
	private List<GbProperty> properties = new ArrayList<GbProperty>();

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
	
	public void setKeyString(String key) {
		this.key = GbDatastoreService.parseKey(key);
	}

	public List<GbProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GbProperty> properties) {
		this.properties = properties;
	}

	public void addProperty(GbProperty gbProperty) {
		this.properties.add(gbProperty);
	}

}
