package org.concordion.ext;


import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;
import org.concordion.logback.StepRecorder;

import ch.qos.logback.classic.Level;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private final LoggingFormatterSpecificationListener listener;
	
	public LoggingFormatterExtension() {
		this(true);
	}
	
	/**
	 * Constructor
	 * @param useLogFileViewer 
	 * 			Flag whether to show raw log file (false) or present the log file inside a log file viewer (true, default).
	 */
	public LoggingFormatterExtension(boolean useLogFileViewer) {
		listener = new LoggingFormatterSpecificationListener(new LogbackAdaptor(), useLogFileViewer);
	}

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withExampleListener(listener);
		concordionExtender.withThrowableListener(listener);
	}
	
	/**
	 * If set to true will add an entry to the log file with the header of the example that is about to be run
	 * 
	 * @param value Value to set
	 * @return A self reference
	 */
	public LoggingFormatterExtension setLogExampleStartEvent(boolean value) {
		listener.setLogExample(value);
		return this;
	}
	
	/**
	 * If set to true will log any exceptions not handled by the test fixture
	 * 
	 * @param value Value to set
	 * @return A self reference
	 */
	public LoggingFormatterExtension setLogExceptions(LogLevel value) {
		listener.setLogExceptions(value);
		return this;
	}
	    
    public enum LogLevel {
    	/** Do not log exceptions */
    	NONE, 
    	
    	/** Log exception message */
    	EXCEPTION, 
    	
    	/** Log exception message of the exception and all its causes (Default) */
    	EXCEPTION_CAUSES, 
    	
    	/** Log full stack trace */
    	EXCEPTION_WITH_STACK_TRACE
    }
}
