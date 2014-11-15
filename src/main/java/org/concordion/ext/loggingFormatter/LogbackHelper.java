package org.concordion.ext.loggingFormatter;

import java.io.File;
import java.util.Iterator;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public class LogbackHelper
{
	public static final String TEST_NAME = "testname";
	private static Boolean isLoggingFilePerTest = null;
	private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";

	/**
	 * print logback's internal status
	 */
	public static void logInternalStatus() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
	}

	/**
	 * Gets the base output folder used by concordion - copied from ConcordionBuilder.getBaseOutputDir()
	 */
	public static String getConcordionBaseOutputDir() {
		String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
		if (outputPath == null) {
			outputPath = new File(System.getProperty("java.io.tmpdir"), "concordion").getAbsolutePath();
		}

		outputPath = outputPath.replaceAll("\\\\", "/");
		if (!outputPath.endsWith("/")) {
			outputPath = outputPath + "/";
		}
		return outputPath;
	}

	/**
	 * Check if logging per test has been enabled
	 * 
	 * @return true or false
	 */
	public static boolean isLoggingFilePerTest() {
		if (isLoggingFilePerTest == null) {
			setIsLoggingFilePerTest();
		}
		return isLoggingFilePerTest;
	}

	/**
	 * If a SiftingAppender is enabled and it's discriminator key is "testname" then we have a match
	 */
	private static void setIsLoggingFilePerTest() {
		isLoggingFilePerTest = false;
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		for (ch.qos.logback.classic.Logger logger1 : lc.getLoggerList()) {
			for (Iterator<Appender<ILoggingEvent>> index = logger1.iteratorForAppenders(); index.hasNext();) {
				Appender<ILoggingEvent> appender = index.next();

				if (appender.getClass().isAssignableFrom(SiftingAppender.class)) {
					SiftingAppender sift = (SiftingAppender) appender;

					if (sift.getDiscriminatorKey().equals(TEST_NAME)) {
						isLoggingFilePerTest = true;
						return;
					}
				}
			}
		}
	}

	/**
	 * Adds the test name to MDC so that sift appender can use it and log the new log events to a different file
	 */
	public static void startTestLogging(final Object testClass) {
		String baseDir = getConcordionBaseOutputDir();
		String testClassName = testClass.getClass().getName();

		MDC.put(TEST_NAME, baseDir + testClassName.replace(".", "/"));
	}

	/**
	 * Removes the key (log file name) from MDC
	 */
	public static String stopTestLogging() {
		String name = MDC.get(TEST_NAME);
		MDC.remove(TEST_NAME);
		return name;
	}

	/**
	 * Gets the key (log file name and path) from MDC and extracts the class name
	 * 
	 * @return name of the log file, if one existed in MDC
	 */
	public static String getTestClassName() {
		String name = MDC.get(TEST_NAME);

		return name.substring(name.lastIndexOf("/") + 1);
	}

	/**
	 * Gets the key (log file name and path) from MDC
	 * 
	 * @return name of the log file, if one existed in MDC
	 */
	public static String getTestPath() {
		String path = MDC.get(TEST_NAME);

		int index = path.lastIndexOf("/");

		path = (index > 0) ? path.substring(0, index) : "";

		if (!path.endsWith("/")) {
			path = path + "/";
		}

		return path;
	}
}