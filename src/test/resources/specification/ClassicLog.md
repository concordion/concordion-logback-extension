# Classic Logs

If all you need are simple text based logs then this ones for you.

The primary purpose of this extension becomes the ability to have a separate log per test (or example) and place a link to the log in the specification.

## Configuration
---

Configuring to use the classic text logs is a simple matter of adding the appender to the logback-jenkins.xml and logback-test.xml files as follows:

    <appender-ref ref="FILE-PER-TEST" />

See [Configuration](Configuration.html) for more information.


### Log Message Format

To customise the log messages edit logback-include.xml and update the pattern:

    <appender name="FILE-PER-TEST" class="ch.qos.logback.classic.sift.SiftingAppender">
    	...		
    			<layout class="ch.qos.logback.classic.PatternLayout">
    				<pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} %-5level %logger{36} - %msg%n%ex{short}</pattern> 
    			</layout>
    	...
    </appender>

## Usage
---

As long as the logger is configured to log to use the [text based log file appender](- "c:assertTrue=isClassicLoggerConfigured()"), appending entries to the log is as simple as declaring the logger and logging away. Note that we are using SLF4J rather than referring to the logging implementation directly. 

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


### Specification Log

For ease of access, and to support running tests in parallel, a separate log file will be created per specification (but only if the test writes a log entry) and a link to that log file will be placed in the [specifications footer](- "c:assertTrue=specificationHasLinkToLogFile(#fixture)").
 
### Example Logs

If the specification makes use of the new Concordion Example command, a separate log file will be created per example and a link to that log file will be placed at the [top right of the example](- "c:assertTrue=exampleHasLinkToLogFile(#fixture)") 

### Log Viewer

This extension includes a log viewer to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.  The log viewer is enabled by calling the method [`setUseLogFileViewer(true)`](- "c:assertTrue=useLogViewer(#fixture, #TEXT)") on the logging extension

    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension().setUseLogFileViewer(true);

This setting is ignored if using HTML Logs.