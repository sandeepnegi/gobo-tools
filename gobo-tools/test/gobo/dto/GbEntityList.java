package gobo.dto;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Cursor;

@SuppressWarnings({ "serial", "hiding" })
public class GbEntityList<GbEntity> extends ArrayList<GbEntity> {

	private Cursor cursor;

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
}
