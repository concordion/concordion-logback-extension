package org.concordion.ext.loggingFormatter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.concordion.api.Element;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.ext.LoggingFormatterExtension.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ExampleListener, ThrowableCaughtListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFormatterSpecificationListener.class);
	private ILoggingAdaptor loggingAdaptor;
	private boolean useLogFileViewer;
	private boolean logExample = true;
	private LogLevel logExceptions = LogLevel.EXCEPTION_WITH_STACK_TRACE;
	
	public void setLogExample(boolean value) {
		this.logExample = value;
	}
	
	public void setLogExceptions(LogLevel value) {
		this.logExceptions = value;
	}
	
	public LoggingFormatterSpecificationListener(ILoggingAdaptor loggingAdaptor, boolean useLogFileViewer) {
		this.loggingAdaptor = loggingAdaptor;
		this.useLogFileViewer = useLogFileViewer;
	}

////////////////////////////// Specification Processing Listener //////////////////////////////
	
	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		// We don't need to do anything here
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (!loggingAdaptor.doesLogfileExist()) {
			return;
		}

		// Update spec with link to viewer
		String logURL = createViewer();
		
		if(logURL.isEmpty()) {
			return;
		}

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

	private String createViewer() {
		String logName = loggingAdaptor.getLogName();
		
		if(logName.isEmpty()) {
			return "";
		}
		
		String viewerSource = "LogViewer.html";
		String viewerDestination = logName.replaceFirst(".log", "") + viewerSource;

		if (useLogFileViewer) {			
			try {
				// Copy LogViewer.html to Concordion output location
				String viewerContent = IOUtils.toString(LoggingFormatterSpecificationListener.class.getResourceAsStream(viewerSource));
				
				viewerContent = viewerContent.replaceAll("LOG_FILE_NAME", logName);
				viewerContent = viewerContent.replaceAll("LOG_FILE_CONTENT", Matcher.quoteReplacement(getLogContent(loggingAdaptor.getLogPath() + logName)));			
				
				FileUtils.writeStringToFile(new File(loggingAdaptor.getLogPath() + viewerDestination), viewerContent);
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				viewerDestination = logName;
			}
		} else {
			viewerDestination = logName;
		}

		return viewerDestination;
	}
	
	private String getLogContent(String logFile) {
		
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
		if (!logExample) return;

		LOGGER.info("");
		LOGGER.info("=== Start: " + getExampleTitle(event.getElement()) + " ===");
	}

	@Override
	public void afterExample(ExampleEvent event) {
		if (!logExample) return;
		
		LOGGER.info("=== End: " + getExampleTitle(event.getElement()) + " ===");
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
		if (logExceptions == LogLevel.NONE) return;
		
    	String message = event.getThrowable().getMessage();

    	if (logExceptions == LogLevel.EXCEPTION_WITH_STACK_TRACE) {
    		message += "\n" + getStackTrace(event.getThrowable());
    	}
    	
    	// Indent multi-line errors to make it easier to scan the log
    	message = message.replace("\r\n", "\r\n\t");
    	message = message.replace("\n", "\n\t");
    	
		LOGGER.error(message);		
	}
    
    private String getStackTrace(final Throwable throwable) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream printStream = new PrintStream(baos);
		throwable.printStackTrace(printStream);
		String exceptionStr = "";
		try {
			exceptionStr = baos.toString("UTF-8");
		} catch (Exception ex) {
			exceptionStr = "Unavailable";
		}
		return exceptionStr;
	}
}
