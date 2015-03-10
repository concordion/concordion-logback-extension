package org.concordion.ext.loggingFormatter;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public interface ILoggingAdaptor
{
	/**
	 * Adds the test name to MDC so that sift appender can use it and log the new log events to a different file
	 * 
	 * @param testClass the test that is being run
	 */
	//public void startTestLogging(final Object testClass);
	
	/**
	 * If running tests sequentially (Concordion's default) then updates the MDC with the name of the previous test to handle tests calling
	 * other tests using the Concordion Run command.  
	 * 
	 * If running tests in parallel then this call is essentially redundant as tests started using the Concordion Run command will start on 
	 * a new thread and MDC maintains a value per thread.
	 */
	//public void stopTestLogging();

	/**
	 * Checks to see if a log file has been created for this test
	 * 
	 * @return true or false
	 */
	public boolean doesLogfileExist();
	
	/**
	 * Gets the key (log file name and path) from MDC and extracts the class name
	 * 
	 * @return name of the log file, if one existed in MDC
	 */
	public String getLogName();
	
	/**
	 * Gets the key (log file name and path) from MDC
	 * 
	 * @return name of the log file, if one existed in MDC
	 */
	public String getLogPath();
}