package org.jsoup.parser;
public class HtmlTreeBuilder extends 











org.jsoup.parser.TreeBuilder {

	private static final java.lang.String[] TagsScriptStyle = new java.lang.String[]{ "script", "style" };
	public static final java.lang.String[] TagsSearchInScope = new java.lang.String[]{ "applet", "caption", "html", "table", "td", "th", "marquee", "object" };
	private static final java.lang.String[] TagSearchList = new java.lang.String[]{ "ol", "ul" };
	private static final java.lang.String[] TagSearchButton = new java.lang.String[]{ "button" };
	private static final java.lang.String[] TagSearchTableScope = new java.lang.String[]{ "html", "table" };
	private static final java.lang.String[] TagSearchSelectScope = new java.lang.String[]{ "optgroup", "option" };
	private static final java.lang.String[] TagSearchEndTags = new java.lang.String[]{ "dd", "dt", "li", "option", "optgroup", "p", "rp", "rt" };
	private static final java.lang.String[] TagSearchSpecial = new java.lang.String[]{ "address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", 
	"blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", 
	"details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", 
	"frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", 
	"iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", 
	"noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", 
	"section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", 
	"title", "tr", "ul", "wbr", "xmp" };

	private org.jsoup.parser.HtmlTreeBuilderState state;
	private org.jsoup.parser.HtmlTreeBuilderState originalState;

	private boolean baseUriSetFromDoc = false;
	private org.jsoup.nodes.Element headElement;
	private org.jsoup.nodes.FormElement formElement;
	private org.jsoup.nodes.Element contextElement;
	private java.util.ArrayList<org.jsoup.nodes.Element> formattingElements = new java.util.ArrayList<org.jsoup.nodes.Element>();
	private java.util.List<java.lang.String> pendingTableCharacters = new java.util.ArrayList<java.lang.String>();
	private org.jsoup.parser.Token.EndTag emptyEnd = new org.jsoup.parser.Token.EndTag();

	private boolean framesetOk = true;
	private boolean fosterInserts = false;
	private boolean fragmentParsing = false;

	HtmlTreeBuilder() {}

	@java.lang.Override
	org.jsoup.nodes.Document parse(java.lang.String input, java.lang.String baseUri, org.jsoup.parser.ParseErrorList errors) {
		state = org.jsoup.parser.HtmlTreeBuilderState.Initial;
		baseUriSetFromDoc = false;
		return super.parse(input, baseUri, errors);
	}

	java.util.List<org.jsoup.nodes.Node> parseFragment(java.lang.String inputFragment, org.jsoup.nodes.Element context, java.lang.String baseUri, org.jsoup.parser.ParseErrorList errors) {

		state = org.jsoup.parser.HtmlTreeBuilderState.Initial;
		initialiseParse(inputFragment, baseUri, errors);
		contextElement = context;
		fragmentParsing = true;
		org.jsoup.nodes.Element root = null;

		if (context != null) {
			if (context.ownerDocument() != null)
				doc.quirksMode(context.ownerDocument().quirksMode());


			java.lang.String contextTag = context.tagName();
			if (org.jsoup.helper.StringUtil.in(contextTag, "title", "textarea"))
				tokeniser.transition(org.jsoup.parser.TokeniserState.Rcdata);else 
			if (org.jsoup.helper.StringUtil.in(contextTag, "iframe", "noembed", "noframes", "style", "xmp"))
				tokeniser.transition(org.jsoup.parser.TokeniserState.Rawtext);else 
			if (contextTag.equals("script"))
				tokeniser.transition(org.jsoup.parser.TokeniserState.ScriptData);else 
			if (contextTag.equals("noscript"))
				tokeniser.transition(org.jsoup.parser.TokeniserState.Data);else 
			if (contextTag.equals("plaintext"))
				tokeniser.transition(org.jsoup.parser.TokeniserState.Data);else

				tokeniser.transition(org.jsoup.parser.TokeniserState.Data);

			root = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("html"), baseUri);
			doc.appendChild(root);
			stack.add(root);
			resetInsertionMode();



			org.jsoup.select.Elements contextChain = context.parents();
			contextChain.add(0, context);
			for (org.jsoup.nodes.Element parent : contextChain) {
				if (parent instanceof org.jsoup.nodes.FormElement) {
					formElement = ((org.jsoup.nodes.FormElement) (parent));
					break;
				}
			}
		}

		runParser();
		if ((context != null) && (root != null))
			return root.childNodes();else

			return doc.childNodes();
	}

	@java.lang.Override
	protected boolean process(org.jsoup.parser.Token token) {
		currentToken = token;
		return this.state.process(token, this);
	}

	boolean process(org.jsoup.parser.Token token, org.jsoup.parser.HtmlTreeBuilderState state) {
		currentToken = token;
		return state.process(token, this);
	}

	void transition(org.jsoup.parser.HtmlTreeBuilderState state) {
		this.state = state;
	}

	org.jsoup.parser.HtmlTreeBuilderState state() {
		return state;
	}

	void markInsertionMode() {
		originalState = state;
	}

	org.jsoup.parser.HtmlTreeBuilderState originalState() {
		return originalState;
	}

	void framesetOk(boolean framesetOk) {
		this.framesetOk = framesetOk;
	}

	boolean framesetOk() {
		return framesetOk;
	}

	org.jsoup.nodes.Document getDocument() {
		return doc;
	}

	java.lang.String getBaseUri() {
		return baseUri;
	}

	void maybeSetBaseUri(org.jsoup.nodes.Element base) {
		if (baseUriSetFromDoc)
			return;

		java.lang.String href = base.absUrl("href");
		if (href.length() != 0) {
			baseUri = href;
			baseUriSetFromDoc = true;
			doc.setBaseUri(href);
		}
	}

	boolean isFragmentParsing() {
		return fragmentParsing;
	}

	void error(org.jsoup.parser.HtmlTreeBuilderState state) {
		if (errors.canAddError())
			errors.add(new org.jsoup.parser.ParseError(reader.pos(), "Unexpected token [%s] when in state [%s]", currentToken.tokenType(), state));
	}

	org.jsoup.nodes.Element insert(org.jsoup.parser.Token.StartTag startTag) {


		if (startTag.isSelfClosing()) {
			org.jsoup.nodes.Element el = insertEmpty(startTag);
			stack.add(el);
			tokeniser.transition(org.jsoup.parser.TokeniserState.Data);
			tokeniser.emit(emptyEnd.reset().name(el.tagName()));
			return el;
		}

		org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf(startTag.name()), baseUri, startTag.attributes);
		insert(el);
		return el;
	}

	org.jsoup.nodes.Element insertStartTag(java.lang.String startTagName) {
		org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf(startTagName), baseUri);
		insert(el);
		return el;
	}

	void insert(org.jsoup.nodes.Element el) {
		insertNode(el);
		stack.add(el);
	}

	org.jsoup.nodes.Element insertEmpty(org.jsoup.parser.Token.StartTag startTag) {
		org.jsoup.parser.Tag tag = org.jsoup.parser.Tag.valueOf(startTag.name());
		org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(tag, baseUri, startTag.attributes);
		insertNode(el);
		if (startTag.isSelfClosing()) {
			if (tag.isKnownTag()) {
				if (tag.isSelfClosing()) 					tokeniser.acknowledgeSelfClosingFlag();
			} else {

				tag.setSelfClosing();
				tokeniser.acknowledgeSelfClosingFlag();
			}
		}
		return el;
	}

	org.jsoup.nodes.FormElement insertForm(org.jsoup.parser.Token.StartTag startTag, boolean onStack) {
		org.jsoup.parser.Tag tag = org.jsoup.parser.Tag.valueOf(startTag.name());
		org.jsoup.nodes.FormElement el = new org.jsoup.nodes.FormElement(tag, baseUri, startTag.attributes);
		setFormElement(el);
		insertNode(el);
		if (onStack)
			stack.add(el);
		return el;
	}

	void insert(org.jsoup.parser.Token.Comment commentToken) {
		org.jsoup.nodes.Comment comment = new org.jsoup.nodes.Comment(commentToken.getData(), baseUri);
		insertNode(comment);
	}

	void insert(org.jsoup.parser.Token.Character characterToken) {
		org.jsoup.nodes.Node node;

		java.lang.String tagName = currentElement().tagName();
		if (tagName.equals("script") || tagName.equals("style"))
			node = new org.jsoup.nodes.DataNode(characterToken.getData(), baseUri);else

			node = new org.jsoup.nodes.TextNode(characterToken.getData(), baseUri);
		currentElement().appendChild(node);
	}

	private void insertNode(org.jsoup.nodes.Node node) {

		if (stack.size() == 0)
			doc.appendChild(node);else 
		if (isFosterInserts())
			insertInFosterParent(node);else

			currentElement().appendChild(node);


		if ((node instanceof org.jsoup.nodes.Element) && ((org.jsoup.nodes.Element) (node)).tag().isFormListed()) {
			if (formElement != null)
				formElement.addElement(((org.jsoup.nodes.Element) (node)));
		}
	}

	org.jsoup.nodes.Element pop() {
		int size = stack.size();
		return stack.remove(size - 1);
	}

	void push(org.jsoup.nodes.Element element) {
		stack.add(element);
	}

	java.util.ArrayList<org.jsoup.nodes.Element> getStack() {
		return stack;
	}

	boolean onStack(org.jsoup.nodes.Element el) {
		return isElementInQueue(stack, el);
	}

	private boolean isElementInQueue(java.util.ArrayList<org.jsoup.nodes.Element> queue, org.jsoup.nodes.Element element) {
		for (int pos = queue.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = queue.get(pos);
			if (next == element) {
				return true;
			}
		}
		return false;
	}

	org.jsoup.nodes.Element getFromStack(java.lang.String elName) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			if (next.nodeName().equals(elName)) {
				return next;
			}
		}
		return null;
	}

	boolean removeFromStack(org.jsoup.nodes.Element el) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			if (next == el) {
				stack.remove(pos);
				return true;
			}
		}
		return false;
	}

	void popStackToClose(java.lang.String elName) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			stack.remove(pos);
			if (next.nodeName().equals(elName))
				break;
		}
	}

	void popStackToClose(java.lang.String... elNames) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			stack.remove(pos);
			if (org.jsoup.helper.StringUtil.in(next.nodeName(), elNames))
				break;
		}
	}

	void popStackToBefore(java.lang.String elName) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			if (next.nodeName().equals(elName)) {
				break;
			} else {
				stack.remove(pos);
			}
		}
	}

	void clearStackToTableContext() {
		clearStackToContext("table");
	}

	void clearStackToTableBodyContext() {
		clearStackToContext("tbody", "tfoot", "thead");
	}

	void clearStackToTableRowContext() {
		clearStackToContext("tr");
	}

	private void clearStackToContext(java.lang.String... nodeNames) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			if (org.jsoup.helper.StringUtil.in(next.nodeName(), nodeNames) || next.nodeName().equals("html"))
				break;else

				stack.remove(pos);
		}
	}

	org.jsoup.nodes.Element aboveOnStack(org.jsoup.nodes.Element el) {
		assert onStack(el);
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = stack.get(pos);
			if (next == el) {
				return stack.get(pos - 1);
			}
		}
		return null;
	}

	void insertOnStackAfter(org.jsoup.nodes.Element after, org.jsoup.nodes.Element in) {
		int i = stack.lastIndexOf(after);
		org.jsoup.helper.Validate.isTrue(i != (-1));
		stack.add(i + 1, in);
	}

	void replaceOnStack(org.jsoup.nodes.Element out, org.jsoup.nodes.Element in) {
		replaceInQueue(stack, out, in);
	}

	private void replaceInQueue(java.util.ArrayList<org.jsoup.nodes.Element> queue, org.jsoup.nodes.Element out, org.jsoup.nodes.Element in) {
		int i = queue.lastIndexOf(out);
		org.jsoup.helper.Validate.isTrue(i != (-1));
		queue.set(i, in);
	}

	void resetInsertionMode() {
		boolean last = false;
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element node = stack.get(pos);
			if (pos == 0) {
				last = true;
				node = contextElement;
			}
			java.lang.String name = node.nodeName();
			if ("select".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InSelect);
				break;
			} else if ("td".equals(name) || ("td".equals(name) && (!last))) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InCell);
				break;
			} else if ("tr".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InRow);
				break;
			} else if (("tbody".equals(name) || "thead".equals(name)) || "tfoot".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InTableBody);
				break;
			} else if ("caption".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InCaption);
				break;
			} else if ("colgroup".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InColumnGroup);
				break;
			} else if ("table".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InTable);
				break;
			} else if ("head".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				break;
			} else if ("body".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				break;
			} else if ("frameset".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InFrameset);
				break;
			} else if ("html".equals(name)) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.BeforeHead);
				break;
			} else if (last) {
				transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				break;
			}
		}
	}


	private java.lang.String[] specificScopeTarget = new java.lang.String[]{ null };

	private boolean inSpecificScope(java.lang.String targetName, java.lang.String[] baseTypes, java.lang.String[] extraTypes) {
		specificScopeTarget[0] = targetName;
		return inSpecificScope(specificScopeTarget, baseTypes, extraTypes);
	}

	private boolean inSpecificScope(java.lang.String[] targetNames, java.lang.String[] baseTypes, java.lang.String[] extraTypes) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element el = stack.get(pos);
			java.lang.String elName = el.nodeName();
			if (org.jsoup.helper.StringUtil.in(elName, targetNames))
				return true;
			if (org.jsoup.helper.StringUtil.in(elName, baseTypes))
				return false;
			if ((extraTypes != null) && org.jsoup.helper.StringUtil.in(elName, extraTypes))
				return false;
		}
		org.jsoup.helper.Validate.fail("Should not be reachable");
		return false;
	}

	boolean inScope(java.lang.String[] targetNames) {
		return inSpecificScope(targetNames, org.jsoup.parser.HtmlTreeBuilder.TagsSearchInScope, null);
	}

	boolean inScope(java.lang.String targetName) {
		return inScope(targetName, null);
	}

	boolean inScope(java.lang.String targetName, java.lang.String[] extras) {
		return inSpecificScope(targetName, org.jsoup.parser.HtmlTreeBuilder.TagsSearchInScope, extras);


	}

	boolean inListItemScope(java.lang.String targetName) {
		return inScope(targetName, org.jsoup.parser.HtmlTreeBuilder.TagSearchList);
	}

	boolean inButtonScope(java.lang.String targetName) {
		return inScope(targetName, org.jsoup.parser.HtmlTreeBuilder.TagSearchButton);
	}

	boolean inTableScope(java.lang.String targetName) {
		return inSpecificScope(targetName, org.jsoup.parser.HtmlTreeBuilder.TagSearchTableScope, null);
	}

	boolean inSelectScope(java.lang.String targetName) {
		for (int pos = stack.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element el = stack.get(pos);
			java.lang.String elName = el.nodeName();
			if (elName.equals(targetName))
				return true;
			if (!org.jsoup.helper.StringUtil.in(elName, org.jsoup.parser.HtmlTreeBuilder.TagSearchSelectScope))
				return false;
		}
		org.jsoup.helper.Validate.fail("Should not be reachable");
		return false;
	}

	void setHeadElement(org.jsoup.nodes.Element headElement) {
		this.headElement = headElement;
	}

	org.jsoup.nodes.Element getHeadElement() {
		return headElement;
	}

	boolean isFosterInserts() {
		return fosterInserts;
	}

	void setFosterInserts(boolean fosterInserts) {
		this.fosterInserts = fosterInserts;
	}

	org.jsoup.nodes.FormElement getFormElement() {
		return formElement;
	}

	void setFormElement(org.jsoup.nodes.FormElement formElement) {
		this.formElement = formElement;
	}

	void newPendingTableCharacters() {
		pendingTableCharacters = new java.util.ArrayList<java.lang.String>();
	}

	java.util.List<java.lang.String> getPendingTableCharacters() {
		return pendingTableCharacters;
	}

	void setPendingTableCharacters(java.util.List<java.lang.String> pendingTableCharacters) {
		this.pendingTableCharacters = pendingTableCharacters;
	}










	void generateImpliedEndTags(java.lang.String excludeTag) {
		while (((excludeTag != null) && (!currentElement().nodeName().equals(excludeTag))) && 
		org.jsoup.helper.StringUtil.in(currentElement().nodeName(), org.jsoup.parser.HtmlTreeBuilder.TagSearchEndTags))
			pop();
	}

	void generateImpliedEndTags() {
		generateImpliedEndTags(null);
	}

	boolean isSpecial(org.jsoup.nodes.Element el) {


		java.lang.String name = el.nodeName();
		return org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilder.TagSearchSpecial);
	}

	org.jsoup.nodes.Element lastFormattingElement() {
		return formattingElements.size() > 0 ? formattingElements.get(formattingElements.size() - 1) : null;
	}

	org.jsoup.nodes.Element removeLastFormattingElement() {
		int size = formattingElements.size();
		if (size > 0)
			return formattingElements.remove(size - 1);else

			return null;
	}


	void pushActiveFormattingElements(org.jsoup.nodes.Element in) {
		int numSeen = 0;
		for (int pos = formattingElements.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element el = formattingElements.get(pos);
			if (el == null)
				break;

			if (isSameFormattingElement(in, el))
				numSeen++;

			if (numSeen == 3) {
				formattingElements.remove(pos);
				break;
			}
		}
		formattingElements.add(in);
	}

	private boolean isSameFormattingElement(org.jsoup.nodes.Element a, org.jsoup.nodes.Element b) {

		return a.nodeName().equals(b.nodeName()) && 

		a.attributes().equals(b.attributes());

	}

	void reconstructFormattingElements() {
		org.jsoup.nodes.Element last = lastFormattingElement();
		if ((last == null) || onStack(last))
			return;

		org.jsoup.nodes.Element entry = last;
		int size = formattingElements.size();
		int pos = size - 1;
		boolean skip = false;
		while (true) {
			if (pos == 0) {
				skip = true;
				break;
			}
			entry = formattingElements.get(--pos);
			if ((entry == null) || onStack(entry))
				break;
		} 
		while (true) {
			if (!skip)
				entry = formattingElements.get(++pos);
			org.jsoup.helper.Validate.notNull(entry);


			skip = false;
			org.jsoup.nodes.Element newEl = insertStartTag(entry.nodeName());

			newEl.attributes().addAll(entry.attributes());


			formattingElements.set(pos, newEl);


			if (pos == (size - 1))
				break;
		} 
	}

	void clearFormattingElementsToLastMarker() {
		while (!formattingElements.isEmpty()) {
			org.jsoup.nodes.Element el = removeLastFormattingElement();
			if (el == null)
				break;
		} 
	}

	void removeFromActiveFormattingElements(org.jsoup.nodes.Element el) {
		for (int pos = formattingElements.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = formattingElements.get(pos);
			if (next == el) {
				formattingElements.remove(pos);
				break;
			}
		}
	}

	boolean isInActiveFormattingElements(org.jsoup.nodes.Element el) {
		return isElementInQueue(formattingElements, el);
	}

	org.jsoup.nodes.Element getActiveFormattingElement(java.lang.String nodeName) {
		for (int pos = formattingElements.size() - 1; pos >= 0; pos--) {
			org.jsoup.nodes.Element next = formattingElements.get(pos);
			if (next == null)
				break;else 
			if (next.nodeName().equals(nodeName))
				return next;
		}
		return null;
	}

	void replaceActiveFormattingElement(org.jsoup.nodes.Element out, org.jsoup.nodes.Element in) {
		replaceInQueue(formattingElements, out, in);
	}

	void insertMarkerToFormattingElements() {
		formattingElements.add(null);
	}

	void insertInFosterParent(org.jsoup.nodes.Node in) {
		org.jsoup.nodes.Element fosterParent;
		org.jsoup.nodes.Element lastTable = getFromStack("table");
		boolean isLastTableParent = false;
		if (lastTable != null) {
			if (lastTable.parent() != null) {
				fosterParent = lastTable.parent();
				isLastTableParent = true;
			} else
				fosterParent = aboveOnStack(lastTable);
		} else {
			fosterParent = stack.get(0);
		}

		if (isLastTableParent) {
			org.jsoup.helper.Validate.notNull(lastTable);
			lastTable.before(in);
		} else

			fosterParent.appendChild(in);
	}

	@java.lang.Override
	public java.lang.String toString() {
		return (((((("TreeBuilder{" + 
		"currentToken=") + currentToken) + 
		", state=") + state) + 
		", currentElement=") + currentElement()) + 
		'}';
	}}