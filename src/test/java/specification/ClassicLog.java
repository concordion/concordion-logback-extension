package specification;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.SimpleJavaFileObject;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.ext.html.HTMLLayout;
import test.concordion.JavaSourceCompiler;
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
		
		ProcessingResult result = processHtmlAndJava("<span concordion:execute=\"logSomething()\"></span>", javaFragment);

		return getLogContent();
		//checkLogContains("DEBUG " + getClassName(javaFragment) + " - Log a value", true);

		//TODO Some of this might help with other tests
	    
//		FileOutputStreamer streamer;
//		
//		getTestRig()
//		    .withFixture(this.getClass().newInstance())
//		    .withOutputStreamer(streamer);
	
//		protected String getBaseOutputDir() {
//	    	return streamer.getBaseOutputDir().getPath();
//	    }
//		getTestRig()
//			.withFixture(this.getClass().newInstance())
//			.processFragment("<span concordion:execute=\"writelog()\" />", "/" + this.getClass().getName().replace(".", "/").replace("$", "/"))
//			.getElementXML("storyboard");
		
	}

	public boolean hasLinkToLogFile() {
		// TODO Nigel: need to be able to pass in code and fixture snippets for various examples and use TestRig to get specification and get footer
		return true;
	}

	public boolean hasExampleLog() {
		// TODO repeat of hasLinkToLogFile() for example link 
		getLogger().debug("This log statement is for the example log");
		return true;
	}

	public boolean useLogViewer() {
		//TODO Nigel: should we support it any more? If so how test?
		return true;
	}
	
	public class fo extends SimpleJavaFileObject {

		protected fo(URI uri, Kind kind) {
			super(uri, kind);
		}
		
	}
}
