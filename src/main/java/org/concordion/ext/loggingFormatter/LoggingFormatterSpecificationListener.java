package org.concordion.ext.loggingFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.concordion.api.Element;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.api.listener.AssertTrueListener;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.slf4j.ILoggingAdaptor;
import org.concordion.slf4j.ext.FluentLogger;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;
import org.slf4j.Marker;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ExampleListener, ThrowableCaughtListener, AssertEqualsListener, AssertTrueListener, AssertFalseListener {
	private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(LoggingFormatterSpecificationListener.class);
	private final ILoggingAdaptor loggingAdaptor;
	private boolean useLogFileViewer = false;
	private boolean handleFailureAndThrowableEvents = true;
	private String testPath = "";
			
	private List<Marker> markers = new ArrayList<Marker>();

	public void setUseLogFileViewer(boolean useLogFileViewer) {
		this.useLogFileViewer = useLogFileViewer;
	}

	public void setHandleFailureAndThrowableEvents(boolean handleFailureAndThrowableEvents) {
		this.handleFailureAndThrowableEvents = handleFailureAndThrowableEvents;
	}
	
	public void setScreenshotTaker(ScreenshotTaker screenshotTaker) {
		FluentLogger.addScreenshotTaker(screenshotTaker);
	}
	
	public ILoggingAdaptor getLoggingAdaptor() {
		return this.loggingAdaptor;
	}

	public void registerMarker(Marker marker) {
		if (marker != null) {
			markers.add(marker);
		}
	}

	public LoggingFormatterSpecificationListener(ILoggingAdaptor loggingAdaptor) {
		this.loggingAdaptor = loggingAdaptor;
		
		FluentLogger.addLoggingAdaptor(this.loggingAdaptor);
	}

////////////////////////////// Specification Processing Listener //////////////////////////////
	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		testPath = event.getResource().getPath();

		loggingAdaptor.startSpecificationLogFile(testPath);
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		try {
            File logFile = loggingAdaptor.getLogFile();
            if (logFile.exists()) {
                appendLogFileLinkToFooter(event, logFile);
            }
		} finally {
			loggingAdaptor.stopLogFile();
			FluentLogger.removeLoggingAdaptor();
			FluentLogger.removeScreenshotTaker();
		}
	}

	private void appendLogFileLinkToFooter(final SpecificationProcessingEvent event, File logFile) {
		String logURL = createViewer(logFile);

		Element body = event.getRootElement().getFirstChildElement("body");

		if (body != null) {
			Element[] divs = body.getChildElements("div");
			for (Element div : divs) {
				if ("footer".equals(div.getAttributeValue("class"))) {
					Element newDiv = new Element("div");
					newDiv.addStyleClass("testTime");

					Element anchor = new Element("a");
					anchor.addAttribute("style", "font-weight: bold; text-decoration: none; color: #89C;");
					anchor.addAttribute("href", logURL);
					anchor.appendText("Log File");

					newDiv.appendChild(anchor);
					div.appendChild(newDiv);

					break;
				}
			}
		}
	}

	private String createViewer(File logFile) {
		String logName = logFile.getName();

		if (!useLogFileViewer) {
			return logName;
		}

		if (logName.toLowerCase().endsWith(".html")) {
			return logName;
		}


		int i = logName.lastIndexOf('.');
		if (i > 0) {
			logName = logName.substring(0, i);
		}

		logName = logName + "LogViewer.html";

		try {
			// Copy LogViewer.html to Concordion output location
			String viewerContent = IOUtils.toString(LoggingFormatterSpecificationListener.class.getResourceAsStream("LogViewer.html"), Charset.defaultCharset());

			viewerContent = viewerContent.replaceAll("LOG_FILE_NAME", logName);
			viewerContent = viewerContent.replaceAll("LOG_FILE_CONTENT", Matcher.quoteReplacement(getLogContent(logFile)));
			
			FileUtils.writeStringToFile(new File(logFile.getParent(), logName), viewerContent, Charset.defaultCharset());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			logName = logFile.getName();
		}

		return logName;
	}
	
	private String getLogContent(File logFile) {
		
		StringBuilder logContent = new StringBuilder();
		BufferedReader br = null;
		
		try {			
			String line;
			String prevline = null;
			String lineLevel = "";
			String prevLineLevel = "";
			int lineNumber = 0;
			
			br = new BufferedReader(new FileReader(logFile));
				
			while ((line = br.readLine()) != null) {
				lineNumber++;
				
				line = line.replaceAll("<", "&lt;");
				line = line.replaceAll(">", "&gt;");
				
				// starts with a date
				if (line.matches("^.*[0-9 -.:].*")) {
					if (line.contains("INFO ")) lineLevel = "info";
					if (line.contains("DEBUG ")) lineLevel = "debug";
					if (line.contains("TRACE ")) lineLevel = "trace";
					if (line.contains("WARN ")) lineLevel = "warn";
					if (line.contains("ERROR ")) lineLevel = "error";
					
					if(prevLineLevel != lineLevel && (lineLevel == "debug" || lineLevel == "trace")) {
						if (prevline != null) {
							prevline = prevline.replace("<li class=\"line ", "<li class=\"line split-" + lineLevel + "-levels ");
						}
					}
					
					prevLineLevel = lineLevel;
				}
				
				if (prevline != null) {
					logContent.append(prevline).append("\n");
				}
				
				prevline = "<li class=\"line " + lineLevel + " " + lineLevel + "-color" + "\"><div class=\"line-numbers\">" + Integer.toString(lineNumber) + "</div><pre>" + line + "</pre></li>";
			}
			
			if (prevline != null) {
				logContent.append(prevline).append("\n");
			}
			
			br.close();
		} catch (Exception e) {
			logContent.append(e.getMessage());
		}
		
		return logContent.toString();

	}
	
