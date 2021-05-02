package org.jfree.data.time;
public class TimePeriodValues extends org.jfree.data.general.Series implements java.io.Serializable {
	static final long serialVersionUID = -2210593619794989709L;

	protected static final java.lang.String DEFAULT_DOMAIN_DESCRIPTION = "Time";

	protected static final java.lang.String DEFAULT_RANGE_DESCRIPTION = "Value";

	private java.lang.String domain;

	private java.lang.String range;

	private java.util.List data;

	private int minStartIndex = -1;

	private int maxStartIndex = -1;

	private int minMiddleIndex = -1;

	private int maxMiddleIndex = -1;

	private int minEndIndex = -1;

	private int maxEndIndex = -1;

	public TimePeriodValues(java.lang.Comparable name) {
		this(name, org.jfree.data.time.TimePeriodValues.DEFAULT_DOMAIN_DESCRIPTION, org.jfree.data.time.TimePeriodValues.DEFAULT_RANGE_DESCRIPTION);
	}

	public TimePeriodValues(java.lang.Comparable name, java.lang.String domain, java.lang.String range) {
		super(name);
		this.domain = domain;
		this.range = range;
		this.data = new java.util.ArrayList();
	}

	public java.lang.String getDomainDescription() {
		return this.domain;
	}

	public void setDomainDescription(java.lang.String description) {
		java.lang.String old = this.domain;
		this.domain = description;
		firePropertyChange("Domain", old, description);
	}

	public java.lang.String getRangeDescription() {
		return this.range;
	}

	public void setRangeDescription(java.lang.String description) {
		java.lang.String old = this.range;
		this.range = description;
		firePropertyChange("Range", old, description);
	}

	public int getItemCount() {
		return this.data.size();
	}

	public org.jfree.data.time.TimePeriodValue getDataItem(int index) {
		return ((org.jfree.data.time.TimePeriodValue) (this.data.get(index)));
	}

	public org.jfree.data.time.TimePeriod getTimePeriod(int index) {
		return getDataItem(index).getPeriod();
	}

	public java.lang.Number getValue(int index) {
		return getDataItem(index).getValue();
	}

	public void add(org.jfree.data.time.TimePeriodValue item) {
		if (item == null) {
			throw new java.lang.IllegalArgumentException("Null item not allowed.");
		}
		this.data.add(item);
		updateBounds(item.getPeriod(), this.data.size() - 1);
		fireSeriesChanged();
	}

	private void updateBounds(org.jfree.data.time.TimePeriod period, int index) {
		long start = period.getStart().getTime();
		long end = period.getEnd().getTime();
		long middle = start + ((end - start) / 2);
		if (this.minStartIndex >= 0) {
			long minStart = getDataItem(this.minStartIndex).getPeriod().getStart().getTime();
			if (start < minStart) {
				this.minStartIndex = index;
			}
		} else {
			this.minStartIndex = index;
		}
		if (this.maxStartIndex >= 0) {
			long maxStart = getDataItem(this.maxStartIndex).getPeriod().getStart().getTime();
			if (start > maxStart) {
				this.maxStartIndex = index;
			}
		} else {
			this.maxStartIndex = index;
		}
		if (this.minMiddleIndex >= 0) {
			long s = getDataItem(this.minMiddleIndex).getPeriod().getStart().getTime();
			long e = getDataItem(this.minMiddleIndex).getPeriod().getEnd().getTime();
			long minMiddle = s + ((e - s) / 2);
			if (middle < minMiddle) {
				this.minMiddleIndex = index;
			}
		} else {
			this.minMiddleIndex = index;
		}
		if (this.maxMiddleIndex >= 0) {
			long s = getDataItem(this.minMiddleIndex).getPeriod().getStart().getTime();
			long e = getDataItem(this.minMiddleIndex).getPeriod().getEnd().getTime();
			long maxMiddle = s + ((e - s) / 2);
			if (middle > maxMiddle) {
				this.maxMiddleIndex = index;
			}
		} else {
			this.maxMiddleIndex = index;
		}
		if (this.minEndIndex >= 0) {
			long minEnd = getDataItem(this.minEndIndex).getPeriod().getEnd().getTime();
			if (end < minEnd) {
				this.minEndIndex = index;
			}
		} else {
			this.minEndIndex = index;
		}
		if (this.maxEndIndex >= 0) {
			long maxEnd = getDataItem(this.maxEndIndex).getPeriod().getEnd().getTime();
			if (end > maxEnd) {
				this.maxEndIndex = index;
			}
		} else {
			this.maxEndIndex = index;
		}
	}

