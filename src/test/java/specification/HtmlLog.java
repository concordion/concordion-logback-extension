package specification;

import java.io.IOException;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.ext.html.HTMLLayout;
import test.concordion.logback.LocationHelper;
import test.concordion.logback.LogBackHelper;

public class HtmlLog extends BaseFixture {
	private static final String HTML_FRAGMENT = "<span concordion:execute=\"logSomething()\"></span>";
	private static final String FIXTURE_CLASSNAME = "Test";
	private static final String FIXTURE_START = "import org.slf4j.ext.ReportLogger;" + System.lineSeparator() +
												"import org.slf4j.ext.ReportLoggerFactory;" + System.lineSeparator() +
												"import test.concordion.logback.DummyScreenshotTaker;" + System.lineSeparator() + 
												System.lineSeparator() +
												"public class Test {" + System.lineSeparator() +
												"    private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(Test.class);" + System.lineSeparator() +
												System.lineSeparator() +
												"    public void logSomething() {" + System.lineSeparator();
	private static final String FIXTURE_STOP =  "    }" + System.lineSeparator() + 
												"}";
    		
	private HTMLLayout layout;
	
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
	//// END Helper Methods
	
	
	// HTML-FILE-PER-TEST appender is attached to the root logger 
	public boolean isHtmlAppenderConfigured() {
		return LogBackHelper.isConfiguredForHtmlLog();
	}
	
	public boolean canUseClassicLogger() {
		resetLogListener();
 
		Logger logger = LoggerFactory.getLogger(HtmlLog.class);
		logger.debug("This uses the classic logger");

		return checkLogContains("<td class=\"Message\">This uses the classic logger</td>", true);
	}

	public boolean canUseReportLogger(String javaFragment, String logMessage) throws Exception {
		resetLogListener();

		processHtmlAndJava(HTML_FRAGMENT, javaFragment);

		return checkLogContains("<td class=\"Message\">" + logMessage + "</td>", true);
	}
	
	public String getLogMessage(String javaFragment) throws Exception {
		resetLogListener();

		processHtmlAndJava(HTML_FRAGMENT, FIXTURE_START + javaFragment + FIXTURE_STOP);

		return getLogMessage();
	}
	
	public boolean consoleLogIsPlainText(String javaFragment) throws Exception {
		resetLogListener();
		attchConsoleLayout();
		
		processHtmlAndJava(HTML_FRAGMENT, FIXTURE_START + javaFragment + FIXTURE_STOP);
		
		releaseConsoleLayout();
		
		return checkConsoleLogContains(FIXTURE_CLASSNAME + " - This is BOLD");
	}
	
	public boolean registerExtension(String javaFragment) throws Exception {
		resetLogListener();
		
		String fixture = FIXTURE_START + javaFragment + FIXTURE_STOP;
				
		fixture = fixture.replace("public class Test {", "import specification.BaseFixture;\r\n\r\npublic class Test extends BaseFixture {");
		
		processHtmlAndJava(HTML_FRAGMENT, fixture);
		
		return true;
	}
	
	public boolean hasScreenshot(String javaFragment) throws Exception {
		resetLogListener();

		processHtmlAndJava(HTML_FRAGMENT, FIXTURE_START + javaFragment + FIXTURE_STOP);

		boolean result = true;
		
		result = checkLogContains("<td class=\"Message\">Clicking &#39;Login&#39;</td>", result);
		result = checkLogContains("<pre><a href=\"HtmlLogLogScreenShot", result);
		
		return result;
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
	
//	TODO: Storyboard will need to be able to link to this entry
//	TODO: Display XML just like Internet Explorer?
//
//	* http://www.geekzilla.co.uk/ViewD245BBE0-2EAB-44C0-9119-8038467926EE.htm
//	* http://www.codeproject.com/Articles/24299/XML-String-Browser-just-like-Internet-Explorer-usi
//
//	or maybe add link an open as file?
//
//	* http://www.w3schools.com/tags/tag_embed.asp
//
//	And Status Icons
//
//	*  http://fontawesome.io
//
//	need to figure out which ones to use - will need to look at extent reports
//
//	clone https://github.com/anshooarora/extentreports and search for fa-check-circle-o

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
