# Classic Logs

If text based logs are enough for you then this ones for you.

The primary purpose of this extension is the ability to have a separate log per test and place a link to the log in the specification footer.

## Appender Configuration

The extension comes with an example set of logback configuration files to use.  

Configuring to use the classic text logs is a simple matter of adding the appender <appender-ref ref="FILE-PER-TEST" /> to the logback-jenkins.xml and logback-test.xml files.

Further customisation, such as the format of the log statement can be done in logback-include.xml.

## Write to Log
When configured to the the appender named [FILE-PER-TEST](- "c:assertTrue=useAppender(#TEXT)") the specification gets a link to the log file in the [footer](- "c:assertTrue=hasLinkInFooter()"). 

## Log Viewer

This extension includes a [LogViewer](- "c:assertTrue=useLogViewer()") to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.


## TODO

See what to do about logging strack trace or causes and what default should apply.  useing %noop to hide stack trace, causes...