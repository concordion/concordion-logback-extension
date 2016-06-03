package org.concordion.logback;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.ScreenshotMarker;

public class LogMarkers {
	public static String STEP = "STEP";
	public static String HTML = "HTML";
	
	private LogMarkers() {}
	
	public static ScreenshotMarker screenshot(String title, ScreenshotTaker screenshotTaker) {
		return new ScreenshotMarker(title, screenshotTaker);
	}
	
	public static DataMarker data(String title, String data) {
		return new DataMarker(title, data, true);
	}

	public static DataMarker html(String title, String data) {
		return new DataMarker(title, data, false);
	}

	public static Marker htmlStatementMarker() {
		return MarkerFactory.getMarker(HTML);
	}

	public static Marker step() {
		return MarkerFactory.getMarker(STEP);
	}
}
