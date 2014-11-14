package org.concordion.ext.loggingFormatter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.concordion.api.Element;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFormatterSpecificationListener implements SpecificationProcessingListener, ConcordionBuildListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFormatterSpecificationListener.class);

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

		try {
			// Copy LogViewer.html to Concordion output location
			String viewerContent = FileUtils.readFileToString(new File(LoggingFormatterSpecificationListener.class.getResource(viewerSource).getFile()));
			viewerContent = viewerContent.replaceAll("LOG_FILE_NAME", logName);
			FileUtils.writeStringToFile(new File(LogbackHelper.getTestPath() + viewerDestination), viewerContent);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return logName;
		}

		return viewerDestination;
	}
}
