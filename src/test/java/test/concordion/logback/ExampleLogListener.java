package test.concordion.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ExampleLogListener extends LoggingListener {

	@Override
	protected void append(ILoggingEvent event) {
		boolean found = false;

		if (event.getMarker().contains("STORYBOARD_SCREENSHOT")) {
			found = true;
		}

		if (event.getMarker().contains("STORYBOARD_CONTAINER")) {
			found = true;
		}
	}

	@Override
	public String[] getFilterMarkers() {
		return new String[] { "STORYBOARD" };
	}
}