	private void recalculateBounds() {
		this.minStartIndex = -1;
		this.minMiddleIndex = -1;
		this.minEndIndex = -1;
		this.maxStartIndex = -1;
		this.maxMiddleIndex = -1;
		this.maxEndIndex = -1;
		for (int i = 0; i < this.data.size(); i++) {
			org.jfree.data.time.TimePeriodValue tpv = ((org.jfree.data.time.TimePeriodValue) (this.data.get(i)));
			updateBounds(tpv.getPeriod(), i);
		}
	}

	public void add(org.jfree.data.time.TimePeriod period, double value) {
		org.jfree.data.time.TimePeriodValue item = new org.jfree.data.time.TimePeriodValue(period, value);
		add(item);
	}

	public void add(org.jfree.data.time.TimePeriod period, java.lang.Number value) {
		org.jfree.data.time.TimePeriodValue item = new org.jfree.data.time.TimePeriodValue(period, value);
		add(item);
	}

	public void update(int index, java.lang.Number value) {
		org.jfree.data.time.TimePeriodValue item = getDataItem(index);
		item.setValue(value);
		fireSeriesChanged();
	}

	public void delete(int start, int end) {
		for (int i = 0; i <= (end - start); i++) {
			this.data.remove(start);
		}
		recalculateBounds();
		fireSeriesChanged();
	}

	public boolean equals(java.lang.Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof org.jfree.data.time.TimePeriodValues)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		org.jfree.data.time.TimePeriodValues that = ((org.jfree.data.time.TimePeriodValues) (obj));
		if (!org.jfree.chart.util.ObjectUtilities.equal(this.getDomainDescription(), that.getDomainDescription())) {
			return false;
		}
		if (!org.jfree.chart.util.ObjectUtilities.equal(this.getRangeDescription(), that.getRangeDescription())) {
			return false;
		}
		int count = getItemCount();
		if (count != that.getItemCount()) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			if (!getDataItem(i).equals(that.getDataItem(i))) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		int result;
		result = (this.domain != null) ? this.domain.hashCode() : 0;
		result = (29 * result) + (this.range != null ? this.range.hashCode() : 0);
		result = (29 * result) + this.data.hashCode();
		result = (29 * result) + this.minStartIndex;
		result = (29 * result) + this.maxStartIndex;
		result = (29 * result) + this.minMiddleIndex;
		result = (29 * result) + this.maxMiddleIndex;
		result = (29 * result) + this.minEndIndex;
		result = (29 * result) + this.maxEndIndex;
		return result;
	}

	public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
		java.lang.Object clone = createCopy(0, getItemCount() - 1);
		return clone;
	}

	public org.jfree.data.time.TimePeriodValues createCopy(int start, int end) throws java.lang.CloneNotSupportedException {
		org.jfree.data.time.TimePeriodValues copy = ((org.jfree.data.time.TimePeriodValues) (super.clone()));
		copy.data = new java.util.ArrayList();
		if (this.data.size() > 0) {
			for (int index = start; index <= end; index++) {
				org.jfree.data.time.TimePeriodValue item = ((org.jfree.data.time.TimePeriodValue) (this.data.get(index)));
				org.jfree.data.time.TimePeriodValue clone = ((org.jfree.data.time.TimePeriodValue) (item.clone()));
				try {
					copy.add(clone);
				} catch (org.jfree.data.general.SeriesException e) {
					java.lang.System.err.println("Failed to add cloned item.");
				}
			}
		}
		return copy;
	}

	public int getMinStartIndex() {
		return this.minStartIndex;
	}

	public int getMaxStartIndex() {
		return this.maxStartIndex;
	}

	public int getMinMiddleIndex() {
		return this.minMiddleIndex;
	}

	public int getMaxMiddleIndex() {
		return this.maxMiddleIndex;
	}

	public int getMinEndIndex() {
		return this.minEndIndex;
	}

	public int getMaxEndIndex() {
		return this.maxEndIndex;
	}
}