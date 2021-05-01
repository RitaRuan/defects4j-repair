package org.apache.commons.jxpath.ri;
public class JXPathContextReferenceImpl extends 





























































org.apache.commons.jxpath.JXPathContext {





	public static final boolean USE_SOFT_CACHE = true;

	private static final org.apache.commons.jxpath.ri.Compiler COMPILER = new org.apache.commons.jxpath.ri.compiler.TreeCompiler();
	private static java.util.Map compiled = new java.util.HashMap();
	private static int cleanupCount = 0;

	private static java.util.Vector nodeFactories = new java.util.Vector();
	private static org.apache.commons.jxpath.ri.model.NodePointerFactory[] nodeFactoryArray = null;
	static {
		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(new org.apache.commons.jxpath.ri.model.beans.CollectionPointerFactory());
		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(new org.apache.commons.jxpath.ri.model.beans.BeanPointerFactory());
		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(new org.apache.commons.jxpath.ri.model.dynamic.DynamicPointerFactory());


		java.lang.Object domFactory = org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.allocateConditionally(
		"org.apache.commons.jxpath.ri.model.dom.DOMPointerFactory", 
		"org.w3c.dom.Node");
		if (domFactory != null) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(domFactory);
		}


		java.lang.Object jdomFactory = org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.allocateConditionally(
		"org.apache.commons.jxpath.ri.model.jdom.JDOMPointerFactory", 
		"org.jdom.Document");
		if (jdomFactory != null) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(jdomFactory);
		}


		java.lang.Object dynaBeanFactory = 
		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.allocateConditionally(
		"org.apache.commons.jxpath.ri.model.dynabeans." + 
		"DynaBeanPointerFactory", 
		"org.apache.commons.beanutils.DynaBean");
		if (dynaBeanFactory != null) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(dynaBeanFactory);
		}

		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(new org.apache.commons.jxpath.ri.model.container.ContainerPointerFactory());
		org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.createNodeFactoryArray();
	}

	private org.apache.commons.jxpath.Pointer rootPointer;
	private org.apache.commons.jxpath.Pointer contextPointer;

	protected org.apache.commons.jxpath.ri.NamespaceResolver namespaceResolver;


	private static final int CLEANUP_THRESHOLD = 500;

	protected JXPathContextReferenceImpl(org.apache.commons.jxpath.JXPathContext parentContext, 
	java.lang.Object contextBean) 
	{
		this(parentContext, contextBean, null);
	}

	public JXPathContextReferenceImpl(
	org.apache.commons.jxpath.JXPathContext parentContext, 
	java.lang.Object contextBean, 
	org.apache.commons.jxpath.Pointer contextPointer) 
	{
		super(parentContext, contextBean);

		synchronized(org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.createNodeFactoryArray();
		}

		if (contextPointer != null) {
			this.contextPointer = contextPointer;
			this.rootPointer = 
			org.apache.commons.jxpath.ri.model.NodePointer.newNodePointer(
			new org.apache.commons.jxpath.ri.QName(null, "root"), 
			contextPointer.getRootNode(), 
			getLocale());
		} else 
		{
			this.contextPointer = 
			org.apache.commons.jxpath.ri.model.NodePointer.newNodePointer(
			new org.apache.commons.jxpath.ri.QName(null, "root"), 
			contextBean, 
			getLocale());
			this.rootPointer = this.contextPointer;
		}

		org.apache.commons.jxpath.ri.NamespaceResolver parentNR = null;
		if (parentContext instanceof org.apache.commons.jxpath.ri.JXPathContextReferenceImpl) {
			parentNR = ((org.apache.commons.jxpath.ri.JXPathContextReferenceImpl) (parentContext)).getNamespaceResolver();
		}
		namespaceResolver = new org.apache.commons.jxpath.ri.NamespaceResolver(parentNR);
		namespaceResolver.setNamespaceContextPointer(
		((org.apache.commons.jxpath.ri.model.NodePointer) (this.contextPointer)));
	}

	private static void createNodeFactoryArray() {
		if (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactoryArray == null) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactoryArray = 
			((org.apache.commons.jxpath.ri.model.NodePointerFactory[]) (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.toArray(
			new org.apache.commons.jxpath.ri.model.NodePointerFactory[org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.size()])));
			java.util.Arrays.sort(org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactoryArray, new java.util.Comparator() {
				public int compare(java.lang.Object a, java.lang.Object b) {
					int orderA = ((org.apache.commons.jxpath.ri.model.NodePointerFactory) (a)).getOrder();
					int orderB = ((org.apache.commons.jxpath.ri.model.NodePointerFactory) (b)).getOrder();
					return orderA - orderB;
				}
			});
		}
	}






	public static void addNodePointerFactory(org.apache.commons.jxpath.ri.model.NodePointerFactory factory) {
		synchronized(org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories) {
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactories.add(factory);
			org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactoryArray = null;
		}
	}

	public static org.apache.commons.jxpath.ri.model.NodePointerFactory[] getNodePointerFactories() {
		return org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.nodeFactoryArray;
	}






	protected org.apache.commons.jxpath.ri.Compiler getCompiler() {
		return org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.COMPILER;
	}

	protected org.apache.commons.jxpath.CompiledExpression compilePath(java.lang.String xpath) {
		return new org.apache.commons.jxpath.ri.JXPathCompiledExpression(xpath, compileExpression(xpath));
	}

	private org.apache.commons.jxpath.ri.compiler.Expression compileExpression(java.lang.String xpath) {
		org.apache.commons.jxpath.ri.compiler.Expression expr;

		synchronized(org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled) {
			if (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.USE_SOFT_CACHE) {
				expr = null;
				java.lang.ref.SoftReference ref = ((java.lang.ref.SoftReference) (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled.get(xpath)));
				if (ref != null) {
					expr = ((org.apache.commons.jxpath.ri.compiler.Expression) (ref.get()));
				}
			} else 
			{
				expr = ((org.apache.commons.jxpath.ri.compiler.Expression) (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled.get(xpath)));
			}
		}

		if (expr != null) {
			return expr;
		}

		expr = ((org.apache.commons.jxpath.ri.compiler.Expression) (org.apache.commons.jxpath.ri.Parser.parseExpression(xpath, getCompiler())));

		synchronized(org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled) {
			if (org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.USE_SOFT_CACHE) {
				if ((org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.cleanupCount++) >= org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.CLEANUP_THRESHOLD) {
					java.util.Iterator it = org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled.entrySet().iterator();
					while (it.hasNext()) {
						java.util.Map.Entry me = ((java.util.Map.Entry) (it.next()));
						if (((java.lang.ref.SoftReference) (me.getValue())).get() == null) {
							it.remove();
						}
					} 
					org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.cleanupCount = 0;
				}
				org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled.put(xpath, new java.lang.ref.SoftReference(expr));
			} else 
			{
				org.apache.commons.jxpath.ri.JXPathContextReferenceImpl.compiled.put(xpath, expr);
			}
		}

		return expr;
	}





	public java.lang.Object getValue(java.lang.String xpath) {
		org.apache.commons.jxpath.ri.compiler.Expression expression = compileExpression(xpath);
































		return getValue(xpath, expression);
	}































	public java.lang.Object getValue(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		java.lang.Object result = expr.computeValue(getEvalContext());
		if (result == null) {
			if (expr instanceof org.apache.commons.jxpath.ri.compiler.Path) {
				if (!isLenient()) {
					throw new org.apache.commons.jxpath.JXPathNotFoundException("No value for xpath: " + 
					xpath);
				}
			}
			return null;
		}
		if (result instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (result));
			result = ctx.getSingleNodePointer();
			if ((!isLenient()) && (result == null)) {
				throw new org.apache.commons.jxpath.JXPathNotFoundException("No value for xpath: " + 
				xpath);
			}
		}
		if (result instanceof org.apache.commons.jxpath.ri.model.NodePointer) {
			result = ((org.apache.commons.jxpath.ri.model.NodePointer) (result)).getValuePointer();
			if ((!isLenient()) && (!((org.apache.commons.jxpath.ri.model.NodePointer) (result)).isActual())) {






				org.apache.commons.jxpath.ri.model.NodePointer parent = 
				((org.apache.commons.jxpath.ri.model.NodePointer) (result)).getImmediateParentPointer();
				if (((parent == null) || 
				(!parent.isContainer())) || 
				(!parent.isActual())) {
					throw new org.apache.commons.jxpath.JXPathNotFoundException("No value for xpath: " + 
					xpath);
				}
			}
			result = ((org.apache.commons.jxpath.ri.model.NodePointer) (result)).getValue();
		}
		return result;
	}





	public java.lang.Object getValue(java.lang.String xpath, java.lang.Class requiredType) {
		org.apache.commons.jxpath.ri.compiler.Expression expr = compileExpression(xpath);
		return getValue(xpath, expr, requiredType);
	}

	public java.lang.Object getValue(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr, java.lang.Class requiredType) {
		java.lang.Object value = getValue(xpath, expr);
		if ((value != null) && (requiredType != null)) {
			if (!org.apache.commons.jxpath.util.TypeUtils.canConvert(value, requiredType)) {
				throw new org.apache.commons.jxpath.JXPathTypeConversionException(
				(((("Invalid expression type. '" + 
				xpath) + 
				"' returns ") + 
				value.getClass().getName()) + 
				". It cannot be converted to ") + 
				requiredType.getName());
			}
			value = org.apache.commons.jxpath.util.TypeUtils.convert(value, requiredType);
		}
		return value;
	}






	public java.util.Iterator iterate(java.lang.String xpath) {
		return iterate(xpath, compileExpression(xpath));
	}

	public java.util.Iterator iterate(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		return expr.iterate(getEvalContext());
	}

	public org.apache.commons.jxpath.Pointer getPointer(java.lang.String xpath) {
		return getPointer(xpath, compileExpression(xpath));
	}

	public org.apache.commons.jxpath.Pointer getPointer(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		java.lang.Object result = expr.computeValue(getEvalContext());
		if (result instanceof org.apache.commons.jxpath.ri.EvalContext) {
			result = ((org.apache.commons.jxpath.ri.EvalContext) (result)).getSingleNodePointer();
		}
		if (result instanceof org.apache.commons.jxpath.Pointer) {
			if ((!isLenient()) && (!((org.apache.commons.jxpath.ri.model.NodePointer) (result)).isActual())) {
				throw new org.apache.commons.jxpath.JXPathNotFoundException("No pointer for xpath: " + 
				xpath);
			}
			return ((org.apache.commons.jxpath.Pointer) (result));
		}
		return org.apache.commons.jxpath.ri.model.NodePointer.newNodePointer(null, result, getLocale());
	}

	public void setValue(java.lang.String xpath, java.lang.Object value) {
		setValue(xpath, compileExpression(xpath), value);
	}

	public void setValue(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr, java.lang.Object value) {
		try {
			setValue(xpath, expr, value, false);
		}
		 catch (java.lang.Throwable ex) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Exception trying to set value with xpath " + xpath, ex);
		}
	}

	public org.apache.commons.jxpath.Pointer createPath(java.lang.String xpath) {
		return createPath(xpath, compileExpression(xpath));
	}

	public org.apache.commons.jxpath.Pointer createPath(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		try {
			java.lang.Object result = expr.computeValue(getEvalContext());
			org.apache.commons.jxpath.Pointer pointer = null;

			if (result instanceof org.apache.commons.jxpath.Pointer) {
				pointer = ((org.apache.commons.jxpath.Pointer) (result));
			} else 
			if (result instanceof org.apache.commons.jxpath.ri.EvalContext) {
				org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (result));
				pointer = ctx.getSingleNodePointer();
			} else 
			{
				checkSimplePath(expr);

				throw new org.apache.commons.jxpath.JXPathException("Cannot create path:" + xpath);
			}
			return ((org.apache.commons.jxpath.ri.model.NodePointer) (pointer)).createPath(this);
		}
		 catch (java.lang.Throwable ex) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Exception trying to create xpath " + xpath, 
			ex);
		}
	}

	public org.apache.commons.jxpath.Pointer createPathAndSetValue(java.lang.String xpath, java.lang.Object value) {
		return createPathAndSetValue(xpath, compileExpression(xpath), value);
	}

	public org.apache.commons.jxpath.Pointer createPathAndSetValue(
	java.lang.String xpath, 
	org.apache.commons.jxpath.ri.compiler.Expression expr, 
	java.lang.Object value) 
	{
		try {
			return setValue(xpath, expr, value, true);
		}
		 catch (java.lang.Throwable ex) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Exception trying to create xpath " + xpath, 
			ex);
		}
	}

	private org.apache.commons.jxpath.Pointer setValue(
	java.lang.String xpath, 
	org.apache.commons.jxpath.ri.compiler.Expression expr, 
	java.lang.Object value, 
	boolean create) 
	{
		java.lang.Object result = expr.computeValue(getEvalContext());
		org.apache.commons.jxpath.Pointer pointer = null;

		if (result instanceof org.apache.commons.jxpath.Pointer) {
			pointer = ((org.apache.commons.jxpath.Pointer) (result));
		} else 
		if (result instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (result));
			pointer = ctx.getSingleNodePointer();
		} else 
		{
			if (create) {
				checkSimplePath(expr);
			}


			throw new org.apache.commons.jxpath.JXPathException("Cannot set value for xpath: " + xpath);
		}
		if (create) {
			pointer = ((org.apache.commons.jxpath.ri.model.NodePointer) (pointer)).createPath(this, value);
		} else 
		{
			pointer.setValue(value);
		}
		return pointer;
	}





	private void checkSimplePath(org.apache.commons.jxpath.ri.compiler.Expression expr) {
		if ((!(expr instanceof org.apache.commons.jxpath.ri.compiler.LocationPath)) || 
		(!((org.apache.commons.jxpath.ri.compiler.LocationPath) (expr)).isSimplePath())) {
			throw new org.apache.commons.jxpath.JXPathInvalidSyntaxException(
			"JXPath can only create a path if it uses exclusively " + 
			("the child:: and attribute:: axes and has " + 
			"no context-dependent predicates"));
		}
	}







	public java.util.Iterator iteratePointers(java.lang.String xpath) {
		return iteratePointers(xpath, compileExpression(xpath));
	}

	public java.util.Iterator iteratePointers(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		return expr.iteratePointers(getEvalContext());
	}

	public void removePath(java.lang.String xpath) {
		removePath(xpath, compileExpression(xpath));
	}

	public void removePath(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		try {
			org.apache.commons.jxpath.ri.model.NodePointer pointer = ((org.apache.commons.jxpath.ri.model.NodePointer) (getPointer(xpath, expr)));
			if (pointer != null) {
				((org.apache.commons.jxpath.ri.model.NodePointer) (pointer)).remove();
			}
		}
		 catch (java.lang.Throwable ex) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Exception trying to remove xpath " + xpath, 
			ex);
		}
	}

	public void removeAll(java.lang.String xpath) {
		removeAll(xpath, compileExpression(xpath));
	}

	public void removeAll(java.lang.String xpath, org.apache.commons.jxpath.ri.compiler.Expression expr) {
		try {
			java.util.ArrayList list = new java.util.ArrayList();
			java.util.Iterator it = expr.iteratePointers(getEvalContext());
			while (it.hasNext()) {
				list.add(it.next());
			} 
			java.util.Collections.sort(list, org.apache.commons.jxpath.util.ReverseComparator.INSTANCE);
			it = list.iterator();
			if (it.hasNext()) {
				org.apache.commons.jxpath.ri.model.NodePointer pointer = ((org.apache.commons.jxpath.ri.model.NodePointer) (it.next()));
				pointer.remove();
				while (it.hasNext()) {
					removePath(((org.apache.commons.jxpath.ri.model.NodePointer) (it.next())).asPath());
				} 
			}
		}
		 catch (java.lang.Throwable ex) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Exception trying to remove all for xpath " + xpath, 
			ex);
		}
	}

	public org.apache.commons.jxpath.JXPathContext getRelativeContext(org.apache.commons.jxpath.Pointer pointer) {
		java.lang.Object contextBean = pointer.getNode();
		if (contextBean == null) {
			throw new org.apache.commons.jxpath.JXPathException(
			"Cannot create a relative context for a non-existent node: " + 
			pointer);
		}
		return new org.apache.commons.jxpath.ri.JXPathContextReferenceImpl(this, contextBean, pointer);
	}

	public org.apache.commons.jxpath.Pointer getContextPointer() {
		return contextPointer;
	}

	private org.apache.commons.jxpath.ri.model.NodePointer getAbsoluteRootPointer() {
		return ((org.apache.commons.jxpath.ri.model.NodePointer) (rootPointer));
	}

	private org.apache.commons.jxpath.ri.EvalContext getEvalContext() {
		return new org.apache.commons.jxpath.ri.axes.InitialContext(new org.apache.commons.jxpath.ri.axes.RootContext(this, 
		((org.apache.commons.jxpath.ri.model.NodePointer) (getContextPointer()))));
	}

	public org.apache.commons.jxpath.ri.EvalContext getAbsoluteRootContext() {
		return new org.apache.commons.jxpath.ri.axes.InitialContext(new org.apache.commons.jxpath.ri.axes.RootContext(this, 
		getAbsoluteRootPointer()));
	}

	public org.apache.commons.jxpath.ri.model.NodePointer getVariablePointer(org.apache.commons.jxpath.ri.QName name) {
		java.lang.String varName = name.toString();
		org.apache.commons.jxpath.JXPathContext varCtx = this;
		org.apache.commons.jxpath.Variables vars = null;
		while (varCtx != null) {
			vars = varCtx.getVariables();
			if (vars.isDeclaredVariable(varName)) {
				break;
			}
			varCtx = varCtx.getParentContext();
			vars = null;
		} 
		if (vars != null) {
			return new org.apache.commons.jxpath.ri.model.VariablePointer(vars, name);
		} else 
		{



			return new org.apache.commons.jxpath.ri.model.VariablePointer(name);
		}
	}

	public org.apache.commons.jxpath.Function getFunction(org.apache.commons.jxpath.ri.QName functionName, java.lang.Object[] parameters) {
		java.lang.String namespace = functionName.getPrefix();
		java.lang.String name = functionName.getName();
		org.apache.commons.jxpath.JXPathContext funcCtx = this;
		org.apache.commons.jxpath.Function func = null;
		org.apache.commons.jxpath.Functions funcs;
		while (funcCtx != null) {
			funcs = funcCtx.getFunctions();
			if (funcs != null) {
				func = funcs.getFunction(namespace, name, parameters);
				if (func != null) {
					return func;
				}
			}
			funcCtx = funcCtx.getParentContext();
		} 
		throw new org.apache.commons.jxpath.JXPathFunctionNotFoundException(
		"Undefined function: " + functionName.toString());
	}

	public void registerNamespace(java.lang.String prefix, java.lang.String namespaceURI) {
		if (namespaceResolver.isSealed()) {
			namespaceResolver = ((org.apache.commons.jxpath.ri.NamespaceResolver) (namespaceResolver.clone()));
		}
		namespaceResolver.registerNamespace(prefix, namespaceURI);
	}

	public java.lang.String getNamespaceURI(java.lang.String prefix) {
		return namespaceResolver.getNamespaceURI(prefix);
	}





	public java.lang.String getPrefix(java.lang.String namespaceURI) {
		return namespaceResolver.getPrefix(namespaceURI);
	}

	public void setNamespaceContextPointer(org.apache.commons.jxpath.Pointer pointer) {
		if (namespaceResolver.isSealed()) {
			namespaceResolver = ((org.apache.commons.jxpath.ri.NamespaceResolver) (namespaceResolver.clone()));
		}
		namespaceResolver.setNamespaceContextPointer(((org.apache.commons.jxpath.ri.model.NodePointer) (pointer)));
	}

	public org.apache.commons.jxpath.Pointer getNamespaceContextPointer() {
		return namespaceResolver.getNamespaceContextPointer();
	}

	public org.apache.commons.jxpath.ri.NamespaceResolver getNamespaceResolver() {
		namespaceResolver.seal();
		return namespaceResolver;
	}





	public static java.lang.Object allocateConditionally(
	java.lang.String className, 
	java.lang.String existenceCheckClassName) 
	{
		try {
			try {
				java.lang.Class.forName(existenceCheckClassName);
			}
			 catch (java.lang.ClassNotFoundException ex) {
				return null;
			}
			java.lang.Class cls = java.lang.Class.forName(className);
			return cls.newInstance();
		}
		 catch (java.lang.Exception ex) {
			throw new org.apache.commons.jxpath.JXPathException("Cannot allocate " + className, ex);
		}
	}}