package org.concordion.ext;


import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;
import org.concordion.logback.LogbackAdaptor;
import org.concordion.logback.LoggingListener;
import org.concordion.logback.filter.MarkerFilter;
import org.concordion.slf4j.ILoggingAdaptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private final LoggingFormatterSpecificationListener listener;
	
	/**
	 * Constructor - defaults to using LogbackAdaptor.
	 */
	public LoggingFormatterExtension() {
		this(new LogbackAdaptor());
	}

	/**
	 * Constructor.
	 * 
	 * @param loggingAdaptor Custom logging adaptor
	 */
	public LoggingFormatterExtension(ILoggingAdaptor loggingAdaptor) {
		listener = new LoggingFormatterSpecificationListener(loggingAdaptor);
	}

	public ILoggingAdaptor getLoggingAdaptor() {
		return listener.getLoggingAdaptor();
	}

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withExampleListener(listener);
		concordionExtender.withThrowableListener(listener);
		concordionExtender.withAssertEqualsListener(listener);
		concordionExtender.withAssertTrueListener(listener);
		concordionExtender.withAssertFalseListener(listener);

		String path = LoggingFormatterExtension.class.getPackage().getName();
		path = path.replaceAll("\\.", "/");
		path = "/" + path;
	}
	
	/**
	 * If set to true will show the an html based log file view of the classic test logs, this setting will be ignored if using the HTML Log.
	 *  
	 * @param useLogFileViewer Value to set
	 * @return A self reference
	 */
	public LoggingFormatterExtension setUseLogFileViewer(boolean useLogFileViewer) {
		listener.setUseLogFileViewer(useLogFileViewer);
		return this;
	}
	
	/**
	 * Registers listeners for other extensions to listen in on log messages.
	 *  
	 * <p>NOTE: In order to correctly filter out log messages from other tests that might be running 
	 * in parallel it automatically filters out log messages not originating from the current thread.</p>
	 *  
	 * @param logListener Log listener to register
	 * @return A self reference
	 */
	public LoggingFormatterExtension registerListener(LoggingListener logListener) {
		if (logListener.getHandleFailureAndThrowableEvents()) {
			listener.registerMarker(logListener.getConcordionEventMarker());
		} else {
			listener.setHandleFailureAndThrowableEvents(false);
		}

		if (logListener instanceof FilterAttachable<?>) {
			MarkerFilter filter = new MarkerFilter();
			
			filter.setThread(Thread.currentThread().getName());

			filter.setMarkers(logListener.getFilterMarkers());
			filter.setOnMismatch(FilterReply.DENY);

			filter.start();
			
			logListener.addFilter(filter);
		}

		logListener.start();

		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(logListener);
		logger.setLevel(Level.ALL);
		logger.setAdditive(true);

		return this;
	}
	
	/**
	 * Set the screenshot taker to use when adding screenshot.  If using the Logging extension
	 * to add screenshots then this does not need to be set.
	 * 
	 * @param screenshotTaker Screenshot taker
	 * @return A self reference
	 */
	public LoggingFormatterExtension setScreenshotTaker(ScreenshotTaker screenshotTaker) {
		listener.setScreenshotTaker(screenshotTaker);
		
		return this;
	}
}
