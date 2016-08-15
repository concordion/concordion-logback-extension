# Logging

The initial requirements behind building this extension where to support the parallel runner and allow and create a single log file per test and to link to that log file within the specification for ease of access.  With this in place it came apparent that plain text log files weren't meeting the needs of the business or the test developers. 

While a well written specification can convey the business intent, it can often be hard to understand what the tests are actually doing and as these tests are often written by developers, it may need a leap in faith for testers to trust them. 

By allowing the logs to be broken down into easily understood steps and including screenshots and data files this extension aims to:

* allow manual testers and business to gain and insight into what our tests are actually doing and increase the level of trust in the tests
* assist in quicker diagnoses of tests failures 
* capture and log exceptions automatically reducing the effort to create logs
* work seemlessly with other extensions that also provide information such as the Storyboard, Tooltip, and ScreenShotTaker extensions


## Getting Started

Getting up and running is a simple matter of following these steps:

1. [Configure LogBack](LogBackConfiguration.md "c:run") 
2. [Register the Extension](Extension.md "c:run")
3. Start Logging???

**Tip:** Don't bother catching exceptions and logging them - this will do it for you.  Ideally just allow exceptions to propagate up the stack and allow this extension to catch and log the exception - unless you need to catch the exception and do something.


## HTML Logging / Reporting

* [HTML Logs](HtmlLog.md "c:run") - provide the information in a more digestible format and can include screenshots and other data to back up what the test has done

It is easy to switch between the two so feel free to experiment.


## Tooltip Logs

[Tooltip Logs](ToolTipLog.md "c:run") provide a handy mechanism for providing information back to anyone reading the generated specification without having to drill down into the logs.

## Integration with Other Extensions 
TODO: link to Extensions




