package gobo.util;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.IMHandle.Scheme;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class TestDataUtil {

	public static final String TEST_KIND = "TestKind";

	public static List<GbEntity> entities() {

		List<GbEntity> list = new ArrayList<GbEntity>();
		for (int i = 1; i <= 5; i++) {

			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(TEST_KIND, i));

			GbProperty property1 = new GbProperty();
			property1.setName("prop1");
			property1.setValueType(GbProperty.STRING);
			property1.setValue("prepare1_" + i);
			entity.addProperty(property1);

			GbProperty property2 = new GbProperty();
			property2.setName("prop2");
			property2.setValueType(GbProperty.LONG);
			property2.setValue(String.valueOf("10" + i));
			entity.addProperty(property2);

			list.add(entity);
		}
		return list;
	}

	public static List<GbEntity> entitiesWidhDiffKeys() {

		List<GbEntity> list = new ArrayList<GbEntity>();
		for (int i = 1; i <= 5; i++) {

			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(TEST_KIND, i + 3));

			GbProperty property1 = new GbProperty();
			property1.setName("prop1");
			property1.setValueType(GbProperty.STRING);
			property1.setValue("prepare2_" + i);
			entity.addProperty(property1);

			GbProperty property2 = new GbProperty();
			property2.setName("prop2");
			property2.setValueType(GbProperty.LONG);
			property2.setValue(String.valueOf("20" + i));
			entity.addProperty(property2);

			list.add(entity);
		}
		return list;
	}

	public static List<GbEntity> entitiesWithDiffProp() {

		List<GbEntity> list = new ArrayList<GbEntity>();
		for (int i = 1; i <= 5; i++) {

			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(TEST_KIND, i));

			GbProperty property1 = new GbProperty();
			property1.setName("prop3");
			property1.setValueType(GbProperty.STRING);
			property1.setValue("prepare3_" + i);
			entity.addProperty(property1);

			GbProperty property2 = new GbProperty();
			property2.setName("prop2");
			property2.setValueType(GbProperty.LONG);
			property2.setValue(String.valueOf("30" + i));
			entity.addProperty(property2);

			list.add(entity);
		}
		return list;
	}

	public static List<GbEntity> entitiesWithNull() {

		List<GbEntity> list = new ArrayList<GbEntity>();
		for (int i = 1; i <= 5; i++) {

			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(TEST_KIND, i));

			GbProperty property1 = new GbProperty();
			property1.setName("prop3");
			property1.setValueType(GbProperty.STRING);
			property1.setValue("prepare4_" + i);
			entity.addProperty(property1);

			GbProperty property2 = new GbProperty();
			property2.setName("prop2");
			property2.setValueType(GbProperty.LONG);
			if (i == 3) {
				property2.setValue(null);
			} else {
				property2.setValue(String.valueOf("40" + i));
			}
			entity.addProperty(property2);

			list.add(entity);
		}
		return list;
	}

	public static List<GbEntity> entities(String kindName) {

		List<GbEntity> list = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(kindName, i + 1));
			entity.setProperties(entities2());
			list.add(entity);
		}
		return list;
	}

	public static List<GbProperty> entities2() {

		List<GbProperty> propList = Lists.newArrayList();

		GbProperty prop1 = new GbProperty();
		prop1.setName("prop1");
		prop1.setValue(new String("a"));
		propList.add(prop1);

		GbProperty prop2 = new GbProperty();
		prop2.setName("prop2");
		prop2.setValue(new Long(1));
		propList.add(prop2);

		GbProperty prop3 = new GbProperty();
		prop3.setName("prop3");
		prop3.setValue(new Boolean(true));
		propList.add(prop3);
		return propList;
	}

	@SuppressWarnings("unchecked")
	public static List<Entity> bulkData(String kindName, Integer count) {

		List list = new ArrayList();
		List keyList = new ArrayList();
		for (int i = 0; i < count; i++) {

			Entity entity = new Entity(KeyFactory.createKey(kindName, i + 1));
			entity.setProperty("String", RandomStringUtils.randomAlphabetic(10));
			entity.setProperty("Integer", new Integer(RandomUtils.nextInt()));
			entity.setProperty("Short", new Short(RandomStringUtils.randomNumeric(1)));
			entity.setProperty("Long", new Long(RandomUtils.nextLong()));
			entity.setProperty("Boolean", new Boolean(RandomUtils.nextBoolean()));
			entity.setProperty("Float", new Float(RandomUtils.nextFloat()));
			entity.setProperty("Double", new Double(RandomUtils.nextDouble()));
			entity.setProperty("Date", new Date(RandomUtils.nextLong()));
			entity.setProperty("User", new User("test@example", "google.com"));
			entity.setProperty("Key", KeyFactory.createKey("test", RandomStringUtils
				.randomAlphabetic(5)));
			entity.setProperty("Category", new Category(RandomStringUtils.randomAlphabetic(3)));
			entity.setProperty("Email", new Email("test@example"));
			entity.setProperty("GeoPt", new GeoPt(new Float(new Integer(RandomStringUtils
				.randomNumeric(2)) - 9), new Float(
				new Integer(RandomStringUtils.randomNumeric(2)) - 9)));
			entity.setProperty("IMHandle", new IMHandle(Scheme.valueOf("sip"), RandomStringUtils
				.randomAlphabetic(2)));
			entity.setProperty("Link", new Link("test"));
			entity.setProperty("PhoneNumber", new PhoneNumber(RandomStringUtils.randomNumeric(11)));
			entity.setProperty("PostalAddress", new PostalAddress(RandomStringUtils
				.randomNumeric(7)));
			entity.setProperty("Rating", new Rating(Integer.parseInt(RandomStringUtils
				.randomNumeric(2))));

			List<String> coll = new ArrayList<String>();
			coll.add(RandomStringUtils.randomAlphabetic(3));
			coll.add(RandomStringUtils.randomAlphabetic(3));
			coll.add(RandomStringUtils.randomAlphabetic(3));
			entity.setProperty("List<String>", coll);

			List<Integer> coll2 = new ArrayList<Integer>();
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			coll2.add(new Integer(RandomStringUtils.randomNumeric(5)));
			entity.setProperty("List<Integer>", coll2);

			// Byte, Blob, Text, ShortBlob, BlobKey
			entity.setProperty("Byte", new Byte(
				RandomStringUtils.randomAlphanumeric(10).getBytes()[0]));
			entity.setProperty("Blob", new Blob(RandomStringUtils
				.randomAlphanumeric(1000)
				.getBytes()));
			entity.setProperty("Text", new Text(RandomStringUtils.randomAlphanumeric(1000)));
			entity.setProperty("ShortBlob", new ShortBlob(RandomStringUtils
				.randomAlphanumeric(500)
				.getBytes()));
			entity.setProperty("BlobKey", new BlobKey(RandomStringUtils.randomAlphanumeric(10)));

			// Make Null Property
			if (keyList.size() == 0) {
				Iterator<String> iterator = entity.getProperties().keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					keyList.add(key);
				}
			}
			int index = RandomUtils.nextInt(keyList.size());
			entity.removeProperty((String) keyList.get(index));

			list.add(entity);

		}
		return list;

	}
}
