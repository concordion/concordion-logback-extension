package spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.logback.LogMarkers;
import org.slf4j.Marker;

import ch.qos.logback.core.FileAppender;
import test.concordion.logback.DummyScreenshotTaker;

public class HtmlLog extends BaseFixture {
	
	public boolean configuration() throws IOException {
		getLogger().info(LogMarkers.step(), "Configuration");
		getLogger().trace("Hello World!");
		getLogger().debug("Hello World!");
		getLogger().info("Hello World!");
		getLogger().warn("Hello World!");
		getLogger().error("Hello World!");
		
		return getLogContent().contains(">Hello World!</td>");
	}
	
	public boolean throwException() throws IOException {
		getLogger().info(LogMarkers.step(), "Exception Handling");
		try {
			throw new IllegalStateException("Hello exception handling!");
		} catch (IllegalStateException e) {
			getLogger().error("Hello World!", e);
		}
		
		return getLogContent().contains("Hello exception handling!");
	}
	
	public boolean recordStepsUsingLogLevel() {
		getLogger().info(LogMarkers.step(), "Step using Log Level");
		return true;
	}
	
	public boolean recordStepsUsingStepMarker() {
		getLogger().info(LogMarkers.step(), "Step using Step Marker");
		return true;
	}
	
	public boolean addScreenshot() throws IOException {
		getLogger().info(LogMarkers.step(), "Screenshot");
		Marker screenshot = LogMarkers.screenshot("CurrentPage", new DummyScreenshotTaker());
				
		getLogger().debug(screenshot, "Have taken a screenshot for some reason...");
		getLogger().debug(screenshot, "And another!");
		
		return getLogContent().contains("<img src=");
	}
	
	public boolean addData() throws IOException {
		getLogger().info(LogMarkers.step(), "Text Data");
		Marker data;
		
		data = LogMarkers.data("Adding data", "Some TEXT data...\r\nHows it going?");
		getLogger().debug(data, "Adding data for some reason...");
		
		data = LogMarkers.data("Adding data", getDataContent("example.csv"));
		getLogger().debug(data, "Some CSV data...");

		data = LogMarkers.data("Adding data", getDataContent("example.json"));
		getLogger().debug(data, "Some JSON data...");
		
		data = LogMarkers.data("Adding data", getDataContent("example.xml"));
		getLogger().debug(data, "Some XML data...");

		return getLogContent().contains("<pre>");
	}

	public boolean addHtmlData() {
		getLogger().info(LogMarkers.step(), "HTML Data");
		
		Marker data = LogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		getLogger().debug(data, "Some <b><i>HTML</i></b> that won't display as HTML plus...");

		// TODO How validate?
		return true;
	}
	
	public boolean addHtmlStatement() {
		getLogger().info(LogMarkers.step(), "HTML Statement");
		
		Marker html = LogMarkers.htmlStatementMarker();
		getLogger().debug(html, "Some <b><i>HTML</i></b> data...");

		// TODO How validate?
		return true;
	}

	public boolean addCombinedHtml() {
		getLogger().info(LogMarkers.step(), "Combinded HTML and Statement");
		
		Marker html = LogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		html.add(LogMarkers.htmlStatementMarker());
		
		getLogger().debug(html, "Some <b><i>Combined HTML Statement</i></b> plus...");

		// TODO How validate?
		return true;
	}

	private String getLogContent() throws IOException {
		FileAppender<?> fileAppender = LogbackAdaptor.getConfiguredAppender();
		
		if (fileAppender == null) {
			return "";
		}
		
		try (InputStream input = new FileInputStream(new File(fileAppender.getFile()))) {
			return IOUtils.toString(input);
		}
	}
	
	private String getDataContent(String fileName) throws IOException {
		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("data/" + fileName)) {
			return IOUtils.toString(input);
		}
	}
	
}
