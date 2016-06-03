package org.concordion.ext.loggingFormatter;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
	public static final String TEST_NAME = "testname";
	private static Stack<String> testStack = new Stack<String>();
	
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
	public void startLogFile(String fileName) {
		testStack.push(fileName);
		
		MDC.put(TEST_NAME, fileName);
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
		
		if(!testStack.isEmpty()) {
			MDC.put(TEST_NAME, testStack.peek());
		} else {
			MDC.remove(TEST_NAME);
		}
	}
		
	@Override
	public boolean doesLogfileExist() {
		String name = MDC.get(TEST_NAME);

		FileAppender<?> appender = getConfiguredAppender();
		if (appender == null) {
			return false;
		}
		
		String file = appender.getFile();
		
		// TODO need to test running tests in parallel
		if (!file.startsWith(name)) {
			System.err.println("MDC out of sync with appenders!");
		}
		
		return new File(file).exists();
	}
	
	@Override
	public String getLogName() {
		String name = MDC.get(TEST_NAME);

		FileAppender<?> appender = getConfiguredAppender();
		return new File(appender.getFile()).getName();
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
	
	/** Finds the first configured appender that we use to create the log file. */
	public static FileAppender<?> getConfiguredAppender() {
		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		for (Logger logger : context.getLoggerList())
		{
			for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
				Object enumElement = index.next();
				if (enumElement instanceof SiftingAppender) {
					SiftingAppender sift = (SiftingAppender)enumElement;

					if (sift.getName().equals("HTML-FILE-PER-TEST") || sift.getName().equals("FILE-PER-TEST")) {
						
						List<Appender<ILoggingEvent>> activeAppenders = (List<Appender<ILoggingEvent>>)sift.getAppenderTracker().allComponents();
						ListIterator<Appender<ILoggingEvent>> li = activeAppenders.listIterator(activeAppenders.size());

						// Iterate in reverse.
						while(li.hasPrevious()) {
							Appender<ILoggingEvent> appender = li.previous();
							if (appender instanceof FileAppender) {
								return (FileAppender<?>)appender;
							}
						  
						}
					}
				}
			}
		}

		return null;
	}
}