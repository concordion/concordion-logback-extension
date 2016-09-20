package specification;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.BeforeSpecification;
import org.concordion.api.Resource;
import org.concordion.api.extension.Extension;
import org.concordion.ext.LogbackLogMessenger;
import org.concordion.ext.LoggingFormatterExtension;
import org.concordion.ext.LoggingTooltipExtension;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.logback.LogbackAdaptor;
import org.concordion.slf4j.ILoggingAdaptor;
import org.concordion.slf4j.ext.FluentLogger;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.logback.ExampleStoryboardListener;
import test.concordion.logback.LayoutFormattedLogListener;
import test.concordion.logback.LogBackHelper;

/**
 * A base class for Google search tests that opens up the Google site at the Google search page, and closes the browser once the test is complete.
 */
@RunWith(ConcordionRunner.class)
public class BaseFixture {
	private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());
	private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());
	protected LayoutFormattedLogListener exampleLogListener = new LayoutFormattedLogListener();
	protected ExampleStoryboardListener exampleStoryboardListener = new ExampleStoryboardListener();
	private JavaSourceCompiler compiler;
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("class\\s*(.*?)\\s*(\\{|extends)");
    private static String MESSAGE_TOKEN = "<td class=\"Message\">";
    private static String END_TOKEN = "</td>";
	private int example = 0;
	private String fixtureName = "testrig";
	
	@Extension
	private final LoggingTooltipExtension tooltipExtension = new LoggingTooltipExtension(new LogbackLogMessenger(tooltipLogger.getName(), Level.ALL, true, "%msg%n"));

	@Extension
	private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()
			.registerListener(exampleLogListener)
			.registerListener(exampleStoryboardListener);
	
	static {
		LogbackAdaptor.logInternalStatus();
	}

	@BeforeSpecification
	private final void beforeSpecification() {
		if (!LogBackHelper.isConfiguredForHtmlLog()) {
			LogBackHelper.restoreLoggerConfiguration();

			loggingExtension.registerListener(exampleLogListener);
			loggingExtension.registerListener(exampleStoryboardListener);
		}
	}

	protected void switchToClassicLogger(boolean useLogViewer) {
		if (!LogBackHelper.isConfiguredForTextLog()) {
			LogBackHelper.switchToClassicLoggerConfiguration();

			loggingExtension.setUseLogFileViewer(useLogViewer);
			loggingExtension.registerListener(exampleLogListener);
			loggingExtension.registerListener(exampleStoryboardListener);
			
			// Already set
			//loggingExtension.setScreenshotTaker(new DummyScreenshotTaker());
		}
	}

	public ReportLogger getLogger() {
		return logger;
	}

	public LoggingFormatterExtension getLoggingExtension() {
		return loggingExtension;
	}

	public void addConcordionTooltip(final String message) {
		// Logging at debug level means the message won't make it to the console, but will make 
		// it to the logs (based on included logback configuration files)
		tooltipLogger.debug(message);
	}

	// Location in log file will appear as if it came from the inheriting class
