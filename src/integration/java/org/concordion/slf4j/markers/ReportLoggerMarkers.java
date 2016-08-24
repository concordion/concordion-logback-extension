package org.concordion.slf4j.markers;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ReportLoggerMarkers {
	public static final Marker TOOLTIP_MARKER = MarkerFactory.getMarker("TOOLTIP");
	public static final Marker PROGRESS_MARKER = MarkerFactory.getMarker("PROGRESS");
	public static final Marker STEP_MARKER = MarkerFactory.getMarker("STEP");
	// public static Marker HTML_MESSAGE_MARKER = MarkerFactory.getMarker("HTML");
	public static final String DATA_MARKER_NAME = "DATA";

	/**
	 * Indicates an assertion failure has occurred in a specification.
	 * 
	 * <p>
	 * Log Listener Extensions wanting to react to these events will need to register with this
	 * marker in the list of markers they filter on.
	 * </p>
	 */
	public static final String FAILURE_REPORTED_MARKER_NAME = "FAILURE_REPORTED";
		
	/**
	 * Indicates a throwable exception has been caught and logged by the logging extension
	 * 
	 * <p>
	 * Log Listener Extensions wanting to react to these events will need to register with this
	 * marker in the list of markers they filter on.
	 * </p>
	 */
	public static final String THROWABLE_CAUGHT_MARKER_NAME = "THROWABLE_CAUGHT";
}
