# HTML Logs

Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want and the context of what is being logged is often lacking.

The goals of the HTML based logs are to:

* Allow adding text based data, html data, screenshots, and exceptions easily
* Integrate with logging framework with minimal changes - ie stick with an SLF4J based logging interface and provide a Logback implementation
* Provide a platform to integrate with other extensions such as tooltip, storyboard and screenshot giving is a common interface for interacting with these extensions
* Support 'location aware' logging so class and line number information can be included in the logs 

The implementation is based around [SLF4J Extensions](http://slf4j.org/extensions.html) and provides a custom [layout](http://logback.qos.ch/manual/layouts.html) and [appender](http://logback.qos.ch/manual/appenders.html) for the LogBack logging framework.

Advanced logging features such as recording steps, screenshots and data are enabled by the use of [Markers](http://www.slf4j.org/apidocs/org/slf4j/Marker.html) (there is some more information on markers buried in the LogBack manuals chapter on [filters](http://logback.qos.ch/manual/filters.html)).  


See [Configuration](LogBackConfiguration.html) for more information.

Configuring to use the HTML logs is a simple matter of [adding the appender](- "c:assertTrue=isHtmlAppenderConfigured()") to the logback-jenkins.xml and logback-test.xml files as follows


## Usage
---

While you can continue to use the [standard logger](- "c:assertTrue=canUseClassicLogger()") to log to the HTML log file, to use the new features you will need to [use the ReportLogger](- "c:assertTrue=canUseReportLogger()").  Arguments are supported in the same manner as the original api. 

    import org.slf4j.ext.ReportLogger;
    import org.slf4j.ext.ReportLoggerFactory;

    public class Test {
        private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(Test.class);
        
        public void logSomething() {
            LOGGER.debug("Log a value {}", "with arguments");
        }
    }
     
The report logger provides a fluent api for advanced logging features. 

### HTML Messages
This will add a log entry in the HTML log with a [bold font](- "c:assertTrue=addHtmlMessage()").

    LOGGER.with()
    	.htmlMessage("<b>This is BOLD</b>")
    	.trace();

Other appenders such as the console appender will continue log the text [without the HTML](- "c:assertTrue=consoleLogIsPlainText()") tags.

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
   
TODO: Storyboard will need to be able to link to this entry
TODO: Display XML just like Internet Explorer?

* http://www.geekzilla.co.uk/ViewD245BBE0-2EAB-44C0-9119-8038467926EE.htm
* http://www.codeproject.com/Articles/24299/XML-String-Browser-just-like-Internet-Explorer-usi

or maybe add link an open as file?

* http://www.w3schools.com/tags/tag_embed.asp

And Status Icons

*  http://fontawesome.io

need to figure out which ones to use - will need to look at extent reports

clone https://github.com/anshooarora/extentreports and search for fa-check-circle-o

### Attachments
If you wish to include non text base files, or just want keep your data outside of the log file, then attachments allow you to do this.

    LOGGER.with()
		.message("Show this")
		.attachment(new File("path/to/something.pdf"))
		.trace();


### Screenshots
Screenshots can be [included](- "c:assertTrue=addScreenshot()") using the following:

    LOGGER.with()
		.message("Clicking 'Login'")
		.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
		.trace();
		
### Exceptions
Exceptions are formatted within a [collapsible section](- "c:assertTrue=throwException()") that presents the error message by default but will allow the user to drill down into the stack trace.  

    LOGGER.error("Something when wrong", new Exception("me"));
    
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


### Grouping Log Statements

A test often involves a series of steps to complete a task.  This extension provides two mechanisms to group log statements under a step.  

Configuration...
    
    // Using a step marker will always work, regardless of the setting of the StepRecorder property
    LOGGER.step("My step here");
    