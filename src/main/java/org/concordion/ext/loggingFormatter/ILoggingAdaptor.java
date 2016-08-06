package org.concordion.ext.loggingFormatter;

import java.io.File;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public interface ILoggingAdaptor
{
	/**
	 * Update logger so that logging statements are directed a file
	 * 
	 * @param fileName Full path to the output folder and class (without the file extension) of the current specification
	 */
	public void startSpecificationLogFile(String resourcePath, String stylesheet);
	
	/**
	 * Update logger so that logging statements for a specific example are directed to specified file
	 * 
	 * @param fileName Full path to the output folder and class (without the file extension) of the current specification
	 */
	public void startExampleLogFile(String testPath, String exampleName);

	/**
	 * Stop directing logging statements to test specific log file
	 */
	public void stopLogFile();
	
	/**
	 * Checks to see if a log file has been created for this test
	 * 
	 * @return true or false
	 */
	public boolean logFileExists();
	
	/**
	 * Return the file for the currently active log.
	 * 
	 * @return the log file, if one exists otherwise null
	 */
	public File getLogFile();

	public String getMDCKey();

	public String getMDCValue();

}