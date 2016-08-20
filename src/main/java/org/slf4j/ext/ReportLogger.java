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

import org.concordion.ext.ScreenshotTaker;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

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
	//public static Marker HTML_MESSAGE_MARKER = MarkerFactory.getMarker("HTML");
	public static Marker DATA_MARKER = MarkerFactory.getMarker("DATA");

	/**
	 * Given an underlying logger, construct an XLogger
	 * 
	 * @param logger
	 *            underlying logger
	 */
	public ReportLogger(Logger logger) {
		super(logger, LoggerWrapper.class.getName());
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

	/**
	 * Access custom reporting methods such as data, html, and screenshots.
	 * 
	 * @return A FluentLogger
	 */
	public FluentLogger with() {
		return new FluentLogger(logger, instanceofLAL);
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
	
	public void setScreenshotTaker(ScreenshotTaker screenshotTaker) {
		FluentLogger.addScreenshotTaker(screenshotTaker);
	}
	
	public void removeScreenshotTaker() {
		FluentLogger.removeScreenshotTaker();
	}
	
	public boolean hasScreenshotTaker() {
		return FluentLogger.hasScreenshotTaker();
	}
}
