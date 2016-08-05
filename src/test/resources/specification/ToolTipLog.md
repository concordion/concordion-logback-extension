# Tooltip Logs

The [Logging Tooltip Extension](https://github.com/concordion/concordion-logging-tooltip-extension) only works with java.utils.logging out of the box.  Fortunately it provides an interface for other loggers and this extension provides it via the LogbackLogMessenger class.

## Usage

Assuming you have included the extension as part of your Maven or Gradle dependencies, then the following will get you up and running. 

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());
	
	@Extension
	private final LoggingTooltipExtension tooltipExtension = new LoggingTooltipExtension(new LogbackLogMessenger(tooltipLogger.getName(), Level.ALL, true, "%msg%n"));

	public void addConcordionTooltip(final String message) {
		// Logging at debug level means the message won't make it to the console, but will 
		// make it to the logs (based on included logback configuration files)
		tooltipLogger.debug(message);
	}

### LogbackLogMessenger parameters as used above

<table style="border: none !important">
	<tr><td><b>loggerName</b></td><td>is set to a unique value - this means the tooltip extension will only pick up log messages specifically targeted for the tooltip</td></tr>  
	<tr><td><b>isAdditive</b></td><td>set to true so that log messages are also picked up by other appenders</td></tr>   
	<tr><td><b>tooltipPattern</b></td><td>default format is "[timestamp] log message", I've overridden that in this example</td></tr>  
</table>

## [Example](-)

When I write [That's my ToolTip!](- "?=writeToolTip(#TEXT)") to the tooltip log it is displayed in the specification. 
