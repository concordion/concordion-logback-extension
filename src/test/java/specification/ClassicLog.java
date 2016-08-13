package specification;

import org.concordion.api.BeforeSpecification;

import test.concordion.logback.LogBackHelper;

public class ClassicLog extends BaseFixture {
	
	@BeforeSpecification
	private final void beforeSpecification() {
		switchToClassicLogger(false);
	}

	public boolean isClassicLoggerConfigured() {
		return LogBackHelper.isConfiguredForTextLog();
	}
	
	public boolean canUseClassicLogger(String fixture) {
		// TODO Use the fixture supplied!
		getLogger().debug("This log statement is for the specification log");

		return true;
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
