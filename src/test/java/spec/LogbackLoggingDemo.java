package spec;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

/**
 * A fixture class for the LoggingDemo.html specification.
 * <p>
 * This adds the Logging Tooltip Extension to Concordion to show logging detail in the Concordion output. The extension picks up any logging output written to
 * Logback.
 * <p>
 * For the purposes of demonstration, we are logging details of WebDriver (Selenium 2) events using the SeleniumEventLogger. (This logging uses slf4j, which
 * writes to Logback when run with the slf4j-jdk14 jar).
 * <p>
 * Run this class as a JUnit test to produce the Concordion results.
 */
public class LogbackLoggingDemo extends AcceptanceTest {

	private Integer result;

	public void multiply(final String calulation) {
		String values[] = calulation.split("[*]");
		
		getLogger().trace("TRACE level logging should NOT appear in the console, but SHOULD appear in the test log");
		getLogger().debug("DEBUG level logging  should NOT appear in the console, but SHOULD appear in the test log");
		getLogger().info("INFO level logging  should appear in the console, and the test log");
		getLogger().warn("WARN level logging  should appear in the console, and the test log");
		getLogger().error("ERROR level logging  should appear in the console, and the test log");
		getLogger().debug("This is\n\ta multi-line\n\tlog entry");
		getLogger().info("NOTE: none of the logging above will appear in the tooltips");
				
		addConcordionTooltip("My tooltip here!");		
		result = Integer.parseInt(values[0].trim()) * Integer.parseInt(values[1].trim());		
	}

	public String getCalculatorResult() {		
		getLogger().debug("NOTE: no tooltip showing for getCalculatorResult()");
		return result.toString();
	}
	
	@BeforeSpecification
	public void before() {
		getLogger().info("before spec demo");
	}

	@AfterSpecification
	public void after() {
		getLogger().info("after spec demo");
	}
}
