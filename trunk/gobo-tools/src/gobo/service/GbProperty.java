package gobo.service;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.users.User;

public class GbProperty {

	private String name;

	private String valueType;

	private String value;

	public Object asTypedValue() {

		Object val = null;
		if ((valueType == null) || (valueType.length() == 0)) {
			val = value;
		} else if (valueType.equals(GbSpreadsheetService.STRING)) {
			val = value;
		} else if (valueType.equals(GbSpreadsheetService.LONG)) {
			val = new Long(value);
		} else if (valueType.equals(GbSpreadsheetService.DOUBLE)) {
			val = new Double(value);
		} else if (valueType.equals(GbSpreadsheetService.BOOLEAN)) {
			val = new Boolean(value);
		} else if (valueType.equals(GbSpreadsheetService.DATE)) {
			throw new RuntimeException("Date type is not supported. value=" + value);
		} else if (valueType.equals(GbSpreadsheetService.CATEGORY)) {
			val = new Category(value);
		} else if (valueType.equals(GbSpreadsheetService.EMAIL)) {
			val = new Email(value);
		} else if (valueType.equals(GbSpreadsheetService.PHONE_NUMBER)) {
			val = new PhoneNumber(value);
		} else if (valueType.equals(GbSpreadsheetService.POSTAL_ADDRESS)) {
			val = new PostalAddress(value);
		} else if (valueType.equals(GbSpreadsheetService.RATING)) {
			val = new Rating(Integer.parseInt(value));
		} else if (valueType.equals(GbSpreadsheetService.USER)) {
			String[] split = value.split(",");
			val = new User(split[0], split[1]);
		} else if (valueType.equals(GbSpreadsheetService.GEO_PT)) {
			String[] split = value.split(",");
			val = new GeoPt(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
		} else if (valueType.equals(GbSpreadsheetService.KEY)) {
			val = GbDatastoreService.parseKey(value);
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
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the data
	 */
	public String getValue() {
		return value;
	}

}
