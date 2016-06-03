package org.slf4j.helpers;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Marker;

public class ScreenshotMarker extends BasicMarker {
	private static final long serialVersionUID = 9167884710836103981L;
	private final ScreenshotTaker screenshotTaker;
	private final String title;
	private String baseFile;
	private Dimension imageSize;
	private String fileName;
	
	public ScreenshotMarker(String title, ScreenshotTaker screenshotTaker) {
		super("SCREENSHOT");

		this.title = title;
		this.screenshotTaker = screenshotTaker;
	}

	public ScreenshotTaker getScreenshotTaker() {
		return screenshotTaker;
	}

	public String getTitle() {
		return title;
	}

	public Dimension getImageSize() {
		return imageSize;
	}

	public void setOutputFolder(String logFile) {
		int pos = logFile.lastIndexOf('.');
		
		if (pos > 0) {
			this.baseFile = logFile.substring(0, pos);
		} else {
			this.baseFile = logFile;
		}
	}
	
	public void writeScreenshot(int index) throws IOException {
		String file = buildFileName(index);
		
		File screenshot = new File(file);
		try (OutputStream outputStream = new FileOutputStream(screenshot)) {
			this.imageSize = screenshotTaker.writeScreenshotTo(outputStream);
		}
		
		this.fileName = screenshot.getName();
	}
	
	private String buildFileName(int index) {
		return String.format("%sScreenShot%s.%s", baseFile, index, screenshotTaker.getFileExtension());		
	}

	public String getFileName() {
		return fileName;
	}

	public ScreenshotMarker withMarker(Marker marker) {
		this.add(marker);
		return this;
	}
}
