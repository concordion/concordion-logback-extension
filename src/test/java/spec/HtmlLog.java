package spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.ext.ReportLogger;

import test.concordion.logback.DummyScreenshotTaker;

public class HtmlLog extends BaseFixture {
	private static final String FUNKY_ARROW = "&#8658;";

	public boolean configuration() throws IOException {
		String script = "if (typeof jQuery === 'undefined') return true; if (jQuery.active != 0) return false; return true;";

		// if (true) {
		// throw new IllegalStateException("Hello exception handling!");
		// }
		
		// LogbackAdaptor.setScreenshotTaker(new DummyScreenshotTaker());

		getLogger().progress("This won't appear in the HTML report...");
		getLogger().step("This is a step");
		//
		getLogger().info("Info"); // Was progress, what now???
		getLogger().debug("Debug"); // Action
		getLogger().trace("Trace"); // Detail on action - such as WebDriver logs

		getLogger().warn("Warn");
		getLogger().error("Error");

		// HTML Formatted Message Option 1
		getLogger().trace(ReportLogger.HTML_MESSAGE_MARKER, "Find element {} <span class=\"greyed\">css selector=.test-login-button-register</span>", FUNKY_ARROW);

		// HTML Formatted Message Option 2
		getLogger().with().htmlMessage("Find element {} <span class=\"greyed\">css selector=.test-login-button-register</span>", FUNKY_ARROW).trace();

		// TEXT Data
		getLogger().with()
				.htmlMessage("Run JavaScript {} <span class=\"greyed\">true</span>", FUNKY_ARROW)
				.data(script)
				.trace();

		// HTML Data
		getLogger().with()
				.message("Some HTML")
				.html("This is <b>BOLD</b>")
				.trace();

		// Screenshot
		getLogger().with()
				.message("Clicking 'Login'")
				.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
				// .addToStoryboard()
				.trace();

		// Exception
		try {
			throw new IllegalStateException("Hello exception handling!");
		} catch (IllegalStateException e) {
			getLogger().error("Hello World!", e);
		}

		return true;
	}
	
	public boolean throwException() throws IOException {
		// getLogger().step("Exception Handling");
		// try {
		// throw new IllegalStateException("Hello exception handling #2!");
		// } catch (IllegalStateException e) {
		// getLogger().error("Hello World!", e);
		// }
		//
		return true; // getLogContent().contains("Hello exception handling!");
	}
	
	public boolean recordStepsUsingLogLevel() {
		// getLogger().step("Step using Log Level");
		return true;
	}
	
	public boolean recordStepsUsingStepMarker() {
		// getLogger().step("Step using Step Marker");
		return true;
	}
	
	public boolean addScreenshot() throws IOException {
		// getLogger().step("Screenshot");
		// Marker screenshot = LogMarkers.screenshot("CurrentPage", new DummyScreenshotTaker());
		//
		// getLogger().debug(screenshot, "Have taken a screenshot for some reason...");
		// getLogger().debug(screenshot, "And another!");
		
		return getLogContent().contains("<img src=");
	}
	
	public boolean addData() throws IOException {
		// getLogger().step("Text Data");
		// Marker data;
		
		// data = LogMarkers.data("Adding data", "Some TEXT data...\r\nHows it going?");
		// getLogger().debug(data, "Adding data for some reason...");
		//
		// data = LogMarkers.data("Adding data", getDataContent("example.csv"));
		// getLogger().debug(data, "Some CSV data...");
		//
		// data = LogMarkers.data("Adding data", getDataContent("example.json"));
		// getLogger().debug(data, "Some JSON data...");
		//
		// data = LogMarkers.data("Adding data", getDataContent("example.xml"));
		// getLogger().debug(data, "Some XML data...");

		return getLogContent().contains("<pre>");
	}

	public boolean addHtmlData() {
		// getLogger().step("HTML Data");
		
		// Marker data = LogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		// getLogger().debug(data, "Some <b><i>HTML</i></b> that won't display as HTML plus...");

		// TODO How validate?
		return true;
	}
	
	public boolean addHtmlStatement() {
		// getLogger().step("HTML Statement");
		
		// Marker html = LogMarkers.html();
		// getLogger().debug(html, "Some <b><i>HTML</i></b> data...");

		// TODO How validate?
		return true;
	}

	public boolean addCombinedHtml() {
		// getLogger().step("Combinded HTML and Statement");
		
		// Marker html = LogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		// html.add(LogMarkers.html());
		
		// getLogger().debug(html, "Some <b><i>Combined HTML Statement</i></b> plus...");

		// TODO How validate?
		return true;
	}

	private String getLogContent() throws IOException {
		File file = getLoggingAdaptor().getLogFile();
		
		if (file == null) {
			return "";
		}
		
		try (InputStream input = new FileInputStream(file)) {
			return IOUtils.toString(input);
		}
	}
	
	private String getDataContent(String fileName) throws IOException {
		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("data/" + fileName)) {
			return IOUtils.toString(input);
		}
	}
	
}
