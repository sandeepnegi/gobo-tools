package gobo.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import gobo.TestBase;
import gobo.dto.GbEntity;
import gobo.util.TestDataUtil;

import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class GbDatastoreServiceTest extends TestBase {

	@Test
	public void restoreOnce() {

		List<GbEntity> list = TestDataUtil.entities();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TestDataUtil.TEST_KIND)).asList(
				FetchOptions.Builder.withDefaults());

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

		List<GbEntity> list = TestDataUtil.entities();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list);

		List<GbEntity> list2 = TestDataUtil.entitiesWidhDiffKeys();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list2);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TestDataUtil.TEST_KIND)).asList(
				FetchOptions.Builder.withDefaults());
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

		List<GbEntity> list1 = TestDataUtil.entities();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list1);

		List<GbEntity> list3 = TestDataUtil.entitiesWithDiffProp();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list3);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TestDataUtil.TEST_KIND)).asList(
				FetchOptions.Builder.withDefaults());

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

		List<GbEntity> list1 = TestDataUtil.entities();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list1);

		List<GbEntity> list4 = TestDataUtil.entitiesWithNull();
		GbDatastoreService.restoreData(TestDataUtil.TEST_KIND, list4);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> asList =
			datastore.prepare(new Query(TestDataUtil.TEST_KIND)).asList(
				FetchOptions.Builder.withDefaults());

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

}
