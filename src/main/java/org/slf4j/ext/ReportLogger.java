//TODO NIGEL: When doing fluent style log message should we return a different class as this will let you call incorrect commands and will not work as expected.   

/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
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

/**
 * A utility that provides standard mechanisms for logging certain kinds of
 * activities.
 * 
 * @author Andrew Sumner
 */
public class ReportLogger extends LoggerWrapper {
	public static Marker TOOLTIP_MARKER = MarkerFactory.getMarker("TOOLTIP");

	public static Marker PROGRESS_MARKER = MarkerFactory.getMarker("PROGRESS");
	public static Marker STEP_MARKER = MarkerFactory.getMarker("STEP");
	public static Marker HTML_MESSAGE_MARKER = MarkerFactory.getMarker("HTML");
	public static Marker DATA_MARKER = MarkerFactory.getMarker("DATA");


	final String reportLoggerFQCN;
	
	/**
	 * Given an underlying logger, construct an XLogger
	 * 
	 * @param logger
	 *            underlying logger
	 */
	public ReportLogger(Logger logger) {
		super(logger, LoggerWrapper.class.getName());
		
		reportLoggerFQCN = ReportLogger.class.getName();
	}

	/**
	 * Logs progress of test suite to console - not added to log file.
	 * 
	 * @param format
	 *            the format string
	 * @param arguments
	 *            a list of arguments
	 */
	public void progress(String format, Object... arguments) {
		logger.info(PROGRESS_MARKER, format, arguments);
	}

	/**
	 * Logs a step.
	 * 
	 * @param format
	 *            the format string
	 * @param arguments
	 *            a list of arguments
	 */
	public void step(String format, Object... arguments) {
		logger.info(STEP_MARKER, format, arguments);
	}

	private Marker marker = null;
	private String format;
	private Object[] arguments;

	private void addMarker(Marker reference) {
		if (marker == null) {
			marker = reference;
		} else {
			marker.add(reference);
		}
	}

	public ReportLogger withHtmlMessage(String format, Object... arguments) {
		addMarker(HTML_MESSAGE_MARKER);
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public ReportLogger withMessage(String format, Object... arguments) {
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public ReportLogger withData(String data) {
		addMarker(new DataMarker(data));
		return this;
	}

	public ReportLogger withHtml(String html) {
		addMarker(new HtmlMarker(html));
		return this;
	}

	public ReportLogger withScreenshot(File logFile, ScreenshotTaker screenshotTaker) {
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

		if (reference.getName().equals(DATA_MARKER.getName())) {
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

	/**
	 * Logs a tool tip.
	 * 
	 * @param format
	 *            the format string
	 * @param arguments
	 *            a list of arguments
	 */
	public void tooltip(String format, Object... arguments) {
		debug(TOOLTIP_MARKER, format, arguments);
	}
}
