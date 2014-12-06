package org.concordion.ext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.concordion.ext.logging.LogMessenger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;

/**
 * Configures Logback logging to store new messages for subsequent retrieval.
 */
public class LogbackLogMessenger implements LogMessenger {
	private OutputStreamAppender<ILoggingEvent> streamAppender = null;
	private final ByteArrayOutputStream printStream;

	/**
	 * Configures loggers to store new messages. Based on JavaUtilLogMessenger.
	 * 
     * @param loggerNames a comma separated list of the names of loggers whose output is to be shown in the Concordion output. An empty string indicates the root logger.
     * @param loggingLevel the logging {@link Level} for the handler that writes to the Concordion output. Log messages of this level and
     * higher will be output.  Note that the associated {@link Logger}s must also have an appropriate logging level set.
     * @param displayRootConsoleLogging <code>false</code> to remove console output for the root logger, <code>true</code> to show the console output
 	 */
	public LogbackLogMessenger(String loggerNames, final Level loggingLevel, final boolean displayRootConsoleLogging) {
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
			
			if (!displayRootConsoleLogging) {
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
		encoder.setPattern("%msg%n");
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