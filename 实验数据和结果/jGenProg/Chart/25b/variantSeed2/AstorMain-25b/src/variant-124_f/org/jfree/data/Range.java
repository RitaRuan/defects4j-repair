package org.jfree.data;
public strictfp class Range implements java.io.Serializable {
	private static final long serialVersionUID = -906333695431863380L;

	private double lower;

	private double upper;

	public Range(double lower, double upper) {
		if (lower > upper) {
			java.lang.String msg = ((("Range(double, double): require lower (" + lower) + ") <= upper (") + upper) + ").";
			throw new java.lang.IllegalArgumentException(msg);
		}
		this.lower = lower;
		this.upper = upper;
	}

	public double getLowerBound() {
		return this.lower;
	}

	public double getUpperBound() {
		return this.upper;
	}

	public double getLength() {
		return this.upper - this.lower;
	}

	public double getCentralValue() {
		return (this.lower / 2.0) + (this.upper / 2.0);
	}

	public boolean contains(double value) {
		return (value >= this.lower) && (value <= this.upper);
	}

	public boolean intersects(double b0, double b1) {
		if (b0 <= this.lower) {
			return b1 > this.lower;
		} else {
			return (b0 < this.upper) && (b1 >= b0);
		}
	}

	public double constrain(double value) {
		double result = value;
		if (!contains(value)) {
			if (value > this.upper) {
				result = this.upper;
			} else if (value < this.lower) {
				result = this.lower;
			}
		}
		return result;
	}

	public static org.jfree.data.Range combine(org.jfree.data.Range range1, org.jfree.data.Range range2) {
		if (range1 == null) {
			return range2;
		} else if (range2 == null) {
			return range1;
		} else {
			double l = java.lang.Math.min(range1.getLowerBound(), range2.getLowerBound());
			double u = java.lang.Math.max(range1.getUpperBound(), range2.getUpperBound());
			return new org.jfree.data.Range(l, u);
		}
	}

	public static org.jfree.data.Range expandToInclude(org.jfree.data.Range range, double value) {
		if (range == null) {
			return new org.jfree.data.Range(value, value);
		}
		if (value < range.getLowerBound()) {
			return new org.jfree.data.Range(value, range.getUpperBound());
		} else if (value > range.getUpperBound()) {
			return new org.jfree.data.Range(range.getLowerBound(), value);
		} else {
			return range;
		}
	}

	public static org.jfree.data.Range expand(org.jfree.data.Range range, double lowerMargin, double upperMargin) {
		if (range == null) {
			throw new java.lang.IllegalArgumentException("Null 'range' argument.");
		}
		double length = range.getLength();
		double lower = length * lowerMargin;
		double upper = length * upperMargin;
		return new org.jfree.data.Range(range.getLowerBound() - lower, range.getUpperBound() + upper);
	}

	public static org.jfree.data.Range shift(org.jfree.data.Range base, double delta) {
		return org.jfree.data.Range.shift(base, delta, false);
	}

	public static org.jfree.data.Range shift(org.jfree.data.Range base, double delta, boolean allowZeroCrossing) {
		if (allowZeroCrossing) {
			return new org.jfree.data.Range(base.getLowerBound() + delta, base.getUpperBound() + delta);
		} else {
			return new org.jfree.data.Range(org.jfree.data.Range.shiftWithNoZeroCrossing(base.getLowerBound(), delta), org.jfree.data.Range.shiftWithNoZeroCrossing(base.getUpperBound(), delta));
		}
	}

	private static double shiftWithNoZeroCrossing(double value, double delta) {
		if (value > 0.0) {
			return java.lang.Math.max(value + delta, 0.0);
		} else if (value < 0.0) {
			return java.lang.Math.min(value + delta, 0.0);
		} else {
			return value + delta;
		}
	}

	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof org.jfree.data.Range)) {
			return false;
		}
		org.jfree.data.Range range = ((org.jfree.data.Range) (obj));
		if (!(this.lower == range.lower)) {
			return false;
		}
		if (!(this.upper == range.upper)) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result;
		long temp;
		temp = java.lang.Double.doubleToLongBits(this.lower);
		result = ((int) (temp ^ (temp >>> 32)));
		temp = java.lang.Double.doubleToLongBits(this.upper);
		result = (29 * result) + ((int) (temp ^ (temp >>> 32)));
		return result;
	}

	public java.lang.String toString() {
		return ((("Range[" + this.lower) + ",") + this.upper) + "]";
	}
}