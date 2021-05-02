package org.apache.commons.jxpath.ri.model.dom;
public class NamespacePointer extends org.apache.commons.jxpath.ri.model.NodePointer {
	private java.lang.String prefix;

	private java.lang.String namespaceURI;

	private static final long serialVersionUID = -7622456151550131709L;

	public NamespacePointer(org.apache.commons.jxpath.ri.model.NodePointer parent, java.lang.String prefix) {
		super(parent);
		this.prefix = prefix;
	}

	public NamespacePointer(org.apache.commons.jxpath.ri.model.NodePointer parent, java.lang.String prefix, java.lang.String namespaceURI) {
		super(parent);
		this.prefix = prefix;
		this.namespaceURI = namespaceURI;
	}

	public org.apache.commons.jxpath.ri.QName getName() {
		return new org.apache.commons.jxpath.ri.QName(prefix);
	}

	public java.lang.Object getBaseValue() {
		return null;
	}

	public boolean isCollection() {
		return false;
	}

	public int getLength() {
		return 1;
	}

	public java.lang.Object getImmediateNode() {
		return getNamespaceURI();
	}

	public java.lang.String getNamespaceURI() {
		if (namespaceURI == null) {
			namespaceURI = parent.getNamespaceURI(prefix);
		}
		return namespaceURI;
	}

	public boolean isLeaf() {
		return true;
	}

	public void setValue(java.lang.Object value) {
		throw new java.lang.UnsupportedOperationException("Cannot modify DOM trees");
	}

	public boolean testNode(org.apache.commons.jxpath.ri.compiler.NodeTest nodeTest) {
		return (nodeTest == null) || ((nodeTest instanceof org.apache.commons.jxpath.ri.compiler.NodeTypeTest) && (((org.apache.commons.jxpath.ri.compiler.NodeTypeTest) (nodeTest)).getNodeType() == org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_NODE));
	}

	public java.lang.String asPath() {
		java.lang.StringBuffer buffer = new java.lang.StringBuffer();
		if (parent != null) {
			buffer.append(parent.asPath());
			if ((buffer.length() == 0) || (buffer.charAt(buffer.length() - 1) != '/')) {
				buffer.append('/');
			}
		}
		buffer.append("namespace::");
		buffer.append(prefix);
		return buffer.toString();
	}

	public int hashCode() {
		return prefix.hashCode();
	}

	public boolean equals(java.lang.Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof org.apache.commons.jxpath.ri.model.dom.NamespacePointer)) {
			return false;
		}
		org.apache.commons.jxpath.ri.model.dom.NamespacePointer other = ((org.apache.commons.jxpath.ri.model.dom.NamespacePointer) (object));
		return prefix.equals(other.prefix);
	}

	public int compareChildNodePointers(org.apache.commons.jxpath.ri.model.NodePointer pointer1, org.apache.commons.jxpath.ri.model.NodePointer pointer2) {
		return 0;
	}
}