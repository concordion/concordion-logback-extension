package specification;

import org.concordion.api.AfterSpecification;
import org.concordion.api.BeforeSpecification;

import test.concordion.logback.LogBackHelper;

public class ClassicLog extends BaseFixture {
	
	@BeforeSpecification
	private final void beforeSpecification() {
		switchToClassicLogger(false);
		getLogger().debug("preparing logger for testing");
		attchTextLayout();
	}

	@AfterSpecification
	private final void afterSpecification() {
		releaseTextLayout();
	}
	
	private void attchTextLayout() {
//		layout = LogBackHelper.getHtmlLayout();
//		backup = LogBackHelper.backupLayout(layout);
		
		exampleLogListener.setLayout(LogBackHelper.getTextLayout());
	}
	
	private void releaseTextLayout() {
		exampleLogListener.setLayout(null);
	}
	
	private void resetLogListener() {
		exampleLogListener.reset();	
	}
	
	public boolean isClassicLoggerConfigured() {
		return LogBackHelper.isConfiguredForTextLog();
	}
	
	public boolean canUseClassicLogger(String fixture) {
		resetLogListener();
		
		// TODO Use the fixture supplied!
		getLogger().debug("This log statement is for the specification log");

		return checkLogContains("DEBUG " + this.getClass().getName() + " - This log statement is for the specification log", true);
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
}
