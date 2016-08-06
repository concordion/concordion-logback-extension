package org.concordion.ext;


import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.ILoggingAdaptor;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.MDCFilter;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import test.concordion.logback.LoggingListener;
import test.concordion.logback.LogFilter;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private final LoggingFormatterSpecificationListener listener;
	private final Resource stylesheetResource;
	
	/**
	 * Constructor - defaults to using LogbackAdaptor.
	 * 
	 * @param useLogFileViewer
	 *            Flag whether to show raw log file (false) or present the log file inside a log file viewer (true, default).
	 */
	public LoggingFormatterExtension() {
		this(new LogbackAdaptor());
	}

	/**
	 * Constructor.
	 * 
	 * @param loggingAdaptor Custom logging adaptor
	 * @param useLogFileViewer Flag whether to show raw log file (false) or present the log file inside a log file viewer (true, default).
	 */
	public LoggingFormatterExtension(ILoggingAdaptor loggingAdaptor) {
		stylesheetResource = new Resource("/font-awesome/css/font-awesome.css");
		listener = new LoggingFormatterSpecificationListener(loggingAdaptor, stylesheetResource);
	}

	public ILoggingAdaptor getLoggingAdaptor() {
		return listener.getLoggingAdaptor();
	}

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withExampleListener(listener);
		concordionExtender.withThrowableListener(listener);
		
		String path = LoggingFormatterExtension.class.getPackage().getName();
		path = path.replaceAll("\\.", "/");
		path = "/" + path;

		concordionExtender.withLinkedCSS("/font-awesome-4.6.3/css/font-awesome.css", stylesheetResource);
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.eot", new Resource("/font-awesome/fonts/fontawesome-webfont.eot"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.svg", new Resource("/font-awesome/fonts/fontawesome-webfont.svg"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.ttf", new Resource("/font-awesome/fonts/fontawesome-webfont.ttf"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff", new Resource("/font-awesome/fonts/fontawesome-webfont.woff"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff2", new Resource("/font-awesome/fonts/fontawesome-webfont.woff2"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/FontAwesome.otf", new Resource("/font-awesome/fonts/FontAwesome.otf"));
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
	 * @param logListener Log listener to register
	 * @return A self reference
	 */
	public LoggingFormatterExtension registerListener(LoggingListener logListener) {
		if (logListener instanceof FilterAttachable<?>) {
			LogFilter filter = new LogFilter(logListener.getFilterMarkers());
			
			if (logListener.getFilterMarkers() != null) {
				((FilterAttachable<ILoggingEvent>) logListener).addFilter(filter);
			}
			
			filter.setMDCKey(listener.getLoggingAdaptor().getMDCKey());
			filter.setMDCValue(listener.getLoggingAdaptor().getMDCValue());
			
		}

		logListener.start();

		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(logListener);
		logger.setLevel(Level.ALL);
		logger.setAdditive(true);

		return this;
	}
}
