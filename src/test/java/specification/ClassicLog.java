package specification;

import org.concordion.api.BeforeSpecification;

import test.concordion.logback.LogBackHelper;

public class ClassicLog extends BaseFixture {
	
	@BeforeSpecification
	private final void beforeSpecification() {
		switchToClassicLogger(false);
	}

	public boolean canUseClassicLogger() {
		getLogger().debug("This log statement is for the specification log");

		return LogBackHelper.isConfiguredForTextLog();
	}

	public boolean hasExampleLog() {
		getLogger().debug("This log statement is for the example log");
		return true;
	}

	public boolean hasLinkToLogFile() {
		// TODO need to be able to pass in code and fixture snippets for various examples
		// TODO use TestRig to get specification and get footer
		// TODO repeat for example link 
		return true;
	}

	public boolean useLogViewer() {
		//TODO how test?
		return true;
	}
}
