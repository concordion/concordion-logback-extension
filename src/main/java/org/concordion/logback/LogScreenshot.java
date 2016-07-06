package org.concordion.logback;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Marker;
import org.slf4j.ext.CLogger;
import org.slf4j.ext.EventData;
import org.slf4j.ext.LogRecorder;

public class LogScreenshot implements LogRecorder {
	public static LogScreenshot capture(ScreenshotTaker screenshotTaker, String format, Object... arguments) {
		return new LogScreenshot(null, screenshotTaker);
	}

	public static LogScreenshot capture(String imagePath) {
		return new LogScreenshot(imagePath);
	}

	private final ScreenshotTaker screenshotTaker;
	private final String title;
	private String baseFile;
	private Dimension imageSize;
	private String fileName;

	private LogScreenshot(String fileName) {
		this.fileName = fileName;

		this.title = null;
		this.screenshotTaker = null;
	}

	private LogScreenshot(String title, ScreenshotTaker screenshotTaker) {
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
		OutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(screenshot);
			this.imageSize = screenshotTaker.writeScreenshotTo(outputStream);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		this.fileName = screenshot.getName();
	}

	private String buildFileName(int index) {
		return String.format("%sScreenShot%s.%s", baseFile, index, screenshotTaker.getFileExtension());
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public Marker getMarker() {
		return CLogger.SCREENSHOT_MARKER;
	}

	@Override
	public EventData getEventData() {
		return new EventData(fileName);
	}

}
