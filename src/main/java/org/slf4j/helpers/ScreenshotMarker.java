package org.slf4j.helpers;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.MDC;

public class ScreenshotMarker extends BaseDataMarker<ScreenshotMarker> {
	private static final long serialVersionUID = 5412731321120168078L;
	private static final String NEXT_FILE_NUMBER = "NEXT_FILE_NUMBER";

	private final String logFile;
	private final ScreenshotTaker screenshotTaker;
	private Dimension imageSize;
	
	public ScreenshotMarker(String logFile, ScreenshotTaker screenshotTaker) {
		super("");

		this.logFile = logFile;
		this.screenshotTaker = screenshotTaker;
	}

	@Override
	public String getFormattedData() {
		StringBuilder buf = new StringBuilder();

		buf.append("<a href=\"").append(data).append("\">");
		buf.append("<img");
		buf.append(" src=\"").append(data).append("\"");
		buf.append(" onMouseOver=\"showScreenPopup(this);this.style.cursor='pointer'\"");
		buf.append(" onMouseOut=\"hideScreenPopup();this.style.cursor='default'\"");

		if (imageSize.width * 1.15 > imageSize.height) {
			int displaySize = 350;

			if (imageSize.width < displaySize) {
				displaySize = imageSize.width;
			}

			buf.append(" width=\"").append(displaySize).append("px\" ");
			buf.append(" class=\"");
			buf.append("sizewidth");
			buf.append("\"");
		} else {
			int displaySize = 200;

			if (imageSize.height < displaySize) {
				displaySize = imageSize.height;
			}

			buf.append(" height=\"").append(displaySize).append("px\" ");
			buf.append(" class=\"");
			buf.append("sizeheight");
			buf.append("\"");
		}

		buf.append("/>");
		buf.append("</a>");

		return buf.toString();
	}

	@Override
	public void prepareData() {
		try {
			writeScreenshot();
		} catch (Exception e) {
			data = e.getMessage();
		}
	}

	public void writeScreenshot() throws IOException {
		int fileNumber = getNextFileNumber();
		String baseFile = getBaseFilename();

		File screenshot = new File(buildFileName(baseFile, fileNumber));
		OutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(screenshot);
			this.imageSize = screenshotTaker.writeScreenshotTo(outputStream);
			this.data = screenshot.getName();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private int getNextFileNumber() {
		int nextNumber = -1;
		String next = MDC.get(NEXT_FILE_NUMBER);

		if (next != null && !next.isEmpty()) {
			nextNumber = Integer.valueOf(next);
		}

		nextNumber++;
		
		MDC.put(NEXT_FILE_NUMBER, String.valueOf(nextNumber));

		return nextNumber;
	}

	private String getBaseFilename() {
		int pos = logFile.lastIndexOf('.');

		if (pos > 0) {
			return logFile.substring(0, pos);
		} else {
			return logFile;
		}
	}

	private String buildFileName(String baseFile, int index) {
		return String.format("%sScreenShot%s.%s", baseFile, index, screenshotTaker.getFileExtension());
	}
}