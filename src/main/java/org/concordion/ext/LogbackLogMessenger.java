package org.concordion.ext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.concordion.ext.logging.LogMessenger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

/**
 * Configures a @link OutputStreamAppender to store new log messages from Logback 
 * for delivery to @Link LoggingTooltipExtension
 */
public class LogbackLogMessenger implements LogMessenger {
	private OutputStreamAppender<ILoggingEvent> streamAppender = null;
	private final ByteArrayOutputStream printStream;
	private final String tooltipPattern;

	/**
	 * Configures loggers to store new messages. Based on JavaUtilLogMessenger.
	 * 
     * @param loggerNames a comma separated list of the names of loggers whose output is to be shown in the Concordion output. An empty string indicates the root logger.
     * @param loggingLevel the logging {@link Level} for the handler that writes to the Concordion output. Log messages of this level and
     * higher will be output.  Note that the associated {@link Logger}s must also have an appropriate logging level set.
     * @param isAdditive <code>false</code> to prevent other loggers/appenders displaying (eg console) displaying tooltip output, <code>true</code> to show the
     * 					 output in other loggers/appenders as their filters allow.  Use carefully as can disable all logging if applied to root/test specific logger.
 	 */
	public LogbackLogMessenger(String loggerNames, final Level loggingLevel, final boolean isAdditive) {
		this(loggerNames, loggingLevel, isAdditive, "[%d{h:mm:ss.SSS}] %msg%n");
	}

	/**
	 * Configures loggers to store new messages.
	 * 
	 * @param loggerNames a comma separated list of the names of loggers whose output is to be shown in the Concordion output. An empty string indicates the root logger.
	 * @param loggingLevel the logging {@link Level} for the handler that writes to the Concordion output. Log messages of this level and
     * 					   higher will be output.  Note that the associated {@link Logger}s must also have an appropriate logging level set.
     * @param isAdditive <code>false</code> to prevent other loggers/appenders displaying (eg console) displaying tooltip output, <code>true</code> to show the
     * 					 output in other loggers/appenders as their filters allow.  Use carefully as can disable all logging if applied to root/test specific logger.
 	 * @param tooltipPattern sets the pattern used to format the tooltip logs.  Any valid logback pattern can be used. <i>Is optional.</i>
	 */
	public LogbackLogMessenger(String loggerNames, final Level loggingLevel, final boolean isAdditive, String tooltipPattern) {
		this.tooltipPattern = tooltipPattern;
		
		printStream = new ByteArrayOutputStream(4096);

		if (loggerNames.isEmpty()) {
			loggerNames = Logger.ROOT_LOGGER_NAME;
		}

		for (String loggerName : loggerNames.split(",")) {
			Logger logger = (Logger) LoggerFactory.getLogger(loggerName.trim());
			
			if (streamAppender == null) {
				streamAppender = getNewAppender(logger.getLoggerContext(), loggingLevel);
			}

			logger.addAppender(streamAppender);
			
			if (!isAdditive) {
				logger.setAdditive(false);
			}		
		
		}
		
	}
	
	private OutputStreamAppender<ILoggingEvent> getNewAppender(final LoggerContext lc, final Level loggingLevel) {
		OutputStreamAppender<ILoggingEvent> streamAppender;

		ThresholdFilter filter = new ThresholdFilter();
		filter.setLevel(loggingLevel.toString());
		filter.start();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(lc);
		encoder.setPattern(this.tooltipPattern);
		encoder.start();

		streamAppender = new OutputStreamAppender<ILoggingEvent>();
		streamAppender.setName("OutputStream Appender");
		streamAppender.setContext(lc);
		streamAppender.setEncoder(encoder);
		streamAppender.setOutputStream(printStream);
		streamAppender.addFilter(filter);
		streamAppender.start();

		return streamAppender;
	}

	@Override
	public String getNewLogMessages() {
		try {
			streamAppender.getOutputStream().flush();
		} catch (IOException e) {
			// Ignore this exception
		}
		
		String text = printStream.toString();
		printStream.reset();

		return text;
	}
}