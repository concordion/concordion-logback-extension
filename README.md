concordion-logback-extension
============================

Provides Logback logging support for concordion including:
* LogbackLogMessenger class for the tooltip extension
* LoggingFormatterExtension class that places a link to a specifications log file at the bottom of each specification.  The assumes that you are using MDC so that you have a unique log file per test.
