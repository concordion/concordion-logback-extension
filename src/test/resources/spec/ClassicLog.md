# Classic Logs

If text based logs are enough for you then this ones for you.

The primary purpose of this extension is the ability to have a separate log per test and place a link to the log in the specification footer.

This extension includes a LogViewer to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.  

## Configuration

The extension comes with an example set of logback configuration files to use.  Configuring to use the classic text logs is a simple matter of adding the appender <appender-ref ref="HTML-FILE-PER-TEST" /> to the logback-jenkins.xml and logback.xml files.

Further customisation, such as the format of the log statement can be done in logback-include.xml.

## [Write to Log](-)
When configured to the the appender named [FILE-PER-TEST]("c:assertTrue=useAppender(#TEXT)") the specification gets a link to the log [file]("#withLogViewer=false") in the [footer]("c:assertTrue=writeLog(#TEXT, #withLogViewer)"). 

## [Use Log Viewer](-)
When configured to the the appender named [FILE-PER-TEST]("c:assertTrue=useAppender(#TEXT)") and use the [log viewer]("#withLogViewer=true") the specification gets a link to the log viewer in the [footer]("c:assertTrue=writeLog(#TEXT, #withLogViewer)"). 

