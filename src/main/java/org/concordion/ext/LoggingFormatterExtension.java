package org.concordion.ext;


import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.ILoggingAdaptor;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;
import org.concordion.logback.LoggingListener;
import org.concordion.logback.MarkerFilter;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterAttachable;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private final LoggingFormatterSpecificationListener listener;
//	private final Resource stylesheetResource;
	
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
		//stylesheetResource = new Resource("/font-awesome/css/font-awesome.css");
		listener = new LoggingFormatterSpecificationListener(loggingAdaptor, null);
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

		//concordionExtender.withLinkedCSS("/font-awesome-4.6.3/css/font-awesome.css", stylesheetResource);
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
		listener.registerThrowableCaughtMarker(logListener.getThrowableCaughtMarker());
		listener.registerFailureReportedMarker(logListener.getFailureReportedMarker());

		if (logListener instanceof FilterAttachable<?>) {
			MarkerFilter filter = new MarkerFilter();
			
			filter.setFilterMarkers(logListener.getFilterMarkers());
			filter.setThread(Thread.currentThread().getName());
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
	
	public LoggingFormatterExtension setScreenshotTaker(ScreenshotTaker screenshotTaker) {
		listener.setScreenshotTaker(screenshotTaker);
		
		return this;
	}
}
