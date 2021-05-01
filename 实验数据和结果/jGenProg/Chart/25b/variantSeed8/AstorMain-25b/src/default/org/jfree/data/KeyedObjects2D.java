package org.jfree.data;
public class KeyedObjects2D implements 






















































java.lang.Cloneable , java.io.Serializable {


	private static final long serialVersionUID = -1015873563138522374L;


	private java.util.List rowKeys;


	private java.util.List columnKeys;


	private java.util.List rows;




	public KeyedObjects2D() {
		this.rowKeys = new java.util.ArrayList();
		this.columnKeys = new java.util.ArrayList();
		this.rows = new java.util.ArrayList();
	}






	public int getRowCount() {
		return this.rowKeys.size();
	}






	public int getColumnCount() {
		return this.columnKeys.size();
	}









	public java.lang.Object getObject(int row, int column) {

		java.lang.Object result = null;
		org.jfree.data.KeyedObjects rowData = ((org.jfree.data.KeyedObjects) (this.rows.get(row)));
		if (rowData != null) {
			java.lang.Comparable columnKey = ((java.lang.Comparable) (this.columnKeys.get(column)));
			if (columnKey != null) {
				result = rowData.getObject(columnKey);
			}
		}
		return result;

	}








	public java.lang.Comparable getRowKey(int row) {
		return ((java.lang.Comparable) (this.rowKeys.get(row)));
	}








	public int getRowIndex(java.lang.Comparable key) {
		return this.rowKeys.indexOf(key);
	}






	public java.util.List getRowKeys() {
		return java.util.Collections.unmodifiableList(this.rowKeys);
	}








	public java.lang.Comparable getColumnKey(int column) {
		return ((java.lang.Comparable) (this.columnKeys.get(column)));
	}








	public int getColumnIndex(java.lang.Comparable key) {
		return this.columnKeys.indexOf(key);
	}






	public java.util.List getColumnKeys() {
		return java.util.Collections.unmodifiableList(this.columnKeys);
	}









	public java.lang.Object getObject(java.lang.Comparable rowKey, java.lang.Comparable columnKey) {

		java.lang.Object result = null;
		int row = this.rowKeys.indexOf(rowKey);
		if (row >= 0) {
			org.jfree.data.KeyedObjects rowData = ((org.jfree.data.KeyedObjects) (this.rows.get(row)));
			result = rowData.getObject(columnKey);
		}
		return result;

	}








	public void addObject(java.lang.Object object, 
	java.lang.Comparable rowKey, 
	java.lang.Comparable columnKey) {
		setObject(object, rowKey, columnKey);
	}








	public void setObject(java.lang.Object object, 
	java.lang.Comparable rowKey, 
	java.lang.Comparable columnKey) {

		org.jfree.data.KeyedObjects row;
		int rowIndex = this.rowKeys.indexOf(rowKey);
		if (rowIndex >= 0) {
			row = ((org.jfree.data.KeyedObjects) (this.rows.get(rowIndex)));
		} else 
		{
			this.rowKeys.add(rowKey);
			row = new org.jfree.data.KeyedObjects();
			this.rows.add(row);
		}
		row.setObject(columnKey, object);
		int columnIndex = this.columnKeys.indexOf(columnKey);
		if (columnIndex < 0) {
			this.columnKeys.add(columnKey);
		}

	}







	public void removeObject(java.lang.Comparable rowKey, java.lang.Comparable columnKey) {
		setObject(null, rowKey, columnKey);


	}






	public void removeRow(int rowIndex) {
		this.rowKeys.remove(rowIndex);
		this.rows.remove(rowIndex);
	}






	public void removeRow(java.lang.Comparable rowKey) {
		removeRow(getRowIndex(rowKey));
	}






	public void removeColumn(int columnIndex) {
		java.lang.Comparable columnKey = getColumnKey(columnIndex);
		removeColumn(columnKey);
	}






	public void removeColumn(java.lang.Comparable columnKey) {
		java.util.Iterator iterator = this.rows.iterator();
		while (iterator.hasNext()) {
			org.jfree.data.KeyedObjects rowData = ((org.jfree.data.KeyedObjects) (iterator.next()));
			rowData.removeValue(columnKey);
		} 
		this.columnKeys.remove(columnKey);
	}








	public boolean equals(java.lang.Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof org.jfree.data.KeyedObjects2D)) {
			return false;
		}

		org.jfree.data.KeyedObjects2D ko2D = ((org.jfree.data.KeyedObjects2D) (obj));
		if (!getRowKeys().equals(ko2D.getRowKeys())) {
			return false;
		}
		if (!getColumnKeys().equals(ko2D.getColumnKeys())) {
			return false;
		}
		int rowCount = getRowCount();
		if (rowCount != ko2D.getRowCount()) {
			return false;
		}

		int colCount = getColumnCount();
		if (colCount != ko2D.getColumnCount()) {
			return false;
		}

		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < colCount; c++) {
				java.lang.Object v1 = getObject(r, c);
				java.lang.Object v2 = ko2D.getObject(r, c);
				if (v1 == null) {
					if (v2 != null) {
						return false;
					}
				} else 

				if (!v1.equals(v2)) {
					return false;
				}

			}
		}
		return true;
	}






	public int hashCode() {
		int result;
		result = this.rowKeys.hashCode();
		result = (29 * result) + this.columnKeys.hashCode();
		result = (29 * result) + this.rows.hashCode();
		return result;
	}









	public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
		return super.clone();
	}}