package gobo.dto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class GbEntityTest extends TestBase {
	
	@Test
	public void keyId() {

		GbEntity entity = new GbEntity();
		Key key = KeyFactory.createKey("TestKind", 1);
		entity.setKey(key);

		String string = entity.getKey().toString();
		assertThat(string, is("TestKind(1)"));
		
		GbEntity entity2 = new GbEntity();
		entity2.setKeyString(string);
		assertEquals(entity2.getKey(), key);
	}
	
	@Test
	public void keyName() {

		GbEntity entity = new GbEntity();
		Key key = KeyFactory.createKey("TestKind", "abc");
		entity.setKey(key);

		String string = entity.getKey().toString();
		assertThat(string, is("TestKind(\"abc\")"));
		
		GbEntity entity2 = new GbEntity();
		entity2.setKeyString(string);
		assertEquals(entity2.getKey(), key);
	}
	
	@Test
	public void parentKeyId() {

		GbEntity entity = new GbEntity();
		Key parent = KeyFactory.createKey("TestKind", 1);
		Key key = KeyFactory.createKey(parent, "Child", 77);
		entity.setKey(key);

		String string = entity.getKey().toString();
		assertThat(string, is("TestKind(1)/Child(77)"));
		
		entity.setKeyString(string);
		assertEquals(entity.getKey(), key);
	}

	@Test
	public void parentKeyName() {

		GbEntity entity = new GbEntity();
		Key parent = KeyFactory.createKey("TestKind", 1);
		Key key = KeyFactory.createKey(parent, "Child", "abc");
		entity.setKey(key);

		String string = entity.getKey().toString();
		assertThat(string, is("TestKind(1)/Child(\"abc\")"));
		
		entity.setKeyString(string);
		assertEquals(entity.getKey(), key);
	}
}