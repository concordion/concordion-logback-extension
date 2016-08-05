# Configuration

## Configuration Files

The extension comes with an example set of logback configuration files to use:

| ------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| logback-test.xml    | Default configuration picked up by LogBack, it is designed to log everything both to standard out and to the log file(s) |
| logback-jenkins.xml | Logs information, warning and error level to standard out and everything to the log file <br/> To use this configuration file pass command line argument of -Dlogback.configurationFile=logback-jenkins.xml |
| logback-include.xml | Configuration for the various appenders used by the logging framework |    


These must be placed in the class path of your project, eg in the src/test/resources folder. 

## Registering the extension

Like all Concordion extensions the logging extension can be registered with an annotation on the test (or ancestor) class:

	@Extensions(LoggingFormatterExtension.class)


However it is likely that you'll want to customise the extension so more often you'll register the extension manually: 
 
    @Extension 
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()

