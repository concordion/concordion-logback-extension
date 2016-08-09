# Logging

The initial requirements behind building this extension where to support the parallel runner and allow and create a single log file per test and to link to that log file within the specification for ease of access.  With this in place it came apparent that plain text log files weren't meeting the needs of the business. 

While a well written specification can convey the business intent, it can often be hard to understand what the tests are actually doing and as these tests are often written by developers, it may need a leap in faith for testers to trust them. 

By allowing the logs to be broken down into easily understood steps and including screenshots and data files this extension aims to:

* allow manual testers and business to gain and insight into what our tests are actually doing and increase the level of trust in the tests
* assist in quicker diagnoses of tests failures 
* capture and log exceptions automatically reducing the effort to create logs
* work seemlessly with other extensions that also provide information such as the Storyboard, Tooltip, and ScreenShotTaker extensions

In order to allow a test application to use the new features with minimum effort the logging interface has been implemented using [SLF4J](http://slf4j.org) and the logging implementation using [LogBack](http://logback.qos.ch) because of its ability to split logs into separate logs per test.


## Getting Started

Before you can use the logging framework a small amount of [configuration](Configuration.md "c:run") is required.

Your next decision is the type of logging that you'd like, you options here are to go for either:

* [Classic Text Logs](ClassicLog.md "c:run") - these have been the staple for many years  
* [HTML Logs](HtmlLog.md "c:run") - provide the information in a more digestible format and can include screenshots and other data to back up what the test has done

It is easy to switch between the two so feel free to experiment.

## Tooltip Logs

[Tooltip Logs](ToolTipLog.md "c:run") provide a handy mechanism for providing information back to anyone reading the generated specification without having to drill down into the logs.


## Integration With Other Extensions

This extension provides a mechanism for other extensions to [integrate](Integration.md "c:run") with the log files, for example sharing screenshots.

## General Usage Notes

* Don't bother catching exceptions and logging them - this will do it for you.  Ideally just allow exceptions to propagate up the stack and allow this extension to catch and log the exception - unless you need to catch the exception and do something.
