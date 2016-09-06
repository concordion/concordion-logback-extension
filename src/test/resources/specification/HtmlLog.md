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
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;

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

If the default HTML to plain text conversion isn't give the required result, you can also combine the message and htmlMessage methods to supply 
custom plain text and html messages:

<div><pre concordion:set="#fixture">
LOGGER.with()
	.message("This is not bold")
	.htmlMessage("&lt;b&gt;This is BOLD&lt;/b&gt;")
	.trace();
</pre></div>

This [example](- "processConsoleExample(#fixture)") will append [This is not bold](- "?=getConsoleMessage()") to the console and [&lt;b&gt;This is BOLD&lt;/b&gt;](- "?=getLogMessage()") to the log file. 

### Screenshots
To include screenshots in your logs you must provide a class that implements ScreenShotTaker.  See the demo project for an example of a Selenium screenshot taker. 

To allow the extension to include screenshots in the logs when an exception or failure is detected during a test, then the screenshot taker must either be registered with:

<div><pre concordion:set="#fixture">
// Registering via extension
getLoggingExtension().setScreenshotTaker(new DummyScreenshotTaker());
</pre></div>

... the [logging extension](- "c:assertTrue=registerExtension(#fixture)")

<div><pre concordion:set="#fixture">
// Registering via ReportLoggerFactory
ReportLoggerFactory.setScreenshotTaker(new DummyScreenshotTaker());
</pre></div>

... or directly with the [report logger](- "c:assertTrue=registerExtension(#fixture)") 

**Note** that the screenshot taker is automatically unregistered as the specification completes and are only valid for the thread they are registered with. 

Logging of the screenshots can be performed:

<div><pre concordion:set="#fixture">
ReportLoggerFactory.setScreenshotTaker(new DummyScreenshotTaker());

LOGGER.with()
	.message("Clicking 'Login'")
	.screenshot()
	.trace();

ReportLoggerFactory.removeScreenshotTaker();
</pre></div>

... using the pre-registered [screenshot taker](- "c:assertTrue=hasScreenshot(#fixture)") 
 
<div><pre concordion:set="#fixture">
LOGGER.with()
	.message("Clicking 'Login'")
	.screenshot(new DummyScreenshotTaker())
	.trace();
</pre></div>

... or for a more customised approach the screen shot taker can be [provided](- "c:assertTrue=hasScreenshot(#fixture)").
 
### HTML Data

<div><pre concordion:set="#fixture">
LOGGER.with()
	.message("Some html will be included below")
	.html("This is &lt;b>BOLD&lt;/b>")
	.trace();
</pre></div>

Custom HTML can be included and rendered as [html](- "c:assertTrue=addHtmlData(#fixture)"):
				
### Text Based Data

<div><pre concordion:set="#fixture">
LOGGER.with()
	.message("Sending SOAP request")
	.data("&lt;soapenv>...&lt;/soapenv>")
	.trace();
</pre></div>

Text based data such as CSV, XML and JSON can be [included](- "c:assertTrue=addData(#fixture)") and any reserved html characters such as '<' will be escaped.

### Attachments

<div><pre concordion:set="#fixture">
InputStream stream = new ByteArrayInputStream("Example".getBytes(StandardCharsets.UTF_8));

LOGGER.with()
	.message("Show this attachment")
	.attachment(stream, "example.txt", MediaType.PLAIN_TEXT)
	.trace();
</pre></div>

If you wish to include non text base files, or just want keep your data outside of the log file, then [attachments](- "c:assertTrue=addAttachment(#fixture)") allow you to do this.

### Exceptions

<div><pre concordion:set="#fixture">
LOGGER.error("Something when wrong", new Exception("me"));
</pre></div>

Exceptions are formatted within a [collapsible section](- "c:assertTrue=throwException(#fixture)") that presents the error message by default but will allow the user to drill down into the stack trace.
 
### Grouping Log Statements

A test often involves a series of steps to complete a task.  This extension provides two mechanisms to group log statements under a step.  
   
<div><pre concordion:set="#fixture">
// Using a step marker will always work, regardless of the setting of the StepRecorder property
LOGGER.step("My step here");
</pre></div>

This will add a [step](- "c:assertTrue=addStep(#fixture)") to the log.

See (Configuration)[LogBackConfiguration.html] for an example of using the log level to achieve the same result.

### Location Aware Logging

When debugging tests it is often desireable to know where something happened within your tests and the SLF4J/LogBack combination can provide the line and file of each logging statement out of the box.  This is not necessary that helpful on helper classes used by tests so we have the option of telling the logger to log the location as that of the calling class rather than the current class.

For example, if we have a helper class that sets the locationAwareParent() to the current class...
 
<div><pre concordion:set="#helperclass">	
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;

public class LocationHelper {
	private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());

	public void logLocationAware() {
		logger.with()
				.htmlMessage("<b>This is a location aware logged entry</b>")
				.locationAwareParent(this)
				.trace();
	}
	
	public void logLocationAware2() {
		logger.with()
				.htmlMessage("<b>This is also a location aware logged entry</b>")
				.locationAwareParent(LocationHelper.class)
				.trace();
	}
}
</pre></div>

That is used by a test...

<div><pre concordion:set="#fixture">
new LocationHelper().logLocationAware();
</pre></div>

Then log statements will show the location and line number of the class where the [logging statement was called](- "c:assertTrue=locationAware(#helperclass, #fixture)").  

**Note** use the locationAwareParent(Class) version when attempting this in a base class and want logging to appear as if it was called from the parent.

    