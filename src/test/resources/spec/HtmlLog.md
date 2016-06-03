# HTML Logs

Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want.

This custom layout and appender for the [LogBack Logger](http://logback.qos.ch) will combine your logs with screenshots, data and stack trace and present the information in an easy to digest fashion.

Advanced logging features such as recording steps, screenshots and data, are enabled by the use of [Markers](http://www.slf4j.org/apidocs/org/slf4j/Marker.html) (there is some more information on markers buried in the LogBack manuals chapter on [filters](http://logback.qos.ch/manual/filters.html)).  


## Splitting Log Files

Log files may be split either by specification or by example.

    @Extension
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension(false).setSplitBy(Split.SPECIFICATION);


### By Specification
A link to the specifications log file is placed in the footer.

### By Example
A link to the log file is placed at the top right of each example - this is the default setting.  If there is no example, or log statements are made outside of an example then a link will also be placed in the footer.  


## Appender Configuration

The extension comes with an example set of logback configuration files to use.  

Configuring to use the HTML logs is a simple matter of [adding the appender](- "c:assertTrue=configuration()") `<appender-ref ref="HTML-FILE-PER-TEST" />` to the logback-jenkins.xml and logback-test.xml files.

Further customisation, such as the format of the log statements and columns shown can also be configured in logback-include.xml.


## Column Configuration

The content of the table columns are specified using a conversion pattern. For more information about this layout, please refer to the online manual at <http://logback.qos.ch/manual/layouts.html#ClassicHTMLLayout>.

There is a choice between two different layout formats:

### Multiple Column Layout

Each conversion word in the layout pattern will be shown in a separate column.  Any literals (including spaces) will result in an extra column being added, so don't include spaces or other characters in your pattern.

The "nopex/nopexception" conversion word is ignored in this setting.

    <layout class="org.concordion.logback.HTMLLayout">
      <format>COLUMN</format>
      <pattern>%date{HH:mm:ss.SSS}%message%file%line</pattern>
    </layout>

### Single Column Layout

Each conversion word in the layout pattern will be shown in a single column.  This is much like your traditional log pattern except displayed in a table.

The "nopex/nopexception" conversion word is automatically added in this setting.

    <layout class="org.concordion.logback.HTMLLayout">
      <format>STRING</format>
      <pattern>%date{HH:mm:ss.SSS} %message [%file:%line]</pattern>
    </layout>


## Grouping Log Statements

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
`

## Exceptions

Exceptions are formatted within a [collapsible section](- "c:assertTrue=throwException()") that presents the error message by default but will allow the user to drill down into the stack trace.  

    try {
        int i = 1 / 0;
    } catch (Exception e) {
        LOGGER.error("Something when wrong", e);
    }


## Screenshots

Screenshots can be [included](- "c:assertTrue=addScreenshot()") using the following:

    Marker screenshot = LogMarkers.screenshotMarker(title, screenshotTaker);
	getLogger().debug(screenshot, "Clicking the 'Login' button");

## Text Based Data

Text based data such as CSV, XML and JSON can be [included](- "c:assertTrue=addData()") using the following:

    Marker data = LogMarkers.dataMarker(title, data);
    getLogger().debug(data, "Sending SOAP request");

## HTML Based Data

Custom HTML can be included as [data](- "c:assertTrue=addHtmlData()"):

    Marker data = LogMarkers.htmlMarker(title, "<p>Some <b><i>HTML</i></b></p>", true);
    getLogger().debug(data, "Sending HTML request");

or in the [log statement](- "c:assertTrue=addHtmlStatement()"):

    Marker data = LogMarkers.htmlStatement();
    getLogger().debug(data, "Sending HTML request");

and can be combined with [other markers](- "c:assertTrue=addCombinedHtml()"):
  
    Marker html = LogMarkers.htmlMarker("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
    html.add(LogMarkers.htmlStatementMarker());
		
    getLogger().debug(html, "Some <b><i>Combined HTML Statement</i></b> plus...");

NOTE: When combining markers the data markers (Screenshot, Data, HTML) must be at the top level otherwise they will be ignored.  The htmlStatementMarker can be added at any level.    

## [Log Within 'Example'](-)
[log](- "loginexample()")
