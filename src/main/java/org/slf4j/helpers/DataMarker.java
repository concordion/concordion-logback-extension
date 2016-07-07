package org.slf4j.helpers;

import org.slf4j.Marker;

import ch.qos.logback.core.helpers.Transform;

public class DataMarker extends BasicMarker {
	private static final long serialVersionUID = 5412731321120168078L;
	
	private final String title;
	private final String data;
	
	public DataMarker(String title, String data) {
		super("DATA");

		this.title = title;
		this.data = data;
	}

	public String getTitle() {
		return title;
	}

	public boolean hasData() {
		return data != null && !data.isEmpty();
	}

	public String getData() {
		return Transform.escapeTags(data);
	}

	public DataMarker withMarker(Marker marker) {
		this.add(marker);
		return this;
	}
}