package org.jfree.data.statistics;
public class DefaultStatisticalCategoryDataset extends 


































































org.jfree.data.general.AbstractDataset implements 
org.jfree.data.statistics.StatisticalCategoryDataset , org.jfree.data.RangeInfo {


	private org.jfree.data.KeyedObjects2D data;


	private double minimumRangeValue;


	private double minimumRangeValueIncStdDev;


	private double maximumRangeValue;


	private double maximumRangeValueIncStdDev;




	public DefaultStatisticalCategoryDataset() {
		this.data = new org.jfree.data.KeyedObjects2D();
		this.minimumRangeValue = java.lang.Double.NaN;
		this.maximumRangeValue = java.lang.Double.NaN;
		this.minimumRangeValueIncStdDev = java.lang.Double.NaN;
		this.maximumRangeValueIncStdDev = java.lang.Double.NaN;
	}









	public java.lang.Number getMeanValue(int row, int column) {
		java.lang.Number result = null;
		org.jfree.data.statistics.MeanAndStandardDeviation masd = 
		((org.jfree.data.statistics.MeanAndStandardDeviation) (this.data.getObject(row, column)));
		if (masd != null) {

		}
		return result;
	}










	public java.lang.Number getValue(int row, int column) {
		return getMeanValue(row, column);
	}










	public java.lang.Number getValue(java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		return getMeanValue(rowKey, columnKey);
	}









	public java.lang.Number getMeanValue(java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		java.lang.Number result = null;
		org.jfree.data.statistics.MeanAndStandardDeviation masd = 
		((org.jfree.data.statistics.MeanAndStandardDeviation) (this.data.getObject(rowKey, columnKey)));
		if (masd != null) {
			result = masd.getMean();
		}
		return result;
	}









	public java.lang.Number getStdDevValue(int row, int column) {
		java.lang.Number result = null;
		org.jfree.data.statistics.MeanAndStandardDeviation masd = 
		((org.jfree.data.statistics.MeanAndStandardDeviation) (this.data.getObject(row, column)));
		if (masd != null) {
			result = masd.getStandardDeviation();
		}
		return result;
	}









	public java.lang.Number getStdDevValue(java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		java.lang.Number result = null;
		org.jfree.data.statistics.MeanAndStandardDeviation masd = 
		((org.jfree.data.statistics.MeanAndStandardDeviation) (this.data.getObject(rowKey, columnKey)));
		if (masd != null) {
			result = masd.getStandardDeviation();
		}
		return result;
	}








	public int getColumnIndex(java.lang.Comparable key) {
		return this.data.getColumnIndex(key);
	}








	public java.lang.Comparable getColumnKey(int column) {
		return this.data.getColumnKey(column);
	}






	public java.util.List getColumnKeys() {
		return this.data.getColumnKeys();
	}








	public int getRowIndex(java.lang.Comparable key) {
		return this.data.getRowIndex(key);
	}








	public java.lang.Comparable getRowKey(int row) {
		return this.data.getRowKey(row);
	}






	public java.util.List getRowKeys() {
		return this.data.getRowKeys();
	}






	public int getRowCount() {
		return this.data.getRowCount();
	}






	public int getColumnCount() {
		return this.data.getColumnCount();
	}









	public void add(double mean, double standardDeviation, 
	java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		add(new java.lang.Double(mean), new java.lang.Double(standardDeviation), rowKey, columnKey);
	}









	public void add(java.lang.Number mean, java.lang.Number standardDeviation, 
	java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		org.jfree.data.statistics.MeanAndStandardDeviation item = new org.jfree.data.statistics.MeanAndStandardDeviation(
		mean, standardDeviation);
		this.data.addObject(item, rowKey, columnKey);
		double m = 0.0;
		double sd = 0.0;
		if (mean != null) {
			m = mean.doubleValue();
		}
		if (standardDeviation != null) {
			sd = standardDeviation.doubleValue();
		}

		if (!java.lang.Double.isNaN(m)) {
			if (java.lang.Double.isNaN(this.maximumRangeValue) || 
			(m > this.maximumRangeValue)) {
				this.maximumRangeValue = m;
			}
		}

		if (!java.lang.Double.isNaN(m + sd)) {
			if (java.lang.Double.isNaN(this.maximumRangeValueIncStdDev) || 
			((m + sd) > this.maximumRangeValueIncStdDev)) {
				this.maximumRangeValueIncStdDev = m + sd;
			}
		}

		if (!java.lang.Double.isNaN(m)) {
			if (java.lang.Double.isNaN(this.minimumRangeValue) || 
			(m < this.minimumRangeValue)) {
				this.minimumRangeValue = m;
			}
		}

		if (!java.lang.Double.isNaN(m - sd)) {
			if (java.lang.Double.isNaN(this.minimumRangeValueIncStdDev) || 
			((m - sd) < this.minimumRangeValueIncStdDev)) {
				this.minimumRangeValueIncStdDev = m - sd;
			}
		}

		fireDatasetChanged();
	}










	public double getRangeLowerBound(boolean includeInterval) {
		return this.minimumRangeValue;
	}










	public double getRangeUpperBound(boolean includeInterval) {
		return this.maximumRangeValue;
	}









	public org.jfree.data.Range getRangeBounds(boolean includeInterval) {
		org.jfree.data.Range result = null;
		if (includeInterval) {
			if ((!java.lang.Double.isNaN(this.minimumRangeValueIncStdDev)) && 
			(!java.lang.Double.isNaN(this.maximumRangeValueIncStdDev)))
				result = new org.jfree.data.Range(this.minimumRangeValueIncStdDev, 
				this.maximumRangeValueIncStdDev);
		} else 

		if ((!java.lang.Double.isNaN(this.minimumRangeValue)) && 
		(!java.lang.Double.isNaN(this.maximumRangeValue)))
			result = new org.jfree.data.Range(this.minimumRangeValue, this.maximumRangeValue);

		return result;
	}








	public boolean equals(java.lang.Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof org.jfree.data.statistics.DefaultStatisticalCategoryDataset)) {
			return false;
		}
		org.jfree.data.statistics.DefaultStatisticalCategoryDataset that = 
		((org.jfree.data.statistics.DefaultStatisticalCategoryDataset) (obj));
		if (!this.data.equals(that.data)) {
			return false;
		}
		return true;
	}}