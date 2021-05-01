package org.jsoup.nodes;
public abstract class Node implements java.lang.Cloneable {
	private static final java.util.List<org.jsoup.nodes.Node> EMPTY_NODES = java.util.Collections.emptyList();

	org.jsoup.nodes.Node parentNode;

	java.util.List<org.jsoup.nodes.Node> childNodes;

	org.jsoup.nodes.Attributes attributes;

	java.lang.String baseUri;

	int siblingIndex;

	protected Node(java.lang.String baseUri, org.jsoup.nodes.Attributes attributes) {
		org.jsoup.helper.Validate.notNull(baseUri);
		org.jsoup.helper.Validate.notNull(attributes);
		childNodes = org.jsoup.nodes.Node.EMPTY_NODES;
		this.baseUri = baseUri.trim();
		this.attributes = attributes;
	}

	protected Node(java.lang.String baseUri) {
		this(baseUri, new org.jsoup.nodes.Attributes());
	}

	protected Node() {
		childNodes = org.jsoup.nodes.Node.EMPTY_NODES;
		attributes = null;
	}

	public abstract java.lang.String nodeName();

	public java.lang.String attr(java.lang.String attributeKey) {
		org.jsoup.helper.Validate.notNull(attributeKey);
		java.lang.String val = attributes.getIgnoreCase(attributeKey);
		if (val.length() > 0)
			return val;
		else if (attributeKey.toLowerCase().startsWith("abs:"))
			return absUrl(attributeKey.substring("abs:".length()));
		else
			return "";

	}

	public org.jsoup.nodes.Attributes attributes() {
		return attributes;
	}

	public org.jsoup.nodes.Node attr(java.lang.String attributeKey, java.lang.String attributeValue) {
		attributes.put(attributeKey, attributeValue);
		return this;
	}

	public boolean hasAttr(java.lang.String attributeKey) {
		org.jsoup.helper.Validate.notNull(attributeKey);
		if (attributeKey.startsWith("abs:")) {
			java.lang.String key = attributeKey.substring("abs:".length());
			if (attributes.hasKeyIgnoreCase(key) && (!absUrl(key).equals("")))
				return true;

		}
		return attributes.hasKeyIgnoreCase(attributeKey);
	}

	public org.jsoup.nodes.Node removeAttr(java.lang.String attributeKey) {
		org.jsoup.helper.Validate.notNull(attributeKey);
		attributes.removeIgnoreCase(attributeKey);
		return this;
	}

	public java.lang.String baseUri() {
		return baseUri;
	}

	public void setBaseUri(final java.lang.String baseUri) {
		org.jsoup.helper.Validate.notNull(baseUri);
		traverse(new org.jsoup.select.NodeVisitor() {
			public void head(org.jsoup.nodes.Node node, int depth) {
				node.baseUri = baseUri;
			}

			public void tail(org.jsoup.nodes.Node node, int depth) {
			}
		});
	}

	public java.lang.String absUrl(java.lang.String attributeKey) {
		org.jsoup.helper.Validate.notEmpty(attributeKey);
		if (!hasAttr(attributeKey)) {
			return "";
		} else {
			return org.jsoup.helper.StringUtil.resolve(baseUri, attr(attributeKey));
		}
	}

	public org.jsoup.nodes.Node childNode(int index) {
		return childNodes.get(index);
	}

	public java.util.List<org.jsoup.nodes.Node> childNodes() {
		return java.util.Collections.unmodifiableList(childNodes);
	}

	public java.util.List<org.jsoup.nodes.Node> childNodesCopy() {
		java.util.List<org.jsoup.nodes.Node> children = new java.util.ArrayList<org.jsoup.nodes.Node>(childNodes.size());
		for (org.jsoup.nodes.Node node : childNodes) {
			children.add(node.clone());
		}
		return children;
	}

	public final int childNodeSize() {
		return childNodes.size();
	}

	protected org.jsoup.nodes.Node[] childNodesAsArray() {
		return childNodes.toArray(new org.jsoup.nodes.Node[childNodeSize()]);
	}

