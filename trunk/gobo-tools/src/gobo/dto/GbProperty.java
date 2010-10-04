package gobo.dto;

import gobo.service.GbDatastoreService;

import java.util.Date;

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
			throw new RuntimeException("Date type is not supported. value=" + value);
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
			String[] split = ((String) value).split(",");
			val = new User(split[0], split[1]);
		} else if (valueType.equals(GEO_PT)) {
			String[] split = ((String) value).split(",");
			val = new GeoPt(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
		} else if (valueType.equals(KEY)) {
			val = GbDatastoreService.parseKey((String) value);
		} else {
			val = value;
		}
		return val;
	}

	public String asSpreadsheetValue() {

		String val = null;
		if (value instanceof PostalAddress) {
			val = ((PostalAddress) value).getAddress();
		} else if (value instanceof PhoneNumber) {
			val = ((PhoneNumber) value).getNumber();
		} else if (value instanceof Category) {
			val = ((Category) value).getCategory();
		} else if (value instanceof Email) {
			val = ((Email) value).getEmail();
		} else if (value instanceof Rating) {
			val = String.valueOf(((Rating) value).getRating());
		} else {
			val = value.toString();
		}
		return val;
	}

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

	public String asValueType() {

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
		}
		return _valueType;
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
