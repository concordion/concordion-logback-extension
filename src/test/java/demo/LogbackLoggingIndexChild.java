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
public class LogbackLoggingIndexChild extends BaseFixture {
    public LogbackLoggingIndexChild() {
        loggingExtension.setDebugit();
    }

	public void logBeforeRun() {
		getLogger().info("logging something to where?");
	}
	
	public void logAfterRun() {
		getLogger().info("done logging?");
	}
	
	@BeforeSpecification
	public void before() {
        getLogger().info("before child index spec");
	}

	@AfterSpecification
	public void after() {
        getLogger().info("after child index spec");
	}

    @BeforeExample
    public void beforeExample(@ExampleName String exampleName) {
        getLogger().info("before child index example: " + exampleName);
    }

    @AfterExample
    public void afterExample(@ExampleName String exampleName) {
        getLogger().info("after child index example: " + exampleName);
    }
}
