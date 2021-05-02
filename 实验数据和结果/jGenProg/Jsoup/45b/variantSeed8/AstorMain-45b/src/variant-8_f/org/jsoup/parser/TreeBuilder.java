package org.jsoup.parser;
abstract class TreeBuilder {
	org.jsoup.parser.CharacterReader reader;

	org.jsoup.parser.Tokeniser tokeniser;

	protected org.jsoup.nodes.Document doc;

	protected java.util.ArrayList<org.jsoup.nodes.Element> stack;

	protected java.lang.String baseUri;

	protected org.jsoup.parser.Token currentToken;

	protected org.jsoup.parser.ParseErrorList errors;

	private org.jsoup.parser.Token.StartTag start = new org.jsoup.parser.Token.StartTag();

	private org.jsoup.parser.Token.EndTag end = new org.jsoup.parser.Token.EndTag();

	protected void initialiseParse(java.lang.String input, java.lang.String baseUri, org.jsoup.parser.ParseErrorList errors) {
		org.jsoup.helper.Validate.notNull(input, "String input must not be null");
		org.jsoup.helper.Validate.notNull(baseUri, "BaseURI must not be null");
		doc = new org.jsoup.nodes.Document(baseUri);
		reader = new org.jsoup.parser.CharacterReader(input);
		this.errors = errors;
		tokeniser = new org.jsoup.parser.Tokeniser(reader, errors);
		stack = new java.util.ArrayList<org.jsoup.nodes.Element>(32);
		this.baseUri = baseUri;
	}

	org.jsoup.nodes.Document parse(java.lang.String input, java.lang.String baseUri) {
		return parse(input, baseUri, org.jsoup.parser.ParseErrorList.noTracking());
	}

	org.jsoup.nodes.Document parse(java.lang.String input, java.lang.String baseUri, org.jsoup.parser.ParseErrorList errors) {
		initialiseParse(input, baseUri, errors);
		runParser();
		return doc;
	}

	protected void runParser() {
		while (true) {
			org.jsoup.parser.Token token = tokeniser.read();
			process(token);
			token.reset();
			if (token.type == org.jsoup.parser.Token.TokenType.EOF)
				break;

		} 
	}

	protected abstract boolean process(org.jsoup.parser.Token token);

	protected boolean processStartTag(java.lang.String name) {
		if (currentToken == start) {
			return process(new org.jsoup.parser.Token.StartTag().name(name));
		}
		return process(start.reset().name(name));
	}

	public boolean processStartTag(java.lang.String name, org.jsoup.nodes.Attributes attrs) {
		if (currentToken == start) {
			return process(new org.jsoup.parser.Token.StartTag().nameAttr(name, attrs));
		}
		start.reset();
		start.nameAttr(name, attrs);
		return process(start);
	}

	protected boolean processEndTag(java.lang.String name) {
		if (currentToken == end) {
			return process(new org.jsoup.parser.Token.EndTag().name(name));
		}
		return process(end.reset().name(name));
	}

	protected org.jsoup.nodes.Element currentElement() {
		int size = stack.size();
		return size > 0 ? stack.get(size - 1) : null;
	}
}