package org.concordion.ext;


import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.ext.loggingFormatter.LoggingFormatterSpecificationListener;

/**
 * Formats the footer of the Concordion specification to show a link to the log file that has been created for this test.<br><br>
 * By default this link leads to a log file viewer which attempts to format the log file for easier reading. For the log file 
 * viewer to work correctly the log file must contain the log level, if not switching the viewer off is advised.
 */
public class LoggingFormatterExtension implements ConcordionExtension {
	private final LoggingFormatterSpecificationListener listener;
	private final Resource stylesheetResource;
	
	public LoggingFormatterExtension() {
		this(true);
	}
	
	/**
	 * Constructor
	 * @param useLogFileViewer 
	 * 			Flag whether to show raw log file (false) or present the log file inside a log file viewer (true, default).
	 */
	public LoggingFormatterExtension(boolean useLogFileViewer) {
		stylesheetResource = new Resource("/font-awesome/css/font-awesome.css");
		listener = new LoggingFormatterSpecificationListener(new LogbackAdaptor(), stylesheetResource, useLogFileViewer);
	}

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(listener);
		concordionExtender.withExampleListener(listener);
		concordionExtender.withThrowableListener(listener);
		
		String path = LoggingFormatterExtension.class.getPackage().getName();
		path = path.replaceAll("\\.", "/");
		path = "/" + path;

		concordionExtender.withLinkedCSS("/font-awesome-4.6.3/css/font-awesome.css", stylesheetResource);
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.eot", new Resource("/font-awesome/fonts/fontawesome-webfont.eot"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.svg", new Resource("/font-awesome/fonts/fontawesome-webfont.svg"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.ttf", new Resource("/font-awesome/fonts/fontawesome-webfont.ttf"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff", new Resource("/font-awesome/fonts/fontawesome-webfont.woff"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff2", new Resource("/font-awesome/fonts/fontawesome-webfont.woff2"));
		concordionExtender.withResource("/font-awesome-4.6.3/fonts/FontAwesome.otf", new Resource("/font-awesome/fonts/FontAwesome.otf"));
	}
	
	/**
	 * If set to true will log the start and end of each example using the header of the current example if found, or the example name
	 * 
	 * @param value Value to set
	 * @return A self reference
	 */
	public LoggingFormatterExtension setLogExampleStartAndEnd(boolean value) {
		listener.setLogExampleStartAndEnd(value);
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
	    
	/**
	 * How to split the logs.
	 * 
	 * @param split Setting
	 * @return A self reference
	 */
	public LoggingFormatterExtension setSplitBy(Split split) {
		listener.setSplitBy(split);
		return this;
	}

	public enum Split {
		EXAMPLE, SPECIFICATION;
	}

    public enum LogLevel {
    	/** Do not log exceptions */
    	NONE, 
    	
		/** Log exception message */
    	EXCEPTION, 
    	
		/** Log exception message of the exception and all its causes (Default) */
		EXCEPTION_CAUSES
    }


}
