package nz.govt.msd.reports;

/*
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.relevantcodes.extentreports.LogStatus;

public class ExtentReporter extends TestWatcher {
	
	@Override
	protected void starting(Description d) {
		ExtentTestManager.startTest(d.getMethodName());
	}

	@Override
	protected void succeeded(Description description) {
		ExtentTestManager.getTest().log(LogStatus.PASS, "Test passed");
	}

	@Override
	protected void failed(Throwable e, Description description) {
		ExtentTestManager.getTest().log(LogStatus.FAIL, e);
	}

	@Override
	protected void skipped(org.junit.internal.AssumptionViolatedException e, Description description) {
		ExtentTestManager.getTest().log(LogStatus.SKIP, "Test skipped " + e);
	}

	@Override
	protected void finished(Description d) {
		ExtentManager.getReporter().endTest(ExtentTestManager.getTest());
		ExtentManager.getReporter().flush();
	}
}
*/