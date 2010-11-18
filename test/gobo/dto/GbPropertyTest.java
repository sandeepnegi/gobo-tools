package gobo.dto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.IMHandle.Scheme;
import com.google.appengine.api.users.User;

import gobo.TestBase;

public class GbPropertyTest extends TestBase {

	private Entity getEntity;

	@Before
	public void setUp() {
		super.setUp();

		Key testKey = KeyFactory.createKey("TestData", "a");
		Entity entity = new Entity(testKey);
		entity.setProperty(GbProperty.BOOLEAN, new Boolean(RandomUtils.nextBoolean()));
		entity.setProperty(GbProperty.STRING, RandomStringUtils.randomAlphabetic(10));
		entity.setProperty(GbProperty.BYTE, RandomStringUtils.randomAlphabetic(1).getBytes()[0]);
		entity.setProperty(GbProperty.SHORT, new Short(RandomStringUtils.randomNumeric(1)));
		entity.setProperty(GbProperty.INTEGER, new Integer(RandomUtils.nextInt()));
		entity.setProperty(GbProperty.LONG, new Long(RandomUtils.nextLong()));
		entity.setProperty(GbProperty.FLOAT, new Float(RandomUtils.nextFloat()));
		entity.setProperty(GbProperty.DOUBLE, new Double(RandomUtils.nextDouble()));
		entity.setProperty(GbProperty.USER, new User(
			RandomStringUtils.randomAlphanumeric(20),
			RandomStringUtils.randomAlphanumeric(20)));
		entity.setProperty(GbProperty.KEY + "Id", KeyFactory.createKey(RandomStringUtils
			.randomAlphabetic(5), RandomUtils.nextLong()));
		entity.setProperty(GbProperty.KEY + "Name", KeyFactory.createKey(RandomStringUtils
			.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5)));
		entity.setProperty(GbProperty.BLOB, new Blob(RandomStringUtils
			.randomAlphanumeric(1000)
			.getBytes()));
		entity.setProperty(GbProperty.TEXT, new Text(RandomStringUtils.randomAlphanumeric(1000)));
		entity.setProperty(GbProperty.DATE, new Date(RandomUtils.nextLong()));
		entity.setProperty(GbProperty.LINK, new Link(RandomStringUtils.randomAlphanumeric(30)));
		entity.setProperty(GbProperty.SHORT_BLOB, new ShortBlob(RandomStringUtils
			.randomAlphanumeric(500)
			.getBytes()));
		entity.setProperty(
			GbProperty.GEO_PT,
			new GeoPt(new Float(new Integer(RandomStringUtils.randomNumeric(2)) - 9), new Float(
				new Integer(RandomStringUtils.randomNumeric(2)) - 9)));
		entity
			.setProperty(GbProperty.CATEGORY, new Category(RandomStringUtils.randomAlphabetic(3)));
		entity.setProperty(GbProperty.RATING, new Rating(Integer.parseInt(RandomStringUtils
			.randomNumeric(2))));
		entity.setProperty(GbProperty.PHONE_NUMBER, new PhoneNumber(RandomStringUtils
			.randomNumeric(11)));
		entity.setProperty(GbProperty.POSTAL_ADDRESS, new PostalAddress(RandomStringUtils
			.randomNumeric(7)));
		entity.setProperty(GbProperty.EMAIL, new Email(RandomStringUtils.randomAlphanumeric(20)));
		entity.setProperty(GbProperty.IMHANDLE, new IMHandle(
			Scheme.valueOf("sip"),
			RandomStringUtils.randomAlphabetic(2)));
		entity.setProperty(GbProperty.BLOB_KEY, new BlobKey(RandomStringUtils
			.randomAlphanumeric(10)));

		List<String> coll = new ArrayList<String>();
		coll.add(RandomStringUtils.randomAlphabetic(3));
		coll.add(RandomStringUtils.randomAlphabetic(3));
		coll.add(RandomStringUtils.randomAlphabetic(3));
		entity.setProperty(GbProperty.LIST + "String", coll);

		List<Integer> coll2 = new ArrayList<Integer>();
		coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
		coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
		coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
		entity.setProperty(GbProperty.LIST + "Integer", coll2);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(entity);

		DatastoreService ds2 = DatastoreServiceFactory.getDatastoreService();
		try {
			getEntity = ds2.get(testKey);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		super.tearDown();

		// DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		// ds.delete(getEntity.getKey());
	}

	@Test
	public void testBoolean() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.BOOLEAN);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.BOOLEAN));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testString() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.STRING);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.STRING));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testByte() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.BYTE);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LONG));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testShort() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.SHORT);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LONG));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testInteger() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.INTEGER);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LONG));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testLong() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.LONG);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LONG));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testFloat() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.FLOAT);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.DOUBLE));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testDouble() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.DOUBLE);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.DOUBLE));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testUser() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.USER);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.USER));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testKeyId() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.KEY + "Id");
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.KEY));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testKeyName() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.KEY + "Name");
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.KEY));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test(expected = RuntimeException.class)
	public void testBlog() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.BLOB);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.BLOB));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		prop2.asDatastoreValue();
	}
	
	@Test(expected = RuntimeException.class)
	public void testText() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.TEXT);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.TEXT));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
//		final Object asDatastoreValue = prop2.asDatastoreValue();
//		assertThat(asDatastoreValue, equalTo(org));
		prop2.asDatastoreValue();
	}
	
	@Test
	public void testDate() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.DATE);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.DATE));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testLink() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.LINK);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LINK));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test(expected = RuntimeException.class)
	public void testShorBlob() {

		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.SHORT_BLOB);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.SHORT_BLOB));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		prop2.asDatastoreValue();
	}
	
	@Test
	public void testGeoPt() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.GEO_PT);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.GEO_PT));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testCategory() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.CATEGORY);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.CATEGORY));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testRating() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.RATING);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.RATING));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testPhoneNumber() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.PHONE_NUMBER);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.PHONE_NUMBER));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testPostalAddress() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.POSTAL_ADDRESS);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.POSTAL_ADDRESS));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}
	
	@Test
	public void testEmail() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.EMAIL);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.EMAIL));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testIMHandle() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.IMHANDLE);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.IMHANDLE));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testBlobKey() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.BLOB_KEY);
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.BLOB_KEY));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}

	@Test
	public void testListString() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.LIST + "String");
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LIST + "<String>"));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}
	
	@Test
	public void testListInteger() {
		
		GbProperty prop = new GbProperty();
		final Object org = getEntity.getProperty(GbProperty.LIST + "Integer");
		prop.setValue(org);

		final String asSpreadsheetValueType = prop.asSpreadsheetValueType();
		final String asSpreadsheetValue = prop.asSpreadsheetValue();
		assertThat(asSpreadsheetValueType, equalTo(GbProperty.LIST + "<Long>"));
		System.out.println(asSpreadsheetValueType + ": " + asSpreadsheetValue);

		GbProperty prop2 = new GbProperty();
		prop2.setValue(asSpreadsheetValue);
		prop2.setValueType(asSpreadsheetValueType);
		final Object asDatastoreValue = prop2.asDatastoreValue();
		assertThat(asDatastoreValue, equalTo(org));
	}
}
