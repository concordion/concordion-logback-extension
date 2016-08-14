package specification;

import java.io.IOException;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.ext.html.Format;
import ch.qos.logback.ext.html.HTMLLayout;
import ch.qos.logback.ext.html.StepRecorder;
import test.concordion.logback.LocationHelper;
import test.concordion.logback.LogBackHelper;

public class HtmlLog extends BaseFixture {
	private HTMLLayout layout;
	private HTMLLayout backup;
	
	@BeforeSpecification
	private final void beforeSpecification() {
		// Force the logger to create the various appenders and layouts required for these tests
		getLogger().debug("preparing logger for testing");
		attchHtmlLayout();
	}
	
	@AfterSpecification
	private final void afterSpecification() {
		releaseHtmlLayout();
	}

	//// Helper Methods
	private void attchHtmlLayout() {
		layout = LogBackHelper.getHtmlLayout();
		
		backup = new HTMLLayout();
		copy(layout, backup);
		
		exampleLogListener.setLayout(layout);
	}

	private void releaseHtmlLayout() {
		exampleLogListener.setLayout(null);
	}
	
	private void attchConsoleLayout() {
		exampleLogListener.setConsoleLayout(LogBackHelper.getConsoleLayout());
	}
	
	private void releaseConsoleLayout() {
		exampleLogListener.setConsoleLayout(null);
	}
	
	private void resetLogListener() {
		exampleLogListener.reset();	
	}

	private void restoreHtmlLayout() {
		copy(backup, layout);
	}

	private void copy(HTMLLayout src, HTMLLayout dest) {
		dest.setStylesheet(src.getStylesheet());
		dest.setFormat(src.getFormat());
		dest.setPattern(src.getPattern());
		dest.setStepRecorder(src.getStepRecorder());
	}
	//// END Helper Methods
	
	
	// HTML-FILE-PER-TEST appender is attached to the root logger 
	public boolean isHtmlAppenderConfigured() {
		return LogBackHelper.isConfiguredForHtmlLog();
	}

	// Log statement is in table column format
	public boolean multiColumnLayout() {
		boolean result = true;

		resetLogListener();

		layout.setFormat(Format.COLUMN.name());
		layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");

		getLogger().debug("multiColumnLayout example");
		
		restoreHtmlLayout();
		
		return checkLogContains("<td class=\"Message\">multiColumnLayout example</td>", result);
	}

	// Log statement is in a single table column
	public boolean singleColumnLayout() {
		boolean result = true;

		resetLogListener();
		
		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		getLogger().debug("singleColumnLayout example");

		restoreHtmlLayout();
		
		return checkLogContains("<td>singleColumnLayout example HtmlLog.java</td>", result);
	}
	
	public boolean recordStepsUsingLogLevel() {
		boolean result = true;

		resetLogListener();
		
		layout.setStepRecorder(StepRecorder.INFO_LOG_LEVEL.name());
		
		getLogger().info("Step");
		getLogger().debug("Statement");
		
		restoreHtmlLayout();
		
		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean recordStepsUsingStepMarker() {
		boolean result = true;

		resetLogListener();
		
		layout.setStepRecorder(StepRecorder.STEP_MARKER.name());
		
		getLogger().step("Step");
		getLogger().info("Statement");

		restoreHtmlLayout();
		
		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean canUseClassicLogger() {
		resetLogListener();

		Logger logger = LoggerFactory.getLogger(HtmlLog.class);
		logger.debug("This uses the classic logger");

		return checkLogContains("<td class=\"Message\">This uses the classic logger</td>", true);
	}

	public boolean canUseReportLogger() {
		// TODO Nigel: how pass in snippet?
		return true;
	}
	
	public boolean addHtmlMessage() {
		boolean result = true;

		resetLogListener();
		attchConsoleLayout();
		
		getLogger().with()
    		.htmlMessage("This is <b>BOLD</b>")
    		.trace();
		
		releaseConsoleLayout();
		
		return checkLogContains("<td class=\"Message\">This is <b>BOLD</b></td>", result);
	}

	public boolean consoleLogIsPlainText() {
		return checkConsoleLogContains("This is BOLD", true);
	}
	
	public boolean addHtmlData() {
		boolean result = true;

		resetLogListener();
		
		getLogger().with()
			.message("Some html will be included below")
			.html("This is <b>BOLD</b>")
			.trace();
		
		result = checkLogContains("<td class=\"Message\">Some html will be included below</td>", result);
		result = checkLogContains("<pre>This is <b>BOLD</b></pre>", result);
				
		return result;
	}
	
	
	public boolean addData() {
		boolean result = true;

		resetLogListener();
		
		getLogger().with()
			.message("Sending SOAP request")
			.data("<soapenv>...</soapenv>")
			.trace();

		result = checkLogContains("<td class=\"Message\">Sending SOAP request</td>", result);
		result = checkLogContains("<pre>&lt;soapenv&gt;...&lt;/soapenv&gt;</pre>", result);
				
		return result;
	}
	
	public boolean addScreenshot() {
		boolean result = true;

		resetLogListener();
		
		getLogger().with()
			.message("Clicking 'Login'")
			.screenshot()
			.trace();

		result = checkLogContains("<td class=\"Message\">Clicking &#39;Login&#39;</td>", result);
		result = checkLogContains("<pre><a href=\"HtmlLogLogScreenShot", result);
		
		return result;
	}
	
	public boolean throwException() {
		boolean result = true;

		resetLogListener();
		getLogger().error("Something when wrong", new Exception("me"));
		
		result = checkLogContains("<td class=\"Message\">Something when wrong</td>", result);
		result = checkLogContains("<input id=\"stackTraceButton", result);

		return result;
	}
	
	public boolean locationAware() throws IOException {
		boolean result = true;
		LocationHelper helper = new LocationHelper();

		resetLogListener();
		
		// Parent Class
		logParentClassLocationAware();
		result = checkLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);

		// Location Unaware
		resetLogListener();
		helper.logLocationUnaware();
		result = checkLogContains("<td class=\"FileOfCaller\">LocationHelper.java</td>", result);
		
		// Location Aware
		resetLogListener();
		helper.logLocationAware();
		result = checkLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);
		
		return result;
	}
}
