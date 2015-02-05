package temp;

import org.concordion.api.FailFast;
import org.concordion.api.extension.Extension;
import org.concordion.api.extension.Extensions;
import org.concordion.ext.LogbackLogMessenger;
import org.concordion.ext.LoggingFormatterExtension;
import org.concordion.ext.LoggingTooltipExtension;
import org.concordion.ext.loggingFormatter.LogbackHelper;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * A base class for Google search tests that opens up the Google site at the Google search page, and closes the browser once the test is complete.
 */
@RunWith(ConcordionRunner.class)
//@Extensions({ LoggingFormatterExtension.class, LoggingTooltipExtension.class })
@Extensions({ LoggingFormatterExtension.class, LoggingTooltipExtension.class })
@FailFast
public abstract class AcceptanceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());

	@Extension
	// Some notes on LogbackLogMessenger parameters:
	// loggerName: is set to a unique value - this means the tooltip extension will only pick up log messages specifically targeted for the tooltip
	// isAdditive: set to true so that log messages are also picked up by other appenders (see addConcordionTooltip() for more info)
	// tooltipPatter: default format is "[timestamp] log message", I've overridden that in this example
	public final LoggingTooltipExtension tooltipExtension = new LoggingTooltipExtension(new LogbackLogMessenger(tooltipLogger.getName(), Level.ALL, true, "%msg%n"));
	
	static {
		LogbackHelper.logInternalStatus();
	}

	public AcceptanceTest() {
		
	}

	public Logger getLogger() {
		return logger;
	}

	public void addConcordionTooltip(final String message) {
		// Logging at debug level means the message won't make it to the console, but will make it to the logs (based on included logback configuration files)
		tooltipLogger.debug(message);
	}
	
	@Before
	public void startUpTest() {
		LogbackHelper.startTestLogging(this);
		logger.info("Initialising the acceptance test class {} on thread {}", this.getClass().getSimpleName(), Thread.currentThread().getName());
	}

	@After
	public void tearDownTest() {
		logger.info("Tearing down the acceptance test class on thread {}", Thread.currentThread().getName());
		LogbackHelper.stopTestLogging();
	}
}
