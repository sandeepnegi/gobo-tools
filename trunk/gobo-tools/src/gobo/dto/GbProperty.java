package gobo.dto;

import gobo.service.GbDatastoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.users.User;

public class GbProperty {

	private String name;

	private String valueType;

	private Object value;

	public final static String STRING = "String";
	public final static String INTEGER = "Integer";
	public final static String SHORT = "Short";
	public final static String LONG = "Long";
	public final static String BOOLEAN = "Boolean";
	public final static String FLOAT = "Float";
	public final static String DOUBLE = "Double";
	public final static String DATE = "Date";
	public final static String USER = "User";
	public final static String KEY = "Key";
	public final static String CATEGORY = "Category";
	public final static String EMAIL = "Email";
	public final static String GEO_PT = "GeoPt";
	public final static String IMHANDLE = "IMHandle";
	public final static String LINK = "Link";
	public final static String PHONE_NUMBER = "PhoneNumber";
	public final static String POSTAL_ADDRESS = "PostalAddress";
	public final static String RATING = "Rating";
	public final static String LIST = "List";
	public final static String SET = "Set";
	public final static String SORTED_SET = "SortedSet";

	// private static final SimpleDateFormat DATE_FORMAT =
	// new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

	private static final String VALUE_SEPARATER = "/";

	public String asSpreadsheetValueType() {

		String _valueType = null;
		if (value instanceof String) {
			_valueType = STRING;
		} else if (value instanceof Integer) {
			_valueType = INTEGER;
		} else if (value instanceof Short) {
			_valueType = SHORT;
		} else if (value instanceof Long) {
			_valueType = LONG;
		} else if (value instanceof Boolean) {
			_valueType = BOOLEAN;
		} else if (value instanceof Float) {
			_valueType = FLOAT;
		} else if (value instanceof Double) {
			_valueType = DOUBLE;
		} else if (value instanceof Date) {
			_valueType = DATE;
		} else if (value instanceof User) {
			_valueType = USER;
		} else if (value instanceof Key) {
			_valueType = KEY;
		} else if (value instanceof Category) {
			_valueType = CATEGORY;
		} else if (value instanceof Email) {
			_valueType = EMAIL;
		} else if (value instanceof GeoPt) {
			_valueType = GEO_PT;
		} else if (value instanceof IMHandle) {
			_valueType = IMHANDLE;
		} else if (value instanceof Link) {
			_valueType = LINK;
		} else if (value instanceof PhoneNumber) {
			_valueType = PHONE_NUMBER;
		} else if (value instanceof PostalAddress) {
			_valueType = POSTAL_ADDRESS;
		} else if (value instanceof Rating) {
			_valueType = RATING;
		} else if (value instanceof Collection<?>) {
			Object object = ((Collection<?>) value).iterator().next();
			GbProperty inner = new GbProperty();
			inner.setValue(object);
			final String innerValueType = inner.asSpreadsheetValueType();
			if (value instanceof List<?>) {
				_valueType = LIST + "<" + innerValueType + ">";
			} else if (value instanceof Set<?>) {
				_valueType = SET + "<" + innerValueType + ">";
			} else if (value instanceof SortedSet<?>) {
				_valueType = SORTED_SET + "<" + innerValueType + ">";
			}
		}
		return _valueType;
	}

