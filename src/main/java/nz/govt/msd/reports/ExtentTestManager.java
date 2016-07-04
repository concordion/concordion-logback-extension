package nz.govt.msd.reports;

/*
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class ExtentTestManager {
	static Map<Long, ExtentTest> extentTestMap = new HashMap<Long, ExtentTest>();
	static ExtentReports extent = ExtentManager.getReporter();

	private ExtentTestManager() {
	}

	public static synchronized ExtentTest getTest() {
		return extentTestMap.get(getTestId());
	}

	public static synchronized void endTest() {
		extent.endTest(extentTestMap.get(getTestId()));
	}

	public static synchronized ExtentTest startTest(String testName) {
		return startTest(testName, "");
	}

	public static synchronized ExtentTest startTest(String testName, String desc) {
		ExtentTest test = extent.startTest(testName, desc);
		extentTestMap.put(getTestId(), test);

		return test;
	}

	private static long getTestId() {
		return (long) Thread.currentThread().getId();
	}
}
*/