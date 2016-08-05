package specification;

import java.io.IOException;
import org.concordion.api.BeforeSpecification;
import ch.qos.logback.ext.html.Format;
import ch.qos.logback.ext.html.HTMLLayout;
import ch.qos.logback.ext.html.StepRecorder;
import test.concordion.logback.DummyScreenshotTaker;
import test.concordion.logback.LogBackHelper;
import test.concordion.logback.LocationHelper;

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
		retrieveLayout();

		layout.setFormat(Format.COLUMN.name());
		layout.setPattern("%date{HH:mm:ss.SSS}%message%file%line");

		getLogger().debug("multiColumnLayout example");
		
		restoreLayout();

		return exampleLogListener.getStreamContent().contains("<td class=\"Message\">multiColumnLayout example</td>");
	}

	// Log statement is in a single table column
	public boolean singleColumnLayout() {
		retrieveLayout();
		
		layout.setFormat(Format.STRING.name());
		layout.setPattern("%message %file");

		getLogger().debug("singleColumnLayout example");

		restoreLayout();

		return exampleLogListener.getStreamContent().contains("<td>singleColumnLayout example HtmlLog.java</td>");
	}
	
	public boolean recordStepsUsingLogLevel() {
		retrieveLayout();
		
		layout.setStepRecorder(StepRecorder.INFO_LOG_LEVEL.name());
		
		getLogger().info("Step");
		getLogger().debug("Statement");
		
		restoreLayout();

		String log = exampleLogListener.getStreamContent();
		
		return log.contains("<td colspan=\"5\">Step</td>") && log.contains("<td class=\"Message\">Statement</td>");
		
	}
	
	public boolean recordStepsUsingStepMarker() {
		retrieveLayout();
		
		layout.setStepRecorder(StepRecorder.STEP_MARKER.name());
		
		getLogger().step("Step");
		getLogger().info("Statement");
		
		restoreLayout();

		String log = exampleLogListener.getStreamContent();
		
		return log.contains("<td colspan=\"5\">Step</td>") && log.contains("<td class=\"Message\">Statement</td>");
	}
	
	public boolean canUseReportLogger() {
		// TODO how pass in snippet?
		return false;
	}
	
	public boolean addHtmlMessage() {
		retrieveLayout();
		
		getLogger().with()
    		.htmlMessage("<b>This is bold</b>")
    		.trace();
		
		return exampleLogListener.getStreamContent().contains("<td class=\"Message\"><b>This is bold</b></td>");
	}

	public boolean addHtmlData() {
		retrieveLayout();
		
		getLogger().with()
			.message("Some html will be included below")
			.html("This is <b>BOLD</b>")
			.trace();
		
		String log = exampleLogListener.getStreamContent();
				
		return log.contains("<td class=\"Message\">Some html will be included below</td>") &&
				log.contains("<pre>This is <b>BOLD</b></pre>");
	}
	
	
	public boolean addData() {
		retrieveLayout();
		
		getLogger().with()
			.message("Sending SOAP request")
			.data("<soapenv>...</soapenv>")
			.trace();
			
		String log = exampleLogListener.getStreamContent();
				
		return log.contains("<td class=\"Message\">Sending SOAP request</td>") &&
				log.contains("<pre>&lt;soapenv&gt;...&lt;/soapenv&gt;</pre>");
	}
	
	public boolean addScreenshot() {
		retrieveLayout();
		
		getLogger().with()
			.message("Clicking 'Login'")
			.screenshot(getLoggingAdaptor().getLogFile(), new DummyScreenshotTaker())
			.trace();
		
		String log = exampleLogListener.getStreamContent();
		
		return log.contains("<td class=\"Message\">Clicking &#39;Login&#39;</td>") &&
				log.contains("<pre><a href=\"HtmlLogLogScreenShot");
	}
	
	public boolean throwException() {
		retrieveLayout();
		
		getLogger().error("Something when wrong", new Exception("me"));
		
		String log = exampleLogListener.getStreamContent();
		
		return log.contains("<td class=\"Message\">Something when wrong</td>") &&
				log.contains("<input id=\"stackTraceButton");
	}
	

	public boolean locationAware() throws IOException {
		retrieveLayout();
		
		LocationHelper helper = new LocationHelper();

		helper.logLocationUnaware(getLoggingAdaptor());
		
		String log = exampleLogListener.getStreamContent();
		
		if (!log.contains("<td class=\"FileOfCaller\">LocationHelper.java</td>")) {
			return false;
		}
		
		exampleLogListener.resetStream();
		helper.logLocationAware(getLoggingAdaptor());
		
		log = exampleLogListener.getStreamContent();
		
		return log.contains("<td class=\"FileOfCaller\">HtmlLog.java</td>");
		
		
	}
	
	

	


}
