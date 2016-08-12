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
	
	private void attchHtmlLayout() {
		layout = LogBackHelper.getHtmlLayout();
		backup = LogBackHelper.backupLayout(layout);
		
		exampleLogListener.setHtmlLayout(layout);
	}
	
	private void restoreHtmlLayout() {
		LogBackHelper.restoreHtmlLayout(backup, layout);	
	}
	
	private void releaseHtmlLayout() {
		exampleLogListener.setHtmlLayout(null);
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
		
		return checkHtmlLogContains("<td class=\"Message\">multiColumnLayout example</td>", result);
	}

	// Log statement is in a single table column
	public boolean singleColumnLayout() {
		boolean result = true;

		resetLogListener();
		
		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		getLogger().debug("singleColumnLayout example");

		restoreHtmlLayout();
		
		return checkHtmlLogContains("<td>singleColumnLayout example HtmlLog.java</td>", result);
	}
	
	public boolean recordStepsUsingLogLevel() {
		boolean result = true;

		resetLogListener();
		
		layout.setStepRecorder(StepRecorder.INFO_LOG_LEVEL.name());
		
		getLogger().info("Step");
		getLogger().debug("Statement");
		
		restoreHtmlLayout();
		
		result = checkHtmlLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkHtmlLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean recordStepsUsingStepMarker() {
		boolean result = true;

		resetLogListener();
		
		layout.setStepRecorder(StepRecorder.STEP_MARKER.name());
		
		getLogger().step("Step");
		getLogger().info("Statement");

		restoreHtmlLayout();
		
		result = checkHtmlLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkHtmlLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean canUseClassicLogger() {
		resetLogListener();

		Logger logger = LoggerFactory.getLogger(HtmlLog.class);
		logger.debug("This uses the classic logger");

		return checkHtmlLogContains("<td class=\"Message\">This uses the classic logger</td>", true);
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
		
		return checkHtmlLogContains("<td class=\"Message\">This is <b>BOLD</b></td>", result);
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
		
		result = checkHtmlLogContains("<td class=\"Message\">Some html will be included below</td>", result);
		result = checkHtmlLogContains("<pre>This is <b>BOLD</b></pre>", result);
				
		return result;
	}
	
	
	public boolean addData() {
		boolean result = true;

		resetLogListener();
		
		getLogger().with()
			.message("Sending SOAP request")
			.data("<soapenv>...</soapenv>")
			.trace();

		result = checkHtmlLogContains("<td class=\"Message\">Sending SOAP request</td>", result);
		result = checkHtmlLogContains("<pre>&lt;soapenv&gt;...&lt;/soapenv&gt;</pre>", result);
				
		return result;
	}
	
	public boolean addScreenshot() {
		boolean result = true;

		resetLogListener();
		
		getLogger().with()
			.message("Clicking 'Login'")
			.screenshot()
			.trace();

		result = checkHtmlLogContains("<td class=\"Message\">Clicking &#39;Login&#39;</td>", result);
		result = checkHtmlLogContains("<pre><a href=\"HtmlLogLogScreenShot", result);
		
		return result;
	}
	
	public boolean throwException() {
		boolean result = true;

		resetLogListener();
		getLogger().error("Something when wrong", new Exception("me"));
		
		result = checkHtmlLogContains("<td class=\"Message\">Something when wrong</td>", result);
		result = checkHtmlLogContains("<input id=\"stackTraceButton", result);

		return result;
	}
	
	public boolean locationAware() throws IOException {
		boolean result = true;
		LocationHelper helper = new LocationHelper();

		resetLogListener();
		
		// Parent Class
		logParentClassLocationAware();
		result = checkHtmlLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);

		// Location Unaware
		resetLogListener();
		helper.logLocationUnaware();
		result = checkHtmlLogContains("<td class=\"FileOfCaller\">LocationHelper.java</td>", result);
		
		// Location Aware
		resetLogListener();
		helper.logLocationAware();
		result = checkHtmlLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);
		
		return result;
	}
}
