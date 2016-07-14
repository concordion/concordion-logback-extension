package org.slf4j.ext;

import java.io.File;
import java.util.Iterator;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.BaseDataMarker;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.HtmlMarker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.ScreenshotMarker;
import org.slf4j.spi.LocationAwareLogger;

public class FluentLogger {
	private final Logger logger;
	private final boolean instanceofLAL;

	private Marker marker = null;
	private String format;
	private Object[] arguments;

	// The fully qualified class name of the logger instance
	private final String reportLoggerFQCN;

	public FluentLogger(Logger logger, boolean instanceofLAL) {
		this.reportLoggerFQCN = FluentLogger.class.getName();
		this.logger = logger;
		this.instanceofLAL = instanceofLAL;
	}

	private void addMarker(Marker reference) {
		if (marker == null) {
			marker = reference;
		} else {
			marker.add(reference);
		}
	}

	public FluentLogger htmlMessage(String format, Object... arguments) {
		addMarker(ReportLogger.HTML_MESSAGE_MARKER);
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public FluentLogger message(String format, Object... arguments) {
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public FluentLogger data(String data) {
		addMarker(new DataMarker(data));
		return this;
	}

	public FluentLogger html(String html) {
		addMarker(new HtmlMarker(html));
		return this;
	}

	public FluentLogger screenshot(File logFile, ScreenshotTaker screenshotTaker) {
		addMarker(new ScreenshotMarker(logFile.getPath(), screenshotTaker));
		return this;
	}

	public void trace() {
		if (!logger.isTraceEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
			((LocationAwareLogger) logger).log(marker, reportLoggerFQCN, LocationAwareLogger.TRACE_INT, formattedMessage, arguments, null);
		} else {
			logger.trace(marker, format, arguments);
		}

		reset();
	}

	public void debug() {
		if (!logger.isDebugEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
			((LocationAwareLogger) logger).log(marker, reportLoggerFQCN, LocationAwareLogger.DEBUG_INT, formattedMessage, arguments, null);
		} else {
			logger.debug(marker, format, arguments);
		}

		reset();
	}

	public void info() {
		if (!logger.isInfoEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
			((LocationAwareLogger) logger).log(marker, reportLoggerFQCN, LocationAwareLogger.INFO_INT, formattedMessage, arguments, null);
		} else {
			logger.info(marker, format, arguments);
		}

		reset();
	}

	public void warn() {
		if (!logger.isWarnEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
			((LocationAwareLogger) logger).log(marker, reportLoggerFQCN, LocationAwareLogger.WARN_INT, formattedMessage, arguments, null);
		} else {
			logger.warn(marker, format, arguments);
		}

		reset();
	}

	public void error() {
		if (!logger.isErrorEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
			((LocationAwareLogger) logger).log(marker, reportLoggerFQCN, LocationAwareLogger.ERROR_INT, formattedMessage, arguments, null);
		} else {
			logger.error(marker, format, arguments);
		}

		reset();
	}

	private void prepareData(Marker reference) {
		if (reference == null) {
			return;
		}

		if (reference.getName().equals(ReportLogger.DATA_MARKER.getName())) {
			((BaseDataMarker<?>) reference).prepareData();
		}

		Iterator<Marker> references = reference.iterator();
		while (references.hasNext()) {
			prepareData(references.next());
		}
	}

	private void reset() {
		this.marker = null;
		this.format = null;
		this.arguments = null;
	}
}