////////////////////////////// Example Listener //////////////////////////////

	@Override
	public void beforeExample(ExampleEvent event) {
		loggingAdaptor.startExampleLogFile(testPath, event.getExampleName());
	}

	@Override
	public void afterExample(ExampleEvent event) {
		try {
            File logFile = loggingAdaptor.getLogFile();
            if (logFile.exists()) {
                appendLogFileLinkToExample(event, logFile);
			}
		} finally  {
			loggingAdaptor.stopLogFile();		
		}
	}
	
	private void appendLogFileLinkToExample(ExampleEvent event, File log) {
		String logURL = createViewer(log);

		Element anchor = new Element("a");
		anchor.addAttribute("style", "font-size: 9pt; font-weight: bold; float: right; display: inline-block; margin-top: 20px; text-decoration: none; color: #89C;");
		anchor.addAttribute("href", logURL);
		anchor.appendText("Log File");

		event.getElement().prependChild(anchor);
	}

////////////////////////////// Throwable Listener //////////////////////////////
	
	@Override
	public void throwableCaught(ThrowableCaughtEvent event) {
		if (!handleFailureAndThrowableEvents) {
			return;
		}
		
		Throwable cause = event.getThrowable();
		
		FluentLogger logger = LOGGER.with()
				.message("Exception thrown while evaluating expression '{}':\r\n\t{}", event.getExpression(), cause.getMessage());

		if (FluentLogger.hasScreenshotTaker()) {
			logger.screenshot();
		}

		for (Marker marker : markers) {
			logger.marker(marker);
		}

		logger.error(cause);
	}

	@Override
	public void successReported(AssertSuccessEvent event) {
	}
	
	@Override
	public void failureReported(AssertFailureEvent event) {
		if (!handleFailureAndThrowableEvents) {
			return;
		}
		
		StringBuilder sb = new StringBuilder().append("Test failed");
		
		if(event.getExpected() != null) {
			sb.append("\n").append("Expected: ").append(event.getExpected());
		}
		
		if(event.getActual() != null) {
			sb.append("\n").append("Actual: ").append(event.getActual().toString());
		}

		FluentLogger logger = LOGGER.with()
				.message(sb.toString());

		if (FluentLogger.hasScreenshotTaker()) {
			logger.screenshot();
		}

		for (Marker marker : markers) {
			logger.marker(marker);
		}

		logger.error();
	}
}
