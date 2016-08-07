package test.concordion.logback;

import org.slf4j.ext.ReportLogger;
import org.slf4j.ext.ReportLoggerFactory;

public class LocationHelper {
	private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());

	public void logLocationAware() {
		logger.with()
				.htmlMessage("<b>This is a location aware logged entry</b>")
				.locationAwareParent(this)
				.trace();
	}
	
	public void logLocationUnaware() {
		logger.with()
				.htmlMessage("<i>This is NOT a location aware logged entry</i>")
				.trace();
	}
}
