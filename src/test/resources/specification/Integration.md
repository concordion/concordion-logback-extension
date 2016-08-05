# Configuration

## Integration With other Extensions
The logging extension allows listeners to be registered to allow other extensions to monitor and [react to log entries](- "c:assertTrue=integration()") when a specific marker is encountered.

    @Extension private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()
			.registerListener(new ExampleStoryboardListener());

These listeners will work with when tests run in [parallel](- "c:assertTrue=parallel()"). 