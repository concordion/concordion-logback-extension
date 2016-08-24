package org.concordion.slf4j.markers;

import org.slf4j.Marker;
import org.slf4j.helpers.ConcordionMarker;

public abstract class BaseDataMarker<T> extends ConcordionMarker {
	private static final long serialVersionUID = 8750307001902436743L;

	protected String data;

	public BaseDataMarker(String data) {
		super(ReportLoggerMarkers.DATA_MARKER_NAME);

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
