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

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.DataMarker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * A utility that provides standard mechanisms for logging certain kinds of
 * activities.
 * 
 * @author Andrew Sumner
 */
public class CLogger extends LoggerWrapper {
	private static final String FQCN = CLogger.class.getName();

	public static Marker TOOLTIP_MARKER = MarkerFactory.getMarker("TOOLTIP");

	public static Marker PROGRESS_MARKER = MarkerFactory.getMarker("PROGRESS");
	public static Marker STEP_MARKER = MarkerFactory.getMarker("STEP");
	public static Marker SCREENSHOT_MARKER = MarkerFactory.getMarker("SCREENSHOT");
	public static Marker HTML_MARKER = MarkerFactory.getMarker("HTML");
	public static Marker DATA_MARKER = MarkerFactory.getMarker("DATA");
	public static Marker DATA_RECORDER = MarkerFactory.getMarker("DATA_RECORDER");

	final String fqcn;
	
	/**
	 * Given an underlying logger, construct an XLogger
	 * 
	 * @param logger
	 *            underlying logger
	 */
	public CLogger(Logger logger) {
		super(logger, LoggerWrapper.class.getName());
		
		fqcn = CLogger.class.getName();
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

	public CLogger withHtmlMessage(String format, Object... arguments) {
		addMarker(HTML_MARKER);
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public CLogger withMessage(String format, Object... arguments) {
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public CLogger withData(String data) {
		addMarker(new DataMarker(null, data));
		return this;
	}

	public void trace() {
        trace(marker, format, arguments);
		reset();
	}

	@Override
	public void trace(Marker marker, String format, Object... args) {
        if (!logger.isTraceEnabled(marker))
            return;
        if (instanceofLAL) {
            String formattedMessage = MessageFormatter.arrayFormat(format, args).getMessage();
            ((LocationAwareLogger) logger).log(marker, fqcn, LocationAwareLogger.TRACE_INT, formattedMessage, args, null);
        } else {
            logger.trace(marker, format, args);
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

	//
	// /**
	// * Log a screenshot.
	// *
	// */
	// public void screenshot(ScreenshotTaker screenshotTaker, String format,
	// Object... arguments) {
	// if (logger.isDebugEnabled(SCREENSHOT_MARKER)) {
	// // TODO Use EventData to pass screenshot information
	// logger.debug(format, arguments);
	// }
	// }
	//
	// public void html(String html, String format, Object... arguments) {
	// if (logger.isDebugEnabled(HTML_MARKER)) {
	// // TODO Use EventData to pass html information
	// logger.debug(format, arguments);
	// }
	//
	// }
	//
	// /**
	// * Log a screenshot.
	// *
	// */
	// public void html(String format, org.slf4j.event.Level level, String html,
	// Object... arguments) {
	// if (instanceofLAL) {
	// // TODO How get next number of screenshot, and where should I take it?
	// ((LocationAwareLogger) logger).log(new HTMLMarker("?", null), FQCN,
	// level.toInt(), format, arguments, null);
	// }
	// }
	//
	// public void data(String format, org.slf4j.event.Level level, String data,
	// Object... arguments) {
	// if (instanceofLAL) {
	// // TODO How get next number of screenshot, and where should I take it?
	// ((LocationAwareLogger) logger).log(new DataMarker("?", null), FQCN,
	// level.toInt(), format, arguments, null);
	// }
	// }

}
