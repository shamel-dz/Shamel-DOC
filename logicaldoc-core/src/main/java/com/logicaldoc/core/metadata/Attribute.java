package com.logicaldoc.core.metadata;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.core.folder.Folder;
import com.logicaldoc.core.security.User;

/**
 * This class defines the value of an attribute associated to an extensible
 * object. For each value, is possible to define the type and if it is mandatory
 * or not.
 * 
 * @author Matteo Caruso - LogicalDOC
 * @since 4.5.1
 */
public class Attribute implements Comparable<Attribute>, Serializable {

	private static final long serialVersionUID = 1L;

	public static final int TYPE_STRING = 0;

	public static final int TYPE_INT = 1;

	public static final int TYPE_DOUBLE = 2;

	public static final int TYPE_DATE = 3;

	public static final int TYPE_USER = 4;

	public static final int TYPE_BOOLEAN = 5;

	public static final int TYPE_FOLDER = 6;

	public static final int EDITOR_DEFAULT = 0;

	public static final int EDITOR_LISTBOX = 1;

	/**
	 * Not persistent
	 */
	private String name;

	private String label;

	private String stringValue;

	/**
	 * String representation of the multiple string values
	 */
	private String stringValues;

	private Long intValue;

	private Double doubleValue;

	private Date dateValue;

	private int type = TYPE_STRING;

	private int hidden = 0;

	private int mandatory = 0;

	private int readonly = 0;

	private int position = 0;

	private int editor = EDITOR_DEFAULT;

	/**
	 * Reference to the Attribute Set
	 */
	private Long setId;

	private int multiple = 0;

	/**
	 * Name of a parent attribute, used for multiple values attributes
	 */
	private String parent;

	/**
	 * Name of another attribute on which the value of this attribute also
	 * depends, used for managing linked presets
	 */
	private String dependsOn;

	/**
	 * Optional validation script
	 */
	private String validation;

	/**
	 * Optional script that defines the initial value
	 */
	private String initialization;

	public Attribute() {
	}

	public Attribute(Attribute source) {
		this.name = source.name;
		this.label = source.label;
		this.stringValue = source.stringValue;
		this.stringValues = source.stringValues;
		this.intValue = source.intValue;
		this.doubleValue = source.doubleValue;
		this.dateValue = source.dateValue;
		this.type = source.type;
		this.hidden = source.hidden;
		this.readonly = source.readonly;
		this.mandatory = source.mandatory;
		this.position = source.position;
		this.editor = source.editor;
		this.setId = source.setId;
		this.multiple = source.multiple;
		this.parent = source.parent;
		this.dependsOn = source.dependsOn;
		this.validation = source.validation;
		this.initialization = source.initialization;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Long getIntValue() {
		return intValue;
	}

	public void setIntValue(Long intValue) {
		this.intValue = intValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Boolean getBooleanValue() {
		if (intValue != null)
			return intValue.intValue() == 1;
		else
			return null;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.type = TYPE_BOOLEAN;
		if (booleanValue != null)
			this.intValue = booleanValue.booleanValue() ? 1L : 0L;
		else
			this.intValue = null;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Gets the attribute value. It can be as String, Long, Double or Date.
	 * 
	 * @return The attribute value as Object.
	 */
	public Object getValue() {
		switch (type) {
		case TYPE_INT:
			return getIntValue();
		case TYPE_DOUBLE:
			return getDoubleValue();
		case TYPE_DATE:
			return getDateValue();
		case TYPE_USER:
			return getIntValue();
		case TYPE_FOLDER:
			return getIntValue();
		case TYPE_BOOLEAN:
			if (getIntValue() == null)
				return null;
			else
				return getIntValue().intValue() == 1;
		default:
			return getStringValue();
		}
	}

	/**
	 * Sets the attribute value. It can be as String, Long, Double or Date.
	 * 
	 * @param value The attribute value.
	 */
	public void setValue(Object value) {
		if (value == null) {
			setStringValue(null);
			setIntValue(null);
			setDoubleValue(null);
			setDateValue(null);
			setBooleanValue(null);
		} else {
			if (value instanceof String string) {
				this.type = TYPE_STRING;
				setStringValue(string);
			} else if (value instanceof Integer integer) {
				this.type = TYPE_INT;
				setIntValue(Long.valueOf(integer));
			} else if (value instanceof Long longVal) {
				this.type = TYPE_INT;
				setIntValue(longVal);
			} else if (value instanceof Double doubleVal) {
				this.type = TYPE_DOUBLE;
				setDoubleValue(doubleVal);
			} else if (value instanceof Date date) {
				this.type = TYPE_DATE;
				setDateValue(date);
			} else if (value instanceof User user) {
				this.type = TYPE_USER;
				this.intValue = user.getId();
				this.stringValue = user.getUsername();
			} else if (value instanceof Folder folder) {
				this.type = TYPE_FOLDER;
				this.intValue = folder.getId();
				this.stringValue = folder.getName();
			} else if (value instanceof Boolean bool) {
				this.type = TYPE_BOOLEAN;
				this.intValue = bool.booleanValue() ? 1L : 0L;
			} else {
				throw new IllegalArgumentException("Not a String, Long, Double, Date, Boolean, User, Folder value");
			}
		}
	}

	/**
	 * Whether an attribute value is mandatory or not.
	 * 
	 * @return If <b>0</b>, the attribute value is not mandatory; if <b>1</b>,
	 *         the attribute value is mandatory.
	 */
	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * This is the position of the attribute into the attributes list
	 * 
	 * @return the attribute's position
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(Attribute o) {
		return Integer.compare(getPosition(), o.getPosition());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}

	public int getHidden() {
		return hidden;
	}

	public void setHidden(int hidden) {
		this.hidden = hidden;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStringValues() {
		return stringValues;
	}

	public void setStringValues(String stringValues) {
		this.stringValues = stringValues;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getInitialization() {
		return initialization;
	}

	public void setInitialization(String initialization) {
		this.initialization = initialization;
	}

	public String getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}

	public int getReadonly() {
		return readonly;
	}

	public void setReadonly(int readonly) {
		this.readonly = readonly;
	}
}