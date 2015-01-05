package org.concordion.ext.loggingFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.concordion.api.Element;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ConcordionBuildListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFormatterSpecificationListener.class);
	private boolean useLogFileViewer;
	
	public LoggingFormatterSpecificationListener(boolean useLogFileViewer) {
		this.useLogFileViewer = useLogFileViewer;
	}

	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		// We don't need to do anything here
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (!LogbackHelper.isLoggingFilePerTest()) {
			return;
		}

		// Update spec with link to viewer
		String logURL = createViewer();

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

	@Override
	public void concordionBuilt(final ConcordionBuildEvent event) {
		event.getTarget();
	}

	private String createViewer() {
		String testName = LogbackHelper.getTestClassName();
		String logName = testName + ".log";
		String viewerSource = "LogViewer.html";
		String viewerDestination = testName + viewerSource;

		if (useLogFileViewer) {			
			try {
				// Copy LogViewer.html to Concordion output location
				String viewerContent = IOUtils.toString(LoggingFormatterSpecificationListener.class.getResourceAsStream(viewerSource));
				
				viewerContent = viewerContent.replaceAll("LOG_FILE_NAME", logName);
				viewerContent = viewerContent.replaceAll("LOG_FILE_CONTENT", Matcher.quoteReplacement(getLogContent(LogbackHelper.getTestPath() + logName)));			
				
				FileUtils.writeStringToFile(new File(LogbackHelper.getTestPath() + viewerDestination), viewerContent);
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
}
