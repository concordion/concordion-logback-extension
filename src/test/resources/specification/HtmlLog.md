# HTML Logs

Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want and the context of what is being logged is often lacking.

The goals of the HTML based logs are to:

* Allow adding text based data, html data, screenshots, and exceptions easily
* Integrate with logging framework with minimal changes - ie stick with an SLF4J based logging interface and provide a Logback implementation
* Provide a platform to integrate with other extensions such as tooltip, storyboard and screenshot giving is a common interface for interacting with these extensions
* Support 'location aware' logging so class and line number information can be included in the logs 

The implementation is based around [SLF4J Extensions](http://slf4j.org/extensions.html) and provides a custom [layout](http://logback.qos.ch/manual/layouts.html) and [appender](http://logback.qos.ch/manual/appenders.html) for the LogBack logging framework.

Advanced logging features such as recording steps, screenshots and data, are enabled by the use of [Markers](http://www.slf4j.org/apidocs/org/slf4j/Marker.html) (there is some more information on markers buried in the LogBack manuals chapter on [filters](http://logback.qos.ch/manual/filters.html)).  


## Configuration File
---

Configuring to use the HTML logs is a simple matter of [adding the appender](- "c:assertTrue=isHtmlAppenderConfigured()") to the logback-jenkins.xml and logback-test.xml files as follows:

    <appender-ref ref="HTML-FILE-PER-TEST" /> 

See [Configuration](Configuration.md) for more information.


## Log Message Format

To customise the log messages edit logback-include.xml and update the [pattern](http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout) with the desired conversion words:

Conversion words should not add exception information to the message (eg %exception, %throwable, %rootException, etc) as this information is automatically appended by HTMLLayout in a new table row below the logging statement.

There is a choice between two different layout formats, multi-column or single-column modes.  The is specified by updating the format property for the HTMLLayout:

**&#8658; Multiple Column Layout**

Each conversion word in the layout pattern will be shown in a [separate column](- "c:assertTrue=multiColumnLayout()").  

One notable exception about the use of PatternLayout with HTMLLayout is that conversion words should not be separated by space characters or more generally by literal text. Each specifier found in the pattern will result in a separate column. Likewise a separate column will be generated for each block of literal text found in the pattern, potentially wasting valuable real-estate on your screen.

    <layout class="org.concordion.logback.HTMLLayout">
      <format>COLUMN</format>
      <pattern>%date{HH:mm:ss.SSS}%message%file%line</pattern>
    </layout>

**&#8658; Single Column Layout**

The log message as defined by the layout pattern will be shown in a [single column](- "c:assertTrue=singleColumnLayout()").  This is much like your traditional log pattern except displayed in a table.

    <layout class="org.concordion.logback.HTMLLayout">
      <format>STRING</format>
      <pattern>%date{HH:mm:ss.SSS} %message [%file:%line]</pattern>
    </layout>


### Grouping Log Statements

A test often involves a series of steps to complete a task.  This extension provides two mechanisms to group log statements under a step.  

The simplest method is to choose select a [log level](- "c:assertTrue=recordStepsUsingLogLevel()") (either INFO or DEBUG) and any log messages logged at that level will create a step in your log file.  This may work well for existing test suites.  

A more flexible option (and the default) is to use a [step marker](- "c:assertTrue=recordStepsUsingStepMarker()"), and any log statement with that marker will be formatted as a step.  

Configuration is done in logback-include.xml by updating the value of the step recorder property to: STEP_MARKER, INFO_LOG_LEVEL, or DEBUG_LOG_LEVEL.

    <!-- This is set in logback-include.xml -->
    <layout class="org.concordion.logback.HTMLLayout">
      <format>COLUMN</format>
      <pattern>%date{HH:mm:ss.SSS}%message%file%line</pattern>
      <stepRecorder>INFO_LOG_LEVEL</stepRecorder>
    </layout>
    
        
    LOGGER.info("My step here");

    // Using a step marker will always work, regardless of the setting of the StepRecorder property
    LOGGER.info(HTMLLogMarkers.step(), "My step here");


## Usage
---

While you can continue to use the standard logger to log to the HTML log file, to use the new features you will need to [use the ReportLogger](- "c:assertTrue=canUseReportLogger()").

    import org.slf4j.ext.ReportLogger;
    import org.slf4j.ext.ReportLoggerFactory;

    public class Test {
        private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(Test.class);
        
        public void logSomething() {
            LOGGER.debug("Log {}", "a value");
        }
    }
     
The report logger provides a fluent api for advanced logging features. 

### HTML Messages
This will add a log entry in the HTML log with a [bold font](- "c:assertTrue=addHtmlMessage()"). 

    LOGGER.with()
    	.htmlMessage("<b>This is bold</b>")
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
   

TODO: Display XML just like Internet Explorer?

* http://www.geekzilla.co.uk/ViewD245BBE0-2EAB-44C0-9119-8038467926EE.htm
* http://www.codeproject.com/Articles/24299/XML-String-Browser-just-like-Internet-Explorer-usi

or maybe add link an open as file?

* http://www.w3schools.com/tags/tag_embed.asp

And Status Icons

*  http://fontawesome.io

need to figure out which ones to use - will need to look at extent reports

clone https://github.com/anshooarora/extentreports and search for fa-check-circle-o


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
    