	public org.jsoup.nodes.Node parent() {
		return parentNode;
	}

	public final org.jsoup.nodes.Node parentNode() {
		return parentNode;
	}

	public org.jsoup.nodes.Document ownerDocument() {
		if (this instanceof org.jsoup.nodes.Document)
			return ((org.jsoup.nodes.Document) (this));
		else if (parentNode == null)
			return null;
		else
			return parentNode.ownerDocument();

	}

	public void remove() {
		org.jsoup.helper.Validate.notNull(parentNode);
		parentNode.removeChild(this);
	}

	public org.jsoup.nodes.Node before(java.lang.String html) {
		addSiblingHtml(siblingIndex, html);
		return this;
	}

	public org.jsoup.nodes.Node before(org.jsoup.nodes.Node node) {
		org.jsoup.helper.Validate.notNull(node);
		org.jsoup.helper.Validate.notNull(parentNode);
		parentNode.addChildren(siblingIndex, node);
		return this;
	}

	public org.jsoup.nodes.Node after(java.lang.String html) {
		addSiblingHtml(siblingIndex + 1, html);
		return this;
	}

	public org.jsoup.nodes.Node after(org.jsoup.nodes.Node node) {
		org.jsoup.helper.Validate.notNull(node);
		org.jsoup.helper.Validate.notNull(parentNode);
		parentNode.addChildren(siblingIndex + 1, node);
		return this;
	}

	private void addSiblingHtml(int index, java.lang.String html) {
		org.jsoup.helper.Validate.notNull(html);
		org.jsoup.helper.Validate.notNull(parentNode);
		org.jsoup.nodes.Element context = (parent() instanceof org.jsoup.nodes.Element) ? ((org.jsoup.nodes.Element) (parent())) : null;
		java.util.List<org.jsoup.nodes.Node> nodes = org.jsoup.parser.Parser.parseFragment(html, context, baseUri());
		parentNode.addChildren(index, nodes.toArray(new org.jsoup.nodes.Node[nodes.size()]));
	}

	public org.jsoup.nodes.Node wrap(java.lang.String html) {
		org.jsoup.helper.Validate.notEmpty(html);
		org.jsoup.nodes.Element context = (parent() instanceof org.jsoup.nodes.Element) ? ((org.jsoup.nodes.Element) (parent())) : null;
		java.util.List<org.jsoup.nodes.Node> wrapChildren = org.jsoup.parser.Parser.parseFragment(html, context, baseUri());
		org.jsoup.nodes.Node wrapNode = wrapChildren.get(0);
		if ((wrapNode == null) || (!(wrapNode instanceof org.jsoup.nodes.Element)))
			return null;

		org.jsoup.nodes.Element wrap = ((org.jsoup.nodes.Element) (wrapNode));
		org.jsoup.nodes.Element deepest = getDeepChild(wrap);
		parentNode.replaceChild(this, wrap);
		deepest.addChildren(this);
		if (wrapChildren.size() > 0) {
			for (int i = 0; i < wrapChildren.size(); i++) {
				org.jsoup.nodes.Node remainder = wrapChildren.get(i);
				remainder.parentNode.removeChild(remainder);
				wrap.appendChild(remainder);
			}
		}
		return this;
	}

	public org.jsoup.nodes.Node unwrap() {
		org.jsoup.helper.Validate.notNull(parentNode);
		org.jsoup.nodes.Node firstChild = (childNodes.size() > 0) ? childNodes.get(0) : null;
		parentNode.addChildren(siblingIndex, this.childNodesAsArray());
		this.remove();
		return firstChild;
	}

	private org.jsoup.nodes.Element getDeepChild(org.jsoup.nodes.Element el) {
		java.util.List<org.jsoup.nodes.Element> children = el.children();
		if (children.size() > 0)
			return getDeepChild(children.get(0));
		else
			return el;

	}

