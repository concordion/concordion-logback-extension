package org.slf4j.ext;

import java.util.Iterator;

import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.loggingFormatter.ILoggingAdaptor;
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
	private static ThreadLocal<ILoggingAdaptor> loggingAdaptors = new ThreadLocal<ILoggingAdaptor>();
	private static ThreadLocal<ScreenshotTaker> screenshotTakers = new ThreadLocal<ScreenshotTaker>();
	
	private final Logger logger;
	// Is instance of location aware logger
	private final boolean instanceofLAL;
	// The fully qualified class name of the logger instance
	private final String reportLoggerFQCN;
	
	private String overrideFQCN = null;
	private Marker marker = null;
	private String format;
	private Object[] arguments;

	public static void addLoggingAdaptor(ILoggingAdaptor loggingAdaptor) {
		loggingAdaptors.set(loggingAdaptor);
	}
	
	public static void removeLoggingAdaptor() {
		loggingAdaptors.remove();
	}

	public static void addScreenshotTaker(ScreenshotTaker screenshotTaker) {
		screenshotTakers.set(screenshotTaker);
	}

	public static void removeScreenshotTaker() {
		screenshotTakers.remove();
	}
	
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

	// TODO Think about using object tag to embed files
	// <script language="javascript" type="text/javascript">
	// function resizeIframe(obj) {
	// var height = obj.contentWindow.document.body.scrollHeight;
	// if (height > 200) height = 200;
	//
	// obj.style.height = 0;
	// obj.style.height = height + 'px';
	// }
	// </script>
	// <a href="test.txt">Open File</a>
	// <object width="100%" height="50" type="text/plain" data="test.txt" border="1" onload="resizeIframe(this)"><a href="test.txt">test.txt</a></object>
	//
	//
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

	private ILoggingAdaptor getLoggingAdaptor() {
		ILoggingAdaptor adaptor = loggingAdaptors.get();
		
		if (adaptor == null) {
			throw new RuntimeException("Logging adapter has not been set for the current thread");
		}
		
		return adaptor;
	}
	
	public static boolean hasScreenshotTaker() {
		return screenshotTakers.get() != null;
	}

	public ScreenshotTaker getScreenshotTaker() {
		ScreenshotTaker screenshotTaker = screenshotTakers.get();
		
		if (screenshotTaker == null) {
			throw new RuntimeException("ScreenshotTaker has not been set");
		}
		
		return screenshotTaker;
	}
	
	public FluentLogger screenshot() {
		return screenshot(getScreenshotTaker());
	}
	
	public FluentLogger screenshot(ScreenshotTaker screenshotTaker) {
		// TODO Don't like having to get logging adaptor - screenshots (and potentially data files)
		// are the only thing that need access to the adaptor.
		addMarker(new ScreenshotMarker(getLoggingAdaptor().getLogFile().getPath(), screenshotTaker));
		return this;
	}

	// TODO Need to be able to prepare page object for screenshot
	// 1. Pass in WebElement so that will highlight element clicking on 
	//		* could have special screenshot taker that does that
	//		* then may as well not register screenshot taker with logging extension -> this and always pass in
	// 2. Sometimes need to run some javascript over page object to force full screen printing
	//		* could implement ScreenshotFormatting interface
	// 3. Force common message format eg Clicking 'Log In'
	//		* Leave this up to implementing application
	// 4. A method to switch ALL screenshots off
	//		* should be able to do this with Logback formatting to disable marker
	//		* support doing it programatically and via configuration
	// 5. Storyboard marker will want to set title and result, could get title from ScreenshotFormatting interface
	public FluentLogger screenshot(ScreenshotFormatting pageObject) {
		return screenshot(getScreenshotTaker(), pageObject);
	}

	public FluentLogger screenshot(ScreenshotTaker screenshotTaker, ScreenshotFormatting pageObject) {
		//addMarker(new ScreenshotMarker(getLoggingAdaptor().getLogFile().getPath(), screenshotTaker, pageObject));
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

	public FluentLogger locationAwareParent(Class<?> currentClass) {
		overrideFQCN = currentClass.getName();
		return this;
	}
	
	public void trace() {
		if (!logger.isTraceEnabled(marker)) {
			return;
		}

		prepare();

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

		prepare();

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

		prepare();

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

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.WARN_INT, getFormattedMessage(), arguments, null);
		} else {
			logger.warn(marker, format, arguments);
		}

		reset();
	}

	public void error() {
		error(null);
	}

	public void error(Throwable t) {
		if (!logger.isErrorEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.ERROR_INT, getFormattedMessage(), arguments, t);
		} else {
			logger.error(marker, format, arguments);
		}

		reset();
	}

	private void prepare() {
		if (format == null) {
			// Tell logger to ignore lines with no message - we're probably just wanting to notify an extension of some event
			addMarker(ReportLogger.PROGRESS_MARKER);
		}

		prepareData(marker);
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
