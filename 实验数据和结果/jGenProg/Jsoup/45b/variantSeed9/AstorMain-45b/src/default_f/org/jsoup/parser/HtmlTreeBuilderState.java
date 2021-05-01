package org.jsoup.parser;
enum HtmlTreeBuilderState {

	Initial() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				return true;
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				org.jsoup.parser.Token.Doctype d = t.asDoctype();
				org.jsoup.nodes.DocumentType doctype = new org.jsoup.nodes.DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri());
				tb.getDocument().appendChild(doctype);
				if (d.isForceQuirks())
					tb.getDocument().quirksMode(org.jsoup.nodes.Document.QuirksMode.quirks);

				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.BeforeHtml);
			} else {
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.BeforeHtml);
				return tb.process(t);
			}
			return true;
		}
	},
	BeforeHtml() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				return true;
			} else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
				tb.insert(t.asStartTag());
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.BeforeHead);
			} else if (t.isEndTag() && org.jsoup.helper.StringUtil.in(t.asEndTag().name(), "head", "body", "html", "br")) {
				return anythingElse(t, tb);
			} else if (t.isEndTag()) {
				tb.error(this);
				return false;
			} else {
				return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			tb.insertStartTag("html");
			tb.transition(org.jsoup.parser.HtmlTreeBuilderState.BeforeHead);
			return tb.process(t);
		}
	},
	BeforeHead() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				return true;
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
				return org.jsoup.parser.HtmlTreeBuilderState.InBody.process(t, tb);
			} else if (t.isStartTag() && t.asStartTag().name().equals("head")) {
				org.jsoup.nodes.Element head = tb.insert(t.asStartTag());
				tb.setHeadElement(head);
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InHead);
			} else if (t.isEndTag() && org.jsoup.helper.StringUtil.in(t.asEndTag().name(), "head", "body", "html", "br")) {
				tb.processStartTag("head");
				return tb.process(t);
			} else if (t.isEndTag()) {
				tb.error(this);
				return false;
			} else {
				tb.processStartTag("head");
				return tb.process(t);
			}
			return true;
		}
	},
	InHead() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				tb.insert(t.asCharacter());
				return true;
			}
			switch (t.type) {
				case Comment :
					tb.insert(t.asComment());
					break;
				case Doctype :
					tb.error(this);
					return false;
				case StartTag :
					org.jsoup.parser.Token.StartTag start = t.asStartTag();
					java.lang.String name = start.name();
					if (name.equals("html")) {
						return org.jsoup.parser.HtmlTreeBuilderState.InBody.process(t, tb);
					} else if (org.jsoup.helper.StringUtil.in(name, "base", "basefont", "bgsound", "command", "link")) {
						org.jsoup.nodes.Element el = tb.insertEmpty(start);
						if (name.equals("base") && el.hasAttr("href"))
							tb.maybeSetBaseUri(el);

					} else if (name.equals("meta")) {
						org.jsoup.nodes.Element meta = tb.insertEmpty(start);
					} else if (name.equals("title")) {
						org.jsoup.parser.HtmlTreeBuilderState.handleRcData(start, tb);
					} else if (org.jsoup.helper.StringUtil.in(name, "noframes", "style")) {
						org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(start, tb);
					} else if (name.equals("noscript")) {
						tb.insert(start);
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InHeadNoscript);
					} else if (name.equals("script")) {
						tb.tokeniser.transition(org.jsoup.parser.TokeniserState.ScriptData);
						tb.markInsertionMode();
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.Text);
						tb.insert(start);
					} else if (name.equals("head")) {
						tb.error(this);
						return false;
					} else {
						return anythingElse(t, tb);
					}
					break;
				case EndTag :
					org.jsoup.parser.Token.EndTag end = t.asEndTag();
					name = end.name();
					if (name.equals("head")) {
						tb.pop();
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.AfterHead);
					} else if (org.jsoup.helper.StringUtil.in(name, "body", "html", "br")) {
						return anythingElse(t, tb);
					} else {
						tb.error(this);
						return false;
					}
					break;
				default :
					return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.TreeBuilder tb) {
			tb.processEndTag("head");
			return tb.process(t);
		}
	},
	InHeadNoscript() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isDoctype()) {
				tb.error(this);
			} else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isEndTag() && t.asEndTag().name().equals("noscript")) {
				tb.pop();
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InHead);
			} else if ((org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t) || t.isComment()) || (t.isStartTag() && org.jsoup.helper.StringUtil.in(t.asStartTag().name(), "basefont", "bgsound", "link", "meta", "noframes", "style"))) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
			} else if (t.isEndTag() && t.asEndTag().name().equals("br")) {
				return anythingElse(t, tb);
			} else if ((t.isStartTag() && org.jsoup.helper.StringUtil.in(t.asStartTag().name(), "head", "noscript")) || t.isEndTag()) {
				tb.error(this);
				return false;
			} else {
				return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			tb.error(this);
			tb.insert(new org.jsoup.parser.Token.Character().data(t.toString()));
			return true;
		}
	},
	AfterHead() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				tb.insert(t.asCharacter());
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				tb.error(this);
			} else if (t.isStartTag()) {
				org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
				java.lang.String name = startTag.name();
				if (name.equals("html")) {
					return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
				} else if (name.equals("body")) {
					tb.insert(startTag);
					tb.framesetOk(false);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				} else if (name.equals("frameset")) {
					tb.insert(startTag);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InFrameset);
				} else if (org.jsoup.helper.StringUtil.in(name, "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title")) {
					tb.error(this);
					org.jsoup.nodes.Element head = tb.getHeadElement();
					tb.push(head);
					tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
					tb.removeFromStack(head);
				} else if (name.equals("head")) {
					tb.error(this);
					return false;
				} else {
					anythingElse(t, tb);
				}
			} else if (t.isEndTag()) {
				if (org.jsoup.helper.StringUtil.in(t.asEndTag().name(), "body", "html")) {
					anythingElse(t, tb);
				} else {
					tb.error(this);
					return false;
				}
			} else {
				anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			tb.processStartTag("body");
			tb.framesetOk(true);
			return tb.process(t);
		}
	},
	InBody() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			switch (t.type) {
				case Character :
					{
						org.jsoup.parser.Token.Character c = t.asCharacter();
						if (c.getData().equals(org.jsoup.parser.HtmlTreeBuilderState.nullString)) {
							tb.error(this);
							return false;
						} else if (tb.framesetOk() && org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(c)) {
							tb.reconstructFormattingElements();
							tb.insert(c);
						} else {
							tb.reconstructFormattingElements();
							tb.insert(c);
							tb.framesetOk(false);
						}
						break;
					}
				case Comment :
					{
						tb.insert(t.asComment());
						break;
					}
				case Doctype :
					{
						tb.error(this);
						return false;
					}
				case StartTag :
					org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
					java.lang.String name = startTag.name();
					if (name.equals("html")) {
						tb.error(this);
						org.jsoup.nodes.Element html = tb.getStack().get(0);
						for (org.jsoup.nodes.Attribute attribute : startTag.getAttributes()) {
							if (!html.hasAttr(attribute.getKey()))
								html.attributes().put(attribute);

						}
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartToHead)) {
						return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
					} else if (name.equals("body")) {
						tb.error(this);
						java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
						if ((stack.size() == 1) || ((stack.size() > 2) && (!stack.get(1).nodeName().equals("body")))) {
							return false;
						} else {
							tb.framesetOk(false);
							org.jsoup.nodes.Element body = stack.get(1);
							for (org.jsoup.nodes.Attribute attribute : startTag.getAttributes()) {
								if (!body.hasAttr(attribute.getKey()))
									body.attributes().put(attribute);

							}
						}
					} else if (name.equals("frameset")) {
						tb.error(this);
						java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
						if ((stack.size() == 1) || ((stack.size() > 2) && (!stack.get(1).nodeName().equals("body")))) {
							return false;
						} else if (!tb.framesetOk()) {
							return false;
						} else {
							org.jsoup.nodes.Element second = stack.get(1);
							if (second.parent() != null)
								second.remove();

							while (stack.size() > 1)
								stack.remove(stack.size() - 1);

							tb.insert(startTag);
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InFrameset);
						}
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartPClosers)) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings)) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						if (org.jsoup.helper.StringUtil.in(tb.currentElement().nodeName(), org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings)) {
							tb.error(this);
							tb.pop();
						}
						tb.insert(startTag);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartPreListing)) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
						tb.framesetOk(false);
					} else if (name.equals("form")) {
						if (tb.getFormElement() != null) {
							tb.error(this);
							return false;
						}
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insertForm(startTag, true);
					} else if (name.equals("li")) {
						tb.framesetOk(false);
						java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
						for (int i = stack.size() - 1; i > 0; i--) {
							org.jsoup.nodes.Element el = stack.get(i);
							if (el.nodeName().equals("li")) {
								tb.processEndTag("li");
								break;
							}
							if (tb.isSpecial(el) && (!org.jsoup.helper.StringUtil.in(el.nodeName(), org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartLiBreakers)))
								break;

						}
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt)) {
						tb.framesetOk(false);
						java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
						for (int i = stack.size() - 1; i > 0; i--) {
							org.jsoup.nodes.Element el = stack.get(i);
							if (org.jsoup.helper.StringUtil.in(el.nodeName(), org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt)) {
								tb.processEndTag(el.nodeName());
								break;
							}
							if (tb.isSpecial(el) && (!org.jsoup.helper.StringUtil.in(el.nodeName(), org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartLiBreakers)))
								break;

						}
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
					} else if (name.equals("plaintext")) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
						tb.tokeniser.transition(org.jsoup.parser.TokeniserState.PLAINTEXT);
					} else if (name.equals("button")) {
						if (tb.inButtonScope("button")) {
							tb.error(this);
							tb.processEndTag("button");
							tb.process(startTag);
						} else {
							tb.reconstructFormattingElements();
							tb.insert(startTag);
							tb.framesetOk(false);
						}
					} else if (name.equals("a")) {
						if (tb.getActiveFormattingElement("a") != null) {
							tb.error(this);
							tb.processEndTag("a");
							org.jsoup.nodes.Element remainingA = tb.getFromStack("a");
							if (remainingA != null) {
								tb.removeFromActiveFormattingElements(remainingA);
								tb.removeFromStack(remainingA);
							}
						}
						tb.reconstructFormattingElements();
						org.jsoup.nodes.Element a = tb.insert(startTag);
						tb.pushActiveFormattingElements(a);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.Formatters)) {
						tb.reconstructFormattingElements();
						org.jsoup.nodes.Element el = tb.insert(startTag);
						tb.pushActiveFormattingElements(el);
					} else if (name.equals("nobr")) {
						tb.reconstructFormattingElements();
						if (tb.inScope("nobr")) {
							tb.error(this);
							tb.processEndTag("nobr");
							tb.reconstructFormattingElements();
						}
						org.jsoup.nodes.Element el = tb.insert(startTag);
						tb.pushActiveFormattingElements(el);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartApplets)) {
						tb.reconstructFormattingElements();
						tb.insert(startTag);
						tb.insertMarkerToFormattingElements();
						tb.framesetOk(false);
					} else if (name.equals("table")) {
						if ((tb.getDocument().quirksMode() != org.jsoup.nodes.Document.QuirksMode.quirks) && tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insert(startTag);
						tb.framesetOk(false);
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTable);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartEmptyFormatters)) {
						tb.reconstructFormattingElements();
						tb.insertEmpty(startTag);
						tb.framesetOk(false);
					} else if (name.equals("input")) {
						tb.reconstructFormattingElements();
						org.jsoup.nodes.Element el = tb.insertEmpty(startTag);
						if (!el.attr("type").equalsIgnoreCase("hidden"))
							tb.framesetOk(false);

					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartMedia)) {
						tb.insertEmpty(startTag);
					} else if (name.equals("hr")) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.insertEmpty(startTag);
						tb.framesetOk(false);
					} else if (name.equals("image")) {
						if (tb.getFromStack("svg") == null)
							return tb.process(startTag.name("img"));
						else
							tb.insert(startTag);

					} else if (name.equals("isindex")) {
						tb.error(this);
						if (tb.getFormElement() != null)
							return false;

						tb.tokeniser.acknowledgeSelfClosingFlag();
						tb.processStartTag("form");
						if (startTag.attributes.hasKey("action")) {
							org.jsoup.nodes.Element form = tb.getFormElement();
							form.attr("action", startTag.attributes.get("action"));
						}
						tb.processStartTag("hr");
						tb.processStartTag("label");
						java.lang.String prompt = (startTag.attributes.hasKey("prompt")) ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
						tb.process(new org.jsoup.parser.Token.Character().data(prompt));
						org.jsoup.nodes.Attributes inputAttribs = new org.jsoup.nodes.Attributes();
						for (org.jsoup.nodes.Attribute attr : startTag.attributes) {
							if (!org.jsoup.helper.StringUtil.in(attr.getKey(), org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartInputAttribs))
								inputAttribs.put(attr);

						}
						inputAttribs.put("name", "isindex");
						tb.processStartTag("input", inputAttribs);
						tb.processEndTag("label");
						tb.processStartTag("hr");
						tb.processEndTag("form");
					} else if (name.equals("textarea")) {
						tb.insert(startTag);
						tb.tokeniser.transition(org.jsoup.parser.TokeniserState.Rcdata);
						tb.markInsertionMode();
						tb.framesetOk(false);
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.Text);
					} else if (name.equals("xmp")) {
						if (tb.inButtonScope("p")) {
							tb.processEndTag("p");
						}
						tb.reconstructFormattingElements();
						tb.framesetOk(false);
						org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(startTag, tb);
					} else if (name.equals("iframe")) {
						tb.framesetOk(false);
						org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(startTag, tb);
					} else if (name.equals("noembed")) {
						org.jsoup.parser.HtmlTreeBuilderState.handleRawtext(startTag, tb);
					} else if (name.equals("select")) {
						tb.reconstructFormattingElements();
						tb.insert(startTag);
						tb.framesetOk(false);
						org.jsoup.parser.HtmlTreeBuilderState state = tb.state();
						if ((((state.equals(org.jsoup.parser.HtmlTreeBuilderState.InTable) || state.equals(org.jsoup.parser.HtmlTreeBuilderState.InCaption)) || state.equals(org.jsoup.parser.HtmlTreeBuilderState.InTableBody)) || state.equals(org.jsoup.parser.HtmlTreeBuilderState.InRow)) || state.equals(org.jsoup.parser.HtmlTreeBuilderState.InCell))
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InSelectInTable);
						else
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InSelect);

					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartOptions)) {
						if (tb.currentElement().nodeName().equals("option"))
							tb.processEndTag("option");

						tb.reconstructFormattingElements();
						tb.insert(startTag);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartRuby)) {
						if (tb.inScope("ruby")) {
							tb.generateImpliedEndTags();
							if (!tb.currentElement().nodeName().equals("ruby")) {
								tb.error(this);
								tb.popStackToBefore("ruby");
							}
							tb.insert(startTag);
						}
					} else if (name.equals("math")) {
						tb.reconstructFormattingElements();
						tb.insert(startTag);
						tb.tokeniser.acknowledgeSelfClosingFlag();
					} else if (name.equals("svg")) {
						tb.reconstructFormattingElements();
						tb.insert(startTag);
						tb.tokeniser.acknowledgeSelfClosingFlag();
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartDrop)) {
						tb.error(this);
						return false;
					} else {
						tb.reconstructFormattingElements();
						tb.insert(startTag);
					}
					break;
				case EndTag :
					org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
					name = endTag.name();
					if (name.equals("body")) {
						if (!tb.inScope("body")) {
							tb.error(this);
							return false;
						} else {
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.AfterBody);
						}
					} else if (name.equals("html")) {
						boolean notIgnored = tb.processEndTag("body");
						if (notIgnored)
							return tb.process(endTag);

					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndClosers)) {
						if (!tb.inScope(name)) {
							tb.error(this);
							return false;
						} else {
							tb.generateImpliedEndTags();
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(name);
						}
					} else if (name.equals("form")) {
						org.jsoup.nodes.Element currentForm = tb.getFormElement();
						tb.setFormElement(null);
						if ((currentForm == null) || (!tb.inScope(name))) {
							tb.error(this);
							return false;
						} else {
							tb.generateImpliedEndTags();
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.removeFromStack(currentForm);
						}
					} else if (name.equals("p")) {
						if (!tb.inButtonScope(name)) {
							tb.error(this);
							tb.processStartTag(name);
							return tb.process(endTag);
						} else {
							tb.generateImpliedEndTags(name);
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(name);
						}
					} else if (name.equals("li")) {
						if (!tb.inListItemScope(name)) {
							tb.error(this);
							return false;
						} else {
							tb.generateImpliedEndTags(name);
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(name);
						}
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.DdDt)) {
						if (!tb.inScope(name)) {
							tb.error(this);
							return false;
						} else {
							tb.generateImpliedEndTags(name);
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(name);
						}
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings)) {
						if (!tb.inScope(org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings)) {
							tb.error(this);
							return false;
						} else {
							tb.generateImpliedEndTags(name);
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(org.jsoup.parser.HtmlTreeBuilderState.Constants.Headings);
						}
					} else if (name.equals("sarcasm")) {
						return anyOtherEndTag(t, tb);
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndAdoptionFormatters)) {
						for (int i = 0; i < 8; i++) {
							org.jsoup.nodes.Element formatEl = tb.getActiveFormattingElement(name);
							if (formatEl == null)
								return anyOtherEndTag(t, tb);
							else if (!tb.onStack(formatEl)) {
								tb.error(this);
								tb.removeFromActiveFormattingElements(formatEl);
								return true;
							} else if (!tb.inScope(formatEl.nodeName())) {
								tb.error(this);
								return false;
							} else if (tb.currentElement() != formatEl)
								tb.error(this);

							org.jsoup.nodes.Element furthestBlock = null;
							org.jsoup.nodes.Element commonAncestor = null;
							boolean seenFormattingElement = false;
							java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
							final int stackSize = stack.size();
							for (int si = 0; (si < stackSize) && (si < 64); si++) {
								org.jsoup.nodes.Element el = stack.get(si);
								if (el == formatEl) {
									commonAncestor = stack.get(si - 1);
									seenFormattingElement = true;
								} else if (seenFormattingElement && tb.isSpecial(el)) {
									furthestBlock = el;
									break;
								}
							}
							if (furthestBlock == null) {
								tb.popStackToClose(formatEl.nodeName());
								tb.removeFromActiveFormattingElements(formatEl);
								return true;
							}
							org.jsoup.nodes.Element node = furthestBlock;
							org.jsoup.nodes.Element lastNode = furthestBlock;
							for (int j = 0; j < 3; j++) {
								if (tb.onStack(node))
									node = tb.aboveOnStack(node);

								if (!tb.isInActiveFormattingElements(node)) {
									tb.removeFromStack(node);
									continue;
								} else if (node == formatEl)
									break;

								org.jsoup.nodes.Element replacement = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf(node.nodeName()), tb.getBaseUri());
								tb.replaceActiveFormattingElement(node, replacement);
								tb.replaceOnStack(node, replacement);
								node = replacement;
								if (lastNode == furthestBlock) {
								}
								if (lastNode.parent() != null)
									lastNode.remove();

								node.appendChild(lastNode);
								lastNode = node;
							}
							if (org.jsoup.helper.StringUtil.in(commonAncestor.nodeName(), org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyEndTableFosters)) {
								if (lastNode.parent() != null)
									lastNode.remove();

								tb.insertInFosterParent(lastNode);
							} else {
								if (lastNode.parent() != null)
									lastNode.remove();

								commonAncestor.appendChild(lastNode);
							}
							org.jsoup.nodes.Element adopter = new org.jsoup.nodes.Element(formatEl.tag(), tb.getBaseUri());
							adopter.attributes().addAll(formatEl.attributes());
							org.jsoup.nodes.Node[] childNodes = furthestBlock.childNodes().toArray(new org.jsoup.nodes.Node[furthestBlock.childNodeSize()]);
							for (org.jsoup.nodes.Node childNode : childNodes) {
								adopter.appendChild(childNode);
							}
							furthestBlock.appendChild(adopter);
							tb.removeFromActiveFormattingElements(formatEl);
							tb.removeFromStack(formatEl);
							tb.insertOnStackAfter(furthestBlock, adopter);
						}
					} else if (org.jsoup.helper.StringUtil.in(name, org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartApplets)) {
						if (!tb.inScope("name")) {
							if (!tb.inScope(name)) {
								tb.error(this);
								return false;
							}
							tb.generateImpliedEndTags();
							if (!tb.currentElement().nodeName().equals(name))
								tb.error(this);

							tb.popStackToClose(name);
							tb.clearFormattingElementsToLastMarker();
						}
					} else if (name.equals("br")) {
						tb.error(this);
						tb.processStartTag("br");
						return false;
					} else {
						return anyOtherEndTag(t, tb);
					}
					break;
				case EOF :
					break;
			}
			return true;
		}

		boolean anyOtherEndTag(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			java.lang.String name = t.asEndTag().name();
			java.util.ArrayList<org.jsoup.nodes.Element> stack = tb.getStack();
			for (int pos = stack.size() - 1; pos >= 0; pos--) {
				org.jsoup.nodes.Element node = stack.get(pos);
				if (node.nodeName().equals(name)) {
					tb.generateImpliedEndTags(name);
					if (!name.equals(tb.currentElement().nodeName()))
						tb.error(this);

					tb.popStackToClose(name);
					break;
				} else if (tb.isSpecial(node)) {
					tb.error(this);
					return false;
				}
			}
			return true;
		}
	},
	Text() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isCharacter()) {
				tb.insert(t.asCharacter());
			} else if (t.isEOF()) {
				tb.error(this);
				tb.pop();
				tb.transition(tb.originalState());
				return tb.process(t);
			} else if (t.isEndTag()) {
				tb.pop();
				tb.transition(tb.originalState());
			}
			return true;
		}
	},
	InTable() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isCharacter()) {
				tb.newPendingTableCharacters();
				tb.markInsertionMode();
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTableText);
				return tb.process(t);
			} else if (t.isComment()) {
				tb.insert(t.asComment());
				return true;
			} else if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isStartTag()) {
				org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
				java.lang.String name = startTag.name();
				if (name.equals("caption")) {
					tb.clearStackToTableContext();
					tb.insertMarkerToFormattingElements();
					tb.insert(startTag);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InCaption);
				} else if (name.equals("colgroup")) {
					tb.clearStackToTableContext();
					tb.insert(startTag);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InColumnGroup);
				} else if (name.equals("col")) {
					tb.processStartTag("colgroup");
					return tb.process(t);
				} else if (org.jsoup.helper.StringUtil.in(name, "tbody", "tfoot", "thead")) {
					tb.clearStackToTableContext();
					tb.insert(startTag);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTableBody);
				} else if (org.jsoup.helper.StringUtil.in(name, "td", "th", "tr")) {
					tb.processStartTag("tbody");
					return tb.process(t);
				} else if (name.equals("table")) {
					tb.error(this);
					boolean processed = tb.processEndTag("table");
					if (processed) {
						return tb.process(t);
					}
				} else if (org.jsoup.helper.StringUtil.in(name, "style", "script")) {
					return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
				} else if (name.equals("input")) {
					if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
						return anythingElse(t, tb);
					} else {
						tb.insertEmpty(startTag);
					}
				} else if (name.equals("form")) {
					tb.error(this);
					if (tb.getFormElement() != null)
						return false;
					else {
						tb.insertForm(startTag, false);
					}
				} else {
					return anythingElse(t, tb);
				}
				return true;
			} else if (t.isEndTag()) {
				org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
				java.lang.String name = endTag.name();
				if (name.equals("table")) {
					if (!tb.inTableScope(name)) {
						tb.error(this);
						return false;
					} else {
						tb.popStackToClose("table");
					}
					tb.resetInsertionMode();
				} else if (org.jsoup.helper.StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
					tb.error(this);
					return false;
				} else {
					return anythingElse(t, tb);
				}
				return true;
			} else if (t.isEOF()) {
				if (tb.currentElement().nodeName().equals("html"))
					tb.error(this);

				return true;
			}
			return anythingElse(t, tb);
		}

		boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			tb.error(this);
			boolean processed;
			if (org.jsoup.helper.StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
				tb.setFosterInserts(true);
				processed = tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
				tb.setFosterInserts(false);
			} else {
				processed = tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			}
			return processed;
		}
	},
	InTableText() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			switch (t.type) {
				case Character :
					org.jsoup.parser.Token.Character c = t.asCharacter();
					if (c.getData().equals(org.jsoup.parser.HtmlTreeBuilderState.nullString)) {
						tb.error(this);
						return false;
					} else {
						tb.getPendingTableCharacters().add(c.getData());
					}
					break;
				default :
					if (tb.getPendingTableCharacters().size() > 0) {
						for (java.lang.String character : tb.getPendingTableCharacters()) {
							if (!org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(character)) {
								tb.error(this);
								if (org.jsoup.helper.StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
									tb.setFosterInserts(true);
									tb.process(new org.jsoup.parser.Token.Character().data(character), org.jsoup.parser.HtmlTreeBuilderState.InBody);
									tb.setFosterInserts(false);
								} else {
									tb.process(new org.jsoup.parser.Token.Character().data(character), org.jsoup.parser.HtmlTreeBuilderState.InBody);
								}
							} else
								tb.insert(new org.jsoup.parser.Token.Character().data(character));

						}
						tb.newPendingTableCharacters();
					}
					tb.transition(tb.originalState());
					return tb.process(t);
			}
			return true;
		}
	},
	InCaption() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isEndTag() && t.asEndTag().name().equals("caption")) {
				org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
				java.lang.String name = endTag.name();
				if (!tb.inTableScope(name)) {
					tb.error(this);
					return false;
				} else {
					tb.generateImpliedEndTags();
					if (!tb.currentElement().nodeName().equals("caption"))
						tb.error(this);

					tb.popStackToClose("caption");
					tb.clearFormattingElementsToLastMarker();
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTable);
				}
			} else if ((t.isStartTag() && org.jsoup.helper.StringUtil.in(t.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) || (t.isEndTag() && t.asEndTag().name().equals("table"))) {
				tb.error(this);
				boolean processed = tb.processEndTag("caption");
				if (processed)
					return tb.process(t);

			} else if (t.isEndTag() && org.jsoup.helper.StringUtil.in(t.asEndTag().name(), "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
				tb.error(this);
				return false;
			} else {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			}
			return true;
		}
	},
	InColumnGroup() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				tb.insert(t.asCharacter());
				return true;
			}
			switch (t.type) {
				case Comment :
					tb.insert(t.asComment());
					break;
				case Doctype :
					tb.error(this);
					break;
				case StartTag :
					org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
					java.lang.String name = startTag.name();
					if (name.equals("html"))
						return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
					else if (name.equals("col"))
						tb.insertEmpty(startTag);
					else
						return anythingElse(t, tb);

					break;
				case EndTag :
					org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
					name = endTag.name();
					if (name.equals("colgroup")) {
						if (tb.currentElement().nodeName().equals("html")) {
							tb.error(this);
							return false;
						} else {
							tb.pop();
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTable);
						}
					} else
						return anythingElse(t, tb);

					break;
				case EOF :
					if (tb.currentElement().nodeName().equals("html"))
						return true;
					else
						return anythingElse(t, tb);

				default :
					return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.TreeBuilder tb) {
			boolean processed = tb.processEndTag("colgroup");
			if (processed)
				return tb.process(t);

			return true;
		}
	},
	InTableBody() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			switch (t.type) {
				case StartTag :
					org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
					java.lang.String name = startTag.name();
					if (name.equals("tr")) {
						tb.clearStackToTableBodyContext();
						tb.insert(startTag);
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InRow);
					} else if (org.jsoup.helper.StringUtil.in(name, "th", "td")) {
						tb.error(this);
						tb.processStartTag("tr");
						return tb.process(startTag);
					} else if (org.jsoup.helper.StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead")) {
						return exitTableBody(t, tb);
					} else
						return anythingElse(t, tb);

					break;
				case EndTag :
					org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
					name = endTag.name();
					if (org.jsoup.helper.StringUtil.in(name, "tbody", "tfoot", "thead")) {
						if (!tb.inTableScope(name)) {
							tb.error(this);
							return false;
						} else {
							tb.clearStackToTableBodyContext();
							tb.pop();
							tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTable);
						}
					} else if (name.equals("table")) {
						return exitTableBody(t, tb);
					} else if (org.jsoup.helper.StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th", "tr")) {
						tb.error(this);
						return false;
					} else
						return anythingElse(t, tb);

					break;
				default :
					return anythingElse(t, tb);
			}
			return true;
		}

		private boolean exitTableBody(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (!((tb.inTableScope("tbody") || tb.inTableScope("thead")) || tb.inScope("tfoot"))) {
				tb.error(this);
				return false;
			}
			tb.clearStackToTableBodyContext();
			tb.processEndTag(tb.currentElement().nodeName());
			return tb.process(t);
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InTable);
		}
	},
	InRow() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isStartTag()) {
				org.jsoup.parser.Token.StartTag startTag = t.asStartTag();
				java.lang.String name = startTag.name();
				if (org.jsoup.helper.StringUtil.in(name, "th", "td")) {
					tb.clearStackToTableRowContext();
					tb.insert(startTag);
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InCell);
					tb.insertMarkerToFormattingElements();
				} else if (org.jsoup.helper.StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr")) {
					return handleMissingTr(t, tb);
				} else {
					return anythingElse(t, tb);
				}
			} else if (t.isEndTag()) {
				org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
				java.lang.String name = endTag.name();
				if (name.equals("tr")) {
					if (!tb.inTableScope(name)) {
						tb.error(this);
						return false;
					}
					tb.clearStackToTableRowContext();
					tb.pop();
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InTableBody);
				} else if (name.equals("table")) {
					return handleMissingTr(t, tb);
				} else if (org.jsoup.helper.StringUtil.in(name, "tbody", "tfoot", "thead")) {
					if (!tb.inTableScope(name)) {
						tb.error(this);
						return false;
					}
					tb.processEndTag("tr");
					return tb.process(t);
				} else if (org.jsoup.helper.StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th")) {
					tb.error(this);
					return false;
				} else {
					return anythingElse(t, tb);
				}
			} else {
				return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InTable);
		}

		private boolean handleMissingTr(org.jsoup.parser.Token t, org.jsoup.parser.TreeBuilder tb) {
			boolean processed = tb.processEndTag("tr");
			if (processed)
				return tb.process(t);
			else
				return false;

		}
	},
	InCell() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isEndTag()) {
				org.jsoup.parser.Token.EndTag endTag = t.asEndTag();
				java.lang.String name = endTag.name();
				if (org.jsoup.helper.StringUtil.in(name, "td", "th")) {
					if (!tb.inTableScope(name)) {
						tb.error(this);
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InRow);
						return false;
					}
					tb.generateImpliedEndTags();
					if (!tb.currentElement().nodeName().equals(name))
						tb.error(this);

					tb.popStackToClose(name);
					tb.clearFormattingElementsToLastMarker();
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InRow);
				} else if (org.jsoup.helper.StringUtil.in(name, "body", "caption", "col", "colgroup", "html")) {
					tb.error(this);
					return false;
				} else if (org.jsoup.helper.StringUtil.in(name, "table", "tbody", "tfoot", "thead", "tr")) {
					if (!tb.inTableScope(name)) {
						tb.error(this);
						return false;
					}
					closeCell(tb);
					return tb.process(t);
				} else {
					return anythingElse(t, tb);
				}
			} else if (t.isStartTag() && org.jsoup.helper.StringUtil.in(t.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
				if (!(tb.inTableScope("td") || tb.inTableScope("th"))) {
					tb.error(this);
					return false;
				}
				closeCell(tb);
				return tb.process(t);
			} else {
				return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
		}

		private void closeCell(org.jsoup.parser.HtmlTreeBuilder tb) {
			if (tb.inTableScope("td"))
				tb.processEndTag("td");
			else
				tb.processEndTag("th");

		}
	},
	InSelect() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			switch (t.type) {
				case Character :
					org.jsoup.parser.Token.Character c = t.asCharacter();
					if (c.getData().equals(org.jsoup.parser.HtmlTreeBuilderState.nullString)) {
						tb.error(this);
						return false;
					} else {
						tb.insert(c);
					}
					break;
				case Comment :
					tb.insert(t.asComment());
					break;
				case Doctype :
					tb.error(this);
					return false;
				case StartTag :
					org.jsoup.parser.Token.StartTag start = t.asStartTag();
					java.lang.String name = start.name();
					if (name.equals("html"))
						return tb.process(start, org.jsoup.parser.HtmlTreeBuilderState.InBody);
					else if (name.equals("option")) {
						tb.processEndTag("option");
						tb.insert(start);
					} else if (name.equals("optgroup")) {
						if (tb.currentElement().nodeName().equals("option"))
							tb.processEndTag("option");
						else if (tb.currentElement().nodeName().equals("optgroup"))
							tb.processEndTag("optgroup");

						tb.insert(start);
					} else if (name.equals("select")) {
						tb.error(this);
						return tb.processEndTag("select");
					} else if (org.jsoup.helper.StringUtil.in(name, "input", "keygen", "textarea")) {
						tb.error(this);
						if (!tb.inSelectScope("select"))
							return false;

						tb.processEndTag("select");
						return tb.process(start);
					} else if (name.equals("script")) {
						return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
					} else {
						return anythingElse(t, tb);
					}
					break;
				case EndTag :
					org.jsoup.parser.Token.EndTag end = t.asEndTag();
					name = end.name();
					if (name.equals("optgroup")) {
						if ((tb.currentElement().nodeName().equals("option") && (tb.aboveOnStack(tb.currentElement()) != null)) && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup"))
							tb.processEndTag("option");

						if (tb.currentElement().nodeName().equals("optgroup"))
							tb.pop();
						else
							tb.error(this);

					} else if (name.equals("option")) {
						if (tb.currentElement().nodeName().equals("option"))
							tb.pop();
						else
							tb.error(this);

					} else if (name.equals("select")) {
						if (!tb.inSelectScope(name)) {
							tb.error(this);
							return false;
						} else {
							tb.popStackToClose(name);
							tb.resetInsertionMode();
						}
					} else
						return anythingElse(t, tb);

					break;
				case EOF :
					if (!tb.currentElement().nodeName().equals("html"))
						tb.error(this);

					break;
				default :
					return anythingElse(t, tb);
			}
			return true;
		}

		private boolean anythingElse(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			tb.error(this);
			return false;
		}
	},
	InSelectInTable() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isStartTag() && org.jsoup.helper.StringUtil.in(t.asStartTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
				tb.error(this);
				tb.processEndTag("select");
				return tb.process(t);
			} else if (t.isEndTag() && org.jsoup.helper.StringUtil.in(t.asEndTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
				tb.error(this);
				if (tb.inTableScope(t.asEndTag().name())) {
					tb.processEndTag("select");
					return tb.process(t);
				} else
					return false;

			} else {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InSelect);
			}
		}
	},
	AfterBody() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isEndTag() && t.asEndTag().name().equals("html")) {
				if (tb.isFragmentParsing()) {
					tb.error(this);
					return false;
				} else {
					tb.transition(org.jsoup.parser.HtmlTreeBuilderState.AfterAfterBody);
				}
			} else if (t.isEOF()) {
			} else {
				tb.error(this);
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				return tb.process(t);
			}
			return true;
		}
	},
	InFrameset() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				tb.insert(t.asCharacter());
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isStartTag()) {
				org.jsoup.parser.Token.StartTag start = t.asStartTag();
				java.lang.String name = start.name();
				if (name.equals("html")) {
					return tb.process(start, org.jsoup.parser.HtmlTreeBuilderState.InBody);
				} else if (name.equals("frameset")) {
					tb.insert(start);
				} else if (name.equals("frame")) {
					tb.insertEmpty(start);
				} else if (name.equals("noframes")) {
					return tb.process(start, org.jsoup.parser.HtmlTreeBuilderState.InHead);
				} else {
					tb.error(this);
					return false;
				}
			} else if (t.isEndTag() && t.asEndTag().name().equals("frameset")) {
				if (tb.currentElement().nodeName().equals("html")) {
					tb.error(this);
					return false;
				} else {
					tb.pop();
					if ((!tb.isFragmentParsing()) && (!tb.currentElement().nodeName().equals("frameset"))) {
						tb.transition(org.jsoup.parser.HtmlTreeBuilderState.AfterFrameset);
					}
				}
			} else if (t.isEOF()) {
				if (!tb.currentElement().nodeName().equals("html")) {
					tb.error(this);
					return true;
				}
			} else {
				tb.error(this);
				return false;
			}
			return true;
		}
	},
	AfterFrameset() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) {
				tb.insert(t.asCharacter());
			} else if (t.isComment()) {
				tb.insert(t.asComment());
			} else if (t.isDoctype()) {
				tb.error(this);
				return false;
			} else if (t.isStartTag() && t.asStartTag().name().equals("html")) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isEndTag() && t.asEndTag().name().equals("html")) {
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.AfterAfterFrameset);
			} else if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
			} else if (t.isEOF()) {
			} else {
				tb.error(this);
				return false;
			}
			return true;
		}
	},
	AfterAfterBody() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isComment()) {
				tb.insert(t.asComment());
			} else if ((t.isDoctype() || org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isEOF()) {
			} else {
				tb.error(this);
				tb.transition(org.jsoup.parser.HtmlTreeBuilderState.InBody);
				return tb.process(t);
			}
			return true;
		}
	},
	AfterAfterFrameset() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			if (t.isComment()) {
				tb.insert(t.asComment());
			} else if ((t.isDoctype() || org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(t)) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InBody);
			} else if (t.isEOF()) {
			} else if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
				return tb.process(t, org.jsoup.parser.HtmlTreeBuilderState.InHead);
			} else {
				tb.error(this);
				return false;
			}
			return true;
		}
	},
	ForeignContent() {
		boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb) {
			return true;
		}
	};
	private static java.lang.String nullString = java.lang.String.valueOf('\u0000');

	abstract boolean process(org.jsoup.parser.Token t, org.jsoup.parser.HtmlTreeBuilder tb);

	private static boolean isWhitespace(org.jsoup.parser.Token t) {
		if (t.isCharacter()) {
			java.lang.String data = t.asCharacter().getData();
			return org.jsoup.parser.HtmlTreeBuilderState.isWhitespace(data);
		}
		return false;
	}

	private static boolean isWhitespace(java.lang.String data) {
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (!org.jsoup.helper.StringUtil.isWhitespace(c))
				return false;

		}
		return true;
	}

	private static void handleRcData(org.jsoup.parser.Token.StartTag startTag, org.jsoup.parser.HtmlTreeBuilder tb) {
		tb.insert(startTag);
		tb.tokeniser.transition(org.jsoup.parser.TokeniserState.Rcdata);
		tb.markInsertionMode();
		tb.transition(org.jsoup.parser.HtmlTreeBuilderState.Text);
	}

	private static void handleRawtext(org.jsoup.parser.Token.StartTag startTag, org.jsoup.parser.HtmlTreeBuilder tb) {
		tb.insert(startTag);
		tb.tokeniser.transition(org.jsoup.parser.TokeniserState.Rawtext);
		tb.markInsertionMode();
		tb.transition(org.jsoup.parser.HtmlTreeBuilderState.Text);
	}

	private static final class Constants {
		private static final java.lang.String[] InBodyStartToHead = new java.lang.String[]{ "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" };

		private static final java.lang.String[] InBodyStartPClosers = new java.lang.String[]{ "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul" };

		private static final java.lang.String[] Headings = new java.lang.String[]{ "h1", "h2", "h3", "h4", "h5", "h6" };

		private static final java.lang.String[] InBodyStartPreListing = new java.lang.String[]{ "pre", "listing" };

		private static final java.lang.String[] InBodyStartLiBreakers = new java.lang.String[]{ "address", "div", "p" };

		private static final java.lang.String[] DdDt = new java.lang.String[]{ "dd", "dt" };

		private static final java.lang.String[] Formatters = new java.lang.String[]{ "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u" };

		private static final java.lang.String[] InBodyStartApplets = new java.lang.String[]{ "applet", "marquee", "object" };

		private static final java.lang.String[] InBodyStartEmptyFormatters = new java.lang.String[]{ "area", "br", "embed", "img", "keygen", "wbr" };

		private static final java.lang.String[] InBodyStartMedia = new java.lang.String[]{ "param", "source", "track" };

		private static final java.lang.String[] InBodyStartInputAttribs = new java.lang.String[]{ "name", "action", "prompt" };

		private static final java.lang.String[] InBodyStartOptions = new java.lang.String[]{ "optgroup", "option" };

		private static final java.lang.String[] InBodyStartRuby = new java.lang.String[]{ "rp", "rt" };

		private static final java.lang.String[] InBodyStartDrop = new java.lang.String[]{ "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr" };

		private static final java.lang.String[] InBodyEndClosers = new java.lang.String[]{ "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul" };

		private static final java.lang.String[] InBodyEndAdoptionFormatters = new java.lang.String[]{ "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u" };

		private static final java.lang.String[] InBodyEndTableFosters = new java.lang.String[]{ "table", "tbody", "tfoot", "thead", "tr" };
	}
}