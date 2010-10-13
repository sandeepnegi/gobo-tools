package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbEntity;
import gobo.dto.GbProperty;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class GbDatastoreServiceTest extends TestBase {

	private static final String TEST_KIND = "TestKind";

	@Test
	public void restoreOnce() {

		GbDatastoreService ds = new GbDatastoreService();
		List<GbEntity> list = prepare1();
		ds.restoreData(TEST_KIND, list);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TEST_KIND)).asList(FetchOptions.Builder.withDefaults());

		assertThat(asList.size(), is(5));

		assertThat(asList.get(0).getProperties().size(), is(2));
		assertThat(asList.get(0).getProperty("prop1").toString(), is("prepare1_1"));
		assertThat(asList.get(0).getProperty("prop2").toString(), is("101"));

		assertThat(asList.get(1).getProperties().size(), is(2));
		assertThat(asList.get(1).getProperty("prop1").toString(), is("prepare1_2"));
		assertThat(asList.get(1).getProperty("prop2").toString(), is("102"));

		assertThat(asList.get(2).getProperties().size(), is(2));
		assertThat(asList.get(2).getProperty("prop1").toString(), is("prepare1_3"));
		assertThat(asList.get(2).getProperty("prop2").toString(), is("103"));

		assertThat(asList.get(3).getProperties().size(), is(2));
		assertThat(asList.get(3).getProperty("prop1").toString(), is("prepare1_4"));
		assertThat(asList.get(3).getProperty("prop2").toString(), is("104"));

		assertThat(asList.get(4).getProperties().size(), is(2));
		assertThat(asList.get(4).getProperty("prop1").toString(), is("prepare1_5"));
		assertThat(asList.get(4).getProperty("prop2").toString(), is("105"));
	}

	@Test
	public void restoreTwice() {

		GbDatastoreService ds = new GbDatastoreService();
		List<GbEntity> list = prepare1();
		ds.restoreData(TEST_KIND, list);

		List<GbEntity> list2 = prepare2();
		ds.restoreData(TEST_KIND, list2);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TEST_KIND)).asList(FetchOptions.Builder.withDefaults());
		assertThat(asList.size(), is(8));

		assertThat(asList.get(0).getProperties().size(), is(2));
		assertThat(asList.get(0).getProperty("prop1").toString(), is("prepare1_1"));
		assertThat(asList.get(0).getProperty("prop2").toString(), is("101"));

		assertThat(asList.get(1).getProperties().size(), is(2));
		assertThat(asList.get(1).getProperty("prop1").toString(), is("prepare1_2"));
		assertThat(asList.get(1).getProperty("prop2").toString(), is("102"));

		assertThat(asList.get(2).getProperties().size(), is(2));
		assertThat(asList.get(2).getProperty("prop1").toString(), is("prepare1_3"));
		assertThat(asList.get(2).getProperty("prop2").toString(), is("103"));

		assertThat(asList.get(3).getProperties().size(), is(2));
		assertThat(asList.get(3).getProperty("prop1").toString(), is("prepare2_1"));
		assertThat(asList.get(3).getProperty("prop2").toString(), is("201"));

		assertThat(asList.get(4).getProperties().size(), is(2));
		assertThat(asList.get(4).getProperty("prop1").toString(), is("prepare2_2"));
		assertThat(asList.get(4).getProperty("prop2").toString(), is("202"));

		assertThat(asList.get(5).getProperties().size(), is(2));
		assertThat(asList.get(5).getProperty("prop1").toString(), is("prepare2_3"));
		assertThat(asList.get(5).getProperty("prop2").toString(), is("203"));

		assertThat(asList.get(6).getProperties().size(), is(2));
		assertThat(asList.get(6).getProperty("prop1").toString(), is("prepare2_4"));
		assertThat(asList.get(6).getProperty("prop2").toString(), is("204"));

		assertThat(asList.get(7).getProperties().size(), is(2));
		assertThat(asList.get(7).getProperty("prop1").toString(), is("prepare2_5"));
		assertThat(asList.get(7).getProperty("prop2").toString(), is("205"));

	}

	@Test
	public void restoreWithDifferentProp() {

		GbDatastoreService ds = new GbDatastoreService();
		List<GbEntity> list1 = prepare1();
		ds.restoreData(TEST_KIND, list1);

		List<GbEntity> list3 = prepare3();
		ds.restoreData(TEST_KIND, list3);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TEST_KIND)).asList(FetchOptions.Builder.withDefaults());

		assertThat(asList.size(), is(5));
		assertThat(asList.get(0).getProperties().size(), is(3));
		assertThat(asList.get(0).getProperty("prop1").toString(), is("prepare1_1"));
		assertThat(asList.get(0).getProperty("prop2").toString(), is("301"));
		assertThat(asList.get(0).getProperty("prop3").toString(), is("prepare3_1"));

		assertThat(asList.get(1).getProperties().size(), is(3));
		assertThat(asList.get(1).getProperty("prop1").toString(), is("prepare1_2"));
		assertThat(asList.get(1).getProperty("prop2").toString(), is("302"));
		assertThat(asList.get(1).getProperty("prop3").toString(), is("prepare3_2"));

		assertThat(asList.get(2).getProperties().size(), is(3));
		assertThat(asList.get(2).getProperty("prop1").toString(), is("prepare1_3"));
		assertThat(asList.get(2).getProperty("prop2").toString(), is("303"));
		assertThat(asList.get(2).getProperty("prop3").toString(), is("prepare3_3"));

		assertThat(asList.get(3).getProperties().size(), is(3));
		assertThat(asList.get(3).getProperty("prop1").toString(), is("prepare1_4"));
		assertThat(asList.get(3).getProperty("prop2").toString(), is("304"));
		assertThat(asList.get(3).getProperty("prop3").toString(), is("prepare3_4"));

		assertThat(asList.get(4).getProperties().size(), is(3));
		assertThat(asList.get(4).getProperty("prop1").toString(), is("prepare1_5"));
		assertThat(asList.get(4).getProperty("prop2").toString(), is("305"));
		assertThat(asList.get(4).getProperty("prop3").toString(), is("prepare3_5"));

	}

	@Test
	public void restoreWithNullValue() {

		GbDatastoreService ds = new GbDatastoreService();
		List<GbEntity> list1 = prepare1();
		ds.restoreData(TEST_KIND, list1);

		List<GbEntity> list4 = prepare4();
		ds.restoreData(TEST_KIND, list4);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TEST_KIND)).asList(FetchOptions.Builder.withDefaults());

		System.out.println(asList);
		
		assertThat(asList.size(), is(5));
		assertThat(asList.get(0).getProperties().size(), is(3));
		assertThat(asList.get(0).getProperty("prop1").toString(), is("prepare1_1"));
		assertThat(asList.get(0).getProperty("prop2").toString(), is("401"));
		assertThat(asList.get(0).getProperty("prop3").toString(), is("prepare4_1"));

		assertThat(asList.get(1).getProperties().size(), is(3));
		assertThat(asList.get(1).getProperty("prop1").toString(), is("prepare1_2"));
		assertThat(asList.get(1).getProperty("prop2").toString(), is("402"));
		assertThat(asList.get(1).getProperty("prop3").toString(), is("prepare4_2"));

		assertThat(asList.get(2).getProperties().size(), is(3));
		assertThat(asList.get(2).getProperty("prop1").toString(), is("prepare1_3"));
		assertNull(asList.get(2).getProperty("prop2"));
		assertThat(asList.get(2).getProperty("prop3").toString(), is("prepare4_3"));

		assertThat(asList.get(3).getProperties().size(), is(3));
		assertThat(asList.get(3).getProperty("prop1").toString(), is("prepare1_4"));
		assertThat(asList.get(3).getProperty("prop2").toString(), is("404"));
		assertThat(asList.get(3).getProperty("prop3").toString(), is("prepare4_4"));

		assertThat(asList.get(4).getProperties().size(), is(3));
		assertThat(asList.get(4).getProperty("prop1").toString(), is("prepare1_5"));
		assertThat(asList.get(4).getProperty("prop2").toString(), is("405"));
		assertThat(asList.get(4).getProperty("prop3").toString(), is("prepare4_5"));

	}

	List<GbEntity> prepare1() {

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

	List<GbEntity> prepare2() {

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

	List<GbEntity> prepare3() {

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

	List<GbEntity> prepare4() {

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
}
