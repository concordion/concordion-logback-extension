package specification;

import java.io.IOException;

import org.concordion.api.BeforeSpecification;

import ch.qos.logback.ext.html.Format;
import ch.qos.logback.ext.html.HTMLLayout;
import ch.qos.logback.ext.html.StepRecorder;
import test.concordion.logback.LocationHelper;
import test.concordion.logback.LogBackHelper;

public class HtmlLog extends BaseFixture {
	private HTMLLayout layout;
	private HTMLLayout backup;
	
	private void retrieveLayout() {
		layout = LogBackHelper.getHtmlLayout();
		backup = LogBackHelper.backupLayout(layout);
		
		exampleLogListener.setLayout(layout);
		exampleLogListener.resetStream();
	}
	
	private void restoreLayout() {
		exampleLogListener.setLayout(null);
		
		LogBackHelper.restoreLayout(backup, layout);	
	}
	
	@BeforeSpecification
	private final void beforeSpecification() {
		// Force the logger to create the various appenders and layouts required for these tests
		getLogger().debug("nothing");
	}
	
	// HTML-FILE-PER-TEST appender is attached to the root logger 
	public boolean isHtmlAppenderConfigured() {
		return LogBackHelper.getHtmlFilePerTestSiftingAppender() != null;
	}

	// Log statement is in table column format
	public boolean multiColumnLayout() {
		boolean result = true;

		retrieveLayout();

		layout.setFormat(Format.COLUMN.name());
		layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");

		getLogger().debug("multiColumnLayout example");
		
		restoreLayout();

		return checkLogContains("<td class=\"Message\">multiColumnLayout example</td>", result);
	}

	// Log statement is in a single table column
	public boolean singleColumnLayout() {
		boolean result = true;

		retrieveLayout();
		
		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		getLogger().debug("singleColumnLayout example");

		restoreLayout();

		return checkLogContains("<td>singleColumnLayout example HtmlLog.java</td>", result);
	}
	
	public boolean recordStepsUsingLogLevel() {
		boolean result = true;

		retrieveLayout();
		
		layout.setStepRecorder(StepRecorder.INFO_LOG_LEVEL.name());
		
		getLogger().info("Step");
		getLogger().debug("Statement");
		
		restoreLayout();

		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean recordStepsUsingStepMarker() {
		boolean result = true;

		retrieveLayout();
		
		layout.setStepRecorder(StepRecorder.STEP_MARKER.name());
		
		getLogger().step("Step");
		getLogger().info("Statement");
		
		restoreLayout();

		result = checkLogContains("<td colspan=\"5\">Step</td>", result);
		result = checkLogContains("<td class=\"Message\">Statement</td>", result);
		
		return result;
	}
	
	public boolean canUseReportLogger() {
		// TODO how pass in snippet?
		return true;
	}
	
	public boolean addHtmlMessage() {
		boolean result = true;

		retrieveLayout();
		
		getLogger().with()
    		.htmlMessage("<b>This is bold</b>")
    		.trace();
		
		return checkLogContains("<td class=\"Message\"><b>This is bold</b></td>", result);
	}

	public boolean addHtmlData() {
		boolean result = true;

		retrieveLayout();
		
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

		retrieveLayout();
		
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

		retrieveLayout();
		
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

		retrieveLayout();
		
		getLogger().error("Something when wrong", new Exception("me"));
		
		result = checkLogContains("<td class=\"Message\">Something when wrong</td>", result);
		result = checkLogContains("<input id=\"stackTraceButton", result);

		return result;
	}
	
	public boolean locationAware() throws IOException {
		boolean result = true;
		LocationHelper helper = new LocationHelper();

		retrieveLayout();
		
		// Parent Class
		logParentClassLocationAware();
		result = checkLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);

		// Location Unaware
		exampleLogListener.resetStream();
		helper.logLocationUnaware();
		result = checkLogContains("<td class=\"FileOfCaller\">LocationHelper.java</td>", result);
		
		// Location Aware
		exampleLogListener.resetStream();
		helper.logLocationAware();
		result = checkLogContains("<td class=\"FileOfCaller\">HtmlLog.java</td>", result);
		
		return result;
	}
}
