package org.slf4j.helpers;

import org.slf4j.Marker;

public class HTMLMarker extends BasicMarker {
	private static final long serialVersionUID = 5412731321120168078L;
	
	private final String title;
	private final String data;
	
	public HTMLMarker(String title, String data) {
		super("HTML");

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
		return data;
	}

	public HTMLMarker withMarker(Marker marker) {
		this.add(marker);
		return this;
	}
}