package org.slf4j.helpers;

import org.slf4j.helpers.BasicMarker;

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

	public String getData() {
		return data;
	}
}