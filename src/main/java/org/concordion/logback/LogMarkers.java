package org.concordion.logback;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.ScreenshotMarker;

public class LogMarkers {
	private LogMarkers() {}
	
	public static ScreenshotMarker screenshotMarker(String title, ScreenshotTaker screenshotTaker) {
		return new ScreenshotMarker(title, screenshotTaker);
	}
	
	public static DataMarker dataMarker(String title, String data) {
		return new DataMarker(title, data);
	}
}
