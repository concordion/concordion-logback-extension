package org.concordion.slf4j.markers;

import org.concordion.api.listener.AssertFailureEvent;
import org.slf4j.helpers.ConcordionMarker;

public class FailureReportedMarker extends ConcordionMarker {
	private static final long serialVersionUID = 8750307001902436743L;

	private final AssertFailureEvent event;

	public FailureReportedMarker(AssertFailureEvent event) {
		super(ReportLoggerMarkers.FAILURE_REPORTED_MARKER_NAME);

		this.event = event;
	}

	public AssertFailureEvent getEvent() {
		return event;
	}
}
