package org.apache.commons.jxpath.ri.model.container;
public class ContainerPointer extends org.apache.commons.jxpath.ri.model.NodePointer {
	private org.apache.commons.jxpath.Container container;

	private org.apache.commons.jxpath.ri.model.NodePointer valuePointer;

	private static final long serialVersionUID = 6140752946621686118L;

	public ContainerPointer(org.apache.commons.jxpath.Container container, java.util.Locale locale) {
		super(null, locale);
		this.container = container;
	}

	public ContainerPointer(org.apache.commons.jxpath.ri.model.NodePointer parent, org.apache.commons.jxpath.Container container) {
		super(parent);
		this.container = container;
	}

	public boolean isContainer() {
		return true;
	}

	public org.apache.commons.jxpath.ri.QName getName() {
		return null;
	}

	public java.lang.Object getBaseValue() {
		return container;
	}

	public boolean isCollection() {
		java.lang.Object value = getBaseValue();
		return (value != null) && org.apache.commons.jxpath.util.ValueUtils.isCollection(value);
	}

	public int getLength() {
		java.lang.Object value = getBaseValue();
		return value == null ? 1 : org.apache.commons.jxpath.util.ValueUtils.getLength(value);
	}

	public boolean isLeaf() {
		return getValuePointer().isLeaf();
	}

	public java.lang.Object getImmediateNode() {
		java.lang.Object value = getBaseValue();
		if (index != org.apache.commons.jxpath.ri.model.NodePointer.WHOLE_COLLECTION) {
			return (index >= 0) && (index < getLength()) ? org.apache.commons.jxpath.util.ValueUtils.getValue(value, index) : null;
		}
		return org.apache.commons.jxpath.util.ValueUtils.getValue(value);
	}

	public void setValue(java.lang.Object value) {
		container.setValue(value);
	}

	public org.apache.commons.jxpath.ri.model.NodePointer getImmediateValuePointer() {
		if (valuePointer == null) {
			java.lang.Object value = getImmediateNode();
			valuePointer = org.apache.commons.jxpath.ri.model.NodePointer.newChildNodePointer(this, getName(), value);
		}
		return valuePointer;
	}

	public int hashCode() {
		return java.lang.System.identityHashCode(container) + index;
	}

	public boolean equals(java.lang.Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof org.apache.commons.jxpath.ri.model.container.ContainerPointer)) {
			return false;
		}
		org.apache.commons.jxpath.ri.model.container.ContainerPointer other = ((org.apache.commons.jxpath.ri.model.container.ContainerPointer) (object));
		return (container == other.container) && (index == other.index);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator childIterator(org.apache.commons.jxpath.ri.compiler.NodeTest test, boolean reverse, org.apache.commons.jxpath.ri.model.NodePointer startWith) {
		return getValuePointer().childIterator(test, reverse, startWith);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator attributeIterator(org.apache.commons.jxpath.ri.QName name) {
		return getValuePointer().attributeIterator(name);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator namespaceIterator() {
		return getValuePointer().namespaceIterator();
	}

	public org.apache.commons.jxpath.ri.model.NodePointer namespacePointer(java.lang.String namespace) {
		return getValuePointer().namespacePointer(namespace);
	}

	public boolean testNode(org.apache.commons.jxpath.ri.compiler.NodeTest nodeTest) {
		return getValuePointer().testNode(nodeTest);
	}

	public int compareChildNodePointers(org.apache.commons.jxpath.ri.model.NodePointer pointer1, org.apache.commons.jxpath.ri.model.NodePointer pointer2) {
		return pointer1.getIndex() - pointer2.getIndex();
	}

	public java.lang.String getNamespaceURI(java.lang.String prefix) {
		return getValuePointer().getNamespaceURI(prefix);
	}

	public java.lang.String asPath() {
		return parent == null ? "/" : parent.asPath();
	}
}