	public String asSpreadsheetValue() {

		String val = null;
		if (value instanceof Date) {
			// val = DATE_FORMAT.format(((Date) value));
			val = String.valueOf(((Date) value).getTime());
		} else if (value instanceof User) {
			User user = (User) value;
			val =
				user.getEmail()
					+ VALUE_SEPARATER
					+ user.getAuthDomain()
					+ VALUE_SEPARATER
					+ user.getUserId()
					+ VALUE_SEPARATER
					+ user.getFederatedIdentity();
		} else if (value instanceof Category) {
			val = ((Category) value).getCategory();
		} else if (value instanceof Email) {
			val = ((Email) value).getEmail();
		} else if (value instanceof GeoPt) {
			GeoPt geoPt = (GeoPt) value;
			val =
				Float.valueOf(geoPt.getLatitude())
					+ VALUE_SEPARATER
					+ Float.valueOf(geoPt.getLongitude());
		} else if (value instanceof IMHandle) {
			val = ((IMHandle) value).toString();
		} else if (value instanceof Link) {
			val = ((Link) value).toString();
		} else if (value instanceof PhoneNumber) {
			val = ((PhoneNumber) value).getNumber();
		} else if (value instanceof PostalAddress) {
			val = ((PostalAddress) value).getAddress();
		} else if (value instanceof Rating) {
			val = String.valueOf(((Rating) value).getRating());
		} else if (value instanceof Collection<?>) {
			Iterator<?> it = ((Collection<?>) value).iterator();
			GbProperty inner = new GbProperty();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				Object next = it.next();
				inner.setValue(next);
				final String innerValue = inner.asSpreadsheetValue();
				sb.append(innerValue + ",");
			}
			val = sb.toString().substring(0, sb.length() - 1);
		} else {
			val = value.toString();
		}
		return val;
	}

	public Object asDatastoreValue() {

		Object val = null;
		if ((valueType == null) || (valueType.length() == 0)) {
			val = value;
		} else if (valueType.equals(STRING)) {
			val = value;
		} else if (valueType.equals(LONG)) {
			val = new Long((String) value);
		} else if (valueType.equals(DOUBLE)) {
			val = new Double((String) value);
		} else if (valueType.equals(BOOLEAN)) {
			val = new Boolean((String) value);
		} else if (valueType.equals(DATE)) {
			// try {
			// val = DATE_FORMAT.parse((String) value);
			// } catch (ParseException e) {
			// throw new RuntimeException(e);
			// }
			val = new Date(Long.parseLong((String) value));
		} else if (valueType.equals(CATEGORY)) {
			val = new Category((String) value);
		} else if (valueType.equals(EMAIL)) {
			val = new Email((String) value);
		} else if (valueType.equals(PHONE_NUMBER)) {
			val = new PhoneNumber((String) value);
		} else if (valueType.equals(POSTAL_ADDRESS)) {
			val = new PostalAddress((String) value);
		} else if (valueType.equals(RATING)) {
			val = new Rating(Integer.parseInt((String) value));
		} else if (valueType.equals(USER)) {
			String[] split = ((String) value).split("/");
			val = new User(split[0], split[1], split[2], split[3]);
		} else if (valueType.equals(GEO_PT)) {
			String[] split = ((String) value).split("/");
			val = new GeoPt(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
		} else if (valueType.equals(KEY)) {
			val = GbDatastoreService.parseKey((String) value);
		} else if (valueType.startsWith(LIST + "<")) {
			List<Object> list = new ArrayList<Object>();
			String[] split = ((String) value).split(",");
			for (int i = 0; i < split.length; i++) {
				GbProperty innser = new GbProperty();
				innser.setValue(split[i]);
				list.add(innser.asDatastoreValue());
			}
			val = list;
		} else if (valueType.startsWith(SET + "<")) {
			Set<Object> list = new HashSet<Object>();
			String[] split = ((String) value).split(",");
			for (int i = 0; i < split.length; i++) {
				GbProperty innser = new GbProperty();
				innser.setValue(split[i]);
				list.add(innser.asDatastoreValue());
			}
			val = list;
		} else if (valueType.startsWith(SORTED_SET + "<")) {
			SortedSet<Object> list = new TreeSet<Object>();
			String[] split = ((String) value).split(",");
			for (int i = 0; i < split.length; i++) {
				GbProperty innser = new GbProperty();
				innser.setValue(split[i]);
				list.add(innser.asDatastoreValue());
			}
			val = list;
		} else {
			val = value;
		}
		return val;
	}

	/**
	 * @param name
	 *            the columnName to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the columnName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param valueType
	 *            the valueType to set
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return the valueType
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 * @param value
	 *            the data to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the data
	 */
	public Object getValue() {
		return value;
	}

}
