concordion-logback-extension
============================

Provides Logback logging support for concordion including:
* LogbackLogMessenger class for the tooltip extension
* LoggingFormatterExtension class that places a link to a specifications log file at the bottom of each specification
 * This assumes that you are using MDC so that you have a unique log file per test
 * It also has a logfile viewer that attempts to make the log file easier to read, not sure if its useful or not... 
