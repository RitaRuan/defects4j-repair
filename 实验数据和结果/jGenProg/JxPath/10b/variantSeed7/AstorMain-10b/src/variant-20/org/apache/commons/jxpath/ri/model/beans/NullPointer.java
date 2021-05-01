package org.apache.commons.jxpath.ri.model.beans;
public class NullPointer extends 


























org.apache.commons.jxpath.ri.model.beans.PropertyOwnerPointer {
	private org.apache.commons.jxpath.ri.QName name;
	private java.lang.String id;

	public NullPointer(org.apache.commons.jxpath.ri.QName name, java.util.Locale locale) {
		super(null, locale);
		this.name = name;
	}




	public NullPointer(org.apache.commons.jxpath.ri.model.NodePointer parent, org.apache.commons.jxpath.ri.QName name) {
		super(parent);
		this.name = name;
	}

	public NullPointer(java.util.Locale locale, java.lang.String id) {
		super(null, locale);
		this.id = id;
	}

	public org.apache.commons.jxpath.ri.QName getName() {
		return name;
	}

	public java.lang.Object getBaseValue() {
		return null;
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public boolean isActual() {
		return false;
	}

	public org.apache.commons.jxpath.ri.model.beans.PropertyPointer getPropertyPointer() {
		return new org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer(this);
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createPath(org.apache.commons.jxpath.JXPathContext context, java.lang.Object value) {
		if (parent != null) {
			return parent.createPath(context, value).getValuePointer();
		}
		throw new java.lang.UnsupportedOperationException(
		"Cannot create the root object: " + asPath());
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createPath(org.apache.commons.jxpath.JXPathContext context) {
		if (parent != null) {
			return parent.createPath(context).getValuePointer();
		}
		throw new java.lang.UnsupportedOperationException(
		"Cannot create the root object: " + asPath());
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createChild(
	org.apache.commons.jxpath.JXPathContext context, 
	org.apache.commons.jxpath.ri.QName name, 
	int index) 
	{
		return createPath(context).createChild(context, name, index);
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createChild(
	org.apache.commons.jxpath.JXPathContext context, 
	org.apache.commons.jxpath.ri.QName name, 
	int index, 
	java.lang.Object value) 
	{
		return createPath(context).createChild(context, name, index, value);
	}

	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}

	public boolean equals(java.lang.Object object) {
		if (object == this) {
			return true;
		}

		if (!(object instanceof org.apache.commons.jxpath.ri.model.beans.NullPointer)) {
			return false;
		}

		org.apache.commons.jxpath.ri.model.beans.NullPointer other = ((org.apache.commons.jxpath.ri.model.beans.NullPointer) (object));
		return (name == other.name) || ((name != null) && name.equals(other.name));
	}

	public java.lang.String asPath() {
		if (id != null) {
			return ("id(" + id) + ")";
		}
		return parent == null ? "null()" : super.asPath();
	}

	public int getLength() {
		return 0;
	}}