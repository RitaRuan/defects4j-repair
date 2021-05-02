package org.jfree.data.time;
public class TimeSeriesDataItem implements 














































































java.lang.Cloneable , java.lang.Comparable , java.io.Serializable {


	private static final long serialVersionUID = -2235346966016401302L;


	private org.jfree.data.time.RegularTimePeriod period;


	private java.lang.Number value;






	private boolean selected;







	public TimeSeriesDataItem(org.jfree.data.time.RegularTimePeriod period, java.lang.Number value) {
		if (period == null) {
			throw new java.lang.IllegalArgumentException("Null 'period' argument.");
		}
		this.period = period;
		this.value = value;
		this.selected = false;
	}







	public TimeSeriesDataItem(org.jfree.data.time.RegularTimePeriod period, double value) {
		this(period, new java.lang.Double(value));
	}






	public org.jfree.data.time.RegularTimePeriod getPeriod() {
		return this.period;
	}








	public java.lang.Number getValue() {
		return this.value;
	}











	public void setValue(java.lang.Number value) {
		this.value = value;
	}











	public boolean isSelected() {
		return this.selected;
	}










	public void setSelected(boolean selected) {
		this.selected = selected;
	}








	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof org.jfree.data.time.TimeSeriesDataItem)) {
			return false;
		}
		org.jfree.data.time.TimeSeriesDataItem that = ((org.jfree.data.time.TimeSeriesDataItem) (obj));
		if (!this.period.equals(that.period)) {
			return false;
		}
		if (!org.jfree.chart.util.ObjectUtilities.equal(this.value, that.value)) {
			return false;
		}
		if (this.selected != that.selected) {
			return false;
		}
		return true;
	}






	public int hashCode() {
		int result;
		result = (this.period != null) ? this.period.hashCode() : 0;
		result = (29 * result) + (this.value != null ? this.value.hashCode() : 0);
		result = org.jfree.chart.util.HashUtilities.hashCode(result, this.selected);
		return result;
	}













	public int compareTo(java.lang.Object o1) {

		int result;



		if (o1 instanceof org.jfree.data.time.TimeSeriesDataItem) {
			org.jfree.data.time.TimeSeriesDataItem datapair = ((org.jfree.data.time.TimeSeriesDataItem) (o1));
			result = getPeriod().compareTo(datapair.getPeriod());
		} else 



		{

			result = 1;
		}

		return result;

	}







	public java.lang.Object clone() {
		java.lang.Object clone = null;
		try {
			clone = super.clone();
		}
		 catch (java.lang.CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}}