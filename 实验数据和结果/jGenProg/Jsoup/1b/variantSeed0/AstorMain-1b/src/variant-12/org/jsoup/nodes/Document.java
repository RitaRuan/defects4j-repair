package org.jsoup.nodes;
public class Document extends 










org.jsoup.nodes.Element {







	public Document(java.lang.String baseUri) {
		super(org.jsoup.parser.Tag.valueOf("#root"), baseUri);
	}






	public static org.jsoup.nodes.Document createShell(java.lang.String baseUri) {
		org.apache.commons.lang.Validate.notNull(baseUri);

		org.jsoup.nodes.Document doc = new org.jsoup.nodes.Document(baseUri);
		org.jsoup.nodes.Element html = doc.appendElement("html");
		html.appendElement("head");
		html.appendElement("body");

		return doc;
	}





	public org.jsoup.nodes.Element head() {
		return getElementsByTag("head").first();
	}





	public org.jsoup.nodes.Element body() {
		return getElementsByTag("body").first();
	}





	public java.lang.String title() {
		org.jsoup.nodes.Element titleEl = getElementsByTag("title").first();
		return titleEl != null ? titleEl.text().trim() : "";
	}






	public void title(java.lang.String title) {
		org.apache.commons.lang.Validate.notNull(title);
		org.jsoup.nodes.Element titleEl = getElementsByTag("title").first();
		if (titleEl == null) {
			head().appendElement("title").text(title);
		} else {
			titleEl.text(title);
		}
	}






	public org.jsoup.nodes.Element createElement(java.lang.String tagName) {
		return new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf(tagName), this.baseUri());
	}






	public org.jsoup.nodes.Document normalise() {
		if (select("html").isEmpty())
			appendElement("html");
		if (head() == null)
			select("html").first().prependElement("head");
		if (body() == null)
			select("html").first().appendElement("body");



		normalise(head());
		normalise(select("html").first());
		normalise(this);

		return this;
	}


	private void normalise(org.jsoup.nodes.Element element) {
		java.util.List<org.jsoup.nodes.Node> toMove = new java.util.ArrayList<org.jsoup.nodes.Node>();
		for (org.jsoup.nodes.Node node : element.childNodes) {
			if (node instanceof org.jsoup.nodes.TextNode) {
				org.jsoup.nodes.TextNode tn = ((org.jsoup.nodes.TextNode) (node));
				if (!tn.isBlank()) 
				{}
			}
		}

		for (org.jsoup.nodes.Node node : toMove) {
			element.removeChild(node);
			body().appendChild(new org.jsoup.nodes.TextNode(" ", ""));
			body().appendChild(node);
		}
	}

	@java.lang.Override
	public java.lang.String outerHtml() {
		return super.html();
	}






	@java.lang.Override
	public org.jsoup.nodes.Element text(java.lang.String text) {
		body().text(text);
		return this;
	}

	@java.lang.Override
	public java.lang.String nodeName() {
		return "#document";
	}}