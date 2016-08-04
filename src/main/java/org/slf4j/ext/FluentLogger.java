package org.slf4j.ext;

import java.io.File;
import java.util.Iterator;

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BaseDataMarker;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.HtmlMarker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.ScreenshotMarker;
import org.slf4j.spi.LocationAwareLogger;

public class FluentLogger {
	private final Logger logger;
	// Is instance of location aware logger
	private final boolean instanceofLAL;
	// The fully qualified class name of the logger instance
	private final String reportLoggerFQCN;

	private String overrideFQCN = null;
	private Marker marker = null;
	private String format;
	private Object[] arguments;

	public FluentLogger(Logger logger, boolean instanceofLAL) {
		this.reportLoggerFQCN = FluentLogger.class.getName();
		this.logger = logger;
		this.instanceofLAL = instanceofLAL;
	}

	private void addMarker(Marker reference) {
		if (marker == null) {
			// Start with a detached marker so that any bound markers that are added are not accidentally reused in
			// subsequent logging statements
			marker = MarkerFactory.getDetachedMarker("FLUENT_LOGGER");
		}

		marker.add(reference);
	}

	public FluentLogger htmlMessage(String format, Object... arguments) {
		addMarker(ReportLogger.HTML_MESSAGE_MARKER);
		this.format = format;
		this.arguments = arguments;

		// TODO Can we log plain text to console and html message to file?
		//
		// Tried the following but came unstuck with HtmlLayout reading correct message
		/*
		addMarker(new HtmlMessageMarker(format, arguments));

		// Remove HTML tags
		this.format = format.replaceAll("<.*?>", "");
		this.arguments = arguments;

		for (int i = 0; i < this.arguments.length; i++) {
			if (this.arguments[i] instanceof String) {
				this.arguments[i] = StringEscapeUtils.unescapeHtml4((String) this.arguments[i]);
			}
		}
		*/
		
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

	public FluentLogger marker(Marker marker) {
		addMarker(marker);
		return this;
	}

	public FluentLogger locationAwareParent(Object currentClass) {
		overrideFQCN = currentClass.getClass().getName();
		return this;
	}

	public void trace() {
		if (!logger.isTraceEnabled(marker)) {
			return;
		}

		prepareData(marker);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.TRACE_INT, getFormattedMessage(), arguments, null);
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
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.DEBUG_INT, getFormattedMessage(), arguments, null);
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
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.INFO_INT, getFormattedMessage(), arguments, null);
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
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.WARN_INT, getFormattedMessage(), arguments, null);
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
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.ERROR_INT, getFormattedMessage(), arguments, null);
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

	private String getFQCN() {
		return overrideFQCN == null ? reportLoggerFQCN : overrideFQCN;
	}

	private String getFormattedMessage() {
		return MessageFormatter.arrayFormat(format, arguments).getMessage();
	}

	private void reset() {
		this.overrideFQCN = null;
		this.marker = null;
		this.format = null;
		this.arguments = null;
	}
}
