package org.apache.commons.jxpath.ri.model.dom;
public class DOMNodePointer extends 



















































org.apache.commons.jxpath.ri.model.NodePointer {

	private static final long serialVersionUID = -8751046933894857319L;

	private org.w3c.dom.Node node;
	private java.util.Map namespaces;
	private java.lang.String defaultNamespace;
	private java.lang.String id;
	private org.apache.commons.jxpath.ri.NamespaceResolver localNamespaceResolver;


	public static final java.lang.String XML_NAMESPACE_URI = 
	"http://www.w3.org/XML/1998/namespace";


	public static final java.lang.String XMLNS_NAMESPACE_URI = 
	"http://www.w3.org/2000/xmlns/";






	public DOMNodePointer(org.w3c.dom.Node node, java.util.Locale locale) {
		super(null, locale);
		this.node = node;
	}







	public DOMNodePointer(org.w3c.dom.Node node, java.util.Locale locale, java.lang.String id) {
		super(null, locale);
		this.node = node;
		this.id = id;
	}






	public DOMNodePointer(org.apache.commons.jxpath.ri.model.NodePointer parent, org.w3c.dom.Node node) {
		super(parent);
		this.node = node;
	}

	public boolean testNode(org.apache.commons.jxpath.ri.compiler.NodeTest test) {
		return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.testNode(node, test);
	}







	public static boolean testNode(org.w3c.dom.Node node, org.apache.commons.jxpath.ri.compiler.NodeTest test) {
		if (test == null) {
			return true;
		}
		if (test instanceof org.apache.commons.jxpath.ri.compiler.NodeNameTest) {
			if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
				return false;
			}

			org.apache.commons.jxpath.ri.compiler.NodeNameTest nodeNameTest = ((org.apache.commons.jxpath.ri.compiler.NodeNameTest) (test));
			org.apache.commons.jxpath.ri.QName testName = nodeNameTest.getNodeName();
			java.lang.String namespaceURI = nodeNameTest.getNamespaceURI();
			boolean wildcard = nodeNameTest.isWildcard();
			java.lang.String testPrefix = testName.getPrefix();
			if (wildcard && (testPrefix == null)) {
				return true;
			}
			if (wildcard || 
			testName.getName().equals(
			org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getLocalName(node))) {
				java.lang.String nodeNS = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getNamespaceURI(node);
				return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.equalStrings(namespaceURI, nodeNS) || ((nodeNS == null) && 
				org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.equalStrings(testPrefix, org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getPrefix(node)));
			}
			return false;
		}
		if (test instanceof org.apache.commons.jxpath.ri.compiler.NodeTypeTest) {
			int nodeType = node.getNodeType();
			switch (((org.apache.commons.jxpath.ri.compiler.NodeTypeTest) (test)).getNodeType()) {
				case org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_NODE :
					return true;
				case org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_TEXT :
					return (nodeType == org.w3c.dom.Node.CDATA_SECTION_NODE) || 
					(nodeType == org.w3c.dom.Node.TEXT_NODE);
				case org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_COMMENT :
					return nodeType == org.w3c.dom.Node.COMMENT_NODE;
				case org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_PI :
					return nodeType == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
				default :
					return false;}

		}
		if ((test instanceof org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest) && 
		(node.getNodeType() == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE)) {
			java.lang.String testPI = ((org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest) (test)).getTarget();
			java.lang.String nodePI = ((org.w3c.dom.ProcessingInstruction) (node)).getTarget();
			return testPI.equals(nodePI);
		}
		return false;
	}







	private static boolean equalStrings(java.lang.String s1, java.lang.String s2) {
		if (s1 == s2) {
			return true;
		}
		s1 = (s1 == null) ? "" : s1.trim();
		s2 = (s2 == null) ? "" : s2.trim();
		return s1.equals(s2);
	}

	public org.apache.commons.jxpath.ri.QName getName() {
		java.lang.String ln = null;
		java.lang.String ns = null;
		int type = node.getNodeType();
		if (type == org.w3c.dom.Node.ELEMENT_NODE) {
			ns = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getPrefix(node);
			ln = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getLocalName(node);
		} else 
		if (type == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE) {
			ln = ((org.w3c.dom.ProcessingInstruction) (node)).getTarget();
		}
		return new org.apache.commons.jxpath.ri.QName(ns, ln);
	}

	public java.lang.String getNamespaceURI() {
		return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getNamespaceURI(node);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator childIterator(org.apache.commons.jxpath.ri.compiler.NodeTest test, boolean reverse, 
	org.apache.commons.jxpath.ri.model.NodePointer startWith) {
		return new org.apache.commons.jxpath.ri.model.dom.DOMNodeIterator(this, test, reverse, startWith);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator attributeIterator(org.apache.commons.jxpath.ri.QName name) {
		return new org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator(this, name);
	}

	public org.apache.commons.jxpath.ri.model.NodePointer namespacePointer(java.lang.String prefix) {
		return new org.apache.commons.jxpath.ri.model.dom.NamespacePointer(this, prefix);
	}

	public org.apache.commons.jxpath.ri.model.NodeIterator namespaceIterator() {
		return new org.apache.commons.jxpath.ri.model.dom.DOMNamespaceIterator(this);
	}

	public synchronized org.apache.commons.jxpath.ri.NamespaceResolver getNamespaceResolver() {
		if (localNamespaceResolver == null) {
			localNamespaceResolver = new org.apache.commons.jxpath.ri.NamespaceResolver(super.getNamespaceResolver());
			localNamespaceResolver.setNamespaceContextPointer(this);
		}
		return localNamespaceResolver;
	}

	public java.lang.String getNamespaceURI(java.lang.String prefix) {
		if ((prefix == null) || prefix.equals("")) {
			return getDefaultNamespaceURI();
		}

		if (prefix.equals("xml")) {
			return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.XML_NAMESPACE_URI;
		}

		if (prefix.equals("xmlns")) {
			return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.XMLNS_NAMESPACE_URI;
		}

		java.lang.String namespace = null;
		if (namespaces == null) {
			namespaces = new java.util.HashMap();
		} else 
		{
			namespace = ((java.lang.String) (namespaces.get(prefix)));
		}

		if (namespace == null) {
			java.lang.String qname = "xmlns:" + prefix;
			org.w3c.dom.Node aNode = node;
			if (aNode instanceof org.w3c.dom.Document) {
				aNode = ((org.w3c.dom.Document) (aNode)).getDocumentElement();
			}
			while (aNode != null) {
				if (aNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Attr attr = ((org.w3c.dom.Element) (aNode)).getAttributeNode(qname);
					if (attr != null) {
						namespace = attr.getValue();
						break;
					}
				}
				aNode = aNode.getParentNode();
			} 
			if ((namespace == null) || namespace.equals("")) {
				namespace = org.apache.commons.jxpath.ri.model.NodePointer.UNKNOWN_NAMESPACE;
			}
		}

		namespaces.put(prefix, namespace);
		if (namespace == org.apache.commons.jxpath.ri.model.NodePointer.UNKNOWN_NAMESPACE) {
			return null;
		}


		return namespace;
	}

	public java.lang.String getDefaultNamespaceURI() {
		if (defaultNamespace == null) {
			org.w3c.dom.Node aNode = node;
			if (aNode instanceof org.w3c.dom.Document) {
				aNode = ((org.w3c.dom.Document) (aNode)).getDocumentElement();
			}
			while (aNode != null) {
				if (aNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Attr attr = ((org.w3c.dom.Element) (aNode)).getAttributeNode("xmlns");
					if (attr != null) {
						defaultNamespace = attr.getValue();
						break;
					}
				}
				aNode = aNode.getParentNode();
			} 
		}
		if (defaultNamespace == null) {
			defaultNamespace = "";
		}

		return defaultNamespace.equals("") ? null : defaultNamespace;
	}

	public java.lang.Object getBaseValue() {
		return node;
	}

	public java.lang.Object getImmediateNode() {
		return node;
	}

	public boolean isActual() {
		return true;
	}

	public boolean isCollection() {
		return false;
	}

	public int getLength() {
		return 1;
	}

	public boolean isLeaf() {
		return !node.hasChildNodes();
	}








	public boolean isLanguage(java.lang.String lang) {
		java.lang.String current = getLanguage();
		return current == null ? super.isLanguage(lang) : 
		current.toUpperCase(java.util.Locale.ENGLISH).startsWith(lang.toUpperCase(java.util.Locale.ENGLISH));
	}








	protected static java.lang.String findEnclosingAttribute(org.w3c.dom.Node n, java.lang.String attrName) {
		while (n != null) {
			if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				org.w3c.dom.Element e = ((org.w3c.dom.Element) (n));
				java.lang.String attr = e.getAttribute(attrName);
				if ((attr != null) && (!attr.equals(""))) {
					return attr;
				}
			}
			n = n.getParentNode();
		} 
		return null;
	}





	protected java.lang.String getLanguage() {
		return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.findEnclosingAttribute(node, "xml:lang");
	}








	public void setValue(java.lang.Object value) {
		if ((node.getNodeType() == org.w3c.dom.Node.TEXT_NODE) || 
		(node.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE)) {
			java.lang.String string = ((java.lang.String) (org.apache.commons.jxpath.util.TypeUtils.convert(value, java.lang.String.class)));
			if ((string != null) && (!string.equals(""))) {
				node.setNodeValue(string);
			} else 
			{
				node.getParentNode().removeChild(node);
			}
		} else 
		{
			org.w3c.dom.NodeList children = node.getChildNodes();
			int count = children.getLength();
			for (int i = count; (--i) >= 0;) {
				org.w3c.dom.Node child = children.item(i);
				node.removeChild(child);
			}

			if (value instanceof org.w3c.dom.Node) {
				org.w3c.dom.Node valueNode = ((org.w3c.dom.Node) (value));
				if ((valueNode instanceof org.w3c.dom.Element) || 
				(valueNode instanceof org.w3c.dom.Document)) {
					children = valueNode.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						org.w3c.dom.Node child = children.item(i);
						node.appendChild(child.cloneNode(true));
					}
				} else 
				{
					node.appendChild(valueNode.cloneNode(true));
				}
			} else 
			{
				java.lang.String string = ((java.lang.String) (org.apache.commons.jxpath.util.TypeUtils.convert(value, java.lang.String.class)));
				if ((string != null) && (!string.equals(""))) {
					org.w3c.dom.Node textNode = 
					node.getOwnerDocument().createTextNode(string);
					node.appendChild(textNode);
				}
			}
		}
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createChild(org.apache.commons.jxpath.JXPathContext context, org.apache.commons.jxpath.ri.QName name, int index) {
		if (index == org.apache.commons.jxpath.ri.model.NodePointer.WHOLE_COLLECTION) {
			index = 0;
		}
		boolean success = 
		getAbstractFactory(context).createObject(
		context, 
		this, 
		node, 
		name.toString(), 
		index);
		if (success) {
			org.apache.commons.jxpath.ri.compiler.NodeTest nodeTest;
			java.lang.String prefix = name.getPrefix();
			java.lang.String namespaceURI = (prefix == null) ? null : context.getNamespaceURI(
			prefix);
			nodeTest = new org.apache.commons.jxpath.ri.compiler.NodeNameTest(name, namespaceURI);

			org.apache.commons.jxpath.ri.model.NodeIterator it = childIterator(nodeTest, false, null);
			if ((it != null) && it.setPosition(index + 1)) {
				return it.getNodePointer();
			}
		}
		throw new org.apache.commons.jxpath.JXPathAbstractFactoryException(
		((((("Factory could not create a child node for path: " + asPath()) + 
		"/") + name) + "[") + (index + 1)) + "]");
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createChild(org.apache.commons.jxpath.JXPathContext context, org.apache.commons.jxpath.ri.QName name, 
	int index, java.lang.Object value) {
		org.apache.commons.jxpath.ri.model.NodePointer ptr = createChild(context, name, index);
		ptr.setValue(value);
		return ptr;
	}

	public org.apache.commons.jxpath.ri.model.NodePointer createAttribute(org.apache.commons.jxpath.JXPathContext context, org.apache.commons.jxpath.ri.QName name) {
		if (!(node instanceof org.w3c.dom.Element)) {
			return super.createAttribute(context, name);
		}
		org.w3c.dom.Element element = ((org.w3c.dom.Element) (node));
		java.lang.String prefix = name.getPrefix();
		if (prefix != null) {
			java.lang.String ns = null;
			org.apache.commons.jxpath.ri.NamespaceResolver nsr = getNamespaceResolver();
			if (nsr != null) {
				ns = nsr.getNamespaceURI(prefix);
			}
			if (ns == null) {
				throw new org.apache.commons.jxpath.JXPathException(
				"Unknown namespace prefix: " + prefix);
			}
			element.setAttributeNS(ns, name.toString(), "");
		} else 

		if (!element.hasAttribute(name.getName())) {
			element.setAttribute(name.getName(), "");
		}

		org.apache.commons.jxpath.ri.model.NodeIterator it = attributeIterator(name);
		it.setPosition(1);
		return it.getNodePointer();
	}

	public void remove() {
		org.w3c.dom.Node parent = node.getParentNode();
		if (parent == null) {
			throw new org.apache.commons.jxpath.JXPathException("Cannot remove root DOM node");
		}
		parent.removeChild(node);
	}

	public java.lang.String asPath() {
		if (id != null) {
			return ("id('" + escape(id)) + "')";
		}

		java.lang.StringBuffer buffer = new java.lang.StringBuffer();
		if (parent != null) {
			buffer.append(parent.asPath());
		}
		switch (node.getNodeType()) {
			case org.w3c.dom.Node.ELEMENT_NODE :



				if (parent instanceof org.apache.commons.jxpath.ri.model.dom.DOMNodePointer) {
					if ((buffer.length() == 0) || 
					(buffer.charAt(buffer.length() - 1) != '/')) {
						buffer.append('/');
					}
					java.lang.String ln = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getLocalName(node);
					java.lang.String nsURI = getNamespaceURI();
					if (nsURI == null) {
						buffer.append(ln);
						buffer.append('[');
						buffer.append(getRelativePositionByQName()).append(']');
					} else 
					{
						java.lang.String prefix = getNamespaceResolver().getPrefix(nsURI);
						if (prefix != null) {
							buffer.append(prefix);
							buffer.append(':');
							buffer.append(ln);
							buffer.append('[');
							buffer.append(getRelativePositionByQName());
							buffer.append(']');
						} else 
						{
							buffer.append("node()");
							buffer.append('[');
							buffer.append(getRelativePositionOfElement());
							buffer.append(']');
						}
					}
				}
				break;
			case org.w3c.dom.Node.TEXT_NODE :
			case org.w3c.dom.Node.CDATA_SECTION_NODE :
				buffer.append("/text()");
				buffer.append('[');
				buffer.append(getRelativePositionOfTextNode()).append(']');
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE :
				buffer.append("/processing-instruction(\'");
				buffer.append(((org.w3c.dom.ProcessingInstruction) (node)).getTarget()).append("')");
				buffer.append('[');
				buffer.append(getRelativePositionOfPI()).append(']');
				break;
			case org.w3c.dom.Node.DOCUMENT_NODE :

				break;
			default :
				break;}

		return buffer.toString();
	}





	private int getRelativePositionByQName() {
		int count = 1;
		org.w3c.dom.Node n = node.getPreviousSibling();
		while (n != null) {
			if ((n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) && matchesQName(n)) {
				count++;
			}
			n = n.getPreviousSibling();
		} 
		return count;
	}

	private boolean matchesQName(org.w3c.dom.Node n) {
		if (getNamespaceURI() != null) {
			return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.equalStrings(org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getNamespaceURI(n), getNamespaceURI()) && 
			org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.equalStrings(node.getLocalName(), n.getLocalName());
		}
		return org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.equalStrings(node.getNodeName(), n.getNodeName());
	}





	private int getRelativePositionOfElement() {
		int count = 1;
		org.w3c.dom.Node n = node.getPreviousSibling();
		while (n != null) {
			if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				count++;
			}
			n = n.getPreviousSibling();
		} 
		return count;
	}





	private int getRelativePositionOfTextNode() {
		int count = 1;
		org.w3c.dom.Node n = node.getPreviousSibling();
		while (n != null) {
			if ((n.getNodeType() == org.w3c.dom.Node.TEXT_NODE) || 
			(n.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE)) {
				count++;
			}
			n = n.getPreviousSibling();
		} 
		return count;
	}





	private int getRelativePositionOfPI() {
		int count = 1;
		java.lang.String target = ((org.w3c.dom.ProcessingInstruction) (node)).getTarget();
		org.w3c.dom.Node n = node.getPreviousSibling();
		while (n != null) {
			if ((n.getNodeType() == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE) && 
			((org.w3c.dom.ProcessingInstruction) (n)).getTarget().equals(target)) {
				count++;
			}
			n = n.getPreviousSibling();
		} 
		return count;
	}

	public int hashCode() {
		return node.hashCode();
	}

	public boolean equals(java.lang.Object object) {
		return (object == this) || ((object instanceof org.apache.commons.jxpath.ri.model.dom.DOMNodePointer) && (node == ((org.apache.commons.jxpath.ri.model.dom.DOMNodePointer) (object)).node));
	}






	public static java.lang.String getPrefix(org.w3c.dom.Node node) {
		java.lang.String prefix = node.getPrefix();
		if (prefix != null) {
			return prefix;
		}

		java.lang.String name = node.getNodeName();
		int index = name.lastIndexOf(':');
		return index < 0 ? null : name.substring(0, index);
	}






	public static java.lang.String getLocalName(org.w3c.dom.Node node) {
		java.lang.String localName = node.getLocalName();
		if (localName != null) {
			return localName;
		}

		java.lang.String name = node.getNodeName();
		int index = name.lastIndexOf(':');
		return index < 0 ? name : name.substring(index + 1);
	}






	public static java.lang.String getNamespaceURI(org.w3c.dom.Node node) {
		if (node instanceof org.w3c.dom.Document) {
			node = ((org.w3c.dom.Document) (node)).getDocumentElement();
		}

		org.w3c.dom.Element element = ((org.w3c.dom.Element) (node));

		java.lang.String uri = element.getNamespaceURI();
		if (uri == null) {
			java.lang.String prefix = org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.getPrefix(node);
			java.lang.String qname = (prefix == null) ? "xmlns" : "xmlns:" + prefix;

			org.w3c.dom.Node aNode = node;
			while (aNode != null) {
				if (aNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					org.w3c.dom.Attr attr = ((org.w3c.dom.Element) (aNode)).getAttributeNode(qname);
					if (attr != null) {
						return attr.getValue();
					}
				}
				aNode = aNode.getParentNode();
			} 
			return null;
		}
		return uri;
	}

	public java.lang.Object getValue() {
		if (node.getNodeType() == org.w3c.dom.Node.COMMENT_NODE) {
			java.lang.String text = ((org.w3c.dom.Comment) (node)).getData();
			return text == null ? "" : text.trim();
		}
		return stringValue(node);
	}






	private java.lang.String stringValue(org.w3c.dom.Node node) {
		int nodeType = node.getNodeType();
		if (nodeType == org.w3c.dom.Node.COMMENT_NODE) {
			return "";
		}
		boolean trim = !"preserve".equals(org.apache.commons.jxpath.ri.model.dom.DOMNodePointer.findEnclosingAttribute(node, "xml:space"));
		if ((nodeType == org.w3c.dom.Node.TEXT_NODE) || (nodeType == org.w3c.dom.Node.CDATA_SECTION_NODE)) {
			java.lang.String text = node.getNodeValue();
			return text == null ? "" : trim ? text.trim() : text;
		}
		if (nodeType == org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE) {
			java.lang.String text = ((org.w3c.dom.ProcessingInstruction) (node)).getData();
			return text == null ? "" : trim ? text.trim() : text;
		}
		org.w3c.dom.NodeList list = node.getChildNodes();
		java.lang.StringBuffer buf = new java.lang.StringBuffer();
		for (int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Node child = list.item(i);
			buf.append(stringValue(child));
		}
		return buf.toString();
	}







	public org.apache.commons.jxpath.Pointer getPointerByID(org.apache.commons.jxpath.JXPathContext context, java.lang.String id) {
		org.w3c.dom.Document document = (node.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE) ? ((org.w3c.dom.Document) (node)) : 
		node.getOwnerDocument();
		org.w3c.dom.Element element = document.getElementById(id);
		return element == null ? ((org.apache.commons.jxpath.Pointer) (new org.apache.commons.jxpath.ri.model.beans.NullPointer(getLocale(), id))) : 
		new org.apache.commons.jxpath.ri.model.dom.DOMNodePointer(element, getLocale(), id);
	}

	public int compareChildNodePointers(org.apache.commons.jxpath.ri.model.NodePointer pointer1, 
	org.apache.commons.jxpath.ri.model.NodePointer pointer2) {
		org.w3c.dom.Node node1 = ((org.w3c.dom.Node) (pointer1.getBaseValue()));
		org.w3c.dom.Node node2 = ((org.w3c.dom.Node) (pointer2.getBaseValue()));
		if (node1 == node2) {
			return 0;
		}

		int t1 = node1.getNodeType();
		int t2 = node2.getNodeType();
		if ((t1 == org.w3c.dom.Node.ATTRIBUTE_NODE) && (t2 != org.w3c.dom.Node.ATTRIBUTE_NODE)) {
			return -1;
		}
		if ((t1 != org.w3c.dom.Node.ATTRIBUTE_NODE) && (t2 == org.w3c.dom.Node.ATTRIBUTE_NODE)) {
			return 1;
		}
		if ((t1 == org.w3c.dom.Node.ATTRIBUTE_NODE) && (t2 == org.w3c.dom.Node.ATTRIBUTE_NODE)) {
			org.w3c.dom.NamedNodeMap map = ((org.w3c.dom.Node) (getNode())).getAttributes();
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				org.w3c.dom.Node n = map.item(i);
				if (n == node1) {
					return -1;
				}
				if (n == node2) {
					return 1;
				}
			}
			return 0;
		}

		org.w3c.dom.Node current = node.getFirstChild();
		while (current != null) {
			if (current == node1) {
				return -1;
			}
			if (current == node2) {
				return 1;
			}
			current = current.getNextSibling();
		} 
		return 0;
	}}