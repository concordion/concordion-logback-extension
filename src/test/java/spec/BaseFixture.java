package spec;

import org.concordion.api.extension.Extension;
import org.concordion.ext.LogbackLogMessenger;
import org.concordion.ext.LoggingFormatterExtension;
import org.concordion.ext.LoggingTooltipExtension;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.CLogger;
import org.slf4j.ext.CLoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * A base class for Google search tests that opens up the Google site at the Google search page, and closes the browser once the test is complete.
 */
@RunWith(ConcordionRunner.class)
public class BaseFixture {
	private final CLogger logger = CLoggerFactory.getCLogger(this.getClass().getName());
	private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());

	@Extension private final LoggingTooltipExtension tooltipExtension = new LoggingTooltipExtension(new LogbackLogMessenger(tooltipLogger.getName(), Level.ALL, true, "%msg%n"));
	@Extension private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension(false);
	
	static {
		LogbackAdaptor.logInternalStatus();
	}

	public CLogger getLogger() {
		return logger;
	}

	public void addConcordionTooltip(final String message) {
		// Logging at debug level means the message won't make it to the console, but will make 
		// it to the logs (based on included logback configuration files)
		tooltipLogger.debug(message);
	}
}
