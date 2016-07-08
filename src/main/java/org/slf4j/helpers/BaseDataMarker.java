package org.slf4j.helpers;

import org.slf4j.Marker;
import org.slf4j.ext.CLogger;

public abstract class BaseDataMarker<T> extends BasicMarker {
	private static final long serialVersionUID = 8750307001902436743L;

	protected String data;

	public BaseDataMarker(String data) {
		super(CLogger.DATA_MARKER.getName());

		this.data = data;
	}

	public abstract String getFormattedData();

	public abstract void prepareData();

	@SuppressWarnings("unchecked")
	public T withMarker(Marker marker) {
		this.add(marker);
		return (T) this;
	}

	public boolean hasData() {
		return data != null && !data.isEmpty();
	}
}