	public void replaceWith(org.jsoup.nodes.Node in) {
		org.jsoup.helper.Validate.notNull(in);
		org.jsoup.helper.Validate.notNull(parentNode);
		parentNode.replaceChild(this, in);
	}

	protected void setParentNode(org.jsoup.nodes.Node parentNode) {
		if (this.parentNode != null)
			this.parentNode.removeChild(this);

		this.parentNode = parentNode;
	}

	protected void replaceChild(org.jsoup.nodes.Node out, org.jsoup.nodes.Node in) {
		org.jsoup.helper.Validate.isTrue(out.parentNode == this);
		org.jsoup.helper.Validate.notNull(in);
		if (in.parentNode != null)
			in.parentNode.removeChild(in);

		final int index = out.siblingIndex;
		childNodes.set(index, in);
		in.parentNode = this;
		in.setSiblingIndex(index);
		out.parentNode = null;
	}

	protected void removeChild(org.jsoup.nodes.Node out) {
		org.jsoup.helper.Validate.isTrue(out.parentNode == this);
		final int index = out.siblingIndex;
		childNodes.remove(index);
		reindexChildren(index);
		out.parentNode = null;
	}

	protected void addChildren(org.jsoup.nodes.Node... children) {
		for (org.jsoup.nodes.Node child : children) {
			reparentChild(child);
			ensureChildNodes();
			childNodes.add(child);
			child.setSiblingIndex(childNodes.size() - 1);
		}
	}

	protected void addChildren(int index, org.jsoup.nodes.Node... children) {
		org.jsoup.helper.Validate.noNullElements(children);
		ensureChildNodes();
		for (int i = children.length - 1; i >= 0; i--) {
			org.jsoup.nodes.Node in = children[i];
			reparentChild(in);
			childNodes.add(index, in);
			reindexChildren(index);
		}
	}

	protected void ensureChildNodes() {
		if (childNodes == org.jsoup.nodes.Node.EMPTY_NODES) {
			childNodes = new java.util.ArrayList<org.jsoup.nodes.Node>(4);
		}
	}

	protected void reparentChild(org.jsoup.nodes.Node child) {
		if (child.parentNode != null)
			child.parentNode.removeChild(child);

		child.setParentNode(this);
	}

	private void reindexChildren(int start) {
		for (int i = start; i < childNodes.size(); i++) {
			childNodes.get(i).setSiblingIndex(i);
		}
	}

	public java.util.List<org.jsoup.nodes.Node> siblingNodes() {
		if (parentNode == null)
			return java.util.Collections.emptyList();

		java.util.List<org.jsoup.nodes.Node> nodes = parentNode.childNodes;
		java.util.List<org.jsoup.nodes.Node> siblings = new java.util.ArrayList<org.jsoup.nodes.Node>(nodes.size() - 1);
		for (org.jsoup.nodes.Node node : nodes)
			if (node != this)
				siblings.add(node);


		return siblings;
	}

	public org.jsoup.nodes.Node nextSibling() {
		if (parentNode == null)
			return null;

		final java.util.List<org.jsoup.nodes.Node> siblings = parentNode.childNodes;
		final int index = siblingIndex + 1;
		if (siblings.size() > index)
			return siblings.get(index);
		else
			return null;

	}

	public org.jsoup.nodes.Node previousSibling() {
		if (parentNode == null)
			return null;

		if (siblingIndex > 0)
			return parentNode.childNodes.get(siblingIndex - 1);
		else
			return null;

	}

	public int siblingIndex() {
		return siblingIndex;
	}

	protected void setSiblingIndex(int siblingIndex) {
		this.siblingIndex = siblingIndex;
	}

	public org.jsoup.nodes.Node traverse(org.jsoup.select.NodeVisitor nodeVisitor) {
		org.jsoup.helper.Validate.notNull(nodeVisitor);
		org.jsoup.select.NodeTraversor traversor = new org.jsoup.select.NodeTraversor(nodeVisitor);
		traversor.traverse(this);
		return this;
	}

