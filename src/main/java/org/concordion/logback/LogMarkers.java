package org.concordion.logback;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.helpers.ScreenshotMarker;

public class LogMarkers {
	private LogMarkers() {}
	
	public static ScreenshotMarker screenshotMarker(ScreenshotTaker screenshotTaker, String title) {
		return new ScreenshotMarker(screenshotTaker, title);
	}
}
