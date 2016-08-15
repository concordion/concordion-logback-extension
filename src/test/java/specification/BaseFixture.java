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
import org.concordion.ext.loggingFormatter.ILoggingAdaptor;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.FluentLogger;
import org.slf4j.ext.ReportLogger;
import org.slf4j.ext.ReportLoggerFactory;

import ch.qos.logback.classic.Level;
import test.concordion.JavaSourceCompiler;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.logback.DummyScreenshotTaker;
import test.concordion.logback.ExampleLogListener;
import test.concordion.logback.ExampleStoryboardListener;
import test.concordion.logback.LogBackHelper;

/**
 * A base class for Google search tests that opens up the Google site at the Google search page, and closes the browser once the test is complete.
 */
@RunWith(ConcordionRunner.class)
public class BaseFixture {
	private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());
	private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());
	protected ExampleLogListener exampleLogListener = new ExampleLogListener();
	protected ExampleStoryboardListener exampleStoryboardListener = new ExampleStoryboardListener();
	private JavaSourceCompiler compiler;
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("class\\s*(.*?)\\s*(\\{|extends)");

	@Extension
	private final LoggingTooltipExtension tooltipExtension = new LoggingTooltipExtension(new LogbackLogMessenger(tooltipLogger.getName(), Level.ALL, true, "%msg%n"));

	@Extension
	private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension()
			.registerListener(exampleLogListener)
			.registerListener(exampleStoryboardListener)
			.setScreenshotTaker(new DummyScreenshotTaker());
	
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

//	public ILoggingAdaptor getLoggingAdaptor() {
//		return loggingExtension.getLoggingAdaptor();
//	}

	public void addConcordionTooltip(final String message) {
		// Logging at debug level means the message won't make it to the console, but will make 
		// it to the logs (based on included logback configuration files)
		tooltipLogger.debug(message);
	}

	// Location in log file will appear as if it came from the inheriting class
	protected void logParentClassLocationAware() {
		getLogger().with()
				.htmlMessage("<b>This is a parent class location aware logged entry</b>")
				.locationAwareParent(BaseFixture.class)
				.trace();
	}

	protected String getLogContent() {
		return exampleLogListener.getLog();
	}
	
	protected boolean checkLogEqual(String expected, boolean currentResult) {
		return checkEqual(expected, exampleLogListener.getLog(), currentResult);
	}

	protected boolean checkLogContains(String expected, boolean currentResult) {
		return checkContains(expected, exampleLogListener.getLog(), currentResult);	
	}

	protected boolean checkConsoleLogContains(String expected, boolean currentResult) {
		return checkContains(expected, exampleLogListener.getConsoleLog(), currentResult);
	}

	protected boolean checkStoryboardLogEqual(String expected, boolean currentResult) {
		return checkEqual(expected, exampleStoryboardListener.getStreamContent(), currentResult);
	}

	protected boolean checkEqual(String expected, String actual, boolean currentResult) {
		if (!actual.equals(expected)) {
			getLogger().error("Actual result not equal to expected: [Expected]: {}, [Actual]: {}", expected, actual);
			return false;
		}

		return currentResult;
	}
	
	protected boolean checkContains(String expected, String actual, boolean currentResult) {
		if (!actual.contains(expected)) {
			getLogger().error("Actual result does not contain expected: [Expected]: {}, [Actual]: {}", expected, actual);
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
		rig.withResource(new Resource("/font-awesome-4.6.3/css/font-awesome.css"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.eot"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.svg"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.ttf"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff2"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/FontAwesome.otf"), "");

		return rig;
	}
	
    public ProcessingResult processHtml(String htmlFragment) throws Exception {
    	return process(htmlFragment, this); //.getClass().newInstance());
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

    	ProcessingResult result = getTestRig()
			.withFixture(fixture)
			.processFragment(htmlFragment);
      
        
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
    	return compiler.compile(getClassName(javaSource), javaSource).newInstance();
	}
	
	public String getClassName(String javaFragment) {
		Matcher matcher = CLASS_NAME_PATTERN.matcher(javaFragment);
		matcher.find();
		return matcher.group(1);
	}
}
