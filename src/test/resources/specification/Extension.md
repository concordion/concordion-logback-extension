# The Logging Formatter Extension 

<div id="registeringtheextension" />

## Registering the extension 

Like all Concordion extensions the logging extension can be registered with an annotation on the test (or ancestor) class:

	@Extensions(LoggingFormatterExtension.class)


However it is likely that you'll want to customise the extension so more often you'll register the extension manually: 
 
    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()

<div id="usingtheextension" />

## Using the extension

Assuming the the logger is [configured correctly](- "c:assertTrue=isClassicLoggerConfigured()") (see [here](LogBackConfiguration.html) for more information), appending entries to the log is as simple as declaring the logger and logging away. 

Note that we are using SLF4J rather than referring to the logging implementation (i.e. LogBack) directly. 

<div><pre concordion:set="#fixture">
import org.concordion.api.extension.Extension;
import org.concordion.ext.LoggingFormatterExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(ConcordionRunner.class)
public class ExampleFixture {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleFixture.class);
    
    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension();

    public void logSomething() {
        LOGGER.debug("Log a value");
    }
}
</pre></div>

This will append the entry [DEBUG ExampleFixture - Log a value](- "?=canUseClassicLogger(#fixture)") into the log file.

### Automatic Exception Logging

The extension will automatically catch and log any [uncaught exceptions](- "c:assertTrue=logUncaughtException()") thrown within your tests.  This removes the need to catch and log exceptions potentially simplifying your test code.

### Specification Log

For ease of access, and to support running tests in parallel, a separate log file will be created per specification (but only if the test writes a log entry) and a link to that log file will be placed in the [specifications footer](- "c:assertTrue=specificationHasLinkToLogFile(#fixture)").
 
### Example Logs

If the specification makes use of the new Concordion Example command, a separate log file will be created per example and a link to that log file will be placed at the [top right of the example](- "c:assertTrue=exampleHasLinkToLogFile(#fixture)") 

### Log Viewer

This extension includes a log viewer to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.  The log viewer is enabled by calling the method [`setUseLogFileViewer(true)`](- "c:assertTrue=useLogViewer(#fixture, #TEXT)") on the logging extension

    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension().setUseLogFileViewer(true);

This setting is ignored if using HTML Logs.

<div id="integration" />

## Integration With other Extensions

The logging extension allows listeners to be registered to allow other extensions to monitor and [react to log entries](- "c:assertTrue=integration()") when a specific marker is encountered.

    @Extension private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()
			.registerListener(new ExampleStoryboardListener());

These listeners will work with when tests run in [parallel](- "c:assertTrue=parallel()"). 

## Using an Alternative Logging Implementation

If for some reason you do not wish to use LogBack then the extension will accept a custom logging adaptor that implements the `ILoggingAdaptor` interface.

