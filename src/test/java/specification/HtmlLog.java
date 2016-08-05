package specification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.concordion.api.BeforeSpecification;
import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.ReportLogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.ext.html.Format;
import ch.qos.logback.ext.html.HTMLLayout;
import test.concordion.logback.DummyScreenshotTaker;
import test.concordion.logback.PageHelper;
import test.concordion.logback.StoryboardMarkerFactory;

public class HtmlLog extends BaseFixture {
	private static final String FUNKY_ARROW = "&#8658;";

	@BeforeSpecification
	private final void beforeSpecification() {
		// Force the logger to create the various appenders and layouts required for these tests
		getLogger().debug("nothing");
	}

	private Logger getRootLogger() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		return context.getLogger(Logger.ROOT_LOGGER_NAME);
	}

	private SiftingAppender getHtmlFilePerTestSiftingAppender() {
		Logger logger = getRootLogger();

		for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
			Appender<ILoggingEvent> appender = index.next();

			if (appender instanceof SiftingAppender) {
				if (appender.getName().equals("HTML-FILE-PER-TEST")) {
					return (SiftingAppender) appender;
				}
			}
		}

		throw new IllegalStateException("HTML-FILE-PER-TEST sifting appender is not configured");
	}

	private FileAppender<?> getHtmlFileAppender() {
		SiftingAppender siftingAppender = getHtmlFilePerTestSiftingAppender();

		for (Appender<ILoggingEvent> appender : siftingAppender.getAppenderTracker().allComponents()) {
			if (appender instanceof FileAppender) {
				return (FileAppender<?>) appender;
			}
		}

		throw new IllegalStateException("HTML-FILE-PER-TEST file appender is not configured");
	}

	private HTMLLayout getHtmlLayout() {
		FileAppender<?> fileAppender = getHtmlFileAppender();

		if (fileAppender.getEncoder() instanceof LayoutWrappingEncoder<?> == false) {
			throw new IllegalStateException("HTML-FILE-PER-TEST layout is not configured");
		}

		LayoutWrappingEncoder<?> encoder = (LayoutWrappingEncoder<?>) fileAppender.getEncoder();

		if (encoder.getLayout() instanceof HTMLLayout == false) {
			throw new IllegalStateException("HTML-FILE-PER-TEST layout is not configured");
		}

		return (HTMLLayout) encoder.getLayout();
	}

	private void copy(HTMLLayout src, HTMLLayout dest) {
		src.setStylesheet(dest.getStylesheet());
		src.setFormat(dest.getFormat());
		src.setPattern(dest.getPattern());
		src.setStepRecorder(dest.getStepRecorder());
	}

	private HTMLLayout backupLayout(HTMLLayout orig) {
		HTMLLayout backup = new HTMLLayout();
		copy(orig, backup);

		return backup;
	}

	private void restoreLayout(HTMLLayout backup, HTMLLayout orig) {
		copy(backup, orig);
	}

	/** HTML-FILE-PER-TEST appender is attached to the root logger */
	public boolean isHtmlAppenderConfigured() {
		return getHtmlFilePerTestSiftingAppender() != null;
	}

	public boolean multiColumnLayout() {
		HTMLLayout layout = getHtmlLayout();
		HTMLLayout backup = backupLayout(layout);

		layout.setFormat(Format.COLUMN.name());
		layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");

		exampleLogListener.setLayout(layout);
		exampleLogListener.resetStream();

		getLogger().debug("multiColumnLayout example");
		
		exampleLogListener.setLayout(null);
		restoreLayout(backup, layout);

		return exampleLogListener.getStreamContent().contains("<td class=\"Message\">multiColumnLayout example</td>");
	}


	public boolean singleColumnLayout() {
		HTMLLayout layout = getHtmlLayout();
		HTMLLayout backup = backupLayout(layout);

		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		exampleLogListener.setLayout(layout);
		exampleLogListener.resetStream();

		getLogger().debug("singleColumnLayout example");

		exampleLogListener.setLayout(null);
		restoreLayout(backup, layout);

		return exampleLogListener.getStreamContent().contains("<td>singleColumnLayout example HtmlLog.java</td>");
	}

	public boolean configuration2() throws IOException {
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
		exampleLogListener.resetStream();

		getLogger().with()
				.message("Clicking 'Login'")
				.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
				.trace();

		Assert.assertTrue(exampleLogListener.getStreamContent().contains("Clicking 'Login'"));

		// Integration with other extensions

		Assert.assertEquals("", exampleStoryboardListener.getStreamContent());

		getLogger().with()
				.marker(StoryboardMarkerFactory.container("Doing Stuff"))
				.trace();

		Assert.assertEquals("FOUND MARKER STORYBOARD_CONTAINER", exampleStoryboardListener.getStreamContent());

		// Location Aware Logging
		PageHelper helper = new PageHelper();

		helper.captureScreenshot(getLoggingAdaptor());

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
