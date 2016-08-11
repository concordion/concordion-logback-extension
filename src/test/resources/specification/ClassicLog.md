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
    				<pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern> 
    			</layout>
    	...
    </appender>

## Usage
---

[Using the logger](- "c:assertTrue=canUseClassicLogger()") is as simple as declaring the logger and logging away, the resulting specification will [contain a link to the log file](- "c:assertTrue=hasLinkToLogFile()").

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    public class Test {
        private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);
        
        public void logSomething() {
            LOGGER.debug("Log {}", "a value");
        }
    }

### Example Logs

If using examples log files will be automatically split into a log per example with a link to the log placed in the top right of the example as follows:

#### [Example] (-)

This example will have its own [log and link](- "c:assertTrue=hasExampleLog()") , while the rest of the specification will have it's own log file.

### Log Viewer

This extension includes a [LogViewer](- "c:assertTrue=useLogViewer()") to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.  

This is ignored if using the HTML Logs.


    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension().setUseLogViewer(true);