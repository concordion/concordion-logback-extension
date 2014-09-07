concordion-logback-extension
============================

Provides Logback logging support for concordion including:
* LogbackLogMessenger class for the tooltip extension
* LoggingFormatterExtension class that places a link to a specifications log file at the bottom of each specification
 * This assumes that you are using MDC so that you have a unique log file per test
 * It also has a logfile viewer that attempts to make the log file easier to read, not sure if its useful or not... 

This extension was originally developed once we started running our tests in parallel (using the latest update to cordion to allow this) and discovered that one interleaved log file was not particularly useful.  Even if you are not running tests in parallel the ability to click on a link in the specification to access the log file is very nice :-)

The log viewer was an attempt to make the logging less scary for non developers, and came before I thought of the storyboard extension.  I'm a little unsure wether its an improvement or not.
