package org.concordion.slf4j.markers;

import org.concordion.api.listener.ThrowableCaughtEvent;
import org.slf4j.helpers.ConcordionMarker;

public class ThrowableCaughtMarker extends ConcordionMarker {
	private static final long serialVersionUID = 8750307001902436743L;

	private final ThrowableCaughtEvent event;

	public ThrowableCaughtMarker(ThrowableCaughtEvent event) {
		super(ReportLoggerMarkers.THROWABLE_CAUGHT_MARKER_NAME);

		this.event = event;
	}

	public ThrowableCaughtEvent getEvent() {
		return event;
	}
}
