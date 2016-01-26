package org.concordion.ext.loggingFormatter;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public interface ILoggingAdaptor
{
	/**
	 * Update logger so that logging statements are directed to specified file
	 * 
	 * @param testClass the test that is being run
	 */
	public void startLogFile(String fileName);
	
	/**
	 * Stop directing logging statements to test specific log file
	 */
	public void stopLogFile();
	
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