package org.slf4j.helpers;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.helpers.BasicMarker;

public class ScreenshotMarker extends BasicMarker {
	private static final long serialVersionUID = 9167884710836103981L;
	private final ScreenshotTaker screenshotTaker;
	private final String title;
	
	public ScreenshotMarker(ScreenshotTaker screenshotTaker, String title) {
		super("SCREENSHOT");

		this.screenshotTaker = screenshotTaker;
		this.title = title;
	}
	
	public ScreenshotTaker getScreenshotTaker() {
		return screenshotTaker;
	}
	
	public String getTitle() {
		return title;
	}
}
