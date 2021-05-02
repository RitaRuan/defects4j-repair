package org.jfree.data.general;
public abstract class Series implements java.lang.Cloneable , java.io.Serializable {
	private static final long serialVersionUID = -6906561437538683581L;

	private java.lang.Comparable key;

	private java.lang.String description;

	private javax.swing.event.EventListenerList listeners;

	private java.beans.PropertyChangeSupport propertyChangeSupport;

	private boolean notify;

	protected Series(java.lang.Comparable key) {
		this(key, null);
	}

	protected Series(java.lang.Comparable key, java.lang.String description) {
		if (key == null) {
			throw new java.lang.IllegalArgumentException("Null 'key' argument.");
		}
		this.key = key;
		this.description = description;
		this.listeners = new javax.swing.event.EventListenerList();
		this.propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		this.notify = true;
	}

	public java.lang.Comparable getKey() {
		return this.key;
	}

	public void setKey(java.lang.Comparable key) {
		if (key == null) {
			throw new java.lang.IllegalArgumentException("Null 'key' argument.");
		}
		java.lang.Comparable old = this.key;
		this.key = key;
		this.propertyChangeSupport.firePropertyChange("Key", old, key);
	}

	public java.lang.String getDescription() {
		return this.description;
	}

	public void setDescription(java.lang.String description) {
		java.lang.String old = this.description;
		this.description = description;
		this.propertyChangeSupport.firePropertyChange("Description", old, description);
	}

	public boolean getNotify() {
		return this.notify;
	}

	public void setNotify(boolean notify) {
		if (this.notify != notify) {
			this.notify = notify;
			fireSeriesChanged();
		}
	}

	public boolean isEmpty() {
		return getItemCount() == 0;
	}

	public abstract int getItemCount();

	public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
		org.jfree.data.general.Series clone = ((org.jfree.data.general.Series) (super.clone()));
		clone.listeners = new javax.swing.event.EventListenerList();
		clone.propertyChangeSupport = new java.beans.PropertyChangeSupport(clone);
		return clone;
	}

	public boolean equals(java.lang.Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof org.jfree.data.general.Series)) {
			return false;
		}
		org.jfree.data.general.Series that = ((org.jfree.data.general.Series) (obj));
		if (!getKey().equals(that.getKey())) {
			return false;
		}
		if (!org.jfree.chart.util.ObjectUtilities.equal(getDescription(), that.getDescription())) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result;
		result = this.key.hashCode();
		result = (29 * result) + (this.description != null ? this.description.hashCode() : 0);
		return result;
	}

	public void addChangeListener(org.jfree.data.event.SeriesChangeListener listener) {
		this.listeners.add(org.jfree.data.event.SeriesChangeListener.class, listener);
	}

	public void removeChangeListener(org.jfree.data.event.SeriesChangeListener listener) {
		this.listeners.remove(org.jfree.data.event.SeriesChangeListener.class, listener);
	}

	public void fireSeriesChanged() {
		if (this.notify) {
			notifyListeners(new org.jfree.data.event.SeriesChangeEvent(this));
		}
	}

	protected void notifyListeners(org.jfree.data.event.SeriesChangeEvent event) {
		java.lang.Object[] listenerList = this.listeners.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == org.jfree.data.event.SeriesChangeListener.class) {
				((org.jfree.data.event.SeriesChangeListener) (listenerList[i + 1])).seriesChanged(event);
			}
		}
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(java.lang.String property, java.lang.Object oldValue, java.lang.Object newValue) {
		this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
	}
}