package gobo.util;

import gobo.dto.GbEntity;
import gobo.dto.GbProperty;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class DataUtil {

	public static final String TEST_KIND = "TestKind";

	public static List<GbEntity> prepare1() {

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

	public static List<GbEntity> prepare2() {

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

	public static List<GbEntity> prepare3() {

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

	public static List<GbEntity> prepare4() {

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

	public static List<GbEntity> getEntityList(String kindName) {

		List<GbEntity> list = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			GbEntity entity = new GbEntity();
			entity.setKey(KeyFactory.createKey(kindName, i + 1));
			entity.setProperties(getPropList());
			list.add(entity);
		}
		return list;
	}

	public static List<GbProperty> getPropList() {

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
}
