package org.slf4j.helpers;

import org.slf4j.Marker;

public class DataMarker extends BasicMarker {
	private static final long serialVersionUID = 5412731321120168078L;
	
	private final String title;
	private final String data;
	private final boolean escapeData;
	
	public DataMarker(String title, String data, boolean escapeData) {
		super("DATA");

		this.title = title;
		this.data = data;
		this.escapeData = escapeData;
	}

	public String getTitle() {
		return title;
	}

	public String getData() {
		return data;
	}

	public boolean escapeData() {
		return escapeData;
	}

	public DataMarker withMarker(Marker marker) {
		this.add(marker);
		return this;
	}
}