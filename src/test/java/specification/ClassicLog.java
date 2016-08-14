package specification;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

import ch.qos.logback.classic.PatternLayout;
import test.concordion.ProcessingResult;
import test.concordion.logback.LogBackHelper;

public class ClassicLog extends BaseFixture {
	private PatternLayout layout;
	private PatternLayout backup;
	
	@BeforeSpecification
	private final void beforeSpecification() {
		switchToClassicLogger(false);
		getLogger().debug("preparing logger for testing");
		attchTextLayout();
	}

	@AfterSpecification
	private final void afterSpecification() {
		releaseTextLayout();
		restoreTextLayout();
	}
	
	////Helper Methods
	private void attchTextLayout() {
		layout = LogBackHelper.getTextLayout();
		
		backup = new PatternLayout();
		copy(layout, backup);
		
		// Remove date from Pattern so for easier comparison of log entry
		layout.setPattern("%-5level %logger{36} - %msg%n");
		layout.stop();
		layout.start();
		
		exampleLogListener.setLayout(layout);
	}
	
	private void releaseTextLayout() {
		exampleLogListener.setLayout(null);
	}
	
	private void resetLogListener() {
		exampleLogListener.reset();	
	}

	private void restoreTextLayout() {
		copy(backup, layout);
	}
	
	private void copy(PatternLayout src, PatternLayout dest) {
		dest.setPattern(src.getPattern());
	}
	////END Helper Methods
	
	// Classic logger is configured and has the pattern we need 
	public boolean isClassicLoggerConfigured() {
		return LogBackHelper.isConfiguredForTextLog();
	}

	public String canUseClassicLogger(String javaFragment) throws Exception {
		resetLogListener();
		
		processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		return getLogContent();
	}

	public boolean specificationHasLinkToLogFile(String javaFragment) throws Exception {
		ProcessingResult processingResult = processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		String html = processingResult.getElementXML("footer");
		
		return html.contains("href=\"testrig.log\">Log File</a>");
	}

	public boolean exampleHasLinkToLogFile(String javaFragment) throws Exception {
		ProcessingResult processingResult = processHtmlAndJava("<div class=\"example1\" concordion:example=\"example1\"><span concordion:execute=\"logSomething()\"></span></div>", javaFragment);

		String html = processingResult.getElementXML("example1");
		
		return html.contains("href=\"testrig[example1].log\">Log File</a>");
	}

	public boolean useLogViewer(String javaFragment, String method) throws Exception {
		javaFragment = javaFragment.replace("new LoggingFormatterExtension();", "new LoggingFormatterExtension()." + method + ";");
	
		ProcessingResult processingResult = processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		String html = processingResult.getElementXML("footer");
		
		return html.contains("href=\"testrigLogViewer.html\">Log File</a>");
	}
}