	public java.lang.String outerHtml() {
		java.lang.StringBuilder accum = new java.lang.StringBuilder(128);
		outerHtml(accum);
		return accum.toString();
	}

	protected void outerHtml(java.lang.Appendable accum) {
		new org.jsoup.select.NodeTraversor(new org.jsoup.nodes.Node.OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
	}

	org.jsoup.nodes.Document.OutputSettings getOutputSettings() {
		return ownerDocument() != null ? ownerDocument().outputSettings() : new org.jsoup.nodes.Document("").outputSettings();
	}

	abstract void outerHtmlHead(java.lang.Appendable accum, int depth, org.jsoup.nodes.Document.OutputSettings out) throws java.io.IOException;

	abstract void outerHtmlTail(java.lang.Appendable accum, int depth, org.jsoup.nodes.Document.OutputSettings out) throws java.io.IOException;

	public <T extends java.lang.Appendable> T html(T appendable) {
		outerHtml(appendable);
		return appendable;
	}

	public java.lang.String toString() {
		return outerHtml();
	}

	protected void indent(java.lang.Appendable accum, int depth, org.jsoup.nodes.Document.OutputSettings out) throws java.io.IOException {
		accum.append("\n").append(org.jsoup.helper.StringUtil.padding(depth * out.indentAmount()));
	}

	@java.lang.Override
	public boolean equals(java.lang.Object o) {
		return this == o;
	}

	public boolean hasSameValue(java.lang.Object o) {
		if (this == o)
			return true;

		if ((o == null) || (getClass() != o.getClass()))
			return false;

		return this.outerHtml().equals(((org.jsoup.nodes.Node) (o)).outerHtml());
	}

	@java.lang.Override
	public org.jsoup.nodes.Node clone() {
		org.jsoup.nodes.Node thisClone = doClone(null);
		java.util.LinkedList<org.jsoup.nodes.Node> nodesToProcess = new java.util.LinkedList<org.jsoup.nodes.Node>();
		nodesToProcess.add(thisClone);
		while (!nodesToProcess.isEmpty()) {
			org.jsoup.nodes.Node currParent = nodesToProcess.remove();
			for (int i = 0; i < currParent.childNodes.size(); i++) {
				org.jsoup.nodes.Node childClone = currParent.childNodes.get(i).doClone(currParent);
				currParent.childNodes.set(i, childClone);
				nodesToProcess.add(childClone);
			}
		} 
		return thisClone;
	}

	protected org.jsoup.nodes.Node doClone(org.jsoup.nodes.Node parent) {
		org.jsoup.nodes.Node clone;
		try {
			clone = ((org.jsoup.nodes.Node) (super.clone()));
		} catch (java.lang.CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
		clone.parentNode = parent;
		clone.siblingIndex = (parent == null) ? 0 : siblingIndex;
		clone.attributes = (attributes != null) ? attributes.clone() : null;
		clone.baseUri = baseUri;
		clone.childNodes = new java.util.ArrayList<org.jsoup.nodes.Node>(childNodes.size());
		for (org.jsoup.nodes.Node child : childNodes)
			clone.childNodes.add(child);

		return clone;
	}

	private static class OuterHtmlVisitor implements org.jsoup.select.NodeVisitor {
		private java.lang.Appendable accum;

		private org.jsoup.nodes.Document.OutputSettings out;

		OuterHtmlVisitor(java.lang.Appendable accum, org.jsoup.nodes.Document.OutputSettings out) {
			this.accum = accum;
			this.out = out;
		}

		public void head(org.jsoup.nodes.Node node, int depth) {
			try {
				node.outerHtmlHead(accum, depth, out);
			} catch (java.io.IOException exception) {
				throw new org.jsoup.SerializationException(exception);
			}
		}

		public void tail(org.jsoup.nodes.Node node, int depth) {
			if (!node.nodeName().equals("#text")) {
				try {
					node.outerHtmlTail(accum, depth, out);
				} catch (java.io.IOException exception) {
					throw new org.jsoup.SerializationException(exception);
				}
			}
		}
	}
}