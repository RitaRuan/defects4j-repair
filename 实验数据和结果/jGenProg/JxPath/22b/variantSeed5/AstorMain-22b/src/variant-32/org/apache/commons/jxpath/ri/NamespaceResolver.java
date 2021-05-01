package org.apache.commons.jxpath.ri;
public class NamespaceResolver implements 





























java.lang.Cloneable , java.io.Serializable {
	private static final long serialVersionUID = 1085590057838651311L;


	protected final org.apache.commons.jxpath.ri.NamespaceResolver parent;

	protected java.util.HashMap namespaceMap = new java.util.HashMap();

	protected java.util.HashMap reverseMap = new java.util.HashMap();

	protected org.apache.commons.jxpath.ri.model.NodePointer pointer;
	private boolean sealed;








	protected static java.lang.String getPrefix(final org.apache.commons.jxpath.ri.model.NodePointer pointer, java.lang.String namespaceURI) {
		org.apache.commons.jxpath.ri.model.NodePointer currentPointer = pointer;
		while (currentPointer != null) {
			org.apache.commons.jxpath.ri.model.NodeIterator ni = currentPointer.namespaceIterator();
			for (int position = 1; (ni != null) && ni.setPosition(position); position++) {
				org.apache.commons.jxpath.ri.model.NodePointer nsPointer = ni.getNodePointer();
				java.lang.String uri = nsPointer.getNamespaceURI();
				if (uri.equals(namespaceURI)) {
					java.lang.String prefix = nsPointer.getName().getName();
					if (!"".equals(prefix)) {
						return prefix;
					}
				}
			}
			currentPointer = currentPointer.getParent();
		} 
		return null;
	}




	public NamespaceResolver() {
		this(null);
	}





	public NamespaceResolver(org.apache.commons.jxpath.ri.NamespaceResolver parent) {
		this.parent = parent;
	}







	public synchronized void registerNamespace(java.lang.String prefix, java.lang.String namespaceURI) {
		if (isSealed()) {
			throw new java.lang.IllegalStateException(
			"Cannot register namespaces on a sealed NamespaceResolver");
		}
		namespaceMap.put(prefix, namespaceURI);
		reverseMap.put(namespaceURI, prefix);
	}





	public void setNamespaceContextPointer(org.apache.commons.jxpath.ri.model.NodePointer pointer) {
		this.pointer = pointer;
	}





	public org.apache.commons.jxpath.Pointer getNamespaceContextPointer() {
		if ((pointer == null) && (parent != null)) {
			return parent.getNamespaceContextPointer();
		}
		return pointer;
	}











	public synchronized java.lang.String getNamespaceURI(java.lang.String prefix) {
		java.lang.String uri = getExternallyRegisteredNamespaceURI(prefix);
		return (uri == null) && (pointer != null) ? pointer.getNamespaceURI(prefix) : 
		uri;
	}








	protected synchronized java.lang.String getExternallyRegisteredNamespaceURI(
	java.lang.String prefix) {
		java.lang.String uri = ((java.lang.String) (namespaceMap.get(prefix)));
		return (uri == null) && (parent != null) ? parent.getExternallyRegisteredNamespaceURI(
		prefix) : uri;
	}






	public synchronized java.lang.String getPrefix(java.lang.String namespaceURI) {
		java.lang.String prefix = getExternallyRegisteredPrefix(namespaceURI);
		return (prefix == null) && (pointer != null) ? org.apache.commons.jxpath.ri.NamespaceResolver.getPrefix(pointer, 
		namespaceURI) : prefix;
	}







	protected synchronized java.lang.String getExternallyRegisteredPrefix(java.lang.String namespaceURI) {
		java.lang.String prefix = ((java.lang.String) (reverseMap.get(namespaceURI)));
		return (prefix == null) && (parent != null) ? parent.getExternallyRegisteredPrefix(
		namespaceURI) : prefix;
	}





	public boolean isSealed() {
		return sealed;
	}




	public void seal() {
		sealed = true;
		if (parent != null) {
			parent.seal();
		}
	}

	public java.lang.Object clone() {
		try {
			org.apache.commons.jxpath.ri.NamespaceResolver result = ((org.apache.commons.jxpath.ri.NamespaceResolver) (super.clone()));
			result.sealed = false;
			return result;
		}
		 catch (java.lang.CloneNotSupportedException e) {

			e.printStackTrace();
			return null;
		}
	}}