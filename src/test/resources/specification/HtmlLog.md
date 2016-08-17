# HTML Logging / Reporting

Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want and the context of what is being logged is often lacking.

The goals of the HTML based logs are to:

* Allow adding text based data, html data, screenshots, and exceptions easily
* Integrate with logging framework with minimal changes - ie stick with an SLF4J based logging interface and provide a Logback implementation
* Provide a platform to integrate with other extensions such as tooltip, storyboard and screenshot giving is a common interface for interacting with these extensions
* Support 'location aware' logging so class and line number information can be included in the logs 

The implementation is based around [SLF4J Extensions](http://slf4j.org/extensions.html) and provides a custom [layout](http://logback.qos.ch/manual/layouts.html) and [appender](http://logback.qos.ch/manual/appenders.html) for the LogBack logging framework.

Advanced logging features such as recording steps, screenshots and data are enabled by the use of [Markers](http://www.slf4j.org/apidocs/org/slf4j/Marker.html) (there is some more information on markers buried in the LogBack manuals chapter on [filters](http://logback.qos.ch/manual/filters.html)).  

Before using this the logger, LogBack must be configured.  See [LogBack Configuration](LogBackConfiguration.html) for more information.


## Usage
---

When the logger is configured to [use the HTML appender](- "c:assertTrue=isHtmlAppenderConfigured()") you can continue to use the [standard logger](- "c:assertTrue=canUseClassicLogger()") to log to the HTML log file.  However, to use the advanced logging features you will need to use the ReportLogger which can be setup as follows:   

<div><pre concordion:set="#fixture">
import org.slf4j.ext.ReportLogger;
import org.slf4j.ext.ReportLoggerFactory;

public class Test {
    private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(Test.class);
    
    public void logSomething() {
        LOGGER.debug("Log a value");
    }
}
</pre></div>

This will append the entry [Log a value](- "c:assertTrue=canUseReportLogger(#fixture, #TEXT)") to the log file.

To aid in the creation of a log entry the logging API allows you to chain a number of methods together before telling it at what level to perform the logging by using the `with()` method on the logger. 

### Text Message
Plain text log entries are still supported and use the same SLF4J format for passing arguments.

<div><pre concordion:set="#fixture">
LOGGER.with()
	.message("This is a &lt;TEXT&gt; {}", "message")
	.debug();
</pre></div>

This will add a debug level entry in the HTML log with the message [This is a &amp;lt;TEXT&amp;gt; message](- "?=getLogMessage(#fixture)").  
	
### HTML Message
The log entry can often be improved with a little HTML.

<div><pre concordion:set="#fixture">
LOGGER.with()
	.htmlMessage("&lt;b&gt;This is BOLD&lt;/b&gt;")
	.trace();
</pre></div>

This will add a log entry in the HTML log with the message [&lt;b&gt;This is BOLD&lt;/b&gt;](- "?=getLogMessage(#fixture)").

Other appenders such as the console appender will continue log the text [without the HTML](- "c:assertTrue=consoleLogIsPlainText(#fixture)") tags.

### Screenshots
Screenshots can be [included](- "c:assertTrue=addScreenshot()") using the following:

    LOGGER.with()
		.message("Clicking 'Login'")
		.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
		.trace();

### HTML Data
Custom HTML can be included and rendered as [html](- "c:assertTrue=addHtmlData()"):

    LOGGER.with()
		.message("Some html will be included below")
		.html("This is <b>BOLD</b>")
		.trace();
				
### Text Based Data
Text based data such as CSV, XML and JSON can be [included](- "c:assertTrue=addData()") and any reserved html characters such as '<' will be escaped.

    LOGGER.with()
		.message("Sending SOAP request")
		.data("<soapenv>...</soapenv>")
		.trace();
   
### Attachments
If you wish to include non text base files, or just want keep your data outside of the log file, then attachments allow you to do this.

    LOGGER.with()
		.message("Show this")
		.attachment(new File("path/to/something.pdf"))
		.trace();
		
### Exceptions
Exceptions are formatted within a [collapsible section](- "c:assertTrue=throwException()") that presents the error message by default but will allow the user to drill down into the stack trace.  

    LOGGER.error("Something when wrong", new Exception("me"));

### Grouping Log Statements

A test often involves a series of steps to complete a task.  This extension provides two mechanisms to group log statements under a step.  

Configuration...
    
    // Using a step marker will always work, regardless of the setting of the StepRecorder property
    LOGGER.step("My step here");
    
### Location Aware Logging
All log statements show the location and line number of the class and line number where the logging statement was called.  By setting the locationAwareParent() to the current class then the location of any the log statement will [appear to be the calling method](- "c:assertTrue=locationAware()").

    Helper.writeLog("hello");
		
	public class Helper {
		private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(PageHelper.class);

		public static writeLog(String message) {
			logger.with()
				.message(message)
				.locationAwareParent(Helper.class)
				.trace();
		}
    }

    