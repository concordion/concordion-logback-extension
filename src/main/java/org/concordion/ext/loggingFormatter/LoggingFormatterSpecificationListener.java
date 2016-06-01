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
import org.concordion.ext.LoggingFormatterExtension.StepRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ExampleListener, ThrowableCaughtListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFormatterSpecificationListener.class);
	private ILoggingAdaptor loggingAdaptor;
	private boolean useLogFileViewer;
	private boolean logExample = true;
	private LogLevel logExceptions = LogLevel.EXCEPTION_CAUSES;
	private StepRecorder stepRecorder = StepRecorder.STEP_MARKER;
	
	public void setLogExample(boolean value) {
		this.logExample = value;
	}
	
	public void setLogExceptions(LogLevel value) {
		this.logExceptions = value;
	}

	public void recordStepsUsing(StepRecorder stepRecorder) {
		this.stepRecorder = stepRecorder;
	}
	
	public LoggingFormatterSpecificationListener(ILoggingAdaptor loggingAdaptor, boolean useLogFileViewer) {
		this.loggingAdaptor = loggingAdaptor;
		this.useLogFileViewer = useLogFileViewer;
	}

////////////////////////////// Specification Processing Listener //////////////////////////////
	/**
	 * Gets the base output folder used by concordion - copied from ConcordionBuilder.getBaseOutputDir()
	 * 
	 * @return base output folder 
	 */
	private static String getConcordionBaseOutputDir() {
		String outputPath = System.getProperty("concordion.output.dir");
		
		if (outputPath == null) {
			outputPath = new File(System.getProperty("java.io.tmpdir"), "concordion").getAbsolutePath();
		}

		outputPath = outputPath.replaceAll("\\\\", "/");
		if (!outputPath.endsWith("/")) {
			outputPath = outputPath + "/";
		}
		return outputPath;
	}
	
	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		String testPath = getConcordionBaseOutputDir() + getTestPath(event.getResource().getPath());
		
		loggingAdaptor.startLogFile(testPath);
	}

	private String getTestPath(String testPath) {
		if (testPath.indexOf(".") > 0) {
			testPath = testPath.substring(0, testPath.indexOf("."));
		}
		
		if (testPath.startsWith("/") || testPath.startsWith("\\")) {
			testPath = testPath.substring(1);
		}
		
		return testPath;
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		try {
			if (loggingAdaptor.doesLogfileExist()) {
				appendLogFileLinkToFooter(event, createViewer());
			}
		} finally  {
			loggingAdaptor.stopLogFile();		
		}
	}

	private void appendLogFileLinkToFooter(final SpecificationProcessingEvent event, String logURL) {
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

		LOGGER.info("Example: " + getExampleTitle(event.getElement()));
	}

	@Override
	public void afterExample(ExampleEvent event) {
		if (!logExample) return;
		
		LOGGER.info("End Example: " + getExampleTitle(event.getElement()));
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
    		while (cause != null) {
    			if (!message.isEmpty()) {
    				 message += "\n\n";
    			}
    			
    			message += cause.getMessage();
	    		cause = cause.getCause();
	    	}
    		break;
    		
    	case EXCEPTION_WITH_STACK_TRACE:
    		message = cause.getMessage() + "\n" + getStackTrace(cause);
    		break;
    		
    	case NONE:
    		return;
    	}
    	
    	// Indent multi-line errors to make it easier to scan the log
    	message = message.replace("\r\n", "\n");
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
