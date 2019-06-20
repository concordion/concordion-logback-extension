package org.concordion.slf4j;

import java.io.File;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps us log each test case into it's own log file. 
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">Sifting Appender</a>
 * @see <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>
 */
public interface ILoggingAdaptor
{
	/* Windows 7 Max filename length */
	public int MAX_FILENAME_LENGTH = 129;

	public int MAX_SPECIFICATION_NAME_LENGTH = 60;
	public int MAX_EXAMPLE_NAME_LENGTH = 40;
	public int MAX_ATTACHMENT_NAME_LENGTH = 29;

	/**
	 * Update logger so that logging statements are directed a file. Alternative to {@link #startSpecificationLogFile(String)} when not
	 * running as a Concordion test.
	 *
	 * @param logPath Full path of the desired log file
	 */
	public void startLogFile(String logPath);
	
	/**
	 * Update logger so that logging statements are directed a file
	 *
	 * @param resourcePath Resource path of the current test (ie without base output directory)
	 */
	public void startSpecificationLogFile(String resourcePath);
	
	/**
	 * Update logger so that logging statements for a specific example are directed to specified file
	 * 
	 * @param testPath Path of the current test
	 * @param exampleName Name of the current example 
	 */
	public void startExampleLogFile(String resourcePath, String exampleName);

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
     * @return the log file, if one exists otherwise a new file object with exists set to false.
     */
	public File getLogFile();
	
	public File getBaseOutputDir();
}