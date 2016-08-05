# Classic Logs

If text based logs are enough for you then this ones for you.

The primary purpose of this extension is the ability to have a separate log per test and place a link to the log in the specification footer.

## Appender Configuration

Configuring to use the classic text logs is a simple matter of adding the appender to the logback-jenkins.xml and logback-test.xml files as follows:

    <appender-ref ref="FILE-PER-TEST" />

See [Configuration](Configuration.md) for more information.


## Log Message Format
To customise the log messages edit logback-include.xml and update the pattern:

    <appender name="FILE-PER-TEST" class="ch.qos.logback.classic.sift.SiftingAppender">
		...		
				<layout class="ch.qos.logback.classic.PatternLayout">
 					<pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern> 
				</layout>
		...
	</appender>
	
## Usage
When configured to the the appender named [FILE-PER-TEST](- "c:assertTrue=useAppender(#TEXT)") the specification gets a link to the log file in the [footer](- "c:assertTrue=hasLinkInFooter()"). 

## Log Viewer

This extension includes a [LogViewer](- "c:assertTrue=useLogViewer()") to make the logs a little friendlier to use and gives the ability to filter the logs by the log level.


## TODO

See what to do about logging strack trace or causes and what default should apply.  useing %noop to hide stack trace, causes...