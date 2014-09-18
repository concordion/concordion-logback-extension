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
	 * Configures a {@link Handler} to store new messages. Based on JavaUtilLogMessenger.
	 * 
	 * @param loggerNames
	 * @param loggingLevel
	 * @param displayRootConsoleLogging
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
		}

		if (!displayRootConsoleLogging) {
			removeRootConsoleHandler();
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

		streamAppender = new OutputStreamAppender<>();
		streamAppender.setName("OutputStream Appender");
		streamAppender.setContext(lc);
		streamAppender.setEncoder(encoder);
		streamAppender.setOutputStream(printStream);
		streamAppender.addFilter(filter);
		streamAppender.start();

		return streamAppender;
	}

	private void removeRootConsoleHandler() {
		// TODO This has never been tested...
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

		for (Logger logger : root.getLoggerContext().getLoggerList()) {
			for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
				Appender<ILoggingEvent> appender = index.next();

				if (appender.getClass().isAssignableFrom(ConsoleAppender.class)) {
					root.detachAppender(appender);
				}
			}
		}
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