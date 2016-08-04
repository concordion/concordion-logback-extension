package test.concordion.logback;

import org.concordion.ext.loggingFormatter.ILoggingAdaptor;
import org.slf4j.ext.ReportLogger;
import org.slf4j.ext.ReportLoggerFactory;

public class PageHelper {
	private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());

	public void captureScreenshot(ILoggingAdaptor loggingAdaptor) {
		logger.with()
				.htmlMessage("<b>This is a location aware logged entry</b>")
				.locationAwareParent(this)
				.trace();

		logger.with()
				.htmlMessage("<i>This is NOT a location aware logged entry</i>")
				.screenshot(loggingAdaptor.getLogFile(), new DummyScreenshotTaker())
				.marker(StoryboardMarkerFactory.screenshot("Doing Stuff"))
				.trace();
	}
}
