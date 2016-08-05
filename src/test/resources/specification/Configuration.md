# Configuration

## Configuration Files
---

The extension comes with an example set of logback configuration files to use:

| ------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| logback-test.xml    | Default configuration picked up by LogBack, it is designed to log everything both to standard out and to the log file(s) |
| logback-jenkins.xml | Logs information, warning and error level to standard out and everything to the log file <br/> To use this configuration file pass command line argument of -Dlogback.configurationFile=logback-jenkins.xml |
| logback-include.xml | Configuration for the various appenders used by the logging framework |    


These must be placed in the class path of your project, eg in the src/test/resources folder. 

### Message Layout

The content of the log messages are specified using a conversion pattern. For more information about this the patterns please refer to the online manual at <http://logback.qos.ch/manual/layouts.html#PatternLayout>.


### Log Type

Configuring to use the classic text logs is a simple matter of adding the appender <appender-ref ref="FILE-PER-TEST" /> to the logback-jenkins.xml and logback-test.xml files.

Further customisation, such as the format of the log statement can be done in logback-include.xml.


## Extension Configuration
---

### 


## Integration With other Extensions
@Extension private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()
			.registerListener(exampleLogListener)
			.registerListener(exampleStoryboardListener);
			
			
			
			
			

## General Usage Notes
---

* Don't bother catching exceptions and logging them - this will do it for you.  Ideally just allow exceptions to propagate up the stack and allow this extension to catch and log the exception - unless you need to catch the exception and do something.
