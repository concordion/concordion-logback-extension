package test.concordion.logback;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import ch.qos.logback.ext.html.HTMLLayout;

public class LogBackHelper {
	private static String HTML_FILE_APPENDER = "HTML-FILE-PER-TEST";
	private static String TEXT_FILE_APPENDER = "FILE-PER-TEST";

	public static Logger getRootLogger() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		return context.getLogger(Logger.ROOT_LOGGER_NAME);
	}

	public static SiftingAppender getHtmlFileSiftingAppender() {
		return getSiftingAppender(HTML_FILE_APPENDER);
	}

	public static SiftingAppender getTextFileSiftingAppender() {
		return getSiftingAppender(TEXT_FILE_APPENDER);
	}

	private static SiftingAppender getSiftingAppender(String name) {
		Logger logger = getRootLogger();

		return (SiftingAppender) logger.getAppender(name);

		// for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
		// Appender<ILoggingEvent> appender = index.next();
		//
		// if (appender instanceof SiftingAppender) {
		// if (appender.getName().equals(name)) {
		// return (SiftingAppender) appender;
		// }
		// }
		// }
		//
		// return null;
	}

	public static FileAppender<?> getHtmlFileAppender() {
		SiftingAppender siftingAppender = getHtmlFileSiftingAppender();

		if (siftingAppender != null) {
			for (Appender<ILoggingEvent> appender : siftingAppender.getAppenderTracker().allComponents()) {
				if (appender instanceof FileAppender) {
					return (FileAppender<?>) appender;
				}
			}
		}

		throw new IllegalStateException("HTML-FILE-PER-TEST file appender is not configured");
	}

	public static HTMLLayout getHtmlLayout() {
		FileAppender<?> fileAppender = getHtmlFileAppender();

		if (fileAppender.getEncoder() instanceof LayoutWrappingEncoder<?> == false) {
			throw new IllegalStateException("HTML-FILE-PER-TEST layout is not configured");
		}

		LayoutWrappingEncoder<?> encoder = (LayoutWrappingEncoder<?>) fileAppender.getEncoder();

		if (encoder.getLayout() instanceof HTMLLayout == false) {
			throw new IllegalStateException("HTML-FILE-PER-TEST layout is not configured");
		}

		return (HTMLLayout) encoder.getLayout();
	}

	private static void copy(HTMLLayout src, HTMLLayout dest) {
		src.setStylesheet(dest.getStylesheet());
		src.setFormat(dest.getFormat());
		src.setPattern(dest.getPattern());
		src.setStepRecorder(dest.getStepRecorder());
	}

	public static HTMLLayout backupLayout(HTMLLayout orig) {
		HTMLLayout backup = new HTMLLayout();
		copy(orig, backup);

		return backup;
	}

	public static void restoreLayout(HTMLLayout backup, HTMLLayout orig) {
		copy(backup, orig);
	}

	public static boolean isHtmlLoggerConfigured() {
		return getHtmlFileSiftingAppender() != null;
	}

	public static void switchToClassicLoggerConfiguration() {
		StringBuilder sb = new StringBuilder();

		sb.append("<configuration>");
		sb.append("  <include resource=\"logback-include.xml\"/>");
		sb.append("  <root level=\"ALL\">");
		sb.append("    <appender-ref ref=\"STDOUT\" />");
		sb.append("    <appender-ref ref=\"FILE-PER-TEST\" />");
		sb.append("  </root>");
		sb.append("</configuration>");

		InputStream stream = new ByteArrayInputStream(sb.toString().getBytes());
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		ContextInitializer ci = new ContextInitializer(loggerContext);

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
			loggerContext.reset();
			configurator.doConfigure(stream);
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}

		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}

	public static void restoreLoggerConfiguration() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		ContextInitializer ci = new ContextInitializer(loggerContext);
		URL url = ci.findURLOfDefaultConfigurationFile(true);

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
			loggerContext.reset();
			configurator.doConfigure(url);
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}

		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}
}
