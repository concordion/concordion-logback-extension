package spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.slf4j.ext.CLogger;

import test.concordion.logback.DummyScreenshotTaker;

public class HtmlLog extends BaseFixture {
	private static final String FUNKY_ARROW = "&#8658;";

	public boolean configuration() throws IOException {
		String script = "if (typeof jQuery === 'undefined') return true; if (jQuery.active != 0) return false; return true;";
		
		LogbackAdaptor.setScreenshotTaker(new DummyScreenshotTaker());

		getLogger().progress("Started testing...");
		getLogger().step("Configuration");
		//
		// getLogger().info("Info"); // Was progress, what now???
		// getLogger().debug("Debug"); // Action
		// getLogger().trace("Trace"); // Detail on action - such as WebDriver logs
		//
		// getLogger().warn("Warn");
		// getLogger().error("Error");

		getLogger().trace(CLogger.HTML_MARKER, "Find element {} <span class=\"greyed\">css selector=.test-login-button-register</span>", FUNKY_ARROW);
		// getLogger().trace(CLogger.HTML_MARKER, LogData.capture(script), "Run JavaScript {} <span class=\"greyed\">true</span>", FUNKY_ARROW);

		getLogger()
				.withHtmlMessage("Find element {} <span class=\"greyed\">css selector=.test-login-button-register</span>", FUNKY_ARROW)
				.withData(script)
				.trace();


		// getLogger()
		// .withMessage("Clicking '{}'", login.getText())
		// .withScreenshot(takeScreenshot(pageObject, login))
		// .addToStoryboard()
		// .trace();

		// getLogger().trace(LogHtml.capture("<b>Hello</b>"), "Hello {} World!", FUNKY_ARROW);

		// TODO SCEENSHOT
		// Needs to allow custom screenshot takers (ie use Concordions interface)
		// Must not know underlying mechanism so really only want path to image and possibly dimensions
		// Get log message from element (eg Clicking 'Login') with option of override
		// Highlight element
		// getLogger().debug(LogScreenshot.capture(new DummyScreenshotTaker(), "Clicking 'Login'"));

		return getLogContent().contains(">Hello World!</td>");
	}
	
	public boolean throwException() throws IOException {
		// getLogger().step("Exception Handling");
		// try {
		// throw new IllegalStateException("Hello exception handling!");
		// } catch (IllegalStateException e) {
		// getLogger().error("Hello World!", e);
		// }
		
		return getLogContent().contains("Hello exception handling!");
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
		File file = new LogbackAdaptor().getLogFile();
		
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
