# Logging

While a well written specification can convey the business intent, it can often be hard to understand what the tests are actually doing and as these tests are often written by developers, it may need a leap in faith for testers to trust them. 

By allowing the logs to be broken down into easily understood steps and including screenshots and data files this extension aims to:

* allow testers and business to gain and insight into what our tests are actually doing and increase the level of trust in the tests
* help developers quickly diagnose issues tests and find performance improvements
* capture and log exceptions automatically reducing the effort to create logs
* work seemlessly with other extensions that also provide information such as the Storyboard, Tooltip, and ScreenShotTaker extensions


## Classic Logs

[Classic Logs](ClassicLog.md "c:run") being purely text data have been the staple for many years and are well supported. 

## HTML Logs

[HTML Logs](HtmlLog.md "c:run") are a step up from text logs and are able to provide the information in a more digestible format can document what your test is doing especially when they include screenshots and data to back up what the test has done.

## Tooltip Logs

[Tooltip Logs](ToolTipLog.md "c:run") provide a handy mechanism for providing information back to anyone reading the generated specification without having to drill down into the logs.

## Look and Feel

In addition to the choice between classic and html logs, this extension offers a few other area of [customisation](Customisation.md "c:run").

## Configuration

Before you can use the logging framework some [configuration](Configuration.md "c:run") files must be included in your project.  