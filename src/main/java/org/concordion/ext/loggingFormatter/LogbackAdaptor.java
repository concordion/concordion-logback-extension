package org.concordion.ext.loggingFormatter;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public class LogbackAdaptor implements ILoggingAdaptor
{
	public static final String LAYOUT_STYLESHEET = "LAYOUT_STYLESHEET";
	public static final String TEST_NAME = "testname";
	public static final String EXAMPLE_SEPERATOR_PREFIX = "[";
	public static final String EXAMPLE_SEPERATOR_SUFFIX = "]";

	private static Stack<String> testStack = new Stack<String>();
	private static String baseFolder = getConcordionBaseOutputDir();
	
	/**
	 * print logback's internal status
	 */
	public static void logInternalStatus() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
	}

	/**
	 * Adds the test name to MDC so that sift appender can use it and log the new log events to a different file
	 * 
	 * @param fileName The full path to the required log file
	 */
	@Override
	public void startSpecificationLogFile(String resourcePath, String stylesheet) {
		String path = baseFolder + getPath(resourcePath);

		// Add path to css style sheet to logger context for later use
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.putProperty(LAYOUT_STYLESHEET, stylesheet);
		
		testStack.push(path);

		MDC.put(TEST_NAME, path);
	}

	@Override
	public void startExampleLogFile(String resourcePath, String exampleName) {
		String path = baseFolder + getPath(resourcePath) + EXAMPLE_SEPERATOR_PREFIX + exampleName + EXAMPLE_SEPERATOR_SUFFIX;

		testStack.push(path);
		
		MDC.put(TEST_NAME, path);
	}
	
	/**
	 * If running tests sequentially (Concordion's default) then updates the MDC with the name of the previous test to handle tests calling
	 * other tests using the Concordion Run command.  
	 * 
	 * If running tests in parallel then this call is essentially redundant as tests started using the Concordion Run command will start on 
	 * a new thread and MDC maintains a value per thread.
	 */
	@Override
	public void stopLogFile() {
		testStack.pop();
		
		if (testStack.isEmpty()) {
			MDC.remove(TEST_NAME);
		} else {
			MDC.put(TEST_NAME, testStack.peek());
		}
	}
		
	@Override
	public boolean logFileExists() {
		File file = getLogFile();

		if (file == null) {
			return false;
		}
		
		return file.exists();
	}

	@Override
	public File getLogFile() {
		FileAppender<?> appender = getConfiguredAppender();

		if (appender == null) {
			return null;
		}

		return new File(appender.getFile());
	}

	/** Finds the first appender matching the MDC value. */
	private static FileAppender<?> getConfiguredAppender() {

		String currentTest = MDC.get(TEST_NAME);

		if (currentTest == null || currentTest.isEmpty()) {
			return null;
		}

		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		for (Logger logger : context.getLoggerList())
		{
			for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
				Appender<ILoggingEvent> outerAppender = index.next();
				
				if (outerAppender instanceof SiftingAppender) {
					if (outerAppender.getName().equals("HTML-FILE-PER-TEST") || outerAppender.getName().equals("FILE-PER-TEST")) {
						for (Appender<ILoggingEvent> appender : ((SiftingAppender) outerAppender).getAppenderTracker().allComponents()) {
							if (appender instanceof FileAppender) {
								FileAppender<?> fileAppender = (FileAppender<?>) appender;
								String file = fileAppender.getFile();
								
								if (file.startsWith(currentTest)) {
									if (file.length() > currentTest.length()) {
										// If no log statements performed then appender won't be created so ensure not accidentally picking up
										// example log file when after specification log file
										if (!file.substring(currentTest.length(), currentTest.length() + 1).equals(EXAMPLE_SEPERATOR_PREFIX)) {
											return fileAppender;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets the base output folder used by concordion - copied from ConcordionBuilder.getBaseOutputDir()
	 * 
	 * @return base output folder
	 */
	private static String getConcordionBaseOutputDir() {
		String outputPath = System.getProperty("concordion.output.dir");

		if (outputPath == null) {
			outputPath = new File(System.getProperty("java.io.tmpdir"), "concordion").getAbsolutePath();
		}

		outputPath = outputPath.replaceAll("\\\\", "/");
		if (!outputPath.endsWith("/")) {
			outputPath = outputPath + "/";
		}
		return outputPath;
	}

	private String getPath(String resourcePath) {
		if (resourcePath.indexOf(".") > 0) {
			resourcePath = resourcePath.substring(0, resourcePath.indexOf("."));
		}

		if (resourcePath.startsWith("/") || resourcePath.startsWith("\\")) {
			resourcePath = resourcePath.substring(1);
		}

		return resourcePath;
	}

}