//	protected void logParentClassLocationAware() {
//		getLogger().with()
//				.htmlMessage("<b>This is a parent class location aware logged entry</b>")
//				.locationAwareParent(BaseFixture.class)
//				.trace();
//	}

	protected String getLogContent() {
		return exampleLogListener.getLog();
	}
	
	protected String getConsoleMessage() {
		String message = exampleLogListener.getConsoleLog();

		int index = message.indexOf(" - ");

		if (index > 0) {
			return message.substring(index + 3);
		} else {
			return message;
		}

	}

	protected void setTestFixtureName(String fixtureName) {
		this.fixtureName = fixtureName;
	}

	protected String getTestSpecificationName() {
		return "extensionExample" + example;
	}

	protected String getLogMessage() {
		String message = exampleLogListener.getLog(); 
		
		int index = message.indexOf(MESSAGE_TOKEN);
		if (index > 0) {
			message = message.substring(index + MESSAGE_TOKEN.length(), message.indexOf(END_TOKEN, index));
		}

		return message;
	}
	
	protected boolean checkLogEqual(String expected, boolean currentResult) {
		return checkEqual(expected, exampleLogListener.getLog(), currentResult);
	}

	protected boolean checkLogContains(String expected, boolean currentResult) {
		return checkContains(expected, exampleLogListener.getLog(), currentResult);	
	}

	protected boolean checkConsoleLogContains(String expected) {
		return checkContains(expected, exampleLogListener.getConsoleLog(), true);
	}

	protected boolean checkStoryboardLogEqual(String expected, boolean currentResult) {
		return checkEqual(expected, exampleStoryboardListener.getStreamContent(), currentResult);
	}

	protected boolean checkEqual(String expected, String actual, boolean currentResult) {
		if (!actual.equals(expected)) {
			getLogger().with()
					.message("Actual result not equal to expected")
					.data("[Expected]: {}\r\n\r\n[Actual]: {}", expected, actual)
					.locationAwareParent(BaseFixture.class)
					.error();

			return false;
		}

		return currentResult;
	}
	
	protected boolean checkContains(String expected, String actual, boolean currentResult) {
		if (!actual.contains(expected)) {
			getLogger().with()
					.message("Actual result not equal to expected")
					.data("[Expected]: {}\r\n\r\n[Actual]: {}", expected, actual)
					.locationAwareParent(BaseFixture.class)
					.error();

			return false;
		}

		return currentResult;
	}

	private TestRig getTestRig() {
		TestRig rig = new TestRig();
		
		rig.withResource(new Resource("/org/concordion/ext/resource/tooltip.css"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/bubble.gif"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/bubble_filler.gif"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/i16.png"), "");

		return rig;
	}
	
    public ProcessingResult processHtml(String htmlFragment) throws Exception {
    	return process(htmlFragment, this);
    }
    
    public ProcessingResult processJava(String... javaFragments) throws Exception {
    	return processHtmlAndJava("", javaFragments);
    }

    public ProcessingResult processHtmlAndJava(String htmlFragment, String... javaFragments) throws Exception {
    	// concept taken from Concordion's ExtensionConfigurationTest
   		compiler = new JavaSourceCompiler();
        
        Object fixture = null;
        
        for (String javaFragment : javaFragments) {
        	fixture = compile(javaFragment);	
		}
        
        return process(htmlFragment, fixture);
    }

	protected ProcessingResult process(String htmlFragment, Object fixture) {
    	// As the TestRig runs the full lifecycle of a test it keeps removing
    	// the screenshot taker
    	ILoggingAdaptor loggingAdaptor = FluentLogger.getLoggingAdaptor();
    	ScreenshotTaker screenshotTaker = FluentLogger.getScreenshotTaker();
		example++;

    	ProcessingResult result = getTestRig()
				.withFixture(fixture)
				.processFragment(htmlFragment, "/" + fixtureName + example);
        
        FluentLogger.addLoggingAdaptor(loggingAdaptor);
        FluentLogger.addScreenshotTaker(screenshotTaker);
    	
        return result;
    }
    
    public class TestRigWorker implements Callable<ProcessingResult> {
    	private Object fixture;
    	private String htmlFragment;
    	
    	public TestRigWorker(Object fixture, String htmlFragment) {
    		this.fixture = fixture;
    		this.htmlFragment = htmlFragment;
    	}
    	
		@Override
		public ProcessingResult call() throws Exception {
			return getTestRig()
		            .withFixture(fixture)
		            .processFragment(htmlFragment);
		}
	}
    
    private Object compile(String javaSource) throws Exception, InstantiationException, IllegalAccessException {
    	return compiler.compile(getClassNameFrom(javaSource), javaSource).newInstance();
	}
	
	public String getClassNameFrom(String javaFragment) {
		Matcher matcher = CLASS_NAME_PATTERN.matcher(javaFragment);
		matcher.find();
		return matcher.group(1);
	}
}
