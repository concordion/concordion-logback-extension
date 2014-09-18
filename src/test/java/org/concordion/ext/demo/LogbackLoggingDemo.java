package org.concordion.ext.demo;

import org.concordion.api.extension.Extensions;
import org.concordion.ext.LoggingTooltipExtension;
import org.concordion.ext.driver.page.GoogleResultsPage;
import org.concordion.ext.driver.page.GoogleSearchPage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

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
@RunWith(ConcordionRunner.class)
@Extensions(LoggingTooltipExtension.class)
public class LogbackLoggingDemo extends AceptanceTest {

	private GoogleSearchPage searchPage;
	private GoogleResultsPage resultsPage;

	public LogbackLoggingDemo() {
		super(true);
	}

	/**
	 * Searches for the specified topic, and waits for the results page to load.
	 */
	public void searchFor(final String topic) {
		getLogger().debug("Opening browser and navigating to google");
		searchPage = new GoogleSearchPage(getBrowser().getDriver());

		getLogger().debug("Searching for the results of 6*9");
		resultsPage = searchPage.searchFor(topic);
	}

	/**
	 * Returns the result from Google calculation.
	 */
	public String getCalculatorResult() {
		getLogger().debug("Getting calculator results");
		String result = resultsPage.getCalculatorResult();
		addConcordionTooltip(String.format("Adding result is '%s' to tooltip only", result));
		return result;
	}
}
