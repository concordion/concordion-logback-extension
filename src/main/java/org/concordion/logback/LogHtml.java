package org.concordion.logback;

import org.slf4j.Marker;
import org.slf4j.ext.CLogger;
import org.slf4j.ext.EventData;
import org.slf4j.ext.LogRecorder;

public class LogHtml implements LogRecorder {
	public static LogHtml capture(String html) {
		return new LogHtml(html);
	}

	private String html;

	private LogHtml(String html) {
		this.html = html;
	}

	@Override
	public Marker getMarker() {
		return CLogger.HTML_MARKER;
	}

	@Override
	public EventData getEventData() {
		return new EventData(html);
	}

}
