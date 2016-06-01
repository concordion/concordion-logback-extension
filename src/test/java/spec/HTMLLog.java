package spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.concordion.logback.HTMLLogMarkers;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import test.concordion.logback.DummyScreenshotTaker;

public class HTMLLog extends BaseFixture {
	
	public boolean configuration() throws IOException {
		getLogger().debug(HTMLLogMarkers.step(), "Configuration");
		getLogger().debug("Hello World!");
		
		return getLogContent().contains(">Hello World!</td>");
	}
	
	public boolean throwException() throws IOException {
		getLogger().debug(HTMLLogMarkers.step(), "Exception Handling");
		try {
			throw new IllegalStateException("Hello exception handling!");
		} catch (IllegalStateException e) {
			getLogger().error("Hello World!", e);
		}
		
		return getLogContent().contains("Hello exception handling!");
	}
	
	public boolean recordStepsUsingLogLevel() {
		getLogger().debug(HTMLLogMarkers.step(), "Step using Log Level");
		return true;
	}
	
	public boolean recordStepsUsingStepMarker() {
		getLogger().debug(HTMLLogMarkers.step(), "Step using Step Marker");
		return true;
	}
	
	public boolean addScreenshot() throws IOException {
		getLogger().debug(HTMLLogMarkers.step(), "Screenshot");
		Marker screenshot = HTMLLogMarkers.screenshot("CurrentPage", new DummyScreenshotTaker());
				
		getLogger().debug(screenshot, "Have taken a screenshot for some reason...");
		getLogger().debug(screenshot, "And another!");
		
		return getLogContent().contains("<img src=");
	}
	
	public boolean addData() throws IOException {
		getLogger().debug(HTMLLogMarkers.step(), "Text Data");
		Marker data;
		
		data = HTMLLogMarkers.data("Adding data", "Some TEXT data...\r\nHows it going?");
		getLogger().debug(data, "Adding data for some reason...");
		
		data = HTMLLogMarkers.data("Adding data", getDataContent("example.csv"));
		getLogger().debug(data, "Some CSV data...");

		data = HTMLLogMarkers.data("Adding data", getDataContent("example.json"));
		getLogger().debug(data, "Some JSON data...");
		
		data = HTMLLogMarkers.data("Adding data", getDataContent("example.xml"));
		getLogger().debug(data, "Some XML data...");

		return getLogContent().contains("<pre>");
	}

	public boolean addHtmlData() {
		getLogger().debug(HTMLLogMarkers.step(), "HTML Data");
		
		Marker data = HTMLLogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		getLogger().debug(data, "Some <b><i>HTML</i></b> that won't display as HTML plus...");

		// TODO How validate?
		return true;
	}
	
	public boolean addHtmlStatement() {
		getLogger().debug(HTMLLogMarkers.step(), "HTML Statement");
		
		Marker html = HTMLLogMarkers.htmlStatementMarker();
		getLogger().debug(html, "Some <b><i>HTML</i></b> data...");

		// TODO How validate?
		return true;
	}

	public boolean addCombinedHtml() {
		getLogger().debug(HTMLLogMarkers.step(), "Combinded HTML and Statement");
		
		Marker html = HTMLLogMarkers.html("Adding data", "<p>This is some <b><i>HTML</i></b> data...");
		html.add(HTMLLogMarkers.htmlStatementMarker());
		
		getLogger().debug(html, "Some <b><i>Combined HTML Statement</i></b> plus...");

		// TODO How validate?
		return true;
	}

	private String getLogContent() throws IOException {
		FileAppender<?> fileAppender = getHTMLAppender();
		
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
	
	private FileAppender<?> getHTMLAppender() {
		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		for (Logger logger : context.getLoggerList())
		{
		     for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
		           Object enumElement = index.next();
		           if (enumElement instanceof SiftingAppender) {
		        	   SiftingAppender sift = (SiftingAppender)enumElement;
		        	   
		        	    if (sift.getName().equals("FILE-PER-TEST")) {
		        	    	for (Appender<?> appender : sift.getAppenderTracker().allComponents()) {
		        	    		if (appender instanceof FileAppender) {
		        	    			return (FileAppender<?>)appender;
		        	    		}
		        	    	}
		                }
		           }
		     }
		}
		
		return null;
	}
}
