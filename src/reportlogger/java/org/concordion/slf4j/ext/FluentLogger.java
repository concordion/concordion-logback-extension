package org.concordion.slf4j.ext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.concordion.ext.ScreenshotTaker;
import org.concordion.slf4j.ILoggingAdaptor;
import org.concordion.slf4j.markers.AttachmentMarker;
import org.concordion.slf4j.markers.BaseDataMarker;
import org.concordion.slf4j.markers.DataMarker;
import org.concordion.slf4j.markers.HtmlMarker;
import org.concordion.slf4j.markers.HtmlMessageMarker;
import org.concordion.slf4j.markers.ReportLoggerMarkers;
import org.concordion.slf4j.markers.ScreenshotMarker;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public class FluentLogger {
	private static ThreadLocal<ILoggingAdaptor> loggingAdaptors = new ThreadLocal<ILoggingAdaptor>();
	private static ThreadLocal<ScreenshotTaker> screenshotTakers = new ThreadLocal<ScreenshotTaker>();

	private final Logger logger;
	// Is instance of location aware logger
	private final boolean instanceofLAL;
	// The fully qualified class name of the logger instance
	private final String reportLoggerFQCN;

	private boolean buffered;
	private int bufferedLevel;
	private Throwable bufferedThrowable = null;
	
	private String overrideFQCN = null;
	private Marker marker = null;
	private String format = null;
	private Object[] arguments = null;

	public static void addLoggingAdaptor(ILoggingAdaptor loggingAdaptor) {
		loggingAdaptors.set(loggingAdaptor);
	}

	public static ILoggingAdaptor getLoggingAdaptor() {
		return loggingAdaptors.get();
	}
	
	public static void removeLoggingAdaptor() {
		loggingAdaptors.remove();
	}

	public static void addScreenshotTaker(ScreenshotTaker screenshotTaker) {
		screenshotTakers.set(screenshotTaker);
	}

	public static boolean hasScreenshotTaker() {
		return screenshotTakers.get() != null;
	}

	public static ScreenshotTaker getScreenshotTaker() {
		return screenshotTakers.get();
	}
	
	public static void removeScreenshotTaker() {
		screenshotTakers.remove();
	}
	
	public FluentLogger(Logger logger, boolean instanceofLAL) {
		this(logger, instanceofLAL, false);
	}

	public FluentLogger(Logger logger, boolean instanceofLAL, boolean buffered) {
		this.reportLoggerFQCN = FluentLogger.class.getName();
		this.logger = logger;
		this.instanceofLAL = instanceofLAL;
		this.buffered = buffered;
	}
	
	public FluentLogger htmlMessage(String format, Object... arguments) {
		addMarker(new HtmlMessageMarker(format, arguments));

		if (this.format == null && this.arguments == null) {
			// Prepare message/arguments for console and other appenders that may not like HTML
			// ... remove HTML tags from message
			this.format = format.replaceAll("<.*?>", "");

			// ... remove special HTML characters from arguments
			this.arguments = arguments.clone();

			for (int i = 0; i < this.arguments.length; i++) {
				if (this.arguments[i] instanceof String) {
					if (this.arguments[i] instanceof String) {
						this.arguments[i] = this.arguments[i].toString().replaceAll("&#.*?;", "");
					}
				}
			}
		}
		
		return this;
	}

	public FluentLogger message(String format, Object... arguments) {
		this.format = format;
		this.arguments = arguments;
		return this;
	}

	public FluentLogger data(String format, Object... arguments) {
		String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
		
		addMarker(new DataMarker(formattedMessage));
		
		return this;
	}

	public FluentLogger html(String html) {
		addMarker(new HtmlMarker(html));
		
		return this;
	}

	public FluentLogger screenshot() {
		return screenshot(getScreenshotTaker());
	}
	
	public FluentLogger screenshot(ScreenshotTaker screenshotTaker) {
		// TODO Don't like having to get logging adaptor - screenshots (and potentially data files)
		// are the only thing that need access to the adaptor.
		
		if (screenshotTaker == null) {
			throw new RuntimeException("ScreenshotTaker has not been set");
		}

		ILoggingAdaptor adaptor = getLoggingAdaptor();
		if (adaptor == null) {
			throw new RuntimeException("Logging adapter has not been set for the current thread");
		}
		
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

	public FluentLogger attachment(String input, String filename, MediaType mediaType) {
		return attachment(new ByteArrayInputStream(input.getBytes()), filename, mediaType.toString());
	}

	public FluentLogger attachment(String input, String filename, String mediaType) {
		return attachment(new ByteArrayInputStream(input.getBytes()), filename, mediaType);
	}

	public FluentLogger attachment(InputStream inputStream, String filename, MediaType mediaType) {
		return attachment(inputStream, filename, mediaType.toString());
	}
	
	public FluentLogger attachment(InputStream inputStream, String filename, String mediaType) {
		addMarker(new AttachmentMarker(getLoggingAdaptor().getLogFile().getPath(), inputStream, filename, mediaType.toString()));

		return this;
	}

	public FluentLogger marker(Marker marker) {
		addMarker(marker);
		return this;
	}

	public FluentLogger locationAwareParent(String currentClass) {
		overrideFQCN = currentClass;
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
		if (buffered) {
			bufferedLevel = LocationAwareLogger.TRACE_INT;
			return;
		}
		
		if (!logger.isTraceEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.TRACE_INT, format, arguments, null);
		} else {
			logger.trace(marker, format, arguments);
		}

		reset();
	}

	public void debug() {
		if (buffered) {
			bufferedLevel = LocationAwareLogger.DEBUG_INT;
			return;
		}
		
		if (!logger.isDebugEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.DEBUG_INT, format, arguments, null);
		} else {
			logger.debug(marker, format, arguments);
		}

		reset();
	}

	public void info() {
		if (buffered) {
			bufferedLevel = LocationAwareLogger.INFO_INT;
			return;
		}
		
		if (!logger.isInfoEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.INFO_INT, format, arguments, null);
		} else {
			logger.info(marker, format, arguments);
		}

		reset();
	}

	public void warn() {
		if (buffered) {
			bufferedLevel = LocationAwareLogger.WARN_INT;
			return;
		}
		
		if (!logger.isWarnEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.WARN_INT, format, arguments, null);
		} else {
			logger.warn(marker, format, arguments);
		}

		reset();
	}

	public void error() {
		error(null);
	}

	public void error(Throwable t) {
		if (buffered) {
			bufferedLevel = LocationAwareLogger.ERROR_INT;
			bufferedThrowable = t;
			return;
		}
		
		if (!logger.isErrorEnabled(marker)) {
			return;
		}

		prepare();

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(marker, getFQCN(), LocationAwareLogger.ERROR_INT, format, arguments, t);
		} else {
			logger.error(marker, format, arguments);
		}

		reset();
	}

	void writeBufferedEntry() {
		if (!buffered) {
			return;
		}
		
		buffered = false;
		
		switch (bufferedLevel) {
		case LocationAwareLogger.TRACE_INT:
			trace();
			break;
		
		case LocationAwareLogger.DEBUG_INT:
			debug();
			break;
			
		case LocationAwareLogger.INFO_INT:
			info();
			break;
			
		case LocationAwareLogger.WARN_INT:
			warn();
			break;
			
		case LocationAwareLogger.ERROR_INT:
			error(bufferedThrowable);
			break;
			
		default:
			throw new RuntimeException("Invalid buffer level");
		}
	}
	
	private int getMarkerChildCount(Marker reference) {
		int count = 0;
	
		if (marker == null) {
			return 0;
		}
	
		Iterator<Marker> references = reference.iterator();
		while (references.hasNext()) {
			count ++;
			references.next();
		}
		
		return count;
	}
	
	private void addMarker(Marker reference) {
		if (marker == null) {
			// Start with a detached marker so that any bound markers that are added are not accidentally reused in
			// subsequent logging statements
			marker = MarkerFactory.getDetachedMarker("FLUENT_LOGGER");
		}

		int count = getMarkerChildCount(marker);
		
		marker.add(reference);
		
		if (count == getMarkerChildCount(marker)) {
			throw new RuntimeException("Marker " + marker.getName() + " has already been added to this logging entry, duplicates markers are not allowed");
		}
	}
	
	private void prepare() {
		if (format == null) {
			// Tell logger to ignore lines with no message - we're probably just wanting to notify an extension of some event
			addMarker(ReportLoggerMarkers.PROGRESS_MARKER);
		}

		try {
			prepareData(marker);
		} catch (Exception e) {
			throw new RuntimeException("Unable to prepare log attachments: " + e.getMessage(), e);
		}
	}

	private void prepareData(Marker reference) throws Exception {

		if (reference == null) {
			return;
		}

		if (reference instanceof BaseDataMarker) {
			((BaseDataMarker<?>) reference).prepareData();
		}

		// TODO This takes screenshots and writes data to files, this probably should be done in HTMLLayout
		// so the effort is not wasted if it's not going to be used.
		Iterator<Marker> references = reference.iterator();
		while (references.hasNext()) {
			prepareData(references.next());
		}
	}

	private String getFQCN() {
		return overrideFQCN == null ? reportLoggerFQCN : overrideFQCN;
	}

	private void reset() {
		this.overrideFQCN = null;
		this.marker = null;
		this.format = null;
		this.arguments = null;
	}
}
