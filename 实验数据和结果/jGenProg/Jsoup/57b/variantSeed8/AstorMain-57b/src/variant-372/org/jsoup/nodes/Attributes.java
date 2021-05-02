package org.jsoup.nodes;
public class Attributes implements 



























java.lang.Iterable<org.jsoup.nodes.Attribute> , java.lang.Cloneable {
	protected static final java.lang.String dataPrefix = "data-";

	private java.util.LinkedHashMap<java.lang.String, org.jsoup.nodes.Attribute> attributes = null;









	public java.lang.String get(java.lang.String key) {
		org.jsoup.helper.Validate.notEmpty(key);

		if (attributes == null)
			return "";

		org.jsoup.nodes.Attribute attr = attributes.get(key);
		return attr != null ? attr.getValue() : "";
	}






	public java.lang.String getIgnoreCase(java.lang.String key) {
		org.jsoup.helper.Validate.notEmpty(key);
		if (attributes == null)
			return "";

		for (java.lang.String attrKey : attributes.keySet()) {
			if (attrKey.equalsIgnoreCase(key))
				return attributes.get(attrKey).getValue();
		}
		return "";
	}






	public void put(java.lang.String key, java.lang.String value) {
		org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(key, value);
		put(attr);
	}






	public void put(java.lang.String key, boolean value) {
		if (value)
			put(new org.jsoup.nodes.BooleanAttribute(key));else

			remove(key);
	}





	public void put(org.jsoup.nodes.Attribute attribute) {
		org.jsoup.helper.Validate.notNull(attribute);
		if (attributes == null)
			attributes = new java.util.LinkedHashMap<java.lang.String, org.jsoup.nodes.Attribute>(2);
		attributes.put(attribute.getKey(), attribute);
	}





	public void remove(java.lang.String key) {
		org.jsoup.helper.Validate.notEmpty(key);
		if (attributes == null)
			return;
		attributes.remove(key);
	}





	public void removeIgnoreCase(java.lang.String key) {
		org.jsoup.helper.Validate.notEmpty(key);
		if (attributes == null)
			return;
		for (java.util.Iterator<java.lang.String> it = attributes.keySet().iterator(); it.hasNext();) {
			java.lang.String attrKey = it.next();
			if (attrKey.equalsIgnoreCase(key)) 
			{ 				attributes.remove(attrKey); 				return;}
		}
	}






	public boolean hasKey(java.lang.String key) {
		return (attributes != null) && attributes.containsKey(key);
	}






	public boolean hasKeyIgnoreCase(java.lang.String key) {
		if (attributes == null)
			return false;
		for (java.lang.String attrKey : attributes.keySet()) {
			if (attrKey.equalsIgnoreCase(key))
				return true;
		}
		return false;
	}





	public int size() {
		if (attributes == null)
			return 0;
		return attributes.size();
	}





	public void addAll(org.jsoup.nodes.Attributes incoming) {
		if (incoming.size() == 0)
			return;
		if (attributes == null)
			attributes = new java.util.LinkedHashMap<java.lang.String, org.jsoup.nodes.Attribute>(incoming.size());
		attributes.putAll(incoming.attributes);
	}

	public java.util.Iterator<org.jsoup.nodes.Attribute> iterator() {
		if ((attributes == null) || attributes.isEmpty()) {
			return java.util.Collections.<org.jsoup.nodes.Attribute>emptyList().iterator();
		}

		return attributes.values().iterator();
	}






	public java.util.List<org.jsoup.nodes.Attribute> asList() {
		if (attributes == null)
			return java.util.Collections.emptyList();

		java.util.List<org.jsoup.nodes.Attribute> list = new java.util.ArrayList<org.jsoup.nodes.Attribute>(attributes.size());
		for (java.util.Map.Entry<java.lang.String, org.jsoup.nodes.Attribute> entry : attributes.entrySet()) {
			list.add(entry.getValue());
		}
		return java.util.Collections.unmodifiableList(list);
	}






	public java.util.Map<java.lang.String, java.lang.String> dataset() {
		return new org.jsoup.nodes.Attributes.Dataset();
	}






	public java.lang.String html() {
		java.lang.StringBuilder accum = new java.lang.StringBuilder();
		try {
			html(accum, new org.jsoup.nodes.Document("").outputSettings());
		} catch (java.io.IOException e) {
			throw new org.jsoup.SerializationException(e);
		}
		return accum.toString();
	}

	void html(java.lang.Appendable accum, org.jsoup.nodes.Document.OutputSettings out) throws java.io.IOException {
		if (attributes == null)
			return;

		for (java.util.Map.Entry<java.lang.String, org.jsoup.nodes.Attribute> entry : attributes.entrySet()) {
			org.jsoup.nodes.Attribute attribute = entry.getValue();
			accum.append(" ");
			attribute.html(accum, out);
		}
	}

	@java.lang.Override
	public java.lang.String toString() {
		return html();
	}






	@java.lang.Override
	public boolean equals(java.lang.Object o) {
		if (this == o) 			return true;
		if (!(o instanceof org.jsoup.nodes.Attributes)) 			return false;

		org.jsoup.nodes.Attributes that = ((org.jsoup.nodes.Attributes) (o));

		return !(attributes != null ? !attributes.equals(that.attributes) : that.attributes != null);
	}





	@java.lang.Override
	public int hashCode() {
		return attributes != null ? attributes.hashCode() : 0;
	}

	@java.lang.Override
	public org.jsoup.nodes.Attributes clone() {
		if (attributes == null)
			return new org.jsoup.nodes.Attributes();

		org.jsoup.nodes.Attributes clone;
		try {
			clone = ((org.jsoup.nodes.Attributes) (super.clone()));
		} catch (java.lang.CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
		clone.attributes = new java.util.LinkedHashMap<java.lang.String, org.jsoup.nodes.Attribute>(attributes.size());
		for (org.jsoup.nodes.Attribute attribute : this)
			clone.attributes.put(attribute.getKey(), attribute.clone());
		return clone;
	}

	private class Dataset extends java.util.AbstractMap<java.lang.String, java.lang.String> {

		private Dataset() {
			if (attributes == null)
				attributes = new java.util.LinkedHashMap<java.lang.String, org.jsoup.nodes.Attribute>(2);
		}

		@java.lang.Override
		public java.util.Set<java.util.Map.Entry<java.lang.String, java.lang.String>> entrySet() {
			return new org.jsoup.nodes.Attributes.Dataset.EntrySet();
		}

		@java.lang.Override
		public java.lang.String put(java.lang.String key, java.lang.String value) {
			java.lang.String dataKey = org.jsoup.nodes.Attributes.dataKey(key);
			java.lang.String oldValue = (hasKey(dataKey)) ? attributes.get(dataKey).getValue() : null;
			org.jsoup.nodes.Attribute attr = new org.jsoup.nodes.Attribute(dataKey, value);
			attributes.put(dataKey, attr);
			return oldValue;
		}

		private class EntrySet extends java.util.AbstractSet<java.util.Map.Entry<java.lang.String, java.lang.String>> {

			@java.lang.Override
			public java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> iterator() {
				return new org.jsoup.nodes.Attributes.Dataset.DatasetIterator();
			}

			@java.lang.Override
			public int size() {
				int count = 0;
				java.util.Iterator iter = new org.jsoup.nodes.Attributes.Dataset.DatasetIterator();
				while (iter.hasNext())
					count++;
				return count;
			}
		}

		private class DatasetIterator implements java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>> {
			private java.util.Iterator<org.jsoup.nodes.Attribute> attrIter = attributes.values().iterator();
			private org.jsoup.nodes.Attribute attr;
			public boolean hasNext() {
				while (attrIter.hasNext()) {
					attr = attrIter.next();
					if (attr.isDataAttribute()) 						return true;
				} 
				return false;
			}

			public java.util.Map.Entry<java.lang.String, java.lang.String> next() {
				return new org.jsoup.nodes.Attribute(attr.getKey().substring(org.jsoup.nodes.Attributes.dataPrefix.length()), attr.getValue());
			}

			public void remove() {
				attributes.remove(attr.getKey());
			}
		}
	}

	private static java.lang.String dataKey(java.lang.String key) {
		return org.jsoup.nodes.Attributes.dataPrefix + key;
	}}