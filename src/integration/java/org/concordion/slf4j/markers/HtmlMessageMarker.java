package org.concordion.slf4j.markers;

import org.slf4j.helpers.ConcordionMarker;

public class HtmlMessageMarker extends ConcordionMarker {
	private static final long serialVersionUID = 5412731321120168078L;
	public static final String MARKER_NAME = "HTML_MESSAGE"; 
	
	private String format;
	private Object[] arguments;

	public HtmlMessageMarker(String format, Object... arguments) {
		super(MARKER_NAME);

		this.format = format;
		this.arguments = arguments;
	}

	public String getFormat() {
		return format;
	}

	public Object[] getArguments() {
		return arguments;
	}
}