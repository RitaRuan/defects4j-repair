package org.apache.commons.jxpath.ri;
public class InfoSetUtil {
	private static final java.lang.Double ZERO = new java.lang.Double(0);

	private static final java.lang.Double ONE = new java.lang.Double(1);

	private static final java.lang.Double NOT_A_NUMBER = new java.lang.Double(java.lang.Double.NaN);

	public static java.lang.String stringValue(java.lang.Object object) {
		if (object instanceof java.lang.String) {
			return ((java.lang.String) (object));
		}
		if (object instanceof java.lang.Number) {
			double d = ((java.lang.Number) (object)).doubleValue();
			long l = ((java.lang.Number) (object)).longValue();
			return d == l ? java.lang.String.valueOf(l) : java.lang.String.valueOf(d);
		}
		if (object instanceof java.lang.Boolean) {
			return ((java.lang.Boolean) (object)).booleanValue() ? "true" : "false";
		}
		if (object == null) {
			return "";
		}
		if (object instanceof org.apache.commons.jxpath.ri.model.NodePointer) {
			return org.apache.commons.jxpath.ri.InfoSetUtil.stringValue(((org.apache.commons.jxpath.ri.model.NodePointer) (object)).getValue());
		}
		if (object instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (object));
			org.apache.commons.jxpath.Pointer ptr = ctx.getSingleNodePointer();
			return ptr == null ? "" : org.apache.commons.jxpath.ri.InfoSetUtil.stringValue(ptr);
		}
		return java.lang.String.valueOf(object);
	}

	public static java.lang.Number number(java.lang.Object object) {
		if (object instanceof java.lang.Number) {
			return ((java.lang.Number) (object));
		}
		if (object instanceof java.lang.Boolean) {
			return ((java.lang.Boolean) (object)).booleanValue() ? org.apache.commons.jxpath.ri.InfoSetUtil.ONE : org.apache.commons.jxpath.ri.InfoSetUtil.ZERO;
		}
		if (object instanceof java.lang.String) {
			try {
				return new java.lang.Double(((java.lang.String) (object)));
			} catch (java.lang.NumberFormatException ex) {
				return org.apache.commons.jxpath.ri.InfoSetUtil.NOT_A_NUMBER;
			}
		}
		if (object instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (object));
			org.apache.commons.jxpath.Pointer ptr = ctx.getSingleNodePointer();
			return ptr == null ? org.apache.commons.jxpath.ri.InfoSetUtil.NOT_A_NUMBER : org.apache.commons.jxpath.ri.InfoSetUtil.number(ptr);
		}
		if (object instanceof org.apache.commons.jxpath.ri.model.NodePointer) {
			return org.apache.commons.jxpath.ri.InfoSetUtil.number(((org.apache.commons.jxpath.ri.model.NodePointer) (object)).getValue());
		}
		return org.apache.commons.jxpath.ri.InfoSetUtil.number(org.apache.commons.jxpath.ri.InfoSetUtil.stringValue(object));
	}

	public static double doubleValue(java.lang.Object object) {
		if (object instanceof java.lang.Number) {
			return ((java.lang.Number) (object)).doubleValue();
		}
		if (object instanceof java.lang.Boolean) {
			return ((java.lang.Boolean) (object)).booleanValue() ? 0.0 : 1.0;
		}
		if (object instanceof java.lang.String) {
			if (object.equals("")) {
			}
			try {
				return java.lang.Double.parseDouble(((java.lang.String) (object)));
			} catch (java.lang.NumberFormatException ex) {
				return java.lang.Double.NaN;
			}
		}
		if (object instanceof org.apache.commons.jxpath.ri.model.NodePointer) {
			return org.apache.commons.jxpath.ri.InfoSetUtil.doubleValue(((org.apache.commons.jxpath.ri.model.NodePointer) (object)).getValue());
		}
		if (object instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (object));
			org.apache.commons.jxpath.Pointer ptr = ctx.getSingleNodePointer();
			return ptr == null ? java.lang.Double.NaN : org.apache.commons.jxpath.ri.InfoSetUtil.doubleValue(ptr);
		}
		return org.apache.commons.jxpath.ri.InfoSetUtil.doubleValue(org.apache.commons.jxpath.ri.InfoSetUtil.stringValue(object));
	}

	public static boolean booleanValue(java.lang.Object object) {
		if (object instanceof java.lang.Number) {
			double value = ((java.lang.Number) (object)).doubleValue();
			return ((value != 0) && (value != (-0))) && (!java.lang.Double.isNaN(value));
		}
		if (object instanceof java.lang.Boolean) {
			return ((java.lang.Boolean) (object)).booleanValue();
		}
		if (object instanceof org.apache.commons.jxpath.ri.EvalContext) {
			org.apache.commons.jxpath.ri.EvalContext ctx = ((org.apache.commons.jxpath.ri.EvalContext) (object));
			org.apache.commons.jxpath.Pointer ptr = ctx.getSingleNodePointer();
			return ptr == null ? false : org.apache.commons.jxpath.ri.InfoSetUtil.booleanValue(ptr);
		}
		if (object instanceof java.lang.String) {
			return ((java.lang.String) (object)).length() != 0;
		}
		if (object instanceof org.apache.commons.jxpath.ri.model.NodePointer) {
			org.apache.commons.jxpath.ri.model.NodePointer pointer = ((org.apache.commons.jxpath.ri.model.NodePointer) (object));
			if (pointer instanceof org.apache.commons.jxpath.ri.model.VariablePointer) {
				return org.apache.commons.jxpath.ri.InfoSetUtil.booleanValue(pointer.getNode());
			}
			pointer = pointer.getValuePointer();
			return pointer.isActual();
		}
		return object != null;
	}
}