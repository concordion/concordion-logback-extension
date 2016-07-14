package org.concordion.ext.loggingFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.ext.LoggingFormatterExtension.LogLevel;
import org.concordion.ext.LoggingFormatterExtension.Split;
import org.slf4j.ext.ReportLogger;
import org.slf4j.ext.ReportLoggerFactory;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ExampleListener, ThrowableCaughtListener {
	private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(LoggingFormatterSpecificationListener.class);
	private final ILoggingAdaptor loggingAdaptor;
	private final Resource stylesheetResource;
	private final boolean useLogFileViewer;
	private boolean logExampleStartAndEnd = false;
	private LogLevel logExceptions = LogLevel.EXCEPTION;
	private String testPath = "";
	private Split splitBy = Split.EXAMPLE;
			
	public void setLogExampleStartAndEnd(boolean value) {
		this.logExampleStartAndEnd = value;
	}
	
	public void setLogExceptions(LogLevel value) {
		this.logExceptions = value;
	}

	public void setSplitBy(Split split) {
		this.splitBy = split;
	}

	public ILoggingAdaptor getLoggingAdaptor() {
		return this.loggingAdaptor;
	}

	public LoggingFormatterSpecificationListener(ILoggingAdaptor loggingAdaptor, Resource stylesheetResource, boolean useLogFileViewer) {
		this.loggingAdaptor = loggingAdaptor;
		this.stylesheetResource = stylesheetResource;
		this.useLogFileViewer = useLogFileViewer;
	}

////////////////////////////// Specification Processing Listener //////////////////////////////
	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		testPath = event.getResource().getPath();

		loggingAdaptor.startSpecificationLogFile(testPath, event.getResource().getRelativePath(stylesheetResource));
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		try {
			if (loggingAdaptor.logFileExists()) {
				appendLogFileLinkToFooter(event, loggingAdaptor.getLogFile());
			}
		} finally {
			loggingAdaptor.stopLogFile();
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
			logName = logName.substring(i + 1);
		}

		logName = logName + "LogViewer.html";

		try {
			// Copy LogViewer.html to Concordion output location
			String viewerContent = IOUtils.toString(LoggingFormatterSpecificationListener.class.getResourceAsStream("LogViewer.html"));

			viewerContent = viewerContent.replaceAll("LOG_FILE_NAME", logName);
			viewerContent = viewerContent.replaceAll("LOG_FILE_CONTENT", Matcher.quoteReplacement(getLogContent(logFile)));

			FileUtils.writeStringToFile(new File(logName), viewerContent);
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
		if (splitBy == Split.EXAMPLE) {
			loggingAdaptor.startExampleLogFile(testPath, event.getExampleName());
		}
		
		if (logExampleStartAndEnd) {
			LOGGER.step("Example: " + getExampleTitle(event.getElement()));
		}
	}

	@Override
	public void afterExample(ExampleEvent event) {
		if (logExampleStartAndEnd) {
			LOGGER.step("End Example: {}", getExampleTitle(event.getElement()));
		}
		
		if (splitBy == Split.EXAMPLE) {
			try {
				if (loggingAdaptor.logFileExists()) {
					appendLogFileLinkToExample(event, loggingAdaptor.getLogFile().getName());
				}
			} finally  {
				loggingAdaptor.stopLogFile();		
			}
		}
	}
	
	private void appendLogFileLinkToExample(ExampleEvent event, String logURL) {
		Element anchor = new Element("a");
		anchor.addAttribute("style", "font-weight: bold; text-decoration: none; color: #89C; float: right; display: inline-block; margin-top: 20px;");
		anchor.addAttribute("href", logURL);
		anchor.appendText("Log File");

		event.getElement().prependChild(anchor);
	}

	public String getExampleTitle(Element element) {
		String title = element.getAttributeValue("example", "http://www.concordion.org/2007/concordion"); 
		
		for (int i = 1; i < 5; i++) {
			Element header = element.getFirstChildElement("h" + String.valueOf(i));
			
			if (header != null) {
				title = header.getText();
				break;
			}		
		}

		return title;
	}
	
////////////////////////////// Throwable Listener //////////////////////////////
	
	@Override
	public void throwableCaught(ThrowableCaughtEvent event) {
		String message = "";
    	Throwable cause = event.getThrowable();

    	switch (logExceptions) {
    	case EXCEPTION:
    		message = cause.getMessage();
    		break;
    		
    	case EXCEPTION_CAUSES:
			Throwable priorCause = cause;

			while (priorCause != null) {
    			if (!message.isEmpty()) {
    				 message += "\n\n";
    			}
    			
				message += priorCause.getMessage();
				priorCause = priorCause.getCause();
	    	}
    		break;
    		
    	case NONE:
    		return;
    	}
    	
    	// Indent multi-line errors to make it easier to scan the log
    	message = message.replace("\r\n", "\n");
    	message = message.replace("\n", "\n\t");
    	
		LOGGER.error(message, cause);
	}
}
