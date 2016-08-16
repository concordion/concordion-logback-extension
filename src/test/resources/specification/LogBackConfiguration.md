# Configuration

In order to remain as flexible as possible the logging interface has been implemented using [SLF4J](http://slf4j.org) and a default logging implementation has been created using [LogBack](http://logback.qos.ch) because of its ability to split logs into separate logs per test and, at the time this extension was created, was recommended as the successor to Log4j.


## Configuration Files

The extension comes with an example set of logback configuration files to use:

| ------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| logback-test.xml    | Default configuration picked up by LogBack, it is designed to log everything both to standard out and to the log file(s) |
| logback-jenkins.xml | Logs information, warning and error level to standard out and everything to the log file <br/> To use this configuration file pass command line argument of -Dlogback.configurationFile=logback-jenkins.xml |
| logback-include.xml | Configuration for the various appenders used by the logging framework |    


These must be placed in the class path of your project, eg in the src/test/resources folder. 



## Appender Configuration

The supplied configuration files provide a number of pre-configured appenders allows you to control the style of the logs you want.

### Console Appender

For console logging you have a choice between:

**&#8658; STDOUT**

Is designed to log everything to the console, you might prefer to use this when running tests within an IDE such as Eclipse.

**&#8658; STDOUT-INFO**

Is designed to log only INFO level and above to the console and ignore DEBUG and below log statements.  This can be useful in a continuous integration environment such as Jenkins, especially if running tests in parallel as the logs can become all but incomprehensible..

### File Appender

To log to a file you have a choice between the following two appenders:
 
**&#8658; FILE-PER-TEST**

Automatically creates a new text based (i.e. classic) log file per test (or example if using Concordion's Example command).r

**&#8658; HTML-FILE-PER-TEST** (default)

Automatically creates a new HTML based log file per test (or example if using Concordion's Example command).  This logger also allows embedding screenshots, and other data that a text based log file cannot handle in an easy to read format.


## Configuration Selection

By default LogBack will search for logback-test.xml and use that to configure logging.  This file has been set up with defaults that will work well in when running tests in your favourite IDE but possibly not so great when running in a continuous integration environment such as Jenkins you might want different behaviour.  

In a continuous integration environment such as Jenkins you have the option to provide a different configuration file to LogBack by providing a command line argument of `-Dlogback.configurationFile=logback-jenkins.xml` to the test run to use the supplied configuration file.


## Log Message Format

First ensure that you're using the desired appender, eg to use the classic text base file appender, edit both the logback-jenkins.xml and logback-test.xml files and replace: 
 
    <appender-ref ref="HTML-FILE-PER-TEST" />
    
with: 

    <appender-ref ref="FILE-PER-TEST" />


To customise the log messages edit logback-include.xml and update the [pattern](http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout) with the desired conversion words.


### Classic Text Log Pattern

This pattern will be found in the FILE-PER-TEST appender configuration.

    <appender name="FILE-PER-TEST" class="ch.qos.logback.classic.sift.SiftingAppender">>
    	...		
    			<layout class="ch.qos.logback.classic.PatternLayout">
    				<pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} %-5level %logger{36} - %msg%n%ex{short}</pattern> 
    			</layout>>
    	....
    </appender>>


### HTML Log Patternn
Conversion words should not add exception information to the message (eg %exception, %throwable, %rootException, etc) as this information is automatically appended by HTMLLayout in a new table row below the logging statement.

There is a choice between two different layout formats, multi-column or single-column modes.  The is specified by updating the format property for the HTMLLayout:

**&#8658; Multiple Column Layout**

Each conversion word in the layout pattern will be shown in a [separate column](- "c:assertTrue=multiColumnLayout()").  

One notable exception about the use of PatternLayout with HTMLLayout is that conversion words should not be separated by space characters or more generally by literal text. Each specifier found in the pattern will result in a separate column. Likewise a separate column will be generated for each block of literal text found in the pattern, potentially wasting valuable real-estate on your screen.

    <layout class="org.concordion.logback.HTMLLayout">>
      <format>COLUMN</format>>
      <pattern>%date{HH:mm:ss.SSS}%message%file%line</pattern>>
    </layout>>

**&#8658; Single Column Layout**

The log message as defined by the layout pattern will be shown in a [single column](- "c:assertTrue=singleColumnLayout()").  This is much like your traditional log pattern except displayed in a table.

    <layout class="org.concordion.logback.HTMLLayout">
      <format>STRING</format>
      <pattern>%date{HH:mm:ss.SSS} %message [%file:%line]</pattern>
    </layout>>
    
### Grouping Log Statements

A test often involves a series of steps to complete a task.  This extension provides two mechanisms to group log statements under a step.  

The simplest method is to choose select a [log level](- "c:assertTrue=recordStepsUsingLogLevel()") (either INFO or DEBUG) and any log messages logged at that level will create a step in your log file.  This may work well for existing test suites.  

A more flexible option (and the default) is to use a [step marker](- "c:assertTrue=recordStepsUsingStepMarker()"), and any log statement with that marker will be formatted as a step.  

Configuration is done in logback-include.xml by updating the value of the step recorder property to: STEP_MARKER, INFO_LOG_LEVEL, or DEBUG_LOG_LEVEL.

    <!-- This is set in logback-include.xml -->
    <layout class="org.concordion.logback.HTMLLayout">
      <format>COLUMN</format>
      <pattern>%date{HH:mm:ss.SSS}%message%file%line</pattern>
      <stepRecorder>STEP_MARKER</stepRecorder>
    </layout>
    
        
    LOGGER.info("My step here");

    // Using a step marker will always work, regardless of the setting of the StepRecorder property
    LOGGER.step("My step here");
    