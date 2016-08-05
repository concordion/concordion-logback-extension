package test.concordion.logback;

import java.util.Iterator;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.ext.html.HTMLLayout;

public class LogBackHelper {
	

	public static Logger getRootLogger() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		return context.getLogger(Logger.ROOT_LOGGER_NAME);
	}

	public static SiftingAppender getHtmlFilePerTestSiftingAppender() {
		Logger logger = getRootLogger();

		for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
			Appender<ILoggingEvent> appender = index.next();

			if (appender instanceof SiftingAppender) {
				if (appender.getName().equals("HTML-FILE-PER-TEST")) {
					return (SiftingAppender) appender;
				}
			}
		}

		throw new IllegalStateException("HTML-FILE-PER-TEST sifting appender is not configured");
	}

	public static FileAppender<?> getHtmlFileAppender() {
		SiftingAppender siftingAppender = getHtmlFilePerTestSiftingAppender();

		for (Appender<ILoggingEvent> appender : siftingAppender.getAppenderTracker().allComponents()) {
			if (appender instanceof FileAppender) {
				return (FileAppender<?>) appender;
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
}
