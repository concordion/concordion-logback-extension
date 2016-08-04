package org.slf4j.helpers;

public class HtmlMessageMarker extends BasicMarker {
	private static final long serialVersionUID = 5412731321120168078L;
	
	private String format;
	private Object[] arguments;

	public HtmlMessageMarker(String format, Object... arguments) {
		super("HTML_MESSAGE");

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