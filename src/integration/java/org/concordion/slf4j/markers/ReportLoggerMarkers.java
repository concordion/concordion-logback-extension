package org.concordion.slf4j.markers;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ReportLoggerMarkers {
	public static Marker TOOLTIP_MARKER = MarkerFactory.getMarker("TOOLTIP");
	public static Marker PROGRESS_MARKER = MarkerFactory.getMarker("PROGRESS");
	public static Marker STEP_MARKER = MarkerFactory.getMarker("STEP");
	// public static Marker HTML_MESSAGE_MARKER = MarkerFactory.getMarker("HTML");
	public static String DATA_MARKER_NAME = "DATA";

	/**
	 * Indicates a throwable exception has been caught and logged by the logging extension
	 * 
	 * <p>
	 * Log Listener Extensions wanting to react to these events will need to register with this
	 * marker in the list of markers they filter on.
	 * </p>
	 */
	public static String THROWABLE_CAUGHT_MARKER_NAME = "THROWABLE_CAUGHT";

	public static Marker throwableCaught(Throwable cause) {
		return new ThrowableCaughtMarker(cause);
	}
}
