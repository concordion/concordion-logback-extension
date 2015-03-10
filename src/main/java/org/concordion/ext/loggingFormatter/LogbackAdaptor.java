package org.concordion.ext.loggingFormatter;

import java.io.File;
import java.util.Stack;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public class LogbackAdaptor implements ILoggingAdaptor
{
	public static final String TEST_NAME = "testname";
	private static final String PROPERTY_OUTPUT_DIR = "concordion.output.dir";
	private static Stack<String> testStack = new Stack<String>();
	
	/**
	 * print logback's internal status
	 */
	public static void logInternalStatus() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
	}

	/**
	 * Gets the base output folder used by concordion - copied from ConcordionBuilder.getBaseOutputDir()
	 * 
	 * @return base output folder 
	 */
	private static String getConcordionBaseOutputDir() {
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

	public static void startTestLogging(final Object testClass) {
		String testName = getConcordionBaseOutputDir() + testClass.getClass().getName().replace(".", "/");
		
		testStack.push(testName);
		MDC.put(TEST_NAME, testName);
	}


	public static void stopTestLogging() {
		testStack.pop();
				
		if(!testStack.isEmpty()) {
			MDC.put(TEST_NAME, testStack.peek());
		}
	}
	
	@Override
	public boolean doesLogfileExist() {
		String name = MDC.get(TEST_NAME);

		if (name == null) {
			return false;
		}
		
		return new File(name + ".log").exists();
	}
	
	@Override
	public String getLogName() {
		String name = MDC.get(TEST_NAME);

		if (name == null) {
			return "";
		}	
		
		return name.substring(name.lastIndexOf("/") + 1) + ".log";
	}

	@Override
	public String getLogPath() {
		String path = MDC.get(TEST_NAME);

		if (path == null) {
			return "";
		}		

		int index = path.lastIndexOf("/");

		path = (index > 0) ? path.substring(0, index) : "";

		if (!path.endsWith("/")) {
			path = path + "/";
		}

		return path;
	}
}