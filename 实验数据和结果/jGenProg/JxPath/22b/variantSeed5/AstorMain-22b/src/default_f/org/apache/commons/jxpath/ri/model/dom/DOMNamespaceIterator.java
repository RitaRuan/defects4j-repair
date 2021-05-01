package org.apache.commons.jxpath.ri.model.dom;
public class DOMNamespaceIterator implements org.apache.commons.jxpath.ri.model.NodeIterator {
	private org.apache.commons.jxpath.ri.model.NodePointer parent;

	private java.util.List attributes;

	private int position = 0;

	public DOMNamespaceIterator(org.apache.commons.jxpath.ri.model.NodePointer parent) {
		this.parent = parent;
		attributes = new java.util.ArrayList();
		collectNamespaces(attributes, ((org.w3c.dom.Node) (parent.getNode())));
	}

	private void collectNamespaces(java.util.List attributes, org.w3c.dom.Node node) {
		org.w3c.dom.Node parent = node.getParentNode();
		if (parent != null) {
			collectNamespaces(attributes, parent);
		}
		if (node.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE) {
			node = ((org.w3c.dom.Document) (node)).getDocumentElement();
		}
		if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
			org.w3c.dom.NamedNodeMap map = node.getAttributes();
			int count = map.getLength();
			for (int i = 0; i < count; i++) {
				org.w3c.dom.Attr attr = ((org.w3c.dom.Attr) (map.item(i)));
				java.lang.String prefix = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getPrefix(attr);
				java.lang.String name = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getLocalName(attr);
				if (((prefix != null) && prefix.equals("xmlns")) || ((prefix == null) && name.equals("xmlns"))) {
					attributes.add(attr);
				}
			}
		}
	}

	public org.apache.commons.jxpath.ri.model.NodePointer getNodePointer() {
		if (position == 0) {
			if (!setPosition(1)) {
				return null;
			}
			position = 0;
		}
		int index = position - 1;
		if (index < 0) {
			index = 0;
		}
		java.lang.String prefix = "";
		org.w3c.dom.Attr attr = ((org.w3c.dom.Attr) (attributes.get(index)));
		java.lang.String name = attr.getPrefix();
		if ((name != null) && name.equals("xmlns")) {
			prefix = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getLocalName(attr);
		}
		return new org.apache.commons.jxpath.ri.model.dom.NamespacePointer(parent, prefix, attr.getValue());
	}

	public int getPosition() {
		return position;
	}

	public boolean setPosition(int position) {
		this.position = position;
		return (position >= 1) && (position <= attributes.size());
	}
}