package demo;

import org.concordion.api.AfterExample;
import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeExample;
import org.concordion.api.BeforeSpecification;
import org.concordion.api.ExampleName;

import specification.BaseFixture;

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
public class LogbackLoggingDemo2 extends BaseFixture {

	private Integer result;

    public LogbackLoggingDemo2() {
        loggingExtension.setDebugit();
    }

	public void multiply(final String calulation) {
		String values[] = calulation.split("[*]");
		
        getLogger().debug("Do something in test example");
		
		result = Integer.parseInt(values[0].trim()) * Integer.parseInt(values[1].trim());		
	}

    public String getCalculatorResult() {
		return result.toString();
	}
	
	@BeforeSpecification
	public void before() {
        getLogger().info("before spec demo 2");
	}

	@AfterSpecification
	public void after() {
        getLogger().info("after spec demo 2");
	}
	
	@BeforeExample
	public void beforeExample(@ExampleName String exampleName) {
        getLogger().info("before demo 2 example: " + exampleName);
	}
	
	@AfterExample
	public void afterExample(@ExampleName String exampleName) {
        getLogger().info("after demo 2 example: " + exampleName);
	}
}
